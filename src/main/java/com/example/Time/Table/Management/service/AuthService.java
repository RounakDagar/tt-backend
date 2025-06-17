package com.example.Time.Table.Management.service;

import com.example.Time.Table.Management.Model.Faculty;
import com.example.Time.Table.Management.Model.Student;
import com.example.Time.Table.Management.Model.User;
import com.example.Time.Table.Management.Model.UserRole;
import com.example.Time.Table.Management.Repo.FacultyRepository;
import com.example.Time.Table.Management.Repo.StudentRepository;
import com.example.Time.Table.Management.Repo.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final EmailService emailService;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    FacultyRepository facultyRepository;

    // Temporary storage for unverified users: storing the complete User object, keyed by email.
    private final ConcurrentHashMap<String, User> tempUserStorage = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       OtpService otpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public String registerUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        // Check if user already exists
        if (existingUser.isPresent()) {
            if (existingUser.get().isVerified()) {
                return "Email already registered and verified! Please log in.";
            } else {
                return "Email already registered but not verified! Check your email for OTP.";
            }
        }

        // Encode password and store user in temporary storage
        System.out.println("Password received: " + user.getEmail());  // Add this


        user.setPassword(passwordEncoder.encode(user.getPassword()));
        tempUserStorage.put(user.getEmail(), user);

        // Generate and send OTP
        String otp = otpService.generateOtp(user.getEmail());
        emailService.sendOtpEmail(user.getEmail(), otp);

        return "User registered successfully! Check email for OTP.";
    }


    public String verifyOtp(String email, String otp) {
        if (otpService.validateOtp(email, otp)) {
            if (tempUserStorage.containsKey(email)) {

                // Check if user is already verified and saved
                Optional<User> existingUser = userRepository.findByEmail(email);
                if (existingUser.isPresent()) {
                    tempUserStorage.remove(email);
                    otpService.removeOtp(email);
                    return "User already verified and registered.";
                }

                User newUser = tempUserStorage.get(email);
                newUser.setVerified(true);

                try {
                    // Save based on role
                    if (newUser.getRole() == UserRole.STUDENT) {
                        Student student = new Student(newUser, newUser.getEmail());
                        studentRepository.save(student);  // saves both student & user tables

                    } else if (newUser.getRole() == UserRole.FACULTY) {
                        Faculty faculty = new Faculty(newUser, newUser.getEmail());
                        facultyRepository.save(faculty);  // saves both faculty & user tables

                    } else {
                        return "Unsupported role!";
                    }

                } catch (DataIntegrityViolationException e) {
                    tempUserStorage.remove(email);
                    otpService.removeOtp(email);
                    return "User already exists in the system.";
                }

                tempUserStorage.remove(email);
                otpService.removeOtp(email);

                return "OTP verified! Registration complete.";
            }

            return "OTP valid, but user data not found. Please register again.";
        }

        return "Invalid OTP!";
    }


    public User loginUser(String email, String password, HttpSession session) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();


            if (passwordEncoder.matches(password, user.getPassword())) {
                return user;
            }
        }

        return null;
    }




    public String verifyLoginOtp(String email, String otp, String password) {
        if (otpService.validateOtp(email, otp)) {
            User user = userRepository.findByEmail(email).orElseThrow(()->new RuntimeException("No User Found"));
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            otpService.removeOtp(email);
            return "Password Changed successful!";
        }
        return "Invalid OTP!";
    }

    public String getOtp(String email) {
        String otp = otpService.generateOtp(email);
        emailService.sendOtpEmail(email, otp);
        return "Otp Sent Successfully";

    }
}
