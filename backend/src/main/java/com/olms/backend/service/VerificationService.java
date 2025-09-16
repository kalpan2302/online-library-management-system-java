// com/olms/backend/service/VerificationService.java
package com.olms.backend.service;

import com.olms.backend.entities.AccountStatus;
import com.olms.backend.entities.User;
import com.olms.backend.entities.VerificationToken;
import com.olms.backend.repository.UserRepository;
import com.olms.backend.repository.VerificationTokenRepository;
import com.olms.backend.security.SecurityUtil;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class VerificationService {

  private final VerificationTokenRepository vtRepo;
  private final UserRepository userRepo;
  private final EmailService emailService;

  @Value("${app.verification.expiry-hours:48}")
  private int expiryHours;

  @Value("${app.frontend.base-url}")
  private String frontendBaseUrl;

  @Async
  @Transactional
  public void issueTokenAndEmail(User user) {
    String plain = SecurityUtil.newVerificationTokenPlain();
    String hash  = SecurityUtil.sha256(plain);

    VerificationToken vt = VerificationToken.builder()
      .userId(user.getId())
      .tokenHash(hash)
      .expiresAt(Instant.now().plus(expiryHours, ChronoUnit.HOURS))
      .build();

    vtRepo.save(vt);

//    String verifyLink = frontendBaseUrl + "/verify?token=" +
//      URLEncoder.encode(plain, StandardCharsets.UTF_8);

          String verifyLink = org.springframework.web.util.UriComponentsBuilder
       .fromHttpUrl(frontendBaseUrl)
        .path("/verify")
       .queryParam("token", plain)
        .build(true)
        .toUriString();

    emailService.sendVerificationEmail(user.getEmail(), verifyLink, expiryHours);
  }

//  @Transactional
//  public void verifyByPlainToken(String tokenPlain) {
//    String hash = SecurityUtil.sha256(tokenPlain);
//    var vt = vtRepo.findValid(hash, Instant.now())
//      .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));
//
//    var user = userRepo.findById(vt.getUserId()).orElseThrow();
//    if (user.getStatus() != AccountStatus.ACTIVE) {
//      user.setStatus(AccountStatus.ACTIVE);
//      userRepo.save(user);
//    }
//    vt.setUsedAt(Instant.now());
//    vtRepo.save(vt);
//  }

  // com/olms/backend/service/VerificationService.java
  @Transactional
  public void verifyByPlainToken(String tokenPlain) {
    String hash = SecurityUtil.sha256(tokenPlain);
    var vt = vtRepo.findValid(hash, Instant.now())
      .orElseThrow(() -> new IllegalArgumentException("Invalid or expired token"));

    var user = userRepo.findById(vt.getUserId()).orElseThrow();

    boolean activatedNow = false;
    if (user.getStatus() != AccountStatus.ACTIVE) {
      user.setStatus(AccountStatus.ACTIVE);
      userRepo.save(user);
      activatedNow = true;
    }

    vt.setUsedAt(Instant.now());
    vtRepo.save(vt);

    // Fire-and-forget: don't fail verification if email send throws
    if (activatedNow) {
      try {
        emailService.sendVerificationSuccessEmail(user.getEmail());
      } catch (Exception ignored) { /* log if you want */ }
    }
  }


  @Async
  @Transactional
  public void resendIfAllowed(User user) {
    var last = vtRepo.findLatestActiveForUser(user.getId()); // Optional<VerificationToken>
    if (last.isPresent() &&
      last.get().getCreatedAt().isAfter(Instant.now().minus(2, ChronoUnit.MINUTES))) {
      return; // throttle
    }
    issueTokenAndEmail(user);
  }

}
