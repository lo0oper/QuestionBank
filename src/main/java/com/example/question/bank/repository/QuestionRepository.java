package com.example.question.bank.repository;

import com.example.question.bank.domain.Question;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface QuestionRepository extends ReactiveMongoRepository<Question, Integer> {
    Mono<Long> count();

    @Query(value = "{}", fields = "{ 'tags': 0, 'answers': 0 }")
    Flux<Question> findQuestions();

}
