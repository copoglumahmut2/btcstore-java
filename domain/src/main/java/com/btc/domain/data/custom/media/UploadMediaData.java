package com.btc.domain.data.custom.media;

import com.btc.domain.data.store.media.StoreUploadMediaData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class UploadMediaData extends StoreUploadMediaData {
}
