package net.formula97.webapp.pims.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("admin/system")
public class AdminSystemPreferenceController extends BaseWebController {

    @RequestMapping(value = "update", method = RequestMethod.POST, params = "updateBtn")
    public String updatePreference() {

        return "";
    }

}
