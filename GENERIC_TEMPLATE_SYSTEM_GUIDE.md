# Generic Template System Guide

## Overview

The Generic Template System allows you to create email templates for ANY entity type in the system (CallRequest, Order, User, Product, etc.) without hardcoding entity-specific logic. The system uses reflection to dynamically extract variables from any entity and process templates.

## Architecture

### 1. Backend Components

#### Domain Layer
- **StoreEmailTemplateModel**: Entity with `entityType` field to specify which entity the template is for
- **TemplateVariableDefinition**: Entity for storing dynamic variable definitions (optional, for future use)

#### Service Layer
- **GenericTemplateService**: Core service that handles template processing for any entity type
  - `processTemplate(templateBody, entity, entityType)`: Process template with entity object
  - `extractVariables(entity, entityType)`: Extract variables from any entity using reflection
  - `getAvailableVariables(entityType)`: Get available variables for an entity type

#### Facade Layer
- **EmailTemplateFacade**: Provides `getAvailableVariables(entityType)` endpoint

#### Controller Layer
- **EmailTemplateController**: REST endpoint `/api/v1/email-templates/variables/{entityType}`

### 2. Frontend Components

#### Type Definitions (`btc-store/src/types/templateVariable.ts`)
```typescript
export const ENTITY_TYPE_VARIABLES: Record<string, EntityTypeVariables> = {
  CallRequest: { ... },
  Order: { ... },
  User: { ... },
  Product: { ... }
};
```

#### Email Template Form
- Entity type dropdown to select which entity the template is for
- Dynamic variable list based on selected entity type
- Preview functionality with example data

### 3. RabbitMQ Integration

#### Event Structure
```java
Map<String, Object> event = new HashMap<>();
event.put("entityType", "CallRequest");  // Generic entity type
event.put("variables", variables);        // Extracted variables
event.put("userGroupEmails", emails);
event.put("templateCode", "template_code");
```

#### Listener
- Receives events with `entityType` and `variables`
- Processes templates using the generic variables
- Sends emails with processed content

## How to Add a New Entity Type

### Step 1: Backend - No Changes Needed!
The system uses reflection, so it automatically works with any entity. However, you can customize available variables in `GenericTemplateServiceImpl.getAvailableVariables()`:

```java
case "YourEntity":
    variables.put("fieldName", "Field Label");
    variables.put("anotherField", "Another Label");
    break;
```

### Step 2: Frontend - Add Entity Type Definition

Edit `btc-store/src/types/templateVariable.ts`:

```typescript
export const ENTITY_TYPE_VARIABLES: Record<string, EntityTypeVariables> = {
  // ... existing types
  YourEntity: {
    entityType: 'YourEntity',
    entityLabel: 'Your Entity Label',
    variables: [
      { key: 'fieldName', label: 'Field Label', example: 'Example Value', type: 'STRING' },
      { key: 'anotherField', label: 'Another Label', example: 'Example', type: 'NUMBER' },
    ],
  },
};
```

### Step 3: Use in Your Service

```java
@Service
public class YourEntityService {
    
    private final GenericTemplateService genericTemplateService;
    private final RabbitTemplate rabbitTemplate;
    
    public void sendNotification(YourEntity entity) {
        // Extract variables using generic service
        Map<String, Object> variables = genericTemplateService.extractVariables(entity, "YourEntity");
        
        // Create event
        Map<String, Object> event = new HashMap<>();
        event.put("entityType", "YourEntity");
        event.put("variables", variables);
        event.put("userGroupEmails", getRecipientEmails());
        event.put("templateCode", "your_entity_notification");
        
        // Send to RabbitMQ
        rabbitTemplate.convertAndSend("call.request.exchange", "call.request.routing.key", event);
    }
}
```

## Template Variable Syntax

Templates use `{{variableName}}` syntax:

```html
<h1>Hello {{customerName}}</h1>
<p>Your order {{orderNumber}} has been {{status}}.</p>
<p>Total: {{totalAmount}}</p>
```

### Nested Variables
The system supports nested object properties:

```html
{{user.firstName}} {{user.lastName}}
{{order.customer.email}}
```

### Date Formatting
Dates are automatically formatted as `dd.MM.yyyy HH:mm`:

```html
Created: {{createdDate}}
```

### Boolean Values
Booleans are converted to "Evet" / "Hayır":

```html
Active: {{isActive}}
```

## Example: Creating a Template for Orders

### 1. Create Template in Admin Panel
- Navigate to Email Templates
- Click "Yeni Template"
- Select Entity Type: "Sipariş"
- Enter template code: `order_confirmation`
- Enter subject: `Siparişiniz Onaylandı - {{orderNumber}}`
- Enter body:
```html
<h2>Merhaba {{customerName}},</h2>
<p>{{orderNumber}} numaralı siparişiniz onaylanmıştır.</p>
<p><strong>Toplam Tutar:</strong> {{totalAmount}}</p>
<p><strong>Teslimat Adresi:</strong> {{shippingAddress}}</p>
<p><strong>Sipariş Tarihi:</strong> {{orderDate}}</p>
```

### 2. Use in OrderService
```java
@Service
public class OrderServiceImpl implements OrderService {
    
    private final GenericTemplateService genericTemplateService;
    private final RabbitTemplate rabbitTemplate;
    
    @Override
    public void confirmOrder(OrderModel order) {
        // ... order confirmation logic
        
        // Send notification
        Map<String, Object> variables = genericTemplateService.extractVariables(order, "Order");
        
        Map<String, Object> event = new HashMap<>();
        event.put("entityType", "Order");
        event.put("variables", variables);
        event.put("userGroupEmails", List.of(order.getCustomerEmail()));
        event.put("templateCode", "order_confirmation");
        
        rabbitTemplate.convertAndSend("call.request.exchange", "call.request.routing.key", event);
    }
}
```

## API Endpoints

### Get Available Variables for Entity Type
```
GET /api/v1/email-templates/variables/{entityType}
```

Response:
```json
{
  "status": "SUCCESS",
  "data": {
    "orderNumber": "Sipariş No",
    "customerName": "Müşteri Adı",
    "totalAmount": "Toplam Tutar",
    "orderDate": "Sipariş Tarihi"
  }
}
```

### Create/Update Template
```
POST /api/v1/email-templates
```

Request Body:
```json
{
  "code": "order_confirmation",
  "templateName": "Order Confirmation",
  "entityType": "Order",
  "subject": "Siparişiniz Onaylandı - {{orderNumber}}",
  "body": "<h2>Merhaba {{customerName}},</h2>...",
  "isActive": true
}
```

## Benefits

1. **No Code Changes**: Add new entity types without modifying backend code
2. **Reflection-Based**: Automatically extracts all fields from any entity
3. **Type-Safe**: Frontend provides type definitions for better DX
4. **Flexible**: Supports nested objects, dates, booleans, etc.
5. **Maintainable**: Single service handles all template processing
6. **Scalable**: Easy to add new entity types and variables

## Testing

### Test Template Processing
```java
@Test
public void testGenericTemplateProcessing() {
    OrderModel order = new OrderModel();
    order.setOrderNumber("ORD-001");
    order.setCustomerName("Ahmet Yılmaz");
    order.setTotalAmount(1250.00);
    
    String template = "Order {{orderNumber}} for {{customerName}}: {{totalAmount}}";
    String result = genericTemplateService.processTemplate(template, order, "Order");
    
    assertEquals("Order ORD-001 for Ahmet Yılmaz: 1250.0", result);
}
```

### Test Variable Extraction
```java
@Test
public void testVariableExtraction() {
    OrderModel order = new OrderModel();
    order.setOrderNumber("ORD-001");
    order.setCustomerName("Ahmet Yılmaz");
    
    Map<String, Object> variables = genericTemplateService.extractVariables(order, "Order");
    
    assertEquals("ORD-001", variables.get("orderNumber"));
    assertEquals("Ahmet Yılmaz", variables.get("customerName"));
}
```

## Future Enhancements

1. **Database-Driven Variables**: Store variable definitions in `TemplateVariableDefinition` table
2. **Custom Formatters**: Add custom formatters for currency, dates, etc.
3. **Conditional Logic**: Support `{{#if}}` conditions in templates
4. **Loops**: Support `{{#each}}` for collections
5. **Template Inheritance**: Support template extends/includes
6. **Multi-Language**: Support i18n in templates
7. **Template Versioning**: Track template changes over time
8. **A/B Testing**: Test different template versions

## Troubleshooting

### Variables Not Replaced
- Check variable name matches entity field name (case-sensitive)
- Ensure entity has getter methods for the field
- Check template syntax: `{{variableName}}` (no spaces)

### Nested Variables Not Working
- Ensure nested object is not null
- Check nested object has getter methods
- Use dot notation: `{{parent.child}}`

### Date Format Issues
- Dates are formatted as `dd.MM.yyyy HH:mm`
- To customize, modify `DATE_FORMAT` in `GenericTemplateServiceImpl`

## Conclusion

The Generic Template System provides a powerful, flexible way to create email templates for any entity type without code changes. It uses reflection to automatically extract variables and process templates, making it easy to add new entity types and maintain existing ones.
