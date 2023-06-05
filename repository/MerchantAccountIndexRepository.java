// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository;

import com.paysafe.op.commons.indexdb.repository.BaseIndexRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class MerchantAccountIndexRepository extends BaseIndexRepository {

  public MerchantAccountIndexRepository(@Value("${data-store.writeIndexName}") String writeIndex) {
    this.setIndex(writeIndex);
  }

}
