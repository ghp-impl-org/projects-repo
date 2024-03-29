package com.predu.evertask.domain.model;

import com.predu.evertask.domain.enums.IssuePriority;
import com.predu.evertask.domain.enums.IssueStatus;
import com.predu.evertask.domain.enums.IssueType;
import com.predu.evertask.util.EnumTypePgSql;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@TypeDef(name = "enum_pgsql", typeClass = EnumTypePgSql.class)

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Table(name = "issues")
@Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
@AuditOverride(forClass = BaseEntity.class)
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Issue extends BaseEntity {

    @NotAudited
    private Integer key;

    private String title;
    private String description;

    @Column(name = "hidden", nullable = false)
    private boolean hidden = false;

    private Integer estimateStoryPoints;
    private Integer estimateHours;
    private String pullRequestUrl;

    @Enumerated(EnumType.STRING)
    @Type(type = "enum_pgsql")
    @NotNull
    private IssueStatus status;

    @Enumerated(EnumType.STRING)
    @Type(type = "enum_pgsql")
    @NotNull
    private IssueType type;

    @Enumerated(EnumType.STRING)
    @Type(type = "enum_pgsql")
    @NotNull
    private IssuePriority priority;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Issue parentIssue;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @ManyToOne
    @JoinColumn(name = "assignee_id")
    private User assignee;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @OneToMany(mappedBy = "issue")
    private List<IssueWorkLog> workLogs = new ArrayList<>();

    @OneToMany(mappedBy = "issue")
    private List<IssueComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "parentIssue")
    @OrderBy("key")
    private List<Issue> subtasks = new ArrayList<>();
}
