// com/olms/backend/service/DueNotificationJob.java
package com.olms.backend.service;

import com.olms.backend.entities.*;
import com.olms.backend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DueNotificationJob {

  private final LoanRepository loanRepo;
  private final UserRepository userRepo;
  private final NotificationRepository notifRepo;
  private final FineRepository fineRepo;
  private final EmailService emailService;

  private static final org.slf4j.Logger log =
    org.slf4j.LoggerFactory.getLogger(DueNotificationJob.class);

  // ======= PROPERTIES =======
  @Value("${app.notify.time-zone:Asia/Kolkata}")
  private String timeZone;

  @Value("${app.notify.due-soon-days:2}")
  private int dueSoonDays;

  @Value("${app.notify.due-soon-include-today:false}")
  private boolean dueSoonIncludeToday;

  @Value("${app.notify.dedup-hours:24}")
  private int dedupHours;

  @Value("${app.notify.overdue-reminder-days:1,3,7,14}")
  private String overdueDaysCsv;

//  @Value("${app.notify.dedup-hours:24}")
//  private int dedupHours;

  @Value("${app.block.overdue-after-days:30}")
  private int blockAfterDays;

  @Value("${app.block.min-unpaid-fine:0}")
  private double minUnpaidFine;

  private Set<Integer> reminderDays;

  @PostConstruct
  void init() {
    reminderDays = Arrays.stream(overdueDaysCsv.split(","))
      .map(String::trim).filter(s -> !s.isEmpty())
      .map(Integer::parseInt).collect(Collectors.toSet());
    if (reminderDays.isEmpty()) reminderDays = Set.of(1,3,7,14);
  }

  // Run at 09:00 in the configured zone
  @Scheduled(cron = "0 0 9 * * *", zone = "${app.notify.time-zone:Asia/Kolkata}")
  @Transactional
//  public void runDaily() {
//    ZoneId zone = ZoneId.of(timeZone);
//    LocalDate today = LocalDate.now(zone);
//
//    // ----- DUE SOON -----
//    LocalDate from = includeToday ? today : today.plusDays(1);
//    LocalDate to   = today.plusDays(dueSoonDays);
//    log.info("DueSoon window: [{} .. {}] (today={}, N={}, includeToday={})",
//      from, to, today, dueSoonDays, includeToday);
//
//    if (!to.isBefore(from)) {
//      var dueSoon = loanRepo.findByStatusAndDueDateBetween(LoanStatus.ISSUED, from, to);
//      for (var loan : dueSoon) {
//        var user = loan.getUser();
//        if (user == null || user.getStatus() == AccountStatus.DISABLED) continue;
//
//        boolean alreadySent = notifRepo.existsRecent(
//          user.getId(), loan.getLoanId(), NotificationType.DUE_SOON,
//          Instant.now().minus(dedupHours, ChronoUnit.HOURS)
//        );
//        if (alreadySent) continue;
//
//        String msg = "Reminder: Your book '" + safeTitle(loan)
//          + "' is due on " + loan.getDueDate() + ".";
//        emailService.sendPlain(user.getEmail(), "Book due soon", msg);
//        log.info("DUE_SOON -> userId={}, loanId={}, dueDate={}",
//          user.getId(), loan.getLoanId(), loan.getDueDate());
//        notifRepo.save(Notification.builder()
//          .userId(user.getId())
//          .loanId(loan.getLoanId())
//          .type(NotificationType.DUE_SOON)
//          .message(msg)
//          .build());
//      }
//    }
  public void runDaily() {
    final ZoneId zone = ZoneId.of(timeZone);
    final LocalDate today = LocalDate.now(zone);

    final LocalDate from = dueSoonIncludeToday ? today : today.plusDays(1);
    final LocalDate to = today.plusDays(dueSoonDays);

    // DEBUG: helps verify window in logs
    log.info("DueSoon window [{} .. {}] (today={}, tz={})", from, to, today, zone);

    var dueSoon = loanRepo.findByStatusAndDueDateBetween(LoanStatus.ISSUED, from, to);

    for (var loan : dueSoon) {
      var user = loan.getUser();
      if (user == null || user.getStatus() == AccountStatus.DISABLED) continue;

      boolean alreadySent = notifRepo.existsRecent(
        user.getId(), loan.getLoanId(), NotificationType.DUE_SOON,
        Instant.now().minus(dedupHours, ChronoUnit.HOURS)
      );
      if (alreadySent) continue;

      String msg = "Reminder: Your book '" + safeTitle(loan) +
        "' is due on " + loan.getDueDate() + ".";
      emailService.sendPlain(user.getEmail(), "Book due soon", msg);
      log.info("DUE_SOON -> userId={}, loanId={}, dueDate={}",
        user.getId(), loan.getLoanId(), loan.getDueDate());

      notifRepo.save(Notification.builder()
        .userId(user.getId())
        .loanId(loan.getLoanId())
        .type(NotificationType.DUE_SOON)
        .message(msg)
        .build());
    }


    // ----- OVERDUE + BLOCKING -----
    var overdue = loanRepo.findByStatusAndDueDateBefore(LoanStatus.ISSUED, today);
    for (var loan : overdue) {
      var user = loan.getUser();
      if (user == null) continue;

      int daysOver = (int) ChronoUnit.DAYS.between(loan.getDueDate(), today);

      // Reminders on specific days late
      if (reminderDays.contains(daysOver)) {
        boolean alreadySent = notifRepo.existsRecent(
          user.getId(), loan.getLoanId(), NotificationType.OVERDUE,
          Instant.now().minus(dedupHours, ChronoUnit.HOURS)
        );
        if (!alreadySent) {
          double estFine = estimateFine(loan, daysOver);
          String msg = "Overdue: '" + safeTitle(loan) + "' is " + daysOver
            + " day(s) late. Estimated fine: " + String.format(java.util.Locale.US, "%.2f", estFine);
          emailService.sendPlain(user.getEmail(), "Book overdue", msg);
          log.info("OVERDUE -> userId={}, loanId={}, daysOverdue={}",
            user.getId(), loan.getLoanId(), daysOver);
          notifRepo.save(Notification.builder()
            .userId(user.getId())
            .loanId(loan.getLoanId())
            .type(NotificationType.OVERDUE)
            .message(msg)
            .build());
        }
      }

      // Blocking policy
      if (daysOver >= blockAfterDays && user.getStatus() != AccountStatus.DISABLED) {
        if (minUnpaidFine > 0) {
          Double total = Optional.ofNullable(
            fineRepo.sumByUserAndStatus(user.getId(), FineStatus.UNPAID)
          ).orElse(0.0);
          if (total < minUnpaidFine) continue;
        }
        user.setStatus(AccountStatus.DISABLED);
        userRepo.save(user);

        String msg = "Account blocked: you are " + daysOver
          + " day(s) overdue on loan #" + loan.getLoanId()
          + ". Return books and clear fines to restore access.";
        emailService.sendPlain(user.getEmail(), "BiblioTrack account blocked", msg);
        log.info("ACCOUNT_BLOCKED -> userId={}, loanId={}, daysOverdue={}",
          user.getId(), loan.getLoanId(), daysOver);
        notifRepo.save(Notification.builder()
          .userId(user.getId())
          .loanId(loan.getLoanId())
          .type(NotificationType.ACCOUNT_BLOCKED)
          .message(msg)
          .build());
      }
    }
  }

  private String safeTitle(Loan loan) {
    try { return loan.getBook() != null ? loan.getBook().getTitle() : "your book"; }
    catch (Exception e) { return "your book"; }
  }

  private double estimateFine(Loan loan, int daysOverdue) {
    Double rate = null;
    try {
      rate = (loan.getBook() != null && loan.getBook().getCategory() != null)
        ? loan.getBook().getCategory().getFineRatePerDay() : null;
    } catch (Exception ignored) {}
    if (rate == null) rate = 0.0;
    return Math.max(daysOverdue, 0) * rate;
  }
}
