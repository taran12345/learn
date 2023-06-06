// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.op.commons.indexdb.repository;

import com.paysafe.op.commons.indexdb.dto.BaseDeleteByQueryRequestDto;
import com.paysafe.op.commons.indexdb.dto.BaseDeleteByQueryResponseDto;
import com.paysafe.op.commons.indexdb.dto.BaseGetResponseDto;
import com.paysafe.op.commons.indexdb.dto.BaseIndexBulkRequestDto;
import com.paysafe.op.commons.indexdb.dto.BaseIndexBulkResponseDto;
import com.paysafe.op.commons.indexdb.dto.BaseIndexBulkResponseInfoDto;
import com.paysafe.op.commons.indexdb.dto.BaseIndexDto;
import com.paysafe.op.commons.indexdb.exception.IndexExceptionHandler;
import com.paysafe.op.commons.indexdb.interceptor.Constants;
import com.paysafe.op.commons.indexdb.util.ElasticSearchUtil;
import com.paysafe.op.commons.indexdb.util.ElasticsearchApiUtil;
import com.paysafe.op.errorhandling.exceptions.BadRequestException;
import com.paysafe.op.errorhandling.exceptions.ExternalGatewayErrorException;
import com.paysafe.op.errorhandling.exceptions.InternalErrorException;
import com.paysafe.op.errorhandling.exceptions.ResourceAlreadyExistsException;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.util.Strings;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteResponse.Result;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.ClearScrollRequest;
import org.elasticsearch.action.search.ClearScrollResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.LoggingDeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.json.JsonXContentParser;
import org.elasticsearch.index.engine.DocumentMissingException;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.script.mustache.SearchTemplateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Base Index Repository class for Elastic search.
 */
@RefreshScope
public class BaseIndexRepository {
  static final Logger LOGGER = LoggerFactory.getLogger(BaseIndexRepository.class);
  private static final String HOST_SEPARATOR = ":";
  private static final String URL_PATH_SEPARATOR = "/";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  private RestHighLevelClient client;
  private RestClient lowLevelRestClient;
  private String index;
  public final IndexExceptionHandler exceptionHandler = new IndexExceptionHandler(LOGGER);

  @Value("${elasticsearch.clusterName:#{null}}")
  String clusterName;

  @Value("${elasticsearch.nodes:#{null}}")
  String nodes;

  @Value("${elasticsearch.scheme:http}")
  String scheme;

  @Value("${elasticsearch.username:#{null}}")
  String username;

  @Value("${elasticsearch.password:#{null}}")
  String password;

  @Value("${elasticsearch.connectTimeout:5000}")
  Integer connectTimeout;

  @Value("${elasticsearch.socketTimeout:60000}")
  Integer socketTimeout;

  /**
   * Initializes the client object.
   */
  @PostConstruct
  public void setClient() {
    if (StringUtils.isNotBlank(clusterName) && nodes != null) {
      setClient(clusterName, scheme, nodes.split(","));
    }
  }

  /**
   * Initializes the client object.
   *
   * @param clusterName String containing name of the cluster.
   * @param protocol scheme type.
   * @param nodes List of String containing host and port combinations.
   */
  public void setClient(String clusterName, String protocol, String... nodes) {
    try {
      List<HttpHost> hosts = new ArrayList<>();
      for (String node : nodes) {
        String[] host = node.trim().split(HOST_SEPARATOR);
        hosts.add(new HttpHost(host[0], NumberUtils.toInt(host[1]), protocol));
      }
      final RestClientBuilder restClientBuilder =
          RestClient.builder(hosts.toArray(new HttpHost[hosts.size()])).setRequestConfigCallback(getConfigCallback());
      if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)) {
        LOGGER.info("ES credentials added to rest client builder.");
        restClientBuilder.setHttpClientConfigCallback(getHttpClientConfigCallback());
      }
      initializeClients(restClientBuilder);
      if (client.ping(RequestOptions.DEFAULT)) {
        LOGGER.info("Created Elastic search RestHighLevelClient connected to {}", hosts);
      } else {
        LOGGER.error("No nodes available on the cluster:{}", clusterName);
        throw InternalErrorException.builder().databaseError()
            .detail("No nodes available on the cluster:{}", clusterName).build();
      }
    } catch (Exception e) {
      LOGGER.error("Unable to set client.", e);
      throw ExternalGatewayErrorException.builder().gatewayError().cause(e)
          .detail("Check the host details again, or check the status of the node.").build();
    }
  }

  private RestClientBuilder.HttpClientConfigCallback getHttpClientConfigCallback() {
    return (HttpAsyncClientBuilder httpClientBuilder) -> {
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY,
          new UsernamePasswordCredentials(username, password));
      return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
    };
  }

  private void initializeClients(final RestClientBuilder restClientBuilder) {
    if (!Objects.nonNull(lowLevelRestClient)) {
      lowLevelRestClient = restClientBuilder.build();
    }
    if (!Objects.nonNull(client)) {
      client = new RestHighLevelClient(restClientBuilder);
    }
  }

  private RestClientBuilder.RequestConfigCallback getConfigCallback() {
    return new RestClientBuilder.RequestConfigCallback() {
      @Override
      public RequestConfig.Builder customizeRequestConfig(RequestConfig.Builder requestConfigBuilder) {
        return requestConfigBuilder.setConnectTimeout(connectTimeout).setSocketTimeout(socketTimeout);
      }
    };
  }

  /**
   * Method to get elastic transport client.
   *
   * @return TransportClient.
   */
  public RestHighLevelClient getClient() {
    return client;
  }

  /**
   * Close connection with elasticsearch.
   */
  @PreDestroy
  public void destroy() throws IOException {
    lowLevelRestClient.close();
    client.close();
  }

  public void setIndex(String index) {
    this.index = index;
  }

  /**
   * Insert a document in elasticsearch index. Returns the ID of the document created.
   *
   * @param request DTO containing name of the index, type and Map to be inserted.
   */
  public String index(BaseIndexDto request, String... indices) {
    final String indexName = this.getIndexName(indices);
    try {
      IndexRequest indexRequest =
          new IndexRequest(indexName, request.getType(), request.getId()).source(request.getDocument());
      IndexResponse response = client.index(indexRequest, RequestOptions.DEFAULT);
      if (Result.CREATED.equals(response.getResult())) {
        LOGGER.info("Document successfully created at index:{}, type:{}, id:{}", response.getIndex(),
            response.getType(), response.getId());
      } else {
        LOGGER.info("Document successfully updated at index:{}, type:{}, id:{}, version:{}", response.getIndex(),
            response.getType(), response.getId(), response.getVersion());
      }
      return response.getId();
    } catch (Exception exception) {
      LOGGER.error("Unable to index the document.", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  private String getIndexName(final String... indices) {
    return (indices.length == 0) ? this.index
        : Arrays.stream(indices).collect(Collectors.joining(Constants.COMMA_DELIMETER));
  }

  /**
   * Retrieve a document provided index, type and id.
   *
   * @param request DTO containing name of the index, type and id of the document to be retrieved.
   * @param indices name of the indices, pass null if original index is to be used.
   * @return Map containing the retrieved document. If document is not present, null is returned.
   */
  public Map<String, Object> getDocument(BaseIndexDto request, String... indices) {
    Optional<GetResponse> response = getDocumentResponse(request, index);
    return response.map(GetResponse::getSource).orElse(null);
  }

  /**
   * Retrieve a document and its version provide index, type and id
   *
   * @param request DTO containing name of the index, type and id of the document to be retrieved.
   * @param indices name of the indices, pass null if original index is to be used.
   * @return Object containing the retrieved document and version. If document is not present, null is returned.
   */
  public BaseGetResponseDto getDocumentWithVersion(BaseIndexDto request, String... indices) {
    Optional<GetResponse> response = getDocumentResponse(request, index);
    return response.map(ElasticSearchUtil::toResponseDto).orElse(null);
  }

  private Optional<GetResponse> getDocumentResponse(BaseIndexDto request, String... indices) {
    try {
      final String indexName = this.getIndexName(indices);
      final String type = Strings.isBlank(request.getType()) ? ElasticsearchApiUtil.ALL : request.getType();
      GetRequest getRequest = new GetRequest(indexName, type, request.getId());
      GetResponse response = client.get(getRequest, RequestOptions.DEFAULT);
      LOGGER.debug("Document retrieval response:{}", response);
      return response.isExists() ? Optional.of(response) : Optional.empty();
    } catch (Exception e) {
      LOGGER.error("Document retrieval failed.", e);
      throw BadRequestException.builder().requestNotParsable().cause(e).build();
    }
  }

  /**
   * Delete a document provided index, type and id. If document not present, the operation succeeds silently.
   *
   * @param request DTO containing name of the index, type and id of the document to be retrieved.
   */
  public void deleteDocument(BaseIndexDto request) {
    try {
      DeleteRequest deleteRequest = new DeleteRequest(index, request.getType(), request.getId());
      DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
      LOGGER.debug("Document delete response:{}", response);
    } catch (Exception e) {
      LOGGER.error("Document could not be deleted.", e);
      throw BadRequestException.builder().requestNotParsable().cause(e).build();
    }
  }

  /**
   * Updates an existing document.
   *
   * @param request DTO containing name of the index, type, id of the document and Map containing updated fields
   * @return true if the update succeeded
   * @throws DocumentMissingException if the document does not exist
   * @throws ResourceAlreadyExistsException if the document was already updated compared to the specific version
   * @throws BadRequestException for all other Elasticsearch exceptions
   * @throws InternalErrorException for all other exceptions
   */
  public boolean update(BaseIndexDto request) {
    try {
      UpdateRequest updateRequest =
          new UpdateRequest(index, request.getType(), request.getId()).doc(request.getDocument());
      if (Objects.nonNull(request.getUpdateSpecificVersion())) {
        updateRequest.version(request.getUpdateSpecificVersion());
      }
      UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
      LOGGER.debug("Update response received:{}", response);
      return true;
    } catch (ElasticsearchException | IOException e) {
      // won't actually return; always throws
      return exceptionHandler.handleUpdateException(request, e);
    }
  }

  /**
   * Updates a document. If it is not already present, it creates the provided document.
   *
   * @param updateDto DTO containing the index, id, type and Map representing updates.
   * @param insertData Map containing the document to be inserted if document to be updated is not present.
   * @param indices name of the indices onto which document to be inserted, pass null if original index is to be used.
   */
  public void upsert(BaseIndexDto updateDto, Map<String, Object> insertData, final String... indices) {
    try {
      final String indexName = this.getIndexName(indices);
      IndexRequest indexRequest =
          new IndexRequest(indexName, updateDto.getType(), updateDto.getId()).source(insertData);
      UpdateRequest updateRequest = new UpdateRequest(indexName, updateDto.getType(), updateDto.getId())
          .doc(updateDto.getDocument()).upsert(indexRequest);
      UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
      LOGGER.debug("Update response received:{}", response);
    } catch (Exception e) {
      LOGGER.error("Upsert failed.", e);
      throw BadRequestException.builder().requestNotParsable().cause(e).build();
    }
  }

  /**
   * Executes a search request.
   *
   * @param searchRequest to search.
   * @return search response.
   */
  public SearchResponse search(SearchRequest searchRequest) {
    try {
      return client.search(searchRequest, RequestOptions.DEFAULT);
    } catch (Exception exception) {
      LOGGER.error("Search operation failed.", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  /**
   * This method provides the capability to search by template.
   *
   * @param index name of the index.
   * @param scriptName name of the script.
   * @param parameters body parameters to be embedded in the script.
   * @return SearchResponse
   */
  public SearchResponse searchWithTemplate(String index, String scriptName, Map<String, Object> parameters) {
    SearchTemplateRequest templateRequest = new SearchTemplateRequest();
    templateRequest.setRequest(new SearchRequest(index));
    templateRequest.setScriptType(ScriptType.STORED);
    templateRequest.setScript(scriptName);
    templateRequest.setScriptParams(parameters);
    try {
      return client.searchTemplate(templateRequest, RequestOptions.DEFAULT).getResponse();
    } catch (IOException exception) {
      LOGGER.error("Failed to query with search template.", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  private String templateQueryString(String scriptName, Map<String, Object> parameters) {
    Map<String, Object> params = new HashMap<>();
    params.put(ElasticsearchApiUtil.TEMPLATE_ID, scriptName);
    params.put(ElasticsearchApiUtil.TEMPLATE_PARAMS, parameters);
    return MAPPER.convertValue(params, JsonNode.class).toString();
  }

  /**
   * This method transforms the response string returned by elasticsearch to SearchResponse.
   *
   * Note:- This method parses only the query results but not the aggregations etc. This method can be extended to
   * support parsing aggregation. Please see the below link.
   * http://discuss.elastic.co/t/elasticsearch-json-response-to-searchresponse-object/124394/6
   *
   * @param jsonString response returned by ES.
   * @return SearchResponse
   */
  private SearchResponse getSearchResponse(String jsonString) {
    try {
      XContentParser parser = new JsonXContentParser(NamedXContentRegistry.EMPTY, LoggingDeprecationHandler.INSTANCE,
          new JsonFactory().createParser(jsonString));
      return SearchResponse.fromXContent(parser);
    } catch (Exception exception) {
      LOGGER.error("Error occured while parsing the search response", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  /**
   * Executes a Scroll Search request.
   *
   * @param searchScrollRequest to search.
   * @return search response.
   */
  public SearchResponse searchScroll(SearchScrollRequest searchScrollRequest) {
    try {
      return client.scroll(searchScrollRequest, RequestOptions.DEFAULT);
    } catch (Exception exception) {
      LOGGER.error("Search Scroll operation failed.", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  /**
   * Bulk indexing in a single call. It retries given no of times after exponentially increasing the wait time.
   *
   * @param indexRequestDto dto {@link BaseIndexBulkRequestDto} containing all the document to be indexed & retry info.
   * @return dto {@link BaseIndexBulkResponseDto} containing execution time and response for each index request.
   */
  public BaseIndexBulkResponseDto bulkIndex(BaseIndexBulkRequestDto indexRequestDto) {
    try {
      List<BaseIndexDto> indices = indexRequestDto.getIndices();
      BulkRequest bulkRequest = new BulkRequest();
      for (BaseIndexDto request : indices) {
        bulkRequest.add(new IndexRequest(index, request.getType(), request.getId()).source(request.getDocument()));
      }
      LOGGER.debug("Bulk index request formed:{}", bulkRequest);
      return performBulkOperation(bulkRequest, indexRequestDto);
    } catch (Exception exception) {
      LOGGER.error("Bulk index operation failed.", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  /**
   * Bulk update in a single call. It retries given no of times after exponentially increasing the wait time.
   *
   * @param indexRequestDto dto {@link BaseIndexBulkRequestDto} containing all the document to be indexed & retry info.
   * @return dto {@link BaseIndexBulkResponseDto} containing execution time and response for each index request.
   */
  public BaseIndexBulkResponseDto bulkUpdate(BaseIndexBulkRequestDto indexRequestDto) {
    try {
      List<BaseIndexDto> indices = indexRequestDto.getIndices();
      BulkRequest bulkRequest = new BulkRequest();
      for (BaseIndexDto request : indices) {
        bulkRequest.add(new IndexRequest(index, request.getType(), request.getId()).source(request.getDocument()));
      }
      LOGGER.debug("Bulk update request formed:{}", bulkRequest);
      return performBulkOperation(bulkRequest, indexRequestDto);
    } catch (Exception exception) {
      LOGGER.error("Bulk update operation failed.", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  /**
   * @param bulkRequest {@link BulkRequestBuilder} contain request for bulk operation.
   * @param indexRequestDto dto {@link BaseIndexBulkRequestDto} containing all the document to be indexed & retry info.
   * @return dto {@link BaseIndexBulkResponseDto} containing execution time and response for each index request.
   */
  private BaseIndexBulkResponseDto performBulkOperation(BulkRequest bulkRequest,
      BaseIndexBulkRequestDto indexRequestDto) throws Exception {
    bulkRequest.setRefreshPolicy(indexRequestDto.getRefreshPolicy());
    final BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
    BaseIndexBulkResponseDto responseDto = new BaseIndexBulkResponseDto();
    responseDto.setTimeTakenInMilli(response.getIngestTookInMillis());
    List<BaseIndexBulkResponseInfoDto> responseInfo = new ArrayList<>();
    for (BulkItemResponse itemResponse : response.getItems()) {
      BaseIndexBulkResponseInfoDto itemResponseInfo = new BaseIndexBulkResponseInfoDto();
      itemResponseInfo.setId(itemResponse.getId());
      itemResponseInfo.setPositionInRequestList(itemResponse.getItemId());
      itemResponseInfo.setFailed(itemResponse.isFailed());
      itemResponseInfo.setFailedMessage(itemResponse.getFailureMessage());
      responseInfo.add(itemResponseInfo);
    }
    responseDto.setResponseInfo(responseInfo);
    LOGGER.debug("Bulk response received:{}", response);
    return responseDto;
  }

  /**
   * Drop index from elastic search.
   */
  public void dropIndex() {
    try {
      Response response = lowLevelRestClient.performRequest(new Request("DELETE", "/" + index));
      if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
        LOGGER.info("Failed to delete the index :{}. Got the status code as:{}, With status:{}", index,
            response.getStatusLine().getStatusCode(), response.getStatusLine());
      } else {
        LOGGER.debug("Index deleted with response:{}", response);
      }
    } catch (Exception exception) {
      LOGGER.error("Could not delete index {}", index, exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  /**
   * Create index in elastic search.
   *
   * @param indexMapping index mappings.
   */
  public void createIndex(Map<String, Object> indexMapping) {
    try {
      String payload = XContentFactory.jsonBuilder().startObject().startObject("mappings").value(indexMapping)
          .endObject().endObject().toString();
      HttpEntity entity = new NStringEntity(payload, ContentType.APPLICATION_JSON);
      Request request = new Request(RequestMethod.PUT.name(), index);
      request.setEntity(entity);
      Response response = lowLevelRestClient.performRequest(request);
      if (HttpStatus.SC_OK != response.getStatusLine().getStatusCode()) {
        LOGGER.info("Failed to create the index :{}. Got the status code as:{}, With status:{}", index,
            response.getStatusLine().getStatusCode(), response.getStatusLine());
      } else {
        LOGGER.debug("Create index response is {}", response);
      }

    } catch (Exception exception) {
      LOGGER.error("Could not create the index {}", index, exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }

  }

  /**
   * This method will clear the provided scroll ids.
   *
   * @param scrollIds List of scroll ids to be cleared.
   */
  public void clearScrolls(List<String> scrollIds) {
    ClearScrollRequest request = new ClearScrollRequest();
    request.setScrollIds(scrollIds);

    ActionListener<ClearScrollResponse> listener = new ActionListener<ClearScrollResponse>() {
      @Override
      public void onResponse(ClearScrollResponse response) {
        LOGGER.debug("Clear scroll completed with status {}, and released {} contexts", response.isSucceeded(),
            response.getNumFreed());
      }

      @Override
      public void onFailure(Exception ex) {
        LOGGER.error("Clear search scrolls failed due to {}", ex.getMessage(), ex);
      }
    };
    client.clearScrollAsync(request, RequestOptions.DEFAULT, listener);
  }

  /**
   * Returns the response of multiple Get requests in a single call. If a document is not found, null is returned at its
   * position in the list.
   *
   * @param requests List of requests to be processed.
   * @return List of responses in the same order.
   */
  public List<Map<String, Object>> multiGet(List<BaseIndexDto> requests) throws JsonProcessingException {
    final String endpoint = ElasticsearchApiUtil.MULTI_GET_ENDPOINT;
    Request request = new Request(ElasticsearchApiUtil.GET_METHOD, endpoint);
    request.setEntity(new NStringEntity(multiGetQueryString(requests), ContentType.APPLICATION_JSON));
    try {
      final Response response = lowLevelRestClient.performRequest(request);
      final String responseString = EntityUtils.toString(response.getEntity());
      LOGGER.info("Retrieved the response from the search template.");
      return parseMultiGetResponse(responseString);
    } catch (JsonProcessingException exception) {
      LOGGER.error("Unable to form the multi get request", exception);
      throw exception;
    } catch (Exception exception) {
      LOGGER.error("Failed to execute multiGet API", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  private String multiGetQueryString(List<BaseIndexDto> requests) {
    List<Map<String, Object>> docs = new ArrayList<>();
    for (BaseIndexDto request : requests) {
      Map<String, Object> requestMap = new HashMap<>();
      requestMap.put(ElasticsearchApiUtil.DOC_INDEX, this.index);
      requestMap.put(ElasticsearchApiUtil.DOC_TYPE, request.getType());
      requestMap.put(ElasticsearchApiUtil.DOC_ID, request.getId());
      docs.add(requestMap);
    }
    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put(ElasticsearchApiUtil.DOCS, docs);
    return MAPPER.convertValue(requestBody, JsonNode.class).toString();
  }

  private List<Map<String, Object>> parseMultiGetResponse(String response) {
    List<Map<String, Object>> apiResponse = new ArrayList<>();
    try {
      final Map<String, Object> responseMap = MAPPER.readValue(response.getBytes(), Map.class);
      final List<Map<String, Object>> docs = (List<Map<String, Object>>) responseMap.get(ElasticsearchApiUtil.DOCS);
      for (Map<String, Object> document : docs) {
        apiResponse.add((Map<String, Object>) document.get(ElasticsearchApiUtil.DOC_SOURCE));
      }
      return apiResponse;
    } catch (Exception exception) {
      LOGGER.error("Failed to parse multiGet API response", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  /**
   * Executes multiple search requests in a single call.
   *
   * @param clauses List of clauses containing all requests.
   * @return List of Responses corresponding to the requests.
   */
  public List<SearchResponse> multiSearch(List<String> clauses) throws JsonProcessingException {
    final String endpoint = new StringBuilder(this.index).append(URL_PATH_SEPARATOR)
        .append(ElasticsearchApiUtil.MULTI_SEARCH_ENDPOINT).toString();
    Request request = new Request(ElasticsearchApiUtil.GET_METHOD, endpoint);
    request.setEntity(new NStringEntity(multiSearchQueryString(clauses), ContentType.APPLICATION_JSON));
    try {
      final Response response = lowLevelRestClient.performRequest(request);
      final String responseString = EntityUtils.toString(response.getEntity());
      LOGGER.info("Retrieved the response from the multiSearch API.");
      return parseMultiSearchResponse(responseString);
    } catch (JsonProcessingException exception) {
      LOGGER.error("Unable to parse the multi search request", exception);
      throw exception;
    } catch (Exception exception) {
      LOGGER.error("Failed to execute multiSearch API", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  private String multiSearchQueryString(List<String> clauses) {
    StringBuilder requestBody = new StringBuilder();
    for (String clause : clauses) {
      requestBody.append(System.lineSeparator()).append(clause).append(System.lineSeparator());
    }
    return requestBody.toString();
  }

  private List<SearchResponse> parseMultiSearchResponse(String response) {
    List<SearchResponse> searchResponseList = new ArrayList<>();
    try {
      final Map responseMap = MAPPER.readValue(response, new TypeReference<Map<String, List<JsonNode>>>() {
      });
      List<JsonNode> responseItems = (List<JsonNode>) responseMap.get(ElasticsearchApiUtil.RESPONSES);
      for (JsonNode node : responseItems) {
        final SearchResponse searchResponse = getSearchResponse(node.toString());
        searchResponseList.add(searchResponse);
      }
    } catch (IOException exception) {
      LOGGER.error("Exception occured while parsing the multi search API response", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
    return searchResponseList;
  }

  /**
   * This method does not support returning bulk and search failures, will only return the number of deleted docs.
   *
   * @param indexRequestDto bulk request for deletion.
   * @return bulk response showing only the number of deleted docs.
   */
  public BaseDeleteByQueryResponseDto deleteByQuery(BaseDeleteByQueryRequestDto indexRequestDto)
      throws JsonProcessingException {
    final List<String> types = indexRequestDto.getTypes();
    final StringBuilder endpointBuilder = new StringBuilder(this.index).append(URL_PATH_SEPARATOR);
    if (CollectionUtils.isNotEmpty(types)) {
      endpointBuilder.append(types.stream().collect(Collectors.joining(","))).append(URL_PATH_SEPARATOR);
    }
    endpointBuilder.append(ElasticsearchApiUtil.DELETE_BY_QUERY_ENDPOINT);
    Request request = new Request(ElasticsearchApiUtil.POST_METHOD, endpointBuilder.toString());
    request.setEntity(new NStringEntity(getDeleteByQueryString(indexRequestDto), ContentType.APPLICATION_JSON));
    try {
      final Response response = lowLevelRestClient.performRequest(request);
      final String responseString = EntityUtils.toString(response.getEntity());
      LOGGER.info("Delete by query response received");
      return parseDeleteByQueryResponse(responseString);
    } catch (JsonProcessingException exception) {
      throw exception;
    } catch (Exception exception) {
      LOGGER.error("Failed to execute delete by query API", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  /**
   * This method support returning bulk and search failures.
   *
   * @param indexRequestDto bulk request for deletion.
   * @return bulk response.
   */
  public BulkByScrollResponse bulkDeleteByQuery(BaseDeleteByQueryRequestDto indexRequestDto)
      throws JsonProcessingException {
    try {
      DeleteByQueryRequest request = new DeleteByQueryRequest(this.index);
      request.setQuery(indexRequestDto.getQuery());
      BulkByScrollResponse response = client.deleteByQuery(request, RequestOptions.DEFAULT);
      return response;
    } catch (JsonProcessingException exception) {
      throw exception;
    } catch (Exception exception) {
      LOGGER.error("Failed to execute delete by query API", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  private String getDeleteByQueryString(BaseDeleteByQueryRequestDto requestDto) throws JsonProcessingException {
    if (Objects.isNull(requestDto.getQuery())) {
      throw InternalErrorException.builder().internalError().details("Query cannot be null.").build();
    }
    try {
      Map<String, JsonNode> queryBody = new HashMap<>();
      final JsonNode node = MAPPER.readValue(requestDto.getQuery().toString(), JsonNode.class);
      queryBody.put(ElasticsearchApiUtil.QUERY, node);
      return MAPPER.convertValue(queryBody, JsonNode.class).toString();
    } catch (JsonProcessingException exception) {
      LOGGER.info("Unable to form the delete by query request", exception);
      throw exception;
    } catch (Exception exception) {
      LOGGER.error("Error occured while parsing the query object", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

  private Map<String, String> getQueryParamsForDeleteByQuery(BaseDeleteByQueryRequestDto requestDto) {
    final Boolean refresh = Optional.ofNullable(requestDto).map(BaseDeleteByQueryRequestDto::isRefresh).orElse(false);
    Map<String, String> queryParams = new HashMap<>();
    queryParams.put(ElasticsearchApiUtil.REFRESH, String.valueOf(refresh));
    return queryParams;
  }

  private BaseDeleteByQueryResponseDto parseDeleteByQueryResponse(String responseString) {
    try {
      BaseDeleteByQueryResponseDto deleteByQueryResponseDto = new BaseDeleteByQueryResponseDto();
      final JsonNode responseNode = MAPPER.readValue(responseString, JsonNode.class);
      deleteByQueryResponseDto.setDeleted(responseNode.get(ElasticsearchApiUtil.DELETED).asLong());
      return deleteByQueryResponseDto;
    } catch (Exception exception) {
      LOGGER.error("Exception occured while parsing the delete by query API response", exception);
      throw InternalErrorException.builder().internalError().cause(exception).build();
    }
  }

}
