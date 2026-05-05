
package com.bornfire.xbrl.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.entities.ReportTemplateConfig;
import com.bornfire.xbrl.entities.BRF_REF_CODE_ENTITY;
import com.bornfire.xbrl.entities.Facility_Repo;
import com.bornfire.xbrl.entities.Facitlity_Entity;
import com.bornfire.xbrl.entities.MISReportMasterList;
import com.bornfire.xbrl.entities.RBRShareHolder_Entity;
import com.bornfire.xbrl.entities.RBRShareHolder_Repo;
import com.bornfire.xbrl.entities.RBRcustomerArchivalRepo;
import com.bornfire.xbrl.entities.RBRcustomerRepo;
import com.bornfire.xbrl.entities.RBRcustomer_Archival_entity;
import com.bornfire.xbrl.entities.RBRcustomer_entity;
import com.bornfire.xbrl.entities.RBRfacilityArchivalRepo;
import com.bornfire.xbrl.entities.RBRfacility_Archival_entity;
import com.bornfire.xbrl.entities.RBRoverallArchivalRepo;
import com.bornfire.xbrl.entities.RBRoverall_Archival_entity;
import com.bornfire.xbrl.entities.RBRpartnerArchivalRepo;
import com.bornfire.xbrl.entities.RBRpartner_Archival_entity;
import com.bornfire.xbrl.entities.RBRprovisionArchivalRepo;
import com.bornfire.xbrl.entities.RBRprovision_Archival_entity;
import com.bornfire.xbrl.entities.RBRsecurityArchivalRepo;
import com.bornfire.xbrl.entities.RBRsecurity_Archival_entity;
import com.bornfire.xbrl.entities.ReportStatusInfo;
import com.bornfire.xbrl.entities.ReportStatusInfoId;
import com.bornfire.xbrl.entities.ReportTemplateConfigRepository;
import com.bornfire.xbrl.entities.Security_Entity;
import com.bornfire.xbrl.entities.Security_Repo;
import com.bornfire.xbrl.entities.UserAuditRepo;
import com.bornfire.xbrl.entities.XBRLAudit;
import com.bornfire.xbrl.entities.XBRLProceduresRep;
import com.bornfire.xbrl.entities.XBRLReportMap;
import com.bornfire.xbrl.entities.XBRLReportsMaster;
import com.bornfire.xbrl.entities.XBRLReportsMasterRep;
//import com.bornfire.xbrl.entities.BRBS.BRF2_MAPPING_REPO;
import com.bornfire.xbrl.entities.BRBS.*;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRPrintPage;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
@Transactional
@ConfigurationProperties("output")
public class ReportServices {

	private static final Logger logger = LoggerFactory.getLogger(ReportServices.class);

	private static final Object TRANMASTERDETAILPage = null;

//	private static final String  = null;

	/*
	 * @Autowired BRF2_MAPPING_REPO brf2_MAPPING_REPO;
	 */

	@NotNull
	private String exportpath;

	@Autowired
	RBRcustomerRepo RBRcustomerRepo;

	@Autowired
	RBRcustomerArchivalRepo RBRcustomerArchivalRepo;

	@Autowired
	AuditServicesRep AuditServicesRep;

	@Autowired
	REPORTLIST_REPO reportLIST_REPO;

	@Autowired
	REPORTLIST_REPO reportlist_repo;

	@Autowired
	BRF2_MAPPING_REPO brf2_mapping_repo;

	@Autowired
	BRF3_MAPPING_REPO brf3_mapping_repo;

	@Autowired
	BRF4_MAPPING_REPO brf4_mapping_repo;

	@Autowired
	BRF7_MAPPING_REPO brf7_mapping_repo;

	@Autowired
	BRF8_MAPPING_REPO brf8_mapping_repo;

	@Autowired
	BRF9_MAPPING_REPO brf9_mapping_repo;

	@Autowired
	BRF10_MAPPING_REPO brf10_mapping_repo;

	@Autowired
	BRF11_MAPPING_REPO brf11_mapping_repo;

	@Autowired
	BRF12_MAPPING_REPO brf12_mapping_repo;

	@Autowired
	BRF13_MAPPING_REPO brf13_mapping_repo;

	@Autowired
	BRF31_MAPPING_REPO brf31_mapping_repo;

	@Autowired
	BRF32_MAPPING_REPO brf32_mapping_repo;

	@Autowired
	BRF34_MAPPING_REPO brf34_mapping_repo;

	@Autowired
	BRF46_MAPPING_REPO brf46_mapping_repo;
	@Autowired
	BRF65_MAPPING_REPO brf65_mapping_repo;
	@Autowired
	BRF71_MAPPING_REPO brf71_mapping_repo;
	@Autowired
	BRF74_MAPPING_REPO brf74_mapping_repo;
	@Autowired
	BRF210_MAPPING_REPO brf210_mapping_repo;

	@Autowired
	BRF50_MAPPING_REPO BRF50_MAPPING_REPO;
	@Autowired
	BRF62_MAPPING_REPO BRF62_MAPPING_REPO;
	@Autowired
	BRF66_MAPPING_REPO BRF66_MAPPING_REPO;
	@Autowired
	BRF94_MAPPING_REPO BRF94_MAPPING_REPO;
	@Autowired
	BRF95_MAPPING_REPO BRF95_MAPPING_REPO;
	@Autowired
	BRF96_MAPPING_REPO BRF96_MAPPING_REPO;
	@Autowired
	BRF103_MAPPING_REPO BRF103_MAPPING_REPO;
	@Autowired
	BRF104_MAPPING_REPO BRF104_MAPPING_REPO;
	@Autowired
	BRF181_MAPPING_REPO BRF181_MAPPING_REPO;
	@Autowired
	BRF204_MAPPING_REPO BRF204_MAPPING_REPO;
	@Autowired
	BRF200_MAPPING_REPO BRF200_MAPPING_REPO;
	@Autowired
	BRF107_MAPPING_REPO BRF107_MAPPING_REPO;

	@Autowired
	BRF151_MAPPING_REPO BRF151_MAPPING_REPO;
	@Autowired
	BRF202_MAPPING_REPO BRF202_MAPPING_REPO;
	@Autowired
	BRF205_MAPPING_REPO BRF205_MAPPING_REPO;
	@Autowired
	BRF206_MAPPING_REPO BRF206_MAPPING_REPO;
	@Autowired
	BRF40_MAPPING_REPO BRF40_MAPPING_REPO;
	@Autowired
	BRF41_MAPPING_REPO BRF41_MAPPING_REPO;

	@Autowired
	BRF100_MAPPING_REPO BRF100_MAPPING_REPO;
	@Autowired
	BRF60_MAPPING_REPO BRF60_MAPPING_REPO;
	@Autowired
	BRF68_MAPPING_REPO BRF68_MAPPING_REPO;
	@Autowired
	BRF92_MAPPING_REPO BRF92_MAPPING_REPO;
	@Autowired
	BRF93_MAPPING_REPO BRF93_MAPPING_REPO;
	@Autowired
	BRF99_MAPPING_REPO BRF99_MAPPING_REPO;
	@Autowired
	BRF105_MAPPING_REPO BRF105_MAPPING_REPO;
	@Autowired
	BRF73_MAPPING_REPO BRF73_MAPPING_REPO;

	@Autowired
	BRF101_MAPPING_REPO BRF101_MAPPING_REPO;
	@Autowired
	BRF106_MAPPING_REPO BRF106_MAPPING_REPO;
	@Autowired
	BRF67_MAPPING_REPO BRF67_MAPPING_REPO;
	@Autowired
	BRF152_MAPPING_REPO BRF152_MAPPING_REPO;
	@Autowired
	BRF153_MAPPING_REPO BRF153_MAPPING_REPO;
	@Autowired
	BRF49_MAPPING_REPO BRF49_MAPPING_REPO;
	@Autowired
	BRF37_MAPPING_REPO BRF37_MAPPING_REPO;
	@Autowired
	BRF56_MAPPING_REPO BRF56_MAPPING_REPO;

	@Autowired
	BRF38_MAPPING_REPO brf38_mapping_repo;
	@Autowired
	BRF44_MAPPING_REPO brf44_mapping_repo;
	@Autowired
	BRF70_MAPPING_REPO brf70_mapping_repo;
	@Autowired
	BRF64_MAPPING_REPO brf64_mapping_repo;
	@Autowired
	BRF300_MAPPING_REPO brf300_mapping_repo;
/////	
	@Autowired
	BRF36_MAPPING_REPO BRF36_MAPPING_REPO;

	@Autowired
	BRF42_MAPPING_REPO BRF42_MAPPING_REPO;
	@Autowired
	BRF48_MAPPING_REPO BRF48_MAPPING_REPO;
	@Autowired
	BRF51_MAPPING_REPO BRF51_MAPPING_REPO;
	@Autowired
	BRF52_MAPPING_REPO BRF52_MAPPING_REPO;
	@Autowired
	BRF53_MAPPING_REPO BRF53_MAPPING_REPO;

	@Autowired
	BRF54_MAPPING_REPO BRF54_MAPPING_REPO;
	@Autowired
	BRF57_MAPPING_REPO BRF57_MAPPING_REPO;
	@Autowired
	BRF207_MAPPING_REPO BRF207_MAPPING_REPO;
	@Autowired
	BRF208_MAPPING_REPO BRF208_MAPPING_REPO;
	@Autowired
	BRF209_MAPPING_REPO BRF209_MAPPING_REPO;
	@Autowired
	BRF109_MAPPING_REPO BRF109_MAPPING_REPO;

	@Autowired
	BRF14_MAPPING_REPO BRF14_MAPPING_REPO;

	@Autowired
	BRF5_MAPPING_REPO BRF5_MAPPING_REPO;

	@Autowired
	XBRLReportsMasterRep xbrlReportsMasterRep;

	@Autowired
	XBRLProceduresRep xbrlProceduresRep;

	@Autowired
	DataSource srcdataSource;

	@Autowired
	Environment env;

	@Autowired

	SessionFactory sessionFactory1;

	@Autowired
	XBRLReportMap xbrlreportmap;

	@Autowired
	static

	SessionFactory sessionFactory;

	@Autowired
	CustomRepGeneratorServices customerrptgenserviceexcel;

	@Autowired
	BRF001ReportService brf001ReportService;

	@Autowired
	private UserAuditRepo userAuditRepo;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Value("${output.exportpathtemp}")
	private String templateFolder;

	// ── Regex: B/C cell value starts with a digit (e.g. "1.0", "14.1", "2.3.4") ──
	private static final Pattern STARTS_WITH_DIGIT = Pattern.compile("^\\d+.*");

	// ── Header labels to skip (B column)
	// ──────────────────────────────────────────
	private static final java.util.Set<String> HEADER_LABELS = new java.util.HashSet<>(
			java.util.Arrays.asList("no", "s.n.", "s.no", "s.no.", "sn"));

	private final org.apache.poi.ss.usermodel.DataFormatter DATA_FORMATTER = new org.apache.poi.ss.usermodel.DataFormatter();
	
	private String convertCode(String code) {

//	    int num = Integer.parseInt(code.replace("BRF",""));
//
//	    return String.format("BRF-%03d", num);
		String numPart = code.replace("BRF", "");

		// if already contains leading zeros, keep it
		if (numPart.startsWith("0")) {
			return "BRF-" + numPart;
		}

		// otherwise pad to 3 digits
		int num = Integer.parseInt(numPart);
		return String.format("BRF-%03d", num);

	}

	public File findTemplate(String reportCode) {

		String pattern = convertCode(reportCode);
		System.out.println("Report Pattern : " + pattern);

		File folder = new File(templateFolder);

		File[] files = folder.listFiles();

		if (files == null)
			return null;
		/*
		 * for(File file : files){ System.out.println(file.getName()); }
		 */
		for (File file : files) {
			if (file.getName().contains(pattern)) {
				return file;
			}
		}

		return null;
	}

	// ── Evaluate cell to its actual value (handles FORMULA cells properly)
	// ────────
	private String getCellValue(Cell cell) {
		if (cell == null)
			return "";

		CellType type = cell.getCellTypeEnum();

		switch (type) {
		case STRING:
			return cell.getStringCellValue().trim();
		case NUMERIC: {
			// DataFormatter respects the cell's display format (e.g. "1.10" stays "1.10")
			String formatted = DATA_FORMATTER.formatCellValue(cell).trim();
			if (!formatted.isEmpty())
				return formatted;
			double n = cell.getNumericCellValue();
			return (n == (long) n) ? String.valueOf((long) n) : String.valueOf(n);
		}
		case BOOLEAN:
			return String.valueOf(cell.getBooleanCellValue());
		case FORMULA:
			return "";
		default:
			return "";
		}
	}

	// ── True when a cell value looks like a numbered item: "1", "1.0", "14.1" ────
	private boolean isNumericLabel(String s) {
		return s != null && STARTS_WITH_DIGIT.matcher(s.trim()).matches();
	}

	// ── True when combined text signals a stop condition
	// ──────────────────────────
	private boolean isStopRow(String b, String c, String d) {
		String bU = b.toUpperCase();
		String cU = c.toUpperCase();
		String dU = d.toUpperCase();
		// Notes section
		if (bU.startsWith("NOTE") || cU.startsWith("NOTE") || dU.startsWith("NOTE"))
			return true;
		// Form number line
		String all = bU + " " + cU + " " + dU;
		return all.contains("FORM NO") || all.contains("BSD/BRF");
	}

	// ── True when the row is a header row that must be skipped
	// ────────────────────
	private boolean isHeaderLabel(String level1) {
		return HEADER_LABELS.contains(level1.toLowerCase().trim());
	}

	// True ONLY for pure integers: "1", "2", "14" — NOT "1.1"
	private boolean isIntegerLabel(String s) {
		if (s == null || s.trim().isEmpty())
			return false;
		return s.trim().matches("^\\d+$");
	}

	// True for sub-codes: "1.1", "14.3", "2.3.4 (a)" — NOT plain "0"
	private boolean isDecimalLabel(String s) {
		if (s == null || s.trim().isEmpty())
			return false;
		return s.trim().matches("^\\d+\\.\\d+.*");
	}

	private List<String> getAvailableCols(Row row, int dataStartCol) {
		List<String> available = new ArrayList<>();
		if (row == null)
			return available;

		int lastCol = row.getLastCellNum();

		// Loop starting exactly from the dynamically saved column you passed in
		for (int col = dataStartCol; col < lastCol; col++) {
			Cell c = row.getCell(col);

			// If the cell has data, map its physical location back to a relative letter (A,
			// B, C...)
			if (c != null && c.getCellTypeEnum() != CellType.BLANK) {
				int relativeIdx = col - dataStartCol;
				String colLetter = getColumnLetter(relativeIdx);
				available.add(colLetter);
			}
		}
		return available;
	}

	// Converts indices (0, 1, 2) to Excel letters (A, B, C)
	private String getColumnLetter(int column) {
		StringBuilder result = new StringBuilder();
		while (column >= 0) {
			int remainder = column % 26;
			result.insert(0, (char) ('A' + remainder));
			column = (column / 26) - 1;
		}
		return result.toString();
	}
	
	@Autowired
	ReportTemplateConfigRepository templateConfigRepo;
	
	// ─────────────────────────────────────────────────────────────────────────────
		public Map<String, Object> readTemplate(String reportCode) throws Exception {

			Map<String, Object> result = new HashMap<>();
			List<Map<String, Object>> rows = new ArrayList<>();

			File templateFile = findTemplate(reportCode);
			Workbook workbook = WorkbookFactory.create(templateFile);
			// Sheet sheet = workbook.getSheetAt(0);
//	        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
			ReportTemplateConfig config = templateConfigRepo.findById(reportCode).orElse(null);
			int activeSheetIndex = 0;
			if (config != null && config.getSheetIndex() != null) {
				activeSheetIndex = config.getSheetIndex();
			}

			// Safety fallback just in case the saved sheet got deleted
			if (activeSheetIndex >= workbook.getNumberOfSheets())
				activeSheetIndex = 0;

			// ── 2. GRAB THE CORRECT SHEET ──
			Sheet sheet = workbook.getSheetAt(activeSheetIndex);

			/* ── 3. Read report name from row 2 (index 1)── */
			String reportName = "";
			Row nameRow = sheet.getRow(1);
			if (nameRow != null) {
				// Row 2 uses a merged cell — POI stores the value in the first cell
				// of the merge. Scan columns B→M (index 1–13) to find it.

				int lastCol = nameRow.getLastCellNum();
				System.out.println("Last Column : " + lastCol);
				for (int col = 1; col <= lastCol; col++) {
					String val = getCellValue(nameRow.getCell(col));
					if (val != null && !val.isEmpty()) {
						reportName = val;
						break;
					}
				}
			}

			int totalRows = sheet.getLastRowNum() + 1;

			/*
			 * ═══════════════════════════════════════════════════════════════ PRE-SCAN –
			 * determine: firstDataRow : first row index where B is a numbered label (or,
			 * for text-only-B files, first row with a numeric value in column C or D)
			 * lastBCNumRow : last row index (before any stop-row) where B or C carries a
			 * numbered label firstStopRow : first row index that is a Note/FormNo line
			 * ═══════════════════════════════════════════════════════════════
			 */
			int firstDataRow = -1;
			int lastBCNumRow = -1;
			int firstStopRow = totalRows;
			boolean fileHasNumB = false;
			int lastAllStringRow = -1; // for structure=0: tracks last all-STRING column-header row

			for (int i = 0; i < totalRows; i++) {
				Row row = sheet.getRow(i);
				if (row == null)
					continue;

				String b = getCellValue(row.getCell(1));
				String c = getCellValue(row.getCell(2));
				String d = getCellValue(row.getCell(3));

				if (isStopRow(b, c, d)) {
					firstStopRow = i;
					break;
				}

				boolean bNum = isNumericLabel(b);
				boolean cNum = isNumericLabel(c);
				if (bNum)
					fileHasNumB = true;

				if (firstDataRow == -1) {
					if (bNum) {
						firstDataRow = i;
					} else if (!fileHasNumB) {
						boolean hasAnyCol = false;
						boolean allString = true;
						int lastCol = row.getLastCellNum();
						for (int col = 2; col < lastCol; col++) {
							Cell dc = row.getCell(col);
							if (dc == null || dc.getCellTypeEnum() == CellType.BLANK)
								continue;
							hasAnyCol = true;
							if (dc.getCellTypeEnum() != CellType.STRING) {
								allString = false;
								break;
							}
						}
						if (hasAnyCol && allString)
							lastAllStringRow = i;
					}
				}

				if (bNum || cNum)
					lastBCNumRow = i;
			}

			// structure=0 (BRF-65 type): firstDataRow = row immediately after the
			// column-header row
			if (!fileHasNumB && firstDataRow == -1 && lastAllStringRow >= 0) {
				firstDataRow = lastAllStringRow + 1;
			}
			Integer savedHeaderStart = null;
			Integer savedStartCol = null;
			System.out.println("Report code : " + reportCode);
			// ReportTemplateConfig config =
			// templateConfigRepo.findById(reportCode).orElse(null);
			if (config != null) {
				firstDataRow = config.getFirstDataRow();
				firstStopRow = config.getFirstStopRow();
				savedHeaderStart = config.getHeaderStartRow();
				savedStartCol = config.getDataStartCol();
			}

			/*
			 * ── Detect file column structure ────────────────────────────────── structure
			 * = 3 → B=level1-int, C=level2-decimal, D=description structure = 2 →
			 * B=int-or-decimal, C=description, D=ignore structure = 0 → B=description-text,
			 * no numeric codes at all
			 * ──────────────────────────────────────────────────────────────────
			 */
			int structure;
			if (!fileHasNumB) {
				structure = 0; // text-only: BRF-65 type
			} else {
				boolean cHasDecimal = false;
				for (int i = (firstDataRow == -1 ? 0 : firstDataRow); i <= Math.min(lastBCNumRow, firstStopRow - 1); i++) {
					Row r = sheet.getRow(i);
					if (r == null)
						continue;
					if (isDecimalLabel(getCellValue(r.getCell(2)))) {
						cHasDecimal = true;
						break;
					}
				}
				structure = cHasDecimal ? 3 : 2;
			}

			List<Map<String, String>> columns = new ArrayList<>();

			if (firstDataRow >= 0) {

				int startCol = (structure == 0) ? 2 : 3;
				if (savedStartCol != null)
					startCol = savedStartCol;

				int headerStart = (structure == 0 && lastAllStringRow >= 0) ? lastAllStringRow
						: Math.max(0, firstDataRow - 6);
				if (savedHeaderStart != null)
					headerStart = savedHeaderStart;

				int endCol = 0;
				for (int r = headerStart; r <= firstDataRow; r++) {
					Row hr = sheet.getRow(r);
					if (hr != null && hr.getLastCellNum() - 1 > endCol) {
						endCol = hr.getLastCellNum() - 1;
					}
				}

				if (endCol < startCol)
					endCol = startCol;

				int totalCols = endCol - startCol + 1;
				String[] colNames = new String[totalCols];
				Arrays.fill(colNames, "");

				for (int r = headerStart; r <= firstDataRow; r++) {
					Row hr = sheet.getRow(r);
					if (hr == null)
						continue;

					for (int col = startCol; col <= endCol; col++) {

						Cell hc = hr.getCell(col);
						if (hc == null || hc.getCellTypeEnum() != CellType.STRING)
							continue;
						String val = hc.getStringCellValue().trim();

						if (!val.isEmpty()) {
							if (!colNames[col - startCol].isEmpty())
								colNames[col - startCol] += " - ";
							colNames[col - startCol] += val;
						}
					}
				}

				for (int i = 0; i < colNames.length; i++) {
					if (!colNames[i].isEmpty()) {
						Map<String, String> cm = new HashMap<>();
						cm.put("colCode", getColumnLetter(i));
						cm.put("colName", colNames[i]);
						columns.add(cm);
					}
				}
			}
			result.put("columns", columns);
//	     System.out.println("COLUMNS: " + columns);
			/*
			 * ═══════════════════════════════════════════════════════════════ MAIN SCAN –
			 * collect data rows
			 * ═══════════════════════════════════════════════════════════════
			 */

			// We no longer need a manual counter! We will calculate it based on the row
			// index.
			for (int i = 0; i < firstStopRow; i++) {
				if (i < firstDataRow) {
					continue;
				}

				// ── Calculate the exact label for this row, so it matches Excel View perfectly
				// ──
				String currentLabel = "ROW" + (101 + (i - firstDataRow));

				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				Cell colB = row.getCell(1);
				Cell colC = row.getCell(2);
				Cell colD = row.getCell(3);

				/* ── Map columns → level1 / level2 / description by structure ── */
				String rawB = getCellValue(colB);
				String rawC = getCellValue(colC);
				String rawD = getCellValue(colD);

				String level1, level2, description;

				if (structure == 3) {
					level1 = isIntegerLabel(rawB) ? rawB : "";
					level2 = isDecimalLabel(rawC) ? rawC : "";
					description = rawD;
				} else if (structure == 2) {
					if (isIntegerLabel(rawB)) {
						level1 = rawB;
						level2 = "";
						description = rawC;
					} else if (isDecimalLabel(rawB)) {
						level1 = "";
						level2 = rawB;
						description = rawC;
					} else {
						level1 = "";
						level2 = "";
						description = rawC.isEmpty() ? rawB : rawC;
					}
				} else {
					level1 = "";
					level2 = "";
					description = rawB;
				}

				/* ── Skip "No" / "S.N." / "S.No" header rows ── */
				if (isHeaderLabel(level1))
					continue;

				/* ── Disable Auto-Stop if a Manual Config is loaded ── */
				if (config == null && isStopRow(level1, level2, description))
					break;

				/* ── Disable Auto-Total-Break if a Manual Config is loaded ── */
				if (config == null && structure != 0 && lastBCNumRow >= 0 && i > lastBCNumRow) {
					String combined = (rawB + " " + rawC + " " + level1 + " " + level2 + " " + description).toUpperCase();
					if (combined.contains("TOTAL")) {

						String header = "";
						int realFormulaCount = 0;
						int headerCheckStart = (structure == 0) ? 2 : 4;
						if (savedStartCol != null)
							headerCheckStart = savedStartCol;

						int lastCol = row.getLastCellNum();
						for (int col = headerCheckStart; col < lastCol; col++) {
							Cell c = row.getCell(col);
							if (c == null || c.getCellTypeEnum() == CellType.BLANK)
								continue;
							if (c.getCellTypeEnum() == CellType.STRING)
								continue;

							if (c.getCellTypeEnum() == CellType.FORMULA) {
								String formula = c.getCellFormula().trim();
								if (!formula.matches("^\\d+(\\.\\d+)?$")) {
									realFormulaCount++;
								}
							}
						}

						if (realFormulaCount > 0) {
							header = "Y";
						}

						Map<String, Object> map = new HashMap<>();
						map.put("level1", level1);
						map.put("level2", level2);
						String totalDesc = !description.isEmpty() ? description : (!rawC.isEmpty() ? rawC : rawB);
						map.put("description", totalDesc);

						map.put("label", currentLabel); // <-- FIX: Use calculated label
						map.put("header", header);
						map.put("availableCols", getAvailableCols(row, structure));
						map.put("remarks", "");
						rows.add(map);
					}
					break; // nothing more to read after the numbered zone
				}

				/* ── Skip fully blank rows inside the data zone ── */
				if (level1.isEmpty() && level2.isEmpty() && description.isEmpty())
					continue;

				/* ── skip fake "1" rows with only text ── */
				boolean hasOnlyText = true;

				int checkStart = (structure == 0) ? 2 : 4;
				if (savedStartCol != null)
					checkStart = savedStartCol;

				for (int col = checkStart; col <= 12; col++) {

					Cell c = row.getCell(col);

					if (c == null || c.getCellTypeEnum() == CellType.BLANK)
						continue;

					// If numeric or formula exists → it's real data row
					if (c.getCellTypeEnum() == CellType.NUMERIC || c.getCellTypeEnum() == CellType.FORMULA) {
						hasOnlyText = false;
						break;
					}
				}

				if (level1.trim().equals("1") && hasOnlyText) {
					continue;
				}

				/* ── Detect formula cell in column D (marks computed/total rows) ── */

				String header = "";

				int formulaCount = 0;
				int nonEmptyCount = 0;

				int headerCheckStart = (structure == 0) ? 2 : 4;
				if (savedStartCol != null)
					headerCheckStart = savedStartCol;
				int lastCol = row.getLastCellNum();
				for (int col = headerCheckStart; col < lastCol; col++) {

					Cell c = row.getCell(col);

					if (c == null || c.getCellTypeEnum() == CellType.BLANK)
						continue;

					nonEmptyCount++;

					if (c.getCellTypeEnum() == CellType.FORMULA) {

						String formula = c.getCellFormula().trim();

						if (!formula.matches("^\\d+(\\.\\d+)?$")) {
							formulaCount++;
						}
					}
				}

				if (nonEmptyCount > 0 && formulaCount == nonEmptyCount) {
					header = "Y";
				}

				Map<String, Object> map = new HashMap<>();
				map.put("level1", level1);
				map.put("level2", level2);
				map.put("description", description);
				map.put("label", currentLabel);
				map.put("header", header);

				map.put("availableCols", getAvailableCols(row, checkStart));

				map.put("remarks", "");
				rows.add(map);
			}

			workbook.close();

			result.put("reportName", reportName);
			result.put("rows", rows);
			System.out.println("ROWS: " + rows.size());
			return result;

		}
	
	public List<String> getGLHeads(String dataType) {
		String sql = "SELECT DISTINCT GL_HEAD FROM BRF_BASE_MAPPING_TABLE "
				+ "WHERE UPPER(DATA_TYPE) = UPPER(?) ORDER BY GL_HEAD";
		return jdbcTemplate.queryForList(sql, String.class, dataType);
	}
	
	public List<Map<String, String>> getGLSubHeads(String dataType, String glHead) {
		String sql = "SELECT DISTINCT GL_SUBHEAD_CODE " + "FROM BRF_BASE_MAPPING_TABLE "
				+ "WHERE UPPER(DATA_TYPE) = UPPER(?) " + "AND UPPER(GL_HEAD) = UPPER(?) " + "ORDER BY GL_SUBHEAD_CODE";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			Map<String, String> map = new HashMap<>();
			map.put("subHeadCode", rs.getString("GL_SUBHEAD_CODE"));
			return map;
		}, dataType, glHead);
	}
	
	

	public Iterable<XBRLReportsMaster> getReportsList(String domainid) {
		logger.info("Getting Report list");

		List<String> domains = new ArrayList<String>();
		domains = Arrays.asList(domainid.split(","));

		return xbrlReportsMasterRep.getReportList(domains);

	}

	public Iterable<XBRLReportsMaster> getArchReportsList(String domainid) {
		logger.info("Getting Report list");

		List<String> domains = new ArrayList<String>();
		domains = Arrays.asList(domainid.split(","));

		return xbrlReportsMasterRep.getArchReportList(domains);

	}

	public Iterable<MISReportMasterList> getMISReportsList(String domainid) {
		logger.info("Getting MIS Report list");

		/*
		 * List<String> domains = new ArrayList<String>(); domains =
		 * Arrays.asList(domainid.split(","));
		 */

		return xbrlReportsMasterRep.getMISReportList();
	}

	public Iterable<XBRLReportsMaster> getReportsMaster() {
		logger.info("Getting Report Master");

		return xbrlReportsMasterRep.findAll();

	}

	public String updateValidity(String reportId, String valid, String userid) {

		String msg = "";
		try {
			xbrlReportsMasterRep.updateValidity(reportId, valid, userid);
			msg = "success";
		} catch (Exception e) {
			msg = "Error Occured. Please contact Administrator";
			e.printStackTrace();
		}

		return msg;

	}

	public List<ReportTitle> getDashBoardRepList(String domainid) {

		List<String> domains = new ArrayList<String>();
		domains = Arrays.asList(domainid.split(","));

		Session hs = sessionFactory1.getCurrentSession();
		@SuppressWarnings("unchecked")
		List<Object[]> reportList = hs.createNativeQuery(/*
															 * "select distinct a.parent_report_id, b.report_name, a.report_frequency, a.next_report_date, b.DOMAIN_ID, "
															 * +
															 * "case when next_report_date>sysdate then 'Y' else 'N' end as completed_flg  from report_master_tb a, xbrl_report_master_tb b "
															 * + "where a.PARENT_REPORT_ID = b.REPORT_ID " +
															 * "and b.report_validity='Y' and b.domain_id in ?1 "
															 */
				"select rpt_code,rpt_description,remarks_1,remarks_2, remarks_5,remarks_3"
						+ " from RR_RPT_MAST where rpt_status='ACTIVE' and del_flg<>'Y' and entity_flg='Y' and remarks_5 in ?1 ")
				.setParameter(1, domains).getResultList();

		List<ReportTitle> fu = new ArrayList<ReportTitle>();

		for (Object[] a : reportList) {

			String repId = (String) a[0];
			String repName = (String) a[1];
			String reportFreq = (String) a[2];
			Date reportDate = (Date) a[3];
			String domainId = (String) a[4];
			Character completedFlg = (Character) a[5];

			fu.add(new ReportTitle(repName, repId, reportDate, domainId, completedFlg, reportFreq));

		}

		return fu;

	};

	@SuppressWarnings("unchecked")
	public List<FileUpload> getFileUploadList() {
		logger.info("Getting Report lists");

		Session hs = sessionFactory1.getCurrentSession();

		List<Object[]> uploadList = hs.createNativeQuery(
				" select dpnd_report_id, report_name, report_frequency, count(*) as file_count,file_name from file_master_tb "
						+ " group by dpnd_report_id, report_name, report_frequency,file_name "
						+ " order by dpnd_report_id")
				.getResultList();

		List<FileUpload> fu = new ArrayList<FileUpload>();

		for (Object[] a : uploadList) {

			String repId = (String) a[0];
			String repName = (String) a[1];
			String reportFreq = (String) a[2];
			String fileCount = a[3].toString();
			String file_name = a[4].toString();

			fu.add(new FileUpload(repId, repName, reportFreq, fileCount, file_name));

		}

		return fu;
	}

	@SuppressWarnings("unchecked")
	public List<FileUpload> getFileUploadListCR_RBS() {
		logger.info("Getting Report list");

		Session hs = sessionFactory1.getCurrentSession();

		List<Object[]> uploadList = hs.createNativeQuery(
				" select dpnd_report_id, report_name, report_frequency, count(*) as file_count from file_master_tb_rbs, WHERE report_name ='CR_RBS_REPORTS' "
						+ " group by dpnd_report_id, report_name, report_frequency " + " order by dpnd_report_id")
				.getResultList();

		List<FileUpload> fu = new ArrayList<FileUpload>();

		for (Object[] a : uploadList) {

			String repId = (String) a[0];
			String repName = (String) a[1];
			String reportFreq = (String) a[2];
			String fileCount = a[3].toString();

			fu.add(new FileUpload(repId, repName, reportFreq, fileCount));

		}

		return fu;
	}

	@SuppressWarnings("unchecked")
	public List<ReportTitle> getReportName(String reportid) {

		logger.info("Getting Report Name :" + reportid);

		Session hs = sessionFactory1.getCurrentSession();
		List<Object[]> reportName = hs.createNativeQuery(
				"select distinct a.report_id, a.report_name from report_master_tb a where a.parent_report_id=?1 order by a.report_id")
				.setParameter(1, reportid).getResultList();

		List<ReportTitle> title = new ArrayList<ReportTitle>();

		for (Object[] a : reportName) {

			String repId = (String) a[0];
			String repName = (String) a[1];

			title.add(new ReportTitle(repName, repId));

		}

		return title;

	}

	public String getParentName(String reportid) {

		logger.info("Getting Report Name :" + reportid);

		String title = xbrlReportsMasterRep.getReportName(reportid);

		title = reportid + "-" + title;

		return title;

	}

	public String getExportpath() {
		return exportpath;
	}

	public void setExportpath(String exportpath) {
		this.exportpath = exportpath;
	}

	public String saveReport(String reportId, String asondate, String fromdate, String todate, String currency) {

		String msg = null;

		logger.info("Saving the Report : " + reportId);

		try {

			xbrlProceduresRep.ReportSaveSp(reportId, "0", asondate, fromdate, todate, currency);

			logger.info("ReportServices->saveReport()->inside try{}");
			msg = "success";

		} catch (Exception e) {
			logger.info("ReportServices->saveReport()->inside catch{}");
			msg = "failed";
		}

		return msg;
	}

	public String saveFIM0500Report(String reportId, String asondate, String fromdate, String todate, String currency,
			String reportingTime) {

		String msg = null;

		logger.info("Saving the Report : " + reportId);

		try {
			xbrlProceduresRep.ReportSaveSp(reportId, reportingTime, asondate, fromdate, todate, currency);

			logger.info("ReportServices->saveFIM0500Report()->inside try{}");
			msg = "success";

		} catch (Exception e) {
			logger.info("ReportServices->saveFIM0500Report()->inside catch{}");
			msg = "failed";
		}

		return msg;
	}

	public List<String> getDomainList() {

		return xbrlReportsMasterRep.getDomainList();
	}

	public class ReportTitle {

		String reportName;
		String reportId;
		Date report_date;
		String domain;
		Character completedFlg;
		String frequency;

		public String getReportName() {
			return reportName;
		}

		public void setReportName(String reportName) {
			this.reportName = reportName;
		}

		public String getReportId() {
			return reportId;
		}

		public void setReportId(String reportId) {
			this.reportId = reportId;
		}

		public Date getReport_date() {
			return report_date;
		}

		public void setReport_date(Date report_date) {
			this.report_date = report_date;
		}

		public String getDomain() {
			return domain;
		}

		public void setDomain(String domain) {
			this.domain = domain;
		}

		public Character getCompletedFlg() {
			return completedFlg;
		}

		public void setCompletedFlg(Character completedFlg) {
			this.completedFlg = completedFlg;
		}

		public String getFrequency() {
			return frequency;
		}

		public void setFrequency(String frequency) {
			this.frequency = frequency;
		}

		public ReportTitle(String reportName, String reportId) {
			super();
			this.reportName = reportName;
			this.reportId = reportId;
		}

		public ReportTitle(String reportName, String reportId, Date reportDate, String domain, Character completedFlg,
				String frequency) {
			super();
			this.reportName = reportName;
			this.reportId = reportId;
			this.report_date = reportDate;
			this.domain = domain;
			this.completedFlg = completedFlg;
			this.frequency = frequency;
		}

	}

	class FileUpload {

		private String dpnd_report_id;
		private String report_name;
		private String report_frequency;
		private String file_count;
		private String file_name;

		public String getDpnd_report_id() {
			return dpnd_report_id;
		}

		public void setDpnd_report_id(String dpnd_report_id) {
			this.dpnd_report_id = dpnd_report_id;
		}

		public String getReport_name() {
			return report_name;
		}

		public void setReport_name(String report_name) {
			this.report_name = report_name;
		}

		public String getReport_frequency() {
			return report_frequency;
		}

		public void setReport_frequency(String report_frequency) {
			this.report_frequency = report_frequency;
		}

		public String getFile_count() {
			return file_count;
		}

		public void setFile_count(String file_count) {
			this.file_count = file_count;
		}

		public String getFile_name() {
			return file_name;
		}

		public void setFile_name(String file_name) {
			this.file_name = file_name;
		}

		public FileUpload(String dpnd_report_id, String report_name, String report_frequency, String file_count,
				String file_name) {
			super();
			this.dpnd_report_id = dpnd_report_id;
			this.report_name = report_name;
			this.report_frequency = report_frequency;
			this.file_count = file_count;
			this.file_name = file_name;
		}

		public FileUpload(String dpnd_report_id, String report_name, String report_frequency, String file_count) {
			super();
			this.dpnd_report_id = dpnd_report_id;
			this.report_name = report_name;
			this.report_frequency = report_frequency;
			this.file_count = file_count;

		}

	}

	public File getAuditLogFile(Date fromdate, Date todate) {

		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

		String path = exportpath;
		String fileName = "AUDIT_LOGS_" + dateFormat.format(new Date()) + ".xlsx";
		File outputFile;

		File jasperFile;

		File folders = new File(path);
		if (!folders.exists()) {
			folders.mkdirs();
		}

		try {
			jasperFile = ResourceUtils.getFile("classpath:static/jasper/AUDIT_LOGS/AuditLogs.jasper");
			JasperReport jr = (JasperReport) JRLoader.loadObject(jasperFile);
			HashMap<String, Object> map = new HashMap<String, Object>();

			logger.info("Inside File Generation Method");

			logger.info("Assigning Parameters for Jasper");
			map.put("FromDate", dateFormat.format(fromdate));
			map.put("ToDate", dateFormat.format(todate));

			logger.info("Inside Method");

			path = path + "/" + fileName;
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(jp));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path));
			exporter.exportReport();
			logger.info("Excel File exported");

		} catch (FileNotFoundException | JRException | SQLException e) {

			logger.info(e.getMessage());
			logger.info("Inside catch");

			e.printStackTrace();
		}

		outputFile = new File(path);

		return outputFile;

	}

	public List<XBRLAudit> getAuditLog(Date fromdate, Date todate) {
		Session hs = sessionFactory1.getCurrentSession();

		List<XBRLAudit> ls = hs.createQuery("from XBRLAudit where audit_date between ?1 and ?2 ", XBRLAudit.class)
				.setParameter(1, fromdate).setParameter(2, todate).getResultList();

		return ls;
	}

	public String updateReportStatus(String reportid, Date asondate, Date fromdate, Date todate, String user,
			String nilflg) {

		String status = "";
		String remarks = "";
		String repStatus = "";

		if (nilflg.equals("Y")) {
			remarks = "Nil Filing";
			repStatus = "0";

		} else {

			remarks = "Report Generated";
			repStatus = "2";
		}

		Session hs = sessionFactory1.getCurrentSession();
		try {

			ReportStatusInfo rs = new ReportStatusInfo(new ReportStatusInfoId(reportid, asondate), fromdate, todate,
					repStatus, new Date(), user, remarks);
			hs.saveOrUpdate(rs);
			status = "Nil Filing Completed Successfully";

		} catch (Exception e) {

			e.printStackTrace();
			logger.info("Error While updating report status" + e.getMessage());
			status = "Update Failed. Please contact Administrator";
		}

		return status;
	}

	public String getDownloadFileFromScript(String userid, String username, String ref_id, String input1, String input2,
			String input3, String input4, String input5, String filename, String reportname)
			throws JRException, SQLException, ParseException, IOException {

		String repfile = null;
		repfile = customerrptgenserviceexcel.runSqlReport(userid, username, ref_id, input1, input2, input3, input4,
				input5, filename, reportname);
		return repfile;

	}

	public File getDownloadFile(String filename) throws JRException, SQLException, ParseException, IOException {

		File repfile = null;
		repfile = customerrptgenserviceexcel.downlaodbypath(filename);
		return repfile;

	}

	public File getDownloadFileFromdata(String userid, String username, String ref_id, String filepath,
			String downloadondelete) throws JRException, SQLException, ParseException, IOException {

		File repfile = null;
		repfile = customerrptgenserviceexcel.runSqlReportData(userid, username, ref_id, filepath, downloadondelete);
		return repfile;

	}

	public ByteArrayInputStream getDownloadFileExcel(String userid, String ref_id, String input1, String input2,
			String input3, String input4, String input5, String filename, String reportname)
			throws FileNotFoundException, JRException, SQLException, ParseException {

		ByteArrayInputStream repfile = null;
		repfile = customerrptgenserviceexcel.getFileExcel(userid, ref_id, input1, input2, input3, input4, input5,
				filename, reportname);
		return repfile;

	}

	public List<AuditServicesEntity> getAuditservices() {
		System.out.println(" inside services");
		List<AuditServicesEntity> is = AuditServicesRep.getauditService();
		System.out.println(" size is : " + is.size());
		return is;
	}

	public List<UserAuditLevel_Entity> getUserAuditLevelList() {
		System.out.println("Fetching USER_AUDIT_LEVEL data...");
		List<UserAuditLevel_Entity> result = userAuditRepo.getUserAuditList();
		System.out.println("Size: " + result.size());
		return result;
	}
	public ByteArrayInputStream generateUserAuditExcel() throws IOException {
	    List<UserAuditLevel_Entity> auditList = getUserAuditLevelList(); // Your existing method
	    
	    String[] columns = {"Audit Ref No", "Audit Table", "Function Code", "Entry User", "Entry Time", 
	                        "Auth User", "Event ID", "Event Name", "Modification Details"};

	    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	        Sheet sheet = workbook.createSheet("User Audit Logs");

	        // Create Header Font
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setColor(IndexedColors.BLACK.getIndex());

	        // Create Header CellStyle
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFont(headerFont);
	        headerCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

	        // Row for Header
	        Row headerRow = sheet.createRow(0);

	        // Create Header Cells
	        for (int col = 0; col < columns.length; col++) {
	            Cell cell = headerRow.createCell(col);
	            cell.setCellValue(columns[col]);
	            cell.setCellStyle(headerCellStyle);
	        }

	        // Create Cell Style for Dates
	        CellStyle dateCellStyle = workbook.createCellStyle();
	        CreationHelper createHelper = workbook.getCreationHelper();
	        dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy HH:mm:ss"));

	        // Fill Data
	        int rowIdx = 1;
	        for (UserAuditLevel_Entity audit : auditList) {
	            Row row = sheet.createRow(rowIdx++);

	            row.createCell(0).setCellValue(audit.getAudit_ref_no());
	            row.createCell(1).setCellValue(audit.getAudit_table());
	            row.createCell(2).setCellValue(audit.getFunc_code());
	            row.createCell(3).setCellValue(audit.getEntry_user());
	            
	            // Handle Date
	            Cell dateCell = row.createCell(4);
	            if(audit.getEntry_time() != null) {
	                dateCell.setCellValue(audit.getEntry_time());
	                dateCell.setCellStyle(dateCellStyle);
	            }

	            row.createCell(5).setCellValue(audit.getAuth_user());
	            row.createCell(6).setCellValue(audit.getEvent_id());
	            row.createCell(7).setCellValue(audit.getEvent_name());
	            row.createCell(8).setCellValue(audit.getModi_details());
	        }

	        // Auto-size columns
	        for (int i = 0; i < columns.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        workbook.write(out);
	        return new ByteArrayInputStream(out.toByteArray());
	    }
	}
	@Autowired
	MANUAL_Service_Rep mANUAL_Service_Rep;
	public ByteArrayInputStream generateServiceAuditExcel() throws IOException {
	    // 1. Fetch the data (Use the same query used for the main list)
	    List<MANUAL_Service_Entity> auditList = mANUAL_Service_Rep.findAll(); // Or your specific custom query
	    
	    String[] columns = {"Audit Ref No", "Table Name", "Function", "Entry User", "Entry Date", 
	                        "Entry Time", "Authorizer", "Modified Data"};

	    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
	        Sheet sheet = workbook.createSheet("Service Audit");

	        // Header Style
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerFont.setColor(IndexedColors.WHITE.getIndex());
	        
	        CellStyle headerCellStyle = workbook.createCellStyle();
	        headerCellStyle.setFont(headerFont);
	        headerCellStyle.setFillForegroundColor(IndexedColors.DARK_TEAL.getIndex()); // Matches your #376275 roughly
	        headerCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	        headerCellStyle.setBorderBottom(BorderStyle.THIN);
	        headerCellStyle.setBorderTop(BorderStyle.THIN);
	        headerCellStyle.setBorderRight(BorderStyle.THIN);
	        headerCellStyle.setBorderLeft(BorderStyle.THIN);

	        // Create Header
	        Row headerRow = sheet.createRow(0);
	        for (int col = 0; col < columns.length; col++) {
	            Cell cell = headerRow.createCell(col);
	            cell.setCellValue(columns[col]);
	            cell.setCellStyle(headerCellStyle);
	        }

	        // Date Formatters
	        CreationHelper createHelper = workbook.getCreationHelper();
	        CellStyle dateStyle = workbook.createCellStyle();
	        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));
	        
	        CellStyle timeStyle = workbook.createCellStyle();
	        timeStyle.setDataFormat(createHelper.createDataFormat().getFormat("HH:mm:ss"));

	        // Fill Data
	        int rowIdx = 1;
	        for (MANUAL_Service_Entity audit : auditList) {
	            Row row = sheet.createRow(rowIdx++);

	            row.createCell(0).setCellValue(audit.getAudit_ref_no());
	            row.createCell(1).setCellValue(audit.getAudit_table());
	            row.createCell(2).setCellValue(audit.getFunc_code());
	            row.createCell(3).setCellValue(audit.getEntry_user());

	            // Handle Date & Time splitting from entry_time
	            if (audit.getEntry_time() != null) {
	                Cell dateCell = row.createCell(4);
	                dateCell.setCellValue(audit.getEntry_time());
	                dateCell.setCellStyle(dateStyle);

	                Cell timeCell = row.createCell(5);
	                timeCell.setCellValue(audit.getEntry_time());
	                timeCell.setCellStyle(timeStyle);
	            } else {
	                row.createCell(4).setCellValue("N/A");
	                row.createCell(5).setCellValue("N/A");
	            }

	            row.createCell(6).setCellValue(audit.getAuth_user());
	            row.createCell(7).setCellValue(audit.getRemarks()); // Or getNew_value/Old_value logic if preferred
	        }

	        // Auto-size columns
	        for (int i = 0; i < columns.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        workbook.write(out);
	        return new ByteArrayInputStream(out.toByteArray());
	    }
	}

	public static ModelAndView getDetailSearch(Pageable pageable) {
		logger.info(" ReportServices-> getTRAN_MASTER_DETAIL()");
		ModelAndView mv = new ModelAndView();

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;

		Session hs = sessionFactory.getCurrentSession();

		List<RBSTransactionMasterEntity> detailList = hs
				.createQuery("from TRAN_MASTER_DETAIL WHERE cust_id is not null ").getResultList();

		logger.info("after the query");

		List<RBSTransactionMasterEntity> pagedlist;

		if (detailList.size() < startItem) {
			pagedlist = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, detailList.size());
			pagedlist = detailList.subList(startItem, toIndex);
		}

		Page<RBSTransactionMasterEntity> detailPage = new PageImpl<RBSTransactionMasterEntity>(pagedlist,
				PageRequest.of(currentPage, pageSize), detailList.size());

		int totalPages = detailPage.getTotalPages();
		if (totalPages > 0) {
			List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages).boxed().collect(Collectors.toList());
			mv.addObject("pageNumbers", pageNumbers);
		}

		logger.info("after pagenation");

		mv.setViewName("XBRLReportServices");
		mv.addObject("Detail", detailPage);
		mv.addObject("singledetail", new RBSTransactionMasterEntity());

		logger.info("returning mv");
		return mv;
	}

	public List<BRF_REF_CODE_ENTITY> genRefCodeDescList1() {
		System.out.println(" inside services");
		List<BRF_REF_CODE_ENTITY> is = xbrlreportmap.genRefCodeDescList2();

		return is;
	}

	public List<BRF_MAPPING_TABLE> getlist() {
		System.out.println(" inside services");
		List<BRF_REF_CODE_ENTITY> is = xbrlreportmap.genRefCodeDescList2();
		List<BRF_MAPPING_TABLE> nn = new ArrayList<BRF_MAPPING_TABLE>();
		BRF_MAPPING_TABLE bb = new BRF_MAPPING_TABLE();
		String ss = is.get(0).getRow_102();
		String[] parts = ss.split(",");
		List<String> gg = new ArrayList<String>();
		for (String f : parts) {

			bb.setAcid("kk888888");
			bb.setReport_label_1(f);

			bb.setReport_addl_criteria_1(f);

			nn.add(bb);
			System.out.println("The changed string is: " + f);
		}

		// bb.setAcid("111");
		// reportLIST_REPO.save(bb);
		return nn;
	}

	public List<BRF_MAPPING_PROPERTY> returnUnmappedObject(String report_code) {

		List<BRF_MAPPING_PROPERTY> UnMapped = new ArrayList();
		switch (report_code) {

		case "BRF1":
			UnMapped = reportlist_repo.genUnMapped();

			break;
		case "BRF2":
			UnMapped = brf2_mapping_repo.genUnMapped();
			break;

		case "BRF3":
			UnMapped = brf3_mapping_repo.genUnMapped();
			break;

		case "BRF4":
			UnMapped = brf4_mapping_repo.genUnMapped();
			break;

		case "BRF7":
			UnMapped = brf7_mapping_repo.genUnMapped();
			break;
		case "BRF8":
			UnMapped = brf8_mapping_repo.genUnMapped();
			break;
		case "BRF9":
			UnMapped = brf9_mapping_repo.genUnMapped();
			break;
		case "BRF10":
			UnMapped = brf10_mapping_repo.genUnMapped();
			break;

		case "BRF11":
			UnMapped = brf11_mapping_repo.genUnMapped();
			break;
		case "BRF12":
			UnMapped = brf12_mapping_repo.genUnMapped();
			break;
		case "BRF13":
			UnMapped = brf13_mapping_repo.genUnMapped();
			break;
		case "BRF31":
			UnMapped = brf31_mapping_repo.genUnMapped();
			break;
		case "BRF32":
			UnMapped = brf32_mapping_repo.genUnMapped();
			break;
		case "BRF34":
			UnMapped = brf34_mapping_repo.genUnMapped();
			break;
		case "BRF46":
			UnMapped = brf46_mapping_repo.genUnMapped();
			break;
		case "BRF65":
			UnMapped = brf65_mapping_repo.genUnMapped();
			break;
		case "BRF71":
			UnMapped = brf71_mapping_repo.genUnMapped();
			break;
		case "BRF74":
			UnMapped = brf74_mapping_repo.genUnMapped();
			break;
		case "BRF210":
			UnMapped = brf210_mapping_repo.genUnMapped();
			break;

		case "BRF38":
			UnMapped = brf38_mapping_repo.genUnMapped();
			break;
		case "BRF44":
			UnMapped = brf44_mapping_repo.genUnMapped();
			break;
		case "BRF64":
			UnMapped = brf64_mapping_repo.genUnMapped();
			break;
		case "BRF70":
			UnMapped = brf70_mapping_repo.genUnMapped();
			break;
		case "BRF300":
			UnMapped = brf300_mapping_repo.genUnMapped();
			break;

		case "BRF50":
			UnMapped = BRF50_MAPPING_REPO.genUnMapped();
			break;
		case "BRF62":
			UnMapped = BRF62_MAPPING_REPO.genUnMapped();
			break;
		case "BRF66":
			UnMapped = BRF66_MAPPING_REPO.genUnMapped();
			break;
		case "BRF200":
			UnMapped = BRF200_MAPPING_REPO.genUnMapped();
			break;
		case "BRF204":
			UnMapped = BRF204_MAPPING_REPO.genUnMapped();
			break;
		case "BRF107":
			UnMapped = BRF107_MAPPING_REPO.genUnMapped();
			break;
		case "BRF94":
			UnMapped = BRF94_MAPPING_REPO.genUnMapped();
			break;
		case "BRF181":
			UnMapped = BRF181_MAPPING_REPO.genUnMapped();
			break;
		case "BRF103":
			UnMapped = BRF103_MAPPING_REPO.genUnMapped();
			break;
		case "BRF104":
			UnMapped = BRF104_MAPPING_REPO.genUnMapped();
			break;
		case "BRF95":
			UnMapped = BRF95_MAPPING_REPO.genUnMapped();
			break;
		case "BRF96":
			UnMapped = BRF96_MAPPING_REPO.genUnMapped();
			break;
		case "BRF40":
			UnMapped = BRF40_MAPPING_REPO.genUnMapped();
			break;
		case "BRF41":
			UnMapped = BRF41_MAPPING_REPO.genUnMapped();
			break;
		case "BRF202":
			UnMapped = BRF202_MAPPING_REPO.genUnMapped();
			break;
		case "BRF205":
			UnMapped = BRF205_MAPPING_REPO.genUnMapped();
			break;
		case "BRF206":
			UnMapped = BRF206_MAPPING_REPO.genUnMapped();
			break;
		case "BRF151":
			UnMapped = BRF151_MAPPING_REPO.genUnMapped();
			break;
		case "BRF100":
			UnMapped = BRF100_MAPPING_REPO.genUnMapped();
			break;
		case "BRF105":
			UnMapped = BRF105_MAPPING_REPO.genUnMapped();
			break;
		case "BRF60":
			UnMapped = BRF60_MAPPING_REPO.genUnMapped();
			break;
		case "BRF68":
			UnMapped = BRF68_MAPPING_REPO.genUnMapped();
			break;
		case "BRF92":
			UnMapped = BRF92_MAPPING_REPO.genUnMapped();
			break;
		case "BRF93":
			UnMapped = BRF93_MAPPING_REPO.genUnMapped();
			break;
		case "BRF99":
			UnMapped = BRF99_MAPPING_REPO.genUnMapped();
			break;
		case "BRF73":
			UnMapped = BRF73_MAPPING_REPO.genUnMapped();
			break;
		case "BRF101":
			UnMapped = BRF101_MAPPING_REPO.genUnMapped();
			break;
		case "BRF106":
			UnMapped = BRF106_MAPPING_REPO.genUnMapped();
			break;
		case "BRF37":
			UnMapped = BRF37_MAPPING_REPO.genUnMapped();
			break;
		case "BRF67":
			UnMapped = BRF67_MAPPING_REPO.genUnMapped();
			break;
		case "BRF153":
			UnMapped = BRF153_MAPPING_REPO.genUnMapped();
			break;
		case "BRF152":
			UnMapped = BRF152_MAPPING_REPO.genUnMapped();
			break;
		case "BRF49":
			UnMapped = BRF49_MAPPING_REPO.genUnMapped();
			break;
		case "BRF56":
			UnMapped = BRF56_MAPPING_REPO.genUnMapped();
			break;
		case "BRF36":
			UnMapped = BRF36_MAPPING_REPO.genUnMapped();
			break;
		case "BRF109":
			UnMapped = BRF109_MAPPING_REPO.genUnMapped();
			break;
		case "BRF42":
			UnMapped = BRF42_MAPPING_REPO.genUnMapped();
			break;
		case "BRF48":
			UnMapped = BRF48_MAPPING_REPO.genUnMapped();
			break;
		case "BRF5":
			UnMapped = BRF5_MAPPING_REPO.genUnMapped();
			break;
		case "BRF14":
			UnMapped = BRF14_MAPPING_REPO.genUnMapped();
			break;
		case "BRF51":
			UnMapped = BRF51_MAPPING_REPO.genUnMapped();
			break;
		case "BRF52":
			UnMapped = BRF52_MAPPING_REPO.genUnMapped();
			break;
		case "BRF53":
			UnMapped = BRF53_MAPPING_REPO.genUnMapped();
			break;
		case "BRF54":
			UnMapped = BRF54_MAPPING_REPO.genUnMapped();
			break;
		case "BRF57":
			UnMapped = BRF57_MAPPING_REPO.genUnMapped();
			break;
		case "BRF207":
			UnMapped = BRF207_MAPPING_REPO.genUnMapped();
			break;
		case "BRF208":
			UnMapped = BRF208_MAPPING_REPO.genUnMapped();
			break;
		case "BRF209":
			UnMapped = BRF209_MAPPING_REPO.genUnMapped();
			break;

		}

		return UnMapped;
	}

	public List<BRF_MAPPING_TABLE> genUnMapped(String report_code) {
		System.out.println(" inside services");

		List<BRF_MAPPING_PROPERTY> is = returnUnmappedObject(report_code);
		List<BRF_MAPPING_TABLE> entityList = new ArrayList<>();

		for (BRF_MAPPING_PROPERTY array : is) {
			BRF_MAPPING_TABLE entity = new BRF_MAPPING_TABLE();

			// Assuming the order of elements in the array matches the entity's properties

			if (array.getCustid() != null) {
				entity.setCust_id(array.getCustid().toString());
			} else {
				System.out.println("Cust_ddid is null");
			}

			if (array.getForacid() != null) {
				entity.setForacid(array.getForacid().toString());
			} else {
				System.out.println("Cust_ddid is null");
			}
			if (array.getAcctname() != null) {
				entity.setAcct_name(array.getAcctname().toString());
			} else {
				System.out.println("Cust_ddid is null");
			}

			if (array.getReportlabel1() != null) {
				entity.setReport_label_1(array.getReportlabel1().toString());
			} else {
				System.out.println("Cust_ddid is null");
			}

			if (array.getReportaddlcriteria1() != null) {
				entity.setReport_addl_criteria_1(array.getReportaddlcriteria1().toString());
			} else {
				System.out.println("Cust_id is null");
			}

			if (array.getSchmcode() != null) {
				entity.setSchm_code(array.getSchmcode().toString());
			} else {
				System.out.println("Cust_id is null");
			}

			if (array.getGlsubheadcode() != null) {
				entity.setGl_sub_head_code(array.getGlsubheadcode().toString());
			} else {
				System.out.println("Cust_id is null");
			}
			if (array.getReportname1() != null) {
				entity.setReport_name_1(array.getReportname1().toString());
			} else {
				System.out.println("Cust_id is null");
			}

			entityList.add(entity);
		}
		return entityList;
	}

	public String detailChanges(BRF_MAPPING_TABLE brf_mapping_table, Character changeType) {

		String msg = "";

		try {

			Session hs = sessionFactory.getCurrentSession();

			if (changeType.equals('E')) {

				hs.saveOrUpdate(brf_mapping_table);
				logger.info("Edited Record");
				msg = "Edited Successfully";

			}

		} catch (Exception e) {

			msg = "error occured. Please contact Administrator";
			e.printStackTrace();
		}

		return msg;
	}

	public File getFile1(String report_name_1, String filetype)
			throws FileNotFoundException, JRException, SQLException {

		System.out.println("inside the path");

//		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		String path = env.getProperty("output.exportpath");
		String fileName = "";
		// String zipFileName = "";
		File outputFile = null;

		/*
		 * File pre = new File(path);
		 * 
		 * if (pre.exists()) { System.out.println("present"); } else { pre.mkdirs(); }
		 */

		// fileName = "Mapped_" + report_name_1;
		logger.info("Getting Output fileName :" + report_name_1);
		try {
			InputStream jasperFile = null;

			logger.info("Getting Jasper file :" + "Third_PARTY");
			if (report_name_1.equals("BRF1")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/MappedAccounts.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF1", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/MappedAccounts.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF1", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF2")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF2.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF2", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF2.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF2", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF3")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF3.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF3", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF3.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF3", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF4")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF4.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF4", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF4.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF4", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF7")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF7.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF7", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF7.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF7", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF8")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF8.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF8", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF8.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF8", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF9")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF9.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF9", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF9.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF9", report_name_1, filetype);
				}
			} else if (report_name_1.equals("BRF10")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF10.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF10", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF10.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF10", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF11")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF11.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF11", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF11.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF11", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF12")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF12.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF12", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF12.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF12", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF13")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF13.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF13", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF13.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF13", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF31")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF31.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF31", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF31.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF31", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF32")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF32.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF32", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF32.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF32", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF34")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF34.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF34", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF34.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF34", report_name_1, filetype);

				}
			}

			else if (report_name_1.equals("BRF46")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF46.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF46", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF46.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF46", report_name_1, filetype);

				}
			}

			else if (report_name_1.equals("BRF65")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF65.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF65", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF65.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF65", report_name_1, filetype);

				}
			}

			else if (report_name_1.equals("BRF71")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF71.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF71", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF71.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF71", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF74")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF74.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF74", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF74.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF74", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF210")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF210.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF210", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF210.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF210", report_name_1, filetype);

				}
			}

			else if (report_name_1.equals("BRF38")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF38.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF38", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF38.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF38", report_name_1, filetype);

				}
			}

			else if (report_name_1.equals("BRF44")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF44.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF44", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF44.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF44", report_name_1, filetype);

				}
			}

			else if (report_name_1.equals("BRF64")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF64.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF64", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF64.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF64", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF300")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF300.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF300", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF300.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF300", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF70")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF70.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF70", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF70.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF70", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF50")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF50.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF50", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF50.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF50", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF62")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF62.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF62", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF62.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF62", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF66")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF66.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF66", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF66.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF66", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF107")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF107.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF107", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF107.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF107", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF181")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF181.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF181", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF181.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF181", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF200")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF200.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF200", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF200.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF200", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF204")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF204.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF204", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF204.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF204", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF94")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF94.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF94", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF94.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF94", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF95")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF95.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF95", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF95.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF95", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF96")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF96.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF96", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF96.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF96", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF103")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF103.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF103", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF103.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF103", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF104")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF104.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF104", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF104.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF104", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF151")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF151.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF151", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF151.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF151", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF202")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF202.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF202", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF202.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF202", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF205")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF205.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF205", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF205.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF205", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF206")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF206.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF206", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF206.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF206", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF40")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF40.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF40", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF40.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF40", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF41")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF41.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF41", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF41.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF41", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF100")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF100.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF100", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF100.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF100", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF105")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF105.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF105", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF105.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF105", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF60")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF60.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF60", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF60.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF60", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF68")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF68.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF68", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF68.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF68", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF73")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF73.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF73", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF73.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF73", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF92")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF92.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF92", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF92.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF92", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF93")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF93.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF93", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF93.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF93", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF99")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF99.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF99", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF99.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF99", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF101")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF101.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF101", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF101.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF101", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF106")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF106.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF106", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF106.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF106", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF37")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF37.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF37", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF37.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF37", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF67")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF67.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF67", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF67.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF67", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF152")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF152.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF152", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF152.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF152", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF153")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF153.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF153", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF153.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF153", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF49")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF49.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF49", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF49.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF49", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF56")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF56.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF56", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF56.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF56", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF36")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF36.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF36", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF36.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF36", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF5")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF5.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF5", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF5.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF5", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF14")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF14.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF14", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF14.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF14", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF109")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF109.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF109", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF109.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF109", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF207")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF207.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF207", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF207.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF207", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF208")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF208.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF208", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF208.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF208", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF209")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF209.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF209", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF209.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF209", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF51")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF51.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF51", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF51.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF51", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF52")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF52.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF52", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF52.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF52", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF53")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF53.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF53", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF53.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF53", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF54")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF54.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF54", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF54.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF54", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF57")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF57.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF57", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF57.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF57", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF42")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF42.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF42", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF42.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF42", report_name_1, filetype);

				}
			} else if (report_name_1.equals("BRF48")) {
				if (filetype.equals("pdf")) {
					logger.info("pdf inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF48.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF48", report_name_1, filetype);
				} else {
					logger.info("excel inside :---");
					jasperFile = this.getClass().getResourceAsStream("/static/jasper/Mapped_BRF48.jrxml");
					outputFile = Mapped(jasperFile, "Mapped_BRF48", report_name_1, filetype);

				}
			}
		} catch (Exception e) {
			// msg = "error occured. Please contact Administrator";
			e.printStackTrace();
		}

		return outputFile;

	}

	public List<BRF_MAPPING_PROPERTY> Mapped(String report_code) {

		List<BRF_MAPPING_PROPERTY> Mapped = new ArrayList();
		switch (report_code) {

		case "BRF1":
			Mapped = reportlist_repo.genMapped(report_code);

			break;

		case "BRF2":
			Mapped = brf2_mapping_repo.genMapped(report_code);
			break;

		case "BRF3":
			Mapped = brf3_mapping_repo.genMapped(report_code);
			break;

		case "BRF4":
			Mapped = brf4_mapping_repo.genMapped(report_code);
			break;

		case "BRF7":
			Mapped = brf7_mapping_repo.genMapped(report_code);
			break;
		case "BRF8":
			Mapped = brf8_mapping_repo.genMapped(report_code);
			break;
		case "BRF9":
			Mapped = brf9_mapping_repo.genMapped(report_code);
			break;
		case "BRF10":
			Mapped = brf10_mapping_repo.genMapped(report_code);
			break;

		case "BRF11":
			Mapped = brf11_mapping_repo.genMapped(report_code);
			break;
		case "BRF12":
			Mapped = brf12_mapping_repo.genMapped(report_code);
			break;
		case "BRF13":
			Mapped = brf13_mapping_repo.genMapped(report_code);
			break;
		case "BRF31":
			Mapped = brf31_mapping_repo.genMapped(report_code);
			break;
		case "BRF32":
			Mapped = brf32_mapping_repo.genMapped(report_code);
			break;
		case "BRF34":
			Mapped = brf34_mapping_repo.genMapped(report_code);
			break;
		case "BRF46":
			Mapped = brf46_mapping_repo.genMapped(report_code);
			break;
		case "BRF65":
			Mapped = brf65_mapping_repo.genMapped(report_code);
			break;
		case "BRF71":
			Mapped = brf71_mapping_repo.genMapped(report_code);
			break;
		case "BRF74":
			Mapped = brf74_mapping_repo.genMapped(report_code);
			break;
		case "BRF210":
			Mapped = brf210_mapping_repo.genMapped(report_code);
			break;

		case "BRF38":
			Mapped = brf38_mapping_repo.genMapped(report_code);
			break;
		case "BRF44":
			Mapped = brf44_mapping_repo.genMapped(report_code);
			break;
		case "BRF64":
			Mapped = brf64_mapping_repo.genMapped(report_code);
			break;
		case "BRF300":
			Mapped = brf300_mapping_repo.genMapped(report_code);
			break;
		case "BRF70":
			Mapped = brf70_mapping_repo.genMapped(report_code);
			break;
		case "BRF50":
			Mapped = BRF50_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF62":
			Mapped = BRF62_MAPPING_REPO.genMapped(report_code);
			break;

		case "BRF66":
			Mapped = BRF66_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF107":
			Mapped = BRF107_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF181":
			Mapped = BRF181_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF200":
			Mapped = BRF200_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF204":
			Mapped = BRF204_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF94":
			Mapped = BRF94_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF95":
			Mapped = BRF95_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF96":
			Mapped = BRF96_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF103":
			Mapped = BRF103_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF104":
			Mapped = BRF104_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF151":
			Mapped = BRF151_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF40":
			Mapped = BRF40_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF41":
			Mapped = BRF41_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF202":
			Mapped = BRF202_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF205":
			Mapped = BRF205_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF206":
			Mapped = BRF206_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF100":
			Mapped = BRF100_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF105":
			Mapped = BRF105_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF60":
			Mapped = BRF60_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF68":
			Mapped = BRF68_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF73":
			Mapped = BRF73_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF92":
			Mapped = BRF206_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF93":
			Mapped = BRF93_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF99":
			Mapped = BRF99_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF49":
			Mapped = BRF49_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF56":
			Mapped = BRF56_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF101":
			Mapped = BRF101_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF106":
			Mapped = BRF106_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF152":
			Mapped = BRF152_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF153":
			Mapped = BRF153_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF37":
			Mapped = BRF37_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF67":
			Mapped = BRF67_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF5":
			Mapped = BRF5_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF14":
			Mapped = BRF14_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF36":
			Mapped = BRF36_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF42":
			Mapped = BRF42_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF48":
			Mapped = BRF48_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF51":
			Mapped = BRF51_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF52":
			Mapped = BRF52_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF53":
			Mapped = BRF53_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF54":
			Mapped = BRF54_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF57":
			Mapped = BRF57_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF109":
			Mapped = BRF109_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF207":
			Mapped = BRF207_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF208":
			Mapped = BRF208_MAPPING_REPO.genMapped(report_code);
			break;
		case "BRF209":
			Mapped = BRF209_MAPPING_REPO.genMapped(report_code);
			break;

		}
		// System.out.println(Mapped.get(0).getCustid());
		return Mapped;
	}

	// Filter Mapping Manitance
	public List<BRF_MAPPING_PROPERTY> MappingList(String report_code, Model md) {

		List<BRF_MAPPING_PROPERTY> Mapped = new ArrayList();
		switch (report_code) {

		case "BRF1":
			Mapped = reportlist_repo.genMapped(report_code);
			md.addAttribute("detailtable1", reportlist_repo.detail1());
//			md.addAttribute("detailtable3", reportlist_repo.detail4());
			md.addAttribute("detailtable5", reportlist_repo.detail5());
			md.addAttribute("detailtable6", reportlist_repo.detail6());
			break;

		case "BRF2":
			Mapped = brf2_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf2_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf2_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf2_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf2_mapping_repo.detail6());
			break;

		case "BRF3":
			Mapped = brf3_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf3_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf3_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf3_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf3_mapping_repo.detail6());
			break;

		case "BRF4":
			Mapped = brf4_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf4_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf4_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf4_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf4_mapping_repo.detail6());
			break;

		case "BRF7":
			Mapped = brf7_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf7_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf7_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf7_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf7_mapping_repo.detail6());
			break;

		case "BRF8":
			Mapped = brf8_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf8_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf8_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf8_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf8_mapping_repo.detail6());
			break;

		case "BRF9":
			Mapped = brf9_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf9_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf9_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf9_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf9_mapping_repo.detail6());
			break;

		case "BRF10":
			Mapped = brf10_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf10_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf10_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf10_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf10_mapping_repo.detail6());
			break;

		case "BRF11":
			Mapped = brf11_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf11_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf11_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf11_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf11_mapping_repo.detail6());
			break;
		case "BRF12":
			Mapped = brf12_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf12_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf12_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf12_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf12_mapping_repo.detail6());
			break;
		case "BRF13":
			Mapped = brf13_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf13_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf13_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf13_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf13_mapping_repo.detail6());
			break;
		case "BRF31":
			Mapped = brf31_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf31_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf31_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf31_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf31_mapping_repo.detail6());
			break;
		case "BRF32":
			Mapped = brf32_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf32_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf32_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf32_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf32_mapping_repo.detail6());
			break;
		case "BRF34":
			Mapped = brf34_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf34_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf34_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf34_mapping_repo.detail6());
			break;
		case "BRF46":
			Mapped = brf46_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf46_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf46_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf46_mapping_repo.detail6());
			break;
		case "BRF65":
			Mapped = brf65_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf65_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf65_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf65_mapping_repo.detail6());
			break;
		case "BRF71":
			Mapped = brf71_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf71_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf71_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf71_mapping_repo.detail6());
			break;

		case "BRF74":
			Mapped = brf74_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf74_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf74_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf74_mapping_repo.detail6());
			break;
		case "BRF210":
			Mapped = brf210_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf210_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf210_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf210_mapping_repo.detail6());
			break;

		case "BRF38":
			Mapped = brf38_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf38_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf38_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf38_mapping_repo.detail6());
			break;
		case "BRF44":
			Mapped = brf44_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf44_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf44_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf44_mapping_repo.detail6());
			break;
		case "BRF64":
			Mapped = brf64_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf64_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf64_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf64_mapping_repo.detail6());
			break;

		case "BRF300":
			Mapped = brf300_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf300_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf300_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf300_mapping_repo.detail6());
			break;
		case "BRF70":
			Mapped = brf70_mapping_repo.genMapped(report_code);
			md.addAttribute("detailtable1", brf70_mapping_repo.detail1());
//			md.addAttribute("detailtable3", brf34_mapping_repo.detail4());
			md.addAttribute("detailtable5", brf70_mapping_repo.detail5());
			md.addAttribute("detailtable6", brf70_mapping_repo.detail6());
			break;
		case "BRF50":
			Mapped = BRF50_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF50_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF50_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF50_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF50_MAPPING_REPO.detail6());
			break;
		case "BRF62":
			Mapped = BRF62_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF62_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF62_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF62_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF62_MAPPING_REPO.detail6());
			break;
		case "BRF66":
			Mapped = BRF66_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF66_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF66_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF66_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF66_MAPPING_REPO.detail6());
			break;
		case "BRF181":
			Mapped = BRF181_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF181_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF181_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF181_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF181_MAPPING_REPO.detail6());
			break;
		case "BRF107":
			Mapped = BRF107_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF107_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF107_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF107_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF107_MAPPING_REPO.detail6());
			break;
		case "BRF204":
			Mapped = BRF204_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF204_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF204_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF204_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF204_MAPPING_REPO.detail6());
			break;
		case "BRF200":
			Mapped = BRF200_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF200_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF200_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF200_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF200_MAPPING_REPO.detail6());
			break;
		case "BRF94":
			Mapped = BRF94_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF94_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF94_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF94_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF94_MAPPING_REPO.detail6());
			break;
		case "BRF95":
			Mapped = BRF95_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF95_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF95_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF95_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF95_MAPPING_REPO.detail6());
			break;
		case "BRF96":
			Mapped = BRF96_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF96_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF96_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF96_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF96_MAPPING_REPO.detail6());
			break;
		case "BRF103":
			Mapped = BRF103_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF103_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF103_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF103_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF103_MAPPING_REPO.detail6());
			break;
		case "BRF104":
			Mapped = BRF104_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF104_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF104_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF104_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF104_MAPPING_REPO.detail6());
			break;
		case "BRF151":
			Mapped = BRF151_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF151_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF151_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF151_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF151_MAPPING_REPO.detail6());
			break;
		case "BRF202":
			Mapped = BRF202_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF202_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF202_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF202_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF202_MAPPING_REPO.detail6());
			break;
		case "BRF205":
			Mapped = BRF205_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF205_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF205_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF205_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF205_MAPPING_REPO.detail6());
			break;
		case "BRF206":
			Mapped = BRF206_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF206_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF206_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF206_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF206_MAPPING_REPO.detail6());
			break;
		case "BRF40":
			Mapped = BRF40_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF40_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF40_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF40_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF40_MAPPING_REPO.detail6());
			break;
		case "BRF41":
			Mapped = BRF41_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF41_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF41_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF41_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF41_MAPPING_REPO.detail6());
			break;
		case "BRF100":
			Mapped = BRF100_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF100_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF100_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF100_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF100_MAPPING_REPO.detail6());
			break;
		case "BRF105":
			Mapped = BRF105_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF105_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF105_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF105_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF105_MAPPING_REPO.detail6());
			break;
		case "BRF73":
			Mapped = BRF73_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF73_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF73_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF73_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF73_MAPPING_REPO.detail6());
			break;
		case "BRF60":
			Mapped = BRF60_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF60_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF60_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF60_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF60_MAPPING_REPO.detail6());
			break;
		case "BRF68":
			Mapped = BRF68_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF68_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF68_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF68_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF68_MAPPING_REPO.detail6());
			break;
		case "BRF92":
			Mapped = BRF92_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF92_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF92_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF92_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF92_MAPPING_REPO.detail6());
			break;
		case "BRF93":
			Mapped = BRF93_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF93_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF93_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF93_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF93_MAPPING_REPO.detail6());
			break;
		case "BRF99":
			Mapped = BRF99_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF99_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF99_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF99_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF99_MAPPING_REPO.detail6());
			break;
		case "BRF49":
			Mapped = BRF49_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF49_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF49_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF49_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF49_MAPPING_REPO.detail6());
			break;
		case "BRF56":
			Mapped = BRF56_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF56_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF56_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF56_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF56_MAPPING_REPO.detail6());
			break;
		case "BRF37":
			Mapped = BRF37_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF37_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF37_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF37_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF37_MAPPING_REPO.detail6());
			break;
		case "BRF67":
			Mapped = BRF67_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF67_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF67_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF67_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF67_MAPPING_REPO.detail6());
			break;
		case "BRF101":
			Mapped = BRF101_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF101_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF101_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF101_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF101_MAPPING_REPO.detail6());
			break;
		case "BRF106":
			Mapped = BRF106_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF106_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF106_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF106_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF106_MAPPING_REPO.detail6());
			break;
		case "BRF152":
			Mapped = BRF152_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF152_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF152_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF152_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF152_MAPPING_REPO.detail6());
			break;
		case "BRF153":
			Mapped = BRF153_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF153_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF153_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF153_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF153_MAPPING_REPO.detail6());
			break;
		case "BRF109":
			Mapped = BRF109_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF109_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF109_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF109_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF109_MAPPING_REPO.detail6());
			break;
		case "BRF14":
			Mapped = BRF14_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF14_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF14_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF14_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF14_MAPPING_REPO.detail6());
			break;
		case "BRF5":
			Mapped = BRF5_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF5_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF5_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF5_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF5_MAPPING_REPO.detail6());
			break;
		case "BRF36":
			Mapped = BRF36_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF36_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF36_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF36_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF36_MAPPING_REPO.detail6());
			break;
		case "BRF42":
			Mapped = BRF42_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF42_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF42_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF42_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF42_MAPPING_REPO.detail6());
			break;
		case "BRF48":
			Mapped = BRF48_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF48_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF48_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF48_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF48_MAPPING_REPO.detail6());
			break;
		case "BRF51":
			Mapped = BRF51_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF51_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF51_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF51_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF51_MAPPING_REPO.detail6());
			break;
		case "BRF52":
			Mapped = BRF52_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF52_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF52_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF52_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF52_MAPPING_REPO.detail6());
			break;
		case "BRF53":
			Mapped = BRF53_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF53_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF53_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF53_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF53_MAPPING_REPO.detail6());
			break;
		case "BRF54":
			Mapped = BRF54_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF54_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF54_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF54_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF54_MAPPING_REPO.detail6());
			break;
		case "BRF207":
			Mapped = BRF207_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF207_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF207_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF207_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF207_MAPPING_REPO.detail6());
			break;
		case "BRF208":
			Mapped = BRF208_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF208_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF208_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF208_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF208_MAPPING_REPO.detail6());
			break;
		case "BRF209":
			Mapped = BRF209_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF209_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF209_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF209_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF209_MAPPING_REPO.detail6());
			break;
		case "BRF57":
			Mapped = BRF57_MAPPING_REPO.genMapped(report_code);
			md.addAttribute("detailtable1", BRF57_MAPPING_REPO.detail1());
//			md.addAttribute("detailtable3", BRF57_MAPPING_REPO.detail4());
			md.addAttribute("detailtable5", BRF57_MAPPING_REPO.detail5());
			md.addAttribute("detailtable6", BRF57_MAPPING_REPO.detail6());
			break;

		}
		return Mapped;
	}

	public BRF_MAPPING getJpaRepository(String report_code) {

		switch (report_code) {

		case "BRF1":
			return reportlist_repo;

		case "BRF2":
			return brf2_mapping_repo;
		case "BRF3":
			return brf3_mapping_repo;
		case "BRF4":
			return brf4_mapping_repo;
		case "BRF7":
			return brf7_mapping_repo;
		case "BRF8":
			return brf8_mapping_repo;
		case "BRF9":
			return brf9_mapping_repo;
		case "BRF10":
			return brf10_mapping_repo;
		case "BRF11":
			return brf11_mapping_repo;
		case "BRF12":
			return brf12_mapping_repo;
		case "BRF13":
			return brf13_mapping_repo;
		case "BRF31":
			return brf31_mapping_repo;
		case "BRF32":
			return brf32_mapping_repo;
		case "BRF34":
			return brf34_mapping_repo;
		case "BRF46":
			return brf46_mapping_repo;
		case "BRF65":
			return brf65_mapping_repo;
		case "BRF71":
			return brf71_mapping_repo;
		case "BRF74":
			return brf74_mapping_repo;
		case "BRF210":
			return brf210_mapping_repo;

		case "BRF38":
			return brf38_mapping_repo;
		case "BRF44":
			return brf44_mapping_repo;
		case "BRF64":
			return brf64_mapping_repo;
		case "BRF300":
			return brf300_mapping_repo;
		case "BRF70":
			return brf70_mapping_repo;
		case "BRF50":
			return BRF50_MAPPING_REPO;
		case "BRF62":
			return BRF62_MAPPING_REPO;
		case "BRF66":
			return BRF66_MAPPING_REPO;
		case "BRF200":
			return BRF200_MAPPING_REPO;
		case "BRF204":
			return BRF204_MAPPING_REPO;
		case "BRF107":
			return BRF107_MAPPING_REPO;
		case "BRF181":
			return BRF181_MAPPING_REPO;
		case "BRF94":
			return BRF94_MAPPING_REPO;
		case "BRF95":
			return BRF95_MAPPING_REPO;
		case "BRF96":
			return BRF96_MAPPING_REPO;
		case "BRF103":
			return BRF103_MAPPING_REPO;
		case "BRF104":
			return BRF104_MAPPING_REPO;
		case "BRF151":
			return BRF151_MAPPING_REPO;
		case "BRF40":
			return BRF40_MAPPING_REPO;
		case "BRF41":
			return BRF41_MAPPING_REPO;
		case "BRF202":
			return BRF202_MAPPING_REPO;
		case "BRF205":
			return BRF205_MAPPING_REPO;
		case "BRF206":
			return BRF206_MAPPING_REPO;
		case "BRF100":
			return BRF100_MAPPING_REPO;
		case "BRF105":
			return BRF105_MAPPING_REPO;
		case "BRF60":
			return BRF60_MAPPING_REPO;
		case "BRF68":
			return BRF68_MAPPING_REPO;
		case "BRF73":
			return BRF73_MAPPING_REPO;
		case "BRF92":
			return BRF92_MAPPING_REPO;
		case "BRF93":
			return BRF93_MAPPING_REPO;
		case "BRF99":
			return BRF99_MAPPING_REPO;
		case "BRF49":
			return BRF49_MAPPING_REPO;
		case "BRF56":
			return BRF56_MAPPING_REPO;
		case "BRF37":
			return BRF37_MAPPING_REPO;
		case "BRF67":
			return BRF67_MAPPING_REPO;
		case "BRF101":
			return BRF101_MAPPING_REPO;
		case "BRF106":
			return BRF106_MAPPING_REPO;
		case "BRF152":
			return BRF152_MAPPING_REPO;
		case "BRF153":
			return BRF153_MAPPING_REPO;
		case "BRF109":
			return BRF109_MAPPING_REPO;
		case "BRF14":
			return BRF14_MAPPING_REPO;
		case "BRF5":
			return BRF5_MAPPING_REPO;
		case "BRF36":
			return BRF36_MAPPING_REPO;
		case "BRF42":
			return BRF42_MAPPING_REPO;
		case "BRF48":
			return BRF153_MAPPING_REPO;
		case "BRF51":
			return BRF51_MAPPING_REPO;
		case "BRF52":
			return BRF52_MAPPING_REPO;
		case "BRF53":
			return BRF53_MAPPING_REPO;
		case "BRF54":
			return BRF54_MAPPING_REPO;
		case "BRF57":
			return BRF57_MAPPING_REPO;
		case "BRF207":
			return BRF207_MAPPING_REPO;
		case "BRF208":
			return BRF208_MAPPING_REPO;
		case "BRF209":
			return BRF209_MAPPING_REPO;
		default:
			return null;
		}
	}

	public BRF_MAPPING getJpaRepositoryEdit(String report_code, String foracid, BRF_MAPPING_TABLE brfmap) {

		switch (report_code) {

		case "BRF1": {
			BRF_MAPPING_PARENT Bp = seteditData(reportlist_repo.findById(foracid).get(), brfmap);
			reportlist_repo.save((BRF_MAPPING_ENTITY) Bp);
			return reportlist_repo;
		}

		case "BRF2": {
			BRF_MAPPING_PARENT Bp = seteditData(brf2_mapping_repo.findById(foracid).get(), brfmap);
			brf2_mapping_repo.save((BRF2_MAPPING_ENTITY) Bp);
			return brf2_mapping_repo;
		}

		case "BRF3": {
			BRF_MAPPING_PARENT Bp = seteditData(brf3_mapping_repo.findById(foracid).get(), brfmap);
			brf3_mapping_repo.save((BRF3_MAPPING_ENTITY) Bp);
			return brf3_mapping_repo;
		}
		case "BRF4": {
			BRF_MAPPING_PARENT Bp = seteditData(brf4_mapping_repo.findById(foracid).get(), brfmap);
			brf4_mapping_repo.save((BRF4_MAPPING_ENTITY) Bp);
			return brf4_mapping_repo;
		}
		case "BRF7": {
			BRF_MAPPING_PARENT Bp = seteditData(brf7_mapping_repo.findById(foracid).get(), brfmap);
			brf7_mapping_repo.save((BRF7_MAPPING_ENTITY) Bp);
			return brf7_mapping_repo;
		}
		case "BRF8": {
			BRF_MAPPING_PARENT Bp = seteditData(brf8_mapping_repo.findById(foracid).get(), brfmap);
			brf8_mapping_repo.save((BRF8_MAPPING_ENTITY) Bp);
			return brf7_mapping_repo;
		}
		case "BRF9": {
			BRF_MAPPING_PARENT Bp = seteditData(brf9_mapping_repo.findById(foracid).get(), brfmap);
			brf9_mapping_repo.save((BRF9_MAPPING_ENTITY) Bp);
			return brf9_mapping_repo;
		}
		case "BRF10": {
			BRF_MAPPING_PARENT Bp = seteditData(brf10_mapping_repo.findById(foracid).get(), brfmap);
			brf10_mapping_repo.save((BRF10_MAPPING_ENTITY) Bp);
			return brf10_mapping_repo;
		}

		case "BRF11": {
			BRF_MAPPING_PARENT Bp = seteditData(brf11_mapping_repo.findById(foracid).get(), brfmap);
			brf11_mapping_repo.save((BRF11_MAPPING_ENTITY) Bp);
			return brf11_mapping_repo;
		}

		case "BRF12": {
			BRF_MAPPING_PARENT Bp = seteditData(brf12_mapping_repo.findById(foracid).get(), brfmap);
			brf12_mapping_repo.save((BRF12_MAPPING_ENTITY) Bp);
			return brf12_mapping_repo;
		}
		case "BRF13": {
			BRF_MAPPING_PARENT Bp = seteditData(brf13_mapping_repo.findById(foracid).get(), brfmap);
			brf13_mapping_repo.save((BRF13_MAPPING_ENTITY) Bp);
			return brf13_mapping_repo;
		}
		case "BRF31": {
			BRF_MAPPING_PARENT Bp = seteditData(brf31_mapping_repo.findById(foracid).get(), brfmap);
			brf31_mapping_repo.save((BRF31_MAPPING_ENTITY) Bp);
			return brf31_mapping_repo;
		}
		case "BRF32": {
			BRF_MAPPING_PARENT Bp = seteditData(brf32_mapping_repo.findById(foracid).get(), brfmap);
			brf32_mapping_repo.save((BRF32_MAPPING_ENTITY) Bp);
			return brf32_mapping_repo;
		}
		case "BRF34": {
			BRF_MAPPING_PARENT Bp = seteditData(brf34_mapping_repo.findById(foracid).get(), brfmap);
			brf34_mapping_repo.save((BRF34_MAPPING_ENTITY) Bp);
			return brf34_mapping_repo;
		}

		case "BRF46": {
			BRF_MAPPING_PARENT Bp = seteditData(brf46_mapping_repo.findById(foracid).get(), brfmap);
			brf46_mapping_repo.save((BRF46_MAPPING_ENTITY) Bp);
			return brf46_mapping_repo;
		}

		case "BRF65": {
			BRF_MAPPING_PARENT Bp = seteditData(brf65_mapping_repo.findById(foracid).get(), brfmap);
			brf65_mapping_repo.save((BRF65_MAPPING_ENTITY) Bp);
			return brf65_mapping_repo;
		}
		case "BRF71": {
			BRF_MAPPING_PARENT Bp = seteditData(brf71_mapping_repo.findById(foracid).get(), brfmap);
			brf71_mapping_repo.save((BRF71_MAPPING_ENTITY) Bp);
			return brf71_mapping_repo;
		}
		case "BRF74": {
			BRF_MAPPING_PARENT Bp = seteditData(brf74_mapping_repo.findById(foracid).get(), brfmap);
			brf74_mapping_repo.save((BRF74_MAPPING_ENTITY) Bp);
			return brf74_mapping_repo;
		}

		case "BRF210": {
			BRF_MAPPING_PARENT Bp = seteditData(brf210_mapping_repo.findById(foracid).get(), brfmap);
			brf210_mapping_repo.save((BRF210_MAPPING_ENTITY) Bp);
			return brf210_mapping_repo;
		}

		case "BRF38": {
			BRF_MAPPING_PARENT Bp = seteditData(brf38_mapping_repo.findById(foracid).get(), brfmap);
			brf38_mapping_repo.save((BRF38_MAPPING_ENTITY) Bp);
			return brf38_mapping_repo;
		}

		case "BRF44": {
			BRF_MAPPING_PARENT Bp = seteditData(brf44_mapping_repo.findById(foracid).get(), brfmap);
			brf44_mapping_repo.save((BRF44_MAPPING_ENTITY) Bp);
			return brf44_mapping_repo;
		}
		case "BRF64": {
			BRF_MAPPING_PARENT Bp = seteditData(brf64_mapping_repo.findById(foracid).get(), brfmap);
			brf64_mapping_repo.save((BRF64_MAPPING_ENTITY) Bp);
			return brf64_mapping_repo;
		}
		case "BRF300": {
			BRF_MAPPING_PARENT Bp = seteditData(brf300_mapping_repo.findById(foracid).get(), brfmap);
			brf300_mapping_repo.save((BRF300_MAPPING_ENTITY) Bp);
			return brf300_mapping_repo;
		}

		case "BRF70": {
			BRF_MAPPING_PARENT Bp = seteditData(brf70_mapping_repo.findById(foracid).get(), brfmap);
			brf70_mapping_repo.save((BRF70_MAPPING_ENTITY) Bp);
			return brf70_mapping_repo;
		}
		case "BRF50": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF50_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF50_MAPPING_REPO.save((BRF50_MAPPING_ENTITY) Bp);
			return BRF50_MAPPING_REPO;
		}
		case "BRF62": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF62_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF62_MAPPING_REPO.save((BRF62_MAPPING_ENTITY) Bp);
			return BRF62_MAPPING_REPO;
		}
		case "BRF66": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF66_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF66_MAPPING_REPO.save((BRF66_MAPPING_ENTITY) Bp);
			return BRF66_MAPPING_REPO;
		}
		case "BRF94": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF94_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF94_MAPPING_REPO.save((BRF94_MAPPING_ENTITY) Bp);
			return BRF94_MAPPING_REPO;
		}
		case "BRF107": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF107_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF107_MAPPING_REPO.save((BRF107_MAPPING_ENTITY) Bp);
			return BRF107_MAPPING_REPO;
		}
		case "BRF181": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF181_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF181_MAPPING_REPO.save((BRF181_MAPPING_ENTITY) Bp);
			return BRF181_MAPPING_REPO;
		}
		case "BRF200": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF200_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF200_MAPPING_REPO.save((BRF200_MAPPING_ENTITY) Bp);
			return BRF200_MAPPING_REPO;
		}
		case "BRF204": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF204_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF204_MAPPING_REPO.save((BRF204_MAPPING_ENTITY) Bp);
			return BRF204_MAPPING_REPO;
		}
		case "BRF104": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF104_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF104_MAPPING_REPO.save((BRF104_MAPPING_ENTITY) Bp);
			return BRF104_MAPPING_REPO;
		}
		case "BRF103": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF103_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF103_MAPPING_REPO.save((BRF103_MAPPING_ENTITY) Bp);
			return BRF103_MAPPING_REPO;
		}
		case "BRF95": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF95_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF95_MAPPING_REPO.save((BRF95_MAPPING_ENTITY) Bp);
			return BRF95_MAPPING_REPO;
		}
		case "BRF96": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF96_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF96_MAPPING_REPO.save((BRF96_MAPPING_ENTITY) Bp);
			return BRF96_MAPPING_REPO;
		}
		case "BRF151": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF151_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF151_MAPPING_REPO.save((BRF151_MAPPING_ENTITY) Bp);
			return BRF151_MAPPING_REPO;
		}
		case "BRF40": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF40_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF40_MAPPING_REPO.save((BRF40_MAPPING_ENTITY) Bp);
			return BRF40_MAPPING_REPO;
		}
		case "BRF41": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF41_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF41_MAPPING_REPO.save((BRF41_MAPPING_ENTITY) Bp);
			return BRF41_MAPPING_REPO;
		}
		case "BRF202": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF202_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF202_MAPPING_REPO.save((BRF202_MAPPING_ENTITY) Bp);
			return BRF202_MAPPING_REPO;
		}
		case "BRF205": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF205_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF205_MAPPING_REPO.save((BRF205_MAPPING_ENTITY) Bp);
			return BRF205_MAPPING_REPO;
		}
		case "BRF206": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF206_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF206_MAPPING_REPO.save((BRF206_MAPPING_ENTITY) Bp);
			return BRF206_MAPPING_REPO;
		}
		case "BRF100": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF100_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF100_MAPPING_REPO.save((BRF100_MAPPING_ENTITY) Bp);
			return BRF100_MAPPING_REPO;
		}
		case "BRF105": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF105_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF105_MAPPING_REPO.save((BRF105_MAPPING_ENTITY) Bp);
			return BRF105_MAPPING_REPO;
		}
		case "BRF60": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF60_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF60_MAPPING_REPO.save((BRF60_MAPPING_ENTITY) Bp);
			return BRF60_MAPPING_REPO;
		}
		case "BRF68": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF68_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF68_MAPPING_REPO.save((BRF68_MAPPING_ENTITY) Bp);
			return BRF68_MAPPING_REPO;
		}
		case "BRF73": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF73_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF73_MAPPING_REPO.save((BRF73_MAPPING_ENTITY) Bp);
			return BRF73_MAPPING_REPO;
		}
		case "BRF92": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF92_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF92_MAPPING_REPO.save((BRF92_MAPPING_ENTITY) Bp);
			return BRF92_MAPPING_REPO;
		}
		case "BRF93": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF93_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF93_MAPPING_REPO.save((BRF93_MAPPING_ENTITY) Bp);
			return BRF93_MAPPING_REPO;
		}
		case "BRF99": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF99_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF99_MAPPING_REPO.save((BRF99_MAPPING_ENTITY) Bp);
			return BRF99_MAPPING_REPO;
		}
		case "BRF49": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF49_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF49_MAPPING_REPO.save((BRF49_MAPPING_ENTITY) Bp);
			return BRF49_MAPPING_REPO;
		}
		case "BRF56": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF56_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF56_MAPPING_REPO.save((BRF56_MAPPING_ENTITY) Bp);
			return BRF56_MAPPING_REPO;
		}
		case "BRF37": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF37_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF37_MAPPING_REPO.save((BRF37_MAPPING_ENTITY) Bp);
			return BRF37_MAPPING_REPO;
		}
		case "BRF67": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF67_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF67_MAPPING_REPO.save((BRF67_MAPPING_ENTITY) Bp);
			return BRF67_MAPPING_REPO;
		}
		case "BRF101": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF101_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF101_MAPPING_REPO.save((BRF101_MAPPING_ENTITY) Bp);
			return BRF101_MAPPING_REPO;
		}
		case "BRF106": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF106_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF106_MAPPING_REPO.save((BRF106_MAPPING_ENTITY) Bp);
			return BRF106_MAPPING_REPO;
		}
		case "BRF152": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF152_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF152_MAPPING_REPO.save((BRF152_MAPPING_ENTITY) Bp);
			return BRF152_MAPPING_REPO;
		}
		case "BRF153": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF153_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF153_MAPPING_REPO.save((BRF153_MAPPING_ENTITY) Bp);
			return BRF153_MAPPING_REPO;
		}
		case "BRF5": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF5_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF5_MAPPING_REPO.save((BRF5_MAPPING_ENTITY) Bp);
			return BRF5_MAPPING_REPO;
		}
		case "BRF14": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF14_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF14_MAPPING_REPO.save((BRF14_MAPPING_ENTITY) Bp);
			return BRF14_MAPPING_REPO;
		}
		case "BRF36": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF36_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF36_MAPPING_REPO.save((BRF36_MAPPING_ENTITY) Bp);
			return BRF36_MAPPING_REPO;
		}
		case "BRF109": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF109_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF109_MAPPING_REPO.save((BRF109_MAPPING_ENTITY) Bp);
			return BRF109_MAPPING_REPO;
		}
		case "BRF42": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF42_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF42_MAPPING_REPO.save((BRF42_MAPPING_ENTITY) Bp);
			return BRF42_MAPPING_REPO;
		}
		case "BRF48": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF48_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF48_MAPPING_REPO.save((BRF48_MAPPING_ENTITY) Bp);
			return BRF48_MAPPING_REPO;
		}
		case "BRF51": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF51_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF51_MAPPING_REPO.save((BRF51_MAPPING_ENTITY) Bp);
			return BRF51_MAPPING_REPO;
		}
		case "BRF52": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF52_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF52_MAPPING_REPO.save((BRF52_MAPPING_ENTITY) Bp);
			return BRF52_MAPPING_REPO;
		}
		case "BRF53": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF53_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF53_MAPPING_REPO.save((BRF53_MAPPING_ENTITY) Bp);
			return BRF53_MAPPING_REPO;
		}
		case "BRF54": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF54_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF54_MAPPING_REPO.save((BRF54_MAPPING_ENTITY) Bp);
			return BRF54_MAPPING_REPO;
		}
		case "BRF57": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF57_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF57_MAPPING_REPO.save((BRF57_MAPPING_ENTITY) Bp);
			return BRF57_MAPPING_REPO;
		}
		case "BRF207": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF207_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF207_MAPPING_REPO.save((BRF207_MAPPING_ENTITY) Bp);
			return BRF207_MAPPING_REPO;
		}
		case "BRF208": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF208_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF208_MAPPING_REPO.save((BRF208_MAPPING_ENTITY) Bp);
			return BRF208_MAPPING_REPO;
		}
		case "BRF209": {
			BRF_MAPPING_PARENT Bp = seteditData(BRF209_MAPPING_REPO.findById(foracid).get(), brfmap);
			BRF209_MAPPING_REPO.save((BRF209_MAPPING_ENTITY) Bp);
			return BRF209_MAPPING_REPO;
		}
		default:
			return null;
		}
	}

	public BRF_MAPPING_PARENT setMappingLabel(BRF_MAPPING_PARENT bmp, String id, String select) {

		bmp.setReport_name_1(select);
		// bmp.setReport_addl_criteria_1(name);
		bmp.setReport_label_1(id);

		return bmp;
	}

//Mapping Manitance MappingLabel
	public BRF_MAPPING getJpaRepositoryMappingLabel(String id, String select, String specificCellValue) {
		switch (select) {

		case "BRF1": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(reportlist_repo.findById(specificCellValue).get(), id, select);

			reportlist_repo.save((BRF_MAPPING_ENTITY) Bp);
			return reportlist_repo;
		}

		case "BRF2": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf2_mapping_repo.findById(specificCellValue).get(), id, select);

			brf2_mapping_repo.save((BRF2_MAPPING_ENTITY) Bp);
			return brf2_mapping_repo;
		}
		case "BRF3": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf3_mapping_repo.findById(specificCellValue).get(), id, select);

			brf3_mapping_repo.save((BRF3_MAPPING_ENTITY) Bp);
			return brf3_mapping_repo;
		}

		case "BRF4": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf4_mapping_repo.findById(specificCellValue).get(), id, select);
			brf4_mapping_repo.save((BRF4_MAPPING_ENTITY) Bp);
			return brf4_mapping_repo;
		}

		case "BRF7": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf7_mapping_repo.findById(specificCellValue).get(), id, select);
			brf7_mapping_repo.save((BRF7_MAPPING_ENTITY) Bp);
			return brf7_mapping_repo;
		}
		case "BRF8": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf8_mapping_repo.findById(specificCellValue).get(), id, select);
			brf8_mapping_repo.save((BRF8_MAPPING_ENTITY) Bp);
			return brf8_mapping_repo;
		}
		case "BRF9": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf9_mapping_repo.findById(specificCellValue).get(), id, select);
			brf9_mapping_repo.save((BRF9_MAPPING_ENTITY) Bp);
			return brf9_mapping_repo;
		}
		case "BRF10": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf10_mapping_repo.findById(specificCellValue).get(), id, select);
			brf10_mapping_repo.save((BRF10_MAPPING_ENTITY) Bp);
			return brf10_mapping_repo;
		}

		case "BRF11": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf11_mapping_repo.findById(specificCellValue).get(), id, select);

			brf11_mapping_repo.save((BRF11_MAPPING_ENTITY) Bp);
			return brf11_mapping_repo;
		}
		case "BRF12": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf12_mapping_repo.findById(specificCellValue).get(), id, select);
			brf12_mapping_repo.save((BRF12_MAPPING_ENTITY) Bp);
			return brf12_mapping_repo;
		}
		case "BRF13": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf13_mapping_repo.findById(specificCellValue).get(), id, select);
			brf13_mapping_repo.save((BRF13_MAPPING_ENTITY) Bp);
			return brf13_mapping_repo;
		}
		case "BRF31": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf31_mapping_repo.findById(specificCellValue).get(), id, select);
			brf31_mapping_repo.save((BRF31_MAPPING_ENTITY) Bp);
			return brf31_mapping_repo;
		}
		case "BRF32": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf32_mapping_repo.findById(specificCellValue).get(), id, select);
			brf32_mapping_repo.save((BRF32_MAPPING_ENTITY) Bp);
			return brf32_mapping_repo;
		}
		case "BRF34": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf34_mapping_repo.findById(specificCellValue).get(), id, select);
			brf34_mapping_repo.save((BRF34_MAPPING_ENTITY) Bp);
			return brf34_mapping_repo;
		}

		case "BRF46": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf46_mapping_repo.findById(specificCellValue).get(), id, select);
			brf46_mapping_repo.save((BRF46_MAPPING_ENTITY) Bp);
			return brf46_mapping_repo;
		}

		case "BRF65": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf65_mapping_repo.findById(specificCellValue).get(), id, select);
			brf65_mapping_repo.save((BRF65_MAPPING_ENTITY) Bp);
			return brf65_mapping_repo;
		}
		case "BRF71": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf71_mapping_repo.findById(specificCellValue).get(), id, select);
			brf71_mapping_repo.save((BRF71_MAPPING_ENTITY) Bp);
			return brf71_mapping_repo;
		}
		case "BRF74": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf74_mapping_repo.findById(specificCellValue).get(), id, select);
			brf74_mapping_repo.save((BRF74_MAPPING_ENTITY) Bp);
			return brf74_mapping_repo;
		}
		case "BRF210": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf210_mapping_repo.findById(specificCellValue).get(), id, select);
			brf210_mapping_repo.save((BRF210_MAPPING_ENTITY) Bp);
			return brf210_mapping_repo;
		}

		case "BRF38": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf38_mapping_repo.findById(specificCellValue).get(), id, select);
			brf38_mapping_repo.save((BRF38_MAPPING_ENTITY) Bp);
			return brf38_mapping_repo;
		}

		case "BRF44": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf44_mapping_repo.findById(specificCellValue).get(), id, select);
			brf44_mapping_repo.save((BRF44_MAPPING_ENTITY) Bp);
			return brf44_mapping_repo;
		}
		case "BRF64": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf64_mapping_repo.findById(specificCellValue).get(), id, select);
			brf64_mapping_repo.save((BRF64_MAPPING_ENTITY) Bp);
			return brf64_mapping_repo;
		}
		case "BRF300": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf300_mapping_repo.findById(specificCellValue).get(), id, select);
			brf300_mapping_repo.save((BRF300_MAPPING_ENTITY) Bp);
			return brf300_mapping_repo;
		}
		case "BRF70": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(brf70_mapping_repo.findById(specificCellValue).get(), id, select);
			brf70_mapping_repo.save((BRF70_MAPPING_ENTITY) Bp);
			return brf70_mapping_repo;
		}
		case "BRF50": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF50_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF50_MAPPING_REPO.save((BRF50_MAPPING_ENTITY) Bp);
			return BRF50_MAPPING_REPO;
		}
		case "BRF62": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF62_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF62_MAPPING_REPO.save((BRF62_MAPPING_ENTITY) Bp);
			return BRF62_MAPPING_REPO;
		}
		case "BRF66": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF66_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF66_MAPPING_REPO.save((BRF66_MAPPING_ENTITY) Bp);
			return BRF66_MAPPING_REPO;
		}
		case "BRF181": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF181_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF181_MAPPING_REPO.save((BRF181_MAPPING_ENTITY) Bp);
			return BRF181_MAPPING_REPO;
		}
		case "BRF107": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF107_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF107_MAPPING_REPO.save((BRF107_MAPPING_ENTITY) Bp);
			return BRF107_MAPPING_REPO;
		}
		case "BRF94": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF94_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF94_MAPPING_REPO.save((BRF94_MAPPING_ENTITY) Bp);
			return BRF94_MAPPING_REPO;
		}
		case "BRF204": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF204_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF204_MAPPING_REPO.save((BRF204_MAPPING_ENTITY) Bp);
			return BRF204_MAPPING_REPO;
		}
		case "BRF200": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF200_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF200_MAPPING_REPO.save((BRF200_MAPPING_ENTITY) Bp);
			return BRF200_MAPPING_REPO;
		}
		case "BRF95": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF95_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF95_MAPPING_REPO.save((BRF95_MAPPING_ENTITY) Bp);
			return BRF95_MAPPING_REPO;
		}
		case "BRF96": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF96_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF96_MAPPING_REPO.save((BRF96_MAPPING_ENTITY) Bp);
			return BRF96_MAPPING_REPO;
		}
		case "BRF103": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF103_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF103_MAPPING_REPO.save((BRF103_MAPPING_ENTITY) Bp);
			return BRF103_MAPPING_REPO;
		}
		case "BRF104": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF104_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF104_MAPPING_REPO.save((BRF104_MAPPING_ENTITY) Bp);
			return BRF104_MAPPING_REPO;
		}
		case "BRF40": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF40_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF40_MAPPING_REPO.save((BRF40_MAPPING_ENTITY) Bp);
			return BRF40_MAPPING_REPO;
		}
		case "BRF41": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF41_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF41_MAPPING_REPO.save((BRF41_MAPPING_ENTITY) Bp);
			return BRF41_MAPPING_REPO;
		}
		case "BRF151": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF151_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF151_MAPPING_REPO.save((BRF151_MAPPING_ENTITY) Bp);
			return BRF151_MAPPING_REPO;
		}
		case "BRF202": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF202_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF202_MAPPING_REPO.save((BRF202_MAPPING_ENTITY) Bp);
			return BRF202_MAPPING_REPO;
		}
		case "BRF205": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF205_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF205_MAPPING_REPO.save((BRF205_MAPPING_ENTITY) Bp);
			return BRF205_MAPPING_REPO;
		}
		case "BRF206": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF206_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF206_MAPPING_REPO.save((BRF206_MAPPING_ENTITY) Bp);
			return BRF206_MAPPING_REPO;
		}
		case "BRF100": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF100_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF100_MAPPING_REPO.save((BRF100_MAPPING_ENTITY) Bp);
			return BRF100_MAPPING_REPO;
		}
		case "BRF105": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF105_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF105_MAPPING_REPO.save((BRF105_MAPPING_ENTITY) Bp);
			return BRF105_MAPPING_REPO;
		}
		case "BRF60": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF60_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF60_MAPPING_REPO.save((BRF60_MAPPING_ENTITY) Bp);
			return BRF60_MAPPING_REPO;
		}
		case "BRF68": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF68_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF68_MAPPING_REPO.save((BRF68_MAPPING_ENTITY) Bp);
			return BRF68_MAPPING_REPO;
		}
		case "BRF73": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF73_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF73_MAPPING_REPO.save((BRF73_MAPPING_ENTITY) Bp);
			return BRF73_MAPPING_REPO;
		}
		case "BRF92": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF92_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF92_MAPPING_REPO.save((BRF92_MAPPING_ENTITY) Bp);
			return BRF92_MAPPING_REPO;
		}
		case "BRF93": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF93_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF93_MAPPING_REPO.save((BRF93_MAPPING_ENTITY) Bp);
			return BRF93_MAPPING_REPO;
		}
		case "BRF99": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF99_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF99_MAPPING_REPO.save((BRF99_MAPPING_ENTITY) Bp);
			return BRF99_MAPPING_REPO;
		}
		case "BRF49": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF49_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF49_MAPPING_REPO.save((BRF49_MAPPING_ENTITY) Bp);
			return BRF49_MAPPING_REPO;
		}
		case "BRF56": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF56_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF56_MAPPING_REPO.save((BRF56_MAPPING_ENTITY) Bp);
			return BRF56_MAPPING_REPO;
		}
		case "BRF37": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF37_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF37_MAPPING_REPO.save((BRF37_MAPPING_ENTITY) Bp);
			return BRF37_MAPPING_REPO;
		}
		case "BRF67": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF67_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF67_MAPPING_REPO.save((BRF67_MAPPING_ENTITY) Bp);
			return BRF67_MAPPING_REPO;
		}
		case "BRF101": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF101_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF101_MAPPING_REPO.save((BRF101_MAPPING_ENTITY) Bp);
			return BRF101_MAPPING_REPO;
		}
		case "BRF106": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF106_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF106_MAPPING_REPO.save((BRF106_MAPPING_ENTITY) Bp);
			return BRF106_MAPPING_REPO;
		}
		case "BRF152": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF152_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF152_MAPPING_REPO.save((BRF152_MAPPING_ENTITY) Bp);
			return BRF152_MAPPING_REPO;
		}
		case "BRF153": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF153_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF153_MAPPING_REPO.save((BRF153_MAPPING_ENTITY) Bp);
			return BRF153_MAPPING_REPO;
		}
		case "BRF53": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF53_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF53_MAPPING_REPO.save((BRF53_MAPPING_ENTITY) Bp);
			return BRF53_MAPPING_REPO;
		}
		case "BRF14": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF14_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF14_MAPPING_REPO.save((BRF14_MAPPING_ENTITY) Bp);
			return BRF14_MAPPING_REPO;
		}
		case "BRF5": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF5_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF5_MAPPING_REPO.save((BRF5_MAPPING_ENTITY) Bp);
			return BRF5_MAPPING_REPO;
		}
		case "BRF109": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF109_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF109_MAPPING_REPO.save((BRF109_MAPPING_ENTITY) Bp);
			return BRF109_MAPPING_REPO;
		}
		case "BRF36": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF36_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF36_MAPPING_REPO.save((BRF36_MAPPING_ENTITY) Bp);
			return BRF36_MAPPING_REPO;
		}
		case "BRF57": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF57_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF57_MAPPING_REPO.save((BRF57_MAPPING_ENTITY) Bp);
			return BRF57_MAPPING_REPO;
		}
		case "BRF51": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF51_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF51_MAPPING_REPO.save((BRF51_MAPPING_ENTITY) Bp);
			return BRF51_MAPPING_REPO;
		}
		case "BRF52": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF52_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF52_MAPPING_REPO.save((BRF52_MAPPING_ENTITY) Bp);
			return BRF52_MAPPING_REPO;
		}
		case "BRF54": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF54_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF54_MAPPING_REPO.save((BRF54_MAPPING_ENTITY) Bp);
			return BRF54_MAPPING_REPO;
		}
		case "BRF42": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF42_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF42_MAPPING_REPO.save((BRF42_MAPPING_ENTITY) Bp);
			return BRF42_MAPPING_REPO;
		}
		case "BRF48": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF48_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF48_MAPPING_REPO.save((BRF48_MAPPING_ENTITY) Bp);
			return BRF48_MAPPING_REPO;
		}
		case "BRF207": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF207_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF207_MAPPING_REPO.save((BRF207_MAPPING_ENTITY) Bp);
			return BRF207_MAPPING_REPO;
		}
		case "BRF208": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF208_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF208_MAPPING_REPO.save((BRF208_MAPPING_ENTITY) Bp);
			return BRF208_MAPPING_REPO;
		}
		case "BRF209": {
			BRF_MAPPING_PARENT Bp = setMappingLabel(BRF209_MAPPING_REPO.findById(specificCellValue).get(), id, select);
			BRF209_MAPPING_REPO.save((BRF209_MAPPING_ENTITY) Bp);
			return BRF209_MAPPING_REPO;
		}

		default:
			return null;
		}
	}

	BRF_MAPPING_PARENT seteditData(BRF_MAPPING_PARENT up1, BRF_MAPPING_TABLE brfmap) {

		up1.setCust_id(brfmap.getCust_id());
		up1.setReport_addl_criteria_1(brfmap.getReport_addl_criteria_1());
		up1.setAcct_name(brfmap.getAcct_name());
		up1.setReport_name_1(brfmap.getReport_name_1());
		String[] splitValues1 = brfmap.getSchm_code().split(",");
		if (splitValues1.length > 1) {
			up1.setSchm_code(splitValues1[1]);
			System.out.println(splitValues1[1]);
		} else {
			// Handle the case where there is no comma-separated value
			up1.setSchm_code(brfmap.getSchm_code());
			System.out.println(brfmap.getSchm_code());
		}

		up1.setReport_addl_criteria_1(brfmap.getReport_addl_criteria_1());
		up1.setReport_label_1(brfmap.getReport_label_1());
		up1.setSchm_code(brfmap.getSchm_code());

		return up1;
	}

	public File getconsolidateFileECL(String fileType) throws JRException, SQLException, IOException {

		logger.info("Entering getConsolidatedFile method");

		String path = env.getProperty("output.exportpath");
		String fileName = "ECL_CONSOLIDATED" + ".xlsx";
		File outputFile = new File(path + fileName);

		try {
			// List of Jasper files and their corresponding sheet names
			InputStream[] jasperFiles = {
					this.getClass().getResourceAsStream("/static/jasper/ECL_RWA_FB_NFB_MAST_TABLE.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_RWA_FB_NFB.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_TREASURY_DATA.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_RATING_DATA.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_SMA_DATA.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_WATCHLIST.jrxml"),
					/*
					 * this.getClass().getResourceAsStream("/static/jasper/CoolOff table1.jrxml"),
					 */
					this.getClass().getResourceAsStream("/static/jasper/ECL_COOLOFF.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_ACC_MASTER_WORKING.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_CUST_MASTER.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_ACC_MASTER.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_COLLATERAL.jrxml"),

			};

			String[] sheetNames = { "RWA FB+NFB", "FB+NFB for ECL", "TreasuryData", "RATING DATA", "SMADATA",
					"WatchList", "COOLOFF", "Acc Master Working", "CUST MASTER", "ACC MASTER", "Collateral"

			};
			List<JasperPrint> jasperPrintList = new ArrayList<>();

			HashMap<String, Object> map = new HashMap<>();
			map.put("PAGE_BREAK_CONDITION", true);
			map.put("REPORT_DATE", ""); // Setting parameters for the Jasper report

			for (int i = 0; i < jasperFiles.length; i++) {
				JasperReport jasperReport = JasperCompileManager.compileReport(jasperFiles[i]);
				JasperPrint jp = JasperFillManager.fillReport(jasperReport, map, srcdataSource.getConnection());
				jp.setName(sheetNames[i]);
				jasperPrintList.add(jp);
			}

			// Exporting the JasperPrintList to Excel
			logger.info("Exporting to Excel");
			SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
			reportConfig.setSheetNames(new String[] { fileName });
			reportConfig.setDetectCellType(true);
			reportConfig.setOnePagePerSheet(false);
			reportConfig.setRemoveEmptySpaceBetweenRows(false);
			reportConfig.setWhitePageBackground(false);
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(SimpleExporterInput.getInstance(jasperPrintList));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(new FileOutputStream(outputFile)));
			exporter.exportReport();
			logger.info("Excel File exported successfully");

		} catch (IOException e) {
			logger.error("Error occurred while exporting to Excel: " + e.getMessage());
			throw new IOException("Error occurred while exporting to Excel", e);
		}

		return outputFile;
	}

	public File getFile1(String filetype) throws FileNotFoundException, JRException, SQLException {

		System.out.println("0000");
		// logger.info(pdfgenerator);
		String path = env.getProperty("output.exportpath");
		// D:\JasperDownload

		System.out.println(path);

		String fileName = "";
		String zipFileName = "";
		File outputFile;

		logger.info("Getting Output file : Third_PARTY");

		fileName = "ECL_STATUS_LIST" + filetype;

		zipFileName = fileName + ".zip";

		try {
			InputStream jasperFile;

			// logger.info("Getting Jasper file :" + "Third_PARTY");

			if (filetype.equals("pdf")) {
				System.out.println("inner pdf");
				jasperFile = this.getClass().getResourceAsStream("/static/jasper/Ecl_Status.jrxml");
			} else {

				jasperFile = this.getClass().getResourceAsStream("/static/jasper/Ecl_Status.jrxml");

			}

			System.out.println("#####");
			JasperReport jr = JasperCompileManager.compileReport(jasperFile);
			System.out.println("@@@@@@@@");
			System.out.println(jr);
			System.out.println("@@@@@@@@");

			HashMap<String, Object> map = new HashMap<String, Object>();
			// logger.info("Assigning Parameters for Jasper");

			// map.put("INV_NO", inv_no);

			if (filetype.equals("pdf")) {
				fileName = fileName + ".pdf";
				path = path + fileName;
				JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
				JasperExportManager.exportReportToPdfFile(jp, path);

			} else {

				fileName = fileName + ".xlsx";
				path += fileName;
				JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path));
				exporter.exportReport();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		outputFile = new File(path);
		return outputFile;
	}

	public File getFile(String filetype) throws FileNotFoundException, JRException, SQLException {

		System.out.println("0000");
		// logger.info(pdfgenerator);
		String path = env.getProperty("output.exportpath");
		// D:\JasperDownload

		System.out.println(path);

		String fileName = "ECL_MDT_AED";
		String zipFileName = "";
		File outputFile;

		logger.info("Getting Output file : Third_PARTY");
		zipFileName = fileName + ".zip";

		try {
			InputStream jasperFile;
			if (filetype.equals("pdf")) {
				System.out.println("inner pdf");
				jasperFile = this.getClass().getResourceAsStream("/static/jasper/ECL_MASTER_DATA_AED.jrxml");
			} else {

				jasperFile = this.getClass().getResourceAsStream("/static/jasper/ECL_MASTER_DATA_AED.jrxml");

			}

			System.out.println("#####");
			JasperReport jr = JasperCompileManager.compileReport(jasperFile);
			System.out.println("@@@@@@@@");
			System.out.println(jr);
			System.out.println("@@@@@@@@");

			HashMap<String, Object> map = new HashMap<String, Object>();
			if (filetype.equals("pdf")) {
				fileName = fileName + ".pdf";
				path = path + fileName;
				JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
				JasperExportManager.exportReportToPdfFile(jp, path);

			} else {

				fileName = fileName + ".xlsx";
				path += fileName;
				JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path));
				exporter.exportReport();
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		outputFile = new File(path);
		return outputFile;
	}

	public File getRBRFile1(String filetype) throws FileNotFoundException, JRException, SQLException, ParseException {

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

		logger.info("GET GENERATION");
		// String path = this.env.getProperty("output.exportpath");
		String path = "D:/RBR_Download/";
		String fileName = "";
		String zipFileName = "";
		File outputFile;

		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");

		fileName = "RBR_Report";

		logger.info("GET GENERATION" + fileName);

		zipFileName = fileName + ".zip";

		// String filetype="detailexcel";

		// dtltype="report";

		try {

			InputStream jasperFile;

			HashMap<String, Object> map = new HashMap<String, Object>();

			jasperFile = this.getClass().getResourceAsStream("/static/jasper/Report_main.jasper");

			InputStream subrep1 = this.getClass().getResourceAsStream("/static/jasper/Subreportrbrt.jrxml");
			InputStream subrep2 = this.getClass().getResourceAsStream("/static/jasper/cxbrlcustomer.jrxml");

			JasperReport sr1 = JasperCompileManager.compileReport(subrep1);
			JasperReport sr2 = JasperCompileManager.compileReport(subrep2);

			map.put("INST", sr1);

			map.put("INDEX", sr2);

			logger.info("GET GENERATION ASSIGNING PARAMETER");

			JasperReport jr = (JasperReport) JRLoader.loadObject(jasperFile);

			fileName = fileName + ".xlsx";

			path = path + fileName;

			JasperPrint jp = JasperFillManager.fillReport(sr1, map, srcdataSource.getConnection());
			logger.info("GET GENERATION ASSIGNING PARAMETER2");

			JRXlsxExporter exporter = new JRXlsxExporter();

			exporter.setExporterInput(new SimpleExporterInput(jp));

			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path));

			exporter.exportReport();

		} catch (Exception e) {

			e.printStackTrace();

		}

		outputFile = new File(path);

		return outputFile;

	}

	public File getRBRFile(String filetype, String tabName, String operationData, HttpServletRequest req)
			throws JRException, SQLException, IOException {

		logger.info("Attempting to generate RBR report. Filetype: {}, TabName: {}, OperationData: {}", filetype,
				tabName, operationData);

		String baseOutputPath = env.getProperty("output.exportpath");
		if (baseOutputPath == null || baseOutputPath.trim().isEmpty()) {
			logger.error("Configuration error: 'output.exportpath' is not set or is empty.");
			throw new IOException("Output path configuration is missing.");
		}
		logger.debug("Base output path: {}", baseOutputPath);

		String determinedFileName = null;
		InputStream jasperFileInputStream = null; // Renamed for clarity

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String branchCode = (String) req.getSession().getAttribute("BRANCHCODE");

		logger.debug("User RoleID: {}, BranchCode: {}", roleId, branchCode);

		// --- Determine Jasper template and output filename ---
		String jasperTemplatePath = null;
		boolean isRBRRole = "RBR".equals(roleId);

		switch (tabName) {
		case "1": // CUSTOMER
			logger.debug("Processing Tab 1: CUSTOMER");
			if (isRBRRole) {
				logger.debug("Role is RBR. Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/CUSTOMER_DATA_ADD.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Customer_Data_Del.jrxml";

				}

				else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Customer_Data_UPD.jrxml";
				else
					jasperTemplatePath = "/static/jasper/CUSTOMER_DATA.jrxml";
			} else {
				logger.debug("Role is NOT RBR (or null). Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/CUSTOMER_DATA_ADD1.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Customer_Data_Del1.jrxml";
				}

				else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Customer_Data_UPD1.jrxml";
				else
					jasperTemplatePath = "/static/jasper/CUSTOMER_DATA1.jrxml";
			}
			determinedFileName = "Customer Data." + filetype;
			break;

		case "2": // PARTNER
			logger.debug("Processing Tab 2: PARTNER");
			if (isRBRRole) {
				logger.debug("Role is RBR. Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Partner and Shareholder Data Add.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Partner and Shareholder Data Del.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Partner and Shareholder Data Upd.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Partner_and_Shareholder_Data.jrxml";
			} else {
				logger.debug("Role is NOT RBR (or null). Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Partner and Shareholder Data Add1.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Partner and Shareholder Data Del1.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Partner and Shareholder Data Upd1.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Partner_and_Shareholder_Data1.jrxml";
			}
			determinedFileName = "Partner Data." + filetype; // Corrected typo from Patrner to Partner
			break;

		case "3": // FACILITY
			logger.debug("Processing Tab 3: FACILITY");
			if (isRBRRole) {
				logger.debug("Role is RBR. Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Facility Data Add.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Facility Data Del.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Facility Data Upd.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Facility_Data.jrxml";
			} else {
				logger.debug("Role is NOT RBR (or null). Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Facility Data Add1.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Facility Data Del1.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Facility Data Upd1.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Facility Data1.jrxml"; // Corrected typo from .jrxmll
			}
			determinedFileName = "Facility Data." + filetype;
			break;

		case "4": // SECURITY
			logger.debug("Processing Tab 4: SECURITY");
			if (isRBRRole) {
				logger.debug("Role is RBR. Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Security Data Add.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Security Data Del.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Security Data Upd.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Security_Data.jrxml";
			} else {
				logger.debug("Role is NOT RBR (or null). Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Security Data Add1.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Security Data Del1.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Security Data Upd1.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Security Data1.jrxml";
			}
			determinedFileName = "Security Data." + filetype;
			break;

		case "5": // PROVISION
			logger.debug("Processing Tab 5: PROVISION");
			if (isRBRRole) {
				logger.debug("Role is RBR. Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Provision Data Add.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Provision Data Del.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Provision Data Upd.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Provision_Data.jrxml";
			} else {
				logger.debug("Role is NOT RBR (or null). Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Provision Data Add1.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Provision Data Del1.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Provision Data Upd1.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Provision Data1.jrxml";
			}
			determinedFileName = "Provision Data." + filetype;
			break;

		case "6": // OVERALL
			logger.debug("Processing Tab 6: OVERALL");
			if (isRBRRole) {
				logger.debug("Role is RBR. Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Overall Data Add.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Overall Data Del.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Overall Data Upd.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Overall_Data.jrxml";
			} else {
				logger.debug("Role is NOT RBR (or null). Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Overall Data Add1.jrxml";
				else if ("DEL".equals(operationData)) {
					jasperTemplatePath = "/static/jasper/Overall Data Del1.jrxml";
				} else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Overall Data Upd1.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Overall Data1.jrxml";
			}
			determinedFileName = "Overall Data." + filetype;
			break;

		case "7": // LEGAL CASES
			logger.debug("Processing Tab 7: LEGAL CASES");
			if (isRBRRole) {
				logger.debug("Role is RBR. Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Legal Cases Add.jrxml";
				else if ("DEL".equals(operationData))
					jasperTemplatePath = "/static/jasper/Legal Cases Del.jrxml";
				else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Legal Cases Upd.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Legal_Cases.jrxml";
			} else {
				logger.debug("Role is NOT RBR (or null). Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Legal Cases Add1.jrxml";
				else if ("DEL".equals(operationData))
					jasperTemplatePath = "/static/jasper/Legal Cases Del1.jrxml";
				else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Legal Cases Upd1.jrxml";
				else
					jasperTemplatePath = "/static/jasper/Legal Cases1.jrxml";
			}
			determinedFileName = "Legal Cases." + filetype;
			break;

		case "8": // INVESTMENTS
			logger.debug("Processing Tab 8: INVESTMENTS");
			if (isRBRRole) {
				logger.debug("Role is RBR. Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Investments Add.jrxml";
				else if ("DEL".equals(operationData))
					jasperTemplatePath = "/static/jasper/Investments Del.jrxml";
				else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Investments Upd.jrxml";
				else
					jasperTemplatePath = "/static/jasper/RBR_LIST_INVESTMENTS.jrxml";
			} else {
				logger.debug("Role is NOT RBR (or null). Operation: {}", operationData);
				if ("ADD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Investments Add1.jrxml";
				else if ("DEL".equals(operationData))
					jasperTemplatePath = "/static/jasper/Investments Del1.jrxml";
				else if ("UPD".equals(operationData))
					jasperTemplatePath = "/static/jasper/Investments Upd1.jrxml";
				else
					jasperTemplatePath = "/static/jasper/RBR_LIST_INVESTMENTS1.jrxml";
			}
			determinedFileName = "Investments." + filetype;
			break;

		case "Rbrv1": // CUSTOMERDATA V1 (no role check)
			logger.debug("Processing Tab Rbrv1: CUSTOMERDATA V1");
			jasperTemplatePath = "/static/jasper/CUSTOMER_DATA1_RBR_V1.jrxml";
			determinedFileName = "Customerdata." + filetype;
			break;

		default:
			logger.warn("Invalid tabName received: {}", tabName);
			throw new IllegalArgumentException("Invalid tab: " + tabName);
		}

		if (jasperTemplatePath == null) { // Should not happen if logic is correct, but as a safeguard
			logger.error("Jasper template path was not determined for tabName: {}", tabName);
			throw new JRException("Internal error: Jasper template path could not be determined.");
		}
		logger.info("Selected Jasper template: {}", jasperTemplatePath);
		jasperFileInputStream = this.getClass().getResourceAsStream(jasperTemplatePath);

		if (jasperFileInputStream == null) {
			logger.error("Jasper file not found at path: {} for tab: {}", jasperTemplatePath, tabName);
			throw new JRException("Jasper file not found: " + jasperTemplatePath);
		}

		// --- Compile, Fill, and Export Report ---
		File outputFile = Paths.get(baseOutputPath, determinedFileName).toFile();
		Connection dbConnection = null; // Declare connection here to be accessible in finally

		try {
			logger.debug("Compiling Jasper report from stream...");
			JasperReport jasperReport = JasperCompileManager.compileReport(jasperFileInputStream);
			logger.debug("Jasper report compiled successfully.");

			HashMap<String, Object> reportParameters = new HashMap<>();
			reportParameters.put("BRANCH_CODE", branchCode);
			// Add any other common parameters here

			logger.debug("Getting database connection...");
			dbConnection = srcdataSource.getConnection(); // Get connection
			if (dbConnection == null) {
				logger.error("Failed to obtain database connection from datasource.");
				throw new SQLException("Could not obtain database connection.");
			}
			logger.debug("Database connection obtained. Filling report...");

			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, reportParameters, dbConnection);
			logger.debug("Report filled successfully.");

			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile.getAbsolutePath())); // Use
																											// absolute
																											// path

			// Set auto-width for columns
			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setDetectCellType(true); // Auto-detect data types
			configuration.setAutoFitPageHeight(true); // Optional: auto-fit page height
			configuration.setOnePagePerSheet(false); // Optional: multiple pages in one sheet
			configuration.setWrapText(true); // Optional: wrap text
			configuration.setCollapseRowSpan(false); // Optional: keep rowspan intact // ✅ THIS enables auto column
														// width
			exporter.setConfiguration(configuration);

			logger.info("Exporting report to: {}", outputFile.getAbsolutePath());
			exporter.exportReport();
			autoSizeColumns(outputFile);

			logger.info("Excel file exported successfully: {}", outputFile.getName());

			if (tabName.equals("1")) {
				CUSTOMERdeltoARCHIVAL(isRBRRole, branchCode);
			} else if (tabName.equals("2")) {
				PARTNERdeltoARCHIVAL(isRBRRole, branchCode);
			} else if (tabName.equals("3")) {
				FACILITYdeltoARCHIVAL(isRBRRole, branchCode);
			} else if (tabName.equals("4")) {
				SECURITYdeltoARCHIVAL(isRBRRole, branchCode);
			} else if (tabName.equals("5")) {
				PROVISIONdeltoARCHIVAL(isRBRRole, branchCode);
			} else if (tabName.equals("6")) {
				OVERALLdeltoARCHIVAL(isRBRRole, branchCode);
			}

			return outputFile;

		} catch (JRException | SQLException e) { // Catch specific exceptions related to Jasper and SQL
			logger.error("Error during Jasper report generation for {}: {}", determinedFileName, e.getMessage(), e);
			// Delete partially created file if it exists and an error occurred
			if (outputFile.exists() && !outputFile.delete()) {
				logger.warn("Could not delete partially created report file: {}", outputFile.getAbsolutePath());
			}
			throw e; // Re-throw the original exception to be handled by the caller
		} catch (Exception e) { // Catch any other unexpected exceptions
			logger.error("Unexpected error during report generation for {}: {}", determinedFileName, e.getMessage(), e);
			if (outputFile.exists() && !outputFile.delete()) {
				logger.warn("Could not delete partially created report file: {}", outputFile.getAbsolutePath());
			}
			throw new JRException("Unexpected error generating report: " + e.getMessage(), e); // Wrap in JRException or
																								// a custom one
		} finally {
			// Close jasperFileInputStream
			if (jasperFileInputStream != null) {
				try {
					jasperFileInputStream.close();
				} catch (IOException e) {
					logger.warn("Could not close jasperFileInputStream: {}", e.getMessage());
				}
			}
			// Close database connection
			if (dbConnection != null) {
				try {
					if (!dbConnection.isClosed()) {
						dbConnection.close();
						logger.debug("Database connection closed.");
					}
				} catch (SQLException e) {
					logger.error("Error closing database connection: {}", e.getMessage(), e);
				}
			}
		}
	}

	public void CUSTOMERdeltoARCHIVAL(boolean isRBRRole, String branchCode) {
		logger.debug("CUSTOMERdeltoARCHIVAL started. isRBRRole={}, branchCode={}", isRBRRole, branchCode);

		List<RBRcustomer_entity> deldata;
		if (isRBRRole) {
			deldata = RBRcustomerRepo.getlistofDEL();
		} else {
			deldata = RBRcustomerRepo.getlistofDELbranch(branchCode);
		}

		logger.debug("Found {} records to archive", deldata.size());

		for (RBRcustomer_entity cud : deldata) {
			try {
				// archive
				RBRcustomer_Archival_entity temdata = new RBRcustomer_Archival_entity(cud);
				RBRcustomerArchivalRepo.save(temdata);

				// delete
				RBRcustomerRepo.deleteById(cud.getSrl_no());

				logger.debug("Archived and deleted record with srl_no={}", cud.getSrl_no());
			} catch (Exception e) {
				logger.error("Error archiving/deleting record with srl_no={}", cud.getSrl_no(), e);
			}
		}
	}

	@Autowired
	RBRShareHolder_Repo rbrShareHolder_Repo;

	@Autowired
	RBRpartnerArchivalRepo RBRpartnerArchivalRepo;

	public void PARTNERdeltoARCHIVAL(boolean isRBRRole, String branchCode) {
		logger.debug("PARTNERdeltoARCHIVAL started. isRBRRole={}, branchCode={}", isRBRRole, branchCode);

		List<RBRShareHolder_Entity> deldata;
		if (isRBRRole) {
			deldata = rbrShareHolder_Repo.getlistofDEL();
		} else {
			deldata = rbrShareHolder_Repo.getlistofDELbranch(branchCode);
		}

		logger.debug("Found {} records to archive", deldata.size());

		for (RBRShareHolder_Entity cud : deldata) {
			try {
				// archive
				RBRpartner_Archival_entity temdata = new RBRpartner_Archival_entity(cud);
				RBRpartnerArchivalRepo.save(temdata);

				// delete
				rbrShareHolder_Repo.deleteById(cud.getSrl_no());

				logger.debug("Archived and deleted record with srl_no={}", cud.getSrl_no());
			} catch (Exception e) {
				logger.error("Error archiving/deleting record with srl_no={}", cud.getSrl_no(), e);
			}
		}
	}

	@Autowired
	Facility_Repo facility_Repo;

	@Autowired
	RBRfacilityArchivalRepo RBRfacilityArchivalRepo;

	public void FACILITYdeltoARCHIVAL(boolean isRBRRole, String branchCode) {
		logger.debug("FACILITYdeltoARCHIVAL started. isRBRRole={}, branchCode={}", isRBRRole, branchCode);

		List<Facitlity_Entity> deldata;
		if (isRBRRole) {
			deldata = facility_Repo.getlistofDEL();
		} else {
			deldata = facility_Repo.getlistofDELbranch(branchCode);
		}

		logger.debug("Found {} records to archive", deldata.size());

		for (Facitlity_Entity cud : deldata) {
			try {
				// archive
				RBRfacility_Archival_entity temdata = new RBRfacility_Archival_entity(cud);
				RBRfacilityArchivalRepo.save(temdata);

				// delete
				facility_Repo.deleteById(cud.getSrl_no());

				logger.debug("Archived and deleted record with srl_no={}", cud.getSrl_no());
			} catch (Exception e) {
				logger.error("Error archiving/deleting record with srl_no={}", cud.getSrl_no(), e);
			}
		}
	}

	@Autowired
	Security_Repo security_Repo;
	@Autowired
	RBRsecurityArchivalRepo RBRsecurityArchivalRepo;

	public void SECURITYdeltoARCHIVAL(boolean isRBRRole, String branchCode) {
		logger.debug("SECURIYdeltoARCHIVAL started. isRBRRole={}, branchCode={}", isRBRRole, branchCode);

		List<Security_Entity> deldata;
		if (isRBRRole) {
			deldata = security_Repo.getlistofDEL();
		} else {
			deldata = security_Repo.getlistofDELbranch(branchCode);
		}

		logger.debug("Found {} records to archive", deldata.size());

		for (Security_Entity cud : deldata) {
			try {
				// archive
				RBRsecurity_Archival_entity temdata = new RBRsecurity_Archival_entity(cud);
				RBRsecurityArchivalRepo.save(temdata);

				// delete
				security_Repo.deleteById(cud.getSrl_no());

				logger.debug("Archived and deleted record with srl_no={}", cud.getSrl_no());
			} catch (Exception e) {
				logger.error("Error archiving/deleting record with srl_no={}", cud.getSrl_no(), e);
			}
		}
	}

	@Autowired
	Provision_Repo Provision_Repo;

	@Autowired
	RBRprovisionArchivalRepo RBRprovisionArchivalRepo;

	public void PROVISIONdeltoARCHIVAL(boolean isRBRRole, String branchCode) {
		logger.debug("PROVISIONdeltoARCHIVAL started. isRBRRole={}, branchCode={}", isRBRRole, branchCode);

		List<Provision_Entity> deldata;
		if (isRBRRole) {
			deldata = Provision_Repo.getlistofDEL();
		} else {
			deldata = Provision_Repo.getlistofDELbranch(branchCode);
		}

		logger.debug("Found {} records to archive", deldata.size());

		for (Provision_Entity cud : deldata) {
			try {
				// archive
				RBRprovision_Archival_entity temdata = new RBRprovision_Archival_entity(cud);
				RBRprovisionArchivalRepo.save(temdata);

				// delete
				Provision_Repo.deleteById(cud.getSrl_no());

				logger.debug("Archived and deleted record with srl_no={}", cud.getSrl_no());
			} catch (Exception e) {
				logger.error("Error archiving/deleting record with srl_no={}", cud.getSrl_no(), e);
			}
		}
	}

	@Autowired
	RBRoverall_Data_Repo RBRoverall_Data_Repo;

	@Autowired
	RBRoverallArchivalRepo RBRoverallArchivalRepo;

	public void OVERALLdeltoARCHIVAL(boolean isRBRRole, String branchCode) {
		logger.debug("OVERALLdeltoARCHIVAL started. isRBRRole={}, branchCode={}", isRBRRole, branchCode);

		List<RBROverall_Data_Entity> deldata;
		if (isRBRRole) {
			deldata = RBRoverall_Data_Repo.getlistofDEL();
		} else {
			deldata = RBRoverall_Data_Repo.getlistofDELbranch(branchCode);
		}

		logger.debug("Found {} records to archive", deldata.size());

		for (RBROverall_Data_Entity cud : deldata) {
			try {
				// archive
				RBRoverall_Archival_entity temdata = new RBRoverall_Archival_entity(cud);
				RBRoverallArchivalRepo.save(temdata);

				// delete
				RBRoverall_Data_Repo.deleteById(cud.getSrl_no());

				logger.debug("Archived and deleted record with srl_no={}", cud.getSrl_no());
			} catch (Exception e) {
				logger.error("Error archiving/deleting record with srl_no={}", cud.getSrl_no(), e);
			}
		}
	}

	public void autoSizeColumns(File excelFile) throws IOException {
		FileInputStream fis = new FileInputStream(excelFile);
		XSSFWorkbook workbook = new XSSFWorkbook(fis);
		XSSFSheet sheet = workbook.getSheetAt(0);

		XSSFRow row = sheet.getRow(0);
		if (row != null) {
			for (int i = 0; i < row.getLastCellNum(); i++) {
				sheet.autoSizeColumn(i);
			}
		}

		fis.close();

		FileOutputStream fos = new FileOutputStream(excelFile);
		workbook.write(fos);
		workbook.close();
		fos.close();
	}

	// CREATED BY GOWTHAM
	public File getMasterRBRFile(String formmode, HttpServletRequest req)
			throws JRException, SQLException, IOException {

		// Retrieve session attributes
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");

		// File storage path
		String path = env.getProperty("output.exportpath");
		if (path == null || path.isEmpty()) {
			throw new IOException("❌ Invalid export path. Check application properties.");
		}
		if (!path.endsWith(File.separator)) {
			path += File.separator;
		}

		String fileName;
		InputStream jasperFile = null;

		try {
			// Determine the report file based on formmode
			switch (formmode) {
			case "1":
				jasperFile = this.getClass().getResourceAsStream("/static/jasper/Customer Data Unverified.jrxml");
				fileName = "Customer Data Unverified.xlsx";
				break;
			case "2":
				jasperFile = this.getClass().getResourceAsStream("/static/jasper/Partner Data Unverified.jrxml");
				fileName = "Partner Data Unverified.xlsx";
				break;
			case "3":
				jasperFile = this.getClass().getResourceAsStream("/static/jasper/Security Data Unverified.jrxml");
				fileName = "security.xlsx";
				break;
			case "4":
				jasperFile = this.getClass().getResourceAsStream("/static/jasper/Facility Data Unverified.jrxml");
				fileName = "facility.xlsx";
				break;
			case "5":
				jasperFile = this.getClass().getResourceAsStream("/static/jasper/Provision Data Unverified.jrxml");
				fileName = "provision.xlsx";
				break;
			case "6":
				jasperFile = this.getClass().getResourceAsStream("/static/jasper/Overall Data Unverified.jrxml");
				fileName = "overall.xlsx";
				break;
			default:
				throw new IllegalArgumentException("❌ Invalid formmode: " + formmode);
			}

			if (jasperFile == null) {
				throw new JRException("❌ Jasper file not found for formmode: " + formmode);
			}

			try (Connection connection = srcdataSource.getConnection()) {
				// Compile the report
				JasperReport jr = JasperCompileManager.compileReport(jasperFile);

				// Pass BRANCH_CODE as a parameter
				HashMap<String, Object> parameters = new HashMap<>();
				parameters.put("BRANCH_CODE", BRANCHCODE);

				// Generate the final report file
				String outputPath = path + fileName;
				File outputFile = new File(outputPath);
				JasperPrint jp = JasperFillManager.fillReport(jr, parameters, connection);

				// Export to Excel
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jp));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputPath));

				SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
				configuration.setDetectCellType(true); // Very important for data types
				configuration.setOnePagePerSheet(false);
				exporter.setConfiguration(configuration);

				// Export the report to the file
				exporter.exportReport();

				// Call your auto-size method AFTER the file has been created
				autoSizeColumns(outputFile);

				return new File(outputPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null; // Return null if an error occurs
		} finally {
			if (jasperFile != null) {
				jasperFile.close();
			}
		}
	}

	public File getECLFile(String filetype) {
		String path = env.getProperty("output.exportpath");
		String fileName = "ECL_DATA" + filetype;
		String zipFileName = fileName + ".zip";
		File outputFile = null;

		try {
			// Load JasperReport files
			InputStream jasperFile = this.getClass().getResourceAsStream("/static/jasper/Report_main.jrxml");
			InputStream[] jasperFiles = { this.getClass().getResourceAsStream("/static/jasper/Ecl_Dcr.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/Ecl_Disb_Rec.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/Ecl_Inr.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/Ecl_Lgd.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/Ecl_Lrw.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/ECL_MASTER_DATA_AED.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/Ecl_Recovery.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/Ecl_Status.jrxml"),
					this.getClass().getResourceAsStream("/static/jasper/Ecl_Wo_Adj.jrxml") };

			// Compile JasperReports
			JasperReport[] jasperReports = new JasperReport[jasperFiles.length];
			for (int i = 0; i < jasperFiles.length; i++) {
				jasperReports[i] = JasperCompileManager.compileReport(jasperFiles[i]);
			}

			// Fill JasperPrint for each report
			JasperPrint[] jasperPrints = new JasperPrint[jasperReports.length];
			for (int i = 0; i < jasperReports.length; i++) {
				jasperPrints[i] = JasperFillManager.fillReport(jasperReports[i], new HashMap<>(),
						srcdataSource.getConnection());
			}

			// Combine JasperPrints
			JasperPrint combinedJasperPrint = new JasperPrint();
			for (JasperPrint jasperPrint : jasperPrints) {
				List<JRPrintPage> pages = jasperPrint.getPages();
				for (JRPrintPage page : pages) {
					combinedJasperPrint.addPage(page);
				}
			}

			// Export to XLSX
			fileName = fileName + ".xlsx";
			path += fileName;
			JRXlsxExporter exporter = new JRXlsxExporter();
			exporter.setExporterInput(new SimpleExporterInput(combinedJasperPrint));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path));
			exporter.exportReport();

		} catch (Exception e) {
			logger.error("Error generating ECL file", e);
		}

		if (path != null) {
			outputFile = new File(path);
		}
		return outputFile;
	}

	public File Mapped(InputStream jasperFile, String yy, String report_name_1, String filetype)
			throws JRException, SQLException {

		List<BRF_MAPPING_TABLE> entityList = new ArrayList<>();
		File outputFile = null;
		JasperReport jr = JasperCompileManager.compileReport(jasperFile);
		HashMap<String, Object> map = new HashMap<String, Object>();

		logger.info("Assigning Parameters for Jasper");
		map.put("REPORT_NAME_1", report_name_1);
		if (filetype.equals("pdf")) {
			// fileName = fileName + ".pdf";
			// path = fileName;
			// Date currentDate = new Date();// Get the current date and time
			LocalDateTime currentDateTime = LocalDateTime.now();

			// Format the date and time as a string
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = currentDateTime.format(formatter);

			String Name = yy + ".pdf";

			// Construct the output path with the formatted date and time
			String outpath = "D:\\" + formattedDateTime + "_" + Name;
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			JasperExportManager.exportReportToPdfFile(jp, outpath);
			logger.info("PDF File exported");
		} else {

			System.out.println("EXCEEEEEll");

			// Get the current date and time
			LocalDateTime currentDateTime = LocalDateTime.now();

			// Format the date and time as a string
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
			String formattedDateTime = currentDateTime.format(formatter);

			String Name = yy + ".xlsx";

			// Construct the output path with the formatted date and time
			String outpath = "D:\\" + formattedDateTime + "_" + Name;
			JRBeanCollectionDataSource dataSrc = new JRBeanCollectionDataSource(entityList);
			JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
			JRXlsxExporter exporter = new JRXlsxExporter();

			exporter.setExporterInput(new SimpleExporterInput(jp));
			exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outpath));

			System.out.println("The path is " + outpath);

			exporter.exportReport();
			logger.info("Excel File exported");

			outputFile = new File(outpath);
		}

		return outputFile;

	}
}
