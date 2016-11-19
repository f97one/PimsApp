package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.service.AuthorizedUsersService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.UserConfigForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Created by f97one on 2016/10/22.
 */
@Controller
@RequestMapping("user")
public class UserController extends BaseWebController {

    @Autowired
    AuthorizedUsersService authUserSvc;

    @ModelAttribute
    private UserConfigForm setUpUserConfigForm() {
        return new UserConfigForm();
    }

    @RequestMapping(path = "config", method = RequestMethod.GET)
    public String openMyUserConfig(Model model, HeaderForm headerForm, UserConfigForm userConfigForm) {
        Users users = getUserState(model, headerForm);

        // Spring Securityでログインを必須にしているため、ユーザー情報なしの時は考慮しない

        userConfigForm.setUsername(users.getUsername());
        userConfigForm.setDisplayName(users.getDisplayName());
        userConfigForm.setEnableUser(users.isEnabled());
        userConfigForm.setAssignedRole(users.getAuthority());

        userConfigForm.setDisableUsernameModify(true);
        userConfigForm.setDisableRoleModify(true);
        userConfigForm.setDisableUserEnableModify(true);
        userConfigForm.setDisableDisplayNameModify(false);

        model.addAttribute("userConfigForm", userConfigForm);

        return "user/userdetail";
    }

    @RequestMapping(path = "config", method = RequestMethod.POST, name = "updateBtn")
    public String updateUserPasswd(Model model, HeaderForm headerForm, UserConfigForm userConfigForm, BindingResult result) {

        // 編集を無効にしている場合はsubmitされてこないため、DBの値で埋めなおす
        Users users = getUserState(model, headerForm);
        if (userConfigForm.isDisableUsernameModify()) {
            userConfigForm.setUsername(users.getUsername());
        }
        if (userConfigForm.isDisableUserEnableModify()) {
            userConfigForm.setEnableUser(users.isEnabled());
        }
        if (userConfigForm.isDisableDisplayNameModify()) {
            userConfigForm.setDisplayName(users.getDisplayName());
        }
        if (userConfigForm.isDisableRoleModify()) {
            userConfigForm.setAssignedRole(users.getAuthority());
        }

        model.addAttribute("userConfigForm", userConfigForm);

        if (result.hasErrors()) {
            return "user/userdetail";
        }

        if (!BCrypt.checkpw(userConfigForm.getOrgPassword(), users.getPassword())) {
            putErrMsg(model, "パスワードが一致しません。");
            return "user/userdetail";
        }

        if (!userConfigForm.isPasswdMatches()) {
            putErrMsg(model, "パスワードが一致しません。");
            return "user/userdetail";
        }

        if (!userConfigForm.isDisableUsernameModify() && userConfigForm.getUsername().trim().length() == 0) {
            putErrMsg(model, "半角スペースだけのユーザーIDは使用できません。");
            return "user/userdetail";
        }

        if (userConfigForm.getPassword().trim().length() == 0) {
            putErrMsg(model, "半角スペースだけのパスワードは使用できません。");
            return "user/userdetail";
        }

        if (userConfigForm.getDisplayName().trim().length() == 0) {
            putErrMsg(model, "半角スペースだけの表示名は使用できません。");
            return "user/userdetail";
        }

        // ユーザー情報の更新
        users.setPassword(BCrypt.hashpw(userConfigForm.getPassword(), BCrypt.gensalt()));
        users.setDisplayName(userConfigForm.getDisplayName());
        users.setAuthority(userConfigForm.getAssignedRole());

        try {
            authUserSvc.saveUsers(users);
        } catch (Exception e) {
            e.printStackTrace();

            putErrMsg(model, "ユーザー情報の更新に失敗しました。");
            return openMyUserConfig(model, headerForm, userConfigForm);
        }

        putInfoMsg(model, "ユーザー情報を更新しました。");

        return "redirect:/user/config";
    }
}
