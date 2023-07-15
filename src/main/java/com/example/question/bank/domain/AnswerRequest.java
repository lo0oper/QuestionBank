package com.example.question.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnswerRequest {
    private String questionId;
    private String userId;
    private String answer;
    private boolean isChatGpt;
}
