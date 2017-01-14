/**
 * 
 */
package net.formula97.webapp.pims.web;

import net.formula97.webapp.pims.domain.IssueItems;
import net.formula97.webapp.pims.domain.IssueLedger;
import net.formula97.webapp.pims.domain.LedgerRefUser;
import net.formula97.webapp.pims.domain.Users;
import net.formula97.webapp.pims.misc.AppConstants;
import net.formula97.webapp.pims.service.IssueItemsService;
import net.formula97.webapp.pims.service.IssueLedgerService;
import net.formula97.webapp.pims.service.LedgerRefUserService;
import net.formula97.webapp.pims.web.forms.HeaderForm;
import net.formula97.webapp.pims.web.forms.IssueItemForm;
import net.formula97.webapp.pims.web.forms.IssueItemsLineForm;
import net.formula97.webapp.pims.web.forms.NewLedgerForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.util.*;

/**
 * @author f97one
 *
 */
@Controller
@RequestMapping("/ledger")
public class LedgerController extends BaseWebController {

    @Autowired
    IssueLedgerService issueLedgerSvc;
    @Autowired
    IssueItemsService issueItemsSvc;
    @Autowired
    LedgerRefUserService ledgerRefUserSvc;

    @ModelAttribute
    NewLedgerForm setUpNewLedgerForm() {
        return new NewLedgerForm();
    }

    @RequestMapping(value = "{ledgerId}", method = RequestMethod.GET)
    public String getLedgerItems(@PathVariable Integer ledgerId, Model model, HeaderForm form) {
        Users users = getUserState(model, form);

        IssueLedger ledger = issueLedgerSvc.getLedgerById(ledgerId);

        List<IssueItemsLineForm> itemForms = new ArrayList<>();
        long incompleteItems = 0;
        long completeItems = 0;
        long totalItems = 0;

        if (ledger == null) {
            putErrMsg(model, "台帳が見つかりません。");
            ledger = new IssueLedger(-1, "", 1, true);
        } else {
            if (!ledger.getPublicLedger()) {
                boolean badDisp = false;
                if (users == null) {
                    badDisp = true;
                } else {
                    LedgerRefUser refUser = ledgerRefUserSvc.findReferenceForUser(users.getUsername(), ledger.getLedgerId());
                    if (refUser == null) {
                        badDisp = true;
                    }
                }

                if (badDisp) {
                    putErrMsg(model, "この台帳は公開されていません。");
                    ledger = new IssueLedger(-1, "", 1, true);
                } else {
                    List<IssueItems> issueItems = issueItemsSvc.getIssueItemsByLedgerId(ledgerId);
                    if (issueItems.size() > 0) {
                        itemForms = issueItemsSvc.getIssueItemsForDisplay(ledgerId);

                        // 完了、未完了の数
                        totalItems = itemForms.size();
                        incompleteItems = itemForms.stream().filter((r) -> r.getConfirmedDate() == null).count();
                        completeItems = totalItems - incompleteItems;
                    }
                }
            } else {
                List<IssueItems> issueItems = issueItemsSvc.getIssueItemsByLedgerId(ledgerId);
                if (issueItems.size() > 0) {
                    itemForms = issueItemsSvc.getIssueItemsForDisplay(ledgerId);

                    // 完了、未完了の数
                    totalItems = itemForms.size();
                    incompleteItems = itemForms.stream().filter((r) -> r.getConfirmedDate() == null).count();
                    completeItems = totalItems - incompleteItems;
                }
            }
        }

        model.addAttribute("issueLedger", ledger);
        model.addAttribute("issueItems", itemForms);
        model.addAttribute("incompleteItems", incompleteItems);
        model.addAttribute("completeItems", completeItems);
        model.addAttribute("totalItems", totalItems);

        return "/ledger/issueList";
    }

    @RequestMapping(value = "create", method = RequestMethod.GET)
    public String showLedger(NewLedgerForm form, Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);
        model.addAttribute("newLedgerForm", form);

        return "/ledger/addLedger";
    }

    @RequestMapping(value = "/create", params = "addLedgerBtn", method = RequestMethod.POST)
    public String addLedger(@ModelAttribute("newLedgerForm") @Valid NewLedgerForm newLedgerForm, BindingResult result, Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        String dest;
        if (result.hasErrors()) {
            return showLedger(newLedgerForm, model, headerForm);
        }

        if (users == null) {
            putErrMsg(model, "台帳の追加にはログインが必要です。");
            dest = "/ledger/addLedger";
        } else {
            IssueLedger ledger = new IssueLedger();
            ledger.setLedgerName(newLedgerForm.getLedgerName());
            ledger.setOpenStatus(1);
            ledger.setPublicLedger(newLedgerForm.isPublicLedger());

            issueLedgerSvc.saveLedger(ledger, users);

            dest = "redirect:/";
        }

        return dest;
    }

    @RequestMapping(value = "{ledgerId}/add", method = RequestMethod.GET)
    public String showIssueItemCreationView(@PathVariable("ledgerId") Integer ledgerId, Model model, HeaderForm headerForm) {
        Users users = getUserState(model, headerForm);

        IssueItemForm issueItemForm = new IssueItemForm();

        issueItemsSvc.mapEmptyUsers(model);

        // 指定された番号の台帳を検索する
        Optional<IssueLedger> ledgerOptional = Optional.ofNullable(issueLedgerSvc.getLedgerById(ledgerId));
        if (ledgerOptional.isPresent()) {
            model.addAttribute("currentLedgerName", ledgerOptional.get().getLedgerName());

            if (users == null) {
                putErrMsg(model, "課題の追加にはログインが必要です。");
                model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
            } else {
                // 課題を編集できるかどうかを判断
                if (issueItemsSvc.hasEditPrivilege(ledgerId, users)) {
                    model.addAttribute("modeTag", AppConstants.EDIT_MODE_ADD);
                    issueItemForm.setFoundUserId(users.getUsername());

                    // 関係ユーザーをマップする
                    issueItemsSvc.mapRelatedUsers(model, ledgerId);
                } else {
                    model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                    putErrMsg(model, "課題を追加する権限がありません。");
                }
            }
        } else {
            // 指定番号の台帳がない場合はエラーにする
            putErrMsg(model, "台帳が見つかりません。");
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);

            model.addAttribute("currentLedgerName", "※不明※");
        }
        model.addAttribute("issueNumberLabel", "新規");
        issueItemForm.setRecordTimestamp(Calendar.getInstance().getTimeInMillis());

        issueItemsSvc.mapMaster(model);

        model.addAttribute("issueItem", issueItemForm);

        return "/ledger/issueItem";
    }

    @RequestMapping(value = "{ledgerId}/add", method = RequestMethod.POST, params = "addItemBtn")
    public String addIssueToLedger(@PathVariable("ledgerId") Integer ledgerId,
                                   @ModelAttribute("issueItem") @Validated IssueItemForm issueItemForm,
                                   BindingResult result, Model model, HeaderForm headerForm) {

        model.addAttribute("issueItem", issueItemForm);

        Users users = getUserState(model, headerForm);
        if (!result.hasErrors()) {
            Optional<IssueLedger> ledgerOptional = Optional.ofNullable(issueLedgerSvc.getLedgerById(ledgerId));
            if (ledgerOptional.isPresent()) {
                if (users == null) {
                    putErrMsg(model, "課題の追加にはログインが必要です。");
                } else {
                    if (issueItemsSvc.hasEditPrivilege(ledgerId, users)) {
                        IssueItems issueItems = issueItemForm.exportToEntity();
                        issueItems.setLedgerId(ledgerId);

                        issueItemsSvc.saveItem(issueItems);
                        putInfoMsg(model, "課題を追加しました。");
                    } else {
                        putErrMsg(model, "課題を追加する権限がありません。");
                    }
                }
            } else {
                // 指定番号の台帳がない場合はエラーにする
                putErrMsg(model, "台帳が見つかりません。");
                model.addAttribute("currentLedgerName", "※不明※");
            }
        }
        issueItemsSvc.mapRelatedUsers(model, ledgerId);
        issueItemsSvc.mapMaster(model);

        return "/ledger/issueItem";
    }

    @RequestMapping(value = "{ledgerId}/{issueId}", method = RequestMethod.GET)
    public String getIssueItem(@PathVariable("ledgerId") Integer ledgerId, @PathVariable("issueId") Integer issueId,
                               Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        IssueLedger ledger = issueLedgerSvc.getLedgerById(ledgerId);
        IssueItems items = issueItemsSvc.getIssueItem(ledgerId, issueId);

        issueItemsSvc.mapMaster(model);

        if (ledger == null) {
            // 台帳が見つからない場合はエラー
            putErrMsg(model, "台帳が見つかりません。");
            model.addAttribute("issueItem", new IssueItemForm());
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);

            issueItemsSvc.mapEmptyUsers(model);
        } else {
            issueItemsSvc.mapRelatedUsers(model, ledgerId);

            model.addAttribute("currentLedgerName", ledger.getLedgerName());

            if (items == null) {
                putErrMsg(model, "課題が見つかりません。");
                model.addAttribute("issueItem", new IssueItemForm());
                model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
            } else {
                model.addAttribute("issueNumberLabel", String.format(Locale.getDefault(), "#%d", items.getIssueId()));

                if (myUserDetail == null) {
                    if (ledger.getPublicLedger()) {
                        // 公開かつ非ログインは、リードオンリーで課題を表示する
                        IssueItemForm form = new IssueItemForm().convertToForm(items);
                        model.addAttribute("issueItem", form);
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                    } else {
                        // 非公開かつ非ログインはエラー
                        putErrMsg(model, "課題が見つかりません。");
                        model.addAttribute("issueItem", new IssueItemForm());
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                    }
                } else {
                    LedgerRefUser ref = ledgerRefUserSvc.findReferenceForUser(myUserDetail.getUsername(), ledgerId);

                    if (ref == null) {
                        if (ledger.getPublicLedger()) {
                            // 関係ないユーザーは、公開台帳の課題を参照だけできる
                            IssueItemForm form = new IssueItemForm().convertToForm(items);
                            model.addAttribute("issueItem", form);
                            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                        } else {
                            // 非公開台帳に所属しない場合はエラー
                            putErrMsg(model, "課題が見つかりません。");
                            model.addAttribute("issueItem", new IssueItemForm());
                            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                        }
                    } else {
                        // 関係あるユーザーは更新を許可
                        IssueItemForm form = new IssueItemForm().convertToForm(items);
                        model.addAttribute("issueItem", form);
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_MODIFY);
                    }
                }
            }
        }

        return "/ledger/issueItem";
    }

    @RequestMapping(value = "{ledgerId}/{issueId}", method = RequestMethod.POST, params = "updateItemBtn")
    public String updateIssue(@PathVariable("ledgerId") Integer ledgerId, @PathVariable("issueId") Integer issueId,
                              @ModelAttribute("issueItem") @Validated IssueItemForm issueItemForm,
                              BindingResult result, Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        model.addAttribute("issueItem", issueItemForm);

        if (!result.hasErrors()) {
            Optional<IssueLedger> ledgerOptional = Optional.ofNullable(issueLedgerSvc.getLedgerById(ledgerId));
            if (ledgerOptional.isPresent()) {
                if (myUserDetail == null) {
                    putErrMsg(model, "課題の更新にはログインが必要です。");
                } else {
                    IssueItems baseItem = issueItemsSvc.getIssueItem(ledgerId, issueId);

                    if (baseItem == null) {
                        // 指定番号の課題が見つからない場合はエラー
                        putErrMsg(model, "課題が見つかりません。");
                    } else {
                        if (issueItemsSvc.hasEditPrivilege(ledgerId, myUserDetail)) {
                            IssueItems issueItems = issueItemForm.exportToEntity();

                            if (issueItems.getRowUpdatedAt().compareTo(baseItem.getRowUpdatedAt()) < 0) {
                                // 他人が更新しているので、この処理での更新を拒否
                                putErrMsg(model, "別のユーザーが課題を更新しました。リロードしてください。");
                            } else {
                                issueItems.setLedgerId(ledgerId);
                                issueItems.setIssueId(issueId);

                                issueItemsSvc.saveItem(issueItems);
                                putInfoMsg(model, "課題を更新しました。");
                            }
                        } else {
                            putErrMsg(model, "課題を更新する権限がありません。");
                        }
                    }
                }
            } else {
                // 指定番号の台帳がない場合はエラーにする
                putErrMsg(model, "台帳が見つかりません。");
                model.addAttribute("currentLedgerName", "※不明※");
            }
        }

        issueItemsSvc.mapRelatedUsers(model, ledgerId);
        issueItemsSvc.mapMaster(model);

        return "/ledger/issueItem";
    }

    @RequestMapping(value = "{ledgerId}/remove/{issueId}", method = RequestMethod.GET)
    public String confirmRemoveIssue(@PathVariable("ledgerId") Integer ledgerId, @PathVariable("issueId") Integer issueId,
                                     Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        IssueLedger ledger = issueLedgerSvc.getLedgerById(ledgerId);
        IssueItems items = issueItemsSvc.getIssueItem(ledgerId, issueId);

        issueItemsSvc.mapMaster(model);

        if (ledger == null) {
            // 台帳が見つからない時はエラー
            putErrMsg(model, "台帳が見つかりません。");
            model.addAttribute("issueItem", new IssueItemForm());
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);

            issueItemsSvc.mapEmptyUsers(model);
        } else {
            model.addAttribute("currentLedgerName", ledger.getLedgerName());
            issueItemsSvc.mapRelatedUsers(model, ledgerId);

            if (items == null) {
                // 課題が見つからない時はエラー
                putErrMsg(model, "課題が見つかりません。");
                model.addAttribute("issueItem", new IssueItemForm());
                model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
            } else {
                model.addAttribute("currentLedgerName", ledger.getLedgerName());

                if (myUserDetail == null) {
                    // 非ログインはエラー
                    putErrMsg(model, "課題が見つかりません。");
                    model.addAttribute("issueItem", new IssueItemForm());
                    model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                } else {
                    LedgerRefUser refUser = ledgerRefUserSvc.findReferenceForUser(myUserDetail.getUsername(), ledgerId);

                    if (refUser == null) {
                        // 台帳に関係ないユーザーだとエラー
                        putErrMsg(model, "課題が見つかりません。");
                        model.addAttribute("issueItem", new IssueItemForm());
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                    } else {
                        // 削除確認メッセージを出して削除モードにする
                        putInfoMsg(model, "この課題を削除します。削除したら元に戻せません。よろしいですか？");
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_REMOVE);

                        IssueItemForm form = new IssueItemForm().convertToForm(items);
                        model.addAttribute("issueItem", form);
                    }
                }
            }
        }

        return "/ledger/issueItem";
    }

    @RequestMapping(value = "{ledgerId}/remove/{issueId}", method = RequestMethod.POST, params = "removeItemBtn")
    public String removeIssue(@PathVariable("ledgerId") Integer ledgerId, @PathVariable("issueId") Integer issueId,
                              @ModelAttribute("issueItem") @Validated IssueItemForm issueItemForm,
                              Model model, HeaderForm headerForm) {
        Users myUserDetail = getUserState(model, headerForm);

        IssueLedger ledger = issueLedgerSvc.getLedgerById(ledgerId);
        IssueItems items = issueItemsSvc.getIssueItem(ledgerId, issueId);

        issueItemsSvc.mapMaster(model);

        if (ledger == null) {
            // 台帳が見つからない時はエラー
            putErrMsg(model, "台帳が見つかりません。");
            model.addAttribute("issueItem", new IssueItemForm());
            model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);

            issueItemsSvc.mapEmptyUsers(model);
        } else {
            model.addAttribute("currentLedgerName", ledger.getLedgerName());
            issueItemsSvc.mapRelatedUsers(model, ledgerId);

            if (items == null) {
                // 課題が見つからない時はエラー
                putErrMsg(model, "課題が見つかりません。");
                model.addAttribute("issueItem", new IssueItemForm());
                model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
            } else {
                if (myUserDetail == null) {
                    // 非ログインだとエラー
                    putErrMsg(model, "課題の削除にはログインが必要です。");
                    model.addAttribute("issueItem", new IssueItemForm());
                    model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                } else {
                    LedgerRefUser refUser = ledgerRefUserSvc.findReferenceForUser(myUserDetail.getUsername(), ledgerId);

                    if (refUser == null) {
                        // 台帳に関係ないユーザーだとエラー
                        putErrMsg(model, "課題を削除する権限がありません。");
                        model.addAttribute("issueItem", new IssueItemForm());
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_READONLY);
                    } else {
                        // 課題を削除して新規追加モードにする
                        issueItemsSvc.removeItem(ledgerId, issueId);
                        putInfoMsg(model, "課題を削除しました。");
                        model.addAttribute("issueItem", new IssueItemForm());
                        model.addAttribute("modeTag", AppConstants.EDIT_MODE_ADD);
                        model.addAttribute("issueNumberLabel", "新規");

                    }
                }
            }
        }

        return "/ledger/issueItem";
    }
}
