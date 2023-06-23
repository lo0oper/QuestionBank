package com.example.question.bank.service;

import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.Question;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class QuestionsService {

    @Autowired
    private QuestionRepository questionRepository;

    public Mono<Question> addQuestion(Question question) {
        return Mono.subscriberContext()
                .map(context -> context.get(ApplicationConstants.LOGGED_USER))
                .map(e -> (User) e)
                .map(User::getUserId)
                .flatMap(userId -> saveQuestion(userId, question));
    }

    private Mono<Question> saveQuestion(String userId, Question question) {
        return questionRepository.count()
                .flatMap(count -> {
                    question.setUserId(userId);
                    question.setQuestionId(count + 1);
                    question.setLastModifiedDate(LocalDate.now().toString());
                    return questionRepository.save(question);
                });
    }

    public Mono<Question> updateQuestion(Question question, Long questionId) {
        return questionRepository.findByQuestionId(questionId)
                .flatMap(existingQuestion -> {
                    existingQuestion.setQuestion(question.getQuestion());
                    return questionRepository.save(existingQuestion);
                });
    }
}
