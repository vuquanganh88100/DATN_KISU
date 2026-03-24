package com.elearning.elearning_support.controllers.chapter;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.elearning.elearning_support.dtos.chapter.ChapterSaveReqDTO;
import com.elearning.elearning_support.dtos.chapter.SubjectChapterCreateDTO;
import com.elearning.elearning_support.services.chapter.ChapterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chapter")
@Tag(name = "APIs Chương (Chapter)")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping
    @Operation(summary = "Tạo các chương của môn học")
    public void createChapter(@RequestBody @Validated SubjectChapterCreateDTO createDTO) {
        chapterService.createListChapter(createDTO);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PutMapping("/{chapterId}")
    @Operation(summary = "Cập nhật chương")
    public void updateChapter(
        @PathVariable(name = "chapterId") Long chapterId,
        @RequestBody ChapterSaveReqDTO updateDTO) {
        chapterService.updateChapter(chapterId, updateDTO);
    }
}
