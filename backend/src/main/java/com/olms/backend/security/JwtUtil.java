////package com.olms.backend.security;
////
////import io.jsonwebtoken.*;
////import io.jsonwebtoken.security.Keys;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.stereotype.Component;
////
////import javax.crypto.SecretKey;
////import java.nio.charset.StandardCharsets;
////import java.util.Date;
////
////@Component
////
////public class JwtUtil {
////
////    // Make sure this string is at least 32 chars (256 bits)
////    private String secret = "MyVeryLongSuperSecretKeyForJWT12345!";
////
////    private final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
////    private long expiration = 86400000; // 24hrs
////
////    public String generateToken(String email, String role) {
////        return Jwts.builder()
////                .setSubject(email)
////                .claim("role", role)
////                .setIssuedAt(new Date())
////                .setExpiration(new Date(System.currentTimeMillis() + expiration))
////                .signWith(key, SignatureAlgorithm.HS256)
////                .compact();
////    }
////
////    public Claims getClaims(String token) {
////        return Jwts.parserBuilder()
////                .setSigningKey(key)
////                .build()
////                .parseClaimsJws(token)
////                .getBody();
////    }
////
////    public boolean isTokenValid(String token) {
////        try {
////            getClaims(token);
////            return true;
////        } catch (JwtException e) {
////            return false;
////        }
////    }
////
////    public String getEmail(String token) {
////        return getClaims(token).getSubject();
////    }
////
////    public String getRole(String token) {
////        return getClaims(token).get("role", String.class);
////    }
////}
//
//
//// Abhishek
//
//
////package com.olms.backend.security;
//
////import io.jsonwebtoken.*;
////import io.jsonwebtoken.security.Keys;
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.stereotype.Component;
////
////import javax.crypto.SecretKey;
////import java.nio.charset.StandardCharsets;
////import java.util.Date;
////import java.util.Map;
////
////@Component
////public class JwtUtil {
////
////  // application.properties:
////  // security.jwt.secret=change_this_to_a_very_long_random_value_at_least_64_chars
////  // security.jwt.expiration-ms=86400000
////  // security.jwt.clock-skew-sec=60
////
////  private final SecretKey key;
////  private final long expirationMs;
////  private final long clockSkewSec;
////
////  public JwtUtil(
////    @Value("${security.jwt.secret}") String secret,
////    @Value("${security.jwt.expiration-ms:86400000}") long expirationMs,
////    @Value("${security.jwt.clock-skew-sec:60}") long clockSkewSec
////  ) {
////    if (secret == null || secret.length() < 32) {
////      throw new IllegalArgumentException("JWT secret must be at least 32 chars");
////    }
////    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
////    this.expirationMs = expirationMs;
////    this.clockSkewSec = clockSkewSec;
////  }
////
////  public String generateToken(Long userId, String email, String role) {
////    Date now = new Date();
////    Date exp = new Date(now.getTime() + expirationMs);
////
////    return Jwts.builder()
////      .setSubject(email) // principal
////      .addClaims(Map.of(
////        "uid", userId,
////        "role", role
////      ))
////      .setIssuedAt(now)
////      .setExpiration(exp)
////      .signWith(key, SignatureAlgorithm.HS256)
////      .compact();
////  }
////
////  public Claims getClaims(String token) throws JwtException {
////    return Jwts.parserBuilder()
////      .setSigningKey(key)
////      .setAllowedClockSkewSeconds(clockSkewSec)
////      .build()
////      .parseClaimsJws(token)
////      .getBody();
////  }
////
////  public boolean isTokenValid(String token) {
////    try {
////      getClaims(token);
////      return true;
////    } catch (JwtException e) {
////      return false;
////    }
////  }
////
////  public String getEmail(String token) {
////    return getClaims(token).getSubject();
////  }
////
////  public String getRole(String token) {
////    return getClaims(token).get("role", String.class);
////  }
////
////  public Long getUserId(String token) {
////    Object val = getClaims(token).get("uid");
////    if (val instanceof Number n) return n.longValue();
////    if (val instanceof String s) return Long.parseLong(s);
////    return null;
////  }
////}
//
//
//// abhishek 2nd attempt
//
//
//package com.olms.backend.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//@Component
//public class JwtUtil {
//  private final String secret = "MyVeryLongSuperSecretKeyForJWT12345!"; // >=32 chars
//  private final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//  private final long expiration = 24 * 60 * 60 * 1000L; // 24h
//
//  public String generateToken(Long userId, String email, String role) {
//    return Jwts.builder()
//      .setSubject(email)                         // <- SUBJECT = EMAIL
//      .claim("uid", userId)
//      .claim("role", role)
//      .setIssuedAt(new Date())
//      .setExpiration(new Date(System.currentTimeMillis() + expiration))
//      .signWith(key, SignatureAlgorithm.HS256)
//      .compact();
//  }
//
//  public Claims getClaims(String token) {
//    return Jwts.parserBuilder().setSigningKey(key).build()
//      .parseClaimsJws(token).getBody();
//  }
//
//  public boolean isTokenValid(String token) {
//    try { getClaims(token); return true; } catch (JwtException e) { return false; }
//  }
//
//  public String getEmail(String token) { return getClaims(token).getSubject(); }
//  public String getRole(String token)  { return getClaims(token).get("role", String.class); }
//  public Long getUserId(String token)  { return getClaims(token).get("uid", Long.class); }
//}


//package com.olms.backend.security;
//
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//import java.util.Map;
//
//@Component
//public class JwtUtil {
//
//  private final SecretKey key;
//  private final long expirationMs;
//  private final long clockSkewSec;
//
//  public JwtUtil(
//    @Value("${security.jwt.secret}") String secret,
//    @Value("${security.jwt.expiration-ms:86400000}") long expirationMs,
//    @Value("${security.jwt.clock-skew-sec:60}") long clockSkewSec
//  ) {
//    if (secret == null || secret.length() < 64) {
//      throw new IllegalArgumentException("security.jwt.secret must be at least 64 chars");
//    }
//    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
//    this.expirationMs = expirationMs;
//    this.clockSkewSec = clockSkewSec;
//  }
//
//  public String generateToken(Long userId, String email, String role) {
//    Date now = new Date();
//    Date exp = new Date(now.getTime() + expirationMs);
//    return Jwts.builder()
//      .setSubject(email)
//      .addClaims(Map.of("uid", userId, "role", role))
//      .setIssuedAt(now)
//      .setExpiration(exp)
//      .signWith(key, SignatureAlgorithm.HS256)
//      .compact();
//  }
//
//  public Claims getClaims(String token) {
//    return Jwts.parserBuilder()
//      .setSigningKey(key)
//      .setAllowedClockSkewSeconds(clockSkewSec)
//      .build()
//      .parseClaimsJws(token)
//      .getBody();
//  }
//
//  public boolean isTokenValid(String token) {
//    try { getClaims(token); return true; } catch (JwtException e) { return false; }
//  }
//
//  public String getEmail(String token) { return getClaims(token).getSubject(); }
//  public String getRole(String token)  { return getClaims(token).get("role", String.class); }
//  public Long getUserId(String token)  {
//    Object uid = getClaims(token).get("uid");
//    return (uid instanceof Number n) ? n.longValue() : Long.valueOf(uid.toString());
//  }
//}


// com/olms/backend/security/JwtUtil.java
package com.olms.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
  private final String secret = "MyVeryLongSuperSecretKeyForJWT12345!"; // replace with strong value in prod
  private final SecretKey key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  private final long expiration = 24 * 60 * 60 * 1000L; // 24h

  public String generateToken(Long userId, String email, String role) {
    return Jwts.builder()
      .setSubject(email)
      .claim("uid", userId)
      .claim("role", role)
      .setIssuedAt(new Date())
      .setExpiration(new Date(System.currentTimeMillis() + expiration))
      .signWith(key, SignatureAlgorithm.HS256)
      .compact();
  }

  public Claims getClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build()
      .parseClaimsJws(token).getBody();
  }

  public boolean isTokenValid(String token) {
    try { getClaims(token); return true; } catch (JwtException e) { return false; }
  }

  public String getEmail(String token) { return getClaims(token).getSubject(); }
  public String getRole(String token)  { return getClaims(token).get("role", String.class); }
  public Long getUserId(String token)  { return getClaims(token).get("uid", Long.class); }
}
