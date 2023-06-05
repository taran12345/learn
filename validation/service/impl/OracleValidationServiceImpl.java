// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.service.impl;

import com.paysafe.mastermerchant.validation.enums.OracleView;
import com.paysafe.mastermerchant.validation.repository.OracleValidationRepository;
import com.paysafe.mastermerchant.validation.repository.model.OracleResponse;
import com.paysafe.mastermerchant.validation.service.OracleValidationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

@Service
public class OracleValidationServiceImpl implements OracleValidationService {

  private static final Logger logger = LoggerFactory.getLogger(OracleValidationService.class);

  @Autowired
  private OracleValidationRepository oracleValidationRepository;

  @Override
  public List<OracleResponse> searchOracleView(OracleView oracleViewName) throws Exception {

    List<OracleResponse> oracleViewResultSet = null;

    try {
      Method oracleRepositoryMethod =
          OracleValidationRepository.class.getMethod(oracleViewName.getRepositoryMethodname());
      oracleViewResultSet = (List<OracleResponse>) oracleRepositoryMethod.invoke(oracleValidationRepository);
    } catch (Exception ex) {
      logger.info("MM validdation - Unable to get resultSet from View", ex);
      throw ex;
    } 

    return oracleViewResultSet;
  }

}
