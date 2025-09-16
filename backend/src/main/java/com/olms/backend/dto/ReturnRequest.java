// package com.olms.backend.dto;

// public class ReturnRequest {
//   private Long loanId;     // optional
//   private Integer bookId;  // optional

//   public ReturnRequest() {}

//   public Long getLoanId() { return loanId; }
//   public void setLoanId(Long loanId) { this.loanId = loanId; }

//   public Integer getBookId() { return bookId; }
//   public void setBookId(Integer bookId) { this.bookId = bookId; }
// }

// snigdha

package com.olms.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class ReturnRequest {

    private Long userId; // required
    private Integer bookId; // required

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate; // optional

    public ReturnRequest() {
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }
}