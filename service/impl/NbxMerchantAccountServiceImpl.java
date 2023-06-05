// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.repository.NbxMerchantAccountRepository;
import com.paysafe.mastermerchant.repository.model.NbxMerchantAccountEntity;
import com.paysafe.mastermerchant.service.MerchantAccountService;
import com.paysafe.mastermerchant.service.dto.MerchantAccountDto;
import com.paysafe.mastermerchant.web.rest.assembler.NbxMerchantAccountAssembler;
import com.paysafe.op.errorhandling.exceptions.NotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Service impl for Netbanx merchant account.
 *
 * @author nareshpentkar
 */
@Service
public class NbxMerchantAccountServiceImpl implements MerchantAccountService {

  @Autowired
  private NbxMerchantAccountRepository nbxMerchantAccountRepository;

  @Autowired
  private NbxMerchantAccountAssembler nbxMerchantAccountAssembler;

  @Override
  public MerchantAccountDto findByAccountId(String accountId) {
    NbxMerchantAccountEntity netbanxMerchantAccountEntity = nbxMerchantAccountRepository.findByAccountId(accountId);
    if (Objects.isNull(netbanxMerchantAccountEntity)) {
      throw NotFoundException.builder().entityNotFound()
          .details(String.format("Invalid Account Id %s", accountId)).build();
    }
    return nbxMerchantAccountAssembler.entityToDto(netbanxMerchantAccountEntity);
  }

}
