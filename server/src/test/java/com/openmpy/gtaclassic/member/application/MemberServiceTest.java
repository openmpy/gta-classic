package com.openmpy.gtaclassic.member.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.openmpy.gtaclassic.member.domain.constants.MemberVerificationStatus;
import com.openmpy.gtaclassic.member.domain.entity.MemberVerification;
import com.openmpy.gtaclassic.member.domain.repository.MemberVerificationRepository;
import com.openmpy.gtaclassic.member.dto.request.MemberCheckVerificationCodeRequest;
import com.openmpy.gtaclassic.member.dto.request.MemberSendVerificationCodeRequest;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberVerificationRepository memberVerificationRepository;

    @DisplayName("[통과] 이메일의 인증 번호가 생성된다.")
    @Test
    void member_service_test_01() {
        // given
        final MemberSendVerificationCodeRequest request = new MemberSendVerificationCodeRequest("test@naver.com");

        // when
        memberService.sendVerificationCode(request);

        // then
        final MemberVerification memberVerification =
                memberVerificationRepository.findByEmail_Value("test@naver.com").get();

        assertThat(memberVerification.getEmail()).isEqualTo("test@naver.com");
        assertThat(memberVerification.getVerificationCode()).isBetween(100000L, 999999L);
        assertThat(memberVerification.getStatus()).isEqualTo(MemberVerificationStatus.PENDING);
    }

    @DisplayName("[통과] 생성된 지 10분이 지난 인증 번호를 제거한다.")
    @Test
    void member_service_test_02() {
        // given
        final LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);
        final MemberVerification memberVerification = MemberVerification.builder()
                .email("test@naver.com")
                .verificationCode(123456L)
                .status(MemberVerificationStatus.PENDING)
                .createdAt(tenMinutesAgo)
                .build();

        memberVerificationRepository.save(memberVerification);

        // when
        memberService.deleteVerificationCode();

        // then
        final long count = memberVerificationRepository.count();

        assertThat(count).isZero();
    }

    @DisplayName("[통과] 인증 번호가 일치한다.")
    @Test
    void member_service_test_03() {
        // given
        final MemberVerification memberVerification = MemberVerification.builder()
                .email("test@naver.com")
                .verificationCode(123456L)
                .status(MemberVerificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        memberVerificationRepository.save(memberVerification);

        // when
        final MemberCheckVerificationCodeRequest request =
                new MemberCheckVerificationCodeRequest("test@naver.com", 123456L);

        memberService.checkVerificationCode(request);

        // then
        final MemberVerification foundMemberVerification =
                memberVerificationRepository.findByEmail_Value("test@naver.com").get();

        assertThat(foundMemberVerification.getStatus()).isEqualTo(MemberVerificationStatus.APPROVAL);
    }

    @DisplayName("[통과] APPROVE 상태의 인증 번호를 제거한다.")
    @Test
    void member_service_test_04() {
        // given
        final MemberVerification memberVerification = MemberVerification.builder()
                .email("test@naver.com")
                .verificationCode(123456L)
                .status(MemberVerificationStatus.APPROVAL)
                .createdAt(LocalDateTime.now())
                .build();

        memberVerificationRepository.save(memberVerification);

        // when
        memberService.deleteApproveVerificationCode();

        // then
        final long count = memberVerificationRepository.count();

        assertThat(count).isZero();
    }

    @DisplayName("[예외] 인증 번호가 이미 전송된 이메일이다.")
    @Test
    void 예외_member_service_test_01() {
        // given
        final MemberVerification memberVerification = MemberVerification.create("test@naver.com");
        memberVerificationRepository.save(memberVerification);

        // when & then
        final MemberSendVerificationCodeRequest request = new MemberSendVerificationCodeRequest("test@naver.com");

        assertThatThrownBy(() -> memberService.sendVerificationCode(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 이메일로는 이미 인증 번호가 전송되었습니다.");
    }

    @DisplayName("[예외] 인증 번호가 전송되지 않은 이메일이다.")
    @Test
    void 예외_member_service_test_02() {
        // when & then
        final MemberCheckVerificationCodeRequest request =
                new MemberCheckVerificationCodeRequest("test@naver.com", 123456L);

        assertThatThrownBy(() -> memberService.checkVerificationCode(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증 번호가 전송되지 않은 이메일입니다.");
    }

    @DisplayName("[예외] 인증 번호가 일치하지 않는다.")
    @Test
    void 예외_member_service_test_03() {
        // given
        final MemberVerification memberVerification = MemberVerification.builder()
                .email("test@naver.com")
                .verificationCode(123456L)
                .status(MemberVerificationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        memberVerificationRepository.save(memberVerification);

        // when & then
        final MemberCheckVerificationCodeRequest request =
                new MemberCheckVerificationCodeRequest("test@naver.com", 654321L);

        assertThatThrownBy(() -> memberService.checkVerificationCode(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("인증 번호가 일치하지 않습니다.");
    }

    @DisplayName("[예외] 더이상 사용할 수 없는 인증 번호다.")
    @Test
    void 예외_member_service_test_04() {
        // given
        final MemberVerification memberVerification = MemberVerification.builder()
                .email("test@naver.com")
                .verificationCode(123456L)
                .status(MemberVerificationStatus.APPROVAL)
                .createdAt(LocalDateTime.now())
                .build();

        memberVerificationRepository.save(memberVerification);

        // when & then
        final MemberCheckVerificationCodeRequest request =
                new MemberCheckVerificationCodeRequest("test@naver.com", 123456L);

        assertThatThrownBy(() -> memberService.checkVerificationCode(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("더이상 사용할 수 없는 인증 번호입니다.");
    }
}