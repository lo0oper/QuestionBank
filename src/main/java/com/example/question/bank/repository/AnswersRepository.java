package com.example.question.bank.repository;

import com.example.question.bank.entity.Answer;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface AnswersRepository extends ReactiveCrudRepository<Answer, Integer> {
    @Query("select * from answers where question_id = :questionId")
    Flux<Answer> findAllAnswersByQuestionId(int questionId);
}
