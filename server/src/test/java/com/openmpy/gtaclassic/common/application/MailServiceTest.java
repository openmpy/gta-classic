package com.openmpy.gtaclassic.common.application;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Disabled
    @DisplayName("[통과] 메일을 전송한다.")
    @Test
    void mail_service_test_01() {
        mailService.sendSimpleMail("test@test.com", "[GTA-Classic] 인증 번호입니다.", "123456");
    }
}