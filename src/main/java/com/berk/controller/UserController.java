package com.berk.controller;


import com.berk.request.ForgotPasswordTokenRequest;
import com.berk.domain.VerificationType;
import com.berk.model.ForgotPasswordToken;
import com.berk.model.User;
import com.berk.model.VerificationCode;
import com.berk.request.ResetPasswordRequest;
import com.berk.response.ApiResponse;
import com.berk.response.AuthResponse;
import com.berk.service.EmailService;
import com.berk.service.ForgotPasswordService;
import com.berk.service.UserService;
import com.berk.service.VerificationCodeService;
import com.berk.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;
    private String jwtToken;

    @GetMapping("/api/users/profile")
    public ResponseEntity<User> getUserProfile(@RequestHeader("Authorization") String jwtToken) throws Exception {
        try {
            User user = userService.findUserByJwtToken(jwtToken);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/api/users/verification/{verificationType}/send-otp")
    public ResponseEntity<String> sendVerificationOtp(@RequestHeader("Authorization") String jwtToken, @PathVariable VerificationType verificationType) throws Exception {
        try {
            User user = userService.findUserByJwtToken(jwtToken);

            VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

            if (verificationCode == null) {
                verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
            }

            if (verificationType.equals(verificationType)) {
                emailService.sendVerificationOtpEmail(user.getEmail(), verificationCode.getOtp());
            }



            return ResponseEntity.ok("Verification code sent successfully");
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }


    @PatchMapping("/api/users/enable-two-factor-auth/verify-otp/{otp}")
    public ResponseEntity<User> enableTwoFactorAuth(
            @PathVariable String otp,
            @RequestHeader("Authorization") String jwtToken) throws Exception {
        try {
            // Kullanıcıyı JWT Token ile bul
            User user = userService.findUserByJwtToken(jwtToken);

            // Kullanıcının doğrulama kodunu bul
            VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUser(user.getId());

            // Doğrulama kodu türüne göre e-posta veya mobil numara belirle
            String sendTo = verificationCode.getVerificationType().equals(VerificationType.EMAIL) ? verificationCode.getEmail() : verificationCode.getMobile();

            // Girilen OTP'nin doğru olup olmadığını kontrol et
            boolean isVerified = verificationCode.getOtp().equals(otp);

            // Eğer doğrulama başarılıysa, iki faktörlü kimlik doğrulamayı etkinleştir ve kullanıcıyı güncelle
            if (isVerified) {
                User updatedUser = userService.enableTwoFactorAuth(verificationCode.getVerificationType(), sendTo, user);

                // Kullanılan doğrulama kodunu sil
                verificationCodeService.deleteVerificationCode(verificationCode);

                // Güncellenen kullanıcı bilgilerini döndür
                return ResponseEntity.ok(updatedUser);
            }

            // Doğrulama başarısız olursa hata fırlat
            throw new Exception("Invalid OTP");

        } catch (Exception e) {
            // Eğer bir hata oluşursa, 401 Unauthorized durumu döndür
            return ResponseEntity.status(401).build();
        }
    }

    @PostMapping("/auth/users/reset-password/send-otp")
    public ResponseEntity<AuthResponse> sendForgotPasswordOtp(

            @RequestBody ForgotPasswordTokenRequest req) throws Exception {
        try {
            User user = userService.findUserByEmail(req.getSendTo());
            String otp = OtpUtils.generateOtp();
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();

            ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findByUser(user.getId());

            if (forgotPasswordToken == null) {
                forgotPasswordToken = forgotPasswordService.createToken(user, id, otp, req.getVerificationType(), req.getSendTo());
            }

            if (req.getVerificationType().equals(VerificationType.EMAIL)) {
                emailService.sendVerificationOtpEmail(
                        user.getEmail(),
                        forgotPasswordToken.getOtp());
            }

            AuthResponse response = new AuthResponse();
            response.setSession(forgotPasswordToken.getId());
            response.setMessage("Password reset otp sent successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
    }

    @PatchMapping("/auth/users/reset-password/verify-otp")
    public ResponseEntity<ApiResponse> resetPassword(
            @RequestParam String id,
            @RequestBody ResetPasswordRequest req,
            @RequestHeader("Authorization") String jwtToken) throws Exception {
        try {

            // id ye göre tokenı bul
            ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findById(id);

            boolean isVerified = forgotPasswordToken.getOtp().equals(req.getOtp());

            if (isVerified) {
                userService.updatePassword(forgotPasswordToken.getUser(), req.getPassword());
                ApiResponse response = new ApiResponse();
                response.setMessage("Password reset successfully");
                return ResponseEntity.accepted().body(response);
            }
            throw new Exception("Invalid OTP");

        } catch (Exception e) {
            // Eğer bir hata oluşursa, 401 Unauthorized durumu döndür
            return ResponseEntity.status(401).build();
        }
    }

}
