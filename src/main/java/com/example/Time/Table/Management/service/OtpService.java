package com.example.Time.Table.Management.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class OtpService {
    private final Map<String, String> otpStorage = new HashMap<>();
    private final Random random = new Random();

    public String generateOtp(String email) {
        String otp = String.valueOf(100000 + random.nextInt(900000)); // Generate 6-digit OTP
        otpStorage.put(email, otp);
        return otp;
    }

    public boolean validateOtp(String email, String enteredOtp) {
        String storedOtp = otpStorage.get(email);
        return storedOtp != null && storedOtp.equals(enteredOtp.trim());
    }


    public void removeOtp(String email) {
        otpStorage.remove(email); // Remove OTP after successful verification
    }
}
