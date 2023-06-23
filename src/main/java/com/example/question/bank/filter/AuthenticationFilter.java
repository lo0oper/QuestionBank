package com.example.question.bank.filter;

import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.repository.UserRepository;
import com.example.question.bank.service.JwtService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor

public class AuthenticationFilter implements WebFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, @NonNull WebFilterChain webFilterChain) {
        String path = serverWebExchange.getRequest().getPath().value();

        if (serverWebExchange.getRequest().getPath().toString().equals("/api/v1/auth/register") || serverWebExchange.getRequest().getPath().toString().equals("/api/v1/auth/authenticate")) {
            return webFilterChain.filter(serverWebExchange);
        }
        final String authHeader = serverWebExchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return serverWebExchange.getResponse().setComplete();
        }
        final String jwt = authHeader.substring(7);
        final String userEmail = jwtService.extractUsername(jwt);

        return isUserAuthenticated(userEmail, jwt)
                .flatMap(isAuthenticated -> {
                    if (isAuthenticated) {
                        return userRepository.findByEmail(userEmail)
                                .flatMap(user -> webFilterChain.filter(serverWebExchange)
                                        .subscriberContext(context -> context.put(ApplicationConstants.LOGGED_USER, user)));
                    } else {
                        serverWebExchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                        return serverWebExchange.getResponse().setComplete();
                    }
                });

    }

    private Mono<Boolean> isUserAuthenticated(String userEmail, String jwt) {
        return userRepository.findByEmail(userEmail)
                .switchIfEmpty(Mono.just(User.builder().build()))
                .flatMap(user -> Mono.just(jwtService.isTokenValid(jwt, user)));
    }
}
