-- Email Template'leri için SQL Script
-- Bu script, kullanıcıya ve gruba atama için email template'lerini ekler

-- 1. Gruba Atama Template'i
INSERT INTO email_templates (
    id,
    code,
    template_name,
    related_item,
    subject,
    body,
    active,
    site_id,
    created_date,
    modified_date
) VALUES (
    NEXTVAL('hibernate_sequence'),
    'call_request_assigned_to_group',
    'Görüşme Gruba Atandı',
    'CallRequestModel',
    'Grubunuza Yeni Görüşme Atandı - #{{id}}',
    '<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Grup Ataması</title>
    <style>
        body { font-family: ''Segoe UI'', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; padding: 30px; text-align: center; }
        .header h1 { margin: 0; font-size: 24px; font-weight: 600; }
        .content { padding: 30px; }
        .alert-box { background-color: #e8f5e9; border-left: 4px solid #4caf50; padding: 15px; margin-bottom: 20px; border-radius: 4px; }
        .alert-box p { margin: 0; color: #2e7d32; font-weight: 500; }
        .info-section { background-color: #f9f9f9; border-radius: 6px; padding: 20px; margin: 20px 0; }
        .info-row { display: flex; padding: 10px 0; border-bottom: 1px solid #e0e0e0; }
        .info-row:last-child { border-bottom: none; }
        .info-label { font-weight: 600; color: #555; min-width: 140px; }
        .info-value { color: #333; flex: 1; }
        .priority-urgent { color: #c62828; font-weight: bold; background-color: #ffebee; padding: 3px 8px; border-radius: 4px; }
        .priority-high { color: #d32f2f; font-weight: bold; }
        .priority-medium { color: #f57c00; font-weight: bold; }
        .priority-low { color: #388e3c; font-weight: bold; }
        .group-badge { display: inline-block; background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; padding: 5px 15px; border-radius: 20px; font-size: 14px; font-weight: 600; margin: 5px 0; }
        .button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; font-weight: 600; text-align: center; }
        .button:hover { opacity: 0.9; }
        .footer { background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #777; }
        .icon { display: inline-block; margin-right: 8px; }
        .action-note { background-color: #fff3e0; border-left: 4px solid #ff9800; padding: 15px; margin: 20px 0; border-radius: 4px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>👥 Gruba Yeni Atama</h1>
        </div>
        <div class="content">
            <div class="alert-box">
                <p><span class="icon">🎯</span> Grubunuza yeni bir görüşme talebi atandı!</p>
            </div>
            <p>Merhaba <strong>{{groupName}}</strong> Ekibi,</p>
            <p>Aşağıdaki görüşme talebi grubunuza atanmıştır. Lütfen ekip içinde uygun kişiye yönlendirme yapınız.</p>
            <div style="text-align: center; margin: 20px 0;">
                <span class="group-badge">{{groupName}}</span>
            </div>
            <div class="info-section">
                <div class="info-row">
                    <div class="info-label">📋 Talep No:</div>
                    <div class="info-value"><strong>#{{id}}</strong></div>
                </div>
                <div class="info-row">
                    <div class="info-label">👤 Müşteri:</div>
                    <div class="info-value">{{customerName}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">📞 Telefon:</div>
                    <div class="info-value">{{customerPhone}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">📧 E-posta:</div>
                    <div class="info-value">{{customerEmail}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">⚡ Öncelik:</div>
                    <div class="info-value priority-{{priorityClass}}">{{priority}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">💬 Mesaj:</div>
                    <div class="info-value">{{message}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">📅 Oluşturma:</div>
                    <div class="info-value">{{createdDate}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">👨‍💼 Atayan:</div>
                    <div class="info-value">{{assignedBy}}</div>
                </div>
            </div>
            <div class="action-note">
                <p style="margin: 0; color: #e65100;">
                    <strong>⚠️ Önemli:</strong> Bu talep grubunuza atanmıştır. Lütfen ekip içinde uygun bir kişiye atama yaparak süreci başlatınız.
                </p>
            </div>
            <p style="margin-top: 25px;"><strong>Sonraki Adımlar:</strong></p>
            <ul style="color: #555;">
                <li>Talebi değerlendirin</li>
                <li>Uygun ekip üyesine atama yapın</li>
                <li>Müşteri ile iletişim sürecini başlatın</li>
            </ul>
            <div style="text-align: center;">
                <a href="{{callRequestUrl}}" class="button">Görüşmeyi Görüntüle</a>
            </div>
        </div>
        <div class="footer">
            <p>Bu e-posta otomatik olarak gönderilmiştir. Lütfen yanıtlamayınız.</p>
            <p>&copy; 2024 BTC Store - Tüm hakları saklıdır.</p>
        </div>
    </div>
</body>
</html>',
    true,
    (SELECT id FROM sites WHERE code = 'btcstore' LIMIT 1),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);


-- 2. Kullanıcıya Atama Template'i
INSERT INTO email_templates (
    id,
    code,
    template_name,
    related_item,
    subject,
    body,
    active,
    site_id,
    created_date,
    modified_date
) VALUES (
    NEXTVAL('hibernate_sequence'),
    'call_request_assigned_to_user',
    'Görüşme Kullanıcıya Atandı',
    'CallRequestModel',
    'Size Yeni Görüşme Atandı - #{{id}}',
    '<!DOCTYPE html>
<html lang="tr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Görüşme Ataması</title>
    <style>
        body { font-family: ''Segoe UI'', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; margin: 0; padding: 0; }
        .container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }
        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; text-align: center; }
        .header h1 { margin: 0; font-size: 24px; font-weight: 600; }
        .content { padding: 30px; }
        .alert-box { background-color: #e8f4fd; border-left: 4px solid #2196F3; padding: 15px; margin-bottom: 20px; border-radius: 4px; }
        .alert-box p { margin: 0; color: #1976D2; font-weight: 500; }
        .info-section { background-color: #f9f9f9; border-radius: 6px; padding: 20px; margin: 20px 0; }
        .info-row { display: flex; padding: 10px 0; border-bottom: 1px solid #e0e0e0; }
        .info-row:last-child { border-bottom: none; }
        .info-label { font-weight: 600; color: #555; min-width: 140px; }
        .info-value { color: #333; flex: 1; }
        .priority-urgent { color: #c62828; font-weight: bold; background-color: #ffebee; padding: 3px 8px; border-radius: 4px; }
        .priority-high { color: #d32f2f; font-weight: bold; }
        .priority-medium { color: #f57c00; font-weight: bold; }
        .priority-low { color: #388e3c; font-weight: bold; }
        .button { display: inline-block; padding: 12px 30px; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; text-decoration: none; border-radius: 5px; margin-top: 20px; font-weight: 600; text-align: center; }
        .button:hover { opacity: 0.9; }
        .footer { background-color: #f5f5f5; padding: 20px; text-align: center; font-size: 12px; color: #777; }
        .icon { display: inline-block; margin-right: 8px; }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1>🎯 Yeni Görüşme Ataması</h1>
        </div>
        <div class="content">
            <div class="alert-box">
                <p><span class="icon">👤</span> Sayın {{assignedUserName}}, size yeni bir görüşme atandı!</p>
            </div>
            <p>Merhaba,</p>
            <p>Aşağıdaki görüşme talebinin takibi için size atama yapılmıştır. Lütfen en kısa sürede değerlendirmenizi yapınız.</p>
            <div class="info-section">
                <div class="info-row">
                    <div class="info-label">📋 Talep No:</div>
                    <div class="info-value"><strong>#{{id}}</strong></div>
                </div>
                <div class="info-row">
                    <div class="info-label">👤 Müşteri:</div>
                    <div class="info-value">{{customerName}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">📞 Telefon:</div>
                    <div class="info-value">{{customerPhone}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">📧 E-posta:</div>
                    <div class="info-value">{{customerEmail}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">⚡ Öncelik:</div>
                    <div class="info-value priority-{{priorityClass}}">{{priority}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">💬 Mesaj:</div>
                    <div class="info-value">{{message}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">📅 Oluşturma:</div>
                    <div class="info-value">{{createdDate}}</div>
                </div>
                <div class="info-row">
                    <div class="info-label">👨‍💼 Atayan:</div>
                    <div class="info-value">{{assignedBy}}</div>
                </div>
            </div>
            <p style="margin-top: 25px;"><strong>Yapmanız Gerekenler:</strong></p>
            <ul style="color: #555;">
                <li>Müşteri ile iletişime geçin</li>
                <li>Görüşme detaylarını sisteme kaydedin</li>
                <li>Durumu güncel tutun</li>
            </ul>
            <div style="text-align: center;">
                <a href="{{callRequestUrl}}" class="button">Görüşmeyi Görüntüle</a>
            </div>
        </div>
        <div class="footer">
            <p>Bu e-posta otomatik olarak gönderilmiştir. Lütfen yanıtlamayınız.</p>
            <p>&copy; 2024 BTC Store - Tüm hakları saklıdır.</p>
        </div>
    </div>
</body>
</html>',
    true,
    (SELECT id FROM sites WHERE code = 'btcstore' LIMIT 1),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Mevcut template'i güncelle (genel bildirim için)
UPDATE email_templates 
SET template_name = 'Genel Görüşme Bildirimi',
    subject = 'Yeni Görüşme Talebi - #{{id}}'
WHERE code = 'call_request_notification';

-- Frontend base URL parametresi (eğer yoksa)
INSERT INTO parameters (
    id,
    code,
    name,
    value,
    description,
    active,
    site_id,
    created_date,
    modified_date
)
SELECT 
    NEXTVAL('hibernate_sequence'),
    'frontend.base.url',
    'Frontend Base URL',
    'http://localhost:3000',
    'Frontend uygulamasının base URL''i (email template''lerinde kullanılır)',
    true,
    (SELECT id FROM sites WHERE code = 'btcstore' LIMIT 1),
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
WHERE NOT EXISTS (
    SELECT 1 FROM parameters WHERE code = 'frontend.base.url'
);

COMMIT;
