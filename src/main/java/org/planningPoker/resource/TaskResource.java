package org.planningPoker.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.planningPoker.model.Task;
import org.planningPoker.service.TaskService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/tasks")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TaskResource {
    
    @Inject
    TaskService taskService;

    @PATCH
    @Operation(summary = "Create a new project.", description = "Admin creates a new project which in turn creates a new collection in the database.")
    @APIResponse(responseCode = "409", description = "The project name already exists.")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "404", description = "The user was not found")
    @APIResponse(responseCode = "200", description = "A new project has been successfully created.")
    @Path("/new-project")
    public Response startNewProject(@HeaderParam("projectName") String projectName, @HeaderParam("Authorization") String jwtToken) {
        return taskService.startNewProject(projectName, jwtToken);
    }

    @GET
    @Operation(summary = "Get all projects", description = "Retrieve all the current projects from the database.")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "404", description = "The user was not found")
    @APIResponse(responseCode = "200", description = "The projects were successfully retrieved from the database.")
    @Path("/get-projects")
    public Response getProjects(@HeaderParam("Authorization") String jwtToken) {
        return taskService.getProjects(jwtToken);
    }

    @GET
    @Operation(summary = "Get all tasks", description = "Retrieve a list of task objects from the specified collection.")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "404", description = "The collection was not found")
    @APIResponse(responseCode = "200", description = "The list was retrieved and returned successfully")
    @Path("/get-tasks")
    public Response getTasks(@HeaderParam("Authorization") String jwtToken, @HeaderParam("projectName") String projectName) {      
        return taskService.getAllTasks(jwtToken, projectName);
    }

    @POST
    @Operation(summary = "Create a new task", description = "A new task is created and added to the specified collection.")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "404", description = "The user was not found")
    @APIResponse(responseCode = "200", description = "A new task was successfully created.")
    @Path("/new-task")
    public Response createNewTask(@HeaderParam("projectName") String projectName, @HeaderParam("Authorization") String jwtToken, @HeaderParam("taskName") String taskName) {
        return taskService.createNewTask(jwtToken, projectName, taskName);
    }
    
    @PATCH
    @Operation(summary = "Edit a task", description = "A task body is sent to the database to update certain values depending on the origin of the request.")
    @APIResponse(responseCode = "401", description = "The user does not have permission to do this.")
    @APIResponse(responseCode = "404", description = "The user was not found")
    @APIResponse(responseCode = "204", description = "The task was not found.")
    @APIResponse(responseCode = "200", description = "The task was successfully updated.")
    @Path("/edit-task")
    public Response editTask(@HeaderParam("projectName") String projectName, @HeaderParam("Authorization") String jwtToken, @HeaderParam("userEmail") String userEmail, @RequestBody Task task) {
        return taskService.editTask(projectName, jwtToken, userEmail, task);
    }

}
