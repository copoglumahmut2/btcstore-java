package com.btc.domain.data.custom.media;

import com.btc.domain.data.store.media.StoreMediaData;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class MediaData extends StoreMediaData {
}
