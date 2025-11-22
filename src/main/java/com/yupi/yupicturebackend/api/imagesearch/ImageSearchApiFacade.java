package com.yupi.yupicturebackend.api.imagesearch;

import com.yupi.yupicturebackend.api.imagesearch.model.ImageSearchResult;
import com.yupi.yupicturebackend.api.imagesearch.model.ImageSearchResultVO;
import com.yupi.yupicturebackend.api.imagesearch.sub.GetImageFirstUrlApi;
import com.yupi.yupicturebackend.api.imagesearch.sub.GetImageListApi;
import com.yupi.yupicturebackend.api.imagesearch.sub.GetImagePageUrlApi;
import com.yupi.yupicturebackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Slf4j
public class ImageSearchApiFacade<T> {

    private List<ImageSearchResult> imageList;
    private T additionalInfo;

    /**
     * 搜索图片
     *
     * @param imageUrl
     * @return 包含搜索结果和相关信息的数组
     */
    public static ImageSearchResultVO searchImage(String imageUrl) {
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String[] imageFirstUrl = GetImageFirstUrlApi.getImageFirstUrl(imagePageUrl);
        List<ImageSearchResult> imageList = GetImageListApi.getImageList(imageFirstUrl[0]);

        ImageSearchResultVO imageSearchResultVO = new ImageSearchResultVO();
        imageSearchResultVO.setImageSearchResults(imageList);
        imageSearchResultVO.setImageTitle(imageFirstUrl[1]);

        return imageSearchResultVO;
    }

    public static void main(String[] args) {
        // 测试以图搜图功能
        String imageUrl = "https://picture-1308313119.cos.ap-guangzhou.myqcloud.com/public/1988099846140461057/2025-11-20_EsWfchLHPo0yszl7.webp";
        ImageSearchResultVO resultList = ImageSearchApiFacade.searchImage(imageUrl);
        System.out.println(resultList);
    }

}
