--liquibase formatted sql
--changeset unknown:get_card_brand_props_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_card_brand_props_fnc(p_personal_account_nbr VARCHAR2, p_chid VARCHAR2,
    p_proc_code VARCHAR2, p_bran_code VARCHAR2) return clob as
cursor cr_card_brand_props is
SELECT PROPERTY_CDE                      AS BP_PROPERTY_CDE,
       PROPERTY_VALUE                     AS BP_PROPERTY_VALUE
 from FALCON.FCN_PROCESSOR_INFO_BRAND_PROPS
 where MERCA_ACCOUNT_ID = to_char(P_PERSONAL_ACCOUNT_NBR)
             and MERCHANT_NBR = to_char(p_chid)
             and PROC_CODE = to_char(p_proc_code)
             and BRAND = to_char(p_bran_code);
type typ_card_brand_props is record (
                                        BP_PROPERTY_CDE                 VARCHAR2(30 CHAR),
                                        BP_PROPERTY_VALUE               VARCHAR2(255 CHAR)
                                 );
  type tbl_typ_card_brand_props is table of typ_card_brand_props index by PLS_INTEGER;
  v_tbl_typ_card_brand_props  tbl_typ_card_brand_props;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        open cr_card_brand_props ;
        fetch cr_card_brand_props bulk collect into v_tbl_typ_card_brand_props;
        close cr_card_brand_props;
        if v_tbl_typ_card_brand_props.count > 0 then
            for i in 1..v_tbl_typ_card_brand_props.count
            loop
              local_obj := null;
              local_obj := '{
                              "BP_PROPERTY_CDE":'||TO_STRING_FNC(v_tbl_typ_card_brand_props(i).BP_PROPERTY_CDE)||',
                              "BP_PROPERTY_VALUE": '||TO_STRING_FNC(v_tbl_typ_card_brand_props(i).BP_PROPERTY_VALUE)||'},';
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
end get_card_brand_props_fnc;
#

--changeset unknown:perm_get_json_list_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_card_brand_props_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_card_brand_props_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_card_brand_props_fnc FOR get_card_brand_props_fnc
#
