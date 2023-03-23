package com.example.manager.model;

import com.example.manager.dto.CrackRequest;
import com.example.manager.exceptions.TaskNotFoundException;
import com.example.manager.model.RequestStatus;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

public interface HashCrackerService {
    RequestStatus getTaskStatus(String requestId) throws TaskNotFoundException;

    String createTask(CrackRequest request);

    void submitHashes(CrackHashWorkerResponse crackHash);
}
