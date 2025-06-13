package com.openmpy.gtaclassic.member.domain;

import jakarta.persistence.Embeddable;
import java.util.Random;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class MemberVerificationCode {

    private static final Random random = new Random();

    private long value;

    public MemberVerificationCode(final long value) {
        this.value = value;
    }

    public static long generate() {
        return 100000 + random.nextLong(900000);
    }
}
