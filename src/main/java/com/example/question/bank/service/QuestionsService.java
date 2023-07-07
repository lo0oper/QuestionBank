package com.example.question.bank.service;

import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.Answer;
import com.example.question.bank.domain.Question;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionsService {

    @Autowired
    private QuestionRepository questionRepository;

    public Mono<Question> addQuestion(Question question) {
        return Mono.subscriberContext()
                .map(context -> context.get(ApplicationConstants.LOGGED_USER))
                .map(e -> (User) e)
                .flatMap(user -> saveQuestion(user, question));
    }

    private Mono<Question> saveQuestion(User user, Question question) {
        String userName = !StringUtils.isEmpty(user.getFirstName()) ? user.getFirstName() : "";
        userName += !StringUtils.isEmpty(user.getLastName()) ? " " + user.getLastName() : "";

        String id = System.currentTimeMillis() + user.getUserId();
        question.setUserId(user.getUserId());
        question.setQuestionId(id);
        question.setLastModifiedDate(LocalDate.now().toString());
        question.setUserName(userName);
        return questionRepository.save(question);
    }

    public Mono<Question> updateQuestion(Question question) {
        return questionRepository.findById(question.getQuestionId())
                .flatMap(existingQuestion -> {
                    existingQuestion.setQuestionTitle(question.getQuestionTitle());
                    existingQuestion.setQuestionDescription(question.getQuestionDescription());
                    return questionRepository.save(existingQuestion);
                });
    }

    public Mono<Void> deleteQuestion(String questionId) {
        return questionRepository.deleteById(questionId);
    }

    public Mono<List<Question>> getAllQuestions() {
        return questionRepository.findQuestions()
                .collectList();
    }

    public Mono<Question> getQuestion(String questionId) {
        return questionRepository.findById(questionId);
    }

    public Mono<Answer> addAnswer(Answer answer, String questionId) {
        return Mono.subscriberContext()
                .map(context -> context.get(ApplicationConstants.LOGGED_USER))
                .map(e -> (User) e)
                .flatMap(user -> saveAnswer(user, answer, questionId));
    }

    private Mono<Answer> saveAnswer(User user, Answer answer, String questionId) {
        String answerId = System.currentTimeMillis() + user.getUserId();

        String userName = !StringUtils.isEmpty(user.getFirstName()) ? user.getFirstName() : "";
        userName += !StringUtils.isEmpty(user.getLastName()) ? " " + user.getLastName() : "";

        String finalUserName = userName;
        return questionRepository.findById(questionId)
                .flatMap(existingQuestion -> {
                    answer.setUserId(user.getUserId());
                    answer.setLastModifiedDate(LocalDate.now().toString());
                    answer.setAnswerId(answerId);
                    answer.setUserName(finalUserName);
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
                        if(ans.getAnswerId().equalsIgnoreCase(answer.getAnswerId())){
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
