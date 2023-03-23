package com.example.manager.api;

import com.example.manager.model.HashCrackerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

@RestController
public class InternalController {

    private final HashCrackerService hashCrackerService;

    public InternalController(HashCrackerService hashCrackerService) {
        this.hashCrackerService = hashCrackerService;
    }

    @PatchMapping("/internal/api/manager/hash/crack/request")
    public ResponseEntity<String> submitHashes(@RequestBody CrackHashWorkerResponse crackHash) {
        hashCrackerService.submitHashes(crackHash);
        return ResponseEntity.ok("");
    }
}
