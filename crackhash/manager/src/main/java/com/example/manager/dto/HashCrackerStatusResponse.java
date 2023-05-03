package com.example.manager.dto;

import com.example.manager.model.RequestStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class HashCrackerStatusResponse {
    private String status;
    private String data;
    public HashCrackerStatusResponse(RequestStatus rs) {
        status = rs.getStatus().toString();
        data = rs.getAnswers();
    }
}
