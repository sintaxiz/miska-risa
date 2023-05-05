package com.example.manager.reps;

import com.example.manager.model.RequestStatus;
import lombok.Data;
import lombok.NonNull;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.annotation.Id;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;

@Data
@JsonIgnoreProperties({"uuid", "hash", "maxLength", "partCount", "startTime", "finishedParts"})
public class HashTask {
    @Id
    @NonNull
    String uuid;

    private RequestStatus.Status status;

    private String hash;

    private int maxLength;

    private List<String> data;

    private Integer partCount;

    private Instant startTime;

    private List<Integer> finishedParts;

    public HashTask(String uuid, String hash, int maxLength, Integer partCount) {
        this.uuid = uuid;
        this.status = RequestStatus.Status.IN_PROGRESS;
        this.hash = hash;
        this.maxLength = maxLength;
        this.data = new ArrayList<>();
        this.partCount = partCount;
        this.startTime = Instant.now();
        this.finishedParts = new ArrayList<>();
    }
}