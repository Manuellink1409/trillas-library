package com.hypermedia.library.auth;

import com.hypermedia.library.email.EmailService;
import com.hypermedia.library.email.EmailTemplateName;
import com.hypermedia.library.role.RoleRepository;
import com.hypermedia.library.security.JwtService;
import com.hypermedia.library.user.Token;
import com.hypermedia.library.user.TokenRepository;
import com.hypermedia.library.user.User;
import com.hypermedia.library.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final JwtService jwtService;

    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;

    private final AuthenticationManager authenticationManager;

    public void register(RegistrationRequest request) throws MessagingException {
        var userRole = roleRepository.findByName("USER").orElseThrow();
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .lastname(request.getLastname())
                .firstname(request.getFirstname())
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();

        userRepository.save(user);
        sendValidationEmail(user);

    }

    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(user.getEmail(), user.fullname(), EmailTemplateName.ACTIVATE_ACCOUNT, activationUrl, newToken, "Account Activation");
    }

    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .token(generatedToken)
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private String generateActivationCode(int lenght) {
        String characters = "0123456789";
        StringBuilder activationCode = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for( int i = 0; i < lenght; i++) {
            int randomIndex = random.nextInt(characters.length());
            activationCode.append(characters.charAt(randomIndex));
        }
        return activationCode.toString();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var auth = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var claims = new HashMap<String,Object>();
        var user = (User)auth.getPrincipal();
        claims.put("fullname", user.fullname());
        var jwtToken = jwtService.generateToken(claims,user);
        return AuthenticationResponse
                .builder()
                .token(jwtToken)
                .build();
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        var tokenValid = tokenRepository.findByToken(token).orElseThrow();
        if (LocalDateTime.now().isAfter(tokenValid.getExpiresAt())) {
            sendValidationEmail(tokenValid.getUser());
            throw new RuntimeException("Activation token expired");
        }
        var user = userRepository.findById(tokenValid.getUser().getId()).orElseThrow();
        user.setEnabled(true);
        userRepository.save(user);
        tokenValid.setValidatedAt(LocalDateTime.now());
        tokenRepository.save(tokenValid);
    }
}
