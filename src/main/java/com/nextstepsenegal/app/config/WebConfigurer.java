package com.nextstepsenegal.app.config;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tech.jhipster.config.JHipsterConstants;
import tech.jhipster.config.JHipsterProperties;
import tech.jhipster.config.h2.H2ConfigurationHelper;

/**
 * Configuration of web application.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;
    private final JHipsterProperties jHipsterProperties;

    public WebConfigurer(Environment env, JHipsterProperties jHipsterProperties) {
        this.env = env;
        this.jHipsterProperties = jHipsterProperties;
    }

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        if (env.getActiveProfiles().length != 0) {
            LOG.info("Web application configuration, using profiles: {}", (Object[]) env.getActiveProfiles());
        }

        if (h2ConsoleIsEnabled(env)) {
            initH2Console(servletContext);
        }
        LOG.info("Web application fully configured");
    }

    private boolean h2ConsoleIsEnabled(Environment env) {
        return (
            env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT)) &&
            "true".equals(env.getProperty("spring.h2.console.enabled"))
        );
    }

    private void initH2Console(ServletContext servletContext) {
        LOG.info("Initialize H2 console");
        H2ConfigurationHelper.initH2Console(servletContext);
    }

    // 🌐 Pour Angular : autorise toutes les origines front sur /api/**
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("http://localhost:8080"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        source.registerCorsConfiguration("/management/**", config);
        source.registerCorsConfiguration("/v3/api-docs/**", config);
        source.registerCorsConfiguration("/swagger-ui/**", config);

        return new CorsFilter(source);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                    .addMapping("/**")
                    .allowedOrigins("http://localhost:8080")
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(true);
            }
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
