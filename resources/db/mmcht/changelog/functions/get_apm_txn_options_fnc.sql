--liquibase formatted sql
--changeset unknown:get_card_brand_props_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_apm_txn_options_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor apm_mm_processor_brands is
SELECT
      ma.account_id as TXN_APMOPTNS_ACCNTID,
	  ma.store_id as TXN_APMOPTNS_STOREID,
	  ma.store_pwd TXN_APMOPTNS_STOREPWD,
	  ma.ip_addresses as TXN_APMOPTNS_IPADDRS,
	  ag.authentication_gwy_code as TXN_APMOPTNS_AUTH_GWAY_C,
	  ag.gwy_merchant_nbr as TXN_APMOPTNS_M_NBR,
	  pg.chid as TXN_APMOPTNS_CHID,
	  pg.password TXN_APMOPTNS_APIT,
	 pg.payment_gwy_code as TXN_APMOPTNS_PAY_GWAY_C,
	 ma.currency_typ_cde as TXN_APMOPTNS_CURR_C,
	 ma.account_status as TXN_APMOPTNS_STATUS,
     CASE WHEN ma.altp_account_ind LIKE '%Y%' THEN 'ALTP' ELSE 'OB' END AS TXN_APM_ACCOUNT_TYPE
	FROM
		dco_merchant_accounts ma,
		dco_authen_gwy_acct_infos ag,
		dco_payment_gwy_acct_infos pg
	WHERE ma.account_id = to_char(p_personal_account_nbr)
	AND ag.merchant_account_id(+) = ma.account_id
	AND pg.merchant_account_id(+) = ma.account_id;

  v_typ_apm_mm_prcsr_brnds  apm_mm_processor_brands%rowtype;
  local_obj varchar2(10000);

  function get_apm_opt_properties_fnc(p_merchant_acct_processor_id VARCHAR2,authen_gwy_code VARCHAR2,p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
    cursor apm_opt_properties is
    SELECT pp.authentication_gwy_code as APM_OPTNS_PROC_C,
		pp.authen_gwy_property_typ_cde as APM_OPTNS_PROP_P,
		dp.display_text as APM_OPTNS_DTEXT,
		pp.value as APM_OPTNS_VALUE
	FROM dco_authen_gwy_acct_props pp,
		dco_processor_properties dp
	WHERE pp.authentication_gwy_code = authen_gwy_code
	AND pp.merchant_account_id = to_char(p_merchant_acct_processor_id)
	AND dp.authen_gwy_property_typ_cde = pp.authen_gwy_property_typ_cde
	AND dp.authentication_gwy_code = pp.authentication_gwy_code;
    type type_apm_opt_properties is record (
                                            APM_OPTNS_PROC_C   VARCHAR2(10),
                                            APM_OPTNS_PROP_P   VARCHAR2(30),
                                            APM_OPTNS_DTEXT  VARCHAR2(200),
											APM_OPTNS_VALUE VARCHAR2(200)
                                           );
    type tbl_apm_opt_properties is table of type_apm_opt_properties index by PLS_INTEGER;
    v_tbl_typ_apm_opt_properties  tbl_apm_opt_properties;
    local_obj varchar2(10000);
    tmp_json_array clob := '[';
    begin
      if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
         return '[]';
      end if;
      open apm_opt_properties ;
      fetch apm_opt_properties bulk collect into v_tbl_typ_apm_opt_properties;
      close apm_opt_properties;
      if v_tbl_typ_apm_opt_properties.count > 0 then
         for i in 1..v_tbl_typ_apm_opt_properties.count
         loop
           local_obj := null;
           local_obj := '{
                          "APM_OPTNS_PROC_C":'||TO_STRING_FNC(v_tbl_typ_apm_opt_properties(i).APM_OPTNS_PROC_C)||',
                          "APM_OPTNS_PROP_P":'||TO_STRING_FNC(v_tbl_typ_apm_opt_properties(i).APM_OPTNS_PROP_P)||',
                          "APM_OPTNS_DTEXT":'||TO_STRING_FNC(v_tbl_typ_apm_opt_properties(i).APM_OPTNS_DTEXT)||',
						  "APM_OPTNS_VALUE":'||TO_STRING_FNC(v_tbl_typ_apm_opt_properties(i).APM_OPTNS_VALUE)||'},';
            tmp_json_array := tmp_json_array||local_obj;
         end loop;
         tmp_json_array := SUBSTR(tmp_json_array, 0, LENGTH(tmp_json_array) - 1);
         tmp_json_array := tmp_json_array||']';
         return tmp_json_array;
      end if;
      return '[]';
    exception
    when others then
     return p_merchant_acct_processor_id||sqlerrm;
    end get_apm_opt_properties_fnc;
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
            return '{}';
        end if;
        open apm_mm_processor_brands ;
        fetch apm_mm_processor_brands into v_typ_apm_mm_prcsr_brnds;
        close apm_mm_processor_brands;
        if v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_ACCNTID is not null then
           local_obj := '{
                              "TXN_APMOPTNS_ACCNTID":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_ACCNTID)||',
                              "TXN_APMOPTNS_STOREID":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_STOREID)||',
                              "TXN_APMOPTNS_STOREPWD":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_STOREPWD)||',
                              "TXN_APMOPTNS_IPADDRS":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_IPADDRS)||',
                              "TXN_APMOPTNS_AUTH_GWAY_C":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_AUTH_GWAY_C)||',
                              "TXN_APMOPTNS_M_NBR":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_M_NBR)||',
                              "TXN_APMOPTNS_CHID":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_CHID)||',
                              "TXN_APMOPTNS_APIT":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_APIT)||',
                              "TXN_APMOPTNS_PAY_GWAY_C":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_PAY_GWAY_C)||',
							  "TXN_APMOPTNS_CURR_C":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_CURR_C)||',
							  "TXN_APMOPTNS_STATUS":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_STATUS)||',
                              "TXN_APM_ACCOUNT_TYPE":'||TO_STRING_FNC(v_typ_apm_mm_prcsr_brnds.TXN_APM_ACCOUNT_TYPE)||',
							  "TXN_APMOPT_PROPERTIES":'||get_apm_opt_properties_fnc(v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_ACCNTID,v_typ_apm_mm_prcsr_brnds.TXN_APMOPTNS_AUTH_GWAY_C)||'},';
       end if;
       local_obj := SUBSTR(local_obj, 0, LENGTH(local_obj) - 1);
       return local_obj;
exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_apm_txn_options_fnc;
#

--changeset unknown:perm_get_json_list_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_apm_txn_options_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_apm_txn_options_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_apm_txn_options_fnc FOR get_apm_txn_options_fnc
#
