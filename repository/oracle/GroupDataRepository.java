// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.oracle;

import com.paysafe.mastermerchant.repository.oracle.model.GroupDataEntity;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface GroupDataRepository extends CrudRepository<GroupDataEntity, String> {

}
