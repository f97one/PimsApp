package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.misc.CommonsStringUtils;
import net.formula97.webapp.pims.service.AuthorizedUsersService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.UserModForm;
import net.formula97.webapp.pims.web.forms.UserSearchConditionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Locale;

/**
 * Created by f97one on 2016/12/23.
 */
@Controller
@RequestMapping("admin/userManagement")
public class AdminUserManagementController extends BaseWebController {

    @Autowired
    AuthorizedUsersService authUsersSvc;

    @RequestMapping(value = "searchUser", method = RequestMethod.GET, name = "searchBtn")
    public String searchUserByCondition(@ModelAttribute("userSearchConditionForm") UserSearchConditionForm userSearchConditionForm,
                                        BindingResult result, Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        if (result.hasErrors()) {
            model.addAttribute("matchUserCount", 0);
            return "/admin/user_list";
        }
        model.addAttribute("searchExecuted", true);

        List<Users> searchResult = authorizedUsersSvc.findUsers(userSearchConditionForm);
        model.addAttribute("matchUserCount", searchResult.size());
        model.addAttribute("dispUserList", searchResult);

        return "/admin/user_list";
    }

    @RequestMapping(value = "add", method = RequestMethod.GET)
    public String showUserAddView(Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        UserModForm userModForm = new UserModForm();
        userModForm.setAssignedRole(AppConstants.ROLE_USER);
        model.addAttribute("userModForm", userModForm);

        model.addAttribute("modeTag", AppConstants.EDIT_MODE_ADD);

        return "/admin/user_detail";
    }

    @RequestMapping(value = "add", method = RequestMethod.POST, params = "addBtn")
    public String registerUser(@ModelAttribute("userModForm") @Validated UserModForm userModForm, BindingResult result, Model model, HeaderForm headerForm) {
        Users myUserState = getUserState(model, headerForm);

        if (result.hasErrors()) {
            putErrMsg(model, "ユーザーを追加できません。");

            erasePasswordElements(userModForm);

            model.addAttribute("userModForm", userModForm);
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_ADD);
        } else {
            if (userModForm.isPasswdMatches()) {
                if (CommonsStringUtils.isNullOrWhiteSpace(userModForm.getPassword())) {
                    // パスワードを空（半角スペースのみを含む）にした場合は拒否
                    putErrMsg(model, "空のパスワードは許可されていません。");

                    erasePasswordElements(userModForm);

                    model.addAttribute("userModForm", userModForm);
                    model.addAttribute("modeTag", AppConstants.EDIT_MODE_ADD);
                } else {
                    Users existingUser = authUsersSvc.findUserById(userModForm.getUsername());

                    if (existingUser != null) {
                        // すでに同じIDのユーザーがいる場合は拒否
                        putErrMsg(model, "このユーザーはすでに追加されています。");

                        erasePasswordElements(userModForm);

                        model.addAttribute("userModForm", userModForm);
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_ADD);
                    } else {
                        Users users = userModForm.createDomain();
                        authUsersSvc.saveUsers(users);

                        putInfoMsg(model, String.format(Locale.getDefault(), "ユーザー %s を追加しました。", users.getUsername()));

                        erasePasswordElements(userModForm);

                        model.addAttribute("userModForm", userModForm);
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_MODIFY);
                    }
                }
            } else {
                // パスワードが一致しない場合は拒否
                putErrMsg(model, "パスワードが一致しません。");

                erasePasswordElements(userModForm);

                model.addAttribute("userModForm", userModForm);
                model.addAttribute("modeTag", AppConstants.EDIT_MODE_ADD);
            }
        }

        return "/admin/user_detail";
    }

    @RequestMapping(value = "{userId}", method = RequestMethod.GET)
    public String shwoUserDetail(@PathVariable String userId, Model model, HeaderForm headerForm, RedirectAttributes redirectAttributes) {
        Users myUserDetail = getUserState(model, headerForm);

        // ユーザーの存在確認
        Users targetUser = authUsersSvc.findUserById(userId);
        if (targetUser == null) {
            String errMsg = String.format(Locale.getDefault(), "ユーザー %s は存在しません。", userId);
            redirectAttributes.addFlashAttribute("errMsg", errMsg);
            return "redirect:/admin/userManagement";
        }

        model.addAttribute("modeTag", AppConstants.EDIT_MODE_MODIFY);

        UserModForm userModForm = new UserModForm(targetUser);

        userModForm.setEnableUser(targetUser.getEnabled());

        model.addAttribute("userModForm", userModForm);

        return "/admin/user_detail";
    }

    /**
     * 画面に返送する前に、パスワード入力値を消去する。
     *
     * @param form 画面入力値が入っているForm
     */
    private void erasePasswordElements(UserModForm form) {
        form.setPassword("");
        form.setOrgPassword("");
        form.setPasswordConfirm("");
    }
}
