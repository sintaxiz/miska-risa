package com.example.manager.model;

import com.example.manager.dto.CrackRequest;
import com.example.manager.exceptions.TaskNotFoundException;
import com.example.manager.reps.HashTask;
import com.example.manager.reps.TaskRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.stereotype.Service;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Service
@Log4j2
public class HashCrackerServiceImpl implements HashCrackerService {

    private final TaskRepository taskRepository;

    // for send tasks to workers
    private final AmqpTemplate rabbitTemplate;
    private final Queue requestQueue;

    private static final int WORKERS_COUNT = 1;
    private static final long TIMEOUT_MS = 30000; // 30 seconds
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789";

    public HashCrackerServiceImpl(TaskRepository taskRepository, AmqpTemplate rabbitTemplate, Queue requestQueue) {
        this.taskRepository = taskRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.requestQueue = requestQueue;
    }

    @Override
    public String createTask(CrackRequest request) {
        String requestId = UUID.randomUUID().toString();
        taskRepository.save(new HashTask(requestId, request.getHash(), request.getMaxLength(),
                WORKERS_COUNT));
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

            rabbitTemplate.convertAndSend(requestQueue.getName(), requestToWorker);
        }
    }

    @Override
    public void submitHashes(CrackHashWorkerResponse crackHash) {
        var id = crackHash.getRequestId();
        var requestStatus = taskRepository.findById(id).orElseThrow();
        requestStatus.getData().addAll(crackHash.getAnswers().getWords());
        requestStatus.getFinishedParts().add(crackHash.getPartNumber());
        if (requestStatus.getPartCount() == requestStatus.getFinishedParts().size()) {
            requestStatus.setStatus(RequestStatus.Status.READY);
        }
        taskRepository.save(requestStatus);
    }

    @Override
    public RequestStatus getTaskStatus(String requestId) throws TaskNotFoundException {
        HashTask hashingTask = taskRepository.findById(requestId).orElseThrow(() ->
         new TaskNotFoundException(""));
        Duration dur = Duration.between(hashingTask.getStartTime(), Instant.now());
        if (dur.toMillis() > TIMEOUT_MS && hashingTask.getStatus() == RequestStatus.Status.IN_PROGRESS) {
            hashingTask.setStatus(RequestStatus.Status.ERROR);
            taskRepository.save(hashingTask);
            return new RequestStatus(hashingTask.getStatus(), hashingTask.getData());
        }
        return new RequestStatus(hashingTask.getStatus(), hashingTask.getData());
    }


}
