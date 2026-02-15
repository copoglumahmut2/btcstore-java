package com.btc_store.facade;

import com.btc_store.domain.data.custom.SuccessStoryData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SuccessStoryFacade {

    List<SuccessStoryData> getAllSuccessStories();

    List<SuccessStoryData> getActiveSuccessStories();

    SuccessStoryData getSuccessStoryByCode(String code);

    SuccessStoryData saveSuccessStory(SuccessStoryData successStoryData, MultipartFile mediaFile, boolean removeMedia);

    void deleteSuccessStory(String code);
}
