package com.yupi.yupicturebackend.models.dto.picture;

import lombok.Data;

@Data
public class PictureUploadByBatchRequest {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 抓取数量
     */
    private Integer count = 10;

    /**
     * 设置名称前缀
     */
    private String namePrefix;
}
