--liquibase formatted sql
--changeset unknown:get_linked_accts_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_linked_accts_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_linked_accts is
SELECT FIREPAY.PERSONAL_ACCOUNT_NBR     AS LINK_ACCT_ACCOUNT_NBR,
       FIREPAY.ACCT_NAME                AS LINK_ACCT_ACCOUNT_NAME,
       STAT_TYPE.DESCRIPTION            AS LINK_ACCT_ACCOUNT_STATUS,
       CAT.DESCRIPTION                  AS LINK_ACCT_ACCOUNT_CATEGORY,
       FIREPAY.CURRENCY_TYPE_CDE        AS LINK_ACCT_CURRENCY,
       MA.CURR_CODE                     AS LINK_ACCT_PROCESSING_CURRENCY,
       FSBS.PMTSBS_SETUP_ID             AS LINK_ACCT_SOURCE_BANK_ID
  from FPA.FIREPAY_PERS_ACCNTS FIREPAY
  INNER JOIN FPA.PERS_ACCNT_STATS STAT ON STAT.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR
  INNER JOIN FPA.PERS_ACCNT_STAT_TYP STAT_TYPE ON STAT.PERSONAL_ACCOUNT_STAT_TYPE_CDE = STAT_TYPE.PERSONAL_ACCOUNT_STAT_TYPE_CDE
  INNER JOIN FPA.FPA_MERCH_ACCT_CATEGORY_TYPS CAT ON CAT.ACCOUNT_CATEGORY_CDE = FIREPAY.MERACT_CDE
  LEFT JOIN FPA.PMT_FMA_SOURCE_BANK_SETUPS FSBS ON FSBS.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR
    AND FSBS.THRU_DATE IS NULL
  LEFT JOIN FALCON.MERCHANT_ACCOUNTS MA ON MA.ACCOUNT_ID = FIREPAY.PERSONAL_ACCOUNT_NBR
  WHERE FIREPAY.PERSONAL_ACCOUNT_NBR IN
    ( SELECT PERSONAL_ACCOUNT_NBR_LINKED_TO
          FROM FPA.FPA_SPLIT_TRANSFERS_ACCOUNTS
          WHERE PERSONAL_ACCOUNT_NBR = to_char(p_personal_account_nbr)
          UNION
          SELECT PERSONAL_ACCOUNT_NBR
          FROM FPA.FPA_SPLIT_TRANSFERS_ACCOUNTS
          WHERE PERSONAL_ACCOUNT_NBR_LINKED_TO = to_char(p_personal_account_nbr)
    )
    AND STAT.THRU_DATE IS NULL;

type typ_linked_accts is record (
                            LINK_ACCT_ACCOUNT_NBR           VARCHAR2(249),
                            LINK_ACCT_ACCOUNT_NAME          VARCHAR2(250 CHAR),
                            LINK_ACCT_ACCOUNT_STATUS        VARCHAR2(240),
                            LINK_ACCT_ACCOUNT_CATEGORY      VARCHAR2(240 CHAR),
                            LINK_ACCT_CURRENCY              VARCHAR2(10),
                            LINK_ACCT_PROCESSING_CURRENCY   VARCHAR2(10),
                            LINK_ACCT_SOURCE_BANK_ID        NUMBER(12)
                              );
  type tbl_typ_linked_accts is table of typ_linked_accts index by PLS_INTEGER;
  v_tbl_typ_linked_accts  tbl_typ_linked_accts;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
            return '[]';
        end if;
        open cr_linked_accts ;
        fetch cr_linked_accts bulk collect into v_tbl_typ_linked_accts;
        close cr_linked_accts;
        if v_tbl_typ_linked_accts.count > 0 then
            for i in 1..v_tbl_typ_linked_accts.count
            loop
              local_obj := null;
              local_obj := '{
                              "LINK_ACCT_ACCOUNT_NBR":'||TO_STRING_FNC(v_tbl_typ_linked_accts(i).LINK_ACCT_ACCOUNT_NBR)||',
                              "LINK_ACCT_ACCOUNT_NAME":'||TO_STRING_FNC(v_tbl_typ_linked_accts(i).LINK_ACCT_ACCOUNT_NAME)||',
                              "LINK_ACCT_ACCOUNT_STATUS":'||TO_STRING_FNC(v_tbl_typ_linked_accts(i).LINK_ACCT_ACCOUNT_STATUS)||',
                              "LINK_ACCT_ACCOUNT_CATEGORY":'||TO_STRING_FNC(v_tbl_typ_linked_accts(i).LINK_ACCT_ACCOUNT_CATEGORY)||',
                              "LINK_ACCT_CURRENCY":'||'"'||v_tbl_typ_linked_accts(i).LINK_ACCT_CURRENCY||'"'||','||'
                              "LINK_ACCT_PROCESSING_CURRENCY":'||TO_STRING_FNC(v_tbl_typ_linked_accts(i).LINK_ACCT_PROCESSING_CURRENCY)||',
                              "LINK_ACCT_SOURCE_BANK_ID": '||TO_NUMBER_FNC(v_tbl_typ_linked_accts(i).LINK_ACCT_SOURCE_BANK_ID)||'},';
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
end get_linked_accts_fnc;
#

--changeset unknown:perm_get_linked_accts_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_linked_accts_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_linked_accts_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_linked_accts_fnc FOR get_linked_accts_fnc
#
