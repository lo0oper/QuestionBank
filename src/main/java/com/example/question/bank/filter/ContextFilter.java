//package com.example.question.bank.filter;
//
//import com.example.question.bank.constants.ApplicationConstants;
//import com.example.question.bank.repository.UserRepository;
//import com.example.question.bank.service.JwtService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.web.server.ServerWebExchange;
//import org.springframework.web.server.WebFilter;
//import org.springframework.web.server.WebFilterChain;
//import reactor.core.publisher.Mono;
//
//
//@Configuration
//public class ContextFilter implements WebFilter {
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
//
//
//
//        final String accessToken = exchange.getRequest().getHeaders().getFirst("x-access-token");
//        if (accessToken == null || accessToken.isEmpty()) {
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        }
//        String userEmail = jwtService.extractUsername(accessToken);
//
//        userRepository.findByEmail(userEmail)
//                .subscribe(e -> System.out.println("this is the user " + e));
//
//        return userRepository.findByEmail(userEmail)
//                .flatMap(user -> chain.filter(exchange)
//                        .subscriberContext(context -> context.put(ApplicationConstants.LOGGED_USER, user)));
//
//    }
//
//
//}
