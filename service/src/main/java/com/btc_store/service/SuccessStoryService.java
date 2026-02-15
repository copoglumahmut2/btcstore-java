package com.btc_store.service;

import com.btc_store.domain.model.custom.SiteModel;
import com.btc_store.domain.model.custom.SuccessStoryModel;

import java.util.List;

public interface SuccessStoryService {

    SuccessStoryModel getSuccessStoryByCode(String code, SiteModel siteModel);

    List<SuccessStoryModel> getAllSuccessStories(SiteModel siteModel);

    List<SuccessStoryModel> getActiveSuccessStories(SiteModel siteModel);

    List<SuccessStoryModel> getAllSuccessStoriesOrdered(SiteModel siteModel);

    List<SuccessStoryModel> getActiveSuccessStoriesOrdered(SiteModel siteModel);

    SuccessStoryModel saveSuccessStory(SuccessStoryModel successStoryModel);

    void deleteSuccessStory(SuccessStoryModel successStoryModel);
}
