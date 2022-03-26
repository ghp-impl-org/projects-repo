package com.predu.evertask.domain.mapper;

import com.predu.evertask.annotation.IncludeAfterMapping;
import com.predu.evertask.annotation.IncludeBeforeMapping;
import com.predu.evertask.domain.dto.issue.IssueDto;
import com.predu.evertask.domain.dto.issue.IssueUpdateDto;
import com.predu.evertask.domain.model.Issue;
import com.predu.evertask.repository.IssueRepository;
import com.predu.evertask.repository.UserRepository;
import com.predu.evertask.service.SprintService;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.stream.Collectors;

@Mapper(uses = UUIDMapper.class, componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class IssueMapper {

    @Autowired
    protected IssueRepository issueRepository;

    @Autowired
    private SprintService sprintService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UUIDMapper uuidMapper;

    @PersistenceContext
    EntityManager entityManager;

    @Mapping(target = "parentIssue", ignore = true)
    @Mapping(source = "assigneeId", target = "assignee.id")
    @Mapping(source = "reporterId", target = "reporter.id")
    @Mapping(target = "sprint", ignore = true)
    @Mapping(source = "projectId", target = "project.id")
    public abstract Issue issueDtoToIssue(IssueDto issueDto);

    @InheritInverseConfiguration(name = "issueDtoToIssue")
    public abstract IssueDto issueToIssueDto(Issue issue);

    @Mapping(target = "parentIssue", ignore = true)
    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "sprint", ignore = true)
    @Mapping(target = "project", ignore = true)
    @BeanMapping(qualifiedBy = {IncludeBeforeMapping.class})
    public abstract Issue issueUpdateDtoToIssue(IssueUpdateDto issueUpdateDto);

    @AfterMapping
    public void afterIssueSaveDtoToIssue(IssueDto issueDto, @MappingTarget Issue issue) {
        if (!issueDto.getSubtasks().isEmpty()) {
            issueDto.setSubtasks(
                    issue.getSubtasks()
                            .stream()
                            .map(this::issueToIssueDto)
                            .collect(Collectors.toSet())
            );
        }

        if (issueDto.getParentId() != null) {
            issue.setParentIssue(issueRepository.findById(uuidMapper.stringToUUID(issueDto.getParentId())).orElse(null));
        }

        if (issueDto.getSprintId() != null) {
            issue.setSprint(sprintService.findById(uuidMapper.stringToUUID(issueDto.getSprintId())).orElse(null));
        }
    }

    @IncludeBeforeMapping
    @BeforeMapping
    void beforeFlushIssue(@MappingTarget IssueDto issueDto, Issue issue) {
        entityManager.flush();
    }

    @IncludeAfterMapping
    @AfterMapping
    public void afterIssueUpdateDtoToIssue(IssueUpdateDto issueUpdateDto, @MappingTarget Issue issue) {
        if (issueUpdateDto.getParentId() != null) {
            issue.setParentIssue(issueRepository.findById(uuidMapper.stringToUUID(issueUpdateDto.getParentId())).orElse(null));
        }

        if (issueUpdateDto.getReporterId() != null) {
            issue.setReporter(userRepository.getById(uuidMapper.stringToUUID(issueUpdateDto.getReporterId())));
        }

        if (issueUpdateDto.getAssigneeId() != null) {
            issue.setAssignee(userRepository.getById(uuidMapper.stringToUUID(issueUpdateDto.getAssigneeId())));
        }
    }
}