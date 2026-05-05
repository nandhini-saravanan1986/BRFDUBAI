package com.bornfire.xbrl.entities;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

public class BRFDETAILID implements Serializable {

	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date report_date;
	private String foracid;

	public Date getReport_date() {
		return report_date;
	}

	public void setReport_date(Date report_date) {
		this.report_date = report_date;
	}

	public String getForacid() {
		return foracid;
	}

	public void setForacid(String foracid) {
		this.foracid = foracid;
	}

}
