package com.daogt.dreamflowapi.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class WorkflowRequest {
    @Data
    public static class NodeInputs {
        private Map<String, Object> inputs;
        @JsonProperty("class_type")
        private String classType;
    }

    @JsonProperty("3")
    private NodeInputs node3;

    @JsonProperty("5")
    private NodeInputs node5;

    @JsonProperty("6")
    private NodeInputs node6;

    @JsonProperty("7")
    private NodeInputs node7;
}