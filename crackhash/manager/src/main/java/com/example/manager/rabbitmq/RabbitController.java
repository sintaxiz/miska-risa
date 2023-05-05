package com.example.manager.rabbitmq;

import com.example.manager.model.HashCrackerServiceImpl;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.nsu.ccfit.schema.crack_hash_response.CrackHashWorkerResponse;

@Component
@RabbitListener(queues = "${rabbitmq.response.queue}", id = "manager")
public class RabbitController {
    final
    HashCrackerServiceImpl managerService;

    public RabbitController(HashCrackerServiceImpl managerService) {
        this.managerService = managerService;
    }

    @RabbitHandler
    public void receiver(CrackHashWorkerResponse response) {
        managerService.submitHashes(response);
    }
}
