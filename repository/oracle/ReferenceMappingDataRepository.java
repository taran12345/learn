// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.oracle;

import com.paysafe.mastermerchant.repository.oracle.model.ReferenceMappingDataEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ReferenceMappingDataRepository extends CrudRepository<ReferenceMappingDataEntity, String> {

}
