package com.btc_store.service.excel.impl;

import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.extend.CodeBasedItemModel;
import com.btc_store.domain.model.custom.extend.ItemModel;
import com.btc_store.domain.model.custom.localize.Localized;
import com.btc_store.domain.model.custom.user.UserModel;
import com.btc_store.service.excel.ExcelService;
import com.btc_store.service.exception.StoreRuntimeException;
import com.btc_store.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExcelServiceImpl implements ExcelService {

    protected final Environment environment;
    protected final UserService userService;

    @Override
    @SneakyThrows
    public InputStream exportExcel(String item, List<? extends ItemModel> items, Set<String> headers) {

        var excelLimit = environment.getProperty("excel.export.max.limit", Integer.class, 10000);
        if (items.size() > excelLimit) {
            throw new StoreRuntimeException("Data count is too big for excel export.", "excel.export.limit.message",
                    new Object[]{excelLimit, items.size()});
        }

        var currentLang = userService.getCurrentUserLanguage();
        var currentLanguageCode = Objects.isNull(currentLang) ? "tr" : currentLang.getCode();


        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(item);
        createHeader(workbook, sheet, headers);
        AtomicInteger rowCount = new AtomicInteger(1);
        items.stream().forEach(itemModel -> {
            var row = sheet.createRow(rowCount.getAndIncrement());
            createRows(itemModel, row, headers, currentLanguageCode);
        });

        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {

            workbook.write(byteArrayOutputStream);
            return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        } catch (IOException e) {
            log.error("Error occurred while exporting excel..." + ExceptionUtils.getMessage(e));
            throw new StoreRuntimeException("Error occurred while exporting excel..." + ExceptionUtils.getMessage(e));
        }
    }

    protected void createRows(ItemModel itemModel, Row row, Set<String> headers, String language) {
        int columnCount = 0;
        for (var header : headers) {
            var cellValue = getValue(itemModel, header, language);
            var cell = row.createCell(columnCount++);
            cell.setCellValue(cellValue.toString());
        }
    }

    @SneakyThrows
    protected Object getValue(Object object, String header, String language) {
        var value = PropertyUtils.getProperty(object, header);

        if (value instanceof UserModel) {
            return ((UserModel) value).getUsername();
        } else if (value instanceof CodeBasedItemModel) {
                /*eğer alan manyToOne bir alandan gelmişse burada
              hangi alanı döneceğimize sırayla bakılır
             */

            var fields = Arrays.asList("value", "name", "description", "code", "id");

            for (var field : fields) {
                try {
                    return getValueForString(PropertyUtils.getProperty(value, field), language);
                } catch (NoSuchMethodException e) {
                    //NO LOGIC
                }
            }

        } else if (value instanceof SiteModel) {
            return ((SiteModel) value).getName();
        }


        return getValueForString(value, language);

    }

    protected String getValueForString(Object object, String language) {
        if (object instanceof Localized) {
            return getValueForLocalized((Localized) object, language);
        }

        return StringUtils.defaultIfEmpty(Objects.isNull(object) ?
                StringUtils.EMPTY : object.toString(), StringUtils.EMPTY);
    }

    protected String getValueForLocalized(Localized value, String language) {
        switch (language) {
            case "tr" -> {
                return value.getTr();
            }
            case "en" -> {
                return value.getEn();
            }
            case "de" -> {
                return value.getDe();
            }
            case "fr" -> {
                return value.getFr();
            }
            case "it" -> {
                return value.getIt();
            }
            case "es" -> {
                return value.getEs();
            }
        }
        return StringUtils.EMPTY;
    }

    protected void createHeader(Workbook workbook, Sheet sheet, Set<String> headers) {
        Row headerRow = sheet.createRow(0);
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontName("Arial");

        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);

        int columnNo = 0;
        for (String header : headers) {
            Cell headerCell = headerRow.createCell(columnNo++);
            headerCell.setCellValue(header);
            headerCell.setCellStyle(cellStyle);
        }
    }
}
