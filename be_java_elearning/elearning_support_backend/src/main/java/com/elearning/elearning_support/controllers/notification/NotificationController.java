package com.elearning.elearning_support.controllers.notification;

import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.elearning.elearning_support.dtos.notification.FCMTokenRegisterDTO;
import com.elearning.elearning_support.dtos.notification.ICountNewNotificationDTO;
import com.elearning.elearning_support.dtos.notification.NotificationFCMReqDTO;
import com.elearning.elearning_support.dtos.notification.INotificationResDTO;
import com.elearning.elearning_support.services.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;


    @GetMapping()
    @Operation(description = "Lấy danh sách thông báo của người dùng đang đăng nhập")
    public List<INotificationResDTO> getListNotification() {
        return notificationService.getListNotification();
    }

    @PostMapping("/fcm/send")
    @Operation(description = "Bắn bản ghi thông qua FCM với một 1 FCM Token")
    public void sendFCMNotification(@RequestBody NotificationFCMReqDTO reqDTO) {
        notificationService.sendFCMNotification(reqDTO);
    }

    @PutMapping("/fcm/register-token")
    @Operation(description = "Đăng ký FCM Token cho user")
    public void registerNotificationToken(@RequestBody @Validated FCMTokenRegisterDTO registerDTO){
        notificationService.registerFCMToken(registerDTO);
    }

    @PutMapping("/new-status/update")
    @Operation(description = "Chuyển trạng thái isNew => false")
    public void updateIsNewNotification() {
        notificationService.updateIsNewNotification();
    }

    @GetMapping("/new-status/count")
    @Operation(description = "Đếm số thông báo mới")
    public ICountNewNotificationDTO countNewNotifications() {
        return notificationService.countNewNotifications();
    }

}
