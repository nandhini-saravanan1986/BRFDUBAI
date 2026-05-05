package com.bornfire.xbrl.controllers;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.xbrl.config.PasswordEncryption;
import com.bornfire.xbrl.config.SequenceGenerator;
import com.bornfire.xbrl.entities.AccessAndRoles;
import com.bornfire.xbrl.entities.AccessandRolesRepository;
import com.bornfire.xbrl.entities.AlertEntity;
import com.bornfire.xbrl.entities.AlertManagementEntity;
import com.bornfire.xbrl.entities.AlertManagementRepository;
import com.bornfire.xbrl.entities.AlertRep;
import com.bornfire.xbrl.entities.BOB_RAM_REPO;
import com.bornfire.xbrl.entities.BRECON_TTUM_TRANSACTION_ENTITY;
import com.bornfire.xbrl.entities.BRECON_TTUM_TRANSACTION_REP;
import com.bornfire.xbrl.entities.BRF_REF_CODE_ENTITY;
import com.bornfire.xbrl.entities.BRF_REF_CODE_REP;
import com.bornfire.xbrl.entities.BRFmappingRepo;
import com.bornfire.xbrl.entities.BankMaster;
import com.bornfire.xbrl.entities.Brecon_Aani_payment_dup_rep;
import com.bornfire.xbrl.entities.CustomRepDownloadRep;
import com.bornfire.xbrl.entities.CustomReportParms;
import com.bornfire.xbrl.entities.CustomReportsParmsRepo;
import com.bornfire.xbrl.entities.ECL_ACC_MASTER_REP;
import com.bornfire.xbrl.entities.ECL_ACC_MASTER_WORKING_REP;
import com.bornfire.xbrl.entities.ECL_COLLATERAL_REP;
import com.bornfire.xbrl.entities.ECL_CUSTMASTER_REP;
import com.bornfire.xbrl.entities.ECL_FUNDED_REP;
import com.bornfire.xbrl.entities.ECL_MAPPING_CONST_DEC_REP;
import com.bornfire.xbrl.entities.ECL_MAPPING_SUBPORT_REP;
import com.bornfire.xbrl.entities.ECL_MDT_AED_Entity;
import com.bornfire.xbrl.entities.ECL_MDT_AED_Rep;
import com.bornfire.xbrl.entities.ECL_MDT_DCR_Entity;
import com.bornfire.xbrl.entities.ECL_MDT_DCR_Rep;
import com.bornfire.xbrl.entities.ECL_MDT_DIS_REC;
import com.bornfire.xbrl.entities.ECL_MDT_DIS_REC_Rep;
import com.bornfire.xbrl.entities.ECL_MDT_HIS_RATE_Rep;
import com.bornfire.xbrl.entities.ECL_MDT_LGD_Entity;
import com.bornfire.xbrl.entities.ECL_MDT_LGD_Rep;
import com.bornfire.xbrl.entities.ECL_MDT_LRW_Entity;
import com.bornfire.xbrl.entities.ECL_MDT_LRW_Rep;
import com.bornfire.xbrl.entities.ECL_MDT_RECOVERY_Entity;
import com.bornfire.xbrl.entities.ECL_MDT_RECOVERY_Rep;
import com.bornfire.xbrl.entities.ECL_MDT_SMOOTH_Entity;
import com.bornfire.xbrl.entities.ECL_MDT_SMOOTH_Rep;
import com.bornfire.xbrl.entities.ECL_MDT_WO_ADJ_Entity;
import com.bornfire.xbrl.entities.ECL_MDT_WO_ADJ_Rep;
import com.bornfire.xbrl.entities.ECL_MasterData_INR_Entity;
import com.bornfire.xbrl.entities.ECL_MasterData_INR_Rep;
import com.bornfire.xbrl.entities.ECL_RATING_REP;
import com.bornfire.xbrl.entities.ECL_SMA_DATA_REP;
import com.bornfire.xbrl.entities.ECL_STATUS_ENTITY;
import com.bornfire.xbrl.entities.ECL_TREASURY_REP;
import com.bornfire.xbrl.entities.ECL_WATCHLIST_REP;
import com.bornfire.xbrl.entities.EcddCorporateEntity;
import com.bornfire.xbrl.entities.EcddCustomerDocumentsEntity;
import com.bornfire.xbrl.entities.Ecdd_profile_report_entity;
import com.bornfire.xbrl.entities.Ecdd_profile_report_repo;
import com.bornfire.xbrl.entities.Ecl_status_repo;
import com.bornfire.xbrl.entities.Facility_Repo;
import com.bornfire.xbrl.entities.Facitlity_Entity;
import com.bornfire.xbrl.entities.GenRefCodeMast;
import com.bornfire.xbrl.entities.GeneralMasterTbRep;
import com.bornfire.xbrl.entities.Gl_balance_recon_rep;
import com.bornfire.xbrl.entities.KYC_Audit_Rep;
import com.bornfire.xbrl.entities.Kyc_Corprate_Repo;
import com.bornfire.xbrl.entities.Kyc_Repo;
import com.bornfire.xbrl.entities.Personal_DBR_Entity;
import com.bornfire.xbrl.entities.Personal_DBR_Repo;
import com.bornfire.xbrl.entities.Personal_INCOME_Entity;
import com.bornfire.xbrl.entities.Personal_INCOME_Repo;
import com.bornfire.xbrl.entities.RBRShareHolder_Entity;
import com.bornfire.xbrl.entities.RBRShareHolder_Repo;
import com.bornfire.xbrl.entities.RBR_CUSTOMER_DATA_V1_REP;
import com.bornfire.xbrl.entities.RBR_Inverstments_Entity;
import com.bornfire.xbrl.entities.RBR_Inverstments_Repo;
import com.bornfire.xbrl.entities.RBR_Legal_Cases_Entity;
import com.bornfire.xbrl.entities.RBR_Legal_Cases_Repo;
import com.bornfire.xbrl.entities.RBRcustomerRepo;
import com.bornfire.xbrl.entities.RBRcustomer_entity;
import com.bornfire.xbrl.entities.Rampop_Entity;
import com.bornfire.xbrl.entities.SCORE_CALCULATION_REPO;
import com.bornfire.xbrl.entities.SCORE_CARD_PERSONAL_LOAN_REPO;
import com.bornfire.xbrl.entities.Security_Entity;
import com.bornfire.xbrl.entities.Security_Repo;
import com.bornfire.xbrl.entities.TransactionInquiryRep;
import com.bornfire.xbrl.entities.UserProfile;
import com.bornfire.xbrl.entities.UserProfileRep;
import com.bornfire.xbrl.entities.Vat_Ledger_Entity;
import com.bornfire.xbrl.entities.Vat_Ledger_Repo;
import com.bornfire.xbrl.entities.XBRLReportMap;
import com.bornfire.xbrl.entities.XBRLReportsMasterRep;
import com.bornfire.xbrl.entities.scorecard_DBR_repo;
/*import com.bornfire.xbrl.entities.BRBS.BRF071ServiceRepo;
import com.bornfire.xbrl.entities.BRBS.BRF074ServiceRepo;*/
//import com.bornfire.xbrl.entities.BRBS.Ecl_upload_Entity;
import com.bornfire.xbrl.entities.BRBS.*;
import com.bornfire.xbrl.entities.services.AccessAndRolesServices;
import com.bornfire.xbrl.services.AlertManagementServices;
import com.bornfire.xbrl.services.BRF001ReportService;
import com.bornfire.xbrl.services.BRF002ReportService;
import com.bornfire.xbrl.services.BRF004ReportService;
import com.bornfire.xbrl.services.BRF007ReportService;
import com.bornfire.xbrl.services.BRF102AReportService;
import com.bornfire.xbrl.services.BRF73ReportService;
import com.bornfire.xbrl.services.BRF79ReportService;
import com.bornfire.xbrl.services.BankServices;
import com.bornfire.xbrl.services.CustomRepGeneratorServices;
import com.bornfire.xbrl.services.CustomRepParamServices;
import com.bornfire.xbrl.services.EcddUploadDocumentService;
import com.bornfire.xbrl.services.EclmasterService;
import com.bornfire.xbrl.services.EtlServices;
import com.bornfire.xbrl.services.GlSubHeadConfigService;
import com.bornfire.xbrl.services.IndividualPdfService;
import com.bornfire.xbrl.services.Kyc_individual_service;
import com.bornfire.xbrl.services.LoginServices;
import com.bornfire.xbrl.services.RBRReportservice;
import com.bornfire.xbrl.services.RBSValidationservices;
import com.bornfire.xbrl.services.RatingService;
import com.bornfire.xbrl.services.ReferenceCodeConfigure;
import com.bornfire.xbrl.services.ReportCodeMappingService;
import com.bornfire.xbrl.services.ReportServices;
import com.bornfire.xbrl.services.ReportServices.ReportTitle;
import com.bornfire.xbrl.services.ScorecardService;
import com.bornfire.xbrl.services.VAT_Ledger_Service;

import net.sf.jasperreports.engine.JRException;

@Controller
@ConfigurationProperties("default")
public class XBRLNavigationController {

	private static final Logger logger = LoggerFactory.getLogger(XBRLNavigationController.class);
	
	@Autowired
	GeneralMasterTbRep generalMasterTbRep;
	
	@Autowired
	TransactionInquiryRep transactionInquiryRep;
	
	@Autowired
	SessionFactory sessionFactory;
	@Autowired
	LoginServices loginServices;
	
	@Autowired
	Kyc_individual_service kyc_individual_service;

	@Autowired
	EcddIndividualProfileRepository ecddIndividualProfileRepository;

	@Autowired
	BRF004ReportService BRF004ReportServices;

	@Autowired
	XBRLReportsMasterRep XBRLReportsMasterReps;
	@Autowired
	BRF095AServiceRepo BRF095AServiceRepos;

	@Autowired
	Brecon_core_rep coresystemlistrep;

	@Autowired
	BRECON_Common_Table_Rep bRECON_Common_Table_Rep;

	/*
	 * @Autowired BRF2_MAPPING_REPO brf2_MAPPING_REPO;
	 */

	@Autowired
	AlertRep alertRep;
	@Autowired
	BRF11_MAPPING_REPO brf11_mapping_repo;
	@Autowired
	BRF65ServiceRepo BRF65ServiceRepo;

	@Autowired
	BRF67ServiceRepo BRF67ServiceRepo;

	@Autowired
	BRF062ServiceRepo BRF062ServiceRepo;

	@Autowired
	BRF064ServiceRepo BRF064ServiceRepo;
	@Autowired
	BRF070AServiceRepo BRF070AServiceRepo;
	@Autowired
	BRF066AServiceRepo BRF066AServiceRepo;
	@Autowired
	BRF099ServiceRepo BRF099ServiceRepo;

	@Autowired
	BRF100AServiceRepo BRF100AServiceRepo;

	@Autowired
	BRF102ServiceRepo BRF102ServiceRepo;

	@Autowired
	BRF103AServiceRepo BRF103ServiceRepo;

	@Autowired
	RRReportRepo RRReportRepo;

	@Autowired
	BRF104AServiceRepo BRF104ServiceRepo;

	@Autowired
	BRF105AServiceRepo BRF105ServiceRepo;

	@Autowired
	BRF106AServiceRepo BRF106ServiceRepo;

	@Autowired
	BRF107AServiceRepo BRF107ServiceRepo;

	@Autowired
	BRF108ServiceRepo BRF108Servicerepo;

	@Autowired
	BRF109AServiceRepo BRF109ServiceRepo;

	@Autowired
	BRF96AServiceRepo BRF96AServiceRepo;
	@Autowired
	BRF095AServiceRepo BRF095AServiceRepo;
	@Autowired
	BRF101ServiceRepo BRF101ServiceRepo;
	@Autowired
	ReportServices reportServices;

	@Autowired
	XBRLReportMap xbrlreportmap;

	@Autowired
	ReferenceCodeConfigure referenceCodeConfigure;

	@Autowired
	BankServices bankServices;

	@Autowired
	ReportCodeMappingService reportCodeMappingService;

	@Autowired
	EtlServices etlServices;

	@Autowired
	ECL_MasterData_INR_Rep eCL_MasterData_INR_Rep;

	@Autowired
	ECL_MDT_AED_Rep eCL_MDT_AED_Rep;

	@Autowired
	ECL_MDT_RECOVERY_Rep eCL_MDT_RECOVERY_Rep;

	@Autowired
	ECL_MDT_SMOOTH_Rep eCL_MDT_SMOOTH_Rep;

	@Autowired
	ECL_MDT_DIS_REC_Rep eCL_MDT_DIS_REC_Rep;

	@Autowired
	ECL_MDT_LGD_Rep eCL_MDT_LGD_Rep;

	@Autowired
	ECL_MDT_LRW_Rep eCL_MDT_LRW_Rep;

	@Autowired
	ECL_MDT_HIS_RATE_Rep eCL_MDT_HIS_RATE_Rep;

	@Autowired
	ECL_MDT_DCR_Rep eCL_MDT_DCR_Rep;

	@Autowired
	ECL_MDT_WO_ADJ_Rep eCL_MDT_WO_ADJ_Rep;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	GlSubHeadConfigService glSubHeadConfigService;

	@Autowired
	CustomRepGeneratorServices customerRepGenServices;

	@Autowired
	CustomReportsParmsRepo custReportsParmsRepo;

	@Autowired
	CustomReportsParmsRepo customReportsParmsRepo;

	@Autowired
	CustomRepDownloadRep customRepDownloadRep;

	@Autowired
	CustomRepParamServices customRepParamServices;

	@Autowired
	RBSReportRepo rbsReportlist;

	@Autowired
	RRReportRepo rrReportlist;

//	@Autowired
//	HostDataRepo hostDataRepo;

	@Autowired
	BRF0001ServiceRepo BRF0001ServiceRepo;

	@Autowired
	BRF001ServiceRepo BRF001ServiceRepo;

	@Autowired
	BRF002ServiceRepo BRF002Servicerepo;

	@Autowired
	BRF003ServiceRepo BRF003Servicerepo;

	@Autowired
	BRF004ServiceRepo BRF004Servicerepo;

	@Autowired
	BRF005ServiceRepo BRF005Servicerepo;

	@Autowired
	BRF007ServiceRepo BRF007Servicerepo;

	@Autowired
	BRF008ServiceRepo BRF008Servicerepo;

	@Autowired
	BRF009ServiceRepo BRF009Servicerepo;

	@Autowired
	BRF010ServiceRepo BRF010Servicerepo;

	@Autowired
	BRF011ServiceRepo BRF011Servicerepo;

	@Autowired
	BRF012ServiceRepo BRF012Servicerepo;

	@Autowired
	BRF013ServiceRepo BRF013Servicerepo;

	@Autowired
	BRF014ServiceRepo BRF014Servicerepo;

	@Autowired
	BRF031ServiceRepo BRF031Servicerepo;

	@Autowired
	BRF32ServiceRepo BRF32Servicerepo;

	@Autowired
	BRF033ServiceRepo BRF033Servicerepo;

	@Autowired
	BRF034ServiceRepo BRF034Servicerepo;

	@Autowired
	BRF035ServiceRepo BRF035Servicerepo;

	@Autowired
	BRF036ServiceRepo BRF036Servicerepo;

	@Autowired
	BRF037ServiceRepo BRF037Servicerepo;

	@Autowired
	BRF038ServiceRepo BRF038Servicerepo;

	@Autowired
	BRF039ServiceRepo BRF039Servicerepo;

	@Autowired
	BRF040ServiceRepo BRF040Servicerepo;

	@Autowired
	BRF041ServiceRepo BRF041Servicerepo;

	@Autowired
	BRF042ServiceRepo BRF042Servicerepo;

	@Autowired
	BRF043ServiceRepo BRF043Servicerepo;

	@Autowired
	BRF044ServiceRepo BRF044Servicerepo;

	@Autowired
	BRF045ServiceRepo BRF045Servicerepo;

	@Autowired
	BRF046ServiceRepo BRF046Servicerepo;

	@Autowired
	BRF047ServiceRepo BRF047Servicerepo;

	@Autowired
	BRF048ServiceRepo BRF048Servicerepo;

	@Autowired
	BRF049ServiceRepo BRF049Servicerepo;

	@Autowired
	BRF050ServiceRepo BRF050Servicerepo;

	@Autowired
	BRF051ServiceRepo BRF051Servicerepo;

	@Autowired
	BRF052ServiceRepo BRF052Servicerepo;

	@Autowired
	BRF053ServiceRepo BRF053Servicerepo;

	@Autowired
	BRF054ServiceRepo BRF054Servicerepo;

	@Autowired
	BRF059ServiceRepo BRF059Servicerepo;

	@Autowired
	BRF060ServiceRepo BRF060Servicerepo;

	@Autowired
	BRF068ServiceRepo BRF068Servicerepo;

	@Autowired
	BRF069Servicerepo BRF069Servicerepo;

	@Autowired
	BRF71_ServiceRepo BRF71Servicerepo;

	@Autowired
	BRF73ServiceRepo BRF73Servicerepo;

	@Autowired
	BRF74ServiceRepo BRF74Servicerepo;

	@Autowired
	BRF077ServiceRepo BRF077Servicerepo;

	@Autowired
	BRF078ServiceRepo BRF078Servicerepo;

	@Autowired
	BRF079ServiceRepo BRF079Servicerepo;

	@Autowired
	BRF080ServiceRepo BRF080Servicerepo;

	@Autowired
	BRF081ServiceRepo BRF081Servicerepo;

	@Autowired
	BRF084ServiceRepo BRF084Servicerepo;

	@Autowired
	BRF085Servicerepo BRF085Servicerepo;

	@Autowired
	BRF086Servicerepo BRF086Servicerepo;

	@Autowired
	BRF082ServiceRepo BRF082Servicerepo;

	@Autowired
	BRF083ServiceRepo BRF083Servicerepo;

	@Autowired
	BRF151ServiceRepo BRF151Servicerepo;

	@Autowired
	BRF152ServiceRepo BRF152Servicerepo;

	@Autowired
	BRF153ServiceRepo BRF153Servicerepo;

	@Autowired
	BRF154ServiceRepo BRF154Servicerepo;

	@Autowired
	BRF155ServiceRepo BRF155Servicerepo;

	@Autowired
	BRF156ServiceRepo BRF156Servicerepo;

	@Autowired
	BRF057ServiceRepo BRF057Servicerepo;

	@Autowired
	BRF204ServiceRepo BRF204Servicerepo;

	@Autowired
	BRF205ServiceRepo BRF205Servicerepo;

	@Autowired
	BRF206ServiceRepo BRF206Servicerepo;

	@Autowired
	BRF207ServiceRepo BRF207Servicerepo;

	@Autowired
	BRF208ServiceRepo BRF208Servicerepo;

	@Autowired
	BRF209ServiceRepo BRF209Servicerepo;

	@Autowired
	BRF210ServiceRepo BRF210Servicerepo;

	@Autowired
	BRF300ServiceRepo BRF300Servicerepo;

	@Autowired
	BRF301ServiceRepo BRF301Servicerepo;

	@Autowired
	BRF087ServiceRepo BRF087Servicerepo;

	@Autowired
	BRF092ServiceRepo BRF092Servicerepo;

	@Autowired
	BRF093Servicerepo BRF093Servicerepo;

	@Autowired
	BRF094Servicerepo BRF094Servicerepo;

	@Autowired
	BRF56ServiceRepo BRF56Servicerepo;

	@Autowired
	BRF181AServiceRepo BRF181Servicerepo;

	@Autowired
	BRF200ServiceRepo BRF200Servicerepo;

	@Autowired
	BRF001ReportService brf001ReportService;

	@Autowired
	ReportValidationsRepo reportValidationsRepo;

	@Autowired
	BRFValidationsRepo brfValidationsRepo;

	@Autowired
	RBSValidationservices rbsValidationservices;

	@Autowired
	T1CurProdServicesRepo t1CurProdServicesRepo;

	@Autowired
	REPORTLIST_REPO reportlist_repo;

	@Autowired
	RBRcustomerRepo rBRcustomerRepo;

	@Autowired
	RBRShareHolder_Repo rbrShareHolder_Repo;

	@Autowired
	Facility_Repo facility_Repo;

	@Autowired
	Security_Repo security_Repo;

	@Autowired
	Provision_Repo Provision_Repo;

	@Autowired
	RBR_Inverstments_Repo RBR_Inverstments_Repo;

	@Autowired
	RBR_Legal_Cases_Repo RBR_Legal_Cases_Repo;

	@Autowired
	RBRoverall_Data_Repo RBRoverall_Data_Repo;

	@Autowired
	RBRReportservice RBRReportservice;

	@Autowired
	private AlertManagementRepository alertmanagementrepository;

	@Autowired
	AlertManagementServices alertservices;

	@Autowired
	BRF002ReportService brf002ReportService;

	@Autowired
	BRF007ReportService brf007ReportService;

	@Autowired
	com.bornfire.xbrl.entities.BRBS.AUD_SERVICE_REPO AUD_SERVICE_REPO;

	@Autowired
	BRF_REF_CODE_REP brf_REP;

	@Autowired
	BRFmappingRepo bRFmappingRepo;

	@Autowired
	Ecl_status_repo ecl_status_repo;

	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	BOB_RAM_REPO bobRAMRep;

	@Autowired
	RatingService ratingService;

	@Autowired
	ECL_MasterData_INR_Rep eclmasterrep;

	@Autowired
	EclmasterService eclmasterService;

	@Autowired
	BRF73ReportService brf73ReportService;

	@Autowired
	BRF102AReportService brf102AReportService;

	@Autowired
	BRF79ReportService bRF79ReportService;

	@Autowired
	Reference_code_Repo reference_code_Repo;

	@Autowired
	BRF202A_entity_repo brf202A_entity_repo;

	@Autowired
	ECL_MAPPING_CONST_DEC_REP constdescrep;
	@Autowired
	ECL_MAPPING_SUBPORT_REP subportrep;

	@Autowired
	SCORE_CALCULATION_REPO scoreCalculationRepo;

	@Autowired
	Personal_DBR_Repo personalDBRRepo;

	@Autowired
	Personal_INCOME_Repo personalIncomeRepo;

	@Autowired
	ScorecardService scoreService;

	@Autowired
	VAT_Ledger_Service vat_ledger_services;

	@Autowired
	Vat_Ledger_Repo vat_ledger_repo;

	@Autowired
	RBR_CUSTOMER_DATA_V1_REP RBR_CUSTOMER_DATA_V1_REP;

	@Autowired
	BRECON_DESTINATION_REPO bRECON_DESTINATION_REPO;

	@Autowired
	BRECON_Audit_Rep bRECON_Audit_Rep;

	@Autowired
	MANUAL_Audit_Rep mANUAL_Audit_Rep;

	@Autowired
	MANUAL_Service_Rep mANUAL_Service_Rep;

	@Autowired
	Charge_Back_Rep charge_Back_Rep;

	@Autowired
	Ecdd_customer_transaction_repo Ecdd_customer_transaction_repo;

	@Autowired
	BRECON_TTUM_TRANSACTION_REP BRECON_TTUM_TRANSACTION_REP;

	@Autowired
	RRReportRepo rrReportRepo;

	@Autowired
	AccessAndRolesServices AccessRoleService;

	@Autowired
	AccessandRolesRepository accessandrolesrepository;

	@Autowired
	Brecon_Aani_payment_dup_rep Brecon_Aani_payment_dup_rep;

	@Autowired
	Ecdd_profile_report_repo Ecdd_profile_report_repo;

	private String auditRefNo;

	private String pagesize;

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	@RequestMapping("/custom-error")
	public String handleError(HttpServletRequest request, Model model) {
		Object statusCode = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		Object errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
		Exception exception = (Exception) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);

		// Ignore Thymeleaf exceptions by returning a simple message or redirecting
		// elsewhere
		if (exception != null) {
			if (exception instanceof org.thymeleaf.exceptions.TemplateInputException
					|| exception instanceof org.thymeleaf.exceptions.TemplateProcessingException) {
				// For example: return a simple page or ignore it silently
				model.addAttribute("status", statusCode);
				model.addAttribute("message", "A template processing error occurred.");
				return "simple-error"; // Or any other simple error page without details
			}
		}

		model.addAttribute("status", statusCode);
		model.addAttribute("message", errorMessage);

		return "error"; // Your normal error.html template
	}

	@GetMapping("/systemotp")
	public String showOtpForm(Model model, HttpSession session) {
		String otp = (String) session.getAttribute("otp");
		model.addAttribute("otp", otp);
		return "XBRLOtpvalidation.html"; // Thymeleaf or HTML page
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("otp") String userOtp, HttpSession session) {
		String actualOtp = (String) session.getAttribute("otp");
		if (actualOtp != null && actualOtp.equals(userOtp)) {
			session.removeAttribute("otp"); // Clear OTP after success
			return "redirect:/Dashboard";
		}
		return "redirect:login?invalidotp";
	}

	@RequestMapping(value = "/", method = { RequestMethod.GET, RequestMethod.POST })
	public String getdashboard(Model md, HttpServletRequest req) {

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		String userid = (String) req.getSession().getAttribute("USERID");
		String Dashboardpage = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");

		md.addAttribute("menu", "Dashboard");
		md.addAttribute("checkpassExpiry", loginServices.checkpassexpirty(userid));
		md.addAttribute("checkAcctExpiry", loginServices.checkAcctexpirty(userid));
		md.addAttribute("changepassword", loginServices.checkPasswordChangeReq(userid));

		if (Dashboardpage.equals("DCD_ADMIN") || Dashboardpage.equals("DCD_BRANCH")) {
			int Completed = 0;
			int Pending = 0;
			int Under_review = 0;

			int CorpCompleted = 0;
			int CorpPending = 0;
			int CorpUnder_review = 0;

			int corpbarcompleted = 0;
			int corpbarPending = 0;
			int corpbarincomplete = 0;

			/// Counts fetched for Dashborad page Pending kyc INDIVIDUAL details branch wise
			BigDecimal DubaiPendIndividuals = new BigDecimal("0");
			BigDecimal AbudhabiPendIndividuals = new BigDecimal("0");
			BigDecimal DeiraPendIndividuals = new BigDecimal("0");
			BigDecimal SharjhaPendIndividuals = new BigDecimal("0");
			BigDecimal RasalkhaimaPendIndividuals = new BigDecimal("0");
			BigDecimal SyndPendIndividuals = new BigDecimal("0");

			BigDecimal DubaiPendCorporate = new BigDecimal("0");
			BigDecimal AbudhabiPendCorporate = new BigDecimal("0");
			BigDecimal DeiraPendCorporate = new BigDecimal("0");
			BigDecimal SharjhaPendCorporate = new BigDecimal("0");
			BigDecimal RasalkhaimaPendCorporate = new BigDecimal("0");
			BigDecimal SyndPendCorporate = new BigDecimal("0");
			if (Dashboardpage.equals("DCD_ADMIN")) {
				Completed = ecddIndividualProfileRepository.Getcompletedcount();
				Under_review = ecddIndividualProfileRepository.GetIncompletedcount();
				Pending = ecddIndividualProfileRepository.GetPendingcount();

				CorpCompleted = kyc_corporate_repo.Getcompletedcount();
				CorpUnder_review = kyc_corporate_repo.GetIncompletedcount();
				CorpPending = kyc_corporate_repo.GetPendingcount();
			} else {
				Completed = ecddIndividualProfileRepository.Getbranchwisecompletedcount(BRANCHCODE);
				Under_review = ecddIndividualProfileRepository.GetbranchwiseIncompletedcount(BRANCHCODE);
				Pending = ecddIndividualProfileRepository.GetbranchwisePendingcount(BRANCHCODE);

				CorpCompleted = kyc_corporate_repo.Getbranchwisecompletedcount(BRANCHCODE);
				CorpUnder_review = kyc_corporate_repo.GetbranchwiseIncompletedcount(BRANCHCODE);
				CorpPending = kyc_corporate_repo.GetbranchwisePendingcount(BRANCHCODE);
			}

			corpbarcompleted = CorpCompleted;
			corpbarPending = CorpPending;
			corpbarincomplete = CorpUnder_review;

			List<Object[]> branchwiseIndividual = ecddIndividualProfileRepository.GetbranchPendingcount();

			for (int i = 0; i < branchwiseIndividual.size(); i++) {

				if (branchwiseIndividual.get(i)[0].toString() != null) {
					if (branchwiseIndividual.get(i)[0].toString().equals("9001")) {
						DubaiPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}

					if (branchwiseIndividual.get(i)[0].toString().equals("9002")) {
						AbudhabiPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
					if (branchwiseIndividual.get(i)[0].toString().equals("9003")) {
						DeiraPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
					if (branchwiseIndividual.get(i)[0].toString().equals("9004")) {
						SharjhaPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}

					if (branchwiseIndividual.get(i)[0].toString().equals("9008")) {
						SyndPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
				}

			}

			List<Object[]> branchwiseCorporate = kyc_corporate_repo.GetbranchPendingcount();
			for (int i = 0; i < branchwiseCorporate.size(); i++) {

				if (branchwiseCorporate.get(i)[0].toString() != null) {
					if (branchwiseCorporate.get(i)[0].toString().equals("9001")) {
						DubaiPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}

					if (branchwiseCorporate.get(i)[0].toString().equals("9002")) {
						AbudhabiPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
					if (branchwiseCorporate.get(i)[0].toString().equals("9003")) {
						DeiraPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
					if (branchwiseCorporate.get(i)[0].toString().equals("9004")) {
						SharjhaPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}

					if (branchwiseCorporate.get(i)[0].toString().equals("9008")) {
						SyndPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
				}

			}
			// Individuals Attribue
			md.addAttribute("DubaiPendIndividuals", DubaiPendIndividuals);
			md.addAttribute("AbudhabiPendIndividuals", AbudhabiPendIndividuals);
			md.addAttribute("DeiraPendIndividuals", DeiraPendIndividuals);
			md.addAttribute("SharjhaPendIndividuals", SharjhaPendIndividuals);
			md.addAttribute("RasalkhaimaPendIndividuals", RasalkhaimaPendIndividuals);
			md.addAttribute("SyndPendIndividuals", SyndPendIndividuals);

			// Corporate Attribute
			md.addAttribute("DubaiPendCorporate", DubaiPendCorporate);
			md.addAttribute("AbudhabiPendCorporate", AbudhabiPendCorporate);
			md.addAttribute("DeiraPendCorporate", DeiraPendCorporate);
			md.addAttribute("SharjhaPendCorporate", SharjhaPendCorporate);
			md.addAttribute("RasalkhaimaPendCorporate", RasalkhaimaPendCorporate);
			md.addAttribute("SyndPendCorporate", SyndPendCorporate);

			md.addAttribute("completed", Completed);
			md.addAttribute("Pending", Pending);
			md.addAttribute("Under_review", Under_review);
			md.addAttribute("Dashboardpage", Dashboardpage);
			md.addAttribute("corpbarcompleted", corpbarcompleted);
			md.addAttribute("corpbarPending", corpbarPending);
			md.addAttribute("corpbarincomplete", corpbarincomplete);

			md.addAttribute("Branch_code", BRANCHCODE);

			System.out.println(Dashboardpage);
		} else if (Dashboardpage.equals("BRC")) {
			LocalDate today = LocalDate.now(); // Get today's date
			Date fromDateToUse = java.sql.Date.valueOf(today.minusDays(1));

			int matchedCount = 0;
			int unmatchedSourceCount = 0;
			int unmatchedDestinationCount = 0;
			int totalTransactionCount = 0;

			List<BRECON_Common_Table_Entity> matchedList = bRECON_Common_Table_Rep.getcommondatavalues(fromDateToUse);
			matchedCount = (matchedList != null) ? matchedList.size() : 0;

			List<Brecon_core_entity> sourceList = coresystemlistrep.getcoresystemlistvalue(fromDateToUse);
			unmatchedSourceCount = (sourceList != null) ? sourceList.size() : 0;

			List<BRECON_DESTINATION_ENTITY> destList = bRECON_DESTINATION_REPO.getDestinationdatavalues(fromDateToUse);
			unmatchedDestinationCount = (destList != null) ? destList.size() : 0;

			List<Brecon_core_entity> TotalList = coresystemlistrep.getcoresystemlisttotvalue(fromDateToUse);
			totalTransactionCount = (TotalList != null) ? TotalList.size() : 0;

			// Add to model
			md.addAttribute("matchedCount", matchedCount);
			md.addAttribute("unmatchedSourceCount", unmatchedSourceCount);
			md.addAttribute("unmatchedDestinationCount", unmatchedDestinationCount);
			md.addAttribute("totalTransactionCount", totalTransactionCount);
			//// duplicates cbs records
			List<Object[]> cbsduplicaterecord = coresystemlistrep.getcbsduplicaterecord();
			md.addAttribute("cbsduplicaterecord", cbsduplicaterecord);
			//// duplicate AANI Payment Records
			List<Object[]> aaniduplicaterecord = bRECON_DESTINATION_REPO.getaaniduplicaterecord();
			md.addAttribute("aaniduplicaterecord", aaniduplicaterecord);

		} else {

			int completed = 0;
			int uncompleted = 0;

			List<ReportTitle> ls = reportServices.getDashBoardRepList(domainid);

			for (ReportTitle var : ls) {
				if (var.getCompletedFlg().equals('Y')) {
					completed++;
				} else {
					uncompleted++;
				}
			}

			md.addAttribute("reportList", ls);
			md.addAttribute("completed", completed);
			md.addAttribute("uncompleted", uncompleted);
		}

		md.addAttribute("menu", "Dashboard");
		return "XBRLDashboard";
	}

	@RequestMapping(value = "Dashboard", method = { RequestMethod.GET, RequestMethod.POST })
	public String dashboard(@RequestParam(name = "frequency", required = false) String frequency, Model md,
			HttpServletRequest req) {

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		String userid = (String) req.getSession().getAttribute("USERID");
		String Dashboardpage = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");

		System.out.println("Dashboard page is:" + Dashboardpage);
		System.out.println("Branchcode is : " + BRANCHCODE);
		md.addAttribute("menu", "Dashboard");
		md.addAttribute("checkpassExpiry", loginServices.checkpassexpirty(userid));
		md.addAttribute("checkAcctExpiry", loginServices.checkAcctexpirty(userid));
		md.addAttribute("changepassword", loginServices.checkPasswordChangeReq(userid));

		if (Dashboardpage.equals("DCD_ADMIN") || Dashboardpage.equals("DCD_BRANCH")) {
			int Completed = 0;
			int Pending = 0;
			int Under_review = 0;

			int CorpCompleted = 0;
			int CorpPending = 0;
			int CorpUnder_review = 0;

			int corpbarcompleted = 0;
			int corpbarPending = 0;
			int corpbarincomplete = 0;

			/// Counts fetched for Dashborad page Pending kyc INDIVIDUAL details branch wise
			BigDecimal DubaiPendIndividuals = new BigDecimal("0");
			BigDecimal AbudhabiPendIndividuals = new BigDecimal("0");
			BigDecimal DeiraPendIndividuals = new BigDecimal("0");
			BigDecimal SharjhaPendIndividuals = new BigDecimal("0");
			BigDecimal RasalkhaimaPendIndividuals = new BigDecimal("0");
			BigDecimal SyndPendIndividuals = new BigDecimal("0");

			BigDecimal DubaiPendCorporate = new BigDecimal("0");
			BigDecimal AbudhabiPendCorporate = new BigDecimal("0");
			BigDecimal DeiraPendCorporate = new BigDecimal("0");
			BigDecimal SharjhaPendCorporate = new BigDecimal("0");
			BigDecimal RasalkhaimaPendCorporate = new BigDecimal("0");
			BigDecimal SyndPendCorporate = new BigDecimal("0");
			if (Dashboardpage.equals("DCD_ADMIN")) {
				Completed = ecddIndividualProfileRepository.Getcompletedcount();
				Under_review = ecddIndividualProfileRepository.GetIncompletedcount();
				Pending = ecddIndividualProfileRepository.GetPendingcount();

				CorpCompleted = kyc_corporate_repo.Getcompletedcount();
				CorpUnder_review = kyc_corporate_repo.GetIncompletedcount();
				CorpPending = kyc_corporate_repo.GetPendingcount();
			} else {
				Completed = ecddIndividualProfileRepository.Getbranchwisecompletedcount(BRANCHCODE);
				Under_review = ecddIndividualProfileRepository.GetbranchwiseIncompletedcount(BRANCHCODE);
				Pending = ecddIndividualProfileRepository.GetbranchwisePendingcount(BRANCHCODE);

				CorpCompleted = kyc_corporate_repo.Getbranchwisecompletedcount(BRANCHCODE);
				CorpUnder_review = kyc_corporate_repo.GetbranchwiseIncompletedcount(BRANCHCODE);
				CorpPending = kyc_corporate_repo.GetbranchwisePendingcount(BRANCHCODE);
			}

			corpbarcompleted = CorpCompleted;
			corpbarPending = CorpPending;
			corpbarincomplete = CorpUnder_review;

			List<Object[]> branchwiseIndividual = ecddIndividualProfileRepository.GetbranchPendingcount();

			for (int i = 0; i < branchwiseIndividual.size(); i++) {

				if (branchwiseIndividual.get(i)[0].toString() != null) {
					if (branchwiseIndividual.get(i)[0].toString().equals("9001")) {
						DubaiPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}

					if (branchwiseIndividual.get(i)[0].toString().equals("9002")) {
						AbudhabiPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
					if (branchwiseIndividual.get(i)[0].toString().equals("9003")) {
						DeiraPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
					if (branchwiseIndividual.get(i)[0].toString().equals("9004")) {
						SharjhaPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}

					if (branchwiseIndividual.get(i)[0].toString().equals("9008")) {
						SyndPendIndividuals = branchwiseIndividual.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseIndividual.get(i)[1].toString());
					}
				}

			}

			List<Object[]> branchwiseCorporate = kyc_corporate_repo.GetbranchPendingcount();
			for (int i = 0; i < branchwiseCorporate.size(); i++) {

				if (branchwiseCorporate.get(i)[0].toString() != null) {
					if (branchwiseCorporate.get(i)[0].toString().equals("9001")) {
						DubaiPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}

					if (branchwiseCorporate.get(i)[0].toString().equals("9002")) {
						AbudhabiPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
					if (branchwiseCorporate.get(i)[0].toString().equals("9003")) {
						DeiraPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
					if (branchwiseCorporate.get(i)[0].toString().equals("9004")) {
						SharjhaPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}

					if (branchwiseCorporate.get(i)[0].toString().equals("9008")) {
						SyndPendCorporate = branchwiseCorporate.get(i)[1] == null ? new BigDecimal("0")
								: new BigDecimal(branchwiseCorporate.get(i)[1].toString());
					}
				}

			}
			// Individuals Attribue
			md.addAttribute("DubaiPendIndividuals", DubaiPendIndividuals);
			md.addAttribute("AbudhabiPendIndividuals", AbudhabiPendIndividuals);
			md.addAttribute("DeiraPendIndividuals", DeiraPendIndividuals);
			md.addAttribute("SharjhaPendIndividuals", SharjhaPendIndividuals);
			md.addAttribute("RasalkhaimaPendIndividuals", RasalkhaimaPendIndividuals);
			md.addAttribute("SyndPendIndividuals", SyndPendIndividuals);

			// Corporate Attribute
			md.addAttribute("DubaiPendCorporate", DubaiPendCorporate);
			md.addAttribute("AbudhabiPendCorporate", AbudhabiPendCorporate);
			md.addAttribute("DeiraPendCorporate", DeiraPendCorporate);
			md.addAttribute("SharjhaPendCorporate", SharjhaPendCorporate);
			md.addAttribute("RasalkhaimaPendCorporate", RasalkhaimaPendCorporate);
			md.addAttribute("SyndPendCorporate", SyndPendCorporate);

			md.addAttribute("completed", Completed);
			md.addAttribute("Pending", Pending);
			md.addAttribute("Under_review", Under_review);
			md.addAttribute("Dashboardpage", Dashboardpage);
			md.addAttribute("corpbarcompleted", corpbarcompleted);
			md.addAttribute("corpbarPending", corpbarPending);
			md.addAttribute("corpbarincomplete", corpbarincomplete);

			md.addAttribute("Branch_code", BRANCHCODE);

			System.out.println(Dashboardpage);
		} else if (Dashboardpage.equals("BRC")) {
			LocalDate today = LocalDate.now(); // Get today's date
			Date fromDateToUse = java.sql.Date.valueOf(today.minusDays(1));

			int matchedCount = 0;
			int unmatchedSourceCount = 0;
			int unmatchedDestinationCount = 0;
			int totalTransactionCount = 0;

			List<BRECON_Common_Table_Entity> matchedList = bRECON_Common_Table_Rep.getcommondatavalues(fromDateToUse);
			matchedCount = (matchedList != null) ? matchedList.size() : 0;

			List<Brecon_core_entity> sourceList = coresystemlistrep.getcoresystemlistvalue(fromDateToUse);
			unmatchedSourceCount = (sourceList != null) ? sourceList.size() : 0;

			List<BRECON_DESTINATION_ENTITY> destList = bRECON_DESTINATION_REPO.getDestinationdatavalues(fromDateToUse);
			unmatchedDestinationCount = (destList != null) ? destList.size() : 0;

			List<Brecon_core_entity> TotalList = coresystemlistrep.getcoresystemlisttotvalue(fromDateToUse);
			totalTransactionCount = (TotalList != null) ? TotalList.size() : 0;

			// Add to model
			md.addAttribute("matchedCount", matchedCount);
			md.addAttribute("unmatchedSourceCount", unmatchedSourceCount);
			md.addAttribute("unmatchedDestinationCount", unmatchedDestinationCount);
			md.addAttribute("totalTransactionCount", totalTransactionCount);
			md.addAttribute("Dashboardpage", Dashboardpage);

			//// duplicates cbs records
			List<Object[]> cbsduplicaterecord = coresystemlistrep.getcbsduplicaterecord();
			md.addAttribute("cbsduplicaterecord", cbsduplicaterecord);
			//// duplicate AANI Payment Records
			List<Object[]> aaniduplicaterecord = bRECON_DESTINATION_REPO.getaaniduplicaterecord();
			md.addAttribute("aaniduplicaterecord", aaniduplicaterecord);

		} else if (Dashboardpage.equalsIgnoreCase("Superadmin")) {

			int completed = 0;
			int uncompleted = 0;

			List<ReportTitle> ls = reportServices.getDashBoardRepList(domainid);

			for (ReportTitle var : ls) {
				if (var.getCompletedFlg().equals('Y')) {
					completed++;
				} else {
					uncompleted++;
				}
			}

			List<Object[]> rawList = XBRLReportsMasterReps.getsinstatus();
			List<Map<String, Object>> brfStatusList = new ArrayList<>();
			for (Object[] row : rawList) {
				Map<String, Object> map = new HashMap<>();
				map.put("reportName", row[0]);
				map.put("description", row[1]);
				map.put("frequency", row[2]);
				map.put("reportingDate", row[3]);
				map.put("status", row[4]);
				brfStatusList.add(map);
			}
			md.addAttribute("brfStatusList", brfStatusList);
			md.addAttribute("menu", "Dashboard");
			md.addAttribute("netprofit", BRF004ReportServices.getBRF004View_one());

			md.addAttribute("reportList", ls);
			md.addAttribute("completed", completed);
			md.addAttribute("uncompleted", uncompleted);
			md.addAttribute("menu", "Dashboard");
			md.addAttribute("Dashboardpage", Dashboardpage);
			md.addAttribute("selectedFrequency", frequency);

		}

		md.addAttribute("menu", "Dashboard");
		return "XBRLDashboard";
	}

	@RequestMapping(value = "RRRptMast", method = { RequestMethod.GET, RequestMethod.POST })
	public String RrRptMast(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String rptcode,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		String loginuserid = (String) req.getSession().getAttribute("USERID");
		String WORKCLASSAC = (String) req.getSession().getAttribute("WORKCLASS");
		String ROLEIDAC = (String) req.getSession().getAttribute("ROLEID");

		System.out.println("work class is : " + WORKCLASSAC);
		// Logging Navigation
		loginServices.SessionLogging("USERPROFILE", "M2", req.getSession().getId(), loginuserid, req.getRemoteAddr(),
				"ACTIVE");
		Session hs1 = sessionFactory.getCurrentSession();
		md.addAttribute("menu", "USER PROFILE"); // To highlight the menu

		if (formmode == null || formmode.equals("list")) {

			md.addAttribute("formmode", "list");// to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("WORKCLASSAC", WORKCLASSAC);
			md.addAttribute("ROLEIDAC", ROLEIDAC);
			md.addAttribute("loginuserid", loginuserid);

			Iterable<UserProfile> user = loginServices.getUsersList(ROLEIDAC);

			md.addAttribute("userProfiles", user);

		} else if ("edit".equals(formmode)) {
			md.addAttribute("formmode", formmode);

			if (rptcode != null && !rptcode.isEmpty()) {
				RRReport entity = rrReportRepo.getParticularReport(rptcode);
				if (entity != null) {
					System.out.println("Fetched srl_no: " + entity.getSrl_no());
					md.addAttribute("rrRptMast", entity);
				} else {
					System.out.println("No report found for rptcode: " + rptcode);
					md.addAttribute("error", "Report not found");
					return "RRRptMast?formmode=list";
				}
			} else {
				System.out.println("rptcode is null or empty");
				return "RRRptMast?formmode=list";
			}

			System.out.println("came to edit controller");
		} else {

			md.addAttribute("formmode", formmode);
			md.addAttribute("domains", reportServices.getDomainList());
			md.addAttribute("FinUserProfiles", loginServices.getFinUsersList());
			md.addAttribute("userProfile", loginServices.getUser(""));

		}

		return "RR_RPT_MAST";
	}

	@GetMapping("/profit-data")
	@ResponseBody
	public Map<String, Double> getProfitDataByYear(@RequestParam String year) {
		Map<String, BigDecimal> rawMap = BRF004ReportServices.getMonthlyProfitByYear(year);
		System.out.println("The Profit size is:" + rawMap.size());
		Map<String, Double> profitMap = new LinkedHashMap<>();
		for (Map.Entry<String, BigDecimal> entry : rawMap.entrySet()) {
			profitMap.put(entry.getKey(), entry.getValue().doubleValue());
		}
		return profitMap;
	}

	@GetMapping("/fetch-report-data")
	@ResponseBody
	public List<Map<String, Object>> getReportData(@RequestParam String reportDate) {
		List<Object[]> rawList = XBRLReportsMasterReps.getReportStatus(reportDate);
		List<Map<String, Object>> result = new ArrayList<>();

		for (Object[] row : rawList) {
			Map<String, Object> map = new HashMap<>();
			map.put("reportName", row[0]);
			map.put("description", row[1]);
			map.put("frequency", row[2]);
			map.put("reportingDate", row[3]);
			map.put("status", row[4]);
			result.add(map);
		}

		return result;
	}

	/*
	 * @RequestMapping(value = "getDashboardReport", method = RequestMethod.GET)
	 * 
	 * @ResponseBody public String getbranchlistInItemCreation(
	 * 
	 * @RequestParam(required=false) String frequency) {
	 * 
	 * 
	 * return "hi";
	 * 
	 * 
	 * }
	 */

	@GetMapping("/getDashboardReport")
	@ResponseBody
	public List<RRReportDTO> getDashboardReport(@RequestParam(required = false) String frequency) {
		List<Object[]> rawList = RRReportRepo.getCustomReportData(frequency);
		List<RRReportDTO> dtoList = new ArrayList<>();

		for (Object[] row : rawList) {
			RRReportDTO dto = new RRReportDTO();
			dto.setRpt_code((String) row[0]);
			dto.setRpt_description((String) row[1]);
			dto.setDOMAIN((String) row[2]);
			dto.setEnd_date((Date) row[3]);
			dto.setRemarks_3((String) row[4]);
			dtoList.add(dto);
		}

		return dtoList;
	}

	@RequestMapping(value = "/getvalues", method = RequestMethod.POST)
	@ResponseBody
	public List<BRF_095_A_REPORT_ENTITY> getvalues(@RequestParam(required = false) String year) {
		return BRF095AServiceRepos.getvalues(year);
	}

	@RequestMapping(value = "AccessandRoles", method = { RequestMethod.GET, RequestMethod.POST })
	public String IPSAccessandRoles(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menu", "ACCESS AND ROLES");
			md.addAttribute("menuname", "ACCESS AND ROLES");
			md.addAttribute("formmode", "list");
			md.addAttribute("AccessandRoles", accessandrolesrepository.rulelist());
		} else if (formmode.equals("add")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - ADD");
			md.addAttribute("formmode", "add");
		} else if (formmode.equals("edit")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - EDIT");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));
		} else if (formmode.equals("view")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - INQUIRY");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));

		} else if (formmode.equals("verify")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - VERIFY");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));

		} else if (formmode.equals("delete")) {
			md.addAttribute("menuname", "ACCESS AND ROLES - DELETE");
			md.addAttribute("formmode", formmode);
			md.addAttribute("IPSAccessRole", AccessRoleService.getRoleId(userid));
		}

		md.addAttribute("adminflag", "adminflag");
		md.addAttribute("userprofileflag", "userprofileflag");

		return "AccessandRoles";
	}

	@RequestMapping(value = "createAccessRole", method = RequestMethod.POST)
	@ResponseBody
	public String createAccessRoleEn(@RequestParam("formmode") String formmode,
			@RequestParam(value = "adminValue", required = false) String adminValue,
			@RequestParam(value = "BRF_ReportsValue", required = false) String BRF_ReportsValue,
			@RequestParam(value = "Basel_ReportsValue", required = false) String Basel_ReportsValue,
			@RequestParam(value = "ArchivalValue", required = false) String ArchivalValue,
			@RequestParam(value = "Audit_InquiriesValue", required = false) String Audit_InquiriesValue,
			@RequestParam(value = "RBR_ReportsValue", required = false) String RBR_ReportsValue,
			@RequestParam(value = "VAT_LedgerValue", required = false) String VAT_LedgerValue,
			@RequestParam(value = "Invoice_DataValue", required = false) String Invoice_DataValue,
			@RequestParam(value = "ReconciliationValue", required = false) String ReconciliationValue,
			@RequestParam(value = "finalString", required = false) String finalString,

			@ModelAttribute AccessAndRoles alertparam, Model md, HttpServletRequest rq) {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		String msg = AccessRoleService.addPARAMETER(alertparam, formmode, adminValue, BRF_ReportsValue,
				Basel_ReportsValue, ArchivalValue, Audit_InquiriesValue, RBR_ReportsValue, ReconciliationValue,
				VAT_LedgerValue, Invoice_DataValue, finalString, userid);

		return msg;

	}

	@RequestMapping(value = "resetPassword1", method = { RequestMethod.GET, RequestMethod.POST })
	public String showResetPasswordPage(Model md, HttpServletRequest req) {
		String Passworduser = (String) req.getSession().getAttribute("USERID");
		String Passwordresest = (String) req.getSession().getAttribute("PASSWORDERROR");

		md.addAttribute("Resetuserid", Passworduser);
		md.addAttribute("Resetreason", Passwordresest);
		return "XBRLresetPassword"; // Name of the HTML file (resetPassword.html)
	}

	@PostMapping("/resetPassword")
	public String resetPassword(@RequestParam String userid, @RequestParam String newPassword)
			throws ParseException, NoSuchAlgorithmException, InvalidKeySpecException {
		Optional<UserProfile> userOptional = userProfileRep.findById(userid);
		String encryptedPassword = PasswordEncryption.getEncryptedPassword(newPassword);
		if (userOptional.isPresent()) {
			UserProfile user = userOptional.get();
			user.setPassword(encryptedPassword); // Encrypt the new password
			String localdateval = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			LocalDate date = LocalDate.parse(localdateval);
			BigDecimal passexpdays = new BigDecimal(user.getPass_exp_days());
			LocalDate date2 = date.plusDays(passexpdays.intValue());
			user.setLog_in_count("1");
			user.setNo_of_attmp(0);
			user.setUser_status("Active");
			user.setUser_status("Active");
			user.setDisable_flg("N");
			user.setUser_locked_flg("N");
			user.setPass_exp_date(new SimpleDateFormat("yyyy-MM-dd").parse(date2.toString()));// Reset the flag
			userProfileRep.save(user);
			return "redirect:login?resetSuccess";
		}

		return "redirect:resetPassword1?error=User not found";
	}

	@GetMapping("/getRoleDetails")
	@ResponseBody
	public AccessAndRoles getRoleDetails(@RequestParam String roleId) {
		System.out.println("role id for fetching is : " + roleId);
		return accessandrolesrepository.findById(roleId).orElse(null);
	}

	@RequestMapping(value = "UserProfile", method = { RequestMethod.GET, RequestMethod.POST })
	public String userprofile(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		String loginuserid = (String) req.getSession().getAttribute("USERID");
		String WORKCLASSAC = (String) req.getSession().getAttribute("WORKCLASS");
		String ROLEIDAC = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("RuleIDType", accessandrolesrepository.roleidtype());

		System.out.println("work class is : " + WORKCLASSAC);
		// Logging Navigation
		loginServices.SessionLogging("USERPROFILE", "M2", req.getSession().getId(), loginuserid, req.getRemoteAddr(),
				"ACTIVE");
		Session hs1 = sessionFactory.getCurrentSession();
		md.addAttribute("menu", "USER PROFILE"); // To highlight the menu

		if (formmode == null || formmode.equals("list")) {

			md.addAttribute("formmode", "list");// to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("WORKCLASSAC", WORKCLASSAC);
			md.addAttribute("ROLEIDAC", ROLEIDAC);
			md.addAttribute("loginuserid", loginuserid);

			Iterable<UserProfile> user = loginServices.getUsersList(ROLEIDAC);

			md.addAttribute("userProfiles", user);

		} else if (formmode.equals("edit")) {

			md.addAttribute("formmode", formmode);
			md.addAttribute("domains", reportServices.getDomainList());
			md.addAttribute("userProfile", loginServices.getUser(userid));

		} else if (formmode.equals("verify")) {

			md.addAttribute("formmode", formmode);
			md.addAttribute("domains", reportServices.getDomainList());
			md.addAttribute("userProfile", loginServices.getUser(userid));

		} else {

			md.addAttribute("formmode", formmode);
			md.addAttribute("domains", reportServices.getDomainList());
			md.addAttribute("FinUserProfiles", loginServices.getFinUsersList());
			md.addAttribute("userProfile", loginServices.getUser(""));

		}

		return "XBRLUserprofile";
	}

	@RequestMapping(value = "BankMaster", method = RequestMethod.GET)
	public String bankmaster(Model md, HttpServletRequest req) {
		// Logging Navigation
		// System.out.print("fgdfh");
		String userid = (String) req.getSession().getAttribute("USERID");
		loginServices.SessionLogging("BANKMAST", "M3", req.getSession().getId(), userid, req.getRemoteAddr(), "ACTIVE");
		md.addAttribute("bankdata", bankServices.getBankData());
		md.addAttribute("singledetail", new BankMaster());
		md.addAttribute("menu", "BankMaster");

		return "XBRLBankMaster";
	}

	@Autowired
	Gl_balance_recon_rep Gl_balance_recon_rep;

	@RequestMapping(value = "GLBalanceRecon", method = { RequestMethod.GET, RequestMethod.POST })
	public String GLBalanceRecon(@RequestParam(required = false) String formmode,
			@RequestParam(value = "Selecteddate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date Selecteddate,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");

		LocalDate today = LocalDate.now();

		if (Selecteddate != null) {
			Selecteddate = Selecteddate;
		} else {
			Selecteddate = java.sql.Date.valueOf(today.minusDays(0));
		}

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menu", "Gl Reconciliation");
			md.addAttribute("menuname", "Gl Reconciliation");
			md.addAttribute("formmode", "list");
			md.addAttribute("Selectedreport_date", Selecteddate);
			md.addAttribute("Gl_balance_recon_data", Gl_balance_recon_rep.GlGetGlbalancebydate(Selecteddate));
		}

		md.addAttribute("adminflag", "adminflag");
		md.addAttribute("userprofileflag", "userprofileflag");

		return "GLBalanceRecon";
	}

	@RequestMapping(value = "EtlMonitor", method = RequestMethod.GET)
	public String etlMonitor(Model md, HttpServletRequest req) {
		// Logging Navigation

		String userid = (String) req.getSession().getAttribute("USERID");

		// loginServices.SessionLogging("", "M3", req.getSession().getId(), userid,
		// req.getRemoteAddr(), "ACTIVE");

		md.addAttribute("EtlError", etlServices.getEtlError());
		md.addAttribute("EtlStatus", etlServices.getEtlStatus());
		md.addAttribute("menu", "EtlMonitor");

		return "XBRLEtlMonitor";
	}

	@RequestMapping(value = "GlSubHead", method = RequestMethod.GET)
	public ModelAndView glSubHead(Model md, HttpServletRequest req,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size) {
		String userid = (String) req.getSession().getAttribute("USERID");

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("xbrlnavigationcontroller -> glSubHead()");

		md.addAttribute("menu", "GlSubHead");
		md.addAttribute("displaymode", "detail");
		ModelAndView mv = glSubHeadConfigService.getGlSubHeadMeta(PageRequest.of(currentPage, pageSize));
		// md.addAttribute("singledetail", new BankMaster());

		return mv;
	}

	@RequestMapping(value = "BranchMaster", method = RequestMethod.GET)
	public String branchMaster(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("BRANCHMAST", "M3", req.getSession().getId(), userid, req.getRemoteAddr(),
				"ACTIVE");

		md.addAttribute("branchList", bankServices.getBranchList());

		md.addAttribute("menu", "BranchMaster");

		return "XBRLBranchMaster";
	}

	@RequestMapping(value = "ReferenceCode", method = RequestMethod.GET)
	public String refcode(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");

		// Logging Navigation
		loginServices.SessionLogging("REFCODE", "M6", req.getSession().getId(), userid, req.getRemoteAddr(), "ACTIVE");

		md.addAttribute("menu", "ReferenceCode");
		md.addAttribute("refCodeTypeList", referenceCodeConfigure.genRefCodeDescList());
		md.addAttribute("referdetail", new GenRefCodeMast());
		return "XBRLRefCodeConfig";
	}

	@RequestMapping(value = "ReportCode", method = RequestMethod.GET)
	public String repcode(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("REPCODE", "M7", req.getSession().getId(), userid, req.getRemoteAddr(), "ACTIVE");

		md.addAttribute("menu", "ReportCode");
		return "XBRLRepCodeConfig";
	}

	@RequestMapping(value = "ReportCodeMaintain", method = RequestMethod.GET)
	public String repmain(Model md, HttpServletRequest req, @RequestParam(required = false) String dtltype,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(required = false) String acctnum) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("REPCODE", "M7", req.getSession().getId(), userid, req.getRemoteAddr(), "ACTIVE");

		md.addAttribute("menu", "ReportCodeMaintain");

		if (dtltype == null) {

			md.addAttribute("repcodelist",
					reportCodeMappingService.getRepCodeMapLists(PageRequest.of(currentPage, pageSize)));
			return "XBRLRepCodeMain";

		} else if (dtltype.equals("page")) {

			md.addAttribute("repcodelist",
					reportCodeMappingService.getRepCodeMapLists(PageRequest.of(currentPage, pageSize)));
			return "XBRLRepCodeMain :: repcodeconfig";

		} else {

			md.addAttribute("repcodelist",
					reportCodeMappingService.getSearchResult(acctnum, PageRequest.of(currentPage, pageSize)));
			return "XBRLRepCodeMain :: repcodeconfig";
		}

	}

	@RequestMapping(value = "ReportCodeMaintain2", method = RequestMethod.GET)
	public String repmain2(Model md, HttpServletRequest req, @RequestParam(required = false) String dtltype,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(required = false) String acctnum) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("REPCODE", "M7", req.getSession().getId(), userid, req.getRemoteAddr(), "ACTIVE");

		md.addAttribute("menu", "ReportCodeMaintain2");

		if (dtltype == null) {

			md.addAttribute("repcodelist",
					reportCodeMappingService.getRepCodeMapLists(PageRequest.of(currentPage, pageSize)));
			return "XBRLRepCodeMain2";

		} else if (dtltype.equals("page")) {

			md.addAttribute("repcodelist",
					reportCodeMappingService.getRepCodeMapLists(PageRequest.of(currentPage, pageSize)));
			return "XBRLRepCodeMain2 :: repcodeconfig";

		} else {

			md.addAttribute("repcodelist",
					reportCodeMappingService.getSearchResult(acctnum, PageRequest.of(currentPage, pageSize)));
			return "XBRLRepCodeMain2 :: repcodeconfig";
		}

	}

	@RequestMapping(value = "ReportMaster", method = RequestMethod.GET)
	public String reportMaster(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("REPORTMAST", "M5", req.getSession().getId(), userid, req.getRemoteAddr(),
				"ACTIVE");

		md.addAttribute("menu", "ReportMaster");
		md.addAttribute("reportList", reportServices.getReportsMaster());
		return "XBRLReportMaster";

	}

	@RequestMapping(value = "Audit", method = RequestMethod.GET)
	public String audit(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("AUDIT", "M11", req.getSession().getId(), userid, req.getRemoteAddr(), "ACTIVE");

		LocalDateTime localDateTime = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		md.addAttribute("menu", "Audit");
		md.addAttribute("auditlogs", reportServices.getAuditLog(
				Date.from(localDateTime.plusDays(-5).atZone(ZoneId.systemDefault()).toInstant()), new Date()));
		return "XBRLAudit";
	}

	@RequestMapping(value = "Userlog", method = RequestMethod.GET)
	public String userlog(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("USERLOG", "M4", req.getSession().getId(), userid, req.getRemoteAddr(), "ACTIVE");

		LocalDateTime localDateTime = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		md.addAttribute("menu", "Userlog");
		md.addAttribute("userlog", loginServices.getUserLog(
				Date.from(localDateTime.plusDays(-5).atZone(ZoneId.systemDefault()).toInstant()), new Date()));

		return "XBRLUserLogs";
	}

	@RequestMapping(value = "XBRLReports", method = RequestMethod.GET)
	public String xbrlrep(Model md, HttpServletRequest req) {

		md.addAttribute("menu", "XBRLReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");

		md.addAttribute("reportlist", reportServices.getReportsList(domainid));
		return "XBRLReports";
	}

	@RequestMapping(value = "XBRLFileUpload", method = RequestMethod.GET)
	public String xbrlFileUpload(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");
		List<UserProfile> list = loginServices.getUsersListone(userid);
		md.addAttribute("domainid", list);
		// Logging Navigation
		loginServices.SessionLogging("FILEUPLOAD", "M10", req.getSession().getId(), userid, req.getRemoteAddr(),
				"ACTIVE");

		md.addAttribute("menu", "XBRLFileUpload");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");

		md.addAttribute("reportlist", reportServices.getFileUploadList());
		return "XBRLFileUpload";
	}

	@RequestMapping(value = "FileUploadRL", method = RequestMethod.GET)
	public String FileUploadReturn(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");
		List<UserProfile> list = loginServices.getUsersListone(userid);
		md.addAttribute("domainid", list);
		// Logging Navigation
		loginServices.SessionLogging("FILEUPLOAD", "M10", req.getSession().getId(), userid, req.getRemoteAddr(),
				"ACTIVE");

		md.addAttribute("menu", "FileUpload");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");

		md.addAttribute("reportlist", reportServices.getFileUploadList());
		return "FileUploadRL";
	}

	@RequestMapping(value = "XBRLArchives", method = RequestMethod.GET)
	public String xbrlarch(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");

		// Logging Navigation
		loginServices.SessionLogging("ARCHREPORTS", "M9", req.getSession().getId(), userid, req.getRemoteAddr(),
				"ACTIVE");

		md.addAttribute("menu", "XBRLArchives");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");

		md.addAttribute("reportlist", reportServices.getArchReportsList(domainid));

		return "XBRLArchive";
	}

	@RequestMapping(value = "MISReports", method = RequestMethod.GET)
	public String xbrlMISReports(Model md, HttpServletRequest req) {

		String userid = (String) req.getSession().getAttribute("USERID");

		// Logging Navigation
		loginServices.SessionLogging("MISREPORTS", "M12", req.getSession().getId(), userid, req.getRemoteAddr(),
				"ACTIVE");

		md.addAttribute("menu", "MISReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");

		md.addAttribute("reportlist", reportServices.getMISReportsList(domainid));

		return "XBRLMISReports";
	}

	@RequestMapping(value = "Finuserdata", method = RequestMethod.GET)
	public ModelAndView Finuserdata(@RequestParam String userid) {
		ModelAndView mv = new ModelAndView("XBRLUserprofile::finuserapply");
		mv.addObject("formmode", "add");

		mv.addObject("userProfile", loginServices.getFinUser(userid));
		return mv;

	}

	@RequestMapping(value = "addreport", method = RequestMethod.POST)
	@ResponseBody
	public String addReport(@RequestParam("formmode") String formmode, @ModelAttribute RRReport rrRptMast, Model md,
			HttpServletRequest rq) {

		String ROLE = (String) rq.getSession().getAttribute("ROLEDESC");
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		String msg = loginServices.addreport(rrRptMast, formmode, userid, username, ROLE);

		System.out.println("came to add report in nav controller which is for add and edit..");
		return msg;

	}

	@RequestMapping(value = "deletereport", method = RequestMethod.POST)
	@ResponseBody
	public String deleteReport(@RequestParam("formmode") String rpt_code, Model md, HttpServletRequest rq) {

		String msg = loginServices.deletereport(rpt_code);

		return msg;

	}

	@RequestMapping(value = "createUser", method = RequestMethod.POST)
	@ResponseBody
	public String createUser(@RequestParam("formmode") String formmode, @ModelAttribute UserProfile userprofile,
			Model md, HttpServletRequest rq) {
		String MOB = (String) rq.getSession().getAttribute("MOBILENUMBER");
		String ROLE = (String) rq.getSession().getAttribute("ROLEDESC");
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		String msg = loginServices.addUser(userprofile, formmode, userid, username, MOB, ROLE);

		return msg;

	}

	@RequestMapping(value = "deleteuser", method = RequestMethod.POST)
	@ResponseBody
	public String deleteuser(@RequestParam("formmode") String userid, Model md, HttpServletRequest rq) {

		String msg = loginServices.deleteuser(userid);

		return msg;

	}

	@RequestMapping(value = "createAlter", method = RequestMethod.POST)
	@ResponseBody
	public String createAlter(@RequestParam("formmode") String formmode, @RequestParam("report_srl") String report_srl,
			@ModelAttribute AlertEntity alertEntity, Model md, HttpServletRequest rq) {
		String MOB = (String) rq.getSession().getAttribute("MOBILENUMBER");
		String ROLE = (String) rq.getSession().getAttribute("ROLEDESC");
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		System.out.println(formmode);
		System.out.println(report_srl);
		String[] a = report_srl.split(",");
		System.out.println(a[0]);
		String report_srl1 = a[0];
		String msg = loginServices.addalerter(alertEntity, formmode, userid, username, MOB, ROLE, report_srl1);

		return msg;

	}

	@RequestMapping(value = "verifyUser", method = RequestMethod.POST)
	@ResponseBody
	public String verifyUser(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.verifyUser(userprofile, userid);

		return msg;

	}

	@RequestMapping(value = "passwordReset", method = RequestMethod.POST)
	@ResponseBody
	public String passwordReset(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.passwordReset(userprofile, userid);

		return msg;

	}

	@RequestMapping(value = "defaultpasswordReset", method = RequestMethod.POST)
	@ResponseBody
	public String DefaultpasswordReset(@ModelAttribute UserProfile userprofile, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.DefaultpasswordReset(userprofile, userid);

		return msg;

	}

	@RequestMapping(value = "changePassword", method = RequestMethod.POST)
	@ResponseBody
	public String changePassword(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass,
			Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String msg = loginServices.changePassword(oldpass, newpass, userid);

		return msg;

	}

	@RequestMapping(value = "updateValidity", method = RequestMethod.POST)
	@ResponseBody
	public String updateValidity(@RequestParam("reportid") String reportid, String valid, HttpServletRequest rq) {

		String userid = (String) rq.getSession().getAttribute("USERID");

		return reportServices.updateValidity(reportid, valid, userid);

	}

	@RequestMapping(value = "userLogs/Download", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource UserDownload(HttpServletResponse response, @RequestParam String fromdate,
			@RequestParam String todate) throws IOException, SQLException {
		response.setContentType("application/octet-stream");

		InputStreamResource resource = null;

		try {
			Date fromdate2 = new SimpleDateFormat("dd-MM-yyyy").parse(fromdate);
			Date todate2 = new SimpleDateFormat("dd-MM-yyyy").parse(todate);
			File repfile = loginServices.getUserLogFile(fromdate2, todate2);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resource;
	}

	@RequestMapping(value = "auditLogs/Download", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource auditDownload(HttpServletResponse response, @RequestParam String fromdate,
			@RequestParam String todate) throws IOException, SQLException {
		response.setContentType("application/octet-stream");

		InputStreamResource resource = null;

		try {
			Date fromdate2 = new SimpleDateFormat("dd-MM-yyyy").parse(fromdate);
			Date todate2 = new SimpleDateFormat("dd-MM-yyyy").parse(todate);
			File repfile = reportServices.getAuditLogFile(fromdate2, todate2);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resource;
	}

	@RequestMapping(value = "repCodeMain/Download", method = RequestMethod.GET)
	@ResponseBody
	public InputStreamResource repCodeMainDownload(HttpServletResponse response, @RequestParam String function)
			throws IOException, SQLException {

		response.setContentType("application/octet-stream");

		logger.info("Function Selected--->>>" + function);

		InputStreamResource resource = null;

		try {

			File repfile = reportCodeMappingService.getDownloadFile(function);
			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return resource;
	}

	@RequestMapping(value = "logoutUpdate", method = RequestMethod.POST)
	@ResponseBody
	public String logoutUpdate(HttpServletRequest req) {

		String msg;

		String userid = (String) req.getSession().getAttribute("USERID");

		try {
			logger.info("Updating Logout");
			loginServices.SessionLogging("LOGOUT", "M0", req.getSession().getId(), userid, req.getRemoteAddr(),
					"IN-ACTIVE");
			msg = "success";
		} catch (Exception e) {
			e.printStackTrace();
			msg = "failed";
		}
		return msg;
	}

	@PostMapping("repCodeMain/Upload")
	@ResponseBody
	public String FileUpload(@RequestParam("file") MultipartFile file, HttpServletRequest rq)
			throws IOException, SQLException {

		String msg = "";

		String userid = (String) rq.getSession().getAttribute("USERID");
		msg = reportCodeMappingService.processUploadFiles(file, userid);

		return msg;
	}

	@RequestMapping(value = "CustomReports", method = { RequestMethod.GET, RequestMethod.POST })
	public String CustomReports(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String rpt_ref_no, @RequestParam(required = false) String userid,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		System.out.println("page" + currentPage);
		System.out.println("page" + pageSize);
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		// md.addAttribute("AMLRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menuname", "Reports Parameter");
			md.addAttribute("formmode", "list"); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("repParameter", custReportsParmsRepo.findAllCustom(PageRequest.of(currentPage, pageSize)));

		} else if (formmode.equals("add")) {

			md.addAttribute("menuname", "Reports Parameter - Add");
			md.addAttribute("formmode", "add");

		} else if (formmode.equals("edit")) {

			md.addAttribute("menuname", "Reports Parameter - Edit");
			md.addAttribute("formmode", "edit");
			md.addAttribute("Parameter", customRepParamServices.getParam(rpt_ref_no));

		} else if (formmode.equals("verify")) {

			md.addAttribute("menuname", "Reports Parameter - Verify");
			md.addAttribute("formmode", "verify");
			md.addAttribute("Parameter", customRepParamServices.getParam(rpt_ref_no));

		} else if (formmode.equals("view")) {

			md.addAttribute("menuname", "Reports Parameter - Inquiry");
			md.addAttribute("formmode", "view");
			md.addAttribute("Parameter", customRepParamServices.getParam(rpt_ref_no));

		}

		return "CustomRepParameter";
	}

	@RequestMapping(value = "createRepParam", method = RequestMethod.POST)
	@ResponseBody
	public String createRepParam(@RequestParam("formmode") String formmode,
			@ModelAttribute CustomReportParms customReportParms, Model md, HttpServletRequest rq)
			throws IOException, SQLException {

		String msg = customRepParamServices.customParam(customReportParms, formmode);
		md.addAttribute("adminflag", "adminflag");

		return msg;

	}

	@RequestMapping(value = "CustomRepGen", method = { RequestMethod.GET, RequestMethod.POST })
	public String CustomRepGen(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));
		String userid1 = (String) req.getSession().getAttribute("USERID");

		System.out.println("page" + currentPage);
		System.out.println("page" + pageSize);
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		// md.addAttribute("AMLRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menu", "Report Generator");
			md.addAttribute("userProfile", loginServices.getUser(userid1));
			md.addAttribute("formmode", "list"); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("repParameter", custReportsParmsRepo.findAllCustom(PageRequest.of(currentPage, pageSize)));
		}

		return "CustomRepGeneration";
	}

	@RequestMapping(value = "CustomRepDown", method = { RequestMethod.GET, RequestMethod.POST })
	public String CustomRepDown(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));
		String userid1 = (String) req.getSession().getAttribute("USERID");

		System.out.println("page" + currentPage);
		System.out.println("page" + pageSize);
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		// md.addAttribute("AMLRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("menu", "Report Generator");
			md.addAttribute("userProfile", loginServices.getUser(userid1));
			md.addAttribute("formmode", "list"); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("repParameter", customRepDownloadRep.findAllCustom(PageRequest.of(currentPage, pageSize)));
		}

		return "CustomRepDownload";
	}

	@RequestMapping(value = "ExecuteRep", method = { RequestMethod.GET, RequestMethod.POST })
	public String ExecuteRep(@RequestParam(value = "ref_id", required = false) String ref_id,
			@RequestParam(value = "Param1", required = false) String input1,
			@RequestParam(value = "Param1", required = false) String input2,
			@RequestParam(value = "Param1", required = false) String input3,
			@RequestParam(value = "Param1", required = false) String input4,
			@RequestParam(value = "Param1", required = false) String input5,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req)
			throws SQLException {
		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		// md.addAttribute("AMLRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("adminflag", "adminflag");

		try {
			md.addAttribute("formmode", "list"); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("headers", customerRepGenServices.getHeaders(ref_id));
			md.addAttribute("repList",
					customerRepGenServices.parameterlistwithdecode(ref_id, PageRequest.of(currentPage, pageSize)));

			CustomReportParms up = customReportsParmsRepo.findByIdcustom(ref_id);
			md.addAttribute("CustomReportParam", up);

		} catch (IOException e) {

			e.printStackTrace();
		}

		return "ReportExecutePage";

	}

	/**************************************************************************
	 * RBS REPORTS
	 **************************************************************************/

	@RequestMapping(value = "rbsreports", method = { RequestMethod.GET, RequestMethod.POST })
	public String RbsReports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RBS Report");

		md.addAttribute("reportlist", rbsReportlist.getReportList());

		return "RBS_AML/RBSReports";
	}

	@RequestMapping(value = "rrreports", method = { RequestMethod.GET, RequestMethod.POST })
	public String RRReports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RR Report");

		md.addAttribute("reportlist", rrReportlist.getReportList());
		// md.addAttribute("reportlist", rrReportlist.getReportListBASEL());

		return "RR/RRReports";
	}

	@RequestMapping(value = "rrreports1", method = { RequestMethod.GET, RequestMethod.POST })
	public String RRReportsBASEL(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListBASEL());

		return "RR/RRReports";
	}

	@RequestMapping(value = "halfyearly1", method = { RequestMethod.GET, RequestMethod.POST })
	public String halfyearly1(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Halfyearly 1 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListhalfyearly1());

		return "RR/RRReports";
	}

	@RequestMapping(value = "halfyearly2", method = { RequestMethod.GET, RequestMethod.POST })
	public String halfyearly2(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Halfyearly 2 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListhalfyearly2());

		return "RR/RRReports";
	}

	@RequestMapping(value = "fort", method = { RequestMethod.GET, RequestMethod.POST })
	public String ford(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Fortnightly -BRF Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getFord());

		return "RR/RRReports";
	}

	@GetMapping("/checkDomainFlag")
	@ResponseBody
	public ResponseEntity<String> checkDomainFlag(@RequestParam String rptcode) {
		Optional<RRReport> report = rrReportRepo.getParticularReport3(rptcode);

		if (report.isPresent()) {
			String domain = report.get().getDOMAIN(); // Add getter in entity if not already
			if ("Y".equalsIgnoreCase(domain)) {
				return ResponseEntity.ok("ENABLED");
			} else {
				return ResponseEntity.ok("DISABLED");
			}
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NOT_FOUND");
		}
	}

	@RequestMapping(value = "bankingbook", method = { RequestMethod.GET, RequestMethod.POST })
	public String bankingbook(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");

		// md.addAttribute("reportsflag", "reportsflag");
		// md.addAttribute("menu", "Fortnightly -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		// md.addAttribute("reportlist", rrReportlist.getFord());

		return "RR/BRFBankingbook";
	}

	@RequestMapping(value = "monthly1", method = { RequestMethod.GET, RequestMethod.POST })
	public String monthly1(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		// md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Monthly 1 - BRF Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListmonthly1());

		return "RR/RRReports";
	}

	@RequestMapping(value = "monthly2", method = { RequestMethod.GET, RequestMethod.POST })
	public String monthly2(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Monthly 2 -BRF Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListmonthly2());

		return "RR/RRReports";
	}

	@RequestMapping(value = "monthly3", method = { RequestMethod.GET, RequestMethod.POST })
	public String monthly3(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Monthly 3 -BRF Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListmonthly3());

		return "RR/RRReports";
	}

	@RequestMapping(value = "quarterly1", method = { RequestMethod.GET, RequestMethod.POST })
	public String quarterly1(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Quarterly 1 -BRF Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListquarterly1());

		return "RR/RRReports";
	}

	@RequestMapping(value = "quarterly2", method = { RequestMethod.GET, RequestMethod.POST })
	public String quarterly2(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Quarterly 2 -BRF Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListquarterly2());

		return "RR/RRReports";
	}

	@RequestMapping(value = "yearly", method = { RequestMethod.GET, RequestMethod.POST })
	public String yearly(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Yearly -BRF Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportListyearly());

		return "RR/RRReports";
	}

	@RequestMapping(value = "rlreports", method = { RequestMethod.GET, RequestMethod.POST })
	public String RLReports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Restructured Loan Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListRL());

		return "RBS_AML/RBSReports";
	}

	@RequestMapping(value = "RBSDataMaintenance", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBSDataMaintenance(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RBS Data Maintenance");

		md.addAttribute("RepMaster", rbsReportlist.getReportList());

		return "RBS_AML/RBSDataMaintenance";
	}

	@RequestMapping(value = "BRFValidations", method = { RequestMethod.GET, RequestMethod.POST })
	public String BRFValidations(Model md, @RequestParam(value = "rptcode", required = false) String rptcode,
			@RequestParam(value = "todate", required = false) String todate, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		System.out.println("role id issssssssssssssssssssssssssss" + roleId);

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		// md.addAttribute("reportsflag", "reportsflag");
		// md.addAttribute("menu", "RBS Data Maintenance");

		md.addAttribute("reportlist", brfValidationsRepo.getValidationList(rptcode));
		md.addAttribute("reportlist1", rrReportlist.getReportbyrptcode(rptcode));
		md.addAttribute("RoleId", roleId);

		md.addAttribute("rpt_date", todate);
		return "RR/BRFValidations";
	}

	@RequestMapping(value = "RBSArchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBSArchival(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RBS Archival");

		md.addAttribute("reportlist", rbsReportlist.getReportList());

		return "RBS_AML/RBSArchival";
	}

	@RequestMapping(value = "CRRBSarchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String CRRBSarchival(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "CR RBS Archival");

		md.addAttribute("reportlist", rbsReportlist.getReportListCR());

		return "CR_RBS/CRRBSArchival";
	}

	@RequestMapping(value = "LRArchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String LRArchival(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "LR RBS Archival");

		md.addAttribute("reportlist", rbsReportlist.getReportListLR());

		return "LR_RBS/LRRBSArchival";
	}

	@RequestMapping(value = "MRArchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String MRArchival(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "MR RBS Archival");

		md.addAttribute("reportlist", rbsReportlist.getReportListMR());

		return "MR_RBS/MRRBSArchival";
	}

	@RequestMapping(value = "rbsarchivalform", method = { RequestMethod.GET, RequestMethod.POST })
	public String Rbsarchivalfrom(Model md, @RequestParam(value = "reportid", required = false) String reportid,
			@RequestParam(value = "repdesc", required = false) String repdesc, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RBS Report");
		md.addAttribute("datemodal", "datefilter");
		md.addAttribute("reportid", reportid);
		md.addAttribute("repdesc", repdesc);
		md.addAttribute("reportmodal", "Y");
		md.addAttribute("reportDATE", t1CurProdServicesRepo.getReportList());
		md.addAttribute("reportlist", rbsReportlist.getReportList());

		return "RBS_AML/RBSArchival";
	}

	@RequestMapping(value = "ORRBSarchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String ORRBSarchival(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Operating Risk Archival");

		md.addAttribute("reportlist", rbsReportlist.getReportListOR());

		return "OR_RBS/ORRBSArchival";
	}

	@RequestMapping(value = "IFRSarchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String IFRSarchival(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "IFRS Quantitative Archival");

		md.addAttribute("reportlist", rbsReportlist.getReportListIFRSQUAN());

		return "IFRS/IFRSArchhival";
	}

	@RequestMapping(value = "IFRSQUALIarchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String IFRSQUALIarchival(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "IFRS Qualitative Archival");

		md.addAttribute("reportlist", rbsReportlist.getReportListIFRSQUALI());

		return "IFRS/IFRSQUALIARCHIVAL";
	}

	@RequestMapping(value = "orrbsreports", method = { RequestMethod.GET, RequestMethod.POST })
	public String ORRbsReports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RBS Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListOR());

		return "OR_RBS/OR_RBSReports";
	}

	@RequestMapping(value = "crrbsreports", method = { RequestMethod.GET, RequestMethod.POST })
	public String CRRbsReports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "CR RBS Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListCR());

		return "RBS_AML/RBSReports";
	}

	@RequestMapping(value = "ifrs9quan", method = { RequestMethod.GET, RequestMethod.POST })
	public String IFRSReports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "IFRS Quantitative Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListIFRSQUAN());

		return "RBS_AML/RBSReports";
	}

	@RequestMapping(value = "ifrs9quli", method = { RequestMethod.GET, RequestMethod.POST })
	public String IFRSqualReports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "IFRS Qualitative Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListIFRSQUALI());

		return "RBS_AML/RBSReports";
	}

	@RequestMapping(value = "ReconFileUpload", method = { RequestMethod.GET, RequestMethod.POST })
	public String Debit_Card_Fileupload(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");
		md.addAttribute("menu", "ReconFileUpload");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");

		return "BRECON/ReconFileupload";
	}

	@RequestMapping(value = "RBSReportGeneration", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBSReportGeneration(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("reportvalue", "RBS Report Generation");
		md.addAttribute("reportid", "RBSReportGeneration");
		md.addAttribute("menu", "RBS Report Generation");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");

		return "RBS_AML/RBSReportGeneration";
	}

	@RequestMapping(value = "RBSORReportGeneration", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBSORReportGeneration(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("reportvalue", "Operating Risk Generation");
		md.addAttribute("reportid", "ORReportGeneration");
		md.addAttribute("menu", "Operating Risk Generation");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");

		return "RBS_AML/RBSReportGeneration";

	}

	@RequestMapping(value = "RBSLRReportGeneration", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBSLRReportGeneration(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("reportvalue", "Operating Risk Generation");
		md.addAttribute("reportid", "LRReportGeneration");
		md.addAttribute("menu", "Liquidity Risk Generation");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");

		return "RBS_AML/RBSReportGeneration";

	}

	@RequestMapping(value = "RBSMRReportGeneration", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBSMRReportGeneration(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("reportvalue", "Market Risk Generation");
		md.addAttribute("reportid", "MRReportGeneration");
		md.addAttribute("menu", "Market Risk Generation");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");

		return "RBS_AML/RBSReportGeneration";

	}

	@RequestMapping(value = "RBSCRReportGeneration", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBSCRReportGeneration(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("reportvalue", "Operating Risk Generation");
		md.addAttribute("reportid", "CRReportGeneration");
		md.addAttribute("menu", "Credit Risk Generation");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");

		return "RBS_AML/RBSReportGeneration";

	}

	@RequestMapping(value = "CREDITGENERATION", method = { RequestMethod.GET, RequestMethod.POST })
	public String CREDITReportGeneration(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("reportvalue", "Report Generation");
		md.addAttribute("reportid", "ReportGeneration");
		md.addAttribute("menu", "Report Generation");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");

		return "RL_RBS/ReportGeneration";

	}

	@RequestMapping(value = "RBSCONTACT", method = RequestMethod.GET)
	public ModelAndView RBSCONTACTDETAIL(Model md, HttpServletRequest req,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size) {
		String userid = (String) req.getSession().getAttribute("USERID");

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		logger.info("xbrlnavigationcontroller -> glSubHead()");

		md.addAttribute("menu", "GlSubHead");
		md.addAttribute("displaymode", "detail");
		md.addAttribute("menu", "BBRF CONTACT DETAIL");
		ModelAndView mv = glSubHeadConfigService.RBSCONTACTLIST(PageRequest.of(currentPage, pageSize));
		// md.addAttribute("singledetail", new BankMaster());

		return mv;
	}

	@RequestMapping(value = "rbsValidations", method = { RequestMethod.GET, RequestMethod.POST })
	public String rbsValidations(@RequestParam(value = "reportDate", required = false) String reportDate, Model md,
			HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");

		if (reportDate == null) {
			md.addAttribute("reportvalue", "RBS Report Generation");
			md.addAttribute("reportid", "rbsReportGeneration");
			reportDate = reportDate;
			md.addAttribute("reportDate1", reportDate);
			// md.addAttribute("reportDate1", reportValidationsRepo.getCurrentQtr(new
			// SimpleDateFormat("dd/MM/yyyy")));
			// reportDate = dateFormat.format(new Date());
		} else {
			reportDate = reportDate;
			md.addAttribute("reportDate1", reportDate);
			md.addAttribute("reportvalue", "RBS Report Generation");
			md.addAttribute("reportid", "rbsReportGeneration");
		}
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RBS Validation Report");
		md.addAttribute("testDate", reportValidationsRepo.getCurrentQtr(new SimpleDateFormat("dd/MM/yyyy")));
		md.addAttribute("reportvalue", "RBS Report Generation");
		md.addAttribute("reportid", "rbsReportGeneration");

		md.addAttribute("RepValid", reportValidationsRepo.getValidationList());

		// md.addAttribute("reportvalue", "File Upload");

		return "RR/Validation";
	}

	@RequestMapping(value = "rbsValidationsChk", method = RequestMethod.POST)
	@ResponseBody
	public ValidationResponse rbsValidationsChk(@RequestParam("srl_no") String srl_no,
			@RequestParam("report_date") String report_date, @ModelAttribute ReportValidations reportValidations,
			Model md, HttpServletRequest rq) throws ParseException {
		logger.info("rbsValidationsChk:  Controller");
		ValidationResponse msg = rbsValidationservices.chkRBSValidations(reportValidations, srl_no, report_date);
		md.addAttribute("reportsflag", "reportsflag");

		return msg;

	}

	/*
	 * @RequestMapping(value = "BRFValidations1", method = { RequestMethod.GET,
	 * RequestMethod.POST }) public String BRFValidations(@RequestParam(value =
	 * "rpt_code", required = false) String rpt_code, Model md, HttpServletRequest
	 * req) {
	 * 
	 * String roleId = (String) req.getSession().getAttribute("ROLEID");
	 * 
	 * if (rpt_code == null) { md.addAttribute("reportvalue",
	 * "RBS Report Generation"); md.addAttribute("reportid", "rbsReportGeneration");
	 * rpt_code = rpt_code; //md.addAttribute("reportDate1", reportDate); //
	 * md.addAttribute("reportDate1", reportValidationsRepo.getCurrentQtr(new //
	 * SimpleDateFormat("dd/MM/yyyy"))); // reportDate = dateFormat.format(new
	 * Date()); } else { rpt_code = rpt_code; md.addAttribute("rpt_code1",
	 * rpt_code); md.addAttribute("reportvalue", "RBS Report Generation");
	 * md.addAttribute("reportid", "rbsReportGeneration"); } String domainid =
	 * (String) req.getSession().getAttribute("DOMAINID");
	 * md.addAttribute("reportsflag", "reportsflag"); md.addAttribute("menu",
	 * "RBS Validation Report"); md.addAttribute("testDate",
	 * reportValidationsRepo.getCurrentQtr(new SimpleDateFormat("dd/MM/yyyy")));
	 * md.addAttribute("reportvalue", "RBS Report Generation");
	 * md.addAttribute("reportid", "rbsReportGeneration");
	 * 
	 * md.addAttribute("RepValid", brfValidationsRepo.getValidationList());
	 * 
	 * // md.addAttribute("reportvalue", "File Upload");
	 * 
	 * return "RR/BRFValidations"; }
	 */
	@RequestMapping(value = "BRFValidationsChk", method = RequestMethod.POST)
	@ResponseBody
	public ValidationResponse brfValidationsChk(@RequestParam("srl_no") String srl_no,
			@RequestParam("rpt_code") String rpt_code, @RequestParam("report_date") String report_date,
			@ModelAttribute BRFValidations brfValidations, Model md, HttpServletRequest rq) throws ParseException {
		logger.info("rbsValidationsChk:  Controller");
		ValidationResponse msg = rbsValidationservices.chkBRFValidations(brfValidations, srl_no, report_date);
		md.addAttribute("reportsflag", "reportsflag");

		return msg;

	}

	@RequestMapping(value = "rrrbsreports", method = { RequestMethod.GET, RequestMethod.POST })
	public String rrrbsreports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		// md.addAttribute("menu", "RBS Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListRR());

		return "RR_RBS/RR_RBSReports";
	}

	@RequestMapping(value = "LRrbsreports", method = { RequestMethod.GET, RequestMethod.POST })
	public String LRrbsreports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "LR RBS Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListLR());

		return "RBS_AML/RBSReports";
	}

	@RequestMapping(value = "MRrbsreports", method = { RequestMethod.GET, RequestMethod.POST })
	public String MRrbsreports(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "MR RBS Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListMR());

		return "MR_RBS/MR_RBSReport";
	}

	@RequestMapping(value = "EPINOutstanding", method = { RequestMethod.GET, RequestMethod.POST })
	public String EPINOutstanding(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "LR RBS Report");

		md.addAttribute("reportlist", rbsReportlist.getReportListLR());

		return "RBS_AML/RBSReports";
	}

	@RequestMapping(value = "RBS_RR_ReportGeneration", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBS_RR_ReportGeneration(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("reportvalue", "Operating Risk Generation");
		md.addAttribute("reportid", "RRReportGeneration");
		md.addAttribute("menu", "Residual Risks Generation");
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");

		return "RBS_AML/RBSReportGeneration";

	}

	@RequestMapping(value = "RBSAlertParameters", method = { RequestMethod.GET, RequestMethod.POST })
	public String AMLAlertManagement(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		if (formmode == null || formmode.equals("list")) {

			md.addAttribute("menu", "BRBSAlertParameters");
			md.addAttribute("menuname", "Alert Parameters");
			md.addAttribute("formmode", "list"); // to set which form - valid values are "edit" , "add" & "list"
			/*
			 * md.addAttribute("RuleLists",
			 * ruleenginerepository.findAll(PageRequest.of(currentPage, pageSize)));
			 */
			md.addAttribute("AlertParameterList",
					alertmanagementrepository.alertlist(PageRequest.of(currentPage, pageSize)));
		} else if (formmode.equals("add")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("AlertSrlNo", alertservices.getSrlNoValue());
		} else if (formmode.equals("edit")) {
			md.addAttribute("formmode", formmode);
			// md.addAttribute("domains", userProfileDao.getDomainList());
			md.addAttribute("AlertParameter", alertservices.getSrlNo(srlno));

		} else if (formmode.equals("view")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("AlertParameter", alertservices.getSrlNo(srlno));

		} else if (formmode.equals("delete")) {
			md.addAttribute("AlertParameter", alertservices.getSrlNo(srlno));
			md.addAttribute("formmode", "delete"); // to set which form - valid values are "edit" , "add" & "list"

		}
		md.addAttribute("adminflag", "adminflag");
		md.addAttribute("parameterflag", "parameterflag");

		return "AMLAlertParameters";
	}

	@RequestMapping(value = "createAlert", method = RequestMethod.POST)
	@ResponseBody
	public String createRule(@RequestParam("formmode") String formmode,
			@ModelAttribute AlertManagementEntity alertparam, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");

		String msg = alertservices.addAlert(alertparam, formmode, userid);

		return msg;

	}

	@RequestMapping(value = "MR1", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR1 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK1());

		return "RR/RRReports";
	}

	@RequestMapping(value = "MR3", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK3(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR3 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK3());

		return "RR/RRReports";
	}

	@RequestMapping(value = "MR2", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK2(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR2 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK2());

		return "RR/RRReports";
	}

	@RequestMapping(value = "MR4", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK4(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR4 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK4());

		return "RR/RRReports";
	}

	@RequestMapping(value = "MR5", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK5(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR5 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK5());

		return "RR/RRReports";
	}

	@RequestMapping(value = "MR6", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK6(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR6 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK6());

		return "RR/RRReports";
	}

	@RequestMapping(value = "MR7", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK7(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR7 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK7());

		return "RR/RRReports";
	}

	@RequestMapping(value = "MR8", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK8(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR8 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK8());

		return "RR/RRReports";
	}

	@RequestMapping(value = "MR9", method = { RequestMethod.GET, RequestMethod.POST })
	public String MARKETRISK9(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " MR9 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTMARKETRISK9());

		return "RR/RRReports";
	}

	@RequestMapping(value = "CR1", method = { RequestMethod.GET, RequestMethod.POST })
	public String CREDITRISK(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " CR1 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTCREDITRISK1());

		return "RR/RRReports";
	}

	@RequestMapping(value = "CR3", method = { RequestMethod.GET, RequestMethod.POST })
	public String CREDITRISK3(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " CR3 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTCREDITRISK3());

		return "RR/RRReports";
	}

	@RequestMapping(value = "CR2", method = { RequestMethod.GET, RequestMethod.POST })
	public String CREDITRISK2(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " CR2 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTCREDITRISK2());

		return "RR/RRReports";
	}

	@RequestMapping(value = "CR4", method = { RequestMethod.GET, RequestMethod.POST })
	public String CREDITRISK4(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " CR4 -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTCREDITRISK4());

		return "RR/RRReports";
	}

	@RequestMapping(value = "SR1", method = { RequestMethod.GET, RequestMethod.POST })
	public String SETTLEMENTRISK(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", " Settlement Risk -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTSettlementRISK());

		return "RR/RRReports";
	}

	@RequestMapping(value = "CV1", method = { RequestMethod.GET, RequestMethod.POST })
	public String CreditValuation(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Credit Valuation Adjustment (CVA)  -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTCreditValuation());

		return "RR/RRReports";
	}

	@RequestMapping(value = "EIF", method = { RequestMethod.GET, RequestMethod.POST })
	public String EquityInvestment(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Equity Investment in Funds (EIF)  -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTEquityInvestment());

		return "RR/RRReports";
	}

	@RequestMapping(value = "OR1", method = { RequestMethod.GET, RequestMethod.POST })
	public String OperationalRisk(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Operational Risk  -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTOPERATIONALRISK());

		return "RR/RRReports";
	}

	@RequestMapping(value = "CAR", method = { RequestMethod.GET, RequestMethod.POST })
	public String CapitalAdequacy(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Capital Adequacy Calculation  -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTCapitalAdequacy());

		return "RR/RRReports";
	}

	@RequestMapping(value = "BRF95FB", method = { RequestMethod.GET, RequestMethod.POST })
	public String BRF95FB(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "BRF95 WORKING SUMMARY -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTFB());

		return "RR/RRReports";
	}

	/*
	 * @RequestMapping(value = "User_Audit", method = RequestMethod.GET) public
	 * String Service_Audit(Model md, HttpServletRequest req) {
	 * 
	 * String userid = (String) req.getSession().getAttribute("USERID");
	 * System.out.println("The login userid is : " + userid); // Logging Navigation
	 * // loginServices.SessionLogging("AUDIT", "M11", req.getSession().getId(), //
	 * userid, req.getRemoteAddr(), "ACTIVE");
	 * 
	 * LocalDateTime localDateTime = new
	 * Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	 * System.out.println("The time is " + localDateTime); md.addAttribute("menu",
	 * "Audit"); // md.addAttribute("auditlogs", //
	 * reportServices.getAuditLog(Date.from(localDateTime.plusDays(-5).atZone(ZoneId
	 * .systemDefault()).toInstant()), // new Date()));
	 * md.addAttribute("auditlogss", reportServices.getAuditservices()); return
	 * "User_Audit"; }
	 */

	@RequestMapping(value = "User_Audit", method = RequestMethod.GET)
	public String Service_Audit(Model md, HttpServletRequest req) {
		String userid = (String) req.getSession().getAttribute("USERID");
		System.out.println("The login userid is : " + userid);

		LocalDateTime localDateTime = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		System.out.println("The time is " + localDateTime);

		md.addAttribute("menu", "Audit");

		// Add both lists to the model
		md.addAttribute("auditlogss", reportServices.getAuditservices());
		md.addAttribute("userAuditLevels", reportServices.getUserAuditLevelList());

		return "User_Audit";
	}

	@RequestMapping(value = "User_Audit/DownloadExcel", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadUserAuditExcel() {
		try {
			ByteArrayInputStream in = reportServices.generateUserAuditExcel();

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=User_Audit_Log.xlsx");

			return ResponseEntity.ok().headers(headers)
					.contentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(new InputStreamResource(in));
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}

	@RequestMapping(value = "Audits", method = { RequestMethod.GET, RequestMethod.POST })
	public String Audits(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String delete_cust_id,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {
		List<MANUAL_Service_Entity> changes = mANUAL_Service_Rep.getServiceAuditList(auditRefNo); // or use
																									// findByAuditRefNo()

		if (changes == null || changes.isEmpty()) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		for (MANUAL_Service_Entity entity : changes) {
			sb.append(entity.getField_name()).append(": OldValue: ").append(entity.getOld_value())
					.append(", NewValue: ").append(entity.getNew_value()).append("|||");
		}
		String loginuserid = (String) req.getSession().getAttribute("USERID");
		List<UserProfile> list = loginServices.getUsersListone(loginuserid);
		md.addAttribute("domainid", list);
		if (formmode == null || formmode.equals("list")) {
			System.out.println("hi");
			md.addAttribute("formmode", "list");
			List<MANUAL_Service_Entity> serviceAudits = mANUAL_Service_Rep.getServiceAuditList(auditRefNo);
			md.addAttribute("audits", serviceAudits);
		}
		// md.addAttribute("inlist", AUD_SERVICE_REPO.findbyId(delete_cust_id));

		// to set which form - valid values are "edit" , "add" & "list"
		// md.addAttribute("CustomerKYC",
		// CMGrepository.findAll(PageRequest.of(currentPage, pageSize)));

		else if (formmode.equals("edit")) {
			System.out.println("hlo");
			md.addAttribute("formmode", "edit");
			/* md.addAttribute("inlist", AUD_SERVICE_REPO.getInquirelist()); */
			md.addAttribute("audit", reportServices.getUserAuditLevelList());

		} else if (formmode.equals("add")) {
			md.addAttribute("formmode", "add");
			/* md.addAttribute("inlist", AUD_SERVICE_REPO.getInquirelist()); */
			md.addAttribute("inlist", AUD_SERVICE_REPO.getInquirelist());

		} else if (formmode.equals("delete")) {
			md.addAttribute("formmode", "delete");
			md.addAttribute("inlist", AUD_SERVICE_REPO.getInquirelist());

		} else if (formmode.equals("download")) {
			md.addAttribute("formmode", "download");
			md.addAttribute("inlist", AUD_SERVICE_REPO.getInquirelist());

		}

		else {

			md.addAttribute("formmode", formmode);
		}

		return "Audits";
	}

	@RequestMapping(value = "Audits/DownloadExcel", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> downloadServiceAuditExcel() {
		try {
			// Call the service method created in Step 2
			ByteArrayInputStream in = reportServices.generateServiceAuditExcel();

			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Disposition", "attachment; filename=Service_Audit_Log.xlsx");

			return ResponseEntity.ok().headers(headers)
					.contentType(MediaType
							.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
					.body(new InputStreamResource(in));
		} catch (IOException e) {
			e.printStackTrace();
			return ResponseEntity.status(500).build();
		}
	}

	@RequestMapping(value = "getchanges2", method = RequestMethod.GET)
	@ResponseBody
	public String getchanges2(@RequestParam("audit_ref_no") String auditRefNo) {
		System.out.println("Received audit_ref_no: " + auditRefNo);

		try {
			List<MANUAL_Service_Entity> changes = mANUAL_Service_Rep.getServiceAudiT(auditRefNo);

			if (changes == null || changes.isEmpty()) {
				return ""; // No data found
			}

			StringBuilder sb = new StringBuilder();
			for (MANUAL_Service_Entity entity : changes) {
				sb.append(entity.getField_name()).append(": OldValue: ").append(entity.getOld_value())
						.append(", NewValue: ").append(entity.getNew_value()).append("|||");
			}

			return sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
			return "Error: " + e.getMessage();
		}
	}

	@RequestMapping(value = "archivalform", method = { RequestMethod.GET, RequestMethod.POST })
	public String archivalform(Model md, @RequestParam(value = "reportid", required = false) String reportid,
			@RequestParam(value = "repdesc", required = false) String repdesc, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String userid = (String) req.getSession().getAttribute("USERID");
		List<UserProfile> list = loginServices.getUsersListone(userid);
		md.addAttribute("domainid", list);

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "BRF Report");
		md.addAttribute("datemodal", "datefilter");
		md.addAttribute("reportid", reportid);
		md.addAttribute("repdesc", repdesc);
		md.addAttribute("reportmodal", "Y");
		// md.addAttribute("reportDATE", t1CurProdServicesRepo.getReportList());
		if ("BRF0001".equals(reportid)) {
			md.addAttribute("reportDATE", BRF0001ServiceRepo.getBRF001FORTSERVICE(reportid, repdesc));
		} else if ("BRF001".equals(reportid)) {
			md.addAttribute("reportDATE", BRF001ServiceRepo.getBRF001REPORTSERVICE(reportid, repdesc));
		} else if ("BRF002".equals(reportid)) {
			md.addAttribute("reportDATE", BRF002Servicerepo.getBRF002REPORTSERVICE(reportid, repdesc));
		} else if ("BRF003".equals(reportid)) {
			md.addAttribute("reportDATE", BRF003Servicerepo.getBRF003REPORTSERVICE(reportid, repdesc));
		} else if ("BRF004".equals(reportid)) {
			md.addAttribute("reportDATE", BRF004Servicerepo.getBRF004REPORTSERVICE(reportid, repdesc));
		} else if ("BRF007".equals(reportid)) {
			md.addAttribute("reportDATE", BRF007Servicerepo.getBRF007REPORTSERVICE(reportid, repdesc));
		} else if ("BRF008".equals(reportid)) {
			md.addAttribute("reportDATE", BRF008Servicerepo.getBRF008REPORTSERVICE(reportid, repdesc));
		} else if ("BRF009".equals(reportid)) {
			md.addAttribute("reportDATE", BRF009Servicerepo.getBRF009REPORTSERVICE(reportid, repdesc));
		} else if ("BRF010".equals(reportid)) {
			md.addAttribute("reportDATE", BRF010Servicerepo.getBRF010REPORTSERVICE(reportid, repdesc));
		} else if ("BRF011".equals(reportid)) {
			md.addAttribute("reportDATE", BRF011Servicerepo.getBRF011REPORTSERVICE(reportid, repdesc));
		} else if ("BRF012".equals(reportid)) {
			md.addAttribute("reportDATE", BRF012Servicerepo.getBRF012REPORTSERVICE(reportid, repdesc));
		} else if ("BRF013".equals(reportid)) {
			md.addAttribute("reportDATE", BRF013Servicerepo.getBRF013REPORTSERVICE(reportid, repdesc));
		} else if ("BRF014".equals(reportid)) {
			md.addAttribute("reportDATE", BRF014Servicerepo.getBRF014REPORTSERVICE(reportid, repdesc));
		} else if ("BRF031A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF031Servicerepo.getBRF031REPORTSERVICE(reportid, repdesc));
		} else if ("BRF032A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF32Servicerepo.getBRF032REPORTSERVICE(reportid, repdesc));
		} else if ("BRF033A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF033Servicerepo.getBRF033REPORTSERVICE(reportid, repdesc));
		} else if ("BRF034A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF034Servicerepo.getBRF034REPORTSERVICE(reportid, repdesc));
		} else if ("BRF037A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF037Servicerepo.getBRF037REPORTSERVICE(reportid, repdesc));
		} else if ("BRF040A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF040Servicerepo.getBRF040AREPORTSERVICE(reportid, repdesc));
		} else if ("BRF041A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF041Servicerepo.getBRF041REPORTSERVICE(reportid, repdesc));
		} else if ("BRF044A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF044Servicerepo.getBRF044REPORTSERVICE(reportid, repdesc));
		} else if ("BRF046A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF046Servicerepo.getBRF046REPORTSERVICE(reportid, repdesc));
		} else if ("BRF048A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF048Servicerepo.getBRF048REPORTSERVICE(reportid, repdesc));
		} else if ("BRF049A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF049Servicerepo.getBRF049REPORTSERVICE(reportid, repdesc));
		} else if ("BRF050A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF050Servicerepo.getBRF050REPORTSERVICE(reportid, repdesc));
		} else if ("BRF051".equals(reportid)) {
			md.addAttribute("reportDATE", BRF051Servicerepo.getBRF051REPORTSERVICE(reportid, repdesc));
		} else if ("BRF052".equals(reportid)) {
			md.addAttribute("reportDATE", BRF052Servicerepo.getBRF052REPORTSERVICE(reportid, repdesc));
		} else if ("BRF053".equals(reportid)) {
			md.addAttribute("reportDATE", BRF053Servicerepo.getBRF053REPORTSERVICE(reportid, repdesc));
		} else if ("BRF054".equals(reportid)) {
			md.addAttribute("reportDATE", BRF054Servicerepo.getBRF054REPORTSERVICE(reportid, repdesc));
		} else if ("BRF056".equals(reportid)) {
			md.addAttribute("reportDATE", BRF56Servicerepo.getBRF056REPORTSERVICE(reportid, repdesc));
		} else if ("BRF060".equals(reportid)) {
			md.addAttribute("reportDATE", BRF060Servicerepo.getBRF060REPORTSERVICE(reportid, repdesc));
		} else if ("BRF068".equals(reportid)) {
			md.addAttribute("reportDATE", BRF068Servicerepo.getBRF068REPORTSERVICE(reportid, repdesc));
		}

		/*
		 * else if ("BRF071A".equals(reportid)) { md.addAttribute("reportDATE",
		 * BRF071Servicerepo.getBRF071REPORTSERVICE(reportid, repdesc)); }else if
		 * ("BRF074A".equals(reportid)) { md.addAttribute("reportDATE",
		 * BRF074Servicerepo.getBRF074REPORTSERVICE(reportid, repdesc)); }
		 */else if ("BRF151".equals(reportid)) {
			md.addAttribute("reportDATE", BRF151Servicerepo.getBRF151REPORTSERVICE(reportid, repdesc));
		} else if ("BRF152".equals(reportid)) {
			md.addAttribute("reportDATE", BRF152Servicerepo.getBRF152REPORTSERVICE(reportid, repdesc));
		} else if ("BRF153".equals(reportid)) {
			md.addAttribute("reportDATE", BRF153Servicerepo.getBRF153REPORTSERVICE(reportid, repdesc));
		} else if ("BRF005".equals(reportid)) {
			if ("BRF005".equals(reportid)) {
				List<Object[]> result = BRF005Servicerepo.getBRF005REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF005Servicerepo.getBRF005REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF005Servicerepo.getBRF005REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF035A".equals(reportid)) {
			if ("BRF035A".equals(reportid)) {
				List<Object[]> result = BRF035Servicerepo.getBRF035EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF035Servicerepo.getBRF035EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF035Servicerepo.getBRF035EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF036A".equals(reportid)) {
			if ("BRF036A".equals(reportid)) {
				List<Object[]> result = BRF036Servicerepo.getBRF036EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF036Servicerepo.getBRF036EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF036Servicerepo.getBRF036EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF038A".equals(reportid)) {
			if ("BRF038A".equals(reportid)) {
				List<Object[]> result = BRF038Servicerepo.getBRF038EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF038Servicerepo.getBRF038EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF038Servicerepo.getBRF038EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF039A".equals(reportid)) {
			if ("BRF039A".equals(reportid)) {
				List<Object[]> result = BRF039Servicerepo.getBRF039EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF039Servicerepo.getBRF039EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF039Servicerepo.getBRF039EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF042A".equals(reportid)) {
			if ("BRF042A".equals(reportid)) {
				List<Object[]> result = BRF042Servicerepo.getBRF042EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF042Servicerepo.getBRF042EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF042Servicerepo.getBRF042EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF043A".equals(reportid)) {
			if ("BRF043A".equals(reportid)) {
				List<Object[]> result = BRF043Servicerepo.getBRF043EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF043Servicerepo.getBRF043EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF043Servicerepo.getBRF043EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF045A".equals(reportid)) {
			if ("BRF045A".equals(reportid)) {
				List<Object[]> result = BRF045Servicerepo.getBRF045EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF045Servicerepo.getBRF045EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF045Servicerepo.getBRF045EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF047A".equals(reportid)) {
			if ("BRF047A".equals(reportid)) {
				List<Object[]> result = BRF047Servicerepo.getBRF047EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF047Servicerepo.getBRF047EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF047Servicerepo.getBRF047EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF102A".equals(reportid)) {
			if ("BRF102A".equals(reportid)) {
				List<Object[]> result = BRF102ServiceRepo.getBRF102EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF102ServiceRepo.getBRF102EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF102ServiceRepo.getBRF102EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF108A".equals(reportid)) {
			if ("BRF108A".equals(reportid)) {
				List<Object[]> result = BRF108Servicerepo.getBRF108EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF108Servicerepo.getBRF108EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF108Servicerepo.getBRF108EPORTSERVICE(reportid, repdesc));
			}
		}

		else if ("BRF154".equals(reportid)) {
			if ("BRF154".equals(reportid)) {
				List<Object[]> result = BRF154Servicerepo.getBRF154REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF154Servicerepo.getBRF154REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF154Servicerepo.getBRF154REPORTSERVICE(reportid, repdesc));
			}

		} else if ("BRF155".equals(reportid)) {
			if ("BRF155".equals(reportid)) {
				List<Object[]> result = BRF155Servicerepo.getBRF155REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF155Servicerepo.getBRF155REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF155Servicerepo.getBRF155REPORTSERVICE(reportid, repdesc));
			}

		} else if ("BRF156".equals(reportid)) {
			if ("BRF156".equals(reportid)) {
				List<Object[]> result = BRF156Servicerepo.getBRF156REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF156Servicerepo.getBRF156REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF156Servicerepo.getBRF156REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF057".equals(reportid)) {
			if ("BRF057".equals(reportid)) {
				List<Object[]> result = BRF057Servicerepo.getBRF057REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF057Servicerepo.getBRF057REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF057Servicerepo.getBRF057REPORTSERVICE(reportid, repdesc));
			}

		} else if ("BRF059".equals(reportid)) {
			if ("BRF059".equals(reportid)) {
				List<Object[]> result = BRF059Servicerepo.getBRF059REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF059Servicerepo.getBRF059REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF059Servicerepo.getBRF059REPORTSERVICE(reportid, repdesc));
			}

		} else if ("BRF069".equals(reportid)) {
			if ("BRF069".equals(reportid)) {
				List<Object[]> result = BRF069Servicerepo.getBRF069EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF069Servicerepo.getBRF069EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF069Servicerepo.getBRF069EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF084".equals(reportid)) {
			if ("BRF084".equals(reportid)) {
				List<Object[]> result = BRF084Servicerepo.getBRF084REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF084Servicerepo.getBRF084REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF084Servicerepo.getBRF084REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF085".equals(reportid)) {
			if ("BRF085".equals(reportid)) {
				List<Object[]> result = BRF085Servicerepo.getBRF085REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF085Servicerepo.getBRF085REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF085Servicerepo.getBRF085REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF086".equals(reportid)) {
			if ("BRF086".equals(reportid)) {
				List<Object[]> result = BRF086Servicerepo.getBRF086REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF086Servicerepo.getBRF086REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF086Servicerepo.getBRF086REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF093A".equals(reportid)) {
			if ("BRF093A".equals(reportid)) {
				List<Object[]> result = BRF093Servicerepo.getBRF093REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF093Servicerepo.getBRF093REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF093Servicerepo.getBRF093REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF094A".equals(reportid)) {
			if ("BRF094A".equals(reportid)) {
				List<Object[]> result = BRF094Servicerepo.getBRF094REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF094Servicerepo.getBRF094REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF094Servicerepo.getBRF094REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF204A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF204Servicerepo.getBRF204REPORTSERVICE(reportid, repdesc));
		} else if ("BRF208A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF208Servicerepo.getBRF208REPORTSERVICE(reportid, repdesc));
		} else if ("BRF209A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF209Servicerepo.getBRF209REPORTSERVICE(reportid, repdesc));
		} else if ("BRF205A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF205Servicerepo.getBRF205REPORTSERVICE(reportid, repdesc));
		} else if ("BRF206A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF206Servicerepo.getBRF206REPORTSERVICE(reportid, repdesc));
		} else if ("BRF207A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF207Servicerepo.getBRF207REPORTSERVICE(reportid, repdesc));
		} else if ("BRF210A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF210Servicerepo.getBRF210REPORTSERVICE(reportid, repdesc));
		} else if ("BRF300A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF300Servicerepo.getBRF300REPORTSERVICE(reportid, repdesc));

		} else if ("BRF301A".equals(reportid)) {
			if ("BRF301A".equals(reportid)) {
				List<Object[]> result = BRF301Servicerepo.getBRF301EPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF301Servicerepo.getBRF301EPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF301Servicerepo.getBRF301EPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF064".equals(reportid)) {
			md.addAttribute("reportDATE", BRF064ServiceRepo.getBRF064REPORTSERVICE(reportid, repdesc));
		} else if ("BRF065".equals(reportid)) {
			md.addAttribute("reportDATE", BRF65ServiceRepo.getBRF065REPORTSERVICE(reportid, repdesc));
		} else if ("BRF067".equals(reportid)) {
			md.addAttribute("reportDATE", BRF67ServiceRepo.getBRF067REPORTSERVICE(reportid, repdesc));
		} else if ("BRF062".equals(reportid)) {
			md.addAttribute("reportDATE", BRF062ServiceRepo.getBRF062REPORTSERVICE(reportid, repdesc));
		} else if ("BRF070A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF070AServiceRepo.getBRF070REPORTSERVICE(reportid, repdesc));
		} else if ("BRF071".equals(reportid)) {
			md.addAttribute("reportDATE", BRF71Servicerepo.getBRF071REPORTSERVICE(reportid, repdesc));
		} else if ("BRF073".equals(reportid)) {
			md.addAttribute("reportDATE", BRF73Servicerepo.getBRF073REPORTSERVICE(reportid, repdesc));
		} else if ("BRF074".equals(reportid)) {
			md.addAttribute("reportDATE", BRF74Servicerepo.getBRF074REPORTSERVICE(reportid, repdesc));
		} else if ("BRF077".equals(reportid)) {
			if ("BRF077".equals(reportid)) {
				List<Object[]> result = BRF077Servicerepo.getBRF077REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF077Servicerepo.getBRF077REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF077Servicerepo.getBRF077REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF078".equals(reportid)) {
			if ("BRF078".equals(reportid)) {
				List<Object[]> result = BRF078Servicerepo.getBRF078REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF078Servicerepo.getBRF078REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF078Servicerepo.getBRF078REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF079".equals(reportid)) {
			if ("BRF079".equals(reportid)) {
				List<Object[]> result = BRF079Servicerepo.getBRF079REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF079Servicerepo.getBRF079REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF079Servicerepo.getBRF079REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF080".equals(reportid)) {
			if ("BRF080".equals(reportid)) {
				List<Object[]> result = BRF080Servicerepo.getBRF080REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF080Servicerepo.getBRF080REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF080Servicerepo.getBRF080REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF081".equals(reportid)) {
			if ("BRF081".equals(reportid)) {
				List<Object[]> result = BRF081Servicerepo.getBRF081REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF081Servicerepo.getBRF081REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF081Servicerepo.getBRF081REPORTSERVICE(reportid, repdesc));
			}

		} else if ("BRF082".equals(reportid)) {
			if ("BRF082".equals(reportid)) {
				List<Object[]> result = BRF082Servicerepo.getBRF082REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF082Servicerepo.getBRF082REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF082Servicerepo.getBRF082REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF083".equals(reportid)) {
			if ("BRF083".equals(reportid)) {
				List<Object[]> result = BRF083Servicerepo.getBRF083REPORTSERVICE(reportid, repdesc);

				List<Object[]> res1 = new ArrayList<>(); // or any other way to initialize the list
				if (result != null) {
					String frequency = null;
					Date reportDate = null;
					String reportCode = null;
					// Iterate through the result
					for (Object[] row : result) {
						// Assuming the Object[] contains Frequency, Report_Date, Report_Code in
						// respective indexes
						frequency = (String) row[0]; // For FREQUENCY (index 0)
						reportDate = (Date) row[1]; // For REPORT_DATE (index 1)
						reportCode = (String) row[2];
						System.out.println("frequency" + frequency);
						System.out.println("reportDate" + reportDate);
						System.out.println("reportCode" + reportCode);

						// Do something with the extracted values, for example, add to res1:
						res1.add(row); // Add the whole row or process data as needed
					}
					md.addAttribute("frequency", frequency);
					md.addAttribute("reportDate", reportDate);
					md.addAttribute("reportCode", reportCode);

					md.addAttribute("reportDATE1", BRF083Servicerepo.getBRF083REPORTSERVICE(reportid, repdesc));
				}
			} else {
				md.addAttribute("reportDATE1", BRF083Servicerepo.getBRF083REPORTSERVICE(reportid, repdesc));
			}
		} else if ("BRF066A".equals(reportid)) {

			md.addAttribute("reportDATE", BRF066AServiceRepo.getBRF066REPORTSERVICE(reportid, repdesc));
		} else if ("BRF095A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF095AServiceRepo.getBRF095REPORTSERVICE(reportid, repdesc));
		} else if ("BRF096A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF96AServiceRepo.getBRF096REPORTSERVICE(reportid, repdesc));
		} else if ("BRF099A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF099ServiceRepo.getBRF099REPORTSERVICE(reportid, repdesc));
		} else if ("BRF100A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF100AServiceRepo.getBRF100REPORTSERVICE(reportid, repdesc));
		} else if ("BRF101A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF101ServiceRepo.getBRF101REPORTSERVICE(reportid, repdesc));
		} else if ("BRF103A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF103ServiceRepo.getBRF103REPORTSERVICE(reportid, repdesc));
		} else if ("BRF104A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF104ServiceRepo.getBRF104REPORTSERVICE(reportid, repdesc));
		} else if ("BRF105A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF105ServiceRepo.getBRF105REPORTSERVICE(reportid, repdesc));
		} else if ("BRF106A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF106ServiceRepo.getBRF106REPORTSERVICE(reportid, repdesc));
		} else if ("BRF107A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF107ServiceRepo.getBRF107REPORTSERVICE(reportid, repdesc));
		} else if ("BRF109A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF109ServiceRepo.getBRF109REPORTSERVICE(reportid, repdesc));
		} else if ("BRF087".equals(reportid)) {
			md.addAttribute("reportDATE", BRF087Servicerepo.getBRF087REPORTSERVICE(reportid, repdesc));
		} else if ("BRF092".equals(reportid)) {
			md.addAttribute("reportDATE", BRF092Servicerepo.getBRF092REPORTSERVICE(reportid, repdesc));
		} else if ("BRF181A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF181Servicerepo.getBRF181REPORTSERVICE(reportid, repdesc));
		} else if ("BRF200A".equals(reportid)) {
			md.addAttribute("reportDATE", BRF200Servicerepo.getBRF200REPORTSERVICE(reportid, repdesc));
		} else {
			System.out.println("No matching report ID found.");
		}

		/*
		 * md.addAttribute("reportDATE", rrReportlist.getReportArchival(reportid,
		 * repdesc));
		 */
		// md.addAttribute("reportlist", rrReportlist.getReportLISTCapitalAdequacy());
		System.out.println(reportid + repdesc);
		return "BaselReport";
	}

	@RequestMapping(value = "BaselArchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String BASELArchival(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String userid = (String) req.getSession().getAttribute("USERID");
		List<UserProfile> list = loginServices.getUsersListone(userid);
		md.addAttribute("domainid", list);
		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "BaselReport");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportList1());

		return "BaselReport";
	}

	@RequestMapping(value = "BRFArchival", method = { RequestMethod.GET, RequestMethod.POST })
	public String BRFArchival(Model md, HttpServletRequest req) {
		String userid = (String) req.getSession().getAttribute("USERID");
		List<UserProfile> list = loginServices.getUsersListone(userid);
		md.addAttribute("domainid", list);

		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "BRF ARCHIVAL");

		List<Object[]> reprtlist = rrReportlist.getReportList2();
		List<RRReport> RRReport = new ArrayList<>();

		for (Object[] obj : reprtlist) {
			RRReport report = new RRReport();
			report.setRpt_code((String) obj[0]);
			report.setRpt_description((String) obj[1]);
			report.setRemarks_4((String) obj[2]);
			RRReport.add(report);
		}

		md.addAttribute("reportlist", RRReport);
		return "BRFArchival";
	}

	@RequestMapping(value = "Consolidated", method = { RequestMethod.GET, RequestMethod.POST })
	public String Consolidated(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String userid = (String) req.getSession().getAttribute("USERID");
		List<UserProfile> list = loginServices.getUsersListone(userid);
		md.addAttribute("domainid", list);
		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "Operational Risk  -RR Report");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getReportLISTOPERATIONALRISK());

		return "Consolidated";
	}

	@RequestMapping(value = "Validation", method = { RequestMethod.GET, RequestMethod.POST })
	public String Validation(@RequestParam(value = "reportDate", required = false) String reportDate, Model md,
			HttpServletRequest req) {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String userid = (String) req.getSession().getAttribute("USERID");
		List<UserProfile> list = loginServices.getUsersListone(userid);
		md.addAttribute("domainid", list);

		if (reportDate == null) {
			md.addAttribute("reportvalue", "RBS Report Generation");
			md.addAttribute("reportid", "rbsReportGeneration");
			reportDate = reportDate;
			md.addAttribute("reportDate1", reportDate);
			// md.addAttribute("reportDate1", reportValidationsRepo.getCurrentQtr(new
			// SimpleDateFormat("dd/MM/yyyy")));
			// reportDate = dateFormat.format(new Date());
		} else {
			reportDate = reportDate;
			md.addAttribute("reportDate1", reportDate);
			md.addAttribute("reportvalue", "RBS Report Generation");
			md.addAttribute("reportid", "rbsReportGeneration");
		}
		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "RBS Validation Report");
		md.addAttribute("testDate", reportValidationsRepo.getCurrentQtr(new SimpleDateFormat("dd/MM/yyyy")));
		md.addAttribute("reportvalue", "RBS Report Generation");
		md.addAttribute("reportid", "rbsReportGeneration");

		md.addAttribute("reportlist", rrReportlist.getReportList1());

		// md.addAttribute("reportvalue", "File Upload");

		return "Validation";
	}

	@RequestMapping(value = "AlertNotificationmodel", method = { RequestMethod.GET, RequestMethod.POST })
	public String AlertNotificationmodel(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) String report_srl,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		String loginuserid = (String) req.getSession().getAttribute("USERID");
		// Logging Navigation
		loginServices.SessionLogging("USERPROFILE", "M2", req.getSession().getId(), loginuserid, req.getRemoteAddr(),
				"ACTIVE");

		md.addAttribute("menu", "UserProfile"); // To highlight the menu

		if (formmode == null || formmode.equals("list")) {

			md.addAttribute("formmode", "list"); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("alertlists", loginServices.getAlertList());

		} else if (formmode.equals("add")) {

			md.addAttribute("formmode", formmode);
			// md.addAttribute("domains", reportServices.getDomainList());
			// md.addAttribute("userProfile", loginServices.getAlerter(report_srl));

		} else if (formmode.equals("edit")) {

			md.addAttribute("formmode", formmode);
			md.addAttribute("domains", reportServices.getDomainList());
			md.addAttribute("userProfile", loginServices.getAlerter(report_srl));

		} else if (formmode.equals("view")) {

			md.addAttribute("formmode", formmode);

			md.addAttribute("userProfile", loginServices.getAlerter(report_srl));

		} else if (formmode.equals("delete")) {

			md.addAttribute("formmode", formmode);

			md.addAttribute("userProfile", loginServices.getAlerter(report_srl));

		}

		return "Alertnotifymodal";
	}

	@DeleteMapping("/delete-report/{reportSrl}")
	public ResponseEntity<String> deleteReport(@PathVariable("reportSrl") String reportSrl) {
		try {
			System.out.println("Deleting report_srl is: " + reportSrl);

			List<AlertEntity> entities = alertRep.findByReportSrl(reportSrl);

			if (entities.isEmpty()) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Report not found.");
			}

			for (AlertEntity entity : entities) {
				alertRep.delete(entity);
			}

			return ResponseEntity.ok("Deleted Successfully");
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting report");
		}
	}

	@RequestMapping(value = "ReportCodeMapping", method = RequestMethod.GET)

	public String ReportCodeMapping(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String userid, @RequestParam(required = false) String foracid,
			@RequestParam(required = false) String cust_id,
			@RequestParam(value = "page", required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(required = false) String REPORT_NAME_1, Model md, HttpServletRequest req,
			@RequestParam(required = false) String scheme_code, @RequestParam(required = false) String gl_sub_head_code,
			String report_addl_criteria_1, String report_codes, String report_code, String row_101, String row_code,
			@RequestParam(required = false) String schm_type, @RequestParam(required = false) String gl_code,
			@RequestParam(required = false) String reportlabel, @RequestParam(required = false) String foracid2,
			@RequestParam(required = false) String reportproductmap) {

		// System.out.println(cust_id);
		loginServices.SessionLogging("REPORTMAST", "M5", req.getSession().getId(), userid, req.getRemoteAddr(),
				"ACTIVE");
		Session hs = sessionFactory.getCurrentSession();
		if (formmode == null || formmode.equals("listone")) {

			md.addAttribute("formmode", "list");
			md.addAttribute("formmode1", formmode);
			// md.addAttribute("listone", reportlist_repo.Mapping1());
			md.addAttribute("refCodeTypeList", brf_REP.getLiist());
			md.addAttribute("refCodeTypeList1", brf_REP.getLiist1(report_code));
			// BRF2 -

			md.addAttribute("rf_code", report_code);

		} else if (formmode.equals("Mapped_Accounts") || formmode.equals("UnMapped_Accounts")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("rf_code", report_code);
			md.addAttribute("refCodeTypeList", brf_REP.getLiist());
			md.addAttribute("refCodeTypeList1", brf_REP.getLiist1(report_code));

			// Determine which service method to call
			List<BRF_MAPPING_PROPERTY> rawList;
			if (formmode.equals("Mapped_Accounts")) {
				rawList = reportServices.Mapped(report_code);
			} else {
				rawList = reportServices.returnUnmappedObject(report_code);
			}

			// Convert different Entity types into one List for the HTML table
			List<BRF_MAPPING_TABLE> displayList = new ArrayList<>();
			for (BRF_MAPPING_PROPERTY item : rawList) {
				BRF_MAPPING_TABLE dto = new BRF_MAPPING_TABLE();
				dto.setCust_id(item.getCustid() != null ? item.getCustid().toString() : "");
				dto.setForacid(item.getForacid() != null ? item.getForacid().toString() : "");
				dto.setAcct_name(item.getAcctname() != null ? item.getAcctname().toString() : "");
				dto.setSchm_code(item.getSchmcode() != null ? item.getSchmcode().toString() : "");
				dto.setReport_label_1(item.getReportlabel1() != null ? item.getReportlabel1().toString() : "");
				dto.setGl_sub_head_code(item.getGlsubheadcode() != null ? item.getGlsubheadcode().toString() : "");
				dto.setReport_addl_criteria_1(
						item.getReportaddlcriteria1() != null ? item.getReportaddlcriteria1().toString() : "");
				dto.setReport_name_1(item.getReportname1() != null ? item.getReportname1().toString() : report_code);
				displayList.add(dto);
			}

			// Set the list based on the mode
			if (formmode.equals("Mapped_Accounts")) {
				md.addAttribute("Mapped_Accounts", displayList);
			} else {
				md.addAttribute("UnMapped_Accounts", displayList);
			}
		} else if (formmode.equals("Mapping")) {
			md.addAttribute("rf_code", report_code);
			md.addAttribute("formmode", "Mapping");
			// md.addAttribute("Mapped_Accounts", reportlist_repo.Mapping1());
			// System.out.println("The name issssssss" + REPORT_NAME_1);
			md.addAttribute("refCodeTypeList", brf_REP.getLiist());
		} else if (formmode.equals("Mappinglist")) {
			md.addAttribute("rf_code", report_code);
			md.addAttribute("formmode1", formmode);
			md.addAttribute("formmode", "Mappinglist");
			md.addAttribute("refCodeTypeList", brf_REP.getLiist());
			System.out.println(foracid);
			// md.addAttribute("refCodeTypeList2", reportlist_repo.getLiist(foracid));
			md.addAttribute("refCodeTypeList1", brf_REP.getLiist1(report_code));
			md.addAttribute("refCodeTypeList", brf_REP.getLiist());
			md.addAttribute("refCodeTypeList2", brf_REP.getLiist2(report_code));

			/*
			 * List<String[]> qw = reportlist_repo.getListDropDown(cust_id, foracid);
			 * md.addAttribute("detailtable2", qw.get(0)); md.addAttribute("detailtable3",
			 * qw.get(1));
			 */

			/* ReportService Dropdown List Switch Case */
			reportServices.MappingList(report_code, md);

			List<Object> List1 = brf_REP.getLiist3(report_code);
			List<BRF_REF_CODE_ENTITY> storedThirdValues = new ArrayList<>();
			for (int i = 0; i < List1.size(); i++) {
				Object obj = List1.get(i);

				if (obj instanceof List) {
					List<?> innerList = (List<?>) obj;
					for (int j = 7; j < innerList.size(); j++) {
						Object value = innerList.get(j);
						String ss = String.valueOf(value);
						String split = ss.split(",").toString();

						// System.out.println("The value is:split " + split);
						// System.out.println("The value is: " + ss);
					}
				} else if (obj instanceof Object[]) {
					Object[] array = (Object[]) obj;
					for (int j = 7; j < array.length; j++) {
						Object value = array[j];
						String ss = String.valueOf(value);
						// System.out.println("The value is:dddd " + ss);
						String[] split = ss.split(",");
						if (split.length >= 3) {
							String thirdValue = split[3].trim(); // Access and trim the third value
							// System.out.println("Third value: " + thirdValue);
							BRF_REF_CODE_ENTITY entity = new BRF_REF_CODE_ENTITY();
							entity.setRow_101(thirdValue);
							entity.setReport_desc(split[2].trim());
							entity.setReport_category(split[1].trim());
							if (thirdValue.equals(reportlabel)) {
								md.addAttribute("reportDes", split[2].trim());
								md.addAttribute("report_category_level", split[1].trim());
							}

							storedThirdValues.add(entity);
						} else {
							System.out.println("The string does not contain at least three values.");
						}
						// System.out.println("The value is:split " + split);
					}
				} else {
					System.out.println("else part");
				}
			}

			md.addAttribute("refCodeTypeList2", storedThirdValues);
			md.addAttribute("selectvalue", "");

			System.out.println(report_code + "vvv");
		} else if (formmode.equals("listMap")) {

			md.addAttribute("formmode", "listMap");
			md.addAttribute("formmode1", formmode);
			md.addAttribute("rf_code", report_code);
			md.addAttribute("row_code", row_code);
			md.addAttribute("refCodeTypeList", brf_REP.getLiist());
			md.addAttribute("reportlabel", reportlabel);
			System.out.println(foracid);
			/*
			 * List<Object[]> bmt_accountList = detailChanges(gl_code, scheme_code,
			 * schm_type, gl_sub_head_code); md.addAttribute("bmt_accountList",
			 * bmt_accountList);
			 */
			List<BRF_MAPPING_TABLE> reportlist = reportlist_repo.getLiist(foracid);
			md.addAttribute("refCodeTypeList2", reportlist);

			md.addAttribute("refCodeTypeList1", brf_REP.getLiist1(report_code));
			System.out.println(reportlist_repo.getLiist(foracid));
			List<Object> List1 = brf_REP.getLiist3(report_code);
			List<BRF_REF_CODE_ENTITY> storedThirdValues = new ArrayList<>();
			for (int i = 0; i < List1.size(); i++) {
				Object obj = List1.get(i);

				if (obj instanceof List) {
					List<?> innerList = (List<?>) obj;
					for (int j = 7; j < innerList.size(); j++) {
						Object value = innerList.get(j);
						String ss = String.valueOf(value);
						String split = ss.split(",").toString();

						// System.out.println("The value is:split " + split);
						// System.out.println("The value is: " + ss);
					}
				} else if (obj instanceof Object[]) {
					Object[] array = (Object[]) obj;
					for (int j = 7; j < array.length; j++) {
						Object value = array[j];
						String ss = String.valueOf(value);
						// System.out.println("The value is:dddd " + ss);
						String[] split = ss.split(",");
						if (split.length >= 3) {
							String thirdValue = split[3].trim(); // Access and trim the third value
							// System.out.println("Third value: " + thirdValue);
							BRF_REF_CODE_ENTITY entity = new BRF_REF_CODE_ENTITY();
							entity.setRow_101(thirdValue);
							entity.setReport_desc(split[2].trim());
							entity.setReport_category(split[1].trim());
							if (thirdValue.equals(reportlabel)) {
								md.addAttribute("reportDes", split[2].trim());
								md.addAttribute("report_category_level", split[1].trim());
							}
							storedThirdValues.add(entity);
						} else {
							System.out.println("The string does not contain at least three values.");
						}
						// System.out.println("The value is:split " + split);
					}
				} else {
					System.out.println("else part");
				}
			}

			md.addAttribute("refCodeTypeList2", storedThirdValues);
			md.addAttribute("selectvalue", "");
			List<BRF_MAPPING_PROPERTY> bmt_accountList = detailChanges1(gl_code, scheme_code, schm_type,
					gl_sub_head_code, foracid, foracid2, cust_id, report_code);

			bmt_accountList = bmt_accountList.stream().filter(e -> Objects.isNull(e.getReportlabel1()))
					.collect(Collectors.toList());

			md.addAttribute("bmt_accountList", bmt_accountList);
		}

		else if (formmode.equals("product_mapping"))

		{

			md.addAttribute("formmode", "product_mapping");
			md.addAttribute("formmode1", formmode);
			md.addAttribute("rf_code", report_code);

			BRF_MAPPING jpa = reportServices.getJpaRepository(report_code);
			List<BRF_PRODUCT_MAPPINGREPO> product123 = jpa.getproduct(reportproductmap);
			md.addAttribute("PMapping_product", product123);
			md.addAttribute("refCodeTypeList1", brf_REP.getLiist1(report_code));
			md.addAttribute("refCodeTypeList", brf_REP.getLiist());

			md.addAttribute("refCodeTypeList2", brf_REP.getLiist2(report_code));

			/* md.addAttribute("refCodeTypeList1", brf_REP.getLiist1(report_code)); */
			List<Object> List1 = brf_REP.getLiist3(report_code);
			List<BRF_REF_CODE_ENTITY> storedThirdValues = new ArrayList<>();
			for (int i = 0; i < List1.size(); i++) {
				Object obj = List1.get(i);

				if (obj instanceof List) {
					List<?> innerList = (List<?>) obj;
					for (int j = 7; j < innerList.size(); j++) {
						Object value = innerList.get(j);
						String ss = String.valueOf(value);
						String split = ss.split(",").toString();

						// System.out.println("The value is:split " + split);
						// System.out.println("The value is: " + ss);
					}
				} else if (obj instanceof Object[]) {
					Object[] array = (Object[]) obj;
					for (int j = 7; j < array.length; j++) {
						Object value = array[j];
						String ss = String.valueOf(value);
						// System.out.println("The value is:dddd " + ss);
						String[] split = ss.split(",");
						if (split.length >= 3) {
							String thirdValue = split[2].trim(); // Access and trim the third value
							// System.out.println("Third value: " + thirdValue);
							BRF_REF_CODE_ENTITY entity = new BRF_REF_CODE_ENTITY();
							entity.setReport_code(split[3].trim());
							entity.setRow_101(thirdValue);
							storedThirdValues.add(entity);
						} else {
							System.out.println("The string does not contain at least three values.");
						}
						// System.out.println("The value is:split " + split);
					}
				} else {
					System.out.println("else part");
				}
			}
			// BRF_MAPPING brf_mapping=
			// reportServices.getJpaRepositoryEdit(reportcode,foracid,brfmap);
			md.addAttribute("refCodeTypeList2", storedThirdValues);

			System.out.println(report_code + "vvv");
		}

		else {
			md.addAttribute("rf_code", report_code);
			md.addAttribute("formmode", formmode);
		}

		md.addAttribute("menu", "ReportMaster");
		md.addAttribute("reportList", reportServices.getReportsMaster());

		return "XBRLReportMapMain";
	}

	private List<String[]> getListDropDown() {

		return null;
	}

	public List<BRF_MAPPING_PROPERTY> detailChanges1(String gl_code, String scheme_code, String schm_type,
			String gl_sub_head_code, String foracid, String foracid_to, String cust_id, String report_code) {

		BRF_MAPPING jpa = reportServices.getJpaRepository(report_code);

		List<BRF_MAPPING_PROPERTY> msg = new ArrayList<BRF_MAPPING_PROPERTY>();

		Session hs = sessionFactory.getCurrentSession();

		/*
		 * if (Objects.nonNull(gl_code) && Objects.nonNull(scheme_code)&&
		 * !Objects.nonNull(schm_type) && Objects.nonNull(gl_sub_head_code)) { msg=
		 * reportlist_repo.getBYGlHeadGlsubHeadAndSchemeCode(gl_code,gl_sub_head_code,
		 * scheme_code); } else if (!Objects.nonNull(gl_code) &&
		 * Objects.nonNull(scheme_code)&& !Objects.nonNull(schm_type) &&
		 * Objects.nonNull(gl_sub_head_code)) { msg=
		 * reportlist_repo.getByGlsubHeadAndSchemeCode(gl_sub_head_code,scheme_code); }
		 */

		if (Objects.nonNull(gl_code) && Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && Objects.nonNull(cust_id)) {
			msg = jpa.getBYGlHeadGlsubHeadAndSchemeCode1(gl_code, gl_sub_head_code, scheme_code, cust_id);
		}

		else if (Objects.nonNull(gl_code) && Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && !Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode2(gl_code, gl_sub_head_code, scheme_code);
		} else if (Objects.nonNull(gl_code) && Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& !Objects.nonNull(gl_sub_head_code) && Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode3(gl_code, scheme_code, cust_id);
		}

		else if (Objects.nonNull(gl_code) && !Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode4(gl_code, gl_sub_head_code, cust_id);
		}

		else if (!Objects.nonNull(gl_code) && Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode5(scheme_code, gl_sub_head_code, cust_id);
		}

		else if (Objects.nonNull(gl_code) && Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& !Objects.nonNull(gl_sub_head_code) && !Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode6(gl_code, scheme_code);
		}

		else if (Objects.nonNull(gl_code) && !Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && !Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode7(gl_code, gl_sub_head_code);
		}

		else if (Objects.nonNull(gl_code) && !Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& !Objects.nonNull(gl_sub_head_code) && Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode8(gl_code, cust_id);
		}

		else if (!Objects.nonNull(gl_code) && Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && !Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode9(gl_sub_head_code, scheme_code);
		} else if (!Objects.nonNull(gl_code) && !Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode10(gl_sub_head_code, cust_id);
		} else if (Objects.nonNull(gl_code) && !Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && !Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode11(gl_sub_head_code, gl_code);
		} else if (!Objects.nonNull(gl_code) && Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& !Objects.nonNull(gl_sub_head_code) && Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode12(scheme_code, cust_id);
		}

		else if (Objects.nonNull(gl_code) && !Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& !Objects.nonNull(gl_sub_head_code) && !Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode13(gl_code);
		} else if (!Objects.nonNull(gl_code) && !Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& Objects.nonNull(gl_sub_head_code) && !Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode14(gl_sub_head_code);
		} else if (!Objects.nonNull(gl_code) && Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& !Objects.nonNull(gl_sub_head_code) && !Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode15(scheme_code);
		} else if (!Objects.nonNull(gl_code) && !Objects.nonNull(scheme_code) && !Objects.nonNull(schm_type)
				&& !Objects.nonNull(gl_sub_head_code) && Objects.nonNull(cust_id)) {
			msg = jpa.getByGlsubHeadAndSchemeCode16(cust_id);
		}

		else if (Objects.nonNull(foracid) && Objects.nonNull(foracid_to)) {
			msg = jpa.getBetweenAccountIds(foracid, foracid_to);
		}

		return msg;
	}

	@RequestMapping(value = "mappingedit", method = RequestMethod.GET)
	@ResponseBody
	public String mappingedit(String foracid, Model md, HttpServletRequest rq,
			@ModelAttribute BRF_MAPPING_TABLE bRF_MAPPING_TABLE,

			@ModelAttribute BRF_MAPPING_TABLE brfmap, String ca_first_name, String reportcode) {

		System.out.println(">>>>>>>>>" + foracid);
		System.out.println("jjjjjjjj");
		BRF_MAPPING brf_mapping = reportServices.getJpaRepositoryEdit(reportcode, foracid, brfmap);
		// Optional<BRF2_MAPPING_ENTITY> brf_mapping1 =
		// brf2_MAPPING_REPO.findById(foracid);
		// Optional<BRF11_MAPPING_ENTITY> brf_mapping11 =
		// brf11_mapping_repo.findById(foracid);
		System.out.println("SalaryParameter");

		return "success";

	}

	@RequestMapping(value = "ReportReferencecode", method = RequestMethod.GET)

	public String ReportReferencecode(Model md, HttpServletRequest req, String report_code) {

		md.addAttribute("menu", "ReferenceCode");

		md.addAttribute("refCodeTypeList", brf_REP.getLiist());
		md.addAttribute("refCodeTypeList1", brf_REP.getLiist1(report_code));

		// md.addAttribute("formmode", "list");
		// md.addAttribute("listone", reportlist_repo.Mapping1());
		md.addAttribute("refCodeTypeList", brf_REP.getLiist());
		md.addAttribute("refCodeTypeList1", brf_REP.getLiist1(report_code));

		md.addAttribute("rf_code", report_code);

		return "XBRLReportRefCode";

	}

	@RequestMapping(value = "/entry_userss")
	@ResponseBody
	public ArrayList<BRF_MAPPING_TABLE> entry_userss(@RequestParam(required = false) String report_name_1,
			@RequestParam(required = false) String Cust_id, HttpServletRequest req) {

		System.out.println(report_name_1);

		List<BRF_MAPPING_TABLE> place = reportlist_repo.getLiist(report_name_1);
		// System.out.println(place);
		ArrayList<BRF_MAPPING_TABLE> Status = new ArrayList<>();
		// System.out.println("hi this is barath");
		for (BRF_MAPPING_TABLE att : place) {
			BRF_MAPPING_TABLE finallist = new BRF_MAPPING_TABLE();
			// System.out.println(att.getReport_code());
			finallist.setForacid(att.getForacid());
			finallist.setCust_id(att.getCust_id());
			finallist.setAcct_name(att.getAcct_name());
			finallist.setGl_sub_head_code(att.getGl_sub_head_code());
			finallist.setReport_name_1(att.getReport_name_1());
			finallist.setForacid(att.getForacid());
			finallist.setSchm_code(att.getSchm_code());
			finallist.setReport_label_1(att.getReport_label_1());
			finallist.setReport_addl_criteria_1(att.getReport_addl_criteria_1());
			Status.add(finallist);
		}
		System.out.println(Status.toString());
		System.out.println(">>>>>>>>>" + Status);

		return Status;
	}

	@RequestMapping(value = "mappingapi", method = RequestMethod.GET)
	@ResponseBody
	public String mappingapi(@RequestParam(required = false) String id, @RequestParam(required = false) String name,
			@RequestParam(required = false) String specificCellValue, String select) {
		String msg = null;

		try {
			System.out.println("The id issss:" + id);
			System.out.println("The id issss:" + name);
			reportServices.getJpaRepositoryMappingLabel(id, select, specificCellValue);
			msg = "Updated Sucessfully !!!";
		} catch (Exception e) {
			msg = "Error Occured !!!";
		}
		return msg;

	}

	@RequestMapping(value = "BRFmapping", method = { RequestMethod.GET, RequestMethod.POST })
	public String BRFmapping(@RequestParam(required = false) String formmode,
			@ModelAttribute BRF_MAPPING_TABLE bRFmappingentity, @RequestParam(required = false) String foracid,
			@RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));

		System.out.println("page" + currentPage);
		// System.out.println("page" + pageSize);
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		// md.addAttribute("AMLRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("list")) {
			/*
			 * md.addAttribute("menuname", "Reports Parameter"); md.addAttribute("formmode",
			 * "list"); // to set which form - valid values are "edit" , "add" & "list"
			 * md.addAttribute("repParameter",
			 * custReportsParmsRepo.findAllCustom(PageRequest.of(currentPage, pageSize)));
			 */
			System.out.println("hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
			md.addAttribute("brfmapping", bRFmappingRepo.getbrfmapping());
			System.out.println(bRFmappingRepo.getbrfmapping());

			md.addAttribute("formmode", "list");
		} else if (formmode.equals("add")) {

//			md.addAttribute("menuname", "Reports Parameter - Add");

			md.addAttribute("formmode", "add");

		} else if (formmode.equals("addsubmit")) {

			System.out.println(foracid);
			md.addAttribute("details", bRFmappingRepo.findByforacid(foracid));

			System.out.println("hi--------------------------------------------" + bRFmappingentity.getForacid());

			md.addAttribute("formmode", "addsubmit");

		} else if (formmode.equals("edit")) {

		} else if (formmode.equals("verify")) {

		} else if (formmode.equals("view")) {

		}

		return "BRFmapping";
	}

	@RequestMapping(value = "eclsts", method = { RequestMethod.GET, RequestMethod.POST })

	public String EclMaster(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String cust_id, @RequestParam(required = false) String role_id, Model md,
			HttpServletRequest req) throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException {

		String userId = (String) req.getSession().getAttribute("USERID");
		// md.addAttribute("RoleMenu", resourceMasterRepo.getrole(userId));
		md.addAttribute("menu", "UserProfile");

		if (formmode == null || formmode.equals("list")) {

			md.addAttribute("formmode", "list");
			md.addAttribute("eclsts", ecl_status_repo.getEclstatus());

			md.addAttribute("c2013", bobRAMRep.getcount2013());
			md.addAttribute("c2014", bobRAMRep.getbob10count2014());
			md.addAttribute("cNotn2014", bobRAMRep.getcount2014());
			md.addAttribute("c2015", bobRAMRep.getbob10count2015());
			md.addAttribute("cNotn2015", bobRAMRep.getcount2015());
			md.addAttribute("c2016", bobRAMRep.getbob10count2016());
			md.addAttribute("cNotn2016", bobRAMRep.getNOTcount2016());
			md.addAttribute("c2017", bobRAMRep.getbob10count2017());
			md.addAttribute("cNotn2017", bobRAMRep.getNOTcount2017());
			md.addAttribute("c2018", bobRAMRep.getbob10count2018());
			md.addAttribute("cNotn2018", bobRAMRep.getNOTcount2017());
			md.addAttribute("c2019", bobRAMRep.getbob10count2018());
			md.addAttribute("cNotn2019", bobRAMRep.getNOTcount2019());
			md.addAttribute("c2020", bobRAMRep.getbob10count2020());
			md.addAttribute("cNotn2020", bobRAMRep.getNOTcount2020());
			md.addAttribute("c2021", bobRAMRep.getbob10count2021());
			md.addAttribute("cNotn2021", bobRAMRep.getNOTcount2021());
			md.addAttribute("c2022", bobRAMRep.getbob10count2022());

		} else if (formmode.equals("add")) {

			md.addAttribute("formmode", formmode);
			// md.addAttribute("BTMAccessRole",new AccessRoles());

		} else if (formmode.equals("edit")) {

			md.addAttribute("formmode", "edit");

			md.addAttribute("eclsts", ecl_status_repo.getEclstatus1(cust_id));
			System.out.println("thhhh" + ecl_status_repo.getEclstatus1(cust_id));

		} else if (formmode.equals("verify"))

		{
			md.addAttribute("formmode", "verify");
			md.addAttribute("eclsts", ecl_status_repo.getEclstatus());

		} else if (formmode.equals("verify1"))

		{
			md.addAttribute("formmode", "verify1");
			md.addAttribute("eclsts", ecl_status_repo.getEclstatus1(cust_id));

		}
		return "RR/ECLstatusList";
	}

	/******* ECLADDRECORD ***/

	@RequestMapping(value = "Ecladdrecord", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev(Model md, HttpServletRequest rq, @ModelAttribute ECL_STATUS_ENTITY eCL_STATUS_ENTITY,
			String cust_id) {

		ECL_STATUS_ENTITY up = eCL_STATUS_ENTITY;

		ecl_status_repo.save(up);
//System.out.println("SalaryParameter");

		return "success";
	}

	/***** Ecledit ****/
	/*
	 * @RequestMapping(value = "EclEditrecord", method = RequestMethod.POST)
	 * 
	 * @ResponseBody public String modify_ECL(@RequestParam(required = false) String
	 * cust_id,
	 * 
	 * @ModelAttribute ECL_STATUS_ENTITY ecl_status, HttpServletRequest hs) {
	 * 
	 * Session stsssave=sessionFactory.getCurrentSession();
	 * 
	 * ecl_status.setCust_id(cust_id);
	 * 
	 * stsssave.saveOrUpdate(ecl_status);
	 * 
	 * 
	 * 
	 * 
	 * logger.info("Edited Record"); msg = "Edited Successfully"; return "success";
	 * }
	 */

	@RequestMapping(value = "/EclEditrecord", method = RequestMethod.POST)
	@ResponseBody
	public String modify_ECL(@RequestParam(required = false) String cust_id,
			@ModelAttribute ECL_STATUS_ENTITY ecl_status, HttpServletRequest hs) {

		ECL_STATUS_ENTITY sansave = ecl_status;

		logger.info("Edit processing****");

		ecl_status_repo.save(sansave);

		logger.info("Edited****");

		/*
		 * ecl_status.setCust_id(ecl_status.getCust_id());
		 * 
		 * System.out.println(" the customer id****" +cust_id );
		 * 
		 * stsssave.saveOrUpdate(ecl_status);
		 * 
		 * logger.info("Edited Record");
		 * 
		 * 
		 * return "success";
		 * 
		 * } catch (Exception e) {
		 * 
		 * logger.error("Error editing record", e);
		 * 
		 * return "error"; }
		 */
		return "success";
	}

	/***** Statusverify ****/

	@RequestMapping(value = "ECLstsverifysanjeev", method = RequestMethod.POST)
	@ResponseBody
	public String ECLstsverifysanjeev(Model md, HttpServletRequest rq, @ModelAttribute ECL_STATUS_ENTITY ecl_status,
			String cust_id) {

		ECL_STATUS_ENTITY up = ecl_status;

		up.setEntity_flg("Y");

		logger.info("flag changed");

		ecl_status_repo.save(up);

		return "success";
	}

	private ECL_STATUS_ENTITY ecl_status() {

		return null;
	}

	@RequestMapping(value = "Eclstatuslist", method = RequestMethod.GET)

	@ResponseBody
	public InputStreamResource Eclstatuslist(HttpServletResponse response,

			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {

		response.setContentType("application/octet-stream");

		InputStreamResource resource = null;
		try {

			logger.info("Getting download File :" + ", FileType :" + filetype + "");

			File repfile = reportServices.getFile1(filetype);

			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));

		} catch (JRException e) {

			e.printStackTrace();
		}

		return resource;
	}

	@RequestMapping(value = "eclconsolidate", method = RequestMethod.GET)

	@ResponseBody
	public InputStreamResource eclconsolidate(HttpServletResponse response,

			@RequestParam(value = "filetype", required = false) String filetype) throws IOException, SQLException {

		response.setContentType("application/octet-stream");

		InputStreamResource resource = null;
		try {

			logger.info("Getting download File :" + ", FileType :" + filetype + "");

			File repfile = reportServices.getconsolidateFileECL(filetype);

			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			resource = new InputStreamResource(new FileInputStream(repfile));

		} catch (JRException e) {

			e.printStackTrace();
		}

		return resource;
	}

	/********* ECL master data list stater *****/

	@RequestMapping(value = "EclMaster", method = { RequestMethod.GET, RequestMethod.POST })

	public String EclMaster(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String currency, @RequestParam(required = false) String cust_id,
			@RequestParam(required = false) String role_id, Model md, HttpServletRequest req)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException {

		String userId = (String) req.getSession().getAttribute("USERID");
		// md.addAttribute("RoleMenu", resourceMasterRepo.getrole(userId));
		md.addAttribute("menu", "UserProfile");

		if (formmode == null || formmode.equals("list")) {
			if (currency == null || currency.equals("INR")) {
				md.addAttribute("formmode", "list");
				md.addAttribute("ecl_inr", eCL_MasterData_INR_Rep.getList());
			} else if (currency.equals("AED")) {
				md.addAttribute("formmode", "list");
				md.addAttribute("ecl_inr", eCL_MDT_AED_Rep.getList());
			}

		} else if (formmode.equals("verifylist")) {
			if (currency == null || currency.equals("INR")) {
				md.addAttribute("formmode", "verifylist");
				md.addAttribute("ecl_inr", eCL_MasterData_INR_Rep.getList());
			} else if (currency.equals("AED")) {
				md.addAttribute("formmode", "verifylist");
				md.addAttribute("ecl_inr", eCL_MDT_AED_Rep.getList());
			}

		} else if (formmode.equals("add")) {

			md.addAttribute("formmode", formmode);
			// md.addAttribute("BTMAccessRole",new AccessRoles());

		} else if (formmode.equals("edit")) {

			md.addAttribute("formmode", formmode);
			md.addAttribute("ecl_inr", eCL_MasterData_INR_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr2", eCL_MDT_AED_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr3", eCL_MDT_WO_ADJ_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr4", eCL_MDT_RECOVERY_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr6", eCL_MDT_LRW_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr7", eCL_MDT_HIS_RATE_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr8", eCL_MDT_AED_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr9", eCL_MDT_DCR_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr10", eCL_MDT_LGD_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr11", eCL_MDT_DIS_REC_Rep.getSingleId(cust_id));
			// md.addAttribute("BTMAccessRole",new AccessRoles());

		} else if (formmode.equals("verify")) {

			md.addAttribute("formmode", formmode);
			md.addAttribute("ecl_inr", eCL_MasterData_INR_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr2", eCL_MDT_AED_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr3", eCL_MDT_WO_ADJ_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr4", eCL_MDT_RECOVERY_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr6", eCL_MDT_LRW_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr7", eCL_MDT_HIS_RATE_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr8", eCL_MDT_AED_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr9", eCL_MDT_DCR_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr10", eCL_MDT_LGD_Rep.getSingleId(cust_id));
			md.addAttribute("ecl_inr11", eCL_MDT_DIS_REC_Rep.getSingleId(cust_id));
			// md.addAttribute("BTMAccessRole",new AccessRoles());

		} else {

			md.addAttribute("formmode", formmode);

		}

		return "EclMaster";
	}

	@RequestMapping(value = "/po_filter")
	@ResponseBody
	public ArrayList<ECL_MasterData_INR_Entity> po_filter(@RequestParam(required = false) String currency,
			HttpServletRequest req) throws ParseException {
		ArrayList<ECL_MasterData_INR_Entity> PO_Status = new ArrayList<>();
		System.out.println(currency);
		if (currency.equals("INR")) {
			List<ECL_MasterData_INR_Entity> place = eCL_MasterData_INR_Rep.getList();
			for (ECL_MasterData_INR_Entity att : place) {
				ECL_MasterData_INR_Entity finallist = new ECL_MasterData_INR_Entity();

				finallist.setCust_id(att.getCust_id());
				finallist.setDisbursement_date(att.getDisbursement_date());
				finallist.setNpa_date(att.getNpa_date());
				finallist.setLimit_sanctioned(att.getLimit_sanctioned());
				finallist.setInt_rate(att.getInt_rate());
				finallist.setColl_cash_security(att.getColl_cash_security());
				finallist.setColl_non_crm_security(att.getColl_non_crm_security());
				finallist.setPri_cash_security(att.getPri_cash_security());
				finallist.setPri_non_crm_security(att.getPri_non_crm_security());
				finallist.setWrite_off_amount(
						att.getWrite_off_amount() != null ? att.getWrite_off_amount() : BigDecimal.ZERO);
				finallist.setWrite_off_date(att.getWrite_off_date() != null ? att.getWrite_off_date() : new Date(0));

				PO_Status.add(finallist);
			}
		} else if (currency.equals("AED")) {
			SimpleDateFormat inputDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
			SimpleDateFormat outputDateFormat = new SimpleDateFormat("dd-MM-yyyy");
			Date date1 = null;
			List<ECL_MDT_AED_Entity> place1 = eCL_MDT_AED_Rep.getList();
			for (ECL_MDT_AED_Entity att : place1) {
				ECL_MasterData_INR_Entity finallist = new ECL_MasterData_INR_Entity();

				double number = 1234567.89;
				NumberFormat indianLakhFormat = NumberFormat.getNumberInstance(new Locale("en", "IN"));
				String formattedNumber = indianLakhFormat.format(number);

				try {
					double parsedNumber = indianLakhFormat.parse(formattedNumber).doubleValue();
					System.out.println("Parsed Number (Double): " + parsedNumber);
				} catch (ParseException e) {
					e.printStackTrace();
				}
				// Print the formatted number
				System.out.println("Formatted Number: " + formattedNumber);

				OffsetDateTime offsetDateTime = OffsetDateTime.parse(att.getDisbursement_date().toString(),
						DateTimeFormatter.ISO_OFFSET_DATE_TIME);
				String formattedDate = offsetDateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				System.out.println("Formatted Date: " + formattedDate);
				Date date = outputDateFormat.parse(formattedDate);
				finallist.setCust_id(att.getCust_id());
				finallist.setDisbursement_date(date);
				finallist.setNpa_date(att.getNpa_date());
				finallist.setLimit_sanctioned(att.getLimit_sanctioned());
				finallist.setInt_rate(att.getInt_rate());
				finallist.setColl_cash_security(att.getColl_cash_security());
				finallist.setColl_non_crm_security(att.getColl_non_crm_security());
				finallist.setPri_cash_security(att.getPri_cash_security());
				finallist.setPri_non_crm_security(att.getPri_non_crm_security());
				finallist.setWrite_off_amount(
						att.getWrite_off_amount() != null ? att.getWrite_off_amount() : BigDecimal.ZERO);
				finallist.setWrite_off_date(att.getWrite_off_date() != null ? att.getWrite_off_date() : new Date(0));

				PO_Status.add(finallist);
			}
		}
		System.out.println(PO_Status.toString());
		System.out.println(">>>>>>>>>" + PO_Status);
		return PO_Status;
	}

	@RequestMapping(value = "INRReportDownload", method = RequestMethod.GET)

	@ResponseBody
	public InputStreamResource INRReportDownload(HttpServletResponse response,
			@RequestParam(value = "filetype", required = false) String filetype)
			throws IOException, SQLException, JRException {

		response.setContentType("application/octet-stream");

		InputStreamResource resource = null;
		try {
			File repfile = reportServices.getECLFile(filetype);

			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); // Set the
																											// content
																											// type to
																											// Excel

			try (InputStream inputStream = new FileInputStream(repfile);
					OutputStream outputStream = response.getOutputStream()) {

				byte[] buffer = new byte[1024];
				int bytesRead;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.flush();
			}
		} catch (FileNotFoundException e) {
			// Handle file not found exception
			e.printStackTrace(); // Consider logging or handling the exception appropriately
		} catch (IOException e) {
			// Handle IO exception
			e.printStackTrace(); // Consider logging or handling the exception appropriately
		} catch (Exception e) {
			// Handle other exceptions
			e.printStackTrace(); // Consider logging or handling the exception appropriately
		}

		return resource;
	}

	@RequestMapping(value = "customervarson", method = RequestMethod.POST)
	@ResponseBody
	public String barathvarson(Model md, HttpServletRequest rq, @ModelAttribute RBRcustomer_entity rBRcustomer_entity,
			String cif_no) {
		System.out.println(rBRcustomer_entity.getCif_no());
		System.out.println("The solid Id >>>>>>>>>>>>>>>>>>>>>>>>> " + rBRcustomer_entity.getCename());
		System.out.println("The solid Id >>>>>>>>>>>>>>>>>>>>>>>>> " + rBRcustomer_entity.getGender());
		RBRcustomer_entity up = rBRcustomer_entity;

		rBRcustomerRepo.save(up);
		return "success";
	}

	@RequestMapping(value = "RBRReportDownload", method = RequestMethod.GET)

	@ResponseBody
	public InputStreamResource RBRReportDownload(HttpServletResponse response,
			@RequestParam(value = "filetype", required = false) String filetype,
			@RequestParam(value = "tabName", required = false) String tabName, HttpServletRequest req,
			@RequestParam(value = "operationData", required = false) String operationData)
			throws IOException, SQLException, JRException {

		response.setContentType("application/octet-stream");
		System.out.println(operationData);

		InputStreamResource resource = null;
		try {
			File repfile = reportServices.getRBRFile(filetype, tabName, operationData, req);

			response.setHeader("Content-Disposition", "attachment; filename=" + repfile.getName());
			response.setContentType(
					"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=windows-1256");
			response.setCharacterEncoding("windows-1256");

			try (InputStream inputStream = new FileInputStream(repfile);
					OutputStream outputStream = response.getOutputStream()) {

				byte[] buffer = new byte[1024];
				int bytesRead;

				while ((bytesRead = inputStream.read(buffer)) != -1) {
					outputStream.write(buffer, 0, bytesRead);
				}

				outputStream.flush();
			}
		} catch (FileNotFoundException e) {
			// Handle file not found exception
			e.printStackTrace(); // Consider logging or handling the exception appropriately
		} catch (IOException e) {
			// Handle IO exception
			e.printStackTrace(); // Consider logging or handling the exception appropriately
		} catch (Exception e) {
			// Handle other exceptions
			e.printStackTrace(); // Consider logging or handling the exception appropriately
		}

		return resource;
	}

	// CREATED BY GOWTHAM
	@RequestMapping(value = "RBRMasterReportDownload", method = RequestMethod.GET)
	@ResponseBody
	public void RBRMasterReportDownload(HttpServletResponse response,
			@RequestParam(value = "filetype", required = false, defaultValue = "xlsx") String filetype,
			@RequestParam(value = "formmode", required = true) String formmode, HttpServletRequest req)
			throws IOException, JRException, SQLException {

		System.out.println("Generating Excel report for formmode: " + formmode);

		// Generate the Excel file
		File reportFile = reportServices.getMasterRBRFile(formmode, req);

		if (reportFile == null || !reportFile.exists()) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
			return;
		}

		// Set response headers for file download
		response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + reportFile.getName() + "\"");
		response.setCharacterEncoding("UTF-8");

		// Write file data to response output stream
		try (InputStream inputStream = new FileInputStream(reportFile);
				OutputStream outputStream = response.getOutputStream()) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			outputStream.flush(); // Ensure all data is written
		} catch (IOException e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing file");
		}
	}

	@RequestMapping(value = "RBR_Master", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBRcustomer_data(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String cif_no, @RequestParam(required = false) String tab, Model md,
			HttpServletRequest req, String cin, @ModelAttribute RBRShareHolder_Entity details1,
			@ModelAttribute RBRcustomer_entity details2, @ModelAttribute Facitlity_Entity details3,
			@ModelAttribute Security_Entity details4, @ModelAttribute Provision_Entity details5,
			@ModelAttribute RBROverall_Data_Entity details6, @ModelAttribute RBR_Legal_Cases_Entity details7,
			@ModelAttribute RBR_Inverstments_Entity details8) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		String WORK_CLASS = (String) req.getSession().getAttribute("WORKCLASS");
		String USER_PERMISSIONS = (String) req.getSession().getAttribute("PERMISSIONS");
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("userid", "userid");

			if (Roleid.equals("RBR")) {
				md.addAttribute("listcustomer", RBRReportservice.getcustdata());
			} else {
				md.addAttribute("listcustomer", RBRReportservice.getBranchcustdata(BRANCHCODE));
			}

			md.addAttribute("USER_ID", userid);

		} else if (formmode.equals("getbycin")) {
			md.addAttribute("tab", tab);
			md.addAttribute("formmode", "add");
			md.addAttribute("listcustomer", rBRcustomerRepo.getbycif_no(cif_no));
			md.addAttribute("listShare", rbrShareHolder_Repo.getbyview(cin));
			md.addAttribute("listFacility", facility_Repo.getbyview(cin));
			md.addAttribute("listSecurity", security_Repo.getbyview(cin));
			md.addAttribute("listProvision", Provision_Repo.getbyview(cin));
			md.addAttribute("listoverall", RBRoverall_Data_Repo.getbyview(cin));
			md.addAttribute("listlegalcases", RBR_Legal_Cases_Repo.getbyview(cin));
			md.addAttribute("listInverstmentscases", RBR_Inverstments_Repo.getbyview(cin));

		} else if (formmode.equals("verify")) {
			md.addAttribute("formmode", "verify");
			md.addAttribute("listcustomer", rBRcustomerRepo.getcin(cin));
			md.addAttribute("listShare", rbrShareHolder_Repo.getview(cin));
			md.addAttribute("listFacility", facility_Repo.getview(cin));
			md.addAttribute("listSecurity", security_Repo.getview(cin));
			md.addAttribute("listProvision", Provision_Repo.getview(cin));
			md.addAttribute("listoverall", RBRoverall_Data_Repo.getview(cin));
			md.addAttribute("listlegalcases", RBR_Legal_Cases_Repo.getview(cin));
			md.addAttribute("listInverstmentscases", RBR_Inverstments_Repo.getview(cin));

		} else if (formmode.equals("updatecin")) {
			md.addAttribute("formmode", "updatecin");
			md.addAttribute("listcustomer", rBRcustomerRepo.getbycif_no(cif_no));

		} else {
			System.out.println("EMPTY");
		}

		return "RBRMaster";

	}

	@RequestMapping(value = "Customerdata", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBRcustomer_data(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String Srl_no, @RequestParam(required = false) String cif_no,
			@RequestParam(required = false) String tab, Model md, HttpServletRequest req, String cin,
			@ModelAttribute RBRcustomer_entity details2) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		String WORK_CLASS = (String) req.getSession().getAttribute("WORKCLASS");
		String USER_PERMISSIONS = (String) req.getSession().getAttribute("PERMISSIONS");
		if (formmode == null || formmode.equals("Customerdata")) {
			md.addAttribute("formmode", "Customerdata");
			md.addAttribute("userid", "userid");
			md.addAttribute("RBRMenuname", "Customer Data");

			if (Roleid.equals("RBR")) {
				md.addAttribute("listcustomerveri", rBRcustomerRepo.getcustomerdata());
				md.addAttribute("listcustomerunveri", rBRcustomerRepo.getcustomerdataunveri());
			} else {
				md.addAttribute("listcustomerveri", rBRcustomerRepo.getcustomerbranchdata(BRANCHCODE));
				md.addAttribute("listcustomerunveri", rBRcustomerRepo.getcustomerbranchdataunveri(BRANCHCODE));
			}
		} else if (formmode.equals("Customeredit")) {
			md.addAttribute("formmode", "Customeredit");
			md.addAttribute("Custedit", rBRcustomerRepo.getcustomeredit(Srl_no));
			md.addAttribute("RBRMenuname", "Customer Edit");
		} else if (formmode.equals("Customeradd")) {
			md.addAttribute("formmode", "Customeradd");
			Long Cust_Srl_no = rBRcustomerRepo.GetCustsrl_no();
			md.addAttribute("Cust_Srl_no", Cust_Srl_no.toString());
			md.addAttribute("RBRMenuname", "Customer Add");
		} else {

		}

		return "RBRMasterdata";

	}

	@RequestMapping(value = "Custdataoperation", method = RequestMethod.POST)
	@ResponseBody
	public String createcustdata(@RequestParam("formmode") String formmode,
			@ModelAttribute RBRcustomer_entity RBRcustomer_entity, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {
		System.out.println();
		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) rq.getSession().getAttribute("BRANCHCODE");

		String msg = RBRReportservice.Custdataoperation(RBRcustomer_entity, formmode, userid, BRANCHCODE);

		return msg;

	}

	@RequestMapping(value = "Partnerdata", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBRPartnerdata(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String Srl_no, @RequestParam(required = false) String cif_no,
			@RequestParam(required = false) String tab, Model md, HttpServletRequest req, String cin,
			@ModelAttribute RBRShareHolder_Entity details2) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		String WORK_CLASS = (String) req.getSession().getAttribute("WORKCLASS");
		String USER_PERMISSIONS = (String) req.getSession().getAttribute("PERMISSIONS");
		if (formmode == null || formmode.equals("Partnerdata")) {
			md.addAttribute("formmode", "Partnerdata");
			md.addAttribute("userid", "userid");
			md.addAttribute("RBRMenuname", "Partner Data");

			if (Roleid.equals("RBR")) {
				md.addAttribute("listpartnerveri", rbrShareHolder_Repo.getverifiedpartner());
				md.addAttribute("listpartnerunveri", rbrShareHolder_Repo.getunverifiedpartner());
			} else {
				md.addAttribute("listpartnerveri", rbrShareHolder_Repo.getverifiedbranchpartner(BRANCHCODE));
				md.addAttribute("listpartnerunveri", rbrShareHolder_Repo.getunverifiedpartner());
			}

		} else if (formmode.equals("Partnerdataedit")) {
			md.addAttribute("formmode", "Partnerdataedit");
			RBRcustomer_entity RBRcustomer_entity = rBRcustomerRepo.getcustomeredit(Srl_no);
			md.addAttribute("RBRMenuname", "Partner Edit");
			/*
			 * String SUBBORR = RBRcustomer_entity.getSub_bor_type();
			 * md.addAttribute("SUBBORR", SUBBORR);
			 */
			md.addAttribute("Partneredit", rbrShareHolder_Repo.getpartnersrlno(Srl_no));
		} else if (formmode.equals("Partneradd")) {

			md.addAttribute("formmode", "Partneradd");
			Long Partner_Srl_no = rbrShareHolder_Repo.getAuditRefUUID();
			md.addAttribute("Partner_Srl_no", Partner_Srl_no.toString());
			md.addAttribute("RBRMenuname", "Partner Add");
		} else {

		}

		return "RBRMasterdata";

	}

	@RequestMapping(value = "Partnerdataoperation", method = RequestMethod.POST)
	@ResponseBody
	public String Partnerdataoperation(@RequestParam("formmode") String formmode,
			@ModelAttribute RBRShareHolder_Entity RBRShareHolder_Entity, Model md, HttpServletRequest rq)
			throws Exception {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) rq.getSession().getAttribute("BRANCHCODE");

		String msg = RBRReportservice.Partnerdataoperation(RBRShareHolder_Entity, formmode, userid, BRANCHCODE);

		return msg;

	}

	@RequestMapping(value = "Securitydata", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBRSecuritydata(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String Srl_no, @RequestParam(required = false) String cif_no,
			@RequestParam(required = false) String tab, Model md, HttpServletRequest req, String cin,
			@ModelAttribute Security_Entity details2) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		String WORK_CLASS = (String) req.getSession().getAttribute("WORKCLASS");
		String USER_PERMISSIONS = (String) req.getSession().getAttribute("PERMISSIONS");
		if (formmode == null || formmode.equals("Securitydata")) {
			md.addAttribute("formmode", "Securitydata");
			md.addAttribute("userid", "userid");
			md.addAttribute("RBRMenuname", "Security Data");

			if (Roleid.equals("RBR")) {
				md.addAttribute("listsecuveri", security_Repo.getsecurityveri());
				md.addAttribute("listsecuunveri", security_Repo.getsecurityunveri());
			} else {
				md.addAttribute("listsecuveri", security_Repo.getsecuritybranch_codeveri(BRANCHCODE));
				md.addAttribute("listsecuunveri", security_Repo.getsecuritybranch_codeunveri(BRANCHCODE));
			}

		} else if (formmode.equals("Securitydataedit")) {

			md.addAttribute("formmode", "Securitydataedit");
			md.addAttribute("Securityedit", security_Repo.Getsecuritysrlno(Srl_no));
			md.addAttribute("RBRMenuname", "Security Edit");
		} else if (formmode.equals("Securityadd")) {
			md.addAttribute("formmode", "Securityadd");
			Long Security_Srl_no = security_Repo.getAuditRefUUID();
			md.addAttribute("Security_Srl_no", Security_Srl_no.toString());
			md.addAttribute("RBRMenuname", "Security Add");
		} else {

		}

		return "RBRMasterdata";

	}

	@RequestMapping(value = "Securitydataoperation", method = RequestMethod.POST)
	@ResponseBody
	public String Securitydataoperation(@RequestParam("formmode") String formmode,
			@ModelAttribute Security_Entity Security_Entity, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) rq.getSession().getAttribute("BRANCHCODE");

		String msg = RBRReportservice.Securitydataopr(Security_Entity, formmode, userid, BRANCHCODE, null);

		return msg;

	}

	@PostMapping("/Securitydataoperation/upload")
	@ResponseBody
	public ResponseEntity<String> Securitydataoperation(@RequestParam("formmode") String formmode,
			@RequestParam("file") MultipartFile file, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		try {

			if (file.isEmpty()) {
				return ResponseEntity.badRequest().body("File is empty.");
			}

			String msg = RBRReportservice.Securitydataupload(file, userid);
			return ResponseEntity.ok("success");
		} catch (Exception ex) {
			ex.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Upload failed: " + ex.getMessage());
		}
	}

	@PostMapping("/Securitydataoperation/verifyall")
	@ResponseBody
	public ResponseEntity<?> verifySecurity(@RequestParam("formmode") String formmode, @RequestBody List<Long> ids,
			HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) rq.getSession().getAttribute("BRANCHCODE");

		String msg = RBRReportservice.Securitydataopr(null, formmode, userid, BRANCHCODE, ids);

		return ResponseEntity.ok(msg);
	}

	@RequestMapping(value = "Facilitydata", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBRFacilitydata(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String Srl_no, @RequestParam(required = false) String cif_no,
			@RequestParam(required = false) String tab, Model md, HttpServletRequest req, String cin,
			@ModelAttribute Facitlity_Entity details2) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		String WORK_CLASS = (String) req.getSession().getAttribute("WORKCLASS");
		String USER_PERMISSIONS = (String) req.getSession().getAttribute("PERMISSIONS");
		if (formmode == null || formmode.equals("Facilitydata")) {
			md.addAttribute("formmode", "Facilitydata");
			md.addAttribute("userid", "userid");
			md.addAttribute("RBRMenuname", "Facility Data");

			if (Roleid.equals("RBR")) {
				md.addAttribute("listFaciveri", facility_Repo.getfacveri());
				md.addAttribute("listFaciunveri", facility_Repo.getfacunveri());
			} else {
				md.addAttribute("listFaciveri", facility_Repo.getfacbranch_codeveri(BRANCHCODE));
				md.addAttribute("listFaciunveri", facility_Repo.getfacbranch_codeunveri(BRANCHCODE));
			}
		} else if (formmode.equals("Facilitydataedit")) {

			md.addAttribute("formmode", "Facilitydataedit");
			md.addAttribute("Facdataedit", facility_Repo.getfacsrlno(Srl_no));
			md.addAttribute("RBRMenuname", "Facility Edit");
		} else if (formmode.equals("Facilityadd")) {
			md.addAttribute("formmode", "Facilityadd");
			Long FAC_Srl_no = facility_Repo.getAuditRefUUID();
			md.addAttribute("FAC_Srl_no", FAC_Srl_no.toString());
			md.addAttribute("RBRMenuname", "Facility Add");
		} else {

		}

		return "RBRSecusheets";

	}

	@RequestMapping(value = "Facilitydataoperation", method = RequestMethod.POST)
	@ResponseBody
	public String Facilitydataoperation(@RequestParam("formmode") String formmode,
			@ModelAttribute Facitlity_Entity Facitlity_Entity, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) rq.getSession().getAttribute("BRANCHCODE");

		String msg = RBRReportservice.Facilitydataopr(Facitlity_Entity, formmode, userid, BRANCHCODE);

		return msg;

	}

	@RequestMapping(value = "Provisiondata", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBRProvisiondata(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String Srl_no, @RequestParam(required = false) String cif_no,
			@RequestParam(required = false) String tab, Model md, HttpServletRequest req, String cin,
			@ModelAttribute Provision_Entity details2) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		String WORK_CLASS = (String) req.getSession().getAttribute("WORKCLASS");
		String USER_PERMISSIONS = (String) req.getSession().getAttribute("PERMISSIONS");
		if (formmode == null || formmode.equals("Provisiondata")) {
			md.addAttribute("formmode", "Provisiondata");
			md.addAttribute("userid", "userid");
			md.addAttribute("RBRMenuname", "Provision Data");

			if (Roleid.equals("RBR")) {
				md.addAttribute("listprovveri", Provision_Repo.getproveri());
				md.addAttribute("listprovunveri", Provision_Repo.getprovunveri());
			} else {
				md.addAttribute("listprovveri", Provision_Repo.getprobranch_codeveri(BRANCHCODE));
				md.addAttribute("listprovunveri", Provision_Repo.getprovbranch_codeunveri(BRANCHCODE));
			}
		} else if (formmode.equals("Provisiondataedit")) {

			md.addAttribute("formmode", "Provisiondataedit");
			md.addAttribute("Provdataedit", Provision_Repo.getprovsrl(Srl_no));
			md.addAttribute("RBRMenuname", "Provision Edit");
		} else if (formmode.equals("Provisionadd")) {
			md.addAttribute("formmode", "Provisionadd");
			Long Pro_Srl_no = Provision_Repo.getAuditRefUUID();
			md.addAttribute("Pro_Srl_no", Pro_Srl_no.toString());
			md.addAttribute("RBRMenuname", "Provision Add");
		} else {

		}

		return "RBRSecusheets";

	}

	@RequestMapping(value = "Provisiondataoperation", method = RequestMethod.POST)
	@ResponseBody
	public String Provisiondataoperation(@RequestParam("formmode") String formmode,
			@ModelAttribute Provision_Entity Provision_Entity, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) rq.getSession().getAttribute("BRANCHCODE");

		String msg = RBRReportservice.Provisiondataopr(Provision_Entity, formmode, userid, BRANCHCODE);

		return msg;

	}

	@RequestMapping(value = "Overalldata", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBROveralldata(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String Srl_no, @RequestParam(required = false) String cif_no,
			@RequestParam(required = false) String tab, Model md, HttpServletRequest req, String cin,
			@ModelAttribute RBROverall_Data_Entity details2) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		String WORK_CLASS = (String) req.getSession().getAttribute("WORKCLASS");
		String USER_PERMISSIONS = (String) req.getSession().getAttribute("PERMISSIONS");
		if (formmode == null || formmode.equals("Overalldata")) {
			md.addAttribute("formmode", "Overalldata");
			md.addAttribute("userid", "userid");
			md.addAttribute("RBRMenuname", "Overall Data");

			if (Roleid.equals("RBR")) {
				md.addAttribute("listprovveri", RBRoverall_Data_Repo.getoverallverifi());
				md.addAttribute("listprovunveri", RBRoverall_Data_Repo.getoverallunverifi());
			} else {
				md.addAttribute("listprovveri", RBRoverall_Data_Repo.getoverallbrachverifi(BRANCHCODE));
				md.addAttribute("listprovunveri", RBRoverall_Data_Repo.getoverallbranchunverifi(BRANCHCODE));
			}
		} else if (formmode.equals("Overalldataedit")) {

			md.addAttribute("formmode", "Overalldataedit");
			md.addAttribute("Overalldataedit", RBRoverall_Data_Repo.getsrl_no(Srl_no));
			md.addAttribute("RBRMenuname", "Overall Edit");
		} else if (formmode.equals("Overalladd")) {
			md.addAttribute("formmode", "Overalladd");
			Long Over_Srl_no = RBRoverall_Data_Repo.getAuditRefUUID();
			md.addAttribute("Over_Srl_no", Over_Srl_no.toString());
			md.addAttribute("RBRMenuname", "Overall Add");
		} else {

		}

		return "RBRSecusheets";

	}

	@RequestMapping(value = "Overalldataoperation", method = RequestMethod.POST)
	@ResponseBody
	public String Overalldataoperation(@RequestParam("formmode") String formmode,
			@ModelAttribute RBROverall_Data_Entity RBROverall_Data_Entity, Model md, HttpServletRequest rq)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException, IOException {

		String userid = (String) rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) rq.getSession().getAttribute("BRANCHCODE");

		String msg = RBRReportservice.Overalldataoper(RBROverall_Data_Entity, formmode, userid, BRANCHCODE);

		return msg;

	}

	@RequestMapping(value = "RBR_1", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBR_1(@RequestParam(required = false) String formmode, @RequestParam(required = false) String cif_no,
			@RequestParam(required = false) String tab, Model md, HttpServletRequest req) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("userid", "userid");
			// md.addAttribute("listcustomer", rBRcustomerRepo.getList());

			if (Roleid.equals("RBR")) {
				md.addAttribute("listcustomer", RBR_CUSTOMER_DATA_V1_REP.findAll());
				md.addAttribute("listcustomerRBR1", RBR_CUSTOMER_DATA_V1_REP.Getverified());

			} else {
				md.addAttribute("listcustomer", RBR_CUSTOMER_DATA_V1_REP.getCUSTList(BRANCHCODE));
				md.addAttribute("listcustomerRBR1", RBR_CUSTOMER_DATA_V1_REP.Getverifiedbranch(BRANCHCODE));
			}

			md.addAttribute("USER_ID", userid);

		}

		return "RR/RBRVersion1";

	}

	public Map<String, Boolean> verifyCinStatus() {
		List<RBRcustomer_entity> customerList = rBRcustomerRepo.findAll();
		List<RBR_Inverstments_Entity> investmentList = RBR_Inverstments_Repo.findAll();
		List<RBRShareHolder_Entity> shareholderList = rbrShareHolder_Repo.findAll();
		List<Facitlity_Entity> facilityList = facility_Repo.findAll();
		List<Security_Entity> securityList = security_Repo.findAll();
		List<Provision_Entity> provisionList = Provision_Repo.findAll();
		List<RBROverall_Data_Entity> overallDataList = RBRoverall_Data_Repo.findAll();
		List<RBR_Legal_Cases_Entity> legalCasesList = RBR_Legal_Cases_Repo.findAll();

		Map<String, Boolean> verificationStatus = new HashMap<>();

		Set<String> allCins = new HashSet<>();
		allCins.addAll(customerList.stream().map(RBRcustomer_entity::getCin).collect(Collectors.toSet()));
		allCins.addAll(investmentList.stream().map(RBR_Inverstments_Entity::getCin).collect(Collectors.toSet()));
		allCins.addAll(shareholderList.stream().map(RBRShareHolder_Entity::getCin).collect(Collectors.toSet()));
		allCins.addAll(facilityList.stream().map(Facitlity_Entity::getCin).collect(Collectors.toSet()));
		allCins.addAll(securityList.stream().map(Security_Entity::getCin).collect(Collectors.toSet()));
		allCins.addAll(provisionList.stream().map(Provision_Entity::getCin).collect(Collectors.toSet()));
		allCins.addAll(overallDataList.stream().map(RBROverall_Data_Entity::getCin).collect(Collectors.toSet()));
		allCins.addAll(legalCasesList.stream().map(RBR_Legal_Cases_Entity::getCin).collect(Collectors.toSet()));

		for (String cin : allCins) {
			boolean isVerified = true;
			isVerified &= customerList.stream().anyMatch(c -> c.getCin().equals(cin) && "Y".equals(c.getAuth_flg()));
			isVerified &= investmentList.stream().anyMatch(i -> i.getCin().equals(cin) && "Y".equals(i.getAuth_flg()));
			isVerified &= shareholderList.stream().anyMatch(s -> s.getCin().equals(cin) && "Y".equals(s.getAuth_flg()));
			isVerified &= facilityList.stream().anyMatch(f -> f.getCin().equals(cin) && "Y".equals(f.getAuth_flg()));
			isVerified &= securityList.stream().anyMatch(se -> se.getCin().equals(cin) && "Y".equals(se.getAuth_flg()));
			isVerified &= provisionList.stream().anyMatch(p -> p.getCin().equals(cin) && "Y".equals(p.getAuth_flg()));
			isVerified &= overallDataList.stream().anyMatch(o -> o.getCin().equals(cin) && "Y".equals(o.getAuth_flg()));
			isVerified &= legalCasesList.stream().anyMatch(l -> l.getCin().equals(cin) && "Y".equals(l.getAuth_flg()));

			verificationStatus.put(cin, isVerified);
		}

		return verificationStatus;
	}

	@RequestMapping(value = "RBR_Final", method = { RequestMethod.GET, RequestMethod.POST })
	public String RBRFINAL(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String cif_no, @RequestParam(required = false) String tab, Model md,
			HttpServletRequest req, String cin) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Roleid = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");
		if (Roleid.equals("RBR")) {
			if (formmode == null || formmode.equals("list")) {
				md.addAttribute("formmode", "list");
				md.addAttribute("listcustomer", rBRcustomerRepo.getFinalRBR());
				md.addAttribute("listShare", rbrShareHolder_Repo.getFinalRBR());
				md.addAttribute("listFacility", facility_Repo.getFinalRBR());
				md.addAttribute("listSecurity", security_Repo.getFinalRBR());
				md.addAttribute("listProvision", Provision_Repo.getFinalRBR());
				md.addAttribute("listoverall", RBRoverall_Data_Repo.getFinalRBR());
				md.addAttribute("listlegalcases", RBR_Legal_Cases_Repo.getFinalRBR());
				md.addAttribute("listInverstmentscases", RBR_Inverstments_Repo.getFinalRBR());

			} else {
				System.out.println("EMPTY");
			}
		} else {
			if (formmode == null || formmode.equals("list")) {
				md.addAttribute("formmode", "list");
				md.addAttribute("listcustomer", rBRcustomerRepo.getFinalbranchRBR(BRANCHCODE));
				md.addAttribute("listShare", rbrShareHolder_Repo.getFinalbranchRBR(BRANCHCODE));
				md.addAttribute("listFacility", facility_Repo.getFinalbranchRBR(BRANCHCODE));
				md.addAttribute("listSecurity", security_Repo.getFinalbranchRBR(BRANCHCODE));
				md.addAttribute("listProvision", Provision_Repo.getFinalbranchRBR(BRANCHCODE));
				md.addAttribute("listoverall", RBRoverall_Data_Repo.getFinalbranchRBR(BRANCHCODE));
				md.addAttribute("listlegalcases", RBR_Legal_Cases_Repo.getFinalbranchRBR(BRANCHCODE));
				md.addAttribute("listInverstmentscases", RBR_Inverstments_Repo.getFinalbranchRBR(BRANCHCODE));
			}
		}
		return "RBRFinal";

	}

	@RequestMapping(value = "/RATNGS")
	@ResponseBody
	public List<Rampop_Entity> RATNGS(@RequestParam(required = false) String parameters, HttpServletRequest req) {

		System.out.println("1");
		List<Rampop_Entity> place = ratingService.getAbsenteesFrom(parameters);
		System.out.println("3");
		for (Rampop_Entity a : place) {
			Rampop_Entity b = new Rampop_Entity();
			;
		}

		return place;
	}

	/**** ECL Master add screen ***/

	@RequestMapping(value = "EclMasterGeneralinr1", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev1(Model md, HttpServletRequest rq, @ModelAttribute ECL_MasterData_INR_Entity eclmasterInr,
			String cust_id) {
		System.out.println("Hi");
		System.out.println(eclmasterInr.getCust_id());

		ECL_MasterData_INR_Entity up = eclmasterInr;
		// md.addAttribute("sanjeev",eclmasterService.remove_expresion(eclmasterInr,
		// cust_id));

		eclmasterrep.save(up);
//System.out.println("SalaryParameter");

		return "success";
	}

	/***** Smooth ***/

	@RequestMapping(value = "EclMasterGeneralSmooth", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev2(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_SMOOTH_Entity eclmasterSmooth,
			String cust_id) {

		ECL_MDT_SMOOTH_Entity up = eclmasterSmooth;

		eCL_MDT_SMOOTH_Rep.save(up);
//System.out.println("SalaryParameter");

		return "success";
	}

	/***** woadj ***/

	@RequestMapping(value = "EclMasterGeneralWOADJ", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev3(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_WO_ADJ_Entity eCL_MDT_WO_ADJ_Entity,
			String cust_id) {

		ECL_MDT_WO_ADJ_Entity up = eCL_MDT_WO_ADJ_Entity;

		eCL_MDT_WO_ADJ_Rep.save(up);
//System.out.println("SalaryParameter");

		return "success";
	}

	/***** Rec ***/

	@RequestMapping(value = "EclMasterRec", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev4(Model md, HttpServletRequest rq,
			@ModelAttribute ECL_MDT_RECOVERY_Entity eCL_MDT_RECOVERY_Entity, String cust_id) {
		System.out.println("the value:" + eCL_MDT_RECOVERY_Entity.getCust_id());
		ECL_MDT_RECOVERY_Entity up = eCL_MDT_RECOVERY_Entity;

		eCL_MDT_RECOVERY_Rep.save(up);
//System.out.println("SalaryParameter");

		return "success";
	}

	/***** LRW ***/

	@RequestMapping(value = "EclMasterLRW", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev5(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_LRW_Entity eCL_MDT_LRW_Entity,
			String cust_id) {
		System.out.println("the value:" + eCL_MDT_LRW_Entity.getCust_id());
		ECL_MDT_LRW_Entity up = eCL_MDT_LRW_Entity;

		eCL_MDT_LRW_Rep.save(up);
//System.out.println("SalaryParameter");

		return "success";
	}

	/***** DISREC ***/

	@RequestMapping(value = "EclMasterDisrec", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev6(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_DIS_REC eCL_MDT_DIS_REC,
			String cust_id) {
		System.out.println("the value:" + eCL_MDT_DIS_REC.getCust_id());
		ECL_MDT_DIS_REC up = eCL_MDT_DIS_REC;

		eCL_MDT_DIS_REC_Rep.save(up);
//System.out.println("SalaryParameter");

		return "success";
	}

	/***** DCR ***/

	@RequestMapping(value = "EclMasterDCR", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev7(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_DCR_Entity eCL_MDT_DCR_Entity,
			String cust_id) {
		System.out.println("the value:" + eCL_MDT_DCR_Entity.getCust_id());
		ECL_MDT_DCR_Entity up = eCL_MDT_DCR_Entity;

		eCL_MDT_DCR_Rep.save(up);
//System.out.println("SalaryParameter");

		return "success";

	}

	/***** LGD ***/

	@RequestMapping(value = "EclMasterLGD", method = RequestMethod.POST)
	@ResponseBody
	public String Snjeev8(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_LGD_Entity eCL_MDT_LGD_Entity,
			String cust_id) {
		System.out.println("the value:" + eCL_MDT_LGD_Entity.getCust_id());
		ECL_MDT_LGD_Entity up = eCL_MDT_LGD_Entity;

		eCL_MDT_LGD_Rep.save(up);
//System.out.println("SalaryParameter");

		return "success";
	}

	/* **** VERIFY ****/

	@RequestMapping(value = "Eclmasterverify1", method = RequestMethod.POST)
	@ResponseBody
	public String V_san1(Model md, HttpServletRequest rq, @ModelAttribute ECL_MasterData_INR_Entity eclmasterInr,
			String cust_id) {

		ECL_MasterData_INR_Entity up = eclmasterInr;

		up.setEntity_flg("Y");

		eclmasterrep.save(up);

		System.out.println("Entity_flag :" + up.getEntity_flg());
//System.out.println("SalaryParameter");

		return "success";
	}

	/**** smooth ***/
	@RequestMapping(value = "Eclmasterverify2", method = RequestMethod.POST)
	@ResponseBody
	public String V_san2(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_AED_Entity eclmasteraed,
			String cust_id) {

		ECL_MDT_AED_Entity up = eclmasteraed;

		up.setEntity_flg("Y");

		eCL_MDT_AED_Rep.save(up);

		return "success";
	}

	/***** woadj_verify ***/

	@RequestMapping(value = "Eclmasterverify3", method = RequestMethod.POST)
	@ResponseBody
	public String V_san3(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_WO_ADJ_Entity eCL_MDT_WO_ADJ_Entity,
			String cust_id) {

		ECL_MDT_WO_ADJ_Entity up = eCL_MDT_WO_ADJ_Entity;

		up.setEntity_flg("Y");

		eCL_MDT_WO_ADJ_Rep.save(up);
		return "success";
	}

	/*** rec verify ***/

	@RequestMapping(value = "Eclmasterverify4", method = RequestMethod.POST)
	@ResponseBody
	public String V_san4(Model md, HttpServletRequest rq,
			@ModelAttribute ECL_MDT_RECOVERY_Entity eCL_MDT_RECOVERY_Entity, String cust_id) {

		ECL_MDT_RECOVERY_Entity up = eCL_MDT_RECOVERY_Entity;

		up.setEntity_flg("Y");

		eCL_MDT_RECOVERY_Rep.save(up);
		return "success";
	}

	/**** LRW ****/

	@RequestMapping(value = "Eclmasterverify5", method = RequestMethod.POST)
	@ResponseBody
	public String V_san5(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_LRW_Entity eCL_MDT_LRW_Entity,
			String cust_id) {

		ECL_MDT_LRW_Entity up = eCL_MDT_LRW_Entity;

		up.setEntity_flg("Y");

		eCL_MDT_LRW_Rep.save(up);
		return "success";
	}

	/***** DISREC ***/

	@RequestMapping(value = "Eclmasterverify6", method = RequestMethod.POST)
	@ResponseBody
	public String V_san6(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_DIS_REC eCL_MDT_DIS_REC,
			String cust_id) {

		ECL_MDT_DIS_REC up = eCL_MDT_DIS_REC;

		up.setEntity_flg("Y");

		eCL_MDT_DIS_REC_Rep.save(up);
		return "success";
	}

	/**** DCR ***/

	@RequestMapping(value = "Eclmasterverify7", method = RequestMethod.POST)
	@ResponseBody
	public String V_san7(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_DCR_Entity eCL_MDT_DCR_Entity,
			String cust_id) {

		ECL_MDT_DCR_Entity up = eCL_MDT_DCR_Entity;

		up.setEntity_flg("Y");

		eCL_MDT_DCR_Rep.save(up);
		return "success";
	}

	/*** LGD ***/

	@RequestMapping(value = "Eclmasterverify8", method = RequestMethod.POST)
	@ResponseBody
	public String V_san8(Model md, HttpServletRequest rq, @ModelAttribute ECL_MDT_LGD_Entity eCL_MDT_LGD_Entity,
			String cust_id) {

		ECL_MDT_LGD_Entity up = eCL_MDT_LGD_Entity;

		up.setEntity_flg("Y");

		eCL_MDT_LGD_Rep.save(up);

		return "success";
	}

	/*
	 * ********ecl file upload screen***** CREATED BY:SANNJEEV DATE:08/01/24
	 * PURPOSE:FOR ECL EXCEL UPLOAD
	 */

	@RequestMapping(value = "eclfileupload", method = { RequestMethod.GET, RequestMethod.POST })
	public String eclfileupload(@RequestParam(required = false) Model md, HttpServletRequest req,
			@RequestParam(required = false) Ecl_upload_Entity ecl_entity) {

//	  md.addAttribute("uploadvaiable",eclmasterService.ecl_file_upload(ecl_entity));

		System.out.println("test controller");

		return "RR/Eclupload";
	}

	/*
	 * CREATED BY:SANJEEVI DATE:13/01/24 PURPOSE:DEPLOYEMENT BRF102
	 * 
	 * 
	 * /*
	 * 
	 * @RequestMapping(value = "eclfileupload", method = { RequestMethod.GET,
	 * RequestMethod.POST }) public String eclfileupload(@RequestParam(required =
	 * false) Model md,HttpServletRequest req,
	 * 
	 * @RequestParam(required = false)Ecl_upload_Entity ecl_entity ) {
	 * 
	 * //md.addAttribute("uploadvaiable",
	 * eclmasterService.ecl_file_upload(ecl_entity));
	 * 
	 * System.out.println("test controller");
	 * 
	 * 
	 * return"RR/Eclupload"; }
	 */

	/*
	 * CREATED BY:SANJEEVI DATE:13/01/24 PURPOSE:DEPLOYEMENT BRF102
	 * 
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
		return brf102AReportService.detailChanges102(detail, foracid, report_addl_criteria_1, act_balance_amt_lc,
				report_label_1, report_name_1);
	}

	/* Reference Code Maintenance */
	@RequestMapping(value = "Referencecodemain", method = { RequestMethod.GET, RequestMethod.POST })

	public String Referencecodemain(@RequestParam(required = false) String formmode,
			@ModelAttribute Reference_code_Entity Reference_Entity, @RequestParam(required = false) String cust_id,
			@RequestParam(required = false) String role_id, Model md, HttpServletRequest req)
			throws NoSuchAlgorithmException, InvalidKeySpecException, ParseException {

		String userId = (String) req.getSession().getAttribute("USERID");
		// md.addAttribute("RoleMenu", resourceMasterRepo.getrole(userId));
		md.addAttribute("menu", "UserProfile");

		if (formmode == null || formmode.equals("list")) {

			md.addAttribute("formmode", "list");

		}

		else if (formmode.equals("list1")) {

			md.addAttribute("formmode", "list1");
			md.addAttribute("getreference", reference_code_Repo.getreferencedetails());

			System.out.println("the testing" + Reference_Entity.getAcct_number());

		}

		else if (formmode.equals("add")) {

			md.addAttribute("formmode", "add");

		} else if (formmode.equals("edit")) {

			md.addAttribute("editcustomer", reference_code_Repo.geteditcustomer(cust_id));
			System.out.println(reference_code_Repo.geteditcustomer(cust_id));
			md.addAttribute("formmode", "edit");

		}
		return "RR/Referencecodemain";
	}

	@RequestMapping(value = "Customermain", method = RequestMethod.POST)
	@ResponseBody
	public String Customermain(Model md, HttpServletRequest rq,
			@ModelAttribute Reference_code_Entity Reference_code_Entity, String cust_id,
			@RequestParam(required = false) String a) {

		Reference_code_Entity add = Reference_code_Entity;
		System.out.println(a);
		add.setAcct_number(a);
		System.out.println("gggggggggggggggg" + Reference_code_Entity.getAcct_number());

		reference_code_Repo.save(add);
//System.out.println("SalaryParameter");
		return "success";
	}

	@RequestMapping(value = "/Referencecodemaincustomerlist")
	@ResponseBody
	public ArrayList<Reference_code_Entity> leavemasteryearszero(@RequestParam(required = false) String year1,
			@RequestParam(required = false) String Employee_id, HttpServletRequest req) {

		System.out.println("Hi it is leave master from btm for testing purpose");

		List<Reference_code_Entity> place = reference_code_Repo.finduserlistcustomer();

		ArrayList<Reference_code_Entity> Status = new ArrayList<>();
		System.out.println("hi this is barath");
		for (Reference_code_Entity att : place) {
			Reference_code_Entity finallist = new Reference_code_Entity();

			finallist.setCust_id(att.getCust_id());
			finallist.setDescription(att.getDescription());
			finallist.setReference_code(att.getReference_code());
			finallist.setReference_type(att.getReference_type());
			finallist.setRemarks(att.getRemarks());
			finallist.setAcct_number(att.getAcct_number());

			// finallist.setLogout(att.getLast_update_time());
			Status.add(finallist);
		}
		System.out.println(Employee_id);
		System.out.println(">>>>>>>>>" + Status);

		/* return PO_Status ; */

		// Convert the list to JSON using Jackson
		return Status;
	}

	@RequestMapping(value = "/Referencecodemaincustomerlist1")
	@ResponseBody
	public ArrayList<Reference_code_Entity> leavemasteryearszero1(@RequestParam(required = false) String year1,
			@RequestParam(required = false) String Employee_id, HttpServletRequest req) {

		System.out.println("Hi it is leave master from btm for testing purpose");

		List<Reference_code_Entity> place = reference_code_Repo.finduserlistcustomer1();

		ArrayList<Reference_code_Entity> Status = new ArrayList<>();
		System.out.println("hi this is barath");
		for (Reference_code_Entity att : place) {
			Reference_code_Entity finallist = new Reference_code_Entity();

			finallist.setCust_id(att.getCust_id());
			finallist.setDescription(att.getDescription());
			finallist.setReference_code(att.getReference_code());
			finallist.setReference_type(att.getReference_type());
			finallist.setRemarks(att.getRemarks());
			finallist.setAcct_number(att.getAcct_number());

			// finallist.setLogout(att.getLast_update_time());
			Status.add(finallist);
		}
		System.out.println(Employee_id);
		System.out.println(">>>>>>>>>" + Status);

		/* return PO_Status ; */

		// Convert the list to JSON using Jackson
		return Status;
	}

	@RequestMapping(value = "editcustomer", method = RequestMethod.POST)
	@ResponseBody
	public String editcustomer(@ModelAttribute Reference_code_Entity Reference_code_Entity, String tran_id,
			String Gst_type, @RequestParam(required = false) String uniqueid,
			@RequestParam(required = false) String Ddt, @RequestParam(required = false) String dsr,
			@RequestParam(required = false) String rds, @RequestParam(required = false) String customer_id,
			@RequestParam(required = false) String f) throws ParseException {
		String u = customer_id;

		System.out.println(u);

		Reference_code_Entity up = reference_code_Repo.findByTran(u);
		System.out.println("hi this is uniqueid for editonusindia" + reference_code_Repo.findByTran(u));
		System.out.println("hi this is btm");

		// SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss",
		// Locale.ENGLISH);

		try {
			// SimpleDateFormat dateFormat = new SimpleDateFormat("dd-mm-yyyy");

			if (up != null) {
				up.setCust_id(Reference_code_Entity.getCust_id());
				up.setAcct_number(Reference_code_Entity.getAcct_number());
				up.setDescription(Reference_code_Entity.getDescription());
				up.setReference_code(Reference_code_Entity.getReference_code());
				up.setReference_type(Reference_code_Entity.getReference_type());
				up.setRemarks(Reference_code_Entity.getRemarks());

				reference_code_Repo.save(up);

				// Additional logic or validations if needed

				return "Edited Successfully";
			} else {
				return "Customer not found with id: " + u;
			}

			// Save the 'up' object with the updated entry_time

		} catch (Exception e) {
			e.printStackTrace(); // Handle potential errors here, such as ParseException
		}

		return "edited Successfully";

	}

	/*-----HR model----*/

	@RequestMapping(value = "hrmodel", method = { RequestMethod.GET, RequestMethod.POST })
	public String hrmodel(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "List of HR Reports");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.gethrmodel());

		return "RR/RRReports";
	}

	@RequestMapping(value = "itmodel", method = { RequestMethod.GET, RequestMethod.POST })
	public String itmodel(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "List of IT Reports");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getitmodel());

		return "RR/RRReports";
	}

	@RequestMapping(value = "opmodel", method = { RequestMethod.GET, RequestMethod.POST })
	public String opmodel(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "List of OP Reports");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getopmodel());

		return "RR/RRReports";
	}

	@RequestMapping(value = "acmodel", method = { RequestMethod.GET, RequestMethod.POST })
	public String acmodel(Model md, HttpServletRequest req) {
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// md.addAttribute("reportvalue", "RBS Reports");
		// md.addAttribute("reportid", "RBSReports");

		String domainid = (String) req.getSession().getAttribute("DOMAINID");
		md.addAttribute("reportsflag", "reportsflag");
		md.addAttribute("menu", "List of AC Reports");

		// md.addAttribute("reportlist", rrReportlist.getReportList());
		md.addAttribute("reportlist", rrReportlist.getacmodel());

		return "RR/RRReports";
	}

	/*--Ecl mapping screen created by sanjeevi-----*/
	@RequestMapping(value = "eclmapping", method = { RequestMethod.GET, RequestMethod.POST })
	public String eclmapping(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("ConstitutionDesc")) {
			System.out.println("Entering constdesc");
			md.addAttribute("formmode", "ConstitutionDesc");
			md.addAttribute("getconstdesc", constdescrep.getconstdes());
		}
		return "Ecl_mapping";
	}

	/*----TREASURY DATA----*/
	@Autowired
	ECL_TREASURY_REP treasuryrep;

	@RequestMapping(value = "Treasurydata", method = { RequestMethod.GET, RequestMethod.POST })
	public String Treasurydata(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("gettreasury", treasuryrep.gettreasurydata());
		}
		return "Treasury";
	}

	/*------nonfunded---*/
	@Autowired
	ECL_FUNDED_REP fundedrep;

	@RequestMapping(value = "fundedbase", method = { RequestMethod.GET, RequestMethod.POST })
	public String fundedbase(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getfundedbase", fundedrep.getfunded());
		}
		return "fundedbase";
	}

	/*------Watchlist---*/
	@Autowired
	ECL_WATCHLIST_REP watchlistrep;

	@RequestMapping(value = "watchlist", method = { RequestMethod.GET, RequestMethod.POST })
	public String watchlist(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getwatchlistdata", watchlistrep.getwatchlistdata());
		}
		return "watchlist";
	}

	@Autowired
	ECL_SMA_DATA_REP smadatarep;

	@RequestMapping(value = "smadata", method = { RequestMethod.GET, RequestMethod.POST })
	public String smadata(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getsmadata", smadatarep.getsmadata());
		}
		return "smadata";
	}

	@Autowired
	ECL_ACC_MASTER_WORKING_REP accmasterworkingrep;

	@RequestMapping(value = "accmasterworking", method = { RequestMethod.GET, RequestMethod.POST })
	public String accmasterworking(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getaccmasterworking", accmasterworkingrep.getaccmasterworking());
		}
		return "accmasterworking";
	}

	@Autowired
	ECL_CUSTMASTER_REP custmasterrep;

	@RequestMapping(value = "custmaster", method = { RequestMethod.GET, RequestMethod.POST })
	public String custmaster(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getcustmaster", custmasterrep.getcustmaster());
		}
		return "custmaster";
	}

	@Autowired
	ECL_ACC_MASTER_REP accmasterrep;

	@RequestMapping(value = "accmaster", method = { RequestMethod.GET, RequestMethod.POST })
	public String accmaster(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getaccmaster", accmasterrep.getaccmaster());
		}
		return "accmaster";
	}

	@Autowired
	ECL_COLLATERAL_REP collateralrep;

	@RequestMapping(value = "collateral", method = { RequestMethod.GET, RequestMethod.POST })
	public String collateral(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getcollateral", collateralrep.getcollateral());
		}
		return "collateral";
	}

	@Autowired
	ECL_RATING_REP ratingrep;

	@RequestMapping(value = "rating", method = { RequestMethod.GET, RequestMethod.POST })
	public String rating(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getrating", ratingrep.getrating());
		}
		return "rating";
	}

	/*----scorecardnewrequirements---*/
	@Autowired
	scorecard_DBR_repo scorecard_dbr_repo;

	@RequestMapping(value = "CLNewcar", method = { RequestMethod.GET, RequestMethod.POST })
	public String CLNewcar(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "DBR");
			md.addAttribute("getDBRvalue", scorecard_dbr_repo.getDBRlist());

			System.out.println(scorecard_dbr_repo.getDBRlist());
		} else if (formmode.equals("INCOME")) {
			md.addAttribute("formmode", "INCOME");
		}
		return "scoredcardnewcar";
	}

	/*-----------scorecardusedcar---------*/

	@RequestMapping(value = "CLUsedcar", method = { RequestMethod.GET, RequestMethod.POST })
	public String CLUsedcar(@RequestParam(required = false) BigDecimal group,
			@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		System.out.println("ENTRING used car");
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "DBR");

		}

		return "scoredcardusedcar";
	}

	/*-----------scorecardpersonalloan---------*/
	@RequestMapping(value = "PersonalLoan", method = { RequestMethod.GET, RequestMethod.POST })
	public String PersonalLoan(@RequestParam(required = false) BigDecimal group,
			@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		System.out.println("ENTERING P-Loan");

		if (formmode == null || formmode.equals("INCOME")) {
			md.addAttribute("formmode", "INCOME");
			List<Personal_INCOME_Entity> incomeValues = personalIncomeRepo.getDBRlist();
			md.addAttribute("getIncomevalue", incomeValues);
			System.out.println("Income Values: " + incomeValues);
		}
		if (formmode == null || formmode.equals("PDBR")) {
			md.addAttribute("formmode", "PDBR");
			List<Personal_DBR_Entity> pdbrValues = personalDBRRepo.getDBRlist();
			md.addAttribute("getPDBRvalue", pdbrValues);
			System.out.println("PDBR Values: " + pdbrValues);
		}

		return "scoredcardpersonalloan";
	}

	/*----scorecard PL---*/
	@Autowired
	SCORE_CARD_PERSONAL_LOAN_REPO SCORE_CARD_PERSONAL_LOAN_Repo;

	@RequestMapping(value = "PLscorecard", method = { RequestMethod.GET, RequestMethod.POST })
	public String PLscorecard(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getPLvalue", scoreService.getPersonalLoanCUSIDList());
		} else if (formmode.equals("edit") || formmode.equals("add")) {
			md.addAttribute("formmode", formmode);

			// Fetching the CAL values for editing
			md.addAttribute("getCALvalue", scoreService.getScoreCalculationList());

			// Fetching the bin values from Personal_DBR_Repo for PDBR dropdown options
			List<Personal_DBR_Entity> pdbrValues = scoreService.getPersonalDBRList();
			md.addAttribute("getPDBRvalue", pdbrValues);

			// Fetching the bin values from Personal_INCOME_Repo for Income dropdown options
			List<Personal_INCOME_Entity> incomeValues = scoreService.getPersonalIncomeList();
			md.addAttribute("getIncomevalue", incomeValues);

			return "scoredcard"; // Assuming "scoredcard" is your Thymeleaf template name for edit mode
		}

		return "scoredcard"; // Assuming "scoredcard" is your Thymeleaf template name for list mode
	}

	@RequestMapping(value = "RBRCustTabdelete", method = RequestMethod.POST)
	@ResponseBody
	public String RBRCustTabdelete(@RequestParam String Srl_no, @RequestParam String cin, @RequestParam String Tabvalue,
			HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		String Msg = "";
		if (Tabvalue.equals("CUSTOMERDATA")) {
			Optional<RBRcustomer_entity> RBRcustomer_entity = rBRcustomerRepo.findById(Srl_no);
			List<RBROverall_Data_Entity> RBRoverall = RBRoverall_Data_Repo.getbyview(cin);
			List<Provision_Entity> Provision = Provision_Repo.getbyview(cin);
			List<Facitlity_Entity> Facitlity_Entity = facility_Repo.getbyview(cin);
			List<Security_Entity> Security = security_Repo.getbyview(cin);
			List<RBRShareHolder_Entity> Shareholder = rbrShareHolder_Repo.getbyview(cin);

			if (Shareholder.size() == 0) {
				if (Facitlity_Entity.size() == 0) {
					if (Security.size() == 0) {
						if (Provision.size() == 0) {
							if (RBRoverall.size() == 0) {
								rBRcustomerRepo.deleteById(Srl_no);

								RBRReportservice.Rbrauditservice(userid, "Customer DATA", "CUSTOMER DATA DELETE",
										cin + " - CUSTOMER DATA DELETED");

								Msg = "Deletion successful! If you don't see the update, Refresh the page";
							} else {
								Msg = "Not able to delete -" + cin + " : is present in RBRoverall sheet";
							}
						} else {
							Msg = "Not able to delete -" + cin + " : is present in Provision sheet";
						}
					} else {
						Msg = "Not able to delete -" + cin + " : is present in Security sheet";
					}
				} else {
					Msg = "Not able to delete -" + cin + " : is present in Facitlity_Entity sheet";
				}
			} else {
				Msg = "Not able to delete -" + cin + " : is present in Partner and shareholder sheet";
			}
			System.out.println(Srl_no + "-Srl   cin-" + cin);
		} else if (Tabvalue.equals("SHAREHOLDERDATA")) {
			rbrShareHolder_Repo.deleteById(Srl_no);

			RBRReportservice.Rbrauditservice(userid, "SHAREHOLDER DATA", "SHAREHOLDER DATA DELETE",
					cin + " - PROVISION DATA DELETED");

			Msg = "Deletion successful! If you don't see the update, Refresh the page";
		} else if (Tabvalue.equals("SECURITYDATA")) {
			security_Repo.deleteById(Srl_no);

			RBRReportservice.Rbrauditservice(userid, "SECURITY DATA", "SECURITY DATA DELETE",
					cin + " - PROVISION DATA DELETED");

			Msg = "Deletion successful! If you don't see the update, Refresh the page";
		} else if (Tabvalue.equals("FACILITYDATA")) {
			facility_Repo.deleteById(Srl_no);

			RBRReportservice.Rbrauditservice(userid, "FACILITY DATA", "FACILITY DATA DELETE",
					cin + " - FACILITY DATA DELETED");

			Msg = "Deletion successful! If you don't see the update, Refresh the page";
		} else if (Tabvalue.equals("PROVISIONDATA")) {
			Provision_Repo.deleteById(Srl_no);

			RBRReportservice.Rbrauditservice(userid, "PROVISION DATA", "PROVISION DATA DELETE",
					cin + " - PROVISION DATA DELETED");

			Msg = "Deletion successful! If you don't see the update, Refresh the page";
		} else if (Tabvalue.equals("OVERALLDATA")) {
			RBRoverall_Data_Repo.deleteById(Srl_no);

			RBRReportservice.Rbrauditservice(userid, "OVERALL DATA", "OVERALL DATA DELETE",
					cin + " - OVERALL DATA DELETED");

			Msg = "Deletion successful! If you don't see the update, Refresh the page";
		}

		return Msg;
	}

	@RequestMapping(value = "RBRCustTab", method = RequestMethod.POST)
	@ResponseBody
	public String RBRCustTab(@RequestParam String cif_no, @RequestBody RBRcustomer_entity details,
			HttpServletRequest rq) {
		System.out.println("RBRCustTab " + cif_no);
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		RBRcustomer_entity up = rBRcustomerRepo.getview(cif_no);
		String msg = "";
		if (up != null) {
			msg = RBRReportservice.RBREditValidation(details);
			if (msg.equals("Verification Ok")) {
				details.setBranch_code(up.getBranch_code());
				details.setCaname("");
				// details.setOperation("UPD");
				details.setModify_flg("Y");
				details.setModify_user(username);
				details.setModify_time(new Date());
				details.setAuth_flg(up.getAuth_flg() != null ? up.getAuth_flg() : "N");
				details.setModify_user(userid);
				details.setReport_date(up.getReport_date());
				details.setBranch(up.getBranch());
				rBRcustomerRepo.save(details);
				return "Edited Successfully";
			} else {
				return msg;
			}
		} else {
			return "Customer not found";
		}
	}

	@RequestMapping(value = "RBRInvestTab", method = RequestMethod.POST)
	@ResponseBody
	public String RBRInvestTab(@RequestParam String cin, @RequestBody RBR_Inverstments_Entity details,
			HttpServletRequest rq) {
		System.out.println("RBRInvestTab " + cin);
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		RBR_Inverstments_Entity up = RBR_Inverstments_Repo.getview(cin);
		if (up != null) {
			details.setOperation("UPD");
			details.setModify_flg("Y");
			details.setModify_user(username);
			details.setModify_time(new Date());
			details.setAuth_flg(up.getAuth_flg() != null ? up.getAuth_flg() : "N");
			details.setModify_user(userid);
			details.setReport_date(up.getReport_date());
			RBR_Inverstments_Repo.save(details);
			return "Edited Successfully";
		} else {
			return "Investment not found";
		}
	}

	@RequestMapping(value = "RBRLegalTab", method = RequestMethod.POST)
	@ResponseBody
	public String RBRLegalTab(@RequestParam String cin, @RequestBody RBR_Legal_Cases_Entity details,
			HttpServletRequest rq) {
		System.out.println("RBRLegalTab " + cin);
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		RBR_Legal_Cases_Entity up = RBR_Legal_Cases_Repo.getview(cin);
		if (up != null) {
			details.setOperation("UPD");
			details.setModify_flg("Y");
			details.setModify_user(username);
			details.setModify_time(new Date());
			details.setAuth_flg(up.getAuth_flg() != null ? up.getAuth_flg() : "N");
			details.setModify_user(userid);
			details.setReport_date(up.getReport_date());
			RBR_Legal_Cases_Repo.save(details);
			return "Edited Successfully";
		} else {
			return "Legal not found";
		}
	}

	@RequestMapping(value = "RBROverallTab", method = RequestMethod.POST)
	@ResponseBody
	public String RBROverallTab(@RequestParam String cin, @RequestBody RBROverall_Data_Entity details,
			HttpServletRequest rq) {
		System.out.println("RBROverallTab " + cin);
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		RBROverall_Data_Entity up = RBRoverall_Data_Repo.getupdate(details.getSrl_no());
		if (up != null) {
			details.setOperation("UPD");
			details.setModify_flg("Y");
			details.setModify_user(username);
			details.setModify_time(new Date());
			details.setAuth_flg(up.getAuth_flg() != null ? up.getAuth_flg() : "N");
			details.setModify_user(userid);
			details.setReport_date(up.getReport_date());
			RBRoverall_Data_Repo.save(details);
			return "Edited Successfully";
		} else {
			return "Overalldata not found";
		}
	}

	@RequestMapping(value = "RBRProvisionTab", method = RequestMethod.POST)
	@ResponseBody
	public String RBRProvisionTab(@RequestParam String cin, @RequestBody Provision_Entity details,
			HttpServletRequest rq) {
		System.out.println("RBRProvisonTab " + cin);
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		Provision_Entity up = Provision_Repo.getupdate(details.getSrl_no());
		if (up != null) {
			details.setOperation("UPD");
			details.setModify_flg("Y");
			details.setModify_user(username);
			details.setModify_time(new Date());
			details.setAuth_flg(up.getAuth_flg() != null ? up.getAuth_flg() : "N");
			details.setModify_user(userid);
			details.setReport_date(up.getReport_date());
			Provision_Repo.save(details);

			return "Edited Successfully";
		} else {
			return "Provision not found";
		}
	}

	@RequestMapping(value = "RBRFacilityTab", method = RequestMethod.POST)
	@ResponseBody
	public String RBRFacilityTab(@RequestParam String cin, @RequestBody Facitlity_Entity details,
			HttpServletRequest rq) {
		System.out.println("RBRFacilityTab " + cin);
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		Facitlity_Entity up = facility_Repo.getupdate(details.getSrl_no());
		if (up != null) {
			details.setOperation("UPD");
			details.setModify_flg("Y");
			details.setModify_user(username);
			details.setModify_time(new Date());
			details.setAuth_flg(up.getAuth_flg() != null ? up.getAuth_flg() : "N");
			details.setModify_user(userid);

			facility_Repo.save(details);
			details.setReport_date(up.getReport_date());
			return "Edited Successfully";
		} else {
			return "Facility not found";
		}
	}

	@RequestMapping(value = "RBRSecurityTab", method = RequestMethod.POST)
	@ResponseBody
	public String RBRSecurityTab(@RequestParam String cin, @RequestBody Security_Entity details,
			HttpServletRequest rq) {
		System.out.println("RBRFacilityTab " + cin);
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		Security_Entity up = security_Repo.getupdate(details.getSrl_no());

		if (up != null) {

			details.setOperation("UPD");
			details.setModify_flg("Y");
			details.setModify_user(username);
			details.setModify_time(new Date());
			details.setAuth_flg(up.getAuth_flg() != null ? up.getAuth_flg() : "N");
			details.setModify_user(userid);
			details.setReport_date(up.getReport_date());
			security_Repo.save(details);
			return "Edited Successfully";

		} else {
			return "Security not found";
		}
	}

	@RequestMapping(value = "RBRPartnerTab", method = RequestMethod.POST)
	@ResponseBody
	public String RBRPartnerTab(@RequestParam String cin, @RequestBody RBRShareHolder_Entity details,
			HttpServletRequest rq) {
		System.out.println("Partner Cin " + cin);
		String userid = (String) rq.getSession().getAttribute("USERID");
		String username = (String) rq.getSession().getAttribute("USERNAME");
		RBRShareHolder_Entity up = rbrShareHolder_Repo.getupdate(details.getSrl_no());
		System.out.println(details.getP_s_cin() + " " + details.getBankcode());
		String Msg = RBRReportservice.RBRPartnervalidation(details);

		if (!cin.equals("ADD")) {

			if (up != null) {
				if (Msg.equals("Validation_done")) {
					details.setOperation("UPD");
					details.setModify_flg("Y");
					details.setModify_user(username);
					details.setModify_time(new Date());
					details.setAuth_flg(up.getAuth_flg() != null ? up.getAuth_flg() : "N");
					details.setModify_user(userid);
					details.setReport_date(up.getReport_date());
					rbrShareHolder_Repo.save(details);
					return "Edited Successfully";
				} else {
					return Msg;
				}
			} else {
				return "Partner and shareholder not found";
			}
		} else {
			RBRShareHolder_Entity rbrshare = rbrShareHolder_Repo.findByCin(details.getCin());
			if (rbrshare.getCin().isEmpty()) {
				return "No data Present for Mentioned Cin";
			} else {

				Long Srl_no = rbrShareHolder_Repo.getAuditRefUUID();
				details.setSrl_no(Srl_no.toString());

				rbrShareHolder_Repo.save(details);

				return "New Partner data Added";

			}
		}
	}

	@RequestMapping(value = "RBRUpdatecin", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> RBRUpdatecin(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String cin_Cust1, @RequestParam(required = false) String cif_no_Cust1,
			@RequestParam(required = false) String csno_Cust1, Model md, HttpServletRequest req) {
		String userid = (String) req.getSession().getAttribute("USERID");
		String Msg;
		RBRcustomer_entity UP = rBRcustomerRepo.getview(cif_no_Cust1);

		UP.setCin(cin_Cust1);
		UP.setCsno(csno_Cust1);

		rBRcustomerRepo.save(UP);

		List<RBRShareHolder_Entity> up11 = rbrShareHolder_Repo.getbycustid(cif_no_Cust1);

		for (RBRShareHolder_Entity up1 : up11) {
			if (up1 != null) {
				String authFlag = up1.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up1.setCin(cin_Cust1);
					up1.setCsno(csno_Cust1);
					rbrShareHolder_Repo.save(up1);
				}
			}
		}

		List<Facitlity_Entity> up31 = facility_Repo.getbycustid(cif_no_Cust1);
		for (Facitlity_Entity up3 : up31) {
			if (up3 != null) {
				String authFlag = up3.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up3.setCin(cin_Cust1);
					up3.setCsno(csno_Cust1);

					facility_Repo.save(up3);
				}
			}
		}

		List<Security_Entity> up41 = security_Repo.getbycustid(cif_no_Cust1);
		for (Security_Entity up4 : up41) {
			if (up4 != null) {
				String authFlag = up4.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up4.setCin(cin_Cust1);
					up4.setCsno(csno_Cust1);
					security_Repo.save(up4);
				}
			}
		}
		List<Provision_Entity> up51 = Provision_Repo.getbycustid(cif_no_Cust1);
		for (Provision_Entity up5 : up51) {
			if (up5 != null) {
				String authFlag = up5.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up5.setCin(cin_Cust1);
					up5.setCsno(csno_Cust1);

					Provision_Repo.save(up5);
				}
			}
		}
		List<RBROverall_Data_Entity> up61 = RBRoverall_Data_Repo.getbycustid(cif_no_Cust1);
		for (RBROverall_Data_Entity up6 : up61) {
			if (up6 != null) {
				String authFlag = up6.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up6.setCin(cin_Cust1);
					up6.setCsno(csno_Cust1);

					RBRoverall_Data_Repo.save(up6);
				}
			}
		}

		RBRReportservice.Rbrauditservice(userid, "All CCSYS TABLES", "CIN and CSNO",
				cin_Cust1 + " - CIN AND " + csno_Cust1 + " - CSNO UPDATE");

		Msg = "Cin Updated successfully";
		return ResponseEntity.ok(Msg);

	}

	@RequestMapping(value = "RBRVerify", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> RBRVerify(@RequestParam String cin, @RequestParam String Datatype,
			@RequestParam String Srl_no, @ModelAttribute RBRShareHolder_Entity details1,
			@ModelAttribute RBRcustomer_entity details2, @ModelAttribute Facitlity_Entity details3,
			@ModelAttribute Security_Entity details4, @ModelAttribute Provision_Entity details5,
			@ModelAttribute RBROverall_Data_Entity details6, @ModelAttribute RBR_Legal_Cases_Entity details7,
			@ModelAttribute RBR_Inverstments_Entity details8, HttpServletRequest rq, Model md) {

		Map<String, Object> response = new HashMap<>();
		String msg = "";
		String userid = (String) rq.getSession().getAttribute("USERID");
		// Boolean a = verifyAndUpdateAuthFlg(cin);
		// md.addAttribute("allVerified", a);

		if (Datatype.equals("CUSTOMERDATA")) {

			msg = RBRReportservice.RBRValidation(cin);

			RBRcustomer_entity up2 = rBRcustomerRepo.findById(Srl_no).get();
			if (up2 != null && up2.getCin() != null) {
				String authFlag = up2.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up2.setAuth_flg("Y");
					up2.setAuth_user(userid);
					up2.setAuth_time(new Date());
					rBRcustomerRepo.save(up2);

					RBRReportservice.Rbrauditservice(userid, "Customer data", "Customer verification",
							up2.getCif_no() + " is verified and Srl no is " + up2.getSrl_no());

					msg = "Customer data successfully verified!";
				}
			} else {
				msg = "Verification failed: CIN is missing. " + "Please provide a valid CIN to proceed.";
			}

		}

		if (Datatype.equals("SHAREHOLDERDATA")) {

			RBRShareHolder_Entity up1 = rbrShareHolder_Repo.findById(Srl_no).get();

			if (up1 != null && up1.getCin() != null) {
				String authFlag = up1.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up1.setAuth_flg("Y");
					up1.setAuth_user(userid);
					up1.setAuth_time(new Date());
					rbrShareHolder_Repo.save(up1);

					RBRReportservice.Rbrauditservice(userid, "partner data", "Partner verification",
							up1.getP_s_cin() + " is verified and Srl no is " + up1.getSrl_no());

					msg = "Partner data successfully verified!";

				}
			} else {
				msg = "Verification failed: CIN is missing. " + "Please provide a valid CIN to proceed.";
			}

		}
		if (Datatype.equals("FACILITYDATA")) {
			Facitlity_Entity up3 = facility_Repo.findById(Srl_no).get();

			if (up3 != null && up3.getCin() != null) {
				String authFlag = up3.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up3.setAuth_flg("Y");
					up3.setAuth_user(userid);
					up3.setAuth_time(new Date());
					facility_Repo.save(up3);

					RBRReportservice.Rbrauditservice(userid, "Facility data", "Facility verification",
							up3.getFac_id() + " is verified and Srl no is " + up3.getSrl_no());

					msg = "Facility data successfully verified!";

				}
			} else {
				msg = "Verification failed: CIN is missing. " + "Please provide a valid CIN to proceed.";
			}
		}
		if (Datatype.equals("SECURITYDATA")) {
			Security_Entity up4 = security_Repo.findById(Srl_no).get();

			if (up4 != null && up4.getCin() != null) {
				String authFlag = up4.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up4.setAuth_flg("Y");
					up4.setAuth_user(userid);
					up4.setAuth_time(new Date());
					security_Repo.save(up4);

					RBRReportservice.Rbrauditservice(userid, "Security data", "Security verification",
							up4.getFac_id() + " is verified and Srl no is " + up4.getSrl_no());

					msg = "Security data successfully verified!";
				}
			} else {
				msg = "Verification failed: CIN is missing. " + "Please provide a valid CIN to proceed.";
			}
		}
		if (Datatype.equals("PROVISIONDATA")) {
			Provision_Entity up5 = Provision_Repo.findById(Srl_no).get();

			if (up5 != null && up5.getCin() != null) {
				String authFlag = up5.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up5.setAuth_flg("Y");
					up5.setAuth_user(userid);
					up5.setAuth_time(new Date());
					Provision_Repo.save(up5);

					RBRReportservice.Rbrauditservice(userid, "Provision data", "Provision verification",
							up5.getFac_id() + " is verified and Srl no is " + up5.getSrl_no());

					msg = "Provision data successfully verified!";
				}
			} else {
				msg = "Verification failed: CIN is missing. " + "Please provide a valid CIN to proceed.";
			}
		}
		if (Datatype.equals("OVERALLDATA")) {
			RBROverall_Data_Entity up6 = RBRoverall_Data_Repo.findById(Srl_no).get();

			if (up6 != null && up6.getCin() != null) {
				String authFlag = up6.getAuth_flg();
				if (authFlag != null && authFlag.equals("N")) {
					up6.setAuth_flg("Y");
					up6.setAuth_user(userid);
					up6.setAuth_time(new Date());
					RBRoverall_Data_Repo.save(up6);

					RBRReportservice.Rbrauditservice(userid, "Overall data", "Overall verification",
							up6.getCin() + " is verified and Srl no is " + up6.getSrl_no());

					msg = "Overall data successfully verified!";
				}
			} else {
				msg = "Verification failed: CIN is missing. " + "Please provide a valid CIN to proceed.";
			}

		}
		response.put("message", msg);

		response.put("cin", cin);
		return response;
	}

	public boolean verifyAndUpdateAuthFlg(String cin) {
		List<RBRcustomer_entity> customerList = rBRcustomerRepo.findAll();
		List<RBR_Inverstments_Entity> investmentList = RBR_Inverstments_Repo.findAll();
		List<RBRShareHolder_Entity> shareholderList = rbrShareHolder_Repo.findAll();
		List<Facitlity_Entity> facilityList = facility_Repo.findAll();
		List<Security_Entity> securityList = security_Repo.findAll();
		List<Provision_Entity> provisionList = Provision_Repo.findAll();
		List<RBROverall_Data_Entity> overallDataList = RBRoverall_Data_Repo.findAll();
		List<RBR_Legal_Cases_Entity> legalCasesList = RBR_Legal_Cases_Repo.findAll();

		RBRcustomer_entity customer = customerList.stream().filter(entity -> cin.equals(entity.getCin())).findFirst()
				.orElse(null);
		System.out.println("Customer: " + customer);

		RBR_Inverstments_Entity investment = investmentList.stream().filter(entity -> cin.equals(entity.getCin()))
				.findFirst().orElse(null);
		System.out.println("Investment: " + investment);

		RBRShareHolder_Entity shareholder = shareholderList.stream().filter(entity -> cin.equals(entity.getCin()))
				.findFirst().orElse(null);
		System.out.println("Shareholder: " + shareholder);

		Facitlity_Entity facility = facilityList.stream().filter(entity -> cin.equals(entity.getCin())).findFirst()
				.orElse(null);
		System.out.println("Facility: " + facility);

		Security_Entity security = securityList.stream().filter(entity -> cin.equals(entity.getCin())).findFirst()
				.orElse(null);
		System.out.println("Security: " + security);

		Provision_Entity provision = provisionList.stream().filter(entity -> cin.equals(entity.getCin())).findFirst()
				.orElse(null);
		System.out.println("Provision: " + provision);

		RBROverall_Data_Entity overallData = overallDataList.stream().filter(entity -> cin.equals(entity.getCin()))
				.findFirst().orElse(null);
		System.out.println("Overall Data: " + overallData);

		RBR_Legal_Cases_Entity legalCases = legalCasesList.stream().filter(entity -> cin.equals(entity.getCin()))
				.findFirst().orElse(null);
		System.out.println("Legal Cases: " + legalCases);

		boolean allVerified = (customer != null && "Y".equals(customer.getAuth_flg()))
				&& (investment != null && "Y".equals(investment.getAuth_flg()))
				&& (shareholder != null && "Y".equals(shareholder.getAuth_flg()))
				&& (facility != null && "Y".equals(facility.getAuth_flg()))
				&& (security != null && "Y".equals(security.getAuth_flg()))
				&& (provision != null && "Y".equals(provision.getAuth_flg()))
				&& (overallData != null && "Y".equals(overallData.getAuth_flg()))
				&& (legalCases != null && "Y".equals(legalCases.getAuth_flg()));
		System.out.println(allVerified + "allVerifiedallVerifiedallVerifiedallVerified");
		return allVerified;
	}

	@Autowired
	com.bornfire.xbrl.entities.ECL_COOLOFF_1_repo ECL_COOLOFF_1_repo;
	@Autowired
	com.bornfire.xbrl.entities.ECL_COOLOFF_2_repo ECL_COOLOFF_2_repo;
	@Autowired
	com.bornfire.xbrl.entities.ECL_COOLOFF_3_repo ECL_COOLOFF_3_repo;
	@Autowired
	com.bornfire.xbrl.entities.ECL_COOLOFF_4_repo ECL_COOLOFF_4_repo;

	@RequestMapping(value = "cooloff", method = { RequestMethod.GET, RequestMethod.POST })
	public String CoolOff(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getcooloff", ECL_COOLOFF_1_repo.getECLCoolOff());
			md.addAttribute("getcooloff2", ECL_COOLOFF_2_repo.getECLCoolOff());
			md.addAttribute("getcooloff3", ECL_COOLOFF_3_repo.getECLCoolOff());
			md.addAttribute("getcooloff4", ECL_COOLOFF_4_repo.getECLCoolOff());
		}
		return "CoolOff";
	}

	@RequestMapping(value = "vatledger", method = { RequestMethod.GET, RequestMethod.POST })
	public String VatLedger(@RequestParam(required = false) String date,
			@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			HttpServletResponse response) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		String formattedDate = null;
		if (date != null) {

			try {
				LocalDate date1 = LocalDate.parse(date, inputFormatter);

				formattedDate = date1.format(outputFormatter);
			} catch (DateTimeParseException e) {
				System.err.println("Invalid date format: " + e.getMessage());
			}

		}

		if (date == null && formmode == null) {

			md.addAttribute("formmode", "LEDGER");
			md.addAttribute("menu", "VAT LEDGER");

			return "VAT_Ledger";
		} else if ("download".equals(formmode) && formattedDate != null) {
			try {
				// Get the file from the service
				File file = vat_ledger_services.getFileB10(formattedDate);

				if (file != null && file.exists()) {
					// Set the response content type and headers for file download
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
					response.setContentLength((int) file.length());

					// Stream the file to the response output
					InputStream inputStream = new FileInputStream(file);
					OutputStream outStream = response.getOutputStream();

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, bytesRead);
					}

					inputStream.close();
					outStream.flush();
					outStream.close();

					return null; // File successfully downloaded, no further view
				} else {
					// File not found, proceed with displaying VAT report
					if (formattedDate != null) {
						md.addAttribute("vat_data", vat_ledger_repo.getVATdata(formattedDate));
					}
					return "VAT_Report";
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JRException | SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			List<Vat_Ledger_Entity> vatData = vat_ledger_repo.getVATdata(formattedDate);
			if (vatData == null || vatData.isEmpty()) {
				// Optionally, log a message or display a message on the front end
				md.addAttribute("formmode", "LEDGER");
				md.addAttribute("message", "No VAT data available for the selected date.");

				return "VAT_Ledger";
			} else {
				// Add the VAT data to the model
				md.addAttribute("formmode", "LEDGER");
				md.addAttribute("vat_data", vatData);

				// Return the VAT report view
				return "VAT_Report";
			}

		}
		return "VAT_Ledger";
	}

	//////////////////////// VAT INVOICE PURCHASE AND SUPPLIES///////////////////

	@RequestMapping(value = "VatIncoice", method = { RequestMethod.GET, RequestMethod.POST })
	public String VatIncoice(@RequestParam(required = false) String date,
			@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			HttpServletResponse response) {
		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		String formattedDate = null;
		if (date != null) {

			try {
				LocalDate date1 = LocalDate.parse(date, inputFormatter);

				formattedDate = date1.format(outputFormatter);
			} catch (DateTimeParseException e) {
				System.err.println("Invalid date format: " + e.getMessage());
			}

		}

		if (date == null && formmode == null) {

			md.addAttribute("formmode", "INVOICE");
			md.addAttribute("menu", "INVOICE PURCHASE AND SUPPLIES");

			return "VAT_Ledger";
		} else if ("download".equals(formmode) && formattedDate != null) {
			try {
				// Get the file from the service
				File file = vat_ledger_services.getFileB10(formattedDate);

				if (file != null && file.exists()) {
					// Set the response content type and headers for file download
					response.setContentType("application/octet-stream");
					response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
					response.setContentLength((int) file.length());

					// Stream the file to the response output
					InputStream inputStream = new FileInputStream(file);
					OutputStream outStream = response.getOutputStream();

					byte[] buffer = new byte[4096];
					int bytesRead;
					while ((bytesRead = inputStream.read(buffer)) != -1) {
						outStream.write(buffer, 0, bytesRead);
					}

					inputStream.close();
					outStream.flush();
					outStream.close();

					return null; // File successfully downloaded, no further view
				} else {
					// File not found, proceed with displaying VAT report
					if (formattedDate != null) {
						md.addAttribute("formmode", "INVOICE");
						md.addAttribute("vat_data", vat_ledger_repo.getVATdata(formattedDate));
					}
					return "VAT_Report";
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JRException | SQLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			List<Vat_Ledger_Entity> vatData = vat_ledger_repo.getVATdata(formattedDate);
			if (vatData == null || vatData.isEmpty()) {
				// Optionally, log a message or display a message on the front end
				md.addAttribute("formmode", "INVOICE");
				md.addAttribute("message", "No VAT data available for the selected date.");

				return "VAT_Ledger";
			} else {
				// Add the VAT data to the model
				md.addAttribute("vat_data", vatData);
				md.addAttribute("formmode", "INVOICE");
				// Return the VAT report view
				return "VAT_Report";
			}

		}
		return "VAT_Ledger";

	}

	// -----downloadforMSExcel by NISHANTHINI
	// coresystem download

	@RequestMapping(value = "downloadExcel", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadExcel() throws IOException {

		System.out.println("the enter the controller--1");

		List<Brecon_core_entity> coresystemEntity = brecon_core_rep.getcoresystemlistdata();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Coresystem Data");

		// Create bold and centered header style with borders
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER); // Center alignment for header
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);

		// Create a regular cell style with borders
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);

		// Create Header Row
		Row headerRow = sheet.createRow(0);
		String[] headers = { "Srl No", "Tran Date", "Tran Id", "Part Tran Id", "Tran Amount", "Tran Type",
				"Tran Account Number", "Tran Account Name", "Tran Particular" };
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle); // Apply centered bold style to header
		}

		int rowIndex = 1;

		for (Brecon_core_entity coresystem : coresystemEntity) {
			Date tranDate = coresystem.getTran_date();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			String formattedDate = formatter.format(tranDate);

			Row row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue(rowIndex - 1);
			row.createCell(1).setCellValue(formattedDate);
			row.createCell(2).setCellValue(coresystem.getTran_id());
			row.createCell(3).setCellValue(coresystem.getPart_tran_srl_num());
			row.createCell(4).setCellValue(coresystem.getTran_amt().doubleValue());
			row.createCell(5).setCellValue(coresystem.getTran_type());
			row.createCell(6).setCellValue(coresystem.getAcid());
			row.createCell(7).setCellValue(coresystem.getBank_code());
			row.createCell(8).setCellValue(coresystem.getTran_particular());

			// Apply cell style with borders to each cell in the row
			for (int i = 0; i < headers.length; i++) {
				row.getCell(i).setCellStyle(cellStyle);
			}
		}

		// Adjust column widths to fit the content
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		// Write data to a ByteArrayOutputStream
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		// Set response headers and return the file
		HttpHeaders headersResponse = new HttpHeaders();
		headersResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headersResponse.setContentDispositionFormData("attachment", "CoresystemData.xlsx");

		return ResponseEntity.ok().headers(headersResponse).body(outputStream.toByteArray());
	}

	/// clearing system ms_excel download

	@RequestMapping(value = "downloadExcel1", method = RequestMethod.GET)
	public ResponseEntity<byte[]> downloadExcel1() throws IOException {

		System.out.println("the enter the controller--1");

		List<BRECON_DESTINATION_ENTITY> clearingsystemEntity = brecon_destination_repo.getDestination();

		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Clearingsystem Data");

		// Create bold and centered header style with borders
		CellStyle headerStyle = workbook.createCellStyle();
		Font headerFont = workbook.createFont();
		headerFont.setBold(true);
		headerStyle.setFont(headerFont);
		headerStyle.setAlignment(HorizontalAlignment.CENTER); // Center alignment for header
		headerStyle.setBorderTop(BorderStyle.THIN);
		headerStyle.setBorderBottom(BorderStyle.THIN);
		headerStyle.setBorderLeft(BorderStyle.THIN);
		headerStyle.setBorderRight(BorderStyle.THIN);

		// Create a regular cell style with borders
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);

		// Create Header Row
		Row headerRow = sheet.createRow(0);
		String[] headers = { "Srl No", "Tran Date", "Tran Id", "Part Tran Id", "Tran Amount", "Tran Type",
				"Tran Account Number", "Tran Account Name", "Tran Particular" };
		for (int i = 0; i < headers.length; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(headers[i]);
			cell.setCellStyle(headerStyle); // Apply centered bold style to header
		}

		int rowIndex = 1;

		for (BRECON_DESTINATION_ENTITY clearingsystem : clearingsystemEntity) {
			Date tranDate = clearingsystem.getStmt_from_date_time();
			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
			String formattedDate = formatter.format(tranDate);

			Row row = sheet.createRow(rowIndex++);
			row.createCell(0).setCellValue(rowIndex - 1);
			row.createCell(1).setCellValue(formattedDate);
			row.createCell(2).setCellValue(clearingsystem.getNtry_btch_currency());
			row.createCell(3).setCellValue(clearingsystem.getNtry_proprietary_code());
			row.createCell(4).setCellValue(clearingsystem.getNtry_transaction_amount().doubleValue());
			row.createCell(5).setCellValue(clearingsystem.getNtry_txdtls_credit_debit_indicator());
			row.createCell(6).setCellValue(clearingsystem.getAccount_no());
			row.createCell(7).setCellValue(clearingsystem.getStmt_account_identifier());
			row.createCell(8).setCellValue(clearingsystem.getNtry_entry_reference());

			// Apply cell style with borders to each cell in the row
			for (int i = 0; i < headers.length; i++) {
				row.getCell(i).setCellStyle(cellStyle);
			}
		}

		// Adjust column widths to fit the content
		for (int i = 0; i < headers.length; i++) {
			sheet.autoSizeColumn(i);
		}

		// Write data to a ByteArrayOutputStream
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		// Set response headers and return the file
		HttpHeaders headersResponse = new HttpHeaders();
		headersResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		headersResponse.setContentDispositionFormData("attachment", "ClearingsystemData.xlsx");

		return ResponseEntity.ok().headers(headersResponse).body(outputStream.toByteArray());
	}

	@RequestMapping(value = "Dataupload", method = RequestMethod.GET)
	public String Dataupload(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");

		}

		return "Dataupload";
	}

	@Autowired
	Brecon_core_rep brecon_core_rep;

	@RequestMapping(value = "coresystem", method = RequestMethod.GET)
	public String coresystem(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("list", brecon_core_rep.getcoresystemlistdata());
		}

		return "Brecon_core";
	}

	@RequestMapping(value = "coresystemlist", method = RequestMethod.GET)
	public String coresystemlist(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		String userid = (String) req.getSession().getAttribute("USERID");
		System.out.println(userid + "userid");
		if (formmode.equals("srlno")) {
			md.addAttribute("formmode", "srlno");
			System.out.println(srlno);
			md.addAttribute("srlno", brecon_core_rep.getSrlno(srlno));

		} else if (formmode.equals("modify")) {
			md.addAttribute("formmode", "modify");
			System.out.println(srlno);
			md.addAttribute("srlno", brecon_core_rep.getSrlno(srlno));
		} else if (formmode.equals("verify")) {
			md.addAttribute("formmode", "verify");
			System.out.println(srlno);
			md.addAttribute("srlno", brecon_core_rep.getSrlno(srlno));
		} else if (formmode.equals("add")) {
			md.addAttribute("formmode", "add");

		}

		return "Brecon_core_list";
	}

	@RequestMapping(value = "breconmodifysubmit", method = RequestMethod.POST)
	@ResponseBody
	public String breconmodifysubmit(@RequestParam(required = false) String srlno, Model md, HttpServletRequest rq,
			@ModelAttribute Brecon_core_entity brecon_core_entity, HttpServletRequest request, HttpServletRequest req) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		System.out.println(userid + "userid");

		String msg;

		// Get role ID from the session
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// Ensure modification logic is executed only if role is "BRC"
		if ("BRC".equals(roleId)) { // Safe comparison to avoid NullPointerException
			System.out.println(brecon_core_entity.getSrlno());

			// Assuming brecon_core_entity is populated with the form data
			// Update flags and user directly
			brecon_core_entity.setDel_flg("N"); // Set delete flag
			brecon_core_entity.setModify_flg("Y"); // Set modify flag
			brecon_core_entity.setEntity_flg("Y");
			brecon_core_entity.setModify_user(userid);

			// Save the entity without checking for an existing one
			brecon_core_rep.save(brecon_core_entity);

			// Generate audit entry
			String auditID = sequence.generateRequestUUId();
			String user1 = (String) req.getSession().getAttribute("USERID");
			String username = (String) req.getSession().getAttribute("USERNAME");

			// Create and populate audit entity
			BRECON_Audit_Entity audit = new BRECON_Audit_Entity();
			Date currentDate = new Date();
			audit.setAudit_date(currentDate);
			audit.setEntry_time(currentDate);
			audit.setEntry_user(user1);
			audit.setFunc_code("MODIFIED");
			audit.setAudit_table("BRECONSOURCETABLE");
			audit.setAudit_screen("MODIFY");
			audit.setEvent_id(user1);
			audit.setEvent_name(username);
			audit.setModi_details("Modified Successfully");

			// Fetch user profile and add authorization details to the audit entity if
			// available
			UserProfile values1 = userProfileRep.getRole(user1);
			if (values1 != null) {
				audit.setAuth_user(values1.getAuth_user());
				audit.setAuth_time(values1.getAuth_time());
			}

			audit.setAudit_ref_no(auditID);

			// Save audit entity
			bRECON_Audit_Rep.save(audit);

			// Success message
			msg = "Record modified successfully";
		} else {
			// If role is not "BRC", modification is not allowed
			msg = "Modification not allowed for this user";
		}

		return msg;
	}

	@RequestMapping(value = "breconverifysubmit", method = RequestMethod.POST)
	@ResponseBody
	public String breconverifysubmit(@RequestParam(required = false) String srlno, Model md, HttpServletRequest rq,
			@ModelAttribute Brecon_core_entity brecon_core_entity, HttpServletRequest req) {
		// Retrieve user information from both 'rq' and 'req'
		String userid = (String) rq.getSession().getAttribute("USERID");
		System.out.println(userid + " userid");

		String msg;

		// Retrieve role ID from the session
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// Ensure modification logic is executed only if the role is "BRC"
		if ("BRC".equals(roleId)) { // Safe comparison to avoid NullPointerException
			System.out.println(brecon_core_entity.getSrlno());

			// Set the modify flag and verified user
			brecon_core_entity.setModify_flg("N"); // Set modify flag to "N"
			brecon_core_entity.setVerify_user(userid); // Set verified user

			// Generate audit ID and retrieve user details from session
			String auditID = sequence.generateRequestUUId();
			String user1 = (String) req.getSession().getAttribute("USERID");
			String username = (String) req.getSession().getAttribute("USERNAME");

			// Create and populate audit entity
			BRECON_Audit_Entity audit = new BRECON_Audit_Entity();
			Date currentDate = new Date();
			audit.setAudit_date(currentDate);
			audit.setEntry_time(currentDate);
			audit.setEntry_user(user1);
			audit.setFunc_code("VERIFIED");
			audit.setAudit_table("BRECONSOURCETABLE");
			audit.setAudit_screen("VERIFY");
			audit.setEvent_id(user1);
			audit.setEvent_name(username);
			audit.setModi_details("Verified Successfully");

			// Fetch user profile and add authorization details to the audit entity if
			// available
			UserProfile values1 = userProfileRep.getRole(user1);
			if (values1 != null) {
				audit.setAuth_user(values1.getAuth_user());
				audit.setAuth_time(values1.getAuth_time());
			}

			// Set audit reference number and save audit entity
			audit.setAudit_ref_no(auditID);
			bRECON_Audit_Rep.save(audit);

			// Save the brecon core entity
			brecon_core_rep.save(brecon_core_entity);

			// Success message
			msg = "Verified successfully";
		} else {
			// If the role is not "BRC", return an error message
			msg = "Verification not allowed for this user";
		}

		return msg;
	}

	@RequestMapping(value = "breconaddsubmit", method = RequestMethod.POST)
	@ResponseBody
	public String breconaddsubmit(@RequestParam(required = false) Model md, HttpServletRequest rq,
			@ModelAttribute Brecon_core_entity brecon_core_entity, HttpServletRequest req) {
		// Retrieve user information from both 'rq' and 'req'
		String userid = (String) rq.getSession().getAttribute("USERID");
		System.out.println(userid + " userid");

		String msg;

		// Retrieve role ID from the session
		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// Ensure modification logic is executed only if the role is "BRC"
		if ("BRC".equals(roleId)) { // Safe comparison to avoid NullPointerException

			// Set the modify flag and verified user
			brecon_core_entity.setModify_flg("N");
			brecon_core_entity.setEntity_flg("N");

			// Generate the next srlno automatically
			String maxSrlNoStr = brecon_core_rep.getMaxSrlNo(); // Fetch the current max srlno as a string

			// Convert to integer and generate the next srlno
			int nextSrlNo;
			try {
				nextSrlNo = Integer.parseInt(maxSrlNoStr) + 1; // Safely parse to int and increment
			} catch (NumberFormatException e) {
				nextSrlNo = 1; // Fallback if parsing fails (e.g., if no valid numbers are found)
			}

			brecon_core_entity.setSrlno(String.valueOf(nextSrlNo)); // Set the new srlno as a string

			// Generate audit ID and retrieve user details from session
			String auditID = sequence.generateRequestUUId();
			String user1 = (String) req.getSession().getAttribute("USERID");
			String username = (String) req.getSession().getAttribute("USERNAME");

			// Create and populate audit entity
			BRECON_Audit_Entity audit = new BRECON_Audit_Entity();
			Date currentDate = new Date();
			audit.setAudit_date(currentDate);
			audit.setEntry_time(currentDate);
			audit.setEntry_user(user1);
			audit.setFunc_code("ADDED");
			audit.setAudit_table("BRECONSOURCETABLE");
			audit.setAudit_screen("ADD");
			audit.setEvent_id(user1);
			audit.setEvent_name(username);
			audit.setModi_details("Added Successfully");

			// Fetch user profile and add authorization details to the audit entity if
			// available
			UserProfile values1 = userProfileRep.getRole(user1);
			if (values1 != null) {
				audit.setAuth_user(values1.getAuth_user());
				audit.setAuth_time(values1.getAuth_time());
			}

			// Set audit reference number and save audit entity
			audit.setAudit_ref_no(auditID);
			bRECON_Audit_Rep.save(audit);

			// Save the brecon core entity
			brecon_core_rep.save(brecon_core_entity);

			// Success message
			msg = "Added successfully";
		} else {
			// If the role is not "BRC", return an error message
			msg = "Added not allowed for this user";
		}

		return msg;
	}

	@Autowired
	BRECON_DESTINATION_REPO brecon_destination_repo;

	@RequestMapping(value = "clearingsystem", method = RequestMethod.GET)
	public String clearingsystem(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("list", brecon_destination_repo.getDestination());

		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");

		}

		return "Brecon_clearing";
	}

	@RequestMapping(value = "clearingsystemlist", method = RequestMethod.GET)
	public String clearingsystemlist(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		String userid = (String) req.getSession().getAttribute("USERID");
		System.out.println(userid + "userid");
		if (formmode.equals("srlno")) {
			md.addAttribute("formmode", "srlno");
			System.out.println(srlno);
			md.addAttribute("srlno", brecon_destination_repo.getSrlno(srlno));

		} else if (formmode.equals("modify")) {
			md.addAttribute("formmode", "modify");
			System.out.println(srlno);
			md.addAttribute("srlno", brecon_destination_repo.getSrlno(srlno));
		} else if (formmode.equals("verify")) {
			md.addAttribute("formmode", "verify");
			System.out.println(srlno);
			md.addAttribute("srlno", brecon_destination_repo.getSrlno(srlno));
		}

		return "Brecon_clearing_list";
	}

	@RequestMapping(value = "brecondestinationmodifysubmit", method = RequestMethod.POST)
	@ResponseBody
	public String brecondestinationmodifysubmit(@RequestParam(required = false) String srlno, Model md,
			HttpServletRequest rq, @ModelAttribute BRECON_DESTINATION_ENTITY brecon_destination_entity,
			HttpServletRequest request, HttpServletRequest req) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		System.out.println(userid + "userid");

		String msg;

		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// Ensure modification logic is executed only if the role is "BRC"
		if ("BRC".equals(roleId)) { // Safe comparison to avoid NullPointerException

			System.out.println(brecon_destination_entity.getSrlno());
			brecon_destination_entity.setDel_flg("N"); // Set delete flag
			brecon_destination_entity.setModify_flg("Y"); // Set modify flag
			brecon_destination_entity.setEntity_flg("Y");

			brecon_destination_entity.setModify_user(userid);

			// Save the entity without checking for an existing one
			brecon_destination_repo.save(brecon_destination_entity);
			// Generate audit entry
			String auditID = sequence.generateRequestUUId();
			String user1 = (String) req.getSession().getAttribute("USERID");
			String username = (String) req.getSession().getAttribute("USERNAME");

			// Create and populate audit entity
			BRECON_Audit_Entity audit = new BRECON_Audit_Entity();
			Date currentDate = new Date();
			audit.setAudit_date(currentDate);
			audit.setEntry_time(currentDate);
			audit.setEntry_user(user1);
			audit.setFunc_code("MODIFIED");
			audit.setAudit_table("BRECONDESTINATIONTABLE");
			audit.setAudit_screen("MODIFY");
			audit.setEvent_id(user1);
			audit.setEvent_name(username);
			audit.setModi_details("Modified Successfully");

			// Fetch user profile and add authorization details to the audit entity if
			// available
			UserProfile values1 = userProfileRep.getRole(user1);
			if (values1 != null) {
				audit.setAuth_user(values1.getAuth_user());
				audit.setAuth_time(values1.getAuth_time());
			}

			audit.setAudit_ref_no(auditID);

			// Save audit entity
			bRECON_Audit_Rep.save(audit);

			// Success message
			msg = "Record modified successfully";
		} else {
			// If role is not "BRC", modification is not allowed
			msg = "Modification not allowed for this user";
		}

		return msg;
	}

	@RequestMapping(value = "brecondestinationverifysubmit", method = RequestMethod.POST)
	@ResponseBody
	public String brecondestinationverifysubmit(@RequestParam(required = false) String srlno, Model md,
			HttpServletRequest rq, @ModelAttribute BRECON_DESTINATION_ENTITY brecon_destination_entity,
			HttpServletRequest req) {
		String userid = (String) rq.getSession().getAttribute("USERID");
		System.out.println(userid + "userid");

		String msg;

		String roleId = (String) req.getSession().getAttribute("ROLEID");

		// Ensure modification logic is executed only if the role is "BRC"
		if ("BRC".equals(roleId)) { // Safe comparison to avoid NullPointerException
			System.out.println(brecon_destination_entity.getSrlno());

			// Set the modify flag and verified user
			brecon_destination_entity.setModify_flg("N"); // Set modify flag to "N"
			brecon_destination_entity.setVerify_user(userid); // Set verified user

			// Generate audit ID and retrieve user details from session
			String auditID = sequence.generateRequestUUId();
			String user1 = (String) req.getSession().getAttribute("USERID");
			String username = (String) req.getSession().getAttribute("USERNAME");

			// Create and populate audit entity
			BRECON_Audit_Entity audit = new BRECON_Audit_Entity();
			Date currentDate = new Date();
			audit.setAudit_date(currentDate);
			audit.setEntry_time(currentDate);
			audit.setEntry_user(user1);
			audit.setFunc_code("VERIFIED");
			audit.setAudit_table("BRECONDESTINATIONTABLE");
			audit.setAudit_screen("VERIFY");
			audit.setEvent_id(user1);
			audit.setEvent_name(username);
			audit.setModi_details("Verified Successfully");

			// Fetch user profile and add authorization details to the audit entity if
			// available
			UserProfile values1 = userProfileRep.getRole(user1);
			if (values1 != null) {
				audit.setAuth_user(values1.getAuth_user());
				audit.setAuth_time(values1.getAuth_time());
			}

			// Set audit reference number and save audit entity
			audit.setAudit_ref_no(auditID);
			bRECON_Audit_Rep.save(audit);

			// Save the brecon core entity
			brecon_destination_repo.save(brecon_destination_entity);

			// Success message
			msg = "Verified successfully";
		} else {
			// If the role is not "BRC", return an error message
			msg = "Verification not allowed for this user";
		}

		return msg;
	}

	@RequestMapping(value = "MAPPING_RECONSCILLATION", method = RequestMethod.GET)
	public String MAPPING_RECONSCILLATION(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");

			md.addAttribute("listvalues", bRECON_DESTINATION_REPO.getlistvalues());
			/*
			 * md.addAttribute("list", coresystemlistrep.getcoresystemlistdata());
			 * md.addAttribute("list1", bRECON_DESTINATION_REPO.getDestination());
			 */
			// md.addAttribute("chargeback", fYITABLE_REP.getlist());
		} else if (formmode.equals("list2")) {
			md.addAttribute("formmode", "list2");
		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");
		}

		return "MAPPING";
	}

	@RequestMapping(value = "UNMAPPED_RECORDS", method = RequestMethod.GET)
	public String UNMAPPED_RECORDS(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("list", coresystemlistrep.getcoresystemlistdata());
			md.addAttribute("list1", bRECON_DESTINATION_REPO.getDestination());
			// md.addAttribute("chargeback", fYITABLE_REP.getlist());
		}

		return "UNMAPPED_RECORDS";
	}

	@RequestMapping(value = "Automatictransaction", method = RequestMethod.GET)
	public String Automatictransaction(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("listvaluesdatas", bRECON_Common_Table_Rep.getDestinationvalues());
			md.addAttribute("listcoredatas", bRECON_Common_Table_Rep.getDestinationvalues());
		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");
		}
		return "Brecon_Automatictransaction";
	}

	@RequestMapping(value = "Partialtransaction", method = RequestMethod.GET)
	public String Partialtransaction(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");

			md.addAttribute("listvaluesdatas", bRECON_DESTINATION_REPO.getDestination());
			md.addAttribute("listcoredatas", coresystemlistrep.getcoresystemlistdata());
		}

		return "Brecon_Partialtransaction";
	}

	@RequestMapping(value = "Manualtransaction", method = RequestMethod.GET)
	public String Manualtransaction(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("listvaluesdatas", bRECON_DESTINATION_REPO.getDestination());
			md.addAttribute("listcoredatas", coresystemlistrep.getcoresystemlistdata());
		}

		return "Brecon_Manualtransaction";
	}

	@RequestMapping(value = "Tmtfiletransaction", method = RequestMethod.GET)
	public String Tmtfiletransaction(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("chargeback", bRECON_DESTINATION_REPO.getlist());
		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");
		} else if (formmode.equals("list1")) {
			md.addAttribute("formmode", "list1");
		} else if (formmode.equals("upload1")) {
			md.addAttribute("formmode", "upload1");
		} else if (formmode.equals("upload2")) {
			md.addAttribute("formmode", "upload2");
		} else if (formmode.equals("upload3")) {
			md.addAttribute("formmode", "upload3");
		} else if (formmode.equals("upload4")) {
			md.addAttribute("formmode", "upload4");
		}

		return "Brecon_Tmt_File";
	}

	@RequestMapping(value = "Audittrailvalue", method = RequestMethod.GET)
	public String Audittrailvalue(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");
		} else if (formmode.equals("list1")) {
			md.addAttribute("formmode", "list1");
		} else if (formmode.equals("upload1")) {
			md.addAttribute("formmode", "upload1");
		} else if (formmode.equals("upload2")) {
			md.addAttribute("formmode", "upload2");
		} else if (formmode.equals("upload3")) {
			md.addAttribute("formmode", "upload3");
		}

		return "Audittrails";
	}

	@RequestMapping(value = "useractivities", method = { RequestMethod.GET, RequestMethod.POST })
	public String useractivities(@RequestParam(required = false) String formmode, Model model, String cust_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date Fromdate,
			HttpServletRequest request) {
		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use

		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today);
		}

		if (formmode == null || formmode.equals("list")) {
			model.addAttribute("formmode", "list");

			// Fetch the audit list based on the determined date

			model.addAttribute("AuditList", bRECON_Audit_Rep.getauditListLocalvaluesbusiness(fromDateToUse));

		}

		return "AuditTrailValues";
	}

	@RequestMapping(value = "OperationLogsval", method = { RequestMethod.GET, RequestMethod.POST })
	public String OperationLogsval(@RequestParam(required = false) String formmode, Model model, String cust_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date Fromdate,
			HttpServletRequest request) {

		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use
		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today);
		}

		if (formmode == null || formmode.equals("list")) {
			model.addAttribute("formmode", "list");
			model.addAttribute("AuditList", bRECON_Audit_Rep.getauditListLocalvaluesbusiness1(fromDateToUse));
		}

		return "BusinessTrail";
	}

	@RequestMapping(value = "ManualAudittrailvalue", method = RequestMethod.GET)
	public String ManualAudittrailvalue(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");
		} else if (formmode.equals("list1")) {
			md.addAttribute("formmode", "list1");
		} else if (formmode.equals("upload1")) {
			md.addAttribute("formmode", "upload1");
		} else if (formmode.equals("upload2")) {
			md.addAttribute("formmode", "upload2");
		} else if (formmode.equals("upload3")) {
			md.addAttribute("formmode", "upload3");
		}

		return "Manual_Audit_service";
	}

	@RequestMapping(value = "Manualuseractivities", method = { RequestMethod.GET, RequestMethod.POST })
	public String Manualuseractivities(@RequestParam(required = false) String formmode, Model model, String cust_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date Fromdate,
			HttpServletRequest request) {
		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use

		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today);
		}

		if (formmode == null || formmode.equals("list")) {
			model.addAttribute("formmode", "list");

			// Fetch the audit list based on the determined date

			model.addAttribute("AuditList", mANUAL_Audit_Rep.getauditListLocalvaluesbusiness(fromDateToUse));

		}

		return "Manual_User_Activity";
	}

	@RequestMapping(value = "ManualOperationLogsval", method = { RequestMethod.GET, RequestMethod.POST })
	public String ManualOperationLogsval(@RequestParam(required = false) String formmode, Model model, String cust_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date Fromdate,
			HttpServletRequest request) {

		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use
		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today);
		}

		if (formmode == null || formmode.equals("list")) {
			model.addAttribute("formmode", "list");
			model.addAttribute("AuditList", mANUAL_Service_Rep.getauditListLocalvaluesbusiness(fromDateToUse));
		}

		return "Manual_Business_Activity";
	}

	@RequestMapping(value = "Reconsilationdatas", method = RequestMethod.GET)
	public String Reconsilationdatas(@RequestParam(required = false) String formmode, String Offsetval, String Limitval,
			@RequestParam(required = false) String srlno, String keyword, Model md,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date Fromdate,
			HttpServletRequest req) {

		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use

		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today.minusDays(1));
		}

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			// common table
			md.addAttribute("listvaluesdatas", bRECON_Common_Table_Rep.getcommondatavalues(fromDateToUse));
			md.addAttribute("datavalue", fromDateToUse);
			// source table
			md.addAttribute("listcoredatas1", coresystemlistrep.getcoresystemlistvalue(fromDateToUse));
			// destination table
			md.addAttribute("listvaluesdatas1", bRECON_DESTINATION_REPO.getDestinationdatavalues(fromDateToUse));
			// ttum transaction
			List<BRECON_TTUM_TRANSACTION_ENTITY> ttumtransacdatas = BRECON_TTUM_TRANSACTION_REP
					.getttumtransaction(fromDateToUse);
			md.addAttribute("ttumtransacdatas", ttumtransacdatas);

			String totalDebitentries = "0";
			String totalDebitamount = "0";
			String totalCreditentries = "0";
			String totalCreditamount = "0";

			totalDebitentries = bRECON_Common_Table_Rep.getdebitentries(fromDateToUse);
			totalDebitamount = bRECON_Common_Table_Rep.getdebitamount(fromDateToUse);

			totalCreditentries = bRECON_Common_Table_Rep.getcreditentries(fromDateToUse);
			totalCreditamount = bRECON_Common_Table_Rep.getcreditamount(fromDateToUse);

			/// Entries and amount
			md.addAttribute("totalDebitentries", totalDebitentries);
			md.addAttribute("totalDebitamount", totalDebitamount);
			md.addAttribute("totalCreditentries", totalCreditentries);
			md.addAttribute("totalCreditamount", totalCreditamount);

			// popup
			md.addAttribute("listcoredatas21", bRECON_Common_Table_Rep.getDestinationvaluesdatavalue());

		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");
		} else if (formmode.equals("view1")) {
			md.addAttribute("formmode", "view1");
			md.addAttribute("srlno", brecon_core_rep.getSrlno(srlno));
		}
		return "Reconsilationprocess";
	}

	@RequestMapping(value = "Reconsilationdupli", method = RequestMethod.GET)
	public String Reconsilationdupli(@RequestParam(required = false) String formmode, String Offsetval, String Limitval,
			@RequestParam(required = false) String srlno, String keyword, Model md,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date Fromdate,
			HttpServletRequest req) {

		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use

		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today.minusDays(1));
		}

		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("datavalue", fromDateToUse);
			// common table
			md.addAttribute("listvaluesdatas", Brecon_Aani_payment_dup_rep.getDestinationdatavalues(fromDateToUse));

		}
		return "Reconsilationdupli";
	}

	@Autowired
	Kyc_Repo kyc_repo;
	@Autowired
	Kyc_Corprate_Repo kyc_corporate_repo;
	@Autowired
	com.bornfire.xbrl.services.Kyc_Corprate_service Kyc_Corprate_service;
	@Autowired
	IndividualPdfService IndividualPdfService;
	@Autowired
	KYC_Audit_Rep KYC_Audit_Rep;

	@RequestMapping(value = "kyc", method = { RequestMethod.GET, RequestMethod.POST })
	public String KYCHome(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String customerRisk, @RequestParam(required = false) Integer age, // 'age'
																												// here
																												// means
																												// pending
																												// days
			Model md, HttpServletRequest req) {

		String ROLEID = (String) req.getSession().getAttribute("ROLEID");
		String BRANCHCODE = (String) req.getSession().getAttribute("BRANCHCODE");

		formmode = (formmode == null) ? "individual" : formmode;

		boolean isBranchRole = "DCD_BRANCH".equals(ROLEID);
		// Check if both filter parameters are present and not empty
		boolean hasFilters = (customerRisk != null && !customerRisk.isEmpty() && age != null);

		if ("corporate".equals(formmode)) {
			List<Object[]> results = isBranchRole
					? (hasFilters ? kyc_corporate_repo.getBranchDynamicValue(customerRisk, age, BRANCHCODE)
							: kyc_corporate_repo.getBranchList(BRANCHCODE))
					: (hasFilters ? kyc_corporate_repo.getDynamicValue(customerRisk, age)
							: kyc_corporate_repo.getList());
			md.addAttribute("kycData", results);
		} else { // Individual case
			List<Object[]> results = isBranchRole
					? (hasFilters
							? ecddIndividualProfileRepository.findFilteredIndividualsByBranch(customerRisk, age,
									BRANCHCODE)
							: ecddIndividualProfileRepository.findAllIndividualsByBranch(BRANCHCODE))
					: (hasFilters ? ecddIndividualProfileRepository.findFilteredIndividuals(customerRisk, age)
							: ecddIndividualProfileRepository.findAllIndividuals());
			md.addAttribute("reportlist", results);
		}
		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use

		fromDateToUse = java.sql.Date.valueOf(today.minusDays(0));
		md.addAttribute("datavalue", fromDateToUse);
		md.addAttribute("formmode", formmode);
		return "KYC_Home";
	}

	@RequestMapping(value = "/kyc/individual", method = { RequestMethod.GET, RequestMethod.POST })
	public Object kycIndividual(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String custid, @RequestParam(required = false) String srlno,
			@RequestParam(defaultValue = "false") boolean ajax, @ModelAttribute Ecdd_Individual_Profile_Entity data,
			Model model, HttpServletRequest req) throws Exception {

		if (ajax) {
			try {
				boolean success = kyc_individual_service.updateKycData(srlno, data, req);
				if (success) {
					return new ResponseEntity<>("Section saved successfully.", HttpStatus.OK);
				} else {
					return new ResponseEntity<>("Record not found for SRL No: " + srlno, HttpStatus.NOT_FOUND);
				}
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>("Error saving data: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		if ("submit".equals(formmode)) {
			kyc_individual_service.updateKycData(srlno, data, req);
			return "redirect:/kyc/individual?formmode=view&srlno=" + srlno;
		}

		if ("verified".equals(formmode)) {
			kyc_individual_service.verified(custid, req);
		} else if ("download".equals(formmode)) {
			kyc_individual_service.GrtPdf(custid);
		} else if ("view".equals(formmode)) {
			model.addAttribute("formmode", "view");
			Ecdd_Individual_Profile_Entity user_data = ecddIndividualProfileRepository.GetUserBySrlNo(srlno);
			model.addAttribute("user_data", user_data);
		} else if ("modify".equals(formmode)) {
			model.addAttribute("formmode", "modify");
			Ecdd_Individual_Profile_Entity user_data = ecddIndividualProfileRepository.GetUserBySrlNo(srlno);
			model.addAttribute("user_data", user_data);
		} else if ("verify".equals(formmode)) {
			model.addAttribute("formmode", "verify");
			Ecdd_Individual_Profile_Entity user_data = ecddIndividualProfileRepository.GetUserBySrlNo(srlno);
			model.addAttribute("user_data", user_data);
		}

		return "Kyc_individual_ecdd";
	}

	@RequestMapping(value = "/kyc/corporate", method = { RequestMethod.GET, RequestMethod.POST })
	public String kyccorporate(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String uae, @RequestParam(required = false) String custid,
			@RequestParam(required = false) String srl_no, @ModelAttribute EcddCorporateEntity data, Model model,
			HttpServletRequest req, HttpServletResponse response)
			throws FileNotFoundException, JRException, SQLException, Exception {

		System.out.println("KYC Corporate form called");

		String userId = (String) req.getSession().getAttribute("USERID");
		String userName = (String) req.getSession().getAttribute("USERNAME");
		String workClass = (String) req.getSession().getAttribute("WORKCLASS");

		String ajaxParam = req.getParameter("ajax");
		if ("true".equals(ajaxParam)) {
			try {
				Kyc_Corprate_service.updateKycData(srl_no, data, req);

				response.setContentType("application/json");
				response.getWriter().write("{\"status\":\"success\", \"message\":\"Section saved!\"}");
				return null;
			} catch (Exception e) {
				response.setContentType("application/json");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				response.getWriter()
						.write("{\"status\":\"error\", \"message\":\"Save failed: " + e.getMessage() + "\"}");
				return null;
			}
		}

		if ("submit".equalsIgnoreCase(formmode)) {
			Kyc_Corprate_service.updateKycData(srl_no, data, req);
			formmode = "view";
		} else if ("verified".equalsIgnoreCase(formmode)) {
			Kyc_Corprate_service.verified(custid, req);
		} else if ("download".equalsIgnoreCase(formmode)) {
			Kyc_Corprate_service.GrtPdf(srl_no, req);
		}

		List<EcddCorporateEntity> user_data = kyc_corporate_repo.GetUser(srl_no);
		model.addAttribute("userId", userId);
		model.addAttribute("user_data", user_data);
		model.addAttribute("formmode", formmode);

		return "kyc_corporate";
	}

	@PostMapping("/kyc/corporate/verify")
	@ResponseBody
	public String verifyRecord(@RequestParam String custid, HttpServletRequest req) {
		try {
			Kyc_Corprate_service.verified(custid, req);
			return "Verification successful";
		} catch (Exception e) {
			e.printStackTrace();
			return "Verification failed";
		}
	}

	@GetMapping("/kyc/Oneyeartran/Download")
	@ResponseBody
	public ResponseEntity<InputStreamResource> downloadCustomer(@RequestParam String custid, HttpServletRequest req) {

		List<Ecdd_customer_transaction> transactions = Ecdd_customer_transaction_repo.gettrandetails(custid);
		System.out.println("Enter Ecddv Transaction Download");

		if (transactions.isEmpty()) {
			System.out.println("No Transaction available for this customer");
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
			Sheet sheet = workbook.createSheet("Transactions");

			// Style for header
			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);

			// Style for normal cells
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);

			// Header row
			String[] headers = { "CUSTOMER ID", "TRAN DATE", "TRAN ID", "TRAN TYPE", "SUB TRAN TYPE",
					"TRANSACTION INDICATOR", "TRANSACTION AMOUNT", "TRAN PARTICULAR" };

			Row header = sheet.createRow(0);
			for (int i = 0; i < headers.length; i++) {
				Cell cell = header.createCell(i);
				cell.setCellValue(headers[i]);
				cell.setCellStyle(headerStyle);
			}

			// Data rows
			int rowNum = 1;
			for (Ecdd_customer_transaction tx : transactions) {
				Row row = sheet.createRow(rowNum++);
				row.createCell(0).setCellValue(tx.getCustomer_id());
				row.createCell(1).setCellValue(tx.getTran_date().toString());
				row.createCell(2).setCellValue(tx.getTran_id());
				row.createCell(3).setCellValue(tx.getTran_type());
				row.createCell(4).setCellValue(tx.getSub_tran_type());
				row.createCell(5).setCellValue(tx.getTranaction_indicator());
				row.createCell(6).setCellValue(tx.getTransaction_amount().doubleValue());
				row.createCell(7).setCellValue(tx.getTran_particular());

				// Apply border style to all cells in row
				for (int i = 0; i < 8; i++) {
					row.getCell(i).setCellStyle(borderStyle);
				}
			}

			// Auto-size all columns
			for (int i = 0; i < headers.length; i++) {
				sheet.autoSizeColumn(i);
			}

			// Protect the sheet with password (read-only protection)
			sheet.protectSheet("Banktrandetailsbornfire@12345"); // Set your own password here

			// Write to stream
			workbook.write(out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			HttpHeaders headersHttp = new HttpHeaders();
			headersHttp.add("Content-Disposition", "attachment; filename=" + custid + "_transactions.xlsx");

			return ResponseEntity.ok().headers(headersHttp).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(in));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@GetMapping("kyc/Reportstatus/Download")
	@ResponseBody
	public ResponseEntity<InputStreamResource> Downlaodkycstatus(
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date Fromdate,
			HttpServletRequest req) {

		logger.info("Receiving Kyc status download request");
		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use

		if (Fromdate != null) {
			fromDateToUse = Fromdate;
		} else {

			fromDateToUse = java.sql.Date.valueOf(today.minusDays(0));
		}
		List<Ecdd_profile_report_entity> ecddProfileReportList = Ecdd_profile_report_repo
				.getcorporatedata(fromDateToUse);
		List<Ecdd_profile_report_entity> ecddProfileReportListIndv = Ecdd_profile_report_repo
				.getindividualdata(fromDateToUse);

		try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

			Sheet sheet = workbook.createSheet("ECDD Report Status");

			// Create title style
			CellStyle titleStyle = workbook.createCellStyle();
			Font titleFont = workbook.createFont();
			titleFont.setBold(true);
			titleFont.setFontHeightInPoints((short) 14);
			titleStyle.setFont(titleFont);
			titleStyle.setAlignment(HorizontalAlignment.CENTER);
			titleStyle.setVerticalAlignment(VerticalAlignment.CENTER);

			// Create header style
			CellStyle headerStyle = workbook.createCellStyle();
			Font headerFont = workbook.createFont();
			headerFont.setBold(true);
			headerStyle.setFont(headerFont);
			headerStyle.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
			headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
			headerStyle.setBorderBottom(BorderStyle.THIN);
			headerStyle.setBorderTop(BorderStyle.THIN);
			headerStyle.setBorderLeft(BorderStyle.THIN);
			headerStyle.setBorderRight(BorderStyle.THIN);
			headerStyle.setAlignment(HorizontalAlignment.CENTER);

			// Border style for data cells
			CellStyle borderStyle = workbook.createCellStyle();
			borderStyle.setBorderBottom(BorderStyle.THIN);
			borderStyle.setBorderTop(BorderStyle.THIN);
			borderStyle.setBorderLeft(BorderStyle.THIN);
			borderStyle.setBorderRight(BorderStyle.THIN);

			// Corporate Completed:Columns A–J
			Row CorpTitleRow = sheet.createRow(1);
			CorpTitleRow.setHeightInPoints(20);
			Cell CorpBankCell = CorpTitleRow.createCell(0);
			CorpBankCell.setCellValue("CORPORATE ECDD");
			CorpBankCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9));

			// SOL ID (A3:A4)
			Row secondTitleRow = sheet.createRow(2);
			Cell solIdCell = secondTitleRow.createCell(0);
			solIdCell.setCellValue("SOL ID");
			solIdCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 3, 0, 0)); // Merge A3:A4

			// Completed (B3:D3)
			Cell completedCell = secondTitleRow.createCell(1);
			completedCell.setCellValue("Completed");
			completedCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 1, 3));

			// Pending (E3:G3)
			Cell pendingCell = secondTitleRow.createCell(4);
			pendingCell.setCellValue("Pending for Verification");
			pendingCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 4, 6));

			// Unattended (H3:I3)
			Cell UnattendCell = secondTitleRow.createCell(7);
			UnattendCell.setCellValue("Not attended yet");
			UnattendCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(2, 2, 7, 9));

			Row Headerrow = sheet.createRow(3);
			String[] subHeaders = { "HIGH", "MEDIUM", "LOW", "HIGH", "MEDIUM", "LOW", "HIGH", "MEDIUM", "LOW" };
			int col = 1;
			for (String subHeader : subHeaders) {
				Cell cell = Headerrow.createCell(col++);
				cell.setCellValue(subHeader);
				cell.setCellStyle(titleStyle);

			}

			int corporaterownum = 4;

			for (Ecdd_profile_report_entity entityloopdata : ecddProfileReportList) {

				Row row = sheet.createRow(corporaterownum++);

				Cell cell0 = row.createCell(0);
				Cell cell1 = row.createCell(1);
				Cell cell2 = row.createCell(2);
				Cell cell3 = row.createCell(3);
				Cell cell4 = row.createCell(4);
				Cell cell5 = row.createCell(5);
				Cell cell6 = row.createCell(6);
				Cell cell7 = row.createCell(7);
				Cell cell8 = row.createCell(8);
				Cell cell9 = row.createCell(9);

				cell0.setCellValue(entityloopdata.getBranch_code() != null ? entityloopdata.getBranch_code() : "");
				cell1.setCellValue(entityloopdata.getHigh_risk_completed() != null
						? entityloopdata.getHigh_risk_completed().toString()
						: "");
				cell2.setCellValue(entityloopdata.getMedium_risk_completed() != null
						? entityloopdata.getMedium_risk_completed().toString()
						: "");
				cell3.setCellValue(entityloopdata.getLow_risk_completed() != null
						? entityloopdata.getLow_risk_completed().toString()
						: "");
				cell4.setCellValue(
						entityloopdata.getHigh_risk_pending() != null ? entityloopdata.getHigh_risk_pending().toString()
								: "");
				cell5.setCellValue(entityloopdata.getMedium_risk_pending() != null
						? entityloopdata.getMedium_risk_pending().toString()
						: "");
				cell6.setCellValue(
						entityloopdata.getLow_risk_pending() != null ? entityloopdata.getLow_risk_pending().toString()
								: "");
				cell7.setCellValue(entityloopdata.getHigh_risk_non_atended() != null
						? entityloopdata.getHigh_risk_non_atended().toString()
						: "");
				cell8.setCellValue(entityloopdata.getMedium_risk_non_atended() != null
						? entityloopdata.getMedium_risk_non_atended().toString()
						: "");
				cell9.setCellValue(entityloopdata.getLow_risk_non_atended() != null
						? entityloopdata.getLow_risk_non_atended().toString()
						: "");

			}

			// INDIVIDUAL Completed:Columns A–J
			Row indivTitleRow = sheet.createRow(12);
			indivTitleRow.setHeightInPoints(20);
			Cell indivBankCell = indivTitleRow.createCell(0);
			indivBankCell.setCellValue("RETAIL ECDD");
			indivBankCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(12, 12, 0, 9));

			// SOL ID (A13:A14)
			Row corpsecondTitleRow = sheet.createRow(13);
			Cell corpsolIdCell = corpsecondTitleRow.createCell(0);
			corpsolIdCell.setCellValue("SOL ID");
			corpsolIdCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(13, 14, 0, 0)); // Merge A3:A4

			// Completed (B13:D13)
			Cell corpcompletedCell = corpsecondTitleRow.createCell(1);
			corpcompletedCell.setCellValue("Completed");
			corpcompletedCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(13, 13, 1, 3));

			// Pending (E13:G13)
			Cell corppendingCell = corpsecondTitleRow.createCell(4);
			corppendingCell.setCellValue("Pending for Verification");
			corppendingCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(13, 13, 4, 6));

			// Unattended (H13:I13)
			Cell corpUnattendCell = corpsecondTitleRow.createCell(7);
			corpUnattendCell.setCellValue("Not attended yet");
			corpUnattendCell.setCellStyle(titleStyle);
			sheet.addMergedRegion(new CellRangeAddress(13, 13, 7, 9));

			Row corpHeaderrow = sheet.createRow(14);
			String[] corpsubHeaders = { "HIGH", "MEDIUM", "LOW", "HIGH", "MEDIUM", "LOW", "HIGH", "MEDIUM", "LOW" };
			int col1 = 1;
			for (String subHeader : corpsubHeaders) {
				Cell cell = corpHeaderrow.createCell(col1++);
				cell.setCellValue(subHeader);
				cell.setCellStyle(titleStyle);

			}

			int Indvrownum = 15;

			for (Ecdd_profile_report_entity entityloopdata : ecddProfileReportListIndv) {

				Row row = sheet.createRow(Indvrownum++);

				Cell cell0 = row.createCell(0);
				Cell cell1 = row.createCell(1);
				Cell cell2 = row.createCell(2);
				Cell cell3 = row.createCell(3);
				Cell cell4 = row.createCell(4);
				Cell cell5 = row.createCell(5);
				Cell cell6 = row.createCell(6);
				Cell cell7 = row.createCell(7);
				Cell cell8 = row.createCell(8);
				Cell cell9 = row.createCell(9);

				cell0.setCellValue(entityloopdata.getBranch_code() != null ? entityloopdata.getBranch_code() : "");
				cell1.setCellValue(entityloopdata.getHigh_risk_completed() != null
						? entityloopdata.getHigh_risk_completed().toString()
						: "");
				cell2.setCellValue(entityloopdata.getMedium_risk_completed() != null
						? entityloopdata.getMedium_risk_completed().toString()
						: "");
				cell3.setCellValue(entityloopdata.getLow_risk_completed() != null
						? entityloopdata.getLow_risk_completed().toString()
						: "");
				cell4.setCellValue(
						entityloopdata.getHigh_risk_pending() != null ? entityloopdata.getHigh_risk_pending().toString()
								: "");
				cell5.setCellValue(entityloopdata.getMedium_risk_pending() != null
						? entityloopdata.getMedium_risk_pending().toString()
						: "");
				cell6.setCellValue(
						entityloopdata.getLow_risk_pending() != null ? entityloopdata.getLow_risk_pending().toString()
								: "");
				cell7.setCellValue(entityloopdata.getHigh_risk_non_atended() != null
						? entityloopdata.getHigh_risk_non_atended().toString()
						: "");
				cell8.setCellValue(entityloopdata.getMedium_risk_non_atended() != null
						? entityloopdata.getMedium_risk_non_atended().toString()
						: "");
				cell9.setCellValue(entityloopdata.getLow_risk_non_atended() != null
						? entityloopdata.getLow_risk_non_atended().toString()
						: "");

			}

			// Write to output stream
			workbook.write(out);
			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());

			// If you want to return as ResponseEntity (Spring Boot)
			HttpHeaders headersHttp = new HttpHeaders();
			headersHttp.add("Content-Disposition", "attachment; filename=ecdd_completed_report.xlsx");

			return ResponseEntity.ok().headers(headersHttp).contentType(MediaType.APPLICATION_OCTET_STREAM)
					.body(new InputStreamResource(in));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping("/kyc/individual/verify")
	@ResponseBody
	public String verifyindRecord(@RequestParam String custid, HttpServletRequest req) {
		try {
			kyc_individual_service.verified(custid, req);
			return "Verification successful";
		} catch (Exception e) {
			e.printStackTrace();
			return "Verification failed";
		}
	}

	@RequestMapping(value = "kyc/corporate/download", method = RequestMethod.GET)

	@ResponseBody
	public ResponseEntity<InputStreamResource> corporateDownload(HttpServletResponse response, HttpServletRequest req,
			@RequestParam(required = false) String srl_no) throws IOException, SQLException {

		try {

			File repfile = Kyc_Corprate_service.GrtPdf(srl_no, req);

			System.out.println("Generated file: " + repfile.getName());

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

	@RequestMapping(value = "kyc/individual/downloadfn", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> individualDownload(HttpServletResponse response, HttpServletRequest req,
			@RequestParam(required = false) String srlno) throws Exception {

		try {

			File repfile = IndividualPdfService.generateIndividualPdf(srlno, req);

			System.out.println("Generated file: " + repfile.getName());

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

	@RequestMapping(value = "auditlogs", method = RequestMethod.GET)
	public String Auditvalue(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String srlno, String keyword, Model md, HttpServletRequest req) {
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
		} else if (formmode.equals("upload")) {
			md.addAttribute("formmode", "upload");
		} else if (formmode.equals("list1")) {
			md.addAttribute("formmode", "list1");
		} else if (formmode.equals("upload1")) {
			md.addAttribute("formmode", "upload1");
		} else if (formmode.equals("upload2")) {
			md.addAttribute("formmode", "upload2");
		} else if (formmode.equals("upload3")) {
			md.addAttribute("formmode", "upload3");
		}

		return "Audittrailskyc";
	}

	@RequestMapping(value = "useractivity", method = { RequestMethod.GET, RequestMethod.POST })
	public String useractivity(@RequestParam(required = false) String formmode, Model model, String cust_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date Fromdate,
			HttpServletRequest request) {
		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use

		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today);
		}

		if (formmode == null || formmode.equals("list")) {
			model.addAttribute("formmode", "list");

			// Fetch the audit list based on the determined date

			model.addAttribute("AuditList", KYC_Audit_Rep.getauditListLocalvaluesbusiness(fromDateToUse));

		}

		return "AuditTrailValueskyc";
	}

	@RequestMapping(value = "OperationLogs", method = { RequestMethod.GET, RequestMethod.POST })
	public String OperationLogs(@RequestParam(required = false) String formmode, Model model, String cust_id,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date Fromdate,
			HttpServletRequest request) {

		LocalDate today = LocalDate.now(); // Get today's date
		Date fromDateToUse; // Declare a variable for the date to use
		if (Fromdate != null) {
			// If Fromdate has a value, use it
			fromDateToUse = Fromdate;
		} else {
			// If Fromdate has no value, use today's date
			fromDateToUse = java.sql.Date.valueOf(today);
		}

		if (formmode == null || formmode.equals("list")) {
			model.addAttribute("formmode", "list");
			model.addAttribute("AuditList", KYC_Audit_Rep.getauditListLocalvaluesbusiness1(fromDateToUse));
		}

		return "BusinessTrailkyc";
	}

	@RequestMapping(value = "getchanges", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String fetchChanges(@RequestParam(required = false) String audit_ref_no) {

		// Fetch data from the database using the repository
		String changeDetails = KYC_Audit_Rep.getchanges(audit_ref_no); // Example of getting data

		// Process the change details to format as required

		return changeDetails; // Return the formatted changes
	}

	@RequestMapping(value = "custprofile", method = RequestMethod.GET)
	public String custprofile(@RequestParam(required = false) String cif_id,
			@RequestParam(required = false) String acct_no, @RequestParam(required = false) String formmode,
			@RequestParam(required = false) String customerType, @RequestParam(required = false) String tranId, // New
			@RequestParam(required = false) String billId, @RequestParam(required = false) String BG_SRL_NUM, // parameter
			@RequestParam(required = false) String DC_ID, Model md, HttpServletRequest req) {

		formmode = (formmode == null) ? "list" : formmode;
		md.addAttribute("formmode", formmode);
		/*
		 * System.out.println("Received cif_id: " + cif_id);
		 * System.out.println("Received natIdCardNum: " + natIdCardNum);
		 */
		/*
		 * if (tranId != null && !tranId.isEmpty()) { md.addAttribute("gettransaction",
		 * charge_Back_Rep.gettransaction(tranId)); } else { // Handle case when tranId
		 * is null or empty md.addAttribute("gettransaction", new ArrayList<>()); //
		 * Send an empty list to avoid errors }
		 */
		if (formmode == "transdetails") {
			System.out.println("hihihihihihi" + charge_Back_Rep.gettransaction(tranId));
			md.addAttribute("gettransaction", charge_Back_Rep.gettransaction(tranId));

		}
		switch (formmode) {
		case "list":
			md.addAttribute("getlistcust", charge_Back_Rep.getAllcust());
			break;
		case "Dataquality":
			md.addAttribute("getper", charge_Back_Rep.getper(cif_id, acct_no));
			break;
		/*
		 * case "persdetail": md.addAttribute("getpersonal",
		 * charge_Back_Rep.getpersonal(cif_id)); break; case "adrsdetail":
		 * md.addAttribute("getadress", charge_Back_Rep.getadress(cif_id)); break; case
		 * "tradinfinance": md.addAttribute("gettrad", charge_Back_Rep.gettrad(cif_id));
		 * break; case "empdetail": md.addAttribute("getemploye",
		 * charge_Back_Rep.getemploye(cif_id)); break; case "documentdetail":
		 * md.addAttribute("getdocument", charge_Back_Rep.getdocument(cif_id)); break;
		 * case "acctsdetail": md.addAttribute("getaccts2",
		 * charge_Back_Rep.getaccts2(acct_no)); break; case "transdetails":
		 * md.addAttribute("gettransaction", charge_Back_Rep.gettransaction(tranId));
		 * break; case "photodetail": md.addAttribute("getpic",
		 * charge_Back_Rep.getpic(cif_id, acct_no)); break; case "JointHolderdetails":
		 * md.addAttribute("getjoint", charge_Back_Rep.getjoint(cif_id)); break; case
		 * "signdetail": md.addAttribute("getsignature",
		 * charge_Back_Rep.getsignature(cif_id)); break; case "associatedetail":
		 * md.addAttribute("getassociate", charge_Back_Rep.getassociate(cif_id)); break;
		 * case "tradflgdetail": //System.out.println("Received BILL_ID: " + billId);
		 * System.out.println( "CIF_ID: " + cif_id + ", BILL_ID: " + billId);
		 * 
		 * md.addAttribute("gettradEflg", charge_Back_Rep.gettradEflg(cif_id, billId));
		 * break;
		 * 
		 * case "tradflgBankGuarantee": //System.out.println("Received BILL_ID: " +
		 * billId); System.out.println( "CIF_ID: " + cif_id + ", BG_SRL_NUM: " +
		 * BG_SRL_NUM);
		 * 
		 * md.addAttribute("getbankflag", charge_Back_Rep.getbankflag(cif_id,
		 * BG_SRL_NUM)); break; case "tradflgLetterOfCredit":
		 * //System.out.println("Received BILL_ID: " + billId); System.out.println(
		 * "CIF_ID: " + cif_id + ", DC_ID: " + DC_ID);
		 * 
		 * md.addAttribute("getLetofcreditS", charge_Back_Rep.getLetofcreditS(cif_id,
		 * DC_ID)); break;
		 */

		default:
			break;
		}

		return "QA_Customer_profile.html";
	}

	@RequestMapping(value = "acctprofile", method = RequestMethod.GET)
	public String acctprofile(@RequestParam(required = false) String cif_id, String acct_no, String formmode,
			@RequestParam(required = false) String customerType1, @RequestParam(required = false) String tranId,
			@RequestParam(required = false) String billId, Model md, HttpServletRequest req) {

		// Default formmode to "list" if null
		formmode = (formmode == null) ? "list" : formmode;
		md.addAttribute("formmode", formmode);

		// If formmode is "trandetail" and tranId is not null
		if ("trandetail".equals(formmode) && tranId != null) {
			System.out.println("Fetching transactions for tranId: " + tranId);
			md.addAttribute("gettransactions", charge_Back_Rep.gettransactions(tranId));
		}

		else if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			md.addAttribute("getlistacct", charge_Back_Rep.getAllacct());
		} else if (formmode.equals("Dataquality")) {
			md.addAttribute("formmode", "Dataquality");
			md.addAttribute("getper", charge_Back_Rep.getper(cif_id, acct_no));
		} else if (formmode.equals("persdetail")) {
			md.addAttribute("formmode", "persdetail");
			md.addAttribute("getper", charge_Back_Rep.getper(cif_id, acct_no));
		} else if (formmode.equals("adrdetail")) {
			md.addAttribute("formmode", "adrdetail");
			md.addAttribute("getadres", charge_Back_Rep.getadres(cif_id, acct_no));
		} else if (formmode.equals("acctdetail")) {
			md.addAttribute("formmode", "acctdetail");
			/* md.addAttribute("getacct", charge_Back_Rep.getacct(cif_id, acct_no)); */
			md.addAttribute("getaccts1", charge_Back_Rep.getaccts1(acct_no));
		} else if (formmode.equals("trandetail")) {
			md.addAttribute("formmode", "trandetail");
			md.addAttribute("gettransactions", charge_Back_Rep.gettransactions(tranId));
		} else if (formmode.equals("docdetail")) {
			md.addAttribute("formmode", "docdetail");
			md.addAttribute("getdoc", charge_Back_Rep.getdoc(cif_id, acct_no));
		} else if (formmode.equals("tradefinance")) {
			md.addAttribute("formmode", "tradefinance");
			md.addAttribute("gettrade", charge_Back_Rep.gettrade(cif_id, acct_no));
		} else if (formmode.equals("empprofile")) {
			md.addAttribute("formmode", "empprofile");
			md.addAttribute("getemp", charge_Back_Rep.getemp(cif_id, acct_no));
		} else if (formmode.equals("signdetail")) {
			md.addAttribute("formmode", "signdetail");
			md.addAttribute("getsign", charge_Back_Rep.getsign(cif_id, acct_no));
		} else if (formmode.equals("associatedetail")) {
			md.addAttribute("formmode", "associatedetail");
			md.addAttribute("getassociated", charge_Back_Rep.getassociated(cif_id, acct_no));
		} else if (formmode.equals("JointHolderdetails")) {
			md.addAttribute("formmode", "JointHolderdetails");
			md.addAttribute("getjoints", charge_Back_Rep.getjoints(cif_id, acct_no));
		} else if (formmode.equals("photodetails")) {
			md.addAttribute("formmode", "photodetails");
			md.addAttribute("getpics", charge_Back_Rep.getpics(cif_id, acct_no));
		} else if (formmode.equals("tradeflgdetail")) {
			md.addAttribute("formmode", "tradeflgdetail");
			System.out.println("CIF_ID: " + cif_id + ", BILL_ID: " + billId);
			md.addAttribute("gettradEflag", charge_Back_Rep.gettradEflag(cif_id, billId));
		} else if ("corporate".equals(customerType1)) {
			md.addAttribute("formmode", "cifnumber1");
			md.addAttribute("getAll1", charge_Back_Rep.getCorporateCustomers1());
		}

		return "QA_Account_profile.html";
	}

	@RequestMapping(value = "dataQuality", method = RequestMethod.GET)
	public String dataprofile(@RequestParam(required = false) String cif_id,
			@RequestParam(required = false) String acct_no, @RequestParam(required = false) String formmode,
			@RequestParam(required = false) String customerType, @RequestParam(required = false) String tranId, // New
																												// parameter
			Model md, HttpServletRequest req) {

		formmode = (formmode == null) ? "list" : formmode;
		md.addAttribute("formmode", formmode);
		/*
		 * System.out.println("Received cif_id: " + cif_id);
		 * System.out.println("Received natIdCardNum: " + natIdCardNum);
		 */
		if (tranId != null && !tranId.isEmpty()) {
			md.addAttribute("gettransaction", charge_Back_Rep.gettransaction(tranId));
		} else {
			// Handle case when tranId is null or empty
			md.addAttribute("gettransaction", new ArrayList<>()); // Send an empty list to avoid errors
		}

		if (formmode == "transdetails") {
			System.out.println("hihihihihihi" + charge_Back_Rep.gettransaction(tranId));
			md.addAttribute("gettransaction", charge_Back_Rep.gettransaction(tranId));

		}
		switch (formmode) {
		/*
		 * case "list": md.addAttribute("getlistcust", charge_Back_Rep.getAllcust());
		 * break; case "list": md.addAttribute("getlistcust",
		 * charge_Back_Rep.getAllcust()); break;
		 */
		case "Dataquality":
			md.addAttribute("getper", charge_Back_Rep.getper(cif_id, acct_no));
			break;

		case "cifnumber":
			// Fetch and add corporate customers
			List<Object[]> corporateCustomers = charge_Back_Rep.getCorporateCustomers();
			md.addAttribute("getCorporateCustomers", corporateCustomers);
			System.out.println("Corporate Customers: " + corporateCustomers);

			// Fetch and add retail customers
			List<Object[]> retailCustomers = charge_Back_Rep.getRetailCustomers();
			md.addAttribute("getRetailCustomers", retailCustomers);
			System.out.println("Retail Customers: " + retailCustomers);

			// Fetch and add all customers (both corporate and retail)
			List<Object[]> allCustomers = charge_Back_Rep.getAll();
			md.addAttribute("getAll", allCustomers);
			System.out.println("All Customers: " + allCustomers);
			break;

		case "customername":
			md.addAttribute("getName", charge_Back_Rep.getName());
			break;
		case "Dateofbirth":
			md.addAttribute("getcustdob", charge_Back_Rep.getcustdob());
			break;
		case "placeofbirth":
			md.addAttribute("getpob", charge_Back_Rep.getpob());
			break;
		case "PassportExpiry":
			md.addAttribute("getPass", charge_Back_Rep.getPass());
			break;

		case "PassportNo":
			md.addAttribute("getPassno", charge_Back_Rep.getPassno());
			break;
		case "customname":
			md.addAttribute("getName1", charge_Back_Rep.getName1());
			break;

		case "CountryofResidency":
			md.addAttribute("getCountRes", charge_Back_Rep.getCountRes());
			break;
		case "MarkerofEmployed":
			md.addAttribute("getMrkEmp", charge_Back_Rep.getMrkEmp());
			break;
		case "EmployerName":
			md.addAttribute("getEmpname", charge_Back_Rep.getEmpname());
			break;

		case "Residencyaddress":
			md.addAttribute("getResadd", charge_Back_Rep.getResadd());
			break;
		case "Poboxpostalcode":
			md.addAttribute("getpostal", charge_Back_Rep.getpostal());
			break;
		case "Customerriskrating":
			md.addAttribute("getriskrate", charge_Back_Rep.getriskrate());
			break;
		case "Monthlysalary":
			md.addAttribute("getmonth", charge_Back_Rep.getmonth());
			break;
		case "Addmonthsalary":
			md.addAttribute("getAddmonth", charge_Back_Rep.getAddmonth());
			break;
		case "Natinality1":
			md.addAttribute("getnation", charge_Back_Rep.getnation());
			break;
		case "Natinality2":
			md.addAttribute("getnation2", charge_Back_Rep.getnation2());
			break;

		case "DualNatinality":
			md.addAttribute("getDualnation", charge_Back_Rep.getDualnation());
			break;
		case "KYCReviewdate":
			md.addAttribute("getkyc", charge_Back_Rep.getkyc());
			break;
		case "TotalAnnualIncome":
			md.addAttribute("getTotalincome", charge_Back_Rep.getTotalincome());
			break;
		case "SalaryTransferredBank":
			md.addAttribute("getkyc", charge_Back_Rep.getkyc());
			break;
		case "EmiratesID":
			md.addAttribute("getEmid", charge_Back_Rep.getEmid());
			break;
		case "RelatedPartiesFlag":
			md.addAttribute("getEmid", charge_Back_Rep.getEmid());
			break;
		case "EmiratesExpDate":
			md.addAttribute("getEmiExpDate", charge_Back_Rep.getEmiExpDate());
			break;
		case "ResidenceMarker":
			md.addAttribute("getResidmark", charge_Back_Rep.getResidmark());
			break;
		case "CustNameMismatch":
			md.addAttribute("getcustName", charge_Back_Rep.getcustName());
			break;
		case "GENDER":
			md.addAttribute("getGEN", charge_Back_Rep.getGEN());
			break;
		case "Email":
			md.addAttribute("getEmail", charge_Back_Rep.getEmail());
			break;
		case "Birthday":
			md.addAttribute("getBirth", charge_Back_Rep.getBirth());
			break;
		case "CountryTaxResidence":
			md.addAttribute("getCountrytax", charge_Back_Rep.getCountrytax());
			break;
		case "Shortname":
			md.addAttribute("getShortname", charge_Back_Rep.getShortname());
			break;
		case "LoanDetails":
			md.addAttribute("getLoan", charge_Back_Rep.getLoan());
			break;
		case "CreditRating":
			md.addAttribute("getLoan", charge_Back_Rep.getLoan());
			break;
		case "Phone":
			md.addAttribute("getphone", charge_Back_Rep.getphone());
			break;
		case "TaxCompliance":
			md.addAttribute("getLoan", charge_Back_Rep.getLoan());
			break;
		case "RealEstate":
			md.addAttribute("getLoan", charge_Back_Rep.getLoan());
			break;
		case "pep":
			md.addAttribute("getnation2", charge_Back_Rep.getnation2());
			break;
		/*
		 * case "TotalAnnualIncome": md.addAttribute("getkyc",
		 * charge_Back_Rep.getkyc()); break;
		 */
		/*
		 * case "customername": List<Object[]> names = charge_Back_Rep.getname();
		 * md.addAttribute("getname", names); names.forEach(name ->
		 * System.out.println(Arrays.toString(name))); // Log each row break;
		 */

		default:
			break;
		}

		return "DataQuality.html";
	}

	@PostMapping("/kyc/indivdual/verify")
	@ResponseBody
	public String verifyRecord1(@RequestParam String custid, HttpServletRequest req) {
		try {
			kyc_individual_service.verified(custid, req);
			return "Verification successful";
		} catch (Exception e) {
			e.printStackTrace();
			return "Verification failed";
		}
	}

	@Autowired
	private EcddUploadDocumentService documentService;

	@PostMapping("/kyc/individual/upload-document")
	public ResponseEntity<String> uploadDocuments(@RequestParam("files") MultipartFile[] files,
			@RequestParam("srl_no") String srlNo, @RequestParam("customer_id") String customerId,
			@RequestParam("customer_type") String customerType, HttpSession session) {
		if (files.length == 0 || (files.length == 1 && files[0].isEmpty())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select at least one file to upload.");
		}

		try {
			String uploadedBy = (String) session.getAttribute("USERNAME");
			if (uploadedBy == null || uploadedBy.isEmpty()) {
				uploadedBy = "SYSTEM"; // Fallback
			}
			documentService.saveDocuments(files, srlNo, customerId, customerType, uploadedBy);

			return ResponseEntity.ok("Documents uploaded successfully.");

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Could not upload files: " + e.getMessage());
		}
	}

	@GetMapping("/kyc/individual/list-documents")
	@ResponseBody // Sends data as JSON
	public List<EcddCustomerDocumentsEntity> listDocuments(@RequestParam("customerId") String customerId) {
		return documentService.getDocumentList(customerId);
	}

	@GetMapping("/kyc/individual/download-doc/{docId}")
	public ResponseEntity<byte[]> downloadDocument(@PathVariable Long docId) {
		try {
			EcddCustomerDocumentsEntity doc = documentService.getDocumentForDownload(docId);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(doc.getMimeType()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getDocumentName() + "\"")
					.body(doc.getDocumentContent());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}

	@PostMapping("/kyc/corporate/upload-document")
	public ResponseEntity<String> uploadcorpDocuments(@RequestParam("files") MultipartFile[] files,
			@RequestParam("srl_no") String srlNo, @RequestParam("customer_id") String customerId,
			@RequestParam("customer_type") String customerType, HttpSession session) {
		if (files.length == 0 || (files.length == 1 && files[0].isEmpty())) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Please select at least one file to upload.");
		}
		try {
			String uploadedBy = (String) session.getAttribute("USERNAME");
			if (uploadedBy == null || uploadedBy.isEmpty())
				uploadedBy = "SYSTEM";

			documentService.saveDocuments(files, srlNo, customerId, customerType, uploadedBy);
			return ResponseEntity.ok("Documents uploaded successfully.");
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Could not upload files: " + e.getMessage());
		}
	}

	@GetMapping("/kyc/corporate/list-documents")
	@ResponseBody
	public List<EcddCustomerDocumentsEntity> listcorpDocuments(@RequestParam("customerId") String customerId) {
		return documentService.getDocumentList(customerId);
	}

	@GetMapping("/kyc/corporate/download-doc/{docId}")
	public ResponseEntity<byte[]> downloadcorpDocument(@PathVariable Long docId) {
		try {
			EcddCustomerDocumentsEntity doc = documentService.getDocumentForDownload(docId);
			return ResponseEntity.ok().contentType(MediaType.parseMediaType(doc.getMimeType()))
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getDocumentName() + "\"")
					.body(doc.getDocumentContent());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
	}
	
	
	@RequestMapping(value = "XBRLAccountInquiry", method = { RequestMethod.GET, RequestMethod.POST })
	public String XBRLAccountInquiry(@RequestParam(required = false) String formmode,
			@RequestParam(value = "account",required = false) String Account,
			@RequestParam(value = "fd",required = false) String fromdate,
			@RequestParam(value = "td",required = false) String todate,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(value = "size", required = false) Optional<Integer> size, Model md, HttpServletRequest req) {

		int currentPage = page.orElse(0);
		int pageSize = size.orElse(Integer.parseInt(pagesize));
		String userid1 = (String) req.getSession().getAttribute("USERID");

		System.out.println("page" + currentPage);
		System.out.println("page" + pageSize);
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		// md.addAttribute("AMLRoleMenu", AccessRoleService.getRoleMenu(roleId));
		if (formmode == null || formmode.equals("cuslist")) {
			md.addAttribute("menu", "Report Generator");
			md.addAttribute("userProfile", loginServices.getUser(userid1));
			md.addAttribute("formmode", "cuslist"); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("custParameter", etlServices.getcustdata());
		}
		else if (formmode.equals("list")) {
			md.addAttribute("menu", "Report Generator");
			md.addAttribute("userProfile", loginServices.getUser(userid1));
			md.addAttribute("formmode", "list"); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("repParameter", generalMasterTbRep.findAllCustom(Account));
		}else if (formmode.equals("account")) {
			md.addAttribute("menu", "Report Generator");
			md.addAttribute("userProfile", loginServices.getUser(userid1));
			md.addAttribute("formmode", formmode); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("repParameter", generalMasterTbRep.findAllCustomind(Account));
			md.addAttribute("tranInquiry", transactionInquiryRep.findAllCustomind(Account));
		
			DateFormat dateFormat1 = new SimpleDateFormat("dd/MM/yyyy");
			 DateTimeFormatter formatters = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			String reportDate = dateFormat1.format(new Date());
			 LocalDate currentDate = LocalDate.now();
			 
		        // Get the start date of the current month
		        LocalDate startDate = currentDate.withDayOfMonth(1);
		        String StreportDate = startDate.format(formatters);
		        // Get the last date of the current month
		        YearMonth yearMonth = YearMonth.from(currentDate);
		        LocalDate endDate = yearMonth.atEndOfMonth();
		    	md.addAttribute("opr_datefd",StreportDate);
				md.addAttribute("opr_datetd",reportDate);
		        // Print the dates
		        System.out.println("Start Date of the Current Month: " + startDate);
		        System.out.println("End Date of the Current Month: " + endDate);
		}else if (formmode.equals("accountlist")) {
			md.addAttribute("menu", "Report Generator");
			md.addAttribute("userProfile", loginServices.getUser(userid1));
			md.addAttribute("formmode", formmode); // to set which form - valid values are "edit" , "add" & "list"
			md.addAttribute("repParameter", generalMasterTbRep.findAllCustomind(Account));
			md.addAttribute("tranInquiry", transactionInquiryRep.findAllCustominddate(Account,fromdate,todate));
			md.addAttribute("opr_datefd",fromdate);
			md.addAttribute("opr_datetd",todate);
			
		}

		return "CustomerInquiry";
	}
}
