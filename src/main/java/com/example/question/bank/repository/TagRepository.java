package com.example.question.bank.repository;

import com.example.question.bank.entity.Tag;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends ReactiveCrudRepository<Tag, Integer> {
}
