package com.bornfire.xbrl.entities;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface BrfBaseMappingRepository
        extends JpaRepository<BrfBaseMapping, String> {

    /**
     * Returns all base-mapping rows whose ACCOUNT_ID_BACID does NOT appear
     * in BRF_COMMON_MAPPING_TABLE for the given reportCode.
     * These are the "unmapped" accounts.
     */
	@Query(
		    value = "SELECT * FROM BRF_BASE_MAPPING_TABLE b " +
		            "WHERE b.ACCOUNT_ID_BACID NOT IN ( " +
		            "SELECT c.ACCOUNT_ID_BACID " +
		            "FROM BRF_COMMON_MAPPING_TABLE c " +
		            "WHERE c.REPORT_CODE = :reportCode)",
		    nativeQuery = true
		)
		List<BrfBaseMapping> findUnmappedAccounts(@Param("reportCode") String reportCode);
	
	// Used in submitAccounts() 
	// All 3 params are optional:
    //   :dataType  → pass "TREASURY" if source=TREASURY, else pass NULL
    //   :glHead    → pass "%value%" if provided,          else pass NULL
    //   :glSubHead → pass "%value%" if provided,          else pass NULL
    //
    // Oracle trick: (:param IS NULL OR col LIKE :param)
    //   → when param is NULL  the condition is skipped (full scan on that col)
    //   → when param has value the LIKE filter is applied at DB level
	@Query(
	        value = "SELECT * FROM BRF_BASE_MAPPING_TABLE " +
	                "WHERE (:dataType  IS NULL OR UPPER(DATA_TYPE)       = UPPER(:dataType))  " +
	                "AND   (:glHead    IS NULL OR UPPER(GL_HEAD)         LIKE UPPER(:glHead)) " +
	                "AND   (:glSubHead IS NULL OR UPPER(GL_SUBHEAD_CODE) LIKE UPPER(:glSubHead))",
	        nativeQuery = true
	    )
    List<BrfBaseMapping> findByFilters(
        @Param("dataType")  String dataType,
        @Param("glHead")    String glHead,
        @Param("glSubHead") String glSubHead
    );
    
 // Spring Data derives the SQL automatically from the method name.
    // No @Query needed.
    // Returns empty Optional if account not found.
    Optional<BrfBaseMapping> findByAccountIdBacid(String accountIdBacid);
    
    /**
     * LIST  —  GET /BRF/BaseMappingParam/list?page=1&size=20&search=
     * Returns one page of rows, filtered by search term.
     * Oracle-style pagination: OFFSET … ROWS FETCH NEXT … ROWS ONLY
     */
    @Query(
    	    value = "SELECT * FROM BRF_BASE_MAPPING_TABLE " +
    	            "WHERE (:search IS NULL OR :search = '' " +
    	            "     OR UPPER(ACCOUNT_ID_BACID)    LIKE '%' || UPPER(:search) || '%' " +
    	            "     OR UPPER(ACCOUNT_DESCRIPTION) LIKE '%' || UPPER(:search) || '%' " +
    	            "     OR UPPER(GL_HEAD)             LIKE '%' || UPPER(:search) || '%' " +
    	            "     OR UPPER(GL_SUBHEAD_CODE)     LIKE '%' || UPPER(:search) || '%') " +
    	            "ORDER BY GL_HEAD, GL_SUBHEAD_CODE " +
    	            "OFFSET (:page - 1) * :size ROWS FETCH NEXT :size ROWS ONLY",
    	    nativeQuery = true
    	)
    List<BrfBaseMapping> findPagedList(
        @Param("search") String search,
        @Param("page")   int page,
        @Param("size")   int size
    );
    
    //INSERT — POST /BRF/BaseMappingParam/save
    @Modifying
    @Transactional
    @Query(
        value = "INSERT INTO BRF_BASE_MAPPING_TABLE " +
                "(ACCOUNT_ID_BACID, GL_HEAD, GL_SUBHEAD_CODE, ACCOUNT_DESCRIPTION, " +
                " CURRENCY, DATA_TYPE) " +
                "VALUES " +
                "(:accountId, :glHead, :glSubHeadCode, :accountDescription, " +
                " :currency, :dataType)",
        nativeQuery = true
    )
    int insertRecord(
        @Param("accountId")           String accountId,
        @Param("glHead")              String glHead,
        @Param("glSubHeadCode")       String glSubHeadCode,
        @Param("accountDescription")  String accountDescription,
        @Param("currency")            String currency,
        @Param("dataType")            String dataType
    );

    /**
     * COUNT companion for the paged list — used to build totalRecords.
     */
    @Query(
    	    value = "SELECT COUNT(*) FROM BRF_BASE_MAPPING_TABLE " +
    	            "WHERE (:search IS NULL OR :search = '' " +
    	            "     OR UPPER(ACCOUNT_ID_BACID)    LIKE '%' || UPPER(:search) || '%' " +
    	            "     OR UPPER(ACCOUNT_DESCRIPTION) LIKE '%' || UPPER(:search) || '%' " +
    	            "     OR UPPER(GL_HEAD)             LIKE '%' || UPPER(:search) || '%' " +
    	            "     OR UPPER(GL_SUBHEAD_CODE)     LIKE '%' || UPPER(:search) || '%')",
    	    nativeQuery = true
    	)
    long countBySearch(@Param("search") String search);

    /**
     * UPDATE  —  PUT /BRF/BaseMappingParam/update
     * Updates all editable columns for a given ACCOUNT_ID_BACID.
     */
    @Modifying
    @Transactional
    @Query(
    	    value = "UPDATE BRF_BASE_MAPPING_TABLE " +
    	            "SET    ACCOUNT_ID_BACID    = :newId, " +
    	            "       GL_HEAD             = :glHead, " +
    	            "       GL_SUBHEAD_CODE     = :glSubHeadCode, " +
    	            "       ACCOUNT_DESCRIPTION = :accountDescription, " +
    	            "       CURRENCY            = :currency, " +
    	            "       DATA_TYPE           = :dataType " +
    	            "WHERE ACCOUNT_ID_BACID = :oldId",
    	    nativeQuery = true
    	)
    int updateRecord(
        @Param("glHead") String glHead,
        @Param("glSubHeadCode") String glSubHeadCode,
        @Param("accountDescription") String accountDescription,
        @Param("currency") String currency,
        @Param("dataType") String dataType,
        @Param("newId") String newId,
        @Param("oldId") String oldId
    );

    /**
     * SOFT DELETE  —  DELETE /BRF/BaseMappingParam/delete/{id}
     * Sets DEL_FLG = 'Y'; never physically removes the row.
     */
//    @Modifying
//    @Transactional
//    @Query(
//        value = "UPDATE BRF_BASE_MAPPING_TABLE " +
//                "SET    DEL_FLG = 'Y' " +
//                "WHERE  ACCOUNT_ID_BACID = :accountId " +
//                "AND   (DEL_FLG IS NULL OR DEL_FLG <> 'Y')",
//        nativeQuery = true
//    )
//    int softDelete(@Param("accountId") String accountId);
    
    @Modifying
    @Transactional
    @Query(
        value = "DELETE FROM BRF_BASE_MAPPING_TABLE " +
                "WHERE ACCOUNT_ID_BACID = :accountId",
        nativeQuery = true
    )
    int deleteRecord(@Param("accountId") String accountId);
    
	@Query(value = "SELECT DISTINCT " + "  DATA_TYPE AS \"source\", " + "  GL_HEAD AS \"glHead\", "
			+ "  GL_SUBHEAD_CODE AS \"subHeadCode\" " + "FROM BRF_BASE_MAPPING_TABLE "
			+ "WHERE DATA_TYPE IS NOT NULL", nativeQuery = true)
	List<Map<String, Object>> findAllDistinctGlMappings();
    
}