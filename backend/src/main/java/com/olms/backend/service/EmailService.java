//// com/olms/backend/service/EmailService.java
//package com.olms.backend.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.mail.SimpleMailMessage;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class EmailService {
//  private final JavaMailSender mailSender;
//
//  @Value("${app.mail.from:}")
//  private String from;
//
//  public void sendVerificationEmail(String to, String verifyLink, int expiryHours) {
//    SimpleMailMessage msg = new SimpleMailMessage();
//    if (from != null && !from.isBlank()) msg.setFrom(from);
//    msg.setTo(to);
//    msg.setSubject("Verify your OLMS account");
//    msg.setText(
//      "Hi,\n\nPlease verify your account by clicking the link below:\n"
//        + verifyLink + "\n\nThis link expires in " + expiryHours + " hour(s).\n\nThanks!"
//    );
//    mailSender.send(msg);
//  }
//}
// com/olms/backend/service/EmailService.java
package com.olms.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
  private final JavaMailSender mailSender;

  @Value("${app.mail.from:}")
  private String from;

  @Value("${app.frontend.base-url}")
  private String frontendBaseUrl;

  public void sendVerificationEmail(String to, String verifyLink, int expiryHours) {
    var msg = new SimpleMailMessage();
    if (!from.isBlank()) msg.setFrom(from);
    msg.setTo(to);
    msg.setSubject("Verify your BiblioTrack account");
    msg.setText("""
        Hi,

        Please verify your account by clicking the link below:
        %s

        This link expires in %d hour(s).

        Thanks!
        """.formatted(verifyLink, expiryHours));
    mailSender.send(msg);
  }

  // NEW: send confirmation after successful verification
  public void sendVerificationSuccessEmail(String to) {
    // String loginLink = frontendBaseUrl + "/auth/login";
    String loginLink = frontendBaseUrl + "/login";
    var msg = new SimpleMailMessage();
    if (!from.isBlank()) msg.setFrom(from);
    msg.setTo(to);
    msg.setSubject("Your BiblioTrack account is verified 🎉");
    msg.setText("""
        Hi,

        Your email has been successfully verified. You can now log in to BiblioTrack:
        %s

        Happy reading!
        """.formatted(loginLink));
    mailSender.send(msg);
  }

  public void sendPlain(String to, String subject, String body) {
    var msg = new org.springframework.mail.SimpleMailMessage();
    msg.setTo(to);
    msg.setSubject(subject);
    msg.setText(body);
    mailSender.send(msg);
  }
}
