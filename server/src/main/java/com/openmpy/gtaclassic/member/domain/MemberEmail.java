package com.openmpy.gtaclassic.member.domain;

import jakarta.persistence.Embeddable;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Embeddable
public class MemberEmail {

    private static final String VALID_EMAIL_PATTERN = "^[a-zA-Z0-9._%+-]+@naver\\.com$";

    private String value;

    public MemberEmail(final String value) {
        validateBlank(value);
        validateEmail(value);

        this.value = value;
    }

    private void validateBlank(final String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("이메일이 빈 값일 수 없습니다.");
        }
    }

    private void validateEmail(final String value) {
        if (!isValidNaver(value)) {
            throw new IllegalArgumentException("네이버 이메일만 사용 가능합니다.");
        }
    }

    private boolean isValidNaver(final String value) {
        return Pattern.matches(VALID_EMAIL_PATTERN, value);
    }

    public String getValue() {
        return value;
    }
}
