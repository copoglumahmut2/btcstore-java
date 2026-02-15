package com.btc_store.service.excel;


import com.btc_store.domain.model.custom.extend.ItemModel;

import java.io.InputStream;
import java.util.List;
import java.util.Set;

public interface ExcelService {

    InputStream exportExcel(String item, List<? extends ItemModel> items, Set<String> headers);
}
