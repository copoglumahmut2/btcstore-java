package com.btc_store.facade.pageable;

import com.btc_store.domain.data.custom.pageable.PageableData;
import com.btc_store.domain.model.custom.extend.ItemModel;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PageableProvider<T extends ItemModel> {

    protected final ModelMapper modelMapper;


    public PageableData map(Page<T> source, Class destinationType) {
        var pageableData = new PageableData();
        pageableData.setPageSize(source.getPageable().getPageSize());
        pageableData.setPageNumber(source.getPageable().getPageNumber() + 1);
        pageableData.setTotalElements(source.getTotalElements());
        pageableData.setTotalPages(source.getTotalPages());
        pageableData.setContent(source.get().map(p -> modelMapper.map(p, destinationType))
                .collect(Collectors.toList()));
        return pageableData;
    }

    public PageableData map(Page<Object> source) {
        var pageableData = new PageableData();
        pageableData.setPageSize(source.getPageable().getPageSize());
        pageableData.setPageNumber(source.getPageable().getPageNumber() + 1);
        pageableData.setTotalElements(source.getTotalElements());
        pageableData.setTotalPages(source.getTotalPages());
        pageableData.setContent(source.getContent());
        return pageableData;
    }
}
