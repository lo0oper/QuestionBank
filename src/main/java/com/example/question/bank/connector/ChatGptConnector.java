package com.example.question.bank.connector;

import com.example.question.bank.domain.chatgpt.ChatGptRequest;
import com.example.question.bank.domain.chatgpt.ChatGptResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class ChatGptConnector {

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ChatGptResponse> fetchChatGptResponse(ChatGptRequest chatGptRequest) {
        return webClientBuilder.build().post()
                .uri("https://api.openai.com/v1/chat/completions")
                .headers(httpHeaders -> httpHeaders.add("Authorization", "Bearer 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"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .bodyValue(chatGptRequest)
                .retrieve()
                .bodyToMono(ChatGptResponse.class)
                .doOnError(error -> System.out.println("error is" + error))
                .doOnSuccess(e -> System.out.println("success" + e))
                .switchIfEmpty(Mono.just(ChatGptResponse.builder().build()))
                .onErrorResume(error -> Mono.just(ChatGptResponse.builder().build()));
    }
}
