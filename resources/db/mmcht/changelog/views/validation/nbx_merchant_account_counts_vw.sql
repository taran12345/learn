--liquibase formatted sql
--changeset unknown:nbx_merchant_account_counts_vw runOnChange:true dbms:oracle endDelimiter:# stripComments:false
-- Each row in this view represents a merchant's organizational address.
CREATE OR REPLACE VIEW nbx_merchant_account_counts_vw AS (

  select
      'merchants_count' as NAME, (select count(UNIQUE FIREPAY.PERSONAL_ACCOUNT_NBR) from FPA.FIREPAY_PERS_ACCNTS FIREPAY LEFT JOIN FPA.PERS_ACCNT_STATS STAT ON STAT.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR where  FIREPAY.acctt_cde='FMA' AND STAT.THRU_DATE IS NULL) as COUNT from dual
  union

  select
      'netellar_merchant_count', (select count(*) from MMCHT.neteller_merchant_accounts_vw) from dual
  )
#

--changeset unknown:nbx_merchant_account_counts_vw_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant SELECT ON nbx_merchant_account_counts_vw TO ${liquibase.schema}_rw
#

grant SELECT ON nbx_merchant_account_counts_vw TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.nbx_merchant_account_counts_vw FOR nbx_merchant_account_counts_vw
#
