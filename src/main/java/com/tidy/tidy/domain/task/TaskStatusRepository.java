package com.tidy.tidy.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    List<TaskStatus> findByPresentationId(Long presentationId);

}
