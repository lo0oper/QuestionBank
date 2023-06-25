package com.example.question.bank.controller;

import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.Question;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class QuestionsController {

    @Autowired
    private QuestionsService questionService;

    @GetMapping("/test")
    private <T> Mono<Object> hello() {
        return Mono.subscriberContext()
                .map(context -> context.get(ApplicationConstants.LOGGED_USER))
                .map(e -> (User) e)
                .map(User::getEmail);
    }

    @PostMapping("/add/question")
    public Mono<Question> addQuestion(@RequestBody Question question) {
        return questionService.addQuestion(question);
    }

    @PatchMapping("/update/question/{questionId}")
    public Mono<Question> updateQuestion(@RequestBody Question question, @PathVariable int questionId) {
        return questionService.updateQuestion(question, questionId);
    }

    @DeleteMapping("/delete/question/{questionId}")
    public Mono<Void> deleteQuestion(@PathVariable int questionId) {
        return questionService.deleteQuestion(questionId);
    }

    @GetMapping("/all/questions")
    public Mono<List<Question>> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    @GetMapping("/question/{questionId}")
    public Mono<Question> getQuestion(@PathVariable int questionId) {
        return questionService.getQuestion(questionId);
    }
}
