package com.olms.backend.service;

import com.olms.backend.entities.*;
import com.olms.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FineService Tests")
class FineServiceTest {

    @Mock
    private FineRepository fineRepository;
    @Mock
    private FinePaymentRepository finePaymentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private FineService fineService;

    private Fine testFine;
    private User testUser;
    private Loan testLoan;

    @Captor
    private ArgumentCaptor<Fine> fineCaptor;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testLoan = new Loan();
        testLoan.setId(1L);
        testLoan.setUser(testUser);

        testFine = new Fine();
        testFine.setFineId(1L);
        testFine.setAmount(50.0);
        testFine.setStatus(FineStatus.UNPAID);
        testFine.setUser(testUser);
        testFine.setLoan(testLoan);
    }
    
    @Nested
    @DisplayName("createOrUpdateFineForLoan Method")
    class CreateOrUpdateFineTests {
        @Test
        @DisplayName("✅ Should create a new fine if one doesn't exist and amount > 0")
        void whenFineIsCreated_shouldSaveNewFine() {
            testLoan.setFineAmount(25.0);
            when(fineRepository.findByLoan(testLoan)).thenReturn(Optional.empty());

            fineService.createOrUpdateFineForLoan(testLoan);

            verify(fineRepository).save(fineCaptor.capture());
            Fine savedFine = fineCaptor.getValue();
            
            assertEquals(25.0, savedFine.getAmount());
            assertEquals(FineStatus.UNPAID, savedFine.getStatus());
            assertEquals(testUser, savedFine.getUser());
        }

        @Test
        @DisplayName("✅ Should waive an existing fine if loan fine amount is 0")
        void whenFineIsZero_andExistingUnpaidFine_shouldWaiveIt() {
            testLoan.setFineAmount(0.0);
            when(fineRepository.findByLoan(testLoan)).thenReturn(Optional.of(testFine));
            
            fineService.createOrUpdateFineForLoan(testLoan);
            
            verify(fineRepository).save(fineCaptor.capture());
            Fine savedFine = fineCaptor.getValue();
            
            assertEquals(0.0, savedFine.getAmount());
            assertEquals(FineStatus.WAIVED, savedFine.getStatus());
        }
    }
    
    @Nested
    @DisplayName("payFine Method")
    class PayFineTests {
        @Test
        @DisplayName("✅ Should process a full payment correctly")
        void whenFullPayment_shouldUpdateFineToPaid() {
            when(fineRepository.findById(1L)).thenReturn(Optional.of(testFine));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            
            fineService.payFine(1L, 1L, 50.0, "CASH", null, "Full payment");

            verify(finePaymentRepository).save(any(FinePayment.class));
            verify(fineRepository).save(fineCaptor.capture());
            Fine updatedFine = fineCaptor.getValue();

            assertEquals(0.0, updatedFine.getAmount());
            assertEquals(FineStatus.PAID, updatedFine.getStatus());
        }

        @Test
        @DisplayName("✅ Should process a partial payment correctly")
        void whenPartialPayment_shouldReduceAmountAndKeepUnpaid() {
            when(fineRepository.findById(1L)).thenReturn(Optional.of(testFine));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

            fineService.payFine(1L, 1L, 20.0, "CASH", null, "Partial payment");

            verify(fineRepository).save(fineCaptor.capture());
            Fine updatedFine = fineCaptor.getValue();
            
            assertEquals(30.0, updatedFine.getAmount()); // 50 - 20
            assertEquals(FineStatus.UNPAID, updatedFine.getStatus());
        }

        @Test
        @DisplayName("❌ Should throw exception if payment exceeds outstanding amount")
        void whenPaymentExceedsOutstanding_shouldThrowException() {
            when(fineRepository.findById(1L)).thenReturn(Optional.of(testFine));
            when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
            
            assertThrows(IllegalArgumentException.class, () -> fineService.payFine(1L, 1L, 60.0, "CASH", null, "Overpayment"));
        }
    }
}