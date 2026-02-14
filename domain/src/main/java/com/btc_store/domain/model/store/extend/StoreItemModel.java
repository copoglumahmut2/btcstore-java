package com.btc_store.domain.model.store.extend;

import com.btc_store.domain.model.custom.extend.ItemModel;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldNameConstants;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@FieldNameConstants
public abstract class StoreItemModel implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final String ID = "id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    private Date createdDate;

    @LastModifiedDate
    private Date lastModifiedDate;

    @Transient
    private Set<String> modifiedAttributes = new HashSet<>();

    @Transient
    private ItemModel snapshot;

    /**
     * This field will be using in AfterSaveInterceptors
     */
    @Transient
    private boolean isNewTransaction;

    protected void addChangeAttribute(String value) {
        modifiedAttributes.add(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StoreItemModel that = (StoreItemModel) o;

        return Objects.equals(hashCode(), that.hashCode());
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
