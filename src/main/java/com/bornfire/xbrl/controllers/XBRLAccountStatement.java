package com.bornfire.xbrl.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bornfire.xbrl.entities.TransactionInquiry;
import com.bornfire.xbrl.entities.TransactionInquiryRep;
import com.bornfire.xbrl.services.EmailServices;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;


@Controller
@ConfigurationProperties("default")
@RequestMapping(value = "ReportsAccount")
public class XBRLAccountStatement {

	
	@Autowired
	DataSource srcdataSource;

	DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	Environment env;
	
	@Autowired
	TransactionInquiryRep transactionInquiryRep;
	
	@Autowired
	EmailServices emailservices;
	
	private static String[] columns_Loan = {"SL","Tran Date", "Tran ID", "Part Tran Type","Value Date","Tran Particular","Debit Amount","Credit Amount","Tran Currency"};

	private static final Logger logger = LoggerFactory.getLogger(XBRLAccountStatement.class);

	private static List<TransactionInquiry> dailyList = new ArrayList<TransactionInquiry>();

	@GetMapping("/ReportDownloadXLSX")
	public ResponseEntity<InputStreamResource> AMLDownloadExcel(HttpServletResponse response,
			@RequestParam(value = "reportId", required = false) String reportId,
			@RequestParam(value = "acid", required = false) String acid,
			@RequestParam(value = "fromdate", required = false) String fromdate,
			@RequestParam(value = "todate", required = false) String todate,
			@RequestParam(value = "userid", required = false) String userid,
			@RequestParam(value = "filetype", required = false) String filetype,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "category", required = false) String category) throws IOException, SQLException, JRException, ParseException {
		response.setContentType("application/octet-stream");
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");

		String toDAte = null;

		if (fromdate != null && !fromdate.isEmpty()) {

			if(todate.equals("undefined")) {
				toDAte="";
			}else {
				Date ConToDate = dateFormat1.parse(todate);
				String strDate1 = formatter1.format(ConToDate);
				toDAte = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate1));
			}
			
		}else {
			toDAte="";
		}
		
		HttpHeaders headers = new HttpHeaders();
					headers.add("Content-Disposition", "attachment; filename=Account Statement"+toDAte+".xlsx");
		String filename = "Account Statement"+toDAte+".xlsx";
		return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(getDownloadFileExcel(null, acid, fromdate, todate, dtltype,
    					filetype,category,filename)));

	
	}
	@GetMapping("/ReportDownloadXLSXE")
	public ResponseEntity<InputStreamResource> AMLDownloadExcelE(HttpServletResponse response,
			@RequestParam(value = "reportId", required = false) String reportId,
			@RequestParam(value = "acid", required = false) String acid,
			@RequestParam(value = "fromdate", required = false) String fromdate,
			@RequestParam(value = "todate", required = false) String todate,
			@RequestParam(value = "userid", required = false) String userid,
			@RequestParam(value = "filetype", required = false) String filetype,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "category", required = false) String category) throws IOException, SQLException, JRException, ParseException {
		response.setContentType("application/octet-stream");
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");

		String toDAte = null;

		if (fromdate != null && !fromdate.isEmpty()) {

			if(todate.equals("undefined")) {
				toDAte="";
			}else {
				Date ConToDate = dateFormat1.parse(todate);
				String strDate1 = formatter1.format(ConToDate);
				toDAte = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate1));
			}
			
		}else {
			toDAte="";
		}
		
		HttpHeaders headers = new HttpHeaders();
					headers.add("Content-Disposition", "attachment; filename=Account Statement"+toDAte+".xlsx");
					String filename = "Account Statement"+toDAte+".xlsx";
		return ResponseEntity
                .ok()
                .headers(headers)
                .body(new InputStreamResource(getDownloadFileExcelE(null, acid, fromdate, todate, dtltype,
    					filetype,category,filename)));

	
	}

	
	@RequestMapping(value = "/ReportDownloadPDF", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource AMLReportDownloadFormattedDate(HttpServletResponse response,
			@RequestParam(value = "reportId", required = false) String reportId,
			@RequestParam(value = "acid", required = false) String acid,
			@RequestParam(value = "fromdate", required = false) String fromdate,
			@RequestParam(value = "todate", required = false) String todate,
			@RequestParam(value = "userid", required = false) String userid,
			@RequestParam(value = "filetype", required = false) String filetype,
			@RequestParam(value = "category", required = false) String category)
			throws IOException, SQLException, ParseException {
		response.setContentType("application/octet-stream");

		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");

		Date ConDateFromdate = dateFormat1.parse(fromdate);
		System.out.println(ConDateFromdate);

		String strDate2 = formatter1.format(ConDateFromdate);
		fromdate = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate2));

		Date ConToDate = dateFormat1.parse(todate);
		System.out.println(ConToDate);

		String strDate1 = formatter1.format(ConToDate);
		todate = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate1));

		InputStreamResource resource = null;
		try {
			logger.info("Getting download File :" + reportId + ", FileType :" + filetype);
			// System.out.println(asondate);getDownloadFile
			File repfile = getDownloadFileScr(acid,userid, reportId, fromdate, todate, null, null, filetype,
					category);

			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return resource;
	}
	@RequestMapping(value = "/ReportDownloadPDFE", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource AMLReportDownloadFormattedDateE(HttpServletResponse response,
			@RequestParam(value = "reportId", required = false) String reportId,
			@RequestParam(value = "acid", required = false) String acid,
			@RequestParam(value = "fromdate", required = false) String fromdate,
			@RequestParam(value = "todate", required = false) String todate,
			@RequestParam(value = "userid", required = false) String userid,
			@RequestParam(value = "filetype", required = false) String filetype,
			@RequestParam(value = "category", required = false) String category)
			throws IOException, SQLException, ParseException {
		response.setContentType("application/octet-stream");

		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");

		Date ConDateFromdate = dateFormat1.parse(fromdate);
		System.out.println(ConDateFromdate);

		String strDate2 = formatter1.format(ConDateFromdate);
		fromdate = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate2));

		Date ConToDate = dateFormat1.parse(todate);
		System.out.println(ConToDate);

		String strDate1 = formatter1.format(ConToDate);
		todate = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate1));

		InputStreamResource resource = null;
		try {
			logger.info("Getting download File :" + reportId + ", FileType :" + filetype);
			// System.out.println(asondate);getDownloadFile
			File repfile = getDownloadFileScr(acid,userid, reportId, fromdate, todate, null, null, filetype,
					category);

			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			//emailservices.sendEmail(repfile.getName());
			
			FileInputStream fis = new FileInputStream(repfile);
			byte[] fileBytes = convertInputStreamToBytes(fis);
			 emailservices.sendEmail(
		                repfile.getName(),
		                fileBytes,
		                "application/pdf"
		        );
			fis.close();
			
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return resource;
	}
	
	public byte[] convertInputStreamToBytes(InputStream is) throws IOException {
	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    byte[] data = new byte[4096];
	    int nRead;

	    while ((nRead = is.read(data, 0, data.length)) != -1) {
	        buffer.write(data, 0, nRead);
	    }

	    return buffer.toByteArray();
	}

	public File getDownloadFileScr(String acid,String userid, String reportId, String fromdate, String todate, String currency,
			String dtltype, String filetype, String catgeory) throws FileNotFoundException, JRException, SQLException, ParseException {

		File repfile = null;

		logger.info("Getting Report File for : " + reportId + " in " + "pdf" + " format");

	
			repfile = getFile(acid,reportId, fromdate, todate, currency, dtltype, filetype,
					catgeory);
	
		return repfile;
	}

	public File getFile(String acid,String reportId, String fromdate, String todate, String currency, String dtltype,
			String filetype,String category) throws FileNotFoundException, JRException, SQLException {

		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

		String path =  env.getProperty("output.exportpath");
		String fileName = "";
		
		File outputFile;

		logger.info("Getting Output file :" + reportId);

		String msg="";


	
		 if (!filetype.equals("xbrl")) {

			try {
				
				
				InputStream fileStream = null;
				
					 fileName = "Account Statement" + "_" + dateFormat.format(new SimpleDateFormat("dd-MMM-yyyy").parse(todate));
						logger.info("Getting Jasper file :" + reportId);
						if (filetype.equals("xlsx")) {
						    fileStream = this.getClass().getResourceAsStream("/static/jasper/Loan.jrxml");
						}else {
						    fileStream = this.getClass().getResourceAsStream("/static/jasper/Loan.jrxml");
						}
				
						JasperReport jr = JasperCompileManager.compileReport(fileStream);
				// JasperReport jr = (JasperReport) JRLoader.loadObject(fileStream);
					HashMap<String, Object> map = new HashMap<String, Object>();

					logger.info("Assigning Parameters for Jasper");
					map.put("TODATE", todate);
					map.put("FROMDATE", fromdate);
					map.put("ACID", acid);

					logger.info("BEFORE GENERATING PDF :" + reportId);
					if (filetype.equals("pdf")) {
						fileName = fileName + ".pdf";
						path =  fileName;
						logger.info("BEFORE GENERATING PDF 1 :" + reportId);
						JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
						logger.info("BEFORE GENERATING PDF 2 :" + path);
						JasperExportManager.exportReportToPdfFile(jp, path);
						logger.info("PDF File exported");
					} else {
						fileName = fileName + ".xlsx";
						path =   fileName;
						JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
						JRXlsxExporter exporter = new JRXlsxExporter();
						exporter.setExporterInput(new SimpleExporterInput(jp));
						exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(path));
						exporter.exportReport();
						logger.info("Excel File exported");
					}

			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		
		outputFile = new File(path);

		return outputFile;

	}
	
	public ByteArrayInputStream getDownloadFileExcel(String userid, String reportId, String fromdate, String todate, 
			String dtltype, String filetype, String catgeory,String fileName ) throws FileNotFoundException, JRException, SQLException, ParseException {

		ByteArrayInputStream repfile = null;
	
				repfile = getFileLoanExcel(userid,reportId, fromdate, todate,  dtltype, filetype,fileName);
				
			return repfile;
	
	}
	public ByteArrayInputStream getDownloadFileExcelE(String userid, String reportId, String fromdate, String todate, 
			String dtltype, String filetype, String catgeory,String fileName ) throws JRException, SQLException, ParseException, IOException {

		ByteArrayInputStream repfile = null;
		
				repfile = getFileLoanExcel(userid,reportId, fromdate, todate,  dtltype, filetype,fileName);
				//emailservices.sendEmail(fileName);
				
				 
			    byte[] fileBytes = convertInputStreamToBytes(repfile);

			    emailservices.sendEmail(
			            fileName,
			            fileBytes,
			            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
			    );
				
				
			return repfile;
	
	}

	public ByteArrayInputStream getFileLoanExcel(String userid, String acid, String fromdate, String todate,
			String dtltype, String filetype,String filename) throws FileNotFoundException, JRException, SQLException, ParseException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		Session hs = sessionFactory.getCurrentSession();
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");

		String fromDAte = null;
		String toDAte = null;

		if (fromdate != null && !fromdate.isEmpty()) {
			Date ConDateFromdate = dateFormat1.parse(fromdate);
			String strDate2 = formatter1.format(ConDateFromdate);
			fromDAte = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate2));

			Date ConToDate = dateFormat1.parse(todate);
			String strDate1 = formatter1.format(ConToDate);
			toDAte = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate1));
		}

		String path = env.getProperty("output.exportpath");
		

		logger.info("Getting Output file :" + acid);

		

		try {
			InputStream fileStream = null;

			Workbook workbook = new XSSFWorkbook();
			CreationHelper createHelper = workbook.getCreationHelper();
			Sheet sheet = workbook.createSheet("Account Statement");

			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerFont.setFontHeightInPoints((short) 14);
			headerFont.setColor(IndexedColors.BLACK.getIndex());

			CellStyle headerCellStyle = workbook.createCellStyle();
			headerCellStyle.setFont(headerFont);
			headerCellStyle.setBorderTop(BorderStyle.MEDIUM);
			headerCellStyle.setBorderBottom(BorderStyle.MEDIUM);
			headerCellStyle.setBorderLeft(BorderStyle.MEDIUM);
			headerCellStyle.setBorderRight(BorderStyle.MEDIUM);

			Row TitleRow = sheet.createRow(0);
			Cell cellTitle = TitleRow.createCell((short) 0);
			cellTitle.setCellValue("BORNFIRE BRF & ACCOUNT STATEMENT GENERATION SYSTEM");
			sheet.addMergedRegion(CellRangeAddress.valueOf("A1:I2"));
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
//			headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellTitle.setCellStyle(headerCellStyle);
			RegionUtil.setBorderTop(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A1:I2"), sheet);
			RegionUtil.setBorderRight(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A1:I2"), sheet);
			RegionUtil.setBorderLeft(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A1:I2"), sheet);
			RegionUtil.setBorderBottom(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A1:I2"), sheet);
//			sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 12));

			Row ReportNameRow = sheet.createRow(2);
			Cell cellReporName = ReportNameRow.createCell((short) 0);
			cellReporName.setCellValue("Account Statement");
			sheet.addMergedRegion(CellRangeAddress.valueOf("A3:I3"));
			headerCellStyle.setAlignment(HorizontalAlignment.CENTER_SELECTION);
//			headerCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellReporName.setCellStyle(headerCellStyle);
			RegionUtil.setBorderTop(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A3:I3"), sheet);
			RegionUtil.setBorderRight(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A3:I3"), sheet);
			RegionUtil.setBorderLeft(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A3:I3"), sheet);
			RegionUtil.setBorderBottom(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A3:I3"), sheet);

			CellStyle reportDateCellStyle = workbook.createCellStyle();
			reportDateCellStyle.setFont(headerFont);
			reportDateCellStyle.setBorderTop(BorderStyle.MEDIUM);
			reportDateCellStyle.setBorderBottom(BorderStyle.MEDIUM);
			reportDateCellStyle.setBorderLeft(BorderStyle.MEDIUM);
			reportDateCellStyle.setBorderRight(BorderStyle.MEDIUM);

			Row Report_Date_Row = sheet.createRow(3);
			Cell cellReporDate = Report_Date_Row.createCell((short) 0);
			cellReporDate.setCellValue("Start Date- " + fromDAte );
			sheet.addMergedRegion(CellRangeAddress.valueOf("A4:C4"));
			reportDateCellStyle.setAlignment(HorizontalAlignment.LEFT);
//			reportDateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellReporDate.setCellStyle(reportDateCellStyle);
			RegionUtil.setBorderTop(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A4:C4"), sheet);
			RegionUtil.setBorderRight(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A4:C4"), sheet);
			RegionUtil.setBorderLeft(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A4:C4"), sheet);
			RegionUtil.setBorderBottom(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("A4:C4"), sheet);

			Row Report_Date_Row2 = sheet.createRow(4);
			Cell cellReporDate2 = Report_Date_Row2.createCell((short) 0);
			cellReporDate2.setCellValue("End Date- " + toDAte);
			sheet.addMergedRegion(CellRangeAddress.valueOf("D4:E4"));
			reportDateCellStyle.setAlignment(HorizontalAlignment.LEFT);
//			reportDateCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			cellReporDate2.setCellStyle(reportDateCellStyle);
			RegionUtil.setBorderTop(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("D4:E4"), sheet);
			RegionUtil.setBorderRight(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("D4:E4"), sheet);
			RegionUtil.setBorderLeft(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("D4:E4"), sheet);
			RegionUtil.setBorderBottom(BorderStyle.MEDIUM,
		            CellRangeAddress.valueOf("D4:E4"), sheet);

			
			
			Row headerRow = sheet.createRow(6);
			for (int i = 0; i < columns_Loan.length; i++) {
				Cell cell = headerRow.createCell(i);
				cell.setCellValue(columns_Loan[i]);
				cell.setCellStyle(headerCellStyle);
			}

			CellStyle dateCellStyle = workbook.createCellStyle();
			dateCellStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd-MM-yyyy"));

			DataFormat fmt = workbook.createDataFormat();
			CellStyle cellStyle = workbook.createCellStyle();
			cellStyle.setDataFormat(fmt.getFormat("@"));

			DataFormat fmt1 = workbook.createDataFormat();
			CellStyle numStyle = workbook.createCellStyle();
			numStyle.setDataFormat(fmt1.getFormat("#,##0.000"));

			int rowNum = 6;
			int sn = 1;

			dailyList = transactionInquiryRep.findAllCustominddate(acid,fromDAte, toDAte);
			for (TransactionInquiry daily_List : dailyList) {
				Row row = sheet.createRow(++rowNum);
				writeBook_Loan(daily_List, row, dateCellStyle, sn, cellStyle,numStyle);
				sn++;
			}

			for (int i = 0; i < columns_Loan.length; i++) {
				sheet.autoSizeColumn(i);
			}

			workbook.write(out);

			out.close();

			workbook.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ByteArrayInputStream(out.toByteArray());

	}
	
	
	private void writeBook_Loan(TransactionInquiry aBook, Row row, CellStyle dateCellStyle, int sn,
			CellStyle dateCell,CellStyle numStyle) {

		Cell cell = row.createCell(0);
		cell.setCellValue(sn);
		cell.setCellStyle(dateCell);

		Cell cif = row.createCell(1);
		cif.setCellValue(aBook.getTran_date());
		cif.setCellStyle(dateCellStyle);

		Cell lastname = row.createCell(2);
		lastname.setCellValue(aBook.getTran_id());
		lastname.setCellStyle(dateCell);

		Cell firstname = row.createCell(3);
		if(aBook.getPart_tran_type().equals("D")) {
			firstname.setCellValue("Debit");
		}else {
			firstname.setCellValue("Credit");
		}
		
		firstname.setCellStyle(dateCell);

		Cell nid = row.createCell(4);
		nid.setCellValue(aBook.getValue_date());
		nid.setCellStyle(dateCellStyle);

		Cell risk_cat = row.createCell(5);
		risk_cat.setCellValue(aBook.getTran_particular());
		risk_cat.setCellStyle(dateCell);

		Cell pep_desc = row.createCell(6);
		if(aBook.getPart_tran_type().equals("D")) {
			pep_desc.setCellValue("-"+String.format("%,.2f", aBook.getTran_amt()));
		}else {
			pep_desc.setCellValue("-");
		}
		
		
		pep_desc.setCellStyle(numStyle);

		Cell cust_pos = row.createCell(7);
		if(aBook.getPart_tran_type().equals("C")) {
			cust_pos.setCellValue(String.format("%,.2f", aBook.getTran_amt()));
		}else {
			cust_pos.setCellValue("-");
		}
		
		
		cust_pos.setCellStyle(numStyle);

		Cell membershipDate = row.createCell(8);
		membershipDate.setCellValue(aBook.getTran_crncy_code());
		membershipDate.setCellStyle(dateCellStyle);

			
	}


}
