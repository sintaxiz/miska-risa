package com.example.manager.reps;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<HashTask, String> {

}