package com.example.question.bank.helper;

import com.example.question.bank.domain.question.Question;
import com.example.question.bank.domain.question.QuestionRequest;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.repository.QuestionRepository;
import com.example.question.bank.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionBankHelper {
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public Mono<List<Question>> resolveSearch(List<Question> questionList, QuestionRequest questionRequest) {
        if(!StringUtils.isEmpty(questionRequest.getSearchTerm())) {
            return Mono.just(questionList.stream()
                    .filter(question -> question.getQuestionTitle().contains(questionRequest.getSearchTerm())
                            || question.getQuestionDescription().contains(questionRequest.getSearchTerm())).collect(Collectors.toList()));
        }
        return Mono.just(questionList);
    }

    public Mono<User> toggleQuestionIdInUserFavourite(String userId, String questionId) {
        return userRepository.findById(userId)
                .flatMap(user -> {
                    if(user.getFavouriteQuestions().contains(questionId)) {
                        user.getFavouriteQuestions().remove(questionId);
                    } else {
                        user.getFavouriteQuestions().add(questionId);
                    }

                    return Mono.just(user);
                })
                .flatMap(user -> userRepository.save(user));
    }
}
