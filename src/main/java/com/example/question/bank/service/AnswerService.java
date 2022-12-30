package com.example.question.bank.service;

import com.example.question.bank.entity.Answer;
import com.example.question.bank.repository.AnswersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class AnswerService {
    @Autowired
    AnswersRepository answersRepository;
    public Mono<Answer> addAnswer(Answer answers) {
        return answersRepository.save(answers);
    }
    public Mono<Void> deleteAnswer(int answerId) {
        return answersRepository.deleteById(answerId).then();
    }
    public Mono<Answer> updateAnswer(Answer answers, int answerId) {
        return answersRepository.findById(answerId)
                .switchIfEmpty(Mono.error(new Exception("Question Not Found")))
                .map(answers1 -> { answers1.setAnswer(answers.getAnswer()); return answers1; })
                .flatMap(answers1 -> answersRepository.save(answers1));
    }
}
