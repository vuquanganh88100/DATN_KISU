package com.elearning.elearning_support.repositories.cronJob;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.elearning.elearning_support.entities.cronJob.CronJobHistory;

@Repository
public interface CronJobHistoryRepository extends JpaRepository<CronJobHistory, Long> {


}
