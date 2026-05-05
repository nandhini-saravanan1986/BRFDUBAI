package com.bornfire.xbrl.entities.BRBS;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.bornfire.xbrl.entities.BRFDETAILID;

public interface BRF2_DetaiRep extends JpaRepository<BRF2_DETAIL_ENTITY, BRFDETAILID> {
@Query(value = "SELECT * FROM  BRF2_DETAILTABLE WHERE FORACID =?1", nativeQuery = true)
		BRF2_DETAIL_ENTITY getallDetails(String acct_no);


@Query(value = "SELECT * FROM  BRF2_DETAILTABLE WHERE FORACID =?1 AND REPORT_DATE=?2", nativeQuery = true)
BRF2_DETAIL_ENTITY getbyaccnoanddate(String acct_no,String report_date);

}
