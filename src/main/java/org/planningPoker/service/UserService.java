package org.planningPoker.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class UserService {

    private final MongoClient mongoClient;

    @Inject
    public UserService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Response testGet() {

        MongoDatabase database = mongoClient.getDatabase("PlanningPoker");
        MongoCollection<Document> collection = database.getCollection("User");
        
        List<Document> userList = new ArrayList<>();
        for (Document document : collection.find()) {
            userList.add(document);
        }
        System.out.println(userList);

        return Response.ok(userList).build();
    }

}
