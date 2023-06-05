// All Rights Reserved, Copyright © Paysafe Holdings UK Limited 2017. For more information see LICENSE

package com.paysafe.mastermerchant.repository;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Data
@Entity
@Table(name = "mvel_templates")
public class MvelTemplateEntity {

  @Id
  @Column(name = "template_name", nullable = false)
  private String templateName;

  @Column(name = "template_doc", nullable = false)
  private String templateDoc;

  @Column(name = "created_by", nullable = false)
  private String createdBy;

  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  @Column(name = "created_date", nullable = false)
  private DateTime createdDate;

  @Column(name = "last_modified_by", nullable = false)
  private String lastModifiedBy;

  @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
  @Column(name = "last_modified_date", nullable = false)
  private DateTime lastModifiedDate;
}
