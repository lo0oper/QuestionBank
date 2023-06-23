package com.example.question.bank.service;

import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.authentication.AuthenticationRequest;
import com.example.question.bank.domain.authentication.AuthenticationResponse;
import com.example.question.bank.domain.authentication.RegisterRequest;
import com.example.question.bank.domain.user.Role;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.exception.QuestionBankException;
import com.example.question.bank.repository.UserRepository;
import com.example.question.bank.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    private final QuestionBankException questionBankException;

    public Mono<AuthenticationResponse> register(RegisterRequest request) {
        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()))
                .role(Role.USER)
                .build();

        return Mono.just(user)
                .flatMap(user1 -> userRepository.save(user))
                .map(jwtService::generateToken)
                .map(jwtToken -> AuthenticationResponse.builder().token(jwtToken).build())
                .onErrorResume(error -> {
                    if (error.toString().contains("11000")) {
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email already exists"));
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "an unknown error has occurred"));
                });

    }


    public Mono<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(questionBankException.AuthorizationException(HttpStatus.UNAUTHORIZED, ApplicationConstants.AUTHENTICATION_ERROR_MESSAGE)))
                .flatMap(user -> {
                    boolean passwordMatches = BCrypt.checkpw(request.getPassword(), user.getPassword());
                    if (!passwordMatches) {
                        return Mono.error(questionBankException.AuthorizationException(HttpStatus.UNAUTHORIZED, ApplicationConstants.AUTHENTICATION_ERROR_MESSAGE));
                    }
                    return Mono.just(user);
                })
                .map(jwtService::generateToken)
                .map(jwtToken -> AuthenticationResponse.builder().token(jwtToken).build());
    }

    public Mono<List<User>> getAllUsers() {
        return userRepository.findAll().collectList();
    }
}
