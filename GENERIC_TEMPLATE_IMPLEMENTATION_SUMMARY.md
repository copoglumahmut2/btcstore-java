# Generic Template System - Implementation Summary

## What Was Done

The email template system has been made fully generic to support ANY entity type (CallRequest, Order, User, Product, etc.) without hardcoding entity-specific logic.

## Changes Made

### Backend Changes

#### 1. Domain Layer
- ✅ **StoreEmailTemplateModel**: Added `entityType` field
- ✅ **StoreEmailTemplateData**: Added `entityType` field
- ✅ **TemplateVariableDefinition**: Created entity for future dynamic variable definitions

#### 2. Service Layer
- ✅ **GenericTemplateService**: Interface for generic template processing
- ✅ **GenericTemplateServiceImpl**: Implementation using reflection
  - Extracts variables from any entity dynamically
  - Processes templates with `{{variable}}` syntax
  - Supports nested objects, dates, booleans
  - Provides available variables per entity type

#### 3. Facade Layer
- ✅ **EmailTemplateFacade**: Added `getAvailableVariables(entityType)` method
- ✅ **EmailTemplateFacadeImpl**: Implementation with GenericTemplateService injection

#### 4. Controller Layer
- ✅ **EmailTemplateController**: Added `/variables/{entityType}` endpoint

#### 5. RabbitMQ Integration
- ✅ **CallRequestListener**: Updated to handle generic entity types
- ✅ **EmailRequestDto**: Added `entityType` field
- ✅ **CallRequestServiceImpl**: Uses GenericTemplateService for variable extraction

### Frontend Changes

#### 1. Type Definitions
- ✅ **templateVariable.ts**: Created with entity type definitions
  - CallRequest variables
  - Order variables
  - User variables
  - Product variables
  - Helper functions: `getVariablesForEntityType()`, `getAllEntityTypes()`

#### 2. Email Template Form
- ✅ **EmailTemplateForm.tsx**: Added entity type selection
  - Entity type dropdown
  - Dynamic variable list based on selected entity
  - Preview with example data
  - Proper state management for entity type changes

## How It Works

### 1. Template Creation Flow
```
User selects entity type (e.g., "Order")
  ↓
Frontend shows available variables for Order
  ↓
User creates template with {{orderNumber}}, {{customerName}}, etc.
  ↓
Template saved with entityType = "Order"
```

### 2. Template Processing Flow
```
Service creates/updates entity (e.g., Order)
  ↓
Service calls GenericTemplateService.extractVariables(order, "Order")
  ↓
Variables extracted using reflection
  ↓
Event sent to RabbitMQ with entityType and variables
  ↓
RabbitMQ listener processes template
  ↓
Email sent with processed content
```

### 3. Variable Extraction (Reflection-Based)
```java
// Automatically extracts ALL fields from any entity
Map<String, Object> variables = genericTemplateService.extractVariables(entity, "Order");

// Result:
{
  "orderNumber": "ORD-001",
  "customerName": "Ahmet Yılmaz",
  "totalAmount": 1250.00,
  "orderDate": "18.02.2024 10:30",
  "status": "Onaylandı"
}
```

## Key Features

### 1. Zero Code Changes for New Entity Types
- Just add entity type definition in frontend `templateVariable.ts`
- Backend automatically extracts variables using reflection
- No service modifications needed

### 2. Flexible Variable System
- Supports primitive types (String, Number, Boolean)
- Supports Date formatting
- Supports nested objects (`{{user.firstName}}`)
- Supports getter methods

### 3. Type-Safe Frontend
- TypeScript definitions for all entity types
- Autocomplete for variables
- Example values for preview

### 4. Event-Driven Architecture
- RabbitMQ handles all email sending
- Decoupled from main application
- Scalable and maintainable

## Example: Adding a New Entity Type

### Step 1: Frontend (templateVariable.ts)
```typescript
Invoice: {
  entityType: 'Invoice',
  entityLabel: 'Fatura',
  variables: [
    { key: 'invoiceNumber', label: 'Fatura No', example: 'INV-2024-001', type: 'STRING' },
    { key: 'amount', label: 'Tutar', example: '5.000,00 TL', type: 'NUMBER' },
    { key: 'dueDate', label: 'Vade Tarihi', example: '01.03.2024', type: 'DATE' },
  ],
}
```

### Step 2: Backend (Optional - for custom labels)
```java
// In GenericTemplateServiceImpl.getAvailableVariables()
case "Invoice":
    variables.put("invoiceNumber", "Fatura No");
    variables.put("amount", "Tutar");
    variables.put("dueDate", "Vade Tarihi");
    break;
```

### Step 3: Use in Service
```java
@Service
public class InvoiceService {
    private final GenericTemplateService genericTemplateService;
    private final RabbitTemplate rabbitTemplate;
    
    public void sendInvoice(InvoiceModel invoice) {
        Map<String, Object> variables = genericTemplateService.extractVariables(invoice, "Invoice");
        
        Map<String, Object> event = new HashMap<>();
        event.put("entityType", "Invoice");
        event.put("variables", variables);
        event.put("userGroupEmails", List.of(invoice.getCustomerEmail()));
        event.put("templateCode", "invoice_notification");
        
        rabbitTemplate.convertAndSend("call.request.exchange", "call.request.routing.key", event);
    }
}
```

That's it! No other changes needed.

## Files Modified

### Backend
1. `btcstore/domain/src/main/java/com/btc_store/domain/model/store/StoreEmailTemplateModel.java`
2. `btcstore/domain/src/main/java/com/btc_store/domain/data/store/StoreEmailTemplateData.java`
3. `btcstore/facade/src/main/java/com/btc_store/facade/EmailTemplateFacade.java`
4. `btcstore/facade/src/main/java/com/btc_store/facade/impl/EmailTemplateFacadeImpl.java`
5. `btcstore/webapp/src/main/java/com/btc_store/controller/v1/EmailTemplateController.java`

### Frontend
1. `btc-store/src/types/templateVariable.ts` (created)
2. `btc-store/src/views/admin/EmailTemplateForm.tsx`

### Already Implemented (from previous work)
1. `btcstore/service/src/main/java/com/btc_store/service/GenericTemplateService.java`
2. `btcstore/service/src/main/java/com/btc_store/service/impl/GenericTemplateServiceImpl.java`
3. `btcstore/domain/src/main/java/com/btc_store/domain/entity/TemplateVariableDefinition.java`
4. `btcstorerabbit/src/main/java/com/btc_store/rabbit/listener/CallRequestListener.java`
5. `btcstorerabbit/src/main/java/com/btc_store/rabbit/dto/EmailRequestDto.java`

## Testing

### Test the System
1. Start backend: `mvn spring-boot:run` (in btcstore)
2. Start RabbitMQ project: `mvn spring-boot:run` (in btcstorerabbit)
3. Start frontend: `npm run dev` (in btc-store)
4. Navigate to: http://localhost:3000/admin/email-templates
5. Create a new template:
   - Select entity type (e.g., "Call Request")
   - See available variables update
   - Create template with variables
   - Preview template
   - Save

### Test Email Sending
1. Create a call request from frontend
2. Check RabbitMQ logs for event processing
3. Check email sent with processed template

## API Endpoints

### Get Available Variables
```
GET /api/v1/email-templates/variables/CallRequest
GET /api/v1/email-templates/variables/Order
GET /api/v1/email-templates/variables/User
GET /api/v1/email-templates/variables/Product
```

### Create/Update Template
```
POST /api/v1/email-templates
Body: {
  "code": "order_confirmation",
  "templateName": "Order Confirmation",
  "entityType": "Order",
  "subject": "Order {{orderNumber}} Confirmed",
  "body": "<h1>Hello {{customerName}}</h1>...",
  "isActive": true
}
```

## Benefits

✅ **Generic**: Works with any entity type
✅ **No Code Changes**: Add new types without backend modifications
✅ **Reflection-Based**: Automatically extracts all fields
✅ **Type-Safe**: TypeScript definitions for frontend
✅ **Maintainable**: Single service handles all processing
✅ **Scalable**: Easy to extend
✅ **Event-Driven**: Decoupled architecture

## Next Steps

1. Test with different entity types (Order, User, Product)
2. Add more entity types as needed
3. Consider database-driven variable definitions (TemplateVariableDefinition)
4. Add template versioning
5. Add conditional logic support ({{#if}})
6. Add loop support ({{#each}})
7. Add multi-language support

## Conclusion

The template system is now fully generic and can handle any entity type without code changes. Simply add the entity type definition in the frontend, and the backend will automatically extract variables using reflection.
