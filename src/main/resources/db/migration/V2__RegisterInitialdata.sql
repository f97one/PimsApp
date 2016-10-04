-- 初期値投入

-- ステータスマスタ
INSERT INTO STATUS_MASTER (STATUS_ID, STATUS_NAME, DISP_ORDER) VALUES ('1', '新規', 0);
INSERT INTO STATUS_MASTER (STATUS_ID, STATUS_NAME, DISP_ORDER) VALUES ('2', '進行中',	1);
INSERT INTO STATUS_MASTER (STATUS_ID, STATUS_NAME, DISP_ORDER) VALUES ('3', '解決', 2);
INSERT INTO STATUS_MASTER (STATUS_ID, STATUS_NAME, DISP_ORDER) VALUES ('4', '終了', 3);
INSERT INTO STATUS_MASTER (STATUS_ID, STATUS_NAME, DISP_ORDER) VALUES ('5', '却下', 4);

-- 緊急度マスタ
INSERT INTO SEVERE_LEVEL_MASTER (SEVERE_LEVEL_ID, SEVERE_LEVEL, DISP_ORDER) VALUES ('1', '低', 0);
INSERT INTO SEVERE_LEVEL_MASTER (SEVERE_LEVEL_ID, SEVERE_LEVEL, DISP_ORDER) VALUES ('2', '中', 1);
INSERT INTO SEVERE_LEVEL_MASTER (SEVERE_LEVEL_ID, SEVERE_LEVEL, DISP_ORDER) VALUES ('3', '高', 2);
INSERT INTO SEVERE_LEVEL_MASTER (SEVERE_LEVEL_ID, SEVERE_LEVEL, DISP_ORDER) VALUES ('4', '非常に高', 3);

-- 工程マスタ
INSERT INTO PROCESS_MASTER (PROCESS_ID, PROCESS_NAME, DISP_ORDER) VALUES ('1', '基本設計', 0);
INSERT INTO PROCESS_MASTER (PROCESS_ID, PROCESS_NAME, DISP_ORDER) VALUES ('2', '詳細設計', 1);
INSERT INTO PROCESS_MASTER (PROCESS_ID, PROCESS_NAME, DISP_ORDER) VALUES ('3', 'PG', 2);
INSERT INTO PROCESS_MASTER (PROCESS_ID, PROCESS_NAME, DISP_ORDER) VALUES ('4', '単体テスト', 3);
INSERT INTO PROCESS_MASTER (PROCESS_ID, PROCESS_NAME, DISP_ORDER) VALUES ('5', '結合テスト', 4);
INSERT INTO PROCESS_MASTER (PROCESS_ID, PROCESS_NAME, DISP_ORDER) VALUES ('6', '受入テスト', 5);

-- システム設定
INSERT INTO SYSTEM_CONFIG (CONFIG_KEY, CONFIG_VALUE) VALUES ('AppTitle', 'PIMS Beta');

-- ユーザー
INSERT INTO USERS (USER_ID, ENCODED_PASSWD, DISPLAY_NAME, MAIL_ADDRESS, LAST_LOGIN_DATE, AUTHORITY) VALUES ('admin', '$2a$10$eE6cp8dD.I7YHfA/9n75RuUIlEPfKbtx0imJXJYAicd7c9aXu4RZa', 'PIMS Admin', 'pims@example.net', '2016-01-01 12:34:56', 'authority_admin');
