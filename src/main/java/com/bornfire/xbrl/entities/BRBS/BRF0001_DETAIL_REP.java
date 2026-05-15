package com.bornfire.xbrl.entities.BRBS;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BRF0001_DETAIL_REP extends JpaRepository<BRF0001_DETAIL_ENTITY, String>{
	@Query(value = "SELECT * FROM  BRF001_DETAILTABLE WHERE foracid =?1", nativeQuery = true)
	BRF0001_DETAIL_ENTITY getallDetails(String foracid);

	@Query(
		    value = "SELECT cust_id, foracid, acct_name, act_balance_amt_lc, " +
		            "report_name_1, report_label_1, report_addl_criteria_1, report_date " +
		            "FROM BRF001_DETAILTABLE " +
		            "WHERE report_date = ?1 "+
		            "ORDER BY report_label_1",
		    nativeQuery = true
		)
	List<Object[]> find(String todate);
	
}
