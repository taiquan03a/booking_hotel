package com.hotel.booking.service;


import com.hotel.booking.dto.auth.EmailDetails;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.context.Context;

public interface EmailService {

    ResponseEntity<?> sendSimpleMail(EmailDetails details);
    String sendMailWithAttachment(EmailDetails details);
    void sendEmailWithHtmlTemplate(String to, String subject, String templateName, Context context);
}
