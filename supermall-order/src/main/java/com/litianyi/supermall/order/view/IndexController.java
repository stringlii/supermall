package com.litianyi.supermall.order.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author litianyi
 * @version 1.0
 */
@Controller
public class IndexController {

    @GetMapping("/{page}.html")
    public String toPage(@PathVariable String page) {
        return page;
    }

}
