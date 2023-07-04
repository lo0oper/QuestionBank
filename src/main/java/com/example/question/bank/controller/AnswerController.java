package com.example.question.bank.controller;

import com.example.question.bank.domain.Answer;
import com.example.question.bank.domain.Question;
import com.example.question.bank.service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin("*")
public class AnswerController {
    @Autowired
    private QuestionsService questionService;

    @PostMapping("/add/answer")
    public Mono<Answer> addAnswer(@RequestBody Answer answer, @RequestParam String questionId) {
        return questionService.addAnswer(answer, questionId);
    }

    @PatchMapping("/update/answer")
    public Mono<Answer> updateQuestion(@RequestBody Answer answer, @RequestParam String questionId) {
        return questionService.updateAnswer(answer, questionId);
    }

    @DeleteMapping("/delete/answer")
    public Mono<Void> deleteQuestion(@RequestParam String answerId, @RequestParam String questionId) {
        return questionService.deleteAnswer(answerId, questionId);
    }
}
