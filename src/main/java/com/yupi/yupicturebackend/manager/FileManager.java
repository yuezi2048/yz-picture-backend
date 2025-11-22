package com.yupi.yupicturebackend.manager;

import com.yupi.yupicturebackend.manager.upload.FilePictureUpload;
import com.yupi.yupicturebackend.manager.upload.UrlPictureUpload;
import com.yupi.yupicturebackend.models.dto.file.UploadPictureResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Slf4j
@Service
public class FileManager {

    @Resource
    private FilePictureUpload filePictureUpload;

    @Resource
    private UrlPictureUpload urlPictureUpload;

    public UploadPictureResult uploadPicture(Object inputSource, String uploadPathPrefix) {
        if (inputSource instanceof String) {
            String url = (String) inputSource;
            return urlPictureUpload.uploadPicture(url, uploadPathPrefix);
        } else {
            return filePictureUpload.uploadPicture(inputSource, uploadPathPrefix);
        }
    }
}
