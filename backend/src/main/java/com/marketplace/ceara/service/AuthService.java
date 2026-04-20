package com.marketplace.ceara.service;

import com.marketplace.ceara.dto.auth.*;
import com.marketplace.ceara.model.ProviderProfile;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.Role;
import com.marketplace.ceara.repository.ProviderProfileRepository;
import com.marketplace.ceara.repository.UserRepository;
import com.marketplace.ceara.security.JwtTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Serviço central de autenticação e registro de usuários.
 *
 * Regras de negócio:
 *  - E-mail e CPF devem ser únicos no sistema.
 *  - Senhas são codificadas com BCrypt antes da persistência.
 *  - O registro de prestador é @Transactional: User + ProviderProfile são
 *    salvos atomicamente. O BackgroundCheck é disparado APÓS o commit.
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final ProviderProfileRepository providerProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;
    private final AuthenticationManager authenticationManager;
    private final BackgroundCheckService backgroundCheckService;

    public AuthService(UserRepository userRepository,
                       ProviderProfileRepository providerProfileRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenService jwtTokenService,
                       AuthenticationManager authenticationManager,
                       BackgroundCheckService backgroundCheckService) {
        this.userRepository = userRepository;
        this.providerProfileRepository = providerProfileRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
        this.authenticationManager = authenticationManager;
        this.backgroundCheckService = backgroundCheckService;
    }

    // -------------------------------------------------------------------------
    // US01 — Cadastro de Cliente
    // -------------------------------------------------------------------------

    @Transactional
    public AuthResponse registerClient(RegisterClientRequest request) {
        validateNewUser(request.email(), request.cpf());

        var user = new User(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.cpf(),
                Role.CLIENT
        );
        userRepository.save(user);
        log.info("Cliente registrado: email={}", user.getEmail());

        String token = jwtTokenService.generateToken(user);
        return AuthResponse.of(token, user.getEmail(), user.getRole().name());
    }

    // -------------------------------------------------------------------------
    // US02 — Cadastro de Prestador (User + ProviderProfile em uma transação)
    // -------------------------------------------------------------------------

    @Transactional
    public AuthResponse registerProvider(RegisterProviderRequest request) {
        validateNewUser(request.email(), request.cpf());

        var user = new User(
                request.fullName(),
                request.email(),
                passwordEncoder.encode(request.password()),
                request.cpf(),
                Role.PROVIDER
        );
        userRepository.save(user);

        var profile = new ProviderProfile(user, request.category(), request.bio(), request.latitude(), request.longitude());
        providerProfileRepository.save(profile);

        log.info("Prestador registrado: email={}, userId={}", user.getEmail(), user.getId());

        // Disparado APÓS o commit da transação — Virtual Thread não bloqueia o caller
        backgroundCheckService.runCheck(user.getId());

        String token = jwtTokenService.generateToken(user);
        return AuthResponse.of(token, user.getEmail(), user.getRole().name());
    }

    // -------------------------------------------------------------------------
    // US01/US02 — Login
    // -------------------------------------------------------------------------

    public AuthResponse login(LoginRequest request) {
        // Lança BadCredentialsException se inválido — Spring Security converte para 401
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado após autenticação"));

        String token = jwtTokenService.generateToken(user);
        return AuthResponse.of(token, user.getEmail(), user.getRole().name());
    }

    // -------------------------------------------------------------------------
    // Validações
    // -------------------------------------------------------------------------

    private void validateNewUser(String email, String cpf) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("E-mail já cadastrado: " + email);
        }
        if (userRepository.existsByCpf(cpf)) {
            throw new IllegalArgumentException("CPF já cadastrado");
        }
    }
}
