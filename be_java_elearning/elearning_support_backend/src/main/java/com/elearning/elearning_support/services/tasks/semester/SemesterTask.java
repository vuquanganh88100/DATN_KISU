package com.elearning.elearning_support.services.tasks.semester;

import java.util.Arrays;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.elearning.elearning_support.annotations.log.ExecutionTimeLog;
import com.elearning.elearning_support.constants.CronJobConstants;
import com.elearning.elearning_support.constants.TimeConstants;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst;
import com.elearning.elearning_support.entities.cronJob.CronJobHistory;
import com.elearning.elearning_support.enums.crobJob.CronJobResultEnum;
import com.elearning.elearning_support.repositories.cronJob.CronJobHistoryRepository;
import com.elearning.elearning_support.services.semester.SemesterService;
import com.elearning.elearning_support.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class SemesterTask {

    private final CronJobHistoryRepository cronJobHistoryRepository;

    private final SemesterService semesterService;

    @Async
    @Scheduled(fixedRate = TimeConstants.MONTH) // scan every month
    @ExecutionTimeLog
    public void autoGenerateSemester() {
        long startInMillis = System.currentTimeMillis();
        CronJobHistory jobHistory = CronJobHistory.builder()
            .startedAt(DateUtils.getCurrentDateTime())
            .jobName("autoGenerateSemester")
            .build();
        try {
            semesterService.autoGenerateSemester();
            // success
            jobHistory.setResult(CronJobResultEnum.SUCCESS.getType());
            jobHistory.setMessage(CronJobConstants.SUCCESS_MESSAGE);
        } catch (Exception e) {
            log.error(MessageConst.EXCEPTION_LOG_FORMAT, e.getMessage(), e.getCause().toString());
            // fail
            jobHistory.setResult(CronJobResultEnum.FAIL.getType());
            jobHistory.setMessage(e.getMessage());
            jobHistory.setStackTrace(Arrays.toString(e.getStackTrace()));
        }
        jobHistory.setEndedAt(DateUtils.getCurrentDateTime());
        jobHistory.setExecutionTime(System.currentTimeMillis() - startInMillis);
        cronJobHistoryRepository.save(jobHistory);
    }

}
