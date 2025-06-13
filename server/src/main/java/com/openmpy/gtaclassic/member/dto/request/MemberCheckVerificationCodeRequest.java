package com.openmpy.gtaclassic.member.dto.request;

public record MemberCheckVerificationCodeRequest(String email, Long verificationCode) {
}
