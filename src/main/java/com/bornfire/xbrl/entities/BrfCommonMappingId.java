package com.bornfire.xbrl.entities;

import java.io.Serializable;
import java.util.Objects;

public class BrfCommonMappingId implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountIdBacid;
    private String reportCode;
    private String rowId;

    public BrfCommonMappingId() {}

    public BrfCommonMappingId(String accountIdBacid, String reportCode, String rowId) {
        this.accountIdBacid = accountIdBacid;
        this.reportCode     = reportCode;
        this.rowId          = rowId;
    }

    public String getAccountIdBacid()             { return accountIdBacid; }
    public void   setAccountIdBacid(String v)     { this.accountIdBacid = v; }
    public String getReportCode()                 { return reportCode; }
    public void   setReportCode(String v)         { this.reportCode = v; }
    public String getRowId()                      { return rowId; }
    public void   setRowId(String v)              { this.rowId = v; }

    // REQUIRED by JPA for composite key equality checks
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BrfCommonMappingId)) return false;
        BrfCommonMappingId that = (BrfCommonMappingId) o;
        return Objects.equals(accountIdBacid, that.accountIdBacid)
            && Objects.equals(reportCode,     that.reportCode)
            && Objects.equals(rowId,          that.rowId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountIdBacid, reportCode, rowId);
    }
}