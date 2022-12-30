package com.example.question.bank.controller;

import com.example.question.bank.entity.Answer;
import com.example.question.bank.service.AnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class AnswersController {
    @Autowired
    AnswerService answerService;

    @PostMapping("/add/answer")
    public Mono<Answer> addAnswer(@RequestBody Answer answers){
        return answerService.addAnswer(answers);
    }

    @DeleteMapping("/delete/{answerId}/answer")
    public Mono<Void> deleteAnswer(@PathVariable int answerId){
       return answerService.deleteAnswer(answerId);
    }
    @PutMapping("/update/{answerId}/answer")
    public Mono<Answer> updateAnswer(@RequestBody Answer answers, @PathVariable int answerId){
        return answerService.updateAnswer(answers, answerId);
    }
}
