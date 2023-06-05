// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import paysafe.ss.tokenization.hashing.exception.TokenizationException;

public interface EncryptionService {

  String tokenize(String clearString) throws TokenizationException;

  String getOriginal(String tokenizedString) throws TokenizationException;

  String getHash(String clearText) throws TokenizationException;
}
