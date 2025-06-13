package com.openmpy.gtaclassic.member.application;

import com.openmpy.gtaclassic.common.application.MailService;
import com.openmpy.gtaclassic.member.domain.constants.MemberVerificationStatus;
import com.openmpy.gtaclassic.member.domain.entity.MemberVerification;
import com.openmpy.gtaclassic.member.domain.repository.MemberVerificationRepository;
import com.openmpy.gtaclassic.member.dto.request.MemberCheckVerificationCodeRequest;
import com.openmpy.gtaclassic.member.dto.request.MemberSendVerificationCodeRequest;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MailService mailService;
    private final MemberVerificationRepository memberVerificationRepository;

    @Transactional
    public void sendVerificationCode(final MemberSendVerificationCodeRequest request) {
        final String email = request.email();

        if (memberVerificationRepository.existsByEmail_Value(email)) {
            throw new IllegalArgumentException("해당 이메일로는 이미 인증 번호가 전송되었습니다.");
        }

        final MemberVerification memberVerification = MemberVerification.create(email);
        memberVerificationRepository.save(memberVerification);

        // 이메일 전송
        final String verificationCode = memberVerification.getVerificationCode().toString();
        mailService.sendSimpleMail(email, "[GTA-Classic] 인증 번호입니다.", verificationCode);
    }

    @Transactional
    public void checkVerificationCode(final MemberCheckVerificationCodeRequest request) {
        final MemberVerification memberVerification = memberVerificationRepository.findByEmail_Value(request.email())
                .orElseThrow(() -> new IllegalArgumentException("인증 번호가 전송되지 않은 이메일입니다."));

        if (!memberVerification.getVerificationCode().equals(request.verificationCode())) {
            throw new IllegalArgumentException("인증 번호가 일치하지 않습니다.");
        }
        if (!memberVerification.getStatus().equals(MemberVerificationStatus.PENDING)) {
            throw new IllegalArgumentException("더이상 사용할 수 없는 인증 번호입니다.");
        }

        memberVerification.updateStatus(MemberVerificationStatus.APPROVAL);
    }

    @Scheduled(fixedRate = 60 * 1000)
    @Transactional
    public void deleteVerificationCode() {
        final LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        memberVerificationRepository.deleteAllByCreatedAtBefore(tenMinutesAgo);
    }

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void deleteApproveVerificationCode() {
        memberVerificationRepository.deleteAllByStatus(MemberVerificationStatus.APPROVAL);
    }
}
