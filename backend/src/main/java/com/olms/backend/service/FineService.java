package com.olms.backend.service;

import com.olms.backend.entities.*;
import com.olms.backend.exception.ResourceNotFoundException;
import com.olms.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class FineService {

  @Autowired
  private FineRepository fineRepository;

  @Autowired
  private FinePaymentRepository finePaymentRepository;

  @Autowired
  private LoanRepository loanRepository;

  @Autowired
  private UserRepository userRepository;

  /**
   * Create or update a fine for a loan. Called when a loan is RETURNED.
   */
  @Transactional
  public Fine createOrUpdateFineForLoan(Loan loan) {
    if (loan == null) throw new IllegalArgumentException("Loan is null");
    // If no fine amount > 0, we may clear existing unpaid fine
    Double loanFine = loan.getFineAmount() == null ? 0.0 : loan.getFineAmount();

    Optional<Fine> opt = fineRepository.findByLoan(loan);
    if (loanFine <= 0.0) {
      // if existing fine exists and is unpaid, mark waived or zero
      if (opt.isPresent()) {
        Fine existing = opt.get();
        existing.setAmount(0.0);
        existing.setStatus(FineStatus.WAIVED);
        existing.setNote("Auto-cleared: no fine on return");
        return fineRepository.save(existing);
      } else {
        // no fine to create
        return null;
      }
    } else {
      Fine f;
      if (opt.isPresent()) {
        f = opt.get();
        f.setAmount(loanFine);
        f.setStatus(FineStatus.UNPAID);
        f.setNote("Auto-created/updated on return");
      } else {
        f = new Fine();
        f.setLoan(loan);
        f.setUser(loan.getUser());
        f.setAmount(loanFine);
        f.setStatus(FineStatus.UNPAID);
        f.setNote("Created on return");
      }
      return fineRepository.save(f);
    }
  }

  public List<Fine> getFinesForUser(Long userId) {
    User u = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
    return fineRepository.findByUser(u);
  }

  public List<Fine> getAllFines() {
    return fineRepository.findAll();
  }

//  @Transactional
//  public FinePayment payFine(Long fineId, Long paidByUserId, Double amountPaid, String paymentMethod, String transactionRef, String note) {
//    if (amountPaid == null || amountPaid <= 0) {
//      throw new IllegalArgumentException("Invalid payment amount");
//    }
//
//    Fine fine = fineRepository.findById(fineId)
//      .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));
//
//    User paidBy = userRepository.findById(paidByUserId)
//      .orElseThrow(() -> new ResourceNotFoundException("Payer user not found"));
//
//    double outstanding = fine.getAmount() == null ? 0.0 : fine.getAmount();
//
//    FinePayment payment = new FinePayment();
//    payment.setFine(fine);
//    payment.setAmountPaid(amountPaid);
//    payment.setPaymentMethod(paymentMethod);
//    payment.setTransactionRef(transactionRef);
//    payment.setPaidBy(paidBy);
//    payment.setStatus(FinePaymentStatus.SUCCESS);
//    payment.setNote(note);
//    finePaymentRepository.save(payment);
//
//    if (amountPaid >= outstanding) {
//      // full (or over) payment -> mark PAID and set outstanding to 0
//      fine.setAmount(0.0);
//      fine.setStatus(FineStatus.PAID);
//      // if overpayment, store info in note (or create credit record)
//      if (amountPaid > outstanding) {
//        double over = amountPaid - outstanding;
//        String newNote = (fine.getNote() == null ? "" : fine.getNote() + " | ") +
//          "Paid in full on " + java.time.LocalDateTime.now() + ". Overpayment: " + over;
//        fine.setNote(newNote);
//        // OPTIONAL: create user credit entry here if you implemented user_credits table
//        // createUserCredit(paidBy, over, "Overpayment for fine " + fineId);
//      } else {
//        fine.setNote((fine.getNote()==null? "":fine.getNote()+" | ") + "Paid in full on " + java.time.LocalDateTime.now());
//      }
//    } else {
//      // partial payment: reduce outstanding
//      fine.setAmount(fine.getAmount() - amountPaid);
//      fine.setStatus(FineStatus.UNPAID);
//      fine.setNote((fine.getNote()==null? "":fine.getNote()+" | ") + "Partial payment of " + amountPaid + " on " + java.time.LocalDateTime.now());
//    }
//    fineRepository.save(fine);
//    return payment;
//  }


// Abhishek

  @Transactional
  public FinePayment payFine(Long fineId, Long paidByUserId, Double amountPaid,
                             String paymentMethod, String transactionRef, String note) {

    if (amountPaid == null || amountPaid <= 0) throw new IllegalArgumentException("Invalid payment amount");
    if (paymentMethod == null || paymentMethod.isBlank()) throw new IllegalArgumentException("Payment method required");
    if (transactionRef != null && !transactionRef.isBlank()
      && finePaymentRepository.existsByTransactionRef(transactionRef)) {
      throw new IllegalArgumentException("Duplicate transaction reference");
    }

    Fine fine = fineRepository.findById(fineId)
      .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));

    if (fine.getStatus() == FineStatus.PAID || fine.getStatus() == FineStatus.WAIVED) {
      throw new IllegalStateException("This fine is already " + fine.getStatus());
    }

    User paidBy = userRepository.findById(paidByUserId)
      .orElseThrow(() -> new ResourceNotFoundException("Payer user not found"));

    double outstanding = Math.max(0.0, Optional.ofNullable(fine.getAmount()).orElse(0.0));
    if (amountPaid > outstanding) {
      throw new IllegalArgumentException("Amount exceeds outstanding fine (" + outstanding + ")");
    }

    FinePayment payment = new FinePayment();
    payment.setFine(fine);
    payment.setAmountPaid(amountPaid);
    payment.setPaymentMethod(paymentMethod);
    payment.setTransactionRef(transactionRef);
    payment.setPaidBy(paidBy);
    payment.setStatus(FinePaymentStatus.SUCCESS);
    payment.setNote(note);
    finePaymentRepository.save(payment);

    if (amountPaid == outstanding) {
      fine.setAmount(0.0);
      fine.setStatus(FineStatus.PAID);
      fine.setNote(appendNote(fine.getNote(), "Paid in full on " + java.time.LocalDateTime.now()));
    } else { // partial
      fine.setAmount(outstanding - amountPaid);
      fine.setStatus(FineStatus.UNPAID);
      fine.setNote(appendNote(fine.getNote(),
        "Partial payment of " + amountPaid + " on " + java.time.LocalDateTime.now()));
    }

    fineRepository.save(fine);
    return payment;
  }

  private static String appendNote(String base, String add) {
    return (base == null || base.isBlank()) ? add : base + " | " + add;
  }


//
//  @Transactional
//  public Fine adjustFine(Long fineId, Double newAmount, String note) {
//    Fine fine = fineRepository.findById(fineId)
//      .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));
//    fine.setAmount(newAmount);
//    fine.setStatus(FineStatus.ADJUSTED);
//    fine.setNote(note);
//    return fineRepository.save(fine);
//  }
//
//  @Transactional
//  public Fine waiveFine(Long fineId, String note) {
//    Fine fine = fineRepository.findById(fineId)
//      .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));
//    fine.setAmount(0.0);
//    fine.setStatus(FineStatus.WAIVED);
//    fine.setNote(note);
//    return fineRepository.save(fine);
//  }
//}

  @Transactional
  public Fine adjustFine(Long fineId, Double newAmount, String note) {
    if (newAmount == null || newAmount < 0.0) throw new IllegalArgumentException("Amount must be >= 0");
    Fine fine = fineRepository.findById(fineId)
      .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));
    if (fine.getStatus() == FineStatus.PAID) throw new IllegalStateException("Cannot adjust a PAID fine");
    fine.setAmount(newAmount);
    fine.setStatus(newAmount == 0.0 ? FineStatus.WAIVED : FineStatus.ADJUSTED);
    fine.setNote(note);
    return fineRepository.save(fine);
  }

  @Transactional
  public Fine waiveFine(Long fineId, String note) {
    Fine fine = fineRepository.findById(fineId)
      .orElseThrow(() -> new ResourceNotFoundException("Fine not found"));
    if (fine.getStatus() == FineStatus.PAID) throw new IllegalStateException("Cannot waive a PAID fine");
    fine.setAmount(0.0);
    fine.setStatus(FineStatus.WAIVED);
    fine.setNote(note);
    return fineRepository.save(fine);
  }

}
