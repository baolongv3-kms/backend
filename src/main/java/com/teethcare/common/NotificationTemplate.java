package com.teethcare.common;

import com.teethcare.model.request.NotificationMsgRequest;

public class NotificationTemplate {
    public static NotificationMsgRequest CHECK_IN_NOTIFICATION = new NotificationMsgRequest(null, "Check in success", "Check in success", "/", null, NotificationType.OPEN_BOOKING_NOTIFICATION.name());
}