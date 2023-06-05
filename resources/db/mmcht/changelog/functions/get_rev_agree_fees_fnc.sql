--liquibase formatted sql
--changeset unknown:get_rev_agree_fees_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_rev_agree_fees_fnc(AGREEMENT_ID in number) return clob as
cursor cr_agreement_fees is
select
    FF.DESCRIPTION     AS AF_FEES_NAME,
    AF.FEET_CDE        AS AF_CODE,
    AF.FEE_VALUE       AS AF_FEES_VALUE,
    FF.FEEUT_CDE       AS AF_UNIT_TYPE
from FPA.AGREEMENT_FEES AF,
     FPA.FEE_TYPS FF
where af.AGRE_ID = to_char(AGREEMENT_ID)
        and ff.FEET_CDE = af.FEET_CDE;

type typ_agreement_fees is record (
                                  AF_FEES_NAME    VARCHAR2(960),
                                  AF_CODE         varchar2(30),
                                  AF_FEES_VALUE   number(15,5),
                                  AF_UNIT_TYPE    VARCHAR2(40)
                                 );
  type tbl_typ_agreement_fees is table of typ_agreement_fees index by PLS_INTEGER;
  v_tbl_agreement_fees  tbl_typ_agreement_fees;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        open cr_agreement_fees;
            fetch cr_agreement_fees bulk collect into v_tbl_agreement_fees;
        close cr_agreement_fees;
        if v_tbl_agreement_fees.count > 0 then
            for i in 1..v_tbl_agreement_fees.count
            loop
              local_obj := null;
              local_obj := '{
                              "AF_FEES_NAME":'||TO_STRING_FNC(v_tbl_agreement_fees(i).AF_FEES_NAME)||',
                              "AF_CODE":'||TO_STRING_FNC(v_tbl_agreement_fees(i).AF_CODE)||',
                              "AF_FEES_VALUE":'||TO_STRING_FNC(v_tbl_agreement_fees(i).AF_FEES_VALUE)||',
                              "AF_UNIT_TYPE":'||TO_STRING_FNC(v_tbl_agreement_fees(i).AF_UNIT_TYPE)||'},';
            tmp_json_array := tmp_json_array||local_obj;
            end loop;
            tmp_json_array := SUBSTR(tmp_json_array, 0, LENGTH(tmp_json_array) - 1);
            tmp_json_array := tmp_json_array||']';
            return tmp_json_array;
    end if;
    return '[]';
exception
when others then
 return AGREEMENT_ID||sqlerrm;
end get_rev_agree_fees_fnc;
#

--changeset unknown:perm_get_rev_agree_fees_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_rev_agree_fees_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_rev_agree_fees_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_rev_agree_fees_fnc FOR get_rev_agree_fees_fnc
#
