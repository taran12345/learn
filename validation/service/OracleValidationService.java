// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.service;

import com.paysafe.mastermerchant.validation.enums.OracleView;
import com.paysafe.mastermerchant.validation.repository.model.OracleResponse;

import java.util.List;

public interface OracleValidationService {
  public List<OracleResponse> searchOracleView(OracleView viewName) throws Exception;
}
