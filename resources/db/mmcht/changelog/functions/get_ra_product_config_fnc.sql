--liquibase formatted sql
--changeset unknown:get_ra_product_config_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_ra_product_config_fnc(p_personal_account_nbr in varchar2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_ra_product_config is
select distinct AT2.AGRE_ID RAB_AGREEMENT_ID from
      FPA.AGREEMENT_TERMS AT2
      inner join FPA.AGREEMENTS A1 on AT2.agre_id = A1.agre_id
      inner join
      (
        SELECT  AA.AGRE_ID agre_id, A2.PROCESSING_TYPE processing_type
        FROM FPA.ACCT_AGREEMENTS AA
            INNER JOIN FPA.AGREEMENTS A2
                ON (A2.AGRE_ID = aa.AGRE_ID AND A2.THRU_DATE IS NULL)
        WHERE aa.PERSONAL_ACCOUNT_NBR = to_char(p_personal_account_nbr)
      ) A on A1.AGRE_ID_BASED_ON = A.agre_id
      where AT2.termt_cde = 'CARD_TYPE' and
      (
      (A.processing_type = 'CC' and AT2.term_value not in ('ECHECK', 'OB')) or
      (A.processing_type = 'DD' and AT2.term_value = 'ECHECK') or
      (A.processing_type != 'CC' and A.processing_type != 'DD' and AT2.term_value = A.processing_type)
      );

type typ_ra_product_config is record (
                                  RAB_AGREEMENT_ID   varchar2(12)
                                 );
  type tbl_typ_agreement_term is table of typ_ra_product_config index by PLS_INTEGER;
  v_tbl_ra_product_config  tbl_typ_agreement_term;
  local_obj varchar2(20000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
           return '[]';
        end if;
        open cr_ra_product_config;
            fetch cr_ra_product_config bulk collect into v_tbl_ra_product_config;
        close cr_ra_product_config;
        if v_tbl_ra_product_config.count > 0 then
            for i in 1..v_tbl_ra_product_config.count
            loop
              local_obj := null;
              local_obj := '{
                              "RAB_AGREEMENT_ID":'||TO_STRING_FNC(v_tbl_ra_product_config(i).RAB_AGREEMENT_ID)||',
                              "RAB_AGREEMENT_TERM":'||GET_REV_AGREE_TERMS_FNC(v_tbl_ra_product_config(i).RAB_AGREEMENT_ID)||',
                              "RAB_AGREEMENT_FEES":'||GET_REV_AGREE_FEES_FNC(v_tbl_ra_product_config(i).RAB_AGREEMENT_ID)||'},';
            tmp_json_array := tmp_json_array||local_obj;
            end loop;
            tmp_json_array := SUBSTR(tmp_json_array, 0, LENGTH(tmp_json_array) - 1);
            tmp_json_array := tmp_json_array||']';
            return tmp_json_array;
    end if;
    return '[]';
exception
when others then
 return p_personal_account_nbr||sqlerrm;
end get_ra_product_config_fnc;
#

--changeset unknown:perm_get_ra_product_config_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_ra_product_config_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_ra_product_config_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_ra_product_config_fnc FOR get_ra_product_config_fnc
#
