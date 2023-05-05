package com.example.worker;

import org.paukov.combinatorics.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import ru.nsu.ccfit.schema.crack_hash_request.CrackHashManagerRequest;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/internal/api/worker/hash/crack")
public class HashCrackWorkerController {

   private final WorkerService workerService;
    public HashCrackWorkerController(WorkerService workerService) {

        this.workerService = workerService;
    }

    @PostMapping("/task")
    public ResponseEntity<String> handleTask(@RequestBody CrackHashManagerRequest taskRequest) {
        try {
            workerService.submitTask(taskRequest    );
            return ResponseEntity.ok("Task in work");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


}
