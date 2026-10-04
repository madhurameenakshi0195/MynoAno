package com.mynoano.service;

import com.mynoano.dto.Dtos.*;
import com.mynoano.entity.*;
import com.mynoano.exception.ApiException;
import com.mynoano.repository.*;
import com.mynoano.util.Geo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository users;
    private final ProfileRepository profiles;
    private final OtpCodeRepository otps;
    private final PasswordEncoder encoder;
    private final com.mynoano.security.JwtService jwt;
    private final OtpSender sender;

    @org.springframework.beans.factory.annotation.Value("${app.otp.dev-fixed-code:}")
    private String fixedCode;

    /** One account per email and per phone number. */
    @Transactional
    public void register(RegisterRequest r) {
        String email = r.email().trim().toLowerCase(Locale.ROOT);
        String phone = r.phone().replaceAll("[^0-9+]", "");
        if (users.existsByEmail(email) || users.existsByPhone(phone))
            throw ApiException.conflict("An account already exists with this email or phone");
        String campus = email.matches(".+@.+\\.(edu|ac\\.in)$") ? email.substring(email.indexOf('@') + 1) : null;
        User u = users.save(User.builder().email(email).phone(phone).passwordHash(encoder.encode(r.password())).campus(campus).build());
        issueOtp(u);
    }

    @Transactional
    public void resend(String email) {
        User u = users.findByEmail(email.trim().toLowerCase(Locale.ROOT)).orElseThrow(() -> ApiException.notFound("No account found"));
        if (u.isVerified()) throw ApiException.bad("Already verified");
        issueOtp(u);
    }

    private void issueOtp(User u) {
        String code = fixedCode == null || fixedCode.isBlank() ? String.format("%06d", new java.security.SecureRandom().nextInt(1_000_000)) : fixedCode;
        otps.deleteByUserId(u.getId());
        otps.save(OtpCode.builder().userId(u.getId()).codeHash(encoder.encode(code)).expiresAt(Instant.now().plus(10, ChronoUnit.MINUTES)).build());
        sender.send(u.getEmail(), u.getPhone(), code);
    }

    @Transactional(noRollbackFor = ApiException.class)
    public AuthResponse verify(VerifyRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase(Locale.ROOT)).orElseThrow(() -> ApiException.bad("Invalid or expired code"));
        OtpCode o = otps.findTopByUserIdOrderByIdDesc(u.getId()).orElseThrow(() -> ApiException.bad("Invalid or expired code"));
        if (o.getExpiresAt().isBefore(Instant.now()) || o.getAttempts() >= 5) throw ApiException.bad("Invalid or expired code");
        if (!encoder.matches(r.code(), o.getCodeHash())) {
            o.setAttempts(o.getAttempts() + 1);
            otps.save(o);
            throw ApiException.bad("Invalid or expired code");
        }
        u.setVerified(true);
        u.setCampusVerified(u.getCampus() != null);
        users.save(u);
        otps.deleteByUserId(u.getId());
        return new AuthResponse(jwt.create(u.getId()), u.getId(), profiles.existsById(u.getId()));
    }

    public AuthResponse login(LoginRequest r) {
        User u = users.findByEmail(r.email().trim().toLowerCase(Locale.ROOT))
                .filter(x -> encoder.matches(r.password(), x.getPasswordHash()))
                .orElseThrow(() -> ApiException.unauthorized("Invalid email or password"));
        if (!u.isVerified()) throw ApiException.forbidden("Verify your email and phone first");
        return new AuthResponse(jwt.create(u.getId()), u.getId(), profiles.existsById(u.getId()));
    }
}
