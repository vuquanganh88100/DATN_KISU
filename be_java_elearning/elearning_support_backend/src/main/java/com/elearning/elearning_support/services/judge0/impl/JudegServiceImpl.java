package com.elearning.elearning_support.services.judge0.impl;

import com.elearning.elearning_support.dtos.judge0.JudgeRequestDto;
import com.elearning.elearning_support.dtos.judge0.JudgeResponseDto;
import com.elearning.elearning_support.dtos.judge0.JudgeResultDto;
import com.elearning.elearning_support.services.judge0.JudgeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JudegServiceImpl implements JudgeService {
    @Value("${spring.judge0.url}")
    private String judge0Url;

    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    public String submit(JudgeRequestDto request) {
        String url = judge0Url + "/submissions?wait=true";

        ResponseEntity<JudgeResponseDto> res =
                restTemplate.postForEntity(url, request, JudgeResponseDto.class);

        return res.getBody().getToken();
    }


    @Override
    public JudgeResultDto pollResult(String token) {
        String url = judge0Url + "/submissions/" + token;

        while (true) {
            JudgeResultDto res =
                    restTemplate.getForObject(url, JudgeResultDto.class);

            if (res.getStatus().getId() <= 2) {
                try { Thread.sleep(300); } catch (InterruptedException ignored) {}
                continue;
            }
            return res;
        }
    }


}
