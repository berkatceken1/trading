package com.berk.service;

import com.berk.model.TwoFactorOTP;
import com.berk.model.User;
import com.berk.repository.TwoFactorOtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TwoFactoOtpServiceImpl implements TwoFactorOtpService {

    @Autowired
    private TwoFactorOtpRepository twoFactorOtpRepository;

    @Override
    public TwoFactorOTP createTwoFactorOtp(User user, String otp, String jwt) {
        // Benzersiz bir kimlik (UUID) oluştur
        UUID uuid = UUID.randomUUID();

        // OTP için gerekli bilgileri ayarla ve kaydet
        String id = uuid.toString();
        TwoFactorOTP twoFactorOtp = new TwoFactorOTP();
        twoFactorOtp.setId(id);
        twoFactorOtp.setOtp(otp);
        twoFactorOtp.setUser(user);
        twoFactorOtp.setJwt(jwt);

        // OTP'yi veritabanına kaydet ve döndür
        return twoFactorOtpRepository.save(twoFactorOtp);
    }

    @Override
    public TwoFactorOTP findByUser(Long userId) {
        // Kullanıcı kimliği ile OTP'yi bul ve döndür
        return twoFactorOtpRepository.findByUserId(userId);
    }

    @Override
    public TwoFactorOTP findById(String id) {
        // OTP kimliği ile veritabanında arama yap
        Optional<TwoFactorOTP> twoFactorOtp = twoFactorOtpRepository.findById(id);

        // OTP bulunamazsa null döner
        return twoFactorOtp.orElse(null);
    }

    @Override
    public boolean verifyOtp(TwoFactorOTP twoFactorOtp, String otp) {
        // Girilen OTP ile kaydedilen OTP'yi karşılaştır
        return twoFactorOtp.getOtp().equals(otp);
    }

    @Override
    public void deleteTwoFactorOtp(TwoFactorOTP twoFactorOtp) {
        // Veritabanından OTP'yi sil
        twoFactorOtpRepository.delete(twoFactorOtp);
    }
}
