package com.nextstepsenegal.app.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
/**
 * Properties specific to User Management Service.
 * <p>
 * Properties are configured in the {@code application.yml} file.
 * See {@link tech.jhipster.config.JHipsterProperties} for a good example.
 */
public class ApplicationProperties {

    private final Liquibase liquibase = new Liquibase();
    public String kcUrl1;
    private String kcUrl;
    private String kcUser;
    private String kcPassword;
    private String kcClientId;
    private String kcClientSecret;
    private String kcRole;
    private String kcBaseUrl;

    private String kcRealm;

    // jhipster-needle-application-properties-property

    public Liquibase getLiquibase() {
        return liquibase;
    }

    // jhipster-needle-application-properties-property-getter

    public String getKcRole() {
        return kcRole;
    }

    public void setKcRole(String kcRole) {
        this.kcRole = kcRole;
    }

    public String getKcUrl() {
        return kcUrl;
    }

    public void setKcUrl(String kcUrl) {
        this.kcUrl = kcUrl;
    }

    public String getKcUser() {
        return kcUser;
    }

    public void setKcUser(String kcUser) {
        this.kcUser = kcUser;
    }

    public String getKcPassword() {
        return kcPassword;
    }

    public void setKcPassword(String kcAdminPassword) {
        this.kcPassword = kcAdminPassword;
    }

    public String getKcClientId() {
        return kcClientId;
    }

    public void setKcClientId(String kcClientId) {
        this.kcClientId = kcClientId;
    }

    public String getKcClientSecret() {
        return kcClientSecret;
    }

    public void setKcClientSecret(String kcClientSecret) {
        this.kcClientSecret = kcClientSecret;
    }

    public String getKcUrl1() {
        return kcUrl1;
    }

    public void setKcUrl1(String kcUrl1) {
        this.kcUrl1 = kcUrl1;
    }

    public String getKcBaseUrl() {
        return kcBaseUrl;
    }

    public void setKcBaseUrl(String kcBaseUrl) {
        this.kcBaseUrl = kcBaseUrl;
    }

    public String getKcRealm() {
        return kcRealm;
    }

    public void setKcRealm(String kcRealm) {
        this.kcRealm = kcRealm;
    }

    public static class Liquibase {

        private Boolean asyncStart;

        public Boolean getAsyncStart() {
            return asyncStart;
        }

        public void setAsyncStart(Boolean asyncStart) {
            this.asyncStart = asyncStart;
        }
    }
    // jhipster-needle-application-properties-property-class
}
