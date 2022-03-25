package com.litianyi.search.service;

import com.litianyi.search.vo.SearchParam;
import com.litianyi.search.vo.SearchResult;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/2/21 2:59 PM
 */
public interface SearchService {

    /**
     * @param param 检索的搜有参数
     * @return 检索结果
     */
    SearchResult search(SearchParam param) throws IOException;

    default String replaceQueryString(String queryString, String value, String key) {
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8").replace("+", "%20")
                    .replace("%28", "(").replace("%29", ")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return queryString.replace("&" + key + "=" + encode, "");
    }
}
