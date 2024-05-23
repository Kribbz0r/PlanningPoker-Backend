package org.planningPoker.service;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.planningPoker.config.AppConfig;
import org.planningPoker.model.Task;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.quarkus.runtime.configuration.ProfileManager;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

@Transactional(Transactional.TxType.SUPPORTS)
@ApplicationScoped
public class TaskService {
    
    @Inject
    AppConfig appConfig;

    @Inject
    SecurityService securityService;

    @Inject
    UserService userService;


    private final MongoClient mongoClient;

    @Inject
    public TaskService(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    public Response startNewProject(String projectName, String jwtToken) {

         Jws<Claims> userClaim = null;

        try {
            userClaim = securityService.verifyJwt(jwtToken);

            if (userClaim.getPayload().get("groups").toString().contains("startproject")) {
                MongoDatabase database;
                if (ProfileManager.getLaunchMode().isDevOrTest()) {
                    database = mongoClient.getDatabase("PlanningPokerDev");
                } else {
                    database = mongoClient.getDatabase("PlanningPoker");
                }

                MongoCollection<Document> collection = database.getCollection("Projects");
                Document query = new Document("projects", projectName);
                boolean projectExists = collection.find(query).iterator().hasNext();
                
                if (projectExists) {
                    return Response.status(Response.Status.CONFLICT).entity("A project with this name alreay exists.").build();
                } else {
                    Document update = new Document("$push", new Document("projects", projectName));
                    collection.updateOne(new Document(), update);
                    database.createCollection(projectName);
                    return Response.ok().entity(projectName + " project has been created").build();
                }
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }

    }

    public Response getProjects(String jwtToken) {
        Jws<Claims> userClaim = null;

        try {
            userClaim = securityService.verifyJwt(jwtToken);

            if (userClaim != null) {
                MongoDatabase database;
                if (ProfileManager.getLaunchMode().isDevOrTest()) {
                    database = mongoClient.getDatabase("PlanningPokerDev");
                } else {
                    database = mongoClient.getDatabase("PlanningPoker");
                }
                MongoCollection<Document> collection = database.getCollection("Projects");
                Document query = new Document();
                return Response.ok(collection.find(query).first()).build();
                
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
    }

    public Response createNewTask(String jwtToken, String projectName, String taskName) {

        Jws<Claims> userClaim = null;

        try {
            userClaim = securityService.verifyJwt(jwtToken);

            if (userClaim.getPayload().get("groups").toString().contains("createnewtask")) {

                Document newDocument = new Document();
                newDocument.append("task", taskName);
                newDocument.append("status", "undervote");
                newDocument.append("votes", 0);
                newDocument.append("approvalvotes", 0);
                newDocument.append("estimatedTime", null);
                newDocument.append("finalTime", null);
                newDocument.append("suggestedTimes", new ArrayList<>());
                newDocument.append("usersthathavevoted", new ArrayList<>());
                newDocument.append("disapproved", false);
                newDocument.append("usersthathaveapproved", new ArrayList<>());

                MongoDatabase database;
                if (ProfileManager.getLaunchMode().isDevOrTest()) {
                    database = mongoClient.getDatabase("PlanningPokerDev");
                } else {
                    database = mongoClient.getDatabase("PlanningPoker");
                }
                MongoCollection<Document> collection = database.getCollection(projectName);
                collection.insertOne(newDocument);

                return Response.ok().entity(taskName + " has been added to " + projectName).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are not authorized to do this!").build();
        }
    }

    public Response editTask(String projectName, String jwtToken, String userEmail, Task task) {

        Jws<Claims> userClaim = null;
    
        try {
            userClaim = securityService.verifyJwt(jwtToken);

            if (userClaim != null) {
        
                MongoDatabase database;
                if (ProfileManager.getLaunchMode().isDevOrTest()) {
                    database = mongoClient.getDatabase("PlanningPokerDev");
                } else {
                    database = mongoClient.getDatabase("PlanningPoker");
                }
    
                MongoCollection<Document> collection = database.getCollection(projectName);
                ObjectId taskDocumentId = new ObjectId(task.getTaskId());
                
                Document query = new Document("_id", taskDocumentId);
                Document document = collection.find(query).first();
                if (document == null) {
                    return Response.status(Response.Status.NOT_FOUND).entity("Task not found").build();
                }

                String newStatus = "undervote";
                Response response = userService.getNumberWithAccess(jwtToken);
                int numberWithAccess = Integer.valueOf(response.getEntity().toString());

                if(task.getFinalTime() != null) {
                    newStatus = "complete";
                } else if (task.getEstimatedTime() != null && task.getFinalTime() == null) {
                    newStatus = "inprogress";
                } else if (task.getDisapproved() == true && task.getUsersthathaveapproved().size() == numberWithAccess) {
                    newStatus = "needattention";
                }

                List<String> currentUsers = task.getUsersthathavevoted();
                if (!currentUsers.contains(userEmail)) {
                    currentUsers.add(userEmail);
                    task.setUsersthathavevoted(currentUsers);
                }
                List<String> currentApproved = task.getUsersthathaveapproved();
                if (!currentApproved.contains(userEmail)) {
                    currentApproved.add(userEmail);
                    task.setUsersthathaveapproved(currentApproved);
                }
                

                Document updateDocument = new Document();
                updateDocument.append("status", newStatus);
                updateDocument.append("estimatedTime", task.getEstimatedTime());
                updateDocument.append("finalTime", task.getFinalTime());
                updateDocument.append("votes", task.getVotes());
                updateDocument.append("approvalvotes", task.getApprovalvotes());
                updateDocument.append("suggestedTimes", task.getSuggestedTimes());
                updateDocument.append("usersthathavevoted", task.getUsersthathavevoted());
                updateDocument.append("disapproved", task.getDisapproved());
                updateDocument.append("userthathaveapproved", task.getUsersthathaveapproved());

                collection.updateOne(query, new Document("$set", updateDocument));

                return Response.ok().entity("The task has been updated!").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("No user found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("You are NOT authorized to do this!").build();
        }
    }
}
