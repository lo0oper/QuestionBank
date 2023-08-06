package com.example.question.bank.helper;

import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.answer.AnswerRequest;
import com.example.question.bank.domain.notification.Notification;
import com.example.question.bank.domain.notification.NotificationComment;
import com.example.question.bank.domain.question.Question;
import com.example.question.bank.domain.question.QuestionRequest;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.repository.NotificationRepository;
import com.example.question.bank.repository.QuestionRepository;
import com.example.question.bank.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QuestionBankHelper {
    @Autowired
    private  UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private NotificationRepository notificationRepository;

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

    public void updateVoteNotification(QuestionRequest questionRequest, Question question, String vote) {
        notificationRepository.findById(question.getUserId())
                .switchIfEmpty(Mono.just(Notification.builder().userId(question.getUserId()).build()))
                .flatMap(notification -> {
                    if (StringUtils.equalsIgnoreCase(vote, ApplicationConstants.UPVOTE)) {
                        if (!CollectionUtils.isEmpty(notification.getDownvotedvotedUsers()) && notification.getDownvotedvotedUsers().contains(questionRequest.getUserId())) {
                            notification.getDownvotedvotedUsers().remove(questionRequest.getUserId());
                            notification.setDownvoteCount(notification.getDownvoteCount() - 1);
                        }
                        notification.getUpvotedUsers().add(questionRequest.getUserId());
                        notification.setUpvoteCount(notification.getUpvoteCount() + 1);
                        notificationRepository.save(notification).subscribe();
                    } else {
                        if (!CollectionUtils.isEmpty(notification.getUpvotedUsers()) && notification.getUpvotedUsers().contains(questionRequest.getUserId())) {
                            notification.getUpvotedUsers().remove(questionRequest.getUserId());
                            notification.setUpvoteCount(notification.getUpvoteCount() - 1);
                        }
                        notification.getDownvotedvotedUsers().add(questionRequest.getUserId());
                        notification.setDownvoteCount(notification.getUpvoteCount() + 1);
                        notificationRepository.save(notification).subscribe();
                    }
                    return null;
                })
                .subscribeOn(Schedulers.elastic())
                .subscribe();
    }

    public void updateCommentNotification(String userName, AnswerRequest answerRequest, String loggedInUserId) {
        if(StringUtils.equalsIgnoreCase(loggedInUserId, answerRequest.getAskedUserId())) {
            return;
        }
        notificationRepository.findById(answerRequest.getAskedUserId())
                .switchIfEmpty(Mono.just(Notification.builder().userId(answerRequest.getAskedUserId()).build()))
                .flatMap(notification -> {

                    NotificationComment notificationComment = NotificationComment.builder()
                            .userName(userName)
                            .comment(answerRequest.getAnswer())
                            .questionId(answerRequest.getQuestionId())
                            .build();
                    notification.getNotificationComments().add(notificationComment);
                    notificationRepository.save(notification).subscribe();
                    return null;
                })
                .subscribeOn(Schedulers.elastic())
                .subscribe();
    }
}
