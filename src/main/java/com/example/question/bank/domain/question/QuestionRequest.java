package com.example.question.bank.domain.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionRequest {
    private String questionId;
    private String userId;
    private String questionTitle;
    private String questionDescription;
    private String voteType;
    private String searchTerm;
}
