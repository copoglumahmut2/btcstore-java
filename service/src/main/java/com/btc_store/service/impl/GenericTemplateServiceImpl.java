package com.btc_store.service.impl;

import com.btc_store.service.GenericTemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class GenericTemplateServiceImpl implements GenericTemplateService {
    
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{([^}]+)\\}\\}");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    
    @Override
    public String processTemplate(String templateBody, Object entity, String entityType) {
        if (templateBody == null || entity == null) {
            return templateBody;
        }
        
        Map<String, Object> variables = extractVariables(entity, entityType);
        return processTemplate(templateBody, variables);
    }
    
    @Override
    public String processTemplate(String templateBody, Map<String, Object> variables) {
        if (templateBody == null || variables == null) {
            return templateBody;
        }
        
        String result = templateBody;
        Matcher matcher = VARIABLE_PATTERN.matcher(templateBody);
        
        while (matcher.find()) {
            String variableKey = matcher.group(1).trim();
            Object value = getNestedValue(variables, variableKey);
            
            if (value != null) {
                String replacement = formatValue(value);
                result = result.replace("{{" + variableKey + "}}", replacement);
            }
        }
        
        return result;
    }
    
    @Override
    public Map<String, Object> extractVariables(Object entity, String entityType) {
        return extractVariablesWithDepth(entity, entityType, new HashSet<>(), 0, 3);
    }
    
    private Map<String, Object> extractVariablesWithDepth(Object entity, String entityType, Set<Object> visited, int depth, int maxDepth) {
        Map<String, Object> variables = new HashMap<>();
        
        if (entity == null || depth > maxDepth) {
            return variables;
        }
        
        // Circular reference kontrolü
        if (visited.contains(entity)) {
            return variables;
        }
        visited.add(entity);
        
        try {
            // Get all fields from entity
            Class<?> clazz = entity.getClass();
            
            // Process all fields
            while (clazz != null && clazz != Object.class) {
                for (Field field : clazz.getDeclaredFields()) {
                    try {
                        field.setAccessible(true);
                        Object value = field.get(entity);
                        
                        if (value != null && !visited.contains(value)) {
                            variables.put(field.getName(), value);
                            
                            // If value is an object, extract nested properties (with depth limit)
                            if (!isPrimitiveOrWrapper(value.getClass()) && 
                                !value.getClass().equals(String.class) &&
                                !value.getClass().equals(Date.class) &&
                                !value.getClass().getName().startsWith("java.") &&
                                !value.getClass().getName().startsWith("javax.") &&
                                depth < maxDepth) {
                                
                                Map<String, Object> nestedVars = extractVariablesWithDepth(value, null, visited, depth + 1, maxDepth);
                                for (Map.Entry<String, Object> entry : nestedVars.entrySet()) {
                                    variables.put(field.getName() + "." + entry.getKey(), entry.getValue());
                                }
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Could not access field: {}", field.getName());
                    }
                }
                clazz = clazz.getSuperclass();
            }
            
            // Also try getter methods
            for (Method method : entity.getClass().getMethods()) {
                if (method.getName().startsWith("get") && 
                    method.getParameterCount() == 0 &&
                    !method.getName().equals("getClass")) {
                    
                    try {
                        String propertyName = method.getName().substring(3);
                        propertyName = Character.toLowerCase(propertyName.charAt(0)) + propertyName.substring(1);
                        
                        if (!variables.containsKey(propertyName)) {
                            Object value = method.invoke(entity);
                            if (value != null && !visited.contains(value)) {
                                variables.put(propertyName, value);
                            }
                        }
                    } catch (Exception e) {
                        log.debug("Could not invoke method: {}", method.getName());
                    }
                }
            }
            
        } catch (Exception e) {
            log.error("Error extracting variables from entity", e);
        }
        
        return variables;
    }
    
    @Override
    public Map<String, String> getAvailableVariables(String entityType) {
        // This could be loaded from database (TemplateVariableDefinition)
        // For now, return common variables
        Map<String, String> variables = new HashMap<>();
        
        switch (entityType) {
            case "CallRequest":
                variables.put("customerName", "Müşteri Adı");
                variables.put("customerEmail", "Müşteri Email");
                variables.put("customerPhone", "Müşteri Telefon");
                variables.put("subject", "Konu");
                variables.put("message", "Mesaj");
                variables.put("assignedGroup", "Atanan Grup");
                variables.put("status", "Durum");
                break;
                
            case "Order":
                variables.put("orderNumber", "Sipariş No");
                variables.put("customerName", "Müşteri Adı");
                variables.put("totalAmount", "Toplam Tutar");
                variables.put("orderDate", "Sipariş Tarihi");
                variables.put("status", "Durum");
                break;
                
            case "User":
                variables.put("username", "Kullanıcı Adı");
                variables.put("email", "Email");
                variables.put("firstName", "Ad");
                variables.put("lastName", "Soyad");
                break;
                
            case "Product":
                variables.put("productName", "Ürün Adı");
                variables.put("productCode", "Ürün Kodu");
                variables.put("price", "Fiyat");
                variables.put("category", "Kategori");
                break;
                
            default:
                // Generic variables
                variables.put("id", "ID");
                variables.put("createdDate", "Oluşturulma Tarihi");
                variables.put("updatedDate", "Güncellenme Tarihi");
                break;
        }
        
        return variables;
    }
    
    private Object getNestedValue(Map<String, Object> variables, String path) {
        if (path.contains(".")) {
            String[] parts = path.split("\\.", 2);
            Object value = variables.get(parts[0]);
            
            if (value instanceof Map) {
                return getNestedValue((Map<String, Object>) value, parts[1]);
            } else if (value != null) {
                // Try to extract from object (with protection)
                try {
                    Map<String, Object> nestedVars = extractVariablesWithDepth(value, null, new HashSet<>(), 0, 2);
                    return getNestedValue(nestedVars, parts[1]);
                } catch (Exception e) {
                    return null;
                }
            }
            return null;
        }
        
        return variables.get(path);
    }
    
    private String formatValue(Object value) {
        if (value == null) {
            return "";
        }
        
        if (value instanceof Date) {
            return DATE_FORMAT.format((Date) value);
        }
        
        if (value instanceof Boolean) {
            return ((Boolean) value) ? "Evet" : "Hayır";
        }
        
        return value.toString();
    }
    
    private boolean isPrimitiveOrWrapper(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz.equals(Boolean.class) ||
               clazz.equals(Integer.class) ||
               clazz.equals(Long.class) ||
               clazz.equals(Double.class) ||
               clazz.equals(Float.class) ||
               clazz.equals(Short.class) ||
               clazz.equals(Byte.class) ||
               clazz.equals(Character.class);
    }
}
