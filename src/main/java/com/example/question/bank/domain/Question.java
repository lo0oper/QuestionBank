package com.example.question.bank.domain;

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
    private int questionId;
    private String userId;
    private String question;
    private String lastModifiedDate;
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @Builder.Default
    private List<String> answers = new ArrayList<>();
    @Builder.Default
    private int answersCount = 0;
    @Builder.Default
    private List<String> comments = new ArrayList<>();
    @Builder.Default
    private int commentsCount = 0;
    @Builder.Default
    private int views = 0;
    @Builder.Default
    private int upvotes = 0;
    @Builder.Default
    private int downvotes = 0;


}
