package com.elearning.elearning_support.services.lecture.imp;

import com.elearning.elearning_support.dtos.answer.AnswerResDTO;
import com.elearning.elearning_support.dtos.answer.AnswerResInterface;
import com.elearning.elearning_support.dtos.lecture.LectureDto;
import com.elearning.elearning_support.dtos.lecture.LectureMaterialDto;
import com.elearning.elearning_support.dtos.lecture.LectureQuestionDto;
import com.elearning.elearning_support.dtos.onlineCourse.ChapterCourseDetailDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseLearningDetailDto;
import com.elearning.elearning_support.dtos.question.QuestionListDTO;
import com.elearning.elearning_support.dtos.question.QuestionListResDTO;
import com.elearning.elearning_support.dtos.testCourse.TestCourseDto;
import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.lecture.Lecture;
import com.elearning.elearning_support.entities.lecture.LectureMaterial;
import com.elearning.elearning_support.entities.lecture.LectureQuestion;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.enums.question.QuestionLevelEnum;
import com.elearning.elearning_support.mapper.lectureMaterial.LectureMapper;
import com.elearning.elearning_support.mapper.lectureMaterial.LectureMaterialMapper;
import com.elearning.elearning_support.repositories.answer.AnswerRepository;
import com.elearning.elearning_support.repositories.chapter.ChapterRepository;
import com.elearning.elearning_support.repositories.lecture.LectureMaterialRepository;
import com.elearning.elearning_support.repositories.lecture.LectureQuestionRepository;
import com.elearning.elearning_support.repositories.lecture.LectureRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseChapterRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseRepository;
import com.elearning.elearning_support.repositories.question.QuestionRepository;
import com.elearning.elearning_support.services.lecture.LectureService;
import com.elearning.elearning_support.services.question.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class LectureServiceImpl implements LectureService {
    @Autowired
    LectureRepository lectureRepository;
    @Autowired
    LectureMaterialRepository lectureMaterialRepository;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    OnlineCourseRepository onlineCourseRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    LectureQuestionRepository lectureQuestionRepository;
    @Autowired
    QuestionService questionService;
    @Autowired
    AnswerRepository answerRepository;
    static String tempVideoUrl="";
    @Override
    public void addAllInformationLecture(LectureDto lectureDto) {
        Integer maxSequence;
        LectureMapper lectureMapper=new LectureMapper();
        if(getMaxSequence(lectureDto.getChapterId(),lectureDto.getOnlineCourseId())!=null){
            maxSequence=getMaxSequence(lectureDto.getChapterId(),lectureDto.getOnlineCourseId());
        }else{
            maxSequence=0;
        }
        lectureDto.setSequence(maxSequence+1);
        Lecture lecture=lectureMapper.toEntity(lectureDto);
        // save chapter
        Chapter chapter = chapterRepository.findById(lectureDto.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));
        lecture.setChapter(chapter);
        // save OnlineCourse
        OnlineCourse onlineCourse=onlineCourseRepository.findById(lectureDto.getOnlineCourseId()).get();
        lecture.setOnlineCourse(onlineCourse);

        Lecture savedLecture= lectureRepository.save(lecture);
        if(!lectureDto.getLectureMaterialDtos().isEmpty()){
            for(LectureMaterialDto lectureMaterialDto:lectureDto.getLectureMaterialDtos()){
                LectureMaterial lectureMaterial=lectureMaterialRepository.findById(lectureMaterialDto.getId()).get();
                lectureMaterial.setLecture(savedLecture);
                lectureMaterialRepository.save(lectureMaterial);
            }
        }
        if(!lectureDto.getLectureQuestionDtos().isEmpty()){
            for(LectureQuestionDto lectureQuestionDto:lectureDto.getLectureQuestionDtos()){
                LectureQuestion lectureQuestion=new LectureQuestion();
                lectureQuestion.setTimeEnd(lectureQuestionDto.getTimeEnd());
                lectureQuestion.setTimeStart(lectureQuestionDto.getTimeStart());
                lectureQuestion.setLecture(savedLecture);
                lectureQuestion.setQuestion(questionRepository.findById(lectureQuestionDto.getQuestionId()).get());
                lectureQuestionRepository.save(lectureQuestion);
            }
        }



    }

    @Override
    public Integer getMaxSequence(long chapterId, long onlineCourseId) {
         return lectureRepository.findMaxSequenceByChapterOfCourse(chapterId,onlineCourseId);
    }

    @Override
    public List<LectureDto> getAllLectureByChapter(long chapterId, long onlineCourseId) {
        List<Lecture>lectureEntity=lectureRepository.getLecture(chapterId,onlineCourseId);
        List<LectureDto>lectureDtos=new ArrayList<>();
        for(Lecture lecture:lectureEntity){
            LectureMapper lectureMapper=new LectureMapper();
            LectureDto lectureDto=lectureMapper.toDto(lecture);
            lectureDtos.add(lectureDto);
        }
        Collections.sort(lectureDtos, new Comparator<LectureDto>() {
            @Override
            public int compare(LectureDto o1, LectureDto o2) {
                return Integer.compare(o1.getSequence(), o2.getSequence());
            }
        });
        return  lectureDtos;
    }

    @Override
    public QuestionListResDTO getLecture(long lectureId) {
        Lecture lecture=lectureRepository.findById(lectureId).get();
        LectureMapper lectureMapper=new LectureMapper();
        LectureDto lectureDto=lectureMapper.toDto(lecture);
        System.out.println("Chapter ID from Lecture: " + lectureDto.getChapterId());

        QuestionListResDTO questionListResDTO = questionService.getListQuestion(
                -1L,
                "",
                Set.of(lectureDto.getChapterId()),
                "",
                QuestionLevelEnum.ALL,
                "",
                -1L,
                50
        );
        List<LectureQuestion>lectureQuestions=lectureQuestionRepository.getLectureQuestionByLectureId(lectureId);
        ;
        List<LectureQuestionDto>lectureQuestionDtos=new ArrayList<>();
                for(LectureQuestion lectureQuestion:lectureQuestions){
                    LectureQuestionDto lectureQuestionDto=new LectureQuestionDto();
                    lectureQuestionDto.setLectureId(lectureId);
                    lectureQuestionDto.setQuestionId(lectureQuestion.getQuestion().getId());
                    lectureQuestionDto.setTimeEnd(lectureQuestion.getTimeEnd());
                    lectureQuestionDto.setTimeStart(lectureQuestion.getTimeStart());
                    lectureQuestionDtos.add(lectureQuestionDto);
                }
        Set<Long> lectureQuestionIds = lectureQuestions.stream()
                .map(lectureQuestion -> lectureQuestion.getQuestion().getId())
                .collect(Collectors.toSet());
        List<QuestionListDTO> matchingQuestions = questionListResDTO.getQuestions().stream()
                .filter(question -> lectureQuestionIds.contains(question.getId())) // Kiểm tra questionId có trong lectureQuestionIds
                .collect(Collectors.toList());

        QuestionListResDTO filteredResDTO = new QuestionListResDTO();
        filteredResDTO.setFetchedSize(matchingQuestions.size());
        filteredResDTO.setTotalSize(matchingQuestions.size());
        filteredResDTO.setQuestions(matchingQuestions);
        return filteredResDTO;

    }
    @Override
    public List<LectureDto> getAllLectureOfCourse(long chapterId, long onlineCourseId) {
        List<Lecture> lectureEntities = lectureRepository.getLecture(chapterId, onlineCourseId);
        List<LectureDto> lectureDtos = new ArrayList<>();
        for (Lecture lecture : lectureEntities) {
            LectureDto lectureDto = mapLectureToDto(lecture);

            lectureDtos.add(lectureDto);
        }
        Collections.sort(lectureDtos, new Comparator<LectureDto>() {
            @Override
            public int compare(LectureDto o1, LectureDto o2) {
                return Integer.compare(o1.getSequence(), o2.getSequence());
            }
        });
        return lectureDtos;
    }
    @Override
    public LectureDto getLectureDetail(long lectureId) {
        Lecture lecture=lectureRepository.findById(lectureId).get();
        LectureDto lectureDto=mapLectureToDto(lecture);
        return  lectureDto;
    }


    public LectureDto mapLectureToDto(Lecture lecture) {
        LectureMapper lectureMapper = new LectureMapper();
        LectureDto lectureDto = lectureMapper.toDto(lecture);
        // Thêm thông tin material
        lectureDto.setLectureMaterialDtos(getLectureMaterials(lectureDto.getId()));
        // Thêm thông tin question
        lectureDto.setLectureQuestionDtos(getLectureQuestions(lectureDto.getId()));
        lectureDto.setUrlVideo(tempVideoUrl);
        return lectureDto;
    }

    public List<LectureMaterialDto> getLectureMaterials(Long lectureId) {
        List<LectureMaterial> lectureMaterials = lectureMaterialRepository.findAllByLecture(lectureId);
        List<LectureMaterialDto> lectureMaterialDtos = new ArrayList<>();
        for (LectureMaterial lectureMaterial : lectureMaterials) {
            LectureMaterialDto lectureMaterialDto = LectureMaterialMapper.toDto(lectureMaterial);
            if(lectureMaterialDto.getStoredType()==0){
                tempVideoUrl=lectureMaterialDto.getExternalLink();
            }
            lectureMaterialDtos.add(lectureMaterialDto);
        }

        return lectureMaterialDtos;
    }


    public List<LectureQuestionDto> getLectureQuestions(Long lectureId) {
        List<Object[]> rows = lectureQuestionRepository.getDetailQuestionByLectureId(lectureId);
        List<LectureQuestionDto> lectureQuestionDtos = new ArrayList<>();
        for (Object[] row : rows) {
            LectureQuestionDto questionDto = new LectureQuestionDto();
            questionDto.setId(((BigInteger) row[0]).longValue());
            questionDto.setLectureId(((BigInteger) row[1]).longValue());
            questionDto.setTimeStart(((BigInteger) row[2]).intValue());
            questionDto.setTimeEnd(((BigInteger) row[3]).intValue());
            questionDto.setQuestionId(((BigInteger) row[4]).longValue());
            questionDto.setContent((String) row[5]);
            questionDto.setMultipleAns((Boolean)row[6]);
            List<AnswerResDTO> answers = getAnswersByQuestionId(questionDto.getQuestionId());
            questionDto.setAnswerResDTOList(answers);
            questionDto.setTimeShownUp((long) (Math.random()*(questionDto.getTimeEnd()-questionDto.getTimeStart())+questionDto.getTimeStart()));
            lectureQuestionDtos.add(questionDto);
        }
        return lectureQuestionDtos;
    }
    @Autowired
    OnlineCourseChapterRepository onlineCourseChapterRepository;
    public List<AnswerResDTO> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findAnswersByQuestionId(questionId)
                .stream()
                .map(projection -> new AnswerResDTO(projection.getId(), projection.getContent(), projection.getIsCorrect()))
                .collect(Collectors.toList());
    }



}
