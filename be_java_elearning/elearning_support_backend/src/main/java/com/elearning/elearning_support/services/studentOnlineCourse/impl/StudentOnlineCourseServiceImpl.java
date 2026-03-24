package com.elearning.elearning_support.services.studentOnlineCourse.impl;

import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseLearningDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.StudentOnlineCourse.ProgressStudentInCourseDto;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.entities.lecture.Lecture;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourseChapter;
import com.elearning.elearning_support.entities.onlineCourse.UserOnlineCourse;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.repositories.lecture.LectureRepository;
import com.elearning.elearning_support.repositories.lecture.LectureStudentProgressRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseChapterRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseRepository;
import com.elearning.elearning_support.repositories.onlineCourse.UserOnlineCourseRepository;
import com.elearning.elearning_support.repositories.testCourse.TestCourseRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.services.studentLecture.StudentLectureService;
import com.elearning.elearning_support.services.studentOnlineCourse.StudentOnlineCourseService;
import com.elearning.elearning_support.services.testCourse.TestCourseService;
import com.elearning.elearning_support.specifications.onlineCourse.OnlineCourseSpecification;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class StudentOnlineCourseServiceImpl implements StudentOnlineCourseService {
    @Autowired
    OnlineCourseRepository onlineCourseRepository;
    @Autowired
    UserOnlineCourseRepository userOnlineCourseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    OnlineCourseChapterRepository onlineCourseChapterRepository;
    @Autowired
    LectureStudentProgressRepository lectureStudentProgressRepository;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    TestCourseRepository testCourseRepository;
    @Autowired
    StudentLectureService studentLectureService;
    @Autowired
    TestCourseService testCourseService;
    @Override
    public Page<OnlineCourseDto> getListCourseCustom(Pageable pageable, String keyword) {
        Specification<OnlineCourse> spec = OnlineCourseSpecification.findOnlineCourse(keyword);
        Page<OnlineCourse> coursesPage = onlineCourseRepository.findAll(spec, pageable);
        List<OnlineCourseDto> onlineCourseDtos = coursesPage.getContent().stream().map(
                courseEntity -> {
                    OnlineCourseDto onlineCourseDto = new OnlineCourseDto();
                    onlineCourseDto.setCourseId(Math.toIntExact(courseEntity.getId()));
                    onlineCourseDto.setSubjectCode(courseEntity.getSubject().getCode());
                    onlineCourseDto.setCourseName(courseEntity.getCourseName());
                    onlineCourseDto.setCourseUrlImg(courseEntity.getCourseUrlImg());
                    onlineCourseDto.setCourseDescription(courseEntity.getCourseDescription());
                    List<UserOnlineCourse> userOnlineCourses = userOnlineCourseRepository.findUserOnlineCourseByOnlineCourseId(courseEntity.getId());
                    List<String> user = new ArrayList<>();
                    List<UserOnlineCourse>teacherInOnlineCourse=userOnlineCourses.stream().
                            filter(teacher -> teacher.getRoleType().equals("Teacher")).collect(Collectors.toList());
                    onlineCourseDto.setTotalStudentEnrolled(userOnlineCourses.size()-teacherInOnlineCourse.size());
                    for (UserOnlineCourse userOnlineCourse : teacherInOnlineCourse) {

                        String firstName = userRepository.findById(userOnlineCourse.getUser().getId()).get().getFirstName();
                        String lastName = userRepository.findById(userOnlineCourse.getUser().getId()).get().getLastName();
                        user.add(firstName + " " + lastName);
                    }
                    onlineCourseDto.setTeacherName(user);
                    onlineCourseDto.setStudentEnrolled(checkEnrolled(onlineCourseDto.getCourseId()));
                    return onlineCourseDto;
                }
        ).collect(Collectors.toList());
        return new PageImpl<>(onlineCourseDtos, pageable, coursesPage.getTotalElements());
    }

    @Override
    public OnlineCourseDto getCourseWithoutEnroll(long id, Pageable pageable, String keyword) {
        Page<OnlineCourseDto> pagedResult = getListCourseCustom(pageable, keyword);
        OnlineCourseDto result = null;
        for (OnlineCourseDto course : pagedResult.getContent()) {
            if (course.getCourseId() == (id)) {
                result = course;
                break;
            }
        }
        return result;

    }

    @Override
    public void enrollCourse(long courseId) {
        UserOnlineCourse userOnlineCourse=new UserOnlineCourse();
        userOnlineCourse.setOnlineCourse(onlineCourseRepository.findById(courseId).get());
        userOnlineCourse.setUser(userRepository.findById(AuthUtils.getCurrentUserId()).get());
        userOnlineCourse.setRoleType("Student");
        userOnlineCourseRepository.save(userOnlineCourse);
    }

    @Override
    public boolean checkEnrolled(long courseId) {
        long userId=AuthUtils.getCurrentUserId();
        UserOnlineCourse userOnlineCourse=userOnlineCourseRepository.checkExistStudent(userId,courseId);
        if(userOnlineCourse==null){
            return false;
        }else{
            return true;
        }
    }
    public OnlineCourseDto getStudentCourseById(Long courseId) {
        Pageable pageable= PageRequest.of(0,Integer.MAX_VALUE);
        OnlineCourseDto onlineCourseDto=getCourseWithoutEnroll(courseId,pageable,null);
        long currentId=AuthUtils.getCurrentUserId();
        Object results = lectureStudentProgressRepository.getLastTimeLearning(currentId, courseId);
        if (results != null) {
            Object[] row = (Object[]) results;
            Timestamp lastUpdated = (Timestamp) row[2];
            Timestamp lastSubmitTest = testCourseService.getLastTimeToDoTest(courseId, currentId);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            long diffLastUpdated = Math.abs(now.getTime() - lastUpdated.getTime());
            long diffLastSubmitTest = lastSubmitTest != null ? Math.abs(now.getTime() - lastSubmitTest.getTime()) : Long.MAX_VALUE;
            Timestamp closerTime = (diffLastUpdated <= diffLastSubmitTest) ? lastUpdated : lastSubmitTest;
            onlineCourseDto.setLastTimeToLearn(closerTime);
        }
        return  onlineCourseDto;
    }
    @Override
    public OnlineCourseDetailDto getCourseDetail(long courseId) {
        int totalQuestions=0;
        int totalDuration=0;
        int totalLecture=0;
        int totalChapter=0;
        OnlineCourseDto courseInfo = getStudentCourseById(courseId);
        Map<String, List<LectureDto>> chapterLectureMap = studentLectureService.getLectureByChapter(courseId);
        Map<String, List<TestCourseDto>> testCourseLectureMap = studentLectureService.getTestCourseByChapter(courseId);

        for(Map.Entry<String,List<LectureDto>>entry:chapterLectureMap.entrySet()){
            List<LectureDto> lectures = entry.getValue();
            totalLecture+=lectures.size();
            for (LectureDto lecture : lectures) {
                totalQuestions += lecture.getTotalQuestion();
                totalDuration+=lecture.getVideoDuration();
            }
        }
        totalChapter=chapterLectureMap.size();
        OnlineCourseDetailDto onlineCourseDetailDto=new OnlineCourseDetailDto();
        onlineCourseDetailDto.setCourseInfo(courseInfo);
        onlineCourseDetailDto.setChapterLectureMap(chapterLectureMap);
        onlineCourseDetailDto.setTestCourseLectureMap(testCourseLectureMap);
        onlineCourseDetailDto.setTotalLecture(totalLecture);
        onlineCourseDetailDto.setTotalQuestion(totalQuestions);
        onlineCourseDetailDto.setTotalVideoDuration(totalDuration);
        onlineCourseDetailDto.setTotalChapter(totalChapter);
        return  onlineCourseDetailDto;
    }
    @Override
    public List<OnlineCourseDetailDto> getAllCourseRegistedByStudent() {
        long userId=AuthUtils.getCurrentUserId();
        List<UserOnlineCourse> userOnlineCourses=userOnlineCourseRepository.getCourseRegistedByUser(AuthUtils.getCurrentUserId());
        List<OnlineCourseDetailDto> onlineCourseDetailDtos=new ArrayList<>();
        for(UserOnlineCourse userOnlineCourse:userOnlineCourses){
            OnlineCourseDetailDto onlineCourseDetailDto =new OnlineCourseDetailDto();
            long onlineCourseId=userOnlineCourse.getOnlineCourse().getId();
            onlineCourseDetailDto=getCourseDetail(onlineCourseId);
            onlineCourseDetailDto.setTotalCompletionPercent(getPercentCompleted(userId,onlineCourseId));
            onlineCourseDetailDtos.add(onlineCourseDetailDto);
        }
        return  onlineCourseDetailDtos;
    }
    @Override
    public double getPercentCompleted(long userId,long onlineCourseId) {
        List<OnlineCourseChapter>onlineCourseChapters=onlineCourseChapterRepository.findByOnlineCourseId(onlineCourseId);
        Map<Long,Integer> mapOnlineCourseChapter=new HashMap<>();
        // get chapterWeight
        for(OnlineCourseChapter occ:onlineCourseChapters){
            mapOnlineCourseChapter.put(occ.getChapter().getId(),occ.getChapterWeight());
        }
        List<Long>lectureIdCompletedInCourse=lectureStudentProgressRepository.findLectureIdCompleted(userId,onlineCourseId);
        List<Lecture> lectureCompletedInCourse=new ArrayList<>();
        for(Long lectureId:lectureIdCompletedInCourse){
            Lecture lecture=lectureRepository.findById(lectureId).get();
            lectureCompletedInCourse.add(lecture);
        }
        List<TestCourseEntity>testCourseCompletedInCourse=testCourseRepository.getTestCourseCompletedInCourse(userId,onlineCourseId);
        double totalCompleteionPercent=0;
        // sum percent lecture&test completed in chapter
        for(Map.Entry<Long,Integer>entry:mapOnlineCourseChapter.entrySet()){
            long chapterId=entry.getKey();
            long chapterWeight=entry.getValue();
            double totalLectureWeightInChapter = lectureCompletedInCourse.stream()
                    .filter(lecture -> lecture.getChapter().getId().equals(chapterId))
                    .mapToDouble(Lecture::getLectureWeight)
                    .sum();
            double totalTestCourseWeightInChapter = testCourseCompletedInCourse.stream()
                    .filter(test -> test.getChapter().getId().equals(chapterId))
                    .mapToDouble(TestCourseEntity::getTestCourseWeightl)
                    .sum();
            double chapterCompletionPercent = ((totalLectureWeightInChapter + totalTestCourseWeightInChapter) / 100.0) * chapterWeight;
            totalCompleteionPercent+=chapterCompletionPercent;
        }
        return totalCompleteionPercent;
    }


    @Override
    public List<ProgressStudentInCourseDto> getListProgressOfStudent( long onlineCourseId) {
        List<UserOnlineCourse> userOnlineCourses = userOnlineCourseRepository.findUserOnlineCourseByOnlineCourseId(onlineCourseId);
        List<ProgressStudentInCourseDto>progressStudentInCourseDtos =new ArrayList<>();
        for(UserOnlineCourse userOnlineCourse:userOnlineCourses){
            if(userOnlineCourse.getRoleType().equals("Student")){
                ProgressStudentInCourseDto progressStudentInCourseDto=new ProgressStudentInCourseDto();
                progressStudentInCourseDto.setStudentCode(userOnlineCourse.getUser().getCode());
                progressStudentInCourseDto.setUserId(userOnlineCourse.getUser().getId());
                progressStudentInCourseDto.setLastName(userOnlineCourse.getUser().getLastName());
                progressStudentInCourseDto.setFirstName(userOnlineCourse.getUser().getFirstName());
                progressStudentInCourseDto.setCompletedPercent(getPercentCompleted(userOnlineCourse.getUser().getId(),onlineCourseId));
                progressStudentInCourseDto.setOnlineCourseDto(getStudentCourseByIdNew(userOnlineCourse.getUser().getId(),onlineCourseId));
                progressStudentInCourseDtos.add(progressStudentInCourseDto);
            }
        }
        return progressStudentInCourseDtos;
    }
    // nao phair fix , thay userId can truyen
    public OnlineCourseDto getStudentCourseByIdNew(long currentUserId,Long courseId) {
        Pageable pageable= PageRequest.of(0,Integer.MAX_VALUE);
        OnlineCourseDto onlineCourseDto=getCourseWithoutEnroll(courseId,pageable,null);
        Object results = lectureStudentProgressRepository.getLastTimeLearning(currentUserId, courseId);
        if (results != null) {
            Object[] row = (Object[]) results;
            Timestamp lastUpdated = (Timestamp) row[2];
            Timestamp lastSubmitTest = testCourseService.getLastTimeToDoTest(courseId, currentUserId);
            Timestamp now = new Timestamp(System.currentTimeMillis());
            long diffLastUpdated = Math.abs(now.getTime() - lastUpdated.getTime());
            long diffLastSubmitTest = lastSubmitTest != null ? Math.abs(now.getTime() - lastSubmitTest.getTime()) : Long.MAX_VALUE;
            Timestamp closerTime = (diffLastUpdated <= diffLastSubmitTest) ? lastUpdated : lastSubmitTest;
            onlineCourseDto.setLastTimeToLearn(closerTime);
        }
        return  onlineCourseDto;
    }
}
