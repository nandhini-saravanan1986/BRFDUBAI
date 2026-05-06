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
import com.bornfire.xbrl.entities.BRF1_ARCHIVENTITY;
import com.bornfire.xbrl.entities.TransactionmastertableRep;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.BRBS.BRF1_DETAIL_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF1_DetaiRep;
import com.bornfire.xbrl.entities.BRBS.BRF1_REPORT_ENTITY;
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
public class BRF001ReportService {

	private static final Logger logger = LoggerFactory.getLogger(BRF001ReportService.class);

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	DataSource srcdataSource;

	@Autowired
	Environment env;

	@Autowired
	TransactionmastertableRep rep;

	@Autowired
	BRF1_DetaiRep BRF1_DetaiRep1;
	
	@Autowired
	MANUAL_Service_Rep mANUAL_Service_Rep;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	UserProfileRep userProfileRep;
	/*
	 * @Autowired BRF73ServiceRepo brf73ServiceRepo;
	 */

	DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

	public String preCheck(String reportid, String fromdate, String todate) {

		String msg = "";
		Session hs = sessionFactory.getCurrentSession();
		Date dt1;
		Date dt9;
		logger.info("Report precheck : " + reportid);

		try {
			//dt1 = new SimpleDateFormat("dd/MM/yyyy").parse(fromdate);
			dt9 = new SimpleDateFormat("dd/MM/yyyy").parse(todate);
			logger.info("Getting No of records in Master table :" + reportid);
			Long dtlcnt = (Long) hs.createQuery("select count(*) from BRF1_REPORT_ENTITY a where a.report_date=?1")
					.setParameter(1, dt9).getSingleResult();

			if (dtlcnt > 0) {
				logger.info("Getting No of records in Mod table :" + reportid);
				Long modcnt = (Long) hs.createQuery("select count(*) from BRF1_REPORT_ENTITY a").getSingleResult();
				if (modcnt > 0) {
					msg = "success";
				}
			} else {
				// msg = "Data Not available for the Report. Please Contact Administrator";
				msg = "success";

			}

		} catch (Exception e) {
			logger.info(e.getMessage());
			msg = "success";
			e.printStackTrace();

		}

		return msg;

	}

	public ModelAndView getBRF001View(String reportId, String fromdate, String todate, String currency, String dtltype,
			Pageable pageable) {

		ModelAndView mv = new ModelAndView();
		Session hs = sessionFactory.getCurrentSession();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<BRF1_REPORT_ENTITY> T1rep = new ArrayList<BRF1_REPORT_ENTITY>();
		// Query<Object[]> qr;

		List<BRF1_REPORT_ENTITY> T1Master = new ArrayList<BRF1_REPORT_ENTITY>();
		/* List<BRF73_TABLE2> T1Master1 = new ArrayList<BRF73_TABLE2>(); */

		logger.info("Inside archive" + currency);

		try {
			Date d1 = df.parse(todate);
			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			T1Master = hs.createQuery("from  BRF1_REPORT_ENTITY a where a.report_date = ?1 ", BRF1_REPORT_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();

			/*
			 * T1Master1 = hs.createQuery("from BRF73_TABLE2 a where a.report_date = ?1 ",
			 * BRF73_TABLE2.class) .setParameter(1, df.parse(todate)).getResultList();
			 */

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

		mv.setViewName("RR/BRF1");
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

	public ModelAndView getBRF001currentDtl(String reportId, String fromdate, String todate, String currency,
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
			searchCondition = " and (CUST_ID like '%" + safeSearch + "%' or FORACID like '%"
					+ safeSearch + "%' or ACCT_NAME like '%" + safeSearch + "%' or ACT_BALANCE_AMT_LC like '%"
					+ safeSearch + "%' or REPORT_NAME_1 like '%" + safeSearch + "%' or REPORT_LABEL_1 like '%"
					+ safeSearch + "%' or REPORT_ADDL_CRITERIA_1 like '%" + safeSearch + "%' or REPORT_DATE like '%"
					+ safeSearch + "%') ";
		}

		if (dtltype.equals("report") || dtltype.equals("ARCH")) {
			if (!filter.equals("null")) {
				qr = hs.createNativeQuery(
						"select * from BRF1_DETAILTABLE  a where report_date = ?1 and report_label_1 =?2" + searchCondition);
				countQr = hs.createNativeQuery("select count(*) from BRF1_DETAILTABLE a where report_date = ?1 and report_label_1 = ?2" + searchCondition);

				qr.setParameter(2, filter);
				countQr.setParameter(2, filter);

			} else {
				qr = hs.createNativeQuery("select * from BRF1_DETAILTABLE a where report_date = ?1" + searchCondition);
				countQr = hs.createNativeQuery("select count(*) from BRF1_DETAILTABLE a where report_date = ?1" + searchCondition);
			}
		} else {
			qr = hs.createNativeQuery("select * from BRF1_DETAILTABLE  where report_date = ?1" + searchCondition);
			countQr = hs.createNativeQuery("select count(*) from BRF1_DETAILTABLE where report_date = ?1" + searchCondition);
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
	    
		List<BRF1_DETAIL_ENTITY> T1Master = new ArrayList<BRF1_DETAIL_ENTITY>();
/*
		try {
			T1Master = hs.createQuery("from BRF1_DETAIL_ENTITY a where a.report_date = ?1", BRF1_DETAIL_ENTITY.class)
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
			String nre_flag = (String) a[49];

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

			BRF1_DETAIL_ENTITY py = new BRF1_DETAIL_ENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
					acct_crncy_code, gl_code, gl_sub_head_code, gl_sub_head_desc, country_of_incorp, cust_type,
					schm_code, schm_type, sol_id, acid, segment, sub_segment, sector, sub_sector, sector_code, group_id,
					constitution_code, country, legal_entity_type, constitution_desc, purpose_of_advn, hni_networth,
					turnover, bacid, report_name_1, report_label_1, report_addl_criteria_1, report_addl_criteria_2,
					report_addl_criteria_3, create_user, create_time, modify_user, modify_time, verify_user,
					verify_time, entity_flg, modify_flg, del_flg, nre_status, report_date, maturity_date, gender,
					version, remarks, nre_flag);

			T1Dt1.add(py);

		}
		;

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

		System.out.println("Size of the list "+T1Dt1.size());
		System.out.println("Size of the list from DB "+T1Dt1Page.getTotalElements());
		mv.setViewName("RR" + "/" + "BRF1::reportcontent");
		mv.addObject("reportdetails", T1Dt1Page.getContent());
		mv.addObject("reportdetailsPage", T1Dt1Page);
		mv.addObject("reportmaster", T1Master);
		mv.addObject("reportmaster12", T1Dt1);
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

		logger.info("Getting Output file :" + reportId);
		fileName = "011-BRF-001-A";
		/*
		 * try { SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy"); Date
		 * ConDate = dateFormat1.parse(todate); System.out.println(ConDate);
		 * SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MM-yyyy"); String
		 * strDate1 = formatter1.format(ConDate); fileName = "t" + reportId + "_" +
		 * strDate1;
		 * 
		 * 
		 * } catch (ParseException e1) {
		 * 
		 * logger.info(e1.getMessage()); e1.printStackTrace(); }
		 */

		if (!filetype.equals("xbrl")) {
			if (!filetype.contains("BRF")) {

				try {
					InputStream jasperFile;
					logger.info("Getting Jasper file :" + reportId);
					if (filetype.equals("detailexcel")) {
						if (dtltype.equals("report")) {
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF1_Detail.jrxml");
						} else {
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF1_Detail.jrxml");
						}

					} else {
						if (dtltype.equals("report")) {
							logger.info("Inside report");
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF1.jrxml");
						} else {
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF1.jrxml");
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
				// Retrieve session attributes safely (assuming it's a web environment)
				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
						.getRequest();
				String user1 = (String) request.getSession().getAttribute("USERID");
				String username = (String) request.getSession().getAttribute("USERNAME");

				String auditID = sequence.generateRequestUUId();

				MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
				audit.setAudit_date(new Date());
				audit.setEntry_time(new Date());
				audit.setEntry_user(user1);
				audit.setAudit_table("BRF1_SUMMARYTABLE");
				audit.setEvent_id(user1);
				audit.setEvent_name(username);

				if (filetype.equals("pdf")) {
					audit.setFunc_code("DOWNLOAD_PDF");
					audit.setAudit_screen("Download PDF");
					audit.setRemarks("BRF1 PDF downloaded successfully");
				} else if (filetype.equals("detailexcel")) {
					audit.setAudit_table("BRF1_DETAILTABLE");
					audit.setFunc_code("DOWNLOAD_EXCEL_DETAIL");
					audit.setAudit_screen("Download Excel Detail");
					audit.setRemarks("BRF1 Detailed Excel downloaded successfully");
				} else {
					audit.setFunc_code("DOWNLOAD");
					audit.setAudit_screen("Download");
					audit.setRemarks("BRF1 File downloaded successfully");
				}

				UserProfile values1 = userProfileRep.getRole(user1);
				audit.setAuth_user(values1.getAuth_user());
				audit.setAuth_time(values1.getAuth_time());
				audit.setAudit_ref_no(auditID);

				mANUAL_Service_Rep.save(audit);

				return outputFile;
			} else {
				List<BRF1_REPORT_ENTITY> T1Master = new ArrayList<BRF1_REPORT_ENTITY>();
				Session hs = sessionFactory.getCurrentSession();
				try {
					Date d1 = df.parse(todate);

					// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

					T1Master = hs.createQuery("from  BRF1_REPORT_ENTITY a where a.report_date = ?1 ",
							BRF1_REPORT_ENTITY.class).setParameter(1, df.parse(todate)).getResultList();

					if (T1Master.size() == 1) {

						for (BRF1_REPORT_ENTITY BRF001 : T1Master) {

							File Responsecamt = new File(
									env.getProperty("output.exportpathtemp") + "011-BRF-001-AT.xls");

							// Load the Excel file
							Workbook workbook = WorkbookFactory.create(Responsecamt);

							// Get the first sheet
							Sheet sheet = workbook.getSheetAt(0);

							/*
							 * Row r1row = sheet.getRow(4); if (r1row != null) { Cell r1cell =
							 * r1row.getCell(2); if (r1cell == null) { r1cell = r1row.createCell(2); //
							 * Create the cell if it doesn't exist } if (BRF001.getReport_date() != null) {
							 * // Formatting the date to dd-MM-yyyy SimpleDateFormat sdf = new
							 * SimpleDateFormat("dd-MM-yyyy"); String formattedDate =
							 * sdf.format(BRF001.getReport_date()); r1cell.setCellValue(formattedDate); //
							 * Set the formatted date in the cell } else { r1cell.setCellValue(""); // Set
							 * an empty value if the report_date is null } }
							 * 
							 */

							///// srl_no -12////////

							Row row = sheet.getRow(11);
							Cell cell = row.getCell(5);
							if (cell != null) {
								cell.setCellValue(BRF001.getR2_amount_aed_resident() == null ? 0
										: BRF001.getR2_amount_aed_resident().intValue());
							}
							Cell cell1 = row.getCell(7);
							if (cell1 != null) {
								cell1.setCellValue(BRF001.getR2_amount_fcy_resident() == null ? 0
										: BRF001.getR2_amount_fcy_resident().intValue());
							}
							Cell cell2 = row.getCell(9);
							if (cell2 != null) {
								cell2.setCellValue(BRF001.getR2_amount_aed_non_resident() == null ? 0
										: BRF001.getR2_amount_aed_non_resident().intValue());
							}
							Cell cell3 = row.getCell(11);
							if (cell3 != null) {
								cell3.setCellValue(BRF001.getR2_amount_fcy_non_resident() == null ? 0
										: BRF001.getR2_amount_fcy_non_resident().intValue());
							}

///////srl_no -13/////

							Row row3 = sheet.getRow(12);
							Cell R3cell = row3.getCell(5);
							if (R3cell != null) {
								R3cell.setCellValue(BRF001.getR3_amount_aed_resident() == null ? 0
										: BRF001.getR3_amount_aed_resident().intValue());
							}
							Cell R3cell1 = row3.getCell(7);
							if (R3cell1 != null) {
								R3cell1.setCellValue(BRF001.getR3_amount_fcy_resident() == null ? 0
										: BRF001.getR3_amount_fcy_resident().intValue());
							}
							Cell R3cell2 = row3.getCell(9);
							if (R3cell2 != null) {
								R3cell2.setCellValue(BRF001.getR3_amount_aed_non_resident() == null ? 0
										: BRF001.getR3_amount_aed_non_resident().intValue());
							}
							Cell R3cell3 = row3.getCell(11);
							if (R3cell3 != null) {
								R3cell3.setCellValue(BRF001.getR3_amount_fcy_non_resident() == null ? 0
										: BRF001.getR3_amount_fcy_non_resident().intValue());
							}

/////srl_no -14/////////

							Row row4 = sheet.getRow(13);
							Cell R4cell = row4.getCell(5);
							if (R4cell != null) {
								R4cell.setCellValue(BRF001.getR4_amount_aed_resident() == null ? 0
										: BRF001.getR4_amount_aed_resident().intValue());
							}
							Cell R4cell1 = row4.getCell(7);
							if (R4cell1 != null) {
								R4cell1.setCellValue(BRF001.getR4_amount_fcy_resident() == null ? 0
										: BRF001.getR4_amount_fcy_resident().intValue());
							}
							Cell R4cell2 = row4.getCell(9);
							if (R4cell2 != null) {
								R4cell2.setCellValue(BRF001.getR4_amount_aed_non_resident() == null ? 0
										: BRF001.getR4_amount_aed_non_resident().intValue());
							}
							Cell R4cell3 = row4.getCell(11);
							if (R4cell3 != null) {
								R4cell3.setCellValue(BRF001.getR4_amount_fcy_non_resident() == null ? 0
										: BRF001.getR4_amount_fcy_non_resident().intValue());
							}
/////srl_no -15/////////

							Row row5 = sheet.getRow(14);
							Cell R5cell = row5.getCell(5);
							if (R5cell != null) {
								R5cell.setCellValue(BRF001.getR5_amount_aed_resident() == null ? 0
										: BRF001.getR5_amount_aed_resident().intValue());
							}
							Cell R5cell1 = row5.getCell(7);
							if (R5cell1 != null) {
								R5cell1.setCellValue(BRF001.getR5_amount_fcy_resident() == null ? 0
										: BRF001.getR5_amount_fcy_resident().intValue());
							}
							Cell R5cell2 = row5.getCell(9);
							if (R5cell2 != null) {
								R5cell2.setCellValue(BRF001.getR5_amount_aed_non_resident() == null ? 0
										: BRF001.getR5_amount_aed_non_resident().intValue());
							}
							Cell R5cell3 = row5.getCell(11);
							if (R5cell3 != null) {
								R5cell3.setCellValue(BRF001.getR5_amount_fcy_non_resident() == null ? 0
										: BRF001.getR5_amount_fcy_non_resident().intValue());
							}
							///// srl_no -17/////////

							Row row7 = sheet.getRow(16);
							Cell R7cell = row7.getCell(5);
							if (R7cell != null) {
								R7cell.setCellValue(BRF001.getR7_amount_aed_resident() == null ? 0
										: BRF001.getR7_amount_aed_resident().intValue());
							}
							Cell R7cell1 = row7.getCell(7);
							if (R7cell1 != null) {
								R7cell1.setCellValue(BRF001.getR7_amount_fcy_resident() == null ? 0
										: BRF001.getR7_amount_fcy_resident().intValue());
							}
							Cell R7cell2 = row7.getCell(9);
							if (R7cell2 != null) {
								R7cell2.setCellValue(BRF001.getR7_amount_aed_non_resident() == null ? 0
										: BRF001.getR7_amount_aed_non_resident().intValue());
							}
							Cell R7cell3 = row7.getCell(11);
							if (R7cell3 != null) {
								R7cell3.setCellValue(BRF001.getR7_amount_fcy_non_resident() == null ? 0
										: BRF001.getR7_amount_fcy_non_resident().intValue());
							}

							///// srl_no -18/////////

							Row row8 = sheet.getRow(17);
							Cell R8cell = row8.getCell(5);
							if (R8cell != null) {
								R8cell.setCellValue(BRF001.getR8_amount_aed_resident() == null ? 0
										: BRF001.getR8_amount_aed_resident().intValue());
							}
							Cell R8cell1 = row8.getCell(7);
							if (R8cell1 != null) {
								R8cell1.setCellValue(BRF001.getR8_amount_fcy_resident() == null ? 0
										: BRF001.getR8_amount_fcy_resident().intValue());
							}
							Cell R8cell2 = row8.getCell(9);
							if (R8cell2 != null) {
								R8cell2.setCellValue(BRF001.getR8_amount_aed_non_resident() == null ? 0
										: BRF001.getR8_amount_aed_non_resident().intValue());
							}
							Cell R8cell3 = row8.getCell(11);
							if (R8cell3 != null) {
								R8cell3.setCellValue(BRF001.getR8_amount_fcy_non_resident() == null ? 0
										: BRF001.getR8_amount_fcy_non_resident().intValue());
							}

							///// srl_no -20/////////

							Row row10 = sheet.getRow(19);
							Cell R10cell = row10.getCell(5);
							if (R10cell != null) {
								R10cell.setCellValue(BRF001.getR10_amount_aed_resident() == null ? 0
										: BRF001.getR10_amount_aed_resident().intValue());
							}
							Cell R10cell1 = row10.getCell(7);
							if (R10cell1 != null) {
								R10cell1.setCellValue(BRF001.getR10_amount_fcy_resident() == null ? 0
										: BRF001.getR10_amount_fcy_resident().intValue());
							}
							Cell R10cell2 = row10.getCell(9);
							if (R10cell2 != null) {
								R10cell2.setCellValue(BRF001.getR10_amount_aed_non_resident() == null ? 0
										: BRF001.getR10_amount_aed_non_resident().intValue());
							}
							Cell R10cell3 = row10.getCell(11);
							if (R10cell3 != null) {
								R10cell3.setCellValue(BRF001.getR10_amount_fcy_non_resident() == null ? 0
										: BRF001.getR10_amount_fcy_non_resident().intValue());
							}

							///// srl_no -21/////////

							Row row11 = sheet.getRow(20);
							Cell R11cell = row11.getCell(5);
							if (R11cell != null) {
								R11cell.setCellValue(BRF001.getR11_amount_aed_resident() == null ? 0
										: BRF001.getR11_amount_aed_resident().intValue());
							}
							Cell R11cell1 = row11.getCell(7);
							if (R11cell1 != null) {
								R11cell1.setCellValue(BRF001.getR11_amount_fcy_resident() == null ? 0
										: BRF001.getR11_amount_fcy_resident().intValue());
							}
							Cell R11cell2 = row11.getCell(9);
							if (R11cell2 != null) {
								R11cell2.setCellValue(BRF001.getR11_amount_aed_non_resident() == null ? 0
										: BRF001.getR11_amount_aed_non_resident().intValue());
							}
							Cell R11cell3 = row11.getCell(11);
							if (R11cell3 != null) {
								R11cell3.setCellValue(BRF001.getR11_amount_fcy_non_resident() == null ? 0
										: BRF001.getR11_amount_fcy_non_resident().intValue());
							}

/////srl_no -22/////////

							Row row12 = sheet.getRow(21);
							Cell R12cell = row12.getCell(5);
							if (R12cell != null) {
								R12cell.setCellValue(BRF001.getR12_amount_aed_resident() == null ? 0
										: BRF001.getR12_amount_aed_resident().intValue());
							}
							Cell R12cell1 = row12.getCell(7);
							if (R12cell1 != null) {
								R12cell1.setCellValue(BRF001.getR12_amount_fcy_resident() == null ? 0
										: BRF001.getR12_amount_fcy_resident().intValue());
							}
							Cell R12cell2 = row12.getCell(9);
							if (R12cell2 != null) {
								R12cell2.setCellValue(BRF001.getR12_amount_aed_non_resident() == null ? 0
										: BRF001.getR12_amount_aed_non_resident().intValue());
							}
							Cell R12cell3 = row12.getCell(11);
							if (R12cell3 != null) {
								R12cell3.setCellValue(BRF001.getR12_amount_fcy_non_resident() == null ? 0
										: BRF001.getR12_amount_fcy_non_resident().intValue());
							}

/////srl_no -24/////////

							Row row14 = sheet.getRow(23);
							Cell R14cell = row14.getCell(5);
							if (R14cell != null) {
								R14cell.setCellValue(BRF001.getR14_amount_aed_resident() == null ? 0
										: BRF001.getR14_amount_aed_resident().intValue());
							}
							Cell R14cell1 = row14.getCell(7);
							if (R14cell1 != null) {
								R14cell1.setCellValue(BRF001.getR14_amount_fcy_resident() == null ? 0
										: BRF001.getR14_amount_fcy_resident().intValue());
							}
							Cell R14cell2 = row14.getCell(9);
							if (R14cell2 != null) {
								R14cell2.setCellValue(BRF001.getR14_amount_aed_non_resident() == null ? 0
										: BRF001.getR14_amount_aed_non_resident().intValue());
							}
							Cell R14cell3 = row14.getCell(11);
							if (R14cell3 != null) {
								R14cell3.setCellValue(BRF001.getR14_amount_fcy_non_resident() == null ? 0
										: BRF001.getR14_amount_fcy_non_resident().intValue());
							}

/////srl_no -25/////////

							Row row15 = sheet.getRow(24);
							Cell R15cell = row15.getCell(5);
							if (R15cell != null) {
								R15cell.setCellValue(BRF001.getR15_amount_aed_resident() == null ? 0
										: BRF001.getR15_amount_aed_resident().intValue());
							}
							Cell R15cell1 = row15.getCell(7);
							if (R15cell1 != null) {
								R15cell1.setCellValue(BRF001.getR15_amount_fcy_resident() == null ? 0
										: BRF001.getR15_amount_fcy_resident().intValue());
							}
							Cell R15cell2 = row15.getCell(9);
							if (R15cell2 != null) {
								R15cell2.setCellValue(BRF001.getR15_amount_aed_non_resident() == null ? 0
										: BRF001.getR15_amount_aed_non_resident().intValue());
							}
							Cell R15cell3 = row15.getCell(11);
							if (R15cell3 != null) {
								R15cell3.setCellValue(BRF001.getR15_amount_fcy_non_resident() == null ? 0
										: BRF001.getR15_amount_fcy_non_resident().intValue());
							}

							///// srl_no -26/////////

							Row row16 = sheet.getRow(25);
							Cell R16cell = row16.getCell(5);
							if (R16cell != null) {
								R16cell.setCellValue(BRF001.getR16_amount_aed_resident() == null ? 0
										: BRF001.getR16_amount_aed_resident().intValue());
							}
							Cell R16cell1 = row16.getCell(7);
							if (R16cell1 != null) {
								R16cell1.setCellValue(BRF001.getR16_amount_fcy_resident() == null ? 0
										: BRF001.getR16_amount_fcy_resident().intValue());
							}
							Cell R16cell2 = row16.getCell(9);
							if (R16cell2 != null) {
								R16cell2.setCellValue(BRF001.getR16_amount_aed_non_resident() == null ? 0
										: BRF001.getR16_amount_aed_non_resident().intValue());
							}
							Cell R16cell3 = row16.getCell(11);
							if (R16cell3 != null) {
								R16cell3.setCellValue(BRF001.getR16_amount_fcy_non_resident() == null ? 0
										: BRF001.getR16_amount_fcy_non_resident().intValue());
							}

/////srl_no -27/////////

							Row row17 = sheet.getRow(26);
							Cell R17cell = row17.getCell(5);
							if (R17cell != null) {
								R17cell.setCellValue(BRF001.getR17_amount_aed_resident() == null ? 0
										: BRF001.getR17_amount_aed_resident().intValue());
							}
							Cell R17cell1 = row17.getCell(7);
							if (R17cell1 != null) {
								R17cell1.setCellValue(BRF001.getR17_amount_fcy_resident() == null ? 0
										: BRF001.getR17_amount_fcy_resident().intValue());
							}
							Cell R17cell2 = row17.getCell(9);
							if (R17cell2 != null) {
								R17cell2.setCellValue(BRF001.getR17_amount_aed_non_resident() == null ? 0
										: BRF001.getR17_amount_aed_non_resident().intValue());
							}
							Cell R17cell3 = row17.getCell(11);
							if (R17cell3 != null) {
								R17cell3.setCellValue(BRF001.getR17_amount_fcy_non_resident() == null ? 0
										: BRF001.getR17_amount_fcy_non_resident().intValue());
							}

/////srl_no -28/////////

							Row row18 = sheet.getRow(27);
							Cell R18cell = row18.getCell(5);
							if (R18cell != null) {
								R18cell.setCellValue(BRF001.getR18_amount_aed_resident() == null ? 0
										: BRF001.getR18_amount_aed_resident().intValue());
							}
							Cell R18cell1 = row18.getCell(7);
							if (R18cell1 != null) {
								R18cell1.setCellValue(BRF001.getR18_amount_fcy_resident() == null ? 0
										: BRF001.getR18_amount_fcy_resident().intValue());
							}
							Cell R18cell2 = row18.getCell(9);
							if (R18cell2 != null) {
								R18cell2.setCellValue(BRF001.getR18_amount_aed_non_resident() == null ? 0
										: BRF001.getR18_amount_aed_non_resident().intValue());
							}
							Cell R18cell3 = row18.getCell(11);
							if (R18cell3 != null) {
								R18cell3.setCellValue(BRF001.getR18_amount_fcy_non_resident() == null ? 0
										: BRF001.getR18_amount_fcy_non_resident().intValue());
							}

/////srl_no -29/////////

							Row row19 = sheet.getRow(28);
							Cell R19cell = row19.getCell(5);
							if (R19cell != null) {
								R19cell.setCellValue(BRF001.getR19_amount_aed_resident() == null ? 0
										: BRF001.getR19_amount_aed_resident().intValue());
							}
							Cell R19cell1 = row19.getCell(7);
							if (R19cell1 != null) {
								R19cell1.setCellValue(BRF001.getR19_amount_fcy_resident() == null ? 0
										: BRF001.getR19_amount_fcy_resident().intValue());
							}
							Cell R19cell2 = row19.getCell(9);
							if (R19cell2 != null) {
								R19cell2.setCellValue(BRF001.getR19_amount_aed_non_resident() == null ? 0
										: BRF001.getR19_amount_aed_non_resident().intValue());
							}
							Cell R19cell3 = row19.getCell(11);
							if (R19cell3 != null) {
								R19cell3.setCellValue(BRF001.getR19_amount_fcy_non_resident() == null ? 0
										: BRF001.getR19_amount_fcy_non_resident().intValue());
							}

/////srl_no -31/////////

							Row row21 = sheet.getRow(30);
							Cell R21cell = row21.getCell(5);
							if (R21cell != null) {
								R21cell.setCellValue(BRF001.getR21_amount_aed_resident() == null ? 0
										: BRF001.getR21_amount_aed_resident().intValue());
							}
							Cell R21cell1 = row21.getCell(7);
							if (R21cell1 != null) {
								R21cell1.setCellValue(BRF001.getR21_amount_fcy_resident() == null ? 0
										: BRF001.getR21_amount_fcy_resident().intValue());
							}
							Cell R21cell2 = row21.getCell(9);
							if (R21cell2 != null) {
								R21cell2.setCellValue(BRF001.getR21_amount_aed_non_resident() == null ? 0
										: BRF001.getR21_amount_aed_non_resident().intValue());
							}
							Cell R21cell3 = row21.getCell(11);
							if (R21cell3 != null) {
								R21cell3.setCellValue(BRF001.getR21_amount_fcy_non_resident() == null ? 0
										: BRF001.getR21_amount_fcy_non_resident().intValue());
							}

/////srl_no -32/////////

							Row row22 = sheet.getRow(31);
							Cell R22cell = row22.getCell(5);
							if (R22cell != null) {
								R22cell.setCellValue(BRF001.getR22_amount_aed_resident() == null ? 0
										: BRF001.getR22_amount_aed_resident().intValue());
							}
							Cell R22cell1 = row22.getCell(7);
							if (R22cell1 != null) {
								R22cell1.setCellValue(BRF001.getR22_amount_fcy_resident() == null ? 0
										: BRF001.getR22_amount_fcy_resident().intValue());
							}
							Cell R22cell2 = row22.getCell(9);
							if (R22cell2 != null) {
								R22cell2.setCellValue(BRF001.getR22_amount_aed_non_resident() == null ? 0
										: BRF001.getR22_amount_aed_non_resident().intValue());
							}
							Cell R22cell3 = row22.getCell(11);
							if (R22cell3 != null) {
								R22cell3.setCellValue(BRF001.getR22_amount_fcy_non_resident() == null ? 0
										: BRF001.getR22_amount_fcy_non_resident().intValue());
							}

/////srl_no -33/////////

							Row row23 = sheet.getRow(32);
							Cell R23cell = row23.getCell(5);
							if (R23cell != null) {
								R23cell.setCellValue(BRF001.getR23_amount_aed_resident() == null ? 0
										: BRF001.getR23_amount_aed_resident().intValue());
							}
							Cell R23cell1 = row23.getCell(7);
							if (R23cell1 != null) {
								R23cell1.setCellValue(BRF001.getR23_amount_fcy_resident() == null ? 0
										: BRF001.getR23_amount_fcy_resident().intValue());
							}
							Cell R23cell2 = row23.getCell(9);
							if (R23cell2 != null) {
								R23cell2.setCellValue(BRF001.getR23_amount_aed_non_resident() == null ? 0
										: BRF001.getR23_amount_aed_non_resident().intValue());
							}
							Cell R23cell3 = row23.getCell(11);
							if (R23cell3 != null) {
								R23cell3.setCellValue(BRF001.getR23_amount_fcy_non_resident() == null ? 0
										: BRF001.getR23_amount_fcy_non_resident().intValue());
							}

/////srl_no -36/////////

							Row row26 = sheet.getRow(35);
							Cell R26cell = row26.getCell(5);
							if (R26cell != null) {
								R26cell.setCellValue(BRF001.getR26_amount_aed_resident() == null ? 0
										: BRF001.getR26_amount_aed_resident().intValue());
							}
							Cell R26cell1 = row26.getCell(7);
							if (R26cell1 != null) {
								R26cell1.setCellValue(BRF001.getR26_amount_fcy_resident() == null ? 0
										: BRF001.getR26_amount_fcy_resident().intValue());
							}
							Cell R26cell2 = row26.getCell(9);
							if (R26cell2 != null) {
								R26cell2.setCellValue(BRF001.getR26_amount_aed_non_resident() == null ? 0
										: BRF001.getR26_amount_aed_non_resident().intValue());
							}
							Cell R26cell3 = row26.getCell(11);
							if (R26cell3 != null) {
								R26cell3.setCellValue(BRF001.getR26_amount_fcy_non_resident() == null ? 0
										: BRF001.getR26_amount_fcy_non_resident().intValue());
							}

/////srl_no -37/////////

							Row row27 = sheet.getRow(36);
							Cell R27cell = row27.getCell(5);
							if (R27cell != null) {
								R27cell.setCellValue(BRF001.getR27_amount_aed_resident() == null ? 0
										: BRF001.getR27_amount_aed_resident().intValue());
							}
							Cell R27cell1 = row27.getCell(7);
							if (R27cell1 != null) {
								R27cell1.setCellValue(BRF001.getR27_amount_fcy_resident() == null ? 0
										: BRF001.getR27_amount_fcy_resident().intValue());
							}
							Cell R27cell2 = row27.getCell(9);
							if (R27cell2 != null) {
								R27cell2.setCellValue(BRF001.getR27_amount_aed_non_resident() == null ? 0
										: BRF001.getR27_amount_aed_non_resident().intValue());
							}
							Cell R27cell3 = row27.getCell(11);
							if (R27cell3 != null) {
								R27cell3.setCellValue(BRF001.getR27_amount_fcy_non_resident() == null ? 0
										: BRF001.getR27_amount_fcy_non_resident().intValue());
							}

/////srl_no -38/////////

							Row row28 = sheet.getRow(37);
							Cell R28cell = row28.getCell(5);
							if (R28cell != null) {
								R28cell.setCellValue(BRF001.getR28_amount_aed_resident() == null ? 0
										: BRF001.getR28_amount_aed_resident().intValue());
							}
							Cell R28cell1 = row28.getCell(7);
							if (R28cell1 != null) {
								R28cell1.setCellValue(BRF001.getR28_amount_fcy_resident() == null ? 0
										: BRF001.getR28_amount_fcy_resident().intValue());
							}
							Cell R28cell2 = row28.getCell(9);
							if (R28cell2 != null) {
								R28cell2.setCellValue(BRF001.getR28_amount_aed_non_resident() == null ? 0
										: BRF001.getR28_amount_aed_non_resident().intValue());
							}
							Cell R28cell3 = row28.getCell(11);
							if (R28cell3 != null) {
								R28cell3.setCellValue(BRF001.getR28_amount_fcy_non_resident() == null ? 0
										: BRF001.getR28_amount_fcy_non_resident().intValue());
							}

/////srl_no -41/////////

							Row row31 = sheet.getRow(40);
							Cell R31cell = row31.getCell(5);
							if (R31cell != null) {
								R31cell.setCellValue(BRF001.getR31_amount_aed_resident() == null ? 0
										: BRF001.getR31_amount_aed_resident().intValue());
							}
							Cell R31cell1 = row31.getCell(7);
							if (R31cell1 != null) {
								R31cell1.setCellValue(BRF001.getR31_amount_fcy_resident() == null ? 0
										: BRF001.getR31_amount_fcy_resident().intValue());
							}
							Cell R31cell2 = row31.getCell(9);
							if (R31cell2 != null) {
								R31cell2.setCellValue(BRF001.getR31_amount_aed_non_resident() == null ? 0
										: BRF001.getR31_amount_aed_non_resident().intValue());
							}
							Cell R31cell3 = row31.getCell(11);
							if (R31cell3 != null) {
								R31cell3.setCellValue(BRF001.getR31_amount_fcy_non_resident() == null ? 0
										: BRF001.getR31_amount_fcy_non_resident().intValue());
							}

/////srl_no -42/////////

							Row row32 = sheet.getRow(41);
							Cell R32cell = row32.getCell(5);
							if (R32cell != null) {
								R32cell.setCellValue(BRF001.getR32_amount_aed_resident() == null ? 0
										: BRF001.getR32_amount_aed_resident().intValue());
							}
							Cell R32cell1 = row32.getCell(7);
							if (R32cell1 != null) {
								R32cell1.setCellValue(BRF001.getR32_amount_fcy_resident() == null ? 0
										: BRF001.getR32_amount_fcy_resident().intValue());
							}
							Cell R32cell2 = row32.getCell(9);
							if (R32cell2 != null) {
								R32cell2.setCellValue(BRF001.getR32_amount_aed_non_resident() == null ? 0
										: BRF001.getR32_amount_aed_non_resident().intValue());
							}
							Cell R32cell3 = row32.getCell(11);
							if (R32cell3 != null) {
								R32cell3.setCellValue(BRF001.getR32_amount_fcy_non_resident() == null ? 0
										: BRF001.getR32_amount_fcy_non_resident().intValue());
							}

/////srl_no -43/////////

							Row row33 = sheet.getRow(42);
							Cell R33cell = row33.getCell(5);
							if (R33cell != null) {
								R33cell.setCellValue(BRF001.getR33_amount_aed_resident() == null ? 0
										: BRF001.getR33_amount_aed_resident().intValue());
							}
							Cell R33cell1 = row33.getCell(7);
							if (R33cell1 != null) {
								R33cell1.setCellValue(BRF001.getR33_amount_fcy_resident() == null ? 0
										: BRF001.getR33_amount_fcy_resident().intValue());
							}
							Cell R33cell2 = row33.getCell(9);
							if (R33cell2 != null) {
								R33cell2.setCellValue(BRF001.getR33_amount_aed_non_resident() == null ? 0
										: BRF001.getR33_amount_aed_non_resident().intValue());
							}
							Cell R33cell3 = row33.getCell(11);
							if (R33cell3 != null) {
								R33cell3.setCellValue(BRF001.getR33_amount_fcy_non_resident() == null ? 0
										: BRF001.getR33_amount_fcy_non_resident().intValue());
							}

/////srl_no -45/////////

							Row row35 = sheet.getRow(44);
							Cell R35cell = row35.getCell(5);
							if (R35cell != null) {
								R35cell.setCellValue(BRF001.getR35_amount_aed_resident() == null ? 0
										: BRF001.getR35_amount_aed_resident().intValue());
							}
							Cell R35cell1 = row35.getCell(7);
							if (R35cell1 != null) {
								R35cell1.setCellValue(BRF001.getR35_amount_fcy_resident() == null ? 0
										: BRF001.getR35_amount_fcy_resident().intValue());
							}
							Cell R35cell2 = row35.getCell(9);
							if (R35cell2 != null) {
								R35cell2.setCellValue(BRF001.getR35_amount_aed_non_resident() == null ? 0
										: BRF001.getR35_amount_aed_non_resident().intValue());
							}
							Cell R35cell3 = row35.getCell(11);
							if (R35cell3 != null) {
								R35cell3.setCellValue(BRF001.getR35_amount_fcy_non_resident() == null ? 0
										: BRF001.getR35_amount_fcy_non_resident().intValue());
							}

/////srl_no -46/////////

							Row row36 = sheet.getRow(45);
							Cell R36cell = row36.getCell(5);
							if (R36cell != null) {
								R36cell.setCellValue(BRF001.getR36_amount_aed_resident() == null ? 0
										: BRF001.getR36_amount_aed_resident().intValue());
							}
							Cell R36cell1 = row36.getCell(7);
							if (R36cell1 != null) {
								R36cell1.setCellValue(BRF001.getR36_amount_fcy_resident() == null ? 0
										: BRF001.getR36_amount_fcy_resident().intValue());
							}
							Cell R36cell2 = row36.getCell(9);
							if (R36cell2 != null) {
								R36cell2.setCellValue(BRF001.getR36_amount_aed_non_resident() == null ? 0
										: BRF001.getR36_amount_aed_non_resident().intValue());
							}
							Cell R36cell3 = row36.getCell(11);
							if (R36cell3 != null) {
								R36cell3.setCellValue(BRF001.getR36_amount_fcy_non_resident() == null ? 0
										: BRF001.getR36_amount_fcy_non_resident().intValue());
							}

/////srl_no -49/////////

							Row row39 = sheet.getRow(48);
							Cell R39cell = row39.getCell(4);
							if (R39cell != null) {
								R39cell.setCellValue(BRF001.getR39_no_acct_aed_resident() == null ? 0
										: BRF001.getR39_no_acct_aed_resident().intValue());
							}
							Cell R39cell1 = row39.getCell(5);
							if (R39cell1 != null) {
								R39cell1.setCellValue(BRF001.getR39_amount_aed_resident() == null ? 0
										: BRF001.getR39_amount_aed_resident().intValue());
							}
							Cell R39cell2 = row39.getCell(6);
							if (R39cell2 != null) {
								R39cell2.setCellValue(BRF001.getR39_no_acct_fcy_resident() == null ? 0
										: BRF001.getR39_no_acct_fcy_resident().intValue());
							}
							Cell R39cell3 = row39.getCell(7);
							if (R39cell3 != null) {
								R39cell3.setCellValue(BRF001.getR39_amount_fcy_resident() == null ? 0
										: BRF001.getR39_amount_fcy_resident().intValue());
							}
							Cell R39cell4 = row39.getCell(8);
							if (R39cell4 != null) {
								R39cell4.setCellValue(BRF001.getR39_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR39_no_acct_aed_non_resident().intValue());
							}
							Cell R39cell5 = row39.getCell(9);
							if (R39cell5 != null) {
								R39cell5.setCellValue(BRF001.getR39_amount_aed_non_resident() == null ? 0
										: BRF001.getR39_amount_aed_non_resident().intValue());
							}
							Cell R39cell6 = row39.getCell(10);
							if (R39cell6 != null) {
								R39cell6.setCellValue(BRF001.getR39_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR39_no_acct_fcy_non_resident().intValue());
							}
							Cell R39cell7 = row39.getCell(11);
							if (R39cell7 != null) {
								R39cell7.setCellValue(BRF001.getR39_amount_fcy_non_resident() == null ? 0
										: BRF001.getR39_amount_fcy_non_resident().intValue());
							}

/////srl_no -50/////////

							Row row40 = sheet.getRow(49);
							Cell R40cell = row40.getCell(4);
							if (R40cell != null) {
								R40cell.setCellValue(BRF001.getR40_no_acct_aed_resident() == null ? 0
										: BRF001.getR40_no_acct_aed_resident().intValue());
							}
							Cell R40cell1 = row40.getCell(5);
							if (R40cell1 != null) {
								R40cell1.setCellValue(BRF001.getR40_amount_aed_resident() == null ? 0
										: BRF001.getR40_amount_aed_resident().intValue());
							}
							Cell R40cell2 = row40.getCell(6);
							if (R40cell2 != null) {
								R40cell2.setCellValue(BRF001.getR40_no_acct_fcy_resident() == null ? 0
										: BRF001.getR40_no_acct_fcy_resident().intValue());
							}
							Cell R40cell3 = row40.getCell(7);
							if (R40cell3 != null) {
								R40cell3.setCellValue(BRF001.getR40_amount_fcy_resident() == null ? 0
										: BRF001.getR40_amount_fcy_resident().intValue());
							}
							Cell R40cell4 = row40.getCell(8);
							if (R40cell4 != null) {
								R40cell4.setCellValue(BRF001.getR40_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR40_no_acct_aed_non_resident().intValue());
							}
							Cell R40cell5 = row40.getCell(9);
							if (R40cell5 != null) {
								R40cell5.setCellValue(BRF001.getR40_amount_aed_non_resident() == null ? 0
										: BRF001.getR40_amount_aed_non_resident().intValue());
							}
							Cell R40cell6 = row40.getCell(10);
							if (R40cell6 != null) {
								R40cell6.setCellValue(BRF001.getR40_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR40_no_acct_fcy_non_resident().intValue());
							}
							Cell R40cell7 = row40.getCell(11);
							if (R40cell7 != null) {
								R40cell7.setCellValue(BRF001.getR40_amount_fcy_non_resident() == null ? 0
										: BRF001.getR40_amount_fcy_non_resident().intValue());
							}

/////srl_no -52/////////

							Row row42 = sheet.getRow(51);
							Cell R42cell = row42.getCell(4);
							if (R42cell != null) {
								R42cell.setCellValue(BRF001.getR42_no_acct_aed_resident() == null ? 0
										: BRF001.getR42_no_acct_aed_resident().intValue());
							}
							Cell R42cell1 = row42.getCell(5);
							if (R42cell1 != null) {
								R42cell1.setCellValue(BRF001.getR42_amount_aed_resident() == null ? 0
										: BRF001.getR42_amount_aed_resident().intValue());
							}
							Cell R42cell2 = row42.getCell(6);
							if (R42cell2 != null) {
								R42cell2.setCellValue(BRF001.getR42_no_acct_fcy_resident() == null ? 0
										: BRF001.getR42_no_acct_fcy_resident().intValue());
							}
							Cell R42cell3 = row42.getCell(7);
							if (R42cell3 != null) {
								R42cell3.setCellValue(BRF001.getR42_amount_fcy_resident() == null ? 0
										: BRF001.getR42_amount_fcy_resident().intValue());
							}
							Cell R42cell4 = row42.getCell(8);
							if (R42cell4 != null) {
								R42cell4.setCellValue(BRF001.getR42_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR42_no_acct_aed_non_resident().intValue());
							}
							Cell R42cell5 = row42.getCell(9);
							if (R42cell5 != null) {
								R42cell5.setCellValue(BRF001.getR42_amount_aed_non_resident() == null ? 0
										: BRF001.getR42_amount_aed_non_resident().intValue());
							}
							Cell R42cell6 = row42.getCell(10);
							if (R42cell6 != null) {
								R42cell6.setCellValue(BRF001.getR42_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR42_no_acct_fcy_non_resident().intValue());
							}
							Cell R42cell7 = row42.getCell(11);
							if (R42cell7 != null) {
								R42cell7.setCellValue(BRF001.getR42_amount_fcy_non_resident() == null ? 0
										: BRF001.getR42_amount_fcy_non_resident().intValue());
							}

/////srl_no -53/////////

							Row row43 = sheet.getRow(52);
							Cell R43cell = row43.getCell(4);
							if (R43cell != null) {
								R43cell.setCellValue(BRF001.getR43_no_acct_aed_resident() == null ? 0
										: BRF001.getR43_no_acct_aed_resident().intValue());
							}
							Cell R43cell1 = row43.getCell(5);
							if (R43cell1 != null) {
								R43cell1.setCellValue(BRF001.getR43_amount_aed_resident() == null ? 0
										: BRF001.getR43_amount_aed_resident().intValue());
							}
							Cell R43cell2 = row43.getCell(6);
							if (R43cell2 != null) {
								R43cell2.setCellValue(BRF001.getR43_no_acct_fcy_resident() == null ? 0
										: BRF001.getR43_no_acct_fcy_resident().intValue());
							}
							Cell R43cell3 = row43.getCell(7);
							if (R43cell3 != null) {
								R43cell3.setCellValue(BRF001.getR43_amount_fcy_resident() == null ? 0
										: BRF001.getR43_amount_fcy_resident().intValue());
							}
							Cell R43cell4 = row43.getCell(8);
							if (R43cell4 != null) {
								R43cell4.setCellValue(BRF001.getR43_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR43_no_acct_aed_non_resident().intValue());
							}
							Cell R43cell5 = row43.getCell(9);
							if (R43cell5 != null) {
								R43cell5.setCellValue(BRF001.getR43_amount_aed_non_resident() == null ? 0
										: BRF001.getR43_amount_aed_non_resident().intValue());
							}
							Cell R43cell6 = row43.getCell(10);
							if (R43cell6 != null) {
								R43cell6.setCellValue(BRF001.getR43_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR43_no_acct_fcy_non_resident().intValue());
							}
							Cell R43cell7 = row43.getCell(11);
							if (R43cell7 != null) {
								R43cell7.setCellValue(BRF001.getR43_amount_fcy_non_resident() == null ? 0
										: BRF001.getR43_amount_fcy_non_resident().intValue());
							}

/////srl_no -56/////////

							Row row46 = sheet.getRow(55);
							Cell R46cell = row46.getCell(4);
							if (R46cell != null) {
								R46cell.setCellValue(BRF001.getR46_no_acct_aed_resident() == null ? 0
										: BRF001.getR46_no_acct_aed_resident().intValue());
							}
							Cell R46cell1 = row46.getCell(5);
							if (R46cell1 != null) {
								R46cell1.setCellValue(BRF001.getR46_amount_aed_resident() == null ? 0
										: BRF001.getR46_amount_aed_resident().intValue());
							}
							Cell R46cell2 = row46.getCell(6);
							if (R46cell2 != null) {
								R46cell2.setCellValue(BRF001.getR46_no_acct_fcy_resident() == null ? 0
										: BRF001.getR46_no_acct_fcy_resident().intValue());
							}
							Cell R46cell3 = row46.getCell(7);
							if (R46cell3 != null) {
								R46cell3.setCellValue(BRF001.getR46_amount_fcy_resident() == null ? 0
										: BRF001.getR46_amount_fcy_resident().intValue());
							}
							Cell R46cell4 = row46.getCell(8);
							if (R46cell4 != null) {
								R46cell4.setCellValue(BRF001.getR46_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR46_no_acct_aed_non_resident().intValue());
							}
							Cell R46cell5 = row46.getCell(9);
							if (R46cell5 != null) {
								R46cell5.setCellValue(BRF001.getR46_amount_aed_non_resident() == null ? 0
										: BRF001.getR46_amount_aed_non_resident().intValue());
							}
							Cell R46cell6 = row46.getCell(10);
							if (R46cell6 != null) {
								R46cell6.setCellValue(BRF001.getR46_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR46_no_acct_fcy_non_resident().intValue());
							}
							Cell R46cell7 = row46.getCell(11);
							if (R46cell7 != null) {
								R46cell7.setCellValue(BRF001.getR46_amount_fcy_non_resident() == null ? 0
										: BRF001.getR46_amount_fcy_non_resident().intValue());
							}

/////srl_no -57/////////

							Row row47 = sheet.getRow(56);
							Cell R47cell = row47.getCell(4);
							if (R47cell != null) {
								R47cell.setCellValue(BRF001.getR47_no_acct_aed_resident() == null ? 0
										: BRF001.getR47_no_acct_aed_resident().intValue());
							}
							Cell R47cell1 = row47.getCell(5);
							if (R47cell1 != null) {
								R47cell1.setCellValue(BRF001.getR47_amount_aed_resident() == null ? 0
										: BRF001.getR47_amount_aed_resident().intValue());
							}
							Cell R47cell2 = row47.getCell(6);
							if (R47cell2 != null) {
								R47cell2.setCellValue(BRF001.getR47_no_acct_fcy_resident() == null ? 0
										: BRF001.getR47_no_acct_fcy_resident().intValue());
							}
							Cell R47cell3 = row47.getCell(7);
							if (R47cell3 != null) {
								R47cell3.setCellValue(BRF001.getR47_amount_fcy_resident() == null ? 0
										: BRF001.getR47_amount_fcy_resident().intValue());
							}
							Cell R47cell4 = row47.getCell(8);
							if (R47cell4 != null) {
								R47cell4.setCellValue(BRF001.getR47_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR47_no_acct_aed_non_resident().intValue());
							}
							Cell R47cell5 = row47.getCell(9);
							if (R47cell5 != null) {
								R47cell5.setCellValue(BRF001.getR47_amount_aed_non_resident() == null ? 0
										: BRF001.getR47_amount_aed_non_resident().intValue());
							}
							Cell R47cell6 = row47.getCell(10);
							if (R47cell6 != null) {
								R47cell6.setCellValue(BRF001.getR47_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR47_no_acct_fcy_non_resident().intValue());
							}
							Cell R47cell7 = row47.getCell(11);
							if (R47cell7 != null) {
								R47cell7.setCellValue(BRF001.getR47_amount_fcy_non_resident() == null ? 0
										: BRF001.getR47_amount_fcy_non_resident().intValue());
							}

/////srl_no -58/////////

							Row row48 = sheet.getRow(57);
							Cell R48cell = row48.getCell(4);
							if (R48cell != null) {
								R48cell.setCellValue(BRF001.getR48_no_acct_aed_resident() == null ? 0
										: BRF001.getR48_no_acct_aed_resident().intValue());
							}
							Cell R48cell1 = row48.getCell(5);
							if (R48cell1 != null) {
								R48cell1.setCellValue(BRF001.getR48_amount_aed_resident() == null ? 0
										: BRF001.getR48_amount_aed_resident().intValue());
							}
							Cell R48cell2 = row48.getCell(6);
							if (R48cell2 != null) {
								R48cell2.setCellValue(BRF001.getR48_no_acct_fcy_resident() == null ? 0
										: BRF001.getR48_no_acct_fcy_resident().intValue());
							}
							Cell R48cell3 = row48.getCell(7);
							if (R48cell3 != null) {
								R48cell3.setCellValue(BRF001.getR48_amount_fcy_resident() == null ? 0
										: BRF001.getR48_amount_fcy_resident().intValue());
							}
							Cell R48cell4 = row48.getCell(8);
							if (R48cell4 != null) {
								R48cell4.setCellValue(BRF001.getR48_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR48_no_acct_aed_non_resident().intValue());
							}
							Cell R48cell5 = row48.getCell(9);
							if (R48cell5 != null) {
								R48cell5.setCellValue(BRF001.getR48_amount_aed_non_resident() == null ? 0
										: BRF001.getR48_amount_aed_non_resident().intValue());
							}
							Cell R48cell6 = row48.getCell(10);
							if (R48cell6 != null) {
								R48cell6.setCellValue(BRF001.getR48_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR48_no_acct_fcy_non_resident().intValue());
							}
							Cell R48cell7 = row48.getCell(11);
							if (R48cell7 != null) {
								R48cell7.setCellValue(BRF001.getR48_amount_fcy_non_resident() == null ? 0
										: BRF001.getR48_amount_fcy_non_resident().intValue());
							}

/////srl_no -59/////////

							Row row49 = sheet.getRow(58);
							Cell R49cell = row49.getCell(4);
							if (R49cell != null) {
								R49cell.setCellValue(BRF001.getR49_no_acct_aed_resident() == null ? 0
										: BRF001.getR49_no_acct_aed_resident().intValue());
							}
							Cell R49cell1 = row49.getCell(5);
							if (R49cell1 != null) {
								R49cell1.setCellValue(BRF001.getR49_amount_aed_resident() == null ? 0
										: BRF001.getR49_amount_aed_resident().intValue());
							}
							Cell R49cell2 = row49.getCell(6);
							if (R49cell2 != null) {
								R49cell2.setCellValue(BRF001.getR49_no_acct_fcy_resident() == null ? 0
										: BRF001.getR49_no_acct_fcy_resident().intValue());
							}
							Cell R49cell3 = row49.getCell(7);
							if (R49cell3 != null) {
								R49cell3.setCellValue(BRF001.getR49_amount_fcy_resident() == null ? 0
										: BRF001.getR49_amount_fcy_resident().intValue());
							}
							Cell R49cell4 = row49.getCell(8);
							if (R49cell4 != null) {
								R49cell4.setCellValue(BRF001.getR49_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR49_no_acct_aed_non_resident().intValue());
							}
							Cell R49cell5 = row49.getCell(9);
							if (R49cell5 != null) {
								R49cell5.setCellValue(BRF001.getR49_amount_aed_non_resident() == null ? 0
										: BRF001.getR49_amount_aed_non_resident().intValue());
							}
							Cell R49cell6 = row49.getCell(10);
							if (R49cell6 != null) {
								R49cell6.setCellValue(BRF001.getR49_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR49_no_acct_fcy_non_resident().intValue());
							}
							Cell R49cell7 = row49.getCell(11);
							if (R49cell7 != null) {
								R49cell7.setCellValue(BRF001.getR49_amount_fcy_non_resident() == null ? 0
										: BRF001.getR49_amount_fcy_non_resident().intValue());
							}

/////srl_no -60/////////

							Row row50 = sheet.getRow(59);
							Cell R50cell = row50.getCell(4);
							if (R50cell != null) {
								R50cell.setCellValue(BRF001.getR50_no_acct_aed_resident() == null ? 0
										: BRF001.getR50_no_acct_aed_resident().intValue());
							}
							Cell R50cell1 = row50.getCell(5);
							if (R50cell1 != null) {
								R50cell1.setCellValue(BRF001.getR50_amount_aed_resident() == null ? 0
										: BRF001.getR50_amount_aed_resident().intValue());
							}
							Cell R50cell2 = row50.getCell(6);
							if (R50cell2 != null) {
								R50cell2.setCellValue(BRF001.getR50_no_acct_fcy_resident() == null ? 0
										: BRF001.getR50_no_acct_fcy_resident().intValue());
							}
							Cell R50cell3 = row50.getCell(7);
							if (R50cell3 != null) {
								R50cell3.setCellValue(BRF001.getR50_amount_fcy_resident() == null ? 0
										: BRF001.getR50_amount_fcy_resident().intValue());
							}
							Cell R50cell4 = row50.getCell(8);
							if (R50cell4 != null) {
								R50cell4.setCellValue(BRF001.getR50_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR50_no_acct_aed_non_resident().intValue());
							}
							Cell R50cell5 = row50.getCell(9);
							if (R50cell5 != null) {
								R50cell5.setCellValue(BRF001.getR50_amount_aed_non_resident() == null ? 0
										: BRF001.getR50_amount_aed_non_resident().intValue());
							}
							Cell R50cell6 = row50.getCell(10);
							if (R50cell6 != null) {
								R50cell6.setCellValue(BRF001.getR50_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR50_no_acct_fcy_non_resident().intValue());
							}
							Cell R50cell7 = row50.getCell(11);
							if (R50cell7 != null) {
								R50cell7.setCellValue(BRF001.getR50_amount_fcy_non_resident() == null ? 0
										: BRF001.getR50_amount_fcy_non_resident().intValue());
							}

/////srl_no -62/////////

							Row row52 = sheet.getRow(61);
							Cell R52cell = row52.getCell(4);
							if (R52cell != null) {
								R52cell.setCellValue(BRF001.getR52_no_acct_aed_resident() == null ? 0
										: BRF001.getR52_no_acct_aed_resident().intValue());
							}
							Cell R52cell1 = row52.getCell(5);
							if (R52cell1 != null) {
								R52cell1.setCellValue(BRF001.getR52_amount_aed_resident() == null ? 0
										: BRF001.getR52_amount_aed_resident().intValue());
							}
							Cell R52cell2 = row52.getCell(6);
							if (R52cell2 != null) {
								R52cell2.setCellValue(BRF001.getR52_no_acct_fcy_resident() == null ? 0
										: BRF001.getR52_no_acct_fcy_resident().intValue());
							}
							Cell R52cell3 = row52.getCell(7);
							if (R52cell3 != null) {
								R52cell3.setCellValue(BRF001.getR52_amount_fcy_resident() == null ? 0
										: BRF001.getR52_amount_fcy_resident().intValue());
							}
							Cell R52cell4 = row52.getCell(8);
							if (R52cell4 != null) {
								R52cell4.setCellValue(BRF001.getR52_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR52_no_acct_aed_non_resident().intValue());
							}
							Cell R52cell5 = row52.getCell(9);
							if (R52cell5 != null) {
								R52cell5.setCellValue(BRF001.getR52_amount_aed_non_resident() == null ? 0
										: BRF001.getR52_amount_aed_non_resident().intValue());
							}
							Cell R52cell6 = row52.getCell(10);
							if (R52cell6 != null) {
								R52cell6.setCellValue(BRF001.getR52_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR52_no_acct_fcy_non_resident().intValue());
							}
							Cell R52cell7 = row52.getCell(11);
							if (R52cell7 != null) {
								R52cell7.setCellValue(BRF001.getR52_amount_fcy_non_resident() == null ? 0
										: BRF001.getR52_amount_fcy_non_resident().intValue());
							}

/////srl_no -63/////////

							Row row53 = sheet.getRow(62);
							Cell R53cell = row53.getCell(4);
							if (R53cell != null) {
								R53cell.setCellValue(BRF001.getR53_no_acct_aed_resident() == null ? 0
										: BRF001.getR53_no_acct_aed_resident().intValue());
							}
							Cell R53cell1 = row53.getCell(5);
							if (R53cell1 != null) {
								R53cell1.setCellValue(BRF001.getR53_amount_aed_resident() == null ? 0
										: BRF001.getR53_amount_aed_resident().intValue());
							}
							Cell R53cell2 = row53.getCell(6);
							if (R53cell2 != null) {
								R53cell2.setCellValue(BRF001.getR53_no_acct_fcy_resident() == null ? 0
										: BRF001.getR53_no_acct_fcy_resident().intValue());
							}
							Cell R53cell3 = row53.getCell(7);
							if (R53cell3 != null) {
								R53cell3.setCellValue(BRF001.getR53_amount_fcy_resident() == null ? 0
										: BRF001.getR53_amount_fcy_resident().intValue());
							}
							Cell R53cell4 = row53.getCell(8);
							if (R53cell4 != null) {
								R53cell4.setCellValue(BRF001.getR53_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR53_no_acct_aed_non_resident().intValue());
							}
							Cell R53cell5 = row53.getCell(9);
							if (R53cell5 != null) {
								R53cell5.setCellValue(BRF001.getR53_amount_aed_non_resident() == null ? 0
										: BRF001.getR53_amount_aed_non_resident().intValue());
							}
							Cell R53cell6 = row53.getCell(10);
							if (R53cell6 != null) {
								R53cell6.setCellValue(BRF001.getR53_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR53_no_acct_fcy_non_resident().intValue());
							}
							Cell R53cell7 = row53.getCell(11);
							if (R53cell7 != null) {
								R53cell7.setCellValue(BRF001.getR53_amount_fcy_non_resident() == null ? 0
										: BRF001.getR53_amount_fcy_non_resident().intValue());
							}

/////srl_no -64/////////

							Row row54 = sheet.getRow(63);
							Cell R54cell = row54.getCell(4);
							if (R54cell != null) {
								R54cell.setCellValue(BRF001.getR54_no_acct_aed_resident() == null ? 0
										: BRF001.getR54_no_acct_aed_resident().intValue());
							}
							Cell R54cell1 = row54.getCell(5);
							if (R54cell1 != null) {
								R54cell1.setCellValue(BRF001.getR54_amount_aed_resident() == null ? 0
										: BRF001.getR54_amount_aed_resident().intValue());
							}
							Cell R54cell2 = row54.getCell(6);
							if (R54cell2 != null) {
								R54cell2.setCellValue(BRF001.getR54_no_acct_fcy_resident() == null ? 0
										: BRF001.getR54_no_acct_fcy_resident().intValue());
							}
							Cell R54cell3 = row54.getCell(7);
							if (R54cell3 != null) {
								R54cell3.setCellValue(BRF001.getR54_amount_fcy_resident() == null ? 0
										: BRF001.getR54_amount_fcy_resident().intValue());
							}
							Cell R54cell4 = row54.getCell(8);
							if (R54cell4 != null) {
								R54cell4.setCellValue(BRF001.getR54_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR54_no_acct_aed_non_resident().intValue());
							}
							Cell R54cell5 = row54.getCell(9);
							if (R54cell5 != null) {
								R54cell5.setCellValue(BRF001.getR54_amount_aed_non_resident() == null ? 0
										: BRF001.getR54_amount_aed_non_resident().intValue());
							}
							Cell R54cell6 = row54.getCell(10);
							if (R54cell6 != null) {
								R54cell6.setCellValue(BRF001.getR54_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR54_no_acct_fcy_non_resident().intValue());
							}
							Cell R54cell7 = row54.getCell(11);
							if (R54cell7 != null) {
								R54cell7.setCellValue(BRF001.getR54_amount_fcy_non_resident() == null ? 0
										: BRF001.getR54_amount_fcy_non_resident().intValue());
							}

/////srl_no -65/////////

							Row row55 = sheet.getRow(64);
							Cell R55cell = row55.getCell(4);
							if (R55cell != null) {
								R55cell.setCellValue(BRF001.getR55_no_acct_aed_resident() == null ? 0
										: BRF001.getR55_no_acct_aed_resident().intValue());
							}
							Cell R55cell1 = row55.getCell(5);
							if (R55cell1 != null) {
								R55cell1.setCellValue(BRF001.getR55_amount_aed_resident() == null ? 0
										: BRF001.getR55_amount_aed_resident().intValue());
							}
							Cell R55cell2 = row55.getCell(6);
							if (R55cell2 != null) {
								R55cell2.setCellValue(BRF001.getR55_no_acct_fcy_resident() == null ? 0
										: BRF001.getR55_no_acct_fcy_resident().intValue());
							}
							Cell R55cell3 = row55.getCell(7);
							if (R55cell3 != null) {
								R55cell3.setCellValue(BRF001.getR55_amount_fcy_resident() == null ? 0
										: BRF001.getR55_amount_fcy_resident().intValue());
							}
							Cell R55cell4 = row55.getCell(8);
							if (R55cell4 != null) {
								R55cell4.setCellValue(BRF001.getR55_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR55_no_acct_aed_non_resident().intValue());
							}
							Cell R55cell5 = row55.getCell(9);
							if (R55cell5 != null) {
								R55cell5.setCellValue(BRF001.getR55_amount_aed_non_resident() == null ? 0
										: BRF001.getR55_amount_aed_non_resident().intValue());
							}
							Cell R55cell6 = row55.getCell(10);
							if (R55cell6 != null) {
								R55cell6.setCellValue(BRF001.getR55_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR55_no_acct_fcy_non_resident().intValue());
							}
							Cell R55cell7 = row55.getCell(11);
							if (R55cell7 != null) {
								R55cell7.setCellValue(BRF001.getR55_amount_fcy_non_resident() == null ? 0
										: BRF001.getR55_amount_fcy_non_resident().intValue());
							}

/////srl_no -66/////////

							Row row56 = sheet.getRow(65);
							Cell R56cell = row56.getCell(4);
							if (R56cell != null) {
								R56cell.setCellValue(BRF001.getR56_no_acct_aed_resident() == null ? 0
										: BRF001.getR56_no_acct_aed_resident().intValue());
							}
							Cell R56cell1 = row56.getCell(5);
							if (R56cell1 != null) {
								R56cell1.setCellValue(BRF001.getR56_amount_aed_resident() == null ? 0
										: BRF001.getR56_amount_aed_resident().intValue());
							}
							Cell R56cell2 = row56.getCell(6);
							if (R56cell2 != null) {
								R56cell2.setCellValue(BRF001.getR56_no_acct_fcy_resident() == null ? 0
										: BRF001.getR56_no_acct_fcy_resident().intValue());
							}
							Cell R56cell3 = row56.getCell(7);
							if (R56cell3 != null) {
								R56cell3.setCellValue(BRF001.getR56_amount_fcy_resident() == null ? 0
										: BRF001.getR56_amount_fcy_resident().intValue());
							}
							Cell R56cell4 = row56.getCell(8);
							if (R56cell4 != null) {
								R56cell4.setCellValue(BRF001.getR56_no_acct_aed_non_resident() == null ? 0
										: BRF001.getR56_no_acct_aed_non_resident().intValue());
							}
							Cell R56cell5 = row56.getCell(9);
							if (R56cell5 != null) {
								R56cell5.setCellValue(BRF001.getR56_amount_aed_non_resident() == null ? 0
										: BRF001.getR56_amount_aed_non_resident().intValue());
							}
							Cell R56cell6 = row56.getCell(10);
							if (R56cell6 != null) {
								R56cell6.setCellValue(BRF001.getR56_no_acct_fcy_non_resident() == null ? 0
										: BRF001.getR56_no_acct_fcy_non_resident().intValue());
							}
							Cell R56cell7 = row56.getCell(11);
							if (R56cell7 != null) {
								R56cell7.setCellValue(BRF001.getR56_amount_fcy_non_resident() == null ? 0
										: BRF001.getR56_amount_fcy_non_resident().intValue());
							}

/////srl_no -68/////////

							Row row58 = sheet.getRow(67);
							Cell R58cell = row58.getCell(5);
							if (R58cell != null) {
								R58cell.setCellValue(BRF001.getR58_amount_aed_resident() == null ? 0
										: BRF001.getR58_amount_aed_resident().intValue());
							}
							Cell R58cell1 = row58.getCell(7);
							if (R58cell1 != null) {
								R58cell1.setCellValue(BRF001.getR58_amount_fcy_resident() == null ? 0
										: BRF001.getR58_amount_fcy_resident().intValue());
							}
							Cell R58cell2 = row58.getCell(9);
							if (R58cell2 != null) {
								R58cell2.setCellValue(BRF001.getR58_amount_aed_non_resident() == null ? 0
										: BRF001.getR58_amount_aed_non_resident().intValue());
							}
							Cell R58cell3 = row58.getCell(11);
							if (R58cell3 != null) {
								R58cell3.setCellValue(BRF001.getR58_amount_fcy_non_resident() == null ? 0
										: BRF001.getR58_amount_fcy_non_resident().intValue());
							}

/////srl_no -69/////////

							Row row59 = sheet.getRow(68);
							Cell R59cell = row59.getCell(5);
							if (R59cell != null) {
								R59cell.setCellValue(BRF001.getR59_amount_aed_resident() == null ? 0
										: BRF001.getR59_amount_aed_resident().intValue());
							}
							Cell R59cell1 = row59.getCell(7);
							if (R59cell1 != null) {
								R59cell1.setCellValue(BRF001.getR59_amount_fcy_resident() == null ? 0
										: BRF001.getR59_amount_fcy_resident().intValue());
							}
							Cell R59cell2 = row59.getCell(9);
							if (R59cell2 != null) {
								R59cell2.setCellValue(BRF001.getR59_amount_aed_non_resident() == null ? 0
										: BRF001.getR59_amount_aed_non_resident().intValue());
							}
							Cell R59cell3 = row59.getCell(11);
							if (R59cell3 != null) {
								R59cell3.setCellValue(BRF001.getR59_amount_fcy_non_resident() == null ? 0
										: BRF001.getR59_amount_fcy_non_resident().intValue());
							}

/////srl_no -70/////////

							Row row60 = sheet.getRow(69);
							Cell R60cell = row60.getCell(5);
							if (R60cell != null) {
								R60cell.setCellValue(BRF001.getR60_amount_aed_resident() == null ? 0
										: BRF001.getR60_amount_aed_resident().intValue());
							}
							Cell R60cell1 = row60.getCell(7);
							if (R60cell1 != null) {
								R60cell1.setCellValue(BRF001.getR60_amount_fcy_resident() == null ? 0
										: BRF001.getR60_amount_fcy_resident().intValue());
							}
							Cell R60cell2 = row60.getCell(9);
							if (R60cell2 != null) {
								R60cell2.setCellValue(BRF001.getR60_amount_aed_non_resident() == null ? 0
										: BRF001.getR60_amount_aed_non_resident().intValue());
							}
							Cell R60cell3 = row60.getCell(11);
							if (R60cell3 != null) {
								R60cell3.setCellValue(BRF001.getR60_amount_fcy_non_resident() == null ? 0
										: BRF001.getR60_amount_fcy_non_resident().intValue());
							}

/////srl_no -71/////////

							Row row61 = sheet.getRow(70);
							Cell R61cell = row61.getCell(5);
							if (R61cell != null) {
								R61cell.setCellValue(BRF001.getR61_amount_aed_resident() == null ? 0
										: BRF001.getR61_amount_aed_resident().intValue());
							}
							Cell R61cell1 = row61.getCell(7);
							if (R61cell1 != null) {
								R61cell1.setCellValue(BRF001.getR61_amount_fcy_resident() == null ? 0
										: BRF001.getR61_amount_fcy_resident().intValue());
							}
							Cell R61cell2 = row61.getCell(9);
							if (R61cell2 != null) {
								R61cell2.setCellValue(BRF001.getR61_amount_aed_non_resident() == null ? 0
										: BRF001.getR61_amount_aed_non_resident().intValue());
							}
							Cell R61cell3 = row61.getCell(11);
							if (R61cell3 != null) {
								R61cell3.setCellValue(BRF001.getR61_amount_fcy_non_resident() == null ? 0
										: BRF001.getR61_amount_fcy_non_resident().intValue());
							}

/////srl_no -72/////////

							Row row62 = sheet.getRow(71);
							Cell R62cell = row62.getCell(5);
							if (R62cell != null) {
								R62cell.setCellValue(BRF001.getR62_amount_aed_resident() == null ? 0
										: BRF001.getR62_amount_aed_resident().intValue());
							}
							Cell R62cell1 = row62.getCell(7);
							if (R62cell1 != null) {
								R62cell1.setCellValue(BRF001.getR62_amount_fcy_resident() == null ? 0
										: BRF001.getR62_amount_fcy_resident().intValue());
							}
							Cell R62cell2 = row62.getCell(9);
							if (R62cell2 != null) {
								R62cell2.setCellValue(BRF001.getR62_amount_aed_non_resident() == null ? 0
										: BRF001.getR62_amount_aed_non_resident().intValue());
							}
							Cell R62cell3 = row62.getCell(11);
							if (R62cell3 != null) {
								R62cell3.setCellValue(BRF001.getR62_amount_fcy_non_resident() == null ? 0
										: BRF001.getR62_amount_fcy_non_resident().intValue());
							}

/////srl_no -73/////////

							Row row63 = sheet.getRow(72);
							Cell R63cell = row63.getCell(5);
							if (R63cell != null) {
								R63cell.setCellValue(BRF001.getR63_amount_aed_resident() == null ? 0
										: BRF001.getR63_amount_aed_resident().intValue());
							}
							Cell R63cell1 = row63.getCell(7);
							if (R63cell1 != null) {
								R63cell1.setCellValue(BRF001.getR63_amount_fcy_resident() == null ? 0
										: BRF001.getR63_amount_fcy_resident().intValue());
							}
							Cell R63cell2 = row63.getCell(9);
							if (R63cell2 != null) {
								R63cell2.setCellValue(BRF001.getR63_amount_aed_non_resident() == null ? 0
										: BRF001.getR63_amount_aed_non_resident().intValue());
							}
							Cell R63cell3 = row63.getCell(11);
							if (R63cell3 != null) {
								R63cell3.setCellValue(BRF001.getR63_amount_fcy_non_resident() == null ? 0
										: BRF001.getR63_amount_fcy_non_resident().intValue());
							}

/////srl_no -75/////////

							Row row65 = sheet.getRow(74);
							Cell R65cell = row65.getCell(5);
							if (R65cell != null) {
								R65cell.setCellValue(BRF001.getR65_amount_aed_resident() == null ? 0
										: BRF001.getR65_amount_aed_resident().intValue());
							}
							Cell R65cell1 = row65.getCell(7);
							if (R65cell1 != null) {
								R65cell1.setCellValue(BRF001.getR65_amount_fcy_resident() == null ? 0
										: BRF001.getR65_amount_fcy_resident().intValue());
							}
							Cell R65cell2 = row65.getCell(9);
							if (R65cell2 != null) {
								R65cell2.setCellValue(BRF001.getR65_amount_aed_non_resident() == null ? 0
										: BRF001.getR65_amount_aed_non_resident().intValue());
							}
							Cell R65cell3 = row65.getCell(11);
							if (R65cell3 != null) {
								R65cell3.setCellValue(BRF001.getR65_amount_fcy_non_resident() == null ? 0
										: BRF001.getR65_amount_fcy_non_resident().intValue());
							}

/////srl_no -76/////////

							Row row66 = sheet.getRow(75);
							Cell R66cell = row66.getCell(5);
							if (R66cell != null) {
								R66cell.setCellValue(BRF001.getR66_amount_aed_resident() == null ? 0
										: BRF001.getR66_amount_aed_resident().intValue());
							}
							Cell R66cell1 = row66.getCell(7);
							if (R66cell1 != null) {
								R66cell1.setCellValue(BRF001.getR66_amount_fcy_resident() == null ? 0
										: BRF001.getR66_amount_fcy_resident().intValue());
							}
							Cell R66cell2 = row66.getCell(9);
							if (R66cell2 != null) {
								R66cell2.setCellValue(BRF001.getR66_amount_aed_non_resident() == null ? 0
										: BRF001.getR66_amount_aed_non_resident().intValue());
							}
							Cell R66cell3 = row66.getCell(11);
							if (R66cell3 != null) {
								R66cell3.setCellValue(BRF001.getR66_amount_fcy_non_resident() == null ? 0
										: BRF001.getR66_amount_fcy_non_resident().intValue());
							}

/////srl_no -77/////////

							Row row67 = sheet.getRow(76);
							Cell R67cell = row67.getCell(5);
							if (R67cell != null) {
								R67cell.setCellValue(BRF001.getR67_amount_aed_resident() == null ? 0
										: BRF001.getR67_amount_aed_resident().intValue());
							}
							Cell R67cell1 = row67.getCell(7);
							if (R67cell1 != null) {
								R67cell1.setCellValue(BRF001.getR67_amount_fcy_resident() == null ? 0
										: BRF001.getR67_amount_fcy_resident().intValue());
							}
							Cell R67cell2 = row67.getCell(9);
							if (R67cell2 != null) {
								R67cell2.setCellValue(BRF001.getR67_amount_aed_non_resident() == null ? 0
										: BRF001.getR67_amount_aed_non_resident().intValue());
							}
							Cell R67cell3 = row67.getCell(11);
							if (R67cell3 != null) {
								R67cell3.setCellValue(BRF001.getR67_amount_fcy_non_resident() == null ? 0
										: BRF001.getR67_amount_fcy_non_resident().intValue());
							}

/////srl_no -78/////////

							Row row68 = sheet.getRow(77);
							Cell R68cell = row68.getCell(5);
							if (R68cell != null) {
								R68cell.setCellValue(BRF001.getR68_amount_aed_resident() == null ? 0
										: BRF001.getR68_amount_aed_resident().intValue());
							}
							Cell R68cell1 = row68.getCell(7);
							if (R68cell1 != null) {
								R68cell1.setCellValue(BRF001.getR68_amount_fcy_resident() == null ? 0
										: BRF001.getR68_amount_fcy_resident().intValue());
							}
							Cell R68cell2 = row68.getCell(9);
							if (R68cell2 != null) {
								R68cell2.setCellValue(BRF001.getR68_amount_aed_non_resident() == null ? 0
										: BRF001.getR68_amount_aed_non_resident().intValue());
							}
							Cell R68cell3 = row68.getCell(11);
							if (R68cell3 != null) {
								R68cell3.setCellValue(BRF001.getR68_amount_fcy_non_resident() == null ? 0
										: BRF001.getR68_amount_fcy_non_resident().intValue());
							}

/////srl_no -79/////////

							Row row69 = sheet.getRow(78);
							Cell R69cell = row69.getCell(5);
							if (R69cell != null) {
								R69cell.setCellValue(BRF001.getR69_amount_aed_resident() == null ? 0
										: BRF001.getR69_amount_aed_resident().intValue());
							}
							Cell R69cell1 = row69.getCell(7);
							if (R69cell1 != null) {
								R69cell1.setCellValue(BRF001.getR69_amount_fcy_resident() == null ? 0
										: BRF001.getR69_amount_fcy_resident().intValue());
							}
							Cell R69cell2 = row69.getCell(9);
							if (R69cell2 != null) {
								R69cell2.setCellValue(BRF001.getR69_amount_aed_non_resident() == null ? 0
										: BRF001.getR69_amount_aed_non_resident().intValue());
							}
							Cell R69cell3 = row69.getCell(11);
							if (R69cell3 != null) {
								R69cell3.setCellValue(BRF001.getR69_amount_fcy_non_resident() == null ? 0
										: BRF001.getR69_amount_fcy_non_resident().intValue());
							}

/////srl_no -80/////////

							Row row70 = sheet.getRow(79);
							Cell R70cell = row70.getCell(5);
							if (R70cell != null) {
								R70cell.setCellValue(BRF001.getR70_amount_aed_resident() == null ? 0
										: BRF001.getR70_amount_aed_resident().intValue());
							}
							Cell R70cell1 = row70.getCell(7);
							if (R70cell1 != null) {
								R70cell1.setCellValue(BRF001.getR70_amount_fcy_resident() == null ? 0
										: BRF001.getR70_amount_fcy_resident().intValue());
							}
							Cell R70cell2 = row70.getCell(9);
							if (R70cell2 != null) {
								R70cell2.setCellValue(BRF001.getR70_amount_aed_non_resident() == null ? 0
										: BRF001.getR70_amount_aed_non_resident().intValue());
							}
							Cell R70cell3 = row70.getCell(11);
							if (R70cell3 != null) {
								R70cell3.setCellValue(BRF001.getR70_amount_fcy_non_resident() == null ? 0
										: BRF001.getR70_amount_fcy_non_resident().intValue());
							}

/////srl_no -81/////////

							Row row71 = sheet.getRow(80);
							Cell R71cell = row71.getCell(5);
							if (R71cell != null) {
								R71cell.setCellValue(BRF001.getR71_amount_aed_resident() == null ? 0
										: BRF001.getR71_amount_aed_resident().intValue());
							}
							Cell R71cell1 = row71.getCell(7);
							if (R71cell1 != null) {
								R71cell1.setCellValue(BRF001.getR71_amount_fcy_resident() == null ? 0
										: BRF001.getR71_amount_fcy_resident().intValue());
							}
							Cell R71cell2 = row71.getCell(9);
							if (R71cell2 != null) {
								R71cell2.setCellValue(BRF001.getR71_amount_aed_non_resident() == null ? 0
										: BRF001.getR71_amount_aed_non_resident().intValue());
							}
							Cell R71cell3 = row71.getCell(11);
							if (R71cell3 != null) {
								R71cell3.setCellValue(BRF001.getR71_amount_fcy_non_resident() == null ? 0
										: BRF001.getR71_amount_fcy_non_resident().intValue());
							}

/////srl_no -82/////////

							Row row72 = sheet.getRow(81);
							Cell R72cell = row72.getCell(5);
							if (R72cell != null) {
								R72cell.setCellValue(BRF001.getR72_amount_aed_resident() == null ? 0
										: BRF001.getR72_amount_aed_resident().intValue());
							}
							Cell R72cell1 = row72.getCell(7);
							if (R72cell1 != null) {
								R72cell1.setCellValue(BRF001.getR72_amount_fcy_resident() == null ? 0
										: BRF001.getR72_amount_fcy_resident().intValue());
							}
							Cell R72cell2 = row72.getCell(9);
							if (R72cell2 != null) {
								R72cell2.setCellValue(BRF001.getR72_amount_aed_non_resident() == null ? 0
										: BRF001.getR72_amount_aed_non_resident().intValue());
							}
							Cell R72cell3 = row72.getCell(11);
							if (R72cell3 != null) {
								R72cell3.setCellValue(BRF001.getR72_amount_fcy_non_resident() == null ? 0
										: BRF001.getR72_amount_fcy_non_resident().intValue());
							}

/////srl_no -84/////////

							Row row74 = sheet.getRow(83);
							Cell R74cell = row74.getCell(5);
							if (R74cell != null) {
								R74cell.setCellValue(BRF001.getR74_amount_aed_resident() == null ? 0
										: BRF001.getR74_amount_aed_resident().intValue());
							}
							Cell R74cell1 = row74.getCell(7);
							if (R74cell1 != null) {
								R74cell1.setCellValue(BRF001.getR74_amount_fcy_resident() == null ? 0
										: BRF001.getR74_amount_fcy_resident().intValue());
							}
							Cell R74cell2 = row74.getCell(9);
							if (R74cell2 != null) {
								R74cell2.setCellValue(BRF001.getR74_amount_aed_non_resident() == null ? 0
										: BRF001.getR74_amount_aed_non_resident().intValue());
							}
							Cell R74cell3 = row74.getCell(11);
							if (R74cell3 != null) {
								R74cell3.setCellValue(BRF001.getR74_amount_fcy_non_resident() == null ? 0
										: BRF001.getR74_amount_fcy_non_resident().intValue());
							}

/////srl_no -85/////////

							Row row75 = sheet.getRow(84);
							Cell R75cell = row75.getCell(5);
							if (R75cell != null) {
								R75cell.setCellValue(BRF001.getR75_amount_aed_resident() == null ? 0
										: BRF001.getR75_amount_aed_resident().intValue());
							}
							Cell R75cell1 = row75.getCell(7);
							if (R75cell1 != null) {
								R75cell1.setCellValue(BRF001.getR75_amount_fcy_resident() == null ? 0
										: BRF001.getR75_amount_fcy_resident().intValue());
							}
							Cell R75cell2 = row75.getCell(9);
							if (R75cell2 != null) {
								R75cell2.setCellValue(BRF001.getR75_amount_aed_non_resident() == null ? 0
										: BRF001.getR75_amount_aed_non_resident().intValue());
							}
							Cell R75cell3 = row75.getCell(11);
							if (R75cell3 != null) {
								R75cell3.setCellValue(BRF001.getR75_amount_fcy_non_resident() == null ? 0
										: BRF001.getR75_amount_fcy_non_resident().intValue());
							}

/////srl_no -86/////////

							Row row76 = sheet.getRow(85);
							Cell R76cell = row76.getCell(5);
							if (R76cell != null) {
								R76cell.setCellValue(BRF001.getR76_amount_aed_resident() == null ? 0
										: BRF001.getR76_amount_aed_resident().intValue());
							}
							Cell R76cell1 = row76.getCell(7);
							if (R76cell1 != null) {
								R76cell1.setCellValue(BRF001.getR76_amount_fcy_resident() == null ? 0
										: BRF001.getR76_amount_fcy_resident().intValue());
							}
							Cell R76cell2 = row76.getCell(9);
							if (R76cell2 != null) {
								R76cell2.setCellValue(BRF001.getR76_amount_aed_non_resident() == null ? 0
										: BRF001.getR76_amount_aed_non_resident().intValue());
							}
							Cell R76cell3 = row76.getCell(11);
							if (R76cell3 != null) {
								R76cell3.setCellValue(BRF001.getR76_amount_fcy_non_resident() == null ? 0
										: BRF001.getR76_amount_fcy_non_resident().intValue());
							}

/////srl_no -87/////////

							Row row77 = sheet.getRow(86);
							Cell R77cell = row77.getCell(5);
							if (R77cell != null) {
								R77cell.setCellValue(BRF001.getR77_amount_aed_resident() == null ? 0
										: BRF001.getR77_amount_aed_resident().intValue());
							}
							Cell R77cell1 = row77.getCell(7);
							if (R77cell1 != null) {
								R77cell1.setCellValue(BRF001.getR77_amount_fcy_resident() == null ? 0
										: BRF001.getR77_amount_fcy_resident().intValue());
							}
							Cell R77cell2 = row77.getCell(9);
							if (R77cell2 != null) {
								R77cell2.setCellValue(BRF001.getR77_amount_aed_non_resident() == null ? 0
										: BRF001.getR77_amount_aed_non_resident().intValue());
							}
							Cell R77cell3 = row77.getCell(11);
							if (R77cell3 != null) {
								R77cell3.setCellValue(BRF001.getR77_amount_fcy_non_resident() == null ? 0
										: BRF001.getR77_amount_fcy_non_resident().intValue());
							}

/////srl_no -88/////////

							Row row78 = sheet.getRow(87);
							Cell R78cell = row78.getCell(5);
							if (R78cell != null) {
								R78cell.setCellValue(BRF001.getR78_amount_aed_resident() == null ? 0
										: BRF001.getR78_amount_aed_resident().intValue());
							}
							Cell R78cell1 = row78.getCell(7);
							if (R78cell1 != null) {
								R78cell1.setCellValue(BRF001.getR78_amount_fcy_resident() == null ? 0
										: BRF001.getR78_amount_fcy_resident().intValue());
							}
							Cell R78cell2 = row78.getCell(9);
							if (R78cell2 != null) {
								R78cell2.setCellValue(BRF001.getR78_amount_aed_non_resident() == null ? 0
										: BRF001.getR78_amount_aed_non_resident().intValue());
							}
							Cell R78cell3 = row78.getCell(11);
							if (R78cell3 != null) {
								R78cell3.setCellValue(BRF001.getR78_amount_fcy_non_resident() == null ? 0
										: BRF001.getR78_amount_fcy_non_resident().intValue());
							}

/////srl_no -89/////////

							Row row79 = sheet.getRow(88);
							Cell R79cell = row79.getCell(5);
							if (R79cell != null) {
								R79cell.setCellValue(BRF001.getR79_amount_aed_resident() == null ? 0
										: BRF001.getR79_amount_aed_resident().intValue());
							}
							Cell R79cell1 = row79.getCell(7);
							if (R79cell1 != null) {
								R79cell1.setCellValue(BRF001.getR79_amount_fcy_resident() == null ? 0
										: BRF001.getR79_amount_fcy_resident().intValue());
							}
							Cell R79cell2 = row79.getCell(9);
							if (R79cell2 != null) {
								R79cell2.setCellValue(BRF001.getR79_amount_aed_non_resident() == null ? 0
										: BRF001.getR79_amount_aed_non_resident().intValue());
							}
							Cell R79cell3 = row79.getCell(11);
							if (R79cell3 != null) {
								R79cell3.setCellValue(BRF001.getR79_amount_fcy_non_resident() == null ? 0
										: BRF001.getR79_amount_fcy_non_resident().intValue());
							}

/////srl_no -90/////////

							Row row80 = sheet.getRow(89);
							Cell R80cell = row80.getCell(5);
							if (R80cell != null) {
								R80cell.setCellValue(BRF001.getR80_amount_aed_resident() == null ? 0
										: BRF001.getR80_amount_aed_resident().intValue());
							}
							Cell R80cell1 = row80.getCell(7);
							if (R80cell1 != null) {
								R80cell1.setCellValue(BRF001.getR80_amount_fcy_resident() == null ? 0
										: BRF001.getR80_amount_fcy_resident().intValue());
							}
							Cell R80cell2 = row80.getCell(9);
							if (R80cell2 != null) {
								R80cell2.setCellValue(BRF001.getR80_amount_aed_non_resident() == null ? 0
										: BRF001.getR80_amount_aed_non_resident().intValue());
							}
							Cell R80cell3 = row80.getCell(11);
							if (R80cell3 != null) {
								R80cell3.setCellValue(BRF001.getR80_amount_fcy_non_resident() == null ? 0
										: BRF001.getR80_amount_fcy_non_resident().intValue());
							}

							// Save the changes
							workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
							FileOutputStream fileOut = new FileOutputStream(
									env.getProperty("output.exportpathfinal") + "011-BRF-001-A.xls");
							workbook.write(fileOut);
							fileOut.close();
							System.out.println(fileOut);
							path = fileOut.toString();
							// Close the workbook
							System.out.println("PATH : " + path);
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

			} //// else end

		}
		outputFile = new File(env.getProperty("output.exportpathfinal") + "011-BRF-001-A.xls");
		// Retrieve session attributes safely (assuming it's a web environment)
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String user1 = (String) request.getSession().getAttribute("USERID");
		String username = (String) request.getSession().getAttribute("USERNAME");

		String auditID = sequence.generateRequestUUId();

		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DOWNLOAD");
		audit.setAudit_table("BRF1_SUMMARYTABLE");
		audit.setAudit_screen("Download");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Download Successfully");

		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		mANUAL_Service_Rep.save(audit);

		return outputFile;

	}
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public String detailChanges1(BRF1_DETAIL_ENTITY detail, String foracid, String report_addl_criteria_1,
	        BigDecimal act_balance_amt_lc, String report_label_1, String report_name_1,String report_date, AuditReasonDTO reason) {

	    String msg = "";

	    try {
	        Session hs = sessionFactory.getCurrentSession();
	        //Optional<BRF1_DETAIL_ENTITY> Brf1detail = BRF1_DetaiRep1.findById(foracid);
	        BRF1_DETAIL_ENTITY Brf1detail = BRF1_DetaiRep1.getbyaccnoanddate(foracid, report_date);

	        if (!Brf1detail.equals(null) && Brf1detail!=null) {
	            BRF1_DETAIL_ENTITY BRFdetail =Brf1detail;

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

	            if (fieldNames.isEmpty()) {
	                msg = "No modification done";
	            } else {
	                BRF1_DetaiRep1.save(BRFdetail);
	                logger.info("Edited Record");

	                HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
	                        .getRequestAttributes()).getRequest();
	                String user1 = (String) request.getSession().getAttribute("USERID");
	                String username = (String) request.getSession().getAttribute("USERNAME");

	                String auditID = sequence.generateRequestUUId();
/*
	                MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
	                audit.setAudit_date(new Date());
	                audit.setEntry_time(new Date());
	                audit.setEntry_user(user1);
	                audit.setFunc_code("EDIT");
	                audit.setAudit_table("BRF1_DETAILTABLE");
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
*/
	                // =========================================================================
	                // PROCEDURE EXECUTION LOGIC (Added without changing method arguments)
	                // =========================================================================
	                try {
	                    // Assuming the entity has a getReport_date() method returning a Date object
	                    Date entityDate = BRFdetail.getReport_date(); 
	                    
	                    if (entityDate != null) {
	                        String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(entityDate);
	        
	                        // Run summary procedure after commit
	                        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
	                            @Override
	                            public void afterCommit() {
	                                try {
	                                    logger.info("Transaction committed — calling BRF1_SUMMARY_PROCEDURE({})", formattedDate);
	                                    // Make sure 'jdbcTemplate' is available in this class
	                                    jdbcTemplate.update("BEGIN BRF1_SUMMARY_PROCEDURE(?); END;", formattedDate);
	                                    logger.info("Procedure executed successfully after commit.");
	                                } catch (Exception e) {
	                                    logger.error("Error executing procedure after commit", e);
	                                }
	                            }
	                        });
	                    } else {
	                        logger.warn("Report Date is null in entity, skipping summary procedure.");
	                    }
	                } catch (Exception e) {
	                    logger.error("Error preparing procedure call", e);
	                }
	                // =========================================================================

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

	// TO show thw Archieve values
	public ModelAndView getArchieveBRF001View(String reportId, String fromdate, String todate, String currency,
			String dtltype, Pageable pageable,String type) {

		ModelAndView mv = new ModelAndView();
		Session hs = sessionFactory.getCurrentSession();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<BRF1_REPORT_ENTITY> T1rep = new ArrayList<BRF1_REPORT_ENTITY>();
		// Query<Object[]> qr;

		List<BRF1_REPORT_ENTITY> T1Master = new ArrayList<BRF1_REPORT_ENTITY>();
		/* List<BRF73_TABLE2> T1Master1 = new ArrayList<BRF73_TABLE2>(); */

		logger.info("Inside archive" + currency);

		try {
			Date d1 = df.parse(todate);
			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			T1Master = hs.createQuery("from  BRF1_REPORT_ENTITY a where a.report_date = ?1 ", BRF1_REPORT_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();

			/*
			 * T1Master1 = hs.createQuery("from BRF73_TABLE2 a where a.report_date = ?1 ",
			 * BRF73_TABLE2.class) .setParameter(1, df.parse(todate)).getResultList();
			 */

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

		mv.setViewName("RR/BRF1ARCH");
		// mv.addObject("currlist", refCodeConfig.currList());
		mv.addObject("reportsummary", T1Master);
		/* mv.addObject("reportsummary1", T1Master1); */
		mv.addObject("reportmaster", T1Master);
		mv.addObject("displaymode", "summary");
		mv.addObject("reportsflag", "reportsflag");
		mv.addObject("menu", reportId);
		mv.addObject("type", type);
		
		System.out.println("scv" + mv.getViewName());
		

		return mv;

	}

	/****
	 * ARCH DETAILS
	 * 
	 * @throws ParseException
	 ****/

	public ModelAndView ARCHgetBRF001currentDtl(String reportId, String fromdate, String todate, String currency,
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
				System.out.println("Filter - " + filter);

				qr = hs.createNativeQuery(
						"select * from BRF1_ARCHIVTABLE a where report_date=?1 and report_label_1=?2");
				qr.setParameter(1, df.parse(todate));
				qr.setParameter(2, filter);

			} else {
				System.out.println("2");
				qr = hs.createNativeQuery("select * from BRF1_ARCHIVTABLE");

			}
		} else {
			System.out.println("3");
			qr = hs.createNativeQuery("select * from BRF1_ARCHIVTABLE  where report_date = ?1");
		}

		/*
		 * try { qr.setParameter(1, df.parse(todate));
		 * 
		 * } catch (ParseException e) { e.printStackTrace(); }
		 */
		List<BRF1_ARCHIVENTITY> T1Master = new ArrayList<BRF1_ARCHIVENTITY>();

		try {
			System.out.println("Values entered");
			T1Master = hs.createQuery("from BRF1_ARCHIVENTITY a where a.report_date = ?1", BRF1_ARCHIVENTITY.class)
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
			String nre_flag = (String) a[49];

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

			BRF1_ARCHIVENTITY py = new BRF1_ARCHIVENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
					acct_crncy_code, gl_code, gl_sub_head_code, gl_sub_head_desc, country_of_incorp, cust_type,
					schm_code, schm_type, sol_id, acid, segment, sub_segment, sector, sub_sector, sector_code, group_id,
					constitution_code, country, legal_entity_type, constitution_desc, purpose_of_advn, hni_networth,
					turnover, bacid, report_name_1, report_label_1, report_addl_criteria_1, report_addl_criteria_2,
					report_addl_criteria_3, create_user, create_time, modify_user, modify_time, verify_user,
					verify_time, entity_flg, modify_flg, del_flg, nre_status, report_date, maturity_date, gender,
					version, remarks, nre_flag);

			T1Dt1.add(py);

		}
		;

		List<Object> pagedlist;

		if (T1Dt1.size() < startItem) {
			pagedlist = Collections.emptyList();

		} else {
			int toIndex = Math.min(startItem + pageSize, T1Dt1.size());
			pagedlist = T1Dt1.subList(startItem, toIndex);
		}

		logger.info("Converting to Page");
		Page<Object> T1Dt1Page = new PageImpl<Object>(pagedlist, PageRequest.of(currentPage, pageSize), T1Dt1.size());

		mv.setViewName("RR" + "/" + "BRF1ARCH::reportcontent");
		mv.addObject("reportdetails", T1Dt1Page.getContent());
		mv.addObject("reportmaster", T1Master);
		mv.addObject("reportmaster1", qr);
		mv.addObject("singledetail", new T1CurProdDetail());
		mv.addObject("reportsflag", "reportsflag");
		mv.addObject("menu", reportId);
		return mv;
	}

}