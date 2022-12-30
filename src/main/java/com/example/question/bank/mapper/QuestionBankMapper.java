package com.example.question.bank.mapper;

import com.example.question.bank.entity.Question;
import com.example.question.bank.models.QuestionResponse;

public class QuestionBankMapper {

    public QuestionBankMapper(){
    }

    public QuestionResponse mapQuestionResponseToQuestion(Question question) {
        return QuestionResponse.builder()
                .questionId(question.getQuestionId())
                .question(question.getQuestion())
                .questionUpvoteCount(question.getQuestionUpvoteCount())
                .userId(question.getUserId())
                .build();
    }
}
