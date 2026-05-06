
package com.bornfire.xbrl.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.AuditReasonDTO;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;

/*import com.bornfire.xbrl.entities.BRBS.BRF60_DETAIL_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF62_DETAIL_ENTITY;*/

//import com.bornfire.xbrl.entities.BRBS.BRF60_DETAIL_ENTITY;
//import com.bornfire.xbrl.entities.BRBS.BRF60_DETAIL_ENTITY;
//import com.bornfire.xbrl.entities.BRBS.BRF94_DETAILENTITY;
import com.bornfire.xbrl.entities.BRBS.*;
//import com.bornfire.xbrl.services.BRF94ReportService;
import com.bornfire.xbrl.services.*;

import net.sf.jasperreports.engine.JRException;

@Controller
@ConfigurationProperties("default")
@RequestMapping(value = "Reports")
public class XBRLReportsController {

	private static final Logger logger = LoggerFactory.getLogger(XBRLReportsController.class);

	@Autowired
	ReportServices reportServices;

	@Autowired
	BRF210AReportService brf210AReportService;

	@Autowired
	BRF99AReportService brf99AReportService;

	@Autowired
	BRF100AReportService brf100AReportService;

	@Autowired
	BRF009ReportService brf009ReportService;

	@Autowired
	BRF105AReportService brf105AReportService;
	@Autowired
	BRF202A_entity_repo brf202A_entity_repo;
	@Autowired
	BRF45ReportService brf45ReportService;
	@Autowired
	BRF43ReportService BRF43ReportService;
	@Autowired
	BRF81ReportService BRF81ReportService;
	@Autowired
	BRF005ReportService BRF005ReportService;
	@Autowired
	BRF87ReportService brf87ReportService;

	@Autowired
	BRF82ReportService BRF82ReportService;
	@Autowired
	BRF83ReportService BRF83ReportService;

	@Autowired
	BRF37ReportService brf37ReportService;

	@Autowired
	BRF034ReportService brf034ReportService;

	@Autowired
	BRF93ReportService brf93ReportService;

//	  @Autowired BRF94ReportService BRF94ReportService;
//	 

	@Autowired
	BRF54ReportService brf054ReportService;

	@Autowired
	RegulatoryReportServices regreportServices;
	@Autowired
	BRF1_Repo BRF1_Repo;
	@Autowired
	LoginServices loginServices;

	@Autowired
	BRF204AReportService brf204AReportService;

	@Autowired
	BRF85ReportService brf85ReportService;

	@Autowired
	BRF010ReportService bRF010ReportService;
	@Autowired
	BRF011ReportService brf011ReportService;

	@Autowired

	BRF35ReportService brf35ReportService;

	@Autowired
	BRF001ReportService brf001ReportService;

	@Autowired
	BRF008ReportService brf008ReportService;

	@Autowired
	BRF73ReportService brf73ReportService;

	@Autowired
	BRF31ReportService brf031ReportService;

	@Autowired
	BRF32ReportService BRF32ReportService;

	@Autowired
	BRF033ReportService BRF033ReportService;

	@Autowired
	BRF153ReportService BRF153ReportService;

	@Autowired
	BRF74ReportService brf74ReportService;
	@Autowired
	BRF008ReportService bRF008ReportService;
	@Autowired
	BRF012ReportService brf012ReportService;
	@Autowired
	CustomerDetailService customerDetailService;
	@Autowired
	BRF56ReportService brf056ReportService;
	@Autowired
	BRF59ReportService brf059ReportService;
	@Autowired
	BRF67ReportService BRF67ReportService;

	@Autowired
	BRF62ReportService BRF062ReportService;

	@Autowired
	BRF64ReportService BRF64ReportService;

	@Autowired
	BRF155ReportService brf155ReportService;
	@Autowired
	BRF155ServiceRepo brf155ServiceRepo;
	@Autowired
	BRF036ReportService brf036ReportService;

	@Autowired
	BRF39ReportService brf039ReportService;

	@Autowired
	B36ServiceRepo b36ServiceRepo;

	@Autowired
	BRF005ReportService brf005ReportService;

	@Autowired
	B5ServiceRepo brf5_Repo;
	@Autowired
	BRF001ReportService BRF001ReportService;

	@Autowired
	BRF002ReportService BRF002ReportService;

	@Autowired
	BRF003ReportService bRF003ReportService;

	@Autowired
	BRF004ReportService BRF004ReportService;
	@Autowired
	BRF200AReportService brf200AReportService;

	@Autowired
	BRF007ReportService bRF007ReportService;

	@Autowired
	BRF013ReportService BRF013ReportService;

	@Autowired
	BRF009ReportService bRF009ReportService;

	@Autowired
	BRF034ReportService BRF034ReportService;

	@Autowired
	BRF37ReportService BRF037ReportService;
	@Autowired
	BRF60ReportService BRF060ReportService;

	@Autowired
	BRF38ReportService BRF38ReportService1;

	@Autowired
	BRF40ReportService BRF40ReportService;
//	@Autowired
//	BRF57ReportService brf57ReportService;

	@Autowired

	BRF77ReportService brf77ReportService;

	@Autowired
	BRF92ReportService brf92ReportService;

	@Autowired
	BRF79ReportService brf79ReportService;

	@Autowired
	BRF78ReportService brf78ReportService;

	@Autowired
	BRF50ReportService brf50ReportService;

	@Autowired
	BRF80ReportService BRF80reportservice;
	@Autowired
	BRF46ReportService brf46ReportService;

	@Autowired
	BRF47ReportService BRF47ReportService;

	@Autowired
	BRF106AReportService bRF106AReportService;

	@Autowired
	BRF206AReportService bRF206AReportService;

	@Autowired
	BRF66AReportService BRF066Areportservice;

	@Autowired
	BRF65ReportService brf65ReportService;

	@Autowired
	BRF151ReportService brf151ReportService;

	@Autowired
	BRF154ReportService BRF154ReportService;
	@Autowired
	BRF181AReportService bRF181AReportService;

	@Autowired
	BRF101ReportService brf101ReportService;

	@Autowired
	BRF71ReportService BRF71reportservice;

	@Autowired
	BRF78ReportService BRF78reportservice;

	@Autowired
	BRF095AReportService brf095AReportService;
	@Autowired
	BRF96AReportService brf96ReportService;

	@Autowired
	BRF69ReportService brf069ReportService;

	@Autowired
	BRF83ReportService brf83ReportService;

	@Autowired
	BRF44ReportService brf44ReportService;

	@Autowired
	BRF103AReportService brf103aReportService;

	@Autowired
	BRF100AReportService brf100aReportService;

	@Autowired
	BRF205AReportService brf205aReportService;
	@Autowired
	BRF84ReportService BRF84reportservice;

	@Autowired
	BRF107AReportService brf107AReportService;

	@Autowired
	BRF207AReportService brf207AReportService;

	@Autowired
	BRF152ReportService brf152ReportService;

	@Autowired
	BRF41ReportService brf41ReportService;

	@Autowired
	BRF014ReportService brf14ReportService;

	@Autowired
	BRF51ReportService brf51ReportService;

	@Autowired
	BRF52ReportService brf52ReportService;

	@Autowired
	BRF53ReportService brf53ReportService;

	@Autowired
	BRF54ReportService brf54ReportService;

	@Autowired
	BRF070AServiceRepo brf070AserviceRepo;

	@Autowired
	BRF71_ServiceRepo bRF71_ServiceRepo;

	@Autowired
	BRF001_FORT_SERVICE brf001_FORT_SERVICE;

	@Autowired
	BRF86ReportService brf86ReportService;

	@Autowired
	BRF76ReportService brf76ReportService;

	@Autowired
	BRF104AReportService brf104ReportService;

	@Autowired
	BRF156ReportService brf156ReportService;

	@Autowired
	BRF88ReportService brf88ReportService;

	@Autowired
	BRF68ReportService brf068ReportService;

	@Autowired

	BRF209AReportService brf209AReportService;

	@Autowired
	BRF208AReportService brf208AReportService;

	@Autowired
	BRF49ReportService brf49ReportService;

	@Autowired
	BRF109ReportService brf109ReportService;

	@Autowired
	BRF48ReportService brf48ReportService;
	@Autowired
	BRF102AReportService brf102ReportService;

	@Autowired
	BRF69_SUMMARY_A_REP brf69_SUMMARY_A_REP;

	@Autowired
	BRF069_SUMMARY_B_REP brf069_SUMMARY_B_REP;

	@Autowired
	BRF069_SUMMARY_C_REP brf069_SUMMARY_C_REP;

	@Autowired
	BRF069_SUMMARY_D_REP brf069_SUMMARY_D_REP;

	@Autowired
	BRF108_ENTITY_REPO brf108_entity_repo;

	@Autowired
	BRF301_entity_repo brf301_entity_repo;

	@Autowired
	BRF201_SUMMARY_REP_A brf201_SUMMARY_REP_A;

	@Autowired
	BRF201_SUMMARY_REP_B brf201_SUMMARY_REP_B;

	@Autowired
	BRF201_SUMMARY_REP_C brf201_SUMMARY_REP_C;

	@Autowired
	BRF201_SUMMARY_REP_D brf201_SUMMARY_REP_D;

	@Autowired
	BRF201_SUMMARY_REP_E brf201_SUMMARY_REP_E;

	@Autowired
	BRF201_SUMMARY_REP_F brf201_SUMMARY_REP_F;

	@Autowired
	BRF201_SUMMARY_REP_G brf201_SUMMARY_REP_G;

	@Autowired
	BRF7_ENTITY_REP BRF7_ENTITY_REP;

	@Autowired
	BRF80_ENTITY_REP BRF80_ENTITY_REP;

	@Autowired
	BBUSDReportService BBUSDReportService;

	@Autowired
	BBGBPReportService BBGBPReportService;

	@Autowired
	BBEURReportService BBEURReportService;

	@Autowired
	BBOTHERCURRENCYReportService BBOTHERCURRENCYReportService;

	@Autowired
	Banking_BookReportService banking_bookReportService;

	@Autowired
	MANUAL_Service_Rep mANUAL_Service_Rep;

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

		return mv;

	}

	// To check report data availability and Pending verification before

	@RequestMapping(value = "{reportid}/nilReport", method = RequestMethod.GET)
	@ResponseBody
	public String nilReport(@PathVariable("reportid") String reportid, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam(value = "reportingTime", required = false) String reportingTime, Model md,
			HttpServletRequest req) {

		logger.info("Get Report Verify Screen" + reportid);

		String userid = (String) req.getSession().getAttribute("USERID");

		Date asondate1 = null;
		Date fromdate1 = null;
		Date todate1 = null;

		try {
			asondate1 = new SimpleDateFormat("dd-MM-yyyy").parse(asondate);
			fromdate1 = new SimpleDateFormat("dd-MM-yyyy").parse(fromdate);
			todate1 = new SimpleDateFormat("dd-MM-yyyy").parse(todate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		try {
			asondate = dateFormat.format(new SimpleDateFormat("dd-MM-yyyy").parse(asondate));
			fromdate = dateFormat.format(new SimpleDateFormat("dd-MM-yyyy").parse(fromdate));
			todate = dateFormat.format(new SimpleDateFormat("dd-MM-yyyy").parse(todate));
		} catch (ParseException e) {
			e.printStackTrace();
		}

		String status = reportServices.updateReportStatus(reportid, asondate1, fromdate1, todate1, userid, "Y");
		String msg = reportServices.saveReport(reportid, asondate, fromdate, todate, "MUR");

		return status;

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
			@RequestParam(value = "reportingTime", required = false) String reportingTime,@RequestParam(value = "searchVal", required = false) String searchVal, Model md) {

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
				currency, reportingTime, dtltype, subreportid, secid, PageRequest.of(currentPage, pageSize), filter,searchVal);

		return mv;

	}

	@RequestMapping(value = "{reportid}/Details1", method = RequestMethod.GET)
	public ModelAndView reportDetail1(@PathVariable("reportid") String reportid,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam(value = "filter", required = false) String filter, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(value = "reportingTime", required = false) String reportingTime,@RequestParam(value = "searchVal", required = false) String searchVal, Model md) {

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
		md.addAttribute("displaymode", "detail1");

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("Getting ModelandView :" + reportid);
		ModelAndView mv = regreportServices.getReportDetails1(reportid, instancecode, asondate, fromdate, todate,
				currency, reportingTime, dtltype, subreportid, secid, PageRequest.of(currentPage, pageSize), filter,searchVal);

		return mv;

	}
	@Autowired
	private AuditService auditService;
	
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
	        @RequestParam("filetype") String filetype, @RequestParam(value = "filter", required = false) String filter)
	        throws IOException, SQLException {
	    response.setContentType("application/octet-stream");

	    try {
	        logger.info(
	                "Getting download File :" + reportid + ", FileType :" + filetype + ", SubreportId :" + subreportid);

	        File repfile = regreportServices.getDownloadFile(reportid, asondate, fromdate, todate, currency,
	                subreportid, secid, dtltype, reportingTime, filetype, instancecode, filter);
	        System.out.println(filter + "filter");

	        // **CALL COMMON AUDIT FUNCTION HERE**
	        auditService.saveCommonAudit(reportid, filetype);

	        HttpHeaders headers = new HttpHeaders();
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

	/*-----consolidate download for BASEL brf095-car-----created by sanjeev-*/
	@RequestMapping(value = "{reportid}/Downloadconsolidate", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<InputStreamResource> Downloadconsolidate(HttpServletResponse response,
			@PathVariable("reportid") String reportid, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "reportingTime", required = false) String reportingTime,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam("filetype") String filetype, @RequestParam(value = "filter", required = false) String filter)
			throws IOException, SQLException {

		System.out.println("entering controller");
		response.setContentType("application/octet-stream");

		try {
			logger.info(
					"Getting download File :" + reportid + ", FileType :" + filetype + ", SubreportId :" + subreportid);
			// System.out.println(asondate);
			File repfile = regreportServices.getconsolidateDownloadFile(reportid, asondate, fromdate, todate, currency,
					subreportid, secid, dtltype, reportingTime, filetype, instancecode, filter);
			System.out.println(filter + "filter");

			HttpHeaders headers = new HttpHeaders();
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

	@RequestMapping(value = "{reportid}/save", method = RequestMethod.GET)
	@ResponseBody
	public String saveReport(@PathVariable("reportid") String reportid, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam(value = "reportingTime", required = false) String reportingTime,
			@RequestParam("currency") String currency, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");

		String msg = "";
		Date asondate1 = null;
		Date fromdate1 = null;
		Date todate1 = null;
		try {

			asondate1 = new SimpleDateFormat("dd-MMM-yyyy").parse(asondate);
			fromdate1 = new SimpleDateFormat("dd-MMM-yyyy").parse(fromdate);
			todate1 = new SimpleDateFormat("dd-MMM-yyyy").parse(todate);
			logger.info("Saving Report :" + reportid);

			if (reportid.equals("FIM0500")) {
				msg = reportServices.saveFIM0500Report(reportid, asondate, fromdate, todate, currency, reportingTime);
			}

			else {
				msg = reportServices.saveReport(reportid, asondate, fromdate, todate, currency);
			}

			// To update report Status Info table on
			reportServices.updateReportStatus(reportid, asondate1, fromdate1, todate1, userid, "N");
		} catch (Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
			msg = "error occured. Please contact Administrator";
		}

		return msg;
	}

	@GetMapping("/ReportDownloadXLSX")
	public ResponseEntity<InputStreamResource> AMLDownloadExcel(HttpServletResponse response,
			@RequestParam(value = "userid", required = false) String userid,
			@RequestParam(value = "filename", required = false) String filename,
			@RequestParam(value = "ref_id", required = false) String ref_id,
			@RequestParam(value = "Param1", required = false) String input1,
			@RequestParam(value = "Param2", required = false) String input2,
			@RequestParam(value = "Param3", required = false) String input3,
			@RequestParam(value = "Param4", required = false) String input4,
			@RequestParam(value = "Param5", required = false) String input5,
			@RequestParam(value = "rptname", required = false) String reportname)
			throws IOException, SQLException, JRException, ParseException {
		response.setContentType("application/octet-stream");
		DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy");
		SimpleDateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd-MMM-yyyy");

		String toDAte = null;

		Date ConToDate = new Date();
		String strDate1 = formatter1.format(ConToDate);
		toDAte = formatter1.format(new SimpleDateFormat("dd-MMM-yyyy").parse(strDate1));

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=" + filename + "_" + toDAte + ".xlsx");

		return ResponseEntity.ok().headers(headers).body(new InputStreamResource(reportServices
				.getDownloadFileExcel(null, ref_id, input1, input2, input3, input4, input5, filename, reportname)));

	}

	@RequestMapping(value = "/ReportDownloadByScript", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String AMLDownload(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "userid", required = false) String userid,
			@RequestParam(value = "filename", required = false) String filename,
			@RequestParam(value = "ref_id", required = false) String ref_id,
			@RequestParam(value = "Param1", required = false) String input1,
			@RequestParam(value = "Param2", required = false) String input2,
			@RequestParam(value = "Param3", required = false) String input3,
			@RequestParam(value = "Param4", required = false) String input4,
			@RequestParam(value = "Param5", required = false) String input5,
			@RequestParam(value = "rptname", required = false) String reportname,
			@RequestParam(value = "username", required = false) String username) throws IOException, SQLException {
		response.setContentType("application/octet-stream");
		String currency = null;
		String msg = "";
		InputStreamResource resource = null;
		try {
			String repfile = null;
			File Master = null;
			try {
				msg = reportServices.getDownloadFileFromScript(userid, username, ref_id, input1, input2, input3, input4,
						input5, filename, reportname);
				// Master = reportServices.getDownloadFile(repfile);

			} catch (ParseException e) {

				e.printStackTrace();
			}

			// response.setHeader("Content-Disposition", "attachment; filename=" +
			// Master.getName());
			// resource = new InputStreamResource(new FileInputStream(Master));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return msg;
	}

	@RequestMapping(value = "/ReportDownloadByData", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public InputStreamResource ReportDownloadByData(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(value = "userid", required = false) String userid,
			@RequestParam(value = "filepath", required = false) String filepath,
			@RequestParam(value = "ref_id", required = false) String ref_id,
			@RequestParam(value = "username", required = false) String username,
			@RequestParam(value = "downloadondelete", required = false) String downloadondelete)
			throws IOException, SQLException {
		response.setContentType("application/octet-stream");
		String currency = null;
		InputStreamResource resource = null;
		try {
			File repfile = null;
			try {
				repfile = reportServices.getDownloadFileFromdata(userid, username, ref_id, filepath, downloadondelete);
			} catch (ParseException e) {

				e.printStackTrace();
			}

			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return resource;
	}

	/**********************************
	 * RBS
	 *******************************************************************/

	/**********************************
	 * Regulatory Reports
	 *******************************************************************/
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

	@RequestMapping(value = "customeradd", method = RequestMethod.POST)
	@ResponseBody
	public String customeradd(@ModelAttribute("singledetail") RBSTransactionMasterEntity detail, HttpServletRequest hs,

			@RequestParam(required = false) Date todate, @RequestParam(required = false) String custid) {
		String userid = (String) hs.getSession().getAttribute("USERID");
		System.out.println("add");
		System.out.println(todate);

		String MOB = (String) hs.getSession().getAttribute("MOBILNUMBER");
		String ROLE = (String) hs.getSession().getAttribute("ROLEDESC");
		String userid1 = (String) hs.getSession().getAttribute("USERID");
		String username = (String) hs.getSession().getAttribute("USERNAME");

		return customerDetailService.detailChanges(detail, 'A', userid, todate, username, MOB, ROLE, custid, userid1);

	}

	@RequestMapping(value = "customerdelete", method = RequestMethod.POST)
	@ResponseBody
	public String customerdelete(@ModelAttribute("singledetail") RBSTransactionMasterEntity detail,
			HttpServletRequest hs,

			@RequestParam(required = false) Date todate, @RequestParam(required = false) String custid) {
		String userid = (String) hs.getSession().getAttribute("USERID");
		System.out.println("delete");

		String MOB = (String) hs.getSession().getAttribute("MOBILNUMBER");
		String ROLE = (String) hs.getSession().getAttribute("ROLEDESC");
		String userid1 = (String) hs.getSession().getAttribute("USERID");
		String username = (String) hs.getSession().getAttribute("USERNAME");
		System.out.println(todate);
		return customerDetailService.detailChanges(detail, 'D', userid, todate, username, MOB, ROLE, custid, userid1);

	}

	@RequestMapping(value = "customeredit", method = RequestMethod.POST)
	@ResponseBody
	public String customeredit(@ModelAttribute("singledetail") RBSTransactionMasterEntity detail, HttpServletRequest rq,

			@RequestParam(required = false) Date todate, @RequestParam(required = false) String custid) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		System.out.println("delete");
		System.out.println(todate);
		String MOB = (String) rq.getSession().getAttribute("MOBILNUMBER");
		String ROLE = (String) rq.getSession().getAttribute("ROLEDESC");
		String userid1 = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		System.out.println("edit");

		rq.getSession().setAttribute("ROLEID", detail.getCust_id());
		System.out.println(detail.getCust_id() + "getid");
		return customerDetailService.detailChanges(detail, 'E', userid, todate, username, MOB, ROLE, custid, userid1);

	}

	@RequestMapping(value = "RBSDownload", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource RBSREPORTGENERATIONDownload(HttpServletResponse response,
			@RequestParam("reportid") String reportid, @RequestParam("asondate") String asondate,
			@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam("currency") String currency,
			@RequestParam(value = "subreportid", required = false) String subreportid,
			@RequestParam(value = "secid", required = false) String secid,
			@RequestParam(value = "dtltype", required = false) String dtltype,
			@RequestParam(value = "reportingTime", required = false) String reportingTime,
			@RequestParam(value = "instancecode", required = false) String instancecode,
			@RequestParam("filetype") String filetype) throws IOException, SQLException, ParseException {
		response.setContentType("application/octet-stream");

		InputStreamResource resource = null;
		try {
			logger.info(
					"Getting download File :" + reportid + ", FileType :" + filetype + ", SubreportId :" + subreportid);
			// System.out.println(asondate);
			// String reportids = "reportId";
			File repfile = regreportServices.getDownloadFile1(reportid, asondate, fromdate, todate, currency,
					subreportid, secid, dtltype, reportingTime, filetype, instancecode);

			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (JRException e) {
			e.printStackTrace();
		}
		return resource;
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

	@RequestMapping(value = "mappingDownload", method = RequestMethod.GET)

	public String invoiceReportDownload(HttpServletResponse response,
			@RequestParam(value = "report_name_1", required = false) String report_name_1,
			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {

		System.out.println("gggggggggggg");
		System.out.println("gggggggggggg" + report_name_1);
		response.setContentType("application/octet-stream");

		InputStreamResource resource = null;
		System.out.println(report_name_1);
		System.out.println(filetype);
		try {

			logger.info("Getting download File :" + report_name_1 + ", FileType :" + filetype + "");

			File repfile = reportServices.getFile1(report_name_1, filetype);

			if (repfile.isFile()) {
				System.out.println("file is present");
				System.out.println(repfile.getName());
				System.out.println(repfile.getAbsolutePath());
			}
			if (repfile.isDirectory()) {
				System.out.println("directory is present");
			}
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
			/*
			 * FileInputStream fileInputStream = (FileInputStream)
			 * resource.getInputStream(); if (fileInputStream != null) {
			 * fileInputStream.close(); }
			 */

		} catch (JRException e) {

			e.printStackTrace();
		}
		return "XBRLReportRefCode";
		// return resource;

	}

	/*
	 * @RequestMapping(value = "CustomerDetailEditBrf1", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf1(@ModelAttribute("singledetail") BRF1_DETAIL_ENTITY
	 * detail, HttpServletRequest hs, @RequestParam("foracid") String foracid,
	 * 
	 * @RequestParam("report_label_1") String report_label_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
	 * 
	 * @RequestParam("report_name_1") String report_name_1,
	 * 
	 * @RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
	 * System.out.println("foracid " + foracid); System.out.println("ReportLabel" +
	 * report_label_1); return BRF001ReportService.detailChanges1(detail,
	 * report_label_1, act_balance_amt_lc, foracid, report_name_1,
	 * report_addl_criteria_1);
	 * 
	 * }
	 */

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 08-JAN-2024 REPORT NAME : BRF56 PURPOSE :
	 * TESTING
	 */

	@RequestMapping(value = "CustomerDetailEditBrf56", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf56(@ModelAttribute("singledetail") BRF56_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf056ReportService.detailChanges056(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf151", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf151(@ModelAttribute("singledetail") BRF151_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf151ReportService.detailChanges151(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf10", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf10(@ModelAttribute("singledetail") BRF10_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return bRF010ReportService.detailChanges10(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf3", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf3(@ModelAttribute("singledetail") BRF3_DETAILTABLE detail, HttpServletRequest hs,
			@RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1,@RequestParam("report_date") String report_date,
			@RequestParam(value = "reason", required = false) String reason) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		AuditReasonDTO dto = new AuditReasonDTO();
		dto.setReason(reason);
		return bRF003ReportService.detailChanges3(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1,report_date,dto);
	}

	@RequestMapping(value = "CustomerDetailEditBrf8", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf8(@ModelAttribute("singledetail") BRF8_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
		return brf008ReportService.detailChanges8(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);

	}

	@RequestMapping(value = "CustomerDetailEditBrf31", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf31(@ModelAttribute("singledetail") BRF31_DETAIL_ENTITY detail, HttpServletRequest hs,
			@RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf031ReportService.detailChanges31(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf34", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf34(@ModelAttribute("singledetail") BRF34_ENTITY detail, HttpServletRequest hs,
			@RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf034ReportService.detailChanges34(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 13-feb-2024 REPORT NAME : BRF45 PURPOSE :
	 * TESTING
	 */
	@RequestMapping(value = "CustomerDetailEditBrf45", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf45(@ModelAttribute("singledetail") BRF45_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return brf45ReportService.detailChanges45(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 13-feb-2024 REPORT NAME : BRF73 PURPOSE :
	 * TESTING
	 */
	@RequestMapping(value = "CustomerDetailEditBrf73", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf73(@ModelAttribute("singledetail") BRF73_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return brf73ReportService.detailChanges73(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 13-feb-2024 REPORT NAME : BRF74 PURPOSE :
	 * TESTING
	 */
	@RequestMapping(value = "CustomerDetailEditBrf74", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf74(@ModelAttribute("singledetail") BRF74_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return brf74ReportService.detailChanges74(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 11-apr-2024 REPORT NAME : BRF62 PURPOSE :
	 * TESTING
	 */
	@RequestMapping(value = "CustomerDetailEditBrf62", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf62(@ModelAttribute("singledetail") BRF62_DETAILENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return BRF062ReportService.detailChanges62(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 13-feb-2024 REPORT NAME : BRF64 PURPOSE :
	 * TESTING
	 */
	@RequestMapping(value = "CustomerDetailEditBrf64", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf64(@ModelAttribute("singledetail") BRF64_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return BRF64ReportService.detailChanges64(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf105", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf105(@ModelAttribute("singledetail") BRF105_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return brf105AReportService.detailChanges105(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf200", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf64(@ModelAttribute("singledetail") BRF200_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return brf200AReportService.detailChanges200(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf43", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf43(@ModelAttribute("singledetail") BRF43_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return BRF43ReportService.detailChanges43(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf2", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf2(@ModelAttribute("singledetail") BRF2_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,@RequestParam("report_date") String report_date,
			@RequestParam(value = "reason", required = false) String reason) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		AuditReasonDTO dto = new AuditReasonDTO();
		dto.setReason(reason);
		return BRF002ReportService.detailChanges2(detail, report_label_1, act_balance_amt_lc, foracid, report_name_1,
				report_addl_criteria_1,report_date,dto);

	}

	@RequestMapping(value = "CustomerDetailEditBrf4", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf4(@ModelAttribute("singledetail") BRF4_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("report_date") String report_date,
			@RequestParam(value = "reason", required = false) String reason) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		AuditReasonDTO dto = new AuditReasonDTO();
		dto.setReason(reason);
		return BRF004ReportService.detailChanges4(detail, report_label_1, act_balance_amt_lc, foracid, report_name_1,
				report_addl_criteria_1,report_date,dto);
	}

	@RequestMapping(value = "CustomerDetailEditBrf209", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf209(@ModelAttribute("singledetail") BRF209_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf209AReportService.detailChanges209(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf208", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf208(@ModelAttribute("singledetail") BRF208_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf208AReportService.detailChanges208(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf101", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf101(@ModelAttribute("singledetail") BRF101_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		return brf101ReportService.detailChanges101(detail, report_label_1, act_balance_amt_lc, foracid, report_name_1,
				report_addl_criteria_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf7", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf7(@ModelAttribute("singledetail") BRF7_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		return bRF007ReportService.detailChanges7(detail, report_label_1, act_balance_amt_lc, foracid, report_name_1,
				report_addl_criteria_1);

	}

	@RequestMapping(value = "CustomerDetailEditBrf12", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf9(@ModelAttribute("singledetail") BRF12_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		return brf012ReportService.detailChanges12(detail, report_label_1, act_balance_amt_lc, foracid, report_name_1,
				report_addl_criteria_1);

	}

	@RequestMapping(value = "CustomerDetailEditBrf59", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf59(@ModelAttribute("singledetail") BRF59_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("country") String country, @RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");
		System.out.println("Acct no " + foracid);
		System.out.println("Report additional criteria 1: " + report_addl_criteria_1);
		System.out.println("Country: " + country);
		return brf059ReportService.detailChanges59(detail, foracid, report_addl_criteria_1, country, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf85", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf85(@ModelAttribute("singledetail") BRF85_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf85ReportService.detailChanges85(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

//	public String CustomerDetailEditBrf57(@ModelAttribute("singledetail") BRF57_DETAIL_ENTITY detail,  HttpServletRequest hs,
//			@RequestParam("foracid") String foracid,@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
//			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,@RequestParam("report_label_1") String report_label_1,@RequestParam("report_name_1") String report_name_1)
//			 {
//		System.out.println("edit");
//	
//		System.out.println("Acct no "+ foracid);
//		
//		return brf57ReportService.detailChanges57(detail,foracid,report_addl_criteria_1, act_balance_amt_lc,report_label_1,report_name_1);	
//	}
	@RequestMapping(value = "CustomerDetailEditBrf86", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf86(@ModelAttribute("singledetail") BRF86_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf86ReportService.detailChanges86(detail, report_label_1, act_balance_amt_lc, foracid, report_name_1,
				report_addl_criteria_1);
	}

	/*
	 * @ResponseBody public String
	 * CustomerDetailEditBrf54(@ModelAttribute("singledetail") BRF54_DETAIL_ENTITY
	 * detail, HttpServletRequest hs, @RequestParam("foracid") String foracid,
	 * 
	 * @RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
	 * 
	 * @RequestParam("acct_balance_lc") BigDecimal acct_balance_lc,
	 * 
	 * @RequestParam("report_label_1") String report_label_1,
	 * 
	 * @RequestParam("report_name_1") String report_name_1) {
	 * System.out.println("edit");
	 * 
	 * System.out.println("Acct no " + foracid);
	 * 
	 * return brf054ReportService.detailChanges54(detail, foracid,
	 * report_addl_criteria_1, acct_balance_lc, report_label_1, report_name_1); }
	 */

	@RequestMapping(value = "CustomerDetailEditBrf11", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEdit11(@ModelAttribute("singledetail") BRF11_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		return brf011ReportService.detailChanges11(detail, report_label_1, act_balance_amt_lc, foracid, report_name_1,
				report_addl_criteria_1);

	}

	@ResponseBody
	public String CustomerDetailEditBrf49(@ModelAttribute("singledetail") BRF49_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("acct_balance_lc") BigDecimal acct_balance_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf49ReportService.detailChanges49(detail, foracid, report_addl_criteria_1, acct_balance_lc,
				report_label_1, report_name_1);
	}

	@ResponseBody
	public String CustomerDetailEditBrf109(@ModelAttribute("singledetail") BRF109_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("acct_balance_lc") BigDecimal acct_balance_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf109ReportService.detailChanges109(detail, foracid, report_addl_criteria_1, acct_balance_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf13", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf13(@ModelAttribute("singledetail") BRF13_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF013ReportService.detailChanges13(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf60", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf60(@ModelAttribute("singledetail") BRF60_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF060ReportService.detailChanges60(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 30-MAR-2024 REPORT NAME : BRF32 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf32", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf32(@ModelAttribute("singledetail") BRF32B_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF32ReportService.detailChanges32(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf33", method = RequestMethod.POST)
	@ResponseBody

	public String CustomerDetailEditBrf33(@ModelAttribute("singledetail") BRF32_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("accr_balance_lc") BigDecimal accr_balance_lc,
			@RequestParam("report_lable_1") String report_lable_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF033ReportService.detailChanges33(detail, foracid, report_addl_criteria_1, accr_balance_lc,
				report_lable_1, report_name_1);
	}

	public String CustomerDetailEditBrf33(@ModelAttribute("singledetail") BRF33_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_lable_1") String report_lable_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF033ReportService.detailChanges33(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_lable_1, report_name_1);
	}

	/*
	 * @RequestMapping(value = "CustomerDetailEditBrf34", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf34(@ModelAttribute("singledetail") BRF34_DETAIL_ENTITY
	 * detail, HttpServletRequest hs,
	 * 
	 * @RequestParam("foracid") String foracid, @RequestParam("nre_flg") Character
	 * nre_flg,
	 * 
	 * @RequestParam("report_lable") String report_lable, @RequestParam("accr_bal")
	 * BigDecimal accr_bal) { System.out.println("edit"); // return
	 * BRF001ReportService.detailChanges1(detail, 'E',acct_no,nre_flg //
	 * ,report_lable,accr_bal); System.out.println("Acct no " + foracid);
	 * System.out.println("NRE_FLG" + nre_flg);
	 * 
	 * return BRF034ReportService.detailChanges34(detail, nre_flg, report_lable,
	 * accr_bal, foracid);
	 * 
	 * }
	 */

	@RequestMapping(value = "CustomerDetailEditBrf35", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf35(@ModelAttribute("singledetail") BRF35_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_name_1") String report_name_1, @RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
		System.out.println("edit");
		// return BRF001ReportService.detailChanges1(detail, 'E',acct_no,nre_flg
		// ,report_lable,accr_bal);
		System.out.println("Acct no " + foracid);
		// System.out.println("NRE_FLG" + nre_flg);
		return brf35ReportService.detailChanges35(detail, report_name_1, report_label_1, act_balance_amt_lc, foracid,
				report_addl_criteria_1);

	}

	@RequestMapping(value = "CustomerDetailEditBrf77", method = RequestMethod.POST)
	@ResponseBody

	public String CustomerDetailEditBrf77(@ModelAttribute("singledetail") BRF77_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_lable_1") String report_lable_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");
		System.out.println("Acct no " + foracid);
		return brf77ReportService.detailChanges77(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_lable_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf92", method = RequestMethod.POST)
	@ResponseBody

	public String CustomerDetailEditBrf92(@ModelAttribute("singledetail") BRF92_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");
		System.out.println("Acct no " + foracid);
		return brf92ReportService.detailChanges92(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf005", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf5(@ModelAttribute("singledetail") BRF5_Detail_Entity detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("acct_balance_amt_lc") BigDecimal acct_balance_amt_lc,
			@RequestParam("acct_balance_amt_ac") BigDecimal acct_balance_amt_ac,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("report_addl_criteria_2") String report_addl_criteria_2) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		return brf005ReportService.detailChanges5(detail, report_label_1, acct_balance_amt_lc, acct_balance_amt_ac,
				report_name_1, report_addl_criteria_1, report_addl_criteria_2, foracid);
	}

	@RequestMapping(value = "CustomerDetailEditBrf46", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf10(@ModelAttribute("singledetail") BRF46_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf46ReportService.detailChanges46(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf48", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf48(@ModelAttribute("singledetail") BRF48_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf48ReportService.detailChanges48(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf79", method = RequestMethod.POST)
	@ResponseBody

	public String CustomerDetailEditBrf79(@ModelAttribute("singledetail") BRF79_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("accr_balance_lc") BigDecimal accr_balance_lc,
			@RequestParam("report_lable_1") String report_lable_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf79ReportService.detailChanges79(detail, foracid, report_addl_criteria_1, accr_balance_lc,
				report_lable_1, report_name_1);

	}

	@RequestMapping(value = "CustomerDetailEditBrf50", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf50(@ModelAttribute("singledetail") BRF50_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf50ReportService.detailChanges50(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf80", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf80(@ModelAttribute("singledetail") BRF80_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		return BRF80reportservice.detailChanges80(detail, report_label_1, act_balance_amt_lc, foracid, report_name_1,
				report_addl_criteria_1);
	}

	/*
	 * CREATED BY : SURIYA. CREATED ON : 24-feb-2024 REPORT NAME : BRF0001 PURPOSE
	 * :Development
	 */
	@RequestMapping(value = "CustomerDetailEditBrf0001", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf0001(@ModelAttribute("singledetail") BRF0001_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1) {
		System.out.println("foracid " + foracid);
		System.out.println("ReportLabel" + report_label_1);
		return brf001_FORT_SERVICE.detailChangesbrf001(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * @RequestMapping(value = "CustomerDetailEditBrf60", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf60(@ModelAttribute("singledetail") BRF60_DETAIL_ENTITY
	 * detail, HttpServletRequest hs,
	 * 
	 * @RequestParam("foracid") String
	 * foracid,@RequestParam("report_addl_criteria_1") String
	 * report_addl_criteria_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal
	 * act_balance_amt_lc,@RequestParam("report_lable_1") String
	 * report_lable_1,@RequestParam("report_name_1") String report_name_1) {
	 * System.out.println("edit");
	 * 
	 * System.out.println("Acct no "+ foracid);
	 * 
	 * return
	 * BRF060ReportService.detailChanges60(detail,foracid,report_addl_criteria_1,
	 * act_balance_amt_lc,report_lable_1,report_name_1); }
	 */

	@RequestMapping(value = "CustomerDetailEditBrf47", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf47(@ModelAttribute("singledetail") BRF47A_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			// @RequestParam("report_lable_1") String report_lable_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF47ReportService.detailChanges47(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf84", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf84(@ModelAttribute("singledetail") BRF84_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF84reportservice.detailChanges84(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf103", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf103(@ModelAttribute("singledetail") BRF103_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf103aReportService.detailChanges103(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf65", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf65(@ModelAttribute("singledetail") BRF65_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf65ReportService.detailChanges65(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * @RequestMapping(value = "CustomerDetailEditBrf60", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf60(@ModelAttribute("singledetail") BRF60_DETAIL_ENTITY
	 * detail, HttpServletRequest hs,
	 * 
	 * @RequestParam("foracid") String
	 * foracid,@RequestParam("report_addl_criteria_1") String
	 * report_addl_criteria_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal
	 * act_balance_amt_lc,@RequestParam("report_lable_1") String
	 * report_lable_1,@RequestParam("report_name_1") String report_name_1) {
	 * System.out.println("edit");
	 * 
	 * System.out.println("Acct no "+ foracid);
	 * 
	 * return
	 * BRF060ReportService.detailChanges60(detail,foracid,report_addl_criteria_1,
	 * act_balance_amt_lc,report_lable_1,report_name_1); }
	 */

	/*
	 * @RequestMapping(value = "CustomerDetailEditBrf60", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf60(@ModelAttribute("singledetail") BRF60_DETAIL_ENTITY
	 * detail, HttpServletRequest hs,
	 * 
	 * @RequestParam("foracid") String
	 * foracid,@RequestParam("report_addl_criteria_1") String
	 * report_addl_criteria_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal
	 * act_balance_amt_lc,@RequestParam("report_lable_1") String
	 * report_lable_1,@RequestParam("report_name_1") String report_name_1) {
	 * System.out.println("edit");
	 * 
	 * System.out.println("Acct no "+ foracid);
	 * 
	 * return
	 * BRF060ReportService.detailChanges60(detail,foracid,report_addl_criteria_1,
	 * act_balance_amt_lc,report_lable_1,report_name_1); }
	 */

	/*
	 * @RequestMapping(value = "CustomerDetailEditBrf60", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf60(@ModelAttribute("singledetail") BRF60_DETAIL_ENTITY
	 * detail, HttpServletRequest hs,
	 * 
	 * @RequestParam("foracid") String
	 * foracid,@RequestParam("report_addl_criteria_1") String
	 * report_addl_criteria_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal
	 * act_balance_amt_lc,@RequestParam("report_lable_1") String
	 * report_lable_1,@RequestParam("report_name_1") String report_name_1) {
	 * System.out.println("edit");
	 * 
	 * System.out.println("Acct no "+ foracid);
	 * 
	 * return
	 * BRF060ReportService.detailChanges60(detail,foracid,report_addl_criteria_1,
	 * act_balance_amt_lc,report_lable_1,report_name_1); }
	 */

	@RequestMapping(value = "CustomerDetailEditBrf66", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf66(@ModelAttribute("singledetail") BRF66_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF066Areportservice.detailChanges66(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 08-JAN-2024 REPORT NAME : BRF67 PURPOSE :
	 * TESTING
	 */
	@RequestMapping(value = "CustomerDetailEditBrf67", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf67(@ModelAttribute("singledetail") BRF67_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return BRF67ReportService.detailChanges67(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf38", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf38(@ModelAttribute("singledetail") BRF38_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,

			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,

			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,

			@RequestParam("report_label_1") String report_label_1,

			@RequestParam("report_name_1") String report_name_1,

			@RequestParam("report_addl_criteria_2") String report_addl_criteria_2,

			@RequestParam("purpose_of_rem") String purpose_of_rem) {
		System.out.println("edit");
		System.out.println("Acct no " + foracid);
		System.out.println("Acct no " + report_addl_criteria_2);
		System.out.println("Acct no " + purpose_of_rem);
		return BRF38ReportService1.detailChanges38(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1, report_addl_criteria_2, purpose_of_rem);
	}

	@RequestMapping(value = "CustomerDetailEditBrf153", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf153(@ModelAttribute("singledetail") BRF153_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("amt_ac") BigDecimal amt_ac, @RequestParam("report_lable_1") String report_lable_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF153ReportService.detailChanges153(detail, foracid, report_addl_criteria_1, act_balance_amt_lc, amt_ac,
				report_lable_1, report_name_1);

	}

	@RequestMapping(value = "CustomerDetailEditBrf154", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf154(@ModelAttribute("singledetail") BRF154_DETAIL_ENTITY detail,
			HttpServletRequest hs,

			@RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,

			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1, @RequestParam("report_name_1") String report_name_1)

	{
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF154ReportService.detailChanges154(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf71", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf71(@ModelAttribute("singledetail") BRF71_DETAIL_ENTITY detail,
			HttpServletRequest hs,

			@RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,

			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1, @RequestParam("report_name_1") String report_name_1)

	{
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF71reportservice.detailChanges71(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);

	}

//	@RequestMapping(value = "CustomerDetailEditBrf155", method = RequestMethod.POST)
//	@ResponseBody
//	public String CustomerDetailEditBrf155(@ModelAttribute("singledetail") BRF155_DETAIL_ENTITY detail,
//			HttpServletRequest hs, @RequestParam("maturity_date") Date maturity_date,
//			@RequestParam("buy_usd") BigDecimal buy_usd,
//			@RequestParam("sell_aed") BigDecimal sell_aed,
//			@RequestParam("buy_aed") BigDecimal buy_aed,
//			@RequestParam("sell_usd") BigDecimal sell_usd,
//			@RequestParam("country") String country) {
//		return brf155ReportService.detailChanges155(detail, maturity_date, buy_usd, sell_aed,
//				buy_aed, sell_usd,country);
//	}

	@RequestMapping(value = "CustomerDetailEditBrf78", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf78(@ModelAttribute("singledetail") BRF78_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		return brf78ReportService.detailChanges78(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf95", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf95(@ModelAttribute("singledetail") BRF95_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf095AReportService.detailChanges95(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf204", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf204(@ModelAttribute("singledetail") BRF204_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf204AReportService.detailChanges204(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);

	}

//	@RequestMapping(value = "CustomerDetailEditBrf107", method = RequestMethod.POST)
//	@ResponseBody
//	public String CustomerDetailEditBrf107(@ModelAttribute("singledetail") BRF107_DETAIL_ENTITY detail,
//			HttpServletRequest hs, @RequestParam("foracid") String foracid,
//			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
//			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
//			@RequestParam("report_label_1") String report_label_1,
//			@RequestParam("report_name_1") String report_name_1) {
//		System.out.println("edit");
//
//		System.out.println("Acct no " + foracid);
//
//		return brf107AReportService.detailChanges107(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
//				report_label_1, report_name_1);
//	}

	@RequestMapping(value = "CustomerDetailEditBrf207", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf207(@ModelAttribute("singledetail") BRF207_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf207AReportService.detailChanges207(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}
	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 20-feb-2024 REPORT NAME : BRF54 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf54", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf54(@ModelAttribute("singledetail") BRF54_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf054ReportService.detailChanges54(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}
	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 17-feb-2024 REPORT NAME : BRF76 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf76", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf76(@ModelAttribute("singledetail") BRF76_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf76ReportService.detailChanges76(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf1", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf76(@ModelAttribute("singledetail") BRF1_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1,@RequestParam("report_date") String report_date,
			@RequestParam(value = "reason", required = false) String reason) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);
		
		System.out.println("Report Date " + report_date);
		AuditReasonDTO dto = new AuditReasonDTO();
		dto.setReason(reason);

		return BRF001ReportService.detailChanges1(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1,report_date,dto);
	}
	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 17-feb-2024 REPORT NAME : BRF104 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf104", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf104(@ModelAttribute("singledetail") BRF104_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf104ReportService.detailChanges104(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}
	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 17-feb-2024 REPORT NAME : BRF40 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf40", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf40(@ModelAttribute("singledetail") BRF40_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF40ReportService.detailChanges40(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 21-MAR-2024 REPORT NAME : BRF44 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf44", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf44(@ModelAttribute("singledetail") BRF44_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf44ReportService.detailChanges44(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}
	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 17-feb-2024 REPORT NAME : BRF156 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf156", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf156(@ModelAttribute("singledetail") BRF156_DETAILENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf156ReportService.detailChanges156(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 09-MAR-2024 REPORT NAME : BRF102 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf102", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf102(@ModelAttribute("singledetail") BRF102_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf102ReportService.detailChanges102(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf88", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf88(@ModelAttribute("singledetail") BRF88_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf88ReportService.detailChanges88(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf68", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf68(@ModelAttribute("singledetail") BRF68_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf068ReportService.detailChanges68(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}
	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 16-MAR-2024 REPORT NAME : BRF39 PURPOSE
	 * :Development
	 */

	@RequestMapping(value = "CustomerDetailEditBrf39", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf39(@ModelAttribute("singledetail") BRF39_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf039ReportService.detailChanges39(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf81", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf81(@ModelAttribute("singledetail") BRF81_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,

			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,

			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,

			@RequestParam("report_label_1") String report_label_1,

			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF81ReportService.detailChanges81(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf82", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf82(@ModelAttribute("singledetail") BRF82_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,

			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,

			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,

			@RequestParam("report_label_1") String report_label_1,

			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF82ReportService.detailChanges82(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf83", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf83(@ModelAttribute("singledetail") BRF83_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return BRF83ReportService.detailChanges83(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}
	/*
	 * /*
	 * 
	 * @RequestMapping(value = "CustomerDetailEditBrf67", method =
	 * RequestMethod.POST)
	 * 
	 * @ResponseBody public String
	 * CustomerDetailEditBrf67(@ModelAttribute("singledetail") BRF67_DETAILENTITY
	 * detail,
	 * 
	 * HttpServletRequest hs, @RequestParam("foracid") String foracid,
	 * 
	 * @RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
	 * 
	 * @RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
	 * 
	 * @RequestParam("report_label_1") String report_label_1,
	 * 
	 * @RequestParam("report_name_1") String report_name_1) {
	 * System.out.println("edit");
	 * 
	 * System.out.println("Acct no " + foracid);
	 * 
	 * return brf96ReportService.detailChanges96(detail, foracid,
	 * report_addl_criteria_1, act_balance_amt_lc, report_label_1, report_name_1); }
	 */

	@RequestMapping(value = "CustomerDetailEditBrf69", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf69(@ModelAttribute("singledetail") BRF69_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf069ReportService.detailChanges69(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf106", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf106(@ModelAttribute("singledetail") BRF106_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return bRF106AReportService.detailChanges106(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf206", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf206(@ModelAttribute("singledetail") BRF206_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return bRF206AReportService.detailChanges206(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf181", method = RequestMethod.POST)

	@ResponseBody
	public String CustomerDetailEditBrf181(@ModelAttribute("singledetail") BRF181_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return bRF181AReportService.detailChanges181(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/*
	 * CREATED BY : GOWTHAM. CREATED ON : 08-JAN-2024 REPORT NAME : BRF67 PURPOSE :
	 * TESTING
	 */
	@RequestMapping(value = "CustomerDetailEditBrf152", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf152(@ModelAttribute("singledetail") BRF152_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("amt_ac") BigDecimal amt_ac, @RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf152ReportService.detailChanges152(detail, foracid, report_addl_criteria_1, act_balance_amt_lc, amt_ac,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf41", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf41(@ModelAttribute("singledetail") BRF41_DETAILENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");
		System.out.println("Acct no " + foracid);

		return brf41ReportService.detailChanges41(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}
	/*
	 * CREATED BY : KAMATCHI CREATED ON : 03-02-2024 REPORT NAME : BRF93 PURPOSE :
	 * DEVELOPMENT
	 */

	@RequestMapping(value = "CustomerDetailEditBrf93", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf93(@ModelAttribute("singledetail") BRF93_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1, @RequestParam("report_name_1") String report_name_1,
			@RequestParam("report_addl_criteria_2") String report_addl_criteria_2,
			@RequestParam("purpose_of_rem") String purpose_of_rem) {
		System.out.println("edit");
		System.out.println("Acct no " + foracid);
		System.out.println("Acct no " + report_addl_criteria_2);
		System.out.println("Acct no " + purpose_of_rem);
		return brf93ReportService.detailChanges93(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1, report_addl_criteria_2, purpose_of_rem);
	}

//	@RequestMapping(value = "CustomerDetailEditBrf94", method = RequestMethod.POST)
//
//	@ResponseBody
//	public String CustomerDetailEditBrf94(@ModelAttribute("singledetail") BRF94_DETAILENTITY detail,
//			HttpServletRequest hs, @RequestParam("foracid") String foracid,
//
//			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
//
//			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
//
//			@RequestParam("report_label_1") String report_label_1,
//
//			@RequestParam("report_name_1") String report_name_1,
//
//			@RequestParam("report_addl_criteria_2") String report_addl_criteria_2,
//
//			@RequestParam("purpose_of_rem") String purpose_of_rem) {
//		System.out.println("edit");
//		System.out.println("Acct no " + foracid);
//		System.out.println("Acct no " + report_addl_criteria_2);
//		System.out.println("Acct no " + purpose_of_rem);
//		return BRF94ReportService.detailChanges94(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
//				report_label_1, report_name_1, report_addl_criteria_2, purpose_of_rem);
//	}

	/*
	 * CREATED BY : DHANALAKSHMI CREATED ON : 15-02-2024 REPORT NAME : BRF14 PURPOSE
	 * : DEVELOPMENT
	 */

	@RequestMapping(value = "CustomerDetailEditBrf14", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf14(@ModelAttribute("singledetail") BRF14_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf14ReportService.detailChanges14(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf205", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf205(@ModelAttribute("singledetail") BRF205_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf205aReportService.detailChanges205(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf210", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf210(@ModelAttribute("singledetail") BRF210_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf210AReportService.detailChanges210(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf100", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf100(@ModelAttribute("singledetail") BRF100_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf100AReportService.detailChanges100(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf99", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf99(@ModelAttribute("singledetail") BRF99_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf99AReportService.detailChanges99(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf9", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf9(@ModelAttribute("singledetail") BRF9_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf009ReportService.detailChanges9(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf87", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf87(@ModelAttribute("singledetail") BRF87_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf87ReportService.detailChanges87(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf37", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf37(@ModelAttribute("singledetail") BRF37_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf37ReportService.detailChanges37(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf51", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf51(@ModelAttribute("singledetail") BRF51_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf51ReportService.detailChanges51(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf52", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf52(@ModelAttribute("singledetail") BRF52_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf52ReportService.detailChanges52(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "CustomerDetailEditBrf53", method = RequestMethod.POST)
	@ResponseBody
	public String CustomerDetailEditBrf53(@ModelAttribute("singledetail") BRF53_DETAIL_ENTITY detail,
			HttpServletRequest hs, @RequestParam("foracid") String foracid,
			@RequestParam("report_addl_criteria_1") String report_addl_criteria_1,
			@RequestParam("act_balance_amt_lc") BigDecimal act_balance_amt_lc,
			@RequestParam("report_label_1") String report_label_1,
			@RequestParam("report_name_1") String report_name_1) {
		System.out.println("edit");

		System.out.println("Acct no " + foracid);

		return brf53ReportService.detailChanges53(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	@RequestMapping(value = "modifyRecord", method = RequestMethod.POST)
	@ResponseBody
	public String modifyRecord(@RequestParam(required = false) Date asondate,
			@ModelAttribute BRF300_ENTITY brf300_Entity, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.modifyRecord(asondate, brf300_Entity, userId, req);
	}

	@PostMapping("modifyRecord201")
	@ResponseBody
	public String modifyRecord201(HttpServletRequest req, @RequestBody YourFormDatas formData,
			@RequestParam(required = false) Date asondate) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.modifyRecord201A(formData, asondate, userId, req);
	}

	@RequestMapping(value = "modifyRecord05", method = { RequestMethod.POST })
	@ResponseBody
	public String Modifybrf5(@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam(value = "asondate", required = false) String asondate,
			@RequestParam(value = "security_identifier", required = false) String security_identifier,
			@RequestParam(value = "rating", required = false) String rating,
			@RequestParam(value = "rating_type", required = false) String rating_type, Model md, HttpServletRequest req)
			throws IOException, ParseException {

		/*
		 * SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy"); DateFormat
		 * format2 = new SimpleDateFormat("dd-MMM-yyyy"); Date date =
		 * format1.parse(todate);
		 */
		try {
			brf005ReportService.editbrf5b(security_identifier, rating, rating_type, asondate);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return "";
	}

	@PostMapping("modifyRecord07")
	@ResponseBody
	public String Modifybrf7(@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam(value = "asondate", required = false) String userID,
			@RequestParam(value = "ROW103", required = false) String ROW103,
			@RequestParam(value = "ROW104", required = false) String ROW104, Model md, HttpServletRequest req)
			throws IOException, ParseException {

		List<BRF7_ENTITY> BRF7_ENTITY1 = BRF7_ENTITY_REP.findAllData(todate);

		if (BRF7_ENTITY1.size() == 1) {

			for (BRF7_ENTITY BRF7_ENT : BRF7_ENTITY1) {

				ROW103 = (ROW103 == null) ? "0" : ROW103;
				ROW104 = (ROW104 == null) ? "0" : ROW104;

				ROW103 = (ROW103 == "") ? "0" : ROW103;
				ROW104 = (ROW104 == "") ? "0" : ROW104;

				BigDecimal R3_amount = new BigDecimal(ROW103);
				R3_amount = R3_amount.setScale(0, BigDecimal.ROUND_UP);
				BigDecimal R4_amount = new BigDecimal(ROW104);
				R4_amount = R4_amount.setScale(0, BigDecimal.ROUND_UP);

				BRF7_ENT.setR3_amount(R3_amount);
				BRF7_ENT.setR4_amount(R4_amount);

				BigDecimal R6_amt = new BigDecimal(0);

				BigDecimal R1_amt = BRF7_ENT.getR1_amount();

				BigDecimal R2_amt = BRF7_ENT.getR2_amount();

				BigDecimal R5_amt = BRF7_ENT.getR5_amount();

				BigDecimal R6_amt1 = R6_amt.add(R3_amount).add(R1_amt).add(R2_amt);

				R6_amt1 = R6_amt1.subtract(R4_amount);

				R6_amt1 = R6_amt1.add(R5_amt);

				System.out.println(R6_amt1);
				BRF7_ENT.setR6_amount(R6_amt1);

				BigDecimal R26_amt = R6_amt1;

				if (R26_amt.compareTo(BigDecimal.ZERO) == 0) {

				} else {

					// Calculate the percentage
					BigDecimal percentage = R6_amt1.divide(R26_amt, 2, BigDecimal.ROUND_HALF_UP)
							.multiply(new BigDecimal("100"));
					BRF7_ENT.setR27_amount(percentage);

					System.out.println(percentage);
				}

				BRF7_ENTITY_REP.save(BRF7_ENT);

			}

		}

		return todate;

	}

	/*
	 * @PostMapping("modifyRecord80")
	 * 
	 * @ResponseBody public String Modifybrf80( @RequestParam("fromdate") String
	 * fromdate,
	 * 
	 * @RequestParam("todate") String todate,
	 * 
	 * @RequestParam(value = "asondate", required = false) String userID,
	 * 
	 * @RequestParam(value = "ROW104", required = false) String ROW104,
	 * 
	 * @RequestParam(value = "ROW105", required = false) String ROW105,
	 * 
	 * @RequestParam(value = "ROW106", required = false) String ROW106,
	 * 
	 * @RequestParam(value = "ROW107", required = false) String ROW107,
	 * 
	 * @RequestParam(value = "ROW108", required = false) String ROW108,
	 * 
	 * @RequestParam(value = "ROW109", required = false) String ROW109,
	 * 
	 * @RequestParam(value = "ROW110", required = false) String ROW110,
	 * 
	 * @RequestParam(value = "ROW111", required = false) String ROW111,
	 * 
	 * @RequestParam(value = "ROW112", required = false) String ROW112,
	 * 
	 * @RequestParam(value = "ROW113", required = false) String ROW113, Model md,
	 * HttpServletRequest req) throws IOException, ParseException {
	 * 
	 * List<BRF80_ENTITY> BRF80_ENTITY1 = BRF80_ENTITY_REP.findAllData(todate);
	 * 
	 * if(BRF80_ENTITY1.size() == 1 ) {
	 * 
	 * for (BRF80_ENTITY BRF80_ENT :BRF80_ENTITY1 ) {
	 * 
	 * ROW104 = (ROW104 == null )? "0":ROW104; ROW105 = (ROW105 == null )?
	 * "0":ROW105; ROW106 = (ROW106 == null )? "0":ROW106; ROW107 = (ROW107 == null
	 * )? "0":ROW107; ROW108 = (ROW108 == null )? "0":ROW108; ROW109 = (ROW109 ==
	 * null )? "0":ROW109; ROW110 = (ROW110 == null )? "0":ROW110; ROW111 = (ROW111
	 * == null )? "0":ROW111; ROW112= (ROW112== null )? "0":ROW112; ROW113 = (ROW113
	 * == null )? "0":ROW113;
	 * 
	 * ROW104 = (ROW104 == "" )? "0":ROW104; ROW105 = (ROW105 == "" )? "0":ROW105;
	 * ROW106 = (ROW106 == "" )? "0":ROW106; ROW107 = (ROW107 == "" )? "0":ROW107;
	 * ROW108 = (ROW108 == "" )? "0":ROW108; ROW109 = (ROW109 == "" )? "0":ROW109;
	 * ROW110 = (ROW110 == "" )? "0":ROW110; ROW111 = (ROW111 == "" )? "0":ROW111;
	 * ROW112 = (ROW112 == "" )? "0":ROW112; ROW113 = (ROW113 == "" )? "0":ROW113;
	 * 
	 * BigDecimal R1_unused_unfunded_ccf = new BigDecimal(ROW107);
	 * R1_unused_unfunded_ccf = R1_unused_unfunded_ccf.setScale(0,
	 * BigDecimal.ROUND_UP); BigDecimal R2_unused_unfunded_ccf = new
	 * BigDecimal(ROW112); R2_unused_unfunded_ccf =
	 * R2_unused_unfunded_ccf.setScale(0, BigDecimal.ROUND_UP);
	 * 
	 * BRF80_ENT.setR1_credit_rating(ROW104); BRF80_ENT.setR1_credit_type(ROW105);
	 * BRF80_ENT.setR1_economic_sec(ROW106);
	 * BRF80_ENT.setR1_unused_unfunded_ccf(R1_unused_unfunded_ccf);
	 * BRF80_ENT.setR1_remarks(ROW108); BRF80_ENT.setR2_credit_rating(ROW109);
	 * BRF80_ENT.setR2_credit_type(ROW110); BRF80_ENT.setR2_economic_sec(ROW111);
	 * BRF80_ENT.setR2_unused_unfunded_ccf(R2_unused_unfunded_ccf);
	 * BRF80_ENT.setR2_remarks(ROW113);
	 * 
	 * BigDecimal R6_amt = new BigDecimal(0) ;
	 * 
	 * BigDecimal R1_amt = BRF7_ENT.getR1_amount();
	 * 
	 * BigDecimal R2_amt = BRF7_ENT.getR2_amount();
	 * 
	 * BigDecimal R5_amt = BRF7_ENT.getR5_amount();
	 * 
	 * BigDecimal R6_amt1 = R6_amt.add(R3_amount).add(R1_amt).add(R2_amt);
	 * 
	 * R6_amt1 = R6_amt1.subtract(R4_amount);
	 * 
	 * R6_amt1 = R6_amt1.add(R5_amt);
	 * 
	 * System.out.println(R6_amt1); BRF7_ENT.setR6_amount(R6_amt1);
	 * 
	 * BigDecimal R26_amt = R6_amt1;
	 * 
	 * if (R26_amt.compareTo(BigDecimal.ZERO) == 0) {
	 * 
	 * } else {
	 * 
	 * // Calculate the percentage BigDecimal percentage = R6_amt1.divide(R26_amt,
	 * 2, BigDecimal.ROUND_HALF_UP).multiply(new BigDecimal("100"));
	 * BRF7_ENT.setR27_amount(percentage);
	 * 
	 * System.out.println(percentage); }
	 * 
	 * BRF80_ENTITY_REP.save(BRF80_ENT);
	 * 
	 * 
	 * }
	 * 
	 * }
	 * 
	 * 
	 * return todate;
	 * 
	 * }
	 */

	@RequestMapping(value = "uploadscreen", method = RequestMethod.POST)
	@ResponseBody
	public String uploadscreen(@RequestParam(required = false) Date asondate,
			@ModelAttribute BRF70_ENTITY brf70_REPORTENTITY, HttpServletRequest req) {
		String msg = "";

		// Lists for change tracking
		List<String> oldValuesList = new ArrayList<>();
		List<String> newValuesList = new ArrayList<>();
		List<String> fieldNames = new ArrayList<>();

		// StringBuilders to capture detailed changes
		StringBuilder oldChange = new StringBuilder();
		StringBuilder newChange = new StringBuilder();
		boolean rowEdited = false; // Flag to track if the row has any changes

		BRF70_ENTITY existingReport = brf070AserviceRepo.getBRF070AReport(asondate);

		if (brf70_REPORTENTITY.getR1_COL_1().compareTo(existingReport.getR1_COL_1()) != 0) {
			fieldNames.add("UAE Nationals (Nos) - Male");
			oldChange.append("R1_COL_1: ").append(existingReport.getR1_COL_1()).append("; ");
			newChange.append("R1_COL_1: ").append(brf70_REPORTENTITY.getR1_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_2().compareTo(existingReport.getR1_COL_2()) != 0) {
			fieldNames.add("UAE Nationals (Nos) - Female");
			oldChange.append("R1_COL_2: ").append(existingReport.getR1_COL_2()).append("; ");
			newChange.append("R1_COL_2: ").append(brf70_REPORTENTITY.getR1_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_3().compareTo(existingReport.getR1_COL_3()) != 0) {
			fieldNames.add("Other Nationalities (Nos) - Male");
			oldChange.append("R1_COL_3: ").append(existingReport.getR1_COL_3()).append("; ");
			newChange.append("R1_COL_3: ").append(brf70_REPORTENTITY.getR1_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_4().compareTo(existingReport.getR1_COL_4()) != 0) {
			fieldNames.add("Other Nationalities (Nos) - Female ");
			oldChange.append("R1_COL_4: ").append(existingReport.getR1_COL_4()).append("; ");
			newChange.append("R1_COL_4: ").append(brf70_REPORTENTITY.getR1_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_5().compareTo(existingReport.getR1_COL_5()) != 0) {
			fieldNames.add("Total - Male");
			oldChange.append("R1_COL_5: ").append(existingReport.getR1_COL_5()).append("; ");
			newChange.append("R1_COL_5: ").append(brf70_REPORTENTITY.getR1_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_6().compareTo(existingReport.getR1_COL_6()) != 0) {
			fieldNames.add("Total - Female");
			oldChange.append("R1_COL_6: ").append(existingReport.getR1_COL_6()).append("; ");
			newChange.append("R1_COL_6: ").append(brf70_REPORTENTITY.getR1_COL_6()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_7().compareTo(existingReport.getR1_COL_7()) != 0) {
			fieldNames.add("Position of UAE Nationals last year, ending 31 December……… - Male");
			oldChange.append("R1_COL_7: ").append(existingReport.getR1_COL_7()).append("; ");
			newChange.append("R1_COL_7: ").append(brf70_REPORTENTITY.getR1_COL_7()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_8().compareTo(existingReport.getR1_COL_8()) != 0) {
			fieldNames.add("Position of UAE Nationals last year, ending 31 December……… - Female");
			oldChange.append("R1_COL_8: ").append(existingReport.getR1_COL_8()).append("; ");
			newChange.append("R1_COL_8: ").append(brf70_REPORTENTITY.getR1_COL_8()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_9().compareTo(existingReport.getR1_COL_9()) != 0) {
			fieldNames.add("% of UAE Nationals to Total");
			oldChange.append("R1_COL_9: ").append(existingReport.getR1_COL_9()).append("; ");
			newChange.append("R1_COL_9: ").append(brf70_REPORTENTITY.getR1_COL_9()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_10().compareTo(existingReport.getR1_COL_10()) != 0) {
			fieldNames.add("% increase in the number of UAE Nationals since 31 December last year");
			oldChange.append("R1_COL_10: ").append(existingReport.getR1_COL_10()).append("; ");
			newChange.append("R1_COL_10: ").append(brf70_REPORTENTITY.getR1_COL_10()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_11().compareTo(existingReport.getR1_COL_11()) != 0) {
			fieldNames.add("No of UAE Nationals resigned/left during the year - Male");
			oldChange.append("R1_COL_11: ").append(existingReport.getR1_COL_11()).append("; ");
			newChange.append("R1_COL_11: ").append(brf70_REPORTENTITY.getR1_COL_11()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_12().compareTo(existingReport.getR1_COL_12()) != 0) {
			fieldNames.add("No of UAE Nationals resigned/left during the year - Female");
			oldChange.append("R1_COL_12: ").append(existingReport.getR1_COL_12()).append("; ");
			newChange.append("R1_COL_12: ").append(brf70_REPORTENTITY.getR1_COL_12()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_13().compareTo(existingReport.getR1_COL_13()) != 0) {
			fieldNames.add("No of UAE Nationals joined during the year - Male");
			oldChange.append("R1_COL_13: ").append(existingReport.getR1_COL_13()).append("; ");
			newChange.append("R1_COL_13: ").append(brf70_REPORTENTITY.getR1_COL_13()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR1_COL_14().compareTo(existingReport.getR1_COL_14()) != 0) {
			fieldNames.add("No of UAE Nationals joined during the year - Female");
			oldChange.append("R1_COL_14: ").append(existingReport.getR1_COL_14()).append("; ");
			newChange.append("R1_COL_14: ").append(brf70_REPORTENTITY.getR1_COL_14()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR2_COL_1().compareTo(existingReport.getR2_COL_1()) != 0) {
			fieldNames.add("UAE Nationals (Nos) - Male");
			oldChange.append("R2_COL_1: ").append(existingReport.getR2_COL_1()).append("; ");
			newChange.append("R2_COL_1: ").append(brf70_REPORTENTITY.getR2_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_2().compareTo(existingReport.getR2_COL_2()) != 0) {
			fieldNames.add("UAE Nationals (Nos) - Female");
			oldChange.append("R2_COL_2: ").append(existingReport.getR2_COL_2()).append("; ");
			newChange.append("R2_COL_2: ").append(brf70_REPORTENTITY.getR2_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_3().compareTo(existingReport.getR2_COL_3()) != 0) {
			fieldNames.add("Other Nationalities (Nos) - Male");
			oldChange.append("R2_COL_3: ").append(existingReport.getR2_COL_3()).append("; ");
			newChange.append("R2_COL_3: ").append(brf70_REPORTENTITY.getR2_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_4().compareTo(existingReport.getR2_COL_4()) != 0) {
			fieldNames.add("Other Nationalities (Nos) - Female ");
			oldChange.append("R2_COL_4: ").append(existingReport.getR2_COL_4()).append("; ");
			newChange.append("R2_COL_4: ").append(brf70_REPORTENTITY.getR2_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_5().compareTo(existingReport.getR2_COL_5()) != 0) {
			fieldNames.add("Total - Male");
			oldChange.append("R2_COL_5: ").append(existingReport.getR2_COL_5()).append("; ");
			newChange.append("R2_COL_5: ").append(brf70_REPORTENTITY.getR2_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_6().compareTo(existingReport.getR2_COL_6()) != 0) {
			fieldNames.add("Total - Female");
			oldChange.append("R2_COL_6: ").append(existingReport.getR2_COL_6()).append("; ");
			newChange.append("R2_COL_6: ").append(brf70_REPORTENTITY.getR2_COL_6()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_7().compareTo(existingReport.getR2_COL_7()) != 0) {
			fieldNames.add("Position of UAE Nationals last year, ending 31 December……… - Male");
			oldChange.append("R2_COL_7: ").append(existingReport.getR2_COL_7()).append("; ");
			newChange.append("R2_COL_7: ").append(brf70_REPORTENTITY.getR2_COL_7()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_8().compareTo(existingReport.getR2_COL_8()) != 0) {
			fieldNames.add("Position of UAE Nationals last year, ending 31 December……… - Female");
			oldChange.append("R2_COL_8: ").append(existingReport.getR2_COL_8()).append("; ");
			newChange.append("R2_COL_8: ").append(brf70_REPORTENTITY.getR2_COL_8()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_9().compareTo(existingReport.getR2_COL_9()) != 0) {
			fieldNames.add("% of UAE Nationals to Total");
			oldChange.append("R2_COL_9: ").append(existingReport.getR2_COL_9()).append("; ");
			newChange.append("R2_COL_9: ").append(brf70_REPORTENTITY.getR2_COL_9()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_10().compareTo(existingReport.getR2_COL_10()) != 0) {
			fieldNames.add("% increase in the number of UAE Nationals since 31 December last year");
			oldChange.append("R2_COL_10: ").append(existingReport.getR2_COL_10()).append("; ");
			newChange.append("R2_COL_10: ").append(brf70_REPORTENTITY.getR2_COL_10()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_11().compareTo(existingReport.getR2_COL_11()) != 0) {
			fieldNames.add("No of UAE Nationals resigned/left during the year - Male");
			oldChange.append("R2_COL_11: ").append(existingReport.getR2_COL_11()).append("; ");
			newChange.append("R2_COL_11: ").append(brf70_REPORTENTITY.getR2_COL_11()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_12().compareTo(existingReport.getR2_COL_12()) != 0) {
			fieldNames.add("No of UAE Nationals resigned/left during the year - Female");
			oldChange.append("R2_COL_12: ").append(existingReport.getR2_COL_12()).append("; ");
			newChange.append("R2_COL_12: ").append(brf70_REPORTENTITY.getR2_COL_12()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_13().compareTo(existingReport.getR2_COL_13()) != 0) {
			fieldNames.add("No of UAE Nationals joined during the year - Male");
			oldChange.append("R2_COL_13: ").append(existingReport.getR2_COL_13()).append("; ");
			newChange.append("R2_COL_13: ").append(brf70_REPORTENTITY.getR2_COL_13()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR2_COL_14().compareTo(existingReport.getR2_COL_14()) != 0) {
			fieldNames.add("No of UAE Nationals joined during the year - Female");
			oldChange.append("R2_COL_14: ").append(existingReport.getR2_COL_14()).append("; ");
			newChange.append("R2_COL_14: ").append(brf70_REPORTENTITY.getR2_COL_14()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR3_COL_1().compareTo(existingReport.getR3_COL_1()) != 0) {
			fieldNames.add("UAE Nationals (Nos) - Male");
			oldChange.append("R3_COL_1: ").append(existingReport.getR3_COL_1()).append("; ");
			newChange.append("R3_COL_1: ").append(brf70_REPORTENTITY.getR3_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_2().compareTo(existingReport.getR3_COL_2()) != 0) {
			fieldNames.add("UAE Nationals (Nos) - Female");
			oldChange.append("R3_COL_2: ").append(existingReport.getR3_COL_2()).append("; ");
			newChange.append("R3_COL_2: ").append(brf70_REPORTENTITY.getR3_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_3().compareTo(existingReport.getR3_COL_3()) != 0) {
			fieldNames.add("Other Nationalities (Nos) - Male");
			oldChange.append("R3_COL_3: ").append(existingReport.getR3_COL_3()).append("; ");
			newChange.append("R3_COL_3: ").append(brf70_REPORTENTITY.getR3_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_4().compareTo(existingReport.getR3_COL_4()) != 0) {
			fieldNames.add("Other Nationalities (Nos) - Female");
			oldChange.append("R3_COL_4: ").append(existingReport.getR3_COL_4()).append("; ");
			newChange.append("R3_COL_4: ").append(brf70_REPORTENTITY.getR3_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_5().compareTo(existingReport.getR3_COL_5()) != 0) {
			fieldNames.add("Total - Male");
			oldChange.append("R3_COL_5: ").append(existingReport.getR3_COL_5()).append("; ");
			newChange.append("R3_COL_5: ").append(brf70_REPORTENTITY.getR3_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_6().compareTo(existingReport.getR3_COL_6()) != 0) {
			fieldNames.add("Total - Female");
			oldChange.append("R3_COL_6: ").append(existingReport.getR3_COL_6()).append("; ");
			newChange.append("R3_COL_6: ").append(brf70_REPORTENTITY.getR3_COL_6()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_7().compareTo(existingReport.getR3_COL_7()) != 0) {
			fieldNames.add("Position of UAE Nationals last year, ending 31 December……… - Male");
			oldChange.append("R3_COL_7: ").append(existingReport.getR3_COL_7()).append("; ");
			newChange.append("R3_COL_7: ").append(brf70_REPORTENTITY.getR3_COL_7()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_8().compareTo(existingReport.getR3_COL_8()) != 0) {
			fieldNames.add("Position of UAE Nationals last year, ending 31 December……… - Female");
			oldChange.append("R3_COL_8: ").append(existingReport.getR3_COL_8()).append("; ");
			newChange.append("R3_COL_8: ").append(brf70_REPORTENTITY.getR3_COL_8()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_9().compareTo(existingReport.getR3_COL_9()) != 0) {
			fieldNames.add("% of UAE Nationals to Total");
			oldChange.append("R3_COL_9: ").append(existingReport.getR3_COL_9()).append("; ");
			newChange.append("R3_COL_9: ").append(brf70_REPORTENTITY.getR3_COL_9()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_10().compareTo(existingReport.getR3_COL_10()) != 0) {
			fieldNames.add("% increase in the number of UAE Nationals since 31 December last year");
			oldChange.append("R3_COL_10: ").append(existingReport.getR3_COL_10()).append("; ");
			newChange.append("R3_COL_10: ").append(brf70_REPORTENTITY.getR3_COL_10()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_11().compareTo(existingReport.getR3_COL_11()) != 0) {
			fieldNames.add("No of UAE Nationals resigned/left during the year - Male");
			oldChange.append("R3_COL_11: ").append(existingReport.getR3_COL_11()).append("; ");
			newChange.append("R3_COL_11: ").append(brf70_REPORTENTITY.getR3_COL_11()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_12().compareTo(existingReport.getR3_COL_12()) != 0) {
			fieldNames.add("No of UAE Nationals resigned/left during the year - Female");
			oldChange.append("R3_COL_12: ").append(existingReport.getR3_COL_12()).append("; ");
			newChange.append("R3_COL_12: ").append(brf70_REPORTENTITY.getR3_COL_12()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_13().compareTo(existingReport.getR3_COL_13()) != 0) {
			fieldNames.add("No of UAE Nationals joined during the year - Male");
			oldChange.append("R3_COL_13: ").append(existingReport.getR3_COL_13()).append("; ");
			newChange.append("R3_COL_13: ").append(brf70_REPORTENTITY.getR3_COL_13()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR3_COL_14().compareTo(existingReport.getR3_COL_14()) != 0) {
			fieldNames.add("No of UAE Nationals joined during the year - Female");
			oldChange.append("R3_COL_14: ").append(existingReport.getR3_COL_14()).append("; ");
			newChange.append("R3_COL_14: ").append(brf70_REPORTENTITY.getR3_COL_14()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR4_COL_1().compareTo(existingReport.getR4_COL_1()) != 0) {
			fieldNames.add("Newly Recruited Employees - Male");
			oldChange.append("R4_COL_1: ").append(existingReport.getR4_COL_1()).append("; ");
			newChange.append("R4_COL_1: ").append(brf70_REPORTENTITY.getR4_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_2().compareTo(existingReport.getR4_COL_2()) != 0) {
			fieldNames.add("Newly Recruited Employees - Female");
			oldChange.append("R4_COL_2: ").append(existingReport.getR4_COL_2()).append("; ");
			newChange.append("R4_COL_2: ").append(brf70_REPORTENTITY.getR4_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_3().compareTo(existingReport.getR4_COL_3()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R4_COL_3: ").append(existingReport.getR4_COL_3()).append("; ");
			newChange.append("R4_COL_3: ").append(brf70_REPORTENTITY.getR4_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_4().compareTo(existingReport.getR4_COL_4()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R4_COL_4: ").append(existingReport.getR4_COL_4()).append("; ");
			newChange.append("R4_COL_4: ").append(brf70_REPORTENTITY.getR4_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_5().compareTo(existingReport.getR4_COL_5()) != 0) {
			fieldNames.add("Training Hours Attended - Male");
			oldChange.append("R4_COL_5: ").append(existingReport.getR4_COL_5()).append("; ");
			newChange.append("R4_COL_5: ").append(brf70_REPORTENTITY.getR4_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_6().compareTo(existingReport.getR4_COL_6()) != 0) {
			fieldNames.add("Training Hours Attended - Female");
			oldChange.append("R4_COL_6: ").append(existingReport.getR4_COL_6()).append("; ");
			newChange.append("R4_COL_6: ").append(brf70_REPORTENTITY.getR4_COL_6()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_7().compareTo(existingReport.getR4_COL_7()) != 0) {
			fieldNames.add("Employees Promoted - Male");
			oldChange.append("R4_COL_7: ").append(existingReport.getR4_COL_7()).append("; ");
			newChange.append("R4_COL_7: ").append(brf70_REPORTENTITY.getR4_COL_7()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_8().compareTo(existingReport.getR4_COL_8()) != 0) {
			fieldNames.add("Employees Promoted - Female");
			oldChange.append("R4_COL_8: ").append(existingReport.getR4_COL_8()).append("; ");
			newChange.append("R4_COL_8: ").append(brf70_REPORTENTITY.getR4_COL_8()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_9().compareTo(existingReport.getR4_COL_9()) != 0) {
			fieldNames.add("Total Employees - Male");
			oldChange.append("R4_COL_9: ").append(existingReport.getR4_COL_9()).append("; ");
			newChange.append("R4_COL_9: ").append(brf70_REPORTENTITY.getR4_COL_9()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_10().compareTo(existingReport.getR4_COL_10()) != 0) {
			fieldNames.add("Total Employees - Female");
			oldChange.append("R4_COL_10: ").append(existingReport.getR4_COL_10()).append("; ");
			newChange.append("R4_COL_10: ").append(brf70_REPORTENTITY.getR4_COL_10()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_11().compareTo(existingReport.getR4_COL_11()) != 0) {
			fieldNames.add("Percentage of Promotions - Male");
			oldChange.append("R4_COL_11: ").append(existingReport.getR4_COL_11()).append("; ");
			newChange.append("R4_COL_11: ").append(brf70_REPORTENTITY.getR4_COL_11()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_12().compareTo(existingReport.getR4_COL_12()) != 0) {
			fieldNames.add("Percentage of Promotions - Female");
			oldChange.append("R4_COL_12: ").append(existingReport.getR4_COL_12()).append("; ");
			newChange.append("R4_COL_12: ").append(brf70_REPORTENTITY.getR4_COL_12()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_13().compareTo(existingReport.getR4_COL_13()) != 0) {
			fieldNames.add("Average Training Hours per Employee - Male");
			oldChange.append("R4_COL_13: ").append(existingReport.getR4_COL_13()).append("; ");
			newChange.append("R4_COL_13: ").append(brf70_REPORTENTITY.getR4_COL_13()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR4_COL_14().compareTo(existingReport.getR4_COL_14()) != 0) {
			fieldNames.add("Average Training Hours per Employee - Female");
			oldChange.append("R4_COL_14: ").append(existingReport.getR4_COL_14()).append("; ");
			newChange.append("R4_COL_14: ").append(brf70_REPORTENTITY.getR4_COL_14()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR5_COL_1().compareTo(existingReport.getR5_COL_1()) != 0) {
			fieldNames.add("Employees on Probation - Male");
			oldChange.append("R5_COL_1: ").append(existingReport.getR5_COL_1()).append("; ");
			newChange.append("R5_COL_1: ").append(brf70_REPORTENTITY.getR5_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_2().compareTo(existingReport.getR5_COL_2()) != 0) {
			fieldNames.add("Employees on Probation - Female");
			oldChange.append("R5_COL_2: ").append(existingReport.getR5_COL_2()).append("; ");
			newChange.append("R5_COL_2: ").append(brf70_REPORTENTITY.getR5_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_3().compareTo(existingReport.getR5_COL_3()) != 0) {
			fieldNames.add("Confirmed Employees - Male");
			oldChange.append("R5_COL_3: ").append(existingReport.getR5_COL_3()).append("; ");
			newChange.append("R5_COL_3: ").append(brf70_REPORTENTITY.getR5_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_4().compareTo(existingReport.getR5_COL_4()) != 0) {
			fieldNames.add("Confirmed Employees - Female");
			oldChange.append("R5_COL_4: ").append(existingReport.getR5_COL_4()).append("; ");
			newChange.append("R5_COL_4: ").append(brf70_REPORTENTITY.getR5_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_5().compareTo(existingReport.getR5_COL_5()) != 0) {
			fieldNames.add("Retired Employees - Male");
			oldChange.append("R5_COL_5: ").append(existingReport.getR5_COL_5()).append("; ");
			newChange.append("R5_COL_5: ").append(brf70_REPORTENTITY.getR5_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_6().compareTo(existingReport.getR5_COL_6()) != 0) {
			fieldNames.add("Retired Employees - Female");
			oldChange.append("R5_COL_6: ").append(existingReport.getR5_COL_6()).append("; ");
			newChange.append("R5_COL_6: ").append(brf70_REPORTENTITY.getR5_COL_6()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_7().compareTo(existingReport.getR5_COL_7()) != 0) {
			fieldNames.add("Employees Transferred In - Male");
			oldChange.append("R5_COL_7: ").append(existingReport.getR5_COL_7()).append("; ");
			newChange.append("R5_COL_7: ").append(brf70_REPORTENTITY.getR5_COL_7()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_8().compareTo(existingReport.getR5_COL_8()) != 0) {
			fieldNames.add("Employees Transferred In - Female");
			oldChange.append("R5_COL_8: ").append(existingReport.getR5_COL_8()).append("; ");
			newChange.append("R5_COL_8: ").append(brf70_REPORTENTITY.getR5_COL_8()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_9().compareTo(existingReport.getR5_COL_9()) != 0) {
			fieldNames.add("Employees Transferred Out - Male");
			oldChange.append("R5_COL_9: ").append(existingReport.getR5_COL_9()).append("; ");
			newChange.append("R5_COL_9: ").append(brf70_REPORTENTITY.getR5_COL_9()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_10().compareTo(existingReport.getR5_COL_10()) != 0) {
			fieldNames.add("Employees Transferred Out - Female");
			oldChange.append("R5_COL_10: ").append(existingReport.getR5_COL_10()).append("; ");
			newChange.append("R5_COL_10: ").append(brf70_REPORTENTITY.getR5_COL_10()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_11().compareTo(existingReport.getR5_COL_11()) != 0) {
			fieldNames.add("Employees on Leave - Male");
			oldChange.append("R5_COL_11: ").append(existingReport.getR5_COL_11()).append("; ");
			newChange.append("R5_COL_11: ").append(brf70_REPORTENTITY.getR5_COL_11()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_12().compareTo(existingReport.getR5_COL_12()) != 0) {
			fieldNames.add("Employees on Leave - Female");
			oldChange.append("R5_COL_12: ").append(existingReport.getR5_COL_12()).append("; ");
			newChange.append("R5_COL_12: ").append(brf70_REPORTENTITY.getR5_COL_12()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_13().compareTo(existingReport.getR5_COL_13()) != 0) {
			fieldNames.add("Employees Engaged in Projects - Male");
			oldChange.append("R5_COL_13: ").append(existingReport.getR5_COL_13()).append("; ");
			newChange.append("R5_COL_13: ").append(brf70_REPORTENTITY.getR5_COL_13()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR5_COL_14().compareTo(existingReport.getR5_COL_14()) != 0) {
			fieldNames.add("Employees Engaged in Projects - Female");
			oldChange.append("R5_COL_14: ").append(existingReport.getR5_COL_14()).append("; ");
			newChange.append("R5_COL_14: ").append(brf70_REPORTENTITY.getR5_COL_14()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR6_COL_1().compareTo(existingReport.getR6_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R6_COL_1: ").append(existingReport.getR6_COL_1()).append("; ");
			newChange.append("R6_COL_1: ").append(brf70_REPORTENTITY.getR6_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_2().compareTo(existingReport.getR6_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R6_COL_2: ").append(existingReport.getR6_COL_2()).append("; ");
			newChange.append("R6_COL_2: ").append(brf70_REPORTENTITY.getR6_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_3().compareTo(existingReport.getR6_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R6_COL_3: ").append(existingReport.getR6_COL_3()).append("; ");
			newChange.append("R6_COL_3: ").append(brf70_REPORTENTITY.getR6_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_4().compareTo(existingReport.getR6_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R6_COL_4: ").append(existingReport.getR6_COL_4()).append("; ");
			newChange.append("R6_COL_4: ").append(brf70_REPORTENTITY.getR6_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_5().compareTo(existingReport.getR6_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R6_COL_5: ").append(existingReport.getR6_COL_5()).append("; ");
			newChange.append("R6_COL_5: ").append(brf70_REPORTENTITY.getR6_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_6().compareTo(existingReport.getR6_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R6_COL_6: ").append(existingReport.getR6_COL_6()).append("; ");
			newChange.append("R6_COL_6: ").append(brf70_REPORTENTITY.getR6_COL_6()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_7().compareTo(existingReport.getR6_COL_7()) != 0) {
			fieldNames.add("Employees Deceased - Male");
			oldChange.append("R6_COL_7: ").append(existingReport.getR6_COL_7()).append("; ");
			newChange.append("R6_COL_7: ").append(brf70_REPORTENTITY.getR6_COL_7()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_8().compareTo(existingReport.getR6_COL_8()) != 0) {
			fieldNames.add("Employees Deceased - Female");
			oldChange.append("R6_COL_8: ").append(existingReport.getR6_COL_8()).append("; ");
			newChange.append("R6_COL_8: ").append(brf70_REPORTENTITY.getR6_COL_8()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_9().compareTo(existingReport.getR6_COL_9()) != 0) {
			fieldNames.add("Employees Retired on Medical Grounds - Male");
			oldChange.append("R6_COL_9: ").append(existingReport.getR6_COL_9()).append("; ");
			newChange.append("R6_COL_9: ").append(brf70_REPORTENTITY.getR6_COL_9()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_10().compareTo(existingReport.getR6_COL_10()) != 0) {
			fieldNames.add("Employees Retired on Medical Grounds - Female");
			oldChange.append("R6_COL_10: ").append(existingReport.getR6_COL_10()).append("; ");
			newChange.append("R6_COL_10: ").append(brf70_REPORTENTITY.getR6_COL_10()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_11().compareTo(existingReport.getR6_COL_11()) != 0) {
			fieldNames.add("Employees Terminated - Male");
			oldChange.append("R6_COL_11: ").append(existingReport.getR6_COL_11()).append("; ");
			newChange.append("R6_COL_11: ").append(brf70_REPORTENTITY.getR6_COL_11()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_12().compareTo(existingReport.getR6_COL_12()) != 0) {
			fieldNames.add("Employees Terminated - Female");
			oldChange.append("R6_COL_12: ").append(existingReport.getR6_COL_12()).append("; ");
			newChange.append("R6_COL_12: ").append(brf70_REPORTENTITY.getR6_COL_12()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_13().compareTo(existingReport.getR6_COL_13()) != 0) {
			fieldNames.add("Employees Absent without Leave - Male");
			oldChange.append("R6_COL_13: ").append(existingReport.getR6_COL_13()).append("; ");
			newChange.append("R6_COL_13: ").append(brf70_REPORTENTITY.getR6_COL_13()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR6_COL_14().compareTo(existingReport.getR6_COL_14()) != 0) {
			fieldNames.add("Employees Absent without Leave - Female");
			oldChange.append("R6_COL_14: ").append(existingReport.getR6_COL_14()).append("; ");
			newChange.append("R6_COL_14: ").append(brf70_REPORTENTITY.getR6_COL_14()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR7_COL_1().compareTo(existingReport.getR7_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R7_COL_1: ").append(existingReport.getR7_COL_1()).append("; ");
			newChange.append("R7_COL_1: ").append(brf70_REPORTENTITY.getR7_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_2().compareTo(existingReport.getR7_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R7_COL_2: ").append(existingReport.getR7_COL_2()).append("; ");
			newChange.append("R7_COL_2: ").append(brf70_REPORTENTITY.getR7_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_3().compareTo(existingReport.getR7_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R7_COL_3: ").append(existingReport.getR7_COL_3()).append("; ");
			newChange.append("R7_COL_3: ").append(brf70_REPORTENTITY.getR7_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_4().compareTo(existingReport.getR7_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R7_COL_4: ").append(existingReport.getR7_COL_4()).append("; ");
			newChange.append("R7_COL_4: ").append(brf70_REPORTENTITY.getR7_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_5().compareTo(existingReport.getR7_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R7_COL_5: ").append(existingReport.getR7_COL_5()).append("; ");
			newChange.append("R7_COL_5: ").append(brf70_REPORTENTITY.getR7_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_6().compareTo(existingReport.getR7_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R7_COL_6: ").append(existingReport.getR7_COL_6()).append("; ");
			newChange.append("R7_COL_6: ").append(brf70_REPORTENTITY.getR7_COL_6()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_7().compareTo(existingReport.getR7_COL_7()) != 0) {
			fieldNames.add("Employees Deceased - Male");
			oldChange.append("R7_COL_7: ").append(existingReport.getR7_COL_7()).append("; ");
			newChange.append("R7_COL_7: ").append(brf70_REPORTENTITY.getR7_COL_7()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_8().compareTo(existingReport.getR7_COL_8()) != 0) {
			fieldNames.add("Employees Deceased - Female");
			oldChange.append("R7_COL_8: ").append(existingReport.getR7_COL_8()).append("; ");
			newChange.append("R7_COL_8: ").append(brf70_REPORTENTITY.getR7_COL_8()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_9().compareTo(existingReport.getR7_COL_9()) != 0) {
			fieldNames.add("Employees Retired on Medical Grounds - Male");
			oldChange.append("R7_COL_9: ").append(existingReport.getR7_COL_9()).append("; ");
			newChange.append("R7_COL_9: ").append(brf70_REPORTENTITY.getR7_COL_9()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_10().compareTo(existingReport.getR7_COL_10()) != 0) {
			fieldNames.add("Employees Retired on Medical Grounds - Female");
			oldChange.append("R7_COL_10: ").append(existingReport.getR7_COL_10()).append("; ");
			newChange.append("R7_COL_10: ").append(brf70_REPORTENTITY.getR7_COL_10()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_11().compareTo(existingReport.getR7_COL_11()) != 0) {
			fieldNames.add("Employees Terminated - Male");
			oldChange.append("R7_COL_11: ").append(existingReport.getR7_COL_11()).append("; ");
			newChange.append("R7_COL_11: ").append(brf70_REPORTENTITY.getR7_COL_11()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_12().compareTo(existingReport.getR7_COL_12()) != 0) {
			fieldNames.add("Employees Terminated - Female");
			oldChange.append("R7_COL_12: ").append(existingReport.getR7_COL_12()).append("; ");
			newChange.append("R7_COL_12: ").append(brf70_REPORTENTITY.getR7_COL_12()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_13().compareTo(existingReport.getR7_COL_13()) != 0) {
			fieldNames.add("Employees Absent without Leave - Male");
			oldChange.append("R7_COL_13: ").append(existingReport.getR7_COL_13()).append("; ");
			newChange.append("R7_COL_13: ").append(brf70_REPORTENTITY.getR7_COL_13()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR7_COL_14().compareTo(existingReport.getR7_COL_14()) != 0) {
			fieldNames.add("Employees Absent without Leave - Female");
			oldChange.append("R7_COL_14: ").append(existingReport.getR7_COL_14()).append("; ");
			newChange.append("R7_COL_14: ").append(brf70_REPORTENTITY.getR7_COL_14()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR8_COL_1().compareTo(existingReport.getR8_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R8_COL_1: ").append(existingReport.getR8_COL_1()).append("; ");
			newChange.append("R8_COL_1: ").append(brf70_REPORTENTITY.getR8_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR8_COL_2().compareTo(existingReport.getR8_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R8_COL_2: ").append(existingReport.getR8_COL_2()).append("; ");
			newChange.append("R8_COL_2: ").append(brf70_REPORTENTITY.getR8_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR8_COL_3().compareTo(existingReport.getR8_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R8_COL_3: ").append(existingReport.getR8_COL_3()).append("; ");
			newChange.append("R8_COL_3: ").append(brf70_REPORTENTITY.getR8_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR8_COL_4().compareTo(existingReport.getR8_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R8_COL_4: ").append(existingReport.getR8_COL_4()).append("; ");
			newChange.append("R8_COL_4: ").append(brf70_REPORTENTITY.getR8_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR8_COL_5().compareTo(existingReport.getR8_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R8_COL_5: ").append(existingReport.getR8_COL_5()).append("; ");
			newChange.append("R8_COL_5: ").append(brf70_REPORTENTITY.getR8_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR8_COL_6().compareTo(existingReport.getR8_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R8_COL_6: ").append(existingReport.getR8_COL_6()).append("; ");
			newChange.append("R8_COL_6: ").append(brf70_REPORTENTITY.getR8_COL_6()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR9_COL_1().compareTo(existingReport.getR9_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R9_COL_1: ").append(existingReport.getR9_COL_1()).append("; ");
			newChange.append("R9_COL_1: ").append(brf70_REPORTENTITY.getR9_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR9_COL_2().compareTo(existingReport.getR9_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R9_COL_2: ").append(existingReport.getR9_COL_2()).append("; ");
			newChange.append("R9_COL_2: ").append(brf70_REPORTENTITY.getR9_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR9_COL_3().compareTo(existingReport.getR9_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R9_COL_3: ").append(existingReport.getR9_COL_3()).append("; ");
			newChange.append("R9_COL_3: ").append(brf70_REPORTENTITY.getR9_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR9_COL_4().compareTo(existingReport.getR9_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R9_COL_4: ").append(existingReport.getR9_COL_4()).append("; ");
			newChange.append("R9_COL_4: ").append(brf70_REPORTENTITY.getR9_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR9_COL_5().compareTo(existingReport.getR9_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R9_COL_5: ").append(existingReport.getR9_COL_5()).append("; ");
			newChange.append("R9_COL_5: ").append(brf70_REPORTENTITY.getR9_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR9_COL_6().compareTo(existingReport.getR9_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R9_COL_6: ").append(existingReport.getR9_COL_6()).append("; ");
			newChange.append("R9_COL_6: ").append(brf70_REPORTENTITY.getR9_COL_6()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR10_COL_1().compareTo(existingReport.getR10_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R10_COL_1: ").append(existingReport.getR10_COL_1()).append("; ");
			newChange.append("R10_COL_1: ").append(brf70_REPORTENTITY.getR10_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR10_COL_2().compareTo(existingReport.getR10_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R10_COL_2: ").append(existingReport.getR10_COL_2()).append("; ");
			newChange.append("R10_COL_2: ").append(brf70_REPORTENTITY.getR10_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR10_COL_3().compareTo(existingReport.getR10_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R10_COL_3: ").append(existingReport.getR10_COL_3()).append("; ");
			newChange.append("R10_COL_3: ").append(brf70_REPORTENTITY.getR10_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR10_COL_4().compareTo(existingReport.getR10_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R10_COL_4: ").append(existingReport.getR10_COL_4()).append("; ");
			newChange.append("R10_COL_4: ").append(brf70_REPORTENTITY.getR10_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR10_COL_5().compareTo(existingReport.getR10_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R10_COL_5: ").append(existingReport.getR10_COL_5()).append("; ");
			newChange.append("R10_COL_5: ").append(brf70_REPORTENTITY.getR10_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR10_COL_6().compareTo(existingReport.getR10_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R10_COL_6: ").append(existingReport.getR10_COL_6()).append("; ");
			newChange.append("R10_COL_6: ").append(brf70_REPORTENTITY.getR10_COL_6()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR11_COL_1().compareTo(existingReport.getR11_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R11_COL_1: ").append(existingReport.getR11_COL_1()).append("; ");
			newChange.append("R11_COL_1: ").append(brf70_REPORTENTITY.getR11_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR11_COL_2().compareTo(existingReport.getR11_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R11_COL_2: ").append(existingReport.getR11_COL_2()).append("; ");
			newChange.append("R11_COL_2: ").append(brf70_REPORTENTITY.getR11_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR11_COL_3().compareTo(existingReport.getR11_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R11_COL_3: ").append(existingReport.getR11_COL_3()).append("; ");
			newChange.append("R11_COL_3: ").append(brf70_REPORTENTITY.getR11_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR11_COL_4().compareTo(existingReport.getR11_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R11_COL_4: ").append(existingReport.getR11_COL_4()).append("; ");
			newChange.append("R11_COL_4: ").append(brf70_REPORTENTITY.getR11_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR11_COL_5().compareTo(existingReport.getR11_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R11_COL_5: ").append(existingReport.getR11_COL_5()).append("; ");
			newChange.append("R11_COL_5: ").append(brf70_REPORTENTITY.getR11_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR11_COL_6().compareTo(existingReport.getR11_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R11_COL_6: ").append(existingReport.getR11_COL_6()).append("; ");
			newChange.append("R11_COL_6: ").append(brf70_REPORTENTITY.getR11_COL_6()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR12_COL_1().compareTo(existingReport.getR12_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R12_COL_1: ").append(existingReport.getR12_COL_1()).append("; ");
			newChange.append("R12_COL_1: ").append(brf70_REPORTENTITY.getR12_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR12_COL_2().compareTo(existingReport.getR12_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R12_COL_2: ").append(existingReport.getR12_COL_2()).append("; ");
			newChange.append("R12_COL_2: ").append(brf70_REPORTENTITY.getR12_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR12_COL_3().compareTo(existingReport.getR12_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R12_COL_3: ").append(existingReport.getR12_COL_3()).append("; ");
			newChange.append("R12_COL_3: ").append(brf70_REPORTENTITY.getR12_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR12_COL_4().compareTo(existingReport.getR12_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R12_COL_4: ").append(existingReport.getR12_COL_4()).append("; ");
			newChange.append("R12_COL_4: ").append(brf70_REPORTENTITY.getR12_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR12_COL_5().compareTo(existingReport.getR12_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R12_COL_5: ").append(existingReport.getR12_COL_5()).append("; ");
			newChange.append("R12_COL_5: ").append(brf70_REPORTENTITY.getR12_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR12_COL_6().compareTo(existingReport.getR12_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R12_COL_6: ").append(existingReport.getR12_COL_6()).append("; ");
			newChange.append("R12_COL_6: ").append(brf70_REPORTENTITY.getR12_COL_6()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR13_COL_1().compareTo(existingReport.getR13_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R13_COL_1: ").append(existingReport.getR13_COL_1()).append("; ");
			newChange.append("R13_COL_1: ").append(brf70_REPORTENTITY.getR13_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR13_COL_2().compareTo(existingReport.getR13_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R13_COL_2: ").append(existingReport.getR13_COL_2()).append("; ");
			newChange.append("R13_COL_2: ").append(brf70_REPORTENTITY.getR13_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR13_COL_3().compareTo(existingReport.getR13_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R13_COL_3: ").append(existingReport.getR13_COL_3()).append("; ");
			newChange.append("R13_COL_3: ").append(brf70_REPORTENTITY.getR13_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR13_COL_4().compareTo(existingReport.getR13_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R13_COL_4: ").append(existingReport.getR13_COL_4()).append("; ");
			newChange.append("R13_COL_4: ").append(brf70_REPORTENTITY.getR13_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR13_COL_5().compareTo(existingReport.getR13_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R13_COL_5: ").append(existingReport.getR13_COL_5()).append("; ");
			newChange.append("R13_COL_5: ").append(brf70_REPORTENTITY.getR13_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR13_COL_6().compareTo(existingReport.getR13_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R13_COL_6: ").append(existingReport.getR13_COL_6()).append("; ");
			newChange.append("R13_COL_6: ").append(brf70_REPORTENTITY.getR13_COL_6()).append("; ");
			rowEdited = true;
		}

		if (brf70_REPORTENTITY.getR14_COL_1().compareTo(existingReport.getR14_COL_1()) != 0) {
			fieldNames.add("Employees Resigned - Male");
			oldChange.append("R14_COL_1: ").append(existingReport.getR14_COL_1()).append("; ");
			newChange.append("R14_COL_1: ").append(brf70_REPORTENTITY.getR14_COL_1()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR14_COL_2().compareTo(existingReport.getR14_COL_2()) != 0) {
			fieldNames.add("Employees Resigned - Female");
			oldChange.append("R14_COL_2: ").append(existingReport.getR14_COL_2()).append("; ");
			newChange.append("R14_COL_2: ").append(brf70_REPORTENTITY.getR14_COL_2()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR14_COL_3().compareTo(existingReport.getR14_COL_3()) != 0) {
			fieldNames.add("Employees Retrenched - Male");
			oldChange.append("R14_COL_3: ").append(existingReport.getR14_COL_3()).append("; ");
			newChange.append("R14_COL_3: ").append(brf70_REPORTENTITY.getR14_COL_3()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR14_COL_4().compareTo(existingReport.getR14_COL_4()) != 0) {
			fieldNames.add("Employees Retrenched - Female");
			oldChange.append("R14_COL_4: ").append(existingReport.getR14_COL_4()).append("; ");
			newChange.append("R14_COL_4: ").append(brf70_REPORTENTITY.getR14_COL_4()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR14_COL_5().compareTo(existingReport.getR14_COL_5()) != 0) {
			fieldNames.add("Employees Suspended - Male");
			oldChange.append("R14_COL_5: ").append(existingReport.getR14_COL_5()).append("; ");
			newChange.append("R14_COL_5: ").append(brf70_REPORTENTITY.getR14_COL_5()).append("; ");
			rowEdited = true;
		}
		if (brf70_REPORTENTITY.getR14_COL_6().compareTo(existingReport.getR14_COL_6()) != 0) {
			fieldNames.add("Employees Suspended - Female");
			oldChange.append("R14_COL_6: ").append(existingReport.getR14_COL_6()).append("; ");
			newChange.append("R14_COL_6: ").append(brf70_REPORTENTITY.getR14_COL_6()).append("; ");
			rowEdited = true;
		}

		// If any changes were detected, save the changes with a row identifier
		if (rowEdited) {
			oldValuesList.add(oldChange.toString().trim());
			newValuesList.add(newChange.toString().trim());

			// Here, save the updated report for each row in the database
			// Example: yourRepository.save(userReport);
		}

		if (existingReport != null) {
			// Update the fields of the existing report with the new data
			existingReport.setR1_COL_1(brf70_REPORTENTITY.getR1_COL_1());
			existingReport.setR1_COL_2(brf70_REPORTENTITY.getR1_COL_2());
			existingReport.setR1_COL_3(brf70_REPORTENTITY.getR1_COL_3());
			existingReport.setR1_COL_4(brf70_REPORTENTITY.getR1_COL_4());
			existingReport.setR1_COL_5(brf70_REPORTENTITY.getR1_COL_5());
			existingReport.setR1_COL_6(brf70_REPORTENTITY.getR1_COL_6());
			existingReport.setR1_COL_7(brf70_REPORTENTITY.getR1_COL_7());
			existingReport.setR1_COL_8(brf70_REPORTENTITY.getR1_COL_8());
			existingReport.setR1_COL_9(brf70_REPORTENTITY.getR1_COL_9());
			existingReport.setR1_COL_10(brf70_REPORTENTITY.getR1_COL_10());
			existingReport.setR1_COL_11(brf70_REPORTENTITY.getR1_COL_11());
			existingReport.setR1_COL_12(brf70_REPORTENTITY.getR1_COL_12());
			existingReport.setR1_COL_13(brf70_REPORTENTITY.getR1_COL_13());
			existingReport.setR1_COL_14(brf70_REPORTENTITY.getR1_COL_14());

			existingReport.setR2_COL_1(brf70_REPORTENTITY.getR2_COL_1());
			existingReport.setR2_COL_2(brf70_REPORTENTITY.getR2_COL_2());
			existingReport.setR2_COL_3(brf70_REPORTENTITY.getR2_COL_3());
			existingReport.setR2_COL_4(brf70_REPORTENTITY.getR2_COL_4());
			existingReport.setR2_COL_5(brf70_REPORTENTITY.getR2_COL_5());
			existingReport.setR2_COL_6(brf70_REPORTENTITY.getR2_COL_6());
			existingReport.setR2_COL_7(brf70_REPORTENTITY.getR2_COL_7());
			existingReport.setR2_COL_8(brf70_REPORTENTITY.getR2_COL_8());
			existingReport.setR2_COL_9(brf70_REPORTENTITY.getR2_COL_9());
			existingReport.setR2_COL_10(brf70_REPORTENTITY.getR2_COL_10());
			existingReport.setR2_COL_11(brf70_REPORTENTITY.getR2_COL_11());
			existingReport.setR2_COL_12(brf70_REPORTENTITY.getR2_COL_12());
			existingReport.setR2_COL_13(brf70_REPORTENTITY.getR2_COL_13());
			existingReport.setR2_COL_14(brf70_REPORTENTITY.getR2_COL_14());

			existingReport.setR3_COL_1(brf70_REPORTENTITY.getR3_COL_1());
			existingReport.setR3_COL_2(brf70_REPORTENTITY.getR3_COL_2());
			existingReport.setR3_COL_3(brf70_REPORTENTITY.getR3_COL_3());
			existingReport.setR3_COL_4(brf70_REPORTENTITY.getR3_COL_4());
			existingReport.setR3_COL_5(brf70_REPORTENTITY.getR3_COL_5());
			existingReport.setR3_COL_6(brf70_REPORTENTITY.getR3_COL_6());
			existingReport.setR3_COL_7(brf70_REPORTENTITY.getR3_COL_7());
			existingReport.setR3_COL_8(brf70_REPORTENTITY.getR3_COL_8());
			existingReport.setR3_COL_9(brf70_REPORTENTITY.getR3_COL_9());
			existingReport.setR3_COL_10(brf70_REPORTENTITY.getR3_COL_10());
			existingReport.setR3_COL_11(brf70_REPORTENTITY.getR3_COL_11());
			existingReport.setR3_COL_12(brf70_REPORTENTITY.getR3_COL_12());
			existingReport.setR3_COL_13(brf70_REPORTENTITY.getR3_COL_13());
			existingReport.setR3_COL_14(brf70_REPORTENTITY.getR3_COL_14());

			existingReport.setR4_COL_1(brf70_REPORTENTITY.getR4_COL_1());
			existingReport.setR4_COL_2(brf70_REPORTENTITY.getR4_COL_2());
			existingReport.setR4_COL_3(brf70_REPORTENTITY.getR4_COL_3());
			existingReport.setR4_COL_4(brf70_REPORTENTITY.getR4_COL_4());
			existingReport.setR4_COL_5(brf70_REPORTENTITY.getR4_COL_5());
			existingReport.setR4_COL_6(brf70_REPORTENTITY.getR4_COL_6());
			existingReport.setR4_COL_7(brf70_REPORTENTITY.getR4_COL_7());
			existingReport.setR4_COL_8(brf70_REPORTENTITY.getR4_COL_8());
			existingReport.setR4_COL_9(brf70_REPORTENTITY.getR4_COL_9());
			existingReport.setR4_COL_10(brf70_REPORTENTITY.getR4_COL_10());
			existingReport.setR4_COL_11(brf70_REPORTENTITY.getR4_COL_11());
			existingReport.setR4_COL_12(brf70_REPORTENTITY.getR4_COL_12());
			existingReport.setR4_COL_13(brf70_REPORTENTITY.getR4_COL_13());
			existingReport.setR4_COL_14(brf70_REPORTENTITY.getR4_COL_14());

			existingReport.setR5_COL_1(brf70_REPORTENTITY.getR5_COL_1());
			existingReport.setR5_COL_2(brf70_REPORTENTITY.getR5_COL_2());
			existingReport.setR5_COL_3(brf70_REPORTENTITY.getR5_COL_3());
			existingReport.setR5_COL_4(brf70_REPORTENTITY.getR5_COL_4());
			existingReport.setR5_COL_5(brf70_REPORTENTITY.getR5_COL_5());
			existingReport.setR5_COL_6(brf70_REPORTENTITY.getR5_COL_6());
			existingReport.setR5_COL_7(brf70_REPORTENTITY.getR5_COL_7());
			existingReport.setR5_COL_8(brf70_REPORTENTITY.getR5_COL_8());
			existingReport.setR5_COL_9(brf70_REPORTENTITY.getR5_COL_9());
			existingReport.setR5_COL_10(brf70_REPORTENTITY.getR5_COL_10());
			existingReport.setR5_COL_11(brf70_REPORTENTITY.getR5_COL_11());
			existingReport.setR5_COL_12(brf70_REPORTENTITY.getR5_COL_12());
			existingReport.setR5_COL_13(brf70_REPORTENTITY.getR5_COL_13());
			existingReport.setR5_COL_14(brf70_REPORTENTITY.getR5_COL_14());

			existingReport.setR6_COL_1(brf70_REPORTENTITY.getR6_COL_1());
			existingReport.setR6_COL_2(brf70_REPORTENTITY.getR6_COL_2());
			existingReport.setR6_COL_3(brf70_REPORTENTITY.getR6_COL_3());
			existingReport.setR6_COL_4(brf70_REPORTENTITY.getR6_COL_4());
			existingReport.setR6_COL_5(brf70_REPORTENTITY.getR6_COL_5());
			existingReport.setR6_COL_6(brf70_REPORTENTITY.getR6_COL_6());
			existingReport.setR6_COL_7(brf70_REPORTENTITY.getR6_COL_7());
			existingReport.setR6_COL_8(brf70_REPORTENTITY.getR6_COL_8());
			existingReport.setR6_COL_9(brf70_REPORTENTITY.getR6_COL_9());
			existingReport.setR6_COL_10(brf70_REPORTENTITY.getR6_COL_10());
			existingReport.setR6_COL_11(brf70_REPORTENTITY.getR6_COL_11());
			existingReport.setR6_COL_12(brf70_REPORTENTITY.getR6_COL_12());
			existingReport.setR6_COL_13(brf70_REPORTENTITY.getR6_COL_13());
			existingReport.setR6_COL_14(brf70_REPORTENTITY.getR6_COL_14());

			existingReport.setR7_COL_1(brf70_REPORTENTITY.getR7_COL_1());
			existingReport.setR7_COL_2(brf70_REPORTENTITY.getR7_COL_2());
			existingReport.setR7_COL_3(brf70_REPORTENTITY.getR7_COL_3());
			existingReport.setR7_COL_4(brf70_REPORTENTITY.getR7_COL_4());
			existingReport.setR7_COL_5(brf70_REPORTENTITY.getR7_COL_5());
			existingReport.setR7_COL_6(brf70_REPORTENTITY.getR7_COL_6());
			existingReport.setR7_COL_7(brf70_REPORTENTITY.getR7_COL_7());
			existingReport.setR7_COL_8(brf70_REPORTENTITY.getR7_COL_8());
			existingReport.setR7_COL_9(brf70_REPORTENTITY.getR7_COL_9());
			existingReport.setR7_COL_10(brf70_REPORTENTITY.getR7_COL_10());
			existingReport.setR7_COL_11(brf70_REPORTENTITY.getR7_COL_11());
			existingReport.setR7_COL_12(brf70_REPORTENTITY.getR7_COL_12());
			existingReport.setR7_COL_13(brf70_REPORTENTITY.getR7_COL_13());
			existingReport.setR7_COL_14(brf70_REPORTENTITY.getR7_COL_14());

			existingReport.setR8_COL_1(brf70_REPORTENTITY.getR8_COL_1());
			existingReport.setR8_COL_2(brf70_REPORTENTITY.getR8_COL_2());
			existingReport.setR8_COL_3(brf70_REPORTENTITY.getR8_COL_3());
			existingReport.setR8_COL_4(brf70_REPORTENTITY.getR8_COL_4());
			existingReport.setR8_COL_5(brf70_REPORTENTITY.getR8_COL_5());
			existingReport.setR8_COL_6(brf70_REPORTENTITY.getR8_COL_6());

			existingReport.setR9_COL_1(brf70_REPORTENTITY.getR9_COL_1());
			existingReport.setR9_COL_2(brf70_REPORTENTITY.getR9_COL_2());
			existingReport.setR9_COL_3(brf70_REPORTENTITY.getR9_COL_3());
			existingReport.setR9_COL_4(brf70_REPORTENTITY.getR9_COL_4());
			existingReport.setR9_COL_5(brf70_REPORTENTITY.getR9_COL_5());
			existingReport.setR9_COL_6(brf70_REPORTENTITY.getR9_COL_6());

			existingReport.setR10_COL_1(brf70_REPORTENTITY.getR10_COL_1());
			existingReport.setR10_COL_2(brf70_REPORTENTITY.getR10_COL_2());
			existingReport.setR10_COL_3(brf70_REPORTENTITY.getR10_COL_3());
			existingReport.setR10_COL_4(brf70_REPORTENTITY.getR10_COL_4());
			existingReport.setR10_COL_5(brf70_REPORTENTITY.getR10_COL_5());
			existingReport.setR10_COL_6(brf70_REPORTENTITY.getR10_COL_6());

			existingReport.setR11_COL_1(brf70_REPORTENTITY.getR11_COL_1());
			existingReport.setR11_COL_2(brf70_REPORTENTITY.getR11_COL_2());
			existingReport.setR11_COL_3(brf70_REPORTENTITY.getR11_COL_3());
			existingReport.setR11_COL_4(brf70_REPORTENTITY.getR11_COL_4());
			existingReport.setR11_COL_5(brf70_REPORTENTITY.getR11_COL_5());
			existingReport.setR11_COL_6(brf70_REPORTENTITY.getR11_COL_6());

			existingReport.setR12_COL_1(brf70_REPORTENTITY.getR12_COL_1());
			existingReport.setR12_COL_2(brf70_REPORTENTITY.getR12_COL_2());
			existingReport.setR12_COL_3(brf70_REPORTENTITY.getR12_COL_3());
			existingReport.setR12_COL_4(brf70_REPORTENTITY.getR12_COL_4());
			existingReport.setR12_COL_5(brf70_REPORTENTITY.getR12_COL_5());
			existingReport.setR12_COL_6(brf70_REPORTENTITY.getR12_COL_6());

			existingReport.setR13_COL_1(brf70_REPORTENTITY.getR13_COL_1());
			existingReport.setR13_COL_2(brf70_REPORTENTITY.getR13_COL_2());
			existingReport.setR13_COL_3(brf70_REPORTENTITY.getR13_COL_3());
			existingReport.setR13_COL_4(brf70_REPORTENTITY.getR13_COL_4());
			existingReport.setR13_COL_5(brf70_REPORTENTITY.getR13_COL_5());
			existingReport.setR13_COL_6(brf70_REPORTENTITY.getR13_COL_6());

			existingReport.setR14_COL_1(brf70_REPORTENTITY.getR14_COL_1());
			existingReport.setR14_COL_2(brf70_REPORTENTITY.getR14_COL_2());
			existingReport.setR14_COL_3(brf70_REPORTENTITY.getR14_COL_3());
			existingReport.setR14_COL_4(brf70_REPORTENTITY.getR14_COL_4());
			existingReport.setR14_COL_5(brf70_REPORTENTITY.getR14_COL_5());
			existingReport.setR14_COL_6(brf70_REPORTENTITY.getR14_COL_6());

			// Repeat for other columns (R3, R4, R5, etc.)

			// Set flags
			existingReport.setDel_flg("N");
			existingReport.setEntity_flg("Y");
			existingReport.setModify_flg("Y");

			// Save the updated entity
			brf070AserviceRepo.save(existingReport);
			msg = "Updated Successfully";

			// Fetch all reports for comparison

			String auditID = sequence.generateRequestUUId();
			String user1 = (String) req.getSession().getAttribute("USERID");
			String username = (String) req.getSession().getAttribute("USERNAME");

			MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
			audit.setAudit_date(new Date());
			audit.setEntry_time(new Date());
			audit.setEntry_user(user1);
			audit.setFunc_code("MODIFY");
			audit.setAudit_table("BRF70_SUMMARYTABLE");
			audit.setAudit_screen("MODIFY");
			audit.setEvent_id(user1);
			audit.setEvent_name(username);
			audit.setOld_value(String.join("; ", oldValuesList));
			audit.setNew_value(String.join("; ", newValuesList));
			System.out.println("New Values: " + String.join("; ", newValuesList));
			audit.setField_name(String.join("; ", fieldNames));
			audit.setRemarks("Modify Successfully");

			UserProfile values1 = userProfileRep.getRole(user1);
			audit.setAuth_user(values1.getAuth_user());
			audit.setAuth_time(values1.getAuth_time());
			audit.setAudit_ref_no(auditID);

			mANUAL_Service_Rep.save(audit);
		}
		return msg;
	}

	@PostMapping("modifyBrf202A")
	@ResponseBody
	public String modifyBrf202A(HttpServletRequest req, @RequestBody ReportBRF202AData formData,
			@RequestParam(required = false) Date asondate) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.modifyBrf202A(formData, asondate, userId, req);
	}

	@PostMapping("modifyBrf301")
	@ResponseBody
	public String modifyBrf301(HttpServletRequest req, @RequestBody ReportBRF301Data formData,
			@RequestParam(required = false) Date asondate, @RequestParam(required = false) String userId) {
		return regreportServices.modifyBrf301(formData, asondate, userId, req);
	}

	@PostMapping("modifyBrf108")
	@ResponseBody
	public String modifyBrf108(HttpServletRequest req, @RequestBody ReportBRF108Data formData,
			@RequestParam(required = false) Date asondate, @RequestParam(required = false) String userId) {
		return regreportServices.modifyBrf108(formData, asondate, userId, req);
	}

	@RequestMapping(value = "Modify71", method = RequestMethod.POST)
	@ResponseBody
	public String Modify71(@RequestParam(required = false) String report_date,
			@ModelAttribute BRF71_ENTITY brf71_REPORTENTITY, HttpServletRequest req) throws ParseException {

		if (report_date == null || report_date.trim().isEmpty()) {
			return "Report date is missing";
		}

		String msg = "";
		List<String> oldValuesList = new ArrayList<>();
		List<String> newValuesList = new ArrayList<>();
		List<String> fieldNames = new ArrayList<>();
		StringBuilder oldChange = new StringBuilder();
		StringBuilder newChange = new StringBuilder();
		boolean rowEdited = false;

		// Remove the catch blocks and allow exceptions to propagate
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date date = inputFormat.parse(report_date); // Will throw ParseException if invalid
		String rep_date = outputFormat.format(date);

		// Fetch existing entity by report date
		BRF71_ENTITY existingEntity = bRF71_ServiceRepo.findByReportDate(rep_date);

		if (existingEntity == null) {
			return "Data Not Found";
		}

		// Compare and track changes for each field
		if (brf71_REPORTENTITY.getR1_visa() != null
				&& !brf71_REPORTENTITY.getR1_visa().equals(existingEntity.getR1_visa())) {
			fieldNames.add("Debit Cards: - Visa");
			oldChange.append("R1_VISA: ").append(existingEntity.getR1_visa()).append("; ");
			newChange.append("R1_VISA: ").append(brf71_REPORTENTITY.getR1_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR1_mastercard() != null
				&& !brf71_REPORTENTITY.getR1_mastercard().equals(existingEntity.getR1_mastercard())) {
			fieldNames.add("Debit Cards: - MasterCard");
			oldChange.append("R1_MASTERCARD: ").append(existingEntity.getR1_mastercard()).append("; ");
			newChange.append("R1_MASTERCARD: ").append(brf71_REPORTENTITY.getR1_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR1_amex() != null
				&& !brf71_REPORTENTITY.getR1_amex().equals(existingEntity.getR1_amex())) {
			fieldNames.add("Debit Cards: - Amex");
			oldChange.append("R1_AMEX: ").append(existingEntity.getR1_amex()).append("; ");
			newChange.append("R1_AMEX: ").append(brf71_REPORTENTITY.getR1_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR1_jcb() != null
				&& !brf71_REPORTENTITY.getR1_jcb().equals(existingEntity.getR1_jcb())) {
			fieldNames.add("Debit Cards: - JCB");
			oldChange.append("R1_JCB: ").append(existingEntity.getR1_jcb()).append("; ");
			newChange.append("R1_JCB: ").append(brf71_REPORTENTITY.getR1_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR1_diners() != null
				&& !brf71_REPORTENTITY.getR1_diners().equals(existingEntity.getR1_diners())) {
			fieldNames.add("Debit Cards: - Diners");
			oldChange.append("R1_DINERS: ").append(existingEntity.getR1_diners()).append("; ");
			newChange.append("R1_DINERS: ").append(brf71_REPORTENTITY.getR1_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR1_unionpay() != null
				&& !brf71_REPORTENTITY.getR1_unionpay().equals(existingEntity.getR1_unionpay())) {
			fieldNames.add("Debit Cards: - Union Pay");
			oldChange.append("R1_UNIONPAY: ").append(existingEntity.getR1_unionpay()).append("; ");
			newChange.append("R1_UNIONPAY: ").append(brf71_REPORTENTITY.getR1_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR1_total() != null
				&& !brf71_REPORTENTITY.getR1_total().equals(existingEntity.getR1_total())) {
			fieldNames.add("Debit Cards: -  TOTAL");
			oldChange.append("R1_TOTAL: ").append(existingEntity.getR1_total()).append("; ");
			newChange.append("R1_TOTAL: ").append(brf71_REPORTENTITY.getR1_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR2_visa() != null
				&& !brf71_REPORTENTITY.getR2_visa().equals(existingEntity.getR2_visa())) {
			fieldNames.add("Chip & PIN: - Visa");
			oldChange.append("R2_VISA: ").append(existingEntity.getR2_visa()).append("; ");
			newChange.append("R2_VISA: ").append(brf71_REPORTENTITY.getR2_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR2_mastercard() != null
				&& !brf71_REPORTENTITY.getR2_mastercard().equals(existingEntity.getR2_mastercard())) {
			fieldNames.add("Chip & PIN: - MasterCard");
			oldChange.append("R2_MASTERCARD: ").append(existingEntity.getR2_mastercard()).append("; ");
			newChange.append("R2_MASTERCARD: ").append(brf71_REPORTENTITY.getR2_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR2_amex() != null
				&& !brf71_REPORTENTITY.getR2_amex().equals(existingEntity.getR2_amex())) {
			fieldNames.add("Chip & PIN: - Amex");
			oldChange.append("R2_AMEX: ").append(existingEntity.getR2_amex()).append("; ");
			newChange.append("R2_AMEX: ").append(brf71_REPORTENTITY.getR2_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR2_jcb() != null
				&& !brf71_REPORTENTITY.getR2_jcb().equals(existingEntity.getR2_jcb())) {
			fieldNames.add("Chip & PIN: - JCB");
			oldChange.append("R2_JCB: ").append(existingEntity.getR2_jcb()).append("; ");
			newChange.append("R2_JCB: ").append(brf71_REPORTENTITY.getR2_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR2_diners() != null
				&& !brf71_REPORTENTITY.getR2_diners().equals(existingEntity.getR2_diners())) {
			fieldNames.add("Chip & PIN: - Diners");
			oldChange.append("R2_DINERS: ").append(existingEntity.getR2_diners()).append("; ");
			newChange.append("R2_DINERS: ").append(brf71_REPORTENTITY.getR2_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR2_unionpay() != null
				&& !brf71_REPORTENTITY.getR2_unionpay().equals(existingEntity.getR2_unionpay())) {
			fieldNames.add("Chip & PIN: - Union Pay");
			oldChange.append("R2_UNIONPAY: ").append(existingEntity.getR2_unionpay()).append("; ");
			newChange.append("R2_UNIONPAY: ").append(brf71_REPORTENTITY.getR2_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR2_total() != null
				&& !brf71_REPORTENTITY.getR2_total().equals(existingEntity.getR2_total())) {
			fieldNames.add("Chip & PIN: -  TOTAL");
			oldChange.append("R2_TOTAL: ").append(existingEntity.getR2_total()).append("; ");
			newChange.append("R2_TOTAL: ").append(brf71_REPORTENTITY.getR2_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR3_visa() != null
				&& !brf71_REPORTENTITY.getR3_visa().equals(existingEntity.getR3_visa())) {
			fieldNames.add("Not Chip & PIN: - Visa");
			oldChange.append("R3_VISA: ").append(existingEntity.getR3_visa()).append("; ");
			newChange.append("R3_VISA: ").append(brf71_REPORTENTITY.getR3_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR3_mastercard() != null
				&& !brf71_REPORTENTITY.getR3_mastercard().equals(existingEntity.getR3_mastercard())) {
			fieldNames.add("Not Chip & PIN: - MasterCard");
			oldChange.append("R3_MASTERCARD: ").append(existingEntity.getR3_mastercard()).append("; ");
			newChange.append("R3_MASTERCARD: ").append(brf71_REPORTENTITY.getR3_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR3_amex() != null
				&& !brf71_REPORTENTITY.getR3_amex().equals(existingEntity.getR3_amex())) {
			fieldNames.add("Not Chip & PIN: - Amex");
			oldChange.append("R3_AMEX: ").append(existingEntity.getR3_amex()).append("; ");
			newChange.append("R3_AMEX: ").append(brf71_REPORTENTITY.getR3_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR3_jcb() != null
				&& !brf71_REPORTENTITY.getR3_jcb().equals(existingEntity.getR3_jcb())) {
			fieldNames.add("Not Chip & PIN: - JCB");
			oldChange.append("R3_JCB: ").append(existingEntity.getR3_jcb()).append("; ");
			newChange.append("R3_JCB: ").append(brf71_REPORTENTITY.getR3_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR3_diners() != null
				&& !brf71_REPORTENTITY.getR3_diners().equals(existingEntity.getR3_diners())) {
			fieldNames.add("Not Chip & PIN: - Diners");
			oldChange.append("R3_DINERS: ").append(existingEntity.getR3_diners()).append("; ");
			newChange.append("R3_DINERS: ").append(brf71_REPORTENTITY.getR3_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR3_unionpay() != null
				&& !brf71_REPORTENTITY.getR3_unionpay().equals(existingEntity.getR3_unionpay())) {
			fieldNames.add("Not Chip & PIN: - Union Pay");
			oldChange.append("R3_UNIONPAY: ").append(existingEntity.getR3_unionpay()).append("; ");
			newChange.append("R3_UNIONPAY: ").append(brf71_REPORTENTITY.getR3_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR3_total() != null
				&& !brf71_REPORTENTITY.getR3_total().equals(existingEntity.getR3_total())) {
			fieldNames.add("Not Chip & PIN: -  TOTAL");
			oldChange.append("R3_TOTAL: ").append(existingEntity.getR3_total()).append("; ");
			newChange.append("R3_TOTAL: ").append(brf71_REPORTENTITY.getR3_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR4_visa() != null
				&& !brf71_REPORTENTITY.getR4_visa().equals(existingEntity.getR4_visa())) {
			fieldNames.add("Prepaid Cards: - Visa");
			oldChange.append("R4_VISA: ").append(existingEntity.getR4_visa()).append("; ");
			newChange.append("R4_VISA: ").append(brf71_REPORTENTITY.getR4_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR4_mastercard() != null
				&& !brf71_REPORTENTITY.getR4_mastercard().equals(existingEntity.getR4_mastercard())) {
			fieldNames.add("Prepaid Cards: - MasterCard");
			oldChange.append("R4_MASTERCARD: ").append(existingEntity.getR4_mastercard()).append("; ");
			newChange.append("R4_MASTERCARD: ").append(brf71_REPORTENTITY.getR4_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR4_amex() != null
				&& !brf71_REPORTENTITY.getR4_amex().equals(existingEntity.getR4_amex())) {
			fieldNames.add("Prepaid Cards: - Amex");
			oldChange.append("R4_AMEX: ").append(existingEntity.getR4_amex()).append("; ");
			newChange.append("R4_AMEX: ").append(brf71_REPORTENTITY.getR4_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR4_jcb() != null
				&& !brf71_REPORTENTITY.getR4_jcb().equals(existingEntity.getR4_jcb())) {
			fieldNames.add("Prepaid Cards: - JCB");
			oldChange.append("R4_JCB: ").append(existingEntity.getR4_jcb()).append("; ");
			newChange.append("R4_JCB: ").append(brf71_REPORTENTITY.getR4_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR4_diners() != null
				&& !brf71_REPORTENTITY.getR4_diners().equals(existingEntity.getR4_diners())) {
			fieldNames.add("Prepaid Cards: - Diners");
			oldChange.append("R4_DINERS: ").append(existingEntity.getR4_diners()).append("; ");
			newChange.append("R4_DINERS: ").append(brf71_REPORTENTITY.getR4_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR4_unionpay() != null
				&& !brf71_REPORTENTITY.getR4_unionpay().equals(existingEntity.getR4_unionpay())) {
			fieldNames.add("Prepaid Cards: - Union Pay");
			oldChange.append("R4_UNIONPAY: ").append(existingEntity.getR4_unionpay()).append("; ");
			newChange.append("R4_UNIONPAY: ").append(brf71_REPORTENTITY.getR4_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR4_total() != null
				&& !brf71_REPORTENTITY.getR4_total().equals(existingEntity.getR4_total())) {
			fieldNames.add("Prepaid Cards: -  TOTAL");
			oldChange.append("R4_TOTAL: ").append(existingEntity.getR4_total()).append("; ");
			newChange.append("R4_TOTAL: ").append(brf71_REPORTENTITY.getR4_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR5_visa() != null
				&& !brf71_REPORTENTITY.getR5_visa().equals(existingEntity.getR5_visa())) {
			fieldNames.add("Chip & PIN: - Visa");
			oldChange.append("R5_VISA: ").append(existingEntity.getR5_visa()).append("; ");
			newChange.append("R5_VISA: ").append(brf71_REPORTENTITY.getR5_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR5_mastercard() != null
				&& !brf71_REPORTENTITY.getR5_mastercard().equals(existingEntity.getR5_mastercard())) {
			fieldNames.add("Chip & PIN: - MasterCard");
			oldChange.append("R5_MASTERCARD: ").append(existingEntity.getR5_mastercard()).append("; ");
			newChange.append("R5_MASTERCARD: ").append(brf71_REPORTENTITY.getR5_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR5_amex() != null
				&& !brf71_REPORTENTITY.getR5_amex().equals(existingEntity.getR5_amex())) {
			fieldNames.add("Chip & PIN: - Amex");
			oldChange.append("R5_AMEX: ").append(existingEntity.getR5_amex()).append("; ");
			newChange.append("R5_AMEX: ").append(brf71_REPORTENTITY.getR5_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR5_jcb() != null
				&& !brf71_REPORTENTITY.getR5_jcb().equals(existingEntity.getR5_jcb())) {
			fieldNames.add("Chip & PIN: - JCB");
			oldChange.append("R5_JCB: ").append(existingEntity.getR5_jcb()).append("; ");
			newChange.append("R5_JCB: ").append(brf71_REPORTENTITY.getR5_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR5_diners() != null
				&& !brf71_REPORTENTITY.getR5_diners().equals(existingEntity.getR5_diners())) {
			fieldNames.add("Chip & PIN: - Diners");
			oldChange.append("R5_DINERS: ").append(existingEntity.getR5_diners()).append("; ");
			newChange.append("R5_DINERS: ").append(brf71_REPORTENTITY.getR5_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR5_unionpay() != null
				&& !brf71_REPORTENTITY.getR5_unionpay().equals(existingEntity.getR5_unionpay())) {
			fieldNames.add("Chip & PIN: - Union Pay");
			oldChange.append("R5_UNIONPAY: ").append(existingEntity.getR5_unionpay()).append("; ");
			newChange.append("R5_UNIONPAY: ").append(brf71_REPORTENTITY.getR5_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR5_total() != null
				&& !brf71_REPORTENTITY.getR5_total().equals(existingEntity.getR5_total())) {
			fieldNames.add("Chip & PIN: -  TOTAL");
			oldChange.append("R5_TOTAL: ").append(existingEntity.getR5_total()).append("; ");
			newChange.append("R5_TOTAL: ").append(brf71_REPORTENTITY.getR5_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR6_visa() != null
				&& !brf71_REPORTENTITY.getR6_visa().equals(existingEntity.getR6_visa())) {
			fieldNames.add("Not Chip & PIN: - Visa");
			oldChange.append("R6_VISA: ").append(existingEntity.getR6_visa()).append("; ");
			newChange.append("R6_VISA: ").append(brf71_REPORTENTITY.getR6_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR6_mastercard() != null
				&& !brf71_REPORTENTITY.getR6_mastercard().equals(existingEntity.getR6_mastercard())) {
			fieldNames.add("Not Chip & PIN: - MasterCard");
			oldChange.append("R6_MASTERCARD: ").append(existingEntity.getR6_mastercard()).append("; ");
			newChange.append("R6_MASTERCARD: ").append(brf71_REPORTENTITY.getR6_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR6_amex() != null
				&& !brf71_REPORTENTITY.getR6_amex().equals(existingEntity.getR6_amex())) {
			fieldNames.add("Not Chip & PIN: - Amex");
			oldChange.append("R6_AMEX: ").append(existingEntity.getR6_amex()).append("; ");
			newChange.append("R6_AMEX: ").append(brf71_REPORTENTITY.getR6_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR6_jcb() != null
				&& !brf71_REPORTENTITY.getR6_jcb().equals(existingEntity.getR6_jcb())) {
			fieldNames.add("Not Chip & PIN: - JCB");
			oldChange.append("R6_JCB: ").append(existingEntity.getR6_jcb()).append("; ");
			newChange.append("R6_JCB: ").append(brf71_REPORTENTITY.getR6_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR6_diners() != null
				&& !brf71_REPORTENTITY.getR6_diners().equals(existingEntity.getR6_diners())) {
			fieldNames.add("Not Chip & PIN: - Diners");
			oldChange.append("R6_DINERS: ").append(existingEntity.getR6_diners()).append("; ");
			newChange.append("R6_DINERS: ").append(brf71_REPORTENTITY.getR6_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR6_unionpay() != null
				&& !brf71_REPORTENTITY.getR6_unionpay().equals(existingEntity.getR6_unionpay())) {
			fieldNames.add("Not Chip & PIN: - Union Pay");
			oldChange.append("R6_UNIONPAY: ").append(existingEntity.getR6_unionpay()).append("; ");
			newChange.append("R6_UNIONPAY: ").append(brf71_REPORTENTITY.getR6_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR6_total() != null
				&& !brf71_REPORTENTITY.getR6_total().equals(existingEntity.getR6_total())) {
			fieldNames.add("Not Chip & PIN: -  TOTAL");
			oldChange.append("R6_TOTAL: ").append(existingEntity.getR6_total()).append("; ");
			newChange.append("R6_TOTAL: ").append(brf71_REPORTENTITY.getR6_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR7_visa() != null
				&& !brf71_REPORTENTITY.getR7_visa().equals(existingEntity.getR7_visa())) {
			fieldNames.add("Credit Cards: - Visa");
			oldChange.append("R7_VISA: ").append(existingEntity.getR7_visa()).append("; ");
			newChange.append("R7_VISA: ").append(brf71_REPORTENTITY.getR7_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR7_mastercard() != null
				&& !brf71_REPORTENTITY.getR7_mastercard().equals(existingEntity.getR7_mastercard())) {
			fieldNames.add("Credit Cards: - MasterCard");
			oldChange.append("R7_MASTERCARD: ").append(existingEntity.getR7_mastercard()).append("; ");
			newChange.append("R7_MASTERCARD: ").append(brf71_REPORTENTITY.getR7_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR7_amex() != null
				&& !brf71_REPORTENTITY.getR7_amex().equals(existingEntity.getR7_amex())) {
			fieldNames.add("Credit Cards: - Amex");
			oldChange.append("R7_AMEX: ").append(existingEntity.getR7_amex()).append("; ");
			newChange.append("R7_AMEX: ").append(brf71_REPORTENTITY.getR7_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR7_jcb() != null
				&& !brf71_REPORTENTITY.getR7_jcb().equals(existingEntity.getR7_jcb())) {
			fieldNames.add("Credit Cards: - JCB");
			oldChange.append("R7_JCB: ").append(existingEntity.getR7_jcb()).append("; ");
			newChange.append("R7_JCB: ").append(brf71_REPORTENTITY.getR7_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR7_diners() != null
				&& !brf71_REPORTENTITY.getR7_diners().equals(existingEntity.getR7_diners())) {
			fieldNames.add("Credit Cards: - Diners");
			oldChange.append("R7_DINERS: ").append(existingEntity.getR7_diners()).append("; ");
			newChange.append("R7_DINERS: ").append(brf71_REPORTENTITY.getR7_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR7_unionpay() != null
				&& !brf71_REPORTENTITY.getR7_unionpay().equals(existingEntity.getR7_unionpay())) {
			fieldNames.add("Credit Cards: - Union Pay");
			oldChange.append("R7_UNIONPAY: ").append(existingEntity.getR7_unionpay()).append("; ");
			newChange.append("R7_UNIONPAY: ").append(brf71_REPORTENTITY.getR7_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR7_total() != null
				&& !brf71_REPORTENTITY.getR7_total().equals(existingEntity.getR7_total())) {
			fieldNames.add("Credit Cards: -  TOTAL");
			oldChange.append("R7_TOTAL: ").append(existingEntity.getR7_total()).append("; ");
			newChange.append("R7_TOTAL: ").append(brf71_REPORTENTITY.getR7_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR8_visa() != null
				&& !brf71_REPORTENTITY.getR8_visa().equals(existingEntity.getR8_visa())) {
			fieldNames.add("Chip & PiN: - Visa");
			oldChange.append("R8_VISA: ").append(existingEntity.getR8_visa()).append("; ");
			newChange.append("R8_VISA: ").append(brf71_REPORTENTITY.getR8_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR8_mastercard() != null
				&& !brf71_REPORTENTITY.getR8_mastercard().equals(existingEntity.getR8_mastercard())) {
			fieldNames.add("Chip & PiN: - MasterCard");
			oldChange.append("R8_MASTERCARD: ").append(existingEntity.getR8_mastercard()).append("; ");
			newChange.append("R8_MASTERCARD: ").append(brf71_REPORTENTITY.getR8_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR8_amex() != null
				&& !brf71_REPORTENTITY.getR8_amex().equals(existingEntity.getR8_amex())) {
			fieldNames.add("Chip & PiN: - Amex");
			oldChange.append("R8_AMEX: ").append(existingEntity.getR8_amex()).append("; ");
			newChange.append("R8_AMEX: ").append(brf71_REPORTENTITY.getR8_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR8_jcb() != null
				&& !brf71_REPORTENTITY.getR8_jcb().equals(existingEntity.getR8_jcb())) {
			fieldNames.add("Chip & PiN: - JCB");
			oldChange.append("R8_JCB: ").append(existingEntity.getR8_jcb()).append("; ");
			newChange.append("R8_JCB: ").append(brf71_REPORTENTITY.getR8_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR8_diners() != null
				&& !brf71_REPORTENTITY.getR8_diners().equals(existingEntity.getR8_diners())) {
			fieldNames.add("Chip & PiN: - Diners");
			oldChange.append("R8_DINERS: ").append(existingEntity.getR8_diners()).append("; ");
			newChange.append("R8_DINERS: ").append(brf71_REPORTENTITY.getR8_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR8_unionpay() != null
				&& !brf71_REPORTENTITY.getR8_unionpay().equals(existingEntity.getR8_unionpay())) {
			fieldNames.add("Chip & PiN: - Union Pay");
			oldChange.append("R8_UNIONPAY: ").append(existingEntity.getR8_unionpay()).append("; ");
			newChange.append("R8_UNIONPAY: ").append(brf71_REPORTENTITY.getR8_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR8_total() != null
				&& !brf71_REPORTENTITY.getR8_total().equals(existingEntity.getR8_total())) {
			fieldNames.add("Chip & PiN: -  TOTAL");
			oldChange.append("R8_TOTAL: ").append(existingEntity.getR8_total()).append("; ");
			newChange.append("R8_TOTAL: ").append(brf71_REPORTENTITY.getR8_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR9_visa() != null
				&& !brf71_REPORTENTITY.getR9_visa().equals(existingEntity.getR9_visa())) {
			fieldNames.add("Not Chip & PIN: - Visa");
			oldChange.append("R9_VISA: ").append(existingEntity.getR9_visa()).append("; ");
			newChange.append("R9_VISA: ").append(brf71_REPORTENTITY.getR9_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR9_mastercard() != null
				&& !brf71_REPORTENTITY.getR9_mastercard().equals(existingEntity.getR9_mastercard())) {
			fieldNames.add("Not Chip & PIN: - MasterCard");
			oldChange.append("R9_MASTERCARD: ").append(existingEntity.getR9_mastercard()).append("; ");
			newChange.append("R9_MASTERCARD: ").append(brf71_REPORTENTITY.getR9_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR9_amex() != null
				&& !brf71_REPORTENTITY.getR9_amex().equals(existingEntity.getR9_amex())) {
			fieldNames.add("Not Chip & PIN: - Amex");
			oldChange.append("R9_AMEX: ").append(existingEntity.getR9_amex()).append("; ");
			newChange.append("R9_AMEX: ").append(brf71_REPORTENTITY.getR9_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR9_jcb() != null
				&& !brf71_REPORTENTITY.getR9_jcb().equals(existingEntity.getR9_jcb())) {
			fieldNames.add("Not Chip & PIN: - JCB");
			oldChange.append("R9_JCB: ").append(existingEntity.getR9_jcb()).append("; ");
			newChange.append("R9_JCB: ").append(brf71_REPORTENTITY.getR9_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR9_diners() != null
				&& !brf71_REPORTENTITY.getR9_diners().equals(existingEntity.getR9_diners())) {
			fieldNames.add("Not Chip & PIN: - Diners");
			oldChange.append("R9_DINERS: ").append(existingEntity.getR9_diners()).append("; ");
			newChange.append("R9_DINERS: ").append(brf71_REPORTENTITY.getR9_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR9_unionpay() != null
				&& !brf71_REPORTENTITY.getR9_unionpay().equals(existingEntity.getR9_unionpay())) {
			fieldNames.add("Not Chip & PIN: - Union Pay");
			oldChange.append("R9_UNIONPAY: ").append(existingEntity.getR9_unionpay()).append("; ");
			newChange.append("R9_UNIONPAY: ").append(brf71_REPORTENTITY.getR9_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR9_total() != null
				&& !brf71_REPORTENTITY.getR9_total().equals(existingEntity.getR9_total())) {
			fieldNames.add("Not Chip & PIN: -  TOTAL");
			oldChange.append("R9_TOTAL: ").append(existingEntity.getR9_total()).append("; ");
			newChange.append("R9_TOTAL: ").append(brf71_REPORTENTITY.getR9_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR10_visa() != null
				&& !brf71_REPORTENTITY.getR10_visa().equals(existingEntity.getR10_visa())) {
			fieldNames.add("Number of Transactions: - Visa");
			oldChange.append("R10_VISA: ").append(existingEntity.getR10_visa()).append("; ");
			newChange.append("R10_VISA: ").append(brf71_REPORTENTITY.getR10_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR10_mastercard() != null
				&& !brf71_REPORTENTITY.getR10_mastercard().equals(existingEntity.getR10_mastercard())) {
			fieldNames.add("Number of Transactions: - MasterCard");
			oldChange.append("R10_MASTERCARD: ").append(existingEntity.getR10_mastercard()).append("; ");
			newChange.append("R10_MASTERCARD: ").append(brf71_REPORTENTITY.getR10_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR10_amex() != null
				&& !brf71_REPORTENTITY.getR10_amex().equals(existingEntity.getR10_amex())) {
			fieldNames.add("Number of Transactions: - Amex");
			oldChange.append("R10_AMEX: ").append(existingEntity.getR10_amex()).append("; ");
			newChange.append("R10_AMEX: ").append(brf71_REPORTENTITY.getR10_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR10_jcb() != null
				&& !brf71_REPORTENTITY.getR10_jcb().equals(existingEntity.getR10_jcb())) {
			fieldNames.add("Number of Transactions: - JCB");
			oldChange.append("R10_JCB: ").append(existingEntity.getR10_jcb()).append("; ");
			newChange.append("R10_JCB: ").append(brf71_REPORTENTITY.getR10_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR10_diners() != null
				&& !brf71_REPORTENTITY.getR10_diners().equals(existingEntity.getR10_diners())) {
			fieldNames.add("Number of Transactions: - Diners");
			oldChange.append("R10_DINERS: ").append(existingEntity.getR10_diners()).append("; ");
			newChange.append("R10_DINERS: ").append(brf71_REPORTENTITY.getR10_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR10_unionpay() != null
				&& !brf71_REPORTENTITY.getR10_unionpay().equals(existingEntity.getR10_unionpay())) {
			fieldNames.add("Number of Transactions: - Union Pay");
			oldChange.append("R10_UNIONPAY: ").append(existingEntity.getR10_unionpay()).append("; ");
			newChange.append("R10_UNIONPAY: ").append(brf71_REPORTENTITY.getR10_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR10_total() != null
				&& !brf71_REPORTENTITY.getR10_total().equals(existingEntity.getR10_total())) {
			fieldNames.add("Number of Transactions: -  TOTAL");
			oldChange.append("R10_TOTAL: ").append(existingEntity.getR10_total()).append("; ");
			newChange.append("R10_TOTAL: ").append(brf71_REPORTENTITY.getR10_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR11_visa() != null
				&& !brf71_REPORTENTITY.getR11_visa().equals(existingEntity.getR11_visa())) {
			fieldNames.add("Transactions EMV (Chip &PIN) compliant: - Visa");
			oldChange.append("R11_VISA: ").append(existingEntity.getR11_visa()).append("; ");
			newChange.append("R11_VISA: ").append(brf71_REPORTENTITY.getR11_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR11_mastercard() != null
				&& !brf71_REPORTENTITY.getR11_mastercard().equals(existingEntity.getR11_mastercard())) {
			fieldNames.add("Transactions EMV (Chip &PIN) compliant: - MasterCard");
			oldChange.append("R11_MASTERCARD: ").append(existingEntity.getR11_mastercard()).append("; ");
			newChange.append("R11_MASTERCARD: ").append(brf71_REPORTENTITY.getR11_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR11_amex() != null
				&& !brf71_REPORTENTITY.getR11_amex().equals(existingEntity.getR11_amex())) {
			fieldNames.add("Transactions EMV (Chip &PIN) compliant: - Amex");
			oldChange.append("R11_AMEX: ").append(existingEntity.getR11_amex()).append("; ");
			newChange.append("R11_AMEX: ").append(brf71_REPORTENTITY.getR11_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR11_jcb() != null
				&& !brf71_REPORTENTITY.getR11_jcb().equals(existingEntity.getR11_jcb())) {
			fieldNames.add("Transactions EMV (Chip &PIN) compliant: - JCB");
			oldChange.append("R11_JCB: ").append(existingEntity.getR11_jcb()).append("; ");
			newChange.append("R11_JCB: ").append(brf71_REPORTENTITY.getR11_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR11_diners() != null
				&& !brf71_REPORTENTITY.getR11_diners().equals(existingEntity.getR11_diners())) {
			fieldNames.add("Transactions EMV (Chip &PIN) compliant: - Diners");
			oldChange.append("R11_DINERS: ").append(existingEntity.getR11_diners()).append("; ");
			newChange.append("R11_DINERS: ").append(brf71_REPORTENTITY.getR11_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR11_unionpay() != null
				&& !brf71_REPORTENTITY.getR11_unionpay().equals(existingEntity.getR11_unionpay())) {
			fieldNames.add("Transactions EMV (Chip &PIN) compliant: - Union Pay");
			oldChange.append("R11_UNIONPAY: ").append(existingEntity.getR11_unionpay()).append("; ");
			newChange.append("R11_UNIONPAY: ").append(brf71_REPORTENTITY.getR11_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR11_total() != null
				&& !brf71_REPORTENTITY.getR11_total().equals(existingEntity.getR11_total())) {
			fieldNames.add("Transactions EMV (Chip &PIN) compliant: -  TOTAL");
			oldChange.append("R11_TOTAL: ").append(existingEntity.getR11_total()).append("; ");
			newChange.append("R11_TOTAL: ").append(brf71_REPORTENTITY.getR11_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR12_visa() != null
				&& !brf71_REPORTENTITY.getR12_visa().equals(existingEntity.getR12_visa())) {
			fieldNames.add("% Transactions EMV (Chip & PIN) compliant: - Visa");
			oldChange.append("R12_VISA: ").append(existingEntity.getR12_visa()).append("; ");
			newChange.append("R12_VISA: ").append(brf71_REPORTENTITY.getR12_visa()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR12_mastercard() != null
				&& !brf71_REPORTENTITY.getR12_mastercard().equals(existingEntity.getR12_mastercard())) {
			fieldNames.add("% Transactions EMV (Chip & PIN) compliant: - MasterCard");
			oldChange.append("R12_MASTERCARD: ").append(existingEntity.getR12_mastercard()).append("; ");
			newChange.append("R12_MASTERCARD: ").append(brf71_REPORTENTITY.getR12_mastercard()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR12_amex() != null
				&& !brf71_REPORTENTITY.getR12_amex().equals(existingEntity.getR12_amex())) {
			fieldNames.add("% Transactions EMV (Chip & PIN) compliant: - Amex");
			oldChange.append("R12_AMEX: ").append(existingEntity.getR12_amex()).append("; ");
			newChange.append("R12_AMEX: ").append(brf71_REPORTENTITY.getR12_amex()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR12_jcb() != null
				&& !brf71_REPORTENTITY.getR12_jcb().equals(existingEntity.getR12_jcb())) {
			fieldNames.add("% Transactions EMV (Chip & PIN) compliant: - JCB");
			oldChange.append("R12_JCB: ").append(existingEntity.getR12_jcb()).append("; ");
			newChange.append("R12_JCB: ").append(brf71_REPORTENTITY.getR12_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR12_diners() != null
				&& !brf71_REPORTENTITY.getR12_diners().equals(existingEntity.getR12_diners())) {
			fieldNames.add("% Transactions EMV (Chip & PIN) compliant: - Diners");
			oldChange.append("R12_DINERS: ").append(existingEntity.getR12_diners()).append("; ");
			newChange.append("R12_DINERS: ").append(brf71_REPORTENTITY.getR12_diners()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR12_unionpay() != null
				&& !brf71_REPORTENTITY.getR12_unionpay().equals(existingEntity.getR12_unionpay())) {
			fieldNames.add("% Transactions EMV (Chip & PIN) compliant: - Union Pay");
			oldChange.append("R12_UNIONPAY: ").append(existingEntity.getR12_unionpay()).append("; ");
			newChange.append("R12_UNIONPAY: ").append(brf71_REPORTENTITY.getR12_unionpay()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR12_total() != null
				&& !brf71_REPORTENTITY.getR12_total().equals(existingEntity.getR12_total())) {
			fieldNames.add("% Transactions EMV (Chip & PIN) compliant: -  TOTAL");
			oldChange.append("R12_TOTAL: ").append(existingEntity.getR12_total()).append("; ");
			newChange.append("R12_TOTAL: ").append(brf71_REPORTENTITY.getR12_total()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR16_jcb() != null
				&& !brf71_REPORTENTITY.getR16_jcb().equals(existingEntity.getR16_jcb())) {
			fieldNames.add("ATM and  Backend Systems compliant: Yes or No: - JCB");
			oldChange.append("R16_JCB: ").append(existingEntity.getR16_jcb()).append("; ");
			newChange.append("R16_JCB: ").append(brf71_REPORTENTITY.getR16_jcb()).append("; ");
			rowEdited = true;
		}
		if (brf71_REPORTENTITY.getR17_jcb() != null
				&& !brf71_REPORTENTITY.getR17_jcb().equals(existingEntity.getR17_jcb())) {
			fieldNames.add("Expected date for ATM & backend systems being fully EMV compliant: - JCB");
			oldChange.append("R17_JCB: ").append(existingEntity.getR17_jcb()).append("; ");
			newChange.append("R17_JCB: ").append(brf71_REPORTENTITY.getR17_jcb()).append("; ");
			rowEdited = true;
		}

		// Add changes to lists if any
		if (rowEdited) {
			oldValuesList.add(oldChange.toString().trim());
			newValuesList.add(newChange.toString().trim());
		}

		// Update fields in the existing entity
		existingEntity.setR1_visa(brf71_REPORTENTITY.getR1_visa());
		existingEntity.setR1_mastercard(brf71_REPORTENTITY.getR1_mastercard());
		existingEntity.setR1_amex(brf71_REPORTENTITY.getR1_amex());
		existingEntity.setR1_jcb(brf71_REPORTENTITY.getR1_jcb());
		existingEntity.setR1_diners(brf71_REPORTENTITY.getR1_diners());
		existingEntity.setR1_unionpay(brf71_REPORTENTITY.getR1_unionpay());
		existingEntity.setR1_total(brf71_REPORTENTITY.getR1_total());

		existingEntity.setR2_visa(brf71_REPORTENTITY.getR2_visa());
		existingEntity.setR2_mastercard(brf71_REPORTENTITY.getR2_mastercard());
		existingEntity.setR2_amex(brf71_REPORTENTITY.getR2_amex());
		existingEntity.setR2_jcb(brf71_REPORTENTITY.getR2_jcb());
		existingEntity.setR2_diners(brf71_REPORTENTITY.getR2_diners());
		existingEntity.setR2_unionpay(brf71_REPORTENTITY.getR2_unionpay());
		existingEntity.setR2_total(brf71_REPORTENTITY.getR2_total());

		existingEntity.setR3_visa(brf71_REPORTENTITY.getR3_visa());
		existingEntity.setR3_mastercard(brf71_REPORTENTITY.getR3_mastercard());
		existingEntity.setR3_amex(brf71_REPORTENTITY.getR3_amex());
		existingEntity.setR3_jcb(brf71_REPORTENTITY.getR3_jcb());
		existingEntity.setR3_diners(brf71_REPORTENTITY.getR3_diners());
		existingEntity.setR3_unionpay(brf71_REPORTENTITY.getR3_unionpay());
		existingEntity.setR3_total(brf71_REPORTENTITY.getR3_total());

		existingEntity.setR4_visa(brf71_REPORTENTITY.getR4_visa());
		existingEntity.setR4_mastercard(brf71_REPORTENTITY.getR4_mastercard());
		existingEntity.setR4_amex(brf71_REPORTENTITY.getR4_amex());
		existingEntity.setR4_jcb(brf71_REPORTENTITY.getR4_jcb());
		existingEntity.setR4_diners(brf71_REPORTENTITY.getR4_diners());
		existingEntity.setR4_unionpay(brf71_REPORTENTITY.getR4_unionpay());
		existingEntity.setR4_total(brf71_REPORTENTITY.getR4_total());

		existingEntity.setR5_visa(brf71_REPORTENTITY.getR5_visa());
		existingEntity.setR5_mastercard(brf71_REPORTENTITY.getR5_mastercard());
		existingEntity.setR5_amex(brf71_REPORTENTITY.getR5_amex());
		existingEntity.setR5_jcb(brf71_REPORTENTITY.getR5_jcb());
		existingEntity.setR5_diners(brf71_REPORTENTITY.getR5_diners());
		existingEntity.setR5_unionpay(brf71_REPORTENTITY.getR5_unionpay());
		existingEntity.setR5_total(brf71_REPORTENTITY.getR5_total());

		existingEntity.setR6_visa(brf71_REPORTENTITY.getR6_visa());
		existingEntity.setR6_mastercard(brf71_REPORTENTITY.getR6_mastercard());
		existingEntity.setR6_amex(brf71_REPORTENTITY.getR6_amex());
		existingEntity.setR6_jcb(brf71_REPORTENTITY.getR6_jcb());
		existingEntity.setR6_diners(brf71_REPORTENTITY.getR6_diners());
		existingEntity.setR6_unionpay(brf71_REPORTENTITY.getR6_unionpay());
		existingEntity.setR6_total(brf71_REPORTENTITY.getR6_total());

		existingEntity.setR7_visa(brf71_REPORTENTITY.getR7_visa());
		existingEntity.setR7_mastercard(brf71_REPORTENTITY.getR7_mastercard());
		existingEntity.setR7_amex(brf71_REPORTENTITY.getR7_amex());
		existingEntity.setR7_jcb(brf71_REPORTENTITY.getR7_jcb());
		existingEntity.setR7_diners(brf71_REPORTENTITY.getR7_diners());
		existingEntity.setR7_unionpay(brf71_REPORTENTITY.getR7_unionpay());
		existingEntity.setR7_total(brf71_REPORTENTITY.getR7_total());

		existingEntity.setR8_visa(brf71_REPORTENTITY.getR8_visa());
		existingEntity.setR8_mastercard(brf71_REPORTENTITY.getR8_mastercard());
		existingEntity.setR8_amex(brf71_REPORTENTITY.getR8_amex());
		existingEntity.setR8_jcb(brf71_REPORTENTITY.getR8_jcb());
		existingEntity.setR8_diners(brf71_REPORTENTITY.getR8_diners());
		existingEntity.setR8_unionpay(brf71_REPORTENTITY.getR8_unionpay());
		existingEntity.setR8_total(brf71_REPORTENTITY.getR8_total());

		existingEntity.setR9_visa(brf71_REPORTENTITY.getR9_visa());
		existingEntity.setR9_mastercard(brf71_REPORTENTITY.getR9_mastercard());
		existingEntity.setR9_amex(brf71_REPORTENTITY.getR9_amex());
		existingEntity.setR9_jcb(brf71_REPORTENTITY.getR9_jcb());
		existingEntity.setR9_diners(brf71_REPORTENTITY.getR9_diners());
		existingEntity.setR9_unionpay(brf71_REPORTENTITY.getR9_unionpay());
		existingEntity.setR9_total(brf71_REPORTENTITY.getR9_total());

		existingEntity.setR10_visa(brf71_REPORTENTITY.getR10_visa());
		existingEntity.setR10_mastercard(brf71_REPORTENTITY.getR10_mastercard());
		existingEntity.setR10_amex(brf71_REPORTENTITY.getR10_amex());
		existingEntity.setR10_jcb(brf71_REPORTENTITY.getR10_jcb());
		existingEntity.setR10_diners(brf71_REPORTENTITY.getR10_diners());
		existingEntity.setR10_unionpay(brf71_REPORTENTITY.getR10_unionpay());
		existingEntity.setR10_total(brf71_REPORTENTITY.getR10_total());

		existingEntity.setR11_visa(brf71_REPORTENTITY.getR11_visa());
		existingEntity.setR11_mastercard(brf71_REPORTENTITY.getR11_mastercard());
		existingEntity.setR11_amex(brf71_REPORTENTITY.getR11_amex());
		existingEntity.setR11_jcb(brf71_REPORTENTITY.getR11_jcb());
		existingEntity.setR11_diners(brf71_REPORTENTITY.getR11_diners());
		existingEntity.setR11_unionpay(brf71_REPORTENTITY.getR11_unionpay());
		existingEntity.setR11_total(brf71_REPORTENTITY.getR11_total());

		existingEntity.setR12_visa(brf71_REPORTENTITY.getR12_visa());
		existingEntity.setR12_mastercard(brf71_REPORTENTITY.getR12_mastercard());
		existingEntity.setR12_amex(brf71_REPORTENTITY.getR12_amex());
		existingEntity.setR12_jcb(brf71_REPORTENTITY.getR12_jcb());
		existingEntity.setR12_diners(brf71_REPORTENTITY.getR12_diners());
		existingEntity.setR12_unionpay(brf71_REPORTENTITY.getR12_unionpay());
		existingEntity.setR12_total(brf71_REPORTENTITY.getR12_total());

		existingEntity.setR16_jcb(brf71_REPORTENTITY.getR16_jcb());
		existingEntity.setR17_jcb(brf71_REPORTENTITY.getR17_jcb());

		// Save the updated entity
		bRF71_ServiceRepo.save(existingEntity);
		msg = "Updated Successfully";

		// Fetch all reports for comparison

		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("MODIFY");
		audit.setAudit_table("BRF71_SUMMARYTABLE");
		audit.setAudit_screen("MODIFY");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setOld_value(String.join("; ", oldValuesList));
		audit.setNew_value(String.join("; ", newValuesList));
		System.out.println("New Values: " + String.join("; ", newValuesList));
		audit.setField_name(String.join("; ", fieldNames));
		audit.setRemarks("Modify Successfully");

		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		mANUAL_Service_Rep.save(audit);

		return msg;
	}

	@PostMapping("/Modify069")
	@ResponseBody
	public String Modify069(HttpServletRequest req, @RequestBody YourFormData formData) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.Modify069(formData, userId, req);
	}

	@PostMapping("verify69Report")
	@ResponseBody
	public String verify69Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verify69Report(asondate, userId, req);
	}

	@PostMapping("verifyBRF300Report")
	@ResponseBody
	public String verifyBRF300Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF300Report(asondate, userId, req);
	}

	@PostMapping("verifyBRF70Report")
	@ResponseBody
	public String verifyBRF70Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF70Report(asondate, userId, req);
	}

	@PostMapping("verifyBRF71Report")

	@ResponseBody
	public String verifyBRF71Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF71Report(asondate, userId, req);
	}

	@PostMapping("verifyBRF201Report")

	@ResponseBody
	public String verifyBRF201Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF201Report(asondate, userId, req);
	}

	@PostMapping("verifyBRF202Report")

	@ResponseBody
	public String verifyBRF202Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF202Report(asondate, userId, req);
	}

	@PostMapping("verifyBRF301Report")

	@ResponseBody
	public String verifyBRF301Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF301Report(asondate, userId, req);
	}

	@PostMapping("verifyBRF108Report")

	@ResponseBody
	public String verifyBRF108Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF108Report(asondate, userId, req);
	}

	@RequestMapping(value = "Deletetobrf202", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf202(HttpServletRequest hs, @RequestParam(required = false) String r1_s_no,
			HttpServletRequest req) {
		BRF202A_entity up = brf202A_entity_repo.getsrl_no(r1_s_no);

		up.setDel_flg("Y");
		brf202A_entity_repo.delete(up);

		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF202_SUMMARYTABLE");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");
		audit.setOld_value(up.toString());

		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf69A", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf69A(HttpServletRequest hs, @RequestParam(required = false) String srl_no,
			HttpServletRequest req) {
		BRF69_SUMMARY_A_ENTITY up = brf69_SUMMARY_A_REP.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf69_SUMMARY_A_REP.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		/*
		 * // Log the details of the row to be deleted String deletedRowDetails =
		 * "Deleted Row is :" + srl_no;
		 */

		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF69_SUMMARYTABLE A ");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");
		audit.setOld_value(up.toString());

		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf69B", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf69B(HttpServletRequest hs, @RequestParam(required = false) String srl_no,
			HttpServletRequest req) {
		BRF069_SUMMARY_B_ENTITY up = brf069_SUMMARY_B_REP.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf069_SUMMARY_B_REP.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		/*
		 * // Log the details of the row to be deleted String deletedRowDetails =
		 * "Deleted Row is :" + srl_no;
		 */

		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF69_SUMMARYTABLE B");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Delete Successfully");
		audit.setOld_value(up.toString());

		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf69C", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf69C(HttpServletRequest hs, @RequestParam(required = false) String srl_no,
			HttpServletRequest req) {
		BRF069_SUMMARY_C_ENTITY up = brf069_SUMMARY_C_REP.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf069_SUMMARY_C_REP.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		/*
		 * // Log the details of the row to be deleted String deletedRowDetails =
		 * "Deleted Row is :" + srl_no;
		 */

		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF69_SUMMARYTABLE C");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");
		audit.setOld_value(up.toString());

		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf69D", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf69D(HttpServletRequest hs, @RequestParam(required = false) String srl_no,
			HttpServletRequest req) {
		BRF069_SUMMARY_D_ENTITY up = brf069_SUMMARY_D_REP.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf069_SUMMARY_D_REP.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		/*
		 * // Log the details of the row to be deleted String deletedRowDetails =
		 * "Deleted Row is :" + srl_no;
		 */

		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF69_SUMMARYTABLE D");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Delete Successfully");
		audit.setOld_value(up.toString());

		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf108", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf108(HttpServletRequest req, @RequestParam(required = false) String r1_s_no) {
		// Retrieve the entity to be deleted
		BRF108_ENTITY up = brf108_entity_repo.getsrl_no(r1_s_no);

		// Mark the entity as deleted
		up.setDel_flg("Y");
		brf108_entity_repo.delete(up);

		// Generate audit details
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF108_SUMMARYTABLE");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);

		// Return success message
		return "Deleted Successfully";
	}

	@RequestMapping(value = "Deletetobrf301", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf301(HttpServletRequest req, @RequestParam(required = false) String r1_s_no) {
		BRF301_entity up = brf301_entity_repo.getsrl_no(r1_s_no);
		up.setDel_flg("Y");
		brf301_entity_repo.delete(up);
		// Generate audit details
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF301_SUMMARYTABLE");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf201A", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf201A(HttpServletRequest req, @RequestParam(required = false) String srl_no) {
		BRF201_SUMMARY_ENTITY_A up = brf201_SUMMARY_REP_A.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf201_SUMMARY_REP_A.delete(up);

		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF201_SUMMARY_TABLE_A");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";

	}

	@RequestMapping(value = "Deletetobrf201B", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf201B(HttpServletRequest req, @RequestParam(required = false) String srl_no) {
		BRF201_SUMMARY_ENTITY_B up = brf201_SUMMARY_REP_B.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf201_SUMMARY_REP_B.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF201_SUMMARY_TABLE_B");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf201C", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf201C(HttpServletRequest req, @RequestParam(required = false) String srl_no) {
		BRF201_SUMMARY_ENTITY_C up = brf201_SUMMARY_REP_C.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf201_SUMMARY_REP_C.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF201_SUMMARY_TABLE_C");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf201D", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf201D(HttpServletRequest req, @RequestParam(required = false) String srl_no) {
		BRF201_SUMMARY_ENTITY_D up = brf201_SUMMARY_REP_D.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf201_SUMMARY_REP_D.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF201_SUMMARY_TABLE_D");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf201E", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf201E(HttpServletRequest req, @RequestParam(required = false) String srl_no) {
		BRF201_SUMMARY_ENTITY_E up = brf201_SUMMARY_REP_E.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf201_SUMMARY_REP_E.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF201_SUMMARY_TABLE_E");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf201F", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf201F(HttpServletRequest req, @RequestParam(required = false) String srl_no) {
		BRF201_SUMMARY_ENTITY_F up = brf201_SUMMARY_REP_F.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf201_SUMMARY_REP_F.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF201_SUMMARY_TABLE_F");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "Deletetobrf201G", method = RequestMethod.POST)
	@ResponseBody
	public String Deletetobrf201G(HttpServletRequest req, @RequestParam(required = false) String srl_no) {
		BRF201_SUMMARY_ENTITY_G up = brf201_SUMMARY_REP_G.getsrl_no(srl_no);
		up.setDel_flg("Y");
		brf201_SUMMARY_REP_G.delete(up);
		String auditID = sequence.generateRequestUUId();
		String user1 = (String) req.getSession().getAttribute("USERID");
		String username = (String) req.getSession().getAttribute("USERNAME");

		// Prepare audit log
		MANUAL_Service_Entity audit = new MANUAL_Service_Entity();
		audit.setAudit_date(new Date());
		audit.setEntry_time(new Date());
		audit.setEntry_user(user1);
		audit.setFunc_code("DELETE");
		audit.setAudit_table("BRF201_SUMMARY_TABLE_G");
		audit.setAudit_screen("DELETE");
		audit.setEvent_id(user1);
		audit.setEvent_name(username);
		audit.setRemarks("Deleted Successfully");

		// Capture the details of the deleted row
		audit.setOld_value("Deleted Row Details: " + up.toString()); // Customize this to format the deleted values

		// Retrieve and set additional user profile details
		UserProfile values1 = userProfileRep.getRole(user1);
		audit.setAuth_user(values1.getAuth_user());
		audit.setAuth_time(values1.getAuth_time());
		audit.setAudit_ref_no(auditID);

		// Save the audit record
		mANUAL_Service_Rep.save(audit);
		return "Delete Successfully";
	}

	@RequestMapping(value = "modifyRecord181", method = RequestMethod.POST)
	@ResponseBody
	public String modifyRecord181(@RequestParam(required = false) Date asondate,
			@ModelAttribute BRF_181_A2_REPORT_ENTITY BRF_181_A2_REPORT_ENTITY, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.modifyRecord181(asondate, BRF_181_A2_REPORT_ENTITY, userId);
	}

	@PostMapping("verifyBRF181Report")
	@ResponseBody
	public String verifyBRF181Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF181Report(asondate, userId);
	}

	@RequestMapping(value = "modifyRecord102", method = { RequestMethod.POST })
	@ResponseBody
	public String Modifybrf102(@RequestParam("fromdate") String fromdate, @RequestParam("todate") String todate,
			@RequestParam(value = "asondate", required = false) String asondate,
			@RequestParam(value = "account_number", required = false) String account_number,
			@RequestParam(value = "last_communication_date", required = false) String last_communication_date, Model md,
			HttpServletRequest req) throws IOException, ParseException {

		/*
		 * SimpleDateFormat format1 = new SimpleDateFormat("dd/MM/yyyy"); DateFormat
		 * format2 = new SimpleDateFormat("dd-MMM-yyyy"); Date date =
		 * format1.parse(todate);
		 */
		try {
			brf102ReportService.editbrf102(account_number, last_communication_date, asondate);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
		return "";
	}

	/// DONE BY KAMATCHI
	@RequestMapping(value = "modifyRecord32", method = RequestMethod.POST)
	@ResponseBody
	public String modifyRecord32(@RequestParam(required = false) Date asondate,
			@ModelAttribute BRF32_ENTITY BRF32_ENTITY, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		System.out.println("Asondate: " + asondate);
		System.out.println("BRF32_ENTITY: " + BRF32_ENTITY.getR10_stage3_provisions());
		System.out.println("BRF32_ENTITY: " + BRF32_ENTITY.getR31_borrower_name());
		System.out.println("UserId: " + userId);
		BigDecimal R16_stage3_provisionsTOTAL = BigDecimal.ZERO;

		BigDecimal R1_stage3_provisions = BRF32_ENTITY.getR1_stage3_provisions();
		BigDecimal R2_stage3_provisions = BRF32_ENTITY.getR2_stage3_provisions();
		BigDecimal R3_stage3_provisions = BRF32_ENTITY.getR3_stage3_provisions();
		BigDecimal R4_stage3_provisions = BRF32_ENTITY.getR4_stage3_provisions();
		BigDecimal R5_stage3_provisions = BRF32_ENTITY.getR5_stage3_provisions();
		BigDecimal R6_stage3_provisions = BRF32_ENTITY.getR6_stage3_provisions();
		BigDecimal R7_stage3_provisions = BRF32_ENTITY.getR7_stage3_provisions();
		BigDecimal R8_stage3_provisions = BRF32_ENTITY.getR8_stage3_provisions();
		BigDecimal R9_stage3_provisions = BRF32_ENTITY.getR9_stage3_provisions();
		BigDecimal R10_stage3_provisions = BRF32_ENTITY.getR10_stage3_provisions();
		BigDecimal R11_stage3_provisions = BRF32_ENTITY.getR11_stage3_provisions();
		BigDecimal R12_stage3_provisions = BRF32_ENTITY.getR12_stage3_provisions();
		BigDecimal R13_stage3_provisions = BRF32_ENTITY.getR13_stage3_provisions();
		BigDecimal R14_stage3_provisions = BRF32_ENTITY.getR14_stage3_provisions();
		BigDecimal R15_stage3_provisions = BRF32_ENTITY.getR15_stage3_provisions();

		// Add the BigDecimal variables
		R16_stage3_provisionsTOTAL = R16_stage3_provisionsTOTAL.add(R1_stage3_provisions)
				.add(R2_stage3_provisions).add(R3_stage3_provisions).add(R4_stage3_provisions)
				.add(R5_stage3_provisions).add(R6_stage3_provisions).add(R7_stage3_provisions)
				.add(R8_stage3_provisions).add(R9_stage3_provisions).add(R10_stage3_provisions)
				.add(R11_stage3_provisions).add(R12_stage3_provisions).add(R13_stage3_provisions)
				.add(R14_stage3_provisions).add(R15_stage3_provisions);
		BRF32_ENTITY.setR16_stage3_provisions(R16_stage3_provisionsTOTAL);
		BigDecimal R16_stage2_provisionsTOTAL = BigDecimal.ZERO;

		BigDecimal R1_stage2_provisions = BRF32_ENTITY.getR1_stage2_provisions();
		BigDecimal R2_stage2_provisions = BRF32_ENTITY.getR2_stage2_provisions();
		BigDecimal R3_stage2_provisions = BRF32_ENTITY.getR3_stage2_provisions();
		BigDecimal R4_stage2_provisions = BRF32_ENTITY.getR4_stage2_provisions();
		BigDecimal R5_stage2_provisions = BRF32_ENTITY.getR5_stage2_provisions();
		BigDecimal R6_stage2_provisions = BRF32_ENTITY.getR6_stage2_provisions();
		BigDecimal R7_stage2_provisions = BRF32_ENTITY.getR7_stage2_provisions();
		BigDecimal R8_stage2_provisions = BRF32_ENTITY.getR8_stage2_provisions();
		BigDecimal R9_stage2_provisions = BRF32_ENTITY.getR9_stage2_provisions();
		BigDecimal R10_stage2_provisions = BRF32_ENTITY.getR10_stage2_provisions();
		BigDecimal R11_stage2_provisions = BRF32_ENTITY.getR11_stage2_provisions();
		BigDecimal R12_stage2_provisions = BRF32_ENTITY.getR12_stage2_provisions();
		BigDecimal R13_stage2_provisions = BRF32_ENTITY.getR13_stage2_provisions();
		BigDecimal R14_stage2_provisions = BRF32_ENTITY.getR14_stage2_provisions();
		BigDecimal R15_stage2_provisions = BRF32_ENTITY.getR15_stage2_provisions();

		// Add the BigDecimal variables
		R16_stage2_provisionsTOTAL = R16_stage2_provisionsTOTAL.add(R1_stage2_provisions)
				.add(R2_stage2_provisions).add(R3_stage2_provisions).add(R4_stage2_provisions)
				.add(R5_stage2_provisions).add(R6_stage2_provisions).add(R7_stage2_provisions)
				.add(R8_stage2_provisions).add(R9_stage2_provisions).add(R10_stage2_provisions)
				.add(R11_stage2_provisions).add(R12_stage2_provisions).add(R13_stage2_provisions)
				.add(R14_stage2_provisions).add(R15_stage2_provisions);
		BRF32_ENTITY.setR16_stage2_provisions(R16_stage2_provisionsTOTAL);
		BigDecimal R32_stage3_provisionsTOTAL = BigDecimal.ZERO;

		BigDecimal R17_stage3_provisions = BRF32_ENTITY.getR17_stage3_provisions();
		BigDecimal R18_stage3_provisions = BRF32_ENTITY.getR18_stage3_provisions();
		BigDecimal R19_stage3_provisions = BRF32_ENTITY.getR19_stage3_provisions();
		BigDecimal R20_stage3_provisions = BRF32_ENTITY.getR20_stage3_provisions();
		BigDecimal R21_stage3_provisions = BRF32_ENTITY.getR21_stage3_provisions();
		BigDecimal R22_stage3_provisions = BRF32_ENTITY.getR22_stage3_provisions();
		BigDecimal R23_stage3_provisions = BRF32_ENTITY.getR23_stage3_provisions();
		BigDecimal R24_stage3_provisions = BRF32_ENTITY.getR24_stage3_provisions();
		BigDecimal R25_stage3_provisions = BRF32_ENTITY.getR25_stage3_provisions();
		BigDecimal R26_stage3_provisions = BRF32_ENTITY.getR26_stage3_provisions();
		BigDecimal R27_stage3_provisions = BRF32_ENTITY.getR27_stage3_provisions();
		BigDecimal R28_stage3_provisions = BRF32_ENTITY.getR28_stage3_provisions();
		BigDecimal R29_stage3_provisions = BRF32_ENTITY.getR29_stage3_provisions();
		BigDecimal R30_stage3_provisions = BRF32_ENTITY.getR30_stage3_provisions();
		BigDecimal R31_stage3_provisions = BRF32_ENTITY.getR31_stage3_provisions();

		R32_stage3_provisionsTOTAL = R32_stage3_provisionsTOTAL.add(R17_stage3_provisions)
				.add(R18_stage3_provisions).add(R19_stage3_provisions).add(R20_stage3_provisions)
				.add(R21_stage3_provisions).add(R22_stage3_provisions).add(R23_stage3_provisions)
				.add(R24_stage3_provisions).add(R25_stage3_provisions).add(R26_stage3_provisions)
				.add(R27_stage3_provisions).add(R28_stage3_provisions).add(R29_stage3_provisions)
				.add(R30_stage3_provisions).add(R31_stage3_provisions);

		BRF32_ENTITY.setR32_stage3_provisions(R32_stage3_provisionsTOTAL);

		BigDecimal R32_stage2_provisionsTOTAL = BigDecimal.ZERO;

		BigDecimal R17_stage2_provisions = BRF32_ENTITY.getR17_stage2_provisions();
		BigDecimal R18_stage2_provisions = BRF32_ENTITY.getR18_stage2_provisions();
		BigDecimal R19_stage2_provisions = BRF32_ENTITY.getR19_stage2_provisions();
		BigDecimal R20_stage2_provisions = BRF32_ENTITY.getR20_stage2_provisions();
		BigDecimal R21_stage2_provisions = BRF32_ENTITY.getR21_stage2_provisions();
		BigDecimal R22_stage2_provisions = BRF32_ENTITY.getR22_stage2_provisions();
		BigDecimal R23_stage2_provisions = BRF32_ENTITY.getR23_stage2_provisions();
		BigDecimal R24_stage2_provisions = BRF32_ENTITY.getR24_stage2_provisions();
		BigDecimal R25_stage2_provisions = BRF32_ENTITY.getR25_stage2_provisions();
		BigDecimal R26_stage2_provisions = BRF32_ENTITY.getR26_stage2_provisions();
		BigDecimal R27_stage2_provisions = BRF32_ENTITY.getR27_stage2_provisions();
		BigDecimal R28_stage2_provisions = BRF32_ENTITY.getR28_stage2_provisions();
		BigDecimal R29_stage2_provisions = BRF32_ENTITY.getR29_stage2_provisions();
		BigDecimal R30_stage2_provisions = BRF32_ENTITY.getR30_stage2_provisions();
		BigDecimal R31_stage2_provisions = BRF32_ENTITY.getR31_stage2_provisions();

		R32_stage2_provisionsTOTAL = R32_stage2_provisionsTOTAL.add(R17_stage2_provisions)
				.add(R18_stage2_provisions).add(R19_stage2_provisions).add(R20_stage2_provisions)
				.add(R21_stage2_provisions).add(R22_stage2_provisions).add(R23_stage2_provisions)
				.add(R24_stage2_provisions).add(R25_stage2_provisions).add(R26_stage2_provisions)
				.add(R27_stage2_provisions).add(R28_stage2_provisions).add(R29_stage2_provisions)
				.add(R30_stage2_provisions).add(R31_stage2_provisions);

		BRF32_ENTITY.setR32_stage2_provisions(R32_stage2_provisionsTOTAL);

		BigDecimal R48_stage2_provisionsTOTAL = BigDecimal.ZERO;

		BigDecimal R33_stage2_provisions = BRF32_ENTITY.getR33_stage2_provisions();
		BigDecimal R34_stage2_provisions = BRF32_ENTITY.getR34_stage2_provisions();
		BigDecimal R35_stage2_provisions = BRF32_ENTITY.getR35_stage2_provisions();
		BigDecimal R36_stage2_provisions = BRF32_ENTITY.getR36_stage2_provisions();
		BigDecimal R37_stage2_provisions = BRF32_ENTITY.getR37_stage2_provisions();
		BigDecimal R38_stage2_provisions = BRF32_ENTITY.getR38_stage2_provisions();
		BigDecimal R39_stage2_provisions = BRF32_ENTITY.getR39_stage2_provisions();
		BigDecimal R40_stage2_provisions = BRF32_ENTITY.getR40_stage2_provisions();
		BigDecimal R41_stage2_provisions = BRF32_ENTITY.getR41_stage2_provisions();
		BigDecimal R42_stage2_provisions = BRF32_ENTITY.getR42_stage2_provisions();
		BigDecimal R43_stage2_provisions = BRF32_ENTITY.getR43_stage2_provisions();
		BigDecimal R44_stage2_provisions = BRF32_ENTITY.getR44_stage2_provisions();
		BigDecimal R45_stage2_provisions = BRF32_ENTITY.getR45_stage2_provisions();
		BigDecimal R46_stage2_provisions = BRF32_ENTITY.getR46_stage2_provisions();
		BigDecimal R47_stage2_provisions = BRF32_ENTITY.getR47_stage2_provisions();

		R48_stage2_provisionsTOTAL = R48_stage2_provisionsTOTAL.add(R33_stage2_provisions)
				.add(R34_stage2_provisions).add(R35_stage2_provisions).add(R36_stage2_provisions)
				.add(R37_stage2_provisions).add(R38_stage2_provisions).add(R39_stage2_provisions)
				.add(R40_stage2_provisions).add(R41_stage2_provisions).add(R42_stage2_provisions)
				.add(R43_stage2_provisions).add(R44_stage2_provisions).add(R45_stage2_provisions)
				.add(R46_stage2_provisions).add(R47_stage2_provisions);

		BRF32_ENTITY.setR48_stage2_provisions(R48_stage2_provisionsTOTAL);

		BigDecimal R48_stage3_provisionsTOTAL = BigDecimal.ZERO;

		BigDecimal R33_stage3_provisions = BRF32_ENTITY.getR33_stage3_provisions();
		BigDecimal R34_stage3_provisions = BRF32_ENTITY.getR34_stage3_provisions();
		BigDecimal R35_stage3_provisions = BRF32_ENTITY.getR35_stage3_provisions();
		BigDecimal R36_stage3_provisions = BRF32_ENTITY.getR36_stage3_provisions();
		BigDecimal R37_stage3_provisions = BRF32_ENTITY.getR37_stage3_provisions();
		BigDecimal R38_stage3_provisions = BRF32_ENTITY.getR38_stage3_provisions();
		BigDecimal R39_stage3_provisions = BRF32_ENTITY.getR39_stage3_provisions();
		BigDecimal R40_stage3_provisions = BRF32_ENTITY.getR40_stage3_provisions();
		BigDecimal R41_stage3_provisions = BRF32_ENTITY.getR41_stage3_provisions();
		BigDecimal R42_stage3_provisions = BRF32_ENTITY.getR42_stage3_provisions();
		BigDecimal R43_stage3_provisions = BRF32_ENTITY.getR43_stage3_provisions();
		BigDecimal R44_stage3_provisions = BRF32_ENTITY.getR44_stage3_provisions();
		BigDecimal R45_stage3_provisions = BRF32_ENTITY.getR45_stage3_provisions();
		BigDecimal R46_stage3_provisions = BRF32_ENTITY.getR46_stage3_provisions();
		BigDecimal R47_stage3_provisions = BRF32_ENTITY.getR47_stage3_provisions();

		R48_stage3_provisionsTOTAL = R48_stage3_provisionsTOTAL.add(R33_stage3_provisions)
				.add(R34_stage3_provisions).add(R35_stage3_provisions).add(R36_stage3_provisions)
				.add(R37_stage3_provisions).add(R38_stage3_provisions).add(R39_stage3_provisions)
				.add(R40_stage3_provisions).add(R41_stage3_provisions).add(R42_stage3_provisions)
				.add(R43_stage3_provisions).add(R44_stage3_provisions).add(R45_stage3_provisions)
				.add(R46_stage3_provisions).add(R47_stage3_provisions);

		BRF32_ENTITY.setR48_stage3_provisions(R48_stage3_provisionsTOTAL);

		BigDecimal R64_stage3_provisionsTOTAL = BigDecimal.ZERO;

		BigDecimal R49_stage3_provisions = BRF32_ENTITY.getR49_stage3_provisions();
		BigDecimal R50_stage3_provisions = BRF32_ENTITY.getR50_stage3_provisions();
		BigDecimal R51_stage3_provisions = BRF32_ENTITY.getR51_stage3_provisions();
		BigDecimal R52_stage3_provisions = BRF32_ENTITY.getR52_stage3_provisions();
		BigDecimal R53_stage3_provisions = BRF32_ENTITY.getR53_stage3_provisions();
		BigDecimal R54_stage3_provisions = BRF32_ENTITY.getR54_stage3_provisions();
		BigDecimal R55_stage3_provisions = BRF32_ENTITY.getR55_stage3_provisions();
		BigDecimal R56_stage3_provisions = BRF32_ENTITY.getR56_stage3_provisions();
		BigDecimal R57_stage3_provisions = BRF32_ENTITY.getR57_stage3_provisions();
		BigDecimal R58_stage3_provisions = BRF32_ENTITY.getR58_stage3_provisions();
		BigDecimal R59_stage3_provisions = BRF32_ENTITY.getR59_stage3_provisions();
		BigDecimal R60_stage3_provisions = BRF32_ENTITY.getR60_stage3_provisions();
		BigDecimal R61_stage3_provisions = BRF32_ENTITY.getR61_stage3_provisions();
		BigDecimal R62_stage3_provisions = BRF32_ENTITY.getR62_stage3_provisions();
		BigDecimal R63_stage3_provisions = BRF32_ENTITY.getR63_stage3_provisions();

		R64_stage3_provisionsTOTAL = R64_stage3_provisionsTOTAL.add(R49_stage3_provisions)
				.add(R50_stage3_provisions).add(R51_stage3_provisions).add(R52_stage3_provisions)
				.add(R53_stage3_provisions).add(R54_stage3_provisions).add(R55_stage3_provisions)
				.add(R56_stage3_provisions).add(R57_stage3_provisions).add(R58_stage3_provisions)
				.add(R59_stage3_provisions).add(R60_stage3_provisions).add(R61_stage3_provisions)
				.add(R62_stage3_provisions).add(R63_stage3_provisions);

		BRF32_ENTITY.setR64_stage3_provisions(R64_stage3_provisionsTOTAL);
///////////////////////
		BigDecimal R64_stage2_provisionsTOTAL = BigDecimal.ZERO;

		BigDecimal R49_stage2_provisions = BRF32_ENTITY.getR49_stage2_provisions();
		BigDecimal R50_stage2_provisions = BRF32_ENTITY.getR50_stage2_provisions();
		BigDecimal R51_stage2_provisions = BRF32_ENTITY.getR51_stage2_provisions();
		BigDecimal R52_stage2_provisions = BRF32_ENTITY.getR52_stage2_provisions();
		BigDecimal R53_stage2_provisions = BRF32_ENTITY.getR53_stage2_provisions();
		BigDecimal R54_stage2_provisions = BRF32_ENTITY.getR54_stage2_provisions();
		BigDecimal R55_stage2_provisions = BRF32_ENTITY.getR55_stage2_provisions();
		BigDecimal R56_stage2_provisions = BRF32_ENTITY.getR56_stage2_provisions();
		BigDecimal R57_stage2_provisions = BRF32_ENTITY.getR57_stage2_provisions();
		BigDecimal R58_stage2_provisions = BRF32_ENTITY.getR58_stage2_provisions();
		BigDecimal R59_stage2_provisions = BRF32_ENTITY.getR59_stage2_provisions();
		BigDecimal R60_stage2_provisions = BRF32_ENTITY.getR60_stage2_provisions();
		BigDecimal R61_stage2_provisions = BRF32_ENTITY.getR61_stage2_provisions();
		BigDecimal R62_stage2_provisions = BRF32_ENTITY.getR62_stage2_provisions();
		BigDecimal R63_stage2_provisions = BRF32_ENTITY.getR63_stage2_provisions();

		R64_stage2_provisionsTOTAL = R64_stage2_provisionsTOTAL.add(R49_stage2_provisions)
				.add(R50_stage2_provisions).add(R51_stage2_provisions).add(R52_stage2_provisions)
				.add(R53_stage2_provisions).add(R54_stage2_provisions).add(R55_stage2_provisions)
				.add(R56_stage2_provisions).add(R57_stage2_provisions).add(R58_stage2_provisions)
				.add(R59_stage2_provisions).add(R60_stage2_provisions).add(R61_stage2_provisions)
				.add(R62_stage2_provisions).add(R63_stage2_provisions);

		BRF32_ENTITY.setR64_stage2_provisions(R64_stage2_provisionsTOTAL);
		return regreportServices.modifyRecord32(asondate, BRF32_ENTITY, userId);
	}

	@RequestMapping(value = "BankingbookUSD", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelAndView BankingbookUSD(HttpServletRequest hs, @RequestParam("report_date") String report_date, Model md)
			throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date parsedDate = inputFormat.parse(report_date);
		md.addAttribute("asondate", report_date);

		System.out.println(parsedDate);
		return BBUSDReportService.BRFBankingbookusdView(parsedDate);
	}

	@RequestMapping(value = "BankingbookGBP", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelAndView BankingbookGBP(HttpServletRequest hs, @RequestParam("report_date") String report_date, Model md)
			throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date parsedDate = inputFormat.parse(report_date);
		md.addAttribute("asondate", report_date);
		System.out.println(parsedDate);
		return BBGBPReportService.BRFBankingbookgbpView(parsedDate);
	}

	@RequestMapping(value = "BankingbookEUR", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelAndView BankingbookEUR(HttpServletRequest hs, @RequestParam("report_date") String report_date, Model md)
			throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date parsedDate = inputFormat.parse(report_date);
		md.addAttribute("asondate", report_date);
		System.out.println(parsedDate);
		return BBEURReportService.BRFBankingbookeurView(parsedDate);
	}

	@RequestMapping(value = "BankingbookOTHERCURRENCY", method = { RequestMethod.POST, RequestMethod.GET })
	@ResponseBody
	public ModelAndView BankingbookOTHERCURRENCY(HttpServletRequest hs, @RequestParam("report_date") String report_date,
			Model md) throws ParseException {
		SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MMM-yyyy");
		Date parsedDate = inputFormat.parse(report_date);
		md.addAttribute("asondate", report_date);
		System.out.println(parsedDate);
		return BBOTHERCURRENCYReportService.BRFBankingbookothercurrencyView(parsedDate);
	}

	@RequestMapping(value = "/DownloadconsolidateBANKINGBOOK", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public ResponseEntity<InputStreamResource> DownloadconsolidateUSD(HttpServletResponse response,
			@RequestParam("todate") String todate) {

		logger.info("Getting download File :" + todate);

		try {
			File repfile = BBUSDReportService.getconsolidateFile(todate);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
			headers.setContentDispositionFormData("attachment", repfile.getName());

			InputStreamResource resource = new InputStreamResource(new FileInputStream(repfile));

			return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.contentLength(repfile.length()).body(resource);
		} catch (IOException | SQLException | JRException e) {
			logger.error("Error occurred while processing the file download: " + e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@RequestMapping(value = "modifyRecord66", method = RequestMethod.POST)
	@ResponseBody
	public String modifyRecord66(@RequestParam(required = false) Date asondate,
			@ModelAttribute BRF66_Entity BRF66_Entity, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.modifyRecord66(asondate, BRF66_Entity, userId);
	}

	@PostMapping("verifyBRF32Report")
	@ResponseBody
	public String verifyBRF32Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF32Report(asondate, userId);
	}

	@PostMapping("verifyBRF66Report")
	@ResponseBody
	public String verifyBRF66Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF66Report(asondate, userId);
	}

	@PostMapping("verifyBRF102Report")
	@ResponseBody
	public String verifyBRF102Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF102Report(asondate, userId);
	}

	@PostMapping("verifyBRF05Report")
	@ResponseBody
	public String verifyBRF05Report(@RequestParam(required = false) Date asondate, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		return regreportServices.verifyBRF05Report(asondate, userId);
	}

///DONE BY KAMATCHI
	@RequestMapping(value = "modifyRecord103", method = RequestMethod.POST)
	@ResponseBody
	public String modifyRecord103(@RequestParam(required = false) Date asondate,
			@ModelAttribute BRF103_ENTITY BRF103_ENTITY, HttpServletRequest req) {
		String userId = (String) req.getSession().getAttribute("USERID");
		System.out.println("Asondate: " + asondate);

		// Directly set R5_ACCOUNTS_AED_DORMANT to the value of R5_ACCOUNTS_AED_ABOVE
		BigDecimal R5_ACCOUNTS_AED_ABOVE = BRF103_ENTITY.getR5_ACCOUNTS_AED_ABOVE() != null
				? BRF103_ENTITY.getR5_ACCOUNTS_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR5_ACCOUNTS_AED_DORMANT(R5_ACCOUNTS_AED_ABOVE);

		BigDecimal R10_ACCOUNTS_AED_ABOVE = BRF103_ENTITY.getR10_ACCOUNTS_AED_ABOVE() != null
				? BRF103_ENTITY.getR10_ACCOUNTS_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR10_ACCOUNTS_AED_DORMANT(R10_ACCOUNTS_AED_ABOVE);

		BigDecimal R15_ACCOUNTS_AED_ABOVE = BRF103_ENTITY.getR15_ACCOUNTS_AED_ABOVE() != null
				? BRF103_ENTITY.getR15_ACCOUNTS_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR15_ACCOUNTS_AED_DORMANT(R15_ACCOUNTS_AED_ABOVE);

		BigDecimal R21_ACCOUNTS_AED_ABOVE = BRF103_ENTITY.getR21_ACCOUNTS_AED_ABOVE() != null
				? BRF103_ENTITY.getR21_ACCOUNTS_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR21_ACCOUNTS_AED_DORMANT(R21_ACCOUNTS_AED_ABOVE);
		/////
		BigDecimal R5_AMOUNT_AED_ABOVE = BRF103_ENTITY.getR5_AMOUNT_AED_ABOVE() != null
				? BRF103_ENTITY.getR5_AMOUNT_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR5_AMOUNT_AED_DORMANT(R5_AMOUNT_AED_ABOVE);

		BigDecimal R10_AMOUNT_AED_ABOVE = BRF103_ENTITY.getR10_AMOUNT_AED_ABOVE() != null
				? BRF103_ENTITY.getR10_AMOUNT_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR10_AMOUNT_AED_DORMANT(R10_AMOUNT_AED_ABOVE);

		BigDecimal R15_AMOUNT_AED_ABOVE = BRF103_ENTITY.getR15_AMOUNT_AED_ABOVE() != null
				? BRF103_ENTITY.getR15_AMOUNT_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR15_AMOUNT_AED_DORMANT(R15_AMOUNT_AED_ABOVE);

		BigDecimal R21_AMOUNT_AED_ABOVE = BRF103_ENTITY.getR21_AMOUNT_AED_ABOVE() != null
				? BRF103_ENTITY.getR21_AMOUNT_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR21_AMOUNT_AED_DORMANT(R21_AMOUNT_AED_ABOVE);
		//////
		BigDecimal R5_ACCOUNTS_FCY_ABOVE = BRF103_ENTITY.getR5_ACCOUNTS_FCY_ABOVE() != null
				? BRF103_ENTITY.getR5_ACCOUNTS_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR5_ACCOUNTS_FCY_DORMANT(R5_ACCOUNTS_FCY_ABOVE);

		BigDecimal R10_ACCOUNTS_FCY_ABOVE = BRF103_ENTITY.getR10_ACCOUNTS_FCY_ABOVE() != null
				? BRF103_ENTITY.getR10_ACCOUNTS_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR10_ACCOUNTS_FCY_DORMANT(R10_ACCOUNTS_FCY_ABOVE);

		BigDecimal R15_ACCOUNTS_FCY_ABOVE = BRF103_ENTITY.getR15_ACCOUNTS_FCY_ABOVE() != null
				? BRF103_ENTITY.getR15_ACCOUNTS_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR15_ACCOUNTS_FCY_DORMANT(R15_ACCOUNTS_FCY_ABOVE);

		BigDecimal R21_ACCOUNTS_FCY_ABOVE = BRF103_ENTITY.getR21_ACCOUNTS_FCY_ABOVE() != null
				? BRF103_ENTITY.getR21_ACCOUNTS_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR21_ACCOUNTS_FCY_DORMANT(R21_ACCOUNTS_FCY_ABOVE);
		/////
		BigDecimal R5_AMOUNT_FCY_ABOVE = BRF103_ENTITY.getR5_AMOUNT_FCY_ABOVE() != null
				? BRF103_ENTITY.getR5_AMOUNT_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR5_AMOUNT_FCY_DORMANT(R5_AMOUNT_FCY_ABOVE);

		BigDecimal R10_AMOUNT_FCY_ABOVE = BRF103_ENTITY.getR10_AMOUNT_FCY_ABOVE() != null
				? BRF103_ENTITY.getR10_AMOUNT_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR10_AMOUNT_FCY_DORMANT(R10_AMOUNT_FCY_ABOVE);

		BigDecimal R15_AMOUNT_FCY_ABOVE = BRF103_ENTITY.getR15_AMOUNT_FCY_ABOVE() != null
				? BRF103_ENTITY.getR15_AMOUNT_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR15_AMOUNT_FCY_DORMANT(R15_AMOUNT_FCY_ABOVE);

		BigDecimal R21_AMOUNT_FCY_ABOVE = BRF103_ENTITY.getR21_AMOUNT_FCY_ABOVE() != null
				? BRF103_ENTITY.getR21_AMOUNT_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR21_AMOUNT_FCY_DORMANT(R21_AMOUNT_FCY_ABOVE);

		/// row11
		BigDecimal R2_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR2_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR2_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR2_ACCOUNTS_AED_DORMANT(R2_ACCOUNTS_AED_5YEAR);

		BigDecimal R2_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR2_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR2_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR2_AMOUNT_AED_DORMANT(R2_AMOUNT_AED_5YEAR);

		BigDecimal R2_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR2_ACCOUNTS_FCY_5YEAR() != null
				? BRF103_ENTITY.getR2_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR2_ACCOUNTS_FCY_DORMANT(R2_ACCOUNTS_FCY_5YEAR);

		BigDecimal R2_AMOUNT_FCY_5YEAR = BRF103_ENTITY.getR2_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR2_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR2_AMOUNT_FCY_DORMANT(R2_AMOUNT_FCY_5YEAR);

		//// row12
		BigDecimal R3_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR3_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR3_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR3_ACCOUNTS_AED_DORMANT(R3_ACCOUNTS_AED_5YEAR);

		BigDecimal R3_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR3_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR3_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR3_AMOUNT_AED_DORMANT(R3_AMOUNT_AED_5YEAR);

		BigDecimal R3_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR3_ACCOUNTS_FCY_5YEAR() != null
				? BRF103_ENTITY.getR3_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR3_ACCOUNTS_FCY_DORMANT(R3_ACCOUNTS_FCY_5YEAR);

		BigDecimal R3_AMOUNT_FCY_5YEAR = BRF103_ENTITY.getR3_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR3_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR3_AMOUNT_FCY_DORMANT(R3_AMOUNT_FCY_5YEAR);

		/// row13

		BigDecimal R4_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR4_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR4_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR4_ACCOUNTS_AED_DORMANT(R4_ACCOUNTS_AED_5YEAR);

		BigDecimal R4_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR4_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR4_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR4_AMOUNT_AED_DORMANT(R4_AMOUNT_AED_5YEAR);

		BigDecimal R4_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR4_ACCOUNTS_FCY_5YEAR() != null
				? BRF103_ENTITY.getR4_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR4_ACCOUNTS_FCY_DORMANT(R4_ACCOUNTS_FCY_5YEAR);

		BigDecimal R4_AMOUNT_FCY_5YEAR = BRF103_ENTITY.getR4_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR4_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR4_AMOUNT_FCY_DORMANT(R4_AMOUNT_FCY_5YEAR);

		/// row16

		BigDecimal R7_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR7_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR7_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR7_ACCOUNTS_AED_DORMANT(R7_ACCOUNTS_AED_5YEAR);

		BigDecimal R7_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR7_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR7_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR7_AMOUNT_AED_DORMANT(R7_AMOUNT_AED_5YEAR);

		BigDecimal R7_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR7_ACCOUNTS_FCY_5YEAR() != null
				? BRF103_ENTITY.getR7_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR7_ACCOUNTS_FCY_DORMANT(R7_ACCOUNTS_FCY_5YEAR);

		BigDecimal R7_AMOUNT_FCY_5YEAR = BRF103_ENTITY.getR7_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR7_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR7_AMOUNT_FCY_DORMANT(R7_AMOUNT_FCY_5YEAR);

///row17

		BigDecimal R8_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR8_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR8_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR8_ACCOUNTS_AED_DORMANT(R8_ACCOUNTS_AED_5YEAR);

		BigDecimal R8_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR8_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR8_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR8_AMOUNT_AED_DORMANT(R8_AMOUNT_AED_5YEAR);

		BigDecimal R8_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR8_ACCOUNTS_FCY_5YEAR() != null
				? BRF103_ENTITY.getR8_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR8_ACCOUNTS_FCY_DORMANT(R8_ACCOUNTS_FCY_5YEAR);

		BigDecimal R8_AMOUNT_FCY_5YEAR = BRF103_ENTITY.getR8_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR8_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR8_AMOUNT_FCY_DORMANT(R8_AMOUNT_FCY_5YEAR);

		/// row18
		BigDecimal R9_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR9_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR9_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR9_ACCOUNTS_AED_DORMANT(R9_ACCOUNTS_AED_5YEAR);

		BigDecimal R9_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR9_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR9_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR9_AMOUNT_AED_DORMANT(R9_AMOUNT_AED_5YEAR);

		BigDecimal R9_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR9_ACCOUNTS_FCY_ABOVE() != null
				? BRF103_ENTITY.getR9_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR9_ACCOUNTS_FCY_DORMANT(R9_ACCOUNTS_FCY_5YEAR);

		BigDecimal R9_AMOUNT_FCY_5YEAR = BRF103_ENTITY.getR9_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR9_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR9_AMOUNT_FCY_DORMANT(R9_AMOUNT_FCY_5YEAR);

		/// row21
		BigDecimal R12_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR12_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR12_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR12_ACCOUNTS_AED_DORMANT(R12_ACCOUNTS_AED_5YEAR);

		BigDecimal R12_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR12_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR12_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR12_AMOUNT_AED_DORMANT(R12_AMOUNT_AED_5YEAR);

		BigDecimal R12_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR12_ACCOUNTS_FCY_5YEAR() != null
				? BRF103_ENTITY.getR12_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR12_ACCOUNTS_FCY_DORMANT(R12_ACCOUNTS_FCY_5YEAR);

		BigDecimal R12_AMOUNT_FCY_5YEAR = BRF103_ENTITY.getR12_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR12_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR12_AMOUNT_FCY_DORMANT(R12_AMOUNT_FCY_5YEAR);

		/// row23
		BigDecimal R14_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR14_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR14_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR14_ACCOUNTS_AED_DORMANT(R14_ACCOUNTS_AED_5YEAR);

		BigDecimal R14_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR14_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR14_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR14_AMOUNT_AED_DORMANT(R14_AMOUNT_AED_5YEAR);

		BigDecimal R14_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR14_ACCOUNTS_FCY_5YEAR() != null
				? BRF103_ENTITY.getR14_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR14_ACCOUNTS_FCY_DORMANT(R14_ACCOUNTS_FCY_5YEAR);

		BigDecimal R14_AMOUNT_FCY_5YEAR = BRF103_ENTITY.getR14_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR14_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR14_AMOUNT_FCY_DORMANT(R14_AMOUNT_FCY_5YEAR);

		//// r26
		BigDecimal R26_ACCOUNTS_AED_ABOVE = BRF103_ENTITY.getR26_ACCOUNTS_AED_ABOVE() != null
				? BRF103_ENTITY.getR26_ACCOUNTS_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR26_ACCOUNTS_AED_DORMANT(R26_ACCOUNTS_AED_ABOVE);

		BigDecimal R26_AMOUNT_AED_ABOVE = BRF103_ENTITY.getR26_AMOUNT_AED_ABOVE() != null
				? BRF103_ENTITY.getR26_AMOUNT_AED_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR26_AMOUNT_AED_ABOVE(R26_AMOUNT_AED_ABOVE);

		BigDecimal R26_ACCOUNTS_FCY_ABOVE = BRF103_ENTITY.getR26_ACCOUNTS_FCY_ABOVE() != null
				? BRF103_ENTITY.getR26_ACCOUNTS_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR26_ACCOUNTS_FCY_DORMANT(R26_ACCOUNTS_FCY_ABOVE);

		BigDecimal R26_AMOUNT_FCY_ABOVE = BRF103_ENTITY.getR26_AMOUNT_FCY_ABOVE() != null
				? BRF103_ENTITY.getR26_AMOUNT_FCY_ABOVE()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR26_AMOUNT_FCY_DORMANT(R26_AMOUNT_FCY_ABOVE);
		/// R24
		BigDecimal R24_ACCOUNTS_AED_5YEAR = BRF103_ENTITY.getR24_ACCOUNTS_AED_5YEAR() != null
				? BRF103_ENTITY.getR24_ACCOUNTS_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR24_ACCOUNTS_AED_DORMANT(R24_ACCOUNTS_AED_5YEAR);

		BigDecimal R24_AMOUNT_AED_5YEAR = BRF103_ENTITY.getR24_AMOUNT_AED_5YEAR() != null
				? BRF103_ENTITY.getR24_AMOUNT_AED_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR24_AMOUNT_AED_DORMANT(R24_AMOUNT_AED_5YEAR);

		BigDecimal R24_ACCOUNTS_FCY_5YEAR = BRF103_ENTITY.getR24_ACCOUNTS_FCY_5YEAR() != null
				? BRF103_ENTITY.getR24_ACCOUNTS_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR24_ACCOUNTS_FCY_DORMANT(R24_ACCOUNTS_FCY_5YEAR);

		BigDecimal R24_AMOUNT_FCY_DORMANT = BRF103_ENTITY.getR24_AMOUNT_FCY_5YEAR() != null
				? BRF103_ENTITY.getR24_AMOUNT_FCY_5YEAR()
				: BigDecimal.ZERO;
		BRF103_ENTITY.setR24_AMOUNT_FCY_DORMANT(R24_AMOUNT_FCY_DORMANT);

		/// ADD
		BigDecimal r17_ACCOUNTS_AED_DORMANT = BRF103_ENTITY.getR17_ACCOUNTS_AED_2YEAR()
				.add(BRF103_ENTITY.getR17_ACCOUNTS_AED_3YEAR()).add(BRF103_ENTITY.getR17_ACCOUNTS_AED_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR17_ACCOUNTS_AED_DORMANT(r17_ACCOUNTS_AED_DORMANT);
		BigDecimal R18_ACCOUNTS_AED_DORMANT = BRF103_ENTITY.getR18_ACCOUNTS_AED_2YEAR()
				.add(BRF103_ENTITY.getR18_ACCOUNTS_AED_3YEAR()).add(BRF103_ENTITY.getR18_ACCOUNTS_AED_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR18_ACCOUNTS_AED_DORMANT(R18_ACCOUNTS_AED_DORMANT);
		BigDecimal R19_ACCOUNTS_AED_DORMANT = BRF103_ENTITY.getR19_ACCOUNTS_AED_2YEAR()
				.add(BRF103_ENTITY.getR19_ACCOUNTS_AED_3YEAR()).add(BRF103_ENTITY.getR19_ACCOUNTS_AED_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR19_ACCOUNTS_AED_DORMANT(R19_ACCOUNTS_AED_DORMANT);
		BigDecimal R19_AMOUNT_AED_DORMANT = BRF103_ENTITY.getR19_AMOUNT_AED_2YEAR()
				.add(BRF103_ENTITY.getR19_AMOUNT_AED_3YEAR()).add(BRF103_ENTITY.getR19_AMOUNT_AED_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR19_AMOUNT_AED_DORMANT(R19_AMOUNT_AED_DORMANT);
		BigDecimal R18_AMOUNT_AED_DORMANT = BRF103_ENTITY.getR18_AMOUNT_AED_2YEAR()
				.add(BRF103_ENTITY.getR18_AMOUNT_AED_3YEAR()).add(BRF103_ENTITY.getR18_AMOUNT_AED_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR18_AMOUNT_AED_DORMANT(R18_AMOUNT_AED_DORMANT);
		BigDecimal R17_AMOUNT_AED_DORMANT = BRF103_ENTITY.getR17_AMOUNT_AED_2YEAR()
				.add(BRF103_ENTITY.getR17_AMOUNT_AED_3YEAR()).add(BRF103_ENTITY.getR17_AMOUNT_AED_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR17_AMOUNT_AED_DORMANT(R17_AMOUNT_AED_DORMANT);
		BigDecimal R17_AMOUNT_FCY_DORMANT = BRF103_ENTITY.getR17_AMOUNT_FCY_2YEAR()
				.add(BRF103_ENTITY.getR17_AMOUNT_FCY_3YEAR()).add(BRF103_ENTITY.getR17_AMOUNT_FCY_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR17_AMOUNT_FCY_DORMANT(R17_AMOUNT_FCY_DORMANT);
		BigDecimal R18_AMOUNT_FCY_DORMANT = BRF103_ENTITY.getR18_AMOUNT_FCY_2YEAR()
				.add(BRF103_ENTITY.getR18_AMOUNT_FCY_3YEAR()).add(BRF103_ENTITY.getR18_AMOUNT_FCY_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR18_AMOUNT_FCY_DORMANT(R18_AMOUNT_FCY_DORMANT);
		BigDecimal R19_AMOUNT_FCY_DORMANT = BRF103_ENTITY.getR19_AMOUNT_FCY_2YEAR()
				.add(BRF103_ENTITY.getR19_AMOUNT_FCY_3YEAR()).add(BRF103_ENTITY.getR19_AMOUNT_FCY_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR19_AMOUNT_FCY_DORMANT(R19_AMOUNT_FCY_DORMANT);
		BigDecimal R19_ACCOUNTS_FCY_DORMANT = BRF103_ENTITY.getR19_ACCOUNTS_FCY_2YEAR()
				.add(BRF103_ENTITY.getR19_ACCOUNTS_FCY_3YEAR()).add(BRF103_ENTITY.getR19_ACCOUNTS_FCY_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR19_ACCOUNTS_FCY_DORMANT(R19_ACCOUNTS_FCY_DORMANT);
		BigDecimal R18_ACCOUNTS_FCY_DORMANT = BRF103_ENTITY.getR18_ACCOUNTS_FCY_2YEAR()
				.add(BRF103_ENTITY.getR18_ACCOUNTS_FCY_3YEAR()).add(BRF103_ENTITY.getR18_ACCOUNTS_FCY_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR18_ACCOUNTS_FCY_DORMANT(R18_ACCOUNTS_FCY_DORMANT);
		BigDecimal R17_ACCOUNTS_FCY_DORMANT = BRF103_ENTITY.getR17_ACCOUNTS_FCY_2YEAR()
				.add(BRF103_ENTITY.getR17_ACCOUNTS_FCY_3YEAR()).add(BRF103_ENTITY.getR17_ACCOUNTS_FCY_5YEAR());

		// Set the calculated value to the target field
		BRF103_ENTITY.setR17_ACCOUNTS_FCY_DORMANT(R17_ACCOUNTS_FCY_DORMANT);

		BigDecimal r2ACCOUNTS = BRF103_ENTITY.getR2_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR2_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r3ACCOUNTS = BRF103_ENTITY.getR3_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR3_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r4ACCOUNTS = BRF103_ENTITY.getR4_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR4_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r5ACCOUNTS = BRF103_ENTITY.getR5_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR5_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R1_ACCOUNTS_AED_DORMANT = r2ACCOUNTS.add(r3ACCOUNTS).add(r4ACCOUNTS).add(r5ACCOUNTS);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR1_ACCOUNTS_AED_DORMANT(R1_ACCOUNTS_AED_DORMANT);

		BigDecimal r2AMOUNT = BRF103_ENTITY.getR2_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR2_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r3AMOUNT = BRF103_ENTITY.getR3_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR3_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r4AMOUNT = BRF103_ENTITY.getR4_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR4_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r5AMOUNT = BRF103_ENTITY.getR5_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR5_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R1_AMOUNT_AED_DORMANT = r2AMOUNT.add(r3AMOUNT).add(r4AMOUNT).add(r5AMOUNT);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR1_AMOUNT_AED_DORMANT(R1_AMOUNT_AED_DORMANT);

		///////////////

		BigDecimal r2Amount = BRF103_ENTITY.getR2_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR2_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r3Amount = BRF103_ENTITY.getR3_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR3_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r4Amount = BRF103_ENTITY.getR4_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR4_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r5Amount = BRF103_ENTITY.getR5_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR5_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R1_AMOUNT_FCY_DORMANT = r2Amount.add(r3Amount).add(r4Amount).add(r5Amount);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR1_AMOUNT_FCY_DORMANT(R1_AMOUNT_FCY_DORMANT);

		BigDecimal r2ACCOUNTS1 = BRF103_ENTITY.getR2_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR2_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r3ACCOUNTS1 = BRF103_ENTITY.getR3_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR3_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r4ACCOUNTS1 = BRF103_ENTITY.getR4_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR4_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r5ACCOUNTS1 = BRF103_ENTITY.getR5_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR5_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R1_ACCOUNTS_FCY_DORMANT = r2ACCOUNTS1.add(r3ACCOUNTS1).add(r4ACCOUNTS1).add(r5ACCOUNTS1);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR1_ACCOUNTS_FCY_DORMANT(R1_ACCOUNTS_FCY_DORMANT);

		////////////// R6
		BigDecimal r7_ACCOUNTS = BRF103_ENTITY.getR7_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR7_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r8_ACCOUNTS = BRF103_ENTITY.getR8_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR8_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r9_ACCOUNTS = BRF103_ENTITY.getR9_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR9_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r10_ACCOUNTS = BRF103_ENTITY.getR10_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR10_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R6_ACCOUNTS_AED_DORMANT = r7_ACCOUNTS.add(r8_ACCOUNTS).add(r9_ACCOUNTS).add(r10_ACCOUNTS);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR6_ACCOUNTS_AED_DORMANT(R6_ACCOUNTS_AED_DORMANT);

		BigDecimal r7_AMOUNT = BRF103_ENTITY.getR7_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR7_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r8_AMOUNT = BRF103_ENTITY.getR8_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR8_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r9_AMOUNT = BRF103_ENTITY.getR9_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR9_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r10_AMOUNT = BRF103_ENTITY.getR10_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR10_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R6_AMOUNT_AED_DORMANT = r7_AMOUNT.add(r8_AMOUNT).add(r9_AMOUNT).add(r10_AMOUNT);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR6_AMOUNT_AED_DORMANT(R6_AMOUNT_AED_DORMANT);

		BigDecimal r7_ACCOUNTS1 = BRF103_ENTITY.getR7_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR7_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r8_ACCOUNTS1 = BRF103_ENTITY.getR8_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR8_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r9_ACCOUNTS1 = BRF103_ENTITY.getR9_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR9_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r10_ACCOUNTS1 = BRF103_ENTITY.getR10_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR10_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R6_ACCOUNTS_FCY_DORMANT = r7_ACCOUNTS1.add(r8_ACCOUNTS1).add(r9_ACCOUNTS1).add(r10_ACCOUNTS1);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR6_ACCOUNTS_FCY_DORMANT(R6_ACCOUNTS_FCY_DORMANT);

		BigDecimal r7_AMOUNT1 = BRF103_ENTITY.getR7_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR7_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r8_AMOUNT1 = BRF103_ENTITY.getR8_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR8_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r9_AMOUNT1 = BRF103_ENTITY.getR9_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR9_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r10_AMOUNT1 = BRF103_ENTITY.getR10_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR10_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R6_AMOUNT_FCY_DORMANT = r7_AMOUNT1.add(r8_AMOUNT1).add(r9_AMOUNT1).add(r10_AMOUNT1);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR6_AMOUNT_FCY_DORMANT(R6_AMOUNT_FCY_DORMANT);

		//////////////// R11

		// Handling nulls for ACCOUNTS_AED_DORMANT
		BigDecimal r12_ACCOUNTS_AED = BRF103_ENTITY.getR12_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR12_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r13_ACCOUNTS_AED = BRF103_ENTITY.getR13_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR13_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r14_ACCOUNTS_AED = BRF103_ENTITY.getR14_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR14_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r15_ACCOUNTS_AED = BRF103_ENTITY.getR15_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR15_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R11_ACCOUNTS_AED_DORMANT = r12_ACCOUNTS_AED.add(r13_ACCOUNTS_AED).add(r14_ACCOUNTS_AED)
				.add(r15_ACCOUNTS_AED);
		BRF103_ENTITY.setR11_ACCOUNTS_AED_DORMANT(R11_ACCOUNTS_AED_DORMANT);

		// Handling nulls for AMOUNT_AED_DORMANT
		BigDecimal r12_AMOUNT_AED = BRF103_ENTITY.getR12_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR12_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r13_AMOUNT_AED = BRF103_ENTITY.getR13_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR13_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r14_AMOUNT_AED = BRF103_ENTITY.getR14_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR14_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r15_AMOUNT_AED = BRF103_ENTITY.getR15_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR15_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R11_AMOUNT_AED_DORMANT = r12_AMOUNT_AED.add(r13_AMOUNT_AED).add(r14_AMOUNT_AED).add(r15_AMOUNT_AED);
		BRF103_ENTITY.setR11_AMOUNT_AED_DORMANT(R11_AMOUNT_AED_DORMANT);

		// Handling nulls for AMOUNT_FCY_DORMANT
		BigDecimal r12_AMOUNT_FCY = BRF103_ENTITY.getR12_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR12_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r13_AMOUNT_FCY = BRF103_ENTITY.getR13_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR13_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r14_AMOUNT_FCY = BRF103_ENTITY.getR14_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR14_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r15_AMOUNT_FCY = BRF103_ENTITY.getR15_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR15_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R11_AMOUNT_FCY_DORMANT = r12_AMOUNT_FCY.add(r13_AMOUNT_FCY).add(r14_AMOUNT_FCY).add(r15_AMOUNT_FCY);
		BRF103_ENTITY.setR11_AMOUNT_FCY_DORMANT(R11_AMOUNT_FCY_DORMANT);

		// Handling nulls for ACCOUNTS_FCY_DORMANT
		BigDecimal r12_ACCOUNTS_FCY = BRF103_ENTITY.getR12_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR12_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r13_ACCOUNTS_FCY = BRF103_ENTITY.getR13_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR13_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r14_ACCOUNTS_FCY = BRF103_ENTITY.getR14_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR14_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r15_ACCOUNTS_FCY = BRF103_ENTITY.getR15_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR15_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R11_ACCOUNTS_FCY_DORMANT = r12_ACCOUNTS_FCY.add(r13_ACCOUNTS_FCY).add(r14_ACCOUNTS_FCY)
				.add(r15_ACCOUNTS_FCY);
		BRF103_ENTITY.setR11_ACCOUNTS_FCY_DORMANT(R11_ACCOUNTS_FCY_DORMANT);

		/// R16
		// Handling nulls for ACCOUNTS_AED_DORMANT
		BigDecimal r17_ACCOUNTS_AED = BRF103_ENTITY.getR17_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR17_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r18_ACCOUNTS_AED = BRF103_ENTITY.getR18_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR18_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r19_ACCOUNTS_AED = BRF103_ENTITY.getR19_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR19_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r20_ACCOUNTS_AED = BRF103_ENTITY.getR20_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR20_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r21_ACCOUNTS_AED = BRF103_ENTITY.getR21_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR21_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R16_ACCOUNTS_AED_DORMANT = r17_ACCOUNTS_AED.add(r18_ACCOUNTS_AED).add(r19_ACCOUNTS_AED)
				.add(r20_ACCOUNTS_AED).add(r21_ACCOUNTS_AED);
		BRF103_ENTITY.setR16_ACCOUNTS_AED_DORMANT(R16_ACCOUNTS_AED_DORMANT);

		// Handling nulls for AMOUNT_AED_DORMANT
		BigDecimal r17_AMOUNT_AED = BRF103_ENTITY.getR17_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR17_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r18_AMOUNT_AED = BRF103_ENTITY.getR18_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR18_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r19_AMOUNT_AED = BRF103_ENTITY.getR19_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR19_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r20_AMOUNT_AED = BRF103_ENTITY.getR20_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR20_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r21_AMOUNT_AED = BRF103_ENTITY.getR21_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR21_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R16_AMOUNT_AED_DORMANT = r17_AMOUNT_AED.add(r18_AMOUNT_AED).add(r19_AMOUNT_AED).add(r20_AMOUNT_AED)
				.add(r21_AMOUNT_AED);
		BRF103_ENTITY.setR16_AMOUNT_AED_DORMANT(R16_AMOUNT_AED_DORMANT);

		// Handling nulls for ACCOUNTS_FCY_DORMANT
		BigDecimal r17_ACCOUNTS_FCY = BRF103_ENTITY.getR17_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR17_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r18_ACCOUNTS_FCY = BRF103_ENTITY.getR18_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR18_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r19_ACCOUNTS_FCY = BRF103_ENTITY.getR19_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR19_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r20_ACCOUNTS_FCY = BRF103_ENTITY.getR20_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR20_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r21_ACCOUNTS_FCY = BRF103_ENTITY.getR21_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR21_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R16_ACCOUNTS_FCY_DORMANT = r17_ACCOUNTS_FCY.add(r18_ACCOUNTS_FCY).add(r19_ACCOUNTS_FCY)
				.add(r20_ACCOUNTS_FCY).add(r21_ACCOUNTS_FCY);
		BRF103_ENTITY.setR16_ACCOUNTS_FCY_DORMANT(R16_ACCOUNTS_FCY_DORMANT);

		// Handling nulls for AMOUNT_FCY_DORMANT
		BigDecimal r17_AMOUNT_FCY = BRF103_ENTITY.getR17_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR17_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r18_AMOUNT_FCY = BRF103_ENTITY.getR18_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR18_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r19_AMOUNT_FCY = BRF103_ENTITY.getR19_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR19_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r20_AMOUNT_FCY = BRF103_ENTITY.getR20_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR20_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r21_AMOUNT_FCY = BRF103_ENTITY.getR21_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR21_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R16_AMOUNT_FCY_DORMANT = r17_AMOUNT_FCY.add(r18_AMOUNT_FCY).add(r19_AMOUNT_FCY).add(r20_AMOUNT_FCY)
				.add(r21_AMOUNT_FCY);
		BRF103_ENTITY.setR16_AMOUNT_FCY_DORMANT(R16_AMOUNT_FCY_DORMANT);

		//// R23
		// Handling nulls for ACCOUNTS_AED_DORMANT
		BigDecimal r24_ACCOUNTS_AED = BRF103_ENTITY.getR24_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR24_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r26_ACCOUNTS_AED = BRF103_ENTITY.getR26_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR26_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R23_ACCOUNTS_AED_DORMANT = r24_ACCOUNTS_AED.add(r26_ACCOUNTS_AED);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR23_ACCOUNTS_AED_DORMANT(R23_ACCOUNTS_AED_DORMANT);

		// Handling nulls for AMOUNT_AED_DORMANT
		BigDecimal r24_AMOUNT_AED = BRF103_ENTITY.getR24_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR24_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r26_AMOUNT_AED = BRF103_ENTITY.getR26_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR26_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R23_AMOUNT_AED_DORMANT = r24_AMOUNT_AED.add(r26_AMOUNT_AED);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR23_AMOUNT_AED_DORMANT(R23_AMOUNT_AED_DORMANT);

		// Handling nulls for AMOUNT_FCY_DORMANT
		BigDecimal r24_AMOUNT_FCY = BRF103_ENTITY.getR24_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR24_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r26_AMOUNT_FCY = BRF103_ENTITY.getR26_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR26_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R23_AMOUNT_FCY_DORMANT = r24_AMOUNT_FCY.add(r26_AMOUNT_FCY);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR23_AMOUNT_FCY_DORMANT(R23_AMOUNT_FCY_DORMANT);

		// Handling nulls for ACCOUNTS_FCY_DORMANT
		BigDecimal r24_ACCOUNTS_FCY = BRF103_ENTITY.getR24_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR24_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r26_ACCOUNTS_FCY = BRF103_ENTITY.getR26_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR26_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R23_ACCOUNTS_FCY_DORMANT = r24_ACCOUNTS_FCY.add(r26_ACCOUNTS_FCY);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR23_ACCOUNTS_FCY_DORMANT(R23_ACCOUNTS_FCY_DORMANT);

		//// 28

		// Handling nulls for ACCOUNTS_AED_DORMANT
		BigDecimal r1_ACCOUNTS_AED = BRF103_ENTITY.getR1_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR1_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r6_ACCOUNTS_AED = BRF103_ENTITY.getR6_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR6_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r11_ACCOUNTS_AED = BRF103_ENTITY.getR11_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR11_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r16_ACCOUNTS_AED = BRF103_ENTITY.getR16_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR16_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r22_ACCOUNTS_AED = BRF103_ENTITY.getR22_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR22_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r23_ACCOUNTS_AED = BRF103_ENTITY.getR23_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR23_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R28_ACCOUNTS_AED_DORMANT = r1_ACCOUNTS_AED.add(r6_ACCOUNTS_AED).add(r11_ACCOUNTS_AED)
				.add(r16_ACCOUNTS_AED).add(r22_ACCOUNTS_AED).add(r23_ACCOUNTS_AED);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR28_ACCOUNTS_AED_DORMANT(R28_ACCOUNTS_AED_DORMANT);

		// Handling nulls for AMOUNT_AED_DORMANT
		BigDecimal r1_AMOUNT_AED = BRF103_ENTITY.getR1_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR1_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r6_AMOUNT_AED = BRF103_ENTITY.getR6_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR6_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r11_AMOUNT_AED = BRF103_ENTITY.getR11_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR11_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r16_AMOUNT_AED = BRF103_ENTITY.getR16_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR16_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r22_AMOUNT_AED = BRF103_ENTITY.getR22_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR22_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r23_AMOUNT_AED = BRF103_ENTITY.getR23_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR23_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R28_AMOUNT_AED_DORMANT = r1_AMOUNT_AED.add(r6_AMOUNT_AED).add(r11_AMOUNT_AED).add(r16_AMOUNT_AED)
				.add(r22_AMOUNT_AED).add(r23_AMOUNT_AED);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR28_AMOUNT_AED_DORMANT(R28_AMOUNT_AED_DORMANT);

		// Handling nulls for AMOUNT_FCY_DORMANT
		BigDecimal r1_AMOUNT_FCY = BRF103_ENTITY.getR1_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR1_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r6_AMOUNT_FCY = BRF103_ENTITY.getR6_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR6_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r11_AMOUNT_FCY = BRF103_ENTITY.getR11_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR11_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r16_AMOUNT_FCY = BRF103_ENTITY.getR16_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR16_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r22_AMOUNT_FCY = BRF103_ENTITY.getR22_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR22_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r23_AMOUNT_FCY = BRF103_ENTITY.getR23_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR23_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r27_AMOUNT_FCY = BRF103_ENTITY.getR27_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR27_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R28_AMOUNT_FCY_DORMANT = r1_AMOUNT_FCY.add(r6_AMOUNT_FCY).add(r11_AMOUNT_FCY).add(r16_AMOUNT_FCY)
				.add(r22_AMOUNT_FCY).add(r23_AMOUNT_FCY).add(r27_AMOUNT_FCY);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR28_AMOUNT_FCY_DORMANT(R28_AMOUNT_FCY_DORMANT);

		// Handling nulls for ACCOUNTS_FCY_DORMANT
		BigDecimal r1_ACCOUNTS_FCY = BRF103_ENTITY.getR1_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR1_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r6_ACCOUNTS_FCY = BRF103_ENTITY.getR6_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR6_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r11_ACCOUNTS_FCY = BRF103_ENTITY.getR11_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR11_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r16_ACCOUNTS_FCY = BRF103_ENTITY.getR16_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR16_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r22_ACCOUNTS_FCY = BRF103_ENTITY.getR22_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR22_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r23_ACCOUNTS_FCY = BRF103_ENTITY.getR23_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR23_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r27_ACCOUNTS_FCY = BRF103_ENTITY.getR27_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR27_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R28_ACCOUNTS_FCY_DORMANT = r1_ACCOUNTS_FCY.add(r6_ACCOUNTS_FCY).add(r11_ACCOUNTS_FCY)
				.add(r16_ACCOUNTS_FCY).add(r22_ACCOUNTS_FCY).add(r23_ACCOUNTS_FCY).add(r27_ACCOUNTS_FCY);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR28_ACCOUNTS_FCY_DORMANT(R28_ACCOUNTS_FCY_DORMANT);

		/////// r29

		// Handling nulls for ACCOUNTS_AED_DORMANT
		BigDecimal r5_ACCOUNTS_AED1 = BRF103_ENTITY.getR5_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR5_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r10_ACCOUNTS_AED1 = BRF103_ENTITY.getR10_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR10_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r15_ACCOUNTS_AED1 = BRF103_ENTITY.getR15_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR15_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r21_ACCOUNTS_AED1 = BRF103_ENTITY.getR21_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR21_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r26_ACCOUNTS_AED1 = BRF103_ENTITY.getR26_ACCOUNTS_AED_DORMANT() != null
				? BRF103_ENTITY.getR26_ACCOUNTS_AED_DORMANT()
				: BigDecimal.ZERO;
		// BigDecimal r27_ACCOUNTS_AED1 = BRF103_ENTITY.getR27_ACCOUNTS_AED_DORMANT() !=
		// null ? BRF103_ENTITY.getR27_ACCOUNTS_AED_DORMANT() : BigDecimal.ZERO;

		BigDecimal R29_ACCOUNTS_AED_DORMANT = r5_ACCOUNTS_AED1.add(r10_ACCOUNTS_AED1).add(r15_ACCOUNTS_AED1)
				.add(r21_ACCOUNTS_AED1).add(r26_ACCOUNTS_AED1);
		// .add(r27_ACCOUNTS_AED1)

		// Set the calculated value to the target field
		BRF103_ENTITY.setR29_ACCOUNTS_AED_DORMANT(R29_ACCOUNTS_AED_DORMANT);

		// Handling nulls for AMOUNT_AED_DORMANT
		BigDecimal r5_AMOUNT_AED1 = BRF103_ENTITY.getR5_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR5_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r10_AMOUNT_AED1 = BRF103_ENTITY.getR10_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR10_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r15_AMOUNT_AED1 = BRF103_ENTITY.getR15_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR15_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r21_AMOUNT_AED1 = BRF103_ENTITY.getR21_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR21_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r26_AMOUNT_AED1 = BRF103_ENTITY.getR26_AMOUNT_AED_DORMANT() != null
				? BRF103_ENTITY.getR26_AMOUNT_AED_DORMANT()
				: BigDecimal.ZERO;
		// BigDecimal r27_AMOUNT_AED1 = BRF103_ENTITY.getR27_AMOUNT_AED_DORMANT() !=
		// null ? BRF103_ENTITY.getR27_AMOUNT_AED_DORMANT() : BigDecimal.ZERO;

		BigDecimal R29_AMOUNT_AED_DORMANT = r5_AMOUNT_AED1.add(r10_AMOUNT_AED1).add(r15_AMOUNT_AED1)
				.add(r21_AMOUNT_AED1).add(r26_AMOUNT_AED1);
		// .add(r27_AMOUNT_AED1);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR29_AMOUNT_AED_DORMANT(R29_AMOUNT_AED_DORMANT);

		// Handling nulls for ACCOUNTS_FCY_DORMANT
		BigDecimal r3_ACCOUNTS_FCY1 = BRF103_ENTITY.getR3_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR3_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r10_ACCOUNTS_FCY1 = BRF103_ENTITY.getR10_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR10_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r15_ACCOUNTS_FCY1 = BRF103_ENTITY.getR15_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR15_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r21_ACCOUNTS_FCY1 = BRF103_ENTITY.getR21_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR21_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r26_ACCOUNTS_FCY1 = BRF103_ENTITY.getR26_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR26_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r27_ACCOUNTS_FCY1 = BRF103_ENTITY.getR27_ACCOUNTS_FCY_DORMANT() != null
				? BRF103_ENTITY.getR27_ACCOUNTS_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R29_ACCOUNTS_FCY_DORMANT = r3_ACCOUNTS_FCY1.add(r10_ACCOUNTS_FCY1).add(r15_ACCOUNTS_FCY1)
				.add(r21_ACCOUNTS_FCY1).add(r26_ACCOUNTS_FCY1).add(r27_ACCOUNTS_FCY1);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR29_ACCOUNTS_FCY_DORMANT(R29_ACCOUNTS_FCY_DORMANT);

		// Handling nulls for AMOUNT_FCY_DORMANT
		BigDecimal r3_AMOUNT_FCY1 = BRF103_ENTITY.getR3_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR3_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r10_AMOUNT_FCY1 = BRF103_ENTITY.getR10_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR10_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r15_AMOUNT_FCY1 = BRF103_ENTITY.getR15_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR15_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r21_AMOUNT_FCY1 = BRF103_ENTITY.getR21_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR21_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r26_AMOUNT_FCY1 = BRF103_ENTITY.getR26_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR26_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;
		BigDecimal r27_AMOUNT_FCY1 = BRF103_ENTITY.getR27_AMOUNT_FCY_DORMANT() != null
				? BRF103_ENTITY.getR27_AMOUNT_FCY_DORMANT()
				: BigDecimal.ZERO;

		BigDecimal R29_AMOUNT_FCY_DORMANT = r3_AMOUNT_FCY1.add(r10_AMOUNT_FCY1).add(r15_AMOUNT_FCY1)
				.add(r21_AMOUNT_FCY1).add(r26_AMOUNT_FCY1).add(r27_AMOUNT_FCY1);

		// Set the calculated value to the target field
		BRF103_ENTITY.setR29_AMOUNT_FCY_DORMANT(R29_AMOUNT_FCY_DORMANT);

		return regreportServices.modifyRecord103(asondate, BRF103_ENTITY, userId);
	}

	public List<AuditTablePojo> getauditListLocalvaluesbusiness(Date fromDateToUse) {
		List<MANUAL_Service_Entity> auditList = mANUAL_Service_Rep.getauditListLocalvaluesbusiness(fromDateToUse);
		List<AuditTablePojo> auditPojoList = new ArrayList<>();

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

		for (MANUAL_Service_Entity ipsAudit : auditList) {
			boolean isUpdated = false;

			// Check if an entry with the same ID and stage3 remarks already exists
			for (AuditTablePojo existingPojo : auditPojoList) {
				String auditRefNo = existingPojo.getAudit_ref_no();
				String remarks = existingPojo.getRemarks();
				String ipsAuditno = ipsAudit.getAudit_ref_no();

				if (auditRefNo != null && ipsAuditno != null && auditRefNo.equals(ipsAuditno) && remarks != null
						&& ("Login Successfully".equals(remarks) || "Logout Successfully".equals(remarks))) {

					// Update existing entry's data
					existingPojo.setAudit_table(ipsAudit.getAudit_table());
					existingPojo.setFunc_code(ipsAudit.getFunc_code());
					existingPojo.setEntry_user(ipsAudit.getEntry_user());
					existingPojo.setEntry_time(ipsAudit.getEntry_time());
					existingPojo.setAuth_user(ipsAudit.getAuth_user());
					existingPojo.setAuth_time(ipsAudit.getAuth_time());
					existingPojo.setRemarks(ipsAudit.getRemarks());

					List<String> fieldName = new ArrayList<>();
					List<String> oldvalue = new ArrayList<>();
					List<String> newvalue = new ArrayList<>();

					// Populate lists excluding "FIELD 4"
					String[] oldValues = ipsAudit.getOld_value().split("\\|\\|");
					String[] newValues = ipsAudit.getNew_value().split("\\|\\|");
					String[] fields = ipsAudit.getField_name().split("\\|\\|");

					for (int i = 0; i < fields.length; i++) {
						if (!"FIELD 4".equals(fields[i])) {
							fieldName.add(fields[i]);

							// Format the old and new values if they are date strings
							String oldFormatted = formatDate(oldValues[i], dateFormat);
							String newFormatted = formatDate(newValues[i], dateFormat);

							oldvalue.add(oldFormatted);
							newvalue.add(newFormatted);
						}
					}

					existingPojo.setField_name(fieldName);
					existingPojo.setOld_value(oldvalue);
					existingPojo.setNew_value(newvalue);

					isUpdated = true;
					break;
				}
			}

			// Create a new entry if no existing entry was updated
			if (!isUpdated) {
				AuditTablePojo auditTablePojo = new AuditTablePojo();
				auditTablePojo.setAudit_table(ipsAudit.getAudit_table());
				auditTablePojo.setFunc_code(ipsAudit.getFunc_code());
				auditTablePojo.setEntry_user(ipsAudit.getEntry_user());
				auditTablePojo.setEntry_time(ipsAudit.getEntry_time());
				auditTablePojo.setAuth_user(ipsAudit.getAuth_user());
				auditTablePojo.setAuth_time(ipsAudit.getAuth_time());
				auditTablePojo.setRemarks(ipsAudit.getRemarks());

				List<String> fieldName = new ArrayList<>();
				List<String> oldvalue = new ArrayList<>();
				List<String> newvalue = new ArrayList<>();

				if (ipsAudit != null && ipsAudit.getModi_details() != null) {
					String[] oldValues = ipsAudit.getOld_value().split("\\|\\|");
					String[] newValues = ipsAudit.getNew_value().split("\\|\\|");
					String[] fields = ipsAudit.getField_name().split("\\|\\|");

					for (int i = 0; i < fields.length; i++) {
						if (!"FIELD 4".equals(fields[i])) {
							fieldName.add(fields[i]);

							// Format the old and new values if they are date strings
							String oldFormatted = formatDate(oldValues[i], dateFormat);
							String newFormatted = formatDate(newValues[i], dateFormat);

							oldvalue.add(oldFormatted);
							newvalue.add(newFormatted);
						}
					}
				} else {
					System.out.println("No modification details available");
				}

				auditTablePojo.setField_name(fieldName);
				auditTablePojo.setOld_value(oldvalue);
				auditTablePojo.setNew_value(newvalue);
				auditPojoList.add(auditTablePojo);
			}
		}

		return auditPojoList;
	}

	// Helper method to format date values as 'DD-MM-YYYY'
	private String formatDate(String value, SimpleDateFormat dateFormat) {
		try {
			// Assuming the value is in a valid date format that SimpleDateFormat can parse
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(value); // Adjust this pattern based on
																						// your date format
			return dateFormat.format(date); // Return formatted date as 'DD-MM-YYYY'
		} catch (Exception e) {
			// If parsing fails, return the original value
			return value;
		}
	}
	
	
	@RequestMapping(value = "Generateloginotp", method = {RequestMethod.GET, RequestMethod.POST})
	@ResponseBody
	public String Generateloginotp(@RequestParam("Userid") String Userid) {
	    String msg = "success";
	    System.out.println(msg);
	    return msg;
	}

}
