package com.btc_store.domain.data.custom.media;

import com.btc_store.domain.data.store.media.StoreUploadMediaData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class UploadMediaData extends StoreUploadMediaData {
}
