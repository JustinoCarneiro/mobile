package com.marketplace.ceara.identity;

import com.marketplace.ceara.dto.auth.LoginRequest;
import com.marketplace.ceara.dto.auth.RegisterClientRequest;
import com.marketplace.ceara.model.User;
import com.marketplace.ceara.model.enums.Role;
import com.marketplace.ceara.repository.UserRepository;
import com.marketplace.ceara.service.AuthService;
import com.marketplace.ceara.security.JwtTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Testes de unidade do AuthService.
 * Usa Mockito — sem Spring context nem banco de dados.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService — Testes de Unidade")
class AuthServiceUnitTest {

    @Mock UserRepository userRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenService jwtTokenService;
    @Mock AuthenticationManager authenticationManager;
    @Mock com.marketplace.ceara.repository.ProviderProfileRepository providerProfileRepository;
    @Mock com.marketplace.ceara.service.BackgroundCheckService backgroundCheckService;

    @InjectMocks AuthService authService;

    private RegisterClientRequest clientRequest;

    @BeforeEach
    void setUp() {
        clientRequest = new RegisterClientRequest(
                "João Silva", "joao@email.com", "senha1234", "12345678901"
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar com e-mail duplicado")
    void shouldThrowWhenEmailAlreadyExists() {
        when(userRepository.existsByEmail("joao@email.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerClient(clientRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("E-mail já cadastrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção ao registrar com CPF duplicado")
    void shouldThrowWhenCpfAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> authService.registerClient(clientRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CPF já cadastrado");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve codificar a senha antes de persistir o usuário")
    void shouldEncodePasswordBeforeSaving() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByCpf(anyString())).thenReturn(false);
        when(passwordEncoder.encode("senha1234")).thenReturn("$2a$10$hashed");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(jwtTokenService.generateToken(any())).thenReturn("jwt-token");

        authService.registerClient(clientRequest);

        verify(passwordEncoder).encode("senha1234");
        verify(userRepository).save(argThat(u -> "$2a$10$hashed".equals(u.getPassword())));
    }

    @Test
    @DisplayName("Login com credenciais corretas deve retornar AuthResponse com token")
    void shouldReturnAuthResponseOnValidLogin() {
        var user = new User("João", "joao@email.com", "$2a$10$hashed", "12345678901", Role.CLIENT);
        when(userRepository.findByEmail("joao@email.com")).thenReturn(Optional.of(user));
        when(jwtTokenService.generateToken(user)).thenReturn("valid-jwt");

        var response = authService.login(new LoginRequest("joao@email.com", "senha1234"));

        assertThat(response.token()).isEqualTo("valid-jwt");
        assertThat(response.role()).isEqualTo("CLIENT");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Login com credenciais incorretas deve propagar BadCredentialsException")
    void shouldPropagateBadCredentialsOnInvalidLogin() {
        doThrow(new BadCredentialsException("Credenciais inválidas"))
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        assertThatThrownBy(() -> authService.login(new LoginRequest("joao@email.com", "errada")))
                .isInstanceOf(BadCredentialsException.class);
    }
}
