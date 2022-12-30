package com.example.question.bank.helper;

import com.example.question.bank.constants.ApplicationConstants;
import com.example.question.bank.entity.Answer;
import com.example.question.bank.entity.Question;
import com.example.question.bank.entity.Tag;
import com.example.question.bank.models.QuestionRequest;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class QuestionBankHelper {

    public QuestionBankHelper(){
    }
    public Mono<List<Question>> filterQuestionList(QuestionRequest questionRequest, List<Question> questionList) {
        return Mono.just(questionList)
                        .flatMap(questionList1 -> filterUserQuestions(questionRequest, questionList1))
                        .flatMap(questionList1 -> filterSearching(questionRequest, questionList1))
                        .flatMap(questionList1 -> filterSort(questionRequest, questionList1));
    }

    private Mono<List<Question>> filterSort(QuestionRequest questionRequest, List<Question> questionList) {
        if (Optional.ofNullable(questionRequest.getFacets()).isPresent() && questionRequest.getFacets().containsKey(ApplicationConstants.SORT_TYPE)) {
            switch (ApplicationConstants.SORT_TYPE) {
                case ApplicationConstants.HIGHEST_RATED:
                    return sortByHighestRated(questionList);
                default:
                    return Mono.just(questionList);
            }
        }
        return Mono.just(questionList);
    }

    private Mono<List<Question>> sortByHighestRated(List<Question> questionList) {
        return Mono.just(questionList.stream()
                .sorted((o1, o2) -> {
                    if (o1.getQuestionUpvoteCount() > o2.getQuestionUpvoteCount())
                        return -1;
                    else if (o1.getQuestionUpvoteCount() < o2.getQuestionUpvoteCount())
                        return 1;
                    return 0;
                }).collect(Collectors.toList()));
    }

    private Mono<List<Question>> filterSearching(QuestionRequest questionRequest, List<Question> questionList) {
        if (StringUtils.hasLength(questionRequest.getSearchTerm()) &&  questionRequest.getSearchTerm().trim().length() > ApplicationConstants.ZERO) {
            List<Question> newQuestionList = questionList
                    .stream().filter(e -> e.getQuestion().equalsIgnoreCase(questionRequest.getSearchTerm().trim())).toList();

            return Mono.just(newQuestionList);
        }
        return Mono.just(questionList);
    }

    private Mono<List<Question>> filterUserQuestions(QuestionRequest questionRequest, List<Question> questionList) {
        if (questionRequest.getUserId() == ApplicationConstants.NO_USER) {
            return Mono.just(questionList);
        }
        return Mono.just(questionList
                .stream().filter(e -> e.getUserId() == questionRequest.getUserId()).toList());

    }

    public List<Answer> getAnswersToQuestions(int questionId, List<Answer> answerList) {
        if(CollectionUtils.isEmpty(answerList)){
            return new ArrayList<>();
        }
        List<Answer> result = new ArrayList<>(answerList);
        return result.stream().filter(e -> e.getQuestionId() == questionId).collect(Collectors.toList());
    }

    public List<Tag> getTagsToQuestions(int questionId, List<Tag> tagList) {
        if(CollectionUtils.isEmpty(tagList)){
            return new ArrayList<>();
        }
        List<Tag> result = new ArrayList<>(tagList);
        return result.stream().filter(e -> e.getQuestionId() == questionId).collect(Collectors.toList());
    }
}

