-- Email Template for Product Contact Requests
-- This template is used when a customer contacts about a specific product

-- Insert email template for product contact requests
INSERT INTO email_template (
    id,
    code,
    name,
    description,
    subject,
    body,
    active,
    site_id,
    created_date,
    modified_date
) VALUES (
    nextval('email_template_seq'),
    'product_contact_request',
    'Ürün İletişim Talebi',
    'Bir müşteri ürün hakkında iletişime geçtiğinde ürün sorumlu kullanıcılarına gönderilen email',
    'Yeni Ürün İletişim Talebi: {{productName}}',
    '<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <style>
        body {
            font-family: Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 600px;
            margin: 0 auto;
            padding: 20px;
        }
        .header {
            background: linear-gradient(135deg, #1e3a8a 0%, #3b82f6 100%);
            color: white;
            padding: 30px;
            border-radius: 10px 10px 0 0;
            text-align: center;
        }
        .header h1 {
            margin: 0;
            font-size: 24px;
        }
        .content {
            background: #f9fafb;
            padding: 30px;
            border: 1px solid #e5e7eb;
            border-top: none;
        }
        .product-info {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border-left: 4px solid #3b82f6;
        }
        .product-info h2 {
            margin-top: 0;
            color: #1e3a8a;
            font-size: 20px;
        }
        .customer-info {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        .customer-info h3 {
            margin-top: 0;
            color: #1e3a8a;
            font-size: 18px;
            border-bottom: 2px solid #e5e7eb;
            padding-bottom: 10px;
        }
        .info-row {
            display: flex;
            padding: 10px 0;
            border-bottom: 1px solid #f3f4f6;
        }
        .info-row:last-child {
            border-bottom: none;
        }
        .info-label {
            font-weight: bold;
            color: #6b7280;
            min-width: 120px;
        }
        .info-value {
            color: #111827;
        }
        .message-box {
            background: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            border: 1px solid #e5e7eb;
        }
        .message-box h3 {
            margin-top: 0;
            color: #1e3a8a;
            font-size: 18px;
        }
        .message-content {
            background: #f9fafb;
            padding: 15px;
            border-radius: 6px;
            border-left: 3px solid #3b82f6;
            white-space: pre-wrap;
            word-wrap: break-word;
        }
        .footer {
            background: #f3f4f6;
            padding: 20px;
            text-align: center;
            border-radius: 0 0 10px 10px;
            color: #6b7280;
            font-size: 14px;
        }
        .action-button {
            display: inline-block;
            background: #3b82f6;
            color: white;
            padding: 12px 30px;
            text-decoration: none;
            border-radius: 6px;
            margin: 20px 0;
            font-weight: bold;
        }
        .action-button:hover {
            background: #2563eb;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>🔔 Yeni Ürün İletişim Talebi</h1>
    </div>
    
    <div class="content">
        <div class="product-info">
            <h2>📦 Ürün Bilgileri</h2>
            <div class="info-row">
                <span class="info-label">Ürün Adı:</span>
                <span class="info-value">{{productName}}</span>
            </div>
            <div class="info-row">
                <span class="info-label">Ürün Kodu:</span>
                <span class="info-value">{{productCode}}</span>
            </div>
            <div class="info-row">
                <span class="info-label">Açıklama:</span>
                <span class="info-value">{{productDescription}}</span>
            </div>
        </div>
        
        <div class="customer-info">
            <h3>👤 Müşteri Bilgileri</h3>
            <div class="info-row">
                <span class="info-label">Ad Soyad:</span>
                <span class="info-value">{{customerName}}</span>
            </div>
            <div class="info-row">
                <span class="info-label">E-posta:</span>
                <span class="info-value"><a href="mailto:{{customerEmail}}">{{customerEmail}}</a></span>
            </div>
            <div class="info-row">
                <span class="info-label">Telefon:</span>
                <span class="info-value"><a href="tel:{{customerPhone}}">{{customerPhone}}</a></span>
            </div>
            <div class="info-row">
                <span class="info-label">Tarih:</span>
                <span class="info-value">{{createdDate}}</span>
            </div>
        </div>
        
        <div class="message-box">
            <h3>💬 Müşteri Mesajı</h3>
            <div class="message-content">{{message}}</div>
        </div>
        
        <div style="text-align: center;">
            <a href="#" class="action-button">Talebi Görüntüle</a>
        </div>
    </div>
    
    <div class="footer">
        <p>Bu email otomatik olarak oluşturulmuştur.</p>
        <p>Ürün sorumlusu olarak bu ürün hakkındaki iletişim taleplerini alıyorsunuz.</p>
    </div>
</body>
</html>',
    true,
    (SELECT id FROM site WHERE code = 'default' LIMIT 1),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Note: Replace 'default' with your actual site code if different
-- You can also add multiple language versions by creating separate templates with language-specific codes
