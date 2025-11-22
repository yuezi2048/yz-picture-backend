package com.yupi.yupicturebackend.manager.upload;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.RandomUtil;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.ciModel.persistence.CIObject;
import com.qcloud.cos.model.ciModel.persistence.ImageInfo;
import com.qcloud.cos.model.ciModel.persistence.ProcessResults;
import com.yupi.yupicturebackend.config.CosClientConfig;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.manager.CosManager;
import com.yupi.yupicturebackend.models.dto.file.UploadPictureResult;
import com.yupi.yupicturebackend.utils.HexColorExpander;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Slf4j
public abstract class PictureUploadTemplate {

    @Resource
    protected CosManager cosManager;

    @Resource
    protected CosClientConfig cosClientConfig;

    /**
     * 模板方法，定义上传流程
     */
    public final UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {

        // 1. 校验文件
        validPicture(inputSource);

        // 2. 设置图片上传地址
        String uuid = RandomUtil.randomString(16);
        String originFilename = this.getOriginalFilename(inputSource);
        String uploadFilename = String.format("%s_%s.%s", DateUtil.formatDate(new Date()), uuid, FileUtil.getSuffix(originFilename));
        // TODO 设计枚举类FileUploadBizEnum 根据业务场景区分上传路径，校验规则
        String uploadPath = String.format("/%s/%s", uploadPathPrefix, uploadFilename);

        // 3. 上传文件
        File file = null;
        try {
            // TODO 用流的形式将请求的文件上传到COS
            // 3.1 创建临时文件
            file = File.createTempFile(uploadPath, null);
            this.processFile(inputSource, file);

            // 3.2 上传到COS
            PutObjectResult putObjectResult = cosManager.putPictureObject(uploadPath, file);
            ImageInfo imageInfo = putObjectResult.getCiUploadResult().getOriginalInfo().getImageInfo();
            ProcessResults processResults = putObjectResult.getCiUploadResult().getProcessResults();
            List<CIObject> objectList = processResults.getObjectList();
            if (CollectionUtil.isNotEmpty(objectList)) {
                CIObject compressCiObject = objectList.get(0);
                CIObject thumbnailCiObject = compressCiObject;
                if (objectList.size() > 1) {
                    thumbnailCiObject = objectList.get(1);
                }
                return buildResult(originFilename, compressCiObject, thumbnailCiObject, imageInfo);
            }
            return buildResult(imageInfo, uploadPath, originFilename, file);
        } catch (Exception e) {
            log.error("file upload error, filepath = " + uploadPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        } finally {
            this.deleteTempFile(file);
        }
    }

    /**
     * 校验文件
     *
     * @param inputSource
     */
    protected abstract void validPicture(Object inputSource);

    /**
     * 获取原始文件名
     *
     * @param inputSource
     */
    protected abstract String getOriginalFilename(Object inputSource);

    /**
     * 处理输入源，生成本地临时文件
     *
     * @param inputSource
     * @param file
     */
    protected abstract void processFile(Object inputSource, File file);

    /**
     * 获取上传图片结果
     *
     * @param imageInfo
     * @param uploadPath
     * @param originFilename
     * @param file
     * @return
     */
    private UploadPictureResult buildResult(ImageInfo imageInfo, String uploadPath, String originFilename, File file) {
        int picWidth = imageInfo.getWidth();
        int picHeight = imageInfo.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();

        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + uploadPath);
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicSize(FileUtil.size(file));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(imageInfo.getFormat());
        uploadPictureResult.setPicColor(HexColorExpander.expandHexColor(imageInfo.getAve()));
        return uploadPictureResult;
    }

    /**
     * 获取上传图片结果（压缩）
     *
     * @param originFilename
     * @param compressedCiObject
     * @return
     */
    private UploadPictureResult buildResult(String originFilename, CIObject compressedCiObject, CIObject thumbnailCiObject, ImageInfo imageInfo) {
        UploadPictureResult uploadPictureResult = new UploadPictureResult();
        int picWidth = compressedCiObject.getWidth();
        int picHeight = compressedCiObject.getHeight();
        double picScale = NumberUtil.round(picWidth * 1.0 / picHeight, 2).doubleValue();
        uploadPictureResult.setPicName(FileUtil.mainName(originFilename));
        uploadPictureResult.setPicWidth(picWidth);
        uploadPictureResult.setPicHeight(picHeight);
        uploadPictureResult.setPicScale(picScale);
        uploadPictureResult.setPicFormat(compressedCiObject.getFormat());
        uploadPictureResult.setPicSize(compressedCiObject.getSize().longValue());
        uploadPictureResult.setPicColor(HexColorExpander.expandHexColor(imageInfo.getAve()));
        // 设置图片为压缩后的地址
        uploadPictureResult.setUrl(cosClientConfig.getHost() + "/" + compressedCiObject.getKey());
        // 设置缩略图的地址
        uploadPictureResult.setThumbnailUrl(cosClientConfig.getHost() + "/" + thumbnailCiObject.getKey());
        return uploadPictureResult;
    }


    /**
     * 删除临时文件
     */
    public void deleteTempFile(File file) {
        if (file == null) {
            return;
        }
        boolean deleteResult = file.delete();
        if (!deleteResult) {
            log.error("file delete error, filepath = {}", file.getAbsolutePath());
        }
    }
}
