package com.btc_store.facade.impl;

import com.btc_store.domain.data.custom.LegalDocumentData;
import com.btc_store.domain.enums.SearchOperator;
import com.btc_store.domain.model.custom.LegalDocumentModel;
import com.btc_store.facade.LegalDocumentFacade;
import com.btc_store.service.ModelService;
import com.btc_store.service.SearchService;
import com.btc_store.service.SiteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class LegalDocumentFacadeImpl implements LegalDocumentFacade {

    private final SiteService siteService;
    private final ModelMapper modelMapper;
    private final ModelService modelService;
    private final SearchService searchService;

    @Override
    public LegalDocumentData getLegalDocumentByCode(String code) {
        var siteModel = siteService.getCurrentSite();
        var legalDocumentModel = searchService.searchByCodeAndSite(LegalDocumentModel.class, code, siteModel);
        return modelMapper.map(legalDocumentModel, LegalDocumentData.class);
    }

    @Override
    public LegalDocumentData saveLegalDocument(LegalDocumentData legalDocumentData) {
        var siteModel = siteService.getCurrentSite();
        LegalDocumentModel legalDocumentModel;

        if (legalDocumentData.isNew()) {
            legalDocumentModel = modelMapper.map(legalDocumentData, LegalDocumentModel.class);
            legalDocumentModel.setVersion("1.0");
            legalDocumentModel.setIsCurrentVersion(true);

            String code = generateCode(legalDocumentData.getDocumentType().name(), "1.0");
            legalDocumentModel.setCode(code);
            legalDocumentModel.setSite(siteModel);

            updateOtherDocumentsCurrentVersion(legalDocumentModel.getDocumentType(), siteModel, null);
        } else {
            legalDocumentModel = searchService.searchByCodeAndSite(LegalDocumentModel.class, legalDocumentData.getCode(), siteModel);
            
            boolean contentChanged = hasContentChanged(legalDocumentModel, legalDocumentData);
            
            if (contentChanged) {
                String newVersion = incrementVersion(legalDocumentModel.getVersion());

                LegalDocumentModel newVersionModel = new LegalDocumentModel();
                modelMapper.map(legalDocumentData, newVersionModel);
                newVersionModel.setId(null);
                newVersionModel.setVersion(newVersion);
                newVersionModel.setIsCurrentVersion(true);
                newVersionModel.setSite(siteModel);

                String newCode = generateCode(legalDocumentData.getDocumentType().name(), newVersion);
                newVersionModel.setCode(newCode);

                legalDocumentModel.setIsCurrentVersion(false);
                legalDocumentModel.setActive(false);
                modelService.save(legalDocumentModel);

                updateOtherDocumentsCurrentVersion(newVersionModel.getDocumentType(), siteModel, legalDocumentModel.getId());
                
                legalDocumentModel = newVersionModel;
            } else {
                legalDocumentModel.setEffectiveDate(legalDocumentData.getEffectiveDate());
                legalDocumentModel.setActive(legalDocumentData.getActive());
            }
        }

        var savedModel = modelService.save(legalDocumentModel);
        return modelMapper.map(savedModel, LegalDocumentData.class);
    }
    
    /**
     * İçerik değişikliğini kontrol et
     */
    private boolean hasContentChanged(LegalDocumentModel existing, LegalDocumentData updated) {
        // Title, shortText veya content değişmişse true döner
        return !Objects.equals(existing.getTitle(), updated.getTitle()) ||
               !Objects.equals(existing.getShortText(), updated.getShortText()) ||
               !Objects.equals(existing.getContent(), updated.getContent());
    }
    
    /**
     * Versiyon numarasını artır
     * 1.0 -> 1.1 -> 1.2 -> ... -> 1.9 -> 2.0
     */
    private String incrementVersion(String currentVersion) {
        String[] parts = currentVersion.split("\\.");
        int major = Integer.parseInt(parts[0]);
        int minor = Integer.parseInt(parts[1]);
        
        minor++;
        if (minor >= 10) {
            major++;
            minor = 0;
        }
        
        return major + "." + minor;
    }
    
    /**
     * Aynı tipteki diğer dokümanları current olmaktan çıkar ve pasife al
     */
    private void updateOtherDocumentsCurrentVersion(com.btc_store.domain.enums.LegalDocumentType documentType, 
                                                     com.btc_store.domain.model.store.StoreSiteModel siteModel,
                                                     Long excludeId) {
        var existingDocuments = searchService.search(LegalDocumentModel.class,
                Map.of("documentType", documentType,
                       "site", siteModel),
                SearchOperator.AND);

        for (var existingDoc : existingDocuments) {
            if (!Objects.equals(existingDoc.getId(), excludeId) && 
                Boolean.TRUE.equals(existingDoc.getIsCurrentVersion())) {
                existingDoc.setIsCurrentVersion(false);
                existingDoc.setActive(false);
                modelService.save(existingDoc);
            }
        }
    }

    @Override
    public void deleteLegalDocument(String code) {
        var siteModel = siteService.getCurrentSite();
        var legalDocumentModel = searchService.searchByCodeAndSite(LegalDocumentModel.class, code, siteModel);
        modelService.remove(legalDocumentModel);
    }

    /**
     * Kod otomatik oluşturma
     * Format: {documentType}-v{version}-{timestamp}
     * Örnek: kvkk-v1-0-20240219143022
     */
    private String generateCode(String documentType, String version) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String typeCode = documentType.toLowerCase().replace("_", "-");
        String versionCode = version.replace(".", "-");
        return String.format("%s-v%s-%s", typeCode, versionCode, timestamp);
    }
}
