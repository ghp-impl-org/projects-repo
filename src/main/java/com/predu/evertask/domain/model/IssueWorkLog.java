package com.predu.evertask.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@Table(name = "issue_work_logs")
@Entity
@EntityListeners(AuditingEntityListener.class)
public class IssueWorkLog extends BaseEntity {

    @NotNull
    private Integer reportedHours;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "issue_id")
    private Issue issue;
}
