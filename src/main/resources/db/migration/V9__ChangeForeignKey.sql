-- issue_ledger 側の外部キー削除
ALTER TABLE ISSUE_LEDGER DROP CONSTRAINT ISSUE_LEDGER_FK1;

-- issue_items に外部キーを定義
ALTER TABLE ISSUE_ITEMS
  ADD CONSTRAINT ISSUE_ITEMS_FK5 FOREIGN KEY (ACTION_STATUS_ID) REFERENCES STATUS_MASTER(STATUS_ID)
  ON DELETE CASCADE
  ON UPDATE CASCADE;

