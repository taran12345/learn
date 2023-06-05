// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.util;

/**
 * Order to select the node first or last to run the spring scheduler jobs on different nodes for netbanx vs irvine.
 *
 * @author nareshpentkar
 */
public enum SchedulerHostOrder {
  FIRST, LAST
}
