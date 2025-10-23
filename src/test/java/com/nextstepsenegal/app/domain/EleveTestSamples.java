package com.nextstepsenegal.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class EleveTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Eleve getEleveSample1() {
        return new Eleve()
            .id(1L)
            .nom("nom1")
            .prenom("prenom1")
            .telephone("telephone1")
            .adresse("adresse1")
            .email("email1")
            .serie("serie1")
            .niveauEtude("niveauEtude1")
            .lycee("lycee1")
            .ville("ville1")
            .password("password1")
            .passwordHash("passwordHash1")
            .keycloakId("keycloakId1");
    }

    public static Eleve getEleveSample2() {
        return new Eleve()
            .id(2L)
            .nom("nom2")
            .prenom("prenom2")
            .telephone("telephone2")
            .adresse("adresse2")
            .email("email2")
            .serie("serie2")
            .niveauEtude("niveauEtude2")
            .lycee("lycee2")
            .ville("ville2")
            .password("password2")
            .passwordHash("passwordHash2")
            .keycloakId("keycloakId2");
    }

    public static Eleve getEleveRandomSampleGenerator() {
        return new Eleve()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .prenom(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString())
            .adresse(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .serie(UUID.randomUUID().toString())
            .niveauEtude(UUID.randomUUID().toString())
            .lycee(UUID.randomUUID().toString())
            .ville(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .passwordHash(UUID.randomUUID().toString())
            .keycloakId(UUID.randomUUID().toString());
    }
}
