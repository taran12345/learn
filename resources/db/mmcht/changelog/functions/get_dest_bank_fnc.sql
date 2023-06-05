--liquibase formatted sql
--changeset unknown:get_dest_bank_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_dest_bank_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_dest_bank is
select DBI.DEST_BANK_ID                     AS DB_BANK_ID,
      DBI.PMTDBT_TYPE_CODE                  AS DB_BANK_TYPE,
      DBI.CURRENCY_TYPE_CDE                 AS DB_CURRENCY,
      DBI.BANK_ACCOUNT_NBR_ENC              AS DB_ACCOUNT_NBR_ENC,
      DBI.BANK_ACCOUNT_NBR_HMAC             AS DB_ACCOUNT_NBR_HMAC,
      DBI.BANK_ACCOUNT_NBR_LAST_DIGITS      AS DB_ACCOUNT_NBR_LAST_DIGITS,
      DBI.BANK_ROUTING_NBR                  AS DB_ROUTING_NBR,
      GA.CODE                               AS DB_COUNTRY_CDE,
      DBC.DESCRIPTION                       AS DB_COUNTRY,
      CASE WHEN DBI.PMTDBT_TYPE_CODE = 'BANKING' THEN DBI.WIRE_BENEF_BANK_SWIFT_CDE END AS DB_SWIFT_NBR,
      CASE WHEN DBI.PMTDBT_TYPE_CODE = 'BANKING' THEN DBI.WIRE_BENEF_BANK_IBAN END AS DB_IBAN_NBR,
      DBI.WIRE_BENEF_NAME                   AS DB_BENEF_NAME,
      DBI.WIRE_BENEF_ACCOUNT_ENC            AS DB_BENEF_ACCOUNT_ENC,
      DBI.WIRE_BENEF_ACCOUNT_HMAC           AS DB_BENEF_ACCOUNT_HMAC,
      DBI.WIRE_BENEF_ACCOUNT_LAST_DIGITS    AS DB_BENEF_ACCOUNT_LAST_DIGITS,
      DBI.WIRE_BENEF_COUNTRY_CDE            AS DB_BENEF_COUNTRY_CDE,
      WBC.DESCRIPTION                       AS DB_BENEF_COUNTRY,
      DBI.WIRE_BENEF_CURRENCY_CODE          AS DB_BENEF_CURRENCY,
      DBI.WIRE_BENEF_ADR1                   AS DB_BENEF_ADDR1,
      DBI.WIRE_BENEF_ADR2                   AS DB_BENEF_ADDR2,
      DBI.WIRE_BENEF_BANK_COUNTRY_CDE       AS DB_BENEF_BANK_COUNTRY_CDE,
      WBBC.DESCRIPTION                      AS DB_BENEF_BANK_COUNTRY,
      DBI.WIRE_BENEF_BANK_NAME              AS DB_BENEF_BANK_NAME,
      DBI.WIRE_BENEF_BANK_ADR1              AS DB_BENEF_BANK_ADDR1,
      DBI.WIRE_BENEF_BANK_ADR2              AS DB_BENEF_BANK_ADDR2,
      DBI.WIRE_BENEF_BANK_SWIFT_CDE         AS DB_BENEF_BANK_SWIFT,
      DBI.WIRE_BENEF_BANK_RTG_NBR           AS DB_BENEF_BANK_ROUTING,
      DBI.WIRE_BENEF_BANK_IBAN_ENC          AS DB_BENEF_BANK_IBAN_ENC,
      DBI.WIRE_BENEF_BANK_IBAN_HMAC         AS DB_BENEF_BANK_IBAN_HMAC,
      DBI.WIRE_BENEF_BANK_IBAN_LAST_DGTS    AS DB_BENEF_BANK_IBAN_LAST_DIGITS,
      DBI.WIRE_INTER1_BANK_NAME             AS DB_INTER_BANK_NAME,
      DBI.WIRE_INTER1_ADR1                  AS DB_INTER_BANK_ADDR1,
      DBI.WIRE_INTER1_ADR2                  AS DB_INTER_BANK_ADDR2,
      DBI.WIRE_INTER1_BANK_COUNTRY_CDE      AS DB_INTER_BANK_COUNTRY_CDE,
      WIBC.DESCRIPTION                      AS DB_INTER_BANK_COUNTRY,
      DBI.WIRE_INTER1_SWIFT_CDE             AS DB_INTER_BANK_SWIFT,
      DBI.WIRE_INTER1_BANK_RTG_NBR          AS DB_INTER_BANK_ROUTING,
      DBI.WIRE_NARRATIVE_LINE_1             AS DB_NARRATIVE,
      to_boolean_fnc(DBI.APPROVED)          AS DB_APPROVED
FROM FPA.PMT_DEST_BANK_INFO DBI
LEFT JOIN FPA.GEO_AREAS GA ON DBI.GEO_AREA_ID = GA.GEO_AREA_ID
LEFT JOIN ALX.ALX_COUNTRY_TYPS DBC ON GA.CODE = DBC.CDE
LEFT JOIN ALX.ALX_COUNTRY_TYPS WBC ON DBI.WIRE_BENEF_COUNTRY_CDE = WBC.CDE
LEFT JOIN ALX.ALX_COUNTRY_TYPS WBBC ON DBI.WIRE_BENEF_BANK_COUNTRY_CDE = WBBC.CDE
LEFT JOIN ALX.ALX_COUNTRY_TYPS WIBC ON DBI.WIRE_INTER1_BANK_COUNTRY_CDE = WIBC.CDE
WHERE DBI.PERSONAL_ACCOUNT_NBR = coalesce(
    (select CHILD.CONSOLIDATED_PAYMT_ACCT from FPA.FMA_PAYMENT_SCHEDULE CHILD where CHILD.PERSONAL_ACCOUNT_NBR = to_char(p_personal_account_nbr)),
    to_char(p_personal_account_nbr)
) AND DBI.THRU_DATE IS NULL;

type typ_dest_bank is record (
                                  DB_BANK_ID                    NUMBER(12,0),
                                  DB_BANK_TYPE                  VARCHAR2(10 CHAR),
                                  DB_CURRENCY                   VARCHAR2(10 CHAR),
                                  DB_ACCOUNT_NBR_ENC            VARCHAR2(256 CHAR),
                                  DB_ACCOUNT_NBR_HMAC           VARCHAR2(256 CHAR),
                                  DB_ACCOUNT_NBR_LAST_DIGITS    VARCHAR2(10 CHAR),
                                  DB_ROUTING_NBR                VARCHAR2(40 CHAR),
                                  DB_COUNTRY_CDE                VARCHAR2(10 CHAR),
                                  DB_COUNTRY                    VARCHAR2(240 CHAR),
                                  DB_SWIFT_NBR                  VARCHAR2(35 CHAR),
                                  DB_IBAN_NBR                   VARCHAR2(35 CHAR),
                                  DB_BENEF_NAME                 VARCHAR2(255 CHAR),
                                  DB_BENEF_ACCOUNT_ENC          VARCHAR2(256 CHAR),
                                  DB_BENEF_ACCOUNT_HMAC         VARCHAR2(256 CHAR),
                                  DB_BENEF_ACCOUNT_LAST_DIGITS  VARCHAR2(10 CHAR),
                                  DB_BENEF_COUNTRY_CDE          VARCHAR2(10 CHAR),
                                  DB_BENEF_COUNTRY              VARCHAR2(240 CHAR),
                                  DB_BENEF_CURRENCY             VARCHAR2(3 CHAR),
                                  DB_BENEF_ADDR1                VARCHAR2(100 CHAR),
                                  DB_BENEF_ADDR2                VARCHAR2(100 CHAR),
                                  DB_BENEF_BANK_COUNTRY_CDE     VARCHAR2(10 CHAR),
                                  DB_BENEF_BANK_COUNTRY         VARCHAR2(240 CHAR),
                                  DB_BENEF_BANK_NAME            VARCHAR2(255 CHAR),
                                  DB_BENEF_BANK_ADDR1           VARCHAR2(100 CHAR),
                                  DB_BENEF_BANK_ADDR2           VARCHAR2(100 CHAR),
                                  DB_BENEF_BANK_SWIFT           VARCHAR2(35 CHAR),
                                  DB_BENEF_BANK_ROUTING         VARCHAR2(40 CHAR),
                                  DB_BENEF_BANK_IBAN_ENC        VARCHAR2(256 CHAR),
                                  DB_BENEF_BANK_IBAN_HMAC       VARCHAR2(256 CHAR),
                                  DB_BENEF_BANK_IBAN_LAST_DIGITS VARCHAR2(10 CHAR),
                                  DB_INTER_BANK_NAME            VARCHAR2(255 CHAR),
                                  DB_INTER_BANK_ADDR1           VARCHAR2(35 CHAR),
                                  DB_INTER_BANK_ADDR2           VARCHAR2(35 CHAR),
                                  DB_INTER_BANK_COUNTRY_CDE     VARCHAR2(10 CHAR),
                                  DB_INTER_BANK_COUNTRY         VARCHAR2(240 CHAR),
                                  DB_INTER_BANK_SWIFT           VARCHAR2(35 CHAR),
                                  DB_INTER_BANK_ROUTING         VARCHAR2(40 CHAR),
                                  DB_NARRATIVE                  VARCHAR2(255 CHAR),
                                  DB_APPROVED                   VARCHAR2(5 CHAR)
                                      );
  local_obj varchar2(10000);
  tmp_json_array clob ;

  typ_cr_dest_bank  cr_dest_bank%rowtype;
  begin
    if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
       return '{}';
    end if;
    open cr_dest_bank ;
    fetch cr_dest_bank into typ_cr_dest_bank;
    if cr_dest_bank%found then
       local_obj := null;
       local_obj := '{
              "DB_BANK_ID":"'||typ_cr_dest_bank.DB_BANK_ID||'",
              "DB_BANK_TYPE":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BANK_TYPE)||',
              "DB_CURRENCY":"'||typ_cr_dest_bank.DB_CURRENCY||'",
              "DB_ACCOUNT_NBR_ENC":'||TO_STRING_FNC(typ_cr_dest_bank.DB_ACCOUNT_NBR_ENC)||',
              "DB_ACCOUNT_NBR_HMAC":'||TO_STRING_FNC(typ_cr_dest_bank.DB_ACCOUNT_NBR_HMAC)||',
              "DB_ACCOUNT_NBR_LAST_DIGITS":'||TO_STRING_FNC(typ_cr_dest_bank.DB_ACCOUNT_NBR_LAST_DIGITS)||',
              "DB_ROUTING_NBR":'||TO_STRING_FNC(typ_cr_dest_bank.DB_ROUTING_NBR)||',
              "DB_COUNTRY_CDE":'||TO_STRING_FNC(typ_cr_dest_bank.DB_COUNTRY_CDE)||',
              "DB_COUNTRY":'||TO_STRING_FNC(typ_cr_dest_bank.DB_COUNTRY)||',
              "DB_SWIFT_NBR":'||TO_STRING_FNC(typ_cr_dest_bank.DB_SWIFT_NBR)||',
              "DB_IBAN_NBR":'||TO_STRING_FNC(typ_cr_dest_bank.DB_IBAN_NBR)||',
              "DB_BENEF_NAME":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_NAME)||',
              "DB_BENEF_ACCOUNT_ENC":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_ACCOUNT_ENC)||',
              "DB_BENEF_ACCOUNT_HMAC":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_ACCOUNT_HMAC)||',
              "DB_BENEF_ACCOUNT_LAST_DIGITS":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_ACCOUNT_LAST_DIGITS)||',
              "DB_BENEF_COUNTRY_CDE":"'||typ_cr_dest_bank.DB_BENEF_COUNTRY_CDE||'",
              "DB_BENEF_COUNTRY":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_COUNTRY)||',
              "DB_BENEF_CURRENCY":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_CURRENCY)||',
              "DB_BENEF_ADDR1":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_ADDR1)||',
              "DB_BENEF_ADDR2":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_ADDR2)||',
              "DB_BENEF_BANK_COUNTRY_CDE":"'||typ_cr_dest_bank.DB_BENEF_BANK_COUNTRY_CDE||'",
              "DB_BENEF_BANK_COUNTRY":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_COUNTRY)||',
              "DB_BENEF_BANK_NAME":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_NAME)||',
              "DB_BENEF_BANK_ADDR1":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_ADDR1)||',
              "DB_BENEF_BANK_ADDR2":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_ADDR2)||',
              "DB_BENEF_BANK_SWIFT":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_SWIFT)||',
              "DB_BENEF_BANK_ROUTING":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_ROUTING)||',
              "DB_BENEF_BANK_IBAN_ENC":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_IBAN_ENC)||',
              "DB_BENEF_BANK_IBAN_HMAC":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_IBAN_HMAC)||',
              "DB_BENEF_BANK_IBAN_LAST_DIGITS":'||TO_STRING_FNC(typ_cr_dest_bank.DB_BENEF_BANK_IBAN_LAST_DIGITS)||',
              "DB_INTER_BANK_NAME":'||TO_STRING_FNC(typ_cr_dest_bank.DB_INTER_BANK_NAME)||',
              "DB_INTER_BANK_ADDR1":'||TO_STRING_FNC(typ_cr_dest_bank.DB_INTER_BANK_ADDR1)||',
              "DB_INTER_BANK_ADDR2":'||TO_STRING_FNC(typ_cr_dest_bank.DB_INTER_BANK_ADDR2)||',
              "DB_INTER_BANK_COUNTRY_CDE":"'||typ_cr_dest_bank.DB_INTER_BANK_COUNTRY_CDE||'",
              "DB_INTER_BANK_COUNTRY":'||TO_STRING_FNC(typ_cr_dest_bank.DB_INTER_BANK_COUNTRY)||',
              "DB_INTER_BANK_SWIFT":'||TO_STRING_FNC(typ_cr_dest_bank.DB_INTER_BANK_SWIFT)||',
              "DB_INTER_BANK_ROUTING":'||TO_STRING_FNC(typ_cr_dest_bank.DB_INTER_BANK_ROUTING)||',
              "DB_NARRATIVE":'||TO_STRING_FNC(typ_cr_dest_bank.DB_NARRATIVE)||',
              "DB_APPROVED":"'||typ_cr_dest_bank.DB_APPROVED||'"
          }';

      close cr_dest_bank;
      return local_obj;
    else
      return '{}';
    end if;
exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_dest_bank_fnc;
#

--changeset unknown:perm_get_dest_bank_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_dest_bank_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_dest_bank_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_dest_bank_fnc FOR get_dest_bank_fnc
#
