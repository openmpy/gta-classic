package com.openmpy.gtaclassic.member.domain.repository;

import com.openmpy.gtaclassic.member.domain.constants.MemberVerificationStatus;
import com.openmpy.gtaclassic.member.domain.entity.MemberVerification;
import java.time.LocalDateTime;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberVerificationRepository extends JpaRepository<MemberVerification, Long> {

    Optional<MemberVerification> findByEmail_Value(final String email);

    boolean existsByEmail_Value(final String email);

    void deleteAllByCreatedAtBefore(LocalDateTime localDateTime);

    void deleteAllByStatus(final MemberVerificationStatus status);
}
