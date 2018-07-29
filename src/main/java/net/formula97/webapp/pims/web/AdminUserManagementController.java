package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.misc.CommonsStringUtils;
import net.formula97.webapp.pims.service.AuthorizedUsersService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.UserModForm;
import net.formula97.webapp.pims.web.forms.UserSearchConditionForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
     * 詳細表示しているユーザーを更新で保存する。
     *
     * @param userModForm ユーザー更新情報のform
     * @param result      BeanValidation結果
     * @param model       画面Model
     * @param headerForm  ヘッダ表示用form
     * @return 正常終了の場合リダイレクト、そうでない場合は自身のview
     */
    @RequestMapping(value = "add", method = RequestMethod.POST, params = "updateBtn")
    public String updateUserDetail(@ModelAttribute("userModForm") @Validated UserModForm userModForm, BindingResult result,
                                   Model model, HeaderForm headerForm, RedirectAttributes redirectAttributes) {
        // ログイン中かどうかを判断
        Users currentUser = getUserState(model, headerForm);

        if (result.hasErrors()) {
            model.addAttribute("userModForm", userModForm);
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_MODIFY);
            return "/admin/user_detail";
        }

        String targetUserId = userModForm.getUsername();
        Users targetUser = authUsersSvc.findUserById(targetUserId);

        // ユーザーがいない場合はエラー
        if (targetUser == null) {
            String errMsg = String.format(Locale.getDefault(), "ユーザー %s は存在しません。", targetUser);
            putErrMsg(model, errMsg);
            model.addAttribute("userModForm", userModForm);
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_MODIFY);
            return "/admin/user_detail";
        }
        // パスワード（新、確認用）のいずれかに入力がある場合、値の一致を検証する
        if (!CommonsStringUtils.isNullOrWhiteSpace(userModForm.getPassword())) {
            if (!userModForm.getPassword().equals(userModForm.getPasswordConfirm())) {
                model.addAttribute("userModForm", userModForm);
                putErrMsg(model, "パスワードが一致しません。");
                model.addAttribute("modeTag", AppConstants.EDIT_MODE_MODIFY);
                return "/admin/user_detail";
            }
        } else if (!CommonsStringUtils.isNullOrWhiteSpace(userModForm.getPasswordConfirm())) {
            if (userModForm.getPasswordConfirm().equals(userModForm.getPassword())) {
                model.addAttribute("userModForm", userModForm);
                putErrMsg(model, "パスワードが一致しません。");
                model.addAttribute("modeTag", AppConstants.EDIT_MODE_MODIFY);
                return "/admin/user_detail";
            }
        }
        // NPE対策でこんなコードになったが、もっとエレガントに書けんかな....

        // パスワードを両方せっていしている場合は、値を更新する
        if (!CommonsStringUtils.isNullOrWhiteSpace(userModForm.getPassword()) && !CommonsStringUtils.isNullOrWhiteSpace(userModForm.getPasswordConfirm())) {
            targetUser.setPassword(BCrypt.hashpw(userModForm.getPassword(), BCrypt.gensalt()));
        }
        targetUser.setDisplayName(userModForm.getDisplayName());
        targetUser.setMailAddress(userModForm.getMailAddress());
        String authority = "";
        if (AppConstants.ROLE_CODE_ADMIN.equals(userModForm.getAssignedRole())) {
            authority = AppConstants.ROLE_ADMIN;
        } else if (AppConstants.ROLE_CODE_USER.equals(userModForm.getAssignedRole())) {
            authority = AppConstants.ROLE_USER;
        }
        targetUser.setAuthority(authority);
        targetUser.setEnabled(userModForm.getEnableUser());

        authUsersSvc.saveUsers(targetUser);
        String infoMsg = String.format(Locale.getDefault(), "ユーザー %s を更新しました。", targetUser);
        redirectAttributes.addFlashAttribute("infoMsg", infoMsg);

        return "redirect:/admin/userManagement/" + targetUserId;
    }

    /**
     * 画面に返送する前に、パスワード入力値を消去する。
     *
     * @param form 画面入力値が入っているForm
     */
    private void erasePasswordElements(UserModForm form) {
        form.setPassword("");
        form.setPasswordConfirm("");
    }
}
