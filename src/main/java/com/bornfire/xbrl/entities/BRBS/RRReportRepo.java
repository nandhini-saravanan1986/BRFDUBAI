package com.bornfire.xbrl.entities.BRBS;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;



@Repository
public interface RRReportRepo extends JpaRepository<RRReport, Integer> {
	

	@Query(value = "select * from RR_RPT_MAST where rpt_code=?1", nativeQuery = true)
	RRReport getReportbyrptcode(String rpt_code);
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'RR' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportList();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'FN' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getFord();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'BASEL' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListBASEL();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'HY1' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListhalfyearly1();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'HY2' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListhalfyearly2();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'M1' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListmonthly1();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'M2' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListmonthly2();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'M3' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListmonthly3();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'Q1' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListquarterly1();
	
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'Q2' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListquarterly2();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'Y' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportListyearly();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR1' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK1();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR3' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK3();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR2' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK2();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR4' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK4();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR5' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK5();
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR6' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK6();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR7' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK7();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR8' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK8();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'MR9' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTMARKETRISK9();
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'CR1' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTCREDITRISK1();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'CR3' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTCREDITRISK3();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'CR2' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTCREDITRISK2();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'CR4' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTCREDITRISK4();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'SR1' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTSettlementRISK();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'CV1' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTCreditValuation();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'EIF' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTEquityInvestment();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'OR1' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTOPERATIONALRISK();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'CAR' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTCapitalAdequacy();
	
	@Query(value = "select * from RR_RPT_MAST WHERE REMARKS_5 = 'FB' ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportLISTFB();
	//@Query(value = "select * from RR_RPT_MAST where srl_no not in (select max(srl_no) from RR_RPT_MAST)", nativeQuery = true)
	//List<RRReport> getReportList1();
	
	@Query(value = "SELECT * FROM RR_RPT_MAST WHERE rpt_code IN ('BRF95-CAR','CR1','CR2','CR2a','CR2b','CR3','CR4','CR4a','CR4b (fx)','SR1','CV1','EIF','MR1','MR2','MR3','MR4','FX (MR5)','FX (MR5a)','MR6','MR7','MR8','MR9','OR1')", nativeQuery = true)
	List<RRReport> getReportList1();
	

/*	@Query(value = "SELECT * FROM RR_RPT_MAST WHERE rpt_code IN ('BRF057','BRF107A','BRF005','BRF049A','BRF068','BRF066A','BRF205A','BRF007','BRF054','BRF048A','BRF047A','BRF088','BRF051','BRF086','BRF100A','BRF085','BRF008','BRF039A','BRF067','BRF064','BRF038A','BRF152','BRF151','BRF300A','BRF014','BRF106A','BRF056','BRF074','BRF032A','BRF204A','BRF077','BRF002','BRF001','BRF071','BRF003','BRF050A','BRF052','BRF034A','BRF011','BRF101A','BRF035A','BRF155','BRF080','BRF154','BRF208A','BRF207A','BRF105A','BRF010','BRF040A','BRF042A','BRF156','BRF083','BRF082','BRF004','BRF096A','BRF206A','BRF073','BRF036A','BRF041A','BRF202A','BRF201A','BRF200A','BRF062','BRF081','BRF094A','BRF093A','BRF069','BRF070A','BRF046A','BRF059','BRF045A','BRF095A','BRF033A','BRF092','BRF084','BRF102A','BRF153','BRF104A','BRF078','BRF037A','BRF076','BRF065','BRF060','BRF044A','BRF181A','BRF031A','BRF053','BRF099A','BRF043A','BRF013','BRF103A','BRF087','BRF012','BRF009','BRF079','BRF210A','BRF209A') ", nativeQuery = true)
	List<RRReport> getReportList2();*/
	
	
	@Query(value = "SELECT rpt_code, rpt_description, remarks_4 FROM RR_RPT_MAST GROUP BY rpt_code, rpt_description, remarks_4 ORDER BY rpt_code", nativeQuery = true)
	List<Object[]> getReportList2();

	
@Query(value = "select * from RR_RPT_MAST", nativeQuery = true)
	List<RRReport> getList( );
	
	@Query(value = "SELECT * FROM RR_RPT_MAST WHERE  rpt_code=?1 and rpt_description=?2 order by end_date desc", nativeQuery = true)
	List<RRReport> getReportArchival(String reportid,String reportdesc);
	
	
	@Query(value = "SELECT * FROM RR_RPT_MAST WHERE rpt_code IN ('BRF069','BRF070A','BRF202A','BRF300A') order by rpt_code", nativeQuery = true)
	List<RRReport> gethrmodel();
	
	@Query(value = "SELECT * FROM RR_RPT_MAST WHERE rpt_code IN ('BRF071','BRF201A') order by rpt_code", nativeQuery = true)
	List<RRReport> getitmodel();
	
	@Query(value = "SELECT * FROM RR_RPT_MAST WHERE rpt_code IN ('BRF301A') order by rpt_code", nativeQuery = true)
	List<RRReport> getopmodel();
	
	@Query(value = "SELECT * FROM RR_RPT_MAST WHERE rpt_code IN ('BRF108A') order by rpt_code", nativeQuery = true)
	List<RRReport> getacmodel();
	
	/*@Query(value = "SELECT rpt_code, rpt_description, DOMAIN, end_date, remarks_3 from RR_RPT_MAST WHERE REMARKS_4 =?1 ORDER BY rpt_code", nativeQuery = true)
	List<RRReport> getReportAsFrequency(String frequency);
	*/
	@Query(value = "SELECT rpt_code, rpt_description, DOMAIN, end_date, remarks_3 FROM RR_RPT_MAST WHERE REMARKS_4 = ?1", nativeQuery = true)
	List<Object[]> getCustomReportData(String frequency);

	@Query(value = "select * from RR_RPT_MAST WHERE RPT_CODE =?1", nativeQuery = true)
	RRReport getParticularReport(String rptcode);
	
	@Query(value = "select * from RR_RPT_MAST WHERE RPT_CODE =?1", nativeQuery = true)
	<Optional>RRReport getParticularReport2(String rptcode);
	
	@Query(value = "select * from RR_RPT_MAST WHERE RPT_CODE =?1", nativeQuery = true)
	Optional<RRReport> getParticularReport3(String rptcode);
	@Query(value = "SELECT * FROM ( " + " SELECT * FROM RR_RPT_MAST t "
			+ " WHERE t.end_date = :end_date AND t.REMARKS_5 = :remarks5 " + " UNION ALL "
			+ " SELECT * FROM RR_RPT_MAST t " + " WHERE t.REMARKS_5 = :remarks5 " + " AND NOT EXISTS ( "
			+ " SELECT 1 " + " FROM RR_RPT_MAST x " + " WHERE x.RPT_CODE = t.RPT_CODE "
			+ " AND x.end_date = :end_date " + " AND x.REMARKS_5 = :remarks5 " + " ) " + " AND t.end_date = ( "
			+ " SELECT MAX(t2.end_date) " + " FROM RR_RPT_MAST t2 "
			+ " WHERE t2.RPT_CODE = t.RPT_CODE AND t2.REMARKS_5 = :remarks5 " + " ) " + ") combined_results "
			+ "ORDER BY RPT_CODE", nativeQuery = true)
	List<RRReport> findDataByDate(@Param("end_date") Date end_date, @Param("remarks5") String remarks5);

	@Query(value = "SELECT * FROM RR_RPT_MAST t " + "WHERE t.REMARKS_5 = :remarks5 " + "AND NOT EXISTS ( "
			+ " SELECT 1 " + " FROM RR_RPT_MAST x " + " WHERE x.RPT_CODE = t.RPT_CODE "
			+ " AND x.end_date = :end_date " + " AND x.REMARKS_5 = :remarks5 " + ") " + "AND t.end_date = ( "
			+ " SELECT MAX(t2.end_date) " + " FROM RR_RPT_MAST t2 "
			+ " WHERE t2.RPT_CODE = t.RPT_CODE AND t2.REMARKS_5 = :remarks5 " + ") "
			+ "ORDER BY t.RPT_CODE", nativeQuery = true)
	List<RRReport> findDataMissing(@Param("end_date") Date end_date, @Param("remarks5") String remarks5);

	@Query(value = "SELECT COALESCE(MAX(srl_no), 0) FROM RR_RPT_MAST", nativeQuery = true)
	int findMaxSerialNo();

	@Query(value = "SELECT * FROM RR_RPT_MAST t " + "WHERE t.REMARKS_5 = :remarks5 " + " AND t.END_DATE = ( "
			+ " SELECT MAX(t2.END_DATE) " + " FROM RR_RPT_MAST t2 " + " WHERE t2.RPT_CODE = t.RPT_CODE "
			+ " AND t2.REMARKS_5 = :remarks5 " + ") " + " ORDER BY t.RPT_CODE", nativeQuery = true)
	List<RRReport> findReportsByRemarks(@Param("remarks5") String remarks5);
	
	@Query(value = "select * from RR_RPT_MAST WHERE RPT_CODE =?1", nativeQuery = true)
	List<RRReport> getParticularReport3list(String rptcode);
	
	@Query(value = "select end_date from RR_RPT_MAST WHERE RPT_CODE =?1", nativeQuery = true)
	List<Date> getdatelist(String rptcode);
	
	@Query(value = "select * from RR_RPT_MAST where rpt_code=?1 AND end_date =?2", nativeQuery = true)
	RRReport getReportbyrptcodeandtodate(String rpt_code, String todate);

} 


