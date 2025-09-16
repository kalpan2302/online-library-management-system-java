package com.olms.backend.dto;

import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

public class IssueRequest {

  @NotNull
  private Integer bookId;      // required

  private Long userId;         // optional (admin-only use)

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate dueDate;   // optional

  public IssueRequest() {}

  public Integer getBookId() { return bookId; }
  public void setBookId(Integer bookId) { this.bookId = bookId; }

  public Long getUserId() { return userId; }
  public void setUserId(Long userId) { this.userId = userId; }

  public LocalDate getDueDate() { return dueDate; }
  public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
}
