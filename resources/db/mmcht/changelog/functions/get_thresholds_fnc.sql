--liquibase formatted sql
--changeset unknown:get_thresholds_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_thresholds_fnc(p_personal_account_nbr VARCHAR2 , p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_thresholds is
SELECT  tht.description as THRESHOLDS_DESCRIPTION,
        freq.CDE        as THRESHOLDS_FREQ_TYPE,
        tht.CDE         as THRESHOLDS_THRETY_TYPE,
        th.MIN_AMOUNT as THRESHOLDS_MIN_AMOUNT,
        th.MAX_AMOUNT as THRESHOLDS_MAX_AMOUNT,
        th.MIN_COUNT as THRESHOLDS_MIN_COUNT,
        th.MAX_COUNT as THRESHOLDS_MAX_COUNT,
        th.tag_value as THRESHOLDS_TAG_VALUE,
        to_boolean_fnc(th.rre_enforce_ind) AS THRESHOLDS_RRE_ENFORCE
  FROM FPA_FMA_TXN_THRESHOLD_TYPS tht,
       FPA_FREQUENCY_TYPS freq,
       FPA_FMA_TXN_THRESHOLDS th
  WHERE th.PERSONAL_ACCOUNT_NBR = to_char(P_PERSONAL_ACCOUNT_NBR)
        AND th.THRESHOLD_TYP_CDE = tht.CDE
        AND th.FREQUENCY_TYP_CDE = tht.FREQUENCY_TYP_CDE
        AND tht.FREQUENCY_TYP_CDE = freq.CDE
        AND tht.ACTIVE_IND = 'Y'
  ORDER BY th.THRESHOLD_TYP_CDE,
           th.FREQUENCY_TYP_CDE;

type typ_thresholds is record (
                            THRESHOLDS_DESCRIPTION    VARCHAR2(240 ),
                            THRESHOLDS_FREQ_TYPE      VARCHAR2(10 ),
                            THRESHOLDS_THRETY_TYPE    VARCHAR2(240 ),
                            THRESHOLDS_MIN_AMOUNT     NUMBER(38,2),
                            THRESHOLDS_MAX_AMOUNT     NUMBER(38,2),
                            THRESHOLDS_MIN_COUNT      NUMBER(30),
                            THRESHOLDS_MAX_COUNT      NUMBER(30),
                            THRESHOLDS_TAG_VALUE      VARCHAR2(200),
                            THRESHOLDS_RRE_ENFORCE    VARCHAR2(5 CHAR)
                           );
  type tbl_typ_thresholds is table of typ_thresholds index by PLS_INTEGER;
  v_tbl_typ_thresholds  tbl_typ_thresholds;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
           return '[]';
        end if;
        open cr_thresholds ;
        fetch cr_thresholds bulk collect into v_tbl_typ_thresholds;
        close cr_thresholds;
        if v_tbl_typ_thresholds.count > 0 then
            for i in 1..v_tbl_typ_thresholds.count
            loop
              local_obj := null;
              local_obj := '{
                              "THRESHOLDS_DESCRIPTION":'||'"'||v_tbl_typ_thresholds(i).THRESHOLDS_DESCRIPTION||'"'||','||'
                              "THRESHOLDS_FREQ_TYPE":'||'"'||v_tbl_typ_thresholds(i).THRESHOLDS_FREQ_TYPE||'"'||','||'
                              "THRESHOLDS_THRETY_TYPE":'||'"'||v_tbl_typ_thresholds(i).THRESHOLDS_THRETY_TYPE||'"'||','||'
                              "THRESHOLDS_MIN_AMOUNT": '||to_number_fnc(v_tbl_typ_thresholds(i).THRESHOLDS_MIN_AMOUNT)||','||'
                              "THRESHOLDS_MAX_AMOUNT": '||to_number_fnc(v_tbl_typ_thresholds(i).THRESHOLDS_MAX_AMOUNT)||','||'
                              "THRESHOLDS_MIN_COUNT": '||to_number_fnc(v_tbl_typ_thresholds(i).THRESHOLDS_MIN_COUNT)||','||'
                              "THRESHOLDS_MAX_COUNT": '||to_number_fnc(v_tbl_typ_thresholds(i).THRESHOLDS_MAX_COUNT)||','||'
                              "THRESHOLDS_TAG_VALUE": '||TO_STRING_FNC(v_tbl_typ_thresholds(i).THRESHOLDS_TAG_VALUE)||','||'
                              "THRESHOLDS_RRE_ENFORCE": '||v_tbl_typ_thresholds(i).THRESHOLDS_RRE_ENFORCE||'},';
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
end get_thresholds_fnc;
#

--changeset unknown:perm_get_thresholds_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_thresholds_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_thresholds_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_thresholds_fnc FOR get_thresholds_fnc
#
