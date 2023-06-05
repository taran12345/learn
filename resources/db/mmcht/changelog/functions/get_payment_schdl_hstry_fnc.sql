--liquibase formatted sql
--changeset unknown:get_payment_schdl_hstry_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_payment_schdl_hstry_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_payment_schdl_hstry is
select to_char(PSJ.FMAPSJ_FROM_DATE, 'YYYY-MM-DD HH24:MI:SS') AS PSH_MODIFIED_ON,
       PSJ.MODIFIED_BY                          AS PSH_MODIFIED_BY,
       PSJ.FMAPST_CDE                           AS PSH_PAYMENT_FREQ_TYPE,
       PSJ.FMAPFT_CDE                           AS PSH_PAYMENT_FREQ,
       PSJ.DAYS_IN_ARREARS                      AS PSH_DAYS_IS_ARREARS,
       decode(PSJ.ARRDT_CODE, 'CAL', 'Calendar', 'BUS', 'Business', PSJ.ARRDT_CODE)  AS PSH_ARREARS_DAY_TYPE,
       PSJ.PAYMENTS_ON_HOLD                     AS PSH_PAYMENT_ON_HOLD,
       POHR.DESCRIPTION                         AS PSH_ON_HOLD_REASON
from FPA.FMA_PAYMENT_SCHEDULE_JN PSJ
    left join FPA.FMA_PMT_ON_HOLD_REASONS POHR on  PSJ.FPOHR_CODE = POHR.CODE
where PERSONAL_ACCOUNT_NBR = to_char(P_PERSONAL_ACCOUNT_NBR)
order by THRU_DATE desc;

type typ_payment_schdl_hstry is record (
                            PSH_MODIFIED_ON           VARCHAR2(100 CHAR),
                            PSH_MODIFIED_BY           VARCHAR2(200),
                            PSH_PAYMENT_FREQ_TYPE     VARCHAR2(40),
                            PSH_PAYMENT_FREQ          VARCHAR2(40),
                            PSH_DAYS_IS_ARREARS       NUMBER(3),
                            PSH_ARREARS_DAY_TYPE      VARCHAR2(20),
                            PSH_PAYMENT_ON_HOLD       VARCHAR2(5),
                            PSH_ON_HOLD_REASON        VARCHAR2(960)
                           );
  type tbl_typ_payment_schdl_hstry is table of typ_payment_schdl_hstry index by PLS_INTEGER;
  v_tbl_typ_payment_schdl_hstry  tbl_typ_payment_schdl_hstry;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
           return '[]';
        end if;
        open cr_payment_schdl_hstry ;
        fetch cr_payment_schdl_hstry bulk collect into v_tbl_typ_payment_schdl_hstry;
        close cr_payment_schdl_hstry;
        if v_tbl_typ_payment_schdl_hstry.count > 0 then
            for i in 1..v_tbl_typ_payment_schdl_hstry.count
            loop
              local_obj := null;
              local_obj := '{
                              "PSH_MODIFIED_ON":'||TO_STRING_FNC(v_tbl_typ_payment_schdl_hstry(i).PSH_MODIFIED_ON)||',
                              "PSH_MODIFIED_BY":'||TO_STRING_FNC(v_tbl_typ_payment_schdl_hstry(i).PSH_MODIFIED_BY)||',
                              "PSH_PAYMENT_FREQ_TYPE":'||TO_STRING_FNC(v_tbl_typ_payment_schdl_hstry(i).PSH_PAYMENT_FREQ_TYPE)||',
                              "PSH_PAYMENT_FREQ":'||TO_STRING_FNC(v_tbl_typ_payment_schdl_hstry(i).PSH_PAYMENT_FREQ)||',
                              "PSH_DAYS_IS_ARREARS":'||TO_NUMBER_FNC(v_tbl_typ_payment_schdl_hstry(i).PSH_DAYS_IS_ARREARS)||',
                              "PSH_ARREARS_DAY_TYPE":'||TO_STRING_FNC(v_tbl_typ_payment_schdl_hstry(i).PSH_ARREARS_DAY_TYPE)||',
                              "PSH_PAYMENT_ON_HOLD":'||TO_BOOL_FNC(v_tbl_typ_payment_schdl_hstry(i).PSH_PAYMENT_ON_HOLD)||',
                              "PSH_ON_HOLD_REASON": '||TO_STRING_FNC(v_tbl_typ_payment_schdl_hstry(i).PSH_ON_HOLD_REASON)||'},';
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
end get_payment_schdl_hstry_fnc;
#

--changeset unknown:perm_get_payment_schdl_hstry_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_payment_schdl_hstry_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_payment_schdl_hstry_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_payment_schdl_hstry_fnc FOR get_payment_schdl_hstry_fnc
#
