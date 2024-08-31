package com.berk.controller;

import com.berk.config.JwtProvider;
import com.berk.model.TwoFactorOTP;
import com.berk.model.User;
import com.berk.repository.UserRepository;
import com.berk.response.AuthResponse;
import com.berk.service.CustomUserDetailsService;
import com.berk.service.EmailService;
import com.berk.service.TwoFactorOtpService;
import com.berk.utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> register(@RequestBody User user) throws Exception {

        User isEmailExist = userRepository.findByEmail(user.getEmail());

        if (isEmailExist != null) {
            throw new Exception("Email already exist");
        }

        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setPassword(user.getPassword());
        newUser.setFullName(user.getFullName());

        User savedUser = userRepository.save(newUser);

        // Kullanıcı doğrulamasını (authentication) oluşturuyoruz.
        Authentication auth = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        // Kullanıcıyı güvenlik bağlamına (context) ekliyoruz.
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        AuthResponse authResponse = new AuthResponse();

        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("User registered successfully");




        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> login(@RequestBody User user) throws Exception {

        String email = user.getEmail();
        String password = user.getPassword();


        // Kullanıcı doğrulamasını (authentication) oluşturuyoruz.
        Authentication auth = authenticate(email, password);

        // Kullanıcıyı güvenlik bağlamına (context) ekliyoruz.
        SecurityContextHolder.getContext().setAuthentication(auth);

        String jwt = JwtProvider.generateToken(auth);

        User authUser = userRepository.findByEmail(email);

        if (user.getTwoFactorAuth().isEnabled()) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setMessage("Two factor authentication is enabled");
            authResponse.setTwoFactorEnabled(true);

            String otp = OtpUtils.generateOtp();

            TwoFactorOTP oldTwoFactorOTP = twoFactorOtpService.findByUser(authUser.getId()); // OTP'yi veritabanında ara
            if (oldTwoFactorOTP != null) {
                twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP); // Eski OTP'yi sil
            }

            TwoFactorOTP newTwoFactorOTP = twoFactorOtpService.createTwoFactorOtp(authUser, otp, jwt); // Yeni OTP oluştur

            emailService.sendVerificationOtpEmail(email, otp); // OTP'yi e-posta ile gönder

            authResponse.setSession(newTwoFactorOTP.getId()); // OTP kimliğini yanıtta gönder
            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);







        }

        AuthResponse authResponse = new AuthResponse();

        authResponse.setJwt(jwt);
        authResponse.setStatus(true);
        authResponse.setMessage("Login successfully");


        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    private Authentication authenticate(String email, String password) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid email");
        }

        if (!password.equals(userDetails.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @GetMapping("/two-factor-auth/verify-otp/{otp}")
    public ResponseEntity<AuthResponse> verifySignInOtp(@PathVariable String otp, @RequestParam String id) {

        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findById(id); // OTP

        if (twoFactorOtpService.verifyOtp(twoFactorOTP, otp)) {
            AuthResponse authResponse = new AuthResponse();
            authResponse.setJwt(twoFactorOTP.getJwt());
            authResponse.setTwoFactorEnabled(true);
            authResponse.setMessage("Two factor authentication verified");

            twoFactorOtpService.deleteTwoFactorOtp(twoFactorOTP); // OTP'yi sil

            return new ResponseEntity<>(authResponse, HttpStatus.CREATED);

        }

        return null;
    }
}
