
package com.mynoano.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.List;


@Service
@Primary
public class BrevoOtpSender implements OtpSender {

    @Value("${brevo.api-key}")
    private String apiKey;

    @Value("${brevo.sender-email}")
    private String senderEmail;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public void send(String email, String phone, String code) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
                "sender", Map.of(
                        "name", "MynoAno",
                        "email", senderEmail
                ),
                "to", List.of(
                        Map.of("email", email)
                ),
                "subject", "Your MynoAno verification code",
                "textContent",
                "Your MynoAno verification code is: " + code
                        + "\n\nThis code is valid for a limited time. "
                        + "Do not share it with anyone. Enjoy your visit in here hahahhahah....."
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        restTemplate.postForEntity(
                url,
                request,
                String.class
        );
    }
}
