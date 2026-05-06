package com.bornfire.xbrl.services;

import com.bornfire.xbrl.entities.BrfBaseMapping;
import com.bornfire.xbrl.entities.BrfCommonMapping;
import com.bornfire.xbrl.entities.BrfBaseMappingRepository;
import com.bornfire.xbrl.entities.BrfCommonMappingRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


@Service
public class MappingAccountService {

    private final BrfCommonMappingRepository commonMappingRepo;
    private final BrfBaseMappingRepository   baseMappingRepo;

    public MappingAccountService(BrfCommonMappingRepository commonMappingRepo,
                                 BrfBaseMappingRepository   baseMappingRepo) {
        this.commonMappingRepo = commonMappingRepo;
        this.baseMappingRepo   = baseMappingRepo;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // MAPPED ACCOUNTS
    // Returns rows from BRF_COMMON_MAPPING_TABLE for the given reportCode.
    // Columns: GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_ID_BACID,
    //          REPORT_CODE, ROW_ID, COLUMN_ID, SOL_ID
    // ─────────────────────────────────────────────────────────────────────────
    public List<Map<String, String>> getMappedAccounts(String reportCode) {
        if (reportCode == null || reportCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<BrfCommonMapping> rows = commonMappingRepo.findByReportCode(reportCode);

        List<Map<String, String>> result = new ArrayList<>();

        for (BrfCommonMapping row : rows) {
            if (row == null) continue;

            Map<String, String> view = new LinkedHashMap<>();

            view.put("GL_HEAD",          nvl(row.getGlHead()));
            view.put("GL_SUBHEAD_CODE",  nvl(row.getGlSubheadCode()));
            view.put("ACCOUNT_ID_BACID", nvl(row.getAccountIdBacid()));
            view.put("REPORT_CODE",      nvl(row.getReportCode()));
            view.put("ROW_ID",           nvl(row.getRowId()));
            view.put("COLUMN_ID",        nvl(row.getColumnId()));
            view.put("REPORT_ADDL_CRITERIA_1", nvl(row.getReportAddlCriteria1()));
            view.put("REPORT_ADDL_CRITERIA_2", nvl(row.getReportAddlCriteria2()));
            view.put("REPORT_ADDL_CRITERIA_3", nvl(row.getReportAddlCriteria3()));
            view.put("DATA_TYPE", nvl(row.getDataType()));
            view.put("SOL_ID", nvl(row.getSolId()));
            // Extra fields needed by Edit Mapped modal
            view.put("ACCOUNT_DESCRIPTION", nvl(row.getAccountDescription()));
            view.put("CURRENCY",            nvl(row.getCurrency()));
            view.put("ACCOUNT_BALANCE_LC",  nvl(row.getAccountBalanceLc()));
            view.put("CONSTITUTION_CODE",   nvl(row.getConstitutionCode()));
            view.put("LEGAL_ENTITY_TYPE",   nvl(row.getLegalEntityType()));
            view.put("HNI_NETWORTH",        nvl(row.getHniNetworth()));
            view.put("TURNOVER",            nvl(row.getTurnover()));
            view.put("FILTER_COLUMNS",      nvl(row.getFilterColumns()));
            view.put("SCHEME_TYPE",      nvl(row.getSchemeType()));

            result.add(view);
        }

        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // UNMAPPED ACCOUNTS
    // Returns rows from BRF_BASE_MAPPING_TABLE whose ACCOUNT_ID_BACID does
    // NOT exist in BRF_COMMON_MAPPING_TABLE for the given reportCode.
    // ─────────────────────────────────────────────────────────────────────────
    public List<Map<String, String>> getUnmappedAccounts(String reportCode) {
        if (reportCode == null || reportCode.trim().isEmpty()) {
            return Collections.emptyList();
        }

        List<BrfBaseMapping> rows = baseMappingRepo.findUnmappedAccounts(reportCode);
        System.out.println("UNMAPPED COUNT = " + rows.size());

        List<Map<String, String>> result = new ArrayList<>();

        for (BrfBaseMapping row : rows) {
            if (row == null) continue;

            Map<String, String> view = new LinkedHashMap<>();

            view.put("GL_HEAD",               nvl(row.getGlHead()));
            view.put("GL_SUBHEAD_CODE",       nvl(row.getGlSubheadCode()));
            view.put("ACCOUNT_ID_BACID",      nvl(row.getAccountIdBacid()));
            view.put("REPORT_CODE",           nvl(row.getReportCode()));
            view.put("ROW_ID",                nvl(row.getRowId()));
            view.put("COLUMN_ID",             nvl(row.getColumnId()));
            view.put("REPORT_ADDL_CRITERIA_1", nvl(row.getReportAddlCriteria1()));
            view.put("REPORT_ADDL_CRITERIA_2", nvl(row.getReportAddlCriteria2()));
            view.put("REPORT_ADDL_CRITERIA_3", nvl(row.getReportAddlCriteria3()));
         // Extra fields needed by Edit UnMapped modal
            view.put("ACCOUNT_DESCRIPTION", nvl(row.getAccountDescription()));
            view.put("CURRENCY",            nvl(row.getCurrency()));
            view.put("ACCOUNT_BALANCE_LC",  nvl(row.getAccountBalanceLc()));

            result.add(view);
        }

        return result;
    }


    private String nvl(String value) {
        return value != null ? value : "";
    }
    
    // BASE MAPPING PARAM — LIST (paged + searched)
    public Map<String, Object> getBaseMappingParamList(String search, int page, int size) {
        String s = (search == null || search.trim().isEmpty()) ? "" : search.trim();

        long total = baseMappingRepo.countBySearch(s);
        List<BrfBaseMapping> rows = baseMappingRepo.findPagedList(s, page, size);

        List<Map<String, String>> data = new ArrayList<>();
        for (BrfBaseMapping row : rows) {
            Map<String, String> view = new LinkedHashMap<>();
            view.put("GL_HEAD",             nvl(row.getGlHead()));
            view.put("GL_SUBHEAD_CODE",     nvl(row.getGlSubheadCode()));
            view.put("ACCOUNT_ID_BACID",    nvl(row.getAccountIdBacid()));
            view.put("ACCOUNT_DESCRIPTION", nvl(row.getAccountDescription()));
            view.put("CURRENCY",            nvl(row.getCurrency()));
            view.put("DATA_TYPE",           nvl(row.getDataType()));
            data.add(view);
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalRecords", total);
        response.put("data", data);
        return response;
    }
    
 // BASE MAPPING PARAM — INSERT
    public String saveBaseMappingParam(Map<String, String> body) {
    	
        String accountId = body.get("accountIdBacid");
        
        if (accountId == null || accountId.trim().isEmpty()) {
            return "ACCOUNT_ID_BACID is required";
        }
        // CHECK DUPLICATE
        if (baseMappingRepo.findByAccountIdBacid(accountId.trim()).isPresent()) {
            return "Account ID already exists: " + accountId;
        }

        int inserted = baseMappingRepo.insertRecord(
            accountId.trim(),
            nvl(body.get("glHead")),
            nvl(body.get("glSubHeadCode")),
            nvl(body.get("accountDescription")),
            nvl(body.get("currency")),
            nvl(body.get("dataType"))
        );

        return inserted > 0 ? "SUCCESS" : "Insert failed for: " + accountId;
    }
    
 // BASE MAPPING PARAM — UPDATE
    public String updateBaseMappingParam(Map<String, String> body) {

        String oldId = body.get("oldAccountId");        //  OLD ID
        String newId = body.get("accountIdBacid");      //  NEW ID

        if (newId == null || newId.trim().isEmpty()) {
            return "ACCOUNT_ID_BACID is required";
        }

        int updated = baseMappingRepo.updateRecord(
            nvl(body.get("glHead")),
            nvl(body.get("glSubHeadCode")),
            nvl(body.get("accountDescription")),
            nvl(body.get("currency")),
            nvl(body.get("dataType")),
            newId.trim(),     // NEW ID (SET)
            oldId.trim()      // OLD ID (WHERE)
        );

        return updated > 0 ? "SUCCESS" : "No active record found for: " + oldId;
    }
    
 // BASE MAPPING PARAM — SOFT DELETE
    public String deleteBaseMappingParam(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            return "ACCOUNT_ID_BACID is required";
        }

        int deleted = baseMappingRepo.deleteRecord(accountId.trim());
        return deleted > 0 ? "SUCCESS" : "No active record found for: " + accountId;
    }
}