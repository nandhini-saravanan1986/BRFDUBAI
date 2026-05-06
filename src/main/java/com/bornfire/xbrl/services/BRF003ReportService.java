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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.AuditReasonDTO;
import com.bornfire.xbrl.entities.BRF3_ARCHIVENTITY;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.BRBS.BRF003_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF3_DETAILTABLE;
import com.bornfire.xbrl.entities.BRBS.BRF3_DetaiRep;
import com.bornfire.xbrl.entities.BRBS.BRF46_Entity;
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
public class BRF003ReportService {

	private static final Logger logger = LoggerFactory.getLogger(BRF003ReportService.class);

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	DataSource srcdataSource;

	@Autowired
	Environment env;

	@Autowired
	BRF3_DetaiRep bRF3_DetaiRep;

	@Autowired
	MANUAL_Service_Rep mANUAL_Service_Rep;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	UserProfileRep userProfileRep;

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
			Long dtlcnt = (Long) hs.createQuery("select count(*) from BRF003_ENTITY a where a.report_date=?1")
					.setParameter(1, dt9).getSingleResult();

			if (dtlcnt > 0) {
				logger.info("Getting No of records in Mod table :" + reportid);
				Long modcnt = (Long) hs.createQuery("select count(*) from BRF003_ENTITY a").getSingleResult();
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

	public ModelAndView getBRF003View(String reportId, String fromdate, String todate, String currency, String dtltype,
			Pageable pageable) {

		ModelAndView mv = new ModelAndView();
		Session hs = sessionFactory.getCurrentSession();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<BRF003_ENTITY> T1rep = new ArrayList<BRF003_ENTITY>();
		// Query<Object[]> qr;

		List<BRF003_ENTITY> T1Master = new ArrayList<BRF003_ENTITY>();
		/* List<BRF73_TABLE2> T1Master1 = new ArrayList<BRF73_TABLE2>(); */

		logger.info("Inside archive" + currency);

		try {
			Date d1 = df.parse(todate);
			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			T1Master = hs.createQuery("from  BRF003_ENTITY a where a.report_date = ?1 ", BRF003_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();

			/*
			 * T1Master1 = hs.createQuery("from BRF73_TABLE2 a where a.report_date = ?1 ",
			 * BRF73_TABLE2.class) .setParameter(1, df.parse(todate)).getResultList();
			 */

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

		mv.setViewName("RR/BRF3");
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

	public ModelAndView getBRF003currentDtl(String reportId, String fromdate, String todate, String currency,
			String dtltype, Pageable pageable, String filter, String searchVal) {

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		// int startItem = currentPage * pageSize;
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
						"select * from BRF3_DETAILTABLE  a where report_date = ?1 and report_label_1 =?2"
								+ searchCondition);
				countQr = hs.createNativeQuery(
						"select count(*) from BRF3_DETAILTABLE a where report_date = ?1 and report_label_1 = ?2"
								+ searchCondition);

				qr.setParameter(2, filter);
				countQr.setParameter(2, filter);

			} else {
				qr = hs.createNativeQuery("select * from BRF3_DETAILTABLE a where report_date = ?1" + searchCondition);
				countQr = hs.createNativeQuery(
						"select count(*) from BRF3_DETAILTABLE a where report_date = ?1" + searchCondition);
			}
		} else {
			qr = hs.createNativeQuery("select * from BRF3_DETAILTABLE  where report_date = ?1" + searchCondition);
			countQr = hs.createNativeQuery(
					"select count(*) from BRF3_DETAILTABLE where report_date = ?1" + searchCondition);
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
		List<BRF3_DETAILTABLE> T1Master = new ArrayList<BRF3_DETAILTABLE>();
		/*
		 * try { T1Master =
		 * hs.createQuery("from BRF3_DETAILTABLE a where a.report_date = ?1",
		 * BRF3_DETAILTABLE.class) .setParameter(1, df.parse(todate)).getResultList(); }
		 * catch (ParseException e) {
		 * 
		 * e.printStackTrace(); }
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
			String entity_flg = (String) a[40];
			String modify_flg = (String) a[41];
			String del_flg = (String) a[42];
			String nre_status = (String) a[43];
			Date report_date = (Date) a[44];
			String maturity_date = (String) a[45];
			String gender = (String) a[46];
			String version = (String) a[47];
			String remarks = (String) a[48];
			String nreflag = (String) a[49];

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

			BRF3_DETAILTABLE py = new BRF3_DETAILTABLE(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
					acct_crncy_code, gl_code, gl_sub_head_code, gl_sub_head_desc, country_of_incorp, cust_type,
					schm_code, schm_type, sol_id, acid, segment, sub_segment, sector, sub_sector, sector_code, group_id,
					constitution_code, country, legal_entity_type, constitution_desc, purpose_of_advn, hni_networth,
					turnover, bacid, report_name_1, report_label_1, report_addl_criteria_1, report_addl_criteria_2,
					report_addl_criteria_3, create_user, create_time, modify_user, modify_time, verify_user,
					verify_time, entity_flg, modify_flg, del_flg, nre_status, report_date, maturity_date, gender,
					version, remarks, nreflag);
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
		// Page<Object> T1Dt1Page = new PageImpl<Object>(pagedlist,
		// PageRequest.of(currentPage, pageSize), T1Dt1.size());

		Page<Object> T1Dt1Page = new PageImpl<>(T1Dt1, pageable, totalRecords);
		mv.addObject("reportdetailsPage", T1Dt1Page);
		mv.setViewName("RR" + "/" + "BRF3::reportcontent");
		mv.addObject("reportdetails", T1Dt1Page.getContent());
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
			String filetype) throws FileNotFoundException, JRException, SQLException {

		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		String path = this.env.getProperty("output.exportpath");
		String fileName = "";
		String zipFileName = "";
		File outputFile;

		logger.info("Getting Output file :" + reportId);
		fileName = "011-BRF-003-A N";

		if (!filetype.equals("xbrl")) {
			if (!filetype.equals("BRF")) {

				try {
					InputStream jasperFile;
					logger.info("Getting Jasper file :" + reportId);
					if (filetype.equals("detailexcel")) {
						if (dtltype.equals("report")) {

							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF3_Details.jrxml");
						} else {
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF3_Details.jrxml");
						}

					} else {
						if (dtltype.equals("report")) {
							logger.info("Inside report");
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF3.jrxml");
						} else {
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF3.jrxml");
						}
					}

					/* JasperReport jr = (JasperReport) JRLoader.loadObject(jasperFile); */
					JasperReport jr = JasperCompileManager.compileReport(jasperFile);
					HashMap<String, Object> map = new HashMap<String, Object>();

					logger.info("Assigning Parameters for Jasper");
					map.put("REPORT_DATE", todate);
					if (filetype.equals("pdf")) {
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
				audit.setAudit_table("BRF3_SUMMARYTABLE");
				audit.setEvent_id(user1);
				audit.setEvent_name(username);

				if (filetype.equals("pdf")) {
					audit.setFunc_code("DOWNLOAD_PDF");
					audit.setAudit_screen("Download PDF");
					audit.setRemarks("BRF3 PDF downloaded successfully");
				} else if (filetype.equals("detailexcel")) {
					audit.setAudit_table("BRF3_DETAILTABLE");
					audit.setFunc_code("DOWNLOAD_EXCEL_DETAIL");
					audit.setAudit_screen("Download Excel Detail");
					audit.setRemarks("BRF3 Detailed Excel downloaded successfully");
				} else {
					audit.setFunc_code("DOWNLOAD");
					audit.setAudit_screen("Download");
					audit.setRemarks("BRF3 File downloaded successfully");
				}

				UserProfile values1 = userProfileRep.getRole(user1);
				audit.setAuth_user(values1.getAuth_user());
				audit.setAuth_time(values1.getAuth_time());
				audit.setAudit_ref_no(auditID);

				mANUAL_Service_Rep.save(audit);

				return outputFile;
			} else {

				List<BRF003_ENTITY> T1Master = new ArrayList<BRF003_ENTITY>();
				Session hs = sessionFactory.getCurrentSession();
				try {
					Date d1 = df.parse(todate);

					// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

					T1Master = hs.createQuery("from  BRF003_ENTITY a where a.report_date = ?1 ", BRF003_ENTITY.class)
							.setParameter(1, df.parse(todate)).getResultList();

					if (T1Master.size() == 1) {

						for (BRF003_ENTITY BRF03 : T1Master) {

							File Responsecamt = new File(
									env.getProperty("output.exportpathtemp") + "011-BRF-003-AT.xls");

							// Load the Excel file
							Workbook workbook = WorkbookFactory.create(Responsecamt);

							// Get the first sheet
							Sheet sheet = workbook.getSheetAt(0);

							/*
							 * Row r1row0 = sheet.getRow(4); if (r1row0 != null) { Cell r1cell0 =
							 * r1row0.getCell(2); if (r1cell0 == null) { r1cell0 = r1row0.createCell(2); //
							 * Create the cell if it doesn't exist } if (BRF03.getReport_date() != null) {
							 * // Formatting the date to dd-MM-yyyy SimpleDateFormat sdf = new
							 * SimpleDateFormat("dd-MM-yyyy"); String formattedDate =
							 * sdf.format(BRF03.getReport_date()); r1cell0.setCellValue(formattedDate); //
							 * Set the formatted date in the cell } else { r1cell0.setCellValue(""); // Set
							 * an empty value if the report_date is null } }
							 */
							Row r1row = sheet.getRow(9);

							Cell r1cell = r1row.getCell(5);
							if (r1cell != null) {
								r1cell.setCellValue(BRF03.getR1_resident_outstand() == null ? 0
										: BRF03.getR1_resident_outstand().intValue());
							}
							Cell r1cell1 = r1row.getCell(6);
							if (r1cell1 != null) {
								r1cell1.setCellValue(BRF03.getR1_non_resident_outstand() == null ? 0
										: BRF03.getR1_non_resident_outstand().intValue());
							}

							Row r2row = sheet.getRow(10);

							Cell r2cell = r2row.getCell(5);
							if (r2cell != null) {
								r2cell.setCellValue(BRF03.getR2_resident_outstand() == null ? 0
										: BRF03.getR2_resident_outstand().intValue());
							}
							Cell r2cell1 = r2row.getCell(6);
							if (r2cell1 != null) {
								r2cell1.setCellValue(BRF03.getR2_non_resident_outstand() == null ? 0
										: BRF03.getR2_non_resident_outstand().intValue());
							}
							Row r3row = sheet.getRow(11);

							Cell r3cell = r3row.getCell(5);
							if (r3cell != null) {
								r3cell.setCellValue(BRF03.getR3_resident_outstand() == null ? 0
										: BRF03.getR3_resident_outstand().intValue());
							}
							Cell r3cell1 = r3row.getCell(6);
							if (r3cell1 != null) {
								r3cell1.setCellValue(BRF03.getR3_non_resident_outstand() == null ? 0
										: BRF03.getR3_non_resident_outstand().intValue());
							}
							Row r4row = sheet.getRow(12);

							Cell r4cell = r4row.getCell(5);
							if (r4cell != null) {
								r4cell.setCellValue(BRF03.getR4_resident_outstand() == null ? 0
										: BRF03.getR4_resident_outstand().intValue());
							}
							Cell r4cell1 = r4row.getCell(6);
							if (r4cell1 != null) {
								r4cell1.setCellValue(BRF03.getR4_non_resident_outstand() == null ? 0
										: BRF03.getR4_non_resident_outstand().intValue());
							}
							Row r5row = sheet.getRow(13);

							Cell r5cell = r5row.getCell(5);
							if (r5cell != null) {
								r5cell.setCellValue(BRF03.getR5_resident_outstand() == null ? 0
										: BRF03.getR5_resident_outstand().intValue());
							}
							Cell r5cell1 = r5row.getCell(6);
							if (r5cell1 != null) {
								r5cell1.setCellValue(BRF03.getR5_non_resident_outstand() == null ? 0
										: BRF03.getR5_non_resident_outstand().intValue());
							}
							Row r6row = sheet.getRow(14);

							Cell r6cell = r6row.getCell(5);
							if (r6cell != null) {
								r6cell.setCellValue(BRF03.getR6_resident_outstand() == null ? 0
										: BRF03.getR6_resident_outstand().intValue());
							}
							Cell r6cell1 = r6row.getCell(6);
							if (r6cell1 != null) {
								r6cell1.setCellValue(BRF03.getR6_non_resident_outstand() == null ? 0
										: BRF03.getR6_non_resident_outstand().intValue());
							}
							Row r7row = sheet.getRow(15);

							Cell r7cell = r7row.getCell(5);
							if (r7cell != null) {
								r7cell.setCellValue(BRF03.getR7_resident_outstand() == null ? 0
										: BRF03.getR7_resident_outstand().intValue());
							}
							Cell r7cell1 = r7row.getCell(6);
							if (r7cell1 != null) {
								r7cell1.setCellValue(BRF03.getR7_non_resident_outstand() == null ? 0
										: BRF03.getR7_non_resident_outstand().intValue());
							}
							Row r8row = sheet.getRow(16);

							Cell r8cell = r8row.getCell(5);
							if (r8cell != null) {
								r8cell.setCellValue(BRF03.getR8_resident_outstand() == null ? 0
										: BRF03.getR8_resident_outstand().intValue());
							}
							Cell r8cell1 = r8row.getCell(6);
							if (r8cell1 != null) {
								r8cell1.setCellValue(BRF03.getR8_non_resident_outstand() == null ? 0
										: BRF03.getR8_non_resident_outstand().intValue());
							}
							Row r9row = sheet.getRow(17);

							Cell r9cell = r9row.getCell(5);
							if (r9cell != null) {
								r9cell.setCellValue(BRF03.getR9_resident_outstand() == null ? 0
										: BRF03.getR9_resident_outstand().intValue());
							}
							Cell r9cell1 = r9row.getCell(6);
							if (r9cell1 != null) {
								r9cell1.setCellValue(BRF03.getR9_non_resident_outstand() == null ? 0
										: BRF03.getR9_non_resident_outstand().intValue());
							}
							Row r10row = sheet.getRow(18);

							Cell r10cell = r10row.getCell(5);
							if (r10cell != null) {
								r10cell.setCellValue(BRF03.getR10_resident_outstand() == null ? 0
										: BRF03.getR10_resident_outstand().intValue());
							}
							Cell r10cell1 = r10row.getCell(6);
							if (r10cell1 != null) {
								r10cell1.setCellValue(BRF03.getR10_non_resident_outstand() == null ? 0
										: BRF03.getR10_non_resident_outstand().intValue());
							}
							Row r11row = sheet.getRow(19);

							Cell r11cell = r11row.getCell(5);
							if (r11cell != null) {
								r11cell.setCellValue(BRF03.getR11_resident_outstand() == null ? 0
										: BRF03.getR11_resident_outstand().intValue());
							}
							Cell r11cell1 = r11row.getCell(6);
							if (r11cell1 != null) {
								r11cell1.setCellValue(BRF03.getR11_non_resident_outstand() == null ? 0
										: BRF03.getR11_non_resident_outstand().intValue());
							}
							Row r12row = sheet.getRow(20);

							Cell r12cell = r12row.getCell(5);
							if (r12cell != null) {
								r12cell.setCellValue(BRF03.getR12_resident_outstand() == null ? 0
										: BRF03.getR12_resident_outstand().intValue());
							}
							Cell r12cell1 = r12row.getCell(6);
							if (r12cell1 != null) {
								r12cell1.setCellValue(BRF03.getR12_non_resident_outstand() == null ? 0
										: BRF03.getR12_non_resident_outstand().intValue());
							}
							Row r13row = sheet.getRow(21);

							Cell r13cell = r13row.getCell(5);
							if (r13cell != null) {
								r13cell.setCellValue(BRF03.getR13_resident_outstand() == null ? 0
										: BRF03.getR13_resident_outstand().intValue());
							}
							Cell r13cell1 = r13row.getCell(6);
							if (r13cell1 != null) {
								r13cell1.setCellValue(BRF03.getR13_non_resident_outstand() == null ? 0
										: BRF03.getR13_non_resident_outstand().intValue());
							}
							Row r14row = sheet.getRow(22);

							Cell r14cell = r14row.getCell(5);
							if (r14cell != null) {
								r14cell.setCellValue(BRF03.getR14_resident_outstand() == null ? 0
										: BRF03.getR14_resident_outstand().intValue());
							}
							Cell r14cell1 = r14row.getCell(6);
							if (r14cell1 != null) {
								r14cell1.setCellValue(BRF03.getR14_non_resident_outstand() == null ? 0
										: BRF03.getR14_non_resident_outstand().intValue());
							}
							Row r15row = sheet.getRow(23);

							Cell r15cell = r15row.getCell(5);
							if (r15cell != null) {
								r15cell.setCellValue(BRF03.getR15_resident_outstand() == null ? 0
										: BRF03.getR15_resident_outstand().intValue());
							}
							Cell r15cell1 = r15row.getCell(6);
							if (r15cell1 != null) {
								r15cell1.setCellValue(BRF03.getR15_non_resident_outstand() == null ? 0
										: BRF03.getR15_non_resident_outstand().intValue());
							}
							Row r16row = sheet.getRow(24);

							Cell r16cell = r16row.getCell(5);
							if (r16cell != null) {
								r16cell.setCellValue(BRF03.getR16_resident_outstand() == null ? 0
										: BRF03.getR16_resident_outstand().intValue());
							}
							Cell r16cell1 = r16row.getCell(6);
							if (r16cell1 != null) {
								r16cell1.setCellValue(BRF03.getR16_non_resident_outstand() == null ? 0
										: BRF03.getR16_non_resident_outstand().intValue());
							}
							Row r17row = sheet.getRow(25);

							Cell r17cell = r17row.getCell(5);
							if (r17cell != null) {
								r17cell.setCellValue(BRF03.getR17_resident_outstand() == null ? 0
										: BRF03.getR17_resident_outstand().intValue());
							}
							Cell r17cell1 = r17row.getCell(6);
							if (r17cell1 != null) {
								r17cell1.setCellValue(BRF03.getR17_non_resident_outstand() == null ? 0
										: BRF03.getR17_non_resident_outstand().intValue());
							}
							Row r18row = sheet.getRow(26);

							Cell r18cell = r18row.getCell(5);
							if (r18cell != null) {
								r18cell.setCellValue(BRF03.getR18_resident_outstand() == null ? 0
										: BRF03.getR18_resident_outstand().intValue());
							}
							Cell r18cell1 = r18row.getCell(6);
							if (r18cell1 != null) {
								r18cell1.setCellValue(BRF03.getR18_non_resident_outstand() == null ? 0
										: BRF03.getR18_non_resident_outstand().intValue());
							}
							Row r29row = sheet.getRow(47);
							Cell r29cell = r29row.getCell(3);
							if (r29cell != null) {
								r29cell.setCellValue(BRF03.getR29_hedging_amount() == null ? 0
										: BRF03.getR29_hedging_amount().intValue());
							}
							Cell r29cell1 = r29row.getCell(4);
							if (r29cell1 != null) {
								r29cell1.setCellValue(BRF03.getR29_trading_amount() == null ? 0
										: BRF03.getR29_trading_amount().intValue());
							}

							Cell r29cell2 = r29row.getCell(5);
							if (r29cell2 != null) {
								r29cell2.setCellValue(BRF03.getR29_resident_amount() == null ? 0
										: BRF03.getR29_resident_amount().intValue());
							}
							Cell r29cell3 = r29row.getCell(6);
							if (r29cell3 != null) {
								r29cell3.setCellValue(BRF03.getR29_non_resident_amount() == null ? 0
										: BRF03.getR29_non_resident_amount().intValue());
							}

							Cell r29cell4 = r29row.getCell(7);
							if (r29cell4 != null) {
								r29cell4.setCellValue(BRF03.getR29_with1year_amount() == null ? 0
										: BRF03.getR29_with1year_amount().intValue());
							}
							Cell r29cell5 = r29row.getCell(8);
							if (r29cell5 != null) {
								r29cell5.setCellValue(BRF03.getR29_1to3year_amount() == null ? 0
										: BRF03.getR29_1to3year_amount().intValue());
							}

							Cell r29cell6 = r29row.getCell(9);
							if (r29cell6 != null) {
								r29cell6.setCellValue(BRF03.getR29_over3year_amount() == null ? 0
										: BRF03.getR29_over3year_amount().intValue());
							}
							Cell r29cell7 = r29row.getCell(10);
							if (r29cell7 != null) {
								r29cell7.setCellValue(BRF03.getR29_10_headging_nl() == null ? 0
										: BRF03.getR29_10_headging_nl().intValue());
							}
///
							Cell r29cell8 = r29row.getCell(11);
							if (r29cell8 != null) {
								r29cell8.setCellValue(BRF03.getR29_10_n_headging_nl_() == null ? 0
										: BRF03.getR29_10_n_headging_nl_().intValue());
							}

							Cell r29cell9 = r29row.getCell(12);
							if (r29cell9 != null) {
								r29cell9.setCellValue(BRF03.getR29_10_trading_nl() == null ? 0
										: BRF03.getR29_10_trading_nl().intValue());
							}
							Cell r29cell10 = r29row.getCell(13);
							if (r29cell10 != null) {
								r29cell10.setCellValue(BRF03.getR29_10_n_trading_nl_() == null ? 0
										: BRF03.getR29_10_n_trading_nl_().intValue());
							}
							Row r30row = sheet.getRow(48);
							Cell r30cell = r30row.getCell(3);
							if (r30cell != null) {
								r30cell.setCellValue(BRF03.getR30_hedging_amount() == null ? 0
										: BRF03.getR30_hedging_amount().intValue());
							}
							Cell r30cell1 = r30row.getCell(4);
							if (r30cell1 != null) {
								r30cell1.setCellValue(BRF03.getR30_trading_amount() == null ? 0
										: BRF03.getR30_trading_amount().intValue());
							}

							Cell r30cell2 = r30row.getCell(5);
							if (r30cell2 != null) {
								r30cell2.setCellValue(BRF03.getR30_resident_amount() == null ? 0
										: BRF03.getR30_resident_amount().intValue());
							}
							Cell r30cell3 = r30row.getCell(6);
							if (r30cell3 != null) {
								r30cell3.setCellValue(BRF03.getR30_non_resident_amount() == null ? 0
										: BRF03.getR30_non_resident_amount().intValue());
							}

							Cell r30cell4 = r30row.getCell(7);
							if (r30cell4 != null) {
								r30cell4.setCellValue(BRF03.getR30_with1year_amount() == null ? 0
										: BRF03.getR30_with1year_amount().intValue());
							}
							Cell r30cell5 = r30row.getCell(8);
							if (r30cell5 != null) {
								r30cell5.setCellValue(BRF03.getR30_1to3year_amount() == null ? 0
										: BRF03.getR30_1to3year_amount().intValue());
							}

							Cell r30cell6 = r30row.getCell(9);
							if (r30cell6 != null) {
								r30cell6.setCellValue(BRF03.getR30_over3year_amount() == null ? 0
										: BRF03.getR30_over3year_amount().intValue());
							}
							Cell r30cell7 = r30row.getCell(10);
							if (r30cell7 != null) {
								r30cell7.setCellValue(BRF03.getR30_10_headging_nl() == null ? 0
										: BRF03.getR30_10_headging_nl().intValue());
							}
///
							Cell r30cell8 = r30row.getCell(11);
							if (r30cell8 != null) {
								r30cell8.setCellValue(BRF03.getR30_10_n_headging_nl_() == null ? 0
										: BRF03.getR30_10_n_headging_nl_().intValue());
							}

							Cell r30cell9 = r30row.getCell(12);
							if (r30cell9 != null) {
								r30cell9.setCellValue(BRF03.getR30_10_trading_nl() == null ? 0
										: BRF03.getR30_10_trading_nl().intValue());
							}
							Cell r30cell10 = r30row.getCell(13);
							if (r30cell10 != null) {
								r30cell10.setCellValue(BRF03.getR30_10_n_trading_nl_() == null ? 0
										: BRF03.getR30_10_n_trading_nl_().intValue());
							}
							Row r31row = sheet.getRow(49);
							Cell r31cell = r31row.getCell(3);
							if (r31cell != null) {
								r31cell.setCellValue(BRF03.getR31_hedging_amount() == null ? 0
										: BRF03.getR31_hedging_amount().intValue());
							}
							Cell r31cell1 = r31row.getCell(4);
							if (r31cell1 != null) {
								r31cell1.setCellValue(BRF03.getR31_trading_amount() == null ? 0
										: BRF03.getR31_trading_amount().intValue());
							}

							Cell r31cell2 = r31row.getCell(5);
							if (r31cell2 != null) {
								r31cell2.setCellValue(BRF03.getR31_resident_amount() == null ? 0
										: BRF03.getR31_resident_amount().intValue());
							}
							Cell r31cell3 = r31row.getCell(6);
							if (r31cell3 != null) {
								r31cell3.setCellValue(BRF03.getR31_non_resident_amount() == null ? 0
										: BRF03.getR31_non_resident_amount().intValue());
							}

							Cell r31cell4 = r31row.getCell(7);
							if (r31cell4 != null) {
								r31cell4.setCellValue(BRF03.getR31_with1year_amount() == null ? 0
										: BRF03.getR31_with1year_amount().intValue());
							}
							Cell r31cell5 = r31row.getCell(8);
							if (r31cell5 != null) {
								r31cell5.setCellValue(BRF03.getR31_1to3year_amount() == null ? 0
										: BRF03.getR31_1to3year_amount().intValue());
							}

							Cell r31cell6 = r31row.getCell(9);
							if (r31cell6 != null) {
								r31cell6.setCellValue(BRF03.getR31_over3year_amount() == null ? 0
										: BRF03.getR31_over3year_amount().intValue());
							}
							Cell r31cell7 = r31row.getCell(10);
							if (r31cell7 != null) {
								r31cell7.setCellValue(BRF03.getR31_10_headging_nl() == null ? 0
										: BRF03.getR31_10_headging_nl().intValue());
							}
///
							Cell r31cell8 = r31row.getCell(11);
							if (r31cell8 != null) {
								r31cell8.setCellValue(BRF03.getR31_10_n_headging_nl_() == null ? 0
										: BRF03.getR31_10_n_headging_nl_().intValue());
							}

							Cell r31cell9 = r31row.getCell(12);
							if (r31cell9 != null) {
								r31cell9.setCellValue(BRF03.getR31_10_trading_nl() == null ? 0
										: BRF03.getR31_10_trading_nl().intValue());
							}
							Cell r31cell10 = r31row.getCell(13);
							if (r31cell10 != null) {
								r31cell10.setCellValue(BRF03.getR31_10_n_trading_nl_() == null ? 0
										: BRF03.getR31_10_n_trading_nl_().intValue());
							}
							Row r32row = sheet.getRow(50);
							Cell r32cell = r32row.getCell(3);
							if (r32cell != null) {
								r32cell.setCellValue(BRF03.getR32_hedging_amount() == null ? 0
										: BRF03.getR32_hedging_amount().intValue());
							}
							Cell r32cell1 = r32row.getCell(4);
							if (r32cell1 != null) {
								r32cell1.setCellValue(BRF03.getR32_trading_amount() == null ? 0
										: BRF03.getR32_trading_amount().intValue());
							}

							Cell r32cell2 = r32row.getCell(5);
							if (r32cell2 != null) {
								r32cell2.setCellValue(BRF03.getR32_resident_amount() == null ? 0
										: BRF03.getR32_resident_amount().intValue());
							}
							Cell r32cell3 = r32row.getCell(6);
							if (r32cell3 != null) {
								r32cell3.setCellValue(BRF03.getR32_non_resident_amount() == null ? 0
										: BRF03.getR32_non_resident_amount().intValue());
							}

							Cell r32cell4 = r32row.getCell(7);
							if (r32cell4 != null) {
								r32cell4.setCellValue(BRF03.getR32_with1year_amount() == null ? 0
										: BRF03.getR32_with1year_amount().intValue());
							}
							Cell r32cell5 = r32row.getCell(8);
							if (r32cell5 != null) {
								r32cell5.setCellValue(BRF03.getR32_1to3year_amount() == null ? 0
										: BRF03.getR32_1to3year_amount().intValue());
							}

							Cell r32cell6 = r32row.getCell(9);
							if (r32cell6 != null) {
								r32cell6.setCellValue(BRF03.getR32_over3year_amount() == null ? 0
										: BRF03.getR32_over3year_amount().intValue());
							}
							Cell r32cell7 = r32row.getCell(10);
							if (r32cell7 != null) {
								r32cell7.setCellValue(BRF03.getR32_10_headging_nl() == null ? 0
										: BRF03.getR32_10_headging_nl().intValue());
							}
///
							Cell r32cell8 = r32row.getCell(11);
							if (r32cell8 != null) {
								r32cell8.setCellValue(BRF03.getR32_10_n_headging_nl_() == null ? 0
										: BRF03.getR32_10_n_headging_nl_().intValue());
							}

							Cell r32cell9 = r32row.getCell(12);
							if (r32cell9 != null) {
								r32cell9.setCellValue(BRF03.getR32_10_trading_nl() == null ? 0
										: BRF03.getR32_10_trading_nl().intValue());
							}
							Cell r32cell10 = r32row.getCell(13);
							if (r32cell10 != null) {
								r32cell10.setCellValue(BRF03.getR32_10_n_trading_nl_() == null ? 0
										: BRF03.getR32_10_n_trading_nl_().intValue());
							}

							Row r33row = sheet.getRow(51);
							Cell r33cell = r33row.getCell(3);
							if (r33cell != null) {
								r33cell.setCellValue(BRF03.getR33_hedging_amount() == null ? 0
										: BRF03.getR33_hedging_amount().intValue());
							}
							Cell r33cell1 = r33row.getCell(4);
							if (r33cell1 != null) {
								r33cell1.setCellValue(BRF03.getR33_trading_amount() == null ? 0
										: BRF03.getR33_trading_amount().intValue());
							}

							Cell r33cell2 = r33row.getCell(5);
							if (r33cell2 != null) {
								r33cell2.setCellValue(BRF03.getR33_resident_amount() == null ? 0
										: BRF03.getR33_resident_amount().intValue());
							}
							Cell r33cell3 = r33row.getCell(6);
							if (r33cell3 != null) {
								r33cell3.setCellValue(BRF03.getR33_non_resident_amount() == null ? 0
										: BRF03.getR33_non_resident_amount().intValue());
							}

							Cell r33cell4 = r33row.getCell(7);
							if (r33cell4 != null) {
								r33cell4.setCellValue(BRF03.getR33_with1year_amount() == null ? 0
										: BRF03.getR33_with1year_amount().intValue());
							}
							Cell r33cell5 = r33row.getCell(8);
							if (r33cell5 != null) {
								r33cell5.setCellValue(BRF03.getR33_1to3year_amount() == null ? 0
										: BRF03.getR33_1to3year_amount().intValue());
							}

							Cell r33cell6 = r33row.getCell(9);
							if (r33cell6 != null) {
								r33cell6.setCellValue(BRF03.getR33_over3year_amount() == null ? 0
										: BRF03.getR33_over3year_amount().intValue());
							}
							Cell r33cell7 = r33row.getCell(10);
							if (r33cell7 != null) {
								r33cell7.setCellValue(BRF03.getR33_10_headging_nl() == null ? 0
										: BRF03.getR33_10_headging_nl().intValue());
							}
///
							Cell r33cell8 = r33row.getCell(11);
							if (r33cell8 != null) {
								r33cell8.setCellValue(BRF03.getR33_10_n_headging_nl_() == null ? 0
										: BRF03.getR33_10_n_headging_nl_().intValue());
							}

							Cell r33cell9 = r33row.getCell(12);
							if (r33cell9 != null) {
								r33cell9.setCellValue(BRF03.getR33_10_trading_nl() == null ? 0
										: BRF03.getR33_10_trading_nl().intValue());
							}
							Cell r33cell10 = r33row.getCell(13);
							if (r33cell10 != null) {
								r33cell10.setCellValue(BRF03.getR33_10_n_trading_nl_() == null ? 0
										: BRF03.getR33_10_n_trading_nl_().intValue());
							}
							Row r34row = sheet.getRow(52);

							Cell r34cell = r34row.getCell(3);
							if (r34cell != null) {
								r34cell.setCellValue(BRF03.getR34_hedging_amount() == null ? 0
										: BRF03.getR34_hedging_amount().intValue());
							}
							Cell r34cell1 = r34row.getCell(4);
							if (r34cell1 != null) {
								r34cell1.setCellValue(BRF03.getR34_trading_amount() == null ? 0
										: BRF03.getR34_trading_amount().intValue());
							}

							Cell r34cell2 = r34row.getCell(5);
							if (r34cell2 != null) {
								r34cell2.setCellValue(BRF03.getR34_resident_amount() == null ? 0
										: BRF03.getR34_resident_amount().intValue());
							}
							Cell r34cell3 = r34row.getCell(6);
							if (r34cell3 != null) {
								r34cell3.setCellValue(BRF03.getR34_non_resident_amount() == null ? 0
										: BRF03.getR34_non_resident_amount().intValue());
							}

							Cell r34cell4 = r34row.getCell(7);
							if (r34cell4 != null) {
								r34cell4.setCellValue(BRF03.getR34_with1year_amount() == null ? 0
										: BRF03.getR34_with1year_amount().intValue());
							}
							Cell r34cell5 = r34row.getCell(8);
							if (r34cell5 != null) {
								r34cell5.setCellValue(BRF03.getR34_1to3year_amount() == null ? 0
										: BRF03.getR34_1to3year_amount().intValue());
							}

							Cell r34cell6 = r34row.getCell(9);
							if (r34cell6 != null) {
								r34cell6.setCellValue(BRF03.getR34_over3year_amount() == null ? 0
										: BRF03.getR34_over3year_amount().intValue());
							}
							Cell r34cell7 = r34row.getCell(10);
							if (r34cell7 != null) {
								r34cell7.setCellValue(BRF03.getR34_10_headging_nl() == null ? 0
										: BRF03.getR34_10_headging_nl().intValue());
							}
///
							Cell r34cell8 = r34row.getCell(11);
							if (r34cell8 != null) {
								r34cell8.setCellValue(BRF03.getR34_10_n_headging_nl_() == null ? 0
										: BRF03.getR34_10_n_headging_nl_().intValue());
							}

							Cell r34cell9 = r34row.getCell(12);
							if (r34cell9 != null) {
								r34cell9.setCellValue(BRF03.getR34_10_trading_nl() == null ? 0
										: BRF03.getR34_10_trading_nl().intValue());
							}
							Cell r34cell10 = r34row.getCell(13);
							if (r34cell10 != null) {
								r34cell10.setCellValue(BRF03.getR34_10_n_trading_nl_() == null ? 0
										: BRF03.getR34_10_n_trading_nl_().intValue());
							}
							Row r35row = sheet.getRow(53);

							Cell r35cell = r35row.getCell(3);
							if (r35cell != null) {
								r35cell.setCellValue(BRF03.getR35_hedging_amount() == null ? 0
										: BRF03.getR35_hedging_amount().intValue());
							}
							Cell r35cell1 = r35row.getCell(4);
							if (r35cell1 != null) {
								r35cell1.setCellValue(BRF03.getR35_trading_amount() == null ? 0
										: BRF03.getR35_trading_amount().intValue());
							}

							Cell r35cell2 = r35row.getCell(5);
							if (r35cell2 != null) {
								r35cell2.setCellValue(BRF03.getR35_resident_amount() == null ? 0
										: BRF03.getR35_resident_amount().intValue());
							}
							Cell r35cell3 = r35row.getCell(6);
							if (r35cell3 != null) {
								r35cell3.setCellValue(BRF03.getR35_non_resident_amount() == null ? 0
										: BRF03.getR35_non_resident_amount().intValue());
							}

							Cell r35cell4 = r35row.getCell(7);
							if (r35cell4 != null) {
								r35cell4.setCellValue(BRF03.getR35_with1year_amount() == null ? 0
										: BRF03.getR35_with1year_amount().intValue());
							}
							Cell r35cell5 = r35row.getCell(8);
							if (r35cell5 != null) {
								r35cell5.setCellValue(BRF03.getR35_1to3year_amount() == null ? 0
										: BRF03.getR35_1to3year_amount().intValue());
							}

							Cell r35cell6 = r35row.getCell(9);
							if (r35cell6 != null) {
								r35cell6.setCellValue(BRF03.getR35_over3year_amount() == null ? 0
										: BRF03.getR35_over3year_amount().intValue());
							}
							Cell r35cell7 = r35row.getCell(10);
							if (r35cell7 != null) {
								r35cell7.setCellValue(BRF03.getR35_10_headging_nl() == null ? 0
										: BRF03.getR35_10_headging_nl().intValue());
							}
///
							Cell r35cell8 = r35row.getCell(11);
							if (r35cell8 != null) {
								r35cell8.setCellValue(BRF03.getR35_10_n_headging_nl_() == null ? 0
										: BRF03.getR35_10_n_headging_nl_().intValue());
							}

							Cell r35cell9 = r35row.getCell(12);
							if (r35cell9 != null) {
								r35cell9.setCellValue(BRF03.getR35_10_trading_nl() == null ? 0
										: BRF03.getR35_10_trading_nl().intValue());
							}
							Cell r35cell10 = r35row.getCell(13);
							if (r35cell10 != null) {
								r35cell10.setCellValue(BRF03.getR35_10_n_trading_nl_() == null ? 0
										: BRF03.getR35_10_n_trading_nl_().intValue());
							}
							Row r36row = sheet.getRow(54);

							Cell r36cell = r36row.getCell(3);
							if (r36cell != null) {
								r36cell.setCellValue(BRF03.getR36_hedging_amount() == null ? 0
										: BRF03.getR36_hedging_amount().intValue());
							}
							Cell r36cell1 = r36row.getCell(4);
							if (r36cell1 != null) {
								r36cell1.setCellValue(BRF03.getR36_trading_amount() == null ? 0
										: BRF03.getR36_trading_amount().intValue());
							}

							Cell r36cell2 = r36row.getCell(5);
							if (r36cell2 != null) {
								r36cell2.setCellValue(BRF03.getR36_resident_amount() == null ? 0
										: BRF03.getR36_resident_amount().intValue());
							}
							Cell r36cell3 = r36row.getCell(6);
							if (r36cell3 != null) {
								r36cell3.setCellValue(BRF03.getR36_non_resident_amount() == null ? 0
										: BRF03.getR36_non_resident_amount().intValue());
							}

							Cell r36cell4 = r36row.getCell(7);
							if (r36cell4 != null) {
								r36cell4.setCellValue(BRF03.getR36_with1year_amount() == null ? 0
										: BRF03.getR36_with1year_amount().intValue());
							}
							Cell r36cell5 = r36row.getCell(8);
							if (r36cell5 != null) {
								r36cell5.setCellValue(BRF03.getR36_1to3year_amount() == null ? 0
										: BRF03.getR36_1to3year_amount().intValue());
							}

							Cell r36cell6 = r36row.getCell(9);
							if (r36cell6 != null) {
								r36cell6.setCellValue(BRF03.getR36_over3year_amount() == null ? 0
										: BRF03.getR36_over3year_amount().intValue());
							}
							Cell r36cell7 = r36row.getCell(10);
							if (r36cell7 != null) {
								r36cell7.setCellValue(BRF03.getR36_10_headging_nl() == null ? 0
										: BRF03.getR36_10_headging_nl().intValue());
							}
///
							Cell r36cell8 = r36row.getCell(11);
							if (r36cell8 != null) {
								r36cell8.setCellValue(BRF03.getR36_10_n_headging_nl_() == null ? 0
										: BRF03.getR36_10_n_headging_nl_().intValue());
							}

							Cell r36cell9 = r36row.getCell(12);
							if (r36cell9 != null) {
								r36cell9.setCellValue(BRF03.getR36_10_trading_nl() == null ? 0
										: BRF03.getR36_10_trading_nl().intValue());
							}
							Cell r36cell10 = r36row.getCell(13);
							if (r36cell10 != null) {
								r36cell10.setCellValue(BRF03.getR36_10_n_trading_nl_() == null ? 0
										: BRF03.getR36_10_n_trading_nl_().intValue());
							}
							Row r37row = sheet.getRow(55);

							Cell r37cell = r37row.getCell(3);
							if (r37cell != null) {
								r37cell.setCellValue(BRF03.getR37_hedging_amount() == null ? 0
										: BRF03.getR37_hedging_amount().intValue());
							}
							Cell r37cell1 = r37row.getCell(4);
							if (r37cell1 != null) {
								r37cell1.setCellValue(BRF03.getR37_trading_amount() == null ? 0
										: BRF03.getR37_trading_amount().intValue());
							}

							Cell r37cell2 = r37row.getCell(5);
							if (r37cell2 != null) {
								r37cell2.setCellValue(BRF03.getR37_resident_amount() == null ? 0
										: BRF03.getR37_resident_amount().intValue());
							}
							Cell r37cell3 = r37row.getCell(6);
							if (r37cell3 != null) {
								r37cell3.setCellValue(BRF03.getR37_non_resident_amount() == null ? 0
										: BRF03.getR37_non_resident_amount().intValue());
							}

							Cell r37cell4 = r37row.getCell(7);
							if (r37cell4 != null) {
								r37cell4.setCellValue(BRF03.getR37_with1year_amount() == null ? 0
										: BRF03.getR37_with1year_amount().intValue());
							}
							Cell r37cell5 = r37row.getCell(8);
							if (r37cell5 != null) {
								r37cell5.setCellValue(BRF03.getR37_1to3year_amount() == null ? 0
										: BRF03.getR37_1to3year_amount().intValue());
							}

							Cell r37cell6 = r37row.getCell(9);
							if (r37cell6 != null) {
								r37cell6.setCellValue(BRF03.getR37_over3year_amount() == null ? 0
										: BRF03.getR37_over3year_amount().intValue());
							}
							Cell r37cell7 = r37row.getCell(10);
							if (r37cell7 != null) {
								r37cell7.setCellValue(BRF03.getR37_10_headging_nl() == null ? 0
										: BRF03.getR37_10_headging_nl().intValue());
							}
///
							Cell r37cell8 = r37row.getCell(11);
							if (r37cell8 != null) {
								r37cell8.setCellValue(BRF03.getR37_10_n_headging_nl_() == null ? 0
										: BRF03.getR37_10_n_headging_nl_().intValue());
							}

							Cell r37cell9 = r37row.getCell(12);
							if (r37cell9 != null) {
								r37cell9.setCellValue(BRF03.getR37_10_trading_nl() == null ? 0
										: BRF03.getR37_10_trading_nl().intValue());
							}
							Cell r37cell10 = r37row.getCell(13);
							if (r37cell10 != null) {
								r37cell10.setCellValue(BRF03.getR37_10_n_trading_nl_() == null ? 0
										: BRF03.getR37_10_n_trading_nl_().intValue());
							}
///row39
							Row r39row = sheet.getRow(61);

							Cell r39cell = r39row.getCell(3);
							if (r39cell != null) {
								r39cell.setCellValue(BRF03.getR39_hedging_amount() == null ? 0
										: BRF03.getR39_hedging_amount().intValue());
							}
							Cell r39cell1 = r39row.getCell(4);
							if (r39cell1 != null) {
								r39cell1.setCellValue(BRF03.getR39_trading_amount() == null ? 0
										: BRF03.getR39_trading_amount().intValue());
							}

							Cell r39cell2 = r39row.getCell(5);
							if (r39cell2 != null) {
								r39cell2.setCellValue(BRF03.getR39_resident_amount() == null ? 0
										: BRF03.getR39_resident_amount().intValue());
							}
							Cell r39cell3 = r39row.getCell(6);
							if (r39cell3 != null) {
								r39cell3.setCellValue(BRF03.getR39_non_resident_amount() == null ? 0
										: BRF03.getR39_non_resident_amount().intValue());
							}

							Cell r39cell4 = r39row.getCell(7);
							if (r39cell4 != null) {
								r39cell4.setCellValue(BRF03.getR39_with1year_amount() == null ? 0
										: BRF03.getR39_with1year_amount().intValue());
							}
							Cell r39cell5 = r39row.getCell(8);
							if (r39cell5 != null) {
								r39cell5.setCellValue(BRF03.getR39_1to3year_amount() == null ? 0
										: BRF03.getR39_1to3year_amount().intValue());
							}

							Cell r39cell6 = r39row.getCell(9);
							if (r39cell6 != null) {
								r39cell6.setCellValue(BRF03.getR39_over3year_amount() == null ? 0
										: BRF03.getR39_over3year_amount().intValue());
							}
							Cell r39cell7 = r39row.getCell(10);
							if (r39cell7 != null) {
								r39cell7.setCellValue(BRF03.getR39_bought_headging_nd() == null ? 0
										: BRF03.getR39_bought_headging_nd().intValue());
							}
///
							Cell r39cell8 = r39row.getCell(11);
							if (r39cell8 != null) {
								r39cell8.setCellValue(BRF03.getR39_sold_headging_nd() == null ? 0
										: BRF03.getR39_sold_headging_nd().intValue());
							}

							Cell r39cell9 = r39row.getCell(12);
							if (r39cell9 != null) {
								r39cell9.setCellValue(BRF03.getR39_bought_trading_nd() == null ? 0
										: BRF03.getR39_bought_trading_nd().intValue());
							}
							Cell r39cell10 = r39row.getCell(13);
							if (r39cell10 != null) {
								r39cell10.setCellValue(BRF03.getR39_sold_trading_nd() == null ? 0
										: BRF03.getR39_sold_trading_nd().intValue());
							}

///row40
							Row r40row = sheet.getRow(62);

							Cell r40cell = r40row.getCell(3);
							if (r40cell != null) {
								r40cell.setCellValue(BRF03.getR40_hedging_amount() == null ? 0
										: BRF03.getR40_hedging_amount().intValue());
							}
							Cell r40cell1 = r40row.getCell(4);
							if (r40cell1 != null) {
								r40cell1.setCellValue(BRF03.getR40_trading_amount() == null ? 0
										: BRF03.getR40_trading_amount().intValue());
							}

							Cell r40cell2 = r40row.getCell(5);
							if (r40cell2 != null) {
								r40cell2.setCellValue(BRF03.getR40_resident_amount() == null ? 0
										: BRF03.getR40_resident_amount().intValue());
							}
							Cell r40cell3 = r40row.getCell(6);
							if (r40cell3 != null) {
								r40cell3.setCellValue(BRF03.getR40_non_resident_amount() == null ? 0
										: BRF03.getR40_non_resident_amount().intValue());
							}

							Cell r40cell4 = r40row.getCell(7);
							if (r40cell4 != null) {
								r40cell4.setCellValue(BRF03.getR40_with1year_amount() == null ? 0
										: BRF03.getR40_with1year_amount().intValue());
							}
							Cell r40cell5 = r40row.getCell(8);
							if (r40cell5 != null) {
								r40cell5.setCellValue(BRF03.getR40_1to3year_amount() == null ? 0
										: BRF03.getR40_1to3year_amount().intValue());
							}

							Cell r40cell6 = r40row.getCell(9);
							if (r40cell6 != null) {
								r40cell6.setCellValue(BRF03.getR40_over3year_amount() == null ? 0
										: BRF03.getR40_over3year_amount().intValue());
							}
							Cell r40cell7 = r40row.getCell(10);
							if (r40cell7 != null) {
								r40cell7.setCellValue(BRF03.getR40_bought_headging_nd() == null ? 0
										: BRF03.getR40_bought_headging_nd().intValue());
							}
///
							Cell r40cell8 = r40row.getCell(11);
							if (r40cell8 != null) {
								r40cell8.setCellValue(BRF03.getR40_sold_headging_nd() == null ? 0
										: BRF03.getR40_sold_headging_nd().intValue());
							}

							Cell r40cell9 = r40row.getCell(12);
							if (r40cell9 != null) {
								r40cell9.setCellValue(BRF03.getR40_bought_trading_nd() == null ? 0
										: BRF03.getR40_bought_trading_nd().intValue());
							}
							Cell r40cell10 = r40row.getCell(13);
							if (r40cell10 != null) {
								r40cell10.setCellValue(BRF03.getR40_sold_trading_nd() == null ? 0
										: BRF03.getR40_sold_trading_nd().intValue());
							}
///row41
							Row r41row = sheet.getRow(63);

							Cell r41cell = r41row.getCell(3);
							if (r41cell != null) {
								r41cell.setCellValue(BRF03.getR41_hedging_amount() == null ? 0
										: BRF03.getR41_hedging_amount().intValue());
							}
							Cell r41cell1 = r41row.getCell(4);
							if (r41cell1 != null) {
								r41cell1.setCellValue(BRF03.getR41_trading_amount() == null ? 0
										: BRF03.getR41_trading_amount().intValue());
							}

							Cell r41cell2 = r41row.getCell(5);
							if (r41cell2 != null) {
								r41cell2.setCellValue(BRF03.getR41_resident_amount() == null ? 0
										: BRF03.getR41_resident_amount().intValue());
							}
							Cell r41cell3 = r41row.getCell(6);
							if (r41cell3 != null) {
								r41cell3.setCellValue(BRF03.getR41_non_resident_amount() == null ? 0
										: BRF03.getR41_non_resident_amount().intValue());
							}

							Cell r41cell4 = r41row.getCell(7);
							if (r41cell4 != null) {
								r41cell4.setCellValue(BRF03.getR41_with1year_amount() == null ? 0
										: BRF03.getR41_with1year_amount().intValue());
							}
							Cell r41cell5 = r41row.getCell(8);
							if (r41cell5 != null) {
								r41cell5.setCellValue(BRF03.getR41_1to3year_amount() == null ? 0
										: BRF03.getR41_1to3year_amount().intValue());
							}

							Cell r41cell6 = r41row.getCell(9);
							if (r41cell6 != null) {
								r41cell6.setCellValue(BRF03.getR41_over3year_amount() == null ? 0
										: BRF03.getR41_over3year_amount().intValue());
							}
							Cell r41cell7 = r41row.getCell(10);
							if (r41cell7 != null) {
								r41cell7.setCellValue(BRF03.getR41_bought_headging_nd() == null ? 0
										: BRF03.getR41_bought_headging_nd().intValue());
							}
///
							Cell r41cell8 = r41row.getCell(11);
							if (r41cell8 != null) {
								r41cell8.setCellValue(BRF03.getR41_sold_headging_nd() == null ? 0
										: BRF03.getR41_sold_headging_nd().intValue());
							}

							Cell r41cell9 = r41row.getCell(12);
							if (r41cell9 != null) {
								r41cell9.setCellValue(BRF03.getR41_bought_trading_nd() == null ? 0
										: BRF03.getR41_bought_trading_nd().intValue());
							}
							Cell r41cell10 = r41row.getCell(13);
							if (r41cell10 != null) {
								r41cell10.setCellValue(BRF03.getR41_sold_trading_nd() == null ? 0
										: BRF03.getR41_sold_trading_nd().intValue());
							}
///row42
							Row r42row = sheet.getRow(64);

							Cell r42cell = r42row.getCell(3);
							if (r42cell != null) {
								r42cell.setCellValue(BRF03.getR42_hedging_amount() == null ? 0
										: BRF03.getR42_hedging_amount().intValue());
							}
							Cell r42cell1 = r42row.getCell(4);
							if (r42cell1 != null) {
								r42cell1.setCellValue(BRF03.getR42_trading_amount() == null ? 0
										: BRF03.getR42_trading_amount().intValue());
							}

							Cell r42cell2 = r42row.getCell(5);
							if (r42cell2 != null) {
								r42cell2.setCellValue(BRF03.getR42_resident_amount() == null ? 0
										: BRF03.getR42_resident_amount().intValue());
							}
							Cell r42cell3 = r42row.getCell(6);
							if (r42cell3 != null) {
								r42cell3.setCellValue(BRF03.getR42_non_resident_amount() == null ? 0
										: BRF03.getR42_non_resident_amount().intValue());
							}

							Cell r42cell4 = r42row.getCell(7);
							if (r42cell4 != null) {
								r42cell4.setCellValue(BRF03.getR42_with1year_amount() == null ? 0
										: BRF03.getR42_with1year_amount().intValue());
							}
							Cell r42cell5 = r42row.getCell(8);
							if (r42cell5 != null) {
								r42cell5.setCellValue(BRF03.getR42_1to3year_amount() == null ? 0
										: BRF03.getR42_1to3year_amount().intValue());
							}

							Cell r42cell6 = r42row.getCell(9);
							if (r42cell6 != null) {
								r42cell6.setCellValue(BRF03.getR42_over3year_amount() == null ? 0
										: BRF03.getR42_over3year_amount().intValue());
							}
							Cell r42cell7 = r42row.getCell(10);
							if (r42cell7 != null) {
								r42cell7.setCellValue(BRF03.getR42_bought_headging_nd() == null ? 0
										: BRF03.getR42_bought_headging_nd().intValue());
							}
///
							Cell r42cell8 = r42row.getCell(11);
							if (r42cell8 != null) {
								r42cell8.setCellValue(BRF03.getR42_sold_headging_nd() == null ? 0
										: BRF03.getR42_sold_headging_nd().intValue());
							}

							Cell r42cell9 = r42row.getCell(12);
							if (r42cell9 != null) {
								r42cell9.setCellValue(BRF03.getR42_bought_trading_nd() == null ? 0
										: BRF03.getR42_bought_trading_nd().intValue());
							}
							Cell r42cell10 = r42row.getCell(13);
							if (r42cell10 != null) {
								r42cell10.setCellValue(BRF03.getR42_sold_trading_nd() == null ? 0
										: BRF03.getR42_sold_trading_nd().intValue());
							}
///row43
							Row r43row = sheet.getRow(65);

							Cell r43cell = r43row.getCell(3);
							if (r43cell != null) {
								r43cell.setCellValue(BRF03.getR43_hedging_amount() == null ? 0
										: BRF03.getR43_hedging_amount().intValue());
							}
							Cell r43cell1 = r43row.getCell(4);
							if (r43cell1 != null) {
								r43cell1.setCellValue(BRF03.getR43_trading_amount() == null ? 0
										: BRF03.getR43_trading_amount().intValue());
							}

							Cell r43cell2 = r43row.getCell(5);
							if (r43cell2 != null) {
								r43cell2.setCellValue(BRF03.getR43_resident_amount() == null ? 0
										: BRF03.getR43_resident_amount().intValue());
							}
							Cell r43cell3 = r43row.getCell(6);
							if (r43cell3 != null) {
								r43cell3.setCellValue(BRF03.getR43_non_resident_amount() == null ? 0
										: BRF03.getR43_non_resident_amount().intValue());
							}

							Cell r43cell4 = r43row.getCell(7);
							if (r43cell4 != null) {
								r43cell4.setCellValue(BRF03.getR43_with1year_amount() == null ? 0
										: BRF03.getR43_with1year_amount().intValue());
							}
							Cell r43cell5 = r43row.getCell(8);
							if (r43cell5 != null) {
								r43cell5.setCellValue(BRF03.getR43_1to3year_amount() == null ? 0
										: BRF03.getR43_1to3year_amount().intValue());
							}

							Cell r43cell6 = r43row.getCell(9);
							if (r43cell6 != null) {
								r43cell6.setCellValue(BRF03.getR43_over3year_amount() == null ? 0
										: BRF03.getR43_over3year_amount().intValue());
							}
							Cell r43cell7 = r43row.getCell(10);
							if (r43cell7 != null) {
								r43cell7.setCellValue(BRF03.getR43_bought_headging_nd() == null ? 0
										: BRF03.getR43_bought_headging_nd().intValue());
							}
///
							Cell r43cell8 = r43row.getCell(11);
							if (r43cell8 != null) {
								r43cell8.setCellValue(BRF03.getR43_sold_headging_nd() == null ? 0
										: BRF03.getR43_sold_headging_nd().intValue());
							}

							Cell r43cell9 = r43row.getCell(12);
							if (r43cell9 != null) {
								r43cell9.setCellValue(BRF03.getR43_bought_trading_nd() == null ? 0
										: BRF03.getR43_bought_trading_nd().intValue());
							}
							Cell r43cell10 = r43row.getCell(13);
							if (r43cell10 != null) {
								r43cell10.setCellValue(BRF03.getR43_sold_trading_nd() == null ? 0
										: BRF03.getR43_sold_trading_nd().intValue());
							}
///row44
							Row r44row = sheet.getRow(66);

							Cell r44cell = r44row.getCell(3);
							if (r44cell != null) {
								r44cell.setCellValue(BRF03.getR44_hedging_amount() == null ? 0
										: BRF03.getR44_hedging_amount().intValue());
							}
							Cell r44cell1 = r44row.getCell(4);
							if (r44cell1 != null) {
								r44cell1.setCellValue(BRF03.getR44_trading_amount() == null ? 0
										: BRF03.getR44_trading_amount().intValue());
							}

							Cell r44cell2 = r44row.getCell(5);
							if (r44cell2 != null) {
								r44cell2.setCellValue(BRF03.getR44_resident_amount() == null ? 0
										: BRF03.getR44_resident_amount().intValue());
							}
							Cell r44cell3 = r44row.getCell(6);
							if (r44cell3 != null) {
								r44cell3.setCellValue(BRF03.getR44_non_resident_amount() == null ? 0
										: BRF03.getR44_non_resident_amount().intValue());
							}

							Cell r44cell4 = r44row.getCell(7);
							if (r44cell4 != null) {
								r44cell4.setCellValue(BRF03.getR44_with1year_amount() == null ? 0
										: BRF03.getR44_with1year_amount().intValue());
							}
							Cell r44cell5 = r44row.getCell(8);
							if (r44cell5 != null) {
								r44cell5.setCellValue(BRF03.getR44_1to3year_amount() == null ? 0
										: BRF03.getR44_1to3year_amount().intValue());
							}

							Cell r44cell6 = r44row.getCell(9);
							if (r44cell6 != null) {
								r44cell6.setCellValue(BRF03.getR44_over3year_amount() == null ? 0
										: BRF03.getR44_over3year_amount().intValue());
							}
							Cell r44cell7 = r44row.getCell(10);
							if (r44cell7 != null) {
								r44cell7.setCellValue(BRF03.getR44_bought_headging_nd() == null ? 0
										: BRF03.getR44_bought_headging_nd().intValue());
							}
///
							Cell r44cell8 = r44row.getCell(11);
							if (r44cell8 != null) {
								r44cell8.setCellValue(BRF03.getR44_sold_headging_nd() == null ? 0
										: BRF03.getR44_sold_headging_nd().intValue());
							}

							Cell r44cell9 = r44row.getCell(12);
							if (r44cell9 != null) {
								r44cell9.setCellValue(BRF03.getR44_bought_trading_nd() == null ? 0
										: BRF03.getR44_bought_trading_nd().intValue());
							}
							Cell r44cell10 = r44row.getCell(13);
							if (r44cell10 != null) {
								r44cell10.setCellValue(BRF03.getR44_sold_trading_nd() == null ? 0
										: BRF03.getR44_sold_trading_nd().intValue());
							}
///row45
							Row r45row = sheet.getRow(67);

							Cell r45cell = r45row.getCell(3);
							if (r45cell != null) {
								r45cell.setCellValue(BRF03.getR45_hedging_amount() == null ? 0
										: BRF03.getR45_hedging_amount().intValue());
							}
							Cell r45cell1 = r45row.getCell(4);
							if (r45cell1 != null) {
								r45cell1.setCellValue(BRF03.getR45_trading_amount() == null ? 0
										: BRF03.getR45_trading_amount().intValue());
							}

							Cell r45cell2 = r45row.getCell(5);
							if (r45cell2 != null) {
								r45cell2.setCellValue(BRF03.getR45_resident_amount() == null ? 0
										: BRF03.getR45_resident_amount().intValue());
							}
							Cell r45cell3 = r45row.getCell(6);
							if (r45cell3 != null) {
								r45cell3.setCellValue(BRF03.getR45_non_resident_amount() == null ? 0
										: BRF03.getR45_non_resident_amount().intValue());
							}

							Cell r45cell4 = r45row.getCell(7);
							if (r45cell4 != null) {
								r45cell4.setCellValue(BRF03.getR45_with1year_amount() == null ? 0
										: BRF03.getR45_with1year_amount().intValue());
							}
							Cell r45cell5 = r45row.getCell(8);
							if (r45cell5 != null) {
								r45cell5.setCellValue(BRF03.getR45_1to3year_amount() == null ? 0
										: BRF03.getR45_1to3year_amount().intValue());
							}

							Cell r45cell6 = r45row.getCell(9);
							if (r45cell6 != null) {
								r45cell6.setCellValue(BRF03.getR45_over3year_amount() == null ? 0
										: BRF03.getR45_over3year_amount().intValue());
							}
							Cell r45cell7 = r45row.getCell(10);
							if (r45cell7 != null) {
								r45cell7.setCellValue(BRF03.getR45_bought_headging_nd() == null ? 0
										: BRF03.getR45_bought_headging_nd().intValue());
							}
///
							Cell r45cell8 = r45row.getCell(11);
							if (r45cell8 != null) {
								r45cell8.setCellValue(BRF03.getR45_sold_headging_nd() == null ? 0
										: BRF03.getR45_sold_headging_nd().intValue());
							}

							Cell r45cell9 = r45row.getCell(12);
							if (r45cell9 != null) {
								r45cell9.setCellValue(BRF03.getR45_bought_trading_nd() == null ? 0
										: BRF03.getR45_bought_trading_nd().intValue());
							}
							Cell r45cell10 = r45row.getCell(13);
							if (r45cell10 != null) {
								r45cell10.setCellValue(BRF03.getR45_sold_trading_nd() == null ? 0
										: BRF03.getR45_sold_trading_nd().intValue());
							}
///row46
							Row r46row = sheet.getRow(68);

							Cell r46cell = r46row.getCell(3);
							if (r46cell != null) {
								r46cell.setCellValue(BRF03.getR45_hedging_amount() == null ? 0
										: BRF03.getR46_hedging_amount().intValue());
							}
							Cell r46cell1 = r46row.getCell(4);
							if (r46cell1 != null) {
								r46cell1.setCellValue(BRF03.getR45_trading_amount() == null ? 0
										: BRF03.getR45_trading_amount().intValue());
							}

							Cell r46cell2 = r46row.getCell(5);
							if (r46cell2 != null) {
								r46cell2.setCellValue(BRF03.getR45_resident_amount() == null ? 0
										: BRF03.getR45_resident_amount().intValue());
							}
							Cell r46cell3 = r46row.getCell(6);
							if (r46cell3 != null) {
								r46cell3.setCellValue(BRF03.getR45_non_resident_amount() == null ? 0
										: BRF03.getR45_non_resident_amount().intValue());
							}

							Cell r46cell4 = r46row.getCell(7);
							if (r46cell4 != null) {
								r46cell4.setCellValue(BRF03.getR45_with1year_amount() == null ? 0
										: BRF03.getR45_with1year_amount().intValue());
							}
							Cell r46cell5 = r46row.getCell(8);
							if (r46cell5 != null) {
								r46cell5.setCellValue(BRF03.getR45_1to3year_amount() == null ? 0
										: BRF03.getR45_1to3year_amount().intValue());
							}

							Cell r46cell6 = r46row.getCell(9);
							if (r46cell6 != null) {
								r46cell6.setCellValue(BRF03.getR45_over3year_amount() == null ? 0
										: BRF03.getR45_over3year_amount().intValue());
							}
							Cell r46cell7 = r46row.getCell(10);
							if (r46cell7 != null) {
								r46cell7.setCellValue(BRF03.getR45_bought_headging_nd() == null ? 0
										: BRF03.getR45_bought_headging_nd().intValue());
							}
///
							Cell r46cell8 = r46row.getCell(11);
							if (r46cell8 != null) {
								r46cell8.setCellValue(BRF03.getR45_sold_headging_nd() == null ? 0
										: BRF03.getR45_sold_headging_nd().intValue());
							}

							Cell r46cell9 = r46row.getCell(12);
							if (r46cell9 != null) {
								r46cell9.setCellValue(BRF03.getR45_bought_trading_nd() == null ? 0
										: BRF03.getR45_bought_trading_nd().intValue());
							}
							Cell r46cell10 = r46row.getCell(13);
							if (r46cell10 != null) {
								r46cell10.setCellValue(BRF03.getR45_sold_trading_nd() == null ? 0
										: BRF03.getR45_sold_trading_nd().intValue());
							}

///row48
							Row r48row = sheet.getRow(74);
							Cell r48cell = r48row.getCell(3);
							if (r48cell != null) {
								r48cell.setCellValue(BRF03.getR48_hedging_amount() == null ? 0
										: BRF03.getR48_hedging_amount().intValue());
							}
							Cell r48cell1 = r48row.getCell(4);
							if (r48cell1 != null) {
								r48cell1.setCellValue(BRF03.getR48_trading_amount() == null ? 0
										: BRF03.getR48_trading_amount().intValue());
							}

							Cell r48cell2 = r48row.getCell(5);
							if (r48cell2 != null) {
								r48cell2.setCellValue(BRF03.getR48_resident_amount() == null ? 0
										: BRF03.getR48_resident_amount().intValue());
							}
							Cell r48cell3 = r48row.getCell(6);
							if (r48cell3 != null) {
								r48cell3.setCellValue(BRF03.getR48_non_resident_amount() == null ? 0
										: BRF03.getR48_non_resident_amount().intValue());
							}

							Cell r48cell4 = r48row.getCell(7);
							if (r48cell4 != null) {
								r48cell4.setCellValue(BRF03.getR48_with1year_amount() == null ? 0
										: BRF03.getR48_with1year_amount().intValue());
							}
							Cell r48cell5 = r48row.getCell(8);
							if (r48cell5 != null) {
								r48cell5.setCellValue(BRF03.getR48_1to3year_amount() == null ? 0
										: BRF03.getR48_1to3year_amount().intValue());
							}

							Cell r48cell6 = r48row.getCell(9);
							if (r48cell6 != null) {
								r48cell6.setCellValue(BRF03.getR48_over3year_amount() == null ? 0
										: BRF03.getR48_over3year_amount().intValue());
							}
							Cell r48cell7 = r48row.getCell(10);
							if (r48cell7 != null) {
								r48cell7.setCellValue(BRF03.getR48_2_headging_nl() == null ? 0
										: BRF03.getR48_2_headging_nl().intValue());
							}
///
							Cell r48cell8 = r48row.getCell(11);
							if (r48cell8 != null) {
								r48cell8.setCellValue(BRF03.getR48_2_n_headging_nl_() == null ? 0
										: BRF03.getR48_2_n_headging_nl_().intValue());
							}

							Cell r48cell9 = r48row.getCell(12);
							if (r48cell9 != null) {
								r48cell9.setCellValue(BRF03.getR48_2_trading_nl() == null ? 0
										: BRF03.getR48_2_trading_nl().intValue());
							}
							Cell r48cell10 = r48row.getCell(13);
							if (r48cell10 != null) {
								r48cell10.setCellValue(BRF03.getR48_2_n_trading_nl_() == null ? 0
										: BRF03.getR48_2_n_trading_nl_().intValue());
							}
///row49
							Row r49row = sheet.getRow(75);
							Cell r49cell = r49row.getCell(3);
							if (r49cell != null) {
								r49cell.setCellValue(BRF03.getR49_hedging_amount() == null ? 0
										: BRF03.getR49_hedging_amount().intValue());
							}
							Cell r49cell1 = r49row.getCell(4);
							if (r49cell1 != null) {
								r49cell1.setCellValue(BRF03.getR49_trading_amount() == null ? 0
										: BRF03.getR49_trading_amount().intValue());
							}

							Cell r49cell2 = r49row.getCell(5);
							if (r49cell2 != null) {
								r49cell2.setCellValue(BRF03.getR49_resident_amount() == null ? 0
										: BRF03.getR49_resident_amount().intValue());
							}
							Cell r49cell3 = r49row.getCell(6);
							if (r49cell3 != null) {
								r49cell3.setCellValue(BRF03.getR49_non_resident_amount() == null ? 0
										: BRF03.getR49_non_resident_amount().intValue());
							}

							Cell r49cell4 = r49row.getCell(7);
							if (r49cell4 != null) {
								r49cell4.setCellValue(BRF03.getR49_with1year_amount() == null ? 0
										: BRF03.getR49_with1year_amount().intValue());
							}
							Cell r49cell5 = r49row.getCell(8);
							if (r49cell5 != null) {
								r49cell5.setCellValue(BRF03.getR49_1to3year_amount() == null ? 0
										: BRF03.getR49_1to3year_amount().intValue());
							}

							Cell r49cell6 = r49row.getCell(9);
							if (r49cell6 != null) {
								r49cell6.setCellValue(BRF03.getR49_over3year_amount() == null ? 0
										: BRF03.getR49_over3year_amount().intValue());
							}
							Cell r49cell7 = r49row.getCell(10);
							if (r49cell7 != null) {
								r49cell7.setCellValue(BRF03.getR49_2_headging_nl() == null ? 0
										: BRF03.getR49_2_headging_nl().intValue());
							}
///
							Cell r49cell8 = r49row.getCell(11);
							if (r49cell8 != null) {
								r49cell8.setCellValue(BRF03.getR49_2_n_headging_nl_() == null ? 0
										: BRF03.getR49_2_n_headging_nl_().intValue());
							}

							Cell r49cell9 = r49row.getCell(12);
							if (r49cell9 != null) {
								r49cell9.setCellValue(BRF03.getR49_2_trading_nl() == null ? 0
										: BRF03.getR49_2_trading_nl().intValue());
							}
							Cell r49cell10 = r49row.getCell(13);
							if (r49cell10 != null) {
								r49cell10.setCellValue(BRF03.getR49_2_n_trading_nl_() == null ? 0
										: BRF03.getR49_2_n_trading_nl_().intValue());
							}
///row51
							Row r51row = sheet.getRow(78);

							/*
							 * Cell r51cell = r51row.getCell(3); if (r51cell != null) {
							 * r51cell.setCellValue( BRF03.getR49_hedging_amount() == null ? 0 :
							 * BRF03.getR49_hedging_amount().intValue()); } Cell r51cell1 =
							 * r51row.getCell(4); if (r51cell1 != null) { r51cell1.setCellValue(
							 * BRF03.getR49_trading_amount() == null ? 0 :
							 * BRF03.getR49_trading_amount().intValue()); }
							 */

							Cell r51cell2 = r51row.getCell(5);
							if (r51cell2 != null) {
								r51cell2.setCellValue(BRF03.getR49_resident_amount() == null ? 0
										: BRF03.getR51_resident_amount().intValue());
							}
							Cell r51cell3 = r51row.getCell(6);
							if (r51cell3 != null) {
								r51cell3.setCellValue(BRF03.getR49_non_resident_amount() == null ? 0
										: BRF03.getR49_non_resident_amount().intValue());
							}

							Cell r51cell4 = r51row.getCell(7);
							if (r51cell4 != null) {
								r51cell4.setCellValue(BRF03.getR49_with1year_amount() == null ? 0
										: BRF03.getR49_with1year_amount().intValue());
							}
							Cell r51cell5 = r51row.getCell(8);
							if (r51cell5 != null) {
								r51cell5.setCellValue(BRF03.getR49_1to3year_amount() == null ? 0
										: BRF03.getR49_1to3year_amount().intValue());
							}

							Cell r51cell6 = r51row.getCell(9);
							if (r51cell6 != null) {
								r51cell6.setCellValue(BRF03.getR49_over3year_amount() == null ? 0
										: BRF03.getR49_over3year_amount().intValue());
							}
							Cell r51cell7 = r51row.getCell(10);
							if (r51cell7 != null) {
								r51cell7.setCellValue(BRF03.getR49_2_headging_nl() == null ? 0
										: BRF03.getR49_2_headging_nl().intValue());
							}
///
							Cell r51cell8 = r51row.getCell(11);
							if (r51cell8 != null) {
								r51cell8.setCellValue(BRF03.getR49_2_n_headging_nl_() == null ? 0
										: BRF03.getR49_2_n_headging_nl_().intValue());
							}

							Cell r51cell9 = r51row.getCell(12);
							if (r51cell9 != null) {
								r51cell9.setCellValue(BRF03.getR49_2_trading_nl() == null ? 0
										: BRF03.getR49_2_trading_nl().intValue());
							}
							Cell r51cell10 = r51row.getCell(13);
							if (r51cell10 != null) {
								r51cell10.setCellValue(BRF03.getR49_2_n_trading_nl_() == null ? 0
										: BRF03.getR49_2_n_trading_nl_().intValue());
							}
							/// row52
							Row r52row = sheet.getRow(83);
							Cell r52cell = r52row.getCell(3);
							if (r52cell != null) {
								r52cell.setCellValue(BRF03.getR52_hedging_amount() == null ? 0
										: BRF03.getR52_hedging_amount().intValue());
							}
							Cell r52cell1 = r52row.getCell(4);
							if (r52cell1 != null) {
								r52cell1.setCellValue(BRF03.getR52_trading_amount() == null ? 0
										: BRF03.getR52_trading_amount().intValue());
							}

							Cell r52cell2 = r52row.getCell(5);
							if (r52cell2 != null) {
								r52cell2.setCellValue(BRF03.getR52_resident_amount() == null ? 0
										: BRF03.getR52_resident_amount().intValue());
							}
							Cell r52cell3 = r52row.getCell(6);
							if (r52cell3 != null) {
								r52cell3.setCellValue(BRF03.getR52_non_resident_amount() == null ? 0
										: BRF03.getR52_non_resident_amount().intValue());
							}

							Cell r52cell4 = r52row.getCell(7);
							if (r52cell4 != null) {
								r52cell4.setCellValue(BRF03.getR52_with1year_amount() == null ? 0
										: BRF03.getR52_with1year_amount().intValue());
							}
							Cell r52cell5 = r52row.getCell(8);
							if (r52cell5 != null) {
								r52cell5.setCellValue(BRF03.getR52_1to3year_amount() == null ? 0
										: BRF03.getR52_1to3year_amount().intValue());
							}

							Cell r52cell6 = r52row.getCell(9);
							if (r52cell6 != null) {
								r52cell6.setCellValue(BRF03.getR52_over3year_amount() == null ? 0
										: BRF03.getR52_over3year_amount().intValue());
							}
							Cell r52cell7 = r52row.getCell(10);
							if (r52cell7 != null) {
								r52cell7.setCellValue(BRF03.getR52_10_headging_nl() == null ? 0
										: BRF03.getR52_10_headging_nl().intValue());
							}
///
							Cell r52cell8 = r52row.getCell(11);
							if (r52cell8 != null) {
								r52cell8.setCellValue(BRF03.getR52_10_n_headging_nl_() == null ? 0
										: BRF03.getR52_10_n_headging_nl_().intValue());
							}

							Cell r52cell9 = r52row.getCell(12);
							if (r52cell9 != null) {
								r52cell9.setCellValue(BRF03.getR52_10_trading_nl() == null ? 0
										: BRF03.getR52_10_trading_nl().intValue());
							}
							Cell r52cell10 = r52row.getCell(13);
							if (r52cell10 != null) {
								r52cell10.setCellValue(BRF03.getR52_10_n_trading_nl_() == null ? 0
										: BRF03.getR52_10_n_trading_nl_().intValue());
							}
///row53
							Row r53row = sheet.getRow(84);
							Cell r53cell = r53row.getCell(3);
							if (r53cell != null) {
								r53cell.setCellValue(BRF03.getR53_hedging_amount() == null ? 0
										: BRF03.getR53_hedging_amount().intValue());
							}
							Cell r53cell1 = r53row.getCell(4);
							if (r53cell1 != null) {
								r53cell1.setCellValue(BRF03.getR53_trading_amount() == null ? 0
										: BRF03.getR53_trading_amount().intValue());
							}

							Cell r53cell2 = r53row.getCell(5);
							if (r53cell2 != null) {
								r53cell2.setCellValue(BRF03.getR53_resident_amount() == null ? 0
										: BRF03.getR53_resident_amount().intValue());
							}
							Cell r53cell3 = r53row.getCell(6);
							if (r53cell3 != null) {
								r53cell3.setCellValue(BRF03.getR53_non_resident_amount() == null ? 0
										: BRF03.getR53_non_resident_amount().intValue());
							}

							Cell r53cell4 = r53row.getCell(7);
							if (r53cell4 != null) {
								r53cell4.setCellValue(BRF03.getR53_with1year_amount() == null ? 0
										: BRF03.getR53_with1year_amount().intValue());
							}
							Cell r53cell5 = r53row.getCell(8);
							if (r53cell5 != null) {
								r53cell5.setCellValue(BRF03.getR53_1to3year_amount() == null ? 0
										: BRF03.getR53_1to3year_amount().intValue());
							}

							Cell r53cell6 = r53row.getCell(9);
							if (r53cell6 != null) {
								r53cell6.setCellValue(BRF03.getR53_over3year_amount() == null ? 0
										: BRF03.getR53_over3year_amount().intValue());
							}
							Cell r53cell7 = r53row.getCell(10);
							if (r53cell7 != null) {
								r53cell7.setCellValue(BRF03.getR53_10_headging_nl() == null ? 0
										: BRF03.getR53_10_headging_nl().intValue());
							}
///
							Cell r53cell8 = r53row.getCell(11);
							if (r53cell8 != null) {
								r53cell8.setCellValue(BRF03.getR53_10_n_headging_nl_() == null ? 0
										: BRF03.getR53_10_n_headging_nl_().intValue());
							}

							Cell r53cell9 = r53row.getCell(12);
							if (r53cell9 != null) {
								r53cell9.setCellValue(BRF03.getR53_10_trading_nl() == null ? 0
										: BRF03.getR53_10_trading_nl().intValue());
							}
							Cell r53cell10 = r53row.getCell(13);
							if (r53cell10 != null) {
								r53cell10.setCellValue(BRF03.getR53_10_n_trading_nl_() == null ? 0
										: BRF03.getR53_10_n_trading_nl_().intValue());
							}
///row54
							Row r54row = sheet.getRow(90);
							Cell r54cell = r54row.getCell(3);
							if (r54cell != null) {
								r54cell.setCellValue(BRF03.getR54_pro_bou_amount() == null ? 0
										: BRF03.getR54_pro_bou_amount().intValue());
							}
							Cell r54cell1 = r54row.getCell(4);
							if (r54cell1 != null) {
								r54cell1.setCellValue(BRF03.getR54_pro_sold_amount() == null ? 0
										: BRF03.getR54_pro_sold_amount().intValue());
							}

							Cell r54cell2 = r54row.getCell(5);
							if (r54cell2 != null) {
								r54cell2.setCellValue(
										BRF03.getR54_resident() == null ? 0 : BRF03.getR54_resident().intValue());
							}
							Cell r54cell3 = r54row.getCell(6);
							if (r54cell3 != null) {
								r54cell3.setCellValue(BRF03.getR54_non_resident() == null ? 0
										: BRF03.getR54_non_resident().intValue());
							}

							Cell r54cell4 = r54row.getCell(7);
							if (r54cell4 != null) {
								r54cell4.setCellValue(BRF03.getR54_with1year_amount() == null ? 0
										: BRF03.getR54_with1year_amount().intValue());
							}
							Cell r54cell5 = r54row.getCell(8);
							if (r54cell5 != null) {
								r54cell5.setCellValue(BRF03.getR54_1to3year_amount() == null ? 0
										: BRF03.getR54_1to3year_amount().intValue());
							}

							Cell r54cell6 = r54row.getCell(9);
							if (r54cell6 != null) {
								r54cell6.setCellValue(BRF03.getR54_over3year_amount() == null ? 0
										: BRF03.getR54_over3year_amount().intValue());
							}

							Cell r54cell9 = r54row.getCell(12);
							if (r54cell9 != null) {
								r54cell9.setCellValue(
										BRF03.getR54_pro_bou_ngl() == null ? 0 : BRF03.getR54_pro_bou_ngl().intValue());
							}
							Cell r54cell10 = r54row.getCell(13);
							if (r54cell10 != null) {
								r54cell10.setCellValue(BRF03.getR54_pro_sold_ngl() == null ? 0
										: BRF03.getR54_pro_sold_ngl().intValue());
							}
///row55
							Row r55row = sheet.getRow(91);
							Cell r55cell = r55row.getCell(3);
							if (r55cell != null) {
								r55cell.setCellValue(BRF03.getR55_pro_bou_amount() == null ? 0
										: BRF03.getR55_pro_bou_amount().intValue());
							}
							Cell r55cell1 = r55row.getCell(4);
							if (r55cell1 != null) {
								r55cell1.setCellValue(BRF03.getR55_pro_sold_amount() == null ? 0
										: BRF03.getR55_pro_sold_amount().intValue());
							}

							Cell r55cell2 = r55row.getCell(5);
							if (r55cell2 != null) {
								r55cell2.setCellValue(
										BRF03.getR55_resident() == null ? 0 : BRF03.getR55_resident().intValue());
							}
							Cell r55cell3 = r55row.getCell(6);
							if (r55cell3 != null) {
								r55cell3.setCellValue(BRF03.getR55_non_resident() == null ? 0
										: BRF03.getR55_non_resident().intValue());
							}

							Cell r55cell4 = r55row.getCell(7);
							if (r55cell4 != null) {
								r55cell4.setCellValue(BRF03.getR55_with1year_amount() == null ? 0
										: BRF03.getR55_with1year_amount().intValue());
							}
							Cell r55cell5 = r55row.getCell(8);
							if (r55cell5 != null) {
								r55cell5.setCellValue(BRF03.getR55_1to3year_amount() == null ? 0
										: BRF03.getR55_1to3year_amount().intValue());
							}

							Cell r55cell6 = r55row.getCell(9);
							if (r55cell6 != null) {
								r55cell6.setCellValue(BRF03.getR55_over3year_amount() == null ? 0
										: BRF03.getR55_over3year_amount().intValue());
							}

							Cell r55cell9 = r55row.getCell(12);
							if (r55cell9 != null) {
								r55cell9.setCellValue(
										BRF03.getR55_pro_bou_ngl() == null ? 0 : BRF03.getR55_pro_bou_ngl().intValue());
							}
							Cell r55cell10 = r55row.getCell(13);
							if (r55cell10 != null) {
								r55cell10.setCellValue(BRF03.getR55_pro_sold_ngl() == null ? 0
										: BRF03.getR55_pro_sold_ngl().intValue());
							}
///row56
							Row r56row = sheet.getRow(92);
							Cell r56cell = r56row.getCell(3);
							if (r56cell != null) {
								r56cell.setCellValue(BRF03.getR56_pro_bou_amount() == null ? 0
										: BRF03.getR56_pro_bou_amount().intValue());
							}
							Cell r56cell1 = r56row.getCell(4);
							if (r56cell1 != null) {
								r56cell1.setCellValue(BRF03.getR56_pro_sold_amount() == null ? 0
										: BRF03.getR56_pro_sold_amount().intValue());
							}

							Cell r56cell2 = r56row.getCell(5);
							if (r56cell2 != null) {
								r56cell2.setCellValue(
										BRF03.getR56_resident() == null ? 0 : BRF03.getR56_resident().intValue());
							}
							Cell r56cell3 = r56row.getCell(6);
							if (r56cell3 != null) {
								r56cell3.setCellValue(BRF03.getR56_non_resident() == null ? 0
										: BRF03.getR56_non_resident().intValue());
							}

							Cell r56cell4 = r56row.getCell(7);
							if (r56cell4 != null) {
								r56cell4.setCellValue(BRF03.getR56_with1year_amount() == null ? 0
										: BRF03.getR56_with1year_amount().intValue());
							}
							Cell r56cell5 = r56row.getCell(8);
							if (r56cell5 != null) {
								r56cell5.setCellValue(BRF03.getR56_1to3year_amount() == null ? 0
										: BRF03.getR56_1to3year_amount().intValue());
							}

							Cell r56cell6 = r56row.getCell(9);
							if (r56cell6 != null) {
								r56cell6.setCellValue(BRF03.getR56_over3year_amount() == null ? 0
										: BRF03.getR56_over3year_amount().intValue());
							}

							Cell r56cell9 = r56row.getCell(12);
							if (r56cell9 != null) {
								r56cell9.setCellValue(
										BRF03.getR56_pro_bou_ngl() == null ? 0 : BRF03.getR56_pro_bou_ngl().intValue());
							}
							Cell r56cell10 = r56row.getCell(13);
							if (r56cell10 != null) {
								r56cell10.setCellValue(BRF03.getR56_pro_sold_ngl() == null ? 0
										: BRF03.getR56_pro_sold_ngl().intValue());
							}

///row57
							Row r57row = sheet.getRow(93);
							Cell r57cell = r57row.getCell(3);
							if (r57cell != null) {
								r57cell.setCellValue(BRF03.getR57_pro_bou_amount() == null ? 0
										: BRF03.getR57_pro_bou_amount().intValue());
							}
							Cell r57cell1 = r57row.getCell(4);
							if (r57cell1 != null) {
								r57cell1.setCellValue(BRF03.getR57_pro_sold_amount() == null ? 0
										: BRF03.getR57_pro_sold_amount().intValue());
							}

							Cell r57cell2 = r57row.getCell(5);
							if (r57cell2 != null) {
								r57cell2.setCellValue(
										BRF03.getR57_resident() == null ? 0 : BRF03.getR57_resident().intValue());
							}
							Cell r57cell3 = r57row.getCell(6);
							if (r57cell3 != null) {
								r57cell3.setCellValue(BRF03.getR57_non_resident() == null ? 0
										: BRF03.getR57_non_resident().intValue());
							}

							Cell r57cell4 = r57row.getCell(7);
							if (r57cell4 != null) {
								r57cell4.setCellValue(BRF03.getR57_with1year_amount() == null ? 0
										: BRF03.getR57_with1year_amount().intValue());
							}
							Cell r57cell5 = r57row.getCell(8);
							if (r57cell5 != null) {
								r57cell5.setCellValue(BRF03.getR57_1to3year_amount() == null ? 0
										: BRF03.getR57_1to3year_amount().intValue());
							}

							Cell r57cell6 = r57row.getCell(9);
							if (r57cell6 != null) {
								r57cell6.setCellValue(BRF03.getR57_over3year_amount() == null ? 0
										: BRF03.getR57_over3year_amount().intValue());
							}

							Cell r57cell9 = r57row.getCell(12);
							if (r57cell9 != null) {
								r57cell9.setCellValue(
										BRF03.getR57_pro_bou_ngl() == null ? 0 : BRF03.getR57_pro_bou_ngl().intValue());
							}
							Cell r57cell10 = r57row.getCell(13);
							if (r57cell10 != null) {
								r57cell10.setCellValue(BRF03.getR57_pro_sold_ngl() == null ? 0
										: BRF03.getR57_pro_sold_ngl().intValue());
							}
///row58
							Row r58row = sheet.getRow(94);
							Cell r58cell = r58row.getCell(3);
							if (r58cell != null) {
								r58cell.setCellValue(BRF03.getR58_pro_bou_amount() == null ? 0
										: BRF03.getR58_pro_bou_amount().intValue());
							}
							Cell r58cell1 = r58row.getCell(4);
							if (r58cell1 != null) {
								r58cell1.setCellValue(BRF03.getR58_pro_sold_amount() == null ? 0
										: BRF03.getR58_pro_sold_amount().intValue());
							}

							Cell r58cell2 = r58row.getCell(5);
							if (r58cell2 != null) {
								r58cell2.setCellValue(
										BRF03.getR58_resident() == null ? 0 : BRF03.getR58_resident().intValue());
							}
							Cell r58cell3 = r58row.getCell(6);
							if (r58cell3 != null) {
								r58cell3.setCellValue(BRF03.getR58_non_resident() == null ? 0
										: BRF03.getR58_non_resident().intValue());
							}

							Cell r58cell4 = r58row.getCell(7);
							if (r58cell4 != null) {
								r58cell4.setCellValue(BRF03.getR58_with1year_amount() == null ? 0
										: BRF03.getR58_with1year_amount().intValue());
							}
							Cell r58cell5 = r58row.getCell(8);
							if (r58cell5 != null) {
								r58cell5.setCellValue(BRF03.getR58_1to3year_amount() == null ? 0
										: BRF03.getR58_1to3year_amount().intValue());
							}

							Cell r58cell6 = r58row.getCell(9);
							if (r58cell6 != null) {
								r58cell6.setCellValue(BRF03.getR58_over3year_amount() == null ? 0
										: BRF03.getR58_over3year_amount().intValue());
							}

							Cell r58cell9 = r58row.getCell(12);
							if (r58cell9 != null) {
								r58cell9.setCellValue(
										BRF03.getR58_pro_bou_ngl() == null ? 0 : BRF03.getR58_pro_bou_ngl().intValue());
							}
							Cell r58cell10 = r58row.getCell(13);
							if (r58cell10 != null) {
								r58cell10.setCellValue(BRF03.getR58_pro_sold_ngl() == null ? 0
										: BRF03.getR58_pro_sold_ngl().intValue());
							}
///row59
							Row r59row = sheet.getRow(95);
							Cell r59cell = r59row.getCell(3);
							if (r59cell != null) {
								r59cell.setCellValue(BRF03.getR59_pro_bou_amount() == null ? 0
										: BRF03.getR59_pro_bou_amount().intValue());
							}
							Cell r59cell1 = r59row.getCell(4);
							if (r59cell1 != null) {
								r59cell1.setCellValue(BRF03.getR59_pro_sold_amount() == null ? 0
										: BRF03.getR59_pro_sold_amount().intValue());
							}

							Cell r59cell2 = r59row.getCell(5);
							if (r59cell2 != null) {
								r59cell2.setCellValue(
										BRF03.getR59_resident() == null ? 0 : BRF03.getR59_resident().intValue());
							}
							Cell r59cell3 = r59row.getCell(6);
							if (r59cell3 != null) {
								r59cell3.setCellValue(BRF03.getR59_non_resident() == null ? 0
										: BRF03.getR59_non_resident().intValue());
							}

							Cell r59cell4 = r59row.getCell(7);
							if (r59cell4 != null) {
								r59cell4.setCellValue(BRF03.getR59_with1year_amount() == null ? 0
										: BRF03.getR59_with1year_amount().intValue());
							}
							Cell r59cell5 = r59row.getCell(8);
							if (r59cell5 != null) {
								r59cell5.setCellValue(BRF03.getR59_1to3year_amount() == null ? 0
										: BRF03.getR59_1to3year_amount().intValue());
							}

							Cell r59cell6 = r59row.getCell(9);
							if (r59cell6 != null) {
								r59cell6.setCellValue(BRF03.getR59_over3year_amount() == null ? 0
										: BRF03.getR59_over3year_amount().intValue());
							}

							Cell r59cell9 = r59row.getCell(12);
							if (r59cell9 != null) {
								r59cell9.setCellValue(
										BRF03.getR59_pro_bou_ngl() == null ? 0 : BRF03.getR59_pro_bou_ngl().intValue());
							}
							Cell r59cell10 = r59row.getCell(13);
							if (r59cell10 != null) {
								r59cell10.setCellValue(BRF03.getR59_pro_sold_ngl() == null ? 0
										: BRF03.getR59_pro_sold_ngl().intValue());
							}
///row60
							Row r60row = sheet.getRow(96);
							Cell r60cell = r60row.getCell(3);
							if (r60cell != null) {
								r60cell.setCellValue(BRF03.getR60_pro_bou_amount() == null ? 0
										: BRF03.getR60_pro_bou_amount().intValue());
							}
							Cell r60cell1 = r60row.getCell(4);
							if (r60cell1 != null) {
								r60cell1.setCellValue(BRF03.getR60_pro_sold_amount() == null ? 0
										: BRF03.getR60_pro_sold_amount().intValue());
							}

							Cell r60cell2 = r60row.getCell(5);
							if (r60cell2 != null) {
								r60cell2.setCellValue(
										BRF03.getR60_resident() == null ? 0 : BRF03.getR60_resident().intValue());
							}
							Cell r60cell3 = r60row.getCell(6);
							if (r60cell3 != null) {
								r60cell3.setCellValue(BRF03.getR60_non_resident() == null ? 0
										: BRF03.getR60_non_resident().intValue());
							}

							Cell r60cell4 = r60row.getCell(7);
							if (r60cell4 != null) {
								r60cell4.setCellValue(BRF03.getR60_with1year_amount() == null ? 0
										: BRF03.getR60_with1year_amount().intValue());
							}
							Cell r60cell5 = r60row.getCell(8);
							if (r60cell5 != null) {
								r60cell5.setCellValue(BRF03.getR60_1to3year_amount() == null ? 0
										: BRF03.getR60_1to3year_amount().intValue());
							}

							Cell r60cell6 = r60row.getCell(9);
							if (r60cell6 != null) {
								r60cell6.setCellValue(BRF03.getR60_over3year_amount() == null ? 0
										: BRF03.getR60_over3year_amount().intValue());
							}

							Cell r60cell9 = r60row.getCell(12);
							if (r60cell9 != null) {
								r60cell9.setCellValue(
										BRF03.getR60_pro_bou_ngl() == null ? 0 : BRF03.getR60_pro_bou_ngl().intValue());
							}
							Cell r60cell10 = r60row.getCell(13);
							if (r60cell10 != null) {
								r60cell10.setCellValue(BRF03.getR60_pro_sold_ngl() == null ? 0
										: BRF03.getR60_pro_sold_ngl().intValue());
							}
///row61
							Row r61row = sheet.getRow(97);

							Cell r61cell = r61row.getCell(3);
							if (r61cell != null) {
								r61cell.setCellValue(BRF03.getR61_pro_bou_amount() == null ? 0
										: BRF03.getR61_pro_bou_amount().intValue());
							}
							Cell r61cell1 = r61row.getCell(4);
							if (r61cell1 != null) {
								r61cell1.setCellValue(BRF03.getR61_pro_sold_amount() == null ? 0
										: BRF03.getR61_pro_sold_amount().intValue());
							}

							Cell r61cell2 = r61row.getCell(5);
							if (r61cell2 != null) {
								r61cell2.setCellValue(
										BRF03.getR61_resident() == null ? 0 : BRF03.getR61_resident().intValue());
							}
							Cell r61cell3 = r61row.getCell(6);
							if (r61cell3 != null) {
								r61cell3.setCellValue(BRF03.getR61_non_resident() == null ? 0
										: BRF03.getR61_non_resident().intValue());
							}

							Cell r61cell4 = r61row.getCell(7);
							if (r61cell4 != null) {
								r61cell4.setCellValue(BRF03.getR61_with1year_amount() == null ? 0
										: BRF03.getR61_with1year_amount().intValue());
							}
							Cell r61cell5 = r61row.getCell(8);
							if (r61cell5 != null) {
								r61cell5.setCellValue(BRF03.getR61_1to3year_amount() == null ? 0
										: BRF03.getR61_1to3year_amount().intValue());
							}

							Cell r61cell6 = r61row.getCell(9);
							if (r61cell6 != null) {
								r61cell6.setCellValue(BRF03.getR61_over3year_amount() == null ? 0
										: BRF03.getR61_over3year_amount().intValue());
							}

							Cell r61cell9 = r61row.getCell(12);
							if (r61cell9 != null) {
								r61cell9.setCellValue(
										BRF03.getR61_pro_bou_ngl() == null ? 0 : BRF03.getR61_pro_bou_ngl().intValue());
							}
							Cell r61cell10 = r61row.getCell(13);
							if (r61cell10 != null) {
								r61cell10.setCellValue(BRF03.getR61_pro_sold_ngl() == null ? 0
										: BRF03.getR61_pro_sold_ngl().intValue());
							}
///row62
							Row r62row = sheet.getRow(98);
							Cell r62cell = r62row.getCell(3);
							if (r62cell != null) {
								r62cell.setCellValue(BRF03.getR62_pro_bou_amount() == null ? 0
										: BRF03.getR62_pro_bou_amount().intValue());
							}
							Cell r62cell1 = r62row.getCell(4);
							if (r62cell1 != null) {
								r62cell1.setCellValue(BRF03.getR62_pro_sold_amount() == null ? 0
										: BRF03.getR62_pro_sold_amount().intValue());
							}

							Cell r62cell2 = r62row.getCell(5);
							if (r62cell2 != null) {
								r62cell2.setCellValue(
										BRF03.getR62_resident() == null ? 0 : BRF03.getR62_resident().intValue());
							}
							Cell r62cell3 = r62row.getCell(6);
							if (r62cell3 != null) {
								r62cell3.setCellValue(BRF03.getR62_non_resident() == null ? 0
										: BRF03.getR62_non_resident().intValue());
							}

							Cell r62cell4 = r62row.getCell(7);
							if (r62cell4 != null) {
								r62cell4.setCellValue(BRF03.getR62_with1year_amount() == null ? 0
										: BRF03.getR62_with1year_amount().intValue());
							}
							Cell r62cell5 = r62row.getCell(8);
							if (r62cell5 != null) {
								r62cell5.setCellValue(BRF03.getR62_1to3year_amount() == null ? 0
										: BRF03.getR62_1to3year_amount().intValue());
							}

							Cell r62cell6 = r62row.getCell(9);
							if (r62cell6 != null) {
								r62cell6.setCellValue(BRF03.getR62_over3year_amount() == null ? 0
										: BRF03.getR62_over3year_amount().intValue());
							}

							Cell r62cell9 = r62row.getCell(12);
							if (r62cell9 != null) {
								r62cell9.setCellValue(
										BRF03.getR62_pro_bou_ngl() == null ? 0 : BRF03.getR62_pro_bou_ngl().intValue());
							}
							Cell r62cell10 = r62row.getCell(13);
							if (r62cell10 != null) {
								r62cell10.setCellValue(BRF03.getR62_pro_sold_ngl() == null ? 0
										: BRF03.getR62_pro_sold_ngl().intValue());
							}
///row63
							Row r63row = sheet.getRow(99);
							Cell r63cell = r63row.getCell(3);
							if (r63cell != null) {
								r63cell.setCellValue(BRF03.getR63_pro_bou_amount() == null ? 0
										: BRF03.getR63_pro_bou_amount().intValue());
							}
							Cell r63cell1 = r63row.getCell(4);
							if (r63cell1 != null) {
								r63cell1.setCellValue(BRF03.getR63_pro_sold_amount() == null ? 0
										: BRF03.getR63_pro_sold_amount().intValue());
							}

							Cell r63cell2 = r63row.getCell(5);
							if (r63cell2 != null) {
								r63cell2.setCellValue(
										BRF03.getR63_resident() == null ? 0 : BRF03.getR63_resident().intValue());
							}
							Cell r63cell3 = r63row.getCell(6);
							if (r63cell3 != null) {
								r63cell3.setCellValue(BRF03.getR63_non_resident() == null ? 0
										: BRF03.getR63_non_resident().intValue());
							}

							Cell r63cell4 = r63row.getCell(7);
							if (r63cell4 != null) {
								r63cell4.setCellValue(BRF03.getR63_with1year_amount() == null ? 0
										: BRF03.getR63_with1year_amount().intValue());
							}
							Cell r63cell5 = r63row.getCell(8);
							if (r63cell5 != null) {
								r63cell5.setCellValue(BRF03.getR63_1to3year_amount() == null ? 0
										: BRF03.getR63_1to3year_amount().intValue());
							}

							Cell r63cell6 = r63row.getCell(9);
							if (r63cell6 != null) {
								r63cell6.setCellValue(BRF03.getR63_over3year_amount() == null ? 0
										: BRF03.getR63_over3year_amount().intValue());
							}

							Cell r63cell9 = r63row.getCell(12);
							if (r63cell9 != null) {
								r63cell9.setCellValue(
										BRF03.getR63_pro_bou_ngl() == null ? 0 : BRF03.getR63_pro_bou_ngl().intValue());
							}
							Cell r63cell10 = r63row.getCell(13);
							if (r63cell10 != null) {
								r63cell10.setCellValue(BRF03.getR63_pro_sold_ngl() == null ? 0
										: BRF03.getR63_pro_sold_ngl().intValue());
							}
///row65
							Row r65row = sheet.getRow(101);
							Cell r65cell = r65row.getCell(3);
							if (r65cell != null) {
								r65cell.setCellValue(BRF03.getR65_pro_bou_amount() == null ? 0
										: BRF03.getR65_pro_bou_amount().intValue());
							}
							Cell r65cell1 = r65row.getCell(4);
							if (r65cell1 != null) {
								r65cell1.setCellValue(BRF03.getR65_pro_sold_amount() == null ? 0
										: BRF03.getR65_pro_sold_amount().intValue());
							}

							Cell r65cell2 = r65row.getCell(5);
							if (r65cell2 != null) {
								r65cell2.setCellValue(
										BRF03.getR65_resident() == null ? 0 : BRF03.getR65_resident().intValue());
							}
							Cell r65cell3 = r65row.getCell(6);
							if (r65cell3 != null) {
								r65cell3.setCellValue(BRF03.getR65_non_resident() == null ? 0
										: BRF03.getR65_non_resident().intValue());
							}

							Cell r65cell4 = r65row.getCell(7);
							if (r65cell4 != null) {
								r65cell4.setCellValue(BRF03.getR65_with1year_amount() == null ? 0
										: BRF03.getR65_with1year_amount().intValue());
							}
							Cell r65cell5 = r65row.getCell(8);
							if (r65cell5 != null) {
								r65cell5.setCellValue(BRF03.getR65_1to3year_amount() == null ? 0
										: BRF03.getR65_1to3year_amount().intValue());
							}

							Cell r65cell6 = r65row.getCell(9);
							if (r65cell6 != null) {
								r65cell6.setCellValue(BRF03.getR65_over3year_amount() == null ? 0
										: BRF03.getR65_over3year_amount().intValue());
							}

							Cell r65cell9 = r65row.getCell(12);
							if (r65cell9 != null) {
								r65cell9.setCellValue(
										BRF03.getR65_pro_bou_ngl() == null ? 0 : BRF03.getR65_pro_bou_ngl().intValue());
							}
							Cell r65cell10 = r65row.getCell(13);
							if (r65cell10 != null) {
								r65cell10.setCellValue(BRF03.getR65_pro_sold_ngl() == null ? 0
										: BRF03.getR65_pro_sold_ngl().intValue());
							}
///row66
							Row r66row = sheet.getRow(102);
							Cell r66cell = r66row.getCell(3);
							if (r66cell != null) {
								r66cell.setCellValue(BRF03.getR66_pro_bou_amount() == null ? 0
										: BRF03.getR66_pro_bou_amount().intValue());
							}
							Cell r66cell1 = r66row.getCell(4);
							if (r66cell1 != null) {
								r66cell1.setCellValue(BRF03.getR66_pro_sold_amount() == null ? 0
										: BRF03.getR66_pro_sold_amount().intValue());
							}

							Cell r66cell2 = r66row.getCell(5);
							if (r66cell2 != null) {
								r66cell2.setCellValue(
										BRF03.getR66_resident() == null ? 0 : BRF03.getR66_resident().intValue());
							}
							Cell r66cell3 = r66row.getCell(6);
							if (r66cell3 != null) {
								r66cell3.setCellValue(BRF03.getR66_non_resident() == null ? 0
										: BRF03.getR66_non_resident().intValue());
							}

							Cell r66cell4 = r66row.getCell(7);
							if (r66cell4 != null) {
								r66cell4.setCellValue(BRF03.getR66_with1year_amount() == null ? 0
										: BRF03.getR66_with1year_amount().intValue());
							}
							Cell r66cell5 = r66row.getCell(8);
							if (r66cell5 != null) {
								r66cell5.setCellValue(BRF03.getR66_1to3year_amount() == null ? 0
										: BRF03.getR66_1to3year_amount().intValue());
							}

							Cell r66cell6 = r66row.getCell(9);
							if (r66cell6 != null) {
								r66cell6.setCellValue(BRF03.getR66_over3year_amount() == null ? 0
										: BRF03.getR66_over3year_amount().intValue());
							}

							Cell r66cell9 = r66row.getCell(12);
							if (r66cell9 != null) {
								r66cell9.setCellValue(
										BRF03.getR66_pro_bou_ngl() == null ? 0 : BRF03.getR66_pro_bou_ngl().intValue());
							}
							Cell r66cell10 = r66row.getCell(13);
							if (r66cell10 != null) {
								r66cell10.setCellValue(BRF03.getR66_pro_sold_ngl() == null ? 0
										: BRF03.getR66_pro_sold_ngl().intValue());
							}
///row67
							Row r67row = sheet.getRow(103);
							Cell r67cell = r67row.getCell(3);
							if (r67cell != null) {
								r67cell.setCellValue(BRF03.getR67_pro_bou_amount() == null ? 0
										: BRF03.getR67_pro_bou_amount().intValue());
							}
							Cell r67cell1 = r67row.getCell(4);
							if (r67cell1 != null) {
								r67cell1.setCellValue(BRF03.getR67_pro_sold_amount() == null ? 0
										: BRF03.getR67_pro_sold_amount().intValue());
							}

							Cell r67cell2 = r67row.getCell(5);
							if (r67cell2 != null) {
								r67cell2.setCellValue(
										BRF03.getR67_resident() == null ? 0 : BRF03.getR67_resident().intValue());
							}
							Cell r67cell3 = r67row.getCell(6);
							if (r67cell3 != null) {
								r67cell3.setCellValue(BRF03.getR67_non_resident() == null ? 0
										: BRF03.getR67_non_resident().intValue());
							}

							Cell r67cell4 = r67row.getCell(7);
							if (r67cell4 != null) {
								r67cell4.setCellValue(BRF03.getR67_with1year_amount() == null ? 0
										: BRF03.getR67_with1year_amount().intValue());
							}
							Cell r67cell5 = r67row.getCell(8);
							if (r67cell5 != null) {
								r67cell5.setCellValue(BRF03.getR67_1to3year_amount() == null ? 0
										: BRF03.getR67_1to3year_amount().intValue());
							}

							Cell r67cell6 = r67row.getCell(9);
							if (r67cell6 != null) {
								r67cell6.setCellValue(BRF03.getR67_over3year_amount() == null ? 0
										: BRF03.getR67_over3year_amount().intValue());
							}

							Cell r67cell9 = r67row.getCell(12);
							if (r67cell9 != null) {
								r67cell9.setCellValue(
										BRF03.getR67_pro_bou_ngl() == null ? 0 : BRF03.getR67_pro_bou_ngl().intValue());
							}
							Cell r67cell10 = r67row.getCell(13);
							if (r67cell10 != null) {
								r67cell10.setCellValue(BRF03.getR67_pro_sold_ngl() == null ? 0
										: BRF03.getR67_pro_sold_ngl().intValue());
							}

///row68
							Row r68row = sheet.getRow(104);
							Cell r68cell = r68row.getCell(3);
							if (r68cell != null) {
								r68cell.setCellValue(BRF03.getR68_pro_bou_amount() == null ? 0
										: BRF03.getR68_pro_bou_amount().intValue());
							}
							Cell r68cell1 = r68row.getCell(4);
							if (r68cell1 != null) {
								r68cell1.setCellValue(BRF03.getR68_pro_sold_amount() == null ? 0
										: BRF03.getR68_pro_sold_amount().intValue());
							}

							Cell r68cell2 = r68row.getCell(5);
							if (r68cell2 != null) {
								r68cell2.setCellValue(
										BRF03.getR68_resident() == null ? 0 : BRF03.getR68_resident().intValue());
							}
							Cell r68cell3 = r68row.getCell(6);
							if (r68cell3 != null) {
								r68cell3.setCellValue(BRF03.getR68_non_resident() == null ? 0
										: BRF03.getR68_non_resident().intValue());
							}

							Cell r68cell4 = r68row.getCell(7);
							if (r68cell4 != null) {
								r68cell4.setCellValue(BRF03.getR68_with1year_amount() == null ? 0
										: BRF03.getR68_with1year_amount().intValue());
							}
							Cell r68cell5 = r68row.getCell(8);
							if (r68cell5 != null) {
								r68cell5.setCellValue(BRF03.getR68_1to3year_amount() == null ? 0
										: BRF03.getR68_1to3year_amount().intValue());
							}

							Cell r68cell6 = r68row.getCell(9);
							if (r68cell6 != null) {
								r68cell6.setCellValue(BRF03.getR68_over3year_amount() == null ? 0
										: BRF03.getR68_over3year_amount().intValue());
							}

							Cell r68cell9 = r68row.getCell(12);
							if (r68cell9 != null) {
								r68cell9.setCellValue(
										BRF03.getR68_pro_bou_ngl() == null ? 0 : BRF03.getR68_pro_bou_ngl().intValue());
							}
							Cell r68cell10 = r68row.getCell(13);
							if (r68cell10 != null) {
								r68cell10.setCellValue(BRF03.getR68_pro_sold_ngl() == null ? 0
										: BRF03.getR68_pro_sold_ngl().intValue());
							}
///row69
							Row r69row = sheet.getRow(105);
							Cell r69cell = r69row.getCell(3);
							if (r69cell != null) {
								r69cell.setCellValue(BRF03.getR69_pro_bou_amount() == null ? 0
										: BRF03.getR69_pro_bou_amount().intValue());
							}
							Cell r69cell1 = r69row.getCell(4);
							if (r69cell1 != null) {
								r69cell1.setCellValue(BRF03.getR69_pro_sold_amount() == null ? 0
										: BRF03.getR69_pro_sold_amount().intValue());
							}

							Cell r69cell2 = r69row.getCell(5);
							if (r69cell2 != null) {
								r69cell2.setCellValue(
										BRF03.getR69_resident() == null ? 0 : BRF03.getR69_resident().intValue());
							}
							Cell r69cell3 = r69row.getCell(6);
							if (r69cell3 != null) {
								r69cell3.setCellValue(BRF03.getR69_non_resident() == null ? 0
										: BRF03.getR69_non_resident().intValue());
							}

							Cell r69cell4 = r69row.getCell(7);
							if (r69cell4 != null) {
								r69cell4.setCellValue(BRF03.getR69_with1year_amount() == null ? 0
										: BRF03.getR69_with1year_amount().intValue());
							}
							Cell r69cell5 = r69row.getCell(8);
							if (r69cell5 != null) {
								r69cell5.setCellValue(BRF03.getR69_1to3year_amount() == null ? 0
										: BRF03.getR69_1to3year_amount().intValue());
							}

							Cell r69cell6 = r69row.getCell(9);
							if (r69cell6 != null) {
								r69cell6.setCellValue(BRF03.getR69_over3year_amount() == null ? 0
										: BRF03.getR69_over3year_amount().intValue());
							}

							Cell r69cell9 = r69row.getCell(12);
							if (r69cell9 != null) {
								r69cell9.setCellValue(
										BRF03.getR69_pro_bou_ngl() == null ? 0 : BRF03.getR69_pro_bou_ngl().intValue());
							}
							Cell r69cell10 = r69row.getCell(13);
							if (r69cell10 != null) {
								r69cell10.setCellValue(BRF03.getR69_pro_sold_ngl() == null ? 0
										: BRF03.getR69_pro_sold_ngl().intValue());
							}
///row70
							Row r70row = sheet.getRow(106);
							Cell r70cell = r70row.getCell(3);
							if (r70cell != null) {
								r70cell.setCellValue(BRF03.getR70_pro_bou_amount() == null ? 0
										: BRF03.getR70_pro_bou_amount().intValue());
							}
							Cell r70cell1 = r70row.getCell(4);
							if (r70cell1 != null) {
								r70cell1.setCellValue(BRF03.getR70_pro_sold_amount() == null ? 0
										: BRF03.getR70_pro_sold_amount().intValue());
							}

							Cell r70cell2 = r70row.getCell(5);
							if (r70cell2 != null) {
								r70cell2.setCellValue(
										BRF03.getR70_resident() == null ? 0 : BRF03.getR70_resident().intValue());
							}
							Cell r70cell3 = r70row.getCell(6);
							if (r70cell3 != null) {
								r70cell3.setCellValue(BRF03.getR70_non_resident() == null ? 0
										: BRF03.getR70_non_resident().intValue());
							}

							Cell r70cell4 = r70row.getCell(7);
							if (r70cell4 != null) {
								r70cell4.setCellValue(BRF03.getR70_with1year_amount() == null ? 0
										: BRF03.getR70_with1year_amount().intValue());
							}
							Cell r70cell5 = r70row.getCell(8);
							if (r70cell5 != null) {
								r70cell5.setCellValue(BRF03.getR70_1to3year_amount() == null ? 0
										: BRF03.getR70_1to3year_amount().intValue());
							}

							Cell r70cell6 = r70row.getCell(9);
							if (r70cell6 != null) {
								r70cell6.setCellValue(BRF03.getR70_over3year_amount() == null ? 0
										: BRF03.getR70_over3year_amount().intValue());
							}

							Cell r70cell9 = r70row.getCell(12);
							if (r70cell9 != null) {
								r70cell9.setCellValue(
										BRF03.getR70_pro_bou_ngl() == null ? 0 : BRF03.getR70_pro_bou_ngl().intValue());
							}
							Cell r70cell10 = r70row.getCell(13);
							if (r70cell10 != null) {
								r70cell10.setCellValue(BRF03.getR70_pro_sold_ngl() == null ? 0
										: BRF03.getR70_pro_sold_ngl().intValue());
							}
///row71
							Row r71row = sheet.getRow(107);
							Cell r71cell = r71row.getCell(3);
							if (r71cell != null) {
								r71cell.setCellValue(BRF03.getR71_pro_bou_amount() == null ? 0
										: BRF03.getR71_pro_bou_amount().intValue());
							}
							Cell r71cell1 = r71row.getCell(4);
							if (r71cell1 != null) {
								r71cell1.setCellValue(BRF03.getR71_pro_sold_amount() == null ? 0
										: BRF03.getR71_pro_sold_amount().intValue());
							}

							Cell r71cell2 = r71row.getCell(5);
							if (r71cell2 != null) {
								r71cell2.setCellValue(
										BRF03.getR71_resident() == null ? 0 : BRF03.getR71_resident().intValue());
							}
							Cell r71cell3 = r71row.getCell(6);
							if (r71cell3 != null) {
								r71cell3.setCellValue(BRF03.getR71_non_resident() == null ? 0
										: BRF03.getR71_non_resident().intValue());
							}

							Cell r71cell4 = r71row.getCell(7);
							if (r71cell4 != null) {
								r71cell4.setCellValue(BRF03.getR71_with1year_amount() == null ? 0
										: BRF03.getR71_with1year_amount().intValue());
							}
							Cell r71cell5 = r71row.getCell(8);
							if (r71cell5 != null) {
								r71cell5.setCellValue(BRF03.getR71_1to3year_amount() == null ? 0
										: BRF03.getR71_1to3year_amount().intValue());
							}

							Cell r71cell6 = r71row.getCell(9);
							if (r71cell6 != null) {
								r71cell6.setCellValue(BRF03.getR71_over3year_amount() == null ? 0
										: BRF03.getR71_over3year_amount().intValue());
							}

							Cell r71cell9 = r71row.getCell(12);
							if (r71cell9 != null) {
								r71cell9.setCellValue(
										BRF03.getR71_pro_bou_ngl() == null ? 0 : BRF03.getR71_pro_bou_ngl().intValue());
							}
							Cell r71cell10 = r71row.getCell(13);
							if (r71cell10 != null) {
								r71cell10.setCellValue(BRF03.getR71_pro_sold_ngl() == null ? 0
										: BRF03.getR71_pro_sold_ngl().intValue());
							}
///row72
							Row r72row = sheet.getRow(108);
							Cell r72cell = r72row.getCell(3);
							if (r72cell != null) {
								r72cell.setCellValue(BRF03.getR72_pro_bou_amount() == null ? 0
										: BRF03.getR72_pro_bou_amount().intValue());
							}
							Cell r72cell1 = r72row.getCell(4);
							if (r72cell1 != null) {
								r72cell1.setCellValue(BRF03.getR72_pro_sold_amount() == null ? 0
										: BRF03.getR72_pro_sold_amount().intValue());
							}

							Cell r72cell2 = r72row.getCell(5);
							if (r72cell2 != null) {
								r72cell2.setCellValue(
										BRF03.getR72_resident() == null ? 0 : BRF03.getR72_resident().intValue());
							}
							Cell r72cell3 = r72row.getCell(6);
							if (r72cell3 != null) {
								r72cell3.setCellValue(BRF03.getR72_non_resident() == null ? 0
										: BRF03.getR72_non_resident().intValue());
							}

							Cell r72cell4 = r72row.getCell(7);
							if (r72cell4 != null) {
								r72cell4.setCellValue(BRF03.getR72_with1year_amount() == null ? 0
										: BRF03.getR72_with1year_amount().intValue());
							}
							Cell r72cell5 = r72row.getCell(8);
							if (r72cell5 != null) {
								r72cell5.setCellValue(BRF03.getR72_1to3year_amount() == null ? 0
										: BRF03.getR72_1to3year_amount().intValue());
							}

							Cell r72cell6 = r72row.getCell(9);
							if (r72cell6 != null) {
								r72cell6.setCellValue(BRF03.getR72_over3year_amount() == null ? 0
										: BRF03.getR72_over3year_amount().intValue());
							}

							Cell r72cell9 = r72row.getCell(12);
							if (r72cell9 != null) {
								r72cell9.setCellValue(
										BRF03.getR72_pro_bou_ngl() == null ? 0 : BRF03.getR72_pro_bou_ngl().intValue());
							}
							Cell r72cell10 = r72row.getCell(13);
							if (r72cell10 != null) {
								r72cell10.setCellValue(BRF03.getR72_pro_sold_ngl() == null ? 0
										: BRF03.getR72_pro_sold_ngl().intValue());
							}

///row73
							Row r73row = sheet.getRow(109);
							Cell r73cell = r73row.getCell(3);
							if (r73cell != null) {
								r73cell.setCellValue(BRF03.getR73_pro_bou_amount() == null ? 0
										: BRF03.getR73_pro_bou_amount().intValue());
							}
							Cell r73cell1 = r73row.getCell(4);
							if (r73cell1 != null) {
								r73cell1.setCellValue(BRF03.getR73_pro_sold_amount() == null ? 0
										: BRF03.getR73_pro_sold_amount().intValue());
							}

							Cell r73cell2 = r73row.getCell(5);
							if (r73cell2 != null) {
								r73cell2.setCellValue(
										BRF03.getR73_resident() == null ? 0 : BRF03.getR73_resident().intValue());
							}
							Cell r73cell3 = r73row.getCell(6);
							if (r73cell3 != null) {
								r73cell3.setCellValue(BRF03.getR73_non_resident() == null ? 0
										: BRF03.getR73_non_resident().intValue());
							}

							Cell r73cell4 = r73row.getCell(7);
							if (r73cell4 != null) {
								r73cell4.setCellValue(BRF03.getR73_with1year_amount() == null ? 0
										: BRF03.getR73_with1year_amount().intValue());
							}
							Cell r73cell5 = r73row.getCell(8);
							if (r73cell5 != null) {
								r73cell5.setCellValue(BRF03.getR73_1to3year_amount() == null ? 0
										: BRF03.getR73_1to3year_amount().intValue());
							}

							Cell r73cell6 = r73row.getCell(9);
							if (r73cell6 != null) {
								r73cell6.setCellValue(BRF03.getR73_over3year_amount() == null ? 0
										: BRF03.getR73_over3year_amount().intValue());
							}

							Cell r73cell9 = r73row.getCell(12);
							if (r73cell9 != null) {
								r73cell9.setCellValue(
										BRF03.getR73_pro_bou_ngl() == null ? 0 : BRF03.getR73_pro_bou_ngl().intValue());
							}
							Cell r73cell10 = r73row.getCell(13);
							if (r73cell10 != null) {
								r73cell10.setCellValue(BRF03.getR73_pro_sold_ngl() == null ? 0
										: BRF03.getR73_pro_sold_ngl().intValue());
							}
///row74
							Row r74row = sheet.getRow(115);
							Cell r74cell = r74row.getCell(3);
							if (r74cell != null) {
								r74cell.setCellValue(BRF03.getR74_hedging_amount() == null ? 0
										: BRF03.getR74_hedging_amount().intValue());
							}
							Cell r74cell1 = r74row.getCell(4);
							if (r74cell1 != null) {
								r74cell1.setCellValue(BRF03.getR74_trading_amount() == null ? 0
										: BRF03.getR74_trading_amount().intValue());
							}

							Cell r74cell2 = r74row.getCell(5);
							if (r74cell2 != null) {
								r74cell2.setCellValue(BRF03.getR74_resident_amount() == null ? 0
										: BRF03.getR74_resident_amount().intValue());
							}
							Cell r74cell3 = r74row.getCell(6);
							if (r74cell3 != null) {
								r74cell3.setCellValue(BRF03.getR74_non_resident_amount() == null ? 0
										: BRF03.getR74_non_resident_amount().intValue());
							}

							Cell r74cell4 = r74row.getCell(7);
							if (r74cell4 != null) {
								r74cell4.setCellValue(BRF03.getR74_with1year_amount() == null ? 0
										: BRF03.getR74_with1year_amount().intValue());
							}
							Cell r74cell5 = r74row.getCell(8);
							if (r74cell5 != null) {
								r74cell5.setCellValue(BRF03.getR74_1to3year_amount() == null ? 0
										: BRF03.getR74_1to3year_amount().intValue());
							}

							Cell r74cell6 = r74row.getCell(9);
							if (r74cell6 != null) {
								r74cell6.setCellValue(BRF03.getR74_over3year_amount() == null ? 0
										: BRF03.getR74_over3year_amount().intValue());
							}
							Cell r74cell7 = r74row.getCell(10);
							if (r74cell7 != null) {
								r74cell7.setCellValue(BRF03.getR74_10_headging_nl() == null ? 0
										: BRF03.getR74_10_headging_nl().intValue());
							}
///
							Cell r74cell8 = r74row.getCell(11);
							if (r74cell8 != null) {
								r74cell8.setCellValue(BRF03.getR74_10_n_headging_nl_() == null ? 0
										: BRF03.getR74_10_n_headging_nl_().intValue());
							}

							Cell r74cell9 = r74row.getCell(12);
							if (r74cell9 != null) {
								r74cell9.setCellValue(BRF03.getR74_10_trading_nl() == null ? 0
										: BRF03.getR74_10_trading_nl().intValue());
							}
							Cell r74cell10 = r74row.getCell(13);
							if (r74cell10 != null) {
								r74cell10.setCellValue(BRF03.getR74_10_n_trading_nl_() == null ? 0
										: BRF03.getR74_10_n_trading_nl_().intValue());
							}
///row75
							Row r75row = sheet.getRow(116);
							Cell r75cell = r75row.getCell(3);
							if (r75cell != null) {
								r75cell.setCellValue(BRF03.getR75_hedging_amount() == null ? 0
										: BRF03.getR75_hedging_amount().intValue());
							}
							Cell r75cell1 = r75row.getCell(4);
							if (r75cell1 != null) {
								r75cell1.setCellValue(BRF03.getR75_trading_amount() == null ? 0
										: BRF03.getR75_trading_amount().intValue());
							}

							Cell r75cell2 = r75row.getCell(5);
							if (r75cell2 != null) {
								r75cell2.setCellValue(BRF03.getR75_resident_amount() == null ? 0
										: BRF03.getR75_resident_amount().intValue());
							}
							Cell r75cell3 = r75row.getCell(6);
							if (r75cell3 != null) {
								r75cell3.setCellValue(BRF03.getR75_non_resident_amount() == null ? 0
										: BRF03.getR75_non_resident_amount().intValue());
							}

							Cell r75cell4 = r75row.getCell(7);
							if (r75cell4 != null) {
								r75cell4.setCellValue(BRF03.getR75_with1year_amount() == null ? 0
										: BRF03.getR75_with1year_amount().intValue());
							}
							Cell r75cell5 = r75row.getCell(8);
							if (r75cell5 != null) {
								r75cell5.setCellValue(BRF03.getR75_1to3year_amount() == null ? 0
										: BRF03.getR75_1to3year_amount().intValue());
							}

							Cell r75cell6 = r75row.getCell(9);
							if (r75cell6 != null) {
								r75cell6.setCellValue(BRF03.getR75_over3year_amount() == null ? 0
										: BRF03.getR75_over3year_amount().intValue());
							}
							Cell r75cell7 = r75row.getCell(10);
							if (r75cell7 != null) {
								r75cell7.setCellValue(BRF03.getR75_10_headging_nl() == null ? 0
										: BRF03.getR75_10_headging_nl().intValue());
							}
///
							Cell r75cell8 = r75row.getCell(11);
							if (r75cell8 != null) {
								r75cell8.setCellValue(BRF03.getR75_10_n_headging_nl_() == null ? 0
										: BRF03.getR75_10_n_headging_nl_().intValue());
							}

							Cell r75cell9 = r75row.getCell(12);
							if (r75cell9 != null) {
								r75cell9.setCellValue(BRF03.getR75_10_trading_nl() == null ? 0
										: BRF03.getR75_10_trading_nl().intValue());
							}
							Cell r75cell10 = r75row.getCell(13);
							if (r75cell10 != null) {
								r75cell10.setCellValue(BRF03.getR75_10_n_trading_nl_() == null ? 0
										: BRF03.getR75_10_n_trading_nl_().intValue());
							}
///row76
							Row r76row = sheet.getRow(117);
							Cell r76cell = r76row.getCell(3);
							if (r76cell != null) {
								r76cell.setCellValue(BRF03.getR76_hedging_amount() == null ? 0
										: BRF03.getR76_hedging_amount().intValue());
							}
							Cell r76cell1 = r76row.getCell(4);
							if (r76cell1 != null) {
								r76cell1.setCellValue(BRF03.getR76_trading_amount() == null ? 0
										: BRF03.getR76_trading_amount().intValue());
							}

							Cell r76cell2 = r76row.getCell(5);
							if (r76cell2 != null) {
								r76cell2.setCellValue(BRF03.getR76_resident_amount() == null ? 0
										: BRF03.getR76_resident_amount().intValue());
							}
							Cell r76cell3 = r76row.getCell(6);
							if (r76cell3 != null) {
								r76cell3.setCellValue(BRF03.getR76_non_resident_amount() == null ? 0
										: BRF03.getR76_non_resident_amount().intValue());
							}

							Cell r76cell4 = r76row.getCell(7);
							if (r76cell4 != null) {
								r76cell4.setCellValue(BRF03.getR76_with1year_amount() == null ? 0
										: BRF03.getR76_with1year_amount().intValue());
							}
							Cell r76cell5 = r76row.getCell(8);
							if (r76cell5 != null) {
								r76cell5.setCellValue(BRF03.getR76_1to3year_amount() == null ? 0
										: BRF03.getR76_1to3year_amount().intValue());
							}

							Cell r76cell6 = r76row.getCell(9);
							if (r76cell6 != null) {
								r76cell6.setCellValue(BRF03.getR76_over3year_amount() == null ? 0
										: BRF03.getR76_over3year_amount().intValue());
							}
							Cell r76cell7 = r76row.getCell(10);
							if (r76cell7 != null) {
								r76cell7.setCellValue(BRF03.getR76_10_headging_nl() == null ? 0
										: BRF03.getR76_10_headging_nl().intValue());
							}
///
							Cell r76cell8 = r76row.getCell(11);
							if (r76cell8 != null) {
								r76cell8.setCellValue(BRF03.getR76_10_n_headging_nl_() == null ? 0
										: BRF03.getR76_10_n_headging_nl_().intValue());
							}

							Cell r76cell9 = r76row.getCell(12);
							if (r76cell9 != null) {
								r76cell9.setCellValue(BRF03.getR76_10_trading_nl() == null ? 0
										: BRF03.getR76_10_trading_nl().intValue());
							}
							Cell r76cell10 = r76row.getCell(13);
							if (r76cell10 != null) {
								r76cell10.setCellValue(BRF03.getR76_10_n_trading_nl_() == null ? 0
										: BRF03.getR76_10_n_trading_nl_().intValue());
							}
///row77
							Row r77row = sheet.getRow(118);
							Cell r77cell = r77row.getCell(3);
							if (r77cell != null) {
								r77cell.setCellValue(BRF03.getR77_hedging_amount() == null ? 0
										: BRF03.getR77_hedging_amount().intValue());
							}
							Cell r77cell1 = r77row.getCell(4);
							if (r77cell1 != null) {
								r77cell1.setCellValue(BRF03.getR77_trading_amount() == null ? 0
										: BRF03.getR77_trading_amount().intValue());
							}

							Cell r77cell2 = r77row.getCell(5);
							if (r77cell2 != null) {
								r77cell2.setCellValue(BRF03.getR77_resident_amount() == null ? 0
										: BRF03.getR77_resident_amount().intValue());
							}
							Cell r77cell3 = r77row.getCell(6);
							if (r77cell3 != null) {
								r77cell3.setCellValue(BRF03.getR77_non_resident_amount() == null ? 0
										: BRF03.getR77_non_resident_amount().intValue());
							}

							Cell r77cell4 = r77row.getCell(7);
							if (r77cell4 != null) {
								r77cell4.setCellValue(BRF03.getR77_with1year_amount() == null ? 0
										: BRF03.getR77_with1year_amount().intValue());
							}
							Cell r77cell5 = r77row.getCell(8);
							if (r77cell5 != null) {
								r77cell5.setCellValue(BRF03.getR77_1to3year_amount() == null ? 0
										: BRF03.getR77_1to3year_amount().intValue());
							}

							Cell r77cell6 = r77row.getCell(9);
							if (r77cell6 != null) {
								r77cell6.setCellValue(BRF03.getR77_over3year_amount() == null ? 0
										: BRF03.getR77_over3year_amount().intValue());
							}
							Cell r77cell7 = r77row.getCell(10);
							if (r77cell7 != null) {
								r77cell7.setCellValue(BRF03.getR77_10_headging_nl() == null ? 0
										: BRF03.getR77_10_headging_nl().intValue());
							}
///
							Cell r77cell8 = r77row.getCell(11);
							if (r77cell8 != null) {
								r77cell8.setCellValue(BRF03.getR77_10_n_headging_nl_() == null ? 0
										: BRF03.getR77_10_n_headging_nl_().intValue());
							}

							Cell r77cell9 = r77row.getCell(12);
							if (r77cell9 != null) {
								r77cell9.setCellValue(BRF03.getR77_10_trading_nl() == null ? 0
										: BRF03.getR77_10_trading_nl().intValue());
							}
							Cell r77cell10 = r77row.getCell(13);
							if (r77cell10 != null) {
								r77cell10.setCellValue(BRF03.getR77_10_n_trading_nl_() == null ? 0
										: BRF03.getR77_10_n_trading_nl_().intValue());
							}

///row78
							Row r78row = sheet.getRow(119);
							Cell r78cell = r78row.getCell(3);
							if (r78cell != null) {
								r78cell.setCellValue(BRF03.getR78_hedging_amount() == null ? 0
										: BRF03.getR78_hedging_amount().intValue());
							}
							Cell r78cell1 = r78row.getCell(4);
							if (r78cell1 != null) {
								r78cell1.setCellValue(BRF03.getR78_trading_amount() == null ? 0
										: BRF03.getR78_trading_amount().intValue());
							}

							Cell r78cell2 = r78row.getCell(5);
							if (r78cell2 != null) {
								r78cell2.setCellValue(BRF03.getR78_resident_amount() == null ? 0
										: BRF03.getR78_resident_amount().intValue());
							}
							Cell r78cell3 = r78row.getCell(6);
							if (r78cell3 != null) {
								r78cell3.setCellValue(BRF03.getR78_non_resident_amount() == null ? 0
										: BRF03.getR78_non_resident_amount().intValue());
							}

							Cell r78cell4 = r78row.getCell(7);
							if (r78cell4 != null) {
								r78cell4.setCellValue(BRF03.getR78_with1year_amount() == null ? 0
										: BRF03.getR78_with1year_amount().intValue());
							}
							Cell r78cell5 = r78row.getCell(8);
							if (r78cell5 != null) {
								r78cell5.setCellValue(BRF03.getR78_1to3year_amount() == null ? 0
										: BRF03.getR78_1to3year_amount().intValue());
							}

							Cell r78cell6 = r78row.getCell(9);
							if (r78cell6 != null) {
								r78cell6.setCellValue(BRF03.getR78_over3year_amount() == null ? 0
										: BRF03.getR78_over3year_amount().intValue());
							}
							Cell r78cell7 = r78row.getCell(10);
							if (r78cell7 != null) {
								r78cell7.setCellValue(BRF03.getR78_10_headging_nl() == null ? 0
										: BRF03.getR78_10_headging_nl().intValue());
							}
///
							Cell r78cell8 = r78row.getCell(11);
							if (r78cell8 != null) {
								r78cell8.setCellValue(BRF03.getR78_10_n_headging_nl_() == null ? 0
										: BRF03.getR78_10_n_headging_nl_().intValue());
							}

							Cell r78cell9 = r78row.getCell(12);
							if (r78cell9 != null) {
								r78cell9.setCellValue(BRF03.getR78_10_trading_nl() == null ? 0
										: BRF03.getR78_10_trading_nl().intValue());
							}
							Cell r78cell10 = r78row.getCell(13);
							if (r78cell10 != null) {
								r78cell10.setCellValue(BRF03.getR78_10_n_trading_nl_() == null ? 0
										: BRF03.getR78_10_n_trading_nl_().intValue());
							}
///row79
							Row r79row = sheet.getRow(120);
							Cell r79cell = r79row.getCell(3);
							if (r79cell != null) {
								r79cell.setCellValue(BRF03.getR79_hedging_amount() == null ? 0
										: BRF03.getR79_hedging_amount().intValue());
							}
							Cell r79cell1 = r79row.getCell(4);
							if (r79cell1 != null) {
								r79cell1.setCellValue(BRF03.getR79_trading_amount() == null ? 0
										: BRF03.getR79_trading_amount().intValue());
							}

							Cell r79cell2 = r79row.getCell(5);
							if (r79cell2 != null) {
								r79cell2.setCellValue(BRF03.getR79_resident_amount() == null ? 0
										: BRF03.getR79_resident_amount().intValue());
							}
							Cell r79cell3 = r79row.getCell(6);
							if (r79cell3 != null) {
								r79cell3.setCellValue(BRF03.getR79_non_resident_amount() == null ? 0
										: BRF03.getR79_non_resident_amount().intValue());
							}

							Cell r79cell4 = r79row.getCell(7);
							if (r79cell4 != null) {
								r79cell4.setCellValue(BRF03.getR79_with1year_amount() == null ? 0
										: BRF03.getR79_with1year_amount().intValue());
							}
							Cell r79cell5 = r79row.getCell(8);
							if (r79cell5 != null) {
								r79cell5.setCellValue(BRF03.getR79_1to3year_amount() == null ? 0
										: BRF03.getR79_1to3year_amount().intValue());
							}

							Cell r79cell6 = r79row.getCell(9);
							if (r79cell6 != null) {
								r79cell6.setCellValue(BRF03.getR79_over3year_amount() == null ? 0
										: BRF03.getR79_over3year_amount().intValue());
							}
							Cell r79cell7 = r79row.getCell(10);
							if (r79cell7 != null) {
								r79cell7.setCellValue(BRF03.getR79_10_headging_nl() == null ? 0
										: BRF03.getR79_10_headging_nl().intValue());
							}
///
							Cell r79cell8 = r79row.getCell(11);
							if (r79cell8 != null) {
								r79cell8.setCellValue(BRF03.getR79_10_n_headging_nl_() == null ? 0
										: BRF03.getR79_10_n_headging_nl_().intValue());
							}

							Cell r79cell9 = r79row.getCell(12);
							if (r79cell9 != null) {
								r79cell9.setCellValue(BRF03.getR79_10_trading_nl() == null ? 0
										: BRF03.getR79_10_trading_nl().intValue());
							}
							Cell r79cell10 = r79row.getCell(13);
							if (r79cell10 != null) {
								r79cell10.setCellValue(BRF03.getR79_10_n_trading_nl_() == null ? 0
										: BRF03.getR79_10_n_trading_nl_().intValue());
							}
///row80
							Row r80row = sheet.getRow(121);
							Cell r80cell = r80row.getCell(3);
							if (r80cell != null) {
								r80cell.setCellValue(BRF03.getR80_hedging_amount() == null ? 0
										: BRF03.getR80_hedging_amount().intValue());
							}
							Cell r80cell1 = r80row.getCell(4);
							if (r80cell1 != null) {
								r80cell1.setCellValue(BRF03.getR80_trading_amount() == null ? 0
										: BRF03.getR80_trading_amount().intValue());
							}

							Cell r80cell2 = r80row.getCell(5);
							if (r80cell2 != null) {
								r80cell2.setCellValue(BRF03.getR80_resident_amount() == null ? 0
										: BRF03.getR80_resident_amount().intValue());
							}
							Cell r80cell3 = r80row.getCell(6);
							if (r80cell3 != null) {
								r80cell3.setCellValue(BRF03.getR80_non_resident_amount() == null ? 0
										: BRF03.getR80_non_resident_amount().intValue());
							}

							Cell r80cell4 = r80row.getCell(7);
							if (r80cell4 != null) {
								r80cell4.setCellValue(BRF03.getR80_with1year_amount() == null ? 0
										: BRF03.getR80_with1year_amount().intValue());
							}
							Cell r80cell5 = r80row.getCell(8);
							if (r80cell5 != null) {
								r80cell5.setCellValue(BRF03.getR80_1to3year_amount() == null ? 0
										: BRF03.getR80_1to3year_amount().intValue());
							}

							Cell r80cell6 = r80row.getCell(9);
							if (r80cell6 != null) {
								r80cell6.setCellValue(BRF03.getR80_over3year_amount() == null ? 0
										: BRF03.getR80_over3year_amount().intValue());
							}
							Cell r80cell7 = r80row.getCell(10);
							if (r80cell7 != null) {
								r80cell7.setCellValue(BRF03.getR80_10_headging_nl() == null ? 0
										: BRF03.getR80_10_headging_nl().intValue());
							}
///
							Cell r80cell8 = r80row.getCell(11);
							if (r80cell8 != null) {
								r80cell8.setCellValue(BRF03.getR80_10_n_headging_nl_() == null ? 0
										: BRF03.getR80_10_n_headging_nl_().intValue());
							}

							Cell r80cell9 = r80row.getCell(12);
							if (r80cell9 != null) {
								r80cell9.setCellValue(BRF03.getR80_10_trading_nl() == null ? 0
										: BRF03.getR80_10_trading_nl().intValue());
							}
							Cell r80cell10 = r80row.getCell(13);
							if (r80cell10 != null) {
								r80cell10.setCellValue(BRF03.getR80_10_n_trading_nl_() == null ? 0
										: BRF03.getR80_10_n_trading_nl_().intValue());
							}
///row81
							Row r81row = sheet.getRow(122);
							Cell r81cell = r81row.getCell(3);
							if (r81cell != null) {
								r81cell.setCellValue(BRF03.getR81_hedging_amount() == null ? 0
										: BRF03.getR81_hedging_amount().intValue());
							}
							Cell r81cell1 = r81row.getCell(4);
							if (r81cell1 != null) {
								r81cell1.setCellValue(BRF03.getR81_trading_amount() == null ? 0
										: BRF03.getR81_trading_amount().intValue());
							}

							Cell r81cell2 = r81row.getCell(5);
							if (r81cell2 != null) {
								r81cell2.setCellValue(BRF03.getR81_resident_amount() == null ? 0
										: BRF03.getR81_resident_amount().intValue());
							}
							Cell r81cell3 = r81row.getCell(6);
							if (r81cell3 != null) {
								r81cell3.setCellValue(BRF03.getR81_non_resident_amount() == null ? 0
										: BRF03.getR81_non_resident_amount().intValue());
							}

							Cell r81cell4 = r81row.getCell(7);
							if (r81cell4 != null) {
								r81cell4.setCellValue(BRF03.getR81_with1year_amount() == null ? 0
										: BRF03.getR81_with1year_amount().intValue());
							}
							Cell r81cell5 = r81row.getCell(8);
							if (r81cell5 != null) {
								r81cell5.setCellValue(BRF03.getR81_1to3year_amount() == null ? 0
										: BRF03.getR81_1to3year_amount().intValue());
							}

							Cell r81cell6 = r81row.getCell(9);
							if (r81cell6 != null) {
								r81cell6.setCellValue(BRF03.getR81_over3year_amount() == null ? 0
										: BRF03.getR81_over3year_amount().intValue());
							}
							Cell r81cell7 = r81row.getCell(10);
							if (r81cell7 != null) {
								r81cell7.setCellValue(BRF03.getR81_10_headging_nl() == null ? 0
										: BRF03.getR81_10_headging_nl().intValue());
							}
///
							Cell r81cell8 = r81row.getCell(11);
							if (r81cell8 != null) {
								r81cell8.setCellValue(BRF03.getR81_10_n_headging_nl_() == null ? 0
										: BRF03.getR81_10_n_headging_nl_().intValue());
							}

							Cell r81cell9 = r81row.getCell(12);
							if (r81cell9 != null) {
								r81cell9.setCellValue(BRF03.getR81_10_trading_nl() == null ? 0
										: BRF03.getR81_10_trading_nl().intValue());
							}
							Cell r81cell10 = r81row.getCell(13);
							if (r81cell10 != null) {
								r81cell10.setCellValue(BRF03.getR81_10_n_trading_nl_() == null ? 0
										: BRF03.getR81_10_n_trading_nl_().intValue());
							}
///row82
							Row r82row = sheet.getRow(123);
							Cell r82cell = r82row.getCell(3);
							if (r82cell != null) {
								r82cell.setCellValue(BRF03.getR82_hedging_amount() == null ? 0
										: BRF03.getR82_hedging_amount().intValue());
							}
							Cell r82cell1 = r82row.getCell(4);
							if (r82cell1 != null) {
								r82cell1.setCellValue(BRF03.getR82_trading_amount() == null ? 0
										: BRF03.getR82_trading_amount().intValue());
							}

							Cell r82cell2 = r82row.getCell(5);
							if (r82cell2 != null) {
								r82cell2.setCellValue(BRF03.getR82_resident_amount() == null ? 0
										: BRF03.getR82_resident_amount().intValue());
							}
							Cell r82cell3 = r82row.getCell(6);
							if (r82cell3 != null) {
								r82cell3.setCellValue(BRF03.getR82_non_resident_amount() == null ? 0
										: BRF03.getR82_non_resident_amount().intValue());
							}

							Cell r82cell4 = r82row.getCell(7);
							if (r82cell4 != null) {
								r82cell4.setCellValue(BRF03.getR82_with1year_amount() == null ? 0
										: BRF03.getR82_with1year_amount().intValue());
							}
							Cell r82cell5 = r82row.getCell(8);
							if (r82cell5 != null) {
								r82cell5.setCellValue(BRF03.getR82_1to3year_amount() == null ? 0
										: BRF03.getR82_1to3year_amount().intValue());
							}

							Cell r82cell6 = r82row.getCell(9);
							if (r82cell6 != null) {
								r82cell6.setCellValue(BRF03.getR82_over3year_amount() == null ? 0
										: BRF03.getR82_over3year_amount().intValue());
							}
							Cell r82cell7 = r82row.getCell(10);
							if (r82cell7 != null) {
								r82cell7.setCellValue(BRF03.getR82_10_headging_nl() == null ? 0
										: BRF03.getR82_10_headging_nl().intValue());
							}
///
							Cell r82cell8 = r82row.getCell(11);
							if (r82cell8 != null) {
								r82cell8.setCellValue(BRF03.getR82_10_n_headging_nl_() == null ? 0
										: BRF03.getR82_10_n_headging_nl_().intValue());
							}

							Cell r82cell9 = r82row.getCell(12);
							if (r82cell9 != null) {
								r82cell9.setCellValue(BRF03.getR82_10_trading_nl() == null ? 0
										: BRF03.getR82_10_trading_nl().intValue());
							}
							Cell r82cell10 = r82row.getCell(13);
							if (r82cell10 != null) {
								r82cell10.setCellValue(BRF03.getR82_10_n_trading_nl_() == null ? 0
										: BRF03.getR82_10_n_trading_nl_().intValue());
							}
///////////////////
///row83
							Row r83row = sheet.getRow(128);
							Cell r83cell = r83row.getCell(5);
							if (r83cell != null) {
								r83cell.setCellValue(BRF03.getR83_resident_for_exc() == null ? 0
										: BRF03.getR83_resident_for_exc().intValue());
							}
							Cell r83cell1 = r83row.getCell(6);
							if (r83cell1 != null) {
								r83cell1.setCellValue(BRF03.getR83_non_resident_for_exc() == null ? 0
										: BRF03.getR83_non_resident_for_exc().intValue());
							}

							Cell r83cell2 = r83row.getCell(12);
							if (r83cell2 != null) {
								r83cell2.setCellValue(BRF03.getR83_resident_curr_opt() == null ? 0
										: BRF03.getR83_resident_curr_opt().intValue());
							}
							Cell r83cell3 = r83row.getCell(13);
							if (r83cell3 != null) {
								r83cell3.setCellValue(BRF03.getR83_non_resident_curr_opt() == null ? 0
										: BRF03.getR83_non_resident_curr_opt().intValue());
							}

///row84
							Row r84row = sheet.getRow(129);
							Cell r84cell = r84row.getCell(5);
							if (r84cell != null) {
								r84cell.setCellValue(BRF03.getR84_resident_for_exc() == null ? 0
										: BRF03.getR84_resident_for_exc().intValue());
							}
							Cell r84cell1 = r84row.getCell(6);
							if (r84cell1 != null) {
								r84cell1.setCellValue(BRF03.getR84_non_resident_for_exc() == null ? 0
										: BRF03.getR84_non_resident_for_exc().intValue());
							}

							Cell r84cell2 = r84row.getCell(12);
							if (r84cell2 != null) {
								r84cell2.setCellValue(BRF03.getR84_resident_curr_opt() == null ? 0
										: BRF03.getR84_resident_curr_opt().intValue());
							}
							Cell r84cell3 = r84row.getCell(13);
							if (r84cell3 != null) {
								r84cell3.setCellValue(BRF03.getR84_non_resident_curr_opt() == null ? 0
										: BRF03.getR84_non_resident_curr_opt().intValue());
							}

///row85
							Row r85row = sheet.getRow(130);
							Cell r85cell = r85row.getCell(5);
							if (r85cell != null) {
								r85cell.setCellValue(BRF03.getR85_resident_for_exc() == null ? 0
										: BRF03.getR85_resident_for_exc().intValue());
							}
							Cell r85cell1 = r85row.getCell(6);
							if (r85cell1 != null) {
								r85cell1.setCellValue(BRF03.getR85_non_resident_for_exc() == null ? 0
										: BRF03.getR85_non_resident_for_exc().intValue());
							}

							Cell r85cell2 = r85row.getCell(12);
							if (r85cell2 != null) {
								r85cell2.setCellValue(BRF03.getR85_resident_curr_opt() == null ? 0
										: BRF03.getR85_resident_curr_opt().intValue());
							}
							Cell r85cell3 = r85row.getCell(13);
							if (r85cell3 != null) {
								r85cell3.setCellValue(BRF03.getR85_non_resident_curr_opt() == null ? 0
										: BRF03.getR85_non_resident_curr_opt().intValue());
							}

///row86
							Row r86row = sheet.getRow(131);
							Cell r86cell = r86row.getCell(5);
							if (r86cell != null) {
								r86cell.setCellValue(BRF03.getR86_resident_for_exc() == null ? 0
										: BRF03.getR86_resident_for_exc().intValue());
							}
							Cell r86cell1 = r86row.getCell(6);
							if (r86cell1 != null) {
								r86cell1.setCellValue(BRF03.getR86_non_resident_for_exc() == null ? 0
										: BRF03.getR86_non_resident_for_exc().intValue());
							}

							Cell r86cell2 = r86row.getCell(12);
							if (r86cell2 != null) {
								r86cell2.setCellValue(BRF03.getR86_resident_curr_opt() == null ? 0
										: BRF03.getR86_resident_curr_opt().intValue());
							}
							Cell r86cell3 = r86row.getCell(13);
							if (r86cell3 != null) {
								r86cell3.setCellValue(BRF03.getR86_non_resident_curr_opt() == null ? 0
										: BRF03.getR86_non_resident_curr_opt().intValue());
							}

///row87
							Row r87row = sheet.getRow(132);
							Cell r87cell = r87row.getCell(5);
							if (r87cell != null) {
								r87cell.setCellValue(BRF03.getR87_resident_for_exc() == null ? 0
										: BRF03.getR87_resident_for_exc().intValue());
							}
							Cell r87cell1 = r87row.getCell(6);
							if (r87cell1 != null) {
								r87cell1.setCellValue(BRF03.getR87_non_resident_for_exc() == null ? 0
										: BRF03.getR87_non_resident_for_exc().intValue());
							}

							Cell r87cell2 = r87row.getCell(12);
							if (r87cell2 != null) {
								r87cell2.setCellValue(BRF03.getR87_resident_curr_opt() == null ? 0
										: BRF03.getR87_resident_curr_opt().intValue());
							}
							Cell r87cell3 = r87row.getCell(13);
							if (r87cell3 != null) {
								r87cell3.setCellValue(BRF03.getR87_non_resident_curr_opt() == null ? 0
										: BRF03.getR87_non_resident_curr_opt().intValue());
							}

///row88
							Row r88row = sheet.getRow(133);
							Cell r88cell = r88row.getCell(5);
							if (r88cell != null) {
								r88cell.setCellValue(BRF03.getR88_resident_for_exc() == null ? 0
										: BRF03.getR88_resident_for_exc().intValue());
							}
							Cell r88cell1 = r88row.getCell(6);
							if (r88cell1 != null) {
								r88cell1.setCellValue(BRF03.getR88_non_resident_for_exc() == null ? 0
										: BRF03.getR88_non_resident_for_exc().intValue());
							}

							Cell r88cell2 = r88row.getCell(12);
							if (r88cell2 != null) {
								r88cell2.setCellValue(BRF03.getR88_resident_curr_opt() == null ? 0
										: BRF03.getR88_resident_curr_opt().intValue());
							}
							Cell r88cell3 = r88row.getCell(13);
							if (r88cell3 != null) {
								r88cell3.setCellValue(BRF03.getR88_non_resident_curr_opt() == null ? 0
										: BRF03.getR88_non_resident_curr_opt().intValue());
							}

///row89
							Row r89row = sheet.getRow(134);
							Cell r89cell = r89row.getCell(5);
							if (r89cell != null) {
								r89cell.setCellValue(BRF03.getR89_resident_for_exc() == null ? 0
										: BRF03.getR89_resident_for_exc().intValue());
							}
							Cell r89cell1 = r89row.getCell(6);
							if (r89cell1 != null) {
								r89cell1.setCellValue(BRF03.getR89_non_resident_for_exc() == null ? 0
										: BRF03.getR89_non_resident_for_exc().intValue());
							}

							Cell r89cell2 = r89row.getCell(12);
							if (r89cell2 != null) {
								r89cell2.setCellValue(BRF03.getR89_resident_curr_opt() == null ? 0
										: BRF03.getR89_resident_curr_opt().intValue());
							}
							Cell r89cell3 = r89row.getCell(13);
							if (r89cell3 != null) {
								r89cell3.setCellValue(BRF03.getR89_non_resident_curr_opt() == null ? 0
										: BRF03.getR89_non_resident_curr_opt().intValue());
							}

///row90
							Row r90row = sheet.getRow(135);
							Cell r90cell = r90row.getCell(5);
							if (r90cell != null) {
								r90cell.setCellValue(BRF03.getR90_resident_for_exc() == null ? 0
										: BRF03.getR90_resident_for_exc().intValue());
							}
							Cell r90cell1 = r90row.getCell(6);
							if (r90cell1 != null) {
								r90cell1.setCellValue(BRF03.getR90_non_resident_for_exc() == null ? 0
										: BRF03.getR90_non_resident_for_exc().intValue());
							}

							Cell r90cell2 = r90row.getCell(12);
							if (r90cell2 != null) {
								r90cell2.setCellValue(BRF03.getR90_resident_curr_opt() == null ? 0
										: BRF03.getR90_resident_curr_opt().intValue());
							}
							Cell r90cell3 = r90row.getCell(13);
							if (r90cell3 != null) {
								r90cell3.setCellValue(BRF03.getR90_non_resident_curr_opt() == null ? 0
										: BRF03.getR90_non_resident_curr_opt().intValue());
							}

///row91
							Row r91row = sheet.getRow(136);
							Cell r91cell = r91row.getCell(5);
							if (r91cell != null) {
								r91cell.setCellValue(BRF03.getR91_resident_for_exc() == null ? 0
										: BRF03.getR91_resident_for_exc().intValue());
							}
							Cell r91cell1 = r91row.getCell(6);
							if (r91cell1 != null) {
								r91cell1.setCellValue(BRF03.getR91_non_resident_for_exc() == null ? 0
										: BRF03.getR91_non_resident_for_exc().intValue());
							}

							Cell r91cell2 = r91row.getCell(12);
							if (r91cell2 != null) {
								r91cell2.setCellValue(BRF03.getR91_resident_curr_opt() == null ? 0
										: BRF03.getR91_resident_curr_opt().intValue());
							}
							Cell r91cell3 = r91row.getCell(13);
							if (r91cell3 != null) {
								r91cell3.setCellValue(BRF03.getR91_non_resident_curr_opt() == null ? 0
										: BRF03.getR91_non_resident_curr_opt().intValue());
							}

///row92
							Row r92row = sheet.getRow(137);
							Cell r92cell = r92row.getCell(5);
							if (r92cell != null) {
								r92cell.setCellValue(BRF03.getR92_resident_for_exc() == null ? 0
										: BRF03.getR92_resident_for_exc().intValue());
							}
							Cell r92cell1 = r92row.getCell(6);
							if (r92cell1 != null) {
								r92cell1.setCellValue(BRF03.getR92_non_resident_for_exc() == null ? 0
										: BRF03.getR92_non_resident_for_exc().intValue());
							}

							Cell r92cell2 = r92row.getCell(12);
							if (r92cell2 != null) {
								r92cell2.setCellValue(BRF03.getR92_resident_curr_opt() == null ? 0
										: BRF03.getR92_resident_curr_opt().intValue());
							}
							Cell r92cell3 = r92row.getCell(13);
							if (r92cell3 != null) {
								r92cell3.setCellValue(BRF03.getR92_non_resident_curr_opt() == null ? 0
										: BRF03.getR92_non_resident_curr_opt().intValue());
							}

///row93
							Row r93row = sheet.getRow(138);
							Cell r93cell = r93row.getCell(5);
							if (r93cell != null) {
								r93cell.setCellValue(BRF03.getR93_resident_for_exc() == null ? 0
										: BRF03.getR93_resident_for_exc().intValue());
							}
							Cell r93cell1 = r93row.getCell(6);
							if (r93cell1 != null) {
								r93cell1.setCellValue(BRF03.getR93_non_resident_for_exc() == null ? 0
										: BRF03.getR93_non_resident_for_exc().intValue());
							}

							Cell r93cell2 = r93row.getCell(12);
							if (r93cell2 != null) {
								r93cell2.setCellValue(BRF03.getR93_resident_curr_opt() == null ? 0
										: BRF03.getR93_resident_curr_opt().intValue());
							}
							Cell r93cell3 = r93row.getCell(13);
							if (r93cell3 != null) {
								r93cell3.setCellValue(BRF03.getR93_non_resident_curr_opt() == null ? 0
										: BRF03.getR93_non_resident_curr_opt().intValue());
							}

///row94
							Row r94row = sheet.getRow(139);
							Cell r94cell = r94row.getCell(5);
							if (r94cell != null) {
								r94cell.setCellValue(BRF03.getR94_resident_for_exc() == null ? 0
										: BRF03.getR94_resident_for_exc().intValue());
							}
							Cell r94cell1 = r94row.getCell(6);
							if (r94cell1 != null) {
								r94cell1.setCellValue(BRF03.getR94_non_resident_for_exc() == null ? 0
										: BRF03.getR94_non_resident_for_exc().intValue());
							}

							Cell r94cell2 = r94row.getCell(12);
							if (r94cell2 != null) {
								r94cell2.setCellValue(BRF03.getR94_resident_curr_opt() == null ? 0
										: BRF03.getR94_resident_curr_opt().intValue());
							}
							Cell r94cell3 = r94row.getCell(13);
							if (r94cell3 != null) {
								r94cell3.setCellValue(BRF03.getR94_non_resident_curr_opt() == null ? 0
										: BRF03.getR94_non_resident_curr_opt().intValue());
							}

///row95
							Row r95row = sheet.getRow(140);
							Cell r95cell = r95row.getCell(5);
							if (r95cell != null) {
								r95cell.setCellValue(BRF03.getR95_resident_for_exc() == null ? 0
										: BRF03.getR95_resident_for_exc().intValue());
							}
							Cell r95cell1 = r95row.getCell(6);
							if (r95cell1 != null) {
								r95cell1.setCellValue(BRF03.getR95_non_resident_for_exc() == null ? 0
										: BRF03.getR95_non_resident_for_exc().intValue());
							}

							Cell r95cell2 = r95row.getCell(12);
							if (r95cell2 != null) {
								r95cell2.setCellValue(BRF03.getR95_resident_curr_opt() == null ? 0
										: BRF03.getR95_resident_curr_opt().intValue());
							}
							Cell r95cell3 = r95row.getCell(13);
							if (r95cell3 != null) {
								r95cell3.setCellValue(BRF03.getR95_non_resident_curr_opt() == null ? 0
										: BRF03.getR95_non_resident_curr_opt().intValue());
							}

///row96
							Row r96row = sheet.getRow(141);
							Cell r96cell = r96row.getCell(5);
							if (r96cell != null) {
								r96cell.setCellValue(BRF03.getR96_resident_for_exc() == null ? 0
										: BRF03.getR96_resident_for_exc().intValue());
							}
							Cell r96cell1 = r96row.getCell(6);
							if (r96cell1 != null) {
								r96cell1.setCellValue(BRF03.getR96_non_resident_for_exc() == null ? 0
										: BRF03.getR96_non_resident_for_exc().intValue());
							}

							Cell r96cell2 = r96row.getCell(12);
							if (r96cell2 != null) {
								r96cell2.setCellValue(BRF03.getR96_resident_curr_opt() == null ? 0
										: BRF03.getR96_resident_curr_opt().intValue());
							}
							Cell r96cell3 = r96row.getCell(13);
							if (r96cell3 != null) {
								r96cell3.setCellValue(BRF03.getR96_non_resident_curr_opt() == null ? 0
										: BRF03.getR96_non_resident_curr_opt().intValue());
							}

///row97
							Row r97row = sheet.getRow(142);
							Cell r97cell = r97row.getCell(5);
							if (r97cell != null) {
								r97cell.setCellValue(BRF03.getR97_resident_for_exc() == null ? 0
										: BRF03.getR97_resident_for_exc().intValue());
							}
							Cell r97cell1 = r97row.getCell(6);
							if (r97cell1 != null) {
								r97cell1.setCellValue(BRF03.getR97_non_resident_for_exc() == null ? 0
										: BRF03.getR97_non_resident_for_exc().intValue());
							}

							Cell r97cell2 = r97row.getCell(12);
							if (r97cell2 != null) {
								r97cell2.setCellValue(BRF03.getR97_resident_curr_opt() == null ? 0
										: BRF03.getR97_resident_curr_opt().intValue());
							}
							Cell r97cell3 = r97row.getCell(13);
							if (r97cell3 != null) {
								r97cell3.setCellValue(BRF03.getR97_non_resident_curr_opt() == null ? 0
										: BRF03.getR97_non_resident_curr_opt().intValue());
							}

///row98
							Row r98row = sheet.getRow(146);

							Cell r98cell = r98row.getCell(5);
							if (r98cell != null) {
								r98cell.setCellValue(BRF03.getR98_resident_interest_exp() == null ? 0
										: BRF03.getR98_resident_interest_exp().intValue());
							}
							Cell r98cell1 = r98row.getCell(6);
							if (r98cell1 != null) {
								r98cell1.setCellValue(BRF03.getR98_non_resident_interest_exp() == null ? 0
										: BRF03.getR98_non_resident_interest_exp().intValue());
							}

							Cell r98cell2 = r98row.getCell(12);
							if (r98cell2 != null) {
								r98cell2.setCellValue(BRF03.getR98_resident_credit_exp() == null ? 0
										: BRF03.getR98_resident_credit_exp().intValue());
							}
							Cell r98cell3 = r98row.getCell(13);
							if (r98cell3 != null) {
								r98cell3.setCellValue(BRF03.getR98_non_resident_credit_exp() == null ? 0
										: BRF03.getR98_non_resident_credit_exp().intValue());
							}

///row99
							Row r99row = sheet.getRow(147);
							Cell r99cell = r99row.getCell(5);
							if (r99cell != null) {
								r99cell.setCellValue(BRF03.getR99_resident_interest_exp() == null ? 0
										: BRF03.getR99_resident_interest_exp().intValue());
							}
							Cell r99cell1 = r99row.getCell(6);
							if (r99cell1 != null) {
								r99cell1.setCellValue(BRF03.getR99_non_resident_interest_exp() == null ? 0
										: BRF03.getR99_non_resident_interest_exp().intValue());
							}

							Cell r99cell2 = r99row.getCell(12);
							if (r99cell2 != null) {
								r99cell2.setCellValue(BRF03.getR99_resident_credit_exp() == null ? 0
										: BRF03.getR99_resident_credit_exp().intValue());
							}
							Cell r99cell3 = r99row.getCell(13);
							if (r99cell3 != null) {
								r99cell3.setCellValue(BRF03.getR99_non_resident_credit_exp() == null ? 0
										: BRF03.getR99_non_resident_credit_exp().intValue());
							}

///row100
							Row r100row = sheet.getRow(148);
							Cell r100cell = r100row.getCell(5);
							if (r100cell != null) {
								r100cell.setCellValue(BRF03.getR100_resident_interest_exp() == null ? 0
										: BRF03.getR100_resident_interest_exp().intValue());
							}
							Cell r100cell1 = r100row.getCell(6);
							if (r100cell1 != null) {
								r100cell1.setCellValue(BRF03.getR100_non_resident_interest_exp() == null ? 0
										: BRF03.getR100_non_resident_interest_exp().intValue());
							}

							Cell r100cell2 = r100row.getCell(12);
							if (r100cell2 != null) {
								r100cell2.setCellValue(BRF03.getR100_resident_credit_exp() == null ? 0
										: BRF03.getR100_resident_credit_exp().intValue());
							}
							Cell r100cell3 = r100row.getCell(13);
							if (r100cell3 != null) {
								r100cell3.setCellValue(BRF03.getR100_non_resident_credit_exp() == null ? 0
										: BRF03.getR100_non_resident_credit_exp().intValue());
							}

///row101
							Row r101row = sheet.getRow(149);
							Cell r101cell = r101row.getCell(5);
							if (r101cell != null) {
								r101cell.setCellValue(BRF03.getR101_resident_interest_exp() == null ? 0
										: BRF03.getR101_resident_interest_exp().intValue());
							}
							Cell r101cell1 = r101row.getCell(6);
							if (r101cell1 != null) {
								r101cell1.setCellValue(BRF03.getR101_non_resident_interest_exp() == null ? 0
										: BRF03.getR101_non_resident_interest_exp().intValue());
							}

							Cell r101cell2 = r101row.getCell(12);
							if (r101cell2 != null) {
								r101cell2.setCellValue(BRF03.getR101_resident_credit_exp() == null ? 0
										: BRF03.getR101_resident_credit_exp().intValue());
							}
							Cell r101cell3 = r101row.getCell(13);
							if (r101cell3 != null) {
								r101cell3.setCellValue(BRF03.getR101_non_resident_credit_exp() == null ? 0
										: BRF03.getR101_non_resident_credit_exp().intValue());
							}

///row102
							Row r102row = sheet.getRow(150);
							Cell r102cell = r102row.getCell(5);
							if (r102cell != null) {
								r102cell.setCellValue(BRF03.getR102_resident_interest_exp() == null ? 0
										: BRF03.getR102_resident_interest_exp().intValue());
							}
							Cell r102cell1 = r102row.getCell(6);
							if (r102cell1 != null) {
								r102cell1.setCellValue(BRF03.getR102_non_resident_interest_exp() == null ? 0
										: BRF03.getR102_non_resident_interest_exp().intValue());
							}

							Cell r102cell2 = r102row.getCell(12);
							if (r102cell2 != null) {
								r102cell2.setCellValue(BRF03.getR102_resident_credit_exp() == null ? 0
										: BRF03.getR102_resident_credit_exp().intValue());
							}
							Cell r102cell3 = r102row.getCell(13);
							if (r102cell3 != null) {
								r102cell3.setCellValue(BRF03.getR102_non_resident_credit_exp() == null ? 0
										: BRF03.getR102_non_resident_credit_exp().intValue());
							}

///row103
							Row r103row = sheet.getRow(151);
							Cell r103cell = r103row.getCell(5);
							if (r103cell != null) {
								r103cell.setCellValue(BRF03.getR103_resident_interest_exp() == null ? 0
										: BRF03.getR103_resident_interest_exp().intValue());
							}
							Cell r103cell1 = r103row.getCell(6);
							if (r103cell1 != null) {
								r103cell1.setCellValue(BRF03.getR103_non_resident_interest_exp() == null ? 0
										: BRF03.getR103_non_resident_interest_exp().intValue());
							}

							Cell r103cell2 = r103row.getCell(12);
							if (r103cell2 != null) {
								r103cell2.setCellValue(BRF03.getR103_resident_credit_exp() == null ? 0
										: BRF03.getR103_resident_credit_exp().intValue());
							}
							Cell r103cell3 = r103row.getCell(13);
							if (r103cell3 != null) {
								r103cell3.setCellValue(BRF03.getR103_non_resident_credit_exp() == null ? 0
										: BRF03.getR103_non_resident_credit_exp().intValue());
							}

///row104
							Row r104row = sheet.getRow(152);
							Cell r104cell = r104row.getCell(5);
							if (r104cell != null) {
								r104cell.setCellValue(BRF03.getR104_resident_interest_exp() == null ? 0
										: BRF03.getR104_resident_interest_exp().intValue());
							}
							Cell r104cell1 = r104row.getCell(6);
							if (r104cell1 != null) {
								r104cell1.setCellValue(BRF03.getR104_non_resident_interest_exp() == null ? 0
										: BRF03.getR104_non_resident_interest_exp().intValue());
							}

							Cell r104cell2 = r104row.getCell(12);
							if (r104cell2 != null) {
								r104cell2.setCellValue(BRF03.getR104_resident_credit_exp() == null ? 0
										: BRF03.getR104_resident_credit_exp().intValue());
							}
							Cell r104cell3 = r104row.getCell(13);
							if (r104cell3 != null) {
								r104cell3.setCellValue(BRF03.getR104_non_resident_credit_exp() == null ? 0
										: BRF03.getR104_non_resident_credit_exp().intValue());
							}

///row105
							Row r105row = sheet.getRow(153);
							Cell r105cell = r105row.getCell(5);
							if (r105cell != null) {
								r105cell.setCellValue(BRF03.getR105_resident_interest_exp() == null ? 0
										: BRF03.getR105_resident_interest_exp().intValue());
							}
							Cell r105cell1 = r105row.getCell(6);
							if (r105cell1 != null) {
								r105cell1.setCellValue(BRF03.getR105_non_resident_interest_exp() == null ? 0
										: BRF03.getR105_non_resident_interest_exp().intValue());
							}

							Cell r105cell2 = r105row.getCell(12);
							if (r105cell2 != null) {
								r105cell2.setCellValue(BRF03.getR105_resident_credit_exp() == null ? 0
										: BRF03.getR105_resident_credit_exp().intValue());
							}
							Cell r105cell3 = r105row.getCell(13);
							if (r105cell3 != null) {
								r105cell3.setCellValue(BRF03.getR105_non_resident_credit_exp() == null ? 0
										: BRF03.getR105_non_resident_credit_exp().intValue());
							}

///row106
							Row r106row = sheet.getRow(154);
							Cell r106cell = r106row.getCell(5);
							if (r106cell != null) {
								r106cell.setCellValue(BRF03.getR106_resident_interest_exp() == null ? 0
										: BRF03.getR106_resident_interest_exp().intValue());
							}
							Cell r106cell1 = r106row.getCell(6);
							if (r106cell1 != null) {
								r106cell1.setCellValue(BRF03.getR106_non_resident_interest_exp() == null ? 0
										: BRF03.getR106_non_resident_interest_exp().intValue());
							}

							Cell r106cell2 = r106row.getCell(12);
							if (r106cell2 != null) {
								r106cell2.setCellValue(BRF03.getR106_resident_credit_exp() == null ? 0
										: BRF03.getR106_resident_credit_exp().intValue());
							}
							Cell r106cell3 = r106row.getCell(13);
							if (r106cell3 != null) {
								r106cell3.setCellValue(BRF03.getR106_non_resident_credit_exp() == null ? 0
										: BRF03.getR106_non_resident_credit_exp().intValue());
							}

///row107
							Row r107row = sheet.getRow(155);
							Cell r107cell = r107row.getCell(5);
							if (r107cell != null) {
								r107cell.setCellValue(BRF03.getR107_resident_interest_exp() == null ? 0
										: BRF03.getR107_resident_interest_exp().intValue());
							}
							Cell r107cell1 = r107row.getCell(6);
							if (r107cell1 != null) {
								r107cell1.setCellValue(BRF03.getR107_non_resident_interest_exp() == null ? 0
										: BRF03.getR107_non_resident_interest_exp().intValue());
							}

							Cell r107cell2 = r107row.getCell(12);
							if (r107cell2 != null) {
								r107cell2.setCellValue(BRF03.getR107_resident_credit_exp() == null ? 0
										: BRF03.getR107_resident_credit_exp().intValue());
							}
							Cell r107cell3 = r107row.getCell(13);
							if (r107cell3 != null) {
								r107cell3.setCellValue(BRF03.getR107_non_resident_credit_exp() == null ? 0
										: BRF03.getR107_non_resident_credit_exp().intValue());
							}

///row108
							Row r108row = sheet.getRow(156);
							Cell r108cell = r108row.getCell(5);
							if (r108cell != null) {
								r108cell.setCellValue(BRF03.getR108_resident_interest_exp() == null ? 0
										: BRF03.getR108_resident_interest_exp().intValue());
							}
							Cell r108cell1 = r108row.getCell(6);
							if (r108cell1 != null) {
								r108cell1.setCellValue(BRF03.getR108_non_resident_interest_exp() == null ? 0
										: BRF03.getR108_non_resident_interest_exp().intValue());
							}

							Cell r108cell2 = r108row.getCell(12);
							if (r108cell2 != null) {
								r108cell2.setCellValue(BRF03.getR108_resident_credit_exp() == null ? 0
										: BRF03.getR108_resident_credit_exp().intValue());
							}
							Cell r108cell3 = r108row.getCell(13);
							if (r108cell3 != null) {
								r108cell3.setCellValue(BRF03.getR108_non_resident_credit_exp() == null ? 0
										: BRF03.getR108_non_resident_credit_exp().intValue());
							}

///row109
							Row r109row = sheet.getRow(157);
							Cell r109cell = r109row.getCell(5);
							if (r109cell != null) {
								r109cell.setCellValue(BRF03.getR109_resident_interest_exp() == null ? 0
										: BRF03.getR109_resident_interest_exp().intValue());
							}
							Cell r109cell1 = r109row.getCell(6);
							if (r109cell1 != null) {
								r109cell1.setCellValue(BRF03.getR109_non_resident_interest_exp() == null ? 0
										: BRF03.getR109_non_resident_interest_exp().intValue());
							}

							Cell r109cell2 = r109row.getCell(12);
							if (r109cell2 != null) {
								r109cell2.setCellValue(BRF03.getR109_resident_credit_exp() == null ? 0
										: BRF03.getR109_resident_credit_exp().intValue());
							}
							Cell r109cell3 = r109row.getCell(13);
							if (r109cell3 != null) {
								r109cell3.setCellValue(BRF03.getR109_non_resident_credit_exp() == null ? 0
										: BRF03.getR109_non_resident_credit_exp().intValue());
							}

///row110
							Row r110row = sheet.getRow(158);
							Cell r110cell = r110row.getCell(5);
							if (r110cell != null) {
								r110cell.setCellValue(BRF03.getR110_resident_interest_exp() == null ? 0
										: BRF03.getR110_resident_interest_exp().intValue());
							}
							Cell r110cell1 = r110row.getCell(6);
							if (r110cell1 != null) {
								r110cell1.setCellValue(BRF03.getR110_non_resident_interest_exp() == null ? 0
										: BRF03.getR110_non_resident_interest_exp().intValue());
							}

							Cell r110cell2 = r110row.getCell(12);
							if (r110cell2 != null) {
								r110cell2.setCellValue(BRF03.getR110_resident_credit_exp() == null ? 0
										: BRF03.getR110_resident_credit_exp().intValue());
							}
							Cell r110cell3 = r110row.getCell(13);
							if (r110cell3 != null) {
								r110cell3.setCellValue(BRF03.getR110_non_resident_credit_exp() == null ? 0
										: BRF03.getR110_non_resident_credit_exp().intValue());
							}

///row111
							Row r111row = sheet.getRow(159);
							Cell r111cell = r111row.getCell(5);
							if (r111cell != null) {
								r111cell.setCellValue(BRF03.getR111_resident_interest_exp() == null ? 0
										: BRF03.getR111_resident_interest_exp().intValue());
							}
							Cell r111cell1 = r111row.getCell(6);
							if (r111cell1 != null) {
								r111cell1.setCellValue(BRF03.getR111_non_resident_interest_exp() == null ? 0
										: BRF03.getR111_non_resident_interest_exp().intValue());
							}

							Cell r111cell2 = r111row.getCell(12);
							if (r111cell2 != null) {
								r111cell2.setCellValue(BRF03.getR111_resident_credit_exp() == null ? 0
										: BRF03.getR111_resident_credit_exp().intValue());
							}
							Cell r111cell3 = r111row.getCell(13);
							if (r111cell3 != null) {
								r111cell3.setCellValue(BRF03.getR111_non_resident_credit_exp() == null ? 0
										: BRF03.getR111_non_resident_credit_exp().intValue());
							}

///row112
							Row r112row = sheet.getRow(160);
							Cell r112cell = r112row.getCell(5);
							if (r112cell != null) {
								r112cell.setCellValue(BRF03.getR112_resident_interest_exp() == null ? 0
										: BRF03.getR112_resident_interest_exp().intValue());
							}
							Cell r112cell1 = r112row.getCell(6);
							if (r112cell1 != null) {
								r112cell1.setCellValue(BRF03.getR112_non_resident_interest_exp() == null ? 0
										: BRF03.getR112_non_resident_interest_exp().intValue());
							}

							Cell r112cell2 = r112row.getCell(12);
							if (r112cell2 != null) {
								r112cell2.setCellValue(BRF03.getR112_resident_credit_exp() == null ? 0
										: BRF03.getR112_resident_credit_exp().intValue());
							}
							Cell r112cell3 = r112row.getCell(13);
							if (r112cell3 != null) {
								r112cell3.setCellValue(BRF03.getR112_non_resident_credit_exp() == null ? 0
										: BRF03.getR112_non_resident_credit_exp().intValue());
							}

							// Save the changes
							workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
							FileOutputStream fileOut = new FileOutputStream(
									env.getProperty("output.exportpathfinal") + "011-BRF-003-A.xls");
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
		outputFile = new File(env.getProperty("output.exportpathfinal") + "011-BRF-003-A.xls");

		return outputFile;

	}

	public String detailChanges3(BRF3_DETAILTABLE detail, String foracid, String report_addl_criteria_1,
			BigDecimal act_balance_amt_lc, String report_label_1, String report_name_1, String report_date,
			AuditReasonDTO reason) {

		String msg = "";

		try {
			Session hs = sessionFactory.getCurrentSession();
			// Optional<BRF3_DETAILTABLE> Brf3detail = bRF3_DetaiRep.findById(foracid);
			BRF3_DETAILTABLE Brf3detail = bRF3_DetaiRep.getbyaccnoanddate(foracid, report_date);

			if (!Brf3detail.equals(null) && Brf3detail != null) {
				BRF3_DETAILTABLE BRFdetail = Brf3detail;

				if (BRFdetail.getReport_label_1().equals(report_label_1)
						&& BRFdetail.getReport_name_1().equals(report_name_1)
						&& BRFdetail.getAct_balance_amt_lc().equals(act_balance_amt_lc)
						&& BRFdetail.getReport_addl_criteria_1().equals(report_addl_criteria_1)) {

					msg = "No modification done";

				} else {
					BRFdetail.setAct_balance_amt_lc(act_balance_amt_lc);
					BRFdetail.setReport_label_1(report_label_1);
					BRFdetail.setReport_name_1(report_name_1);
					BRFdetail.setReport_addl_criteria_1(report_addl_criteria_1);
					bRF3_DetaiRep.save(BRFdetail);

					// === Begin Audit Block ===
					HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
							.getRequestAttributes()).getRequest();
					String user1 = (String) request.getSession().getAttribute("USERID");
					String username = (String) request.getSession().getAttribute("USERNAME");

					String auditID = sequence.generateRequestUUId();

					List<String> oldValues = new ArrayList<>();
					List<String> newValues = new ArrayList<>();
					List<String> fieldNames = new ArrayList<>();

					if (!Objects.equals(detail.getReport_label_1(), report_label_1)) {
						oldValues.add(detail.getReport_label_1());
						newValues.add(report_label_1);
						fieldNames.add("report_label_1");
					}
					if (!Objects.equals(detail.getReport_name_1(), report_name_1)) {
						oldValues.add(detail.getReport_name_1());
						newValues.add(report_name_1);
						fieldNames.add("report_name_1");
					}
					if (!Objects.equals(detail.getAct_balance_amt_lc(), act_balance_amt_lc)) {
						oldValues.add(detail.getAct_balance_amt_lc().toString());
						newValues.add(act_balance_amt_lc.toString());
						fieldNames.add("act_balance_amt_lc");
					}
					if (!Objects.equals(detail.getReport_addl_criteria_1(), report_addl_criteria_1)) {
						oldValues.add(detail.getReport_addl_criteria_1());
						newValues.add(report_addl_criteria_1);
						fieldNames.add("report_addl_criteria_1");
					}
					/*
					 * MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
					 * audit.setAudit_date(new Date()); audit.setEntry_time(new Date());
					 * audit.setEntry_user(user1); audit.setFunc_code("EDIT");
					 * audit.setAudit_table("BRF3_DETAILTABLE"); audit.setAudit_screen("Edit");
					 * audit.setEvent_id(user1); audit.setEvent_name(username);
					 * audit.setRemarks("Edit Successfully"); audit.setField_name(String.join("; ",
					 * fieldNames)); audit.setOld_value(String.join("; ", oldValues));
					 * audit.setNew_value(String.join("; ", newValues));
					 * 
					 * UserProfile values1 = userProfileRep.getRole(user1);
					 * audit.setAuth_user(values1.getAuth_user());
					 * audit.setAuth_time(values1.getAuth_time()); audit.setAudit_ref_no(auditID);
					 * 
					 * mANUAL_Service_Rep.save(audit);
					 */
					// === End Audit Block ===

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

	// TO show thw Archieve values
	public ModelAndView getArchieveBRF003View(String reportId, String fromdate, String todate, String currency,
			String dtltype, Pageable pageable) {

		ModelAndView mv = new ModelAndView();
		Session hs = sessionFactory.getCurrentSession();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<BRF003_ENTITY> T1rep = new ArrayList<BRF003_ENTITY>();
		// Query<Object[]> qr;

		List<BRF003_ENTITY> T1Master = new ArrayList<BRF003_ENTITY>();
		/* List<BRF73_TABLE2> T1Master1 = new ArrayList<BRF73_TABLE2>(); */

		logger.info("Inside archive" + currency);

		try {
			Date d1 = df.parse(todate);
			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			T1Master = hs.createQuery("from  BRF003_ENTITY a where a.report_date = ?1 ", BRF003_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();

			/*
			 * T1Master1 = hs.createQuery("from BRF73_TABLE2 a where a.report_date = ?1 ",
			 * BRF73_TABLE2.class) .setParameter(1, df.parse(todate)).getResultList();
			 */

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

		mv.setViewName("RR/BRF3ARCH");
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

	/****
	 * ARCH DETAILS
	 * 
	 * @throws ParseException
	 ****/

	public ModelAndView ARCHgetBRF003currentDtl(String reportId, String fromdate, String todate, String currency,
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
						"select * from BRF3_ARCHIVTABLE a where report_date=?1 and report_label_1=?2");
				qr.setParameter(1, df.parse(todate));
				qr.setParameter(2, filter);

			} else {
				System.out.println("2");
				qr = hs.createNativeQuery("select * from BRF3_ARCHIVTABLE");

			}
		} else {
			System.out.println("3");
			qr = hs.createNativeQuery("select * from BRF3_ARCHIVTABLE  where report_date = ?1");
		}

		/*
		 * try { qr.setParameter(1, df.parse(todate));
		 * 
		 * } catch (ParseException e) { e.printStackTrace(); }
		 */
		List<BRF3_ARCHIVENTITY> T1Master = new ArrayList<BRF3_ARCHIVENTITY>();

		try {
			System.out.println("Values entered");
			T1Master = hs.createQuery("from BRF3_ARCHIVENTITY a where a.report_date = ?1", BRF3_ARCHIVENTITY.class)
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
			String entity_flg = (String) a[40];
			String modify_flg = (String) a[41];
			String del_flg = (String) a[42];
			String nre_status = (String) a[43];
			Date report_date = (Date) a[44];
			String maturity_date = (String) a[45];
			String gender = (String) a[46];
			String version = (String) a[47];
			String remarks = (String) a[48];
			String nreflag = (String) a[49];

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

			BRF3_ARCHIVENTITY py = new BRF3_ARCHIVENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
					acct_crncy_code, gl_code, gl_sub_head_code, gl_sub_head_desc, country_of_incorp, cust_type,
					schm_code, schm_type, sol_id, acid, segment, sub_segment, sector, sub_sector, sector_code, group_id,
					constitution_code, country, legal_entity_type, constitution_desc, purpose_of_advn, hni_networth,
					turnover, bacid, report_name_1, report_label_1, report_addl_criteria_1, report_addl_criteria_2,
					report_addl_criteria_3, create_user, create_time, modify_user, modify_time, verify_user,
					verify_time, entity_flg, modify_flg, del_flg, nre_status, report_date, maturity_date, gender,
					version, remarks, nreflag);
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

		mv.setViewName("RR" + "/" + "BRF3ARCH::reportcontent");
		mv.addObject("reportdetails", T1Dt1Page.getContent());
		mv.addObject("reportmaster", T1Master);
		mv.addObject("reportmaster1", qr);
		mv.addObject("singledetail", new T1CurProdDetail());
		mv.addObject("reportsflag", "reportsflag");
		mv.addObject("menu", reportId);
		return mv;
	}

}
