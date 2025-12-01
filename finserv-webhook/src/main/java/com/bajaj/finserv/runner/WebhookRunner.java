package com.bajaj.finserv.runner;

import com.bajaj.finserv.dto.WebhookResponse;
import com.bajaj.finserv.service.WebhookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class WebhookRunner implements ApplicationRunner {
    
    @Autowired
    private WebhookService webhookService;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("=".repeat(80));
        log.info("Starting Bajaj Finserv Webhook Application");
        log.info("=".repeat(80));
        
        try {
           
            String name = "Vimedha Chaturvedi";
            String regNo = "22BCE7796";
            String email = "vimedha.22bce7796@vitapstudent.ac.in";
            
            log.info("Step 1: Generating webhook with regNo: {}", regNo);
            WebhookResponse webhookResponse = webhookService.generateWebhook(name, regNo, email);
            
            if (webhookResponse == null) {
                log.error("Failed to get webhook response");
                return;
            }
            
            String webhookUrl = webhookResponse.getWebhook();
            String accessToken = webhookResponse.getAccessToken();
            
            log.info("✓ Webhook generated successfully");
            log.info("  - Webhook URL: {}", webhookUrl);
            log.info("  - Access Token: {}", accessToken.substring(0, Math.min(20, accessToken.length())) + "...");
            
           
            log.info("Step 2: Preparing SQL solution for regNo: {}", regNo);
            String sqlQuery = webhookService.getSQLQuery(regNo);
            
            log.info("✓ SQL query prepared");
            log.info("  - Query Type: Question 2 (Even last two digits)");
            
           
            log.info("Step 3: Submitting solution to webhook");
            webhookService.submitSolution(webhookUrl, accessToken, sqlQuery);
            
            log.info("✓ Solution submitted successfully!");
            log.info("=".repeat(80));
            log.info("Webhook flow completed successfully");
            log.info("=".repeat(80));
            
        } catch (Exception e) {
            log.error("Error during webhook execution: ", e);
            log.error("=".repeat(80));
        }
    }
}