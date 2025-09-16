//package com.olms.backend.repository;
//
//import com.olms.backend.entities.Loan;
//import com.olms.backend.entities.LoanStatus;
//import com.olms.backend.entities.User;
//import com.olms.backend.entities.Book;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
////public interface LoanRepository extends JpaRepository<Loan, Long> {
////  List<Loan> findByUserAndStatus(User user, com.olms.backend.entities.LoanStatus status);
////  List<Loan> findByBookAndStatus(Book book, com.olms.backend.entities.LoanStatus status);
////  Optional<Loan> findByBookAndUserAndStatus(Book book, User user, com.olms.backend.entities.LoanStatus status);
////}
////
//
//
//// Abhishek
//
//// LoanRepository.java
//public interface LoanRepository extends JpaRepository<Loan, Long> {
//  long countByUserAndStatus(User user, LoanStatus status);
//  boolean existsByBookAndUserAndStatus(Book book, User user, LoanStatus status);
//  Optional<Loan> findByBookAndUserAndStatus(Book book, User user, LoanStatus status);
//}


//package com.olms.backend.repository;
//
//import com.olms.backend.entities.*;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.Optional;
//
//public interface LoanRepository extends JpaRepository<Loan, Long> {
//  long countByUserAndStatus(User user, LoanStatus status);
//  boolean existsByBookAndUserAndStatus(Book book, User user, LoanStatus status);
//  Optional<Loan> findByBookAndUserAndStatus(Book book, User user, LoanStatus status);
//}


// com/olms/backend/repository/LoanRepository.java
package com.olms.backend.repository;

import com.olms.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
  long countByUserAndStatus(User user, LoanStatus status);
  boolean existsByBookAndUserAndStatus(Book book, User user, LoanStatus status);
  Optional<Loan> findByBookAndUserAndStatus(Book book, User user, LoanStatus status);
  List<Loan> findByStatusAndDueDate(LoanStatus status, LocalDate dueDate);
  List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDate before);
  List<Loan> findByStatusAndDueDateBetween(LoanStatus status, LocalDate from, LocalDate to);

  List<Loan> findByUser(User user);
  List<Loan> findByUserAndStatus(User user, LoanStatus status);
  List<Loan> findByUserAndStatusAndDueDateBefore(User user, LoanStatus status, LocalDate before);
  List<Loan> findAllByStatus(LoanStatus status);
}
