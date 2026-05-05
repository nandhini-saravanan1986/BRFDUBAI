package com.bornfire.xbrl.entities.BRBS;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BBRF_REPORT_VALIDATION_TABLE")
public class BRFValidations {

	@Id
	private String srl_no;
	private String val_det;
	private String val_tables;
	private String cur_status;
	private String pre_status;
	private String rpt_code;
	private String remarks2;

	private String SRC_COLUMN;
	private String SRC_TABLE;
	private String DEST_COLUMN;
	private String DEST_TABLE;
	private String SRC_FORMULA;
	private String DEST_FORMULA;

	public String getSRC_COLUMN() {
		return SRC_COLUMN;
	}

	public void setSRC_COLUMN(String sRC_COLUMN) {
		SRC_COLUMN = sRC_COLUMN;
	}

	public String getSRC_TABLE() {
		return SRC_TABLE;
	}

	public void setSRC_TABLE(String sRC_TABLE) {
		SRC_TABLE = sRC_TABLE;
	}

	public String getDEST_COLUMN() {
		return DEST_COLUMN;
	}

	public void setDEST_COLUMN(String dEST_COLUMN) {
		DEST_COLUMN = dEST_COLUMN;
	}

	public String getDEST_TABLE() {
		return DEST_TABLE;
	}

	public void setDEST_TABLE(String dEST_TABLE) {
		DEST_TABLE = dEST_TABLE;
	}

	public String getSRC_FORMULA() {
		return SRC_FORMULA;
	}

	public void setSRC_FORMULA(String sRC_FORMULA) {
		SRC_FORMULA = sRC_FORMULA;
	}

	public String getDEST_FORMULA() {
		return DEST_FORMULA;
	}

	public void setDEST_FORMULA(String dEST_FORMULA) {
		DEST_FORMULA = dEST_FORMULA;
	}

	public String getSrl_no() {
		return srl_no;
	}

	public String getVal_det() {
		return val_det;
	}

	public String getVal_tables() {
		return val_tables;
	}

	public void setSrl_no(String srl_no) {
		this.srl_no = srl_no;
	}

	public void setVal_det(String val_det) {
		this.val_det = val_det;
	}

	public void setVal_tables(String val_tables) {
		this.val_tables = val_tables;
	}

	public String getCur_status() {
		return cur_status;
	}

	public String getPre_status() {
		return pre_status;
	}

	public void setCur_status(String cur_status) {
		this.cur_status = cur_status;
	}

	public void setPre_status(String pre_status) {
		this.pre_status = pre_status;
	}

	public String getRpt_code() {
		return rpt_code;
	}

	public void setRpt_code(String rpt_code) {
		this.rpt_code = rpt_code;
	}

	public String getRemarks2() {
		return remarks2;
	}

	public void setRemarks2(String remarks2) {
		this.remarks2 = remarks2;
	}

	public BRFValidations() {
		super();
		// TODO Auto-generated constructor stub
	}

}
