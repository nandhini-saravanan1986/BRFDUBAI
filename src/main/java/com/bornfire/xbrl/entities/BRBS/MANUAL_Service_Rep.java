package com.bornfire.xbrl.entities.BRBS;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface MANUAL_Service_Rep extends JpaRepository<MANUAL_Service_Entity, String>{
	@Query(value = "select * from MANUAL_SERVICE_TABLE where AUDIT_TABLE = 'XBRLUSERPROFILETABLE'", nativeQuery = true)
	List<MANUAL_Service_Entity> getauditListLocalvalues();
	
	@Query(value = "select * from MANUAL_SERVICE_TABLE where TRUNC(AUDIT_DATE) = ?1", nativeQuery = true)
	List<MANUAL_Service_Entity> getauditListLocalvaluesbusiness(Date fromDateToUse);

	@Query(value = "select * from MANUAL_SERVICE_TABLE where TRUNC(ENTRY_TIME) = ?1", nativeQuery = true)
	List<MANUAL_Service_Entity> getauditListLocalvaluesbusiness21(String fromDateToUse);
	
	@Query(value = "select * from manual_service_table where ENTRY_TIME =?1", nativeQuery = true)
	MANUAL_Service_Entity getauditListLocalvaluesbusiness1(String fromDateToUse);
	
	@Query(value = "SELECT * FROM MANUAL_SERVICE_TABLE", nativeQuery = true)
	List<MANUAL_Service_Entity> getServiceAuditList(String auditRefNo);
	
	@Query(value = "SELECT * FROM MANUAL_SERVICE_TABLE WHERE AUDIT_REF_NO = ?1", nativeQuery = true)
	List<MANUAL_Service_Entity> getServiceAudiT(String auditRefNo);

		@Query(value = "SELECT * FROM MANUAL_SERVICE_TABLE " +
	               "WHERE AUDIT_DATE >= TO_DATE(:startDate, 'YYYY-MM-DD') " +
	               "AND AUDIT_DATE <= TO_TIMESTAMP(:endDate || ' 23:59:59', 'YYYY-MM-DD HH24:MI:SS')", 
	       nativeQuery = true)
	List<MANUAL_Service_Entity> findByDateRange(@Param("startDate") String startDate, @Param("endDate") String endDate);
	
}
