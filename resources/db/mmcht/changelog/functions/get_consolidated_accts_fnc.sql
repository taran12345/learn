--liquibase formatted sql
--changeset unknown:get_consolidated_accts_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_consolidated_accts_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_consolidated_accts is
SELECT PYMT_SHDL.PERSONAL_ACCOUNT_NBR       AS CHILD_ACCT_ACCOUNT_NBR,
       FIREPAY.ACCT_NAME                    AS CHILD_ACCT_ACCOUNT_NAME,
       STAT_TYPE.DESCRIPTION                AS CHILD_ACCT_ACCOUNT_STATUS,
       FIREPAY.CURRENCY_TYPE_CDE            AS CHILD_ACCT_CURRENCY,
       MA.CURR_CODE                         AS CHILD_ACCT_PROCESSING_CURRENCY,
       PYMT_SHDL.DAYS_IN_ARREARS            AS CHILD_ACCT_DAYS_IN_ARREARS,
       decode(PYMT_SHDL.ARRDT_CODE, 'CAL', 'Calendar', 'BUS', 'Business', PYMT_SHDL.ARRDT_CODE) AS CHILD_ACCT_ARREARS_DAY_TYPE,
       PYMT_SHDL.MINIMUM_FLOAT              AS CHILD_ACCT_MINIMUM_FLOAT,
       PYMT_SHDL.MINIMUM_PAYMENT            AS CHILD_ACCT_MINIMUM_PAYMENT,
       to_boolean_fnc(PYMT_SHDL.PAYMENTS_ON_HOLD)   AS CHILD_ACCT_PYMT_ON_HOLD,
       PYMT_SHDL.FPOHR_CODE                 AS CHILD_ACCT_ON_HOLD_REASON_CDE,
       REASON.DESCRIPTION                   AS CHILD_ACCT_ON_HOLD_REASON_DESC
  from FPA.FMA_PAYMENT_SCHEDULE PYMT_SHDL
  INNER JOIN FPA.FIREPAY_PERS_ACCNTS FIREPAY ON FIREPAY.PERSONAL_ACCOUNT_NBR = PYMT_SHDL.PERSONAL_ACCOUNT_NBR
  INNER JOIN FPA.PERS_ACCNT_STATS STAT ON STAT.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR AND STAT.THRU_DATE IS NULL
  INNER JOIN FPA.PERS_ACCNT_STAT_TYP STAT_TYPE ON STAT.PERSONAL_ACCOUNT_STAT_TYPE_CDE = STAT_TYPE.PERSONAL_ACCOUNT_STAT_TYPE_CDE
  LEFT JOIN FPA.FMA_PMT_ON_HOLD_REASONS REASON ON REASON.CODE = PYMT_SHDL.FPOHR_CODE
  LEFT JOIN FALCON.MERCHANT_ACCOUNTS MA ON MA.ACCOUNT_ID = FIREPAY.PERSONAL_ACCOUNT_NBR
  WHERE PYMT_SHDL.CONSOLIDATED_PAYMT_ACCT = to_char(P_PERSONAL_ACCOUNT_NBR)
  ORDER BY PYMT_SHDL.PERSONAL_ACCOUNT_NBR;

type typ_consolidated_accts is record (
                            CHILD_ACCT_ACCOUNT_NBR          VARCHAR2(249),
                            CHILD_ACCT_ACCOUNT_NAME         VARCHAR2(250 CHAR),
                            CHILD_ACCT_ACCOUNT_STATUS       VARCHAR2(240),
                            CHILD_ACCT_CURRENCY             VARCHAR2(10),
                            CHILD_ACCT_PROCESSING_CURRENCY  VARCHAR2(10),
                            CHILD_ACCT_DAYS_IN_ARREARS      NUMBER(3),
                            CHILD_ACCT_ARREARS_DAY_TYPE     VARCHAR2(20),
                            CHILD_ACCT_MINIMUM_FLOAT        NUMBER(12,2),
                            CHILD_ACCT_MINIMUM_PAYMENT      NUMBER(12,2),
                            CHILD_ACCT_PYMT_ON_HOLD         VARCHAR2(5),
                            CHILD_ACCT_ON_HOLD_REASON_CDE   VARCHAR2(10),
                            CHILD_ACCT_ON_HOLD_REASON_DESC  VARCHAR2(240)
                              );
  type tbl_typ_consolidated_accts is table of typ_consolidated_accts index by PLS_INTEGER;
  v_tbl_typ_consolidated_accts  tbl_typ_consolidated_accts;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
            return '[]';
        end if;
        open cr_consolidated_accts ;
        fetch cr_consolidated_accts bulk collect into v_tbl_typ_consolidated_accts;
        close cr_consolidated_accts;
        if v_tbl_typ_consolidated_accts.count > 0 then
            for i in 1..v_tbl_typ_consolidated_accts.count
            loop
              local_obj := null;
              local_obj := '{
                              "CHILD_ACCT_ACCOUNT_NBR":'||TO_STRING_FNC(v_tbl_typ_consolidated_accts(i).CHILD_ACCT_ACCOUNT_NBR)||',
                              "CHILD_ACCT_ACCOUNT_NAME":'||TO_STRING_FNC(v_tbl_typ_consolidated_accts(i).CHILD_ACCT_ACCOUNT_NAME)||',
                              "CHILD_ACCT_ACCOUNT_STATUS":'||TO_STRING_FNC(v_tbl_typ_consolidated_accts(i).CHILD_ACCT_ACCOUNT_STATUS)||',
                              "CHILD_ACCT_CURRENCY":'||'"'||v_tbl_typ_consolidated_accts(i).CHILD_ACCT_CURRENCY||'"'||','||'
                              "CHILD_ACCT_PROCESSING_CURRENCY":'||TO_STRING_FNC(v_tbl_typ_consolidated_accts(i).CHILD_ACCT_PROCESSING_CURRENCY)||','||'
                              "CHILD_ACCT_DAYS_IN_ARREARS":'||'"'||v_tbl_typ_consolidated_accts(i).CHILD_ACCT_DAYS_IN_ARREARS||'"'||','||'
                              "CHILD_ACCT_ARREARS_DAY_TYPE":'||'"'||v_tbl_typ_consolidated_accts(i).CHILD_ACCT_ARREARS_DAY_TYPE||'"'||','||'
                              "CHILD_ACCT_MINIMUM_FLOAT":'||'"'||v_tbl_typ_consolidated_accts(i).CHILD_ACCT_MINIMUM_FLOAT||'"'||','||'
                              "CHILD_ACCT_MINIMUM_PAYMENT": '||to_number_fnc(v_tbl_typ_consolidated_accts(i).CHILD_ACCT_MINIMUM_PAYMENT)||','||'
                              "CHILD_ACCT_PYMT_ON_HOLD":'||'"'||v_tbl_typ_consolidated_accts(i).CHILD_ACCT_PYMT_ON_HOLD||'"'||','||'
                              "CHILD_ACCT_ON_HOLD_REASON_CDE":'||TO_STRING_FNC(v_tbl_typ_consolidated_accts(i).CHILD_ACCT_ON_HOLD_REASON_CDE)||',
                              "CHILD_ACCT_ON_HOLD_REASON_DESC": '||TO_STRING_FNC(v_tbl_typ_consolidated_accts(i).CHILD_ACCT_ON_HOLD_REASON_DESC)||'},';
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
end get_consolidated_accts_fnc;
#

--changeset unknown:perm_get_consolidated_accts_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_consolidated_accts_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_consolidated_accts_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_consolidated_accts_fnc FOR get_consolidated_accts_fnc
#
