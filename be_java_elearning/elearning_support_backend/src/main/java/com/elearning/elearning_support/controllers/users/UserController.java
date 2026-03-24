package com.elearning.elearning_support.controllers.users;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.elearning.elearning_support.components.BaseController;
import com.elearning.elearning_support.dtos.fileAttach.importFile.ImportResponseDTO;
import com.elearning.elearning_support.dtos.users.ChangePasswordReqDTO;
import com.elearning.elearning_support.dtos.users.IGetUserListDTO;
import com.elearning.elearning_support.dtos.users.ProfileUpdateDTO;
import com.elearning.elearning_support.dtos.users.ProfileUserDTO;
import com.elearning.elearning_support.dtos.users.UserCreateDTO;
import com.elearning.elearning_support.dtos.users.UserDetailDTO;
import com.elearning.elearning_support.dtos.users.UserSaveReqDTO;
import com.elearning.elearning_support.enums.users.UserTypeEnum;
import com.elearning.elearning_support.services.users.UserService;
import com.elearning.elearning_support.utils.file.FileUtils.Excel;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user")
@Tag(name = "APIs Người dùng (User)")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    /*
     *  ================== API dành cho user START =================
     */
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping
    @Operation(summary = "Tạo người dùng")
    public void createUser(@RequestBody @Validated UserCreateDTO createDTO) {
        userService.createUser(createDTO);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PutMapping("/{userId}")
    @Operation(summary = "Cập nhật thông tin người dùng")
    public void updateUser(
        @PathVariable(name = "userId") Long userId,
        @RequestBody @Validated UserSaveReqDTO updateDTO) {
        userService.updateUser(userId, updateDTO);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{userId}")
    @Operation(summary = "Chi tiết người dùng")
    public UserDetailDTO getUserDetail(@PathVariable(name = "userId") Long userId) {
        return userService.getUserDetail(userId);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @DeleteMapping("/hard/{id}")
    @Operation(summary = "Xóa cứng người dùng")
    public void deleteUserHard(
        @RequestParam(name = "userType") UserTypeEnum userType,
        @PathVariable(name = "id") Long id) {
        userService.deleteUserHard(userType, id);
    }

    @GetMapping("/profile")
    @Operation(summary = "Lấy thông tin user đăng nhập")
    public ProfileUserDTO getProfile() {
        return userService.getUserProfile();
    }

    @PutMapping("/profile")
    @Operation(summary = "Cập nhật thông tin người dùng")
    public void updateProfile(@RequestBody @Validated ProfileUpdateDTO updateDTO) {
        userService.updateProfile(updateDTO);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @GetMapping("/admin/page")
    @Operation(description = "Danh sách Quản trị viên hệ thống")
    public Page<IGetUserListDTO> getPageAdmin(
        @RequestParam(name = "search", required = false, defaultValue = "") String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
        @RequestParam(name = "sort", required = false, defaultValue = "lastModifiedAt") String sort
    ) {
        Pageable pageable = BaseController.getPageable(page, size, Collections.singletonList(sort));
        return userService.getPageAdmin(search, pageable);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PutMapping("/password/update")
    @Operation(description = "Đổi mật khẩu người dùng")
    void changePassword(@RequestBody @Validated ChangePasswordReqDTO reqDTO){
        userService.changePassword(reqDTO);
    }

    /*
     *  ================== API dành cho user END =======================
     */


    /*
     *  ================== API dành cho học sinh START =================
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @GetMapping("/student/page")
    @Operation(summary = "Danh sách học sinh / sinh viên dạng phân trang")
    public Page<IGetUserListDTO> getPageStudent(
        @RequestParam(name = "search", required = false, defaultValue = "") String search,
        @RequestParam(name = "courseNums", required = false, defaultValue = "-1") Set<Integer> courseNums,
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
        @RequestParam(name = "sort", required = false, defaultValue = "lastModifiedAt") String sort) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return userService.getPageStudent(search, courseNums, pageable);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/student/list")
    @Operation(summary = "Danh sách học sinh / sinh viên danh sách toàn bộ")
    public List<IGetUserListDTO> getListStudent(
        @RequestParam(name = "search", required = false, defaultValue = "") String search,
        @RequestParam(name = "courseNums", required = false, defaultValue = "-1") Set<Integer> courseNums
    ) {
        return userService.getListStudent(search, courseNums);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @PostMapping(value = "/student/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Import HSSV")
    public ImportResponseDTO importStudent(@RequestParam("file") MultipartFile fileImport) throws IOException {
        return userService.importStudent(fileImport);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @GetMapping(value = "/student/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Export danh sách HSSV")
    public ResponseEntity<InputStreamResource> exportStudent(
        @RequestParam(name = "search", required = false, defaultValue = "") String search,
        @RequestParam(name = "courseNums", required = false, defaultValue = "-1") Set<Integer> courseNums
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String fileName = String.format("StudentExport_%s_.xlsx", LocalDateTime.now());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType(String.join(";", Arrays.asList(Excel.CONTENT_TYPES))).toString());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return ResponseEntity.ok().headers(headers).body(userService.exportStudent(search, courseNums));
    }

    /*
     *  ================== API dành cho học sinh END =================
     */


    /*
     *  ================== API dành cho giáo viên / giảng viên START =================
     */
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @GetMapping("/teacher/page")
    @Operation(summary = "Danh sách giáo viên / giảng viên dạng phân trang")
    public Page<IGetUserListDTO> getPageTeacher(
        @RequestParam(name = "search", required = false, defaultValue = "") String search,
        @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
        @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
        @RequestParam(name = "sort", required = false, defaultValue = "lastModifiedAt") String sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort).descending());
        return userService.getPageTeacher(search, pageable);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/teacher/list")
    @Operation(summary = "Danh sách giáo viên / giảng viên danh sách toàn bộ")
    public List<IGetUserListDTO> getListTeacher(
        @RequestParam(name = "search", required = false, defaultValue = "") String search
    ) {
        return userService.getListTeacher(search);
    }

    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @PostMapping(value = "/teacher/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Import giáo viên / giảng viên")
    public ImportResponseDTO importTeacher(@RequestParam("file") MultipartFile fileImport) throws IOException {
        return userService.importTeacher(fileImport);
    }

    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'TEACHER')")
    @GetMapping(value = "/teacher/export", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @Operation(summary = "Export danh sách GV")
    public ResponseEntity<?> exportTeacher(
        @RequestParam(name = "search", required = false, defaultValue = "") String search
    ) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        String fileName = String.format("TeacherExport_%s_.xlsx", LocalDateTime.now());
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.parseMediaType(String.join(";", Arrays.asList(Excel.CONTENT_TYPES))).toString());
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        return ResponseEntity.ok().headers(headers).body(userService.exportTeacher(search));
    }

    /*
     *  ================== API dành cho giáo viên / giảng viên END =================
     */


}
