package com.example.manager.api;

import com.example.manager.dto.CrackRequest;
import com.example.manager.dto.CrackResponse;
import com.example.manager.dto.HashCrackerStatusResponse;
import com.example.manager.exceptions.TaskNotFoundException;
import com.example.manager.model.HashCrackerService;
import com.example.manager.model.RequestStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hash")
public class HashCrackerController {

    private final HashCrackerService hashCrackerService;

    public HashCrackerController(HashCrackerService hashCrackerService) {
        this.hashCrackerService = hashCrackerService;
    }

    @PostMapping("/crack")
    public ResponseEntity<CrackResponse> crackHash(@RequestBody CrackRequest request) {
        String requestId = hashCrackerService.createTask(request);
        CrackResponse response = new CrackResponse(requestId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<HashCrackerStatusResponse> getStatus(@RequestParam String requestId) {
        RequestStatus status;
        try {
            status = hashCrackerService.getTaskStatus(requestId);
        } catch (TaskNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
        HashCrackerStatusResponse response = new HashCrackerStatusResponse(status);
        return ResponseEntity.ok(response);
    }
}
