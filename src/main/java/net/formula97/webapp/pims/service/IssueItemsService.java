package net.formula97.webapp.pims.service;

import net.formula97.webapp.pims.domain.*;
import net.formula97.webapp.pims.repository.IssueItemsRepository;
import net.formula97.webapp.pims.repository.IssueLedgerRepository;
import net.formula97.webapp.pims.repository.LedgerRefUserRepository;
import net.formula97.webapp.pims.repository.MySpecificationAdapter;
import net.formula97.webapp.pims.web.forms.IssueItemsLineForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    public IssueItems getIssueItem(Integer issueId, Integer ledgerId) {
        return issueItemsRepo.findOne(new IssueItemsPK(ledgerId, issueId));
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
            if (item.getConfirmedDate() != null) {
                frm.setLineCssStyle("background-color:silver;");
            } else if (item.getCorrespondingEndDate() != null) {
                frm.setLineCssStyle("background-color:cyan");
            } else {
                frm.setLineCssStyle("");
            }

            issueItemsLineForms.add(frm);
        }

        return issueItemsLineForms;
    }

    @Transactional
    public void saveItem(IssueItems item) {
        issueItemsRepo.save(item);
    }

    public boolean hasEditPrivilege(int ledgerId, Users users) {
        if (users == null) {
            return false;
        }

        IssueLedger ledger = issueLedgerRepo.findOne(ledgerId);
        if (ledger == null) {
            return false;
        } else {
            LedgerRefUser ledgerRefUser = ledgerRefUserRepo.findOne(new LedgerRefUserPK(ledgerId, users.getUsername()));
            if (ledgerRefUser == null) {
                return false;
            }
        }

        return true;
    }
}
