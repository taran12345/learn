--liquibase formatted sql
--changeset unknown:all_grants_fnc runOnChange:true endDelimiter:# dbms:oracle stripComments:false

GRANT SELECT ON FPA.ADDRESSES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.PARTY_ADDRESSES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.PARTIES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.GEO_AREAS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FIREPAY_PERS_ACCNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.PERS_ACCNT_STAT_TYP TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.MERCA_LEGAL_ENTITY_TYPES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.LEGAL_ENTITY_TYPES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.PERS_ACCNT_STATS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.MERCHANT_CATEGORY_TYPES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.ACCOUNT_MANAGERS TO MMCHT,MMCHTTXN  WITH GRANT OPTION
#

GRANT SELECT ON FALCON.MERCHANT_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_ACCOUNT_CONTACTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_CONTACT_PREVIOUS_ADDRESSES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_CONTACTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FMA_PAYMENT_SCHEDULE TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FMA_PAYMENT_FREQUENCY_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FMA_PMT_ON_HOLD_REASONS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.PMT_FMA_SOURCE_BANK_SETUPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.PMT_SOURCE_BANKS_SETUP TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_SOURCE_BANKS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_SOURCE_BANK_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.PMT_DEST_BANK_INFO TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_PMLE TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_FMA_REGISTRATION_DETAILS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_FMA_TRADING_ADDRESSES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.BUSINESS_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.PROCESSOR_INFO_BRANDS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.FCN_PROCESSOR_INFO_BRAND_PROPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.PROCESSOR_INFOS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_PARTNER_PRODUCTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_PARTNERS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.MERCHANT_ACCT_PERMISSIONS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.BANK_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.ACQUIRER_CB_IDENTIFIERS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_SALES_ENTITY_FMAS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_SALES_ENTITIES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_MERCH_GWY_PROPERTIES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_GWY_PROVIDER_PROPERTY_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_GWY_PROVIDER_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.AGREEMENT_FEES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_ALT_ACCOUNT_AGREEMENTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_ACCOUNT_GROUP_FMAS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FEE_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON ALX.ALX_COUNTRY_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON ALX.ALX_STATE_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.FCN_MERCH_SVC_PROPERTIES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FALCON.MERCHANT_ACCNT_KEYWORDS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_ACCOUNT_GROUP_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_MERCH_BUS_CATEGORY_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.PERS_ACCNT_STAT_REAS_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.TRANSACTION_THRESHOLDS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.THRESHOLD_TYPES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_MERCH_ACCT_CATEGORY_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_SPLIT_TRANSFERS_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_FREQUENCY_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_FMA_TXN_THRESHOLDS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_FMA_TXN_THRESHOLD_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.ACCT_AGREEMENTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.AGREEMENTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_AGREEMENT_FEE_TRANSF_ACCTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.AGREEMENT_TERMS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.TERM_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON ALTP.ACCOUNT_GROUPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON ALTP.ACCOUNT_GROUP_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON ALTP.ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_SHOPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FMA_PAYMENT_SCHEDULE_JN TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON DCO.DCO_MERCHANT_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON JUR.ECHECK_ACCT_PROCESSORS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON JUR.ECHECK_MERCHANT_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON JUR.ECHECK_ACCT_STATUS_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_SOURCE_BANK_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON DCO.DCO_PAYMENT_GWY_ACCT_INFOS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON DCO.DCO_AUTHEN_GWY_ACCT_INFOS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON DCO.DCO_AUTHEN_GWY_ACCT_PROPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON DCO.DCO_PROCESSOR_PROPERTIES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON JUR.JUR_REPRESENTMENT_SCHEDULE TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON JUR.JUR_ACCT_PROCESSOR_PROPERTIES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON JUR.JUR_ACCT_PROC_PROPERTY_TYPS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_ACCOUNT_TERMS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_ONBOARDING_TERMS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_GROUPED_ACCOUNTS TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

GRANT SELECT ON FPA.FPA_ONBOARD_SETUP_PROPERTIES TO MMCHT,MMCHTTXN WITH GRANT OPTION
#

