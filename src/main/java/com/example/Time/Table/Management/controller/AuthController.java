package com.example.Time.Table.Management.controller;

import com.example.Time.Table.Management.Config.JwtUtil;
import com.example.Time.Table.Management.Repo.UserRepository;
import com.example.Time.Table.Management.dto.LoginDTO;
import com.example.Time.Table.Management.dto.OtpDTO;
import com.example.Time.Table.Management.Model.User;
import com.example.Time.Table.Management.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody User userDTO) {

        String response = authService.registerUser(userDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-signup")
    public ResponseEntity<?> verifyOtp(@RequestBody OtpDTO otpDTO) {
        String email = otpDTO.getEmail();
        String otp = otpDTO.getOtp();

        String response = authService.verifyOtp(email, otp);

        // If OTP verification was successful
        if (response.equals("OTP verified! Registration complete.")) {

            Optional<User> userOptional = userRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String token = jwtUtil.generateToken(user);

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("message", response);
                responseBody.put("role", user.getRole());
                responseBody.put("name", user.getName());
                responseBody.put("token", token);

                return ResponseEntity.ok(responseBody);
            }
        }

        // If OTP was invalid or user doesn't exist
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", response));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, HttpSession session) {
        User user = authService.loginUser(loginDTO.getEmail(), loginDTO.getPassword(), session);

        if (user != null) {
            String token = jwtUtil.generateToken(user);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Successfully Logged In");
            response.put("role", user.getRole());
            response.put("name", user.getName());
            response.put("token", token);
            response.put("userId", user.getEmail());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid credentials"));
        }
    }


    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        session.invalidate();  // ✅ Clears the session
        return ResponseEntity.ok("Logged out successfully.");
    }

    @GetMapping("/user")
    public ResponseEntity<User> getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<String> verifyLoginOtp(@RequestBody OtpDTO otpDTO) {
        String response = authService.verifyLoginOtp(otpDTO.getEmail(), otpDTO.getOtp(),otpDTO.getPassword());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getOtp")
    public String getOTP(@RequestParam String email){
        return authService.getOtp(email);
    }
}
