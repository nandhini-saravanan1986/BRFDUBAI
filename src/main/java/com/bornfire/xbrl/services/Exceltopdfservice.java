package com.bornfire.xbrl.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;

@Component
@Service
public class Exceltopdfservice {

    public byte[] convertExcelBytesToPdf(byte[] excelBytes, String pageSize) {
        try (
            InputStream inputStream = new ByteArrayInputStream(excelBytes);
            Workbook workbook = WorkbookFactory.create(inputStream);
            ByteArrayOutputStream pdfOut = new ByteArrayOutputStream()
        ) {
            Sheet sheet = workbook.getSheetAt(0);

            Rectangle selectedPageSize;
            switch (pageSize != null ? pageSize.toUpperCase() : "A3") {
                case "A2": selectedPageSize = PageSize.A2.rotate(); break;
                case "A4": selectedPageSize = PageSize.A4.rotate(); break;
                default:   selectedPageSize = PageSize.A3.rotate(); break;
            }
            Document document = new Document(selectedPageSize, 20, 20, 20, 20);
            PdfWriter.getInstance(document, pdfOut);
            document.open();

            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            evaluator.setIgnoreMissingWorkbooks(true);
            DataFormatter formatter = new DataFormatter();
            List<CellRangeAddress> mergedRegions = sheet.getMergedRegions();
            Set<String> mergedCells = new HashSet<>();

            // ── BUG FIX 1: Column detection ──────────────────────────────────────────
            // Old code only added columns that had non-empty cells, which caused columns
            // with numeric zeros or formula-only rows to be skipped entirely, collapsing
            // the table width and misaligning every subsequent column.
            // Fix: scan all columns that are explicitly defined in the sheet's column
            // definitions OR that appear in any cell, including empty ones.
            // We use a contiguous range from min to max so no column is ever skipped.
            int minCol = Integer.MAX_VALUE;
            int maxCol = Integer.MIN_VALUE;
            for (Row row : sheet) {
                if (row == null) continue;
                for (Cell cell : row) {
                    if (cell == null) continue;
                    int ci = cell.getColumnIndex();
                    if (ci < minCol) minCol = ci;
                    if (ci > maxCol) maxCol = ci;
                }
            }
            if (minCol == Integer.MAX_VALUE)
                throw new IllegalArgumentException("Excel sheet has no data.");

            List<Integer> colIndexes = new ArrayList<>();
            for (int c = minCol; c <= maxCol; c++) colIndexes.add(c);

            // ── BUG FIX 2: Proportional column widths from Excel ─────────────────────
            // Old code used PdfPTable(colIndexes.size()) with no relative widths, so
            // every column got equal space regardless of the template design.
            // Fix: read each column's width from the Sheet (in POI 1/256-char units)
            // and convert them to relative float widths for iText.
            float[] colWidths = new float[colIndexes.size()];
            float totalWidth = 0f;
            for (int i = 0; i < colIndexes.size(); i++) {
                // getColumnWidth returns 0 for default-width columns; fall back to
                // getDefaultColumnWidth() in that case so relative ratios are correct.
                int raw = sheet.getColumnWidth(colIndexes.get(i));
                if (raw == 0) raw = sheet.getDefaultColumnWidth() * 256;
                colWidths[i] = raw;
                totalWidth += raw;
            }
            // Normalise to avoid iText float overflow
            for (int i = 0; i < colWidths.length; i++) colWidths[i] = (colWidths[i] / totalWidth) * 100f;

            PdfPTable table = new PdfPTable(colIndexes.size());
            table.setWidths(colWidths);

            // lock the table width instead of shrinking columns
            float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            table.setTotalWidth(pageWidth);
            table.setLockedWidth(true);

            table.setSpacingBefore(10f);
            table.setSplitLate(false);
            table.setSplitRows(true);
            DecimalFormat df = new DecimalFormat("#,##0.###");  
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                // ── BUG FIX 3: Row-height from Excel ─────────────────────────────────
                // Old code never transferred Excel row heights to iText, so multi-line
                // description rows (1.6–1.9) had the same height as single-line rows,
                // causing text to be clipped vertically even when wrapping was enabled.
                // Fix: read the row height (in POI 1/20-pt units) and set it as the
                // iText cell minimum height.
                float rowHeightPt = 15f; // default
                if (row != null && row.getHeight() > 0) {
                    rowHeightPt = row.getHeightInPoints();
                }

                // Skip rows that are completely empty (all resolved values blank)
                if (row != null) {
                    boolean rowEmpty = true;
                    for (int colIndex : colIndexes) {
                        Cell cell = row.getCell(colIndex);
                        if (cell != null) {
                            String val;
                            if (cell.getCellTypeEnum() == CellType.FORMULA) {
                                try {
                                    CellValue cv = evaluator.evaluate(cell);
                                    val = (cv != null) ? cv.formatAsString() : "";
                                } catch (Exception ex) {
                                    val = "";
                                }
                            } else {
                                val = formatter.formatCellValue(cell);
                            }
                            if (!val.trim().isEmpty() && !val.equals("0")) {
                                rowEmpty = false;
                                break;
                            }
                        }
                    }
                    if (rowEmpty) continue;
                } else {
                    continue; // null row — skip
                }

                for (int colIndex : colIndexes) {
                    String cellKey = rowIndex + "-" + colIndex;
                    if (mergedCells.contains(cellKey)) continue;

                    Cell cell = row.getCell(colIndex);
                    String cellValue = "";
                    org.apache.poi.ss.usermodel.CellStyle style = null;

                    if (cell != null) {
                        style = cell.getCellStyle();

                        if (cell.getCellTypeEnum() == CellType.FORMULA) {
                            try {
                                CellValue eval = evaluator.evaluate(cell);
                                if (eval != null) {
                                    switch (eval.getCellTypeEnum()) {
                                        case STRING:
                                            cellValue = eval.getStringValue();
                                            break;
                                        case NUMERIC:
                                            double num = eval.getNumberValue();
                                            // Use the cell's own number format if available;
                                            // fall back to our DecimalFormat for plain numbers.
                                            String fmt = formatter.formatCellValue(cell, evaluator);
                                            cellValue = (fmt != null && !fmt.trim().isEmpty())
                                                    ? fmt
                                                    : (num == Math.floor(num)
                                                            ? String.valueOf((long) num)
                                                            : df.format(num));
                                            break;
                                        case BOOLEAN:
                                            cellValue = String.valueOf(eval.getBooleanValue());
                                            break;
                                        default:
                                            cellValue = "";
                                    }
                                }
                            } catch (Exception ex) {
                                cellValue = "";
                            }
                        } else if (cell.getCellTypeEnum() == CellType.NUMERIC) {
                            // Use the cell's own number format (honours #,##0.000 etc.)
                            String fmt = formatter.formatCellValue(cell, evaluator);
                            cellValue = (fmt != null && !fmt.trim().isEmpty())
                                    ? fmt
                                    : df.format(cell.getNumericCellValue());
                        } else {
                            cellValue = formatter.formatCellValue(cell);
                        }
                    }

                    // PDF font from Excel cell style
                    Font cellFont;
                    if (cell != null && style != null) {
                        short fontIdx = style.getFontIndex();
                        org.apache.poi.ss.usermodel.Font excelFont = workbook.getFontAt(fontIdx);
                        boolean isBold = excelFont.getBold();
                        boolean isItalic = excelFont.getItalic();
                        int styleInt = Font.NORMAL;
                        if (isBold)   styleInt |= Font.BOLD;
                        if (isItalic) styleInt |= Font.ITALIC;
                        cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9f, styleInt);
                    } else {
                        cellFont = FontFactory.getFont(FontFactory.HELVETICA, 9f, Font.NORMAL);
                    }

                    Phrase phrase = new Phrase(cellValue, cellFont);
                    PdfPCell pdfCell = new PdfPCell(phrase);

                    // ── BUG FIX 4: Merged-cell colspan counted against filtered list ──
                    // Old code set colspan = lastCol - firstCol + 1 using raw Excel column
                    // indices.  When colIndexes skipped columns the colspan was too large,
                    // causing iText to throw or render extra blank phantom cells that pushed
                    // real data off the right edge.
                    // Fix: count how many entries in colIndexes fall within the merged range
                    // so the colspan matches the actual number of PDF columns being spanned.
                    for (CellRangeAddress range : mergedRegions) {
                        if (range.isInRange(rowIndex, colIndex)) {
                            if (rowIndex == range.getFirstRow() && colIndex == range.getFirstColumn()) {
                                int rowspan = range.getLastRow() - range.getFirstRow() + 1;
                                // Count PDF columns spanned (not raw Excel columns)
                                int colspan = 0;
                                for (int c : colIndexes) {
                                    if (c >= range.getFirstColumn() && c <= range.getLastColumn()) colspan++;
                                }
                                if (rowspan > 1) pdfCell.setRowspan(rowspan);
                                if (colspan > 1) pdfCell.setColspan(colspan);
                                // Mark all non-origin cells in this merged region as consumed
                                for (int mr = range.getFirstRow(); mr <= range.getLastRow(); mr++) {
                                    for (int mc : colIndexes) {
                                        if (mc >= range.getFirstColumn() && mc <= range.getLastColumn()) {
                                            if (mr != range.getFirstRow() || mc != range.getFirstColumn()) {
                                                mergedCells.add(mr + "-" + mc);
                                            }
                                        }
                                    }
                                }
                            } else {
                                mergedCells.add(cellKey);
                                pdfCell = null;
                            }
                            break;
                        }
                    }
                    if (pdfCell == null) continue;

                    // Background color
                    BaseColor bgColor = null;
                    if (cell != null && style != null) {
                        org.apache.poi.ss.usermodel.Color poiColor = style.getFillForegroundColorColor();
                        if (poiColor == null) poiColor = style.getFillBackgroundColorColor();

                        if (poiColor instanceof XSSFColor) {
                            XSSFColor xssfColor = (XSSFColor) poiColor;
                            byte[] rgb = xssfColor.getRGBWithTint();
                            if (rgb != null && (rgb[0] != 0 || rgb[1] != 0 || rgb[2] != 0)) {
                                bgColor = new BaseColor(rgb[0] & 0xFF, rgb[1] & 0xFF, rgb[2] & 0xFF);
                            }
                        } else if (workbook instanceof HSSFWorkbook) {
                            HSSFWorkbook hwb = (HSSFWorkbook) workbook;
                            short idx = style.getFillForegroundColor();
                            if (idx == 0) idx = style.getFillBackgroundColor();
                            if (idx > 0) {
                                HSSFPalette palette = hwb.getCustomPalette();
                                HSSFColor color = palette.getColor(idx);
                                if (color != null && color.getTriplet() != null) {
                                    short[] rgb = color.getTriplet();
                                    if (!(rgb[0] == 0 && rgb[1] == 0 && rgb[2] == 0))
                                        bgColor = new BaseColor(rgb[0], rgb[1], rgb[2]);
                                }
                            }
                        }
                    }
                    if (bgColor != null) pdfCell.setBackgroundColor(bgColor);
                    else pdfCell.setBackgroundColor(BaseColor.WHITE);

                    // Borders from Excel
                    if (style != null) {
                        boolean top    = style.getBorderTopEnum()    != BorderStyle.NONE;
                        boolean bottom = style.getBorderBottomEnum() != BorderStyle.NONE;
                        boolean left   = style.getBorderLeftEnum()   != BorderStyle.NONE;
                        boolean right  = style.getBorderRightEnum()  != BorderStyle.NONE;
                        int border = 0;
                        if (top)    border |= Rectangle.TOP;
                        if (bottom) border |= Rectangle.BOTTOM;
                        if (left)   border |= Rectangle.LEFT;
                        if (right)  border |= Rectangle.RIGHT;
                        pdfCell.setBorder(border == 0 ? Rectangle.NO_BORDER : border);
                    } else {
                        pdfCell.setBorder(Rectangle.NO_BORDER);
                    }

                 // Alignment from Excel
                    if (style != null) {
                        HorizontalAlignment hAlign = style.getAlignmentEnum();

                        // ── FIX: Auto-right-align numeric/formula-numeric cells with GENERAL alignment
                        if (hAlign == HorizontalAlignment.GENERAL) {
                            boolean isNumeric = false;
                            if (cell != null) {
                                CellType type = cell.getCellTypeEnum();
                                if (type == CellType.NUMERIC) {
                                    isNumeric = true;
                                } else if (type == CellType.FORMULA) {
                                    try {
                                        CellValue cv = evaluator.evaluate(cell);
                                        if (cv != null && cv.getCellTypeEnum() == CellType.NUMERIC) {
                                            isNumeric = true;
                                        }
                                    } catch (Exception ex) {
                                        // ignore
                                    }
                                }
                            }
                            pdfCell.setHorizontalAlignment(isNumeric ? Element.ALIGN_RIGHT : Element.ALIGN_LEFT);
                        } else {
                            switch (hAlign) {
                                case LEFT:
                                    pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                    break;
                                case RIGHT:
                                    pdfCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                                    break;
                                case CENTER:
                                    pdfCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                                    break;
                                default:
                                    pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            }
                        }

                        VerticalAlignment vAlign = style.getVerticalAlignmentEnum();

                        switch (vAlign) {

                            case TOP:
                                pdfCell.setVerticalAlignment(Element.ALIGN_TOP);
                                break;

                            case CENTER:
                                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                break;

                            case BOTTOM:
                                pdfCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
                                break;

                            default:
                                pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                        }

                    } else {
                        pdfCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        pdfCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    }

                 

                    pdfCell.setPadding(4f);

                    // ── BUG FIX 5: NoWrap was TRUE — the single biggest cause of truncation
                    // setNoWrap(true) tells iText to never wrap text inside a cell under
                    // ANY circumstances.  For a description column that holds sentences like
                    // "UAE local governments publicly traded debt securities..." this meant
                    // all text past the right edge of the cell was silently discarded.
                    // Fix: honour the Excel wrapText flag.  If the Excel cell says wrap,
                    // allow wrapping; otherwise let iText decide (default is false / no-wrap).
                    boolean excelWrap = (style != null) && style.getWrapText();
                    pdfCell.setNoWrap(!excelWrap);

                    // Apply row height so multi-line rows are tall enough to show all lines
                    pdfCell.setMinimumHeight(rowHeightPt);

                    table.addCell(pdfCell);
                }
            }

            document.add(table);
            document.close();
            return pdfOut.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Conversion failed", e);
        }
    }
}
