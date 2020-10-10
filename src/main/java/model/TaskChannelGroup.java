package model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TaskChannelGroup {

    @JsonProperty
    private TaskChannel toDo;

    @JsonProperty
    private TaskChannel inProgress;

    @JsonProperty
    private TaskChannel completed;


    public TaskChannel getToDo() {
        return toDo;
    }

    public TaskChannel getInProgress() {
        return inProgress;
    }

    public TaskChannel getCompleted() {
        return completed;
    }
}
