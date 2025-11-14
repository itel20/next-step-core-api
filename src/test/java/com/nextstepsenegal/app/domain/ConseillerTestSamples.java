package com.nextstepsenegal.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ConseillerTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Conseiller getConseillerSample1() {
        return new Conseiller()
            .id(1L)
            .nom("nom1")
            .prenom("prenom1")
            .specialite("specialite1")
            .email("email1")
            .description("description1")
            .password("password1")
            .passwordHash("passwordHash1")
            .keycloakId("keycloakId1");
    }

    public static Conseiller getConseillerSample2() {
        return new Conseiller()
            .id(2L)
            .nom("nom2")
            .prenom("prenom2")
            .specialite("specialite2")
            .email("email2")
            .description("description2")
            .password("password2")
            .passwordHash("passwordHash2")
            .keycloakId("keycloakId2");
    }

    public static Conseiller getConseillerRandomSampleGenerator() {
        return new Conseiller()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .prenom(UUID.randomUUID().toString())
            .specialite(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .passwordHash(UUID.randomUUID().toString())
            .keycloakId(UUID.randomUUID().toString());
    }
}
