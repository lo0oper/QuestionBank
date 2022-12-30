package com.example.question.bank.repository;

import com.example.question.bank.entity.Question;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public interface QuestionRepository extends ReactiveCrudRepository<Question, Integer> {

    @Query("select * from questions where user_id = :userId")
    Mono<List<Question>> findByUserId(int userId);

}
