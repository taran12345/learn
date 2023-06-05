--liquibase formatted sql
--changeset unknown:insert_get_json_list_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_json_list_fnc(p_PERSONAL_ACCOUNT_NBR VARCHAR2 , p_type varchar2) return clob as
  cursor cr_merchant_business_owners is
  SELECT REPLACE(REPLACE(REPLACE((C.JOB_TITLE), '\', '\\'), '"', '\"'), CHR(9), '\t')    AS PRAC_BO_JOBTITLE,
       C.PERCENTAGE_OWNERSHIP           AS PRAC_BO_PERCENTAGEOWNERSHIP,
       REPLACE(REPLACE(REPLACE((C.FIRST_NAME), '\', '\\'), '"', '\"'), CHR(9), '\t')     AS PRAC_BO_P_FIRSTNAME,
       REPLACE(REPLACE(REPLACE((C.MIDDLE_NAME), '\', '\\'), '"', '\"'), CHR(9), '\t')    AS PRAC_BO_P_MIDDLENAME,
       REPLACE(REPLACE(REPLACE((C.LAST_NAME), '\', '\\'), '"', '\"'), CHR(9), '\t')      AS PRAC_BO_P_LASTNAME,
       REPLACE(REPLACE(REPLACE((C.EMAIL), '\', '\\'), '"', '\"'), CHR(9), '\t')          AS PRAC_BO_P_EMAIL,
       REPLACE(REPLACE(REPLACE((C.MOBILE_NBR), '\', '\\'), '"', '\"'), CHR(9), '\t')            AS PRAC_BO_P_MOBILEPHONE_NUMBER,
       REPLACE(REPLACE(REPLACE((C.HOME_NBR), '\', '\\'), '"', '\"'), CHR(9), '\t')              AS PRAC_BO_P_HOMEPHONE_NUMBER,
       REPLACE(REPLACE(REPLACE((C.WORK_NBR), '\', '\\'), '"', '\"'), CHR(9), '\t')              AS PRAC_BO_P_WORKPHONE_NUMBER,
       C.NATIONALITY_COUNTRY_CDE        AS PRAC_BO_P_NATIONALITY,
       C.GENDER                         AS PRAC_BO_P_GENDER,
       EXTRACT(YEAR  FROM C.DATE_OF_BIRTH) AS PRAC_BO_P_DATEOFBIRTH_YEAR,
       EXTRACT(MONTH FROM C.DATE_OF_BIRTH) AS PRAC_BO_P_DATEOFBIRTH_MONTH,
       EXTRACT(DAY   FROM C.DATE_OF_BIRTH) AS PRAC_BO_P_DATEOFBIRTH_DAY,
       REPLACE(REPLACE(REPLACE((C.ADDRESS1), '\', '\\'), '"', '\"'), CHR(9), '\t')                              AS PRAC_BO_P_CA_STREET,
       REPLACE(REPLACE(REPLACE((C.ADDRESS2), '\', '\\'), '"', '\"'), CHR(9), '\t')                              AS PRAC_BO_P_CA_STREET2,
       REPLACE(REPLACE(REPLACE((C.CITY), '\', '\\'), '"', '\"'), CHR(9), '\t')                           AS PRAC_BO_P_CA_CITY,
       C.STATE_CDE                      AS PRAC_BO_P_CA_STATE,
       C.COUNTRY_CDE                    AS PRAC_BO_P_CA_COUNTRY,
       C.POSTAL_CODE                    AS PRAC_BO_P_CA_ZIP,
       C.YEARS_AT_CURRENT_ADDRESS       AS PRAC_BO_P_CA_YEARSATADDRESS,
       REPLACE(REPLACE(REPLACE((PA.ADDRESS1), '\', '\\'), '"', '\"'), CHR(9), '\t')             AS PRAC_BO_P_PA_STREET,
       REPLACE(REPLACE(REPLACE((PA.ADDRESS2), '\', '\\'), '"', '\"'), CHR(9), '\t')             AS PRAC_BO_P_PA_STREET2,
       REPLACE(REPLACE(REPLACE((PA.CITY), '\', '\\'), '"', '\"'), CHR(9), '\t')                 AS PRAC_BO_P_PA_CITY,
       PA.STATE_CDE                     AS PRAC_BO_P_PA_STATE,
       PA.COUNTRY_CDE                   AS PRAC_BO_P_PA_COUNTRY,
       PA.POSTAL_CODE                   AS PRAC_BO_P_PA_ZIP,
       PA.YEARS_AT_ADDRESS              AS PRAC_BO_P_PA_YEARSATADDRESS
  FROM FPA.FPA_CONTACTS C
  LEFT JOIN FPA.FPA_ACCOUNT_CONTACTS AC  ON C.ID = AC.CONTACT_ID
  LEFT JOIN FPA.FPA_CONTACT_PREVIOUS_ADDRESSES PA  ON C.ID = PA.CONTACT_ID
 WHERE C.ROLE_PRIMARY_IND = 'Y'
   AND AC.personal_account_nbr = to_char(p_PERSONAL_ACCOUNT_NBR);
  type business_owner_list is record (  PRAC_BO_JOBTITLE             VARCHAR2(100),
                                        PRAC_BO_PERCENTAGEOWNERSHIP  VARCHAR2(100),
                                        PRAC_BO_P_FIRSTNAME          NVARCHAR2(100),
                                        PRAC_BO_P_MIDDLENAME         NVARCHAR2(100),
                                        PRAC_BO_P_LASTNAME           NVARCHAR2(100),
                                        PRAC_BO_P_EMAIL              NVARCHAR2(255),
                                        PRAC_BO_P_MOBILEPHONE_NUMBER NVARCHAR2(255),
                                        PRAC_BO_P_HOMEPHONE_NUMBER  NVARCHAR2(255),
                                        PRAC_BO_P_WORKPHONE_NUMBER  NVARCHAR2(255),
                                        PRAC_BO_P_NATIONALITY       VARCHAR2(10),
                                        PRAC_BO_P_GENDER            VARCHAR2(1),
                                        PRAC_BO_P_DATEOFBIRTH_YEAR  NUMBER,
                                        PRAC_BO_P_DATEOFBIRTH_MONTH NUMBER,
                                        PRAC_BO_P_DATEOFBIRTH_DAY   NUMBER,
                                        PRAC_BO_P_CA_STREET         NVARCHAR2(255),
                                        PRAC_BO_P_CA_STREET2        NVARCHAR2(255),
                                        PRAC_BO_P_CA_CITY           NVARCHAR2(255),
                                        PRAC_BO_P_CA_STATE          VARCHAR2(30),
                                        PRAC_BO_P_CA_COUNTRY        VARCHAR2(10),
                                        PRAC_BO_P_CA_ZIP            VARCHAR2(15),
                                        PRAC_BO_P_CA_YEARSATADDRESS NUMBER(3,0),
                                        PRAC_BO_P_PA_STREET         NVARCHAR2(255),
                                        PRAC_BO_P_PA_STREET2        NVARCHAR2(255),
                                        PRAC_BO_P_PA_CITY           NVARCHAR2(255),
                                        PRAC_BO_P_PA_STATE          VARCHAR2(30),
                                        PRAC_BO_P_PA_COUNTRY        VARCHAR2(10),
                                        PRAC_BO_P_PA_ZIP            VARCHAR2(15),
                                        PRAC_BO_P_PA_YEARSATADDRESS VARCHAR2(15)
                                 );
  type r_business_owner_list is table of business_owner_list index by PLS_INTEGER;
  v_r_business_owner_list  r_business_owner_list;
  cursor cr_merchant_contacts is
  SELECT C.JOB_TITLE             AS PAAC_C_JOBTITLE,
        C.ROLE_PRIMARY_IND      AS PAAC_C_ISPRIMARY,
        C.ROLE_CUST_CARE_IND    AS PAAC_C_ISCUSTOMERCARE,
        C.ROLE_TECH_IND         AS PAAC_C_ISTECH,
        C.FIRST_NAME            AS PAAC_C_FIRSTNAME,
        C.MIDDLE_NAME           AS PAAC_C_MIDDLENAME,
        C.LAST_NAME             AS PAAC_C_LASTNAME,
        C.EMAIL                 AS PAAC_C_EMAIL,
        C.MOBILE_NBR            AS PAAC_C_PHONE_NUMBER,
        C.HOME_NBR              AS PAAC_C_HOMEPHONE_NUMBER,
        C.WORK_NBR              AS PAAC_C_WORKPHONE_NUMBER
  FROM FPA.FPA_CONTACTS C
 INNER JOIN FPA.FPA_ACCOUNT_CONTACTS AC ON C.ID = AC.CONTACT_ID
  LEFT JOIN FPA.FPA_CONTACT_PREVIOUS_ADDRESSES PA ON C.ID = PA.CONTACT_ID
  WHERE AC.PERSONAL_ACCOUNT_NBR = to_char(p_PERSONAL_ACCOUNT_NBR) ;
  type contact_list is record ( PAAC_C_JOBTITLE           VARCHAR2(100),
                                PAAC_C_ISPRIMARY          VARCHAR2(100),
                                PAAC_C_ISCUSTOMERCARE     NVARCHAR2(100),
                                PAAC_C_ISTECH             NVARCHAR2(100),
                                PAAC_C_FIRSTNAME          NVARCHAR2(100),
                                PAAC_C_MIDDLENAME         NVARCHAR2(255),
                                PAAC_C_LASTNAME           NVARCHAR2(255),
                                PAAC_C_EMAIL              NVARCHAR2(255),
                                PAAC_C_PHONE_NUMBER       NVARCHAR2(255),
                                PAAC_C_HOMEPHONE_NUMBER   VARCHAR2(20),
                                PAAC_C_WORKPHONE_NUMBER   VARCHAR2(20)
                           );
  type r_contact_list is table of contact_list index by PLS_INTEGER;
  v_r_contact_list  r_contact_list;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
    if p_type = 'B' then --business owner list
        open cr_merchant_business_owners ;
        fetch cr_merchant_business_owners bulk collect into v_r_business_owner_list;
        close cr_merchant_business_owners;
        if v_r_business_owner_list.count > 0 then
            for i in 1..v_r_business_owner_list.count
            loop
              local_obj := null;
              local_obj := '{
                              "PRAC_BO_JOBTITLE":'||'"'||v_r_business_owner_list(i).PRAC_BO_JOBTITLE||'"'||','||'
                              "PRAC_BO_PERCENTAGEOWNERSHIP":'||'"'||v_r_business_owner_list(i).PRAC_BO_PERCENTAGEOWNERSHIP||'"'||','||'
                              "PRAC_BO_P_FIRSTNAME":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_FIRSTNAME||'"'||','||'
                              "PRAC_BO_P_MIDDLENAME":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_MIDDLENAME||'"'||','||'
                              "PRAC_BO_P_LASTNAME":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_LASTNAME||'"'||','||'
                              "PRAC_BO_P_EMAIL":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_EMAIL||'"'||','||'
                              "PRAC_BO_P_MOBILEPHONE_NUMBER":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_MOBILEPHONE_NUMBER||'"'||','||'
                              "PRAC_BO_P_HOMEPHONE_NUMBER":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_HOMEPHONE_NUMBER||'"'||','||'
                              "PRAC_BO_P_WORKPHONE_NUMBER":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_WORKPHONE_NUMBER||'"'||','||'
                              "PRAC_BO_P_NATIONALITY":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_NATIONALITY||'"'||','||'
                              "PRAC_BO_P_GENDER":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_GENDER||'"'||','||'
                              "PRAC_BO_P_DATEOFBIRTH_YEAR":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_DATEOFBIRTH_YEAR||'"'||','||'
                              "PRAC_BO_P_DATEOFBIRTH_MONTH":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_DATEOFBIRTH_MONTH||'"'||','||'
                              "PRAC_BO_P_DATEOFBIRTH_DAY":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_DATEOFBIRTH_DAY||'"'||','||'
                              "PRAC_BO_P_CA_STREET":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_CA_STREET||'"'||','||'
                              "PRAC_BO_P_CA_STREET2":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_CA_STREET2||'"'||','||'
                              "PRAC_BO_P_CA_CITY": '||'"'||v_r_business_owner_list(i).PRAC_BO_P_CA_CITY||'"'||','||'
                              "PRAC_BO_P_CA_STATE":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_CA_STATE||'"'||','||'
                              "PRAC_BO_P_CA_COUNTRY":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_CA_COUNTRY||'"'||','||'
                              "PRAC_BO_P_CA_ZIP":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_CA_ZIP||'"'||','||'
                              "PRAC_BO_P_CA_YEARSATADDRESS": '||'"'||v_r_business_owner_list(i).PRAC_BO_P_CA_YEARSATADDRESS||'"'||','||'
                              "PRAC_BO_P_PA_STREET":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_PA_STREET||'"'||','||'
                              "PRAC_BO_P_PA_STREET2":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_PA_STREET2||'"'||','||'
                              "PRAC_BO_P_PA_CITY":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_PA_CITY||'"'||','||'
                              "PRAC_BO_P_PA_STATE":'||'"'||v_r_business_owner_list(i).PRAC_BO_P_PA_STATE||'"'||','||'
                              "PRAC_BO_P_PA_COUNTRY": '||'"'||v_r_business_owner_list(i).PRAC_BO_P_PA_COUNTRY||'"'||','||'
                              "PRAC_BO_P_PA_ZIP": '||'"'||v_r_business_owner_list(i).PRAC_BO_P_PA_ZIP||'"'||','||'
                              "PRAC_BO_P_PA_YEARSATADDRESS": '||'"'||v_r_business_owner_list(i).PRAC_BO_P_PA_YEARSATADDRESS||'"'||'},';
            tmp_json_array := tmp_json_array||local_obj;
            end loop;
            tmp_json_array := SUBSTR(tmp_json_array, 0, LENGTH(tmp_json_array) - 1);
            tmp_json_array := tmp_json_array||']';
            return tmp_json_array;
        else
           return '[]';
        end if;
    elsif p_type = 'C' then
      open cr_merchant_contacts ;  --contact list
        fetch cr_merchant_contacts bulk collect into v_r_contact_list;
        close cr_merchant_contacts;
        if v_r_contact_list.count > 0 then
            for i in 1..v_r_contact_list.count
            loop
              local_obj := null;
              local_obj := '{
                              "PAAC_C_JOBTITLE":'||'"'||v_r_contact_list(i).PAAC_C_JOBTITLE||'"'||','||'
                              "PAAC_C_ISPRIMARY":'||'"'||v_r_contact_list(i).PAAC_C_ISPRIMARY||'"'||','||'
                              "PAAC_C_ISCUSTOMERCARE":'||'"'||v_r_contact_list(i).PAAC_C_ISCUSTOMERCARE||'"'||','||'
                              "PAAC_C_ISTECH":'||'"'||v_r_contact_list(i).PAAC_C_ISTECH||'"'||','||'
                              "PAAC_C_FIRSTNAME":'||'"'||v_r_contact_list(i).PAAC_C_FIRSTNAME||'"'||','||'
                              "PAAC_C_MIDDLENAME":'||'"'||v_r_contact_list(i).PAAC_C_MIDDLENAME||'"'||','||'
                              "PAAC_C_LASTNAME":'||'"'||v_r_contact_list(i).PAAC_C_LASTNAME||'"'||','||'
                              "PAAC_C_EMAIL":'||'"'||v_r_contact_list(i).PAAC_C_EMAIL||'"'||','||'
                              "PAAC_C_PHONE_NUMBER":'||'"'||v_r_contact_list(i).PAAC_C_PHONE_NUMBER||'"'||','||'
                              "PAAC_C_HOMEPHONE_NUMBER":'||'"'||v_r_contact_list(i).PAAC_C_HOMEPHONE_NUMBER||'"'||','||'
                              "PAAC_C_WORKPHONE_NUMBER": '||'"'||v_r_contact_list(i).PAAC_C_WORKPHONE_NUMBER||'"'||'},';

            tmp_json_array := tmp_json_array||local_obj;
            end loop;
            tmp_json_array := SUBSTR(tmp_json_array, 0, LENGTH(tmp_json_array) - 1);
            tmp_json_array := tmp_json_array||']';
            return tmp_json_array;
        else
           return '[]';
        end if;
    end if;
    return '[]';
exception
when others then
 return p_PERSONAL_ACCOUNT_NBR||sqlerrm;
end get_json_list_fnc;
#

--changeset unknown:perm_get_json_list_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_json_list_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_json_list_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_json_list_fnc FOR get_json_list_fnc
#
