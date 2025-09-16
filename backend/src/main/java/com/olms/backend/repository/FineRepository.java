//package com.olms.backend.repository;
//
//import com.olms.backend.entities.Fine;
//import com.olms.backend.entities.User;
//import com.olms.backend.entities.Loan;
//import com.olms.backend.entities.FineStatus;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//@Repository
////public interface FineRepository extends JpaRepository<Fine, Long> {
////  List<Fine> findByUser(User user);
////  List<Fine> findByStatus(FineStatus status);
////  Optional<Fine> findByLoan(Loan loan);
////}
//
//
//// Abhishek
//// FineRepository.java
//public interface FineRepository extends JpaRepository<Fine, Long> {
//  Optional<Fine> findByLoan(Loan loan);
//  List<Fine> findByUser(User user);
//  long countByUserAndStatus(User user, FineStatus status);
//  boolean existsByUserAndStatus(User user, FineStatus status);
//}


//package com.olms.backend.repository;
//
//import com.olms.backend.entities.*;
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import java.util.*;
//
//public interface FineRepository extends JpaRepository<Fine, Long> {
//  Optional<Fine> findByLoan(Loan loan);
//  List<Fine> findByUser(User user);
//  long countByUserAndStatus(User user, FineStatus status);
//}



// com/olms/backend/repository/FineRepository.java
package com.olms.backend.repository;

import com.olms.backend.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface FineRepository extends JpaRepository<Fine, Long> {
  Optional<Fine> findByLoan(Loan loan);
  List<Fine> findByUser(User user);
  long countByUserAndStatus(User user, FineStatus status);

  @Query("select coalesce(sum(f.amount),0) from Fine f where f.user.id = :uid and f.status = :status")
  Double sumByUserAndStatus(@Param("uid") Long userId, @Param("status") FineStatus status);
}

