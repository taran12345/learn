// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

/**
 * This file contains constants which are used across this application.
 *
 * @author pratyushagarwal
 *
 */

public final class DataConstants {
  public static final String JSONDB_TABLE_PATH = "/MPP/jsondb/masterMerchant/";
  public static final long KAFKA_POLLTIMEOUT = 30000;
  public static final int KAFKA_CONSUMER_CORE_THREADPOOL_SIZE = 10;
  public static final int KAFKA_CONSUMER_MAX_THREADPOOL_SIZE = 50;
  public static final int KAFKA_CONSUMER_THREAD_QUEUE_SIZE = 100;
  public static final String KAFKA_CONSUMER_EXECUTOR_NAME = "KafkaConsumerAsyncExecutor";
  public static final String KAFKA_SESSION_TIMEOUT_VALUE = "120000";
  public static final String KAFKA_COMMIT_INTERVAL_VALUE = "3000";
  public static final String AUTO_COMMIT_INTERVAL_MILLISECONDS = "auto.commit.interval.ms";
  public static final String KAFKA_SESSION_TIMEOUT_MILLISECONDS = "session.timeout.ms";
  public static final String KAFKA_TOPIC_DELIMITER = ".";
  public static final String KAFKA_AUTO_OFFSET_RESET = "latest";
  public static final String MASTER_MERCHANT_GROUP_ID = "group.id";
  public static final String ID = "id";
  public static final String DOCUMENT_ID = "_id";
  public static final String NAME = "name";
  public static final String MASTER_MERCHANT_TYPE = "masterMerchantType";

  public static final String GROUP_KEY = "group.id";
  public static final String CLIENT_KEY = "client.id";
  public static final String PAYMENTACCOUNTID = "paymentAccountId";
  public static final String MASTERMERCHANTID = "masterMerchantId";
  public static final String PROCESSING_ACCOUNT_ID = "processingAccountId";
  public static final String GROUP_ID = "groupId";
  public static final String ORIGIN = "origin";
  public static final String REFERENCE_ID = "referenceId";
  public static final String TYPE = "type";

  public static final String CORRELATIONID = "X-INTERNAL-CORRELATION-ID";

  public static final String VIEW_PERMISSION = "merchant:master-merchant:view";
  public static final String CONFIGURE_PERMISSION = "merchant:master-merchant:configure";

  public static final int MAX_RESULTS_FOR_SMARTSEARCH = 10000;
  public static final int MAX_LIMIT_FOR_SMARTSEARCH = 500;
  public static final int MIN_LENGTH_FOR_SEARCHQUERY = 2;
  public static final long ZERO = 0;
  public static final String WILDCARD_SYMBOL = "*";

  public static final String ADVANCED_SEARCH_TEMPLATE = "mm_customgroupsearch";
  public static final String SMART_SEARCH_TEMPLATE = "mm_smartsearch";
  public static final String PMLE_SEARCH_TEMPLATE = "mm_pmle_search";
  public static final String PROCESSING_ACCOUNT_UUID_SEARCH_TEMPLATE = "mm_processing_account_uuid_search";
  public static final String PROCESSING_ACCOUNT_REF_ID_SEARCH_TEMPLATE = "mm_processing_account_reference_id_search";
  public static final String PAYMENT_ACCOUNT_UUID_SEARCH_TEMPLATE = "mm_payment_account_uuid_search";

  public static final String ELASTIC_RESPONSE_PROCESSING_ACCOUNT_JSON_PATH =
      "$.hits.hits[*]._source.paymentAccounts[*].processingAccounts[0]";
  public static final String ELASTIC_RESPONSE_PAYMENT_ACCOUNT_JSON_PATH = "$.hits.hits[*]._source.paymentAccounts[0]";

  public static final String REFERENCE_MAPPING_NESTED_ENTITY_TYPE_PROCESSING_ACCOUNT = "processingAccount";
  public static final String REFERENCE_MAPPING_NESTED_ENTITY_TYPE_PAYMENT_ACCOUNT = "paymentAccount";
  public static final String REFERENCE_MAPPING_NESTED_ENTITY_TYPE_GROUP = "group";
  public static final String RECORD_TYPE_MERCHANT = "MERCHANT";
  public static final String REFERENCE_MAPPING_KEY_SEPARATOR = "#";

  public static final String PAYMENT_ACCOUNTS = "paymentAccounts";
  public static final String PROCESSING_ACCOUNTS = "processingAccounts";

  public static final String PAYMENT_AGGREGATIONS = "paymentAggregations";
  public static final String PROCESSING_AGGREGATIONS = "processingAggregations";
  public static final String ROOT_AGGREGATIONS = "rootAggregations";

  public static final int AGGREGATION_RESULT_SIZE = 1000;
  public static final String MERCHANT_SUMMARY_COUNT_TOTAL_COUNT_TYPE = "total";
  public static final String MERCHANT_SUMMARY_COUNT_STATUS_TYPE = "status";
  public static final String REFERENCE_MAPPING_DOCUMENT_ID = "documentId";

  public static final String EUREKA_INSTANCE_HOSTNAME = "EUREKA_INSTANCE_HOSTNAME";

  public static final String DAY = "day";
  public static final String MONTH = "month";
  public static final String YEAR = "year";

  public static final String DEFAULT_PAYMENT_VEHCILE = "WIRE";

  public static final String MERCHANT_SEARCH_DEFAULT_SORT_FIELD = "merchantId";
  public static final String OCTET_STREAM_MEDIA_TYPE = "application/octet-stream";
  public static final String ATTACHMENT_FILENAME = "attachment; filename=\"merchant_results.csv\"";

  public static final String GROUP_CONTAINER_FACTORY = "groupContainerFactory";
  public static final String PAYMENT_CONTAINER_FACTORY = "paymentAccountContainerFactory";
  public static final String PROCESSING_CONTAINER_FACTORY = "processingAccountContainerFactory";
  public static final String MASTER_MERCHANT_CONTAINER_FACTORY = "kafkaListenerContainerFactory";
  public static final String RS2_DATA_CONTAINER_FACTORY = "rs2DataContainerFactory";
  public static final String APM_DATA_CONTAINER_FACTORY = "apmDataContainerFactory";

  public static final String EMPTY_JSON_ARRAY_STRING = "[]";
  public static final String EMPTY_JSON_STRING = "{}";

  public static final String UNITY = "UNITY";

  public static final String ENABLED = "Enabled";
  public static final String SYSTEM = "SYSTEM";
  public static final String ADMIN_USER = "ADMIN";

  public static final String ENABLED_DATE = "enabledDate";
  public static final String CREATION_DATE = "creationDate";
  public static final String DATE_TIME_SEPARATOR = " ";
  public static final String SPACE = " ";
  public static final String DATE_FIELD_ELASTIC_RESPONSE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

  public static final String SMARTSEARCH_REGEX = ".*[a-zA-Z_0-9]+.*";

  // Used for fetching mvel_templates.
  public static final String NBX_ACCOUNT_TO_GROUPDATA_MVEL_TEMPLATE = "netbanxMerchantAccount_to_groupData";
  public static final String NBX_ACCOUNT_TO_PAYMENTDATA_MVEL_TEMPLATE = "netbanxMerchantAccount_to_paymentAccountData";
  public static final String NBX_ACCOUNT_TO_PROCESSINGDATA_MVEL_TEMPLATE =
      "netbanxMerchantAccount_to_processingAccountData";
  public static final String NBX_ACCOUNT_TO_RS2_PAYMENTDATA_MVEL_TEMPLATE =
      "netbanxMerchantAccount_to_rs2_processingAccountData";
  public static final String IRVINE_ACCOUNT_TO_GROUPDATA_MVEL_TEMPLATE = "irvineMerchantAccount_to_groupData";
  public static final String IRVINE_ACCOUNT_TO_PAYMENTDATA_MVEL_TEMPLATE =
      "irvineMerchantAccount_to_paymentAccountData";
  public static final String IRVINE_ACCOUNT_TO_PROCESSINGDATA_MVEL_TEMPLATE =
      "irvineMerchantAccount_to_processingAccountData";
  public static final int SEND_MERCHANT_TO_STREAM_PAGINATION_QUERY_LIMIT = 1000;
  public static final String RS2_GROUP_ACCOUNT_TO_MM_DATA_MVEL_TEMPLATE =
      "rs2Group_merchantAccount_to_mmData";
  public static final String RS2_SUBGROUP_ACCOUNT_TO_MM_DATA_MVEL_TEMPLATE =
      "rs2SubGroup_merchantAccount_to_mmData";

  // used for merchant search in elastic
  public static final String TRUE = "true";
  public static final String MID = "mid";
  public static final String MID_ENABLED = "midEnabled";
  public static final String REFERENCE_ID_PATH = "sourceAuthority.referenceId";

  // used for schedulers
  public static final String GET_HOST_ADDRESS = "getHostAddress";
  public static final String GET_HOST_NAME = "getHostName";
  public static final String GET_CANONICAL_HOST_NAME = "getCanonicalHostName";

  public static final String DESTINATION_BANK_ACCOUNT_NUMBER = "destinationBank_accountNumber";
  public static final String DESPOSIT_ACCOUNT_NUMBER = "despositAccountNumber";

  private DataConstants() {
  }
}
