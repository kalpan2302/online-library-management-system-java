package com.olms.backend.repository;

import com.olms.backend.entities.FinePayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
//public interface FinePaymentRepository extends JpaRepository<FinePayment, Long> {
//}

//Abhishek

// FinePaymentRepository.java
public interface FinePaymentRepository extends JpaRepository<FinePayment, Long> {
  boolean existsByTransactionRef(String transactionRef);
}
