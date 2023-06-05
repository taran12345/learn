// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service.impl;

import com.paysafe.mastermerchant.service.EncryptionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import paysafe.ss.tokenization.hashing.HmacService;
import paysafe.ss.tokenization.hashing.TokenizationService;
import paysafe.ss.tokenization.hashing.exception.TokenizationException;

import java.util.Objects;

@Service
public class EncryptionServiceImpl implements EncryptionService {

  private static final String BANK_DOMAIN = "bank";

  private final TokenizationService tokenizationService;

  private final HmacService hmacService;

  @Autowired
  public EncryptionServiceImpl(TokenizationService tokenizationService, HmacService hmacService) {
    this.tokenizationService = tokenizationService;
    this.hmacService = hmacService;
  }

  /**
   * Encryption method.
   *
   * @param clearString plain string
   * @return String
   * @throws TokenizationException exception
   */
  public String tokenize(String clearString) throws TokenizationException {
    if (Objects.isNull(clearString)) {
      return null;
    }
    return tokenizationService.tokenize(clearString, BANK_DOMAIN);
  }

  /**
   * Decryption method.
   *
   * @param tokenizedString encrypted string
   * @return String
   * @throws TokenizationException exception
   */
  public String getOriginal(String tokenizedString) throws TokenizationException {
    if (Objects.isNull(tokenizedString)) {
      return null;
    }
    return tokenizationService.getOriginal(tokenizedString);
  }

  /**
   * Hash method.
   *
   * @param clearText plain string
   * @return String
   * @throws TokenizationException exception
   */
  public String getHash(String clearText) throws TokenizationException {
    if (Objects.isNull(clearText)) {
      return null;
    }
    return hmacService.hash(clearText, BANK_DOMAIN).getCurrent();
  }
}
