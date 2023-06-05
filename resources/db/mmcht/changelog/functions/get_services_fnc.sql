--liquibase formatted sql
--changeset unknown:insert_get_services_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_services_fnc(p_personal_account_nbr varchar2 , p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
tmp_json_array   clob ;
function get_provider_array (p_personal_account_nbr varchar2 , p_provider_cde varchar2 ) return varchar2 as

cursor cr_services is
select * from (
                select CASE
                          WHEN mgp.provider_cde = 'Payfacto' or mgp.provider_cde = 'Handpoint' THEN 'TECH_PROVIDER'
                          WHEN mgp.provider_cde = 'MON' or mgp.provider_cde = 'GPS' or mgp.provider_cde = 'DJN' or
                               mgp.provider_cde = 'OPN' or mgp.provider_cde = 'TAS' or  mgp.provider_cde = 'VAN' THEN 'UPDATER'
                          WHEN mgp.provider_cde = 'ETHC' THEN 'ETHOCA'
                          WHEN mgp.provider_cde = 'VERI' THEN 'VERIFI'
                          WHEN mgp.provider_cde = 'Idology' or mgp.provider_cde = 'McMatch' OR mgp.provider_cde = 'Certegy' or mgp.provider_cde = 'eIDVerify' OR
                               mgp.provider_cde = 'eIDCompare' or mgp.provider_cde = 'ID3Global' OR mgp.provider_cde = 'IpCommerce' or mgp.provider_cde = 'Idology_bg' THEN 'IDENTITY'
                       END AS service_type_cde,
                       mgp.provider_cde , fgt.display_text as provider_desiplay , mgp.value , typ.display_text
                  from fpa_merch_gwy_properties mgp ,
                       fpa_gwy_provider_property_typs typ ,
                       fpa_gwy_provider_typs fgt
                 where mgp.provider_cde = typ.provider_cde
                   and mgp.property_cde = typ.property_cde
                   and mgp.provider_cde = fgt.cde
                   and personal_account_nbr = to_char(p_personal_account_nbr))
   where service_type_cde = p_provider_cde;

type typ_services is record (
                              service_type_cde   VARCHAR2(30),
                              provider_cde       VARCHAR2(30),
                              provider_desiplay  VARCHAR2(30),
                              value              VARCHAR2(4000),
                              display_text       VARCHAR2(30)
                           );
type tbl_typ_services is table of typ_services index by PLS_INTEGER;
v_tbl_typ_services  tbl_typ_services;
locl_json clob ;
begin
    if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
           return '[]';
    end if;
    open cr_services;
    fetch cr_services bulk collect into v_tbl_typ_services;
    close cr_services;

    if v_tbl_typ_services.count > 0 then

       locl_json := locl_json||' "providerName":'||TO_STRING_FNC(v_tbl_typ_services(1).provider_desiplay)||',
                                 "properties":{ ';

       for i in 1..v_tbl_typ_services.count
       loop
         locl_json := locl_json||'"'||v_tbl_typ_services(i).display_text||'"'||':'||TO_STRING_FNC(v_tbl_typ_services(i).value)||',';
       end loop;

       locl_json := SUBSTR(locl_json, 0, length(locl_json) - 1);
       locl_json := locl_json||'}';
       return locl_json;
    end if;
    return '"providerName": null, "properties": {}';
end get_provider_array;

function get_fee_array (p_personal_account_nbr varchar2 , p_provider_cde varchar2 ) return varchar2 as

cursor cr_fees is
select af.fee_value , ft.feet_cde , ft.display_text
  from fpa_agreement_fees af ,
       fpa_alt_account_agreements aa ,
       fpa_fee_typs ft
 where af.agre_id  = aa.agre_id
   and af.feet_cde = ft.feet_cde
   and ft.service_cde = p_provider_cde
   and aa.personal_account_nbr = to_char(p_personal_account_nbr);

type typ_fees is record (
                              fee_value      NUMBER(15,5),
                              feet_cde       VARCHAR2(30),
                              display_text   VARCHAR2(30)
                           );
type tbl_typ_fees is table of typ_fees index by PLS_INTEGER;
v_tbl_typ_fees  tbl_typ_fees;
locl_json clob ;
begin
    open cr_fees;
    fetch cr_fees bulk collect into v_tbl_typ_fees;
    close cr_fees;

    if v_tbl_typ_fees.count > 0 then

       locl_json := locl_json||'{';

       for i in 1..v_tbl_typ_fees.count
       loop
         locl_json := locl_json||'"'||v_tbl_typ_fees(i).display_text||'"'||':'||TO_STRING_FNC(v_tbl_typ_fees(i).fee_value)||',';
       end loop;

       locl_json := SUBSTR(locl_json, 0, length(locl_json) - 1);
       locl_json := locl_json||'}';
       return locl_json;
    end if;
    return '{}';
end get_fee_array;
begin
    tmp_json_array := '[
                     {
                        "name" : "3rd Party Technology Provider",'||
                        get_provider_array(p_personal_account_nbr,'TECH_PROVIDER') ||', "fees" : '||
                        get_fee_array(p_personal_account_nbr,'TECH_PROVIDER') ||'
                     },
                     {
                        "name" : "Account Card Updater",'||
                        get_provider_array(p_personal_account_nbr,'UPDATER') ||', "fees" : '||
                        get_fee_array(p_personal_account_nbr,'UPDATER') ||'
                     },
                     {
                        "name" : "Account Card Updater Scheme Fees",
                        "providerName": null,
                        "properties" : {},
                        "fees" : {}
                     },
                     {
                        "name" : "Dispute Prevention (Ethoca)",'||
                        get_provider_array(p_personal_account_nbr,'ETHOCA') ||', "fees" : '||
                        get_fee_array(p_personal_account_nbr,'ETHOCA') ||'
                     },
                     {
                        "name" : "Dispute Prevention (Verifi)",'||
                        get_provider_array(p_personal_account_nbr,'VERIFI') ||', "fees" : '||
                        get_fee_array(p_personal_account_nbr,'VERIFI') ||'
                     },
                     {
                        "name" : "Identity",'||
                        get_provider_array(p_personal_account_nbr,'IDENTITY') ||', "fees" : '||
                        get_fee_array(p_personal_account_nbr,'IDENTITY') ||'
                     },
                     {
                        "name" : "UK Scheme fees",
                        "providerName": null,
                        "properties" : {},
                        "fees" : {}
                     }]
                   ' ;

   return tmp_json_array;

exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_services_fnc;
#

--changeset unknown:get_services_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_services_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_services_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_services_fnc FOR get_services_fnc
#
