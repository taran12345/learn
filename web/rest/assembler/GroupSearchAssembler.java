// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.web.rest.assembler;

import com.paysafe.mastermerchant.service.dto.GroupSearchDto;
import com.paysafe.mastermerchant.service.dto.GroupSearchRequestDto;
import com.paysafe.mastermerchant.service.dto.GroupSearchResponseDto;
import com.paysafe.mastermerchant.util.MasterMerchantCommonUtil;
import com.paysafe.mastermerchant.web.rest.resource.GroupSearchRequestResource;
import com.paysafe.mastermerchant.web.rest.resource.GroupSearchResource;
import com.paysafe.mastermerchant.web.rest.resource.GroupSearchResponseResource;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;
import com.paysafe.mpp.commons.dto.ProcessingAccountDto;
import com.paysafe.mpp.commons.util.AssemblerUtils;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * GroupSearchAssembler.java.
 *
 * @author kamarapuprabhath
 *
 *
 */

@Service
public class GroupSearchAssembler {

  /**
   * This method converts GroupSearchRequestResource to GroupSearchRequestDto.
   *
   * @param groupSearchRequestResource
   *          groupSearchRequestResource
   * @return GroupSearchRequestDto
   */
  public GroupSearchRequestDto
      getGroupSearchDto(GroupSearchRequestResource groupSearchRequestResource) {
    GroupSearchRequestDto groupSearchRequestDto = new GroupSearchRequestDto();
    groupSearchRequestDto = (GroupSearchRequestDto) MasterMerchantCommonUtil.deepCopy(
        groupSearchRequestResource, groupSearchRequestDto
    );
    formatGroupSearchResponseResource(groupSearchRequestResource, groupSearchRequestDto);

    return groupSearchRequestDto;
  }

  /**
   * This method converts GroupSearchResponseDto to GroupSearchResponseResource.
   *
   * @param groupSearchResponseDto
   *          groupSearchResponseDto
   * @return GroupSearchResponseResource
   */
  public GroupSearchResource
      getGroupSearchResource(GroupSearchDto groupSearchResponseDto) {
    final GroupSearchResource groupSearchResponseResource =
        new GroupSearchResource();
    AssemblerUtils.copyProperties(groupSearchResponseResource, groupSearchResponseDto);
    if (groupSearchResponseDto.getEmails() != null
        && !groupSearchResponseDto.getEmails().isEmpty()) {
      groupSearchResponseResource.setEmail(groupSearchResponseDto.getEmails().get(0));
    }
    return groupSearchResponseResource;
  }

  /**
   * getResponseresource.
   *
   * @param groupSearchResponseDto
   *          dto.
   * @return resource.
   */
  public GroupSearchResponseResource
      getGroupSearchResponseResource(GroupSearchResponseDto groupSearchResponseDto) {
    GroupSearchResponseResource groupSearchResponseResource = new GroupSearchResponseResource();
    groupSearchResponseResource.setMerchants(groupSearchResponseDto.getMerchants());
    groupSearchResponseResource.setLimit(groupSearchResponseDto.getLimit());
    groupSearchResponseResource.setOffset(groupSearchResponseDto.getOffset());
    groupSearchResponseResource.setTotalCount(groupSearchResponseDto.getTotalCount());
    return groupSearchResponseResource;
  }

  /**
   * createMasterMerchantDataObject.
   *
   * @param masterMerchantDto - group data
   * @param masterPaymentAccountDto - payment data
   * @param processingAccountDto - processing data
   *
   */
  public void createMasterMerchantDataObject(MasterMerchantDto masterMerchantDto,
      MasterPaymentAccountDto masterPaymentAccountDto, ProcessingAccountDto processingAccountDto) {

    List<ProcessingAccountDto> processingAccounts = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(masterPaymentAccountDto.getProcessingAccountIds())) {
      // To unescape the unicode characters
      processingAccounts.add(MasterMerchantCommonUtil.copyWithUnicodeUnescaped(processingAccountDto));
    }
    masterPaymentAccountDto.setProcessingAccounts(processingAccounts);

    List<MasterPaymentAccountDto> paymentAccounts = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(masterMerchantDto.getPaymentAccountIds())) {
      paymentAccounts.add(masterPaymentAccountDto);
    }
    masterMerchantDto.setPaymentAccounts(paymentAccounts);
  }

  private void formatGroupSearchResponseResource(GroupSearchRequestResource groupSearchRequestResource,
      GroupSearchRequestDto groupSearchRequestDto) {

    int offsetValue = groupSearchRequestDto.getOffset() == null ? 0 : groupSearchRequestDto.getOffset();
    int limitValue = groupSearchRequestDto.getLimit() == null ? 10 : groupSearchRequestDto.getLimit();
    groupSearchRequestDto.setOffset(offsetValue);
    groupSearchRequestDto.setLimit(limitValue);

    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());

    if (groupSearchRequestResource.getProcessingAccountCreationStartDate() != null) {
      groupSearchRequestDto.setCreatedOnStartDateTime(
          groupSearchRequestResource.getProcessingAccountCreationStartDate().format(dateFormat));
    }

    if (groupSearchRequestResource.getProcessingAccountCreationEndDate() != null) {
      groupSearchRequestDto.setCreatedOnEndDateTime(
          groupSearchRequestResource.getProcessingAccountCreationEndDate().format(dateFormat));
    }

  }
}
