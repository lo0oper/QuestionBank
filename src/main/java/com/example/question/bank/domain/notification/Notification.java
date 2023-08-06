package com.example.question.bank.domain.notification;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "notification")
public class Notification {

    @Id
    @Field("_id")
    private String userId; // question owner
    private int upvoteCount;
    @Builder.Default
    private List<String> upvotedUsers = new ArrayList<>();
    @Builder.Default
    private List<String> downvotedvotedUsers = new ArrayList<>();
    private int downvoteCount; // downvoted person name
    @Builder.Default
    private List<NotificationComment> notificationComments = new ArrayList<>();
}
