package com.yupi.yupicturebackend.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import com.yupi.yupicturebackend.exception.ThrowUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
public class UrlPictureUpload extends PictureUploadTemplate {
    @Override
    protected void validPicture(Object inputSource) {
        // 1. 校验URL格式
        String fileUrl = (String) inputSource;
        ThrowUtils.throwIf(fileUrl == null, ErrorCode.PARAMS_ERROR, "文件不能为空");
        try {
            new URL(fileUrl);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件地址格式错误");
        }
        ThrowUtils.throwIf(!fileUrl.startsWith("http") && !fileUrl.startsWith("https"), ErrorCode.PARAMS_ERROR, "文件地址格式错误");

        // 2. 发送HEAD请求文件头信息
        HttpResponse response = null;
        try {
            final int TIMEOUT = 5000; // 5 seconds
            response = HttpUtil.createRequest(Method.HEAD, fileUrl)
                    .setConnectionTimeout(TIMEOUT)  // Connection timeout
                    .setReadTimeout(TIMEOUT)        // Read timeout
                    .execute();

            // 如果请求HEAD失败，直接返回即可
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                return ;
            }

            // 3. 校验文件类型（对于文件头有才校验）
            String contentType = response.header("Content-Type");
            if (! StrUtil.isBlank(contentType)) {
                final List<String> validTypes = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp");
                ThrowUtils.throwIf(!validTypes.contains(contentType), ErrorCode.PARAMS_ERROR, "文件类型错误");
            }

            // 4. 校验文件大小
            String contentLengthStr = response.header("Content-Length");
            if (!StrUtil.isBlank(contentLengthStr)) {
                try {
                    long contentLength = Long.parseLong(contentLengthStr);
                    final long TWO_MB = 2 * 1024 * 1024L;
                    ThrowUtils.throwIf(contentLength > 2 * TWO_MB, ErrorCode.PARAMS_ERROR, "文件大小不能超过 2M");
                } catch (NumberFormatException e) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件大小格式错误");
                }
            }
        } finally {
            if (response != null) {
                response.close();
            }
        }

    }

    @Override
    protected String getOriginalFilename(Object inputSource) {
        String fileUrl = (String) inputSource;
        return FileUtil.mainName(fileUrl);
    }

    @Override
    protected void processFile(Object inputSource, File file) {
        String fileUrl = (String) inputSource;
        HttpUtil.downloadFile(fileUrl, file);
    }
}
