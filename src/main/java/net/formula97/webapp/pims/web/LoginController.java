/**
 * 
 */
package net.formula97.webapp.pims.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author f97one
 *
 */
@Controller
public class LoginController {

    @RequestMapping("loginForm")
    String loginForm() {
        return "/loginForm";
    }
}
