// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.service;

import com.paysafe.mpp.commons.dto.SourceAuthority.Origin;
import com.paysafe.mpp.commons.enums.EntityType;
import com.paysafe.mpp.commons.enums.PaymentAccountType;

public interface ReferenceMappingDataService {

  String getOrCreateUuidForReferenceMapping(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId);

  String getUuidForReferenceMapping(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId);

  String createReferenceMappingInOracle(Origin origin, EntityType entityType, PaymentAccountType accountType,
      String referenceId, String uuid);
}
