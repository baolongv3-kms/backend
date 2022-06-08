package com.teethcare.controller;

import com.teethcare.common.Constant;
import com.teethcare.common.EndpointConstant;
import com.teethcare.common.Message;
import com.teethcare.model.entity.NotificationStore;
import com.teethcare.model.request.FCMTokenRequest;
import com.teethcare.service.FirebaseMessagingService;
import com.teethcare.service.NotificationStoreService;
import com.teethcare.utils.PaginationAndSortFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping(EndpointConstant.Notification.FCM_TOKEN_ENDPOINT)
public class FCMTokenController {

    private final FirebaseMessagingService firebaseMessagingService;

    @PostMapping
    public ResponseEntity<Message> addNewToken(@RequestBody FCMTokenRequest fcmTokenRequest,
                                               @RequestHeader(value = AUTHORIZATION) String authorHeader) {
        firebaseMessagingService.addNewToken(fcmTokenRequest.getFcmToken(), authorHeader.substring("Bearer ".length()));
        return new ResponseEntity<>(Message.SUCCESS_FUNCTION, HttpStatus.OK);
    }
}
