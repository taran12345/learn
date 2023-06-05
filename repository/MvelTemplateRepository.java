// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MvelTemplateRepository extends JpaRepository<MvelTemplateEntity, String> {

  String FIND_BY_ID_QUERY = "select n from MvelTemplateEntity n where n.templateName = :#{#templateName}";

  @Query(value = FIND_BY_ID_QUERY)
  Optional<MvelTemplateEntity> findByTemplateName(@Param("templateName") String templateName);

}
