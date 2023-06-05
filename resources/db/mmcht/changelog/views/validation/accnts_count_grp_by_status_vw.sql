--liquibase formatted sql
--changeset unknown:accnts_count_grp_by_status_vw runOnChange:true dbms:oracle endDelimiter:# stripComments:false
-- Each row in this view represents a merchant's organizational address.
CREATE OR REPLACE VIEW accnts_count_grp_by_status_vw AS (

  select STAT_TYPE.DESCRIPTION as NAME, count(FIREPAY.PERSONAL_ACCOUNT_NBR) COUNT from FPA.FIREPAY_PERS_ACCNTS FIREPAY LEFT JOIN FPA.PERS_ACCNT_STATS STAT ON STAT.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR LEFT JOIN FPA.PERS_ACCNT_STAT_TYP STAT_TYPE ON STAT.PERSONAL_ACCOUNT_STAT_TYPE_CDE = STAT_TYPE.PERSONAL_ACCOUNT_STAT_TYPE_CDE where  FIREPAY.acctt_cde='FMA'  AND STAT.THRU_DATE IS NULL group by STAT_TYPE.DESCRIPTION

  )
#

--changeset unknown:accnts_count_grp_by_status_vw_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant SELECT ON accnts_count_grp_by_status_vw TO ${liquibase.schema}_rw
#

grant SELECT ON accnts_count_grp_by_status_vw TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.accnts_count_grp_by_status_vw FOR accnts_count_grp_by_status_vw
#
