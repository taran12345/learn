--liquibase formatted sql
--changeset unknown:get_acc_status_hstry_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_acc_status_hstry_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_acc_status_hstry is
select PAST.DESCRIPTION         AS ASH_STATUS,
       ASRT.DESCRIPTION         AS ASH_REASON_TYPE,
       PAS.COMMENTS             AS ASH_COMMENTS,
       to_char(PAS.CREATION_DATE, 'YYYY-MM-DD HH24:MI:SS') AS ASH_CREATED_ON,
       PAS.CREATED_BY           AS ASH_CREATED_BY
from fpa.PERS_ACCNT_STATS PAS
    left join FPA.PERS_ACCNT_STAT_TYP PAST on PAS.PERSONAL_ACCOUNT_STAT_TYPE_CDE = PAST.PERSONAL_ACCOUNT_STAT_TYPE_CDE
    left join FPA.PERS_ACCNT_STAT_REAS_TYPS ASRT on PAS.STAT_REASON_TYP = ASRT.STAT_REASON_TYP_CDE
where PAS.PERSONAL_ACCOUNT_NBR = to_char(P_PERSONAL_ACCOUNT_NBR)
order by PAS.CREATION_DATE desc;

type typ_acc_status_hstry is record (
                            ASH_STATUS       VARCHAR2(960),
                            ASH_REASON_TYPE  VARCHAR2(960),
                            ASH_COMMENTS     VARCHAR2(960),
                            ASH_CREATED_ON   VARCHAR2(100 CHAR),
                            ASH_CREATED_BY   VARCHAR2(240)
                           );
  type tbl_typ_acc_status_hstry is table of typ_acc_status_hstry index by PLS_INTEGER;
  v_tbl_typ_acc_status_hstry  tbl_typ_acc_status_hstry;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
           return '[]';
        end if;
        open cr_acc_status_hstry ;
        fetch cr_acc_status_hstry bulk collect into v_tbl_typ_acc_status_hstry;
        close cr_acc_status_hstry;
        if v_tbl_typ_acc_status_hstry.count > 0 then
            for i in 1..v_tbl_typ_acc_status_hstry.count
            loop
              local_obj := null;
              local_obj := '{
                              "ASH_STATUS":'||TO_STRING_FNC(v_tbl_typ_acc_status_hstry(i).ASH_STATUS)||',
                              "ASH_REASON_TYPE":'||TO_STRING_FNC(v_tbl_typ_acc_status_hstry(i).ASH_REASON_TYPE)||',
                              "ASH_COMMENTS":'||TO_STRING_FNC(v_tbl_typ_acc_status_hstry(i).ASH_COMMENTS)||',
                              "ASH_CREATED_ON":'||TO_STRING_FNC(v_tbl_typ_acc_status_hstry(i).ASH_CREATED_ON)||',
                              "ASH_CREATED_BY": '||TO_STRING_FNC(v_tbl_typ_acc_status_hstry(i).ASH_CREATED_BY)||'},';
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
end get_acc_status_hstry_fnc;
#

--changeset unknown:perm_get_acc_status_hstry_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_acc_status_hstry_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_acc_status_hstry_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_acc_status_hstry_fnc FOR get_acc_status_hstry_fnc
#
