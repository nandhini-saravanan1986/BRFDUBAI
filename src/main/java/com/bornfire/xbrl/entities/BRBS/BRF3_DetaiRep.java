package com.bornfire.xbrl.entities.BRBS;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bornfire.xbrl.entities.BRFDETAILID;

public interface BRF3_DetaiRep extends JpaRepository<BRF3_DETAILTABLE, BRFDETAILID> {
	@Query(value = "SELECT * FROM  BRF3_DETAILTABLE WHERE foracid =?1", nativeQuery = true)
	BRF3_DETAILTABLE getallDetails(String foracid);
	
	@Query(value = "SELECT * FROM  BRF3_DETAILTABLE WHERE FORACID =?1 AND REPORT_DATE=?2", nativeQuery = true)
	BRF3_DETAILTABLE getbyaccnoanddate(String acct_no,String report_date);

}
