package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.SystemConfigService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.SystemPreferenceForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("admin/system")
public class AdminSystemPreferenceController extends BaseWebController {

    @Autowired
    private SystemConfigService sysConfigSvc;

    @RequestMapping(value = "update", method = RequestMethod.POST, params = "updateBtn")
    public String updatePreference(SystemPreferenceForm sysPrefForm, BindingResult bindingResult, Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        model.addAttribute("systemPreferenceForm", sysPrefForm);

        if (!bindingResult.hasErrors()) {
            sysConfigSvc.storePreference(sysPrefForm);

            putInfoMsg(model, "システム定数を更新しました。");
        }

        return "/admin/system_preference";
    }

}
