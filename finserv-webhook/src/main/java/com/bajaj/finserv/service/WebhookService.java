package com.bajaj.finserv.service;

import com.bajaj.finserv.dto.WebhookRequest;
import com.bajaj.finserv.dto.WebhookResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
public class WebhookService {
    
    private static final String GENERATE_WEBHOOK_URL = 
        "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    
    private static final String SUBMIT_SOLUTION_URL = 
        "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    @Autowired
    private RestTemplate restTemplate;
    
   
    public WebhookResponse generateWebhook(String name, String regNo, String email) {
        try {
            log.info("Generating webhook for regNo: {}", regNo);
            
            WebhookRequest request = new WebhookRequest(name, regNo, email);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            HttpEntity<WebhookRequest> entity = new HttpEntity<>(request, headers);
            
            WebhookResponse response = restTemplate.postForObject(
                GENERATE_WEBHOOK_URL,
                entity,
                WebhookResponse.class
            );
            
            log.info("Webhook generated successfully. Webhook URL: {}", response.getWebhook());
            return response;
            
        } catch (Exception e) {
            log.error("Error generating webhook: ", e);
            throw new RuntimeException("Failed to generate webhook", e);
        }
    }
    
   
    public void submitSolution(String webhookUrl, String accessToken, String sqlQuery) {
        try {
            log.info("Submitting SQL solution to webhook");
            
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("finalQuery", sqlQuery);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", accessToken);
            
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);
            
            String response = restTemplate.postForObject(
                SUBMIT_SOLUTION_URL,
                entity,
                String.class
            );
            
            log.info("Solution submitted successfully. Response: {}", response);
            
        } catch (Exception e) {
            log.error("Error submitting solution: ", e);
            throw new RuntimeException("Failed to submit solution", e);
        }
    }
    
  
    public String getSQLQuery(String regNo) {
      
        String lastTwoDigits = regNo.substring(regNo.length() - 2);
        int lastDigits = Integer.parseInt(lastTwoDigits);
       
        String sqlQuery = """
            SELECT 
                d.DEPARTMENT_NAME,
                ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())), 2) AS AVERAGE_AGE,
                SUBSTRING_INDEX(
                    GROUP_CONCAT(
                        DISTINCT CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME)
                        ORDER BY e.EMP_ID
                        SEPARATOR ', '
                    ),
                    ',',
                    10
                ) AS EMPLOYEE_LIST
            FROM 
                DEPARTMENT d
            INNER JOIN 
                EMPLOYEE e ON d.DEPARTMENT_ID = e.DEPARTMENT
            INNER JOIN 
                PAYMENTS p ON e.EMP_ID = p.EMP_ID
            WHERE 
                p.AMOUNT > 70000
            GROUP BY 
                d.DEPARTMENT_ID, d.DEPARTMENT_NAME
            ORDER BY 
                d.DEPARTMENT_ID DESC
            """;
        
        log.info("Using SQL query for regNo: {} (last two digits: {})", regNo, lastDigits);
        return sqlQuery;
    }
}