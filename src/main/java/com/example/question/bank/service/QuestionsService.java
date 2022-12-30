package com.example.question.bank.service;

import com.example.question.bank.entity.Answer;
import com.example.question.bank.entity.Question;
import com.example.question.bank.entity.Tag;
import com.example.question.bank.helper.QuestionBankHelper;
import com.example.question.bank.mapper.QuestionBankMapper;
import com.example.question.bank.models.QuestionRequest;
import com.example.question.bank.models.QuestionResponse;
import com.example.question.bank.repository.AnswersRepository;
import com.example.question.bank.repository.QuestionRepository;
import com.example.question.bank.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class QuestionsService {

    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    AnswersRepository answersRepository;
    @Autowired
    TagRepository tagRepository;
    private QuestionBankHelper questionBankHelper;
    private QuestionBankMapper questionBankMapper;

    public QuestionsService(){
        questionBankHelper = new QuestionBankHelper();
        questionBankMapper = new QuestionBankMapper();
    }

    public Mono<Question> addQuestion(Question question) {
        return questionRepository.save(question);
    }
    public Mono<List<QuestionResponse>> getQuestions(QuestionRequest questionRequest) {

        Mono<List<Question>> questionList = questionRepository.findAll()
                .collectList()
                .flatMap(questionsList -> questionBankHelper.filterQuestionList(questionRequest, questionsList));

        Mono<List<Answer>> answerList = answersRepository.findAll().collectList();

        Mono<List<Tag>> tagList = tagRepository.findAll().collectList();

        return Mono.zip(questionList, answerList, tagList).map(e -> {
            List<QuestionResponse> response = new ArrayList<>();
            for(Question question : e.getT1()){
                QuestionResponse newQuestionResponse = questionBankMapper.mapQuestionResponseToQuestion(question);
                newQuestionResponse.setAnswers(questionBankHelper.getAnswersToQuestions(question.getQuestionId(), e.getT2()));
                newQuestionResponse.setTags(questionBankHelper.getTagsToQuestions(question.getQuestionId(), e.getT3()));
                response.add(newQuestionResponse);
            }
            return response;
        });
    }

    private Mono<List<Answer>> retrieveAnswers(Integer questionId) {
        return answersRepository.findAllAnswersByQuestionId(questionId).collectList();
    }

    public Mono<Question> updateQuestion(Question question, int questionId) {
        return questionRepository.findById(questionId)
                .map(question1 -> {question1.setQuestion(question.getQuestion()); return question1; })
                .flatMap(question1 -> questionRepository.save(question1));
    }
    public Mono<Void> deleteQuestion(int questionId) {
        return questionRepository.deleteById(questionId).then();
    }
}
