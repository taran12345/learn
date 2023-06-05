// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.oracle.model;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "REFERENCE_MAPPINGS")
public class ReferenceMappingDataEntity {

  @Id
  @Column(name = "id", nullable = false)
  private String id;

  @Column(name = "UUID", nullable = false, updatable = false)
  private String uuid;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Column(name = "CREATED_DATE", nullable = false, updatable = false)
  @CreationTimestamp
  private OffsetDateTime createdDate;

  @Column(name = "last_modified_by", nullable = false)
  private String lastModifiedBy;

  @Column(name = "LAST_MODIFIED_DATE", nullable = false)
  @UpdateTimestamp
  private OffsetDateTime lastModifiedDate;
}
