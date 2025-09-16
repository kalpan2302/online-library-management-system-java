package com.olms.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UpdateBookRequest {

  @Size(max = 200)
  private String title;

  @Pattern(regexp = "^[0-9Xx-]{10,17}$", message = "Invalid ISBN format")
  private String isbn;

  @Min(0)
  private Integer totalCopies;

  @Min(0)
  private Integer availableCopies;

  private String shelfLocation;
  private String publicationName; 
  private String categoryName;  
  private List<String> authorsNames;

  /** Cross-field validation: only enforced when both fields are present. */
  @JsonIgnore
  @jakarta.validation.constraints.AssertTrue(message = "availableCopies cannot exceed totalCopies")
  public boolean isCopiesConsistent() {
    if (totalCopies == null || availableCopies == null) return true;
    return availableCopies <= totalCopies;
  }
}
