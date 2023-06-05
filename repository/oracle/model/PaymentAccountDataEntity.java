// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository.oracle.model;

import com.paysafe.mastermerchant.repository.oracle.converter.PaymentAccountDataConverter;
import com.paysafe.mpp.commons.dto.MasterPaymentAccountDto;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
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
@Table(name = "PAYMENT_ACCOUNTS_DATA")
public class PaymentAccountDataEntity {

  @Id
  @Column(name = "ID", nullable = false)
  private String id;

  @Column(name = "DATA", nullable = false)
  @Convert(converter = PaymentAccountDataConverter.class)
  private MasterPaymentAccountDto data;

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
