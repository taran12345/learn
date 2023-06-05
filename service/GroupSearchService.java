// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mastermerchant.service.dto.GroupSearchRequestDto;
import com.paysafe.mastermerchant.service.dto.MasterMerchantResponseDto;
import com.paysafe.mpp.commons.dto.MasterMerchantDto;

import java.io.IOException;

/**
 * GroupSearchService.java.
 * 
 * @author kamarapuprabhath
 *
 * 
 */

public interface GroupSearchService {

  public void encryptAndSaveToElastic(MasterMerchantDto masterMerchant);

  public void saveGroup(MasterMerchantDto masterMerchant);

  public MasterMerchantResponseDto findSimilarGroupDto(GroupSearchRequestDto groupSearchDto) throws IOException;

  public MasterMerchantResponseDto smartSearch(String id, Integer offset, Integer limit) throws IOException;

}