package com.example.question.bank.repository;

import com.example.question.bank.domain.Question;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface QuestionRepository extends ReactiveMongoRepository<Question, Integer> {
    Mono<Long> count();
    Mono<Question> findByQuestionId(Long questionId);
}
