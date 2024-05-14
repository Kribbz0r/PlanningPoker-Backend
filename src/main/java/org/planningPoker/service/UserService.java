package org.planningPoker.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import jakarta.ws.rs.core.Response;

public class UserService {

    private final MongoClient mongoClient;

    public UserService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Response testGet() {

        MongoDatabase database = mongoClient.getDatabase("planningpoker");
        MongoCollection<Document> collection = database.getCollection("User");
        FindIterable<Document> documents = collection.find();
        List<Document> userList = new ArrayList<>();

        for (Document document : documents) {
            userList.add(document);
        }
        
        return Response.ok(userList).build();
    }
    
}
