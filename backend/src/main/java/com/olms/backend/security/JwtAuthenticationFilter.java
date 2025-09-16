////package com.olms.backend.security;
////
////import jakarta.servlet.FilterChain;
////import jakarta.servlet.ServletException;
////import jakarta.servlet.http.HttpServletRequest;
////import jakarta.servlet.http.HttpServletResponse;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.core.GrantedAuthority;
////import org.springframework.security.core.authority.SimpleGrantedAuthority;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.stereotype.Component;
////import org.springframework.web.filter.OncePerRequestFilter;
////
////import java.io.IOException;
////import java.util.List;
////
////@Component
////public class JwtAuthenticationFilter extends OncePerRequestFilter {
////
////    @Autowired
////    private JwtUtil jwtUtil;
////
////    @Override
////    protected void doFilterInternal(HttpServletRequest request,
////            HttpServletResponse response,
////            FilterChain filterChain) throws ServletException, IOException {
////
////        String path = request.getRequestURI();
////        System.out.println("JwtAuthenticationFilter path: " + path);
////        if (path.startsWith("/auth/")) { // skip login/register
////            filterChain.doFilter(request, response);
////            return;
////        }
////
////        String authHeader = request.getHeader("Authorization");
////        if (authHeader != null && authHeader.startsWith("Bearer ")) {
////            String token = authHeader.substring(7);
////            if (jwtUtil.isTokenValid(token)) {
////                String email = jwtUtil.getEmail(token);
////                String role = jwtUtil.getRole(token);
////
////                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role)); // use literal ADMIN
////                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
////                        authorities);
////                SecurityContextHolder.getContext().setAuthentication(auth);
////            }
////        }
////
////        filterChain.doFilter(request, response);
////    }
////}
//
//// Abhishek
//
//
//
////package com.olms.backend.security;
////
////import jakarta.servlet.FilterChain;
////import jakarta.servlet.ServletException;
////import jakarta.servlet.http.HttpServletRequest;
////import jakarta.servlet.http.HttpServletResponse;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
////import org.springframework.security.core.GrantedAuthority;
////import org.springframework.security.core.authority.SimpleGrantedAuthority;
////import org.springframework.security.core.context.SecurityContextHolder;
////import org.springframework.stereotype.Component;
////import org.springframework.util.StringUtils;
////import org.springframework.web.filter.OncePerRequestFilter;
////
////import java.io.IOException;
////import java.util.List;
//////
////@Component
////public class JwtAuthenticationFilter extends OncePerRequestFilter {
////
////  @Autowired
////  private JwtUtil jwtUtil;
////
////  @Override
////  protected void doFilterInternal(HttpServletRequest request,
////                                  HttpServletResponse response,
////                                  FilterChain filterChain) throws ServletException, IOException {
////
////    final String path = request.getRequestURI();
////
////    // Skip auth endpoints and CORS preflight
//////    if (path.startsWith("/auth/") || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
//////      filterChain.doFilter(request, response);
//////      return;
//////    }
////
//// // Abhishek
////
////// Skip only auth endpoints + preflight
////    if ("/auth/login".equals(path) || "/auth/register".equals(path)
////      || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
////      filterChain.doFilter(request, response);
////      return;
////    }
////
////    final String authHeader = request.getHeader("Authorization");
////    if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
////      final String token = authHeader.substring(7);
////
////      if (jwtUtil.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
////        final String email = jwtUtil.getEmail(token);
////        final String role = jwtUtil.getRole(token); // expect "ADMIN" or "USER"
////
////        // Spring role convention -> "ROLE_ADMIN" / "ROLE_USER"
////        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
////
////        UsernamePasswordAuthenticationToken authentication =
////          new UsernamePasswordAuthenticationToken(email, null, authorities);
////
////        // Optionally attach details like userId:
////        // Long userId = jwtUtil.getUserId(token);
////        // authentication.setDetails(userId);
////
////        SecurityContextHolder.getContext().setAuthentication(authentication);
////      }
////    }
////
////    filterChain.doFilter(request, response);
////  }
////}
//
//
//package com.olms.backend.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.*;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//  @Autowired
//  private JwtUtil jwtUtil;
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request,
//                                  HttpServletResponse response,
//                                  FilterChain chain) throws ServletException, IOException {
//
//    String path = request.getRequestURI();
//    if (path.startsWith("/auth/") && !"/auth/profile".equals(path)) {
//      chain.doFilter(request, response);
//      return;
//    }
//
//    String authHeader = request.getHeader("Authorization");
//    if (authHeader != null && authHeader.startsWith("Bearer ")) {
//      String token = authHeader.substring(7);
//      if (jwtUtil.isTokenValid(token)) {
//        String email = jwtUtil.getEmail(token);
//        String role  = jwtUtil.getRole(token);
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role)); // "ADMIN" or "USER"
//        UsernamePasswordAuthenticationToken auth =
//          new UsernamePasswordAuthenticationToken(email, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//      }
//    }
//    chain.doFilter(request, response);
//  }
//}

//package com.olms.backend.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.*;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//  @Autowired private JwtUtil jwtUtil;
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request,
//                                  HttpServletResponse response,
//                                  FilterChain chain) throws ServletException, IOException {
//
//    String path = request.getRequestURI();
//    if ("/auth/login".equals(path) || "/auth/register".equals(path)
//      || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
//      chain.doFilter(request, response);
//      return;
//    }
//
//    String authHeader = request.getHeader("Authorization");
//    if (authHeader != null && authHeader.startsWith("Bearer ")) {
//      String token = authHeader.substring(7);
//      if (jwtUtil.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
//        String email = jwtUtil.getEmail(token);
//        String role  = jwtUtil.getRole(token);            // "ADMIN" or "USER"
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(role));
//        UsernamePasswordAuthenticationToken auth =
//          new UsernamePasswordAuthenticationToken(email, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//      }
//    }
//    chain.doFilter(request, response);
//  }
//}

//// com/olms/backend/security/JwtAuthenticationFilter.java
//package com.olms.backend.security;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.*;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//
//@Component
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//
//  @Autowired private JwtUtil jwtUtil;
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
//    throws ServletException, IOException {
//
//    String path = request.getRequestURI();
//    if ("/auth/login".equals(path) || "/auth/register".equals(path) || "OPTIONS".equalsIgnoreCase(request.getMethod())) {
//      chain.doFilter(request, response);
//      return;
//    }
//
//    String auth = request.getHeader("Authorization");
//    if (auth != null && auth.startsWith("Bearer ")) {
//      String token = auth.substring(7);
//      if (jwtUtil.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
//        String email = jwtUtil.getEmail(token);
//        String role  = jwtUtil.getRole(token); // "ADMIN"/"USER"
//        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
//        UsernamePasswordAuthenticationToken authentication =
//          new UsernamePasswordAuthenticationToken(email, null, authorities);
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//      }
//    }
//    chain.doFilter(request, response);
//  }
//}

// com/olms/backend/security/JwtAuthenticationFilter.java
package com.olms.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtUtil jwtUtil;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {

    final String path = request.getRequestURI();

    // 1) Bypass public endpoints & preflight
    // if ("OPTIONS".equalsIgnoreCase(request.getMethod())
    // || path.startsWith("/auth/") // login, register, verify, resend...
    // || path.startsWith("/public/")) {
    // chain.doFilter(request, response);
    // return;
    // }

    if ("OPTIONS".equalsIgnoreCase(request.getMethod())
        || path.startsWith("/auth/login")
        || path.startsWith("/auth/register")
        || path.startsWith("/auth/verify")
        || path.startsWith("/auth/resend")
        || path.startsWith("/auth/resend-verification")
        || path.startsWith("/public/")) {
      chain.doFilter(request, response);
      return;
    }

    // 2) Parse & validate JWT if present
    String auth = request.getHeader("Authorization");
    if (auth != null && auth.startsWith("Bearer ")) {
      String token = auth.substring(7).trim();
      try {
        if (jwtUtil.isTokenValid(token) && SecurityContextHolder.getContext().getAuthentication() == null) {
          String email = jwtUtil.getEmail(token);
          String role = jwtUtil.getRole(token); // expect "ADMIN" or "USER"
          System.out.println("Decoded JWT role: " + role); // <-- add this line

          List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
          UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(email, null,
              authorities);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      } catch (Exception ex) {
        // Silently ignore bad/expired tokens; SecurityConfig will handle authz (401) if
        // required.
      }
    }

    chain.doFilter(request, response);
  }
}
