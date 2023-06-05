--liquibase formatted sql
--changeset unknown:get_source_bank_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_source_bank_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_source_bank is
select SB.CDE                      AS SB_CDE,
      SB.BANK_NAME                 AS SB_BANK_NAME,
      SBS.DESCRIPTION              AS SB_BANK_DESC,
      SBS.PMTPV_CODE               AS SB_PAYMENT_VEHICLE,
      SBS.PMTFF_CODE               AS SB_FORMAT,
      SBS.CURRENCY_TYPE_CDE        AS SB_CURRENCY,
      SBS.ROUTING_NBR              AS SB_ROUTING_NBR,
      SBS.ACCOUNT_NBR_ENC          AS SB_ACCOUNT_NBR_ENC,
      SBS.ACCOUNT_NBR_HMAC         AS SB_ACCOUNT_NBR_HMAC,
      SBS.ACCOUNT_NBR_LAST_DIGITS  AS SB_ACCOUNT_NBR_LAST_DIGITS,
      to_boolean_fnc(SBS.ACCEPT_DEBIT)           AS SB_ACCEPT_DEBIT,
      to_boolean_fnc(SBS.FLOW_OF_FUNDS_IND)      AS SB_FLOW_OF_FUNDS,
      SBA.BANK_ACCOUNT_NAME                      AS SB_NAVISION_CODE
FROM FPA.PMT_FMA_SOURCE_BANK_SETUPS FSBS
LEFT JOIN FPA.PMT_SOURCE_BANKS_SETUP SBS ON FSBS.PMTSBS_SETUP_ID = SBS.SETUP_ID
    AND SBS.ACTIVE_FLAG = 1 AND SBS.THRU_DATE IS NULL
LEFT JOIN FPA.FPA_SOURCE_BANKS SB ON SBS.PMTSB_CODE = SB.CDE
LEFT JOIN FPA.FPA_SOURCE_BANK_ACCOUNTS SBA ON SBA.ID = SBS.SOUBAC_ID
WHERE FSBS.PERSONAL_ACCOUNT_NBR = coalesce(
    (select CHILD.CONSOLIDATED_PAYMT_ACCT from FPA.FMA_PAYMENT_SCHEDULE CHILD where CHILD.PERSONAL_ACCOUNT_NBR = to_char(p_personal_account_nbr)),
    to_char(p_personal_account_nbr)
)
AND FSBS.THRU_DATE IS NULL;

type typ_source_bank is record (
                                  SB_CDE                        VARCHAR2(5 CHAR),
                                  SB_BANK_NAME                  VARCHAR2(40 CHAR),
                                  SB_BANK_DESC                  VARCHAR2(240 CHAR),
                                  SB_PAYMENT_VEHICLE            VARCHAR2(15 CHAR),
                                  SB_FORMAT                     VARCHAR2(15 CHAR),
                                  SB_CURRENCY                   VARCHAR2(10 CHAR),
                                  SB_ROUTING_NBR                VARCHAR2(40 CHAR),
                                  SB_ACCOUNT_NBR_ENC            VARCHAR2(256 CHAR),
                                  SB_ACCOUNT_NBR_HMAC           VARCHAR2(256 CHAR),
                                  SB_ACCOUNT_NBR_LAST_DIGITS    VARCHAR2(10 CHAR),
                                  SB_ACCEPT_DEBIT               VARCHAR2(5 CHAR),
                                  SB_FLOW_OF_FUNDS              VARCHAR2(5 CHAR),
                                  SB_NAVISION_CODE              VARCHAR2(100 CHAR)
                                      );
  local_obj varchar2(10000);
  tmp_json_array clob ;

  typ_cr_source_bank  cr_source_bank%rowtype;
  begin
    if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
       return '{}';
    end if;
    open cr_source_bank ;
    fetch cr_source_bank into typ_cr_source_bank;
    if cr_source_bank%found then
       local_obj := null;
       local_obj := '{
               "SB_CDE":'||to_string_fnc(typ_cr_source_bank.SB_CDE)||',
               "SB_BANK_NAME":'||to_string_fnc(typ_cr_source_bank.SB_BANK_NAME)||',
               "SB_BANK_DESC":'||to_string_fnc(typ_cr_source_bank.SB_BANK_DESC)||',
               "SB_PAYMENT_VEHICLE":'||to_string_fnc(typ_cr_source_bank.SB_PAYMENT_VEHICLE)||',
               "SB_FORMAT":'||to_string_fnc(typ_cr_source_bank.SB_FORMAT)||',
               "SB_CURRENCY":'||to_string_fnc(typ_cr_source_bank.SB_CURRENCY)||',
               "SB_ROUTING_NBR":'||to_string_fnc(typ_cr_source_bank.SB_ROUTING_NBR)||',
               "SB_ACCOUNT_NBR_ENC":'||to_string_fnc(typ_cr_source_bank.SB_ACCOUNT_NBR_ENC)||',
               "SB_ACCOUNT_NBR_HMAC":'||to_string_fnc(typ_cr_source_bank.SB_ACCOUNT_NBR_HMAC)||',
               "SB_ACCOUNT_NBR_LAST_DIGITS":'||to_string_fnc(typ_cr_source_bank.SB_ACCOUNT_NBR_LAST_DIGITS)||',
               "SB_ACCEPT_DEBIT":"'||typ_cr_source_bank.SB_ACCEPT_DEBIT||'",
               "SB_FLOW_OF_FUNDS":"'||typ_cr_source_bank.SB_FLOW_OF_FUNDS||'",
               "SB_NAVISION_CODE":'||to_string_fnc(typ_cr_source_bank.SB_NAVISION_CODE)||'
          }';

      close cr_source_bank;
      return local_obj;
    else
      return '{}';
    end if;
exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_source_bank_fnc;
#

--changeset unknown:perm_get_source_bank_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_source_bank_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_source_bank_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_source_bank_fnc FOR get_source_bank_fnc
#
