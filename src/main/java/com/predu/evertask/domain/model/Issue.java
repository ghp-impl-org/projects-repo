package com.predu.evertask.domain.model;

import com.predu.evertask.domain.enums.IssuePriority;
import com.predu.evertask.domain.enums.IssueStatus;
import com.predu.evertask.domain.enums.IssueType;
import com.predu.evertask.util.EnumTypePgSql;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@TypeDef(name = "enum_pgsql", typeClass = EnumTypePgSql.class)

@Getter
@Setter
@Table(name = "issues")
@Entity
public class Issue extends BaseEntity {

    private int key;

    @Length(min = 6, max = 50)
    private String title;

    @Column(name = "hidden", nullable = false)
    private boolean hidden = false;

    @Column(name = "done", nullable = false)
    private boolean done = false;

    private int estimateStoryPoints;
    private int estimateHours;

    @Length(min = 10, max = 150)
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

    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "sprint_id")
    private Sprint sprint;

    @OneToMany(mappedBy = "parentIssue")
    private Set<Issue> subtasks = new HashSet<>();
}