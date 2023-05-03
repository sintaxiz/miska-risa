package com.example.worker;

import org.paukov.combinatorics.*;
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

    public static final String API_MANAGER_HASH_CRACK_REQUEST = "http://manager/internal/api/manager/hash/crack/request";

    private final RestTemplate restTemplate = new RestTemplate();

    private final ExecutorService executorService;

    public HashCrackWorkerController() {
        executorService = Executors.newCachedThreadPool();
    }

    @PostMapping("/task")
    public ResponseEntity<String> handleTask(@RequestBody CrackHashManagerRequest taskRequest) {
        try {

            executorService.submit(()-> doWork(taskRequest));
            return ResponseEntity.ok("Task in work");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    private void doWork(CrackHashManagerRequest taskRequest) {
        // Calculate word range to test
        int partSize = calculatePartSize(taskRequest.getMaxLength(), taskRequest.getPartCount());
        int startIdx = taskRequest.getPartNumber() * partSize;
        int endIdx = (taskRequest.getPartNumber() + 1) * partSize - 1;
        if (endIdx > calculateWordSpace(taskRequest.getMaxLength())) {
            endIdx = calculateWordSpace(taskRequest.getMaxLength()) - 1;
        }

        // Generate and test words
        List<String> matchedWords = new ArrayList<>();
        for (var word : generateWords(startIdx, endIdx, taskRequest.getMaxLength(), taskRequest.getAlphabet().getSymbols())) {
            if (word.equals(taskRequest.getHash())) {
                matchedWords.add(word);
            }
        }

        doneWork(matchedWords, taskRequest.getRequestId(), taskRequest.getPartNumber());

    }

    private void doneWork(List<String> matchedWords, String requestId, int partNumber) {
        // Send response to manager
        CrackHashWorkerResponse taskResponse = new CrackHashWorkerResponse();
        taskResponse.setRequestId(requestId);
        taskResponse.setAnswers(convertToAnswers(matchedWords));
        taskResponse.setPartNumber(partNumber);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        HttpEntity<CrackHashWorkerResponse> entity = new HttpEntity<>(taskResponse, headers);
        restTemplate.exchange(API_MANAGER_HASH_CRACK_REQUEST, HttpMethod.PATCH, entity, String.class);
    }

    private CrackHashWorkerResponse.Answers convertToAnswers(List<String> words) {
        var answers = new CrackHashWorkerResponse.Answers();
        answers.setWords(words);
        return answers;
    }

    // Helper methods for calculating word range and generating words

    private int calculatePartSize(int maxWordLength, int partCount) {
        int wordSpace = calculateWordSpace(maxWordLength);
        return (int) Math.ceil((double) wordSpace / partCount);
    }

    private int calculateWordSpace(int maxWordLength) {
        int alphabetSize = 36; // 26 letters + 10 digits
        int wordSpace = 0;
        for (int i = 1; i <= maxWordLength; i++) {
            wordSpace += Math.pow(alphabetSize, i);
        }
        return wordSpace;
    }

    private List<String> generateWords(int startIdx, int endIdx, int maxWordLength, List<String> alphabet) {

        ICombinatoricsVector<String> vector = CombinatoricsFactory.createVector(alphabet);
        Generator<String> gen = CombinatoricsFactory.createPermutationWithRepetitionGenerator(vector, maxWordLength);
        var words = new ArrayList<String>();
        for (var word : gen.generateObjectsRange(startIdx, endIdx)) {
            words.add(String.join("", word.getVector()));
        }

        return words;
    }
}
