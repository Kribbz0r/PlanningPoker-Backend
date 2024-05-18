package org.planningPoker.service;

import org.planningPoker.config.AppConfig;

import com.mongodb.client.MongoClient;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class TaskService {
    
    @Inject
    AppConfig appConfig;

    private final MongoClient mongoClient;

    @Inject
    public TaskService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }
}
