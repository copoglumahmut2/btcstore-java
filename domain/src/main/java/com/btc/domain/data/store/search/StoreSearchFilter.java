package com.btc.domain.data.store.search;

import com.btc.domain.data.custom.search.SearchFilter;
import com.btc.domain.enums.SearchCondition;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StoreSearchFilter {

    private String name;
    private String relationField;
    private Object value;
    private Date date;
    private String locale;
    private SearchCondition searchCondition;
    private List<Object> values;
    //current user üzerinden bir alan alınacak mı?
    private boolean currentUser;

    //current user üzerinden hangi alan alınacak?
    private String currentUserRelationField;

    //current user'dan alınan alan hangi değere yazılacak (values,value)
    private String setValueForCurrentUser;
    private SearchFilter child;

}
