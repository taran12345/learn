// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

import static com.paysafe.mastermerchant.util.DataConstants.GET_CANONICAL_HOST_NAME;
import static com.paysafe.mastermerchant.util.DataConstants.GET_HOST_ADDRESS;
import static com.paysafe.mastermerchant.util.DataConstants.GET_HOST_NAME;

import com.paysafe.mastermerchant.config.SpringSchedulerConfig;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

/**
 * Discovery server list client fetches currently available server nodes list dynamically from eureka.
 *
 * @author nareshpentkar
 */
@Component
@Slf4j
public class DiscoveryServiceutil {

  @Autowired
  private DiscoveryClient discoveryClient;

  @Autowired
  private SpringSchedulerConfig springSchedulerConfig;

  /**
   * This method will fetch and select the current host from all the server nodes available currently.
   *
   * @throws UnknownHostException unknownHostException
   */
  public boolean isCurrentServerNode(Integer order) throws UnknownHostException {
    if (springSchedulerConfig.isEnabled()) {
      String currentNodeIdentifier = getCurrentNodeIdentifier();
      List<String> allNodeIdentifiers = getAllNodeIdentifiers();
      log.info("Total nodes {}", allNodeIdentifiers);
      log.info("Current Node {}", currentNodeIdentifier);

      String selectedNode = selectNodeBasedOnOrder(allNodeIdentifiers, order);
      return currentNodeIdentifier.equals(selectedNode);
    }
    return false;
  }

  /**
   * Fetches the node depending on the order in roundRobin fashion.
   */
  private String selectNodeBasedOnOrder(List<String> hosts, Integer order) {
    return hosts.get(order % hosts.size());
  }

  /**
   * returns list of sorted hostIps .
   *
   * @throws UnknownHostException unknownHostException
   */
  private List<String> getAllNodeIdentifiers() throws UnknownHostException {
    List<ServiceInstance> serviceNodesList = discoveryClient.getInstances(springSchedulerConfig.getServiceId());

    log.info("Total service nodes - {}",serviceNodesList.size());

    List<String> serviceNodeNames =  serviceNodesList.stream()
        .map(ServiceInstance::getHost)
        .filter(StringUtils::isNotBlank)
        .map(host -> host.toLowerCase(Locale.US).trim()).collect(Collectors.toList());

    serviceNodeNames.add(getCurrentNodeIdentifier());
    return serviceNodeNames.stream().distinct().sorted().collect(Collectors.toList());
  }

  /**
   * returns current node identifier.
   *
   * @throws UnknownHostException unknownHostException
   */
  private String getCurrentNodeIdentifier() throws UnknownHostException {
    String currentNodeIp = InetAddress.getLocalHost().getHostAddress().toLowerCase(Locale.US).trim();
    String currentNodeName = InetAddress.getLocalHost().getHostName() .toLowerCase(Locale.US).trim();
    String canonicalNodeName = InetAddress.getLocalHost().getCanonicalHostName().toLowerCase(Locale.US).trim();
    log.info("Current Node Ip: {}", currentNodeIp);
    log.info("Current Node name: {}", currentNodeName);
    log.info("canonical Host Name: {}", canonicalNodeName);
    String nodeIdentifierMethodName = springSchedulerConfig.getNodeIdentifierMethodName();

    switch (nodeIdentifierMethodName) {
      case GET_HOST_ADDRESS:
        return currentNodeIp;
      case GET_HOST_NAME:
        return currentNodeName;
      case GET_CANONICAL_HOST_NAME:
        return canonicalNodeName;
      default:
        throw new IllegalStateException("Unexpected value: " + nodeIdentifierMethodName);
    }
  }
}

