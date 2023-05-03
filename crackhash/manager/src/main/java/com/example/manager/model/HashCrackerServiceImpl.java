package com.example.manager.model;

import com.example.manager.dto.CrackRequest;
import com.example.manager.exceptions.TaskNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Log4j2
public class HashCrackerServiceImpl implements HashCrackerService {

    private static final int WORKERS_COUNT = 1;
    private static final long TIMEOUT_MS = 30000; // 30 seconds
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";

    private final Map<String, RequestStatus> requestStatuses = new ConcurrentHashMap<>();

    private final RestTemplate restTemplate;

    public HashCrackerServiceImpl() {
        this.restTemplate = new RestTemplate();
        //    this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public String createTask(CrackRequest request) {
        String requestId = UUID.randomUUID().toString();
        RequestStatus requestStatus = new RequestStatus(requestId, WORKERS_COUNT);
        requestStatuses.put(requestId, requestStatus);
        log.info("add new task with id = " + requestId);
        sendTasksToWorkers(request, requestId);
        return requestId;
    }

    private void sendTasksToWorkers(CrackRequest request, String requestId) {
        for (int worker = 0; worker < WORKERS_COUNT; worker++) {
            CrackHashManagerRequest requestToWorker = new CrackHashManagerRequest();
            requestToWorker.setRequestId(requestId);
            requestToWorker.setHash(request.getHash());
            requestToWorker.setMaxLength(request.getMaxLength());
            var alphabet = new CrackHashManagerRequest.Alphabet();
            alphabet.getSymbols().addAll(Arrays.stream(ALPHABET.split("")).toList());
            requestToWorker.setAlphabet(alphabet);
            requestToWorker.setPartCount(WORKERS_COUNT);

            requestToWorker.setPartNumber(worker);
            HttpEntity<CrackHashManagerRequest> httpEntity = new HttpEntity<>(requestToWorker);
            String url = "http://worker:8080/internal/api/worker/hash/crack/task";
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    String.class);
            log.info("body: " + responseEntity.getBody());
        }
    }

    @Override
    public void submitHashes(CrackHashWorkerResponse crackHash) {
        var id = crackHash.getRequestId();
        var requestStatus = requestStatuses.get(id);
        requestStatus.donePart(crackHash.getAnswers().getWords());
    }

    @Override
    public RequestStatus getTaskStatus(String requestId) throws TaskNotFoundException {
        RequestStatus requestStatus = requestStatuses.get(requestId);
        if (requestStatus == null) {
            throw new TaskNotFoundException("Request not found");
        }
        return requestStatus;
    }

    // method to periodically check status of tasks and update requestStatuses map
    @Scheduled(fixedRate = 1000)
    public void checkTaskStatuses() {
        // iterate over all requests in IN_PROGRESS status
        requestStatuses.values().stream()
                .filter(status -> status.getStatus() == RequestStatus.Status.IN_PROGRESS)
                .forEach(status -> {
                    // check if all tasks have been completed
                    if (System.currentTimeMillis() - status.getStartTime() > TIMEOUT_MS) {
                        status.setStatus(RequestStatus.Status.ERROR);
                    }
                });
    }

}
