package com.tidy.tidy.domain.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    List<TaskStatus> findByPresentationId(Long presentationId);

    @Transactional
    @Modifying
    @Query("delete from TaskStatus t where t.presentationId = :presentationId")
    void deleteAllByPresentationId(Long presentationId);

}
