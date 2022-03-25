package com.litianyi.search.controller;

import com.litianyi.search.service.SearchService;
import com.litianyi.search.vo.SearchParam;
import com.litianyi.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author litianyi
 * @version 1.0
 * @date 2022/2/20 3:08 PM
 */
@Controller
public class SearchController {

    @Autowired
    private SearchService searchService;

    @GetMapping({"/list.html", "/search.html", "/"})
    public String listPage(SearchParam param, Model model, HttpServletRequest request) throws IOException {
        param.set_queryString(request.getQueryString());
        SearchResult result = searchService.search(param);
        model.addAttribute("result", result);
        return "list";
    }
}
