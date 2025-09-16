//package com.olms.backend.entities;
//
//import jakarta.persistence.*;
//import lombok.Data;
//import java.time.LocalDateTime;
//
//@Entity
//@Data
//@Table(name = "users")
//public class User {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "user_id")
//    private Long id;
//
//
//    private String name;
//    private String email;
//    private String phone;
//    private String password;
//
//    @Enumerated(EnumType.STRING)
//    private Role role;
//
//    @Column(name = "created_at", updatable = false)
//    private LocalDateTime createdAt;
//
//    @PrePersist
//    protected void onCreate() {
//        this.createdAt = LocalDateTime.now();
//    }
//}

// Abhishek


//
//package com.olms.backend.entities;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import jakarta.persistence.*;
//
//import lombok.*;
//import java.time.LocalDateTime;
//
//@Entity
//@Data
//@Table(name = "users",
//  indexes = {
//    @Index(name = "idx_users_email", columnList = "email")
//  },
//  uniqueConstraints = {
//    @UniqueConstraint(name = "uk_users_email", columnNames = {"email"})
//  })
//public class User {
//
//  @Id
//  @GeneratedValue(strategy = GenerationType.IDENTITY)
//  @Column(name = "user_id")
//  private Long id;
//
//  @Column(nullable = true, length = 255)
//  private String name;
//
//  @Column(nullable = false, length = 255)
//  private String email;
//
//  @Column(nullable = true, length = 255)
//  private String phone;
//
//  @JsonIgnore
//  @Column(nullable = false, length = 255)
//  private String password;
//
//  @Enumerated(EnumType.STRING)
//  @Column(nullable = false, length = 10)
//  private Role role;
//
//  @Enumerated(EnumType.STRING)
//  @Builder.Default
//  private AccountStatus status = AccountStatus.PENDING; // <--- add this
//
//  @Column(name = "created_at", updatable = false)
//  private LocalDateTime createdAt;
//
//  @PrePersist
//  protected void onCreate() {
//
//    this.createdAt = LocalDateTime.now();
//    if (status == null) status = AccountStatus.PENDING;
//    if (this.role == null) this.role = Role.USER; // default
//  }
//}

package com.olms.backend.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(
  name = "users",
  indexes = { @Index(name = "idx_users_email", columnList = "email") },
  uniqueConstraints = { @UniqueConstraint(name = "uk_users_email", columnNames = {"email"}) }
)
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "user_id")
  private Long id;

  @Column(length = 255)
  private String name;

  @Column(nullable = false, length = 255)
  private String email;

  @Column(length = 255)
  private String phone;

  @JsonIgnore
  @Column(nullable = false, length = 255)
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  private Role role; // ADMIN / USER

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  @Builder.Default
  private AccountStatus status = AccountStatus.PENDING;

  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    if (status == null) status = AccountStatus.PENDING;
    if (this.role == null) this.role = Role.USER;
  }
}
