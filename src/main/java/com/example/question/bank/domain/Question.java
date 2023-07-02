package com.example.question.bank.domain;

import com.example.question.bank.domain.user.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "questions")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Question {

    @Id
    @Field("_id")
    private String questionId;
    private String userId;
    private String questionTitle;
    private String questionDescription;
    private String lastModifiedDate;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @Builder.Default
    private List<String> answers = new ArrayList<>();
    @Builder.Default
    private int answersCount = 0;
    @Builder.Default
    private int views = 0;
    @Builder.Default
    private int upvotes = 0;
    @Builder.Default
    private int downvotes = 0;
    private String userName;
}
