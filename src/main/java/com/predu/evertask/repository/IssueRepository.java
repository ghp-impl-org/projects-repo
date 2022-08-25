package com.predu.evertask.repository;

import com.predu.evertask.domain.model.Issue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IssueRepository extends BaseRepository<Issue, UUID>,
        JpaRepository<Issue, UUID> {

    List<Issue> findAllByAssigneeId(UUID assigneeId);

    List<Issue> findAllByProjectId(UUID projectId);

    List<Issue> findAllByProjectIdAndCreatedAtBetween(UUID projectId, OffsetDateTime startDate, OffsetDateTime endDate);

    @Query(value = "SELECT * FROM issues i " +
            "WHERE i.sprint_id = ?1 " +
            "AND i.type != 'SUBTASK' " +
            "ORDER BY i.key", nativeQuery = true)
    List<Issue> findAllBySprintIdAndIsNotSubtask(UUID sprintId);

    List<Issue> findAllBySprintId(UUID sprintId);

    Page<Issue> findAllByProjectIdAndSprintIsNullOrderByKeyDesc(UUID projectId, Pageable pageable);

    List<Issue> findTop10ByProjectIdOrderByCreatedAtDesc(UUID projectId);

    Optional<Issue> findTopByProjectIdOrderByUpdatedAtDesc(UUID projectId);

    Optional<Issue> findTopByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
