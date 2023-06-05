--liquibase formatted sql
--changeset unknown:get_txn_options_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_txn_options_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_mm_processor_brands is
SELECT PIB.MERCA_ACCOUNT_ID              AS TXN_OPTIONS_MERCA_ACCOUNT_ID,
       PIB.BRAN_CODE                     AS TXN_OPTIONS_BRAN_CODE,
       to_boolean_fnc(PIB.CARD_CATEGORY_TYPE_CREDIT_IND)
                                         AS TXN_OPTIONS_CREDIT_IND,
       to_boolean_fnc(PIB.CARD_CATEGORY_TYPE_DEBIT_IND)
                                         AS TXN_OPTIONS_DEBIT_IND,
       PIB.ACCBI_ID                      AS TXN_OPTIONS_ACCBI_ID,
       to_boolean_fnc(PIB.ACTIVE_FLAG)       AS TXN_OPTIONS_ACTIVE_FLAG,
       CBI.CHAC_CDE                      AS TXN_OPTIONS_ACQUIRER_CODE,
       CASE WHEN PIB.DEF_FRAUD_SERVICES LIKE '%R{1%' THEN 'true'
         ELSE 'false'
       END                               AS TXN_OPTIONS_RISKCONTROLS_PASS1,
       CASE
         WHEN PIB.DEF_FRAUD_SERVICES LIKE '%2}%' THEN 'true'
         ELSE 'false'
       END                               AS TXN_OPTIONS_RISKCONTROLS_PASS2,
       CASE
         WHEN PIB.DEF_FRAUD_SERVICES LIKE '%E{Z%' THEN 'true'
         ELSE 'false'
       END                               AS TXN_OPTIONS_RISKCONTROLS_AVS,
       CASE
         WHEN PIB.DEF_FRAUD_SERVICES LIKE '%V}%' THEN 'true'
         ELSE 'false'
       END                               AS TXN_OPTIONS_RISKCONTROLS_CVS,
       CASE
         WHEN PIB.DEF_FRAUD_SERVICES LIKE '%TDS%' THEN 'true'
         ELSE 'false'
       END                               AS TXN_OPTIONS_RISKCONTROLS_3DS ,
       CASE
         WHEN PIB.DEF_FRAUD_SERVICES LIKE '%RT{S%' THEN 'true'
         ELSE 'false'
       END                               AS TXN_OPTIONS_REALTIMESETTLEMENT,
       CASE
         WHEN PIB.DEF_FRAUD_SERVICES LIKE '%C}%' THEN 'true'
         ELSE 'false'
       END                               AS TXN_OPTIONS_REALTIMECREDIT,
       to_boolean_fnc(PIB.REALTIME_OCT_IND)  AS TXN_OPTIONS_REALTIMEOCT,
       to_boolean_fnc(PIB.ALLOW_CREDIT)  AS TXN_OPTIONS_CREDITS,
       to_boolean_fnc(PIB.CARD_UPDATER_ENABLED_IND)  AS TXN_OPTIONS_UPDATER,
       PIB.baac_id                       AS TXN_OPTIONS_SB_ID,
       BA.bank_account_name              AS TXN_OPTIONS_SB_NAME,
       BA.DESCRIPTION                    AS TXN_OPTIONS_SB_DESCRIPTION,
       BA.bank_cde                       AS TXN_OPTIONS_SB_CODE,
       BA.curr_code                      AS TXN_OPTIONS_SB_CURRENCY,
       PIB.proci_proc_code               AS TXN_OPTIONS_PROCESSORCODE,
       PIB.proci_merchant_nbr            AS TXN_OPTIONS_CHID,
       PIB.MAX_EXTRA_CREDIT_AMOUNT       AS TXN_OPTIONS_COS_AMOUNT,
       PIB.MAX_EXTRA_CREDIT_PERCENT      AS TXN_OPTIONS_COS_PERCENT,
       PI.C01                            AS TXN_OPTIONS_C01,
       PI.C02                            AS TXN_OPTIONS_C02,
       PI.C03                            AS TXN_OPTIONS_C03,
       PI.C04                            AS TXN_OPTIONS_C04,
       PI.C05                            AS TXN_OPTIONS_C05,
       PI.SIC                            AS TXN_OPTIONS_SIC_CODE,
       to_boolean_fnc(PI.JV_FLAG)        AS TXN_OPTIONS_JVFLAG,
       PI.BUSINESS_CASE_ID               AS TXN_OPTIONS_BUSINESS_CASE_ID,
       PIB.TDS_USERNAME                  AS TXN_OPTIONS_TDS_USERNAME,
       PIB.TDS_PASSWORD                  AS TXN_OPTIONS_TDS_PASSWORD,
       PIB.TDS_ACQUIRER_BIN              AS TXN_OPTIONS_TDS_ACQUIRER_BIN,
       get_card_brand_props_fnc(PIB.MERCA_ACCOUNT_ID, PIB.proci_merchant_nbr, PIB.proci_proc_code, PIB.BRAN_CODE)   AS TXN_OPTIONS_BRAND_PROPS,
       to_char(PIB.CREATION_DATE, 'YYYY-MM-DD HH24:MI:SS')    AS TXN_OPTIONS_CREATION_DATE,
       to_char(PIB.MODIFICATION_DATE,'YYYY-MM-DD HH24:MI:SS') AS TXN_OPTIONS_MODIFICATION_DATE
  from FALCON.PROCESSOR_INFO_BRANDS PIB
  INNER JOIN ACQUIRER_CB_IDENTIFIERS CBI  ON CBI.ACCBI_ID = PIB.ACCBI_ID  AND CBI.BAAC_ID = PIB.BAAC_ID
  INNER JOIN FALCON.BANK_ACCOUNTS BA      ON BA.BAAC_ID = PIB.BAAC_ID
  LEFT JOIN FALCON.PROCESSOR_INFOS PI     ON PI.PROC_CODE = PIB.PROCI_PROC_CODE
    and PI.MERCHANT_NBR = PIB.PROCI_MERCHANT_NBR and PI.BAAC_ID = PIB.BAAC_ID
  INNER JOIN ( select * from (
                        SELECT pib2.merca_account_id , pib2.bran_code , pib2.creation_date, pib2.active_flag,
                               row_number() over ( partition by pib2.merca_account_id , pib2.bran_code
                                                   order by pib2.active_flag desc ) active_Rank
                          FROM falcon.processor_info_brands pib2
                         )
                        where active_Rank = 1
              ) flag_rank ON PIB.MERCA_ACCOUNT_ID = flag_rank.MERCA_ACCOUNT_ID
   and PIB.bran_code = flag_rank.bran_code
   and PIB.creation_date = flag_rank.creation_date
   and PIB.active_flag = flag_rank.active_flag
 where PIB.MERCA_ACCOUNT_ID = to_char(P_PERSONAL_ACCOUNT_NBR);
type typ_mm_processor_brands is record (
                                        TXN_OPTIONS_MERCA_ACCOUNT_ID       VARCHAR2(10),
                                        TXN_OPTIONS_BRAN_CODE              VARCHAR2(2),
                                        TXN_OPTIONS_CREDIT_IND             VARCHAR2(5),
                                        TXN_OPTIONS_DEBIT_IND              NVARCHAR2(5),
                                        TXN_OPTIONS_ACCBI_ID               VARCHAR2(16),
                                        TXN_OPTIONS_ACTIVE_FLAG            VARCHAR2(5),
                                        TXN_OPTIONS_ACQUIRER_CODE          VARCHAR2(10),
                                        TXN_OPTIONS_RISKCONTROLS_PASS1     VARCHAR2(5),
                                        TXN_OPTIONS_RISKCONTROLS_PASS2     VARCHAR2(5),
                                        TXN_OPTIONS_RISKCONTROLS_AVS       VARCHAR2(5),
                                        TXN_OPTIONS_RISKCONTROLS_CVS       VARCHAR2(5),
                                        TXN_OPTIONS_RISKCONTROLS_3DS       VARCHAR2(5),
                                        TXN_OPTIONS_REALTIMESETTLEMENT     VARCHAR2(5),
                                        TXN_OPTIONS_REALTIMECREDIT         VARCHAR2(5),
                                        TXN_OPTIONS_REALTIMEOCT            VARCHAR2(5),
                                        TXN_OPTIONS_CREDITS                VARCHAR2(5),
                                        TXN_OPTIONS_UPDATER                VARCHAR2(5),
                                        TXN_OPTIONS_SB_ID                  VARCHAR2(12),
                                        TXN_OPTIONS_SB_NAME                VARCHAR2(100),
                                        TXN_OPTIONS_SB_DESCRIPTION         VARCHAR2(240),
                                        TXN_OPTIONS_SB_CODE                VARCHAR2(5),
                                        TXN_OPTIONS_SB_CURRENCY            VARCHAR2(10),
                                        TXN_OPTIONS_PROCESSORCODE          VARCHAR2(3),
                                        TXN_OPTIONS_CHID                   VARCHAR2(24),
                                        TXN_OPTIONS_COS_AMOUNT             NUMBER(4),
                                        TXN_OPTIONS_COS_PERCENT            NUMBER(3),
                                        TXN_OPTIONS_C01                    VARCHAR2(255),
                                        TXN_OPTIONS_C02                    VARCHAR2(4000),
                                        TXN_OPTIONS_C03                    VARCHAR2(4000),
                                        TXN_OPTIONS_C04                    VARCHAR2(255),
                                        TXN_OPTIONS_C05                    VARCHAR2(255),
                                        TXN_OPTIONS_SIC_CODE               VARCHAR2(4 CHAR),
                                        TXN_OPTIONS_JVFLAG                 VARCHAR2(5),
                                        TXN_OPTIONS_BUSINESS_CASE_ID       VARCHAR2(50 CHAR),
                                        TXN_OPTIONS_TDS_USERNAME           VARCHAR2(50 CHAR),
                                        TXN_OPTIONS_TDS_PASSWORD           VARCHAR2(50 CHAR),
                                        TXN_OPTIONS_TDS_ACQUIRER_BIN       VARCHAR2(50 CHAR),
                                        TXN_OPTIONS_BRAND_PROPS            CLOB,
                                        TXN_OPTIONS_CREATION_DATE          VARCHAR2(100),
                                        TXN_OPTIONS_MODIFICATION_DATE      VARCHAR2(100)
                                 );
  type tbl_typ_mm_processor_brands is table of typ_mm_processor_brands index by PLS_INTEGER;
  v_tbl_typ_mm_processor_brands  tbl_typ_mm_processor_brands;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
            return '[]';
        end if;
        open cr_mm_processor_brands ;
        fetch cr_mm_processor_brands bulk collect into v_tbl_typ_mm_processor_brands;
        close cr_mm_processor_brands;
        if v_tbl_typ_mm_processor_brands.count > 0 then
            for i in 1..v_tbl_typ_mm_processor_brands.count
            loop
              local_obj := null;
              local_obj := '{
                              "TXN_OPTIONS_MERCA_ACCOUNT_ID":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).TXN_OPTIONS_MERCA_ACCOUNT_ID)||',
                              "TXN_OPTIONS_BRAN_CODE":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).TXN_OPTIONS_BRAN_CODE)||',
                              "TXN_OPTIONS_CREDIT_IND":'||'"'||v_tbl_typ_mm_processor_brands(i).TXN_OPTIONS_CREDIT_IND||'"'||','||'
                              "TXN_OPTIONS_DEBIT_IND":'||'"'||v_tbl_typ_mm_processor_brands(i).TXN_OPTIONS_DEBIT_IND||'"'||','||'
                              "TXN_OPTIONS_ACCBI_ID":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).TXN_OPTIONS_ACCBI_ID)||',
                              "TXN_OPTIONS_ACQUIRER_CODE":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_acquirer_code)||',
                              "TXN_OPTIONS_RISKCONTROLS_PASS1":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_riskcontrols_pass1||'"'||','||'
                              "TXN_OPTIONS_RISKCONTROLS_PASS2":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_riskcontrols_pass2||'"'||','||'
                              "TXN_OPTIONS_RISKCONTROLS_AVS":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_riskcontrols_avs||'"'||','||'
                              "TXN_OPTIONS_RISKCONTROLS_CVS":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_riskcontrols_cvs||'"'||','||'
                              "TXN_OPTIONS_REALTIMESETTLEMENT":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_realtimesettlement||'"'||','||'
                              "TXN_OPTIONS_REALTIMECREDIT":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_realtimecredit||'"'||','||'
                              "TXN_OPTIONS_REALTIMEOCT":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_realtimeoct||'"'||','||'
                              "TXN_OPTIONS_CREDITS":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_credits||'"'||','||'
                              "TXN_OPTIONS_UPDATER":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_updater||'"'||','||'
                              "TXN_OPTIONS_RISKCONTROLS_3DS":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_riskcontrols_3ds||'"'||','||'
                              "TXN_OPTIONS_SB_ID":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_sb_id)||',
                              "TXN_OPTIONS_SB_NAME":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_sb_name)||',
                              "TXN_OPTIONS_SB_DESCRIPTION":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_sb_description)||',
                              "TXN_OPTIONS_SB_CODE":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_sb_code)||',
                              "TXN_OPTIONS_SB_CURRENCY":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_sb_currency)||',
                              "TXN_OPTIONS_PROCESSORCODE":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_processorcode)||',
                              "TXN_OPTIONS_CHID":'||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_chid)||',
                              "TXN_OPTIONS_ACTIVE_FLAG": '||'"'||v_tbl_typ_mm_processor_brands(i).TXN_OPTIONS_ACTIVE_FLAG||'"'||','||'
                              "TXN_OPTIONS_COS_AMOUNT": '||to_number_fnc(v_tbl_typ_mm_processor_brands(i).TXN_OPTIONS_COS_AMOUNT)||','||'
                              "TXN_OPTIONS_COS_PERCENT": '||to_number_fnc(v_tbl_typ_mm_processor_brands(i).TXN_OPTIONS_COS_PERCENT)||','||'
                              "TXN_OPTIONS_C01": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_c01)||','||'
                              "TXN_OPTIONS_C02": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_c02)||','||'
                              "TXN_OPTIONS_C03": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_c03)||','||'
                              "TXN_OPTIONS_C04": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_c04)||','||'
                              "TXN_OPTIONS_C05": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_c05)||','||'
                              "TXN_OPTIONS_SIC_CODE": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_sic_code)||','||'
                              "TXN_OPTIONS_JVFLAG":'||'"'||v_tbl_typ_mm_processor_brands(i).txn_options_jvflag||'"'||','||'
                              "TXN_OPTIONS_BUSINESS_CASE_ID": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_business_case_id)||','||'
                              "TXN_OPTIONS_TDS_USERNAME": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_tds_username)||','||'
                              "TXN_OPTIONS_TDS_PASSWORD": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_tds_password)||','||'
                              "TXN_OPTIONS_TDS_ACQUIRER_BIN": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_tds_acquirer_bin)||','||'
                              "TXN_OPTIONS_BRAND_PROPS":'||v_tbl_typ_mm_processor_brands(i).txn_options_brand_props||','||'
                              "TXN_OPTIONS_CREATION_DATE": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_creation_date)||','||'
                              "TXN_OPTIONS_MODIFICATION_DATE": '||TO_STRING_FNC(v_tbl_typ_mm_processor_brands(i).txn_options_modification_date)||'},';
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
end get_txn_options_fnc;
#

--changeset unknown:perm_get_json_list_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_txn_options_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_txn_options_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_txn_options_fnc FOR get_txn_options_fnc
#
