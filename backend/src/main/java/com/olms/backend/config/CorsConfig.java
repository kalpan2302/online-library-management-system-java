//// com/olms/backend/config/CorsConfig.java
//package com.olms.backend.config;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
//import org.springframework.web.filter.CorsFilter;
//
//import java.net.URI;
//import java.util.Arrays;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Configuration
//public class CorsConfig {
//
//  @Value("${app.cors.allowed-origins:}")
//  private String allowedOriginsCsv;
//
//  @Value("${app.frontend.base-url:}")
//  private String frontendBaseUrl;
//
//  private List<String> resolveAllowedOrigins() {
//    // if explicit list provided, use it
//    if (allowedOriginsCsv != null && !allowedOriginsCsv.isBlank()) {
//      return Arrays.stream(allowedOriginsCsv.split("\\s*,\\s*"))
//        .filter(s -> !s.isBlank())
//        .collect(Collectors.toList());
//    }
//    // else fall back to the origin from frontendBaseUrl
//    if (frontendBaseUrl != null && !frontendBaseUrl.isBlank()) {
//      URI u = URI.create(frontendBaseUrl);
//      String origin = u.getScheme() + "://" + u.getHost() + (u.getPort() > 0 ? ":" + u.getPort() : "");
//      return List.of(origin);
//    }
//    // final fallback: dev localhost (only if nothing set)
//    return List.of("http://localhost:4200");
//  }
//
//  @Bean
//  public CorsFilter corsFilter() {
//    CorsConfiguration cfg = new CorsConfiguration();
//    resolveAllowedOrigins().forEach(cfg::addAllowedOrigin); // exact origins only (credentials-safe)
//    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
//    cfg.setAllowedHeaders(List.of("*"));
//    cfg.setAllowCredentials(true);
//    cfg.setMaxAge(3600L);
//
//    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
//    src.registerCorsConfiguration("/**", cfg);
//    return new CorsFilter(src);
//  }
//}


// com/olms/backend/config/CorsConfig.java
package com.olms.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class CorsConfig {

  @Value("${app.cors.allowed-origins:}")
  private String allowedOriginsCsv;

  @Value("${app.frontend.base-url:}")
  private String frontendBaseUrl;

  private List<String> resolveAllowedOrigins() {
    // if explicit list provided, use it
    if (allowedOriginsCsv != null && !allowedOriginsCsv.isBlank()) {
      return Arrays.stream(allowedOriginsCsv.split("\\s*,\\s*"))
        .filter(s -> !s.isBlank())
        .collect(Collectors.toList());
    }
    // else fall back to the origin from frontendBaseUrl
    if (frontendBaseUrl != null && !frontendBaseUrl.isBlank()) {
      URI u = URI.create(frontendBaseUrl);
      String origin = u.getScheme() + "://" + u.getHost() + (u.getPort() > 0 ? ":" + u.getPort() : "");
      return List.of(origin);
    }
    // final fallback: dev localhost (only if nothing set)
    return List.of("http://localhost:4200");
  }

  @Bean
  public CorsFilter corsFilter() {
    CorsConfiguration cfg = new CorsConfiguration();
    resolveAllowedOrigins().forEach(cfg::addAllowedOrigin); // exact origins only (credentials-safe)
    cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    cfg.setAllowedHeaders(List.of("*"));
    cfg.setAllowCredentials(true);
    cfg.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
    src.registerCorsConfiguration("/**", cfg);
    return new CorsFilter(src);
  }
}

