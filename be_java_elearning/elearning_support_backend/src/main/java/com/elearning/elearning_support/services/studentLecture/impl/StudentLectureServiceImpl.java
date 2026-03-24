package com.elearning.elearning_support.services.studentLecture.impl;

import com.elearning.elearning_support.dtos.lecture.LearningLectureStudentProgressDto;
import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.lecture.LectureProgressDto;
import com.elearning.elearning_support.dtos.lecture.LectureStudentProgressDto;
import com.elearning.elearning_support.dtos.onlineCourse.*;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.entities.lecture.Lecture;
import com.elearning.elearning_support.entities.lecture.LectureQuestion;
import com.elearning.elearning_support.entities.lecture.LectureStudentProgress;
import com.elearning.elearning_support.entities.onlineCourse.UserOnlineCourse;
import com.elearning.elearning_support.entities.question.Question;
import com.elearning.elearning_support.entities.testCourse.TestCourseEntity;
import com.elearning.elearning_support.mapper.lectureStudent.LectureStudentProgressMapper;
import com.elearning.elearning_support.repositories.chapter.ChapterRepository;
import com.elearning.elearning_support.repositories.lecture.LectureQuestionRepository;
import com.elearning.elearning_support.repositories.lecture.LectureRepository;
import com.elearning.elearning_support.repositories.lecture.LectureStudentProgressRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseChapterRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseRepository;
import com.elearning.elearning_support.repositories.onlineCourse.UserOnlineCourseRepository;
import com.elearning.elearning_support.repositories.resultTestCourse.ResultTestCourseRepository;
import com.elearning.elearning_support.repositories.testCourse.TestCourseRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.services.lecture.LectureService;
import com.elearning.elearning_support.services.studentLecture.StudentLectureService;
import com.elearning.elearning_support.services.studentOnlineCourse.StudentOnlineCourseService;
import com.elearning.elearning_support.services.testCourse.TestCourseService;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StudentLectureServiceImpl implements StudentLectureService {
    @Autowired
    OnlineCourseRepository onlineCourseRepository;
    @Autowired
    LectureService lectureService;
    @Autowired
    OnlineCourseChapterRepository onlineCourseChapterRepository;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    LectureStudentProgressRepository lectureStudentProgressRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    UserOnlineCourseRepository userOnlineCourseRepository;
    @Autowired
    TestCourseService testCourseService;
    @Autowired
    ResultTestCourseRepository resultTestCourseRepository;
    @Autowired
    LectureQuestionRepository lectureQuestionRepository;

//    @Override
//    public OnlineCourseDto getStudentCourseById(Long courseId) {
//        Pageable pageable= PageRequest.of(0,Integer.MAX_VALUE);
//        OnlineCourseDto onlineCourseDto=studentOnlineCourseService.getCourseWithoutEnroll(courseId,pageable,null);
//        long currentId=AuthUtils.getCurrentUserId();
//        Object results = lectureStudentProgressRepository.getLastTimeLearning(currentId, courseId);
//        if (results != null) {
//            Object[] row = (Object[]) results;
//            Timestamp lastUpdated = (Timestamp) row[2];
//            Timestamp lastSubmitTest = testCourseService.getLastTimeToDoTest(courseId, currentId);
//            Timestamp now = new Timestamp(System.currentTimeMillis());
//            long diffLastUpdated = Math.abs(now.getTime() - lastUpdated.getTime());
//            long diffLastSubmitTest = lastSubmitTest != null ? Math.abs(now.getTime() - lastSubmitTest.getTime()) : Long.MAX_VALUE;
//            Timestamp closerTime = (diffLastUpdated <= diffLastSubmitTest) ? lastUpdated : lastSubmitTest;
//            onlineCourseDto.setLastTimeToLearn(closerTime);
//        }
//        return  onlineCourseDto;
//    }

    @Override
    public Map<String, List<TestCourseDto>> getTestCourseByChapter(long courseId) {
        // dung hash map để lưu thứ tự insert
        Map<String, List<TestCourseDto>> chapterTestMap = new LinkedHashMap<>();
        List<Long>chapterIds=onlineCourseChapterRepository.findChapterIdsByOnlineCourseId(courseId);
        for(Long x:chapterIds){
            String chapterName=chapterRepository.findById(x).get().getTitle();
            String key="ID: "+String.valueOf(x)+"Chapter: "+chapterName;
            List<TestCourseDto>testCourseDtos=testCourseService.getAllTestOfChapterCourse(x,courseId);
            chapterTestMap.put(chapterName,testCourseDtos);
        }
        return chapterTestMap;
    }

    @Override
    public Map<String, List<LectureDto>> getLectureByChapter(long courseId) {
        // key là chapter ID và chapter name , value sẽ là lecture
        Map<String, List<LectureDto>> chapterLectureMap = new LinkedHashMap<>();
        List<Long>chapterIds=onlineCourseChapterRepository.findChapterIdsByOnlineCourseId(courseId);
        for(Long x:chapterIds){
            String chapterName=chapterRepository.findById(x).get().getTitle();
            String key="ID: "+String.valueOf(x)+"Chapter: "+chapterName;
            List<LectureDto>lectureDtos=lectureService.getAllLectureByChapter(x,courseId);
            chapterLectureMap.put(chapterName,lectureDtos);
        }
        return chapterLectureMap;
     }

//    @Override
//    public OnlineCourseDetailDto getCourseDetail(long courseId) {
//        int totalQuestions=0;
//        int totalDuration=0;
//        int totalLecture=0;
//        int totalChapter=0;
//        OnlineCourseDto courseInfo = getStudentCourseById(courseId);
//        Map<String, List<LectureDto>> chapterLectureMap = getLectureByChapter(courseId);
//        Map<String, List<TestCourseDto>> testCourseLectureMap = getTestCourseByChapter(courseId);
//
//        for(Map.Entry<String,List<LectureDto>>entry:chapterLectureMap.entrySet()){
//            List<LectureDto> lectures = entry.getValue();
//            totalLecture+=lectures.size();
//            for (LectureDto lecture : lectures) {
//                totalQuestions += lecture.getTotalQuestion();
//                totalDuration+=lecture.getVideoDuration();
//            }
//        }
//        totalChapter=chapterLectureMap.size();
//        OnlineCourseDetailDto onlineCourseDetailDto=new OnlineCourseDetailDto();
//        onlineCourseDetailDto.setCourseInfo(courseInfo);
//        onlineCourseDetailDto.setChapterLectureMap(chapterLectureMap);
//        onlineCourseDetailDto.setTestCourseLectureMap(testCourseLectureMap);
//        onlineCourseDetailDto.setTotalLecture(totalLecture);
//        onlineCourseDetailDto.setTotalQuestion(totalQuestions);
//        onlineCourseDetailDto.setTotalVideoDuration(totalDuration);
//        onlineCourseDetailDto.setTotalChapter(totalChapter);
//        return  onlineCourseDetailDto;
//    }
    @Override
    public OnlineCourseLearningDetailDto getLearningCourseDetail(long courseId) {
        // Lấy danh sách các chapter của khóa học
        List<Object[]> rawResults = onlineCourseChapterRepository.findChapterDetailByOnlineCourse(courseId);

        List<ChapterCourseDetailDto> chapterCourseDetailDtos = rawResults.stream()
                .map(row -> {
                    ChapterCourseDetailDto dto = new ChapterCourseDetailDto();
                    dto.setChapterId(((BigInteger) row[2]).longValue());
                    dto.setCourseId(((BigInteger) row[1]).longValue());
                    dto.setChapterWeight((Integer) row[3]);
                    dto.setChapterSequence((Integer) row[5]);
                    dto.setChapterName((String) row[4]);

                    // Lấy và set lecture cho chapter
                    List<LectureDto> lectureDtos = lectureService.getAllLectureOfCourse(dto.getChapterId(), dto.getCourseId());
                    dto.setLectureList(lectureDtos);
                    dto.setLectureCount(lectureDtos.size());

                    // Lấy và set test course cho chapter
                    List<TestCourseDto> testCourseDtos = testCourseService.getAllTestOfChapterCourse(dto.getChapterId(), dto.getCourseId());
                    dto.setTestCourseDtos(testCourseDtos);

                    return dto;
                })
                .sorted(Comparator.comparingInt(ChapterCourseDetailDto::getChapterSequence))
                .collect(Collectors.toList());

        // Tính tổng số lecture
        int totalLecture = chapterCourseDetailDtos.stream()
                .mapToInt(ChapterCourseDetailDto::getLectureCount)
                .sum();

        // Lấy các lecture đã hoàn thành
        List<Long> completedLectures = lectureStudentProgressRepository
                .findLectureIdCompleted(AuthUtils.getCurrentUserId(), courseId);

        // Lấy các testCourse đã hoàn thành
        List<Long> completedTestCourse = resultTestCourseRepository.findCompletedTestInCourse(
                AuthUtils.getCurrentUserId(), courseId
        );

        // Xác định current position
        CurrentLearningPositionDto currentPosition = determineCurrentPosition(
                chapterCourseDetailDtos,
                completedLectures,
                completedTestCourse
        );

        OnlineCourseLearningDetailDto onlineCourseLearningDetailDto = new OnlineCourseLearningDetailDto();
        onlineCourseLearningDetailDto.setChapterCourseDetailDtos(chapterCourseDetailDtos);
        onlineCourseLearningDetailDto.setTotalLecture(totalLecture);
        onlineCourseLearningDetailDto.setLectureCompleted(completedLectures);
        onlineCourseLearningDetailDto.setCurrentPosition(currentPosition);
        onlineCourseLearningDetailDto.setTestCourseCompleted(completedTestCourse);
        return onlineCourseLearningDetailDto;
    }

    public CurrentLearningPositionDto determineCurrentPosition(
            List<ChapterCourseDetailDto> chapters,
            List<Long> completedLectures,
            List<Long> completedTestCourse) {

        // Nếu chưa hoàn thành lecture nào
        if (completedLectures.isEmpty()) {
            // Lấy ra lecture đầu tiên
            ChapterCourseDetailDto firstChapter = chapters.get(0);
            LectureDto firstLecture = firstChapter.getLectureList().get(0);
            return new CurrentLearningPositionDto(
                    firstLecture.getId(),
                    "LECTURE",
                    firstLecture.getLectureName(),
                    firstChapter.getChapterId()
            );
        }

        // Tìm lecture cuối cùng đã hoàn thành
        Long lastCompletedLectureId = completedLectures.get(completedLectures.size() - 1);

        for (ChapterCourseDetailDto chapter : chapters) {
            List<LectureDto> lectures = chapter.getLectureList();
            List<TestCourseDto> tests = chapter.getTestCourseDtos();

            // Tìm vị trí lecture cuối cùng đã hoàn thành trong chapter
            for (int i = 0; i < lectures.size(); i++) {
                if (lectures.get(i).getId() == lastCompletedLectureId) {
                    // Nếu còn lecture tiếp theo trong chapter
                    if (i + 1 < lectures.size()) {
                        LectureDto nextLecture = lectures.get(i + 1);
                        return new CurrentLearningPositionDto(
                                nextLecture.getId(),
                                "LECTURE",
                                nextLecture.getLectureName(),
                                chapter.getChapterId()
                        );
                    }

                    // Đã hoàn thành tất cả lecture trong chapter
                    if (!tests.isEmpty()) {
                        // Tìm test course đầu tiên chưa hoàn thành
                        Optional<TestCourseDto> nextTest = tests.stream()
                                .filter(test -> !completedTestCourse.contains(test.getId()))
                                .findFirst();

                        if (nextTest.isPresent()) {
                            TestCourseDto test = nextTest.get();
                            return new CurrentLearningPositionDto(
                                    test.getId(),
                                    "TEST_COURSE",
                                    test.getName(),
                                    chapter.getChapterId()
                            );
                        }
                    }

                    // Không có test hoặc đã hoàn thành test, chuyển sang chapter tiếp theo
                    int nextChapterIndex = chapters.indexOf(chapter) + 1;
                    if (nextChapterIndex < chapters.size()) {
                        ChapterCourseDetailDto nextChapter = chapters.get(nextChapterIndex);
                        if (!nextChapter.getLectureList().isEmpty()) {
                            LectureDto nextLecture = nextChapter.getLectureList().get(0);
                            return new CurrentLearningPositionDto(
                                    nextLecture.getId(),
                                    "LECTURE",
                                    nextLecture.getLectureName(),
                                    nextChapter.getChapterId()
                            );
                        }
                    }
                }
            }
        }

        // Nếu không tìm thấy vị trí tiếp theo, trả về lecture đầu tiên
        ChapterCourseDetailDto firstChapter = chapters.get(0);
        LectureDto firstLecture = firstChapter.getLectureList().get(0);
        return new CurrentLearningPositionDto(
                firstLecture.getId(),
                "LECTURE",
                firstLecture.getLectureName(),
                firstChapter.getChapterId()
        );
    }


    public long findFirstLectureId(List<ChapterCourseDetailDto> chapterCourseDetailDtos) {
        return chapterCourseDetailDtos.stream()
                .filter(chapter -> !chapter.getLectureList().isEmpty())
                .findFirst()
                .map(chapter -> chapter.getLectureList().get(0).getId())
                .orElse(0L);
    }

    @Override
    public LearningLectureStudentProgressDto getLearningLectureDetail(long lectureId) {
        LearningLectureStudentProgressDto learningDto= new LearningLectureStudentProgressDto();
        LectureDto lectureDto  = lectureService.getLectureDetail(lectureId);
        long currendUserId= AuthUtils.getCurrentUserId();
        LectureStudentProgress lectureStudentProgress=lectureStudentProgressRepository.findByStudentIdAndLectureId(currendUserId,lectureId);

        if (lectureStudentProgress == null) {
            lectureStudentProgress = new LectureStudentProgress();
            lectureStudentProgress.setUser(userRepository.findById(currendUserId).get());
            lectureStudentProgress.setLecture(lectureRepository.findById(lectureId).get());
            lectureStudentProgress.setMaxWatchedTime(0L); // Khởi tạo tiến trình mặc định là 0
            lectureStudentProgress.setTotalAnswerCorrect(0L);
            lectureStudentProgress.setLastUpdated(new Date());
            lectureStudentProgressRepository.save(lectureStudentProgress);
        }else{
         LectureStudentProgressMapper mapper=new LectureStudentProgressMapper();
         LectureStudentProgressDto dto=mapper.toDto(lectureStudentProgress);
            learningDto.setLectureStudentProgressDto(dto);
        }
        learningDto.setLectureDto(lectureDto);
        return learningDto;
    }

    @Override
    public LectureStudentProgressDto saveTime(long time,long userId,long lectureId)
    {
        LectureStudentProgress lectureStudentProgress=lectureStudentProgressRepository.findByStudentIdAndLectureId(userId,lectureId);
        if(time>lectureStudentProgress.getMaxWatchedTime()){
            lectureStudentProgress.setMaxWatchedTime(time);
        }
        if(lectureStudentProgress.getMaxWatchedTime()>=getRequiredTime(lectureId)&&
                lectureStudentProgress.getTotalAnswerCorrect()>=getRequiredAnsCorrect(lectureId)){
            lectureStudentProgress.setCompleted(true);
        }
        lectureStudentProgress.setLastUpdated(new Date());
        lectureStudentProgressRepository.save(lectureStudentProgress);
        LectureStudentProgressMapper lectureStudentProgressMapper=new LectureStudentProgressMapper();
        return lectureStudentProgressMapper.toDto(lectureStudentProgress);
    }

    @Override
    public LectureStudentProgressDto saveTotalAnsCorrect(boolean studentAns, long userId, long lectureId) {
            LectureStudentProgress lectureStudentProgress=lectureStudentProgressRepository.findByStudentIdAndLectureId(userId,lectureId);
            long tempTotalAnsCor=lectureStudentProgress.getTotalAnswerCorrect();
            if(studentAns){
                lectureStudentProgress.setTotalAnswerCorrect(tempTotalAnsCor+1);
            }
        if(lectureStudentProgress.getMaxWatchedTime()>=getRequiredTime(lectureId)&&
                lectureStudentProgress.getTotalAnswerCorrect()>=getRequiredAnsCorrect(lectureId)){
            lectureStudentProgress.setCompleted(true);
        }
        lectureStudentProgress.setLastUpdated(new Date());
            lectureStudentProgressRepository.save(lectureStudentProgress);
        LectureStudentProgressMapper lectureStudentProgressMapper=new LectureStudentProgressMapper();
        return lectureStudentProgressMapper.toDto(lectureStudentProgress);
    }

    @Override
    public void resetProgress(long userId, long lectureId) {
        LectureStudentProgress lectureStudentProgress=lectureStudentProgressRepository.findByStudentIdAndLectureId(userId,lectureId);
        lectureStudentProgress.setMaxWatchedTime(0L);
        lectureStudentProgress.setTotalAnswerCorrect(0L);
        lectureStudentProgress.setLastUpdated(new Date());
        lectureStudentProgressRepository.save(lectureStudentProgress);
    }

    @Override
    public long getRequiredTime(long lectureId) {
        Lecture lecture=lectureRepository.findById(lectureId).get();
        return lecture.getRequiredTime();
    }

    @Override
    public long getRequiredAnsCorrect(long lectureId) {
        Lecture lecture=lectureRepository.findById(lectureId).get();
        return lecture.getRequiredCorrectAns();
    }
    @Override
    public LectureStudentProgressDto checkStatus(long userId, long lectureId) {
        LectureStudentProgress lectureStudentProgress=lectureStudentProgressRepository.findByStudentIdAndLectureId(userId,lectureId);
            LectureStudentProgressMapper lectureStudentProgressMapper = new LectureStudentProgressMapper();
            LectureStudentProgressDto dto=lectureStudentProgressMapper.toDto(lectureStudentProgress);
            return  dto;
    }

//    @Override
//    public List<OnlineCourseDetailDto> getAllCourseRegistedByStudent() {
//        List<UserOnlineCourse> userOnlineCourses=userOnlineCourseRepository.getCourseRegistedByUser(AuthUtils.getCurrentUserId());
//        List<OnlineCourseDetailDto> onlineCourseDetailDtos=new ArrayList<>();
//        for(UserOnlineCourse userOnlineCourse:userOnlineCourses){
//            OnlineCourseDetailDto onlineCourseDetailDto =new OnlineCourseDetailDto();
//            long onlineCourseId=userOnlineCourse.getOnlineCourse().getId();
//            onlineCourseDetailDto=getCourseDetail(onlineCourseId);
//            onlineCourseDetailDto.setTotalCompletionPercent(studentOnlineCourseService.getPercentCompleted(onlineCourseId));
//            onlineCourseDetailDtos.add(onlineCourseDetailDto);
//        }
//        return  onlineCourseDetailDtos;
//    }

    @Override
    public QuestionListResDTO exportLectureQuestion(long lectureId) {
       return  lectureService.getLecture(lectureId);
    }

    @Override
    public Map<Long, LectureProgressDto> getInfomationLectureProgress(long userId) {
        List<LectureStudentProgress> listLectureProgress=lectureStudentProgressRepository.findByUser_Id(userId);
        Map<Long,LectureProgressDto>infomationLectureProgress=new TreeMap<>();
        for(LectureStudentProgress lectureStudentProgress:listLectureProgress){
            LectureProgressDto lectureProgressDto=new LectureProgressDto();
            long lectureId=lectureStudentProgress.getLecture().getId();

            Lecture lecture=lectureRepository.findById(lectureId).get();
            lectureProgressDto.setLectureName(lecture.getLectureName());
            System.out.println("max watch time : "+lectureStudentProgress.getMaxWatchedTime());
            System.out.println("video durration : "+lecture.getVideoDuration());
            lectureProgressDto.setPercentWatchedTime(((double) lectureStudentProgress.getMaxWatchedTime() / lecture.getVideoDuration()) * 100);
            List<LectureQuestion>lectureQuestions= lectureQuestionRepository.getLectureQuestionByLectureId(lectureId);
            if(lectureQuestions!=null){
                lectureProgressDto.setPercentCorrectAns(((double) lectureStudentProgress.getTotalAnswerCorrect() / lectureQuestions.size()) * 100);
            }
            lectureProgressDto.setLectureId(lecture.getId());
            long sequence=lecture.getSequence();
            infomationLectureProgress.put(sequence,lectureProgressDto);
        }
        return infomationLectureProgress;
    }


}
