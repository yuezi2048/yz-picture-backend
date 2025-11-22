package com.yupi.yupicturebackend.api.imagesearch.model;

import lombok.Data;

import java.util.List;

@Data
public class ImageSearchResultVO {

    /**
     * 查询到的图片信息
     */
    List<ImageSearchResult> imageSearchResults;

    /**
     * 图片识别信息
     */
    String imageTitle;

}
