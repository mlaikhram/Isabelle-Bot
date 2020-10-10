package model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class YmlConfig {

    @JsonProperty
    private String token;

    @JsonProperty
    private TaskChannelGroup channels;

    @JsonProperty
    private List<Long> tasks;


    public String getToken() {
        return token;
    }

    public TaskChannelGroup getChannels() {
        return channels;
    }

    public List<Long> getTasks() {
        return tasks;
    }
}
