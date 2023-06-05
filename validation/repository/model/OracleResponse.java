// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.validation.repository.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "nbx_merchant_account_counts_vw")
public class OracleResponse {

  @Id
  @Column(name = "NAME")
  private String name;

  @Column(name = "COUNT")
  private Integer count;

}
