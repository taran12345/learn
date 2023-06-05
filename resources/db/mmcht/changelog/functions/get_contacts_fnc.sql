--liquibase formatted sql
--changeset unknown:get_contacts_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_contacts_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_mm_contacts is
SELECT C.JOB_TITLE                         AS PRAC_C_JOBTITLE,
       C.PERCENTAGE_OWNERSHIP              AS PRAC_C_PERCENTAGEOWNERSHIP,
       C.FIRST_NAME                        AS PRAC_C_P_FIRSTNAME,
       C.MIDDLE_NAME                       AS PRAC_C_P_MIDDLENAME,
       C.LAST_NAME                         AS PRAC_C_P_LASTNAME,
       C.EMAIL                             AS PRAC_C_P_EMAIL,
       C.MOBILE_NBR                        AS PRAC_C_P_MOBILEPHONE_NUMBER,
       C.HOME_NBR                          AS PRAC_C_P_HOMEPHONE_NUMBER,
       C.WORK_NBR                          AS PRAC_C_P_WORKPHONE_NUMBER,
       C.NATIONALITY_COUNTRY_CDE           AS PRAC_C_P_NATIONALITY_CDE,
       CNC.DESCRIPTION                     AS PRAC_C_P_NATIONALITY,
       C.GENDER                            AS PRAC_C_P_GENDER,
       EXTRACT(YEAR  FROM C.DATE_OF_BIRTH) AS PRAC_C_P_DATEOFBIRTH_YEAR,
       EXTRACT(MONTH FROM C.DATE_OF_BIRTH) AS PRAC_C_P_DATEOFBIRTH_MONTH,
       EXTRACT(DAY   FROM C.DATE_OF_BIRTH) AS PRAC_C_P_DATEOFBIRTH_DAY,
       C.ADDRESS1                          AS PRAC_C_P_CA_STREET,
       C.ADDRESS2                          AS PRAC_C_P_CA_STREET2,
       C.CITY                              AS PRAC_C_P_CA_CITY,
       C.STATE_CDE                         AS PRAC_C_P_CA_STATE_CDE,
       CS.DESCRIPTION                      AS PRAC_C_P_CA_STATE,
       C.COUNTRY_CDE                       AS PRAC_C_P_CA_COUNTRY_CDE,
       CC.DESCRIPTION                      AS PRAC_C_P_CA_COUNTRY,
       C.POSTAL_CODE                       AS PRAC_C_P_CA_ZIP,
       C.REGION                            AS PRAC_C_P_CA_REGION,
       decode(C.YEARS_AT_CURRENT_ADDRESS, null, 0, C.YEARS_AT_CURRENT_ADDRESS)  AS PRAC_C_P_CA_YEARSATADDRESS,
       PA.ADDRESS1                        AS PRAC_C_P_PA_STREET,
       PA.ADDRESS2                        AS PRAC_C_P_PA_STREET2,
       PA.CITY                            AS PRAC_C_P_PA_CITY,
       PA.STATE_CDE                       AS PRAC_C_P_PA_STATE_CDE,
       CPS.DESCRIPTION                    AS PRAC_C_P_PA_STATE,
       PA.COUNTRY_CDE                     AS PRAC_C_P_PA_COUNTRY_CDE,
       CPC.DESCRIPTION                    AS PRAC_C_P_PA_COUNTRY,
       PA.POSTAL_CODE                     AS PRAC_C_P_PA_ZIP,
       PA.REGION                          AS PRAC_C_P_PA_REGION,
       decode(PA.YEARS_AT_ADDRESS, null, 0, PA.YEARS_AT_ADDRESS)        AS PRAC_C_P_PA_YEARSATADDRESS ,
       decode(C.ROLE_PRIMARY_IND, 'Y', 'true', 'N', 'false', 'null')    AS PAAC_C_ISPRIMARY,
       decode(C.ROLE_CUST_CARE_IND, 'Y', 'true', 'N', 'false', 'null')  AS PAAC_C_ISCUSTOMERCARE,
       decode(C.ROLE_TECH_IND, 'Y', 'true', 'N', 'false', 'null')       AS PAAC_C_ISTECH,
       decode(C.APPLICANT_IND, 'Y', 'true', 'N', 'false', 'null')       AS PAAC_C_ISAPPLICANT,
       decode(C.CONTROL_PRONG_VALUE, 'Y', 'true', 'N', 'false', 'null') AS PAAC_C_ISCONTROLPRONG,
       to_char(C.CREATION_DATE, 'YYYY-MM-DD HH24:MI:SS')                AS PAAC_C_CREATION_DATE,
       to_char(C.MODIFICATION_DATE,'YYYY-MM-DD HH24:MI:SS')             AS PAAC_C_MODIFICATION_DATE
  FROM FPA.FPA_CONTACTS C
  LEFT JOIN FPA.FPA_ACCOUNT_CONTACTS AC  ON C.ID = AC.CONTACT_ID
  LEFT JOIN FPA.FPA_CONTACT_PREVIOUS_ADDRESSES PA  ON C.ID = PA.CONTACT_ID
  LEFT JOIN ALX.ALX_COUNTRY_TYPS CNC  ON C.NATIONALITY_COUNTRY_CDE = CNC.CDE
  LEFT JOIN ALX.ALX_COUNTRY_TYPS CC  ON C.COUNTRY_CDE = CC.CDE
  LEFT JOIN ALX.ALX_COUNTRY_TYPS CPC  ON PA.COUNTRY_CDE = CPC.CDE
  LEFT JOIN ALX.ALX_STATE_TYPS CS  ON C.STATE_CDE = CS.CDE AND C.COUNTRY_CDE = CS.COUNTY_CDE
  LEFT JOIN ALX.ALX_STATE_TYPS CPS  ON PA.STATE_CDE = CPS.CDE AND PA.COUNTRY_CDE = CPS.COUNTY_CDE
 WHERE AC.personal_account_nbr = to_char(p_PERSONAL_ACCOUNT_NBR);
type typ_mm_contact_lst is record ( PRAC_C_JOBTITLE             VARCHAR2(100),
                                PRAC_C_PERCENTAGEOWNERSHIP  VARCHAR2(100),
                                PRAC_C_P_FIRSTNAME          VARCHAR2(100),
                                PRAC_C_P_MIDDLENAME         VARCHAR2(100),
                                PRAC_C_P_LASTNAME           VARCHAR2(100),
                                PRAC_C_P_EMAIL              VARCHAR2(255),
                                PRAC_C_P_MOBILEPHONE_NUMBER VARCHAR2(255),
                                PRAC_C_P_HOMEPHONE_NUMBER  VARCHAR2(255),
                                PRAC_C_P_WORKPHONE_NUMBER  VARCHAR2(255),
                                PRAC_C_P_NATIONALITY_CDE   VARCHAR2(10),
                                PRAC_C_P_NATIONALITY       VARCHAR2(240),
                                PRAC_C_P_GENDER            VARCHAR2(1),
                                PRAC_C_P_DATEOFBIRTH_YEAR  NUMBER,
                                PRAC_C_P_DATEOFBIRTH_MONTH NUMBER,
                                PRAC_C_P_DATEOFBIRTH_DAY   NUMBER,
                                PRAC_C_P_CA_STREET         VARCHAR2(255),
                                PRAC_C_P_CA_STREET2        VARCHAR2(255),
                                PRAC_C_P_CA_CITY           VARCHAR2(255),
                                PRAC_C_P_CA_STATE_CDE      VARCHAR2(30),
                                PRAC_C_P_CA_STATE          VARCHAR2(240),
                                PRAC_C_P_CA_COUNTRY_CDE    VARCHAR2(10),
                                PRAC_C_P_CA_COUNTRY        VARCHAR2(240),
                                PRAC_C_P_CA_ZIP            VARCHAR2(15),
                                PRAC_C_P_CA_REGION         VARCHAR2(240),
                                PRAC_C_P_CA_YEARSATADDRESS NUMBER,
                                PRAC_C_P_PA_STREET         VARCHAR2(255),
                                PRAC_C_P_PA_STREET2        VARCHAR2(255),
                                PRAC_C_P_PA_CITY           VARCHAR2(255),
                                PRAC_C_P_PA_STATE_CDE      VARCHAR2(30),
                                PRAC_C_P_PA_STATE          VARCHAR2(240),
                                PRAC_C_P_PA_COUNTRY_CDE    VARCHAR2(10),
                                PRAC_C_P_PA_COUNTRY        VARCHAR2(240),
                                PRAC_C_P_PA_ZIP            VARCHAR2(15),
                                PRAC_C_P_PA_REGION         VARCHAR2(240),
                                PRAC_C_P_PA_YEARSATADDRESS NUMBER,
                                PAAC_C_ISPRIMARY            VARCHAR2(100),
                                PAAC_C_ISCUSTOMERCARE       VARCHAR2(100),
                                PAAC_C_ISTECH               VARCHAR2(100),
                                PAAC_C_ISAPPLICANT          VARCHAR2(100),
                                PAAC_C_ISCONTROLPRONG       VARCHAR2(100),
                                PAAC_C_CREATION_DATE        VARCHAR2(100),
                                PAAC_C_MODIFICATION_DATE    VARCHAR2(100)
                                 );
  type tbl_typ_mm_contact_lst is table of typ_mm_contact_lst index by PLS_INTEGER;
  v_tbl_typ_mm_contact_lst  tbl_typ_mm_contact_lst;
  local_obj varchar2(10000);
  tmp_json_array clob := '[';
  begin
        if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
           return '[]';
        end if;
        open cr_mm_contacts ;
        fetch cr_mm_contacts bulk collect into v_tbl_typ_mm_contact_lst;
        close cr_mm_contacts;
        if v_tbl_typ_mm_contact_lst.count > 0 then
            for i in 1..v_tbl_typ_mm_contact_lst.count
            loop
              local_obj := null;
              local_obj := '{
                              "PRAC_C_JOBTITLE": '||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_JOBTITLE)||','||'
                              "PRAC_C_PERCENTAGEOWNERSHIP":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_PERCENTAGEOWNERSHIP)||','||'
                              "PRAC_C_P_FIRSTNAME":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_FIRSTNAME)||','||'
                              "PRAC_C_P_MIDDLENAME":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_MIDDLENAME)||','||'
                              "PRAC_C_P_LASTNAME":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_LASTNAME)||','||'
                              "PRAC_C_P_EMAIL":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_EMAIL)||','||'
                              "PRAC_C_P_MOBILEPHONE_NUMBER":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_MOBILEPHONE_NUMBER)||','||'
                              "PRAC_C_P_HOMEPHONE_NUMBER":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_HOMEPHONE_NUMBER)||','||'
                              "PRAC_C_P_WORKPHONE_NUMBER":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_WORKPHONE_NUMBER)||','||'
                              "PRAC_C_P_NATIONALITY_CDE":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_NATIONALITY_CDE)||','||'
                              "PRAC_C_P_NATIONALITY":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_NATIONALITY)||','||'
                              "PRAC_C_P_GENDER":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_GENDER)||','||'
                              "PRAC_C_P_DATEOFBIRTH_YEAR":'||TO_NUMBER_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_DATEOFBIRTH_YEAR)||','||'
                              "PRAC_C_P_DATEOFBIRTH_MONTH":'||TO_NUMBER_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_DATEOFBIRTH_MONTH)||','||'
                              "PRAC_C_P_DATEOFBIRTH_DAY":'||TO_NUMBER_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_DATEOFBIRTH_DAY)||','||'
                              "PRAC_C_P_CA_STREET":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_STREET)||','||'
                              "PRAC_C_P_CA_STREET2":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_STREET2)||','||'
                              "PRAC_C_P_CA_CITY": '||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_CITY)||','||'
                              "PRAC_C_P_CA_STATE_CDE":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_STATE_CDE)||','||'
                              "PRAC_C_P_CA_STATE":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_STATE)||','||'
                              "PRAC_C_P_CA_COUNTRY_CDE":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_COUNTRY_CDE)||','||'
                              "PRAC_C_P_CA_COUNTRY":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_COUNTRY)||','||'
                              "PRAC_C_P_CA_ZIP":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_ZIP)||','||'
                              "PRAC_C_P_CA_REGION":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_REGION)||','||'
                              "PRAC_C_P_CA_YEARSATADDRESS": '||v_tbl_typ_mm_contact_lst(i).PRAC_C_P_CA_YEARSATADDRESS||','||'
                              "PRAC_C_P_PA_STREET":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_STREET)||','||'
                              "PRAC_C_P_PA_STREET2":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_STREET2)||','||'
                              "PRAC_C_P_PA_CITY":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_CITY)||','||'
                              "PRAC_C_P_PA_STATE_CDE":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_STATE_CDE)||','||'
                              "PRAC_C_P_PA_STATE":'||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_STATE)||','||'
                              "PRAC_C_P_PA_COUNTRY_CDE": '||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_COUNTRY_CDE)||','||'
                              "PRAC_C_P_PA_COUNTRY": '||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_COUNTRY)||','||'
                              "PRAC_C_P_PA_ZIP": '||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_ZIP)||','||'
                              "PRAC_C_P_PA_REGION": '||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_REGION)||','||'
                              "PAAC_C_ISPRIMARY": '||v_tbl_typ_mm_contact_lst(i).PAAC_C_ISPRIMARY||','||'
                              "PAAC_C_ISCUSTOMERCARE": '||v_tbl_typ_mm_contact_lst(i).PAAC_C_ISCUSTOMERCARE||','||'
                              "PAAC_C_ISTECH": '||v_tbl_typ_mm_contact_lst(i).PAAC_C_ISTECH||','||'
                              "PAAC_C_ISAPPLICANT": '||v_tbl_typ_mm_contact_lst(i).PAAC_C_ISAPPLICANT||','||'
                              "PAAC_C_ISCONTROLPRONG": '||v_tbl_typ_mm_contact_lst(i).PAAC_C_ISCONTROLPRONG||','||'
                              "PAAC_C_CREATION_DATE": '||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PAAC_C_CREATION_DATE)||','||'
                              "PAAC_C_MODIFICATION_DATE": '||TO_STRING_FNC(v_tbl_typ_mm_contact_lst(i).PAAC_C_MODIFICATION_DATE)||','||'
                              "PRAC_C_P_PA_YEARSATADDRESS": '||v_tbl_typ_mm_contact_lst(i).PRAC_C_P_PA_YEARSATADDRESS||'},';
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
end get_contacts_fnc;

#

--changeset unknown:perm_get_contacts_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_contacts_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_contacts_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_contacts_fnc FOR get_contacts_fnc
#
