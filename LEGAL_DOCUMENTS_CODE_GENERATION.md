# Yasal Doküman Kod Otomatik Oluşturma

## Backend Service Örneği

Kod alanı kullanıcı tarafından girilmeyecek, backend'de otomatik oluşturulacak.

### Java Service Örneği

```java
@Service
public class LegalDocumentService {
    
    @Autowired
    private LegalDocumentRepository repository;
    
    /**
     * Kod otomatik oluşturma
     * Format: {documentType}-{version}-{timestamp}
     * Örnek: kvkk-1.0-20240219143022
     */
    private String generateCode(LegalDocumentType documentType, String version) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String typeCode = documentType.name().toLowerCase().replace("_", "-");
        String versionCode = version.replace(".", "-");
        
        return String.format("%s-v%s-%s", typeCode, versionCode, timestamp);
    }
    
    /**
     * Alternatif: Daha kısa kod
     * Format: {documentType}-{version}
     * Örnek: kvkk-1.0
     * Not: Aynı tip ve versiyonda çakışma olabilir
     */
    private String generateShortCode(LegalDocumentType documentType, String version) {
        String typeCode = documentType.name().toLowerCase().replace("_", "-");
        return String.format("%s-%s", typeCode, version);
    }
    
    /**
     * Alternatif: UUID bazlı
     * Format: {documentType}-{uuid}
     * Örnek: kvkk-a1b2c3d4
     */
    private String generateUuidCode(LegalDocumentType documentType) {
        String typeCode = documentType.name().toLowerCase().replace("_", "-");
        String shortUuid = UUID.randomUUID().toString().substring(0, 8);
        return String.format("%s-%s", typeCode, shortUuid);
    }
    
    @Transactional
    public LegalDocumentModel save(LegalDocumentModel document) {
        // Yeni kayıt ise kod oluştur
        if (document.getId() == null || document.getCode() == null) {
            String code = generateCode(document.getDocumentType(), document.getVersion());
            document.setCode(code);
        }
        
        // Eğer güncel versiyon olarak işaretlendiyse, aynı tipteki diğerlerini false yap
        if (document.getIsCurrentVersion()) {
            repository.findByDocumentTypeAndSiteIdAndActiveTrue(
                document.getDocumentType(), 
                document.getSite().getId()
            ).forEach(existing -> {
                if (!existing.getId().equals(document.getId())) {
                    existing.setIsCurrentVersion(false);
                    repository.save(existing);
                }
            });
        }
        
        return repository.save(document);
    }
}
```

## Önerilen Format

**Timestamp bazlı (Önerilen):**
- Format: `{type}-v{version}-{timestamp}`
- Örnek: `kvkk-v1-0-20240219143022`
- Avantaj: Her zaman unique, sıralama kolay
- Dezavantaj: Uzun kod

**Kısa format:**
- Format: `{type}-{version}`
- Örnek: `kvkk-1.0`
- Avantaj: Okunabilir, kısa
- Dezavantaj: Aynı tip/versiyon çakışabilir

**UUID bazlı:**
- Format: `{type}-{uuid}`
- Örnek: `kvkk-a1b2c3d4`
- Avantaj: Kesinlikle unique
- Dezavantaj: Anlamsız karakterler

## Frontend Değişikliği

Frontend'de kod alanı kaldırıldı. Kullanıcı sadece:
- Doküman tipi
- Versiyon
- Başlık
- İçerik
- Diğer ayarlar

Backend otomatik olarak kod oluşturacak.

## Veritabanı

```sql
-- Kod artık unique (site_id ile değil)
ALTER TABLE legal_documents 
DROP INDEX uk_legal_doc_code_site,
ADD UNIQUE KEY uk_legal_doc_code (code);

-- Kod nullable olabilir (otomatik oluşturulacak)
ALTER TABLE legal_documents 
MODIFY COLUMN code VARCHAR(255) NULL;
```

## Test Senaryosu

1. Kullanıcı yeni KVKK dokümanı oluşturur
2. Versiyon: 1.0
3. Backend kod oluşturur: `kvkk-v1-0-20240219143022`
4. Kayıt başarılı

5. Kullanıcı aynı KVKK'yı günceller
6. Versiyon: 1.1
7. Backend yeni kod oluşturur: `kvkk-v1-1-20240219150530`
8. Eski versiyon `isCurrentVersion=false` olur
9. Yeni versiyon `isCurrentVersion=true` olur
