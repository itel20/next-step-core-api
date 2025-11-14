package com.nextstepsenegal.app.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EtudiantTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Etudiant getEtudiantSample1() {
        return new Etudiant()
            .id(1L)
            .nom("nom1")
            .prenom("prenom1")
            .telephone("telephone1")
            .adresse("adresse1")
            .email("email1")
            .typeBac("typeBac1")
            .anneeBac(1)
            .niveauDetudes("niveauDetudes1")
            .universiteSouhaitee("universiteSouhaitee1")
            .specialiteSouhaitee("specialiteSouhaitee1")
            .password("password1")
            .passwordHash("passwordHash1")
            .keycloakId("keycloakId1");
    }

    public static Etudiant getEtudiantSample2() {
        return new Etudiant()
            .id(2L)
            .nom("nom2")
            .prenom("prenom2")
            .telephone("telephone2")
            .adresse("adresse2")
            .email("email2")
            .typeBac("typeBac2")
            .anneeBac(2)
            .niveauDetudes("niveauDetudes2")
            .universiteSouhaitee("universiteSouhaitee2")
            .specialiteSouhaitee("specialiteSouhaitee2")
            .password("password2")
            .passwordHash("passwordHash2")
            .keycloakId("keycloakId2");
    }

    public static Etudiant getEtudiantRandomSampleGenerator() {
        return new Etudiant()
            .id(longCount.incrementAndGet())
            .nom(UUID.randomUUID().toString())
            .prenom(UUID.randomUUID().toString())
            .telephone(UUID.randomUUID().toString())
            .adresse(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .typeBac(UUID.randomUUID().toString())
            .anneeBac(intCount.incrementAndGet())
            .niveauDetudes(UUID.randomUUID().toString())
            .universiteSouhaitee(UUID.randomUUID().toString())
            .specialiteSouhaitee(UUID.randomUUID().toString())
            .password(UUID.randomUUID().toString())
            .passwordHash(UUID.randomUUID().toString())
            .keycloakId(UUID.randomUUID().toString());
    }
}
