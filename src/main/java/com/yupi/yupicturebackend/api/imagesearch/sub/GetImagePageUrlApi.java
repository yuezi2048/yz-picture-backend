package com.yupi.yupicturebackend.api.imagesearch.sub;

import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class GetImagePageUrlApi {

    private static final String acsToken = "1763702521450_1763731758772_YuJ1Z0bxEZBMjO5DLEikpeKGv6f6H9aMQmFOSvw5XE87FitXS+62hdmQreTIYJIYjOWVQYGH7+/bCOt3ciDuuWM8SCrLD9iTtYlPQtKjLQiLDHWG2xRpfb8426vrR7Lds4EZP5Ezb7BzlkTFvtjcmViZnEyg/nk2h4/XgMvEFyixjLRtp6VzgybK46zISro6MYVf5ox1LImmmM05EnUpsGLpmUQj9LWaAduT6zs4DgWB46sRo9QrvsJtQl7g/QRKfKxwZKx9eIokjTPg3yc3iK/T3zzkUIzDZ9DM4BpQwDZJrowzJqg9RaTIHMD+D6YPWtA7meZgoR6CbW374G+LtjbOAUMNA7EPqPhrn0KhWCi/5PrsoCvOsAkzZg4zNaP8jTi3Qn01XynD/HJPPr/+zUF0aRszbJOYlVLOAxhZQ2g=";

    /**
     * 获取图片的页面链接
     *
     * @param imageUrl
     * @return
     */
    public static String getImagePageUrl(String imageUrl) {
        // 1. 准备请求参数
        HashMap<String, Object> formData = new HashMap<>();
        formData.put("image", imageUrl);
        formData.put("tn", "pc");
        formData.put("from", "pc");
        formData.put("image_source", "PC_UPLOAD_URL");
        // 2. 发送POST请求到百度接口
        long uptime = System.currentTimeMillis();
        String url = "https://graph.baidu.com/upload?uptime=" + uptime;

        try {
            HttpResponse response = HttpRequest.post(url)
                    .header("acs-token", acsToken)
                    .form(formData)
                    .timeout(5000)
                    .execute();
            if (response.getStatus() != HttpStatus.HTTP_OK) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口调用失败");
            }
            // 3. 处理响应数据
            String responseBody = response.body();
            Map<String, Object> result = JSONUtil.toBean(responseBody, Map.class);
            if (result == null || !Integer.valueOf(0).equals(result.get("status"))) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口调用失败");
            }
            Map<String, Object> data = (Map<String, Object>) result.get("data");
            String rawUrl = (String) data.get("url");
            // 对 URL 进行解码
            String searchResultUrl = URLUtil.decode(rawUrl, StandardCharsets.UTF_8);
            // 如果 URL 为空
            if (searchResultUrl == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未返回有效结果");
            }
            return searchResultUrl;

        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

}
