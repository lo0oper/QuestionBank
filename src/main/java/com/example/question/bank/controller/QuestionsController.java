package com.example.question.bank.controller;

import com.example.question.bank.entity.Question;
import com.example.question.bank.models.QuestionRequest;
import com.example.question.bank.models.QuestionResponse;
import com.example.question.bank.service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import java.util.List;

@RestController
public class QuestionsController {
    @Autowired
    QuestionsService questionsService;

    @PostMapping("/add/question")
    public Mono<Question> addQuestion(@RequestBody Question question){
        return questionsService.addQuestion(question);
    }

    @GetMapping("/questions")
    public Mono<List<QuestionResponse>> getQuestions(@RequestBody QuestionRequest questionRequest){
        return questionsService.getQuestions(questionRequest);
    }

    @DeleteMapping("/delete/{questionId}/question")
    public Mono<Void> deleteQuestion(@PathVariable int questionId){
        return questionsService.deleteQuestion(questionId);
    }
    @PutMapping("/update/{questionId}/question")
    public Mono<Question> updateQuestion(@RequestBody Question question, @PathVariable int questionId){
        return questionsService.updateQuestion(question, questionId);
    }
}
