package com.btc.domain.model.custom;

import com.btc.domain.model.store.media.StoreMediaModel;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;

import java.io.Serial;

@Entity
@EqualsAndHashCode(callSuper = true,onlyExplicitlyIncluded = true)
public class MediaModel extends StoreMediaModel {

    @Serial
    private static final long serialVersionUID = 1L;
}
