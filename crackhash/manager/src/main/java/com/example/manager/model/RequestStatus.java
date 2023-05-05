package com.example.manager.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class RequestStatus {

    public RequestStatus(Status status, Collection<String> answers) {
        this.status = status;
        this.answers = answers;
    }

    private final Status status;
    private final Collection<String> answers;
    public Object getStatus() {
        return status;
    }

    public String getAnswers() {
        return Arrays.toString(answers.toArray());
    }

    public enum Status {READY, IN_PROGRESS, ERROR}
}
