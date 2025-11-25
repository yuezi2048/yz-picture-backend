package com.yupi.yupicturebackend.api.imagesearch.sub;

import com.yupi.yupicturebackend.exception.BusinessException;
import com.yupi.yupicturebackend.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class GetImageFirstUrlApi {
    /**
     * 获取图片列表页面地址
     *
     * @param url
     * @return
     */
    public static String[] getImageFirstUrl(String url) {
        String[] result = new String[2];

        try {
            // 使用 Jsoup 获取 HTML 内容
            Document document = Jsoup.connect(url)
                    .timeout(5000)
                    .get();

            // 获取所有 <script> 标签
            Elements scriptElements = document.getElementsByTag("script");

            // 遍历找到包含 `firstUrl` 的脚本内容
            for (Element script : scriptElements) {
                // 提取 firstUrl
                String scriptContent = script.html();
                if (scriptContent.contains("\"firstUrl\"")) {
                    // 正则表达式提取 firstUrl 的值
                    Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*\"(.*?)\"");
                    Matcher matcher = pattern.matcher(scriptContent);
                    if (matcher.find()) {
                        String firstUrl = matcher.group(1);
                        // 处理转义字符
                        firstUrl = firstUrl.replace("\\/", "/");
                        result[0] = firstUrl;
                    }
                }

                // 提取 subTitle
                Pattern subTitlePattern = Pattern.compile("\"subTitle\"\\s*:\\s*\"(.*?)\"");
                Matcher subTitleMatcher = subTitlePattern.matcher(scriptContent);
                if (subTitleMatcher.find()) {
                    String subTitle = subTitleMatcher.group(1);
                    subTitle = subTitle.replace("\\/", "/");
                    result[1] = subTitle;
                }
            }

            if (result[0] == null && result[1] == null) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "未找到 url");
            }

            return result;
        } catch (Exception e) {
            log.error("搜索失败", e);
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "搜索失败");
        }
    }

    public static void main(String[] args) {
        // 请求目标 URL
        String url = "https://graph.baidu.com/s?card_key=&entrance=GENERAL&extUiData%5BisLogoShow%5D=1&f=all&isLogoShow=1&session_id=8159702979385873223&sign=1218239dd3e1f4ea2853201763731758&tpl_from=pc";
        String[] imageFirstUrl = getImageFirstUrl(url);
        System.out.println("搜索成功，结果：" + imageFirstUrl[0] + imageFirstUrl[1]);
    }

}
