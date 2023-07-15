package com.example.question.bank.helper;

import com.example.question.bank.domain.question.Question;
import com.example.question.bank.domain.question.QuestionRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionBankHelper {

    public Mono<List<Question>> resolveSearch(List<Question> questionList, QuestionRequest questionRequest) {
        if(!StringUtils.isEmpty(questionRequest.getSearchTerm())) {
            return Mono.just(questionList.stream()
                    .filter(question -> question.getQuestionTitle().contains(questionRequest.getSearchTerm())
                            || question.getQuestionDescription().contains(questionRequest.getSearchTerm())).collect(Collectors.toList()));
        }
        return Mono.just(questionList);
    }
}
