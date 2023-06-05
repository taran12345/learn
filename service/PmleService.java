// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mastermerchant.service.dto.PmleDto;

/**
 * This service is responsible for operations related to PMLE (Parent Merchant Legal Entity).
 *
 * @author narayananulaganathan
 */
public interface PmleService {

  /**
   * Fetches PMLE ID given its name.
   *
   * @param name Name of the PMLE
   * @return {@link PmleDto}, which holds the PMLE ID , name and netbanxReference.
   */
  PmleDto getPmleIdByName(String name);
  
  /**
   * Fetches PMLE  given its PMLE Id.
   *
   * @param id Id of the PMLE
   * @return {@link PmleDto}, which holds the PMLE ID , name and netbanxReference.
   */
  PmleDto getPmleById(Long id);
}
