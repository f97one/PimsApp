package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.*;
import net.formula97.webapp.pims.repository.*;
import net.formula97.webapp.pims.web.forms.IssueItemsLineForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.*;

/**
 * Created by f97one on 2016/11/12.
 */
@Service
public class IssueItemsService extends BaseService {

    @Autowired
    IssueLedgerRepository issueLedgerRepo;
    @Autowired
    IssueItemsRepository issueItemsRepo;
    @Autowired
    LedgerRefUserRepository ledgerRefUserRepo;
    @Autowired
    CategoryMasterRepository categoryMasterRepo;
    @Autowired
    ProcessMasterRepository processMasterRepo;
    @Autowired
    SevereLevelMasterRepository severeLevelMasterRepo;
    @Autowired
    StatusMasterRepository statusMasterRepo;
    @Autowired
    UserRepository userRepo;

    public IssueItems getIssueItem(Integer ledgerId, Integer issueId) {
        return issueItemsRepo.findById(new IssueItemsPK(ledgerId, issueId)).orElse(null);
    }

    public List<IssueItems> getIssueItemsByLedgerId(Integer ledgerId) {
        MySpecificationAdapter<IssueItems> issueItemsSpecification = new MySpecificationAdapter<>(IssueItems.class);
        return issueItemsRepo.findAll(issueItemsSpecification.eq("ledgerId", ledgerId));
    }

    public List<IssueItemsLineForm> getIssueItemsForDisplay(Integer ledgerId) {
        List<IssueItems> issueItemses = getIssueItemsByLedgerId(ledgerId);
        // マスタ類の取得
        List<CategoryMaster> categoryMasterList = categoryMasterRepo.findAll();
        List<ProcessMaster> processMasterList = processMasterRepo.findAll();
        List<SevereLevelMaster> severeLevelMasterList = severeLevelMasterRepo.findAll();
        List<StatusMaster> statusMasterList = statusMasterRepo.findAll();
        List<Users> usersList = userRepo.findAll();
        Map<String, String> sysConfigMap = getSystemConfig();
        // TODO: 表示上限を取得する処理を書く

        List<IssueItemsLineForm> issueItemsLineForms = new ArrayList<>();

        for (IssueItems item : issueItemses) {
            IssueItemsLineForm frm = new IssueItemsLineForm();

            // 台帳ID
            frm.setIssueId(item.getIssueId());

            // 重篤度
            frm.setSevereLevel(findSeverityById(severeLevelMasterList, item.getSevereLevelId()));

            // 対応ステータス
            frm.setActionStatus(findStatusById(statusMasterList, item.getActionStatusId()));

            // 発見日
            frm.setFoundDate(item.getFoundDate());

            // 発見者
            frm.setFoundUser(findUserById(usersList, item.getFoundUser()));

            // 製造工程
            frm.setProcess(findProcessById(processMasterList, item.getFoundProcessId()));

            // 問題
            frm.setIssueDetail(item.getIssueDetail());

            // 原因
            frm.setCaused(item.getCaused());

            // 対応内容
            frm.setCountermeasures(item.getCountermeasures());

            // 対応者
            frm.setCorrespondingUser(findUserById(usersList, item.getCorrespondingUserId()));

            // 対応時間

            // 対応終了日
            frm.setCorrespondingEndDate(item.getCorrespondingEndDate());

            // 確認者
            frm.setConfirmedUser(findUserById(usersList, item.getConfirmedId()));

            // 確認日
            frm.setConfirmedDate(item.getConfirmedDate());

            // 行のCSS
            //   対応ステータスIDが終了ステータスの立っているものに一致する場合は、終了色にする
            if (item.getConfirmedDate() != null || isItemFinished(item.getActionStatusId(), statusMasterList)) {
                frm.setLineCssStyle("background-color:silver;");
            } else if (item.getCorrespondingEndDate() != null) {
                frm.setLineCssStyle("background-color:cyan;");
            } else {
                frm.setLineCssStyle("");
            }

            issueItemsLineForms.add(frm);
        }

        return issueItemsLineForms;
    }

    @Transactional
    public void saveItem(IssueItems item) {
        // 更新日時をUpdateする
        item.setRowUpdatedAt(new Date());
        issueItemsRepo.save(item);
    }

    @Transactional
    public void removeItem(int ledgerId, int issueId) {
        IssueItemsPK pk = new IssueItemsPK(ledgerId, issueId);

        issueItemsRepo.deleteById(pk);
    }

    public boolean hasEditPrivilege(int ledgerId, Users users) {
        if (users == null) {
            return false;
        }

        IssueLedger ledger = issueLedgerRepo.findById(ledgerId).orElse(null);
        if (ledger == null) {
            return false;
        } else {
            Optional<LedgerRefUser> ledgerRefUserOpt = ledgerRefUserRepo.findById(new LedgerRefUserPK(ledgerId, users.getUsername()));
            return ledgerRefUserOpt.isPresent();
        }

    }

    public void mapMaster(Model model) {
        // カテゴリーマスタ
        List<CategoryMaster> categoryMasterList = categoryMasterRepo.findAllOrderByDispOrder();
        Map<Integer, String> categoryItemMap = new LinkedHashMap<>();
        for (CategoryMaster cm : categoryMasterList) {
            categoryItemMap.put(cm.getCategoryId(), cm.getCategoryName());
        }
        model.addAttribute("moduleCategoryList", categoryItemMap);

        // 工程マスタ
        List<ProcessMaster> processMasterList = processMasterRepo.findAllOrderByDispOrder();
        Map<Integer, String> processItemMap = new LinkedHashMap<>();
        for (ProcessMaster pm : processMasterList) {
            processItemMap.put(pm.getProcessId(), pm.getProcessName());
        }
        model.addAttribute("causedProcessList", processItemMap);

        // 緊急度マスタ
        List<SevereLevelMaster> severeLevelMasterList = severeLevelMasterRepo.findAllOrderByDispOrder();
        Map<Integer, String> severityItemMap = new LinkedHashMap<>();
        for (SevereLevelMaster slm : severeLevelMasterList) {
            severityItemMap.put(slm.getSevereLevelId(), slm.getSevereLevel());
        }
        model.addAttribute("severityList", severityItemMap);

        // ステータスマスタ
        List<StatusMaster> statusMasterList = statusMasterRepo.findAllOrderByDispOrder();
        Map<Integer, String> statusItemMap = new LinkedHashMap<>();
        for (StatusMaster sm : statusMasterList) {
            statusItemMap.put(sm.getStatusId(), sm.getStatusName());
        }
        model.addAttribute("statusList", statusItemMap);
    }

    public void mapRelatedUsers(Model model, int ledgerId) {
        List<Users> relatedUsers = userRepo.findRelatedUsers(ledgerId);
        Map<String, String> joinedUsers = new LinkedHashMap<>();
        for (Users u : relatedUsers) {
            joinedUsers.put(u.getUsername(), u.getDisplayName());
        }
        model.addAttribute("joinedUsers", joinedUsers);

        Map<String, String> correspondingUsers = new LinkedHashMap<>(joinedUsers);
        model.addAttribute("correspondingUsers", correspondingUsers);

        Map<String, String> confirmedUsers = new LinkedHashMap<>(joinedUsers);
        model.addAttribute("confirmedUsers", confirmedUsers);
    }

    public void mapEmptyUsers(Model model) {
        Map<String, String> empty1 = new LinkedHashMap<>();
        model.addAttribute("joinedUsers", empty1);

        Map<String, String> empty2 = new LinkedHashMap<>();
        model.addAttribute("correspondingUsers", empty2);

        Map<String, String> empty3 = new LinkedHashMap<>();
        model.addAttribute("confirmedUsers", empty3);
    }

    /**
     * 課題アイテムが、終了とみなされるか否かを判断する。
     *
     * @param statusId         課題アイテムの対応ステータスID
     * @param statusMasterList ステータスマスタのリスト
     * @return 終了とみなされる場合true、そぅでない場合false
     */
    private boolean isItemFinished(int statusId, List<StatusMaster> statusMasterList) {
        Optional<StatusMaster> stOpt = statusMasterList.stream().filter(StatusMaster::getTreatAsFinished).findAny();

        if (stOpt.isPresent()) {
            return stOpt.filter(r -> r.getStatusId() == statusId).isPresent();
        }

        return false;
    }
}
