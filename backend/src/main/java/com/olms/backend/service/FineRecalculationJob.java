package com.olms.backend.service;

import com.olms.backend.entities.Loan;
import com.olms.backend.entities.LoanStatus;
import com.olms.backend.repository.LoanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class FineRecalculationJob {

  private final LoanRepository loanRepo;

  @Value("${app.notify.time-zone:Asia/Kolkata}")
  private String timeZone;

  // Runs every minute. Safe to leave enabled in prod; it’s lightweight.
  @Scheduled(cron = "${app.fine.cron:0 * * * * *}", zone = "${app.notify.time-zone:Asia/Kolkata}")
  @Transactional
  public void recomputeFines() {
    ZoneId zone = ZoneId.of(timeZone);
    LocalDate today = LocalDate.now(zone);

    var loans = loanRepo.findAllByStatus(LoanStatus.ISSUED);
    for (Loan l : loans) {
      double newFine = computeFine(l, today);
      Double oldFine = l.getFineAmount() == null ? 0.0 : l.getFineAmount();
      if (Double.compare(newFine, oldFine) != 0) {
        l.setFineAmount(newFine);
        loanRepo.save(l);
      }
    }
  }

  private double computeFine(Loan l, LocalDate today) {
    if (l.getDueDate() == null) return 0.0;
    LocalDate end = (l.getReturnDate() != null) ? l.getReturnDate() : today;

    long daysOver = Math.max(ChronoUnit.DAYS.between(l.getDueDate(), end), 0);
    Double rate = 0.0;
    try {
      if (l.getBook() != null && l.getBook().getCategory() != null) {
        rate = l.getBook().getCategory().getFineRatePerDay();
        if (rate == null) rate = 0.0;
      }
    } catch (Exception ignored) {}

    return daysOver * rate;
  }
}
