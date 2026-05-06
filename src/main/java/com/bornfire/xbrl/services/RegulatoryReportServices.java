package com.bornfire.xbrl.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.entities.XBRLProceduresRep;
import com.bornfire.xbrl.entities.XBRLReportsMasterRep;
import com.bornfire.xbrl.entities.BRBS.BRF103_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF300_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF32_ENTITY;
import com.bornfire.xbrl.entities.BRBS.BRF66_Entity;
import com.bornfire.xbrl.entities.BRBS.BRF_181_A2_REPORT_ENTITY;
import com.bornfire.xbrl.entities.BRBS.ReportBRF108Data;
import com.bornfire.xbrl.entities.BRBS.ReportBRF202AData;
import com.bornfire.xbrl.entities.BRBS.ReportBRF301Data;
//import com.bornfire.xbrl.entities.BRBS.YourFormData;
import com.bornfire.xbrl.entities.BRBS.YourFormData;
import com.bornfire.xbrl.entities.BRBS.YourFormDatas;

import net.sf.jasperreports.engine.JRException;

@Component
@Service
@Transactional
@ConfigurationProperties("output")
public class RegulatoryReportServices {

	private static final Logger logger = LoggerFactory.getLogger(RegulatoryReportServices.class);

	@NotNull
	private String exportpath;

	@Autowired
	BRF109ReportService brf109ReportService;
	
	@Autowired
	BRF202AReportService brf202AReportService;

	@Autowired
	XBRLReportsMasterRep xbrlReportsMasterRep;

	@Autowired
	XBRLProceduresRep xbrlProceduresRep;

	@Autowired
	DataSource srcdataSource;
	@Autowired
	BRF181AReportService BRF181AReportService;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	CustomRepGeneratorServices customerrptgenserviceexcel;

	@Autowired
	BRF32ReportService brf32ReportService;

	@Autowired
	B15ReportService b15ReportService;

	@Autowired
	B16ReportService b16ReportService;

	@Autowired
	B17ReportService b17ReportService;

	@Autowired
	B34ReportService b34ReportService;

	@Autowired
	B33ReportService b33ReportService;

	@Autowired
	BRF151ReportService brf151ReportService;

	@Autowired
	BRF152ReportService brf152ReportService;

	@Autowired
	BRF153ReportService brf153ReportService;
	@Autowired
	BRF70AReportService BRF70AReportService;

	@Autowired
	BRF154ReportService brf154ReportService;

	@Autowired

	BRF155ReportService brf155ReportService;

	@Autowired
	BRF156ReportService brf156ReportService;

	@Autowired
	BRF51ReportService brf051ReportService;

	@Autowired
	BRF52ReportService brf052ReportService;

	@Autowired

	BRF53ReportService brf53ReportService;

	@Autowired
	BRF54ReportService brf054ReportService;

	@Autowired
	BRF56ReportService brf056ReportService;

	@Autowired
	BRF57ReportService brf057ReportService;

	@Autowired
	BRF59ReportService brf59ReportService;

	@Autowired
	BRF60ReportService brf060ReportService;

	@Autowired
	BRF62ReportService brf062ReportService;

	/* @Autowired BRF094AReportService BRF094AReportService; */

	@Autowired
	BASEL002AReportService BASEL002AReportService;

	@Autowired
	BRF74ReportService brf74ReportService;

	@Autowired
	BRF036ReportService brf036ReportService;

	@Autowired
	BRF204AReportService brf204AReportService;

	@Autowired
	BRF205AReportService brf205AReportService;

	@Autowired
	BRF206AReportService brf206AReportService;

	@Autowired
	BRF201AReportService brf201AReportService;

	@Autowired
	BRF200AReportService brf200AReportService;

	@Autowired
	BRF207AReportService brf207AReportService;

	@Autowired
	BRF208AReportService brf208AReportService;

	@Autowired
	BRF209AReportService brf209AReportService;

	@Autowired
	BRF210AReportService brf210AReportService;

	@Autowired
	BRF66AReportService brf066AReportService;

	@Autowired
	BRF70AReportService brf070AReportService;

	@Autowired
	BRF71ReportService BRF71ReportService;

	@Autowired
	BRF93ReportService brf93ReportService;
	
	@Autowired
	BRF095AReportService brf095AReportService;

	@Autowired
	BASELB2ReportService baselb2ReportService;

	@Autowired
	BASELB3ReportService baselb3ReportService;

	@Autowired
	BASELSECReportService baselsecReportService;

	@Autowired
	B18BASELSECTRADReportService b18baselsectradReportService;

	@Autowired
	B19TO25BASELReportService b19to25baselReportService;

	@Autowired
	B20BASELReportService b20baselReportService;

	@Autowired
	B21BASELReportService b21baselReportService;
	@Autowired
	B22BASELReportService b22baselReportService;
	@Autowired
	B23BASELReportService b23baselReportService;
	@Autowired
	B24BASELReportService b24baselReportService;

	@Autowired
	B25BASELReportService b25baselReportService;
	
	

	@Autowired
	B32BASELReportService b32baselReportService;

	@Autowired
	B31BASELReportService b31baselReportService;

	@Autowired
	B27BASELReportService b27baselReportService;

	@Autowired

	B28BASELReportService b28baselReportService;

	@Autowired
	B29BASELReportService b29baselReportService;

	@Autowired
	B30BASELReportService b30baselReportService;

	@Autowired
	B26BASELReportService b26baselReportService;

	@Autowired
	BRF96AReportService brf96AReportService;

	@Autowired
	BRF102AReportService brf102AReportService;

	@Autowired
	BRF104AReportService brf104AReportService;

	@Autowired
	BRF100AReportService brf100AReportService;

	@Autowired
	BRF101ReportService brf101ReportService;

	@Autowired
	BRF103AReportService brf103AReportService;

	@Autowired
	BRF106AReportService brf106AReportService;

	@Autowired
	BRF105AReportService brf105AReportService;

	@Autowired
	BRF107AReportService brf107AReportService;

	@Autowired
	BRF300AReportService brf300AReportService;

	@Autowired
	BRF99AReportService brf99AReportService;

	@Autowired
	BRF181AReportService brf181AReportService;

	@Autowired
	BRF65ReportService brf65ReportService;

	@Autowired
	BRF64ReportService brf64ReportService;

	@Autowired
	BRF094ReportService brf094ReportService;

	@Autowired
	BASELMR9ReportService baselmr9ReportService;

	@Autowired
	BRF67ReportService brf67ReportService;

	@Autowired
	BRF77ReportService brf77ReportService;

	@Autowired
	BRF78ReportService brf78ReportService;

	@Autowired
	BRF79ReportService brf79ReportService;

	@Autowired
	BRF80ReportService brf80ReportService;

	@Autowired
	BRF81ReportService brf81ReportService;

	@Autowired
	BRF82ReportService brf82ReportService;

	@Autowired
	BRF83ReportService brf83ReportService;

	@Autowired
	BRF84ReportService brf84ReportService;

	@Autowired
	BRF85ReportService brf85ReportService;

	@Autowired
	BRF86ReportService brf86ReportService;

	@Autowired
	BRF88ReportService brf88ReportService;

	@Autowired
	BRF92ReportService brf92ReportService;

	@Autowired
	BRF73ReportService brf73ReportService;

	@Autowired
	BRF001ReportService brf001ReportService;

	@Autowired
	BRF002ReportService brf002ReportService;

	@Autowired
	BRF005ReportService brf005ReportService;

	@Autowired
	BRF003ReportService brf003ReportService;

	@Autowired
	BRF004ReportService brf004ReportService;

	@Autowired
	BRF007ReportService brf007ReportService;

	@Autowired
	BRF008ReportService brf008ReportService;

	@Autowired
	BRF009ReportService brf009ReportService;

	@Autowired
	BRF010ReportService brf010ReportService;

	@Autowired
	BRF011ReportService brf011ReportService;

	@Autowired
	BRF012ReportService brf012ReportService;

	@Autowired
	BRF013ReportService brf013ReportService;
	@Autowired
	BRF014ReportService brf014ReportService;

	@Autowired
	BRF033ReportService brf033ReportService;

	@Autowired
	BRF034ReportService brf034ReportService;

	@Autowired
	BRF35ReportService BRF35ReportService;

	@Autowired
	BRF37ReportService brf37ReportService;

	/*
	 * @Autowired BRF038ReportService brf038ReportService;
	 */

	@Autowired
	BRF38ReportService brf38ReportService1;

	@Autowired
	BRF39ReportService brf039ReportService;

	@Autowired
	BRF40ReportService brf40ReportService;

	@Autowired
	BRF42ReportService brf42ReportService;

	@Autowired
	BRF43ReportService brf43ReportService;

	@Autowired
	BRF47ReportService brf47ReportService;

	@Autowired
	BRF48ReportService brf48ReportService;

	@Autowired
	BRF44ReportService brf44ReportService;

	@Autowired
	BRF31ReportService brf031ReportService;

	@Autowired
	BRF45ReportService brf45ReportService;

	@Autowired
	BRF50ReportService brf50ReportService;

	@Autowired
	BRF41ReportService brf41ReportService;

	@Autowired
	BRF46ReportService brf46ReportService;

	@Autowired
	BRF49ReportService brf49ReportService;

	@Autowired
	B10ReportService b10ReportService;

	@Autowired
	B13ReportService b13ReportService;

	@Autowired
	B5ReportService b5ReportService;

	@Autowired
	B14ReportService b14ReportService;

	@Autowired
	BASELL_12_ReportService basell_12_ReportService;

	@Autowired
	BASEL_8_ReportService basel_8_ReportService;

	@Autowired
	BASEL_9_ReportService basel_9_ReportService;

	@Autowired
	BRF68ReportService brf068ReportService;

	@Autowired
	BRF44ReportService brf044ReportService;

	@Autowired
	BRF69ReportService brf069ReportService;

	@Autowired
	B4ReportService b4ReportService;

	@Autowired
	BRF71ReportService brf71ReportService;

	@Autowired
	BRF76ReportService brf76ReportService;

	@Autowired
	BASELEQUALITY_ReportService baselEQUALITY_ReportService;

	@Autowired
	BASCELMR6_ReportService bascelMR6_ReportService;

	@Autowired
	BASELCR3_REPORTService baselCR3_REPORTService;

	@Autowired
	B7_ReportService b7_ReportService;

	@Autowired
	BASEL_OR1_ReportService basel_OR1_ReportService;

	@Autowired
	BASCELMR8_ReportService bascelMR8_ReportService;

	@Autowired
	BRF87ReportService brf87ReportService;

	@Autowired
	BRF202AReportService brf202ReportService;

	@Autowired
	BRF014ReportService brf14ReportService;

	@Autowired
	BRF001_FORT_SERVICE brf001_FORT_SERVIVE;

	@Autowired
	BRF301ReportService brf301ReportService;

	@Autowired
	BRF108ReportService brf108ReportService;

	@Autowired
	Banking_BookReportService banking_bookReportService;
	
	@Autowired
	BRF95_FUND_ReportService brf95_fund_ReportService;

	String getExportpath() {
		return exportpath;
	}

	public void setExportpath(String exportpath) {
		this.exportpath = exportpath;
	}

	public ModelAndView getReportView(String reportId, String reportDate, String fromdate, String todate,
			String currency, String dtltype, String subreportid, String secid, String reportingTime, Pageable pageable,
			BigDecimal srl_no, String req) {

		ModelAndView repsummary = new ModelAndView();

		logger.info("Getting View for the Report :" + reportId);
		switch (reportId) {

		case "OR1":
			repsummary = basel_OR1_ReportService.getOR1View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF0001":
			repsummary = brf001_FORT_SERVIVE.getBRF0001View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR8":
			repsummary = bascelMR8_ReportService.getMR8View(reportId, fromdate, todate, currency, dtltype, pageable);

			break;

		case "FX (MR5)":
			repsummary = b33ReportService.getB33View(reportId, fromdate, todate, currency, dtltype, pageable);

			break;

		case "MR6":
			repsummary = baselEQUALITY_ReportService.getB35View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "MR7":
			repsummary = bascelMR6_ReportService.getB36View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR3":
			repsummary = baselCR3_REPORTService.getB6View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR4":
			repsummary = b7_ReportService.getB7View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "SR1":
			repsummary = basell_12_ReportService.getB12View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "CR4a":
			repsummary = basel_8_ReportService.getB8View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR4b (fx)":
			repsummary = basel_9_ReportService.getB9View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "FX (MR5a)":
			repsummary = b34ReportService.getB34View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "CR2a":
			repsummary = b4ReportService.getB4View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR2":
			repsummary = b15ReportService.getB15View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "MR2_AED":
			repsummary = b16ReportService.getB16View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "MR2_EUR":
			repsummary = b17ReportService.getB17View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF032A":
			repsummary = brf32ReportService.getBRF32View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF151":
			repsummary = brf151ReportService.getBRF151View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF152":
			repsummary = brf152ReportService.getBRF152View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF153":
			repsummary = brf153ReportService.getBRF153View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF154":
			repsummary = brf154ReportService.getBRF154View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF155":
			repsummary = brf155ReportService.getBRF155View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF156":
			repsummary = brf156ReportService.getBRF156View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF051":
			repsummary = brf051ReportService.getBRF051View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF052":
			repsummary = brf052ReportService.getBRF052View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF053":
			repsummary = brf53ReportService.getBRF053View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF054":
			repsummary = brf054ReportService.getBRF054View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF056":
			repsummary = brf056ReportService.getBRF056View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF057":
			repsummary = brf057ReportService.getBRF057View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF059":
			repsummary = brf59ReportService.getBRF059View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF060":
			repsummary = brf060ReportService.getBRF060View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF062":
			repsummary = brf062ReportService.getBRF062View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF073":
			repsummary = brf73ReportService.getBRF73View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF070A":
			repsummary = brf070AReportService.getBRF070AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF066A":
			repsummary = brf066AReportService.getBRF066AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF093A":
			repsummary = brf93ReportService.getBRF093AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF095A":
			repsummary = brf095AReportService.getBRF095AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "SEC":
			repsummary = baselsecReportService.getBASELSECView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR1":
			repsummary = baselb2ReportService.getBASELB2View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR2":
			repsummary = baselb3ReportService.getBASELB3View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF95-CAR":
			repsummary = BASEL002AReportService.getB1View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
			
	

		case "EIF":
			repsummary = b10ReportService.getB10View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "CR2b":
			repsummary = b5ReportService.getB5View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CV1":
			repsummary = b13ReportService.getB13View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR1":
			repsummary = b14ReportService.getB14View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "SEC-TB":
			repsummary = b18baselsectradReportService.getB18BASELSECTRADView(reportId, fromdate, todate, currency,
					dtltype, pageable);
			break;
		case "MR3":
			repsummary = b19to25baselReportService.getB19TO25BASELView(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "MR3-USD":
			repsummary = b20baselReportService.getB20BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-GBP":
			repsummary = b21baselReportService.getB21BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-EUR":
			repsummary = b22baselReportService.getB22BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-AED":
			repsummary = b23baselReportService.getB23BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-FUR":
			repsummary = b24baselReportService.getB24BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-RES":
			repsummary = b25baselReportService.getB25BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-RES":
			repsummary = b32baselReportService.getB32BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-FUR":
			repsummary = b31baselReportService.getB31BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4":
			repsummary = b26baselReportService.getB26BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-USD":
			repsummary = b27baselReportService.getB27BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-GBP":
			repsummary = b28baselReportService.getB28BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "MR4-EUR":
			repsummary = b29baselReportService.getB29BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-AED":
			repsummary = b30baselReportService.getB30BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF068":
			repsummary = brf068ReportService.getBRF068View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF069":
			repsummary = brf069ReportService.getBRF069View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF096A":
			repsummary = brf96AReportService.getBRF96AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR9":

			repsummary = baselmr9ReportService.getBASELMR9View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF102A":
			repsummary = brf102AReportService.getBRF102AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF104A":
			repsummary = brf104AReportService.getBRF104AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF105A":
			repsummary = brf105AReportService.getBRF105AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF106A":
			repsummary = brf106AReportService.getBRF106AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF107A":
			repsummary = brf107AReportService.getBRF107AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF300A":
			repsummary = brf300AReportService.getBRF300AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF100A":
			repsummary = brf100AReportService.getBRF100AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF101A":
			repsummary = brf101ReportService.getBRF101View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF103A":
			repsummary = brf103AReportService.getBRF103AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF099A":
			repsummary = brf99AReportService.getBRF99AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF181A":
			repsummary = brf181AReportService.getBRF181AView(reportId, fromdate, todate, currency, dtltype, pageable,
					req);
			break;

		/*
		 * case "BRF094A": repsummary = BRF094AReportService.getBRF094AView(reportId,
		 * fromdate, todate, currency, dtltype, pageable); break;
		 */

		case "BRF074":
			repsummary = brf74ReportService.getBRF74View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF076":
			repsummary = brf76ReportService.getBRF76View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF077":
			repsummary = brf77ReportService.getBRF77View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF078":
			repsummary = brf78ReportService.getBRF78View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF079":
			repsummary = brf79ReportService.getBRF79View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF080":
			repsummary = brf80ReportService.getBRF80View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF081":
			repsummary = brf81ReportService.getBRF81View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF082":
			repsummary = brf82ReportService.getBRF82View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF083":
			repsummary = brf83ReportService.getBRF83View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF084":
			repsummary = brf84ReportService.getBRF84View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF085":
			repsummary = brf85ReportService.getBRF85View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF086":
			repsummary = brf86ReportService.getBRF86View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF087":
			repsummary = brf87ReportService.getBRF87View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF088":
			repsummary = brf88ReportService.getBRF88View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF092":
			repsummary = brf92ReportService.getBRF92View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF065":
			repsummary = brf65ReportService.getBRF065View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF064":
			repsummary = brf64ReportService.getBRF064View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF094A":
			repsummary = brf094ReportService.getBRF094View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF067":
			repsummary = brf67ReportService.getBRF67View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF071":
			repsummary = brf71ReportService.getBRF71View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF036A":
			repsummary = brf036ReportService.getBRF036View(reportId, fromdate, todate, currency, dtltype, pageable,
					srl_no);
			break;
		case "BRF204A":
			repsummary = brf204AReportService.getBRF204AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF205A":
			repsummary = brf205AReportService.getBRF205AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRFBankingbook":
			repsummary = banking_bookReportService.getBRFBanking_BookView(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
			
		case "BRF95FB":
			repsummary = brf95_fund_ReportService.getBRFFB_View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF206A":
			repsummary = brf206AReportService.getBRF206AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF208A":
			repsummary = brf208AReportService.getBRF208AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF207A":
			repsummary = brf207AReportService.getBRF207AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF209A":
			repsummary = brf209AReportService.getBRF209AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF210A":
			repsummary = brf210AReportService.getBRF210AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF201A":
			repsummary = brf201AReportService.getBRF201AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF202A":
			repsummary = brf202AReportService.getBRF202AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF301A":
			repsummary = brf301ReportService.getBRF301View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF108A":
			repsummary = brf108ReportService.getBRF108View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		/* DEVLOPED BY SANJEEVI */
		case "BRF200A":
			repsummary = brf200AReportService.getBRF200AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF001":
			repsummary = brf001ReportService.getBRF001View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF002":
			repsummary = brf002ReportService.getBRF002View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF037A":
			repsummary = brf37ReportService.getBRF37View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF038A":
			repsummary = brf38ReportService1.getBRF38View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF039A":
			repsummary = brf039ReportService.getBRF039View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF040A":
			repsummary = brf40ReportService.getBRF40View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF042A":
			repsummary = brf42ReportService.getBRF42View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF043A":
			repsummary = brf43ReportService.getBRF43View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF047A":
			repsummary = brf47ReportService.getBRF47View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF048A":
			repsummary = brf48ReportService.getBRF48View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF031A":
			repsummary = brf031ReportService.getBRF031View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF044A":
			repsummary = brf44ReportService.getBRF44View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF045A":
			repsummary = brf45ReportService.getBRF45View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF050A":
			repsummary = brf50ReportService.getBRF50View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF041A":
			repsummary = brf41ReportService.getBRF41View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF003":
			repsummary = brf003ReportService.getBRF003View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF004":
			repsummary = brf004ReportService.getBRF004View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF005":
			repsummary = brf005ReportService.getBRF005View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF007":
			repsummary = brf007ReportService.getBRF007View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF008":
			repsummary = brf008ReportService.getBRF008View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF009":
			repsummary = brf009ReportService.getBRF009View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF011":
			repsummary = brf011ReportService.getBRF011View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF013":
			repsummary = brf013ReportService.getBRF013View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF014":
			repsummary = brf014ReportService.getBRF014View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF033A":
			repsummary = brf033ReportService.getBRF033View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF034A":
			repsummary = brf034ReportService.getBRF034View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF035A":
			repsummary = BRF35ReportService.getBRF035View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF010":
			repsummary = brf010ReportService.getBRF010View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF012":
			repsummary = brf012ReportService.getBRF012View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF049A":
			repsummary = brf49ReportService.getBRF49View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF109A":
			repsummary = brf109ReportService.getBRF109View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
			
		case "BRF046A":
			repsummary = brf46ReportService.getBRF46View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		/*
		 * case "MR3-RES": repsummary = b25baselReportService.getB25BASELView(reportId,
		 * fromdate, todate, currency, dtltype, pageable); break;
		 */

		}

		return repsummary;
	}

	public ModelAndView getReportSummary(String reportId, String reportDate, String fromdate, String todate,
			String currency, String dtltype, String subreportid, String secid, String reportingTime, Pageable pageable,
			BigDecimal srl_no, String req) {

		ModelAndView repsummary = new ModelAndView();
		logger.info("Getting Summary for the Report :" + reportId);
		switch (reportId) {

		case "OR1":
			repsummary = basel_OR1_ReportService.getOR1View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF0001":
			repsummary = brf001_FORT_SERVIVE.getBRF0001View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR8":
			repsummary = bascelMR8_ReportService.getMR8View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR6":
			repsummary = baselEQUALITY_ReportService.getB35View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "MR7":
			repsummary = bascelMR6_ReportService.getB36View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "CR3":
			repsummary = baselCR3_REPORTService.getB6View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR4":
			repsummary = b7_ReportService.getB7View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "FX (MR5)":
			repsummary = b33ReportService.getB33View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "FX (MR5a)":
			repsummary = b34ReportService.getB34View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "CR2a":
			repsummary = b4ReportService.getB4View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR2":
			repsummary = b15ReportService.getB15View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR2_AED":
			repsummary = b16ReportService.getB16View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR2_EUR":
			repsummary = b17ReportService.getB17View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF001":
			repsummary = brf001ReportService.getBRF001View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF001N":
			repsummary = brf001ReportService.getBRF001View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF046A":
			repsummary = brf46ReportService.getBRF46View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF049A":
			repsummary = brf49ReportService.getBRF49View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
			
		case "BRF109A":
			repsummary = brf109ReportService.getBRF109View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF005":
			repsummary = brf005ReportService.getBRF005View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF002":
			repsummary = brf002ReportService.getBRF002View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF003":
			repsummary = brf003ReportService.getBRF003View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF004":
			repsummary = brf004ReportService.getBRF004View(reportId, fromdate, todate, currency, dtltype, pageable);

			break;

		case "SR1":
			repsummary = basell_12_ReportService.getB12View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR4a":
			repsummary = basel_8_ReportService.getB8View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR4b (fx)":
			repsummary = basel_9_ReportService.getB9View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF073":
			repsummary = brf73ReportService.getBRF73View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF070A":
			repsummary = brf070AReportService.getBRF070AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF066A":
			repsummary = brf066AReportService.getBRF066AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF093A":
			repsummary = brf93ReportService.getBRF093AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		/*
		 * case"BRF094A": repsummary = BRF094AReportService.getBRF094AView(reportId,
		 * fromdate, todate, currency, dtltype, pageable); break;
		 */

		case "BRF095A":
			repsummary = brf095AReportService.getBRF095AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF95-CAR":
			repsummary = BASEL002AReportService.getB1View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "CR2b":
			repsummary = b5ReportService.getB5View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "EIF":
			repsummary = b10ReportService.getB10View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "CV1":
			repsummary = b13ReportService.getB13View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "CR2":
			repsummary = baselb3ReportService.getBASELB3View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "CR1":
			repsummary = baselb2ReportService.getBASELB2View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "SEC":
			repsummary = baselsecReportService.getBASELSECView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "MR1":
			repsummary = b14ReportService.getB14View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "SEC-TB":
			repsummary = b18baselsectradReportService.getB18BASELSECTRADView(reportId, fromdate, todate, currency,
					dtltype, pageable);
			break;
		case "MR3":
			repsummary = b19to25baselReportService.getB19TO25BASELView(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "MR3-USD":
			repsummary = b20baselReportService.getB20BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-GBP":
			repsummary = b21baselReportService.getB21BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-EUR":
			repsummary = b22baselReportService.getB22BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-AED":
			repsummary = b23baselReportService.getB23BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-FUR":
			repsummary = b24baselReportService.getB24BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR3-RES":
			repsummary = b25baselReportService.getB25BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-RES":
			repsummary = b32baselReportService.getB32BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-FUR":
			repsummary = b31baselReportService.getB31BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4":
			repsummary = b26baselReportService.getB26BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "MR4-USD":
			repsummary = b27baselReportService.getB27BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-GBP":
			repsummary = b28baselReportService.getB28BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "MR4-EUR":
			repsummary = b29baselReportService.getB29BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR4-AED":
			repsummary = b30baselReportService.getB30BASELView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF096A":
			repsummary = brf96AReportService.getBRF96AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF102A":
			repsummary = brf102AReportService.getBRF102AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF104A":
			repsummary = brf104AReportService.getBRF104AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF105A":
			repsummary = brf105AReportService.getBRF105AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF100A":
			repsummary = brf100AReportService.getBRF100AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF101A":
			repsummary = brf101ReportService.getBRF101View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF103A":
			repsummary = brf103AReportService.getBRF103AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF106A":
			repsummary = brf106AReportService.getBRF106AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF107A":
			repsummary = brf107AReportService.getBRF107AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF300A":
			repsummary = brf300AReportService.getBRF300AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF099A":
			repsummary = brf99AReportService.getBRF99AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF181A":
			repsummary = brf181AReportService.getBRF181AView(reportId, fromdate, todate, currency, dtltype, pageable,
					req);
			break;

		case "BRF074":
			repsummary = brf74ReportService.getBRF74View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF076":
			repsummary = brf76ReportService.getBRF76View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF077":
			repsummary = brf77ReportService.getBRF77View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF078":
			repsummary = brf78ReportService.getBRF78View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF079":
			repsummary = brf79ReportService.getBRF79View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF080":
			repsummary = brf80ReportService.getBRF80View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF081":
			repsummary = brf81ReportService.getBRF81View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF082":
			repsummary = brf82ReportService.getBRF82View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF083":
			repsummary = brf83ReportService.getBRF83View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF084":
			repsummary = brf84ReportService.getBRF84View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF085":
			repsummary = brf85ReportService.getBRF85View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF086":
			repsummary = brf86ReportService.getBRF86View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF087":
			repsummary = brf87ReportService.getBRF87View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF088":
			repsummary = brf88ReportService.getBRF88View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF092":
			repsummary = brf92ReportService.getBRF92View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF032A":
			repsummary = brf32ReportService.getBRF32View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF151":
			repsummary = brf151ReportService.getBRF151View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF152":
			repsummary = brf152ReportService.getBRF152View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF153":
			repsummary = brf153ReportService.getBRF153View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF154":
			repsummary = brf154ReportService.getBRF154View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF155":
			repsummary = brf155ReportService.getBRF155View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF156":
			repsummary = brf156ReportService.getBRF156View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF051":
			repsummary = brf051ReportService.getBRF051View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF052":
			repsummary = brf052ReportService.getBRF052View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF053":
			repsummary = brf53ReportService.getBRF053View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF054":
			repsummary = brf054ReportService.getBRF054View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF056":
			repsummary = brf056ReportService.getBRF056View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF057":
			repsummary = brf057ReportService.getBRF057View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF059":
			repsummary = brf59ReportService.getBRF059View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF060":
			repsummary = brf060ReportService.getBRF060View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF062":
			repsummary = brf062ReportService.getBRF062View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF068":
			repsummary = brf068ReportService.getBRF068View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF069":
			repsummary = brf069ReportService.getBRF069View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF204A":
			repsummary = brf204AReportService.getBRF204AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF205A":
			repsummary = brf205AReportService.getBRF205AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRFBankingbook":
			repsummary = banking_bookReportService.getBRFBanking_BookView(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF95FB":
			repsummary = brf95_fund_ReportService.getBRFFB_View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF206A":
			repsummary = brf206AReportService.getBRF206AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF207A":
			repsummary = brf207AReportService.getBRF207AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF208A":
			repsummary = brf208AReportService.getBRF208AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF209A":
			repsummary = brf209AReportService.getBRF209AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF210A":
			repsummary = brf210AReportService.getBRF210AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF201A":
			repsummary = brf201AReportService.getBRF201AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF200A":
			repsummary = brf200AReportService.getBRF200AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF202A":
			repsummary = brf202AReportService.getBRF202AView(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF301A":
			repsummary = brf301ReportService.getBRF301View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF108A":
			repsummary = brf108ReportService.getBRF108View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF065":
			repsummary = brf65ReportService.getBRF065View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF064":
			repsummary = brf64ReportService.getBRF064View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF094A":
			repsummary = brf094ReportService.getBRF094View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "MR9":

			repsummary = baselmr9ReportService.getBASELMR9View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF071":
			repsummary = brf71ReportService.getBRF71View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF036A":
			repsummary = brf036ReportService.getBRF036View(reportId, fromdate, todate, currency, dtltype, pageable,
					srl_no);
			break;
		case "BRF037A":
			repsummary = brf37ReportService.getBRF37View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF038A":
			repsummary = brf38ReportService1.getBRF38View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF039A":
			repsummary = brf039ReportService.getBRF039View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF040A":
			repsummary = brf40ReportService.getBRF40View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF042A":
			repsummary = brf42ReportService.getBRF42View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF043A":
			repsummary = brf43ReportService.getBRF43View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF047A":
			repsummary = brf47ReportService.getBRF47View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF048A":
			repsummary = brf48ReportService.getBRF48View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF031A":
			repsummary = brf031ReportService.getBRF031View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF044A":
			repsummary = brf44ReportService.getBRF44View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF045A":
			repsummary = brf45ReportService.getBRF45View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF050A":
			repsummary = brf50ReportService.getBRF50View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF041A":
			repsummary = brf41ReportService.getBRF41View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF067":
			repsummary = brf67ReportService.getBRF67View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF007":
			repsummary = brf007ReportService.getBRF007View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF008":
			repsummary = brf008ReportService.getBRF008View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF009":

			repsummary = brf009ReportService.getBRF009View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF011":
			repsummary = brf011ReportService.getBRF011View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF033A":
			repsummary = brf033ReportService.getBRF033View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF034A":
			repsummary = brf034ReportService.getBRF034View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF035A":

			repsummary = BRF35ReportService.getBRF035View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF010":
			repsummary = brf010ReportService.getBRF010View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF012":
			repsummary = brf012ReportService.getBRF012View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;
		case "BRF013":

			repsummary = brf013ReportService.getBRF013View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		case "BRF014":
			repsummary = brf014ReportService.getBRF014View(reportId, fromdate, todate, currency, dtltype, pageable);
			break;

		}

		return repsummary;
	}

	public ModelAndView getReportDetails(String reportId, String instanceCode, String asondate, String fromdate,
			String todate, String currency, String reportingTime, String dtltype, String subreportid, String secid,
			Pageable pageable, String Filter,String searchVal) {

		ModelAndView repdetail = new ModelAndView();
		logger.info("Getting Details for the Report :" + reportId);

		switch (reportId) {

		case "OR1":
			repdetail = basel_OR1_ReportService.getOR1currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF046A":
			repdetail = brf46ReportService.getBRF46currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "CR1":
			repdetail = baselb2ReportService.getBASELB2Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "CR2":
			repdetail = baselb3ReportService.getBASELB3Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "CR3":
			repdetail = baselCR3_REPORTService.getB6currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "CR4":
			repdetail = b7_ReportService.getB7currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "FX (MR5)":
			repdetail = b33ReportService.getB33Details(reportId, fromdate, todate, currency, dtltype, pageable, Filter);
			break;
		case "FX (MR5a)":
			repdetail = b34ReportService.getB34Details(reportId, fromdate, todate, currency, dtltype, pageable, Filter);
			break;
		case "CR2a":
			repdetail = b4ReportService.getB4Details(reportId, fromdate, todate, currency, dtltype, pageable, Filter);
			break;
		case "MR2":
			repdetail = b15ReportService.getB15Details(reportId, fromdate, todate, currency, dtltype, pageable, Filter);
			break;
		case "MR2_AED":
			repdetail = b16ReportService.getB16Details(reportId, fromdate, todate, currency, dtltype, pageable, Filter);
			break;
		case "MR2_EUR":
			repdetail = b17ReportService.getB17Details(reportId, fromdate, todate, currency, dtltype, pageable, Filter);
			break;

		case "BRF093A":
			repdetail = brf93ReportService.getBRF093ADetails(reportId, fromdate, todate, currency, dtltype, pageable,Filter);
			break;

		case "MR6":
			repdetail = baselEQUALITY_ReportService.getB35currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR7":
			repdetail = bascelMR6_ReportService.getB36currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF95-CAR":
			repdetail = BASEL002AReportService.getB1currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "SEC":
			repdetail = baselsecReportService.getBASELSECDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "SEC-TB":
			repdetail = b18baselsectradReportService.getB18BASELSECTRADDetails(reportId, fromdate, todate, currency,
					dtltype, pageable, Filter);
			break;
		case "MR3":
			repdetail = b19to25baselReportService.getB19TO25BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR3-USD":
			repdetail = b20baselReportService.getB20BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR3-GBP":
			repdetail = b21baselReportService.getB21BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR3-EUR":
			repdetail = b22baselReportService.getB22BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR3-AED":
			repdetail = b23baselReportService.getB23BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR3-FUR":
			repdetail = b24baselReportService.getB24BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR3-RES":
			repdetail = b25baselReportService.getB25BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR4-RES":
			repdetail = b32baselReportService.getB32BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR4-FUR":
			repdetail = b31baselReportService.getB31BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR4":
			repdetail = b26baselReportService.getB26BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR4-USD":
			repdetail = b27baselReportService.getB27BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR4-GBP":
			repdetail = b28baselReportService.getB28BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR4-EUR":
			repdetail = b29baselReportService.getB29BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR4-AED":
			repdetail = b30baselReportService.getB30BASELDetails(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF070A":
			repdetail = brf070AReportService.getBRF070AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF066A":
			repdetail = brf066AReportService.getBRF066ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF095A":
			repdetail = brf095AReportService.getBRF095ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);

			break;
		case "BRF096A":
			repdetail = brf96AReportService.getBRF96ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF102A":
			repdetail = brf102AReportService.getBRF102ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF104A":
			repdetail = brf104AReportService.getBRF104ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF105A":
			repdetail = brf105AReportService.getBRF105ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF106A":
			repdetail = brf106AReportService.getBRF106ADetails(reportId, fromdate, todate, dtltype, pageable, Filter);
			break;
		case "BRF107A":
			repdetail = brf107AReportService.getBRF107ADetails(reportId, fromdate, todate, dtltype, pageable, Filter);
			break;
		case "BRF300A":
			repdetail = brf300AReportService.getBRF300ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF100A":
			repdetail = brf100AReportService.getBRF100currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF101A":
			repdetail = brf101ReportService.getBRF101currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		/*
		 * case "BRF103A": repdetail = brf103AReportService.getBRF103ADetails(reportId,
		 * fromdate, todate, currency, dtltype, pageable,Filter); break;
		 */
		case "BRF099A":
			repdetail = brf99AReportService.getBRF99AcurrentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF181A":
			repdetail = brf181AReportService.getBRF181ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF073":
			repdetail = brf73ReportService.getBRF73Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF074":
			repdetail = brf74ReportService.getBRF74Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF076":
			repdetail = brf76ReportService.getBRF76Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF077":
			repdetail = brf77ReportService.getBRF77Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF078":
			repdetail = brf78ReportService.getBRF78Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF079":
			repdetail = brf79ReportService.getBRF79Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF080":
			repdetail = brf80ReportService.getBRF80currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF081":
			repdetail = brf81ReportService.getBRF81Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF082":
			repdetail = brf82ReportService.getBRF82Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF083":
			repdetail = brf83ReportService.getBRF83Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF084":
			repdetail = brf84ReportService.getBRF84Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF085":
			repdetail = brf85ReportService.getBRF85Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF086":
			repdetail = brf86ReportService.getBRF86Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF087":
			repdetail = brf87ReportService.getBRF87Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF088":
			repdetail = brf88ReportService.getBRF88Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF092":
			repdetail = brf92ReportService.getBRF92Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF036A":
			repdetail = brf036ReportService.getBRF036currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF065":
			repdetail = brf65ReportService.getBRF065currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF064":
			repdetail = brf64ReportService.getBRF064currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF094A":
			repdetail = brf094ReportService.getBRF094currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

//		  case "BRF094A": repdetail = BRF094AReportService.getBRF094ADetails(reportId,
//		  fromdate, todate, currency, dtltype, pageable, Filter); break;
//		 

		case "MR8":
			repdetail = bascelMR8_ReportService.getMR8currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "MR9":

			repdetail = baselmr9ReportService.getBASELMR9currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF067":
			repdetail = brf67ReportService.getBRF67currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF069":
			repdetail = brf069ReportService.getBRF069Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF071":
			repdetail = brf71ReportService.getBRF71currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF032A":
			repdetail = brf32ReportService.getBRF32Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF151":
			repdetail = brf151ReportService.getBRF151Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF152":
			repdetail = brf152ReportService.getBRF152Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF153":
			repdetail = brf153ReportService.getBRF153Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF154":
			repdetail = brf154ReportService.getBRF154currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF155":
			repdetail = brf155ReportService.getBRF155Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF156":
			repdetail = brf156ReportService.getBRF156Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF051":
			repdetail = brf051ReportService.getBRF051Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF052":
			repdetail = brf052ReportService.getBRF052Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF053":
			repdetail = brf53ReportService.getBRF053Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF054":
			repdetail = brf054ReportService.getBRF054Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF056":
			repdetail = brf056ReportService.getBRF056Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF057":
			repdetail = brf057ReportService.getBRF057Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF059":
			repdetail = brf59ReportService.getBRF059Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF060":
			repdetail = brf060ReportService.getBRF060Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF062":
			repdetail = brf062ReportService.getBRF062Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF068":
			repdetail = brf068ReportService.getBRF068Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF204A":
			repdetail = brf204AReportService.getBRF204AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF205A":
			repdetail = brf205AReportService.getBRF205AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		/* 206 */
		case "BRF206A":
			repdetail = brf206AReportService.getBRF206ADetails(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF207A":
			repdetail = brf207AReportService.getBRF207AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF208A":
			repdetail = brf208AReportService.getBRF208AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF209A":
			repdetail = brf209AReportService.getBRF209AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF210A":
			repdetail = brf210AReportService.getBRF210AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF201A":
			repdetail = brf201AReportService.getBRF201AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF200A":
			repdetail = brf200AReportService.getBRF200AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF202A":
			repdetail = brf202ReportService.getBRF202currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF037A":

			repdetail = brf37ReportService.getBRF37currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);

			break;

		case "BRF038A":
			repdetail = brf38ReportService1.getBRF038currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF039A":
			repdetail = brf039ReportService.getBRF039currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF040A":
			repdetail = brf40ReportService.getBRF40currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF042A":
			repdetail = brf42ReportService.getBRF42currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF043A":
			repdetail = brf43ReportService.getBRF043currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF047A":
			repdetail = brf47ReportService.getBRF47currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF048A":
			repdetail = brf48ReportService.getBRF48currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF031A":
			repdetail = brf031ReportService.getBRF031currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF044A":
			repdetail = brf44ReportService.getBRF44currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF103A":
			repdetail = brf103AReportService.getBRF103currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF045A":
			repdetail = brf45ReportService.getBRF45currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF050A":
			repdetail = brf50ReportService.getBRF50currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF041A":
			repdetail = brf41ReportService.getBRF41currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF001":
			repdetail = brf001ReportService.getBRF001currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter,searchVal);
			break;

		case "BRF002":
			repdetail = brf002ReportService.getBRF002currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter,searchVal);
			break;

		case "BRF005":
			repdetail = brf005ReportService.getBRF005currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF003":
			repdetail = brf003ReportService.getBRF003currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter,searchVal);
			break;

		case "BRF010":
			repdetail = brf010ReportService.getBRF010currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF014":
			repdetail = brf014ReportService.getBRF014currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF012":
			repdetail = brf012ReportService.getBRF012currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF013":
			repdetail = brf013ReportService.getBRF013currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF004":
			repdetail = brf004ReportService.getBRF004currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter,searchVal);
			break;
		case "BRF007":
			repdetail = brf007ReportService.getBRF007currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF008":
			repdetail = brf008ReportService.getBRF008currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF009":
			repdetail = brf009ReportService.getBRF009currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "BRF011":
			repdetail = brf011ReportService.getBRF011currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF033A":
			repdetail = brf033ReportService.getBRF033currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF049A":
			repdetail = brf49ReportService.getBRF49currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			
		case "BRF109A":
			repdetail = brf109ReportService.getBRF109currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF034A":
			repdetail = brf034ReportService.getBRF034currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF035A":
			repdetail = BRF35ReportService.getBRF035currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "SR1":
			repdetail = basell_12_ReportService.getB12currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "CR4a":
			repdetail = basel_8_ReportService.getB8currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "CR4b (fx)":
			repdetail = basel_9_ReportService.getB9currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "CR2b":
			repdetail = b5ReportService.getB5currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "EIF":
			repdetail = b10ReportService.getB10currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		case "CV1":
			repdetail = b13ReportService.getB13currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "MR1":
			repdetail = b14ReportService.getB14currentDtl(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;
		case "BRF0001":
			repdetail = brf001_FORT_SERVIVE.getBRF0001currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter,searchVal);
			break;

		}
		return repdetail;
	}

	public ModelAndView getReportDetails1(String reportId, String instanceCode, String asondate, String fromdate,
			String todate, String currency, String reportingTime, String dtltype, String subreportid, String secid,
			Pageable pageable, String Filter,String searchVal) {

		ModelAndView repdetail = new ModelAndView();
		logger.info("Getting Details for the Report :" + reportId);

		switch (reportId) {

		case "BRF032A":
			repdetail = brf32ReportService.getBRF32Details(reportId, fromdate, todate, currency, dtltype, pageable,
					Filter);
			break;

		}
		return repdetail;
	}

	public File getDownloadFile(String reportId, String asondate, String fromdate, String todate, String currency,
			String subreportid, String secid, String dtltype, String reportingTime, String filetype,
			String instancecode, String filter) throws JRException, SQLException, IOException {

		File repfile = null;

		logger.info("Getting Report File for : " + reportId + " in " + filetype + " format");

		switch (reportId) {
		case "OR1":
			repfile = basel_OR1_ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF0001":
			repfile = brf001_FORT_SERVIVE.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "MR8":
			repfile = bascelMR8_ReportService.getFileMR8(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "MR6":
			repfile = baselEQUALITY_ReportService.getFileB35(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR7":
			repfile = bascelMR6_ReportService.getFileB36(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "CR3":
			repfile = baselCR3_REPORTService.getFileB6(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "CR7":
			repfile = b7_ReportService.getFileB7(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "FX (MR5)":
			repfile = b33ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "FX (MR5a)":
			repfile = b34ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "CR2a":
			repfile = b4ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "MR2":
			repfile = b15ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "MR2_AED":
			repfile = b16ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "MR2_EUR":
			repfile = b17ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF001":
			repfile = brf001ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype, filter);
			break;

		case "BRF046A":
			repfile = brf46ReportService.getFileBRF46(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF049A":
			repfile = brf49ReportService.getFileBRF49(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
			
		case "BRF109A":
			repfile = brf109ReportService.getFileBRF109(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF002":
			repfile = brf002ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype, filter);
			break;
		case "BRF005":
			repfile = brf005ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF003":
			repfile = brf003ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF004":
			repfile = brf004ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF073":
			repfile = brf73ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF074":
			repfile = brf74ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF076":
			repfile = brf76ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF077":
			repfile = brf77ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF078":
			repfile = brf78ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF079":
			repfile = brf79ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF080":
			repfile = brf80ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF081":
			repfile = brf81ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);

			break;

		case "BRF082":
			repfile = brf82ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF083":
			repfile = brf83ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF084":
			repfile = brf84ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF085":
			repfile = brf85ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF086":
			repfile = brf86ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF087":
			repfile = brf87ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF088":
			repfile = brf88ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);

			break;

		case "BRF092":
			repfile = brf92ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF036A":
			repfile = brf036ReportService.getFileBRF036(reportId, fromdate, todate, currency, dtltype, filetype);

			break;

		case "BRF070A":
			repfile = brf070AReportService.getFileBRF070A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF066A":
			repfile = brf066AReportService.getFileBRF066A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		
		case "BRF093A":
            repfile = brf93ReportService.getFileBRF093(reportId, fromdate, todate, currency, dtltype, filetype);
            break;
		
		/*
		 * case "BRF094A": repfile = BRF094AReportService.getFileBRF094A(reportId,
		 * fromdate, todate, currency, dtltype, filetype); break;
		 */

			
//		  case "BRF094A": repfile = BRF094AReportService.getFileBRF094A(reportId,
//		  fromdate, todate, currency, dtltype, filetype); break;
//		 \

		case "BRF095A":
			repfile = brf095AReportService.getFileBRF095A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF95-CAR":
			repfile = BASEL002AReportService.getFileB1(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "CR2b":
			repfile = b5ReportService.getFileB5(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "EIF":
			repfile = b10ReportService.getFileB10(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "CV1":
			repfile = b13ReportService.getFileB13(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "CR2":
			repfile = baselb3ReportService.getFileBASELB3(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "CR1":
			repfile = baselb2ReportService.getFileBASELB2(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "SEC":
			repfile = baselsecReportService.getFileBASELSEC(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "MR1":
			repfile = b14ReportService.getFileB14(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "SEC-TB":
			repfile = b18baselsectradReportService.getFileB18BASELSECTRAD(reportId, fromdate, todate, currency, dtltype,
					filetype);
			break;

		case "MR3":
			repfile = b19to25baselReportService.getFileB19TO25BASEL(reportId, fromdate, todate, currency, dtltype,
					filetype);
			break;

		case "MR3-USD":
			repfile = b20baselReportService.getFileB20BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR3-GBP":
			repfile = b21baselReportService.getFileB21BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR3-EUR":
			repfile = b22baselReportService.getFileB22BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR3-AED":
			repfile = b23baselReportService.getFileB23BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR3-FUR":
			repfile = b24baselReportService.getFileB24BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR3-RES":
			repfile = b25baselReportService.getFileB25BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR4-RES":
			repfile = b32baselReportService.getFileB32BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR4-FUR":
			repfile = b31baselReportService.getFileB31BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR4":
			repfile = b26baselReportService.getFileB26BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR4-USD":
			repfile = b27baselReportService.getFileB27BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR4-GBP":
			repfile = b28baselReportService.getFileB28BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR4-EUR":
			repfile = b29baselReportService.getFileB29BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR4-AED":
			repfile = b30baselReportService.getFileB30BASEL(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF096A":
			repfile = brf96AReportService.getFileBRF96A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF102A":
			repfile = brf102AReportService.getFileBRF102A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF104A":
			repfile = brf104AReportService.getFileBRF104A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF105A":
			repfile = brf105AReportService.getFileBRF105A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF106A":
			repfile = brf106AReportService.getFileBRF106A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF107A":
			repfile = brf107AReportService.getFileBRF107A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF300A":
			repfile = brf300AReportService.getFileBRF300A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF100A":
			repfile = brf100AReportService.getFileBRF100A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF101A":
			repfile = brf101ReportService.getFileBRF101(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF103A":
			repfile = brf103AReportService.getFileBRF103A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF099A":
			repfile = brf99AReportService.getFileBRF99A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF181A":
			repfile = brf181AReportService.getFileBRF181A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF032A":
			repfile = brf32ReportService.getFileBRF32(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF151":
			repfile = brf151ReportService.getFileBRF151(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF152":
			repfile = brf152ReportService.getFileBRF152(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF153":
			repfile = brf153ReportService.getFileBRF153(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF154":
			repfile = brf154ReportService.getFileBRF154(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF155":
			repfile = brf155ReportService.getFileBRF155(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF156":
			repfile = brf156ReportService.getFileBRF156(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF051":
			repfile = brf051ReportService.getFileBRF051(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF052":
			repfile = brf052ReportService.getFileBRF052(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF053":
			repfile = brf53ReportService.getFileBRF053(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF054":
			repfile = brf054ReportService.getFileBRF054(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF056":
			repfile = brf056ReportService.getFileBRF056(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF057":
			repfile = brf057ReportService.getFileBRF057(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF059":
			repfile = brf59ReportService.getFileBRF059(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF060":
			repfile = brf060ReportService.getFileBRF060(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF062":
			repfile = brf062ReportService.getFileBRF062(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF068":
			repfile = brf068ReportService.getFileBRF068(reportId, fromdate, todate, currency, dtltype, filetype);

			break;

		case "BRF069":
			repfile = brf069ReportService.getFileBRF069(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF065":
			repfile = brf65ReportService.getFileBRF065(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF064":
			repfile = brf64ReportService.getFileBRF064(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF094A":
			repfile = brf094ReportService.getFileBRF094(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "MR9":

			repfile = baselmr9ReportService.getFileBASELMR9(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF067":
			repfile = brf67ReportService.getFileBRF67(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF071":
			repfile = brf71ReportService.getFileBRF71(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF037A":
			repfile = brf37ReportService.getFileBRF37(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF038A":
			repfile = brf38ReportService1.getFileBRF038(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF039A":
			repfile = brf039ReportService.getFileBRF039(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF040A":
			repfile = brf40ReportService.getFileBRF40(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF042A":
			repfile = brf42ReportService.getFileBRF42(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF043A":
			repfile = brf43ReportService.getFileBRF43(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF047A":
			repfile = brf47ReportService.getFileBRF47(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF048A":
			repfile = brf48ReportService.getFileBRF48(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF031A":
			repfile = brf031ReportService.getFileBRF031(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF044A":
			repfile = brf44ReportService.getFileBRF44(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF045A":
			repfile = brf45ReportService.getFileBRF45(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF050A":
			repfile = brf50ReportService.getFileBRF50(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF041A":
			repfile = brf41ReportService.getFileBRF41(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF007":
			repfile = brf007ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF008":
			repfile = brf008ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF009":
			repfile = brf009ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF010":
			repfile = brf010ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF011":
			repfile = brf011ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF033A":
			repfile = brf033ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF034A":
			repfile = brf034ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF035A":
			repfile = BRF35ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF012":
			repfile = brf012ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF013":
			repfile = brf013ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF014":

			repfile = brf014ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "SR1":
			repfile = basell_12_ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "CR4a":
			repfile = basel_8_ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "CR4b (fx)":
			repfile = basel_9_ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);

			break;
		case "BRF204A":
			repfile = brf204AReportService.getFileBRF204A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF205A":
			repfile = brf205AReportService.getFileBRF205A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF206A":
			repfile = brf206AReportService.getFileBRF206A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF207A":
			repfile = brf207AReportService.getFileBRF207A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF208A":
			repfile = brf208AReportService.getFileBRF208A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF209A":
			repfile = brf209AReportService.getFileBRF209A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRFBankingbook":
			repfile = banking_bookReportService.getFileBanking_Book(reportId, fromdate, todate, currency, dtltype,
					filetype);
			break;
			
		case "BRF95FB":
			repfile = brf95_fund_ReportService.getFileBRF95(reportId, fromdate, todate, currency, dtltype,
					filetype);
			break;
		/*
		 * case "BRF201A": repfile = brf201AReportService.getFile(reportId, fromdate,
		 * todate, currency, dtltype, filetype); break;
		 * 
		 * case "BRF200A": repfile = brf200AReportService.getFile(reportId, fromdate,
		 * todate, currency, dtltype, filetype); break;
		 */

		case "BRF210A":
			repfile = brf210AReportService.getFileBRF210A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF201A":
			repfile = brf201AReportService.getFileBRF201A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF200A":
			repfile = brf200AReportService.getFileBRF200A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF202A":
			repfile = brf202AReportService.getFileBRF202A(reportId, fromdate, todate, currency, dtltype, filetype);
			break;
		case "BRF301A":
			repfile = brf301ReportService.getFileBRF301(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "BRF108A":
			repfile = brf108ReportService.getFileBRF108(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		case "CR4":
			repfile = b7_ReportService.getFileB7(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		}

		return repfile;

	}

	public String preCheckReportRBS(String reportid, String fromdate, String todate) {

		String msg = "";

		logger.info("Report precheck : " + reportid);

		switch (reportid) {

		case "OR1":
			msg = basel_OR1_ReportService.preCheck(reportid, fromdate, todate);

			break;
		case "BRF0001":
			msg = brf001_FORT_SERVIVE.preCheck(reportid, fromdate, todate);

			break;

		case "MR8":
			msg = bascelMR8_ReportService.preCheck(reportid, fromdate, todate);

			break;

		case "BRFBankingbook":
			msg = banking_bookReportService.preCheck(reportid, fromdate, todate);

			break;

		case "BRF95FB":
			msg = brf95_fund_ReportService.preCheck(reportid, fromdate, todate);

			break;

		case "BRF032A":
			msg = brf32ReportService.preCheckBRF32(reportid, fromdate, todate);

			break;

		case "FX (MR5)":
			msg = b33ReportService.preCheckB33(reportid, fromdate, todate);

			break;
		case "FX (MR5a)":
			msg = b34ReportService.preCheckB34(reportid, fromdate, todate);

			break;

		case "BRF068":
			msg = brf068ReportService.preCheckBRF068(reportid, fromdate, todate);

			break;

		case "BRF069":
			msg = brf069ReportService.preCheckBRF069(reportid, fromdate, todate);
			break;

		case "CR2a":
			msg = b4ReportService.preCheckB4(reportid, fromdate, todate);

			break;
		case "MR2":
			msg = b15ReportService.preCheckB15(reportid, fromdate, todate);
			break;
		case "MR2_AED":
			msg = b16ReportService.preCheckB16(reportid, fromdate, todate);
			break;
		case "MR2_EUR":
			msg = b17ReportService.preCheckB17(reportid, fromdate, todate);
			break;

		case "BRF046A":
			msg = brf46ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF049A":
			msg = brf49ReportService.preCheck(reportid, fromdate, todate);
			break;
			
		case "BRF109A":
			msg = brf109ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF002":
			msg = brf002ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF151":
			msg = brf151ReportService.preCheckBRF151(reportid, fromdate, todate);
			break;

		case "BRF152":
			msg = brf152ReportService.preCheckBRF152(reportid, fromdate, todate);
			break;
		case "BRF153":
			msg = brf153ReportService.preCheckBRF153(reportid, fromdate, todate);
			break;
		case "BRF154":
			msg = brf154ReportService.preCheckBRF154(reportid, fromdate, todate);
			break;
		case "BRF155":
			msg = brf155ReportService.preCheckBRF155(reportid, fromdate, todate);
			break;
		case "BRF156":
			msg = brf156ReportService.preCheckBRF156(reportid, fromdate, todate);
			break;

		case "BRF051":
			msg = brf051ReportService.preCheckBRF051(reportid, fromdate, todate);
			break;

		case "BRF052":
			msg = brf052ReportService.preCheckBRF052(reportid, fromdate, todate);
			break;
		case "BRF053":
			msg = brf53ReportService.preCheckBRF053(reportid, fromdate, todate);
			break;
		case "BRF054":
			msg = brf054ReportService.preCheckBRF054(reportid, fromdate, todate);
			break;
		case "BRF056":
			msg = brf056ReportService.preCheckBRF056(reportid, fromdate, todate);
			break;
		case "BRF057":
			msg = brf057ReportService.preCheckBRF057(reportid, fromdate, todate);
			break;
		case "BRF059":
			msg = brf59ReportService.preCheckBRF059(reportid, fromdate, todate);
			break;
		case "BRF060":
			msg = brf060ReportService.preCheckBRF060(reportid, fromdate, todate);
			break;
		case "BRF062":
			msg = brf062ReportService.preCheckBRF062(reportid, fromdate, todate);
			break;
		case "BRF073":
			msg = brf73ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF074":
			msg = brf74ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF070A":
			msg = brf070AReportService.preCheckBRF070A(reportid, fromdate, todate);
			break;
		case "BRF066A":
			msg = brf066AReportService.preCheckBRF066A(reportid, fromdate, todate);
			break;

		case "BRF093A":
			msg = brf93ReportService.preCheckBRF093A(reportid, fromdate, todate);
			break;

		case "BRF013":
			msg = brf013ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF095A":
			msg = brf095AReportService.preCheckBRF095A(reportid, fromdate, todate);

			break;

		case "BRF95-CAR":
			msg = BASEL002AReportService.preCheckB1(reportid, fromdate, todate);
			break;

		case "CR1":
			msg = baselb2ReportService.preCheckBASELB2(reportid, fromdate, todate);
			break;
		case "CR2":
			msg = baselb3ReportService.preCheckBASELB3(reportid, fromdate, todate);
			break;

		case "SEC":
			msg = baselsecReportService.preCheckBASELSEC(reportid, fromdate, todate);

			break;

		case "SEC-TB":
			msg = b18baselsectradReportService.preCheckB18BASELSECTRAD(reportid, fromdate, todate);
			break;

		case "MR3":
			msg = b19to25baselReportService.preCheckB19TO25BASEL(reportid, fromdate, todate);
			break;

		case "MR3-USD":
			msg = b20baselReportService.preCheckB20BASEL(reportid, fromdate, todate);
			break;

		case "MR3-GBP":
			msg = b21baselReportService.preCheckB21BASEL(reportid, fromdate, todate);
			break;
		case "MR3-EUR":
			msg = b22baselReportService.preCheckB22BASEL(reportid, fromdate, todate);
			break;
		case "MR3-AED":
			msg = b23baselReportService.preCheckB23BASEL(reportid, fromdate, todate);
			break;

		case "MR3-FUR":
			msg = b24baselReportService.preCheckB24BASEL(reportid, fromdate, todate);
			break;

		case "MR3-RES":
			msg = b25baselReportService.preCheckB25BASEL(reportid, fromdate, todate);
			break;

		case "MR4":
			msg = b26baselReportService.preCheckB26BASEL(reportid, fromdate, todate);
			break;

		case "MR4-FUR":
			msg = b31baselReportService.preCheckB31BASEL(reportid, fromdate, todate);
			break;

		case "MR4-RES":
			msg = b32baselReportService.preCheckB32BASEL(reportid, fromdate, todate);
			break;

		case "MR4-USD":
			msg = b27baselReportService.preCheckB27BASEL(reportid, fromdate, todate);
			break;

		case "MR4-GBP":
			msg = b28baselReportService.preCheckB28BASEL(reportid, fromdate, todate);
			break;

		case "MR4-EUR":
			msg = b29baselReportService.preCheckB29BASEL(reportid, fromdate, todate);
			break;

		case "MR4-AED":
			msg = b30baselReportService.preCheckB30BASEL(reportid, fromdate, todate);
			break;

		case "BRF104A":
			msg = brf104AReportService.preCheckBRF104A(reportid, fromdate, todate);
			break;
		case "BRF105A":
			msg = brf105AReportService.preCheckBRF105A(reportid, fromdate, todate);
			break;
		case "BRF100A":
			msg = brf100AReportService.preCheckBRF100A(reportid, fromdate, todate);
			break;
		case "BRF101A":
			msg = brf101ReportService.preCheckBRF101(reportid, fromdate, todate);
			break;
		case "BRF102A":
			msg = brf102AReportService.preCheckBRF102A(reportid, fromdate, todate);
			break;
		case "BRF181A":
			msg = brf181AReportService.preCheckBRF181A(reportid, fromdate, todate);
			break;
		case "BRF103A":
			msg = brf103AReportService.preCheckBRF103A(reportid, fromdate, todate);
			break;
		case "BRF106A":
			msg = brf106AReportService.preCheckBRF106A(reportid, fromdate, todate);
			break;

		case "BRF107A":
			msg = brf107AReportService.preCheckBRF107A(reportid, fromdate, todate);
			break;
		case "BRF300A":
			msg = brf300AReportService.preCheckBRF300A(reportid, fromdate, todate);
			break;
		case "BRF099A":
			msg = brf99AReportService.preCheckBRF99A(reportid, fromdate, todate);
			break;

		case "BRF096A":
			msg = brf96AReportService.preCheckBRF96A(reportid, fromdate, todate);
			break;

		case "BRF076":
			msg = brf76ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF077":
			msg = brf77ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF078":
			msg = brf78ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF079":
			msg = brf79ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF080":
			msg = brf80ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF081":
			msg = brf81ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF082":
			msg = brf82ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF083":
			msg = brf83ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF084":
			msg = brf84ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF085":
			msg = brf85ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF086":
			msg = brf86ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF087":
			msg = brf87ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF088":
			msg = brf88ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF092":
			msg = brf92ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF036A":
			msg = brf036ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF065":
			msg = brf65ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF064":
			msg = brf64ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF094A":
			msg = brf094ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF067":
			msg = brf67ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF200A":
			msg = brf200AReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF201A":
			msg = brf201AReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF202A":
			msg = brf202AReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF301A":
			msg = brf301ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF108A":
			msg = brf108ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF204A":
			msg = brf204AReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF205A":
			msg = brf205AReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF206A":
			msg = brf206AReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF207A":
			msg = brf207AReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF208A":
			msg = brf208AReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF209A":
			msg = brf209AReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF210A":
			msg = brf210AReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF037A":
			msg = brf37ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF038A":
			msg = brf38ReportService1.preCheck(reportid, fromdate, todate);
			break;
		case "BRF039A":
			msg = brf039ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF040A":
			msg = brf40ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF042A":
			msg = brf42ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF043A":
			msg = brf43ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF047A":
			msg = brf47ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF048A":
			msg = brf48ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF031A":
			msg = brf031ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF044A":
			msg = brf44ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF045A":
			msg = brf45ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF050A":
			msg = brf50ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF041A":
			msg = brf41ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF001":
			msg = brf001ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF005":
			msg = brf005ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF003":
			msg = brf003ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF004":
			msg = brf004ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF007":
			msg = brf007ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF008":
			msg = brf008ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF009":
			msg = brf009ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF011":
			msg = brf011ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF014":
			msg = brf014ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF033A":
			msg = brf033ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF034A":
			msg = brf034ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF035A":
			msg = BRF35ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF010":
			msg = brf010ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "BRF012":
			msg = brf012ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "MR6":
			msg = baselEQUALITY_ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "MR7":
			msg = bascelMR6_ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "CR3":
			msg = baselCR3_REPORTService.preCheck(reportid, fromdate, todate);
			break;
		case "CR4":
			msg = b7_ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "CR2b":
			msg = b5ReportService.preCheckB5(reportid, fromdate, todate);
			break;
		case "SR1":
			msg = basell_12_ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "CR4a":
			msg = basel_8_ReportService.preCheck(reportid, fromdate, todate);
			break;
		case "CR4b (fx)":
			msg = basel_9_ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "MR9":
			msg = baselmr9ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "BRF071":
			msg = brf71ReportService.preCheck(reportid, fromdate, todate);
			break;

		case "EIF":
			msg = b10ReportService.preCheckB10(reportid, fromdate, todate);
			break;

		case "CV1":
			msg = b13ReportService.preCheckB13(reportid, fromdate, todate);
			break;

		case "MR1":
			msg = b14ReportService.preCheckB14(reportid, fromdate, todate);
			break;

		default:
			logger.info("default -> preCheck()");
			msg = "Master - need to process";
		}

		return msg;
	}

	public File getDownloadFile1(String reportId, String asondate, String fromdate, String todate, String currency,
			String subreportid, String secid, String dtltype, String reportingTime, String filetype,
			String instancecode) throws FileNotFoundException, JRException, SQLException {

		File repfile = null;

		logger.info("Getting Report File for : " + reportId + " in " + filetype + " format");

		switch (reportId) {
		case "OR1":
			repfile = basel_OR1_ReportService.getFile(reportId, fromdate, todate, currency, dtltype, filetype);
			break;

		}
		return repfile;

	}

	/****** arch view ****/
	public ModelAndView getArchiveReportView(String reportId, String reportDate, String fromdate, String todate,
			String currency, String dtltype, String subreportid, String secid, String reportingTime, Pageable pageable,
			BigDecimal srl_no,String type) {

		ModelAndView repsummary = new ModelAndView();

		logger.info("Getting View for the Report :" + reportId);
		switch (reportId) {

		case "BRF001":
			repsummary = brf001ReportService.getArchieveBRF001View(reportId, fromdate, todate, currency, dtltype,
					pageable,type);
			break;
		case "BRF0001":
			repsummary = brf001_FORT_SERVIVE.getArchieveBRF0001View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF002":
			repsummary = brf002ReportService.getArchieveBRF002View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF012":
			repsummary = brf012ReportService.getArchieveBRF012View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF010":
			repsummary = brf010ReportService.getArchieveBRF010View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF008":
			repsummary = brf008ReportService.getArchieveBRF008View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF003":
			repsummary = brf003ReportService.getArchieveBRF003View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF004":
			repsummary = brf004ReportService.getArchieveBRF004View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF101A":
			repsummary = brf101ReportService.getArchieveBRF101View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF005":
			repsummary = brf005ReportService.getArchieveBRF005View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF007":
			repsummary = brf007ReportService.getArchieveBRF007View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF009":
			repsummary = brf009ReportService.getArchieveBRF009View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF011":
			repsummary = brf011ReportService.getArchieveBRF011View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF045A":
			repsummary = brf45ReportService.getArchieveBRF045View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF013":
			repsummary = brf013ReportService.getArchieveBRF013View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF031A":
			repsummary = brf031ReportService.getArchieveBRF031View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF032A":
			repsummary = brf32ReportService.getArchieveBRF032View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF033A":
			repsummary = brf033ReportService.getArchieveBRF033View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF034A":
			repsummary = brf034ReportService.getArchieveBRF034View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF035A":
			repsummary = BRF35ReportService.getArchieveBRF035View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF037A":
			repsummary = brf37ReportService.getArchieveBRF037View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF060":
			repsummary = brf060ReportService.getArchieveBRF060View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF038A":
			repsummary = brf38ReportService1.getArchieveBRF038View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF039A":
			repsummary = brf039ReportService.getArchieveBRF039View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF040A":
			repsummary = brf40ReportService.getArchieveBRF40View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF077":
			repsummary = brf77ReportService.getArchieveBRF077View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF046A":
			repsummary = brf46ReportService.getArchieveBRF046View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF056":
			repsummary = brf056ReportService.getArchieveBRF056View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF050A":
			repsummary = brf50ReportService.getArchieveBRF050View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF079":
			repsummary = brf79ReportService.getArchieveBRF079View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF080":
			repsummary = brf80ReportService.getArchieveBRF080View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF086":
			repsummary = brf86ReportService.getArchieveBRF086View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF151":
			repsummary = brf151ReportService.getArchieveBRF151View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF059":
			repsummary = brf59ReportService.getArchieveBRF059View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF084":
			repsummary = brf84ReportService.getArchieveBRF084View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		/*
		 * case "BRF060A": repsummary =
		 * brf060ReportService.getArchieveBRF060View(reportId, fromdate, todate,
		 * currency, dtltype, pageable); break;
		 */

		case "BRF047A":
			repsummary = brf47ReportService.getArchieveBRF047View(reportId, fromdate, todate, currency, dtltype,
					pageable);

			break;
		case "BRF067":
			repsummary = brf67ReportService.getArchieveBRF067View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF062":
			repsummary = brf062ReportService.getArchieveBRF062View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF153":
			repsummary = brf153ReportService.getArchieveBRF0153View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF043A":
			repsummary = brf43ReportService.getArchieveBRF043View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF154":
			repsummary = brf154ReportService.getArchieveBRF154View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF155":
			repsummary = brf155ReportService.getArchieveBRF155View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

//			case "BRF060A":
//				repsummary = brf060ReportService.getArchieveBRF060View(reportId, fromdate, todate, currency, dtltype, pageable);	
//				break;	

		case "BRF073":
			repsummary = brf73ReportService.getArchieveBRF073View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF083":
			repsummary = brf83ReportService.getArchieveBRF083View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF068":
			repsummary = brf068ReportService.getArchieveBRF068View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF103A":
			repsummary = brf103AReportService.getArchieveBRF103View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF044A":
			repsummary = brf044ReportService.getArchieveBRF044View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF081":
			repsummary = brf81ReportService.getArchieveBRF081View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF082":
			repsummary = brf82ReportService.getArchieveBRF082View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF074":
			repsummary = brf74ReportService.getArchieveBRF074View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF204A":
			repsummary = brf204AReportService.getArchieveBRF204View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF205A":
			repsummary = brf205AReportService.getArchieveBRF205View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF054":
			repsummary = brf054ReportService.getArchieveBRF54View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF066A":
			repsummary = brf066AReportService.getArchieveBRF066View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF071":
			repsummary = brf71ReportService.getArchieveBRF071View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF078":
			repsummary = brf78ReportService.getArchieveBRF078View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF092":
			repsummary = brf92ReportService.getArchieveBRF092View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF095A":
			repsummary = brf095AReportService.getArchieveBRF095View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF096A":
			repsummary = brf96AReportService.getArchieveBRF096View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF069":
			repsummary = brf069ReportService.getArchieveBRF069View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF107A":
			repsummary = brf107AReportService.getArchieveBRF0107View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF207A":
			repsummary = brf207AReportService.getArchieveBRF0207View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		/* sanjeevreport */

		case "BRF206A":
			repsummary = brf206AReportService.getArchieveBRF206View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF076":
			repsummary = brf76ReportService.getArchieveBRF076View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF057":
			repsummary = brf057ReportService.getArchieveBRF057View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF049A":
			repsummary = brf49ReportService.getArchieveBRF049View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
			
		case "BRF109A":
			repsummary = brf109ReportService.getArchieveBRF109View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF048A":
			repsummary = brf48ReportService.getArchieveBRF048View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF208A":
			repsummary = brf208AReportService.getArchieveBRF208View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF209A":
			repsummary = brf209AReportService.getArchieveBRF209View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF106A":
			repsummary = brf106AReportService.getArchieveBRF106View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF181A":
			repsummary = brf181AReportService.getArchieveBRF181View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF152":
			repsummary = brf152ReportService.getArchieveBRF0152View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF099A":

			repsummary = brf99AReportService.getArchieveBRF99View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF036A":
			repsummary = brf036ReportService.getArchieveBRF036View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF064":
			repsummary = brf64ReportService.getArchieveBRF064View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF094A":
			repsummary = brf094ReportService.getArchieveBRF094View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

			
	

		case "BRF210A":
			repsummary = brf210AReportService.getArchieveBRF210View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF087":
			repsummary = brf87ReportService.getArchieveBRF087View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

			/* Developed by sanjeevi */

		case "BRF200A":
			repsummary = brf200AReportService.getArchieveBRF200AView(reportId, fromdate, todate, currency, dtltype,
					pageable);

			break;

		case "BRF300A":
			repsummary = brf300AReportService.getArchieveBRF300View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF104A":
			repsummary = brf104AReportService.getArchieveBRF0104View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF070A":
			repsummary = brf070AReportService.getArchieveBRF070View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF201A":
			repsummary = brf201AReportService.getArchieveBRF201AView(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF156":
			repsummary = brf156ReportService.getArchieveBRF156AView(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF202A":
			repsummary = brf202ReportService.getArchieveBRF202View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF108A":
			repsummary = brf108ReportService.getArchieveBRF108View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF301A":
			repsummary = brf301ReportService.getArchieveBRF301View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		/* Developed by Gowtham */
		case "BRF105A":
			repsummary = brf105AReportService.getArchieveBRF0105View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF102A":
			repsummary = brf102AReportService.getArchieveBRF102AView(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF065":
			repsummary = brf65ReportService.getArchieveBRF065View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF100A":
			repsummary = brf100AReportService.getArchieveBRF100View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF085":
			repsummary = brf85ReportService.getArchieveBRF085View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF041A":
			repsummary = brf41ReportService.getArchieveBRF041View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF014":
			repsummary = brf14ReportService.getArchieveBRF014View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF042A":
			repsummary = brf42ReportService.getArchieveBRF042View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF052":
			repsummary = brf052ReportService.getArchieveBRF052View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF051":
			repsummary = brf051ReportService.getArchieveBRF051View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;
		case "BRF053":
			repsummary = brf53ReportService.getArchieveBRF053View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		case "BRF088":
			repsummary = brf88ReportService.getArchieveBRF088View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		/* Developed by KAMATCHI */

		case "BRF093A":
			repsummary = brf93ReportService.getArchieveBRF093View(reportId, fromdate, todate, currency, dtltype,
					pageable);
			break;

		/* Developed by KAMATCHI */

		/*
		 * case "BRF094A": repsummary =
		 * BRF094AReportService.getArchieveBRF094View(reportId, fromdate, todate,
		 * currency, dtltype, pageable); break;
		 */

		}
		return repsummary;
	}

	/*****
	 * Archeve details
	 * 
	 * @throws ParseException
	 *****/
	public ModelAndView ArchgetReportDetails(String reportId, String instanceCode, String asondate, String fromdate,
			String todate, String currency, String reportingTime, String dtltype, String subreportid, String secid,
			Pageable pageable, String Filter) throws ParseException {

		ModelAndView repdetail = new ModelAndView();
		logger.info("Getting Details for the Report :" + reportId);

		switch (reportId) {

		case "BRF001":
			repdetail = brf001ReportService.ARCHgetBRF001currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF002":
			repdetail = brf002ReportService.ARCHgetBRF002currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF045A":
			repdetail = brf45ReportService.ARCHgetBRF045currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF012":
			repdetail = brf012ReportService.ARCHgetBRF012currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF010":
			repdetail = brf010ReportService.ARCHgetBRF010currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF008":
			repdetail = brf008ReportService.ARCHgetBRF008currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF009":
			repdetail = brf009ReportService.ARCHgetBRF009currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF003":
			repdetail = brf003ReportService.ARCHgetBRF003currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF004":
			repdetail = brf004ReportService.ARCHgetBRF004currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF101A":
			repdetail = brf101ReportService.ARCHgetBRF101currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF007":
			repdetail = brf007ReportService.ARCHgetBRF007currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF005":
			repdetail = brf005ReportService.ARCHgetBRF005currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF011":
			repdetail = brf011ReportService.ARCHgetBRF011currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF060":
			repdetail = brf060ReportService.ARCHgetBRF060currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF013":
			repdetail = brf013ReportService.ARCHgetBRF013currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF031A":
			repdetail = brf031ReportService.ARCHgetBRF031currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF032A":
			repdetail = brf32ReportService.ARCHgetBRF032currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF033A":
			repdetail = brf033ReportService.ARCHgetBRF033currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF034A":
			repdetail = brf034ReportService.ARCHgetBRF034currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF035A":
			repdetail = BRF35ReportService.ARCHgetBRF035currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF037A":
			repdetail = brf37ReportService.ARCHgetBRF037currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF038A":
			repdetail = brf38ReportService1.ARCHgetBRF038currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF040A":
			repdetail = brf40ReportService.ARCHgetBRF40currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		/*
		 * case "BRF040A": repdetail =
		 * brf40ReportService.ARCHgetBRF40currentDtl(reportId, fromdate, todate,
		 * currency, dtltype, pageable, Filter); break;
		 */
		case "BRF077":
			repdetail = brf77ReportService.ARCHgetBRF077currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF050A":
			repdetail = brf50ReportService.ARCHgetBRF050currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF079":
			repdetail = brf79ReportService.ARCHgetBRF079currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF080":
			repdetail = brf80ReportService.ARCHgetBRF080currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF086":
			repdetail = brf86ReportService.ARCHgetBRF086currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF057":
			repdetail = brf057ReportService.ARCHgetBRF57currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF046A":
			repdetail = brf46ReportService.ARCHgetBRF046currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF151":
			repdetail = brf151ReportService.ARCHgetBRF151currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF048A":
			repdetail = brf48ReportService.ARCHgetBRF048currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		/*
		 * case "BRF060": repdetail =
		 * brf060ReportService.ARCHgetBRF060currentDtl(reportId, fromdate, todate,
		 * currency, dtltype, pageable,Filter); break;
		 */

		case "BRF062":
			repdetail = brf062ReportService.ARCHgetBRF062currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF047A":
			repdetail = brf47ReportService.ARCHgetBRF047currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF059":
			repdetail = brf59ReportService.ARCHgetBRF059currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		/*
		 * case "BRF060": repdetail =
		 * brf060ReportService.ARCHgetBRF060currentDtl(reportId, fromdate, todate,
		 * currency, dtltype, pageable,Filter); break;
		 */
		/*
		 * case "BRF047A": repdetail =
		 * brf47ReportService.ARCHgetBRF047currentDtl(reportId, fromdate, todate,
		 * currency, dtltype, pageable,Filter); break;
		 */
		case "BRF067":
			repdetail = brf67ReportService.ARCHgetBRF067currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF084":
			repdetail = brf84ReportService.ARCHgetBRF084currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		/*
		 * case "BRF060": repdetail =
		 * brf060ReportService.ARCHgetBRF060currentDtl(reportId, fromdate, todate,
		 * currency, dtltype, pageable,Filter); break;
		 */

		case "BRF066A":
			repdetail = brf066AReportService.ARCHgetBRF066currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF068":
			repdetail = brf068ReportService.ARCHgetBRF068currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF103A":
			repdetail = brf103AReportService.ARCHgetBRF103currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF039A":
			repdetail = brf039ReportService.ARCHgetBRF039currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF043A":
			repdetail = brf43ReportService.ARCHgetBRF043currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF154":
			repdetail = brf154ReportService.ARCHgetBRF154currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF155":
			repdetail = brf155ReportService.ARCHgetBRF155currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF073":
			repdetail = brf73ReportService.ARCHgetBRF073currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF083":
			repdetail = brf83ReportService.ARCHgetBRF083currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF044A":
			repdetail = brf044ReportService.ARCHgetBRF044currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF081":
			repdetail = brf81ReportService.ARCHgetBRF081currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF082":
			repdetail = brf82ReportService.ARCHgetBRF082currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF100A":
			repdetail = brf100AReportService.ARCHgetBRF100currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF205A":
			repdetail = brf205AReportService.ARCHgetBRF205currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF053":
			repdetail = brf53ReportService.ARCHgetBRF53currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF054":

			repdetail = brf054ReportService.ARCHgetBRF54currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF069":
			repdetail = brf069ReportService.ARCHgetBRF069currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF107A":
			repdetail = brf107AReportService.ARCHgetBRF0107currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF207A":
			repdetail = brf207AReportService.ARCHgetBRF0207currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF206A":
			repdetail = brf206AReportService.ARCHgetBRF206currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF106A":
			repdetail = brf106AReportService.ARCHgetBRF106currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF181A":
			repdetail = brf181AReportService.ARCHgetBRF181currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF056":
			repdetail = brf056ReportService.ARCHgetBRF056currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF152":
			repdetail = brf152ReportService.ARCHgetBRF0152currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF153":
			repdetail = brf153ReportService.ARCHgetBRF153currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF099A":
			repdetail = brf99AReportService.ARCHgetBRF99currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF064":
			repdetail = brf64ReportService.ARCHgetBRF064currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		
		 case "BRF094A": 
			 repdetail = brf094ReportService.ARCHgetBRF094currentDtl(reportId, fromdate, todate,currency, dtltype,
					 pageable, Filter);
			 break;
		
		case "BRF204A":
			repdetail = brf204AReportService.ARCHgetBRF204currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF071":
			repdetail = brf71ReportService.ARCHgetBRF071currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF078":
			repdetail = brf78ReportService.ARCHgetBRF078currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF092":
			repdetail = brf92ReportService.ARCHgetBRF092currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF095A":
			repdetail = brf095AReportService.ARCHgetBRF095currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF096A":
			repdetail = brf96AReportService.ARCHgetBRF096currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF036A":
			repdetail = brf036ReportService.ARCHgetBRF036currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF210A":
			repdetail = brf210AReportService.ARCHgetBRF210currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF087":
			repdetail = brf87ReportService.ARCHgetBRF087currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF300A":
			repdetail = brf300AReportService.ARCHgetBRF300currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF156":
			repdetail = brf156ReportService.ARCHgetBRF156currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF104A":
			repdetail = brf104AReportService.ARCHgetBRF0104currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF051":
			repdetail = brf051ReportService.ARCHgetBRF051currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF052":
			repdetail = brf052ReportService.ARCHgetBRF052currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF070A":
			repdetail = brf070AReportService.ARCHgetBRF070currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF201A":
			repdetail = brf201AReportService.ARCHgetBRF201AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		/* Developed by sanjeevi */

		case "BRF200A":
			repdetail = brf200AReportService.ARCHgetBRF200AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		/* Developed by GOWTHAM */
		case "BRF105A":
			repdetail = brf105AReportService.ARCHgetBRF0105currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF209A":
			repdetail = brf209AReportService.ARCHgetBRF0209currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF208A":
			repdetail = brf208AReportService.ARCHgetBRF0208currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF202A":
			repdetail = brf202ReportService.ARCHgetBRF202currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF049A":
			repdetail = brf49ReportService.ARCHgetBRF049currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
			
		case "BRF109A":
			repdetail = brf109ReportService.ARCHgetBRF109currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF102A":
			repdetail = brf102AReportService.ARCHgetBRF102AcurrentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF065":
			repdetail = brf65ReportService.ARCHgetBRF065currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF0001":
			repdetail = brf001_FORT_SERVIVE.ARCHgetBRF0001currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF074":
			repdetail = brf74ReportService.ARCHgetBRF074currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF085":
			repdetail = brf85ReportService.ARCHgetBRF085currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF041A":
			repdetail = brf41ReportService.ARCHgetBRF041currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		case "BRF076":
			repdetail = brf76ReportService.ARCHgetBRF076currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;
		case "BRF088":
			repdetail = brf88ReportService.ARCHgetBRF088currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		/* Developed by KAMATCHI */

		case "BRF093A":
			repdetail = brf93ReportService.ARCHgetBRF093currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		/* Developed by KAMATCHI */

		/*
		 * case "BRF094A": repdetail =
		 * BRF094AReportService.ARCHgetBRF094currentDtl(reportId, fromdate, todate,
		 * currency, dtltype, pageable, Filter); break;
		 */

		case "BRF014":
			repdetail = brf14ReportService.ARCHgetBRF014currentDtl(reportId, fromdate, todate, currency, dtltype,
					pageable, Filter);
			break;

		}

		return repdetail;
	}

	public String modifyRecord(Date asondate, BRF300_ENTITY brf300_ENTITY, String userId, HttpServletRequest req) {
		return brf300AReportService.modifyBRF300Report(asondate, brf300_ENTITY, userId,req);
	}

	public String modifyBrf202A(ReportBRF202AData formData, Date asondate, String userId,HttpServletRequest req) {
		return brf202ReportService.modifyBrf202A(formData, asondate, userId, req);
	}

	public String modifyBrf301(ReportBRF301Data formData, Date asondate, String userId,HttpServletRequest req) {
		return brf301ReportService.modifyBrf301(formData, asondate, userId, req);
	}

	
	
	public String modifyBrf108(ReportBRF108Data formData, Date asondate, String userId, HttpServletRequest req) {
		return brf108ReportService.modifyBrf108(formData, asondate, userId,req);
	}


	public String modifyRecord201A(YourFormDatas formData, Date asondate, String userId, HttpServletRequest req) {
		return brf201AReportService.modifyBRF201Report(formData, asondate, userId, req);
	}

	public String Modify069(YourFormData formData, String userId,HttpServletRequest req) {
		return brf069ReportService.ModifyReport(formData, userId,req);
	}

	public String verify69Report(Date asondate, String userId,HttpServletRequest req) {
		return brf069ReportService.verify69Report(asondate, userId,req);
	}

	public String verifyBRF300Report(Date asondate, String userId, HttpServletRequest req) {
		return brf300AReportService.verifyBRF300Report(asondate, userId,req);
	}

	public String verifyBRF70Report(Date asondate, String userId, HttpServletRequest req) {
		return BRF70AReportService.verifyBRF70Report(asondate, userId,req);
	}

	public String verifyBRF71Report(Date asondate, String userId, HttpServletRequest req) {
		return BRF71ReportService.verifyBRF71Report(asondate, userId,req);
	}

	public String verifyBRF201Report(Date asondate, String userId, HttpServletRequest req) {
		return brf201AReportService.verifyBRF201Report(asondate, userId, req);
	}

	public String verifyBRF202Report(Date asondate, String userId, HttpServletRequest req) {
		return brf202ReportService.verifyBRF202Report(asondate, userId,req);
	}

	public String verifyBRF301Report(Date asondate, String userId, HttpServletRequest req) {
		return brf301ReportService.verifyBRF301Report(asondate, userId, req);
	}

	public String verifyBRF108Report(Date asondate, String userId, HttpServletRequest req) {
		return brf108ReportService.verifyBRF108Report(asondate, userId,req);
	}

	public String modifyRecord181(Date asondate, BRF_181_A2_REPORT_ENTITY BRF_181_A2_REPORT_ENTITY, String userId) {
		return BRF181AReportService.modifyRecord181(asondate, BRF_181_A2_REPORT_ENTITY, userId);
	}

	public String verifyBRF181Report(Date asondate, String userId) {
		return BRF181AReportService.verifyBRF181Report(asondate, userId);
	}

	public String modifyRecord32(Date asondate, BRF32_ENTITY BRF32_ENTITY, String userId)  {
		return brf32ReportService.modifyRecord32(asondate, BRF32_ENTITY, userId);
	}
	public String modifyRecord66(Date asondate, BRF66_Entity BRF66_Entity, String userId)  {
		return brf066AReportService.modifyRecord66(asondate, BRF66_Entity, userId);
	}
	public String verifyBRF32Report(Date asondate, String userId) {
		return brf32ReportService.verifyBRF32Report(asondate, userId);
	}
	public String verifyBRF66Report(Date asondate, String userId) {
		return brf066AReportService.verifyBRF66Report(asondate, userId);
	}
	public String verifyBRF102Report(Date asondate, String userId) {
		return brf102AReportService.verifyBRF102Report(asondate, userId);
	}
	public String verifyBRF05Report(Date asondate, String userId) {
		return brf005ReportService.verifyBRF05Report(asondate, userId);
	}
	
	public String verifyBRF103Report(Date asondate, String userId) {
		return brf103AReportService.verifybrf103Report(asondate, userId);
		
	}
	
	public String modifyRecord103(Date asondate, BRF103_ENTITY BRF103_ENTITY, String userId) {
		return brf103AReportService.modifyRecord103(asondate, BRF103_ENTITY, userId);
	}

	/*-----consolidatereportdownload---- created by sanjeev*/

	public File getconsolidateDownloadFile(String reportId, String asondate, String fromdate, String todate,
			String currency, String subreportid, String secid, String dtltype, String reportingTime, String filetype,
			String instancecode, String filter) throws JRException, SQLException, IOException {

		File repfile = null;

		logger.info("Getting Report File for : " + reportId + " in " + filetype + " format");

		switch (reportId) {
		case "BRF95-CAR":
			repfile = BASEL002AReportService.getconsolidateFile(reportId, fromdate, todate, currency, dtltype,
					filetype);
			break;
		case "BRFBankingbook":
			repfile = banking_bookReportService.getconsolidateFile(reportId, fromdate, todate, currency, dtltype,
					filetype);
			break;
		case "BRF95FB":
			repfile = brf95_fund_ReportService.getconsolidateFile95(reportId, fromdate, todate, currency, dtltype,
					filetype);
			break;
		}
		return repfile;
	}

}