--liquibase formatted sql
--changeset unknown:neteller_merchant_accounts_vw runOnChange:true dbms:oracle endDelimiter:# stripComments:false
-- Each row in this view represents a merchant.
CREATE OR REPLACE VIEW neteller_merchant_accounts_vw
AS (
  SELECT
        ACC."ID"                as ACCOUNT_ID,
        AG.origin_reference_id  as PMLE,
        ACC.CURRENCY_CODE       as CURRENCY,
        ACC.PAYMENT_TYPE        as PAYMENT_TYPE,
        cast(ACC.LAST_MODIFIED_DATE as date)  as ACCOUNT_MDATE,
        cast(AG.LAST_MODIFIED_DATE as date)  as ACCOUNT_GROUP_MDATE
     FROM ALTP.ACCOUNT_GROUPS AG
      LEFT JOIN ALTP.ACCOUNT_GROUP_ACCOUNTS AGA
           on AG."ID" = AGA.ACCOUNT_GROUP_ID
      LEFT JOIN ALTP.ACCOUNTS ACC
           on  AGA.ACCOUNT_ID = ACC."ID"
     WHERE AG.TYPE = 'PMLE'
     AND ACC.PAYMENT_TYPE = 'NETELLER'
   )
#

--changeset unknown:neteller_merchant_accounts_vw_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant SELECT ON neteller_merchant_accounts_vw TO ${liquibase.schema}_rw
#

grant SELECT ON neteller_merchant_accounts_vw TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.neteller_merchant_accounts_vw FOR neteller_merchant_accounts_vw
#
