// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.PmleEntity;
import com.paysafe.mastermerchant.repository.PmleRepository;
import com.paysafe.mastermerchant.service.PmleService;
import com.paysafe.mastermerchant.service.assembler.PmleServiceAssembler;
import com.paysafe.mastermerchant.service.dto.PmleDto;
import com.paysafe.op.errorhandling.exceptions.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link PmleService} Includes operations related to PMLE.
 *
 * @author narayananulaganathan
 */
@Service
public class PmleServiceImpl implements PmleService {

  @Autowired
  private PmleRepository repository;

  @Autowired
  private PmleServiceAssembler assembler;

  /**
   * Gets PMLE ID given its name.
   *
   * @param name Name of the PMLE
   * @return {@link PmleDto}, Data Transfer Object containing the PMLE ID.
   */
  @Override
  public PmleDto getPmleIdByName(String name) {
    final PmleEntity pmleEntity = repository.findByName(name);
    if (pmleEntity == null) {
      throw NotFoundException.builder().entityNotFound().details("PMLE with name "
          + name + " is not found").build();
    }
    return assembler.dtoFromEntity(pmleEntity);
  }
  
  /**
   * Gets PMLE given its id.
   *
   * @param id Id of the PMLE
   * @return {@link PmleDto}, Data Transfer Object containing the PMLE ID.
   */
  @Override
  public PmleDto getPmleById(Long id) {
    final PmleEntity pmleEntity = repository.findById(id).orElseThrow(
        () -> NotFoundException.builder().entityNotFound().details("PMLE with id "
        + id + " is not found").build());

    return assembler.dtoFromEntity(pmleEntity);
  }
  
}
