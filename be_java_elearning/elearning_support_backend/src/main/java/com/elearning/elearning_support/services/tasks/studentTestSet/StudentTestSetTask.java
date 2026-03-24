package com.elearning.elearning_support.services.tasks.studentTestSet;

import java.util.Arrays;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.elearning.elearning_support.constants.CronJobConstants;
import com.elearning.elearning_support.constants.TimeConstants;
import com.elearning.elearning_support.constants.message.messageConst.MessageConst;
import com.elearning.elearning_support.entities.cronJob.CronJobHistory;
import com.elearning.elearning_support.enums.crobJob.CronJobResultEnum;
import com.elearning.elearning_support.repositories.cronJob.CronJobHistoryRepository;
import com.elearning.elearning_support.services.studentTestSet.StudentTestSetService;
import com.elearning.elearning_support.utils.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StudentTestSetTask {

    private final StudentTestSetService studentTestSetService;

    private final CronJobHistoryRepository cronJobHistoryRepository;


    @Async
    @Scheduled(fixedRate = 30 * TimeConstants.MINUTE) // scan every 30 minutes
    public void scanDueStudentTestSet() {
        log.info("===== STARTED scanDueStudentTestSet() in {} =====", this.getClass().getName());
        long startInMillis = System.currentTimeMillis();
        CronJobHistory jobHistory = CronJobHistory.builder()
            .startedAt(DateUtils.getCurrentDateTime())
            .jobName("scanDueStudentTestSet")
            .build();
        try {
            studentTestSetService.scanDueStudentTestSet();
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
        log.info("===== ENDED scanDueStudentTestSet() in {} AFTER {} ms =====", this.getClass().getName(), System.currentTimeMillis() - startInMillis);
    }

}
