package com.bornfire.xbrl.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.config.SequenceGenerator;


import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.BRBS.BRF1_DETAIL_ENTITY;
import com.bornfire.xbrl.services.BRF001ReportService;

import com.bornfire.xbrl.services.LoginServices;
import com.bornfire.xbrl.services.RegulatoryReportServices;
import com.bornfire.xbrl.services.ReportServices;

import net.sf.jasperreports.engine.JRException;

@Controller
@ConfigurationProperties("default")
@RequestMapping(value = "Reports")
public class XBRLReportsController {

	private static final Logger logger = LoggerFactory.getLogger(XBRLReportsController.class);

	@Autowired
	ReportServices reportServices;
	@Autowired
	LoginServices loginServices;
	@Autowired
	BRF001ReportService brf001ReportService;

	@Autowired
	RegulatoryReportServices regreportServices;
	@Autowired
	BRF001ReportService BRF001ReportService;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	UserProfileRep userProfileRep;
	

	private String pagesize;

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");

	// To show the required report at the first stage
	@RequestMapping(value = "{reportid}", method = RequestMethod.POST)
	public ModelAndView reportView(@PathVariable("reportid") String reportid,
			@RequestParam(value = "function", required = false) String function,
			@RequestParam("asondate") String asondate, @RequestParam(required = false) String fromdate,
			@RequestParam("todate") String todate, @RequestParam(value = "currency", required = false) String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md,
			HttpServletRequest req, BigDecimal srl_no) {

		String userid = (String) req.getSession().getAttribute("USERID");
		String roleid = (String) req.getSession().getAttribute("ROLEID");
		String accesscode = (String) req.getSession().getAttribute("ACCESSCODE");
		// Logging Navigation
		if (dtltype.equals("report")) {
			md.addAttribute("menu", "XBRLReports");
			loginServices.SessionLogging("REPORTS" + reportid, "M8", req.getSession().getId(), userid,
					req.getRemoteAddr(), "ACTIVE");
		} else {
			md.addAttribute("menu", "XBRLArchives");
			loginServices.SessionLogging("ARCHREPORTS" + reportid, "M9", req.getSession().getId(), userid,
					req.getRemoteAddr(), "ACTIVE");
		}

		logger.info("Get Report :" + reportid);
		logger.info("Get Report :" + asondate);
		try {
			asondate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(asondate));
			fromdate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(fromdate));
			todate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(todate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		logger.info("Get Report :" + asondate);
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Assigning Model Attributes :" + reportid);
		// Assigning required Modal Attributes
		md.addAttribute("UserId", userid);
		md.addAttribute("RoleId", roleid);
		md.addAttribute("UserCol", accesscode);

		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);
		md.addAttribute("fromdate", fromdate);
		md.addAttribute("todate", todate);
		md.addAttribute("currency", currency);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("type", type);
		md.addAttribute("reportingTime", reportingTime);
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = new ModelAndView();
		System.out.println("tttttttttttttt" + userid);

		mv = regreportServices.getReportView(reportid, asondate, fromdate, todate, currency, dtltype, subreportid,
				secid, reportingTime, PageRequest.of(currentPage, pageSize), srl_no, userid);

		// System.out.println("----------------------");

		// Page<Object> sup0700RepPage = (Page<Object>)
		// mv.getModelMap().get("reportsummary");

		// sup0700RepPage.getContent().forEach((a)-> System.out.println(a.toString()));
		List<String> pageSizes = Arrays.asList("A2", "A3", "A4");
		mv.addObject("pageSizes", pageSizes);

		return mv;

	}

	@RequestMapping(value = "{reportid}/Summary", method = RequestMethod.GET)
	public ModelAndView reportSummay(@PathVariable("reportid") String reportid,
			@RequestParam("asondate") String asondate, @RequestParam("fromdate") String fromdate,
			@RequestParam("todate") String todate, @RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md, BigDecimal srl_no,
			HttpServletRequest req) {

		logger.info("Getting Report Summary :" + reportid);

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Assigning Model Attributes :" + reportid);
		md.addAttribute("menu", "XBRLReports");
		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);
		md.addAttribute("fromdate", fromdate);
		md.addAttribute("todate", todate);
		md.addAttribute("type", type);
		md.addAttribute("currency", currency);
		md.addAttribute("reportingTime", reportingTime);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));
		md.addAttribute("reportingTime", reportingTime);
		md.addAttribute("displaymode", "summary");

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		System.out.println("role id issssssssssssssssssssssssssss" + roleId);
		md.addAttribute("operation", roleId);

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = regreportServices.getReportSummary(reportid, asondate, fromdate, todate, currency, dtltype,
				subreportid, secid, reportingTime, PageRequest.of(currentPage, pageSize), srl_no, roleId);
		
		List<String> pageSizes = Arrays.asList("A2", "A3", "A4");
		mv.addObject("pageSizes", pageSizes);

		return mv;

	}

	@RequestMapping(value = "{reportid}/Details", method = RequestMethod.GET)
	public ModelAndView reportDetail(@PathVariable("reportid") String reportid,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam(value = "filter", required = false) String filter, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime,
			@RequestParam(value = "searchVal", required = false) String searchVal, Model md) {

		logger.info("Getting Report Details :" + reportid);
		logger.info("Assigning Model Attributes :" + reportid);

		md.addAttribute("menu", "XBRLReports");
		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);
		md.addAttribute("fromdate", fromdate);
		md.addAttribute("todate", todate);
		md.addAttribute("filter", filter);
		md.addAttribute("currency", currency);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("reportingTime", reportingTime);
		// md.addAttribute("instancecode", Integer.parseInt(instancecode));
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));
		md.addAttribute("displaymode", "detail");

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(100);

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = regreportServices.getReportDetails(reportid, instancecode, asondate, fromdate, todate,
				currency, reportingTime, dtltype, subreportid, secid, PageRequest.of(currentPage, pageSize), filter,
				searchVal);

		return mv;

	}
/*


	@RequestMapping(value = "{reportid}/Download", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<InputStreamResource> XBRLDownload(HttpServletResponse response,
			@PathVariable("reportid") String reportid, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "reportingTime", required = false) String reportingTime,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam("filetype") String filetype, @RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "pagesize", required = false, defaultValue = "A3") String pagesize)
			throws IOException, SQLException {
		response.setContentType("application/octet-stream");

		try {
			logger.info(
					"Getting download File :" + reportid + ", FileType :" + filetype + ", SubreportId :" + subreportid);
			
			System.out.println("page size " + pagesize );
			
			HttpHeaders headers = new HttpHeaders();
			
			if ("detailexcel".equalsIgnoreCase(filetype)) {

	            List<Object[]> entityList;

	            switch (reportid) {
	                case "BRF001": entityList = brf1_DetaiRep.find(); break;
	                case "BRF002": entityList = brf2_DetaiRep.find(); break;
	                case "BRF004": entityList = brf4_DetaiRep.find(); break;
	                default: throw new RuntimeException("Unknown reportid: " + reportid);
	            }

	            byte[] excelBytes = brf_DetailExcel_Service.generateReport(entityList);

	            headers.setContentType(MediaType.parseMediaType("application/vnd.ms-excel"));
	            headers.setContentDispositionFormData("attachment", reportid + "_Detail.xlsx"); 

	            return ResponseEntity.ok()
	                    .headers(headers)
	                    .contentLength(excelBytes.length)
	                    .body(new InputStreamResource(new ByteArrayInputStream(excelBytes)));
	        }
			
			if ("detailpdf".equalsIgnoreCase(filetype)) {

			    List<Object[]> entityList;

			    switch (reportid) {
			        case "BRF001": entityList = brf1_DetaiRep.find(); break;
			        case "BRF002": entityList = brf2_DetaiRep.find(); break;
			        case "BRF004": entityList = brf4_DetaiRep.find(); break;
			        default: throw new RuntimeException("Unknown reportid: " + reportid);
			    }

			    byte[] excelBytes = brf_DetailExcel_Service.generateReport(entityList);
			    byte[] pdfBytes = brf_DetailExcel_Service.convertExcelBytesToPdf(excelBytes);

			    InputStreamResource resource =
			            new InputStreamResource(new ByteArrayInputStream(pdfBytes));

			    headers.setContentType(MediaType.APPLICATION_PDF);
			    headers.setContentDispositionFormData(
			            "attachment",
			            reportid + "_Detail.pdf"  
			    );

			    return ResponseEntity.ok()
			            .headers(headers)
			            .contentLength(pdfBytes.length)
			            .body(resource);
			}

			File repfile = regreportServices.getDownloadFile(reportid, asondate, fromdate, todate, currency,
					subreportid, secid, dtltype, reportingTime, filetype, instancecode, filter);
			System.out.println(filter + "filter");

			// **CALL COMMON AUDIT FUNCTION HERE**
			//auditService.saveCommonAudit(reportid, filetype);
			
			System.out.println("FileType: " + filetype);
			System.out.println("File: " + repfile);
			
			// Excel → PDF
	        if ("BRFEXCELTOPDF".equalsIgnoreCase(filetype)) {

	        	// File → byte[]
	            byte[] excelBytes = Files.readAllBytes(repfile.toPath());

	            
	            byte[] pdfBytes = exceltopdfservice.convertExcelBytesToPdf(excelBytes, pagesize);

	            InputStreamResource resource =
	                    new InputStreamResource(new ByteArrayInputStream(pdfBytes));

	            headers.setContentType(MediaType.APPLICATION_PDF);
	            headers.setContentDispositionFormData(
	                    "attachment",
	                    repfile.getName().replace(".xlsx", ".pdf")
	            );

	            return ResponseEntity.ok()
	                    .headers(headers)
	                    .contentLength(pdfBytes.length)
	                    .body(resource);
	        }
	        
	        
			
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", repfile.getName());

			InputStreamResource resource = new InputStreamResource(new FileInputStream(repfile));

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.contentLength(repfile.length()).body(resource);
		} catch (JRException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
*/
	@RequestMapping(value = "{reportid}/PrecheckRR", method = RequestMethod.GET)
	@ResponseBody
	public String reportPreCheckRR(@PathVariable("reportid") String reportid,

			@RequestParam(required = false) String fromdate, @RequestParam("todate") String todate)
			throws ParseException {

		logger.info("Precheck for Report :" + reportid);

		if (todate.length() == 10) {
			return regreportServices.preCheckReportRBS(reportid, fromdate, todate);
		} else {

			try {
				todate = new SimpleDateFormat("dd-MM-yyyy").format(dateFormat.parse(todate));

			} catch (ParseException e) {

				e.printStackTrace();
			}

			return regreportServices.preCheckReportRBS(reportid, fromdate, todate);
		}

	}


	// To show the required report at the first stage
	@RequestMapping(value = "{reportid}/ArchiveRR", method = RequestMethod.POST)
	public ModelAndView ArchievereportView(@PathVariable("reportid") String reportid,
			@RequestParam(value = "function", required = false) String function,
			@RequestParam("asondate") String asondate,
			@RequestParam(value = "fromdate", required = false) String fromdate,
			@RequestParam(value = "todate", required = false) String todate,
			@RequestParam(value = "currency", required = false) String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "type", required = false) String type,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md,
			HttpServletRequest req, BigDecimal srl_no) {

		System.out.println(reportid);
		System.out.println(todate);

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		if (dtltype.equals("report")) {
			md.addAttribute("menu", "XBRLReports");
			loginServices.SessionLogging("REPORTS" + reportid, "M8", req.getSession().getId(), userid,
					req.getRemoteAddr(), "ACTIVE");
		} else {
			md.addAttribute("menu", "XBRLArchives");
			loginServices.SessionLogging("ARCHREPORTS" + reportid, "M9", req.getSession().getId(), userid,
					req.getRemoteAddr(), "ACTIVE");
		}

		logger.info("Get Report :" + reportid);
		logger.info("Get Report :" + asondate);
		try {
			asondate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(asondate));
			// fromdate = dateFormat.format(new
			// SimpleDateFormat("dd/MM/yyyy").parse(fromdate));
			todate = dateFormat.format(new SimpleDateFormat("dd/MM/yyyy").parse(todate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		logger.info("Get Report :" + asondate);
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Assigning Model Attributes :" + reportid);
		// Assigning required Modal Attributes

		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);

		md.addAttribute("todate", todate);
		md.addAttribute("currency", currency);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("type", type);
		md.addAttribute("reportingTime", reportingTime);
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = new ModelAndView();

		mv = regreportServices.getArchiveReportView(reportid, asondate, fromdate, todate, currency, dtltype,
				subreportid, secid, reportingTime, PageRequest.of(currentPage, pageSize), srl_no,type);

		// System.out.println("----------------------");

		// Page<Object> sup0700RepPage = (Page<Object>)
		// mv.getModelMap().get("reportsummary");

		// sup0700RepPage.getContent().forEach((a)-> System.out.println(a.toString()));

		return mv;

	}

	/*****
	 * Archeve details
	 * 
	 * @throws ParseException
	 ******/
	@RequestMapping(value = "{reportid}/DetailsARCH", method = RequestMethod.GET)
	public ModelAndView ARCHreportDetail(@PathVariable("reportid") String reportid,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam(value = "filter", required = false) String filter, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md)
			throws ParseException {

		logger.info("Getting Report Details :" + reportid);
		logger.info("Assigning Model Attributes :" + reportid);

		md.addAttribute("menu", "XBRLReports");
		md.addAttribute("reportid", reportid);
		md.addAttribute("asondate", asondate);
		md.addAttribute("fromdate", fromdate);
		md.addAttribute("todate", todate);
		md.addAttribute("filter", filter);
		md.addAttribute("currency", currency);
		md.addAttribute("dtltype", dtltype);
		md.addAttribute("reportingTime", reportingTime);
//md.addAttribute("instancecode", Integer.parseInt(instancecode));
		md.addAttribute("reportTitle", reportServices.getReportName(reportid));
		md.addAttribute("displaymode", "detail");

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = regreportServices.ArchgetReportDetails(reportid, instancecode, asondate, fromdate, todate,
				currency, reportingTime, dtltype, subreportid, secid, PageRequest.of(currentPage, pageSize), filter);

		return mv;

	}
	
	
	@RequestMapping(value = "CustomerDetailEditBrf1", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf76(@ModelAttribute("singledetail") BRF1_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1, @RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_date") String report_date,
			@RequestParam(value = "reason", required = false) String reason) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		System.out.println("Report Date " + report_date);
		//AuditReasonDTO dto = new AuditReasonDTO();
		//dto.setReason(reason);

		return BRF001ReportService.detailChanges1(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1, report_date);
	}

	@RequestMapping(value = "Generateloginotp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String Generateloginotp(@RequestParam("Userid") String Userid) {
		String msg = "success";
		System.out.println(msg);
		return msg;
	}

}
