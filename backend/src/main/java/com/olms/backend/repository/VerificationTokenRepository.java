package com.olms.backend.repository;

import com.olms.backend.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

  @Query("""
           select v from VerificationToken v
           where v.tokenHash = :hash and v.usedAt is null and v.expiresAt > :now
           """)
  Optional<VerificationToken> findValid(String hash, Instant now);

  @Query("""
           select v from VerificationToken v
           where v.userId = :userId and v.usedAt is null
           order by v.createdAt desc
           """)
  Optional<VerificationToken> findLatestActiveForUser(Long userId);
}
