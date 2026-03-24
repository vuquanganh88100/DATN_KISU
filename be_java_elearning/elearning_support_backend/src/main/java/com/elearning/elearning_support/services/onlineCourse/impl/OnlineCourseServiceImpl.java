package com.elearning.elearning_support.services.onlineCourse.impl;
import com.elearning.elearning_support.dtos.combo.DepartmentAndSubjectDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseChapterDto;
import com.elearning.elearning_support.dtos.onlineCourse.OnlineCourseDto;
import com.elearning.elearning_support.entities.chapter.Chapter;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourse;
import com.elearning.elearning_support.entities.onlineCourse.OnlineCourseChapter;
import com.elearning.elearning_support.entities.onlineCourse.UserOnlineCourse;
import com.elearning.elearning_support.entities.users.User;
import com.elearning.elearning_support.mapper.OnlineCourseChapterMapper;
import com.elearning.elearning_support.mapper.OnlineCourseMapper;
import com.elearning.elearning_support.repositories.chapter.ChapterRepository;
import com.elearning.elearning_support.repositories.comboBox.ComboBoxRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseChapterRepository;
import com.elearning.elearning_support.repositories.onlineCourse.OnlineCourseRepository;
import com.elearning.elearning_support.repositories.onlineCourse.UserOnlineCourseRepository;
import com.elearning.elearning_support.repositories.users.UserRepository;
import com.elearning.elearning_support.services.comboBox.ComboBoxService;
import com.elearning.elearning_support.services.fileAttach.FileAttachService;
import com.elearning.elearning_support.services.onlineCourse.OnlineCourseService;
import com.elearning.elearning_support.specifications.onlineCourse.OnlineCourseSpecification;
import com.elearning.elearning_support.utils.auth.AuthUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OnlineCourseServiceImpl implements OnlineCourseService {
    @Autowired
    OnlineCourseMapper onlineCourseMapper;
    @Autowired
    private OnlineCourseRepository onlineCourseRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserOnlineCourseRepository userOnlineCourseRepository;
    @Autowired
    FileAttachService fileAttachService;
    @Autowired
    OnlineCourseChapterMapper onlineCourseChapterMapper;
    @Autowired
    OnlineCourseChapterRepository onlineCourseChapterRepository;
    @Autowired
    ChapterRepository chapterRepository;
    @Autowired
    ComboBoxService comboBoxService;


    @Override
    public void addOnlineCourse(OnlineCourseDto onlineCourseDto) {
        String imageUrl=fileAttachService.uploadImageCourse(onlineCourseDto.getFileCourseImg());
        OnlineCourse onlineCourse = new OnlineCourse();
        onlineCourseDto.setCourseUrlImg(imageUrl);
        onlineCourse=onlineCourseMapper.toEntity(onlineCourseDto);

        onlineCourseRepository.save(onlineCourse);

        List<Long> teacherIds=onlineCourseDto.getTeacherId();
        for(Long teacherId:teacherIds){
            UserOnlineCourse userOnlineCourse=new UserOnlineCourse();
            userOnlineCourse.setUser(userRepository.findById(teacherId).orElse(null));
            userOnlineCourse.setOnlineCourse(onlineCourse);
            userOnlineCourse.setRoleType("Teacher");
            userOnlineCourseRepository.save(userOnlineCourse);
        }

        List<OnlineCourseChapterDto>onlineCourseChapterDtos=onlineCourseDto.getOnlineCourseChaptersDto();
        for (OnlineCourseChapterDto chapterDto : onlineCourseChapterDtos) {
            chapterDto.setOnlineCourseId(onlineCourse.getId());
          configureChapterWeight(chapterDto);
        }

    }
    @Override
    public void updateOnlineCourse(Long onlineCourseId, OnlineCourseDto onlineCourseDto) {
        Optional<OnlineCourse> onlineCourseOptional = onlineCourseRepository.findById(onlineCourseId);
        if (onlineCourseOptional.isPresent()) {
            OnlineCourse onlineCourse = onlineCourseOptional.get();


            if (onlineCourse.getCourseUrlImg() != null) {
                String oldImageUrl = onlineCourse.getCourseUrlImg();
                String oldPublicId = fileAttachService.getPublicIdFromUrl(oldImageUrl);
                if (oldPublicId != null) {
                    fileAttachService.deleteImageFromCloudinary(oldPublicId);  // delete old img by publicId
                }
            }

            String imageUrl = fileAttachService.uploadImageCourse(onlineCourseDto.getFileCourseImg());

            onlineCourse = onlineCourseMapper.toEntity(onlineCourseDto);
            onlineCourse.setCourseUrlImg(imageUrl);

            onlineCourse.setId(onlineCourseId);
            onlineCourseRepository.save(onlineCourse);

            List<Long> teacherIds = onlineCourseDto.getTeacherId();
            userOnlineCourseRepository.deleteByOnlineCourseId(onlineCourseId);
            for (Long teacherId : teacherIds) {
                UserOnlineCourse userOnlineCourse = new UserOnlineCourse();
                userOnlineCourse.setUser(userRepository.findById(teacherId).orElse(null));
                userOnlineCourse.setOnlineCourse(onlineCourse);
                userOnlineCourse.setRoleType("Teacher");
                userOnlineCourseRepository.save(userOnlineCourse);
            }
            onlineCourseChapterRepository.deleteByOnlineCourseId(onlineCourseId);
            List<OnlineCourseChapterDto> onlineCourseChapterDtos = onlineCourseDto.getOnlineCourseChaptersDto();
            for (OnlineCourseChapterDto chapterDto : onlineCourseChapterDtos) {
                chapterDto.setOnlineCourseId(onlineCourse.getId());
                configureChapterWeight(chapterDto);
            }
        }
    }


    @Override
    public void configureChapterWeight(OnlineCourseChapterDto onlineCourseChapterDto) {
        OnlineCourse onlineCourse = onlineCourseRepository.findById(onlineCourseChapterDto.getOnlineCourseId())
                .orElseThrow(() -> new RuntimeException("OnlineCourse not found"));
        Chapter chapter = chapterRepository.findById(onlineCourseChapterDto.getChapterId())
                .orElseThrow(() -> new RuntimeException("Chapter not found"));

        OnlineCourseChapter onlineCourseChapter = onlineCourseChapterMapper.toEntity(onlineCourseChapterDto);
        try{
            onlineCourseChapterRepository.save(onlineCourseChapter);
        }catch (ConstraintViolationException cve){
            System.out.println(cve);
        }
    }

    @Override

    public Page<OnlineCourseDto> getListCourse(Pageable pageable) {
       int offset=pageable.getPageSize()* pageable.getPageNumber();
       List<Map<String,Object>> result=new ArrayList<>();
        if(AuthUtils.isSuperAdmin()||AuthUtils.isBaseStudent()){
            System.out.println("super admin");
            result = onlineCourseRepository.getAllPagedCourses(pageable.getPageSize(), offset);
        }else if (AuthUtils.isBaseTeacher()){
            result=onlineCourseRepository.getPagedCoursesByUserId(AuthUtils.getCurrentUserId(), pageable.getPageSize(), offset);
            System.out.println("role teacher");
        }
        long totalElement=onlineCourseRepository.count();
        List<OnlineCourseDto>onlineCourseDtos=new ArrayList<>();
        for (Map<String, Object> course : result) {

            OnlineCourseDto dto=new OnlineCourseDto();
            DepartmentAndSubjectDto departmentAndSubjectDto=new DepartmentAndSubjectDto();

            // cast BigInteger to int using intValue()
            BigInteger id=(BigInteger)course.get("id");
            dto.setPublish((boolean) course.get("is_publish"));
            dto.setCourseId(id.intValue());
            dto.setCourseName((String) course.get("course_name"));
            dto.setCourseDescription((String) course.get("course_description"));
            dto.setCourseUrlImg((String) course.get("course_urlimg"));
            BigInteger subejctId=(BigInteger) course.get("subject_id");
            dto.setSubjectId(subejctId.intValue());
            String tempTeacherIds=(String)course.get("teacherids");
            if (tempTeacherIds != null && !tempTeacherIds.isEmpty()) {
                if (tempTeacherIds.contains(",")) {
                    String[] teacherIds = tempTeacherIds.split(",");
                    List<Long> teacherIdsDto = new ArrayList<>();
                    for (String teacherId : teacherIds) {
                        teacherIdsDto.add(Long.valueOf(teacherId));
                    }
                    dto.setTeacherId(teacherIdsDto);
                } else {
                    List<Long> teacherIdsDto = new ArrayList<>();
                    teacherIdsDto.add(Long.valueOf(tempTeacherIds));
                    dto.setTeacherId(teacherIdsDto);
                }
            } else {
                dto.setTeacherId(Collections.emptyList());
            }

            System.out.println(course.get("onlineCourseChapters").getClass().getName());
            String listChapterJsonString = (String)course.get("onlineCourseChapters");
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String,Object>>listChapter=null;
            try {
                listChapter=mapper.readValue(listChapterJsonString,
                        mapper.getTypeFactory().constructCollectionType(List.class, Map.class)
                );
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            List<OnlineCourseChapterDto> chapters = new ArrayList<>();
            for (Map<String, Object> chapter : listChapter) {
                OnlineCourseChapterDto chapterDto = new OnlineCourseChapterDto();
                chapterDto.setChapterId(((Number) chapter.get("chapterId")).longValue());
                chapterDto.setChapterWeight(((Number) chapter.get("chapterWeight")).intValue());
                chapters.add(chapterDto);
            }
            dto.setOnlineCourseChaptersDto(chapters);
            departmentAndSubjectDto=comboBoxService.getDepartmentAndSubject(dto.getSubjectId());
            dto.setSubjectName(departmentAndSubjectDto.getSubjectName());
            dto.setDepartmentName(departmentAndSubjectDto.getDepartmentName());
            onlineCourseDtos.add(dto);
        }

        return new PageImpl<>(onlineCourseDtos, pageable, totalElement);
    }

    @Override
    public OnlineCourseDto getCourseById(Long courseId, Pageable pageable) {
        Page<OnlineCourseDto> pagedResult = getListCourse(pageable);
        OnlineCourseDto result = null;
        for (OnlineCourseDto course : pagedResult.getContent()) {
            if (course.getCourseId()==(courseId)) {
                result = course;
                break;
            }
        }
        return  result;
    }

    @Override
    public void updatePublish(long onlineCourseId, boolean isPublish) {
        OnlineCourse onlineCourse=onlineCourseRepository.findById(onlineCourseId).get();
        onlineCourse.setPublish(isPublish);
        onlineCourseRepository.save(onlineCourse);
    }





}

