--liquibase formatted sql
--changeset unknown:netbanx_merchant_accounts_vw runOnChange:true dbms:oracle endDelimiter:# stripComments:false
-- Each row in this view represents a merchant.
DROP VIEW netbanx_merchant_accounts_vw
#

CREATE OR REPLACE VIEW netbanx_merchant_accounts_vw
  AS (
  SELECT FIREPAY.PERSONAL_ACCOUNT_NBR AS ACCOUNT_ID,
          STAT_TYPE.DESCRIPTION        AS ACCOUNT_STATUS,
          STAT.MODIFICATION_DATE       AS ACCOUNT_STATUS_MODIFIEDDATE,
          RES_TYPE.DESCRIPTION         AS ACCOUNT_STATUS_REASON,
          FIREPAY.ACCT_NAME            AS COMPANY_NAME,
          (CASE FIREPAY.processing_type
            WHEN 'CC' THEN 'CARD'
            WHEN 'OB' THEN 'ALTP'
            ELSE FIREPAY.processing_type
          END)                         AS PROCESSING_TYPE,
          LE.CODE                      AS LEGALENTITY_CODE,
          LE.DESCRIPTION               AS LEGALENTITY_DESC,
          PLE.CODE                     AS PAYSAFE_LEGALENTITY_CODE,
          PLE.DESCRIPTION              AS PAYSAFE_LEGALENTITY_DESC,
          FIREPAY.PMLE_ID              AS PMLE,
          PMLE_TABLE.NAME              AS PMLE_NAME,
          PMLE_TABLE.TYPE_CODE         AS PMLE_TYPE,
          PMLE_TABLE.BUSINESS_MODEL    AS BUSINESS_MODEL,
          ONB_TERMS.VERSION            AS TERMS_VERSION,
          ACCT_TERMS.MODIFICATION_DATE AS TERMS_EFFECTIVE_DATE,
          FIREPAY.MCC_CODE             AS MCC_CODE,
          MCC.DESCRIPTION              AS MCC_DESCRIPTION,
          FIREPAY.SALESFORCE_ID        AS SALESFORCEID,
          MANAGER.DESCRIPTION          AS ACCOUNT_MANAGER,
          FIREPAY.CURRENCY_TYPE_CDE    AS PAYMENT_CURRENCY,
          MA.CURR_CODE                 AS PROCESSING_CURRENCY,
          FIREPAY.MODIFICATION_DATE    AS FIREPAY_MDATE,
          FIREPAY.ACCOUNT_LAST_UPDATED          AS FIREPAY_ACCOUNT_MDATE,
          FIREPAY.ACCOUNT_LAST_BATCH_UPDATED    AS FIREPAY_BATCH_MDATE,
          STAT_TYPE.MODIFICATION_DATE  AS STAT_TYPE_MDATE,
          LE.MODIFICATION_DATE         AS LE_MDATE,
          MANAGER.MODIFICATION_DATE    AS MANAGER_MDATE,
          MCC.MODIFICATION_DATE        AS MCC_MDATE,
          MA.MODIFICATION_DATE         AS MA_MDATE,
          BUSINESS_OWNERS.FC_MDATE     AS BJ_FC_MDATE,
          BUSINESS_OWNERS.AC_MDATE     AS BJ_AC_MDATE,
          BUSINESS_OWNERS.PA_MDATE     AS BJ_PA_MDATE,
          BTYP.DESCRIPTION             AS REGI_DETLS_TYPE_DESC,
          REG_DET.BUSINESS_DESCRIPTION AS REGI_DETL_BUSI_DESC,
          to_boolean_fnc(REG_DET.SHIP_GOODS_IND)            AS REGI_DETL_SHIP_GOODS,
          to_boolean_fnc(REG_DET.REFUND_POLICY_IND)         AS REGI_DETL_REFUND_POLICY,
          to_boolean_fnc(REG_DET.CARD_PRESENT_IND)          AS REGI_DETL_CARD_PRESENT,
          to_boolean_fnc(REG_DET.PREVIOUSLY_USED_CARDS_IND) AS REGI_DETL_PREV_USED_CARDS,
          REG_DET.DELIVERY_DAYS_RANGE_CDE   AS REGI_DETL_DELIVERY_DAYS_RANGE,
          REG_DET.REFUND_POLICY_DESCRIPTION AS REGI_DETL_REFUND_POLICY_DESC,
          REG_DET.MODIFICATION_DATE         AS REGI_DETL_MDATE,
          MA.DESCRIPTOR_MERCHANT_INFO     AS DESCRIPTOR_MERCHANT_INFO,
          MA.DESCRIPTOR_MERCHANT_NAME     AS DESCRIPTOR_MERCHANT_NAME,
          MA.DESCRIPTOR_STATE_PROVINCE    AS DESCRIPTOR_STATE_PROVINCE,
          MA.DESCRIPTOR_ADDRESS_LINE_1    AS DESCRIPTOR_ADDRESS_LINE_1,
          MA.DESCRIPTOR_ADDRESS_LINE_2    AS DESCRIPTOR_ADDRESS_LINE_2,
          MA.DESCRIPTOR_CITY              AS DESCRIPTOR_CITY,
          FIREPAY.IP_ADDRESS           AS IP_ADDRESS,
          FIREPAY.WEBSITE_URL          AS WEBSITE_URL,
          FIREPAY.LOCALE_CDE           AS LOCALE_CDE,
          FIREPAY.BUSINESS_DAY_START_TIME    AS BUSINESS_DAY_START_TIME,
          FIREPAY.BUSINESS_DAY_TIME_ZONE     AS BUSINESS_DAY_TIME_ZONE,
          FIREPAY.YEARLY_VOLUME_RANGE_CDE     AS YEARLY_VOLUME_RANGE_CDE,
          FIREPAY.MONTHLY_VOLUME              AS MONTHLY_VOLUME,
          FIREPAY.AVERAGE_TRANSACTION_AMOUNT  AS AVERAGE_TRANSACTION_AMOUNT,
          ADDR.ADDRESS1                AS TRADING_ADDR_ADDRESS1,
          ADDR.ADDRESS2                AS TRADING_ADDR_ADDRESS2,
          ADDR.CITY                    AS TRADING_ADDR_CITY,
          ADDR.REGION                  AS TRADING_ADDR_REGION,
          ADDR.POSTAL_CODE             AS TRADING_ADDR_POSTAL_CODE,
          ADDR.COUNTRY_CDE             AS TRADING_ADDR_COUNTRY_CDE,
          ADDRC.DESCRIPTION            AS TRADING_ADDR_COUNTRY,
          ADDR.STATE_CDE               AS TRADING_ADDR_STATE_CDE,
          ADDRS.DESCRIPTION            AS TRADING_ADDR_STATE,
          ADDR.MODIFICATION_DATE       AS TRADING_ADDR_MDATE,
          FIREPAY.SATRT_CODE                 AS SALES_REGION_CDE,
          (CASE FIREPAY.SATRT_CODE
            WHEN 'CAN' THEN 'Canada'
            WHEN 'USA' THEN 'United States'
            WHEN 'UK' THEN 'United Kingdom'
            ELSE FIREPAY.SATRT_CODE
          END)                         AS SALES_REGION,
          FIREPAY.CUSTOMER_SUPPORT_NBR       AS CUSTOMER_SUPPORT_NUMBER,
          FIREPAY.CUSTOMER_SUPPORT_EMAIL       AS CUSTOMER_SUPPORT_EMAIL,
          FIREPAY.COMPANY_REGISTRATION_NBR   AS COMPANY_REGISTRATION_NBR,
          FIREPAY.FMA_OWNED_TAX_REGISTRATION_NBR     AS TAX_REGISTRATION_NBR,
          to_boolean_fnc(FIREPAY.CHARGE_TAXES_IND)   AS CHARGE_TAXES,
          DSB.CDE                      AS DEFAULT_BANK_CDE,
          DSB.BANK_NAME                AS DEFAULT_BANK_NAME,
          FIREPAY.PARTNER_ID           AS PARTNER_ID,
          PF.NAME                      AS PARTNER_NAME,
          FIREPAY.PARTNER_PRODUCT_ID   AS PARTNER_PRODUCT_ID,
          ORG_ADDRESS.ADDRESS1         AS ORG_ADDRESS1,
          ORG_ADDRESS.ADDRESS2         AS ORG_ADDRESS2,
          ORG_ADDRESS.CITY_NAME        AS ORG_CITY_NAME,
          ORG_ADDRESS.STATE_NAME       AS ORG_STATE_NAME,
          ORG_ADDRESS.COUNTRY_NAME     AS ORG_COUNTRY_NAME,
          ORG_ADDRESS.COUNTRY_CDE      AS ORG_COUNTRY_CDE,
          ORG_ADDRESS.POSTAL_CODE      AS ORG_POSTAL_CODE,
          PP.PARTNER_PRODUCT_NAME      AS PARTNER_PRODUCT_NAME,
          TP.EQF_PROC_ID        AS EQF_PROC_ID,
          TP.EQF_MERCH_ID       AS EQF_MERCH_ID,
          TP.EQF_MERCH_PWD      AS EQF_MERCH_PWD,
          TP.CUST_NBR       AS ACRO_CUST_NBR,
          TP.SEC_CDE        AS ACRO_SEC_CDE,
          TP.CUST_CDE       AS ACRO_CUST_CDE,
          TP.ORG_ID         AS TMX_ORG_ID,
          TP.API_KEY        AS TMX_API_KEY,
          TP.CAR_PROC_ID        AS CAR_PROC_ID,
          TP.CAR_MERCH_ID       AS CAR_MERCH_ID,
          TP.CAR_MERCH_PWD      AS CAR_MERCH_PWD,
          KEYWORDS.KEYWORDS_LST AS KEYWORDS,
          FIREPAY.CREATION_DATE AS CREATION_DATE,
          FIREPAY.MERCH_BUSINESS_CATEGORY_CDE   AS MERCH_BUSINESS_CATEGORY_CDE,
          MBC.DESCRIPTION       AS MERCH_BUSINESS_CATEGORY_DESC,
          to_boolean_fnc(FIREPAY.RECUR_BILLING_IND) AS RECURRING_BILLING,
          FIREPAY.MERACT_CDE    AS CATEGORY_CDE,
          MAC.DESCRIPTION       AS CATEGORY_DESC,
          FIREPAY.SHOP_ID       AS SHOP_ID,
          SHOP.NAME             AS SHOP_NAME,
          GRP_ACCT.VALUE        AS CONTRACT_TYPE,
          OSP.PROPERTY_VALUE    AS AMEX_MID,
          GRP_ACCT_BRS.VALUE    AS BUSINESS_RELATION_NAME,
          ACC_GROUPS.ACC_GROUP_LST AS ACC_GROUPS,
          to_boolean_fnc(FIREPAY.SPLIT_TRANSFERS_IND)                AS SPLIT_TRANSFERS_IND,
          to_boolean_fnc(FIREPAY.TRANSFER_API_IND)                   AS TRANSFER_API_IND,
          to_boolean_fnc(FIREPAY.ALLOW_OVERDRAFT_TRANSFER_IND)       AS ALLOW_OVERDRAFT_TRANSFER_IND,
          to_boolean_fnc(DCOA.ALTP_ACCOUNT_IND)       AS ALTP_ACCOUNT_IND
     FROM FPA.FIREPAY_PERS_ACCNTS FIREPAY
     LEFT JOIN (SELECT    AC.PERSONAL_ACCOUNT_NBR as PERSONAL_ACCOUNT_NBR,
                          MAX( FC.MODIFICATION_DATE ) FC_MDATE,
                          MAX( AC.MODIFICATION_DATE) AC_MDATE,
                          MAX( PA.MODIFICATION_DATE ) PA_MDATE
                   FROM FPA.FPA_CONTACTS FC
                   LEFT JOIN FPA.FPA_ACCOUNT_CONTACTS AC ON FC.ID = AC.CONTACT_ID
                   LEFT JOIN FPA.FPA_CONTACT_PREVIOUS_ADDRESSES PA ON FC.ID = PA.CONTACT_ID
       GROUP BY AC.PERSONAL_ACCOUNT_NBR
                ) BUSINESS_OWNERS
            ON BUSINESS_OWNERS.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR
     LEFT JOIN (SELECT MERCA_ACCOUNT_ID,
                        LISTAGG(MKEY.KEYWORD, ', ') WITHIN GROUP (ORDER BY MKEY.KEYWORD DESC) KEYWORDS_LST
                FROM FALCON.MERCHANT_ACCNT_KEYWORDS MKEY
                GROUP BY MKEY.MERCA_ACCOUNT_ID) KEYWORDS
            ON KEYWORDS.MERCA_ACCOUNT_ID = FIREPAY.PERSONAL_ACCOUNT_NBR
     LEFT JOIN (SELECT AGF.PERSONAL_ACCOUNT_NBR,
                        LISTAGG(AGT.ACCOG_NAME, ',') WITHIN GROUP (ORDER BY AGT.ACCOG_NAME DESC) ACC_GROUP_LST
                from FPA.FPA_ACCOUNT_GROUP_FMAS AGF
                     LEFT JOIN FPA.FPA_ACCOUNT_GROUP_TYPS AGT ON AGF.ACCOUNT_GROUP_ID = AGT.ID
                GROUP BY AGF.PERSONAL_ACCOUNT_NBR) ACC_GROUPS
            ON ACC_GROUPS.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR
     LEFT JOIN FPA.PERS_ACCNT_STATS STAT
            ON STAT.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR
     LEFT JOIN FPA.PERS_ACCNT_STAT_TYP STAT_TYPE
            ON STAT.PERSONAL_ACCOUNT_STAT_TYPE_CDE = STAT_TYPE.PERSONAL_ACCOUNT_STAT_TYPE_CDE
     LEFT JOIN FPA.PERS_ACCNT_STAT_REAS_TYPS RES_TYPE
            ON STAT.STAT_REASON_TYP = RES_TYPE.STAT_REASON_TYP_CDE
     LEFT JOIN FPA.MERCA_LEGAL_ENTITY_TYPES LE
            ON FIREPAY.MELET_CODE = LE.CODE
     LEFT JOIN FPA.LEGAL_ENTITY_TYPES PLE
            ON FIREPAY.LEENT_CODE = PLE.CODE
     LEFT JOIN FALCON.MERCHANT_CATEGORY_TYPES MCC
            ON MCC.CODE = FIREPAY.MCC_CODE
     LEFT JOIN FPA.ACCOUNT_MANAGERS MANAGER
            ON FIREPAY.ACCMA_CODE = MANAGER.CODE
     LEFT JOIN FALCON.MERCHANT_ACCOUNTS MA
            ON MA.ACCOUNT_ID = FIREPAY.PERSONAL_ACCOUNT_NBR
     LEFT JOIN FPA.FPA_FMA_TRADING_ADDRESSES ADDR
            ON FIREPAY.PERSONAL_ACCOUNT_NBR = ADDR.PERSONAL_ACCOUNT_NBR
     LEFT JOIN ALX.ALX_COUNTRY_TYPS ADDRC ON ADDR.COUNTRY_CDE = ADDRC.CDE
     LEFT JOIN ALX.ALX_STATE_TYPS ADDRS ON ADDR.STATE_CDE = ADDRS.CDE AND ADDR.COUNTRY_CDE = ADDRS.COUNTY_CDE
    LEFT JOIN MMCHT.ORGANIZATION_ADDRESS_VW ORG_ADDRESS
            ON FIREPAY.PARTY_ID = ORG_ADDRESS.PARTY_ID
     LEFT JOIN FPA.FPA_FMA_REGISTRATION_DETAILS REG_DET ON FIREPAY.PERSONAL_ACCOUNT_NBR = REG_DET.PERSONAL_ACCOUNT_NBR
     LEFT JOIN FPA.BUSINESS_TYPS BTYP ON REG_DET.BUSINESS_CDE = BTYP.BUSIT_CDE
     LEFT JOIN FPA.FPA_SOURCE_BANKS DSB ON FIREPAY.FMA_DEFAULT_BANK_CDE = DSB.CDE
     LEFT JOIN FPA.FPA_PARTNER_PRODUCTS PP ON PP.id = FIREPAY.PARTNER_PRODUCT_ID
     LEFT JOIN FPA.FPA_PARTNERS PF ON PF.id = FIREPAY.PARTNER_ID
     LEFT JOIN FPA.FPA_PMLE PMLE_TABLE ON PMLE_TABLE.id = FIREPAY.PMLE_ID
     LEFT JOIN (select MSP.MERCHANT_ACCOUNT_ID,
        max(case when MSP.SERVNT_CDE = 'EQF' and MSP.MERSPT_CDE = 'PROC_ID' then VALUE end ) as EQF_PROC_ID,
        max(case when MSP.SERVNT_CDE = 'EQF' and MSP.MERSPT_CDE = 'MERCH_ID'  then VALUE  end ) as EQF_MERCH_ID,
        max(case when MSP.SERVNT_CDE = 'EQF' and MSP.MERSPT_CDE = 'MERCH_PWD'  then VALUE  end ) as EQF_MERCH_PWD,
        max(case when MSP.SERVNT_CDE = 'ACRO' and MSP.MERSPT_CDE = 'CUST_NBR'  then VALUE  end ) as CUST_NBR,
        max(case when MSP.SERVNT_CDE = 'ACRO' and MSP.MERSPT_CDE = 'SEC_CDE'  then VALUE  end ) as SEC_CDE,
        max(case when MSP.SERVNT_CDE = 'ACRO' and MSP.MERSPT_CDE = 'CUST_CDE' then VALUE end ) as CUST_CDE,
        max(case when MSP.SERVNT_CDE = 'TMX' and MSP.MERSPT_CDE = 'ORG_ID' then VALUE end ) as ORG_ID,
        max(case when MSP.SERVNT_CDE = 'TMX' and MSP.MERSPT_CDE = 'API_KEY'  then VALUE  end ) as API_KEY,
        max(case when MSP.SERVNT_CDE = 'CAR' and MSP.MERSPT_CDE = 'PROC_ID' then VALUE end ) as CAR_PROC_ID,
        max(case when MSP.SERVNT_CDE = 'CAR' and MSP.MERSPT_CDE = 'MERCH_ID'  then VALUE  end ) as CAR_MERCH_ID,
        max(case when MSP.SERVNT_CDE = 'CAR' and MSP.MERSPT_CDE = 'MERCH_PWD'  then VALUE  end ) as CAR_MERCH_PWD
        from FALCON.FCN_MERCH_SVC_PROPERTIES MSP group by MSP.MERCHANT_ACCOUNT_ID
     ) TP ON TP.MERCHANT_ACCOUNT_ID = FIREPAY.PERSONAL_ACCOUNT_NBR
     LEFT JOIN FPA.FPA_MERCH_BUS_CATEGORY_TYPS MBC ON FIREPAY.MERCH_BUSINESS_CATEGORY_CDE = MBC.CDE
     LEFT JOIN FPA.FPA_MERCH_ACCT_CATEGORY_TYPS MAC ON FIREPAY.MERACT_CDE = MAC.ACCOUNT_CATEGORY_CDE
     LEFT JOIN FPA.FPA_SHOPS SHOP ON FIREPAY.SHOP_ID = SHOP.ID
     LEFT JOIN DCO.DCO_MERCHANT_ACCOUNTS DCOA ON DCOA.ACCOUNT_ID = FIREPAY.PERSONAL_ACCOUNT_NBR
     LEFT JOIN FPA.FPA_ACCOUNT_TERMS ACCT_TERMS ON ACCT_TERMS.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR
     LEFT JOIN FPA.FPA_ONBOARDING_TERMS ONB_TERMS ON ONB_TERMS.ID = ACCT_TERMS.TERM_ID
     LEFT JOIN FPA.FPA_GROUPED_ACCOUNTS GRP_ACCT
            ON GRP_ACCT.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR AND GRP_ACCT.GROUP_CODE = 'CONTRACT_TYPE'
     LEFT JOIN FPA.FPA_GROUPED_ACCOUNTS GRP_ACCT_BRS
            ON GRP_ACCT_BRS.PERSONAL_ACCOUNT_NBR = FIREPAY.PERSONAL_ACCOUNT_NBR 
           AND GRP_ACCT_BRS.GROUP_CODE = 'BIZ_RELATIONSHIP'
     LEFT JOIN FPA.FPA_ONBOARD_SETUP_PROPERTIES OSP
            ON OSP.ENTITY_VALUE = FIREPAY.PERSONAL_ACCOUNT_NBR AND OSP.PROPERTY_TYPE = 'AMEX_MID'
     WHERE FIREPAY.ACCTT_CDE='FMA'
      AND STAT.THRU_DATE IS NULL
   )
#

--changeset unknown:netbanx_merchant_accounts_vw_perm runOnChange:true endDelimiter:# dbms:oracle stripComments:false
grant SELECT ON  netbanx_merchant_accounts_vw TO ${liquibase.schema}_rw
#

grant SELECT ON  netbanx_merchant_accounts_vw TO ${liquibase.schema}_ro
#

CREATE OR REPLACE  SYNONYM ${liquibase.schema}txn.netbanx_merchant_accounts_vw FOR netbanx_merchant_accounts_vw
#
