package com.nextstepsenegal.app;

import com.nextstepsenegal.app.config.AsyncSyncConfiguration;
import com.nextstepsenegal.app.config.EmbeddedElasticsearch;
import com.nextstepsenegal.app.config.EmbeddedKafka;
import com.nextstepsenegal.app.config.EmbeddedSQL;
import com.nextstepsenegal.app.config.JacksonConfiguration;
import com.nextstepsenegal.app.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(
    classes = { UserManagementServiceApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class }
)
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
