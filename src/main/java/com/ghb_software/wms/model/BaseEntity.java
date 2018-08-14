package com.ghb_software.wms.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.persistence.Id;
import javax.persistence.Version;
import java.time.ZonedDateTime;
import java.util.Date;


/**
 * Created by alexandrugheboianu on 04.06.2017.
 */

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Version
  private int version;

  @CreatedDate
  private ZonedDateTime createdDate;

  @LastModifiedDate
  private ZonedDateTime lastUpdateDate;

  @CreatedBy
  private String createdBy;

  @LastModifiedBy
  private String updatedBy;
}
