// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
/**
 * Repository dealing with operations of 'FPA_PMLE' table.
 *
 * @author narayananulaganathan
 */

@Repository
public interface PmleRepository extends JpaRepository<PmleEntity, Long> {

  /**
   * Finds a PMLE record from the table given a name.
   *
   * @param name Name of the desired PMLE
   * @return {@link PmleEntity}, holding the entire details of the PMLE
   */
  PmleEntity findByName(String name);
  
  /**
   * Finds a PMLE record from the table given a id.
   *
   * @param id Id of the desired PMLE
   * @return {@link PmleEntity}, holding the entire details of the PMLE
   */
  Optional<PmleEntity> findById(Long id);
}
