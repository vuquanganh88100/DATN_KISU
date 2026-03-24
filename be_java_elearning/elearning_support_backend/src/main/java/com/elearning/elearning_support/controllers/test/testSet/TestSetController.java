package com.elearning.elearning_support.controllers.test.testSet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.elearning.elearning_support.dtos.fileAttach.FileAttachDTO;
import com.elearning.elearning_support.dtos.test.studentTestSet.HandledImagesDeleteDTO;
import com.elearning.elearning_support.dtos.test.testSet.ITestSetPreviewDTO;
import com.elearning.elearning_support.dtos.test.testSet.ScoringPreviewResDTO;
import com.elearning.elearning_support.dtos.test.testSet.ScoringTestSetReqDTO;
import com.elearning.elearning_support.dtos.test.testSet.TestSetCreateDTO;
import com.elearning.elearning_support.dtos.test.testSet.TestSetDetailDTO;
import com.elearning.elearning_support.dtos.test.testSet.TestSetGenerateReqDTO;
import com.elearning.elearning_support.dtos.test.testSet.TestSetPreviewDTO;
import com.elearning.elearning_support.dtos.test.testSet.TestSetSearchReqDTO;
import com.elearning.elearning_support.dtos.test.testSet.TestSetUpdateDTO;
import com.elearning.elearning_support.services.test.TestSetService;
import com.elearning.elearning_support.utils.file.FileUtils.Word;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/test-set")
@Tag(name = "APIs Đề thi (TestSet)")
@RequiredArgsConstructor
public class TestSetController {

    private final TestSetService testSetService;

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping("/generate")
    @Operation(summary = "Gen đề thi trong kỳ thi")
    public List<TestSetPreviewDTO> genTestSetFromTest(@RequestBody TestSetGenerateReqDTO generateDTO) {
        return testSetService.generateTestSet(generateDTO);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping("/create")
    @Operation(summary = "Tạo một đề thi thủ công")
    public TestSetPreviewDTO createTestSet(@RequestBody @Validated TestSetCreateDTO createDTO){
        return testSetService.createTestSet(createDTO);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping(value = "/export", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Export đề thi ra file word")
    public ResponseEntity<InputStreamResource> exportTestSetToWord(@RequestBody TestSetSearchReqDTO searchReqDTO) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String fileName = String.format("TestSetExport_%s_%s.docx", searchReqDTO.getCode(), LocalDateTime.now());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType(String.join(";", Arrays.asList(Word.CONTENT_TYPES))).toString());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return ResponseEntity.ok().headers(headers).body(testSetService.exportTestSet(searchReqDTO));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER','STUDENT')")
    @PostMapping("/detail")
    @Operation(summary = "Lấy chi tiết đề thi")
    public TestSetDetailDTO getTestSetDetail(@RequestBody TestSetSearchReqDTO searchReqDTO) {
        return testSetService.getTestSetDetail(searchReqDTO);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER','STUDENT')")
    @GetMapping("/list")
    @Operation(summary = "Lấy danh sách các đề thi hiện có trong kỳ thi")
    public List<ITestSetPreviewDTO> getListTestSetPreview(@RequestParam(name = "testId") Long testId) {
        return testSetService.getListTestSetPreview(testId);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PutMapping
    @Operation(summary = "Cập nhật đề thi")
    public void updateTestSet(@RequestBody @Validated TestSetUpdateDTO updateDTO) {
        testSetService.updateTestSet(updateDTO);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @DeleteMapping("/{testSetId}")
    @Operation(summary = "Xóa một đề thi")
    public void deleteTestSet(@PathVariable(name = "testSetId") Long testSetId) {
        testSetService.deleteTestSetById(testSetId);
    }


    /*
    ==================================== TEST SET SCORING CONTROLLER =================================
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping("/scoring")
    @Operation(summary = "Chấm điểm bài thi chắc nghiệm (với dữ liệu có sẵn)")
    public ScoringPreviewResDTO scoringStudentTestSet(@RequestBody @Validated ScoringTestSetReqDTO scoringReqDTO) {
        return testSetService.scoreStudentTestSet(scoringReqDTO.getClassCode(), scoringReqDTO.getHandledTestSets());
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @GetMapping("/scoring/exam-class/{exClassCode}")
    @Operation(summary = "Lấy dữ liệu chấm điểm từ công cụ AI")
    public ResponseEntity<?> loadScoredStudentTestSet(@PathVariable(name = "exClassCode") String exClassCode){
        return ResponseEntity.ok(testSetService.scoreExamClassTestSet(exClassCode));
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping("/handled-answers/upload/{examClassCode}")
    @Operation(summary = "Upload bài làm theo lớp")
    public void uploadStudentHandledTestSet(
        @PathVariable(name = "examClassCode") String examClassCode,
        @RequestParam(name = "files") MultipartFile[] handledFiles) throws IOException {
        testSetService.uploadStudentHandledAnswerSheet(examClassCode, handledFiles);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping("/handled-answers/delete")
    @Operation(summary = "Xóa các file đã upload trong folder của mã lớp thi")
    public void deleteImagesInClassFolder(@RequestBody @Validated HandledImagesDeleteDTO deleteDTO) throws IOException {
        testSetService.deleteImagesInClassFolder(deleteDTO);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @GetMapping("/handled-answers/uploaded/{examClassCode}")
    @Operation(summary = "Preview các file đã tải lên")
    public List<FileAttachDTO> getListFileInExFolder(@PathVariable(name = "examClassCode") String examClassCode) {
        return testSetService.getListFileInExClassFolder(examClassCode);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping("/scoring/result/save")
    @Operation(description = "Lưu kết quả chấm điểm vào DB")
    public void saveScoringResult(@RequestParam(name = "tempFileCode") String tempFileCode,
        @RequestParam("option") String option) {
        testSetService.saveScoringResults(tempFileCode, option);
    }

    @GetMapping("/scored-data/exam-class/{exClassCode}")
    @Operation(description = "Load dữ liệu trả về của lần chấm gần nhất")
    public ScoringPreviewResDTO loadExamClassTempScoredData(@PathVariable(name = "exClassCode") String examClassCode) {
        return testSetService.loadExamClassTempScoredData(examClassCode);
    }
}
