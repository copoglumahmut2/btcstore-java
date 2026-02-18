-- Call Request System için gerekli parametreler ve email template'leri

-- 1. Call Center Group Parametresi
-- Bu parametre, yeni call request geldiğinde hangi gruplara mail gönderileceğini belirler
-- Birden fazla grup için noktalı virgül (;) ile ayırın
INSERT INTO parameter_model (code, value, description, site_id, created_date, last_modified_date)
VALUES ('call.center.group', 'super_admin', 'Call request geldiğinde mail gönderilecek kullanıcı grupları (noktalı virgül ile ayırın)', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE value = 'super_admin', description = 'Call request geldiğinde mail gönderilecek kullanıcı grupları (noktalı virgül ile ayırın)';

-- 2. Email Template - Yeni Call Request Bildirimi
INSERT INTO email_template_model (code, name, subject, body, active, site_id, created_date, last_modified_date)
VALUES (
    'call_request_notification',
    'Yeni Call Request Bildirimi',
    '🔔 Yeni Arama Talebi - {{customerName}}',
    '<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Yeni Arama Talebi</title>
</head>
<body style="margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, ''Segoe UI'', Roboto, ''Helvetica Neue'', Arial, sans-serif; background-color: #f3f4f6;">
    <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f3f4f6; padding: 40px 20px;">
        <tr>
            <td align="center">
                <table width="600" cellpadding="0" cellspacing="0" style="background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);">
                    <!-- Header -->
                    <tr>
                        <td style="background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); padding: 40px 30px; text-align: center;">
                            <h1 style="margin: 0; color: #ffffff; font-size: 28px; font-weight: 700;">
                                🔔 Yeni Arama Talebi
                            </h1>
                            <p style="margin: 10px 0 0 0; color: #e0e7ff; font-size: 14px;">
                                Bir müşteri sizinle iletişime geçmek istiyor
                            </p>
                        </td>
                    </tr>
                    
                    <!-- Content -->
                    <tr>
                        <td style="padding: 40px 30px;">
                            <p style="margin: 0 0 30px 0; color: #374151; font-size: 16px; line-height: 1.6;">
                                Merhaba,
                            </p>
                            <p style="margin: 0 0 30px 0; color: #374151; font-size: 16px; line-height: 1.6;">
                                Yeni bir arama talebi oluşturuldu. Lütfen en kısa sürede müşteriyle iletişime geçin.
                            </p>
                            
                            <!-- Customer Info Card -->
                            <table width="100%" cellpadding="0" cellspacing="0" style="background-color: #f9fafb; border-radius: 8px; border: 1px solid #e5e7eb; margin-bottom: 20px;">
                                <tr>
                                    <td style="padding: 20px;">
                                        <h2 style="margin: 0 0 20px 0; color: #1f2937; font-size: 18px; font-weight: 600;">
                                            👤 Müşteri Bilgileri
                                        </h2>
                                        
                                        <!-- Name -->
                                        <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 15px;">
                                            <tr>
                                                <td style="padding: 12px; background-color: #ffffff; border-left: 3px solid #1e40af; border-radius: 4px;">
                                                    <span style="color: #6b7280; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; display: block; margin-bottom: 4px;">Ad Soyad</span>
                                                    <span style="color: #1f2937; font-size: 16px; font-weight: 600;">{{customerName}}</span>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Email -->
                                        <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 15px;">
                                            <tr>
                                                <td style="padding: 12px; background-color: #ffffff; border-left: 3px solid #1e40af; border-radius: 4px;">
                                                    <span style="color: #6b7280; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; display: block; margin-bottom: 4px;">📧 E-posta</span>
                                                    <a href="mailto:{{customerEmail}}" style="color: #2563eb; font-size: 16px; text-decoration: none; font-weight: 500;">{{customerEmail}}</a>
                                                </td>
                                            </tr>
                                        </table>
                                        
                                        <!-- Phone -->
                                        <table width="100%" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td style="padding: 12px; background-color: #ffffff; border-left: 3px solid #1e40af; border-radius: 4px;">
                                                    <span style="color: #6b7280; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; display: block; margin-bottom: 4px;">📞 Telefon</span>
                                                    <a href="tel:{{customerPhone}}" style="color: #2563eb; font-size: 16px; text-decoration: none; font-weight: 500;">{{customerPhone}}</a>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- Subject (if exists) -->
                            {{#if subject}}
                            <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 20px;">
                                <tr>
                                    <td style="padding: 15px; background-color: #fef3c7; border-left: 3px solid #f59e0b; border-radius: 4px;">
                                        <span style="color: #92400e; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; display: block; margin-bottom: 4px;">📋 Konu</span>
                                        <span style="color: #78350f; font-size: 16px; font-weight: 600;">{{subject}}</span>
                                    </td>
                                </tr>
                            </table>
                            {{/if}}
                            
                            <!-- Message (if exists) -->
                            {{#if message}}
                            <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 20px;">
                                <tr>
                                    <td style="padding: 15px; background-color: #f0fdf4; border-left: 3px solid #10b981; border-radius: 4px;">
                                        <span style="color: #065f46; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; display: block; margin-bottom: 8px;">💬 Mesaj</span>
                                        <p style="margin: 0; color: #047857; font-size: 14px; line-height: 1.6; white-space: pre-wrap;">{{message}}</p>
                                    </td>
                                </tr>
                            </table>
                            {{/if}}
                            
                            <!-- Status & Assignment Info -->
                            <table width="100%" cellpadding="0" cellspacing="0" style="margin-bottom: 30px;">
                                <tr>
                                    <td style="padding: 15px; background-color: #ede9fe; border-left: 3px solid #8b5cf6; border-radius: 4px;">
                                        <table width="100%" cellpadding="0" cellspacing="0">
                                            <tr>
                                                <td width="50%" style="padding-right: 10px;">
                                                    <span style="color: #5b21b6; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; display: block; margin-bottom: 4px;">🏷️ Durum</span>
                                                    <span style="color: #6b21a8; font-size: 14px; font-weight: 600;">{{status}}</span>
                                                </td>
                                                {{#if assignedGroup}}
                                                <td width="50%" style="padding-left: 10px; border-left: 1px solid #c4b5fd;">
                                                    <span style="color: #5b21b6; font-size: 12px; text-transform: uppercase; letter-spacing: 0.5px; display: block; margin-bottom: 4px;">👥 Atanan Grup</span>
                                                    <span style="color: #6b21a8; font-size: 14px; font-weight: 600;">{{assignedGroup}}</span>
                                                </td>
                                                {{/if}}
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- CTA Button -->
                            <table width="100%" cellpadding="0" cellspacing="0">
                                <tr>
                                    <td align="center" style="padding: 20px 0;">
                                        <a href="http://localhost:3000/admin/call-requests/{{id}}" style="display: inline-block; padding: 16px 40px; background: linear-gradient(135deg, #1e40af 0%, #3b82f6 100%); color: #ffffff; text-decoration: none; border-radius: 8px; font-size: 16px; font-weight: 600; box-shadow: 0 4px 6px rgba(30, 64, 175, 0.3);">
                                            📋 Detayları Görüntüle
                                        </a>
                                    </td>
                                </tr>
                            </table>
                            
                            <!-- Additional Info -->
                            <table width="100%" cellpadding="0" cellspacing="0" style="margin-top: 30px; padding-top: 20px; border-top: 1px solid #e5e7eb;">
                                <tr>
                                    <td>
                                        <p style="margin: 0; color: #6b7280; font-size: 13px; line-height: 1.6;">
                                            <strong>💡 İpucu:</strong> Müşteriye en kısa sürede dönüş yaparak memnuniyeti artırabilirsiniz.
                                        </p>
                                    </td>
                                </tr>
                            </table>
                        </td>
                    </tr>
                    
                    <!-- Footer -->
                    <tr>
                        <td style="background-color: #f9fafb; padding: 30px; text-align: center; border-top: 1px solid #e5e7eb;">
                            <p style="margin: 0 0 10px 0; color: #6b7280; font-size: 13px;">
                                Bu mail otomatik olarak gönderilmiştir.
                            </p>
                            <p style="margin: 0; color: #9ca3af; font-size: 12px;">
                                © 2025 BTC Store - Tüm hakları saklıdır.
                            </p>
                        </td>
                    </tr>
                </table>
            </td>
        </tr>
    </table>
</body>
</html>',
    true,
    1,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE 
    name = 'Yeni Call Request Bildirimi',
    subject = '🔔 Yeni Arama Talebi - {{customerName}}',
    body = VALUES(body),
    active = true;

-- 3. Email Template - Gruba Atama Bildirimi
INSERT INTO email_template_model (code, name, subject, body, active, site_id, created_date, last_modified_date)
VALUES (
    'call_request_assigned_to_group',
    'Call Request Gruba Atandı',
    'Size Yeni Bir Görev Atandı: {{customerName}}',
    '<html>
<head>
    <style>
        body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }
        .container { max-width: 600px; margin: 0 auto; padding: 20px; }
        .header { background-color: #2563eb; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
        .content { background-color: #f9fafb; padding: 20px; border: 1px solid #e5e7eb; }
        .info-row { margin: 10px 0; padding: 10px; background-color: white; border-left: 3px solid #2563eb; }
        .label { font-weight: bold; color: #2563eb; }
        .footer { text-align: center; padding: 20px; color: #6b7280; font-size: 12px; }
        .button { display: inline-block; padding: 12px 24px; background-color: #2563eb; color: white; text-decoration: none; border-radius: 5px; margin: 20px 0; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>📋 Yeni Görev Ataması</h1>
        </div>
        <div class="content">
            <p>Merhaba,</p>
            <p>Grubunuza yeni bir arama talebi atandı:</p>
            
            <div class="info-row">
                <span class="label">👤 Müşteri:</span> {{customerName}}
            </div>
            
            <div class="info-row">
                <span class="label">📞 Telefon:</span> {{customerPhone}}
            </div>
            
            <div class="info-row">
                <span class="label">👥 Atanan Grup:</span> {{assignedGroup}}
            </div>
            
            <div style="text-align: center;">
                <a href="http://localhost:3000/admin/call-requests/{{id}}" class="button">
                    Görevi Görüntüle
                </a>
            </div>
        </div>
        <div class="footer">
            <p>Bu mail otomatik olarak gönderilmiştir.</p>
        </div>
    </div>
</body>
</html>',
    true,
    1,
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE 
    name = 'Call Request Gruba Atandı',
    subject = 'Size Yeni Bir Görev Atandı: {{customerName}}',
    body = VALUES(body),
    active = true;
