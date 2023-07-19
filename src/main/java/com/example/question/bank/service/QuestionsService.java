package com.example.question.bank.service;

import com.example.question.bank.connector.ChatGptConnector;
import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.Answer;
import com.example.question.bank.domain.AnswerRequest;
import com.example.question.bank.domain.chatgpt.ChatGptRequest;
import com.example.question.bank.domain.chatgpt.Message;
import com.example.question.bank.domain.question.Question;
import com.example.question.bank.domain.question.QuestionRequest;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.helper.QuestionBankHelper;
import com.example.question.bank.repository.QuestionRepository;
import com.example.question.bank.repository.UserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuestionsService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionBankHelper questionBankHelper;

    @Autowired
    private ChatGptConnector chatGptConnector;

    Mono<User> getUser() {
        return Mono.subscriberContext()
                .map(context -> context.get(ApplicationConstants.LOGGED_USER))
                .map(e -> (User) e);
    }

    public Mono<Question> addQuestion(QuestionRequest questionRequest) {

        return getUser()
                .flatMap(user -> saveQuestion(user, questionRequest))
                .flatMap(question -> {
                    saveChatGptAnswer(question.getQuestionId(), question.getQuestionDescription());
                    return Mono.just(question);
                });

    }

    private void saveChatGptAnswer(String questionId, String questionDescription) {
        ChatGptRequest chatGptRequest = ChatGptRequest.builder().messages(Collections.singletonList(Message.builder().content(questionDescription).build())).build();
        Mono.just(chatGptRequest)
                .publishOn(Schedulers.elastic())
                .subscribe(request -> chatGptConnector.fetchChatGptResponse(chatGptRequest)
                        .flatMap(chatGptResponse -> {

                            AnswerRequest answerRequest = AnswerRequest.builder()
                                    .questionId(questionId)
                                    .answer(Optional.ofNullable(chatGptResponse.getChoices()).map(choices -> choices.get(0).getMessage().getContent()).orElse(null))
                                    .isChatGpt(true)
                                    .build();
                            return saveAnswer(null, answerRequest);
                        }).subscribe()
                );
    }

    private Mono<Question> saveQuestion(User user, QuestionRequest questionRequest) {
        String userName = !StringUtils.isEmpty(user.getFirstName()) ? user.getFirstName() : "";
        userName += !StringUtils.isEmpty(user.getLastName()) ? " " + user.getLastName() : "";

        String id = System.currentTimeMillis() + user.getUserId();
        Question question =
                Question.builder()
                        .userId(user.getUserId())
                        .questionId(id)
                        .lastModifiedDate(LocalDate.now().toString())
                        .userName(userName)
                        .questionTitle(questionRequest.getQuestionTitle())
                        .questionDescription(questionRequest.getQuestionDescription())
                        .build();

        return questionRepository.save(question);
    }

    public Mono<Question> updateQuestion(QuestionRequest questionRequest) {
        return questionRepository.findById(questionRequest.getQuestionId())
                .flatMap(existingQuestion -> saveQuestionContent(questionRequest, existingQuestion))
                .flatMap(existingQuestion -> voteQuestion(questionRequest, existingQuestion))
                .flatMap(existingQuestion -> questionRepository.save(existingQuestion));
    }

    private Mono<Question> saveQuestionContent(QuestionRequest questionRequest, Question question) {
        if (!StringUtils.isEmpty(questionRequest.getQuestionTitle()) && !StringUtils.isEmpty(questionRequest.getQuestionDescription())) {
            question.setQuestionTitle(questionRequest.getQuestionTitle());
            question.setQuestionDescription(questionRequest.getQuestionDescription());
        }
        return Mono.just(question);
    }

    private Mono<Question> voteQuestion(QuestionRequest questionRequest, Question question) {

        if (!StringUtils.isEmpty(questionRequest.getVoteType())) {

            String vote = questionRequest.getVoteType();
            String userId = questionRequest.getUserId();

            if (StringUtils.equalsIgnoreCase(vote, ApplicationConstants.UPVOTE) && !question.getUpvotedUsers().contains(userId)) {
                question.setUpvotes(question.getUpvotes() + 1);
                question.getUpvotedUsers().add(userId);
                boolean removed = question.getDownvotedUsers().remove(userId);
                if (removed) {
                    question.setDownvotes(question.getDownvotes() - 1);
                }
            } else if (StringUtils.equalsIgnoreCase(vote, ApplicationConstants.DOWNVOTE) && !question.getDownvotedUsers().contains(userId)) {
                question.setDownvotes(question.getDownvotes() + 1);
                question.getDownvotedUsers().add(userId);

                boolean removed = question.getUpvotedUsers().remove(userId);
                if (removed) {
                    question.setUpvotes(question.getUpvotes() - 1);
                }
            }
        }
        return Mono.just(question);
    }

    public Mono<Void> deleteQuestion(String questionId) {
        return questionRepository.deleteById(questionId);
    }

    public Mono<List<Question>> getAllQuestions(QuestionRequest questionRequest) {
        return questionRepository.findQuestions().collectList()
                .flatMap(list -> questionBankHelper.resolveSearch(list, questionRequest));
    }

    public Mono<Question> getQuestion(String questionId) {
        return questionRepository.findById(questionId);
    }

    public Mono<Answer> addAnswer(AnswerRequest answerRequest) {
        return Mono.subscriberContext()
                .map(context -> context.get(ApplicationConstants.LOGGED_USER))
                .map(e -> (User) e)
                .flatMap(user -> saveAnswer(user, answerRequest));
    }

    private Mono<Answer> saveAnswer(User user, AnswerRequest answerRequest) {
        if (StringUtils.isEmpty(answerRequest.getAnswer())) {
            return Mono.empty();
        }
        String answerId = user != null ? System.currentTimeMillis() + user.getUserId() : null;

        String userName = "";
        if (answerRequest.isChatGpt()) {
            userName = "Chat GPT";
        } else {
            userName += !StringUtils.isEmpty(user.getFirstName()) ? user.getFirstName() : "";
            userName += !StringUtils.isEmpty(user.getLastName()) ? " " + user.getLastName() : "";
        }

        String finalUserName = userName;
        return questionRepository.findById(answerRequest.getQuestionId())
                .flatMap(existingQuestion -> {
                    Answer answer = Answer.builder()
                            .answer(answerRequest.getAnswer())
                            .userId(Optional.ofNullable(user).map(User::getUserId).orElse(null))
                            .lastModifiedDate(LocalDate.now().toString())
                            .answerId(answerId)
                            .userName(finalUserName)
                            .build();
                    existingQuestion.getAnswers().add(answer);
                    existingQuestion.setAnswersCount(existingQuestion.getAnswersCount() + 1);
                    return questionRepository.save(existingQuestion)
                            .then(Mono.just(answer));
                });
    }

    public Mono<Answer> updateAnswer(Answer answer, String questionId) {
        return questionRepository.findById(questionId)
                .flatMap(existingQuestion -> {
                    existingQuestion.getAnswers().forEach(ans -> {
                        if (ans.getAnswerId().equalsIgnoreCase(answer.getAnswerId())) {
                            ans.setAnswer(answer.getAnswer());
                        }
                    });
                    answer.setLastModifiedDate(LocalDate.now().toString());
                    return questionRepository.save(existingQuestion)
                            .then(Mono.just(answer));
                });
    }

    public Mono<Void> deleteAnswer(String answerId, String questionId) {
        return questionRepository.findById(questionId)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "We went into some errors.")))
                .flatMap(existingQuestion -> {
                    List<Answer> updatedAnswerList = existingQuestion.getAnswers().stream()
                            .filter(ans -> !ans.getAnswerId().equalsIgnoreCase(answerId))
                            .collect(Collectors.toList());
                    existingQuestion.setAnswers(updatedAnswerList);
                    existingQuestion.setAnswersCount(existingQuestion.getAnswersCount() - 1);
                    return questionRepository.save(existingQuestion)
                            .then(Mono.empty());
                });
    }
}
