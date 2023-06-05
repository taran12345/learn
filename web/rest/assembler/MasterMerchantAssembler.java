// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.assembler;

import com.paysafe.mastermerchant.service.dto.MasterMerchantResponseDto;
import com.paysafe.mastermerchant.util.LogUtil;
import com.paysafe.mastermerchant.util.MasterMerchantCommonUtil;
import com.paysafe.mastermerchant.web.rest.resource.MasterMerchantRecordCreateRequestResource;
import com.paysafe.mastermerchant.web.rest.resource.MasterMerchantResponseResource;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.mastermerchant.resources.MasterMerchantResource;
import com.paysafe.mpp.commons.util.AssemblerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an assembler class for MasterMerchantService.
 * 
 * @author meharchandra
 *
 */
@Component
public class MasterMerchantAssembler {

  private static final Logger logger = LoggerFactory.getLogger(MasterMerchantAssembler.class);

  /**
   * This method converts MasterMerchantDto to MasterMerchantResource.
   * 
   * @param masterMerchantDto masterMerchantDto
   * @return MasterMerchantResource
   */
  public MasterMerchantResource getMasterMerchantResource(MasterMerchantDto masterMerchantDto) {
    final MasterMerchantResource masterMerchantResource = new MasterMerchantResource();
    AssemblerUtils.copyProperties(masterMerchantResource, masterMerchantDto);
    logger.info("MasterMerchantResource created for merchant id : {}", masterMerchantDto.getId());
    return masterMerchantResource;
  }

  /**
   * This method converts MasterMerchantResource to MasterMerchantDto.
   * 
   * @param createRequestResource createRequestResource
   * @return MasterMerchantDto
   */
  public MasterMerchantDto getMasterMerchantDto(
      MasterMerchantRecordCreateRequestResource createRequestResource) {
    final MasterMerchantDto masterMerchantDto = new MasterMerchantDto();
    AssemblerUtils.copyProperties(masterMerchantDto, createRequestResource.getRecord());

    if (createRequestResource.getPmleId() != null) {
      masterMerchantDto.getPaymentAccounts().get(0).getProcessingAccounts().get(0)
          .setPmleId(createRequestResource.getPmleId());
    }

    String referenceId = LogUtil.sanitizeInput(createRequestResource.getRecord().getSourceAuthority().getReferenceId());
    logger.info("MasterMerchantDto created for merchant id : {}", referenceId);
    return masterMerchantDto;
  }

  /**
   * This method converts MasterMerchantResponseDto to MasterMerchantResponseResource.
   * 
   * @param masterMerchantResponseDto MasterMerchantResponseDto
   * @return MasterMerchantResponseResource
   */
  public MasterMerchantResponseResource
      getMasterMerchantResponseResource(MasterMerchantResponseDto masterMerchantResponseDto) {
    MasterMerchantResponseResource masterMerchantResponseResource = new MasterMerchantResponseResource();
    masterMerchantResponseResource.setMerchants(masterMerchantResponseDto.getMerchants());
    masterMerchantResponseResource.setLimit(masterMerchantResponseDto.getLimit());
    masterMerchantResponseResource.setOffset(masterMerchantResponseDto.getOffset());
    masterMerchantResponseResource.setTotalSearchMatches(masterMerchantResponseDto.getTotalSearchMatches());
    logger.info("MasterMerchantResponseResource created for {} merchants.",
        masterMerchantResponseDto.getTotalSearchMatches());
    return masterMerchantResponseResource;
  }

  /**
   * createMasterMerchantDataObject.
   *
   * @param masterMerchantDto - group data
   * @param masterPaymentAccountDto - payment data
   * @param processingAccountDto - processing data
   *
   */
  public void updateMasterMerchantDto(MasterMerchantDto masterMerchantDto,
      MasterPaymentAccountDto masterPaymentAccountDto, ProcessingAccountDto processingAccountDto) {

    List<ProcessingAccountDto> processingAccounts = new ArrayList<>();
    processingAccounts.add(MasterMerchantCommonUtil.copyWithUnicodeUnescaped(processingAccountDto));
    masterPaymentAccountDto.setProcessingAccounts(processingAccounts);

    List<MasterPaymentAccountDto> paymentAccounts = new ArrayList<>();
    paymentAccounts.add(masterPaymentAccountDto);
    masterMerchantDto.setPaymentAccounts(paymentAccounts);
  }

  /**
   * gets elasticSearch storeId from master merchant.
   * 
   * @param masterMerchant masterMerchant
   * @return storageId.
   */
  public static String getElasticStorageId(MasterMerchantDto masterMerchant) {
    StringBuilder storageId = new StringBuilder();
    storageId.append(masterMerchant.getId());

    String paymentAccountId = masterMerchant.getPaymentAccounts().get(0).getId();
    storageId.append("-").append(paymentAccountId);

    String processingAccountId = masterMerchant.getPaymentAccounts().get(0).getProcessingAccounts().get(0).getId();
    storageId.append("-").append(processingAccountId);

    return storageId.toString();
  }
}
