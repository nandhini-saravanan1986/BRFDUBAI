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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
import com.bornfire.xbrl.entities.BRF4_ARCHIVENTITY;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.BRBS.BRF4_DETAIL_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF4_DetaiRep;
import com.bornfire.xbrl.entities.BRBS.BRF4_ENTITY;
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
public class BRF004ReportService {
	private static final Logger logger = LoggerFactory.getLogger(BRF004ReportService.class);

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	DataSource srcdataSource;

	@Autowired
	Environment env;

	@Autowired
	BRF4_DetaiRep BRF4_DetaiRep1;
	
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
			dt9 = new SimpleDateFormat("dd/MM/yyyy").parse(todate);
			logger.info("Getting No of records in Master table :" + reportid);
			Long dtlcnt = (Long) hs.createQuery("select count(*) from BRF4_ENTITY a where a.report_date=?1")
					.setParameter(1, dt9).getSingleResult();

			if (dtlcnt > 0) {
				logger.info("Getting No of records in Mod table :" + reportid);
				Long modcnt = (Long) hs.createQuery("select count(*) from BRF4_ENTITY a").getSingleResult();
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

	public ModelAndView getBRF004View(String reportId, String fromdate, String todate, String currency, String dtltype,
			Pageable pageable) {

		ModelAndView mv = new ModelAndView();
		Session hs = sessionFactory.getCurrentSession();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<BRF4_ENTITY> T1rep = new ArrayList<BRF4_ENTITY>();
		// Query<Object[]> qr;

		List<BRF4_ENTITY> T1Master = new ArrayList<BRF4_ENTITY>();
		/* List<BRF73_TABLE2> T1Master1 = new ArrayList<BRF73_TABLE2>(); */

		logger.info("Inside archive" + currency);

		try {
			Date d1 = df.parse(todate);
			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			T1Master = hs.createQuery("from  BRF4_ENTITY a where a.report_date = ?1 ", BRF4_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();

			/*
			 * T1Master1 = hs.createQuery("from BRF73_TABLE2 a where a.report_date = ?1 ",
			 * BRF73_TABLE2.class) .setParameter(1, df.parse(todate)).getResultList();
			 */

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

		mv.setViewName("RR/BRF4");
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

	public ModelAndView getBRF004currentDtl(String reportId, String fromdate, String todate, String currency,
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
						"select * from BRF4_DETAILTABLE  a where report_date = ?1 and report_label_1 =?2" + searchCondition);
				countQr = hs.createNativeQuery("select count(*) from BRF4_DETAILTABLE a where report_date = ?1 and report_label_1 = ?2" + searchCondition);

				qr.setParameter(2, filter);
				countQr.setParameter(2, filter);

			} else {
				qr = hs.createNativeQuery("select * from BRF4_DETAILTABLE a where report_date = ?1" + searchCondition);
				countQr = hs.createNativeQuery("select count(*) from BRF4_DETAILTABLE a where report_date = ?1" + searchCondition);
			}
		} else {
			qr = hs.createNativeQuery("select * from BRF4_DETAILTABLE  where report_date = ?1" + searchCondition);
			countQr = hs.createNativeQuery("select count(*) from BRF4_DETAILTABLE where report_date = ?1" + searchCondition);
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
		List<BRF4_DETAIL_ENTITY> T1Master = new ArrayList<BRF4_DETAIL_ENTITY>();
/*
		try {
			T1Master = hs.createQuery("from BRF4_DETAIL_ENTITY a where a.report_date = ?1", BRF4_DETAIL_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();
		} catch (ParseException e) {

			e.printStackTrace();
		}*/

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

			BRF4_DETAIL_ENTITY py = new BRF4_DETAIL_ENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
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
		mv.addObject("reportdetailsPage", T1Dt1Page);
		mv.addObject("searchvalue", searchVal);
		
		mv.setViewName("RR" + "/" + "BRF4::reportcontent");
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
		fileName = "011-BRF-004-A";
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
			if (!filetype.equals("BRF")) {
				try {
					InputStream jasperFile;
					logger.info("Getting Jasper file :" + reportId);
					if (filetype.equals("detailexcel")) {
						if (dtltype.equals("report")) {

							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF4_Detail.jrxml");
						} else {
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF4_Detail.jrxml");
						}

					} else {
						if (dtltype.equals("report")) {
							logger.info("Inside report");
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF4.jrxml");
						} else {
							jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF4.jrxml");
						}
					}

					/* JasperReport jr = (JasperReport) JRLoader.loadObject(jasperFile); */
					JasperReport jr = JasperCompileManager.compileReport(jasperFile);
					HashMap<String, Object> map = new HashMap<String, Object>();

					logger.info("Assigning Parameters for Jasper");
					map.put("REPORT_DATE", todate);
					/*
					 * try { SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd-MM-yyyy"); Date
					 * ConDate = dateFormat1.parse(todate); SimpleDateFormat formatter1 = new
					 * SimpleDateFormat("dd-MMM-yyyy"); String strDate1 =
					 * formatter1.format(ConDate);
					 * 
					 * String today = dateFormat.format(new
					 * SimpleDateFormat("dd-MMM-yyyy").parse(strDate1));
					 * 
					 * } catch (ParseException e1) {
					 * 
					 * logger.info(e1.getMessage()); e1.printStackTrace(); }
					 */
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
				audit.setAudit_table("BRF4_SUMMARYTABLE");
				audit.setEvent_id(user1);
				audit.setEvent_name(username);

				if (filetype.equals("pdf")) {
					audit.setFunc_code("DOWNLOAD_PDF");
					audit.setAudit_screen("Download PDF");
					audit.setRemarks("BRF4 PDF downloaded successfully");
				} else if (filetype.equals("detailexcel")) {
					audit.setAudit_table("BRF4_DETAILTABLE");
					audit.setFunc_code("DOWNLOAD_EXCEL_DETAIL");
					audit.setAudit_screen("Download Excel Detail");
					audit.setRemarks("BRF4 Detailed Excel downloaded successfully");
				} else {
					audit.setFunc_code("DOWNLOAD");
					audit.setAudit_screen("Download");
					audit.setRemarks("BRF4 File downloaded successfully");
				}

				UserProfile values1 = userProfileRep.getRole(user1);
				audit.setAuth_user(values1.getAuth_user());
				audit.setAuth_time(values1.getAuth_time());
				audit.setAudit_ref_no(auditID);

				mANUAL_Service_Rep.save(audit);
				
				return outputFile;
			} else {

				List<BRF4_ENTITY> T1Master = new ArrayList<BRF4_ENTITY>();
				Session hs = sessionFactory.getCurrentSession();
				try {
					Date d1 = df.parse(todate);

					// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

					T1Master = hs.createQuery("from  BRF4_ENTITY a where a.report_date = ?1 ", BRF4_ENTITY.class)
							.setParameter(1, df.parse(todate)).getResultList();

					if (T1Master.size() == 1) {

						for (BRF4_ENTITY BRF04 : T1Master) {

							File Responsecamt = new File(
									env.getProperty("output.exportpathtemp") + "011-BRF-004-AT.xls");

							// Load the Excel file
							Workbook workbook = WorkbookFactory.create(Responsecamt);

							// Get the first sheet
							Sheet sheet = workbook.getSheetAt(0);

							/*
							 * Row r1row0 = sheet.getRow(4); if (r1row0 != null) { Cell r1cell0 =
							 * r1row0.getCell(2); if (r1cell0 == null) { r1cell0 = r1row0.createCell(2); //
							 * Create the cell if it doesn't exist } if (BRF04.getReport_date() != null) {
							 * // Formatting the date to dd-MM-yyyy SimpleDateFormat sdf = new
							 * SimpleDateFormat("dd-MM-yyyy"); String formattedDate =
							 * sdf.format(BRF04.getReport_date()); r1cell0.setCellValue(formattedDate); //
							 * Set the formatted date in the cell } else { r1cell0.setCellValue(""); // Set
							 * an empty value if the report_date is null } }
							 */

							/// Row2
							Row r2row = sheet.getRow(8);
							Cell r2cell = r2row.getCell(4);
							if (r2cell != null) {
								r2cell.setCellValue(
										BRF04.getR2_year_to_date() == null ? 0 : BRF04.getR2_year_to_date().intValue());
							}

							/// Row3
							Row r3row = sheet.getRow(9);
							Cell r3cell = r3row.getCell(4);
							if (r3cell != null) {
								r3cell.setCellValue(
										BRF04.getR3_year_to_date() == null ? 0 : BRF04.getR3_year_to_date().intValue());
							}
							/// Row5
							Row r5row = sheet.getRow(11);
							Cell r5cell = r5row.getCell(4);
							if (r5cell != null) {
								r5cell.setCellValue(
										BRF04.getR5_year_to_date() == null ? 0 : BRF04.getR5_year_to_date().intValue());
							}
							/// Row6
							Row r6row = sheet.getRow(12);
							Cell r6cell = r6row.getCell(4);
							if (r6cell != null) {
								r6cell.setCellValue(
										BRF04.getR6_year_to_date() == null ? 0 : BRF04.getR6_year_to_date().intValue());
							}
							/// Row7
							Row r7row = sheet.getRow(13);
							Cell r7cell = r7row.getCell(4);
							if (r7cell != null) {
								r7cell.setCellValue(
										BRF04.getR7_year_to_date() == null ? 0 : BRF04.getR7_year_to_date().intValue());
							}
							/// Row8
							Row r8row = sheet.getRow(14);
							Cell r8cell = r8row.getCell(4);
							if (r8cell != null) {
								r8cell.setCellValue(
										BRF04.getR8_year_to_date() == null ? 0 : BRF04.getR8_year_to_date().intValue());
							}
							/// Row9
							Row r9row = sheet.getRow(15);
							Cell r9cell = r9row.getCell(4);
							if (r9cell != null) {
								r9cell.setCellValue(
										BRF04.getR9_year_to_date() == null ? 0 : BRF04.getR9_year_to_date().intValue());
							}
							/// Row10
							Row r10row = sheet.getRow(16);
							Cell r10cell = r10row.getCell(4);
							if (r10cell != null) {
								r10cell.setCellValue(BRF04.getR10_year_to_date() == null ? 0
										: BRF04.getR10_year_to_date().intValue());
							}
							/// Row11
							Row r11row = sheet.getRow(17);
							Cell r11cell = r11row.getCell(4);
							if (r11cell != null) {
								r11cell.setCellValue(BRF04.getR11_year_to_date() == null ? 0
										: BRF04.getR11_year_to_date().intValue());
							}
							/// Row12
							Row r12row = sheet.getRow(18);
							Cell r12cell = r12row.getCell(4);
							if (r12cell != null) {
								r12cell.setCellValue(BRF04.getR12_year_to_date() == null ? 0
										: BRF04.getR12_year_to_date().intValue());
							}
							/// Row13
							Row r13row = sheet.getRow(19);
							Cell r13cell = r13row.getCell(4);
							if (r13cell != null) {
								r13cell.setCellValue(BRF04.getR13_year_to_date() == null ? 0
										: BRF04.getR13_year_to_date().intValue());
							}
							/// Row15
							Row r15row = sheet.getRow(21);
							Cell r15cell = r15row.getCell(4);
							if (r15cell != null) {
								r15cell.setCellValue(BRF04.getR15_year_to_date() == null ? 0
										: BRF04.getR15_year_to_date().intValue());
							}
							/// Row16
							Row r16row = sheet.getRow(22);
							Cell r16cell = r16row.getCell(4);
							if (r16cell != null) {
								r16cell.setCellValue(BRF04.getR16_year_to_date() == null ? 0
										: BRF04.getR16_year_to_date().intValue());
							}
							/// Row17
							Row r17row = sheet.getRow(23);
							Cell r17cell = r17row.getCell(4);
							if (r17cell != null) {
								r17cell.setCellValue(BRF04.getR17_year_to_date() == null ? 0
										: BRF04.getR17_year_to_date().intValue());
							}
							/// Row18
							Row r18row = sheet.getRow(24);
							Cell r18cell = r18row.getCell(4);
							if (r18cell != null) {
								r18cell.setCellValue(BRF04.getR18_year_to_date() == null ? 0
										: BRF04.getR18_year_to_date().intValue());
							}
							/// Row19
							Row r19row = sheet.getRow(25);
							Cell r19cell = r19row.getCell(4);
							if (r19cell != null) {
								r19cell.setCellValue(BRF04.getR19_year_to_date() == null ? 0
										: BRF04.getR19_year_to_date().intValue());
							}
							/// Row21
							Row r21row = sheet.getRow(27);
							Cell r21cell = r21row.getCell(4);
							if (r21cell != null) {
								r21cell.setCellValue(BRF04.getR21_year_to_date() == null ? 0
										: BRF04.getR21_year_to_date().intValue());
							}
							/// Row22
							Row r22row = sheet.getRow(28);
							Cell r22cell = r22row.getCell(4);
							if (r22cell != null) {
								r22cell.setCellValue(BRF04.getR22_year_to_date() == null ? 0
										: BRF04.getR22_year_to_date().intValue());
							}
							/// Row23
							Row r23row = sheet.getRow(29);
							Cell r23cell = r23row.getCell(4);
							if (r23cell != null) {
								r23cell.setCellValue(BRF04.getR23_year_to_date() == null ? 0
										: BRF04.getR23_year_to_date().intValue());
							}
							/// Row25
							Row r25row = sheet.getRow(31);
							Cell r25cell = r25row.getCell(4);
							if (r25cell != null) {
								r25cell.setCellValue(BRF04.getR25_year_to_date() == null ? 0
										: BRF04.getR25_year_to_date().intValue());
							}
							/// Row26
							Row r26row = sheet.getRow(32);
							Cell r26cell = r26row.getCell(4);
							if (r26cell != null) {
								r26cell.setCellValue(BRF04.getR26_year_to_date() == null ? 0
										: BRF04.getR26_year_to_date().intValue());
							}
							/// Row27
							Row r27row = sheet.getRow(33);
							Cell r27cell = r27row.getCell(4);
							if (r27cell != null) {
								r27cell.setCellValue(BRF04.getR27_year_to_date() == null ? 0
										: BRF04.getR27_year_to_date().intValue());
							}
							/// Row28
							Row r28row = sheet.getRow(34);
							Cell r28cell = r28row.getCell(4);
							if (r28cell != null) {
								r28cell.setCellValue(BRF04.getR28_year_to_date() == null ? 0
										: BRF04.getR28_year_to_date().intValue());
							}
							/// Row31
							Row r31row = sheet.getRow(37);
							Cell r31cell = r31row.getCell(4);
							if (r31cell != null) {
								r31cell.setCellValue(BRF04.getR31_year_to_date() == null ? 0
										: BRF04.getR31_year_to_date().intValue());
							}
							/// Row33
							Row r33row = sheet.getRow(39);
							Cell r33cell = r33row.getCell(4);
							if (r33cell != null) {
								r33cell.setCellValue(BRF04.getR33_year_to_date() == null ? 0
										: BRF04.getR33_year_to_date().intValue());
							}
							/// Row34
							Row r34row = sheet.getRow(40);
							Cell r34cell = r34row.getCell(4);
							if (r34cell != null) {
								r34cell.setCellValue(BRF04.getR34_year_to_date() == null ? 0
										: BRF04.getR34_year_to_date().intValue());
							}
							/// Row35
							Row r35row = sheet.getRow(41);
							Cell r35cell = r35row.getCell(4);
							if (r35cell != null) {
								r35cell.setCellValue(BRF04.getR35_year_to_date() == null ? 0
										: BRF04.getR35_year_to_date().intValue());
							}
							/// Row36
							Row r36row = sheet.getRow(42);
							Cell r36cell = r36row.getCell(4);
							if (r36cell != null) {
								r36cell.setCellValue(BRF04.getR36_year_to_date() == null ? 0
										: BRF04.getR36_year_to_date().intValue());
							}
							/// Row37
							Row r37row = sheet.getRow(43);
							Cell r37cell = r37row.getCell(4);
							if (r37cell != null) {
								r37cell.setCellValue(BRF04.getR37_year_to_date() == null ? 0
										: BRF04.getR37_year_to_date().intValue());
							}
							/// Row38
							Row r38row = sheet.getRow(44);
							Cell r38cell = r38row.getCell(4);
							if (r38cell != null) {
								r38cell.setCellValue(BRF04.getR38_year_to_date() == null ? 0
										: BRF04.getR38_year_to_date().intValue());
							}
							/// Row39
							Row r39row = sheet.getRow(45);
							Cell r39cell = r39row.getCell(4);
							if (r39cell != null) {
								r39cell.setCellValue(BRF04.getR39_year_to_date() == null ? 0
										: BRF04.getR39_year_to_date().intValue());
							}

							/// Row40
							Row r40row = sheet.getRow(46);
							Cell r40cell = r40row.getCell(4);
							if (r40cell != null) {
								r40cell.setCellValue(BRF04.getR40_year_to_date() == null ? 0
										: BRF04.getR40_year_to_date().intValue());
							}
							/// Row41
							Row r41row = sheet.getRow(47);
							Cell r41cell = r41row.getCell(4);
							if (r41cell != null) {
								r41cell.setCellValue(BRF04.getR41_year_to_date() == null ? 0
										: BRF04.getR41_year_to_date().intValue());
							}
							/// Row42
							Row r42row = sheet.getRow(48);
							Cell r42cell = r42row.getCell(4);
							if (r42cell != null) {
								r42cell.setCellValue(BRF04.getR42_year_to_date() == null ? 0
										: BRF04.getR42_year_to_date().intValue());
							}
							/// Row45
							Row r45row = sheet.getRow(51);
							Cell r45cell = r45row.getCell(4);
							if (r45cell != null) {
								r45cell.setCellValue(BRF04.getR45_year_to_date() == null ? 0
										: BRF04.getR45_year_to_date().intValue());
							}
							/// Row46
							Row r46row = sheet.getRow(52);
							Cell r46cell = r46row.getCell(4);
							if (r46cell != null) {
								r46cell.setCellValue(BRF04.getR46_year_to_date() == null ? 0
										: BRF04.getR46_year_to_date().intValue());
							}
							/// Row47
							Row r47row = sheet.getRow(53);
							Cell r47cell = r47row.getCell(4);
							if (r47cell != null) {
								r47cell.setCellValue(BRF04.getR47_year_to_date() == null ? 0
										: BRF04.getR47_year_to_date().intValue());
							}
							/// Row48
							Row r48row = sheet.getRow(54);
							Cell r48cell = r48row.getCell(4);
							if (r48cell != null) {
								r48cell.setCellValue(BRF04.getR48_year_to_date() == null ? 0
										: BRF04.getR48_year_to_date().intValue());
							}
							/// Row49
							Row r49row = sheet.getRow(55);
							Cell r49cell = r49row.getCell(4);
							if (r49cell != null) {
								r49cell.setCellValue(BRF04.getR49_year_to_date() == null ? 0
										: BRF04.getR49_year_to_date().intValue());
							}
							/// Row51
							Row r51row = sheet.getRow(57);
							Cell r51cell = r51row.getCell(4);
							if (r51cell != null) {
								r51cell.setCellValue(BRF04.getR51_year_to_date() == null ? 0
										: BRF04.getR51_year_to_date().intValue());
							}
							/// Row52
							Row r52row = sheet.getRow(58);
							Cell r52cell = r52row.getCell(4);
							if (r52cell != null) {
								r52cell.setCellValue(BRF04.getR52_year_to_date() == null ? 0
										: BRF04.getR52_year_to_date().intValue());
							}
							/// Row53
							Row r53row = sheet.getRow(59);
							Cell r53cell = r53row.getCell(4);
							if (r53cell != null) {
								r53cell.setCellValue(BRF04.getR53_year_to_date() == null ? 0
										: BRF04.getR53_year_to_date().intValue());
							}
							/// Row56
							Row r56row = sheet.getRow(62);
							Cell r56cell = r56row.getCell(4);
							if (r56cell != null) {
								r56cell.setCellValue(BRF04.getR56_year_to_date() == null ? 0
										: BRF04.getR56_year_to_date().intValue());
							}
							/// Row57
							Row r57row = sheet.getRow(63);
							Cell r57cell = r57row.getCell(4);
							if (r57cell != null) {
								r57cell.setCellValue(BRF04.getR57_year_to_date() == null ? 0
										: BRF04.getR57_year_to_date().intValue());
							}
							/// Row59
							Row r59row = sheet.getRow(65);
							Cell r59cell = r59row.getCell(4);
							if (r59cell != null) {
								r59cell.setCellValue(BRF04.getR59_year_to_date() == null ? 0
										: BRF04.getR59_year_to_date().intValue());
							}

							/// Row61
							Row r61row = sheet.getRow(67);
							Cell r61cell = r61row.getCell(4);
							if (r61cell != null) {
								r61cell.setCellValue(BRF04.getR61_year_to_date() == null ? 0
										: BRF04.getR61_year_to_date().intValue());
							}
							/// Row62
							Row r62row = sheet.getRow(68);
							Cell r62cell = r62row.getCell(4);
							if (r62cell != null) {
								r62cell.setCellValue(BRF04.getR62_year_to_date() == null ? 0
										: BRF04.getR62_year_to_date().intValue());
							}

							/// Row63
							Row r63row = sheet.getRow(69);
							Cell r63cell = r63row.getCell(4);
							if (r63cell != null) {
								r63cell.setCellValue(BRF04.getR63_year_to_date() == null ? 0
										: BRF04.getR63_year_to_date().intValue());
							}
							/// Row64
							Row r64row = sheet.getRow(70);
							Cell r64cell = r64row.getCell(4);
							if (r64cell != null) {
								r64cell.setCellValue(BRF04.getR64_year_to_date() == null ? 0
										: BRF04.getR64_year_to_date().intValue());
							}
							/// Row65
							Row r65row = sheet.getRow(71);
							Cell r65cell = r65row.getCell(4);
							if (r65cell != null) {
								r65cell.setCellValue(BRF04.getR65_year_to_date() == null ? 0
										: BRF04.getR65_year_to_date().intValue());
							}
							/// Row66
							Row r66row = sheet.getRow(72);
							Cell r66cell = r66row.getCell(4);
							if (r66cell != null) {
								r66cell.setCellValue(BRF04.getR66_year_to_date() == null ? 0
										: BRF04.getR66_year_to_date().intValue());
							}
							/// Row68
							Row r68row = sheet.getRow(74);
							Cell r68cell = r68row.getCell(4);
							if (r68cell != null) {
								r68cell.setCellValue(BRF04.getR68_year_to_date() == null ? 0
										: BRF04.getR68_year_to_date().intValue());
							}

							// Save the changes
							workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
							FileOutputStream fileOut = new FileOutputStream(
									env.getProperty("output.exportpathfinal") + "011-BRF-004-A.xls");
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
		outputFile = new File(env.getProperty("output.exportpathfinal") + "011-BRF-004-A.xls");

		return outputFile;

	}
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public String detailChanges4(BRF4_DETAIL_ENTITY detail, String report_label_1, BigDecimal act_balance_amt_lc,
			String foracid, String report_name_1, String report_addl_criteria_1,String report_date, AuditReasonDTO reason){

		String msg = "";

		try {
			Session hs = sessionFactory.getCurrentSession();
			//Optional<BRF4_DETAIL_ENTITY> Brf4detail = BRF4_DetaiRep1.findById(foracid);
			 BRF4_DETAIL_ENTITY Brf4detail = BRF4_DetaiRep1.getbyaccnoanddate(foracid, report_date);

			if (!Brf4detail.equals(null) && Brf4detail!=null) {
				BRF4_DETAIL_ENTITY BRFdetail = Brf4detail;

				// Improved Null-safe check to determine if any changes exist
				boolean isUnchanged = Objects.equals(BRFdetail.getReport_label_1(), report_label_1)
						&& Objects.equals(BRFdetail.getReport_name_1(), report_name_1)
						&& Objects.equals(BRFdetail.getAct_balance_amt_lc(), act_balance_amt_lc)
						&& Objects.equals(BRFdetail.getReport_addl_criteria_1(), report_addl_criteria_1);

				if (isUnchanged) {
					msg = "No modification done";
				} else {

					List<String> oldValues = new ArrayList<>();
					List<String> newValues = new ArrayList<>();
					List<String> fieldNames = new ArrayList<>();

					// Detect specific changes
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

					BRF4_DetaiRep1.save(BRFdetail);
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
					audit.setAudit_table("BRF4_DETAILTABLE");
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
					// PROCEDURE EXECUTION LOGIC (BRF4)
					// =========================================================================
					try {
						// Extract Date from the entity
						Date entityDate = BRFdetail.getReport_date();

						if (entityDate != null) {
							String formattedDate = new SimpleDateFormat("dd-MM-yyyy").format(entityDate);

							// Run summary procedure after commit
							TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
								@Override
								public void afterCommit() {
									try {
										logger.info("Transaction committed — calling BRF4_SUMMARY_PROCEDURE({})", formattedDate);
										
										// Make sure 'jdbcTemplate' is available
										jdbcTemplate.update("BEGIN BRF4_SUMMARY_PROCEDURE(?); END;", formattedDate);
										
										logger.info("BRF4 Procedure executed successfully after commit.");
									} catch (Exception e) {
										logger.error("Error executing BRF4 procedure after commit", e);
									}
								}
							});
						} else {
							logger.warn("Report Date is null in BRF4 entity, skipping summary procedure.");
						}
					} catch (Exception e) {
						logger.error("Error preparing BRF4 procedure call", e);
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

	// TO show thw Archieve values
	public ModelAndView getArchieveBRF004View(String reportId, String fromdate, String todate, String currency,
			String dtltype, Pageable pageable) {

		ModelAndView mv = new ModelAndView();
		Session hs = sessionFactory.getCurrentSession();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<BRF4_ENTITY> T1rep = new ArrayList<BRF4_ENTITY>();
		// Query<Object[]> qr;

		List<BRF4_ENTITY> T1Master = new ArrayList<BRF4_ENTITY>();
		/* List<BRF73_TABLE2> T1Master1 = new ArrayList<BRF73_TABLE2>(); */

		logger.info("Inside archive" + currency);

		try {
			Date d1 = df.parse(todate);
			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			T1Master = hs.createQuery("from  BRF4_ENTITY a where a.report_date = ?1 ", BRF4_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();

			/*
			 * T1Master1 = hs.createQuery("from BRF73_TABLE2 a where a.report_date = ?1 ",
			 * BRF73_TABLE2.class) .setParameter(1, df.parse(todate)).getResultList();
			 */

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

		mv.setViewName("RR/BRF4ARCH");
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

	public ModelAndView ARCHgetBRF004currentDtl(String reportId, String fromdate, String todate, String currency,
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
						"select * from BRF4_ARCHIVTABLE a where report_date=?1 and report_label_1=?2");
				qr.setParameter(1, df.parse(todate));
				qr.setParameter(2, filter);

			} else {
				System.out.println("2");
				qr = hs.createNativeQuery("select * from BRF4_ARCHIVTABLE");

			}
		} else {
			System.out.println("3");
			qr = hs.createNativeQuery("select * from BRF4_ARCHIVTABLE  where report_date = ?1");
		}

		/*
		 * try { qr.setParameter(1, df.parse(todate));
		 * 
		 * } catch (ParseException e) { e.printStackTrace(); }
		 */
		List<BRF4_ARCHIVENTITY> T1Master = new ArrayList<BRF4_ARCHIVENTITY>();

		try {
			System.out.println("Values entered");
			T1Master = hs.createQuery("from BRF4_ARCHIVENTITY a where a.report_date = ?1", BRF4_ARCHIVENTITY.class)
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

			BRF4_ARCHIVENTITY py = new BRF4_ARCHIVENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
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

		mv.setViewName("RR" + "/" + "BRF4ARCH::reportcontent");
		mv.addObject("reportdetails", T1Dt1Page.getContent());
		mv.addObject("reportmaster", T1Master);
		mv.addObject("reportmaster1", qr);
		mv.addObject("singledetail", new T1CurProdDetail());
		mv.addObject("reportsflag", "reportsflag");
		mv.addObject("menu", reportId);
		return mv;
	}

	public Map<String, BigDecimal> getBRF004View_one() {

	    Session hs = sessionFactory.getCurrentSession();
	    Map<String, BigDecimal> monthlyProfitMap = new LinkedHashMap<>();

	    try {
	        LocalDate now = LocalDate.now(); // Current date
	        LocalDate startOfYear = now.withDayOfYear(1);
	        LocalDate endOfYear = now.withMonth(12).withDayOfMonth(31);

	        // Using current date instead of a fixed date
	        LocalDate filterDate = now; // Current date

	        List<BRF4_ENTITY> T1Master = hs.createQuery(
	            "from BRF4_ENTITY a where a.report_date = :reportDate", BRF4_ENTITY.class)
	            .setParameter("reportDate", java.sql.Date.valueOf(filterDate)) // Passing the current date
	            .getResultList();

	        // Initialize all months with 0
	        for (int i = 1; i <= 12; i++) {
	            String monthName = String.format("%02d", i); // e.g., "01", "02"
	            monthlyProfitMap.put(monthName, BigDecimal.ZERO);
	        }

	        for (BRF4_ENTITY entity : T1Master) {
	            Date reportDate = entity.getReport_date();
	            BigDecimal profit = entity.getR67_year_to_date();

	            if (reportDate != null && profit != null) {
	                // Convert java.sql.Date to java.util.Date first
	                java.util.Date utilDate = new java.util.Date(reportDate.getTime());
	                
	                // Then convert java.util.Date to LocalDate
	                LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	                if (localDate != null) {
	                    String monthName = String.format("%02d", localDate.getMonthValue()); // e.g., "03" for March

	                    BigDecimal current = monthlyProfitMap.getOrDefault(monthName, BigDecimal.ZERO);
	                    monthlyProfitMap.put(monthName, current.add(profit));
	                }
	            }
	        }

	        // Print all values for debugging
	        for (Map.Entry<String, BigDecimal> entry : monthlyProfitMap.entrySet()) {
	            System.out.println("Month: " + entry.getKey() + " - Profit: " + entry.getValue());
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return monthlyProfitMap;
	}



	

	public Map<String, BigDecimal> getMonthlyProfitByYear(String year) {

	    Session hs = sessionFactory.getCurrentSession();
	    Map<String, BigDecimal> monthlyProfitMap = new LinkedHashMap<>();

	    try {
	        // Parse the year string to an integer
	        int yearInt = Integer.parseInt(year);

	        // Create start and end dates for the given year
	        LocalDate startOfYear = LocalDate.of(yearInt, 1, 1);
	        LocalDate endOfYear = LocalDate.of(yearInt, 12, 31);

	        // Fetch all records within the year
	        List<BRF4_ENTITY> T1Master = hs.createQuery(
	            "from BRF4_ENTITY a where a.report_date between :start and :end", BRF4_ENTITY.class)
	            .setParameter("start", java.sql.Date.valueOf(startOfYear))
	            .setParameter("end", java.sql.Date.valueOf(endOfYear))
	            .getResultList();

	        // Initialize all months with 0
	        for (int i = 1; i <= 12; i++) {
	            String monthName = String.format("%02d", i); // "01", "02", etc.
	            monthlyProfitMap.put(monthName, BigDecimal.ZERO);
	        }

	        for (BRF4_ENTITY entity : T1Master) {
	            Date reportDate = entity.getReport_date();
	            BigDecimal profit = entity.getR67_year_to_date();

	            if (reportDate != null && profit != null) {
	                java.util.Date utilDate = new java.util.Date(reportDate.getTime());
	                LocalDate localDate = utilDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	                String monthName = String.format("%02d", localDate.getMonthValue());
	                BigDecimal current = monthlyProfitMap.getOrDefault(monthName, BigDecimal.ZERO);
	                monthlyProfitMap.put(monthName, current.add(profit));
	            }
	        }

	        // Debug print
	        for (Map.Entry<String, BigDecimal> entry : monthlyProfitMap.entrySet()) {
	            System.out.println("Month: " + entry.getKey() + " - Profit: " + entry.getValue());
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return monthlyProfitMap;
	}
}
