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
import java.util.Optional;

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
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.entities.BRBS.BRF0001_ARCHIV_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF0001_DETAIL_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF0001_DETAIL_REP;
import com.bornfire.xbrl.entities.BRBS.BRF001_FORT_ENTITY;

import com.bornfire.xbrl.entities.BRBS.BRF2_DETAIL_ENTITY;
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
public class BRF001_FORT_SERVICE {
	private static final Logger logger = LoggerFactory.getLogger(BRF204AReportService.class);

	DateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	DataSource srcdataSource;

	@Autowired
	Environment env;

	@Autowired
	BRF0001_DETAIL_REP BRF0001_DETAIL_REP;

	public String preCheck(String reportid, String fromdate, String todate) {

		String msg = "";
		Session hs = sessionFactory.getCurrentSession();
		Date dt1;
		Date dt9;
		logger.info("Report precheck : " + reportid);

		try {
			
			dt9 = new SimpleDateFormat("dd/MM/yyyy").parse(todate);
			logger.info("Getting No of records in Master table :" + reportid);
			Long dtlcnt = (Long) hs.createQuery("select count(*) from BRF001_FORT_ENTITY a where a.report_date=?1")
					.setParameter(1, dt9).getSingleResult();

			if (dtlcnt > 0) {
				logger.info("Getting No of records in Mod table :" + reportid);
				Long modcnt = (Long) hs.createQuery("select count(*) from BRF001_FORT_ENTITY a").getSingleResult();
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

	public ModelAndView getBRF0001View(String reportId, String fromdate, String todate, String currency, String dtltype,
			Pageable pageable) {

		ModelAndView mv = new ModelAndView();
		Session hs = sessionFactory.getCurrentSession();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;

		// Query<Object[]> qr;

		List<BRF001_FORT_ENTITY> T1Master = new ArrayList<BRF001_FORT_ENTITY>();

		logger.info("Inside archive" + currency);

		try {
			Date d1 = df.parse(todate);
			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			T1Master = hs.createQuery("from BRF001_FORT_ENTITY a where a.report_date = ?1 ", BRF001_FORT_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();

		} catch (ParseException e) {
			e.printStackTrace();
		}

		// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

		mv.setViewName("RR/BRF0001");
		// mv.addObject("currlist", refCodeConfig.currList());
		mv.addObject("reportsummary", T1Master);

		mv.addObject("reportmaster", T1Master);
		mv.addObject("displaymode", "summary");
		mv.addObject("reportsflag", "reportsflag");
		mv.addObject("menu", reportId);
		System.out.println("scv" + mv.getViewName());

		return mv;

	}

	public ModelAndView getBRF0001currentDtl(String reportId, String fromdate, String todate, String currency,
			String dtltype, Pageable pageable, String filter,String searchVal) {

		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;

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
						"select * from BRF001_DETAILTABLE  a where report_date = ?1 and report_label_1 =?2" + searchCondition);
				countQr = hs.createNativeQuery("select count(*) from BRF001_DETAILTABLE a where report_date = ?1 and report_label_1 = ?2" + searchCondition);

				qr.setParameter(2, filter);
				countQr.setParameter(2, filter);

			} else {
			qr = hs.createNativeQuery("select * from BRF001_DETAILTABLE a where report_date = ?1" + searchCondition);
			countQr = hs.createNativeQuery("select count(*) from BRF001_DETAILTABLE a where report_date = ?1" + searchCondition);

			}
		} else {System.out.println("3");
		qr = hs.createNativeQuery("select * from BRF001_DETAILTABLE  where report_date = ?1" + searchCondition);
		countQr = hs.createNativeQuery("select count(*) from BRF001_DETAILTABLE where report_date = ?1" + searchCondition);
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
		
		
		List<BRF0001_DETAIL_ENTITY> T1Master = new ArrayList<BRF0001_DETAIL_ENTITY>();

		/*try {
			T1Master = hs.createQuery("from BRF0001_DETAIL_ENTITY a where a.report_date = ?1", BRF0001_DETAIL_ENTITY.class)
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

			BRF0001_DETAIL_ENTITY py = new BRF0001_DETAIL_ENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc, acct_name,
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
		Page<Object> T1Dt1Page = new PageImpl<>(T1Dt1, pageable, totalRecords);
		mv.addObject("reportdetailsPage", T1Dt1Page);
		mv.setViewName("RR" + "/" + "BRF0001::reportcontent");
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
			String filetype) throws FileNotFoundException, JRException, SQLException {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		String path = this.env.getProperty("output.exportpath");
		String fileName = "";
		String zipFileName = "";
		File outputFile;

		logger.info("Getting Output file :" + reportId);
		fileName = "011-BRF-001-A";

		if (!filetype.equals("xbrl")) {
			if(!filetype.equals("BRF")) {
			try {
				InputStream jasperFile;
				logger.info("Getting Jasper file :" + reportId);
				if (filetype.equals("detailexcel")) {
					if (dtltype.equals("report")) {

						jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF001_Detail.jrxml");
					} else {
						jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF001_Detail.jrxml");
					}

				} else {
					if (dtltype.equals("report")) {
						logger.info("Inside report");
						jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF001.jrxml");
					} else {
						jasperFile = this.getClass().getResourceAsStream("/static/jasper/BRF001.jrxml");
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
			return outputFile;
		}
		else {
			List<BRF001_FORT_ENTITY> T1Master = new ArrayList<>();
			

			Session hs = sessionFactory.getCurrentSession();
			try {
				Date d1 = df.parse(todate);

				T1Master = hs.createQuery("from BRF001_FORT_ENTITY a where a.report_date = ?1", BRF001_FORT_ENTITY.class)
						.setParameter(1, df.parse(todate)).getResultList();
				

				File responseCamt = new File(env.getProperty("output.exportpathtemp") + "011-BRF-0001-AT.xls");
				Workbook workbook = WorkbookFactory.create(responseCamt);
				
				Sheet sheet = workbook.getSheetAt(0);
				
				if (T1Master.size() == 1) {
					for (BRF001_FORT_ENTITY brf001 : T1Master) {
						Row row9 = sheet.getRow(8);
						Cell R9cell1 = row9.getCell(3); 
						if (R9cell1 != null) {
							R9cell1.setCellValue(brf001.getR9_average_qualify() == null ? 0 : brf001.getR9_average_qualify().intValue());
						}
						Row row10 = sheet.getRow(9);
						Cell R10cell1 = row10.getCell(3); 
						if (R10cell1 != null) {
							R10cell1.setCellValue(brf001.getR10_average_qualify() == null ? 0 : brf001.getR10_average_qualify().intValue());
						}
						Row row11 = sheet.getRow(10);
						Cell R11cell1 = row11.getCell(3); 
						if (R11cell1 != null) {
							R11cell1.setCellValue(brf001.getR11_average_qualify() == null ? 0 : brf001.getR11_average_qualify().intValue());
						}
						Row row12 = sheet.getRow(11);
						Cell R12cell1 = row12.getCell(3); 
						if (R12cell1 != null) {
							R12cell1.setCellValue(brf001.getR12_average_qualify() == null ? 0 : brf001.getR12_average_qualify().intValue());
						}
						Row row13 = sheet.getRow(12);
						Cell R13cell1 = row13.getCell(3); 
						if (R13cell1 != null) {
							R13cell1.setCellValue(brf001.getR13_average_qualify() == null ? 0 : brf001.getR13_average_qualify().intValue());
						}
                        Row row15 = sheet.getRow(14);
                        Cell R15cell1 = row15.getCell(3); 
						if (R15cell1 != null) {
							R15cell1.setCellValue(brf001.getR15_average_qualify() == null ? 0 : brf001.getR15_average_qualify().intValue());
						}
						Row row16 = sheet.getRow(15);
						Cell R16cell1 = row16.getCell(3); 
						if (R16cell1 != null) {
							R16cell1.setCellValue(brf001.getR16_average_qualify() == null ? 0 : brf001.getR16_average_qualify().intValue());
						}
						Row row17 = sheet.getRow(16);
						Cell R17cell1 = row17.getCell(3); 
						if (R17cell1 != null) {
							R17cell1.setCellValue(brf001.getR17_average_qualify() == null ? 0 : brf001.getR17_average_qualify().intValue());
						}
						Row row18 = sheet.getRow(17);
						Cell R18cell1 = row18.getCell(3); 
						if (R18cell1 != null) {
							R18cell1.setCellValue(brf001.getR18_average_qualify() == null ? 0 : brf001.getR18_average_qualify().intValue());
						}
                        Row row19 = sheet.getRow(18);
                        Cell R19cell1 = row19.getCell(3); 
						if (R19cell1 != null) {
							R19cell1.setCellValue(brf001.getR19_average_qualify() == null ? 0 : brf001.getR19_average_qualify().intValue());
						}
						Row row21 = sheet.getRow(20);
						Cell R21cell1 = row21.getCell(3); 
						if (R21cell1 != null) {
							R21cell1.setCellValue(brf001.getR21_average_qualify() == null ? 0 : brf001.getR21_average_qualify().intValue());
						}
						Row row22 = sheet.getRow(21);
						Cell R22cell1 = row22.getCell(3); 
						if (R22cell1 != null) {
							R22cell1.setCellValue(brf001.getR22_average_qualify() == null ? 0 : brf001.getR22_average_qualify().intValue());
						}
						Row row23 = sheet.getRow(22);
						Cell R23cell1 = row23.getCell(3); 
						if (R23cell1 != null) {
							R23cell1.setCellValue(brf001.getR23_average_qualify() == null ? 0 : brf001.getR23_average_qualify().intValue());
						}
                        Row row24 = sheet.getRow(23);
                        Cell R24cell1 = row24.getCell(3); 
						if (R24cell1 != null) {
							R24cell1.setCellValue(brf001.getR24_average_qualify() == null ? 0 : brf001.getR24_average_qualify().intValue());
						}
						Row row25 = sheet.getRow(24);
						Cell R25cell1 = row25.getCell(3); 
						if (R25cell1 != null) {
							R25cell1.setCellValue(brf001.getR25_average_qualify() == null ? 0 : brf001.getR25_average_qualify().intValue());
						}
						Row row27 = sheet.getRow(26);
						Cell R27cell1 = row27.getCell(3); 
						if (R27cell1 != null) {
							R27cell1.setCellValue(brf001.getR27_average_qualify() == null ? 0 : brf001.getR27_average_qualify().intValue());
						}
						Row row28 = sheet.getRow(27);
						Cell R28cell1 = row28.getCell(3); 
						if (R28cell1 != null) {
							R28cell1.setCellValue(brf001.getR28_average_qualify() == null ? 0 : brf001.getR28_average_qualify().intValue());
						}
                        Row row29 = sheet.getRow(28);
                        Cell R29cell1 = row29.getCell(3); 
						if (R29cell1 != null) {
							R29cell1.setCellValue(brf001.getR29_average_qualify() == null ? 0 : brf001.getR29_average_qualify().intValue());
						}
						Row row30 = sheet.getRow(29);
						Cell R30cell1 = row30.getCell(3); 
						if (R30cell1 != null) {
							R30cell1.setCellValue(brf001.getR30_average_qualify() == null ? 0 : brf001.getR30_average_qualify().intValue());
						}
						Row row31 = sheet.getRow(30);
						Cell R31cell1 = row31.getCell(3); 
						if (R31cell1 != null) {
							R31cell1.setCellValue(brf001.getR31_average_qualify() == null ? 0 : brf001.getR31_average_qualify().intValue());
						}
					}
				}
				// Save the changes
				workbook.getCreationHelper().createFormulaEvaluator().evaluateAll();
				FileOutputStream fileOut = new FileOutputStream(env.getProperty("output.exportpathfinal")+"011-BRF-001-A.xls");
	            workbook.write(fileOut);
	            fileOut.close();
	            System.out.println(fileOut);
	            path= fileOut.toString();
	            // Close the workbook
	            System.out.println("PATH : "+path);
	            workbook.close();
					
				
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
		outputFile = new File(env.getProperty("output.exportpathfinal")+"011-BRF-001-A.xls");

		return outputFile;
	
	
	
	}
				
//EDIT
	public String detailChangesbrf001(BRF0001_DETAIL_ENTITY detail, String foracid, String report_addl_criteria_1,
			BigDecimal act_balance_amt_lc, String report_label_1, String report_name_1) {

		String msg = "";

		try {

			Session hs = sessionFactory.getCurrentSession();
			Optional<BRF0001_DETAIL_ENTITY> Brf0001detail = BRF0001_DETAIL_REP.findById(foracid);

			if (Brf0001detail.isPresent()) {
				BRF0001_DETAIL_ENTITY BRFdetail = Brf0001detail.get();

				if (BRFdetail.getReport_label_1().equals(report_label_1)
						&& BRFdetail.getReport_name_1().equals(report_name_1)
						&& BRFdetail.getAct_balance_amt_lc().equals(act_balance_amt_lc)
						&& BRFdetail.getReport_addl_criteria_1().equals(report_addl_criteria_1)

				) {
					msg = "No modification done";
				} else {

					BRFdetail.setAct_balance_amt_lc(act_balance_amt_lc);
					BRFdetail.setReport_label_1(report_label_1);
					BRFdetail.setReport_name_1(report_name_1);
					BRFdetail.setReport_addl_criteria_1(report_addl_criteria_1);
					BRF0001_DETAIL_REP.save(BRFdetail);

//						hs.saveOrUpdate(detail);
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

	/****
	 * ARCH SUMMARY
	 * 
	 * @throws ParseException
	 ****/
	public ModelAndView getArchieveBRF0001View(String reportId, String fromdate, String todate, String currency,
			String dtltype, Pageable pageable) {

		ModelAndView mv = new ModelAndView();
		Session hs = sessionFactory.getCurrentSession();
		int pageSize = pageable.getPageSize();
		int currentPage = pageable.getPageNumber();
		int startItem = currentPage * pageSize;
		List<BRF001_FORT_ENTITY> T1Master = new ArrayList<BRF001_FORT_ENTITY>();

		logger.info("Inside archive" + currency);

		try {
			Date d1 = df.parse(todate);
			// T1rep = t1CurProdServiceRepo.getT1CurProdServices(d1);

			T1Master = hs.createQuery("from BRF001_FORT_ENTITY a where a.report_date = ?1 ", BRF001_FORT_ENTITY.class)
					.setParameter(1, df.parse(todate)).getResultList();

		} catch (ParseException e) {
			e.printStackTrace();
		}
		mv.setViewName("RR/BRF0001ARCH");
		mv.addObject("reportsummary", T1Master);

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

	public ModelAndView ARCHgetBRF0001currentDtl(String reportId, String fromdate, String todate, String currency,
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
				qr = hs.createNativeQuery(
						"select * from BRF001_ARCHIVTABLE   a where report_date = ?1 and report_label_1 =?2");
				qr.setParameter(2, filter);

			} else {
				System.out.println("2");
				qr = hs.createNativeQuery("select * from BRF001_ARCHIVTABLE a where report_date = ?1");

			}
		} else {
			System.out.println("3");
			qr = hs.createNativeQuery("select * from BRF001_ARCHIVTABLE  where report_date = ?1");
		}

		try {
			qr.setParameter(1, df.parse(todate));

		} catch (ParseException e) {
			e.printStackTrace();
		}
		List<BRF0001_ARCHIV_ENTITY> T1Master = new ArrayList<BRF0001_ARCHIV_ENTITY>();

		try {
			T1Master = hs
					.createQuery("from BRF0001_ARCHIV_ENTITY a where a.report_date = ?1", BRF0001_ARCHIV_ENTITY.class)
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
			Date maturity_date = (Date) a[45];
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

			BRF0001_ARCHIV_ENTITY py = new BRF0001_ARCHIV_ENTITY(cust_id, foracid, act_balance_amt_lc, eab_lc,
					acct_name, acct_crncy_code, gl_code, gl_sub_head_code, gl_sub_head_desc, country_of_incorp,
					cust_type, schm_code, schm_type, sol_id, acid, segment, sub_segment, sector, sub_sector,
					sector_code, group_id, constitution_code, country, legal_entity_type, constitution_desc,
					purpose_of_advn, hni_networth, turnover, bacid, report_name_1, report_label_1,
					report_addl_criteria_1, report_addl_criteria_2, report_addl_criteria_3, create_user, create_time,
					modify_user, modify_time, verify_user, verify_time, entity_flg, modify_flg, del_flg, nre_status,
					report_date, maturity_date, gender, version, remarks, nreflag);
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

		mv.setViewName("RR" + "/" + "BRF0001ARCH::reportcontent");
		mv.addObject("reportdetails", T1Dt1Page.getContent());
		mv.addObject("reportmaster", T1Master);
		mv.addObject("reportmaster1", qr);
		mv.addObject("singledetail", new T1CurProdDetail());
		mv.addObject("reportsflag", "reportsflag");
		mv.addObject("menu", reportId);
		return mv;
	}
}
