--liquibase formatted sql
--changeset unknown:get_card_configs_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false


create or replace function get_dd_txn_options_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor dd_mm_processor_brands is
SELECT
      txn.merca_account_id  AS TXN_DDOPTNS_MERCA_ACCOUNT_ID,
      txn.PROCESSOR_ID      AS TXN_DDOPTNS_PROCESSOR_ID,
      txn.PROCESSOR_PASSWORD  AS TXN_DDOPTNS_PROCESSOR_PASSWORD,
      txn.proc_cde    AS TXN_DDOPTNS_PROC_CODE,
      txn.merchant_acct_processor_id  AS TXN_DDOPTNS_MRCHNT_ACCPRCSRID,
      txn.in_realtime_processor_mode AS TXN_DDOPTNS_REALTIME_PRCSR_MD,
      txn.in_batch_processor_mode   AS TXN_DDOPTNS_BATCH_PRCSR_MD,
      txn.outgoing_batch_grouping_key AS TXN_DDOPTNS_OUTGNG_BTCH_GK,
      txn.outgoing_txn_grouping_key   AS TXN_DDOPTNS_OUTGNG_TXN_GK,
	  fma.STORE_ID  AS TXN_DDOPTNS_STORE_ID,
      fma.STORE_PASSWORD  AS TXN_DDOPTNS_STORE_PASSWORD,
      fma.ACCOST_CDE  AS TXN_DDOPTNS_ACCOST_CDE,
      east.DESCRIPTION AS TXN_DDOPTNS_DESCRIPTION,
      fma.CURRENCY_TYPE_CDE   AS TXN_DDOPTNS_CURRENCY_TYPE_CDE,
      fma.OPERATION_PERMISSION  AS TXN_DDOPTNS_OPRTN_PRMSN,
      to_boolean_fnc(fma.GUARANTEED_ECHECK_IND) AS TXN_DDOPTNS_GRNTD_ECHCK_IND,
      fma.AUTHORIZED_IP_ADDRESSES AS TXN_DDOPTNS_ATHRZD_IP_ADRS,
      to_boolean_fnc(fma.OVERDRAFT_PULL_IND) AS TXN_DDOPTNS_OVRDRFT_PULL_IND,
      fma.OVERDRAFT_PULL_STATUS_REASON  AS TXN_DDOPTNS_OVRDRFT_PL_STS_RSN,
      fma.STATEMENT_DESCRIPTOR AS TXN_DDOPTNS_STMNT_DSCRPTR,
	  nvl(txn.soubac_id,0) AS TXN_DDOPTNS_BNK_ACCID,
      fma.DEF_FRAUD_SVCS AS TXN_DDOPTNS_DEF_FRAUD_SVCS,
	  CASE WHEN txn.SAME_DAY_CREDIT_IND LIKE '%Y%' THEN 'true' ELSE 'false' END AS TXN_DDOPTNS_SM_DAY_C,
	  CASE WHEN txn.SAME_DAY_DEBIT_IND LIKE '%Y%' THEN 'true' ELSE 'false' END AS TXN_DDOPTNS_SM_DAY_D,
	  CASE WHEN fma.OPERATION_PERMISSION LIKE '%S%' THEN 'true' ELSE 'false' END AS DD_AT_TXNCHARGE,
      CASE WHEN fma.OPERATION_PERMISSION LIKE '%C%' THEN 'true' ELSE 'false' END AS DD_AT_TXNCREDIT,
      CASE WHEN fma.OPERATION_PERMISSION LIKE '%V%' THEN 'true' ELSE 'false' END AS DD_AT_TXNVERIFY,
      CASE WHEN fma.OPERATION_PERMISSION LIKE '%I%' THEN 'true' ELSE 'false' END AS DD_AT_TXNINFO_LK,
      CASE WHEN fma.OPERATION_PERMISSION LIKE '%M%' THEN 'true' ELSE 'false' END AS DD_AT_TXNMANDATE,
      CASE WHEN fma.OPERATION_PERMISSION LIKE '%Z%' THEN 'true' ELSE 'false' END AS DD_AT_TXNVOID,
      CASE WHEN fma.DEF_FRAUD_SVCS LIKE '%rre%' THEN 'true' ELSE 'false' END AS TXN_DDOPTNS_OPTRRE,
      CASE WHEN fma.DEF_FRAUD_SVCS LIKE '%mmf%' THEN 'true' ELSE 'false' END AS TXN_DDOPTNS_MAXMND,
      BA.BANK_ACCOUNT_NAME   as TXN_BANK_ACCOUNT_NAME,
      BA.ACCOUNT_NBR_ENC     as TXN_ACCOUNT_NBR_ENC,
      BA.ACCOUNT_NBR_HMAC    as TXN_ACCOUNT_NBR_HMAC,
      BA.ACCOUNT_LAST_DIGITS as TXN_ACCOUNT_LAST_DIGITS,
      BA.ROUTING_NBR         as TXN_ROUTING_NBR
    FROM
      jur_ECHECK_ACCT_PROCESSORS txn,
      jur_ECHECK_MERCHANT_ACCOUNTS fma,
      JUR_ECHECK_ACCT_STATUS_TYPS east,
      FPA_SOURCE_BANK_ACCOUNTS ba
    WHERE merca_account_id = to_char(P_PERSONAL_ACCOUNT_NBR)
      AND txn.merca_account_id = fma.ACCOUNT_ID
      AND txn.thru_date is null
      and east.ACCOST_CDE = fma.ACCOST_CDE
      and ba.id = txn.soubac_id;

  v_typ_dd_mm_prcsr_brnds  dd_mm_processor_brands%rowtype;
  local_obj varchar2(10000);

  function get_dd_opt_representments_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
    cursor dd_opt_representments is
    SELECT rs.PAYFT_CDE  as TXN_DDOPTNS_REPRSMNT_FREQ,
           rs.ECHEST_CDE as TXN_DDOPTNS_REPRSMNT_SCODE
      FROM JUR_REPRESENTMENT_SCHEDULE rs
     WHERE rs.MERCHANT_ACCOUNT_ID = to_char(P_PERSONAL_ACCOUNT_NBR)
     order by rs.ECHEST_CDE;

    type type_dd_opt_representments is record (
                                                TXN_DDOPTNS_REPRSMNT_FREQ    VARCHAR2(10),
                                                TXN_DDOPTNS_REPRSMNT_SCODE   VARCHAR2(10)
                                              );
    type tbl_dd_opt_representments is table of type_dd_opt_representments index by PLS_INTEGER;
    v_tbl_typ_dd_opt_reprsntmnts  tbl_dd_opt_representments;
    local_obj varchar2(10000);
    tmp_json_array clob := '[';
    begin
     if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
        return '[]';
     end if;
     open dd_opt_representments ;
     fetch dd_opt_representments bulk collect into v_tbl_typ_dd_opt_reprsntmnts;
     close dd_opt_representments;
     if v_tbl_typ_dd_opt_reprsntmnts.count > 0 then
        for i in 1..v_tbl_typ_dd_opt_reprsntmnts.count
        loop
          local_obj := null;
          local_obj := '{
                         "TXN_DDOPTNS_REPRSMNT_FREQ":'||TO_STRING_FNC(v_tbl_typ_dd_opt_reprsntmnts(i).TXN_DDOPTNS_REPRSMNT_FREQ)||',
                         "TXN_DDOPTNS_REPRSMNT_SCODE":'||TO_STRING_FNC(v_tbl_typ_dd_opt_reprsntmnts(i).TXN_DDOPTNS_REPRSMNT_SCODE)||'},';
          tmp_json_array := tmp_json_array||local_obj;
        end loop;
        tmp_json_array := SUBSTR(tmp_json_array, 0, LENGTH(tmp_json_array) - 1);
        tmp_json_array := tmp_json_array||']';
        return tmp_json_array;
     end if;
     return '[]';
    exception
    when others then
     return P_Personal_Account_Nbr||sqlerrm;
    end get_dd_opt_representments_fnc;
  function get_dd_opt_properties_fnc(p_merchant_acct_processor_id VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
    cursor dd_opt_properties is
    SELECT pp.PROPERTY_TYPE_CDE as TXN_DDOPTNS_PROP_CODE,
           ppt.DESCRIPTION as TXN_DDOPTNS_PROP_DESC,
           pp.VALUE as TXN_DDOPTNS_PROP_VALUE
      FROM JUR_ACCT_PROCESSOR_PROPERTIES pp,
           JUR_ACCT_PROC_PROPERTY_TYPS ppt
     WHERE pp.MERCHANT_ACCT_PROCESSOR_ID = p_merchant_acct_processor_id
       and ppt.PROPERTY_TYPE_CDE = pp.PROPERTY_TYPE_CDE
       and pp.PROPERTY_TYPE_CDE not in ('ACCOUNT_NBR','TRANSIT_NBR','INSTITUTION_ID')
    order by ppt.DESCRIPTION;
    type type_dd_opt_properties is record (
                                            TXN_DDOPTNS_PROP_CODE   VARCHAR2(32),
                                            TXN_DDOPTNS_PROP_DESC   VARCHAR2(240),
                                            TXN_DDOPTNS_PROP_VALUE  VARCHAR2(200)
                                           );
    type tbl_dd_opt_properties is table of type_dd_opt_properties index by PLS_INTEGER;
    v_tbl_typ_dd_opt_properties  tbl_dd_opt_properties;
    local_obj varchar2(10000);
    tmp_json_array clob := '[';
    begin
      if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
         return '[]';
      end if;
      open dd_opt_properties ;
      fetch dd_opt_properties bulk collect into v_tbl_typ_dd_opt_properties;
      close dd_opt_properties;
      if v_tbl_typ_dd_opt_properties.count > 0 then
         for i in 1..v_tbl_typ_dd_opt_properties.count
         loop
           local_obj := null;
           local_obj := '{
                          "TXN_DDOPTNS_PROP_CODE":'||TO_STRING_FNC(v_tbl_typ_dd_opt_properties(i).TXN_DDOPTNS_PROP_CODE)||',
                          "TXN_DDOPTNS_PROP_DESC":'||TO_STRING_FNC(v_tbl_typ_dd_opt_properties(i).TXN_DDOPTNS_PROP_DESC)||',
                          "TXN_DDOPTNS_PROP_VALUE":'||TO_STRING_FNC(v_tbl_typ_dd_opt_properties(i).TXN_DDOPTNS_PROP_VALUE)||'},';
            tmp_json_array := tmp_json_array||local_obj;
         end loop;
         tmp_json_array := SUBSTR(tmp_json_array, 0, LENGTH(tmp_json_array) - 1);
         tmp_json_array := tmp_json_array||']';
         return tmp_json_array;
      end if;
      return '[]';
    exception
    when others then
     return p_merchant_acct_processor_id||sqlerrm;
    end get_dd_opt_properties_fnc;
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
            return '{}';
        end if;
        open dd_mm_processor_brands ;
        fetch dd_mm_processor_brands into v_typ_dd_mm_prcsr_brnds;
        close dd_mm_processor_brands;
        if v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_MERCA_ACCOUNT_ID is not null then
           local_obj := '{
                              "TXN_DDOPTNS_MERCA_ACCOUNT_ID":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_MERCA_ACCOUNT_ID)||',
                              "TXN_DDOPTNS_PROCESSOR_ID":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_PROCESSOR_ID)||',
                              "TXN_DDOPTNS_PROCESSOR_PASSWORD":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_PROCESSOR_PASSWORD)||',
                              "TXN_DDOPTNS_PROC_CODE":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_PROC_CODE)||',
                              "TXN_DDOPTNS_MRCHNT_ACCPRCSRID":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_MRCHNT_ACCPRCSRID)||',
                              "TXN_DDOPTNS_REALTIME_PRCSR_MD":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_REALTIME_PRCSR_MD)||',
                              "TXN_DDOPTNS_BATCH_PRCSR_MD":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_BATCH_PRCSR_MD)||',
                              "TXN_DDOPTNS_OUTGNG_BTCH_GK":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_OUTGNG_BTCH_GK)||',
                              "TXN_DDOPTNS_OUTGNG_TXN_GK":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_OUTGNG_TXN_GK)||',
                              "TXN_DDOPTNS_STORE_ID":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_STORE_ID)||',
                              "TXN_DDOPTNS_STORE_PASSWORD":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_STORE_PASSWORD)||',
                              "TXN_DDOPTNS_ACCOST_CDE":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_ACCOST_CDE)||',
                              "TXN_DDOPTNS_DESCRIPTION":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_DESCRIPTION)||',
                              "TXN_DDOPTNS_CURRENCY_TYPE_CDE":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_CURRENCY_TYPE_CDE)||',
                              "TXN_DDOPTNS_OPRTN_PRMSN":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_OPRTN_PRMSN)||',
                              "TXN_DDOPTNS_GRNTD_ECHCK_IND":'||v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_GRNTD_ECHCK_IND||',
                              "TXN_DDOPTNS_ATHRZD_IP_ADRS":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_ATHRZD_IP_ADRS)||',
                              "TXN_DDOPTNS_OVRDRFT_PULL_IND":'||v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_OVRDRFT_PULL_IND||',
                              "TXN_DDOPTNS_OVRDRFT_PL_STS_RSN":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_OVRDRFT_PL_STS_RSN)||',
                              "TXN_DDOPTNS_STMNT_DSCRPTR":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_STMNT_DSCRPTR)||',
                              "TXN_DDOPTNS_BNK_ACCID":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_BNK_ACCID)||',
                              "TXN_DDOPTNS_DEF_FRAUD_SVCS": '||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_DEF_FRAUD_SVCS)||',
							   "TXN_DDOPTNS_SM_DAY_C":'||v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_SM_DAY_C||',
                              "TXN_DDOPTNS_SM_DAY_D":'||v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_SM_DAY_D||',
                              "DD_AT_TXNCHARGE":'||v_typ_dd_mm_prcsr_brnds.DD_AT_TXNCHARGE||',
                              "DD_AT_TXNCREDIT":'||v_typ_dd_mm_prcsr_brnds.DD_AT_TXNCREDIT||',
                              "DD_AT_TXNVERIFY":'||v_typ_dd_mm_prcsr_brnds.DD_AT_TXNVERIFY||',
                               "DD_AT_TXNINFO_LK":'||v_typ_dd_mm_prcsr_brnds.DD_AT_TXNINFO_LK||',
                              "DD_AT_TXNMANDATE":'||v_typ_dd_mm_prcsr_brnds.DD_AT_TXNMANDATE||',
                              "DD_AT_TXNVOID":'||v_typ_dd_mm_prcsr_brnds.DD_AT_TXNVOID||',
                              "TXN_DDOPTNS_OPTRRE":'||v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_OPTRRE||',
                              "TXN_DDOPTNS_MAXMND":'||v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_MAXMND||',
                              "TXN_BANK_ACCOUNT_NAME":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_BANK_ACCOUNT_NAME)||',
                              "TXN_ACCOUNT_NBR_ENC":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_ACCOUNT_NBR_ENC)||',
                              "TXN_ACCOUNT_NBR_HMAC":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_ACCOUNT_NBR_HMAC)||',
                              "TXN_ACCOUNT_LAST_DIGITS":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_ACCOUNT_LAST_DIGITS)||',
                              "TXN_ROUTING_NBR":'||TO_STRING_FNC(v_typ_dd_mm_prcsr_brnds.TXN_ROUTING_NBR)||',
                              "DD_OPT_REPRESENTMENTS":'||get_dd_opt_representments_fnc(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_MERCA_ACCOUNT_ID)||',
                              "DD_OPT_PROPERTIES":'||get_dd_opt_properties_fnc(v_typ_dd_mm_prcsr_brnds.TXN_DDOPTNS_MRCHNT_ACCPRCSRID)||'},';
       end if;
       local_obj := SUBSTR(local_obj, 0, LENGTH(local_obj) - 1);
       return local_obj;
exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_dd_txn_options_fnc;
#

--changeset unknown:perm_get_card_configs_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_dd_txn_options_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_dd_txn_options_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_dd_txn_options_fnc FOR get_dd_txn_options_fnc
#
