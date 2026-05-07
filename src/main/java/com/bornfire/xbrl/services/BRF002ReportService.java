package com.bornfire.xbrl.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.AuditReasonDTO;
import com.bornfire.xbrl.entities.BRF2_ARCHIVENTITY;
import com.bornfire.xbrl.entities.TransactionmastertableRep;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.BRBS.BRF1_DETAIL_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF2_DETAIL_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF2_DetaiRep;
import com.bornfire.xbrl.entities.BRBS.BRF2_ENTITY;
import com.bornfire.xbrl.entities.BRBS.MANUAL_Service_Entity;
import com.bornfire.xbrl.entities.BRBS.MANUAL_Service_Rep;
import com.bornfire.xbrl.entities.BRBS.T1CurProdDetail;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;


@Service
@Transactional
@ConfigurationProperties("output")
public class BRF002ReportService {

	private static final Logger logger = LoggerFactory.getLogger(BRF002ReportService.class);

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	DataSource srcdataSource;
	
	@Autowired
	Environment env;
	
	@Autowired
	TransactionmastertableRep rep;
	
	@Autowired
	BRF2_DetaiRep BRF2_DetaiRep1;
	
	@Autowired
	MANUAL_Service_Rep mANUAL_Service_Rep;
	
	@Autowired
	SequenceGenerator sequence;

	@Autowired
	UserProfileRep userProfileRep;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
    
	
	DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");


	public String preCheck(String reportid, String fromdate, String todate) {
		
			String msg = "";
			Session hs = sessionFactory.getCurrentSession();
			Date dt1;
			Date dt9;
			logger.info("Report precheck : " + reportid);

			try {
				
				dt9 = new SimpleDateFormat("dd/MM/yyyy").parse(todate);
				logger.info("Getting No of records in Master table :" + reportid);
				Long dtlcnt = (Long) hs.createQuery("select count(*) from BRF2_ENTITY a where a.report_date=?1")
						.setParameter(1, dt9).getSingleResult();

				if (dtlcnt > 0) {
					logger.info("Getting No of records in Mod table :" + reportid);
					Long modcnt = (Long) hs.createQuery("select count(*) from BRF2_ENTITY a").getSingleResult();
					if (modcnt > 0) {
						msg = "success";
					}
				} else {
				//	msg = "Data Not available for the Report. Please Contact Administrator";
					msg = "success";

				}

			} catch (Exception e) {
				logger.info(e.getMessage());
				msg = "success";
				e.printStackTrace();

			}

			return msg;

		}	
	public ModelAndView getBRF002View(String reportId, String fromdate, String todate, String currency, String dtltype,
			Pageable pageable) {
		
			ModelAndView mv = new ModelAndView();
			Session hs = sessionFactory.getCurrentSession();
			int pageSize = pageable.getPageSize();
			int currentPage = pageable.getPageNumber();
			int startItem = currentPage * pageSize;
			List<BRF2_ENTITY> T1rep = new ArrayList<BRF2_ENTITY>();
			// Query<Object[]> qr;

			List<BRF2_ENTITY> T1Master = new ArrayList<BRF2_ENTITY>();
			/* List<BRF73_TABLE2> T1Master1 = new ArrayList<BRF73_TABLE2>(); */

			logger.info("Inside archive" +currency);

			try {
				Date d1 = df.parse(todate);
			//	T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

				T1Master = hs.createQuery("from  BRF2_ENTITY a where a.report_date = ?1 ", BRF2_ENTITY.class)
						.setParameter(1, df.parse(todate)).getResultList();

				/*
				 * T1Master1 = hs.createQuery("from BRF73_TABLE2 a where a.report_date = ?1 ",
				 * BRF73_TABLE2.class) .setParameter(1, df.parse(todate)).getResultList();
				 */

			} catch (ParseException e) {
				e.printStackTrace();
			}

			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			mv.setViewName("RR/BRF2");
			// mv.addObject("currlist", refCodeConfig.currList());
			mv.addObject("reportsummary", T1Master);
			/* mv.addObject("reportsummary1", T1Master1); */
			mv.addObject("reportmaster", T1Master);
			mv.addObject("displaymode", "summary");
			mv.addObject("reportsflag", "reportsflag");
			mv.addObject("menu", reportId);
			System.out.println("scv" + mv.getViewName());

			return mv;

		}
	
		
	public ModelAndView getBRF002currentDtl(String reportId, String fromdate, String todate, String currency,
			String dtltype, Pageable pageable, String filter,String searchVal) {

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		//int startItem = currentPage * pageSize;
		int startItem = (int) pageable.getOffset();

		ModelAndView mv = new ModelAndView();

		Session hs = sessionFactory.getCurrentSession();
		List<Object> T1Dt1 = new ArrayList<Object>();

		Query<Object[]> qr;
		Query<Number> countQr;

		String searchCondition = "";
		if (searchVal != null && !searchVal.trim().isEmpty()) {
			String safeSearch = searchVal.replace("'", "''").toUpperCase();
			searchCondition = " and (CUST_ID like '%" + safeSearch + "%' or FORACID like '%" + safeSearch
					+ "%' or ACCT_NAME like '%" + safeSearch + "%' or ACT_BALANCE_AMT_LC like '%" + safeSearch
					+ "%' or REPORT_NAME_1 like '%" + safeSearch + "%' or REPORT_LABEL_1 like '%" + safeSearch
					+ "%' or REPORT_ADDL_CRITERIA_1 like '%" + safeSearch + "%' or REPORT_DATE like '%" + safeSearch
					+ "%') ";
		}

		if (dtltype.equals("report") || dtltype.equals("ARCH")) {
			if (!filter.equals("null")) {
				qr = hs.createNativeQuery(
						"select * from BRF2_DETAILTABLE  a where report_date = ?1 and report_label_1 =?2" + searchCondition);
				countQr = hs.createNativeQuery("select count(*) from BRF2_DETAILTABLE a where report_date = ?1 and report_label_1 = ?2" + searchCondition);

				qr.setParameter(2, filter);
				countQr.setParameter(2, filter);

			} else {
				qr = hs.createNativeQuery("select * from BRF2_DETAILTABLE a where report_date = ?1" + searchCondition);
				countQr = hs.createNativeQuery("select count(*) from BRF2_DETAILTABLE a where report_date = ?1" + searchCondition);
			}
		} else {
			qr = hs.createNativeQuery("select * from BRF2_DETAILTABLE  where report_date = ?1" + searchCondition);
			countQr = hs.createNativeQuery("select count(*) from BRF2_DETAILTABLE where report_date = ?1" + searchCondition);
		}

		try {
			qr.setParameter(1, df.parse(todate));
			countQr.setParameter(1, df.parse(todate));

		} catch (ParseException e) {
			e.printStackTrace();
		}
		long totalRecords = countQr.getSingleResult().longValue();

		logger.info("REQUESTED PAGE SIZE: " + pageSize);
		logger.info("REQUESTED OFFSET: " + startItem);

		qr.setFirstResult(startItem);
		qr.setMaxResults(pageSize);
		
		List<BRF2_DETAIL_ENTITY> T1Master = new ArrayList<BRF2_DETAIL_ENTITY>();
		/*
		try {
			T1Master = hs.createQuery("from BRF2_DETAIL_ENTITY a where a.report_date = ?1", BRF2_DETAIL_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();
		} catch (ParseException e) {
 
			e.printStackTrace();
		}
 */

		logger.info("Getting Report Detail for : " + reportId + "," + fromdate + "," + todate + "," + currency);
		List<Object[]> result = qr.getResultList();
		for (Object[] a : result) {
			String cust_id = (String) a[0];
			String foracid = (String) a[1];
			BigDecimal act_balance_amt_lc = (BigDecimal) a[2];
			BigDecimal eab_lc = (BigDecimal) a[3];
			String acct_name = (String) a[4];
			String acct_crncy_code = (String) a[5];
			String gl_code = (String) a[6];
			String gl_sub_head_code = (String) a[7];
			String gl_sub_head_desc = (String) a[8];
			String country_of_incorp = (String) a[9];
			String cust_type = (String) a[10];
			String schm_code = (String) a[11];
			String schm_type = (String) a[12];
			String sol_id = (String) a[13];
			String acid = (String) a[14];
			String segment = (String) a[15];
			String sub_segment = (String) a[16];
			BigDecimal sector = (BigDecimal) a[17];
			String sub_sector = (String) a[18];
			String sector_code = (String) a[19];
			String group_id = (String) a[20];
			String constitution_code = (String) a[21];
			String country = (String) a[22];
			String legal_entity_type = (String) a[23];
			String constitution_desc = (String) a[24];
			String purpose_of_advn = (String) a[25];
			BigDecimal hni_networth = (BigDecimal) a[26];
			String turnover = (String) a[27];
			String bacid = (String) a[28];
			String report_name_1 = (String) a[29];
			String report_label_1 = (String) a[30];
			String report_addl_criteria_1 = (String) a[31];
			String report_addl_criteria_2 = (String) a[32];
			String report_addl_criteria_3 = (String) a[33];
			String create_user = (String) a[34];
			Date create_time = (Date) a[35];
			String modify_user = (String) a[36];
			Date modify_time = (Date) a[37];
			String verify_user = (String) a[38];
			Date verify_time = (Date) a[39];
			Character entity_flg = (Character) a[40];
			Character modify_flg = (Character) a[41];
			Character del_flg = (Character) a[42];
			Character nre_status = (Character) a[43];
			Date report_date = (Date) a[44];
			Date maturity_date = (Date) a[45];
			String gender = (String) a[46];
			String version = (String) a[47];
			String remarks = (String) a[48];
			Character nre_flag = (Character) a[49];

			String Remarks1;

			if (act_balance_amt_lc != null) {
				if (act_balance_amt_lc.toString().contains("-")) {
					Remarks1 = "DR";
				} else {
					Remarks1 = "CR";
				}
			} else {
				Remarks1 = "";
			}

			BRF2_DETAIL_ENTITY py = new BRF2_DETAIL_ENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
					acct_crncy_code, gl_code, gl_sub_head_code, gl_sub_head_desc, country_of_incorp, cust_type,
					schm_code, schm_type, sol_id, acid, segment, sub_segment, sector, sub_sector, sector_code, group_id,
					constitution_code, country, legal_entity_type, constitution_desc, purpose_of_advn, hni_networth,
					turnover, bacid, report_name_1, report_label_1, report_addl_criteria_1, report_addl_criteria_2,
					report_addl_criteria_3, create_user, create_time, modify_user, modify_time, verify_user,
					verify_time, entity_flg, modify_flg, del_flg, nre_status, report_date, maturity_date, gender,
					version, remarks, nre_flag);

			T1Dt1.add(py);

		};


		List<Object> pagedlist;

		if (T1Dt1.size() < startItem) {
			pagedlist = Collections.emptyList();
		} else {
			int toIndex = Math.min(startItem + pageSize, T1Dt1.size());
			pagedlist = T1Dt1.subList(startItem, toIndex);
		}

		logger.info("Converting to Page");
		//Page<Object> T1Dt1Page = new PageImpl<Object>(pagedlist, PageRequest.of(currentPage, pageSize), T1Dt1.size());

		Page<Object> T1Dt1Page = new PageImpl<>(T1Dt1, pageable, totalRecords);
		mv.addObject("reportdetailsPage", T1Dt1Page);
		mv.setViewName("RR" + "/" + "BRF2::reportcontent");
		mv.addObject("reportdetails", T1Dt1Page.getContent());
		mv.addObject("reportmaster", T1Master);
		mv.addObject("reportmaster1", qr);
		mv.addObject("singledetail", new T1CurProdDetail());
		mv.addObject("reportsflag", "reportsflag");
		mv.addObject("menu", reportId);
		mv.addObject("dtltype", dtltype);
		return mv;
	}


	
	
	
	public File getFile(String reportId, String fromdate, String todate, String currency, String dtltype,
			String filetype, String filter) throws FileNotFoundException, JRException, SQLException {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		String path = this.env.getProperty("output.exportpath");
		String fileName = "";
		String zipFileName = "";
		File outputFile;
		System.out.println(filter);
		System.out.println(todate);
		logger.info("Getting Output file :" + reportId);
		fileName = "011-BRF-002-A";
		

		if (!filetype.equals("xbrl")) {
			if(!filetype.contains("BRF")) {

			try {
				InputStream jasperFile;
				logger.info("Getting Jasper file :" + reportId);
				if (filetype.equals("detailexcel")) {
					if (dtltype.equals("report")) {
					    jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF2_Detail.jrxml");
					}else {
						jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF2_Detail.jrxml");
					}

				} else {
					if (dtltype.equals("report")) {
						logger.info("Inside report");
						jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF2.jrxml");
					} else {
						jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF2.jrxml");
					}
				}

				/* JasperReport jr = (JasperReport) JRLoader.loadObject(jasperFile); */
				JasperReport jr = JasperCompileManager.compileReport(jasperFile);
				HashMap<String, Object> map = new HashMap<String, Object>();

				logger.info("Assigning Parameters for Jasper");
				map.put("REPORT_DATE", todate);
				map.put("CELL_MAPPING", filter);
				
				if (filetype.equals("pdf")) {
					System.out.println("PDF");
					fileName = fileName + ".pdf";
					path += fileName;
					JasperPrint jp = JasperFillManager.fillReport(jr, map, srcdataSource.getConnection());
					JasperExportManager.exportReportToPdfFile(jp, path);
					logger.info("PDF File exported");
				} else {

					System.out.println("EXCEEEEEll");
					fileName = fileName + ".xlsx";
					path += fileName;
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
			outputFile = new File(path);
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
					.getRequest();
			String user1 = (String) request.getSession().getAttribute("USERID");
			String username = (String) request.getSession().getAttribute("USERNAME");

			String auditID = sequence.generateRequestUUId();

			MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(user1);
			audit.setAudit_table("BRF2_SUMMARYTABLE");
			audit.setEvent_id(user1);
			audit.setEvent_name(username);

			if (filetype.equals("pdf")) {
				audit.setFunc_code("DOWNLOAD_PDF");
				audit.setAudit_screen("Download PDF");
				audit.setRemarks("BRF2 PDF downloaded successfully");
			} else if (filetype.equals("detailexcel")) {
				audit.setAudit_table("BRF2_DETAILTABLE");
				audit.setFunc_code("DOWNLOAD_EXCEL_DETAIL");
				audit.setAudit_screen("Download Excel Detail");
				audit.setRemarks("BRF2 Detailed Excel downloaded successfully");
			} else {
				audit.setFunc_code("DOWNLOAD");
				audit.setAudit_screen("Download");
				audit.setRemarks("BRF2 File downloaded successfully");
			}

			UserProfile values1 = userProfileRep.getRole(user1);
			audit.setAuth_user(values1.getAuth_user());
			audit.setAuth_time(values1.getAuth_time());
			audit.setAudit_ref_no(auditID);

			mANUAL_Service_Rep.save(audit);
			return outputFile;
			}else {	
		
		
		List<BRF2_ENTITY> T1Master = new ArrayList<BRF2_ENTITY>();
		Session hs = sessionFactory.getCurrentSession();
		try {
			Date d1 = df.parse(todate);
		
		// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

		T1Master = hs.createQuery("from  BRF2_ENTITY a where a.report_date = ?1 ", BRF2_ENTITY.class)
				.setParameter(1, df.parse(todate)).getResultList();
		
		if(T1Master.size() == 1) {
			
			for(BRF2_ENTITY BRF002 : T1Master ) {
		
		File Responsecamt = new File(env.getProperty("output.exportpathtemp")+"011-BRF-002-AT.xls");
		
		  // Load the Excel file
    	Workbook workbook = WorkbookFactory.create(Responsecamt);

        // Get the first sheet
    	Sheet sheet = workbook.getSheetAt(0);
    	
		/*
		 * Row r1row = sheet.getRow(4); if (r1row != null) { Cell r1cell =
		 * r1row.getCell(2); if (r1cell == null) { r1cell = r1row.createCell(2); //
		 * Create the cell if it doesn't exist } if (BRF002.getReport_date() != null) {
		 * // Formatting the date to dd-MM-yyyy SimpleDateFormat sdf = new
		 * SimpleDateFormat("dd-MM-yyyy"); String formattedDate =
		 * sdf.format(BRF002.getReport_date()); r1cell.setCellValue(formattedDate); //
		 * Set the formatted date in the cell } else { r1cell.setCellValue(""); // Set
		 * an empty value if the report_date is null } }
		 */
    	
		
    	
    	
    /////srl_no -12/////////
		Row row2 = sheet.getRow(11);
		Cell R2cell = row2.getCell(4);
		if (R2cell != null) {
			R2cell.setCellValue(BRF002.getR2_no_aed_resident() == null ? 0 : BRF002.getR2_no_aed_resident().intValue());
		}
		Cell R2cell1 = row2.getCell(5);
		if (R2cell1 != null) {
			R2cell1.setCellValue(
					BRF002.getR2_amount_aed_resident() == null ? 0 : BRF002.getR2_amount_aed_resident().intValue());
		}
		Cell R2cell2 = row2.getCell(6);
		if (R2cell2 != null) {
			R2cell2.setCellValue(
					BRF002.getR2_no_fcy_resident() == null ? 0 : BRF002.getR2_no_fcy_resident().intValue());
		}
		Cell R2cell3 = row2.getCell(7);
		if (R2cell3 != null) {
			R2cell3.setCellValue(
					BRF002.getR2_amount_fcy_resident() == null ? 0 : BRF002.getR2_amount_fcy_resident().intValue());
		}
		Cell R2cell4 = row2.getCell(8);
		if (R2cell4 != null) {
			R2cell4.setCellValue(
					BRF002.getR2_no_aed_non_resident() == null ? 0 : BRF002.getR2_no_aed_non_resident().intValue());
		}
		Cell R2cell5 = row2.getCell(9);
		if (R2cell5 != null) {
			R2cell5.setCellValue(BRF002.getR2_amount_aed_non_resident() == null ? 0
					: BRF002.getR2_amount_aed_non_resident().intValue());
		}
		Cell R2cell6 = row2.getCell(10);
		if (R2cell6 != null) {
			R2cell6.setCellValue(
					BRF002.getR2_no_fcy_non_resident() == null ? 0 : BRF002.getR2_no_fcy_non_resident().intValue());
		}
		Cell R2cell7 = row2.getCell(11);
		if (R2cell7 != null) {
			R2cell7.setCellValue(BRF002.getR2_amount_fcy_non_resident() == null ? 0
					: BRF002.getR2_amount_fcy_non_resident().intValue());
		}
    	
    /////srl_no -13/////////
	Row row3 = sheet.getRow(12);
	Cell R3cell = row3.getCell(4);
	if (R3cell != null) {
		R3cell.setCellValue(BRF002.getR3_no_aed_resident() == null ? 0 : BRF002.getR3_no_aed_resident().intValue());
	}
	Cell R3cell1 = row3.getCell(5);
	if (R3cell1 != null) {
		R3cell1.setCellValue(
				BRF002.getR3_amount_aed_resident() == null ? 0 : BRF002.getR3_amount_aed_resident().intValue());
	}
	Cell R3cell2 = row3.getCell(6);
	if (R3cell2 != null) {
		R3cell2.setCellValue(BRF002.getR3_no_fcy_resident() == null ? 0 : BRF002.getR3_no_fcy_resident().intValue());
	}
	Cell R3cell3 = row3.getCell(7);
	if (R3cell3 != null) {
		R3cell3.setCellValue(
				BRF002.getR3_amount_fcy_resident() == null ? 0 : BRF002.getR3_amount_fcy_resident().intValue());
	}
	Cell R3cell4 = row3.getCell(8);
	if (R3cell4 != null) {
		R3cell4.setCellValue(
				BRF002.getR3_no_aed_non_resident() == null ? 0 : BRF002.getR3_no_aed_non_resident().intValue());
	}
	Cell R3cell5 = row3.getCell(9);
	if (R3cell5 != null) {
		R3cell5.setCellValue(
				BRF002.getR3_amount_aed_non_resident() == null ? 0 : BRF002.getR3_amount_aed_non_resident().intValue());
	}
	Cell R3cell6 = row3.getCell(10);
	if (R3cell6 != null) {
		R3cell6.setCellValue(
				BRF002.getR3_no_fcy_non_resident() == null ? 0 : BRF002.getR3_no_fcy_non_resident().intValue());
	}
	Cell R3cell7 = row3.getCell(11);
	if (R3cell7 != null) {
		R3cell7.setCellValue(
				BRF002.getR3_amount_fcy_non_resident() == null ? 0 : BRF002.getR3_amount_fcy_non_resident().intValue());
	}
	///// srl_no -14/////////
	Row row4 = sheet.getRow(13);
	Cell R4cell = row4.getCell(4);
	if (R4cell != null) {
		R4cell.setCellValue(BRF002.getR4_no_aed_resident() == null ? 0 : BRF002.getR4_no_aed_resident().intValue());
	}
	Cell R4cell1 = row4.getCell(5);
	if (R4cell1 != null) {
		R4cell1.setCellValue(
				BRF002.getR4_amount_aed_resident() == null ? 0 : BRF002.getR4_amount_aed_resident().intValue());
	}
	Cell R4cell2 = row4.getCell(6);
	if (R4cell2 != null) {
		R4cell2.setCellValue(BRF002.getR4_no_fcy_resident() == null ? 0 : BRF002.getR4_no_fcy_resident().intValue());
	}
	Cell R4cell3 = row4.getCell(7);
	if (R4cell3 != null) {
		R4cell3.setCellValue(
				BRF002.getR4_amount_fcy_resident() == null ? 0 : BRF002.getR4_amount_fcy_resident().intValue());
	}
	Cell R4cell4 = row4.getCell(8);
	if (R4cell4 != null) {
		R4cell4.setCellValue(
				BRF002.getR4_no_aed_non_resident() == null ? 0 : BRF002.getR4_no_aed_non_resident().intValue());
	}
	Cell R4cell5 = row4.getCell(9);
	if (R4cell5 != null) {
		R4cell5.setCellValue(
				BRF002.getR4_amount_aed_non_resident() == null ? 0 : BRF002.getR4_amount_aed_non_resident().intValue());
	}
	Cell R4cell6 = row4.getCell(10);
	if (R4cell6 != null) {
		R4cell6.setCellValue(
				BRF002.getR4_no_fcy_non_resident() == null ? 0 : BRF002.getR4_no_fcy_non_resident().intValue());
	}
	Cell R4cell7 = row4.getCell(11);
	if (R4cell7 != null) {
		R4cell7.setCellValue(
				BRF002.getR4_amount_fcy_non_resident() == null ? 0 : BRF002.getR4_amount_fcy_non_resident().intValue());
	}

	///// srl_no -15/////////
	Row row5 = sheet.getRow(14);
	Cell R5cell = row5.getCell(4);
	if (R5cell != null) {
		R5cell.setCellValue(BRF002.getR5_no_aed_resident() == null ? 0 : BRF002.getR5_no_aed_resident().intValue());
	}
	Cell R5cell1 = row5.getCell(5);
	if (R5cell1 != null) {
		R5cell1.setCellValue(
				BRF002.getR5_amount_aed_resident() == null ? 0 : BRF002.getR5_amount_aed_resident().intValue());
	}
	Cell R5cell2 = row5.getCell(6);
	if (R5cell2 != null) {
		R5cell2.setCellValue(BRF002.getR5_no_fcy_resident() == null ? 0 : BRF002.getR5_no_fcy_resident().intValue());
	}
	Cell R5cell3 = row5.getCell(7);
	if (R5cell3 != null) {
		R5cell3.setCellValue(
				BRF002.getR5_amount_fcy_resident() == null ? 0 : BRF002.getR5_amount_fcy_resident().intValue());
	}
	Cell R5cell4 = row5.getCell(8);
	if (R5cell4 != null) {
		R5cell4.setCellValue(
				BRF002.getR5_no_aed_non_resident() == null ? 0 : BRF002.getR5_no_aed_non_resident().intValue());
	}
	Cell R5cell5 = row5.getCell(9);
	if (R5cell5 != null) {
		R5cell5.setCellValue(
				BRF002.getR5_amount_aed_non_resident() == null ? 0 : BRF002.getR5_amount_aed_non_resident().intValue());
	}
	Cell R5cell6 = row5.getCell(10);
	if (R5cell6 != null) {
		R5cell6.setCellValue(
				BRF002.getR5_no_fcy_non_resident() == null ? 0 : BRF002.getR5_no_fcy_non_resident().intValue());
	}
	Cell R5cell7 = row5.getCell(11);
	if (R5cell7 != null) {
		R5cell7.setCellValue(
				BRF002.getR5_amount_fcy_non_resident() == null ? 0 : BRF002.getR5_amount_fcy_non_resident().intValue());
	}

	///// srl_no -17/////////
	Row row7 = sheet.getRow(16);
	Cell R7cell = row7.getCell(4);
	if (R7cell != null) {
		R7cell.setCellValue(BRF002.getR7_no_aed_resident() == null ? 0 : BRF002.getR7_no_aed_resident().intValue());
	}
	Cell R7cell1 = row7.getCell(5);
	if (R7cell1 != null) {
		R7cell1.setCellValue(
				BRF002.getR7_amount_aed_resident() == null ? 0 : BRF002.getR7_amount_aed_resident().intValue());
	}
	Cell R7cell2 = row7.getCell(6);
	if (R7cell2 != null) {
		R7cell2.setCellValue(BRF002.getR7_no_fcy_resident() == null ? 0 : BRF002.getR7_no_fcy_resident().intValue());
	}
	Cell R7cell3 = row7.getCell(7);
	if (R7cell3 != null) {
		R7cell3.setCellValue(
				BRF002.getR7_amount_fcy_resident() == null ? 0 : BRF002.getR7_amount_fcy_resident().intValue());
	}
	Cell R7cell4 = row7.getCell(8);
	if (R7cell4 != null) {
		R7cell4.setCellValue(
				BRF002.getR7_no_aed_non_resident() == null ? 0 : BRF002.getR7_no_aed_non_resident().intValue());
	}
	Cell R7cell5 = row7.getCell(9);
	if (R7cell5 != null) {
		R7cell5.setCellValue(
				BRF002.getR7_amount_aed_non_resident() == null ? 0 : BRF002.getR7_amount_aed_non_resident().intValue());
	}
	Cell R7cell6 = row7.getCell(10);
	if (R7cell6 != null) {
		R7cell6.setCellValue(
				BRF002.getR7_no_fcy_non_resident() == null ? 0 : BRF002.getR7_no_fcy_non_resident().intValue());
	}
	Cell R7cell7 = row7.getCell(11);
	if (R7cell7 != null) {
		R7cell7.setCellValue(
				BRF002.getR7_amount_fcy_non_resident() == null ? 0 : BRF002.getR7_amount_fcy_non_resident().intValue());
	}

	///// srl_no -18/////////
	Row row8 = sheet.getRow(17);
	Cell R8cell = row8.getCell(4);
	if (R8cell != null) {
		R8cell.setCellValue(BRF002.getR8_no_aed_resident() == null ? 0 : BRF002.getR8_no_aed_resident().intValue());
	}
	Cell R8cell1 = row8.getCell(5);
	if (R8cell1 != null) {
		R8cell1.setCellValue(
				BRF002.getR8_amount_aed_resident() == null ? 0 : BRF002.getR8_amount_aed_resident().intValue());
	}
	Cell R8cell2 = row8.getCell(6);
	if (R8cell2 != null) {
		R8cell2.setCellValue(BRF002.getR8_no_fcy_resident() == null ? 0 : BRF002.getR8_no_fcy_resident().intValue());
	}
	Cell R8cell3 = row8.getCell(7);
	if (R8cell3 != null) {
		R8cell3.setCellValue(
				BRF002.getR8_amount_fcy_resident() == null ? 0 : BRF002.getR8_amount_fcy_resident().intValue());
	}
	Cell R8cell4 = row8.getCell(8);
	if (R8cell4 != null) {
		R8cell4.setCellValue(
				BRF002.getR8_no_aed_non_resident() == null ? 0 : BRF002.getR8_no_aed_non_resident().intValue());
	}
	Cell R8cell5 = row8.getCell(9);
	if (R8cell5 != null) {
		R8cell5.setCellValue(
				BRF002.getR8_amount_aed_non_resident() == null ? 0 : BRF002.getR8_amount_aed_non_resident().intValue());
	}
	Cell R8cell6 = row8.getCell(10);
	if (R8cell6 != null) {
		R8cell6.setCellValue(
				BRF002.getR8_no_fcy_non_resident() == null ? 0 : BRF002.getR8_no_fcy_non_resident().intValue());
	}
	Cell R8cell7 = row8.getCell(11);
	if (R8cell7 != null) {
		R8cell7.setCellValue(
				BRF002.getR8_amount_fcy_non_resident() == null ? 0 : BRF002.getR8_amount_fcy_non_resident().intValue());
	}

	///// srl_no -19/////////
	Row row9 = sheet.getRow(18);
	Cell R9cell = row9.getCell(4);
	if (R9cell != null) {
		R9cell.setCellValue(BRF002.getR9_no_aed_resident() == null ? 0 : BRF002.getR9_no_aed_resident().intValue());
	}
	Cell R9cell1 = row9.getCell(5);
	if (R9cell1 != null) {
		R9cell1.setCellValue(
				BRF002.getR9_amount_aed_resident() == null ? 0 : BRF002.getR9_amount_aed_resident().intValue());
	}
	Cell R9cell2 = row9.getCell(6);
	if (R9cell2 != null) {
		R9cell2.setCellValue(BRF002.getR9_no_fcy_resident() == null ? 0 : BRF002.getR9_no_fcy_resident().intValue());
	}
	Cell R9cell3 = row9.getCell(7);
	if (R9cell3 != null) {
		R9cell3.setCellValue(
				BRF002.getR9_amount_fcy_resident() == null ? 0 : BRF002.getR9_amount_fcy_resident().intValue());
	}
	Cell R9cell4 = row9.getCell(8);
	if (R9cell4 != null) {
		R9cell4.setCellValue(
				BRF002.getR9_no_aed_non_resident() == null ? 0 : BRF002.getR9_no_aed_non_resident().intValue());
	}
	Cell R9cell5 = row9.getCell(9);
	if (R9cell5 != null) {
		R9cell5.setCellValue(
				BRF002.getR9_amount_aed_non_resident() == null ? 0 : BRF002.getR9_amount_aed_non_resident().intValue());
	}
	Cell R9cell6 = row9.getCell(10);
	if (R9cell6 != null) {
		R9cell6.setCellValue(
				BRF002.getR9_no_fcy_non_resident() == null ? 0 : BRF002.getR9_no_fcy_non_resident().intValue());
	}
	Cell R9cell7 = row9.getCell(11);
	if (R9cell7 != null) {
		R9cell7.setCellValue(
				BRF002.getR9_amount_fcy_non_resident() == null ? 0 : BRF002.getR9_amount_fcy_non_resident().intValue());
	}

	///// srl_no -20/////////
	Row row10 = sheet.getRow(19);
	Cell R10cell = row10.getCell(4);
	if (R10cell != null) {
		R10cell.setCellValue(BRF002.getR10_no_aed_resident() == null ? 0 : BRF002.getR10_no_aed_resident().intValue());
	}
	Cell R10cell1 = row10.getCell(5);
	if (R10cell1 != null) {
		R10cell1.setCellValue(
				BRF002.getR10_amount_aed_resident() == null ? 0 : BRF002.getR10_amount_aed_resident().intValue());
	}
	Cell R10cell2 = row10.getCell(6);
	if (R10cell2 != null) {
		R10cell2.setCellValue(BRF002.getR10_no_fcy_resident() == null ? 0 : BRF002.getR10_no_fcy_resident().intValue());
	}
	Cell R10cell3 = row10.getCell(7);
	if (R10cell3 != null) {
		R10cell3.setCellValue(
				BRF002.getR10_amount_fcy_resident() == null ? 0 : BRF002.getR10_amount_fcy_resident().intValue());
	}
	Cell R10cell4 = row10.getCell(8);
	if (R10cell4 != null) {
		R10cell4.setCellValue(
				BRF002.getR10_no_aed_non_resident() == null ? 0 : BRF002.getR10_no_aed_non_resident().intValue());
	}
	Cell R10cell5 = row10.getCell(9);
	if (R10cell5 != null) {
		R10cell5.setCellValue(BRF002.getR10_amount_aed_non_resident() == null ? 0
				: BRF002.getR10_amount_aed_non_resident().intValue());
	}
	Cell R10cell6 = row10.getCell(10);
	if (R10cell6 != null) {
		R10cell6.setCellValue(
				BRF002.getR10_no_fcy_non_resident() == null ? 0 : BRF002.getR10_no_fcy_non_resident().intValue());
	}
	Cell R10cell7 = row10.getCell(11);
	if (R10cell7 != null) {
		R10cell7.setCellValue(BRF002.getR10_amount_fcy_non_resident() == null ? 0
				: BRF002.getR10_amount_fcy_non_resident().intValue());
	}

	///// srl_no -23/////////
	Row row13 = sheet.getRow(22);
	Cell R13cell = row13.getCell(4);
	if (R13cell != null) {
		R13cell.setCellValue(BRF002.getR13_no_aed_resident() == null ? 0 : BRF002.getR13_no_aed_resident().intValue());
	}
	Cell R13cell1 = row13.getCell(5);
	if (R13cell1 != null) {
		R13cell1.setCellValue(
				BRF002.getR13_amount_aed_resident() == null ? 0 : BRF002.getR13_amount_aed_resident().intValue());
	}
	Cell R13cell2 = row13.getCell(6);
	if (R13cell2 != null) {
		R13cell2.setCellValue(BRF002.getR13_no_fcy_resident() == null ? 0 : BRF002.getR13_no_fcy_resident().intValue());
	}
	Cell R13cell3 = row13.getCell(7);
	if (R13cell3 != null) {
		R13cell3.setCellValue(
				BRF002.getR13_amount_fcy_resident() == null ? 0 : BRF002.getR13_amount_fcy_resident().intValue());
	}
	Cell R13cell4 = row13.getCell(8);
	if (R13cell4 != null) {
		R13cell4.setCellValue(
				BRF002.getR13_no_aed_non_resident() == null ? 0 : BRF002.getR13_no_aed_non_resident().intValue());
	}
	Cell R13cell5 = row13.getCell(9);
	if (R13cell5 != null) {
		R13cell5.setCellValue(BRF002.getR13_amount_aed_non_resident() == null ? 0
				: BRF002.getR13_amount_aed_non_resident().intValue());
	}
	Cell R13cell6 = row13.getCell(10);
	if (R13cell6 != null) {
		R13cell6.setCellValue(
				BRF002.getR13_no_fcy_non_resident() == null ? 0 : BRF002.getR13_no_fcy_non_resident().intValue());
	}
	Cell R13cell7 = row13.getCell(11);
	if (R13cell7 != null) {
		R13cell7.setCellValue(BRF002.getR13_amount_fcy_non_resident() == null ? 0
				: BRF002.getR13_amount_fcy_non_resident().intValue());
	}

	///// srl_no -24/////////
	Row row14 = sheet.getRow(23);
	Cell R14cell = row14.getCell(4);
	if (R14cell != null) {
		R14cell.setCellValue(BRF002.getR14_no_aed_resident() == null ? 0 : BRF002.getR14_no_aed_resident().intValue());
	}
	Cell R14cell1 = row14.getCell(5);
	if (R14cell1 != null) {
		R14cell1.setCellValue(
				BRF002.getR14_amount_aed_resident() == null ? 0 : BRF002.getR14_amount_aed_resident().intValue());
	}
	Cell R14cell2 = row14.getCell(6);
	if (R14cell2 != null) {
		R14cell2.setCellValue(BRF002.getR14_no_fcy_resident() == null ? 0 : BRF002.getR14_no_fcy_resident().intValue());
	}
	Cell R14cell3 = row14.getCell(7);
	if (R14cell3 != null) {
		R14cell3.setCellValue(
				BRF002.getR14_amount_fcy_resident() == null ? 0 : BRF002.getR14_amount_fcy_resident().intValue());
	}
	Cell R14cell4 = row14.getCell(8);
	if (R14cell4 != null) {
		R14cell4.setCellValue(
				BRF002.getR14_no_aed_non_resident() == null ? 0 : BRF002.getR14_no_aed_non_resident().intValue());
	}
	Cell R14cell5 = row14.getCell(9);
	if (R14cell5 != null) {
		R14cell5.setCellValue(BRF002.getR14_amount_aed_non_resident() == null ? 0
				: BRF002.getR14_amount_aed_non_resident().intValue());
	}
	Cell R14cell6 = row14.getCell(10);
	if (R14cell6 != null) {
		R14cell6.setCellValue(
				BRF002.getR14_no_fcy_non_resident() == null ? 0 : BRF002.getR14_no_fcy_non_resident().intValue());
	}
	Cell R14cell7 = row14.getCell(11);
	if (R14cell7 != null) {
		R14cell7.setCellValue(BRF002.getR14_amount_fcy_non_resident() == null ? 0
				: BRF002.getR14_amount_fcy_non_resident().intValue());
	}

	///// srl_no -25/////////
	Row row15 = sheet.getRow(24);
	Cell R15cell = row15.getCell(4);
	if (R15cell != null) {
		R15cell.setCellValue(BRF002.getR15_no_aed_resident() == null ? 0 : BRF002.getR15_no_aed_resident().intValue());
	}
	Cell R15cell1 = row15.getCell(5);
	if (R15cell1 != null) {
		R15cell1.setCellValue(
				BRF002.getR15_amount_aed_resident() == null ? 0 : BRF002.getR15_amount_aed_resident().intValue());
	}
	Cell R15cell2 = row15.getCell(6);
	if (R15cell2 != null) {
		R15cell2.setCellValue(BRF002.getR15_no_fcy_resident() == null ? 0 : BRF002.getR15_no_fcy_resident().intValue());
	}
	Cell R15cell3 = row15.getCell(7);
	if (R15cell3 != null) {
		R15cell3.setCellValue(
				BRF002.getR15_amount_fcy_resident() == null ? 0 : BRF002.getR15_amount_fcy_resident().intValue());
	}
	Cell R15cell4 = row15.getCell(8);
	if (R15cell4 != null) {
		R15cell4.setCellValue(
				BRF002.getR15_no_aed_non_resident() == null ? 0 : BRF002.getR15_no_aed_non_resident().intValue());
	}
	Cell R15cell5 = row15.getCell(9);
	if (R15cell5 != null) {
		R15cell5.setCellValue(BRF002.getR15_amount_aed_non_resident() == null ? 0
				: BRF002.getR15_amount_aed_non_resident().intValue());
	}
	Cell R15cell6 = row15.getCell(10);
	if (R15cell6 != null) {
		R15cell6.setCellValue(
				BRF002.getR15_no_fcy_non_resident() == null ? 0 : BRF002.getR15_no_fcy_non_resident().intValue());
	}
	Cell R15cell7 = row15.getCell(11);
	if (R15cell7 != null) {
		R15cell7.setCellValue(BRF002.getR15_amount_fcy_non_resident() == null ? 0
				: BRF002.getR15_amount_fcy_non_resident().intValue());
	}

	///// srl_no -26/////////
	Row row16 = sheet.getRow(25);
	Cell R16cell = row16.getCell(4);
	if (R16cell != null) {
		R16cell.setCellValue(BRF002.getR16_no_aed_resident() == null ? 0 : BRF002.getR16_no_aed_resident().intValue());
	}
	Cell R16cell1 = row16.getCell(5);
	if (R16cell1 != null) {
		R16cell1.setCellValue(
				BRF002.getR16_amount_aed_resident() == null ? 0 : BRF002.getR16_amount_aed_resident().intValue());
	}
	Cell R16cell2 = row16.getCell(6);
	if (R16cell2 != null) {
		R16cell2.setCellValue(BRF002.getR16_no_fcy_resident() == null ? 0 : BRF002.getR16_no_fcy_resident().intValue());
	}
	Cell R16cell3 = row16.getCell(7);
	if (R16cell3 != null) {
		R16cell3.setCellValue(
				BRF002.getR16_amount_fcy_resident() == null ? 0 : BRF002.getR16_amount_fcy_resident().intValue());
	}
	Cell R16cell4 = row16.getCell(8);
	if (R16cell4 != null) {
		R16cell4.setCellValue(
				BRF002.getR16_no_aed_non_resident() == null ? 0 : BRF002.getR16_no_aed_non_resident().intValue());
	}
	Cell R16cell5 = row16.getCell(9);
	if (R16cell5 != null) {
		R16cell5.setCellValue(BRF002.getR16_amount_aed_non_resident() == null ? 0
				: BRF002.getR16_amount_aed_non_resident().intValue());
	}
	Cell R16cell6 = row16.getCell(10);
	if (R16cell6 != null) {
		R16cell6.setCellValue(
				BRF002.getR16_no_fcy_non_resident() == null ? 0 : BRF002.getR16_no_fcy_non_resident().intValue());
	}
	Cell R16cell7 = row16.getCell(11);
	if (R16cell7 != null) {
		R16cell7.setCellValue(BRF002.getR16_amount_fcy_non_resident() == null ? 0
				: BRF002.getR16_amount_fcy_non_resident().intValue());
	}

	///// srl_no -28/////////
	Row row18 = sheet.getRow(27);
	Cell R18cell = row18.getCell(4);
	if (R18cell != null) {
		R18cell.setCellValue(BRF002.getR18_no_aed_resident() == null ? 0 : BRF002.getR18_no_aed_resident().intValue());
	}
	Cell R18cell1 = row18.getCell(5);
	if (R18cell1 != null) {
		R18cell1.setCellValue(
				BRF002.getR18_amount_aed_resident() == null ? 0 : BRF002.getR18_amount_aed_resident().intValue());
	}
	Cell R18cell2 = row18.getCell(6);
	if (R18cell2 != null) {
		R18cell2.setCellValue(BRF002.getR18_no_fcy_resident() == null ? 0 : BRF002.getR18_no_fcy_resident().intValue());
	}
	Cell R18cell3 = row18.getCell(7);
	if (R18cell3 != null) {
		R18cell3.setCellValue(
				BRF002.getR18_amount_fcy_resident() == null ? 0 : BRF002.getR18_amount_fcy_resident().intValue());
	}
	Cell R18cell4 = row18.getCell(8);
	if (R18cell4 != null) {
		R18cell4.setCellValue(
				BRF002.getR18_no_aed_non_resident() == null ? 0 : BRF002.getR18_no_aed_non_resident().intValue());
	}
	Cell R18cell5 = row18.getCell(9);
	if (R18cell5 != null) {
		R18cell5.setCellValue(BRF002.getR18_amount_aed_non_resident() == null ? 0
				: BRF002.getR18_amount_aed_non_resident().intValue());
	}
	Cell R18cell6 = row18.getCell(10);
	if (R18cell6 != null) {
		R18cell6.setCellValue(
				BRF002.getR18_no_fcy_non_resident() == null ? 0 : BRF002.getR18_no_fcy_non_resident().intValue());
	}
	Cell R18cell7 = row18.getCell(11);
	if (R18cell7 != null) {
		R18cell7.setCellValue(BRF002.getR18_amount_fcy_non_resident() == null ? 0
				: BRF002.getR18_amount_fcy_non_resident().intValue());
	}
	///// srl_no -29/////////
	Row row19 = sheet.getRow(28);
	Cell R19cell = row19.getCell(4);
	if (R19cell != null) {
		R19cell.setCellValue(BRF002.getR19_no_aed_resident() == null ? 0 : BRF002.getR19_no_aed_resident().intValue());
	}
	Cell R19cell1 = row19.getCell(5);
	if (R19cell1 != null) {
		R19cell1.setCellValue(
				BRF002.getR19_amount_aed_resident() == null ? 0 : BRF002.getR19_amount_aed_resident().intValue());
	}
	Cell R19cell2 = row19.getCell(6);
	if (R19cell2 != null) {
		R19cell2.setCellValue(BRF002.getR19_no_fcy_resident() == null ? 0 : BRF002.getR19_no_fcy_resident().intValue());
	}
	Cell R19cell3 = row19.getCell(7);
	if (R19cell3 != null) {
		R19cell3.setCellValue(
				BRF002.getR19_amount_fcy_resident() == null ? 0 : BRF002.getR19_amount_fcy_resident().intValue());
	}
	Cell R19cell4 = row19.getCell(8);
	if (R19cell4 != null) {
		R19cell4.setCellValue(
				BRF002.getR19_no_aed_non_resident() == null ? 0 : BRF002.getR19_no_aed_non_resident().intValue());
	}
	Cell R19cell5 = row19.getCell(9);
	if (R19cell5 != null) {
		R19cell5.setCellValue(BRF002.getR19_amount_aed_non_resident() == null ? 0
				: BRF002.getR19_amount_aed_non_resident().intValue());
	}
	Cell R19cell6 = row19.getCell(10);
	if (R19cell6 != null) {
		R19cell6.setCellValue(
				BRF002.getR19_no_fcy_non_resident() == null ? 0 : BRF002.getR19_no_fcy_non_resident().intValue());
	}
	Cell R19cell7 = row19.getCell(11);
	if (R19cell7 != null) {
		R19cell7.setCellValue(BRF002.getR19_amount_fcy_non_resident() == null ? 0
				: BRF002.getR19_amount_fcy_non_resident().intValue());
	}

	///// srl_no -30/////////
	Row row20 = sheet.getRow(29);
	Cell R20cell = row20.getCell(4);
	if (R20cell != null) {
		R20cell.setCellValue(BRF002.getR20_no_aed_resident() == null ? 0 : BRF002.getR20_no_aed_resident().intValue());
	}
	Cell R20cell1 = row20.getCell(5);
	if (R20cell1 != null) {
		R20cell1.setCellValue(
				BRF002.getR20_amount_aed_resident() == null ? 0 : BRF002.getR20_amount_aed_resident().intValue());
	}
	Cell R20cell2 = row20.getCell(6);
	if (R20cell2 != null) {
		R20cell2.setCellValue(BRF002.getR20_no_fcy_resident() == null ? 0 : BRF002.getR20_no_fcy_resident().intValue());
	}
	Cell R20cell3 = row20.getCell(7);
	if (R20cell3 != null) {
		R20cell3.setCellValue(
				BRF002.getR20_amount_fcy_resident() == null ? 0 : BRF002.getR20_amount_fcy_resident().intValue());
	}
	Cell R20cell4 = row20.getCell(8);
	if (R20cell4 != null) {
		R20cell4.setCellValue(
				BRF002.getR20_no_aed_non_resident() == null ? 0 : BRF002.getR20_no_aed_non_resident().intValue());
	}
	Cell R20cell5 = row20.getCell(9);
	if (R20cell5 != null) {
		R20cell5.setCellValue(BRF002.getR20_amount_aed_non_resident() == null ? 0
				: BRF002.getR20_amount_aed_non_resident().intValue());
	}
	Cell R20cell6 = row20.getCell(10);
	if (R20cell6 != null) {
		R20cell6.setCellValue(
				BRF002.getR20_no_fcy_non_resident() == null ? 0 : BRF002.getR20_no_fcy_non_resident().intValue());
	}
	Cell R20cell7 = row20.getCell(11);
	if (R20cell7 != null) {
		R20cell7.setCellValue(BRF002.getR20_amount_fcy_non_resident() == null ? 0
				: BRF002.getR20_amount_fcy_non_resident().intValue());
	}

	///// srl_no -31/////////
	Row row21 = sheet.getRow(30);
	Cell R21cell = row21.getCell(4);
	if (R21cell != null) {
		R21cell.setCellValue(BRF002.getR21_no_aed_resident() == null ? 0 : BRF002.getR21_no_aed_resident().intValue());
	}
	Cell R21cell1 = row21.getCell(5);
	if (R21cell1 != null) {
		R21cell1.setCellValue(
				BRF002.getR21_amount_aed_resident() == null ? 0 : BRF002.getR21_amount_aed_resident().intValue());
	}
	Cell R21cell2 = row21.getCell(6);
	if (R21cell2 != null) {
		R21cell2.setCellValue(BRF002.getR21_no_fcy_resident() == null ? 0 : BRF002.getR21_no_fcy_resident().intValue());
	}
	Cell R21cell3 = row21.getCell(7);
	if (R21cell3 != null) {
		R21cell3.setCellValue(
				BRF002.getR21_amount_fcy_resident() == null ? 0 : BRF002.getR21_amount_fcy_resident().intValue());
	}
	Cell R21cell4 = row21.getCell(8);
	if (R21cell4 != null) {
		R21cell4.setCellValue(
				BRF002.getR21_no_aed_non_resident() == null ? 0 : BRF002.getR21_no_aed_non_resident().intValue());
	}
	Cell R21cell5 = row21.getCell(9);
	if (R21cell5 != null) {
		R21cell5.setCellValue(BRF002.getR21_amount_aed_non_resident() == null ? 0
				: BRF002.getR21_amount_aed_non_resident().intValue());
	}
	Cell R21cell6 = row21.getCell(10);
	if (R21cell6 != null) {
		R21cell6.setCellValue(
				BRF002.getR21_no_fcy_non_resident() == null ? 0 : BRF002.getR21_no_fcy_non_resident().intValue());
	}
	Cell R21cell7 = row21.getCell(11);
	if (R21cell7 != null) {
		R21cell7.setCellValue(BRF002.getR21_amount_fcy_non_resident() == null ? 0
				: BRF002.getR21_amount_fcy_non_resident().intValue());
	}

	///// srl_no -33/////////
	Row row23 = sheet.getRow(32);
	Cell R23cell = row23.getCell(4);
	if (R23cell != null) {
		R23cell.setCellValue(BRF002.getR23_no_aed_resident() == null ? 0 : BRF002.getR23_no_aed_resident().intValue());
	}
	Cell R23cell1 = row23.getCell(5);
	if (R23cell1 != null) {
		R23cell1.setCellValue(
				BRF002.getR23_amount_aed_resident() == null ? 0 : BRF002.getR23_amount_aed_resident().intValue());
	}
	Cell R23cell2 = row23.getCell(6);
	if (R23cell2 != null) {
		R23cell2.setCellValue(BRF002.getR23_no_fcy_resident() == null ? 0 : BRF002.getR23_no_fcy_resident().intValue());
	}
	Cell R23cell3 = row23.getCell(7);
	if (R23cell3 != null) {
		R23cell3.setCellValue(
				BRF002.getR23_amount_fcy_resident() == null ? 0 : BRF002.getR23_amount_fcy_resident().intValue());
	}
	Cell R23cell4 = row23.getCell(8);
	if (R23cell4 != null) {
		R23cell4.setCellValue(
				BRF002.getR23_no_aed_non_resident() == null ? 0 : BRF002.getR23_no_aed_non_resident().intValue());
	}
	Cell R23cell5 = row23.getCell(9);
	if (R23cell5 != null) {
		R23cell5.setCellValue(BRF002.getR23_amount_aed_non_resident() == null ? 0
				: BRF002.getR23_amount_aed_non_resident().intValue());
	}
	Cell R23cell6 = row23.getCell(10);
	if (R23cell6 != null) {
		R23cell6.setCellValue(
				BRF002.getR23_no_fcy_non_resident() == null ? 0 : BRF002.getR23_no_fcy_non_resident().intValue());
	}
	Cell R23cell7 = row23.getCell(11);
	if (R23cell7 != null) {
		R23cell7.setCellValue(BRF002.getR23_amount_fcy_non_resident() == null ? 0
				: BRF002.getR23_amount_fcy_non_resident().intValue());
	}

	///// srl_no -34/////////
	Row row24 = sheet.getRow(33);
	Cell R24cell = row24.getCell(4);
	if (R24cell != null) {
		R24cell.setCellValue(BRF002.getR24_no_aed_resident() == null ? 0 : BRF002.getR24_no_aed_resident().intValue());
	}
	Cell R24cell1 = row24.getCell(5);
	if (R24cell1 != null) {
		R24cell1.setCellValue(
				BRF002.getR24_amount_aed_resident() == null ? 0 : BRF002.getR24_amount_aed_resident().intValue());
	}
	Cell R24cell2 = row24.getCell(6);
	if (R24cell2 != null) {
		R24cell2.setCellValue(BRF002.getR24_no_fcy_resident() == null ? 0 : BRF002.getR24_no_fcy_resident().intValue());
	}
	Cell R24cell3 = row24.getCell(7);
	if (R24cell3 != null) {
		R24cell3.setCellValue(
				BRF002.getR24_amount_fcy_resident() == null ? 0 : BRF002.getR24_amount_fcy_resident().intValue());
	}
	Cell R24cell4 = row24.getCell(8);
	if (R24cell4 != null) {
		R24cell4.setCellValue(
				BRF002.getR24_no_aed_non_resident() == null ? 0 : BRF002.getR24_no_aed_non_resident().intValue());
	}
	Cell R24cell5 = row24.getCell(9);
	if (R24cell5 != null) {
		R24cell5.setCellValue(BRF002.getR24_amount_aed_non_resident() == null ? 0
				: BRF002.getR24_amount_aed_non_resident().intValue());
	}
	Cell R24cell6 = row24.getCell(10);
	if (R24cell6 != null) {
		R24cell6.setCellValue(
				BRF002.getR24_no_fcy_non_resident() == null ? 0 : BRF002.getR24_no_fcy_non_resident().intValue());
	}
	Cell R24cell7 = row24.getCell(11);
	if (R24cell7 != null) {
		R24cell7.setCellValue(BRF002.getR24_amount_fcy_non_resident() == null ? 0
				: BRF002.getR24_amount_fcy_non_resident().intValue());
	}
	///// srl_no -35/////////
	Row row25 = sheet.getRow(34);
	Cell R25cell = row25.getCell(4);
	if (R25cell != null) {
		R25cell.setCellValue(BRF002.getR25_no_aed_resident() == null ? 0 : BRF002.getR25_no_aed_resident().intValue());
	}
	Cell R25cell1 = row25.getCell(5);
	if (R25cell1 != null) {
		R25cell1.setCellValue(
				BRF002.getR25_amount_aed_resident() == null ? 0 : BRF002.getR25_amount_aed_resident().intValue());
	}
	Cell R25cell2 = row25.getCell(6);
	if (R25cell2 != null) {
		R25cell2.setCellValue(BRF002.getR25_no_fcy_resident() == null ? 0 : BRF002.getR25_no_fcy_resident().intValue());
	}
	Cell R25cell3 = row25.getCell(7);
	if (R25cell3 != null) {
		R25cell3.setCellValue(
				BRF002.getR25_amount_fcy_resident() == null ? 0 : BRF002.getR25_amount_fcy_resident().intValue());
	}
	Cell R25cell4 = row25.getCell(8);
	if (R25cell4 != null) {
		R25cell4.setCellValue(
				BRF002.getR25_no_aed_non_resident() == null ? 0 : BRF002.getR25_no_aed_non_resident().intValue());
	}
	Cell R25cell5 = row25.getCell(9);
	if (R25cell5 != null) {
		R25cell5.setCellValue(BRF002.getR25_amount_aed_non_resident() == null ? 0
				: BRF002.getR25_amount_aed_non_resident().intValue());
	}
	Cell R25cell6 = row25.getCell(10);
	if (R25cell6 != null) {
		R25cell6.setCellValue(
				BRF002.getR25_no_fcy_non_resident() == null ? 0 : BRF002.getR25_no_fcy_non_resident().intValue());
	}
	Cell R25cell7 = row25.getCell(11);
	if (R25cell7 != null) {
		R25cell7.setCellValue(BRF002.getR25_amount_fcy_non_resident() == null ? 0
				: BRF002.getR25_amount_fcy_non_resident().intValue());
	}

	///// srl_no -36/////////
	Row row26 = sheet.getRow(35);
	Cell R26cell = row26.getCell(4);
	if (R26cell != null) {
		R26cell.setCellValue(BRF002.getR26_no_aed_resident() == null ? 0 : BRF002.getR26_no_aed_resident().intValue());
	}
	Cell R26cell1 = row26.getCell(5);
	if (R26cell1 != null) {
		R26cell1.setCellValue(
				BRF002.getR26_amount_aed_resident() == null ? 0 : BRF002.getR26_amount_aed_resident().intValue());
	}
	Cell R26cell2 = row26.getCell(6);
	if (R26cell2 != null) {
		R26cell2.setCellValue(BRF002.getR26_no_fcy_resident() == null ? 0 : BRF002.getR26_no_fcy_resident().intValue());
	}
	Cell R26cell3 = row26.getCell(7);
	if (R26cell3 != null) {
		R26cell3.setCellValue(
				BRF002.getR26_amount_fcy_resident() == null ? 0 : BRF002.getR26_amount_fcy_resident().intValue());
	}
	Cell R26cell4 = row26.getCell(8);
	if (R26cell4 != null) {
		R26cell4.setCellValue(
				BRF002.getR26_no_aed_non_resident() == null ? 0 : BRF002.getR26_no_aed_non_resident().intValue());
	}
	Cell R26cell5 = row26.getCell(9);
	if (R26cell5 != null) {
		R26cell5.setCellValue(BRF002.getR26_amount_aed_non_resident() == null ? 0
				: BRF002.getR26_amount_aed_non_resident().intValue());
	}
	Cell R26cell6 = row26.getCell(10);
	if (R26cell6 != null) {
		R26cell6.setCellValue(
				BRF002.getR26_no_fcy_non_resident() == null ? 0 : BRF002.getR26_no_fcy_non_resident().intValue());
	}
	Cell R26cell7 = row26.getCell(11);
	if (R26cell7 != null) {
		R26cell7.setCellValue(BRF002.getR26_amount_fcy_non_resident() == null ? 0
				: BRF002.getR26_amount_fcy_non_resident().intValue());
	}

	///// srl_no -38/////////
	Row row28 = sheet.getRow(37);
	Cell R28cell = row28.getCell(4);
	if (R28cell != null) {
		R28cell.setCellValue(BRF002.getR28_no_aed_resident() == null ? 0 : BRF002.getR28_no_aed_resident().intValue());
	}
	Cell R28cell1 = row28.getCell(5);
	if (R28cell1 != null) {
		R28cell1.setCellValue(
				BRF002.getR28_amount_aed_resident() == null ? 0 : BRF002.getR28_amount_aed_resident().intValue());
	}
	Cell R28cell2 = row28.getCell(6);
	if (R28cell2 != null) {
		R28cell2.setCellValue(BRF002.getR28_no_fcy_resident() == null ? 0 : BRF002.getR28_no_fcy_resident().intValue());
	}
	Cell R28cell3 = row28.getCell(7);
	if (R28cell3 != null) {
		R28cell3.setCellValue(
				BRF002.getR28_amount_fcy_resident() == null ? 0 : BRF002.getR28_amount_fcy_resident().intValue());
	}
	Cell R28cell4 = row28.getCell(8);
	if (R28cell4 != null) {
		R28cell4.setCellValue(
				BRF002.getR28_no_aed_non_resident() == null ? 0 : BRF002.getR28_no_aed_non_resident().intValue());
	}
	Cell R28cell5 = row28.getCell(9);
	if (R28cell5 != null) {
		R28cell5.setCellValue(BRF002.getR28_amount_aed_non_resident() == null ? 0
				: BRF002.getR28_amount_aed_non_resident().intValue());
	}
	Cell R28cell6 = row28.getCell(10);
	if (R28cell6 != null) {
		R28cell6.setCellValue(
				BRF002.getR28_no_fcy_non_resident() == null ? 0 : BRF002.getR28_no_fcy_non_resident().intValue());
	}
	Cell R28cell7 = row28.getCell(11);
	if (R28cell7 != null) {
		R28cell7.setCellValue(BRF002.getR28_amount_fcy_non_resident() == null ? 0
				: BRF002.getR28_amount_fcy_non_resident().intValue());
	}

	///// srl_no -39/////////
	Row row29 = sheet.getRow(38);
	Cell R29cell1 = row29.getCell(5);
	if (R29cell1 != null) {
		R29cell1.setCellValue(
				BRF002.getR29_amount_aed_resident() == null ? 0 : BRF002.getR29_amount_aed_resident().intValue());
	}

	Cell R29cell3 = row29.getCell(7);
	if (R29cell3 != null) {
		R29cell3.setCellValue(
				BRF002.getR29_amount_fcy_resident() == null ? 0 : BRF002.getR29_amount_fcy_resident().intValue());
	}

	Cell R29cell5 = row29.getCell(9);
	if (R29cell5 != null) {
		R29cell5.setCellValue(BRF002.getR29_amount_aed_non_resident() == null ? 0
				: BRF002.getR29_amount_aed_non_resident().intValue());
	}

	Cell R29cell7 = row29.getCell(11);
	if (R29cell7 != null) {
		R29cell7.setCellValue(BRF002.getR29_amount_fcy_non_resident() == null ? 0
				: BRF002.getR29_amount_fcy_non_resident().intValue());
	}

	///// srl_no -41/////////
	Row row31 = sheet.getRow(40);
	Cell R31cell1 = row31.getCell(5);
	if (R31cell1 != null) {
		R31cell1.setCellValue(
				BRF002.getR31_amount_aed_resident() == null ? 0 : BRF002.getR31_amount_aed_resident().intValue());
	}

	Cell R31cell3 = row31.getCell(7);
	if (R31cell3 != null) {
		R31cell3.setCellValue(
				BRF002.getR31_amount_fcy_resident() == null ? 0 : BRF002.getR31_amount_fcy_resident().intValue());
	}

	Cell R31cell5 = row31.getCell(9);
	if (R31cell5 != null) {
		R31cell5.setCellValue(BRF002.getR31_amount_aed_non_resident() == null ? 0
				: BRF002.getR31_amount_aed_non_resident().intValue());
	}

	Cell R31cell7 = row31.getCell(11);
	if (R31cell7 != null) {
		R31cell7.setCellValue(BRF002.getR31_amount_fcy_non_resident() == null ? 0
				: BRF002.getR31_amount_fcy_non_resident().intValue());
	}

	///// srl_no -42/////////
	Row row32 = sheet.getRow(41);

	Cell R32cell1 = row32.getCell(5);
	if (R32cell1 != null) {
		R32cell1.setCellValue(
				BRF002.getR32_amount_aed_resident() == null ? 0 : BRF002.getR32_amount_aed_resident().intValue());
	}

	Cell R32cell3 = row32.getCell(7);
	if (R32cell3 != null) {
		R32cell3.setCellValue(
				BRF002.getR32_amount_fcy_resident() == null ? 0 : BRF002.getR32_amount_fcy_resident().intValue());
	}

	Cell R32cell5 = row32.getCell(9);
	if (R32cell5 != null) {
		R32cell5.setCellValue(BRF002.getR32_amount_aed_non_resident() == null ? 0
				: BRF002.getR32_amount_aed_non_resident().intValue());
	}

	Cell R32cell7 = row32.getCell(11);
	if (R32cell7 != null) {
		R32cell7.setCellValue(BRF002.getR32_amount_fcy_non_resident() == null ? 0
				: BRF002.getR32_amount_fcy_non_resident().intValue());
	}

	///// srl_no -43/////////
	Row row33 = sheet.getRow(42);

	Cell R33cell1 = row33.getCell(5);
	if (R33cell1 != null) {
		R33cell1.setCellValue(
				BRF002.getR33_amount_aed_resident() == null ? 0 : BRF002.getR33_amount_aed_resident().intValue());
	}

	Cell R33cell3 = row33.getCell(7);
	if (R33cell3 != null) {
		R33cell3.setCellValue(
				BRF002.getR33_amount_fcy_resident() == null ? 0 : BRF002.getR33_amount_fcy_resident().intValue());
	}

	Cell R33cell5 = row33.getCell(9);
	if (R33cell5 != null) {
		R33cell5.setCellValue(BRF002.getR33_amount_aed_non_resident() == null ? 0
				: BRF002.getR33_amount_aed_non_resident().intValue());
	}

	Cell R33cell7 = row33.getCell(11);
	if (R33cell7 != null) {
		R33cell7.setCellValue(BRF002.getR33_amount_fcy_non_resident() == null ? 0
				: BRF002.getR33_amount_fcy_non_resident().intValue());
	}

	///// srl_no -45/////////
	Row row35 = sheet.getRow(44);

	Cell R35cell1 = row35.getCell(5);
	if (R35cell1 != null) {
		R35cell1.setCellValue(
				BRF002.getR35_amount_aed_resident() == null ? 0 : BRF002.getR35_amount_aed_resident().intValue());
	}

	Cell R35cell3 = row35.getCell(7);
	if (R35cell3 != null) {
		R35cell3.setCellValue(
				BRF002.getR35_amount_fcy_resident() == null ? 0 : BRF002.getR35_amount_fcy_resident().intValue());
	}

	Cell R35cell5 = row35.getCell(9);
	if (R35cell5 != null) {
		R35cell5.setCellValue(BRF002.getR35_amount_aed_non_resident() == null ? 0
				: BRF002.getR35_amount_aed_non_resident().intValue());
	}

	Cell R35cell7 = row35.getCell(11);
	if (R35cell7 != null) {
		R35cell7.setCellValue(BRF002.getR35_amount_fcy_non_resident() == null ? 0
				: BRF002.getR35_amount_fcy_non_resident().intValue());
	}

	///// srl_no -46/////////
	Row row36 = sheet.getRow(45);

	Cell R36cell1 = row36.getCell(5);
	if (R36cell1 != null) {
		R36cell1.setCellValue(
				BRF002.getR36_amount_aed_resident() == null ? 0 : BRF002.getR36_amount_aed_resident().intValue());
	}

	Cell R36cell3 = row36.getCell(7);
	if (R36cell3 != null) {
		R36cell3.setCellValue(
				BRF002.getR36_amount_fcy_resident() == null ? 0 : BRF002.getR36_amount_fcy_resident().intValue());
	}

	Cell R36cell5 = row36.getCell(9);
	if (R36cell5 != null) {
		R36cell5.setCellValue(BRF002.getR36_amount_aed_non_resident() == null ? 0
				: BRF002.getR36_amount_aed_non_resident().intValue());
	}

	Cell R36cell7 = row36.getCell(11);
	if (R36cell7 != null) {
		R36cell7.setCellValue(BRF002.getR36_amount_fcy_non_resident() == null ? 0
				: BRF002.getR36_amount_fcy_non_resident().intValue());
	}

	///// srl_no -48/////////
	Row row38 = sheet.getRow(47);

	Cell R38cell1 = row38.getCell(5);
	if (R38cell1 != null) {
		R38cell1.setCellValue(
				BRF002.getR38_amount_aed_resident() == null ? 0 : BRF002.getR38_amount_aed_resident().intValue());
	}

	Cell R38cell3 = row38.getCell(7);
	if (R38cell3 != null) {
		R38cell3.setCellValue(
				BRF002.getR38_amount_fcy_resident() == null ? 0 : BRF002.getR38_amount_fcy_resident().intValue());
	}

	Cell R38cell5 = row38.getCell(9);
	if (R38cell5 != null) {
		R38cell5.setCellValue(BRF002.getR38_amount_aed_non_resident() == null ? 0
				: BRF002.getR38_amount_aed_non_resident().intValue());
	}

	Cell R38cell7 = row38.getCell(11);
	if (R38cell7 != null) {
		R38cell7.setCellValue(BRF002.getR38_amount_fcy_non_resident() == null ? 0
				: BRF002.getR38_amount_fcy_non_resident().intValue());
	}
	///// srl_no -49/////////
	Row row39 = sheet.getRow(48);

	Cell R39cell1 = row39.getCell(5);
	if (R39cell1 != null) {
		R39cell1.setCellValue(
				BRF002.getR39_amount_aed_resident() == null ? 0 : BRF002.getR39_amount_aed_resident().intValue());
	}

	Cell R39cell3 = row39.getCell(7);
	if (R39cell3 != null) {
		R39cell3.setCellValue(
				BRF002.getR39_amount_fcy_resident() == null ? 0 : BRF002.getR39_amount_fcy_resident().intValue());
	}

	Cell R39cell5 = row39.getCell(9);
	if (R39cell5 != null) {
		R39cell5.setCellValue(BRF002.getR39_amount_aed_non_resident() == null ? 0
				: BRF002.getR39_amount_aed_non_resident().intValue());
	}

	Cell R39cell7 = row39.getCell(11);
	if (R39cell7 != null) {
		R39cell7.setCellValue(BRF002.getR39_amount_fcy_non_resident() == null ? 0
				: BRF002.getR39_amount_fcy_non_resident().intValue());
	}

	///// srl_no -50/////////
	Row row40 = sheet.getRow(49);

	Cell R40cell1 = row40.getCell(5);
	if (R40cell1 != null) {
		R40cell1.setCellValue(
				BRF002.getR40_amount_aed_resident() == null ? 0 : BRF002.getR40_amount_aed_resident().intValue());
	}

	Cell R40cell3 = row40.getCell(7);
	if (R40cell3 != null) {
		R40cell3.setCellValue(
				BRF002.getR40_amount_fcy_resident() == null ? 0 : BRF002.getR40_amount_fcy_resident().intValue());
	}

	Cell R40cell5 = row40.getCell(9);
	if (R40cell5 != null) {
		R40cell5.setCellValue(BRF002.getR40_amount_aed_non_resident() == null ? 0
				: BRF002.getR40_amount_aed_non_resident().intValue());
	}

	Cell R40cell7 = row40.getCell(11);
	if (R40cell7 != null) {
		R40cell7.setCellValue(BRF002.getR40_amount_fcy_non_resident() == null ? 0
				: BRF002.getR40_amount_fcy_non_resident().intValue());
	}

	///// srl_no -51/////////
	Row row41 = sheet.getRow(50);

	Cell R41cell1 = row41.getCell(5);
	if (R41cell1 != null) {
		R41cell1.setCellValue(
				BRF002.getR41_amount_aed_resident() == null ? 0 : BRF002.getR41_amount_aed_resident().intValue());
	}

	Cell R41cell3 = row41.getCell(7);
	if (R41cell3 != null) {
		R41cell3.setCellValue(
				BRF002.getR41_amount_fcy_resident() == null ? 0 : BRF002.getR41_amount_fcy_resident().intValue());
	}

	Cell R41cell5 = row41.getCell(9);
	if (R41cell5 != null) {
		R41cell5.setCellValue(BRF002.getR41_amount_aed_non_resident() == null ? 0
				: BRF002.getR41_amount_aed_non_resident().intValue());
	}

	Cell R41cell7 = row41.getCell(11);
	if (R41cell7 != null) {
		R41cell7.setCellValue(BRF002.getR41_amount_fcy_non_resident() == null ? 0
				: BRF002.getR41_amount_fcy_non_resident().intValue());
	}
	////ROW43
	Row row43 = sheet.getRow(52);
	Cell R43cell1 = row43.getCell(5);
	if (R43cell1 != null) {
		R43cell1.setCellValue(
				BRF002.getR43_amount_aed_resident() == null ? 0 : BRF002.getR43_amount_aed_resident().intValue());
	}

	Cell R43cell3 = row43.getCell(7);
	if (R43cell3 != null) {
		R43cell3.setCellValue(
				BRF002.getR43_amount_fcy_resident() == null ? 0 : BRF002.getR43_amount_fcy_resident().intValue());
	}

	Cell R43cell5 = row43.getCell(9);
	if (R43cell5 != null) {
		R43cell5.setCellValue(BRF002.getR43_amount_aed_non_resident() == null ? 0
				: BRF002.getR43_amount_aed_non_resident().intValue());
	}

	Cell R43cell7 = row43.getCell(11);
	if (R43cell7 != null) {
		R43cell7.setCellValue(BRF002.getR43_amount_fcy_non_resident() == null ? 0
				: BRF002.getR43_amount_fcy_non_resident().intValue());
	}
	///// srl_no -54/////////
	Row row44 = sheet.getRow(53);

	Cell R44cell1 = row44.getCell(5);
	if (R44cell1 != null) {
		R44cell1.setCellValue(
				BRF002.getR44_amount_aed_resident() == null ? 0 : BRF002.getR44_amount_aed_resident().intValue());
	}

	Cell R44cell3 = row44.getCell(7);
	if (R44cell3 != null) {
		R44cell3.setCellValue(
				BRF002.getR44_amount_fcy_resident() == null ? 0 : BRF002.getR44_amount_fcy_resident().intValue());
	}

	Cell R44cell5 = row44.getCell(9);
	if (R44cell5 != null) {
		R44cell5.setCellValue(BRF002.getR44_amount_aed_non_resident() == null ? 0
				: BRF002.getR44_amount_aed_non_resident().intValue());
	}

	Cell R44cell7 = row44.getCell(11);
	if (R44cell7 != null) {
		R44cell7.setCellValue(BRF002.getR44_amount_fcy_non_resident() == null ? 0
				: BRF002.getR44_amount_fcy_non_resident().intValue());
	}

	//// srl_no -55/////////
	Row row45 = sheet.getRow(54);

	Cell R45cell1 = row45.getCell(5);
	if (R45cell1 != null) {
		R45cell1.setCellValue(
				BRF002.getR45_amount_aed_resident() == null ? 0 : BRF002.getR45_amount_aed_resident().intValue());
	}

	Cell R45cell3 = row45.getCell(7);
	if (R45cell3 != null) {
		R45cell3.setCellValue(
				BRF002.getR45_amount_fcy_resident() == null ? 0 : BRF002.getR45_amount_fcy_resident().intValue());
	}

	Cell R45cell5 = row45.getCell(9);
	if (R45cell5 != null) {
		R45cell5.setCellValue(BRF002.getR45_amount_aed_non_resident() == null ? 0
				: BRF002.getR45_amount_aed_non_resident().intValue());
	}

	Cell R45cell7 = row45.getCell(11);
	if (R45cell7 != null) {
		R45cell7.setCellValue(BRF002.getR45_amount_fcy_non_resident() == null ? 0
				: BRF002.getR45_amount_fcy_non_resident().intValue());
	}

	///// srl_no -57/////////
	Row row47 = sheet.getRow(56);
	Cell R47cell1 = row47.getCell(5);
	if (R47cell1 != null) {
		R47cell1.setCellValue(
				BRF002.getR47_amount_aed_resident() == null ? 0 : BRF002.getR47_amount_aed_resident().intValue());
	}

	Cell R47cell3 = row47.getCell(7);
	if (R47cell3 != null) {
		R47cell3.setCellValue(
				BRF002.getR47_amount_fcy_resident() == null ? 0 : BRF002.getR47_amount_fcy_resident().intValue());
	}

	Cell R47cell5 = row47.getCell(9);
	if (R47cell5 != null) {
		R47cell5.setCellValue(BRF002.getR47_amount_aed_non_resident() == null ? 0
				: BRF002.getR47_amount_aed_non_resident().intValue());
	}

	Cell R47cell7 = row47.getCell(11);
	if (R47cell7 != null) {
		R47cell7.setCellValue(BRF002.getR47_amount_fcy_non_resident() == null ? 0
				: BRF002.getR47_amount_fcy_non_resident().intValue());
	}

	///// srl_no -59/////////
	Row row49 = sheet.getRow(58);

	Cell R49cell1 = row49.getCell(5);
	if (R49cell1 != null) {
		R49cell1.setCellValue(
				BRF002.getR49_amount_aed_resident() == null ? 0 : BRF002.getR49_amount_aed_resident().intValue());
	}

	Cell R49cell3 = row49.getCell(7);
	if (R49cell3 != null) {
		R49cell3.setCellValue(
				BRF002.getR49_amount_fcy_resident() == null ? 0 : BRF002.getR49_amount_fcy_resident().intValue());
	}

	Cell R49cell5 = row49.getCell(9);
	if (R49cell5 != null) {
		R49cell5.setCellValue(BRF002.getR49_amount_aed_non_resident() == null ? 0
				: BRF002.getR49_amount_aed_non_resident().intValue());
	}

	Cell R49cell7 = row49.getCell(11);
	if (R49cell7 != null) {
		R49cell7.setCellValue(BRF002.getR49_amount_fcy_non_resident() == null ? 0
				: BRF002.getR49_amount_fcy_non_resident().intValue());
	}

	///// srl_no -60/////////
	Row row50 = sheet.getRow(59);

	Cell R50cell1 = row50.getCell(5);
	if (R50cell1 != null) {
		R50cell1.setCellValue(
				BRF002.getR50_amount_aed_resident() == null ? 0 : BRF002.getR50_amount_aed_resident().intValue());
	}

	Cell R50cell3 = row50.getCell(7);
	if (R50cell3 != null) {
		R50cell3.setCellValue(
				BRF002.getR50_amount_fcy_resident() == null ? 0 : BRF002.getR50_amount_fcy_resident().intValue());
	}

	Cell R50cell5 = row50.getCell(9);
	if (R50cell5 != null) {
		R50cell5.setCellValue(BRF002.getR50_amount_aed_non_resident() == null ? 0
				: BRF002.getR50_amount_aed_non_resident().intValue());
	}

	Cell R50cell7 = row50.getCell(11);
	if (R50cell7 != null) {
		R50cell7.setCellValue(BRF002.getR50_amount_fcy_non_resident() == null ? 0
				: BRF002.getR50_amount_fcy_non_resident().intValue());
	}

	///// srl_no -61/////////
	Row row51 = sheet.getRow(60);
	Cell R51cell1 = row51.getCell(5);
	if (R51cell1 != null) {
		R51cell1.setCellValue(
				BRF002.getR51_amount_aed_resident() == null ? 0 : BRF002.getR51_amount_aed_resident().intValue());
	}

	Cell R51cell3 = row51.getCell(7);
	if (R51cell3 != null) {
		R51cell3.setCellValue(
				BRF002.getR51_amount_fcy_resident() == null ? 0 : BRF002.getR51_amount_fcy_resident().intValue());
	}

	Cell R51cell5 = row51.getCell(9);
	if (R51cell5 != null) {
		R51cell5.setCellValue(BRF002.getR51_amount_aed_non_resident() == null ? 0
				: BRF002.getR51_amount_aed_non_resident().intValue());
	}

	Cell R51cell7 = row51.getCell(11);
	if (R51cell7 != null) {
		R51cell7.setCellValue(BRF002.getR51_amount_fcy_non_resident() == null ? 0
				: BRF002.getR51_amount_fcy_non_resident().intValue());
	}

	///// srl_no -62/////////
	Row row52 = sheet.getRow(61);

	Cell R52cell1 = row52.getCell(5);
	if (R52cell1 != null) {
		R52cell1.setCellValue(
				BRF002.getR52_amount_aed_resident() == null ? 0 : BRF002.getR52_amount_aed_resident().intValue());
	}

	Cell R52cell3 = row52.getCell(7);
	if (R52cell3 != null) {
		R52cell3.setCellValue(
				BRF002.getR52_amount_fcy_resident() == null ? 0 : BRF002.getR52_amount_fcy_resident().intValue());
	}

	Cell R52cell5 = row52.getCell(9);
	if (R52cell5 != null) {
		R52cell5.setCellValue(BRF002.getR52_amount_aed_non_resident() == null ? 0
				: BRF002.getR52_amount_aed_non_resident().intValue());
	}

	Cell R52cell7 = row52.getCell(11);
	if (R52cell7 != null) {
		R52cell7.setCellValue(BRF002.getR52_amount_fcy_non_resident() == null ? 0
				: BRF002.getR52_amount_fcy_non_resident().intValue());
	}

	///// srl_no -63/////////
	Row row53 = sheet.getRow(62);

	Cell R53cell1 = row53.getCell(5);
	if (R53cell1 != null) {
		R53cell1.setCellValue(
				BRF002.getR53_amount_aed_resident() == null ? 0 : BRF002.getR53_amount_aed_resident().intValue());
	}

	Cell R53cell3 = row53.getCell(7);
	if (R53cell3 != null) {
		R53cell3.setCellValue(
				BRF002.getR53_amount_fcy_resident() == null ? 0 : BRF002.getR53_amount_fcy_resident().intValue());
	}

	Cell R53cell5 = row53.getCell(9);
	if (R53cell5 != null) {
		R53cell5.setCellValue(BRF002.getR53_amount_aed_non_resident() == null ? 0
				: BRF002.getR53_amount_aed_non_resident().intValue());
	}

	Cell R53cell7 = row53.getCell(11);
	if (R53cell7 != null) {
		R53cell7.setCellValue(BRF002.getR53_amount_fcy_non_resident() == null ? 0
				: BRF002.getR53_amount_fcy_non_resident().intValue());
	}

	///// srl_no -64/////////
	Row row54 = sheet.getRow(63);

	Cell R54cell1 = row54.getCell(5);
	if (R54cell1 != null) {
		R54cell1.setCellValue(
				BRF002.getR54_amount_aed_resident() == null ? 0 : BRF002.getR54_amount_aed_resident().intValue());
	}

	Cell R54cell3 = row54.getCell(7);
	if (R54cell3 != null) {
		R54cell3.setCellValue(
				BRF002.getR54_amount_fcy_resident() == null ? 0 : BRF002.getR54_amount_fcy_resident().intValue());
	}

	Cell R54cell5 = row54.getCell(9);
	if (R54cell5 != null) {
		R54cell5.setCellValue(BRF002.getR54_amount_aed_non_resident() == null ? 0
				: BRF002.getR54_amount_aed_non_resident().intValue());
	}

	Cell R54cell7 = row54.getCell(11);
	if (R54cell7 != null) {
		R54cell7.setCellValue(BRF002.getR54_amount_fcy_non_resident() == null ? 0
				: BRF002.getR54_amount_fcy_non_resident().intValue());
	}

	///// srl_no -65/////////
	Row row55 = sheet.getRow(64);

	Cell R55cell1 = row55.getCell(5);
	if (R55cell1 != null) {
		R55cell1.setCellValue(
				BRF002.getR55_amount_aed_resident() == null ? 0 : BRF002.getR55_amount_aed_resident().intValue());
	}

	Cell R55cell3 = row55.getCell(7);
	if (R55cell3 != null) {
		R55cell3.setCellValue(
				BRF002.getR55_amount_fcy_resident() == null ? 0 : BRF002.getR55_amount_fcy_resident().intValue());
	}

	Cell R55cell5 = row55.getCell(9);
	if (R55cell5 != null) {
		R55cell5.setCellValue(BRF002.getR55_amount_aed_non_resident() == null ? 0
				: BRF002.getR55_amount_aed_non_resident().intValue());
	}

	Cell R55cell7 = row55.getCell(11);
	if (R55cell7 != null) {
		R55cell7.setCellValue(BRF002.getR55_amount_fcy_non_resident() == null ? 0
				: BRF002.getR55_amount_fcy_non_resident().intValue());
	}

	///// srl_no -67/////////
	Row row57 = sheet.getRow(66);

	Cell R57cell1 = row57.getCell(5);
	if (R57cell1 != null) {
		R57cell1.setCellValue(
				BRF002.getR57_amount_aed_resident() == null ? 0 : BRF002.getR57_amount_aed_resident().intValue());
	}

	Cell R57cell3 = row57.getCell(7);
	if (R57cell3 != null) {
		R57cell3.setCellValue(
				BRF002.getR57_amount_fcy_resident() == null ? 0 : BRF002.getR57_amount_fcy_resident().intValue());
	}

	Cell R57cell5 = row57.getCell(9);
	if (R57cell5 != null) {
		R57cell5.setCellValue(BRF002.getR57_amount_aed_non_resident() == null ? 0
				: BRF002.getR57_amount_aed_non_resident().intValue());
	}

	Cell R57cell7 = row57.getCell(11);
	if (R57cell7 != null) {
		R57cell7.setCellValue(BRF002.getR57_amount_fcy_non_resident() == null ? 0
				: BRF002.getR57_amount_fcy_non_resident().intValue());
	}

	///// srl_no -68/////////
	Row row58 = sheet.getRow(67);

	Cell R58cell1 = row58.getCell(5);
	if (R58cell1 != null) {
		R58cell1.setCellValue(
				BRF002.getR58_amount_aed_resident() == null ? 0 : BRF002.getR58_amount_aed_resident().intValue());
	}

	Cell R58cell3 = row58.getCell(7);
	if (R58cell3 != null) {
		R58cell3.setCellValue(
				BRF002.getR58_amount_fcy_resident() == null ? 0 : BRF002.getR58_amount_fcy_resident().intValue());
	}

	Cell R58cell5 = row58.getCell(9);
	if (R58cell5 != null) {
		R58cell5.setCellValue(BRF002.getR58_amount_aed_non_resident() == null ? 0
				: BRF002.getR58_amount_aed_non_resident().intValue());
	}

	Cell R58cell7 = row58.getCell(11);
	if (R58cell7 != null) {
		R58cell7.setCellValue(BRF002.getR58_amount_fcy_non_resident() == null ? 0
				: BRF002.getR58_amount_fcy_non_resident().intValue());
	}

	///// srl_no -69/////////
	Row row59 = sheet.getRow(68);

	Cell R59cell1 = row59.getCell(5);
	if (R59cell1 != null) {
		R59cell1.setCellValue(
				BRF002.getR59_amount_aed_resident() == null ? 0 : BRF002.getR59_amount_aed_resident().intValue());
	}

	Cell R59cell3 = row59.getCell(7);
	if (R59cell3 != null) {
		R59cell3.setCellValue(
				BRF002.getR59_amount_fcy_resident() == null ? 0 : BRF002.getR59_amount_fcy_resident().intValue());
	}

	Cell R59cell5 = row59.getCell(9);
	if (R59cell5 != null) {
		R59cell5.setCellValue(BRF002.getR59_amount_aed_non_resident() == null ? 0
				: BRF002.getR59_amount_aed_non_resident().intValue());
	}

	Cell R59cell7 = row59.getCell(11);
	if (R59cell7 != null) {
		R59cell7.setCellValue(BRF002.getR59_amount_fcy_non_resident() == null ? 0
				: BRF002.getR59_amount_fcy_non_resident().intValue());
	}

	///// srl_no -70/////////
	Row row60 = sheet.getRow(69);

	Cell R60cell1 = row60.getCell(5);
	if (R60cell1 != null) {
		R60cell1.setCellValue(
				BRF002.getR60_amount_aed_resident() == null ? 0 : BRF002.getR60_amount_aed_resident().intValue());
	}

	Cell R60cell3 = row60.getCell(7);
	if (R60cell3 != null) {
		R60cell3.setCellValue(
				BRF002.getR60_amount_fcy_resident() == null ? 0 : BRF002.getR60_amount_fcy_resident().intValue());
	}

	Cell R60cell5 = row60.getCell(9);
	if (R60cell5 != null) {
		R60cell5.setCellValue(BRF002.getR60_amount_aed_non_resident() == null ? 0
				: BRF002.getR60_amount_aed_non_resident().intValue());
	}

	Cell R60cell7 = row60.getCell(11);
	if (R60cell7 != null) {
		R60cell7.setCellValue(BRF002.getR60_amount_fcy_non_resident() == null ? 0
				: BRF002.getR60_amount_fcy_non_resident().intValue());
	}

	///// srl_no -71/////////
	Row row61 = sheet.getRow(70);

	Cell R61cell1 = row61.getCell(5);
	if (R61cell1 != null) {
		R61cell1.setCellValue(
				BRF002.getR61_amount_aed_resident() == null ? 0 : BRF002.getR61_amount_aed_resident().intValue());
	}

	Cell R61cell3 = row61.getCell(7);
	if (R61cell3 != null) {
		R61cell3.setCellValue(
				BRF002.getR61_amount_fcy_resident() == null ? 0 : BRF002.getR61_amount_fcy_resident().intValue());
	}

	Cell R61cell5 = row61.getCell(9);
	if (R61cell5 != null) {
		R61cell5.setCellValue(BRF002.getR61_amount_aed_non_resident() == null ? 0
				: BRF002.getR61_amount_aed_non_resident().intValue());
	}

	Cell R61cell7 = row61.getCell(11);
	if (R61cell7 != null) {
		R61cell7.setCellValue(BRF002.getR61_amount_fcy_non_resident() == null ? 0
				: BRF002.getR61_amount_fcy_non_resident().intValue());
	}

	///// srl_no -72/////////
	Row row62 = sheet.getRow(71);
	Cell R62cell1 = row62.getCell(5);
	if (R62cell1 != null) {
		R62cell1.setCellValue(
				BRF002.getR62_amount_aed_resident() == null ? 0 : BRF002.getR62_amount_aed_resident().intValue());
	}

	Cell R62cell3 = row62.getCell(7);
	if (R62cell3 != null) {
		R62cell3.setCellValue(
				BRF002.getR62_amount_fcy_resident() == null ? 0 : BRF002.getR62_amount_fcy_resident().intValue());
	}

	Cell R62cell5 = row62.getCell(9);
	if (R62cell5 != null) {
		R62cell5.setCellValue(BRF002.getR62_amount_aed_non_resident() == null ? 0
				: BRF002.getR62_amount_aed_non_resident().intValue());
	}

	Cell R62cell7 = row62.getCell(11);
	if (R62cell7 != null) {
		R62cell7.setCellValue(BRF002.getR62_amount_fcy_non_resident() == null ? 0
				: BRF002.getR62_amount_fcy_non_resident().intValue());
	}

	///// srl_no -73/////////
	Row row63 = sheet.getRow(72);

	Cell R63cell1 = row63.getCell(5);
	if (R63cell1 != null) {
		R63cell1.setCellValue(
				BRF002.getR63_amount_aed_resident() == null ? 0 : BRF002.getR63_amount_aed_resident().intValue());
	}

	Cell R63cell3 = row63.getCell(7);
	if (R63cell3 != null) {
		R63cell3.setCellValue(
				BRF002.getR63_amount_fcy_resident() == null ? 0 : BRF002.getR63_amount_fcy_resident().intValue());
	}

	Cell R63cell5 = row63.getCell(9);
	if (R63cell5 != null) {
		R63cell5.setCellValue(BRF002.getR63_amount_aed_non_resident() == null ? 0
				: BRF002.getR63_amount_aed_non_resident().intValue());
	}

	Cell R63cell7 = row63.getCell(11);
	if (R63cell7 != null) {
		R63cell7.setCellValue(BRF002.getR63_amount_fcy_non_resident() == null ? 0
				: BRF002.getR63_amount_fcy_non_resident().intValue());
	}

	///// srl_no -74/////////
	Row row64 = sheet.getRow(73);

	Cell R64cell1 = row64.getCell(5);
	if (R64cell1 != null) {
		R64cell1.setCellValue(
				BRF002.getR64_amount_aed_resident() == null ? 0 : BRF002.getR64_amount_aed_resident().intValue());
	}

	Cell R64cell3 = row64.getCell(7);
	if (R64cell3 != null) {
		R64cell3.setCellValue(
				BRF002.getR64_amount_fcy_resident() == null ? 0 : BRF002.getR64_amount_fcy_resident().intValue());
	}

	Cell R64cell5 = row64.getCell(9);
	if (R64cell5 != null) {
		R64cell5.setCellValue(BRF002.getR64_amount_aed_non_resident() == null ? 0
				: BRF002.getR64_amount_aed_non_resident().intValue());
	}

	Cell R64cell7 = row64.getCell(11);
	if (R64cell7 != null) {
		R64cell7.setCellValue(BRF002.getR64_amount_fcy_non_resident() == null ? 0
				: BRF002.getR64_amount_fcy_non_resident().intValue());
	}

	///// srl_no -75/////////
	Row row65 = sheet.getRow(74);

	Cell R65cell1 = row65.getCell(5);
	if (R65cell1 != null) {
		R65cell1.setCellValue(
				BRF002.getR65_amount_aed_resident() == null ? 0 : BRF002.getR65_amount_aed_resident().intValue());
	}

	Cell R65cell3 = row65.getCell(7);
	if (R65cell3 != null) {
		R65cell3.setCellValue(
				BRF002.getR65_amount_fcy_resident() == null ? 0 : BRF002.getR65_amount_fcy_resident().intValue());
	}

	Cell R65cell5 = row65.getCell(9);
	if (R65cell5 != null) {
		R65cell5.setCellValue(BRF002.getR65_amount_aed_non_resident() == null ? 0
				: BRF002.getR65_amount_aed_non_resident().intValue());
	}

	Cell R65cell7 = row65.getCell(11);
	if (R65cell7 != null) {
		R65cell7.setCellValue(BRF002.getR65_amount_fcy_non_resident() == null ? 0
				: BRF002.getR65_amount_fcy_non_resident().intValue());
	}

	///// srl_no -77/////////
	Row row67 = sheet.getRow(76);

	Cell R67cell1 = row67.getCell(5);
	if (R67cell1 != null) {
		R67cell1.setCellValue(
				BRF002.getR67_amount_aed_resident() == null ? 0 : BRF002.getR67_amount_aed_resident().intValue());
	}

	Cell R67cell3 = row67.getCell(7);
	if (R67cell3 != null) {
		R67cell3.setCellValue(
				BRF002.getR67_amount_fcy_resident() == null ? 0 : BRF002.getR67_amount_fcy_resident().intValue());
	}

	Cell R67cell5 = row67.getCell(9);
	if (R67cell5 != null) {
		R67cell5.setCellValue(BRF002.getR67_amount_aed_non_resident() == null ? 0
				: BRF002.getR67_amount_aed_non_resident().intValue());
	}

	Cell R67cell7 = row67.getCell(11);
	if (R67cell7 != null) {
		R67cell7.setCellValue(BRF002.getR67_amount_fcy_non_resident() == null ? 0
				: BRF002.getR67_amount_fcy_non_resident().intValue());
	}

	///// srl_no -78/////////
	Row row68 = sheet.getRow(77);

	Cell R68cell1 = row68.getCell(5);
	if (R68cell1 != null) {
		R68cell1.setCellValue(
				BRF002.getR68_amount_aed_resident() == null ? 0 : BRF002.getR68_amount_aed_resident().intValue());
	}

	Cell R68cell3 = row68.getCell(7);
	if (R68cell3 != null) {
		R68cell3.setCellValue(
				BRF002.getR68_amount_fcy_resident() == null ? 0 : BRF002.getR68_amount_fcy_resident().intValue());
	}

	Cell R68cell5 = row68.getCell(9);
	if (R68cell5 != null) {
		R68cell5.setCellValue(BRF002.getR68_amount_aed_non_resident() == null ? 0
				: BRF002.getR68_amount_aed_non_resident().intValue());
	}

	Cell R68cell7 = row68.getCell(11);
	if (R68cell7 != null) {
		R68cell7.setCellValue(BRF002.getR68_amount_fcy_non_resident() == null ? 0
				: BRF002.getR68_amount_fcy_non_resident().intValue());
	}

	///// srl_no -79/////////
	Row row69 = sheet.getRow(78);

	Cell R69cell1 = row69.getCell(5);
	if (R69cell1 != null) {
		R69cell1.setCellValue(
				BRF002.getR69_amount_aed_resident() == null ? 0 : BRF002.getR69_amount_aed_resident().intValue());
	}

	Cell R69cell3 = row69.getCell(7);
	if (R69cell3 != null) {
		R69cell3.setCellValue(
				BRF002.getR69_amount_fcy_resident() == null ? 0 : BRF002.getR69_amount_fcy_resident().intValue());
	}

	Cell R69cell5 = row69.getCell(9);
	if (R69cell5 != null) {
		R69cell5.setCellValue(BRF002.getR69_amount_aed_non_resident() == null ? 0
				: BRF002.getR69_amount_aed_non_resident().intValue());
	}

	Cell R69cell7 = row69.getCell(11);
	if (R69cell7 != null) {
		R69cell7.setCellValue(BRF002.getR69_amount_fcy_non_resident() == null ? 0
				: BRF002.getR69_amount_fcy_non_resident().intValue());
	}

	///// srl_no -80/////////
	Row row70 = sheet.getRow(79);

	Cell R70cell1 = row70.getCell(5);
	if (R70cell1 != null) {
		R70cell1.setCellValue(
				BRF002.getR70_amount_aed_resident() == null ? 0 : BRF002.getR70_amount_aed_resident().intValue());
	}

	Cell R70cell3 = row70.getCell(7);
	if (R70cell3 != null) {
		R70cell3.setCellValue(
				BRF002.getR70_amount_fcy_resident() == null ? 0 : BRF002.getR70_amount_fcy_resident().intValue());
	}

	Cell R70cell5 = row70.getCell(9);
	if (R70cell5 != null) {
		R70cell5.setCellValue(BRF002.getR70_amount_aed_non_resident() == null ? 0
				: BRF002.getR70_amount_aed_non_resident().intValue());
	}

	Cell R70cell7 = row70.getCell(11);
	if (R70cell7 != null) {
		R70cell7.setCellValue(BRF002.getR70_amount_fcy_non_resident() == null ? 0
				: BRF002.getR70_amount_fcy_non_resident().intValue());
	}

	///// srl_no -81/////////
	Row row71 = sheet.getRow(80);

	Cell R71cell1 = row71.getCell(5);
	if (R71cell1 != null) {
		R71cell1.setCellValue(
				BRF002.getR71_amount_aed_resident() == null ? 0 : BRF002.getR71_amount_aed_resident().intValue());
	}

	Cell R71cell3 = row71.getCell(7);
	if (R71cell3 != null) {
		R71cell3.setCellValue(
				BRF002.getR71_amount_fcy_resident() == null ? 0 : BRF002.getR71_amount_fcy_resident().intValue());
	}

	Cell R71cell5 = row71.getCell(9);
	if (R71cell5 != null) {
		R71cell5.setCellValue(BRF002.getR71_amount_aed_non_resident() == null ? 0
				: BRF002.getR71_amount_aed_non_resident().intValue());
	}

	Cell R71cell7 = row71.getCell(11);
	if (R71cell7 != null) {
		R71cell7.setCellValue(BRF002.getR71_amount_fcy_non_resident() == null ? 0
				: BRF002.getR71_amount_fcy_non_resident().intValue());
	}

	///// srl_no -82/////////
	Row row72 = sheet.getRow(81);

	Cell R72cell1 = row72.getCell(5);
	if (R72cell1 != null) {
		R72cell1.setCellValue(
				BRF002.getR72_amount_aed_resident() == null ? 0 : BRF002.getR72_amount_aed_resident().intValue());
	}

	Cell R72cell3 = row72.getCell(7);
	if (R72cell3 != null) {
		R72cell3.setCellValue(
				BRF002.getR72_amount_fcy_resident() == null ? 0 : BRF002.getR72_amount_fcy_resident().intValue());
	}

	Cell R72cell5 = row72.getCell(9);
	if (R72cell5 != null) {
		R72cell5.setCellValue(BRF002.getR72_amount_aed_non_resident() == null ? 0
				: BRF002.getR72_amount_aed_non_resident().intValue());
	}

	Cell R72cell7 = row72.getCell(11);
	if (R72cell7 != null) {
		R72cell7.setCellValue(BRF002.getR72_amount_fcy_non_resident() == null ? 0
				: BRF002.getR72_amount_fcy_non_resident().intValue());
	}

	///// srl_no -83/////////
	Row row73 = sheet.getRow(82);

	Cell R73cell1 = row73.getCell(5);
	if (R73cell1 != null) {
		R73cell1.setCellValue(
				BRF002.getR73_amount_aed_resident() == null ? 0 : BRF002.getR73_amount_aed_resident().intValue());
	}

	Cell R73cell3 = row73.getCell(7);
	if (R73cell3 != null) {
		R73cell3.setCellValue(
				BRF002.getR73_amount_fcy_resident() == null ? 0 : BRF002.getR73_amount_fcy_resident().intValue());
	}

	Cell R73cell5 = row73.getCell(9);
	if (R73cell5 != null) {
		R73cell5.setCellValue(BRF002.getR73_amount_aed_non_resident() == null ? 0
				: BRF002.getR73_amount_aed_non_resident().intValue());
	}

	Cell R73cell7 = row73.getCell(11);
	if (R73cell7 != null) {
		R73cell7.setCellValue(BRF002.getR73_amount_fcy_non_resident() == null ? 0
				: BRF002.getR73_amount_fcy_non_resident().intValue());
	}

	///// srl_no -86/////////
	Row row76 = sheet.getRow(85);

	Cell R76cell1 = row76.getCell(5);
	if (R76cell1 != null) {
		R76cell1.setCellValue(
				BRF002.getR76_amount_aed_resident() == null ? 0 : BRF002.getR76_amount_aed_resident().intValue());
	}

	Cell R76cell3 = row76.getCell(7);
	if (R76cell3 != null) {
		R76cell3.setCellValue(
				BRF002.getR76_amount_fcy_resident() == null ? 0 : BRF002.getR76_amount_fcy_resident().intValue());
	}

	Cell R76cell5 = row76.getCell(9);
	if (R76cell5 != null) {
		R76cell5.setCellValue(BRF002.getR76_amount_aed_non_resident() == null ? 0
				: BRF002.getR76_amount_aed_non_resident().intValue());
	}

	Cell R76cell7 = row76.getCell(11);
	if (R76cell7 != null) {
		R76cell7.setCellValue(BRF002.getR76_amount_fcy_non_resident() == null ? 0
				: BRF002.getR76_amount_fcy_non_resident().intValue());
	}

	///// srl_no -87/////////
	Row row77 = sheet.getRow(86);

	Cell R77cell1 = row77.getCell(5);
	if (R77cell1 != null) {
		R77cell1.setCellValue(
				BRF002.getR77_amount_aed_resident() == null ? 0 : BRF002.getR77_amount_aed_resident().intValue());
	}

	Cell R77cell3 = row77.getCell(7);
	if (R77cell3 != null) {
		R77cell3.setCellValue(
				BRF002.getR77_amount_fcy_resident() == null ? 0 : BRF002.getR77_amount_fcy_resident().intValue());
	}

	Cell R77cell5 = row77.getCell(9);
	if (R77cell5 != null) {
		R77cell5.setCellValue(BRF002.getR77_amount_aed_non_resident() == null ? 0
				: BRF002.getR77_amount_aed_non_resident().intValue());
	}

	Cell R77cell7 = row77.getCell(11);
	if (R77cell7 != null) {
		R77cell7.setCellValue(BRF002.getR77_amount_fcy_non_resident() == null ? 0
				: BRF002.getR77_amount_fcy_non_resident().intValue());
	}

	///// srl_no -89/////////
	Row row79 = sheet.getRow(88);

	Cell R79cell1 = row79.getCell(5);
	if (R79cell1 != null) {
		R79cell1.setCellValue(
				BRF002.getR79_amount_aed_resident() == null ? 0 : BRF002.getR79_amount_aed_resident().intValue());
	}

	Cell R79cell3 = row79.getCell(7);
	if (R79cell3 != null) {
		R79cell3.setCellValue(
				BRF002.getR79_amount_fcy_resident() == null ? 0 : BRF002.getR79_amount_fcy_resident().intValue());
	}

	Cell R79cell5 = row79.getCell(9);
	if (R79cell5 != null) {
		R79cell5.setCellValue(BRF002.getR79_amount_aed_non_resident() == null ? 0
				: BRF002.getR79_amount_aed_non_resident().intValue());
	}

	Cell R79cell7 = row79.getCell(11);
	if (R79cell7 != null) {
		R79cell7.setCellValue(BRF002.getR79_amount_fcy_non_resident() == null ? 0
				: BRF002.getR79_amount_fcy_non_resident().intValue());
	}

	///// srl_no -90/////////
	Row row80 = sheet.getRow(89);

	Cell R80cell1 = row80.getCell(5);
	if (R80cell1 != null) {
		R80cell1.setCellValue(
				BRF002.getR80_amount_aed_resident() == null ? 0 : BRF002.getR80_amount_aed_resident().intValue());
	}

	Cell R80cell3 = row80.getCell(7);
	if (R80cell3 != null) {
		R80cell3.setCellValue(
				BRF002.getR80_amount_fcy_resident() == null ? 0 : BRF002.getR80_amount_fcy_resident().intValue());
	}

	Cell R80cell5 = row80.getCell(9);
	if (R80cell5 != null) {
		R80cell5.setCellValue(BRF002.getR80_amount_aed_non_resident() == null ? 0
				: BRF002.getR80_amount_aed_non_resident().intValue());
	}

	Cell R80cell7 = row80.getCell(11);
	if (R80cell7 != null) {
		R80cell7.setCellValue(BRF002.getR80_amount_fcy_non_resident() == null ? 0
				: BRF002.getR80_amount_fcy_non_resident().intValue());
	}

	///// srl_no -91/////////
	Row row81 = sheet.getRow(90);

	Cell R81cell1 = row81.getCell(5);
	if (R81cell1 != null) {
		R81cell1.setCellValue(
				BRF002.getR81_amount_aed_resident() == null ? 0 : BRF002.getR81_amount_aed_resident().intValue());
	}

	Cell R81cell3 = row81.getCell(7);
	if (R81cell3 != null) {
		R81cell3.setCellValue(
				BRF002.getR81_amount_fcy_resident() == null ? 0 : BRF002.getR81_amount_fcy_resident().intValue());
	}

	Cell R81cell5 = row81.getCell(9);
	if (R81cell5 != null) {
		R81cell5.setCellValue(BRF002.getR81_amount_aed_non_resident() == null ? 0
				: BRF002.getR81_amount_aed_non_resident().intValue());
	}

	Cell R81cell7 = row81.getCell(11);
	if (R81cell7 != null) {
		R81cell7.setCellValue(BRF002.getR81_amount_fcy_non_resident() == null ? 0
				: BRF002.getR81_amount_fcy_non_resident().intValue());
	}
	///// srl_no -92/////////
	Row row82 = sheet.getRow(91);

	Cell R82cell1 = row82.getCell(5);
	if (R82cell1 != null) {
		R82cell1.setCellValue(
				BRF002.getR82_amount_aed_resident() == null ? 0 : BRF002.getR82_amount_aed_resident().intValue());
	}

	Cell R82cell3 = row82.getCell(7);
	if (R82cell3 != null) {
		R82cell3.setCellValue(
				BRF002.getR82_amount_fcy_resident() == null ? 0 : BRF002.getR82_amount_fcy_resident().intValue());
	}

	Cell R82cell5 = row82.getCell(9);
	if (R82cell5 != null) {
		R82cell5.setCellValue(BRF002.getR82_amount_aed_non_resident() == null ? 0
				: BRF002.getR82_amount_aed_non_resident().intValue());
	}

	Cell R82cell7 = row82.getCell(11);
	if (R82cell7 != null) {
		R82cell7.setCellValue(BRF002.getR82_amount_fcy_non_resident() == null ? 0
				: BRF002.getR82_amount_fcy_non_resident().intValue());
	}
	///// srl_no -93/////////
	Row row83 = sheet.getRow(92);

	Cell R83cell1 = row83.getCell(5);
	if (R83cell1 != null) {
		R83cell1.setCellValue(
				BRF002.getR83_amount_aed_resident() == null ? 0 : BRF002.getR83_amount_aed_resident().intValue());
	}

	Cell R83cell3 = row83.getCell(7);
	if (R83cell3 != null) {
		R83cell3.setCellValue(
				BRF002.getR83_amount_fcy_resident() == null ? 0 : BRF002.getR83_amount_fcy_resident().intValue());
	}

	Cell R83cell5 = row83.getCell(9);
	if (R83cell5 != null) {
		R83cell5.setCellValue(BRF002.getR83_amount_aed_non_resident() == null ? 0
				: BRF002.getR83_amount_aed_non_resident().intValue());
	}

	Cell R83cell7 = row83.getCell(11);
	if (R83cell7 != null) {
		R83cell7.setCellValue(BRF002.getR83_amount_fcy_non_resident() == null ? 0
				: BRF002.getR83_amount_fcy_non_resident().intValue());
	}

	///// srl_no -94/////////
	Row row84 = sheet.getRow(93);

	Cell R84cell1 = row84.getCell(5);
	if (R84cell1 != null) {
		R84cell1.setCellValue(
				BRF002.getR84_amount_aed_resident() == null ? 0 : BRF002.getR84_amount_aed_resident().intValue());
	}

	Cell R84cell3 = row84.getCell(7);
	if (R84cell3 != null) {
		R84cell3.setCellValue(
				BRF002.getR84_amount_fcy_resident() == null ? 0 : BRF002.getR84_amount_fcy_resident().intValue());
	}

	Cell R84cell5 = row84.getCell(9);
	if (R84cell5 != null) {
		R84cell5.setCellValue(BRF002.getR84_amount_aed_non_resident() == null ? 0
				: BRF002.getR84_amount_aed_non_resident().intValue());
	}

	Cell R84cell7 = row84.getCell(11);
	if (R84cell7 != null) {
		R84cell7.setCellValue(BRF002.getR84_amount_fcy_non_resident() == null ? 0
				: BRF002.getR84_amount_fcy_non_resident().intValue());
	}

	///// srl_no -95/////////
	Row row85 = sheet.getRow(94);

	Cell R85cell1 = row85.getCell(5);
	if (R85cell1 != null) {
		R85cell1.setCellValue(
				BRF002.getR85_amount_aed_resident() == null ? 0 : BRF002.getR85_amount_aed_resident().intValue());
	}

	Cell R85cell3 = row85.getCell(7);
	if (R85cell3 != null) {
		R85cell3.setCellValue(
				BRF002.getR85_amount_fcy_resident() == null ? 0 : BRF002.getR85_amount_fcy_resident().intValue());
	}

	Cell R85cell5 = row85.getCell(9);
	if (R85cell5 != null) {
		R85cell5.setCellValue(BRF002.getR85_amount_aed_non_resident() == null ? 0
				: BRF002.getR85_amount_aed_non_resident().intValue());
	}

	Cell R85cell7 = row85.getCell(11);
	if (R85cell7 != null) {
		R85cell7.setCellValue(BRF002.getR85_amount_fcy_non_resident() == null ? 0
				: BRF002.getR85_amount_fcy_non_resident().intValue());
	}
        	
    	
	Row row87 = sheet.getRow(100);
	Cell R87cell1 = row87.getCell(4);
	if (R87cell1 != null) {
		R87cell1.setCellValue(
				BRF002.getR87_stage3_loans() == null ? 0 : BRF002.getR87_stage3_loans().intValue());
	}

	
	Cell R87cell2 = row87.getCell(5);
	if (R87cell2 != null) {
		R87cell2.setCellValue(
				BRF002.getR87_stage3_investments() == null ? 0 : BRF002.getR87_stage3_investments().intValue());
	}

	Cell R87cell3 = row87.getCell(6);
	if (R87cell3 != null) {
		R87cell3.setCellValue(
				BRF002.getR87_all_othr_assets_colc_prov() == null ? 0 : BRF002.getR87_all_othr_assets_colc_prov().intValue());
	}

	/*
	 * Cell R87cell5 = row87.getCell(7); if (R87cell5 != null) {
	 * R87cell5.setCellValue(BRF002.getR87_other_colc_prov() == null ? 0 :
	 * BRF002.getR87_other_colc_prov().intValue()); }
	 */

	Cell R87cell7 = row87.getCell(8);
	if (R87cell7 != null) {
		R87cell7.setCellValue(BRF002.getR87_loans_advances_colc_prov() == null ? 0
				: BRF002.getR87_loans_advances_colc_prov().intValue());
	}
	
	Cell R87cell9 = row87.getCell(9);
	if (R87cell9!= null) {
		R87cell9.setCellValue(BRF002.getR87_legacy_interest() == null ? 0
				: BRF002.getR87_legacy_interest().intValue());
	}
	
	Cell R87cell8 = row87.getCell(10);
	if (R87cell8 != null) {
		R87cell8.setCellValue(BRF002.getR87_provision_interest() == null ? 0
				: BRF002.getR87_provision_interest().intValue());
	}
	
	Row row88 = sheet.getRow(101);
	Cell R88cell1 = row88.getCell(4);
	if (R88cell1 != null) {
		R88cell1.setCellValue(
				BRF002.getR88_stage3_loans() == null ? 0 : BRF002.getR88_stage3_loans().intValue());
	}

	
	Cell R88cell2 = row88.getCell(5);
	if (R88cell2 != null) {
		R88cell2.setCellValue(
				BRF002.getR88_stage3_investments() == null ? 0 : BRF002.getR88_stage3_investments().intValue());
	}

	Cell R88cell3 = row88.getCell(6);
	if (R88cell3 != null) {
		R88cell3.setCellValue(
				BRF002.getR88_all_othr_assets_colc_prov() == null ? 0 : BRF002.getR88_all_othr_assets_colc_prov().intValue());
	}

	/*
	 * Cell R88cell5 = row88.getCell(7); if (R88cell5 != null) {
	 * R88cell5.setCellValue(BRF002.getR88_other_colc_prov() == null ? 0 :
	 * BRF002.getR88_other_colc_prov().intValue()); }
	 */

	Cell R88cell7 = row88.getCell(8);
	if (R88cell7 != null) {
		R88cell7.setCellValue(BRF002.getR88_loans_advances_colc_prov() == null ? 0
				: BRF002.getR88_loans_advances_colc_prov().intValue());
	}
	
	Cell R88cell9 = row88.getCell(9);
	if (R88cell9!= null) {
		R88cell9.setCellValue(BRF002.getR88_legacy_interest() == null ? 0
				: BRF002.getR88_legacy_interest().intValue());
	}
	
	Cell R88cell8 = row88.getCell(10);
	if (R88cell8 != null) {
		R88cell8.setCellValue(BRF002.getR88_provision_interest() == null ? 0
				: BRF002.getR88_provision_interest().intValue());
	}
	
	Row row89 = sheet.getRow(102);
	Cell R89cell1 = row89.getCell(4);
	if (R89cell1 != null) {
		R89cell1.setCellValue(
				BRF002.getR89_stage3_loans() == null ? 0 : BRF002.getR89_stage3_loans().intValue());
	}

	
	Cell R89cell2 = row89.getCell(5);
	if (R89cell2 != null) {
		R89cell2.setCellValue(
				BRF002.getR89_stage3_investments() == null ? 0 : BRF002.getR89_stage3_investments().intValue());
	}

	Cell R89cell3 = row89.getCell(6);
	if (R89cell3 != null) {
		R89cell3.setCellValue(
				BRF002.getR89_all_othr_assets_colc_prov() == null ? 0 : BRF002.getR89_all_othr_assets_colc_prov().intValue());
	}

	/*
	 * Cell R89cell5 = row89.getCell(7); if (R89cell5 != null) {
	 * R89cell5.setCellValue(BRF002.getR89_other_colc_prov() == null ? 0 :
	 * BRF002.getR89_other_colc_prov().intValue()); }
	 */

	Cell R89cell7 = row89.getCell(8);
	if (R89cell7 != null) {
		R89cell7.setCellValue(BRF002.getR89_loans_advances_colc_prov() == null ? 0
				: BRF002.getR89_loans_advances_colc_prov().intValue());
	}
	
	Cell R89cell9 = row89.getCell(9);
	if (R89cell9!= null) {
		R89cell9.setCellValue(BRF002.getR89_legacy_interest() == null ? 0
				: BRF002.getR89_legacy_interest().intValue());
	}
	
	Cell R89cell8 = row89.getCell(10);
	if (R89cell8 != null) {
		R89cell8.setCellValue(BRF002.getR89_provision_interest() == null ? 0
				: BRF002.getR89_provision_interest().intValue());
	}
	
	Row row90 = sheet.getRow(103);
	Cell R90cell1 = row90.getCell(4);
	if (R90cell1 != null) {
		R90cell1.setCellValue(
				BRF002.getR90_stage3_loans() == null ? 0 : BRF002.getR90_stage3_loans().intValue());
	}

	
	Cell R90cell2 = row90.getCell(5);
	if (R90cell2 != null) {
		R90cell2.setCellValue(
				BRF002.getR90_stage3_investments() == null ? 0 : BRF002.getR90_stage3_investments().intValue());
	}

	Cell R90cell3 = row90.getCell(6);
	if (R90cell3 != null) {
		R90cell3.setCellValue(
				BRF002.getR90_all_othr_assets_colc_prov() == null ? 0 : BRF002.getR90_all_othr_assets_colc_prov().intValue());
	}

	/*
	 * Cell R90cell5 = row90.getCell(7); if (R90cell5 != null) {
	 * R90cell5.setCellValue(BRF002.getR90_other_colc_prov() == null ? 0 :
	 * BRF002.getR90_other_colc_prov().intValue()); }
	 */

	Cell R90cell7 = row90.getCell(8);
	if (R90cell7 != null) {
		R90cell7.setCellValue(BRF002.getR90_loans_advances_colc_prov() == null ? 0
				: BRF002.getR90_loans_advances_colc_prov().intValue());
	}
	
	Cell R90cell9 = row90.getCell(9);
	if (R90cell9!= null) {
		R90cell9.setCellValue(BRF002.getR90_legacy_interest() == null ? 0
				: BRF002.getR90_legacy_interest().intValue());
	}
	
	Cell R90cell8 = row90.getCell(10);
	if (R90cell8 != null) {
		R90cell8.setCellValue(BRF002.getR90_provision_interest() == null ? 0
				: BRF002.getR90_provision_interest().intValue());
	}
	
	Row row92 = sheet.getRow(107);
	Cell R92cell1 = row92.getCell(4);
	if (R92cell1 != null) {
		R92cell1.setCellValue(
				BRF002.getR92_stage3_loans() == null ? 0 : BRF002.getR92_stage3_loans().intValue());
	}

	
	Row row93 = sheet.getRow(108);
	Cell R93cell1 = row93.getCell(4);
	if (R93cell1 != null) {
		R93cell1.setCellValue(
				BRF002.getR93_stage3_loans() == null ? 0 : BRF002.getR93_stage3_loans().intValue());
	}

	
	Row row94 = sheet.getRow(109);
	Cell R94cell1 = row94.getCell(4);
	if (R94cell1 != null) {
		R94cell1.setCellValue(
				BRF002.getR94_stage3_loans() == null ? 0 : BRF002.getR94_stage3_loans().intValue());
	}

		
    	
    	
    	
    	
    	
    	
    	// Save the changes
    	   workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
    	        FileOutputStream fileOut = new FileOutputStream(env.getProperty("output.exportpathfinal")+"011-BRF-002-A.xls");
    	        workbook.write(fileOut);
    	        fileOut.close();
    	        System.out.println(fileOut);
    	        path= fileOut.toString();
    	        // Close the workbook
    	        System.out.println("PATH : "+path);
    	        workbook.close();
    				}
    			
    			}
    			
    			} catch (ParseException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (EncryptedDocumentException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (InvalidFormatException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			} catch (IOException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			
    			

    			
    		}////else end

    	}
    	outputFile = new File(env.getProperty("output.exportpathfinal")+"011-BRF-002-A.xls");

    	return outputFile;



    	}
    			
	
	public String detailChanges2(BRF2_DETAIL_ENTITY detail, String report_label_1, BigDecimal act_balance_amt_lc,
	        String foracid, String report_name_1, String report_addl_criteria_1,String report_date, AuditReasonDTO reason) {

	    String msg = "";

	    try {
	        Session hs = sessionFactory.getCurrentSession();
	       // Optional<BRF2_DETAIL_ENTITY> Brf2detail = BRF2_DetaiRep1.findById(foracid);	        
	        
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
			LocalDate parsedDate = LocalDate.parse(report_date, inputFormatter);
			String formattedDate1 = parsedDate.format(dateFormatter);			
			
	        BRF2_DETAIL_ENTITY Brf2detail = BRF2_DetaiRep1.getbyaccnoanddate(foracid, formattedDate1);

	        if (!Brf2detail.equals(null) && Brf2detail!=null) {
	            BRF2_DETAIL_ENTITY BRFdetail = Brf2detail;

	            // Null-safe comparison to avoid NPE if DB values are null
	            boolean noChanges = Objects.equals(BRFdetail.getReport_label_1(), report_label_1)
	                    && Objects.equals(BRFdetail.getReport_name_1(), report_name_1)
	                    && Objects.equals(BRFdetail.getAct_balance_amt_lc(), act_balance_amt_lc)
	                    && Objects.equals(BRFdetail.getReport_addl_criteria_1(), report_addl_criteria_1);

	            if (noChanges) {
	                msg = "No modification done";
	            } else {

	                List<String> oldValues = new ArrayList<>();
	                List<String> newValues = new ArrayList<>();
	                List<String> fieldNames = new ArrayList<>();

	                if (!Objects.equals(BRFdetail.getReport_label_1(), report_label_1)) {
	                    oldValues.add(BRFdetail.getReport_label_1());
	                    newValues.add(report_label_1);
	                    fieldNames.add("report_label_1");
	                    BRFdetail.setReport_label_1(report_label_1);
	                }
	                if (!Objects.equals(BRFdetail.getReport_name_1(), report_name_1)) {
	                    oldValues.add(BRFdetail.getReport_name_1());
	                    newValues.add(report_name_1);
	                    fieldNames.add("report_name_1");
	                    BRFdetail.setReport_name_1(report_name_1);
	                }
	                if (!Objects.equals(BRFdetail.getAct_balance_amt_lc(), act_balance_amt_lc)) {
	                    oldValues.add(BRFdetail.getAct_balance_amt_lc() != null ? BRFdetail.getAct_balance_amt_lc().toString() : "null");
	                    newValues.add(act_balance_amt_lc != null ? act_balance_amt_lc.toString() : "null");
	                    fieldNames.add("act_balance_amt_lc");
	                    BRFdetail.setAct_balance_amt_lc(act_balance_amt_lc);
	                }
	                if (!Objects.equals(BRFdetail.getReport_addl_criteria_1(), report_addl_criteria_1)) {
	                    oldValues.add(BRFdetail.getReport_addl_criteria_1());
	                    newValues.add(report_addl_criteria_1);
	                    fieldNames.add("report_addl_criteria_1");
	                    BRFdetail.setReport_addl_criteria_1(report_addl_criteria_1);
	                }

	                BRF2_DetaiRep1.save(BRFdetail);
/*
	                // === Begin Audit Block ===
	                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
	                        .getRequestAttributes()).getRequest();
	                String user1 = (String) request.getSession().getAttribute("USERID");
	                String username = (String) request.getSession().getAttribute("USERNAME");

	                String auditID = sequence.generateRequestUUId();

	                MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
	                audit.setAudit_date(new Date());
	                audit.setEntry_time(new Date());
	                audit.setEntry_user(user1);
	                audit.setFunc_code("EDIT");
	                audit.setAudit_table("BRF2_DETAILTABLE");
	                audit.setAudit_screen("Edit");
	                audit.setEvent_id(user1);
	                audit.setEvent_name(username);
	                audit.setRemarks("Edit Successfully");
	                audit.setField_name(String.join("; ", fieldNames));
	                audit.setOld_value(String.join("; ", oldValues));
	                audit.setNew_value(String.join("; ", newValues));

	                UserProfile values1 = userProfileRep.getRole(user1);
	                audit.setAuth_user(values1.getAuth_user());
	                audit.setAuth_time(values1.getAuth_time());
	                audit.setAudit_ref_no(auditID);

	                mANUAL_Service_Rep.save(audit);
	                // === End Audit Block ===
*/
	                // =========================================================================
	                // PROCEDURE EXECUTION LOGIC (BRF2)
	                // =========================================================================
	                try {
	                    // Extract Date from the entity itself
	                    Date entityDate = BRFdetail.getReport_date(); 
	                    
	                    if (entityDate != null) {
	                        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(entityDate);
	        
	                        // Run summary procedure after commit
	                        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
	                            @Override
	                            public void afterCommit() {
	                                try {
	                                    logger.info("Transaction committed — calling BRF2_SUMMARY_PROCEDURE({})", formattedDate);
	                                    
	                                    // Make sure 'jdbcTemplate' is available in this class
	                                    jdbcTemplate.update("BEGIN BRF2_SUMMARY_PROCEDURE(?); END;", formattedDate);
	                                    
	                                    logger.info("BRF2 Procedure executed successfully after commit.");
	                                } catch (Exception e) {
	                                    logger.error("Error executing BRF2 procedure after commit", e);
	                                }
	                            }
	                        });
	                    } else {
	                        logger.warn("Report Date is null in BRF2 entity, skipping summary procedure.");
	                    }
	                } catch (Exception e) {
	                    logger.error("Error preparing BRF2 procedure call", e);
	                }
	                // =========================================================================

	                logger.info("Edited Record");
	                msg = "Edited Successfully";
	            }
	        } else {
	            msg = "No data Found";
	        }
	    } catch (Exception e) {
	        msg = "error occured. Please contact Administrator";
	        e.printStackTrace();
	    }

	    return msg;
	}
/****** ARCHEVE VIEW ******/
	
	public ModelAndView getArchieveBRF002View(String reportId, String fromdate, String todate, String currency, String dtltype,
			Pageable pageable) {
		
			ModelAndView mv = new ModelAndView();
			Session hs = sessionFactory.getCurrentSession();
			int pageSize = pageable.getPageSize();
			int currentPage = pageable.getPageNumber();
			int startItem = currentPage * pageSize;
			List<BRF2_ENTITY> T1rep = new ArrayList<BRF2_ENTITY>();
			// Query<Object[]> qr;

			List<BRF2_ENTITY> T1Master = new ArrayList<BRF2_ENTITY>();
			/* List<BRF73_TABLE2> T1Master1 = new ArrayList<BRF73_TABLE2>(); */

			logger.info("Inside archive" +currency);

			try {
				Date d1 = df.parse(todate);
			//	T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

				T1Master = hs.createQuery("from  BRF2_ENTITY a where a.report_date = ?1 ", BRF2_ENTITY.class)
						.setParameter(1, df.parse(todate)).getResultList();

				/*
				 * T1Master1 = hs.createQuery("from BRF73_TABLE2 a where a.report_date = ?1 ",
				 * BRF73_TABLE2.class) .setParameter(1, df.parse(todate)).getResultList();
				 */

			} catch (ParseException e) {
				e.printStackTrace();
			}

			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			mv.setViewName("RR/BRF2ARCH");
			// mv.addObject("currlist", refCodeConfig.currList());
			mv.addObject("reportsummary", T1Master);
			/* mv.addObject("reportsummary1", T1Master1); */
			mv.addObject("reportmaster", T1Master);
			mv.addObject("displaymode", "summary");
			mv.addObject("reportsflag", "reportsflag");
			mv.addObject("menu", reportId);
			
			System.out.println("scv" + mv.getViewName());

			return mv;

		}

/**** ARCH DETAILS 
	 * @throws ParseException ****/

	public ModelAndView ARCHgetBRF002currentDtl(String reportId, String fromdate, String todate, String currency,
			String dtltype, Pageable pageable, String filter) throws ParseException {

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;

		ModelAndView mv = new ModelAndView();

		Session hs = sessionFactory.getCurrentSession();
		List<Object> T1Dt1 = new ArrayList<Object>();

		Query<Object[]> qr;

		if (dtltype.equals("report")) {
			if (!filter.equals("null")) {
				System.out.println("1");
				System.out.println("Filter - "+filter);
				
				
					qr = hs.createNativeQuery("select * from BRF2_ARCHIVTABLE a where report_date=?1 and report_label_1=?2");
					qr.setParameter(1,  df.parse(todate));
					qr.setParameter(2,filter);
						

			} else {
				System.out.println("2");
				qr = hs.createNativeQuery("select * from BRF2_ARCHIVTABLE");

			}
		} else {
			System.out.println("3");
			qr = hs.createNativeQuery("select * from BRF2_ARCHIVTABLE  where report_date = ?1");
		}

		/*
		 * try { qr.setParameter(1, df.parse(todate));
		 * 
		 * } catch (ParseException e) { e.printStackTrace(); }
		 */
		List<BRF2_ARCHIVENTITY> T1Master = new ArrayList<BRF2_ARCHIVENTITY>();

		try {
			System.out.println("Values entered");
			T1Master = hs.createQuery("from BRF2_ARCHIVENTITY a where a.report_date = ?1", BRF2_ARCHIVENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();
			
			
			
		} catch (ParseException e) {

			e.printStackTrace();
		}

		logger.info("Getting Report Detail for : " + reportId + "," + fromdate + "," + todate + "," + currency);
		List<Object[]> result = qr.getResultList();

		
			for (Object[] a : result) {
				String cust_id = (String) a[0];
				String foracid = (String) a[1];
				BigDecimal act_balance_amt_lc = (BigDecimal) a[2];
				BigDecimal eab_lc = (BigDecimal) a[3];
				String acct_name = (String) a[4];
				String acct_crncy_code = (String) a[5];
				String gl_code = (String) a[6];
				String gl_sub_head_code = (String) a[7];
				String gl_sub_head_desc = (String) a[8];
				String country_of_incorp = (String) a[9];
				String cust_type = (String) a[10];
				String schm_code = (String) a[11];
				String schm_type = (String) a[12];
				String sol_id = (String) a[13];
				String acid = (String) a[14];
				String segment = (String) a[15];
				String sub_segment = (String) a[16];
				BigDecimal sector = (BigDecimal) a[17];
				String sub_sector = (String) a[18];
				String sector_code = (String) a[19];
				String group_id = (String) a[20];
				String constitution_code = (String) a[21];
				String country = (String) a[22];
				String legal_entity_type = (String) a[23];
				String constitution_desc = (String) a[24];
				String purpose_of_advn = (String) a[25];
				BigDecimal hni_networth = (BigDecimal) a[26];
				String turnover = (String) a[27];
				String bacid = (String) a[28];
				String report_name_1 = (String) a[29];
				String report_label_1 = (String) a[30];
				String report_addl_criteria_1 = (String) a[31];
				String report_addl_criteria_2 = (String) a[32];
				String report_addl_criteria_3 = (String) a[33];
				String create_user = (String) a[34];
				Date create_time = (Date) a[35];
				String modify_user = (String) a[36];
				Date modify_time = (Date) a[37];
				String verify_user = (String) a[38];
				Date verify_time = (Date) a[39];
				Character entity_flg = (Character) a[40];
				Character modify_flg = (Character) a[41];
				Character del_flg = (Character) a[42];
				Character nre_status = (Character) a[43];
				Date report_date = (Date) a[44];
				Date maturity_date = (Date) a[45];
				String gender = (String) a[46];
				String version = (String) a[47];
				String remarks = (String) a[48];
				Character nre_flag = (Character) a[49];

				String Remarks1;

				if (act_balance_amt_lc != null) {
					if (act_balance_amt_lc.toString().contains("-")) {
						Remarks1 = "DR";
					} else {
						Remarks1 = "CR";
					}
				} else {
					Remarks1 = "";
				}

				BRF2_ARCHIVENTITY py = new BRF2_ARCHIVENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
						acct_crncy_code, gl_code, gl_sub_head_code, gl_sub_head_desc, country_of_incorp, cust_type,
						schm_code, schm_type, sol_id, acid, segment, sub_segment, sector, sub_sector, sector_code, group_id,
						constitution_code, country, legal_entity_type, constitution_desc, purpose_of_advn, hni_networth,
						turnover, bacid, report_name_1, report_label_1, report_addl_criteria_1, report_addl_criteria_2,
						report_addl_criteria_3, create_user, create_time, modify_user, modify_time, verify_user,
						verify_time, entity_flg, modify_flg, del_flg, nre_status, report_date, maturity_date, gender,
						version, remarks, nre_flag);

				T1Dt1.add(py);

			};


		List<Object> pagedlist;

		if (T1Dt1.size() < startItem) {
			pagedlist = Collections.emptyList();
			
		} else {
			int toIndex = Math.min(startItem + pageSize, T1Dt1.size());
			pagedlist = T1Dt1.subList(startItem, toIndex);
		}

		logger.info("Converting to Page");
		Page<Object> T1Dt1Page = new PageImpl<Object>(pagedlist, PageRequest.of(currentPage, pageSize), T1Dt1.size());

		mv.setViewName("RR" + "/" + "BRF2ARCH::reportcontent");
		mv.addObject("reportdetails", T1Dt1Page.getContent());
		mv.addObject("reportmaster", T1Master);
		mv.addObject("reportmaster1", qr);
		mv.addObject("singledetail", new T1CurProdDetail());
		mv.addObject("reportsflag", "reportsflag");
		mv.addObject("menu", reportId);
		return mv;
	}
	

	
	
	
	}