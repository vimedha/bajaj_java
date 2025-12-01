package com.bajaj.finserv.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookRequest {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("regNo")
    private String regNo;
    
    @JsonProperty("email")
    private String email;
}