package org.planningPoker.model;

import org.bson.codecs.pojo.annotations.BsonId;
import java.util.List;

public class Task {
    
    @BsonId
    private String taskId;
    private String task;
    private String status;
    private String estimatedTime;
    private String finalTime;
    private int votes;
    private int approvalvotes;
    private List<Integer> suggestedTimes;
    private List<String> usersthathavevoted;
    private Boolean disapproved;
    private List<String> usersthathaveapproved;

    public Task(String taskId, String task, String status, String estimatedTime, String finalTime, int votes,
            int approvalvotes, List<Integer> suggestedTimes, List<String> usersthathavevoted, Boolean disapproved, List<String> usersthathaveapproved) {
        this.taskId = taskId;
        this.task = task;
        this.status = status;
        this.estimatedTime = estimatedTime;
        this.finalTime = finalTime;
        this.votes = votes;
        this.approvalvotes = approvalvotes;
        this.suggestedTimes = suggestedTimes;
        this.usersthathavevoted = usersthathavevoted;
        this.disapproved = disapproved;
        this.usersthathaveapproved = usersthathaveapproved;
    }

    public Task() {}

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstimatedTime() {
        return estimatedTime;
    }

    public void setEstimatedTime(String estimatedTime) {
        this.estimatedTime = estimatedTime;
    }

    public String getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(String finalTime) {
        this.finalTime = finalTime;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getApprovalvotes() {
        return approvalvotes;
    }

    public void setApprovalvotes(int approvalvotes) {
        this.approvalvotes = approvalvotes;
    }

    public List<Integer> getSuggestedTimes() {
        return suggestedTimes;
    }

    public void setSuggestedTimes(List<Integer> suggestedTimes) {
        this.suggestedTimes = suggestedTimes;
    }

    public List<String> getUsersthathavevoted() {
        return usersthathavevoted;
    }

    public void setUsersthathavevoted(List<String> usersthathavevoted) {
        this.usersthathavevoted = usersthathavevoted;
    }

    public Boolean getDisapproved() {
        return disapproved;
    }

    public void setDisapproved(Boolean disapproved) {
        this.disapproved = disapproved;
    }

    public List<String> getUsersthathaveapproved() {
        return usersthathaveapproved;
    }

    public void setUsersthathaveapproved(List<String> usersthathaveapproved) {
        this.usersthathaveapproved = usersthathaveapproved;
    }

    

    





}
