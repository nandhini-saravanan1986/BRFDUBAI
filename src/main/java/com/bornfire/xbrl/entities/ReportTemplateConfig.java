package com.bornfire.xbrl.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BRF_TEMPLATE_CONFIG")
public class ReportTemplateConfig {

	@Id
	@Column(name = "REPORT_CODE", length = 50)
	private String reportCode;

	@Column(name = "HEADER_START_ROW")
	private Integer headerStartRow;

	@Column(name = "FIRST_DATA_ROW")
	private Integer firstDataRow;

	@Column(name = "FIRST_STOP_ROW")
	private Integer firstStopRow;

	@Column(name = "DATA_START_COL")
	private Integer dataStartCol;

	@Column(name = "SHEET_INDEX")
	private Integer sheetIndex;

	public Integer getSheetIndex() {
		return sheetIndex;
	}

	public void setSheetIndex(Integer sheetIndex) {
		this.sheetIndex = sheetIndex;
	}

	public String getReportCode() {
		return reportCode;
	}

	public void setReportCode(String reportCode) {
		this.reportCode = reportCode;
	}

	public Integer getHeaderStartRow() {
		return headerStartRow;
	}

	public void setHeaderStartRow(Integer headerStartRow) {
		this.headerStartRow = headerStartRow;
	}

	public Integer getFirstDataRow() {
		return firstDataRow;
	}

	public void setFirstDataRow(Integer firstDataRow) {
		this.firstDataRow = firstDataRow;
	}

	public Integer getFirstStopRow() {
		return firstStopRow;
	}

	public void setFirstStopRow(Integer firstStopRow) {
		this.firstStopRow = firstStopRow;
	}

	public Integer getDataStartCol() {
		return dataStartCol;
	}

	public void setDataStartCol(Integer dataStartCol) {
		this.dataStartCol = dataStartCol;
	}
}