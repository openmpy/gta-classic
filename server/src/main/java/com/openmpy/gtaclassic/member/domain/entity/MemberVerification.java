package com.openmpy.gtaclassic.member.domain.entity;

import com.openmpy.gtaclassic.member.domain.MemberEmail;
import com.openmpy.gtaclassic.member.domain.MemberVerificationCode;
import com.openmpy.gtaclassic.member.domain.constants.MemberVerificationStatus;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class MemberVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", unique = true, nullable = false))
    private MemberEmail email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "verification_code", nullable = false))
    private MemberVerificationCode verificationCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MemberVerificationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Builder
    public MemberVerification(
            final String email,
            final Long verificationCode,
            final MemberVerificationStatus status,
            final LocalDateTime createdAt
    ) {
        this.email = new MemberEmail(email);
        this.verificationCode = new MemberVerificationCode(verificationCode);
        this.status = status;
        this.createdAt = createdAt;
    }

    public static MemberVerification create(final String email) {
        final long verificationCode = MemberVerificationCode.generate();

        return MemberVerification.builder()
                .email(email)
                .verificationCode(verificationCode)
                .status(MemberVerificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateStatus(final MemberVerificationStatus status) {
        this.status = status;
    }

    public String getEmail() {
        return email.getValue();
    }

    public Long getVerificationCode() {
        return verificationCode.getValue();
    }
}
