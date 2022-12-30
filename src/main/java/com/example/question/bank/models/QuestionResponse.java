package com.example.question.bank.models;

import com.example.question.bank.entity.Answer;
import com.example.question.bank.entity.Tag;
import lombok.Builder;
import lombok.Data;
import org.apache.logging.log4j.util.Strings;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.List;

@Data
@Builder
public class QuestionResponse {
    private int questionId;
    private int userId;
    private String question;
    private int questionUpvoteCount = 0;
    private List<Answer> answers;
    private List<Tag> tags;
}
