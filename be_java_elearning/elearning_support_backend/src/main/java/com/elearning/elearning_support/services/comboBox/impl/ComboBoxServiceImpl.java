package com.elearning.elearning_support.services.comboBox.impl;

import java.math.BigInteger;
import java.util.*;

import com.elearning.elearning_support.dtos.chapter.ChapterBaseResDTO;
import com.elearning.elearning_support.dtos.combo.DepartmentAndSubjectDto;
import com.elearning.elearning_support.dtos.lecture.LectureDto;
import org.springframework.stereotype.Service;
import com.elearning.elearning_support.constants.RoleConstants;
import com.elearning.elearning_support.dtos.common.ICommonIdCode;
import com.elearning.elearning_support.dtos.common.ICommonIdCodeName;
import com.elearning.elearning_support.enums.system.SystemObjectEnum;
import com.elearning.elearning_support.enums.test.TestTypeEnum;
import com.elearning.elearning_support.enums.users.UserTypeEnum;
import com.elearning.elearning_support.repositories.comboBox.ComboBoxRepository;
import com.elearning.elearning_support.repositories.subject.SubjectRepository;
import com.elearning.elearning_support.services.comboBox.ComboBoxService;
import com.elearning.elearning_support.services.subject.SubjectService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class ComboBoxServiceImpl implements ComboBoxService {

    private final ComboBoxRepository comboBoxRepository;

    private final SubjectService subjectService;

    private final SubjectRepository subjectRepository;

    @Override
    public List<ICommonIdCodeName> getListSubject(String subjectName, String subjectCode, Set<Long> departmentIds) {
        Set<Long> viewableSubjectIds = subjectService.getListViewableSubjectIds();
        return comboBoxRepository.getListSubject(viewableSubjectIds, subjectName, subjectCode, departmentIds);
    }

    @Override
    public List<ICommonIdCodeName> getListViewableSubject(String subjectTitle, String subjectCode, SystemObjectEnum targetObject) {
        Set<Long> viewableSubjectIds = subjectService.getListViewableSubjectIds();
        if (Objects.equals(targetObject, SystemObjectEnum.EXAM_CLASS) && AuthUtils.isBaseTeacher()) {
            viewableSubjectIds.addAll(subjectRepository.getListViewableSubjectIdByExamClass(Boolean.FALSE, AuthUtils.getCurrentUserId()));
        }
        return comboBoxRepository.getListSubject(viewableSubjectIds, subjectTitle, subjectCode, Collections.singleton(-1L));
    }

    @Override
    public List<ICommonIdCodeName> getListChapterInSubject(Long subjectId, String chapterTitle, String chapterCode) {
        return comboBoxRepository.getListChapterInSubject(subjectId, chapterTitle, chapterCode);
    }

    @Override
    public List<ICommonIdCodeName> getListStudent(String studentName, String studentCode) {
        return comboBoxRepository.getListUserWithUserTypeAndRoleBase(studentName, studentCode, UserTypeEnum.STUDENT.getType(),
            RoleConstants.ROLE_STUDENT);
    }

    @Override
    public List<ICommonIdCodeName> getListTeacher(String teacherName, String teacherCode) {
        return comboBoxRepository.getListUserWithUserTypeAndRoleBase(teacherName, teacherCode, UserTypeEnum.TEACHER.getType(),
            RoleConstants.ROLE_TEACHER);
    }

    @Override
    public List<ICommonIdCodeName> getListRole(String search, UserTypeEnum userType) {
        return comboBoxRepository.getListRole(search, userType.getType());
    }

    @Override
    public List<ICommonIdCodeName> getListSemester(String search) {
        return comboBoxRepository.getListSemester(search);
    }

    @Override
    public List<ICommonIdCodeName> getListTest(String search) {
        return comboBoxRepository.getListTest(search);
    }

    @Override
    public List<ICommonIdCodeName> getListExamClass(Long semesterId, Long subjectId, TestTypeEnum testType, String search) {
        return comboBoxRepository.getListExamClass(AuthUtils.isSuperAdmin(), AuthUtils.getCurrentUserId(), semesterId, subjectId, testType.getType(), search);
    }

    @Override
    public List<ICommonIdCode> getListCourse(Long semesterId, Long subjectId, String search) {
        return comboBoxRepository.getListCourse(semesterId, subjectId, search);
    }

    @Override
    public List<ICommonIdCodeName> getListDepartment(String search) {
        // viewable departments
        Set<Long> viewableDepartmentIds;
        if (AuthUtils.isSuperAdmin()) {
            viewableDepartmentIds = Collections.singleton(-1L);
        } else {
            viewableDepartmentIds = AuthUtils.getCurrentUserDepartmentIds();
        }
        return comboBoxRepository.getListDepartment(search, viewableDepartmentIds);
    }

    /**
     * Lấy thông tin trường và môn học dành cho việc hiển thị online-course
     *
     * @param subjectId
     */
    @Override
    public DepartmentAndSubjectDto getDepartmentAndSubject(int subjectId) {
        List<Object[]> result = comboBoxRepository.getDepartmentAndSubjectDtoRaw(subjectId);
        if (result.isEmpty()) {
            return null;
        }

        Object[] row = result.get(0); // Chỉ lấy kết quả đầu tiên
        return new DepartmentAndSubjectDto(
                (String) row[0],
                (String) row[1]
        );
    }

    @Override
    public List<ChapterBaseResDTO> getChaptersByCourse(long onlineCourseId) {
        List<Object[]> rawResults = comboBoxRepository.getChapterByOnlineCourse(onlineCourseId);
        List<ChapterBaseResDTO> chapters = new ArrayList<>();

        for (Object[] row : rawResults) {
            ChapterBaseResDTO chapter = new ChapterBaseResDTO();
            chapter.setId(((BigInteger) row[0]).longValue());
            chapter.setCode((String) row[1]);
            chapter.setTitle((String) row[2]);
            chapter.setOrders((Integer) row[4]);
            chapters.add(chapter);
        }
        return chapters;
    }


}
