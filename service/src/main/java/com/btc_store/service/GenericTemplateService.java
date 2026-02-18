package com.btc_store.service;

import java.util.Map;

public interface GenericTemplateService {
    
    /**
     * Process template with dynamic variables from any object
     * 
     * @param templateBody Template body with {{variable}} placeholders
     * @param entity Any entity object (CallRequest, Order, User, Product, etc.)
     * @param entityType Entity type identifier
     * @return Processed template with replaced variables
     */
    String processTemplate(String templateBody, Object entity, String entityType);
    
    /**
     * Process template with manual variable map
     * 
     * @param templateBody Template body with {{variable}} placeholders
     * @param variables Map of variable key-value pairs
     * @return Processed template with replaced variables
     */
    String processTemplate(String templateBody, Map<String, Object> variables);
    
    /**
     * Extract variables from entity using reflection and variable definitions
     * 
     * @param entity Any entity object
     * @param entityType Entity type identifier
     * @return Map of variable key-value pairs
     */
    Map<String, Object> extractVariables(Object entity, String entityType);
    
    /**
     * Get available variables for entity type
     * 
     * @param entityType Entity type identifier
     * @return Map of variable definitions
     */
    Map<String, String> getAvailableVariables(String entityType);
}
