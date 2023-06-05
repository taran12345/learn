--liquibase formatted sql
--changeset unknown:get_card_configs_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

create or replace function get_card_configs_fnc(p_personal_account_nbr VARCHAR2, p_execute_or_not VARCHAR2 DEFAULT 'Y') return clob as
cursor cr_card_config_dtls is
SELECT MA.DEFAULT_PAYMENT_METHOD_TYP_CDE                 AS CC_PAYMENT_METHOD_TYPE_CDE,
      MA.STORE_ID                                        AS CC_STORE_ID,
      MA.STORE_PASSWORD                                  AS CC_STORE_PASSWORD,
      MA.IP_ADDRESSS                                     AS CC_IP_ADDRESSES,
      MA.FIREPT_CDE                                      AS CC_VT_PROTOCOL,
      to_boolean_fnc(MA.RAW_GATEWAY_RESPONSE_IND)        AS CC_RAW_GATEWAY_RESPONSE,
      to_boolean_fnc(MA.BALANCE_RESPONSE_IND)            AS CC_BALANCE_RESPONSE,
      MA.SETT_BATCH_DELAY_IN_HOURS                       AS CC_BATCH_DELAY_IN_HOURS,
      to_boolean_fnc(MA.AUTO_CB_DISPUTE_IND)             AS CC_AUTO_CB_DISPUTE,
      to_boolean_fnc(MA.ASYNC_RRE_IND)                   AS CC_ASYNC_RRE,
      MA.RRE_BEHAVIOR_CDE                                AS CC_RRE_BEHAVIOR_CDE,
      to_boolean_fnc(MA.RRE_EVALUATE_ALL_RULES_IND)      AS CC_RRE_EVALUATE_ALL_RULES,
      to_boolean_fnc(MA.DR_LITE_ALLOWED_IND)             AS CC_DR_LITE_ALLOWED,
      to_boolean_fnc(MA.EXTRA_CREDIT_ENABLED_IND)        AS CC_EXTRA_CREDIT_ENABLED,
      to_boolean_fnc(MA.APPLEPAY_ALLOWED_IND)            AS CC_APPLEPAY_ALLOWED,
      to_boolean_fnc(MA.ANDROIDPAY_ALLOWED_IND)          AS CC_ANDROIDPAY_ALLOWED,
      to_boolean_fnc(MA.L3_CARD_ENABLED_IND)             AS CC_L3_CARD_ENABLED,
      to_boolean_fnc(MA.QUASI_CASH_IND)                  AS CC_QUASI_CASH,
      to_boolean_fnc(MA.BYPASS_TSYS_API_IND)             AS CC_BYPASS_TSYS_API,
      MA.DEFAULT_PRE_AUTH_IND                            AS CC_DEFAULT_PRE_AUTH,
      MA.DEFAULT_VISA_BUSINESS_APP_ID                    AS CC_VISA_BUSINESS_APP_ID,
      MA.ECOMMERCE_MODEL                                 AS CC_ECOMMERCE_MODEL,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%A%' THEN 'true' ELSE 'false' END AS CC_AT_AUTHORIZATION,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%S%' THEN 'true' ELSE 'false' END AS CC_AT_SETTLEMENT,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%X%' THEN 'true' ELSE 'false' END AS CC_AT_CANCEL_SETTLEMENT,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%C%' THEN 'true' ELSE 'false' END AS CC_AT_CREDIT,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%Z%' THEN 'true' ELSE 'false' END AS CC_AT_CANCEL_CREDIT,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%P%' THEN 'true' ELSE 'false' END AS CC_AT_PURCHASE,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%O%' THEN 'true' ELSE 'false' END AS CC_AT_OFFLINE_AUTH,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%V%' THEN 'true' ELSE 'false' END AS CC_AT_AVS,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%L%' THEN 'true' ELSE 'false' END AS CC_AT_AUTH_REVERSAL,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%W%' THEN 'true' ELSE 'false' END AS CC_AT_AUTH_REVERSAL_ON_CS,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%M%' THEN 'true' ELSE 'false' END AS CC_AT_PAYMENT,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%Y%' THEN 'true' ELSE 'false' END AS CC_AT_CANCEL_PAYMENT,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%N%' THEN 'true' ELSE 'false' END AS CC_AT_INDP_CREDIT,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%F%' THEN 'true' ELSE 'false' END AS CC_AT_INDP_CREDIT_AS_REFUNDS,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%K%' THEN 'true' ELSE 'false' END AS CC_AT_CANCEL_INDP_CREDIT,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%E%' THEN 'true' ELSE 'false' END AS CC_AT_VERIFICATION,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%R%' THEN 'true' ELSE 'false' END AS CC_AT_RISK,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%I%' THEN 'true' ELSE 'false' END AS CC_AT_INFO_LOOKUP,
      CASE WHEN MAP.TXN_PERMISSION LIKE '%J%' THEN 'true' ELSE 'false' END AS CC_AT_LOOKUP_BY_CHILD,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% VZ%' THEN 'true' ELSE 'false' END AS CC_SO_INCLUDE_AVS,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% N%' THEN 'true' ELSE 'false' END  AS CC_SO_NEGATIVE_DB,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% Z%' THEN 'true' ELSE 'false' END  AS CC_SO_ADDRESS_VERIFICATION,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% MMF%' THEN 'true' ELSE 'false' END AS CC_SO_MIN_FRAUD,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% TMX%' THEN 'true' ELSE 'false' END AS CC_SO_THREAT_METRIX,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% AEC%' THEN 'true' ELSE 'false' END AS CC_SO_EXPIRED_CARD,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% ACR%' THEN 'true' ELSE 'false' END AS CC_SO_CREDIT_RETRIEVALS,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% CCR%' THEN 'true' ELSE 'false' END AS CC_SO_CONTINUITY_CREDITS,
      CASE WHEN MA.DEF_FRAUD_SVCS LIKE '% O3D%' THEN 'true' ELSE 'false' END AS CC_SO_OVERRIDE_ENF_BY_3DS,
      to_boolean_fnc(MA.TEST_CARD_ENABLED_IND)     AS CC_TEST_CARD_ENABLED,
      to_boolean_fnc(MA.PREVENT_OVERDRAFT)         AS CC_PREVENT_OVERDRAFT,
      AGT."NAME"                                   AS CC_STORED_ACC_GROUP_NAME,
      AGT1."NAME"                                  AS CC_CREDIT_ACC_GROUP_NAME,
      CASE WHEN TDS.PIB_COUNT > 0 THEN 'true' ELSE 'false' END AS CC_3DS_ENABLED
FROM FALCON.MERCHANT_ACCOUNTS MA
LEFT JOIN FALCON.MERCHANT_ACCT_PERMISSIONS MAP ON MAP.MERCA_ACCOUNT_ID = MA.ACCOUNT_ID
LEFT JOIN FPA.FPA_ACCOUNT_GROUP_TYPS AGT ON AGT.ACCOG_NAME = MA.STORED_DATA_ACCOUNT_GROUP
LEFT JOIN FPA.FPA_ACCOUNT_GROUP_TYPS AGT1 ON AGT1.ACCOG_NAME = MA.CREDIT_ACCOUNT_GROUP
LEFT JOIN (SELECT COUNT(1) AS PIB_COUNT, PIB.MERCA_ACCOUNT_ID FROM FALCON.PROCESSOR_INFO_BRANDS PIB
    WHERE PIB.DEF_FRAUD_SERVICES LIKE '%TDS%' AND PIB.ACTIVE_FLAG = 1 GROUP BY PIB.MERCA_ACCOUNT_ID) TDS
    ON TDS.MERCA_ACCOUNT_ID = MA.ACCOUNT_ID
WHERE MA.ACCOUNT_ID = to_char(P_PERSONAL_ACCOUNT_NBR);

type typ_card_dtls is record (
                                CC_PAYMENT_METHOD_TYPE_CDE     VARCHAR2(10 ),
                                CC_STORE_ID                    VARCHAR2(80 CHAR),
                                CC_STORE_PASSWORD              VARCHAR2(20 CHAR),
                                CC_IP_ADDRESSES                VARCHAR2(1000 CHAR),
                                CC_VT_PROTOCOL                 VARCHAR2(10 CHAR),
                                CC_RAW_GATEWAY_RESPONSE        VARCHAR2(5 CHAR),
                                CC_BALANCE_RESPONSE            VARCHAR2(5 CHAR),
                                CC_BATCH_DELAY_IN_HOURS        NUMBER(3,0),
                                CC_AUTO_CB_DISPUTE             VARCHAR2(5 CHAR),
                                CC_ASYNC_RRE                   VARCHAR2(5 CHAR),
                                CC_RRE_BEHAVIOR_CDE            VARCHAR2(30 CHAR),
                                CC_RRE_EVALUATE_ALL_RULES      VARCHAR2(5 CHAR),
                                CC_DR_LITE_ALLOWED             VARCHAR2(5 CHAR),
                                CC_EXTRA_CREDIT_ENABLED        VARCHAR2(5 CHAR),
                                CC_APPLEPAY_ALLOWED            VARCHAR2(5 CHAR),
                                CC_ANDROIDPAY_ALLOWED          VARCHAR2(5 CHAR),
                                CC_L3_CARD_ENABLED             VARCHAR2(5 CHAR),
                                CC_QUASI_CASH                  VARCHAR2(5 CHAR),
                                CC_BYPASS_TSYS_API             VARCHAR2(5 CHAR),
                                CC_DEFAULT_PRE_AUTH            VARCHAR2(1 CHAR),
                                CC_VISA_BUSINESS_APP_ID        VARCHAR2(2 CHAR),
                                CC_ECOMMERCE_MODEL             VARCHAR2(30 CHAR),
                                CC_AT_AUTHORIZATION            VARCHAR2(5 CHAR),
                                CC_AT_SETTLEMENT               VARCHAR2(5 CHAR),
                                CC_AT_CANCEL_SETTLEMENT        VARCHAR2(5 CHAR),
                                CC_AT_CREDIT                   VARCHAR2(5 CHAR),
                                CC_AT_CANCEL_CREDIT            VARCHAR2(5 CHAR),
                                CC_AT_PURCHASE                 VARCHAR2(5 CHAR),
                                CC_AT_OFFLINE_AUTH             VARCHAR2(5 CHAR),
                                CC_AT_AVS                      VARCHAR2(5 CHAR),
                                CC_AT_AUTH_REVERSAL            VARCHAR2(5 CHAR),
                                CC_AT_AUTH_REVERSAL_ON_CS      VARCHAR2(5 CHAR),
                                CC_AT_PAYMENT                  VARCHAR2(5 CHAR),
                                CC_AT_CANCEL_PAYMENT           VARCHAR2(5 CHAR),
                                CC_AT_INDP_CREDIT              VARCHAR2(5 CHAR),
                                CC_AT_INDP_CREDIT_AS_REFUNDS   VARCHAR2(5 CHAR),
                                CC_AT_CANCEL_INDP_CREDIT       VARCHAR2(5 CHAR),
                                CC_AT_VERIFICATION             VARCHAR2(5 CHAR),
                                CC_AT_RISK                     VARCHAR2(5 CHAR),
                                CC_AT_INFO_LOOKUP              VARCHAR2(5 CHAR),
                                CC_AT_LOOKUP_BY_CHILD          VARCHAR2(5 CHAR),
                                CC_SO_INCLUDE_AVS              VARCHAR2(5 CHAR),
                                CC_SO_NEGATIVE_DB              VARCHAR2(5 CHAR),
                                CC_SO_ADDRESS_VERIFICATION     VARCHAR2(5 CHAR),
                                CC_SO_MIN_FRAUD                VARCHAR2(5 CHAR),
                                CC_SO_THREAT_METRIX            VARCHAR2(5 CHAR),
                                CC_SO_EXPIRED_CARD             VARCHAR2(5 CHAR),
                                CC_SO_CREDIT_RETRIEVALS        VARCHAR2(5 CHAR),
                                CC_SO_CONTINUITY_CREDITS       VARCHAR2(5 CHAR),
                                CC_SO_OVERRIDE_ENF_BY_3DS      VARCHAR2(5 CHAR),
                                CC_TEST_CARD_ENABLED           VARCHAR2(5 CHAR),
                                CC_PREVENT_OVERDRAFT           VARCHAR2(5 CHAR),
                                CC_STORED_ACC_GROUP_NAME       VARCHAR2(30 CHAR),
                                CC_CREDIT_ACC_GROUP_NAME       VARCHAR2(30 CHAR),
                                CC_3DS_ENABLED                 VARCHAR2(5 CHAR)
                                      );
  local_obj varchar2(10000);
  tmp_json_array clob ;

  typ_cr_card_config_dtls  cr_card_config_dtls%rowtype;
  begin
    if TO_BOOLEAN_FNC(p_execute_or_not) = 'false' then
           return '{}';
    end if;
    open cr_card_config_dtls ;
    fetch cr_card_config_dtls into typ_cr_card_config_dtls;
    if cr_card_config_dtls%found then
       local_obj := null;
       local_obj := '{
          "CC_PAYMENT_METHOD_TYPE_CDE":'||to_string_fnc(typ_cr_card_config_dtls.CC_PAYMENT_METHOD_TYPE_CDE)||',
          "CC_STORE_ID":'||to_string_fnc(typ_cr_card_config_dtls.CC_STORE_ID)||',
          "CC_STORE_PASSWORD":'||to_string_fnc(typ_cr_card_config_dtls.CC_STORE_PASSWORD)||',
          "CC_IP_ADDRESSES":'||to_string_fnc(typ_cr_card_config_dtls.CC_IP_ADDRESSES)||',
          "CC_VT_PROTOCOL":'||to_string_fnc(typ_cr_card_config_dtls.CC_VT_PROTOCOL)||',
          "CC_RAW_GATEWAY_RESPONSE":'||'"'||typ_cr_card_config_dtls.CC_RAW_GATEWAY_RESPONSE||'"'||','||'
          "CC_BALANCE_RESPONSE":'||'"'||typ_cr_card_config_dtls.CC_BALANCE_RESPONSE||'"'||','||'
          "CC_BATCH_DELAY_IN_HOURS":'||TO_NUMBER_FNC(typ_cr_card_config_dtls.CC_BATCH_DELAY_IN_HOURS)||',
          "CC_AUTO_CB_DISPUTE":'||'"'||typ_cr_card_config_dtls.CC_AUTO_CB_DISPUTE||'"'||','||'
          "CC_ASYNC_RRE":'||'"'||typ_cr_card_config_dtls.CC_ASYNC_RRE||'"'||','||'
          "CC_RRE_BEHAVIOR_CDE":'||to_string_fnc(typ_cr_card_config_dtls.CC_RRE_BEHAVIOR_CDE)||',
          "CC_RRE_EVALUATE_ALL_RULES":'||'"'||typ_cr_card_config_dtls.CC_RRE_EVALUATE_ALL_RULES||'"'||','||'
          "CC_DR_LITE_ALLOWED":'||'"'||typ_cr_card_config_dtls.CC_DR_LITE_ALLOWED||'"'||','||'
          "CC_EXTRA_CREDIT_ENABLED":'||'"'||typ_cr_card_config_dtls.CC_EXTRA_CREDIT_ENABLED||'"'||','||'
          "CC_APPLEPAY_ALLOWED":'||'"'||typ_cr_card_config_dtls.CC_APPLEPAY_ALLOWED||'"'||','||'
          "CC_ANDROIDPAY_ALLOWED":'||'"'||typ_cr_card_config_dtls.CC_ANDROIDPAY_ALLOWED||'"'||','||'
          "CC_L3_CARD_ENABLED":'||'"'||typ_cr_card_config_dtls.CC_L3_CARD_ENABLED||'"'||','||'
          "CC_QUASI_CASH":'||'"'||typ_cr_card_config_dtls.CC_QUASI_CASH||'"'||','||'
          "CC_BYPASS_TSYS_API":'||'"'||typ_cr_card_config_dtls.CC_BYPASS_TSYS_API||'"'||','||'
          "CC_DEFAULT_PRE_AUTH":'||to_string_fnc(typ_cr_card_config_dtls.CC_DEFAULT_PRE_AUTH)||',
          "CC_VISA_BUSINESS_APP_ID":'||to_string_fnc(typ_cr_card_config_dtls.CC_VISA_BUSINESS_APP_ID)||',
          "CC_ECOMMERCE_MODEL":'||to_string_fnc(typ_cr_card_config_dtls.CC_ECOMMERCE_MODEL)||',
          "CC_AT_AUTHORIZATION":'||'"'||typ_cr_card_config_dtls.CC_AT_AUTHORIZATION||'"'||','||'
          "CC_AT_SETTLEMENT":'||'"'||typ_cr_card_config_dtls.CC_AT_SETTLEMENT||'"'||','||'
          "CC_AT_CANCEL_SETTLEMENT":'||'"'||typ_cr_card_config_dtls.CC_AT_CANCEL_SETTLEMENT||'"'||','||'
          "CC_AT_CREDIT":'||'"'||typ_cr_card_config_dtls.CC_AT_CREDIT||'"'||','||'
          "CC_AT_CANCEL_CREDIT":'||'"'||typ_cr_card_config_dtls.CC_AT_CANCEL_CREDIT||'"'||','||'
          "CC_AT_PURCHASE":'||'"'||typ_cr_card_config_dtls.CC_AT_PURCHASE||'"'||','||'
          "CC_AT_OFFLINE_AUTH":'||'"'||typ_cr_card_config_dtls.CC_AT_OFFLINE_AUTH||'"'||','||'
          "CC_AT_AVS":'||'"'||typ_cr_card_config_dtls.CC_AT_AVS||'"'||','||'
          "CC_AT_AUTH_REVERSAL":'||'"'||typ_cr_card_config_dtls.CC_AT_AUTH_REVERSAL||'"'||','||'
          "CC_AT_AUTH_REVERSAL_ON_CS":'||'"'||typ_cr_card_config_dtls.CC_AT_AUTH_REVERSAL_ON_CS||'"'||','||'
          "CC_AT_PAYMENT":'||'"'||typ_cr_card_config_dtls.CC_AT_PAYMENT||'"'||','||'
          "CC_AT_CANCEL_PAYMENT":'||'"'||typ_cr_card_config_dtls.CC_AT_CANCEL_PAYMENT||'"'||','||'
          "CC_AT_INDP_CREDIT":'||'"'||typ_cr_card_config_dtls.CC_AT_INDP_CREDIT||'"'||','||'
          "CC_AT_INDP_CREDIT_AS_REFUNDS":'||'"'||typ_cr_card_config_dtls.CC_AT_INDP_CREDIT_AS_REFUNDS||'"'||','||'
          "CC_AT_CANCEL_INDP_CREDIT":'||'"'||typ_cr_card_config_dtls.CC_AT_CANCEL_INDP_CREDIT||'"'||','||'
          "CC_AT_VERIFICATION":'||'"'||typ_cr_card_config_dtls.CC_AT_VERIFICATION||'"'||','||'
          "CC_AT_RISK":'||'"'||typ_cr_card_config_dtls.CC_AT_RISK||'"'||','||'
          "CC_AT_INFO_LOOKUP":'||'"'||typ_cr_card_config_dtls.CC_AT_INFO_LOOKUP||'"'||','||'
          "CC_AT_LOOKUP_BY_CHILD":'||'"'||typ_cr_card_config_dtls.CC_AT_LOOKUP_BY_CHILD||'"'||','||'
          "CC_SO_INCLUDE_AVS":'||'"'||typ_cr_card_config_dtls.CC_SO_INCLUDE_AVS||'"'||','||'
          "CC_SO_NEGATIVE_DB":'||'"'||typ_cr_card_config_dtls.CC_SO_NEGATIVE_DB||'"'||','||'
          "CC_SO_ADDRESS_VERIFICATION":'||'"'||typ_cr_card_config_dtls.CC_SO_ADDRESS_VERIFICATION||'"'||','||'
          "CC_SO_MIN_FRAUD":'||'"'||typ_cr_card_config_dtls.CC_SO_MIN_FRAUD||'"'||','||'
          "CC_SO_THREAT_METRIX":'||'"'||typ_cr_card_config_dtls.CC_SO_THREAT_METRIX||'"'||','||'
          "CC_SO_EXPIRED_CARD":'||'"'||typ_cr_card_config_dtls.CC_SO_EXPIRED_CARD||'"'||','||'
          "CC_SO_CREDIT_RETRIEVALS":'||'"'||typ_cr_card_config_dtls.CC_SO_CREDIT_RETRIEVALS||'"'||','||'
          "CC_SO_CONTINUITY_CREDITS":'||'"'||typ_cr_card_config_dtls.CC_SO_CONTINUITY_CREDITS||'"'||','||'
          "CC_SO_OVERRIDE_ENF_BY_3DS":'||'"'||typ_cr_card_config_dtls.CC_SO_OVERRIDE_ENF_BY_3DS||'"'||','||'
          "CC_TEST_CARD_ENABLED":'||'"'||typ_cr_card_config_dtls.CC_TEST_CARD_ENABLED||'"'||','||'
          "CC_STORED_ACC_GROUP_NAME":'||to_string_fnc(typ_cr_card_config_dtls.CC_STORED_ACC_GROUP_NAME)||',
          "CC_CREDIT_ACC_GROUP_NAME":'||to_string_fnc(typ_cr_card_config_dtls.CC_CREDIT_ACC_GROUP_NAME)||',
          "CC_3DS_ENABLED":'||'"'||typ_cr_card_config_dtls.CC_3DS_ENABLED||'"'||','||'
          "CC_PREVENT_OVERDRAFT": '||'"'||typ_cr_card_config_dtls.CC_PREVENT_OVERDRAFT||'"'||'}';

      close cr_card_config_dtls;
      return local_obj;
    else
      return '{}';
    end if;
exception
when others then
 return P_Personal_Account_Nbr||sqlerrm;
end get_card_configs_fnc;
#

--changeset unknown:perm_get_card_configs_fnc_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant EXECUTE ON get_card_configs_fnc TO ${liquibase.schema}_rw
#

grant EXECUTE ON get_card_configs_fnc TO ${liquibase.schema}_ro
#

CREATE OR REPLACE SYNONYM ${liquibase.schema}txn.get_card_configs_fnc FOR get_card_configs_fnc
#
