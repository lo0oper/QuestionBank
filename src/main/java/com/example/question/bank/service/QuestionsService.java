package com.example.question.bank.service;

import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.domain.Question;
import com.example.question.bank.domain.user.User;
import com.example.question.bank.helper.QuestionBankHelper;
import com.example.question.bank.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

@Service
public class QuestionsService {

    @Autowired
    private QuestionRepository questionRepository;

    private QuestionBankHelper questionBankHelper;

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
}
