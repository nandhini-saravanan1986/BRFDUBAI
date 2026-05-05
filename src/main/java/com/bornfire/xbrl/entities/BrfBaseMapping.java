package com.bornfire.xbrl.entities;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "BRF_BASE_MAPPING_TABLE")
public class BrfBaseMapping {

    @Id
    @Column(name = "ACCOUNT_ID_BACID", length = 50)
    private String accountIdBacid;

    @Column(name = "GL_HEAD",              length = 50)
    private String glHead;

    @Column(name = "GL_SUBHEAD_CODE",      length = 50)
    private String glSubheadCode;

    @Column(name = "ACCOUNT_DESCRIPTION",  length = 200)
    private String accountDescription;

    @Column(name = "CURRENCY",             length = 50)
    private String currency;

    @Column(name = "DATA_TYPE",            length = 50)
    private String dataType;

    @Column(name = "ROW_ID",               length = 50)
    private String rowId;

    @Column(name = "COLUMN_ID",            length = 50)
    private String columnId;

    @Column(name = "REPORT_CODE",          length = 100)
    private String reportCode;

    @Column(name = "REPORT_DESC",          length = 100)
    private String reportDesc;

    @Column(name = "REPORT_VERSION",       length = 100)
    private String reportVersion;

    @Column(name = "REPORT_FREQUENCY",     length = 100)
    private String reportFrequency;

    @Column(name = "REPORT_ADDL_CRITERIA_1", length = 100)
    private String reportAddlCriteria1;

    @Column(name = "REPORT_ADDL_CRITERIA_2", length = 100)
    private String reportAddlCriteria2;

    @Column(name = "REPORT_ADDL_CRITERIA_3", length = 100)
    private String reportAddlCriteria3;

    @Column(name = "ACCOUNT_BALANCE_LC",   length = 50)
    private String accountBalanceLc;

    @Column(name = "ENTITY_FLG",           length = 1)
    private String entityFlg;

    @Column(name = "AUTH_FLG",             length = 1)
    private String authFlg;

    @Column(name = "MODIFY_FLG",           length = 1)
    private String modifyFlg;

    @Column(name = "DEL_FLG",              length = 1)
    private String delFlg;

    @Column(name = "ENTRY_USER",           length = 20)
    private String entryUser;

    @Column(name = "MODIFY_USER",          length = 20)
    private String modifyUser;

    @Column(name = "AUTH_USER",            length = 20)
    private String authUser;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENTRY_TIME")
    private Date entryTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFY_TIME")
    private Date modifyTime;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "AUTH_TIME")
    private Date authTime;

    @Temporal(TemporalType.DATE)
    @Column(name = "REPORT_DATE")
    private Date reportDate;

    // ── Getters & Setters ────────────────────────────────────────────────────
    public String getAccountIdBacid()               { return accountIdBacid; }
    public void   setAccountIdBacid(String v)       { this.accountIdBacid = v; }
    public String getGlHead()                       { return glHead; }
    public void   setGlHead(String v)               { this.glHead = v; }
    public String getGlSubheadCode()                { return glSubheadCode; }
    public void   setGlSubheadCode(String v)        { this.glSubheadCode = v; }
    public String getAccountDescription()           { return accountDescription; }
    public void   setAccountDescription(String v)   { this.accountDescription = v; }
    public String getCurrency()                     { return currency; }
    public void   setCurrency(String v)             { this.currency = v; }
    public String getDataType()                     { return dataType; }
    public void   setDataType(String v)             { this.dataType = v; }
    public String getRowId()                        { return rowId; }
    public void   setRowId(String v)                { this.rowId = v; }
    public String getColumnId()                     { return columnId; }
    public void   setColumnId(String v)             { this.columnId = v; }
    public String getReportCode()                   { return reportCode; }
    public void   setReportCode(String v)           { this.reportCode = v; }
    public String getReportDesc()                   { return reportDesc; }
    public void   setReportDesc(String v)           { this.reportDesc = v; }
    public String getReportVersion()                { return reportVersion; }
    public void   setReportVersion(String v)        { this.reportVersion = v; }
    public String getReportFrequency()              { return reportFrequency; }
    public void   setReportFrequency(String v)      { this.reportFrequency = v; }
    public String getReportAddlCriteria1()          { return reportAddlCriteria1; }
    public void   setReportAddlCriteria1(String v)  { this.reportAddlCriteria1 = v; }
    public String getReportAddlCriteria2()          { return reportAddlCriteria2; }
    public void   setReportAddlCriteria2(String v)  { this.reportAddlCriteria2 = v; }
    public String getReportAddlCriteria3()          { return reportAddlCriteria3; }
    public void   setReportAddlCriteria3(String v)  { this.reportAddlCriteria3 = v; }
    public String getAccountBalanceLc()             { return accountBalanceLc; }
    public void   setAccountBalanceLc(String v)     { this.accountBalanceLc = v; }
    public String getEntityFlg()                    { return entityFlg; }
    public void   setEntityFlg(String v)            { this.entityFlg = v; }
    public String getAuthFlg()                      { return authFlg; }
    public void   setAuthFlg(String v)              { this.authFlg = v; }
    public String getModifyFlg()                    { return modifyFlg; }
    public void   setModifyFlg(String v)            { this.modifyFlg = v; }
    public String getDelFlg()                       { return delFlg; }
    public void   setDelFlg(String v)               { this.delFlg = v; }
    public String getEntryUser()                    { return entryUser; }
    public void   setEntryUser(String v)            { this.entryUser = v; }
    public String getModifyUser()                   { return modifyUser; }
    public void   setModifyUser(String v)           { this.modifyUser = v; }
    public String getAuthUser()                     { return authUser; }
    public void   setAuthUser(String v)             { this.authUser = v; }
    public Date   getEntryTime()                    { return entryTime; }
    public void   setEntryTime(Date v)              { this.entryTime = v; }
    public Date   getModifyTime()                   { return modifyTime; }
    public void   setModifyTime(Date v)             { this.modifyTime = v; }
    public Date   getAuthTime()                     { return authTime; }
    public void   setAuthTime(Date v)               { this.authTime = v; }
    public Date   getReportDate()                   { return reportDate; }
    public void   setReportDate(Date v)             { this.reportDate = v; }
}