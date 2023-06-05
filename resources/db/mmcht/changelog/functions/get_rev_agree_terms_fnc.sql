--liquibase formatted sql
--changeset unknown:get_rev_agree_terms_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_rev_agree_terms_fnc(AGREEMENT_ID in number) return clob as
cursor cr_agreement_terms is
select
    at.TERMT_CDE   AS AT_TERM_CODES,
    at.TERM_VALUE  AS AT_TERM_VALUE,
    tt.DESCRIPTION AS AT_TERM_NAME
from FPA.AGREEMENT_TERMS at
     INNER JOIN FPA.TERM_TYPS tt on at.TERMT_CDE = tt.TERMT_CDE
where at.AGRE_ID = to_char(AGREEMENT_ID);

type typ_agreement_terms is record (
                                  AT_TERM_CODES         VARCHAR2(100),
                                  AT_TERM_VALUE         VARCHAR2(100),
                                  AT_TERM_NAME          VARCHAR2(200)
                                 );
  type tbl_typ_agreement_term is table of typ_agreement_terms index by PLS_INTEGER;
  v_tbl_agreement_terms  tbl_typ_agreement_term;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        open cr_agreement_terms;
            fetch cr_agreement_terms bulk collect into v_tbl_agreement_terms;
        close cr_agreement_terms;
        if v_tbl_agreement_terms.count > 0 then
            for i in 1..v_tbl_agreement_terms.count
            loop
              local_obj := null;
              local_obj := '{
                              "AT_TERM_CODE":'||TO_STRING_FNC(v_tbl_agreement_terms(i).AT_TERM_CODES)||',
                              "AT_TERM_VALUE":'||TO_STRING_FNC(v_tbl_agreement_terms(i).AT_TERM_VALUE)||',
                              "AT_TERM_NAME":'||TO_STRING_FNC(v_tbl_agreement_terms(i).AT_TERM_NAME)||'},';
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
end get_rev_agree_terms_fnc;
#

--changeset unknown:perm_get_rev_agree_terms_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_rev_agree_terms_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_rev_agree_terms_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_rev_agree_terms_fnc FOR get_rev_agree_terms_fnc
#