package com.bornfire.xbrl.entities.BRBS;


import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

import com.bornfire.xbrl.entities.BRFDETAILID;


@Entity
@IdClass(BRFDETAILID.class)
@Table(name="BRF2_DETAILTABLE")
public class BRF2_DETAIL_ENTITY {
	
	private String	cust_id;
	@Id
	private String	foracid;
	private BigDecimal	act_balance_amt_lc;
	private BigDecimal	eab_lc;
	private String	acct_name;
	private String	acct_crncy_code;
	private String	gl_code;
	private String	gl_sub_head_code;
	private String	gl_sub_head_desc;
	private String	country_of_incorp;
	private String	cust_type;
	private String	schm_code;
	private String	schm_type;
	private String	sol_id;
	private String	acid;
	private String	segment;
	private String	sub_segment;
	private BigDecimal	sector;
	private String	sub_sector;
	private String	sector_code;
	private String	group_id;
	private String	constitution_code;
	private String	country;
	private String	legal_entity_type;
	private String	constitution_desc;
	private String	purpose_of_advn;
	private BigDecimal	hni_networth;
	private String	turnover;
	private String	bacid;
	private String	report_name_1;
	private String	report_label_1;
	private String	report_addl_criteria_1;
	private String	report_addl_criteria_2;
	private String	report_addl_criteria_3;
	private String	create_user;
	private Date	create_time;
	private String	modify_user;
	private Date	modify_time;
	private String	verify_user;
	private Date	verify_time;
	private Character	entity_flg;
	private Character	modify_flg;
	private Character	del_flg;
	private Character	nre_status;
	@Id
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date	report_date;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date	maturity_date;
	private String	gender;
	private String	version;
	private String	remarks;
	private Character	nre_flg;
	public String getCust_id() {
		return cust_id;
	}
	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}
	public String getForacid() {
		return foracid;
	}
	public void setForacid(String foracid) {
		this.foracid = foracid;
	}
	public BigDecimal getAct_balance_amt_lc() {
		return act_balance_amt_lc;
	}
	public void setAct_balance_amt_lc(BigDecimal act_balance_amt_lc) {
		this.act_balance_amt_lc = act_balance_amt_lc;
	}
	public BigDecimal getEab_lc() {
		return eab_lc;
	}
	public void setEab_lc(BigDecimal eab_lc) {
		this.eab_lc = eab_lc;
	}
	public String getAcct_name() {
		return acct_name;
	}
	public void setAcct_name(String acct_name) {
		this.acct_name = acct_name;
	}
	public String getAcct_crncy_code() {
		return acct_crncy_code;
	}
	public void setAcct_crncy_code(String acct_crncy_code) {
		this.acct_crncy_code = acct_crncy_code;
	}
	public String getGl_code() {
		return gl_code;
	}
	public void setGl_code(String gl_code) {
		this.gl_code = gl_code;
	}
	public String getGl_sub_head_code() {
		return gl_sub_head_code;
	}
	public void setGl_sub_head_code(String gl_sub_head_code) {
		this.gl_sub_head_code = gl_sub_head_code;
	}
	public String getGl_sub_head_desc() {
		return gl_sub_head_desc;
	}
	public void setGl_sub_head_desc(String gl_sub_head_desc) {
		this.gl_sub_head_desc = gl_sub_head_desc;
	}
	public String getCountry_of_incorp() {
		return country_of_incorp;
	}
	public void setCountry_of_incorp(String country_of_incorp) {
		this.country_of_incorp = country_of_incorp;
	}
	public String getCust_type() {
		return cust_type;
	}
	public void setCust_type(String cust_type) {
		this.cust_type = cust_type;
	}
	public String getSchm_code() {
		return schm_code;
	}
	public void setSchm_code(String schm_code) {
		this.schm_code = schm_code;
	}
	public String getSchm_type() {
		return schm_type;
	}
	public void setSchm_type(String schm_type) {
		this.schm_type = schm_type;
	}
	public String getSol_id() {
		return sol_id;
	}
	public void setSol_id(String sol_id) {
		this.sol_id = sol_id;
	}
	public String getAcid() {
		return acid;
	}
	public void setAcid(String acid) {
		this.acid = acid;
	}
	public String getSegment() {
		return segment;
	}
	public void setSegment(String segment) {
		this.segment = segment;
	}
	public String getSub_segment() {
		return sub_segment;
	}
	public void setSub_segment(String sub_segment) {
		this.sub_segment = sub_segment;
	}
	public BigDecimal getSector() {
		return sector;
	}
	public void setSector(BigDecimal sector) {
		this.sector = sector;
	}
	public String getSub_sector() {
		return sub_sector;
	}
	public void setSub_sector(String sub_sector) {
		this.sub_sector = sub_sector;
	}
	public String getSector_code() {
		return sector_code;
	}
	public void setSector_code(String sector_code) {
		this.sector_code = sector_code;
	}
	public String getGroup_id() {
		return group_id;
	}
	public void setGroup_id(String group_id) {
		this.group_id = group_id;
	}
	public String getConstitution_code() {
		return constitution_code;
	}
	public void setConstitution_code(String constitution_code) {
		this.constitution_code = constitution_code;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getLegal_entity_type() {
		return legal_entity_type;
	}
	public void setLegal_entity_type(String legal_entity_type) {
		this.legal_entity_type = legal_entity_type;
	}
	public String getConstitution_desc() {
		return constitution_desc;
	}
	public void setConstitution_desc(String constitution_desc) {
		this.constitution_desc = constitution_desc;
	}
	public String getPurpose_of_advn() {
		return purpose_of_advn;
	}
	public void setPurpose_of_advn(String purpose_of_advn) {
		this.purpose_of_advn = purpose_of_advn;
	}
	public BigDecimal getHni_networth() {
		return hni_networth;
	}
	public void setHni_networth(BigDecimal hni_networth) {
		this.hni_networth = hni_networth;
	}
	public String getTurnover() {
		return turnover;
	}
	public void setTurnover(String turnover) {
		this.turnover = turnover;
	}
	public String getBacid() {
		return bacid;
	}
	public void setBacid(String bacid) {
		this.bacid = bacid;
	}
	public String getReport_name_1() {
		return report_name_1;
	}
	public void setReport_name_1(String report_name_1) {
		this.report_name_1 = report_name_1;
	}
	public String getReport_label_1() {
		return report_label_1;
	}
	public void setReport_label_1(String report_label_1) {
		this.report_label_1 = report_label_1;
	}
	public String getReport_addl_criteria_1() {
		return report_addl_criteria_1;
	}
	public void setReport_addl_criteria_1(String report_addl_criteria_1) {
		this.report_addl_criteria_1 = report_addl_criteria_1;
	}
	public String getReport_addl_criteria_2() {
		return report_addl_criteria_2;
	}
	public void setReport_addl_criteria_2(String report_addl_criteria_2) {
		this.report_addl_criteria_2 = report_addl_criteria_2;
	}
	public String getReport_addl_criteria_3() {
		return report_addl_criteria_3;
	}
	public void setReport_addl_criteria_3(String report_addl_criteria_3) {
		this.report_addl_criteria_3 = report_addl_criteria_3;
	}
	public String getCreate_user() {
		return create_user;
	}
	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public String getModify_user() {
		return modify_user;
	}
	public void setModify_user(String modify_user) {
		this.modify_user = modify_user;
	}
	public Date getModify_time() {
		return modify_time;
	}
	public void setModify_time(Date modify_time) {
		this.modify_time = modify_time;
	}
	public String getVerify_user() {
		return verify_user;
	}
	public void setVerify_user(String verify_user) {
		this.verify_user = verify_user;
	}
	public Date getVerify_time() {
		return verify_time;
	}
	public void setVerify_time(Date verify_time) {
		this.verify_time = verify_time;
	}
	public Character getEntity_flg() {
		return entity_flg;
	}
	public void setEntity_flg(Character entity_flg) {
		this.entity_flg = entity_flg;
	}
	public Character getModify_flg() {
		return modify_flg;
	}
	public void setModify_flg(Character modify_flg) {
		this.modify_flg = modify_flg;
	}
	public Character getDel_flg() {
		return del_flg;
	}
	public void setDel_flg(Character del_flg) {
		this.del_flg = del_flg;
	}
	public Character getNre_status() {
		return nre_status;
	}
	public void setNre_status(Character nre_status) {
		this.nre_status = nre_status;
	}
	public Date getReport_date() {
		return report_date;
	}
	public void setReport_date(Date report_date) {
		this.report_date = report_date;
	}
	public Date getMaturity_date() {
		return maturity_date;
	}
	public void setMaturity_date(Date maturity_date) {
		this.maturity_date = maturity_date;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Character getNre_flg() {
		return nre_flg;
	}
	public void setNre_flg(Character nre_flg) {
		this.nre_flg = nre_flg;
	}
	public BRF2_DETAIL_ENTITY(String cust_id, String foracid, BigDecimal act_balance_amt_lc, BigDecimal eab_lc,
			String acct_name, String acct_crncy_code, String gl_code, String gl_sub_head_code, String gl_sub_head_desc,
			String country_of_incorp, String cust_type, String schm_code, String schm_type, String sol_id, String acid,
			String segment, String sub_segment, BigDecimal sector, String sub_sector, String sector_code,
			String group_id, String constitution_code, String country, String legal_entity_type,
			String constitution_desc, String purpose_of_advn, BigDecimal hni_networth, String turnover, String bacid,
			String report_name_1, String report_label_1, String report_addl_criteria_1, String report_addl_criteria_2,
			String report_addl_criteria_3, String create_user, Date create_time, String modify_user, Date modify_time,
			String verify_user, Date verify_time, Character entity_flg, Character modify_flg, Character del_flg,
			Character nre_status, Date report_date, Date maturity_date, String gender, String version, String remarks,
			Character nre_flg) {
		super();
		this.cust_id = cust_id;
		this.foracid = foracid;
		this.act_balance_amt_lc = act_balance_amt_lc;
		this.eab_lc = eab_lc;
		this.acct_name = acct_name;
		this.acct_crncy_code = acct_crncy_code;
		this.gl_code = gl_code;
		this.gl_sub_head_code = gl_sub_head_code;
		this.gl_sub_head_desc = gl_sub_head_desc;
		this.country_of_incorp = country_of_incorp;
		this.cust_type = cust_type;
		this.schm_code = schm_code;
		this.schm_type = schm_type;
		this.sol_id = sol_id;
		this.acid = acid;
		this.segment = segment;
		this.sub_segment = sub_segment;
		this.sector = sector;
		this.sub_sector = sub_sector;
		this.sector_code = sector_code;
		this.group_id = group_id;
		this.constitution_code = constitution_code;
		this.country = country;
		this.legal_entity_type = legal_entity_type;
		this.constitution_desc = constitution_desc;
		this.purpose_of_advn = purpose_of_advn;
		this.hni_networth = hni_networth;
		this.turnover = turnover;
		this.bacid = bacid;
		this.report_name_1 = report_name_1;
		this.report_label_1 = report_label_1;
		this.report_addl_criteria_1 = report_addl_criteria_1;
		this.report_addl_criteria_2 = report_addl_criteria_2;
		this.report_addl_criteria_3 = report_addl_criteria_3;
		this.create_user = create_user;
		this.create_time = create_time;
		this.modify_user = modify_user;
		this.modify_time = modify_time;
		this.verify_user = verify_user;
		this.verify_time = verify_time;
		this.entity_flg = entity_flg;
		this.modify_flg = modify_flg;
		this.del_flg = del_flg;
		this.nre_status = nre_status;
		this.report_date = report_date;
		this.maturity_date = maturity_date;
		this.gender = gender;
		this.version = version;
		this.remarks = remarks;
		this.nre_flg = nre_flg;
	}
	public BRF2_DETAIL_ENTITY() {
		super();
		
	}

	
	
	
}
