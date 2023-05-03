package com.example.manager.model;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class RequestStatus {

    private final String requestId;
    private final long startTime;
    private Status status;
    private int partsCount;
    private int donePartsCount;
    private final Collection<String> answers;

    public RequestStatus(String requestId, int partsCount) {
        this.requestId = requestId;
        this.partsCount = partsCount;
        donePartsCount = 0;
        status = Status.IN_PROGRESS;
        startTime = System.currentTimeMillis();
        answers = new HashSet<>();
    }

    public void donePart(List<String> words) {
        if (status == Status.IN_PROGRESS) {
            this.answers.addAll(words);
            partsCount += 1;
        }
        if (partsCount == donePartsCount) {
            status = Status.READY;
        }
    }

    public Status getStatus() {
        return status;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getAnswers() {
        return Arrays.toString(answers.toArray());
    }

    public enum Status {READY, IN_PROGRESS, ERROR}
}
