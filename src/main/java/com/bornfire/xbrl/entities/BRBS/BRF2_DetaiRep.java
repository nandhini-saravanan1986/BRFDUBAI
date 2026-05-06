package com.bornfire.xbrl.entities.BRBS;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bornfire.xbrl.entities.BRFDETAILID;

public interface BRF2_DetaiRep extends JpaRepository<BRF2_DETAIL_ENTITY, BRFDETAILID> {
@Query(value = "SELECT * FROM  BRF2_DETAILTABLE WHERE FORACID =?1", nativeQuery = true)
		BRF2_DETAIL_ENTITY getallDetails(String acct_no);


@Query(value = "SELECT * FROM  BRF2_DETAILTABLE WHERE FORACID =?1 AND REPORT_DATE=?2", nativeQuery = true)
BRF2_DETAIL_ENTITY getbyaccnoanddate(String acct_no,String report_date);

@Query(
	    value = "SELECT cust_id, foracid, acct_name, act_balance_amt_lc, " +
	            "report_name_1, report_label_1, report_addl_criteria_1, report_date " +
	            "FROM brf2_detailtable " +
	            "ORDER BY report_label_1",
	    nativeQuery = true
	)
List<Object[]> find();

}
