package com.mynoano.mynoano.service;

import com.mynoano.mynoano.dto.LoginRequest;
import com.mynoano.mynoano.dto.LoginResponse;
import com.mynoano.mynoano.dto.RegisterRequest;
import com.mynoano.mynoano.entity.User;
import com.mynoano.mynoano.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    // =========================
    // REGISTER
    // =========================

    public void register(RegisterRequest request) {

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Create new user
        User user = new User();

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        // NEVER store plain password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        // Save user
        userRepository.save(user);
    }


    // =========================
    // LOGIN
    // =========================

    public LoginResponse login(LoginRequest request) {

        System.out.println("========== LOGIN DEBUG ==========");
        System.out.println("Email received: [" + request.getEmail() + "]");

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElse(null);

        if (user == null) {
            System.out.println("❌ USER DOES NOT EXIST");
            throw new RuntimeException("Invalid email or password");
        }

        System.out.println("✅ USER FOUND");
        System.out.println("Username: " + user.getUsername());
        System.out.println("User ID: " + user.getId());
        System.out.println("Stored password: " + user.getPassword());

        boolean matches = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        System.out.println("Password matches: " + matches);
        System.out.println("=================================");

        if (!matches) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getUsername()
        );

        return new LoginResponse(
                token,
                user.getId(),
                user.getUsername()
        );
    }
}