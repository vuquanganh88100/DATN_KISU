package com.elearning.elearning_support.dtos.examClass;

import java.util.Date;
import com.elearning.elearning_support.utils.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;

public interface IExamClassDetailDTO extends ICommonExamClassDTO {

    @JsonFormat(pattern = DateUtils.FORMAT_DATE_HH_MM_YYYY_HH_MM, timezone = DateUtils.TIME_ZONE)
    Date getLastModifiedAt();

    String getLstStudentId();

    String getLstSupervisorId();

    String getLstTestSetCode();

    String getLstTestSetId();

    Boolean getExistedResult();


}
