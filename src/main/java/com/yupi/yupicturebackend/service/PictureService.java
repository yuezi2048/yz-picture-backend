package com.yupi.yupicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupi.yupicturebackend.models.domain.Picture;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupi.yupicturebackend.models.domain.User;
import com.yupi.yupicturebackend.models.dto.picture.PictureQueryRequest;
import com.yupi.yupicturebackend.models.dto.picture.PictureReviewRequest;
import com.yupi.yupicturebackend.models.dto.picture.PictureUploadByBatchRequest;
import com.yupi.yupicturebackend.models.dto.picture.PictureUploadRequest;
import com.yupi.yupicturebackend.models.vo.picture.PictureVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

/**
* @author ljy
* @description 针对表【picture(图片)】的数据库操作Service
* @createDate 2025-11-12 19:55:43
*/
public interface PictureService extends IService<Picture> {

    /**
     * 上传图片
     *
     * @param inputSource
     * @param pictureUploadRequest
     * @param loginUser
     * @return
     */
    PictureVO uploadPicture(Object inputSource,
                            PictureUploadRequest pictureUploadRequest,
                            User loginUser);

    /**
     * 获取查询条件
     *
     * @param pictureQueryRequest
     * @return
     */
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest);

    /**
     * 获取单条图片结果
     *
     * @param picture
     * @param request
     * @return
     */
    PictureVO getPictureVO(Picture picture, HttpServletRequest request);

    /**
     * 获取多条图片结果
     *
     * @param picturePage
     * @param request
     * @return
     */
    Page<PictureVO> getPictureVOPage(Page<Picture> picturePage, HttpServletRequest request);

    /**
     * 图片审核
     *
     * @param pictureReviewRequest
     * @param loginUser
     */
    void doPictureReview(PictureReviewRequest pictureReviewRequest, User loginUser);

    /**
     * 批量抓取和创建图片
     *
     * @param pictureUploadByBatchRequest
     * @param loginUser
     * @return 成功创建的图片数
     */
    Integer uploadPictureByBatch(PictureUploadByBatchRequest pictureUploadByBatchRequest, User loginUser);


    void validPicture(Picture picture);

    void fillReviewParams(Picture picture, User loginUser);
}
