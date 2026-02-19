package com.btc_store.domain.data.store.search;

import com.btc_store.domain.data.custom.CategoryData;
import com.btc_store.domain.data.custom.LegalDocumentData;
import com.btc_store.domain.data.custom.ProductData;
import com.btc_store.domain.data.custom.localize.LocalizeData;
import com.btc_store.domain.data.custom.media.MediaData;
import com.btc_store.domain.data.custom.user.UserSummaryData;
import com.btc_store.domain.data.extend.BaseLocalizedDescriptionData;
import com.btc_store.domain.data.extend.back.BackBaseData;
import com.btc_store.domain.enums.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class StoreSearchResultData extends BackBaseData {

    private LocalizeData title;
    private LocalizeData subtitle;
    private LocalizeData buttonText;
    private String buttonLink;
    private MediaData media;
    private Integer order;
    private Boolean active;
    private Boolean showTitle;
    private Boolean showSubtitle;
    private Boolean showButton;
    private Object name;
    private LocalizeData description;
    private String backgroundColor;
    private String textColor;
    private String buttonBackgroundColor;
    private String buttonBorderColor;
    private String buttonTextColor;
    private Boolean showOnHomepage;
    private String company;
    private String industry;
    private Boolean showOnHome;
    private String value;
    private DataType dataType;
    private ParameterType parameterType;
    private Boolean encrypt;
    private String username;
    private String firstName;
    private String lastName;
    private Boolean deleted;
    private String email;
    private String phoneNumber;
    private Set<BaseLocalizedDescriptionData> userGroups;
    private MediaData picture;
    private LocalizeData shortDescription;
    private Set<CategoryData> categories;
    private Set<UserSummaryData> responsibleUsers;
    private MediaData mainImage;
    private List<MediaData> medias;
    private List<ProductData> products;
    private LegalDocumentType documentType;
    private LocalizeData shortText;
    private Boolean isCurrentVersion;
    private Date effectiveDate;
    private String version;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String subject;
    private String message;
    private CallRequestPriority priority;
    private CallRequestStatus status;
    private Set<BaseLocalizedDescriptionData> assignedGroups;
    private Set<UserSummaryData> assignedUsers;
    private Date completedAt;
    private String ipAddress;
    private LegalDocumentData acceptedLegalDocument;
    private ProductData product;
    private String link;
    private String icon;
    private Integer displayOrder = 0;
    private Boolean showCount = true;
    private String searchItemType;
    private String searchFilters;
    private DashboardModuleType moduleType;
}
