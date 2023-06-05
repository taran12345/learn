// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * The entity class which denotes the 'FPA_PMLE' table.
 *
 * @author narayananulaganathan
 */
@Data
@Entity
@Table(name = "FPA_PMLE")
public class PmleEntity {

  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "created_by", updatable = false, nullable = false)
  @CreatedBy
  private String createdBy;

  @Column(name = "creation_date", updatable = false, nullable = false)
  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  @CreatedDate
  private DateTime creationDate;

  @Column(name = "modified_by", nullable = false)
  @LastModifiedBy
  private String modifiedBy;

  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  @Column(name = "modification_date")
  @LastModifiedDate
  private DateTime modificationDate;

  @Column(name = "partner_id")
  private Long partnerId;

  @Column(name = "netbanx_reference")
  private String netbanxReference;

  @Column(name = "max_profiles_per_card")
  private Integer maxProfilesPerCard;

  @Column(name = "max_profiles_per_cc_enable_ind")
  private String maxProfilesPerCcEnableInd;

  @Column(name = "max_cc_per_profile")
  private Integer maxCcPerProfile;

  @Column(name = "max_cc_per_profile_enable_ind")
  private String maxCcPerProfileEnableInd;

  @Column(name = "max_bnk_accnts_per_profile")
  private Integer maxBankAccountsPerProfile;

  @Column(name = "max_bnk_accnts_per_profile_ind")
  private String maxBankAccountsPerProfileInd;

  @Column(name = "max_profiles_per_bnk_accnt")
  private Integer maxProfilesPerBankAccount;

  @Column(name = "max_profiles_per_bnk_accnt_ind")
  private String maxProfilesPerBankAccountInd;

  @Column(name = "type_code")
  private String typeCode;

}
