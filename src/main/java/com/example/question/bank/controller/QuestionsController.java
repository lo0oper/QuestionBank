package com.example.question.bank.controller;

import com.example.question.bank.connector.ChatGptConnector;
import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.chatgpt.ChatGptRequest;
import com.example.question.bank.domain.chatgpt.ChatGptResponse;
import com.example.question.bank.domain.chatgpt.Message;
import com.example.question.bank.domain.question.Question;
import com.example.question.bank.domain.question.QuestionRequest;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.repository.QuestionRepository;
import com.example.question.bank.service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@CrossOrigin("*")
@RestController
public class QuestionsController {

    @Autowired
    private QuestionsService questionService;
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ChatGptConnector chatGptConnector;

    @GetMapping("/test")
    private <T> Mono<Object> hello() {
        return Mono.subscriberContext()
                .map(context -> context.get(ApplicationConstants.LOGGED_USER))
                .map(e -> (User) e)
                .map(User::getEmail);
    }

    @PostMapping("/add/question")
    public Mono<Question> addQuestion(@RequestBody QuestionRequest questionRequest) {
        return questionService.addQuestion(questionRequest);
    }

    @PatchMapping("/update/question")
    public Mono<Question> updateQuestion(@RequestBody QuestionRequest questionRequest) {
        return questionService.updateQuestion(questionRequest);
    }

    @DeleteMapping("/delete/question/{questionId}")
    public Mono<Void> deleteQuestion(@PathVariable String questionId) {
        return questionService.deleteQuestion(questionId);
    }

    @PostMapping("/all/questions")
    public Mono<List<Question>> getAllQuestions(@RequestBody QuestionRequest questionRequest) {
        return questionService.getAllQuestions(questionRequest);
    }

    @GetMapping("/question/{questionId}")
    public Mono<Question> getQuestion(@PathVariable String questionId) {
        return questionService.getQuestion(questionId);
    }

    @PostMapping("/joshi")
    public Mono<ChatGptResponse> joshi () {
        ChatGptRequest chatGptRequest = ChatGptRequest.builder().messages(Arrays.asList(Message.builder().content("Say Hello").build())).build();
        return chatGptConnector.fetchChatGptResponse(chatGptRequest);
    }

}
