////package com.olms.backend.security;
////
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.http.SessionCreationPolicy;
////import org.springframework.security.web.SecurityFilterChain;
////
////@Configuration
////@EnableMethodSecurity
////public class SecurityConfig {
////
////    @Autowired
////    private JwtAuthenticationFilter jwtAuthenticationFilter;
////
////    @Bean
////    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////        http
////            .csrf(csrf -> csrf.disable())
////            .cors(cors -> {})
////            .authorizeHttpRequests(auth -> auth
////                .requestMatchers("/auth/**").permitAll()      // must allow login & register
////                .requestMatchers("/api/admin/**").hasAuthority("ADMIN") // admin routes
////                .requestMatchers("/api/books/all").permitAll()    // public books list
////                .anyRequest().authenticated()
////            )
////            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
////
////        http.addFilterBefore(jwtAuthenticationFilter,
////                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);
////
////        return http.build();
////    }
////}
//
//
//
//// Abhishek
//
////package com.olms.backend.security;
////
////import jakarta.servlet.http.HttpServletResponse;
////import org.springframework.beans.factory.annotation.Autowired;
////import org.springframework.context.annotation.Bean;
////import org.springframework.context.annotation.Configuration;
////import org.springframework.http.HttpMethod;
////import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
////import org.springframework.security.config.annotation.web.builders.HttpSecurity;
////import org.springframework.security.config.http.SessionCreationPolicy;
////import org.springframework.security.web.SecurityFilterChain;
////import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
////
////@Configuration
////@EnableMethodSecurity
////public class SecurityConfig {
////
////  @Autowired
////  private JwtAuthenticationFilter jwtAuthenticationFilter;
////
////  @Bean
////  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
////    http
////      .csrf(csrf -> csrf.disable())
////      .cors(cors -> {}) // use your CorsConfig bean
////      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
////      .exceptionHandling(ex -> ex
////        .authenticationEntryPoint((req, res, e) -> {
////          res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
////          res.setContentType("application/json");
////          res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Authentication required\"}");
////        })
////        .accessDeniedHandler((req, res, e) -> {
////          res.setStatus(HttpServletResponse.SC_FORBIDDEN);
////          res.setContentType("application/json");
////          res.getWriter().write("{\"error\":\"forbidden\",\"message\":\"Insufficient privileges\"}");
////        })
////      )
////      .authorizeHttpRequests(auth -> auth
////        // CORS preflight
////        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
////
////        // Auth endpoints
//////        .requestMatchers("/auth/**").permitAll()
////
////        // Abhishek
////          // NEW (just these two are public)
////          .requestMatchers("/auth/login", "/auth/register").permitAll()
////
////        // Public browsing endpoints (adjust to your controllers)
////        .requestMatchers(
////          "/public/**",
////          "/api/books/all" // your existing public list
////        ).permitAll()
////
////        // Static / SPA assets (Angular)
////        .requestMatchers(
////          "/", "/index.html", "/assets/**", "/favicon.ico",
////          "/android-chrome-192x192.png", "/android-chrome-512x512.png",
////          "/apple-touch-icon.png", "/site.webmanifest"
////        ).permitAll()
////
////        // Admin
////        .requestMatchers("/api/admin/**").hasRole("ADMIN")
////
////        // Everything else requires auth
////        .anyRequest().authenticated()
////      );
////
////    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
////
////    return http.build();
////  }
////}
//
//
//// ABhishek 2nd attmept
//
//
//
//package com.olms.backend.security;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.*;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.*;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfig {
//
//  @Autowired
//  private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http
//      .csrf(csrf -> csrf.disable())
//      .cors(cors -> {})
//      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .exceptionHandling(ex -> ex
//        .authenticationEntryPoint((req, res, e) ->
//          res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication required"))
//      )
//      .authorizeHttpRequests(auth -> auth
//        .requestMatchers("/auth/login", "/auth/register").permitAll()
//        // profile now requires auth so you can rely on SecurityContext
//        .requestMatchers("/auth/profile").authenticated()
//        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
//        .requestMatchers("/api/books/all").permitAll()
//        .anyRequest().authenticated()
//      )
//      .httpBasic(b -> b.disable())
//      .formLogin(f -> f.disable());
//
//    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//    return http.build();
//  }
//}


//package com.olms.backend.security;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.*;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.*;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfig {
//
//  @Autowired private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http
//      .csrf(csrf -> csrf.disable())
//      .cors(cors -> {})
//      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .exceptionHandling(ex -> ex
//        .authenticationEntryPoint((req, res, e) -> {
//          res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//          res.setContentType("application/json");
//          res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Authentication required\"}");
//        })
//      )
//      .authorizeHttpRequests(auth -> auth
//        .requestMatchers("/auth/login", "/auth/register").permitAll()
//        .requestMatchers("/auth/profile").authenticated()
//        .requestMatchers("/api/books/all").permitAll()
//        .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
//        .anyRequest().authenticated()
//      )
//      .httpBasic(b -> b.disable())
//      .formLogin(f -> f.disable());
//
//    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//    return http.build();
//  }
//}


// com/olms/backend/security/SecurityConfig.java
//package com.olms.backend.security;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.*;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.*;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfig {
//
//  @Autowired
//  private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http
//      .csrf(csrf -> csrf.disable())
//      .cors(cors -> {}) // uses your CorsConfig bean
//      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .exceptionHandling(ex -> ex
//        .authenticationEntryPoint((req, res, e) -> {
//          res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//          res.setContentType("application/json");
//          res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Authentication required\"}");
//        })
//      )
//      .authorizeHttpRequests(auth -> auth
//        .requestMatchers("/auth/login", "/auth/register").permitAll()
//        .requestMatchers("/api/admin/**").hasRole("ADMIN")       // <= use hasRole
//        .requestMatchers("/api/books/all", "/public/**").permitAll()
//        .anyRequest().authenticated()
//      )
//      .httpBasic(b -> b.disable())
//      .formLogin(f -> f.disable());
//
//    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//    return http.build();
//  }
//}
//


//// com/olms/backend/security/SecurityConfig.java
//package com.olms.backend.security;
//
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.context.annotation.*;
//import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.http.SessionCreationPolicy;
//import org.springframework.security.web.*;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableMethodSecurity
//public class SecurityConfig {
//
//  private final JwtAuthenticationFilter jwtAuthenticationFilter;
//  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
//    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
//  }
//
//  @Bean
//  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//    http
//      .csrf(csrf -> csrf.disable())
//      .cors(cors -> {}) // uses your CorsConfig bean
//      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//      .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
//          res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//          res.setContentType("application/json");
//          res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Authentication required\"}");
//        })
//      )
//      .authorizeHttpRequests(auth -> auth
//        // PUBLIC endpoints (incl. verification)
//        .requestMatchers(
//          "/auth/register",
//          "/auth/login",
//          "/auth/verify",
//          "/auth/resend",
//          "/auth/resend-verification",
//          "/actuator/health",
//          "/public/**",
//          "/api/books/all",
//        "/api/notifications/**",
//          "/error",
//          "/favicon.ico",
//          "/**/*.css","/**/*.js","/**/*.html","/**/*.png","/**/*.jpg"
//        ).permitAll()
//        // Admin-only APIs
//        .requestMatchers("/api/admin/**").hasRole("ADMIN")
//        // Everything else requires auth
//        .anyRequest().authenticated()
//      )
//      .httpBasic(b -> b.disable())
//      .formLogin(f -> f.disable());
//
//    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
//    return http.build();
//  }
//}

package com.olms.backend.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;
  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
      .csrf(csrf -> csrf.disable())
      .cors(cors -> {})
      .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .exceptionHandling(ex -> ex.authenticationEntryPoint((req, res, e) -> {
          res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          res.setContentType("application/json");
          res.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Authentication required\"}");
        })
      )
      .authorizeHttpRequests(auth -> auth
        // Public auth + health + a couple open APIs
        .requestMatchers(
          "/auth/register",
          "/auth/login",
          "/auth/verify",
          "/auth/resend",
          "/auth/resend-verification",
          "/actuator/health",
          "/public/**",
          "/api/books/all",
          "/error",
          "/favicon.ico"
        ).permitAll()

        // Admin-only
        .requestMatchers("/api/admin/**").hasRole("ADMIN")

        // Everything else requires auth (incl. /api/notifications/**)
        .anyRequest().authenticated()
      )
      .httpBasic(b -> b.disable())
      .formLogin(f -> f.disable());

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
