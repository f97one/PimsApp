-- Project Name : PIMS
-- Date/Time    : 2016/09/18 20:25:53
-- Author       : 2016 HAJIME Fukuna (a.k.a. f97one)
-- RDBMS Type   : PostgreSQL
-- Application  : A5:SQL Mk-2

-- ステータスマスタ
create table STATUS_MASTER (
  STATUS_ID serial
  , STATUS_NAME character varying(16)
  , constraint STATUS_MASTER_PKC primary key (STATUS_ID)
) ;

-- グループ所属
create table GROUP_BELONGINGS (
  USER_ID character varying(32)
  , GROUP_ID character varying(32)
  , constraint GROUP_BELONGINGS_PKC primary key (USER_ID,GROUP_ID)
) ;

-- グループマスタ
create table GROUP_MASTER (
  GROUP_ID character varying(32)
  , GROUP_DESC character varying(2048)
  , constraint GROUP_MASTER_PKC primary key (GROUP_ID)
) ;

-- ユーザーマスタ
create table USERS (
  USER_ID character varying(32)
  , ENCODED_PASSWD character varying(128) not null
  , DISPLAY_NAME character varying(128)
  , LAST_LOGIN_DATE timestamp
  , MAIN_GROUP_ID character varying
  , constraint USERS_PKC primary key (USER_ID)
) ;

-- カテゴリーマスタ
create table CATEGORY_MASTER (
  CATEGORY_ID serial
  , CATEGORY_NAME character varying(128)
  , constraint CATEGORY_MASTER_PKC primary key (CATEGORY_ID)
) ;

-- 工程マスタ
create table PROCESS_MASTER (
  PROCESS_ID serial
  , PROCESS_NAME character varying(16)
  , constraint PROCESS_MASTER_PKC primary key (PROCESS_ID)
) ;

-- 緊急度マスタ
create table SEVERE_LEVEL_MASTER (
  SEVERE_LEVEL_ID serial
  , SEVERE_LEVEL character varying(8)
  , constraint SEVERE_LEVEL_MASTER_PKC primary key (SEVERE_LEVEL_ID)
) ;

-- 課題項目
create table ISSUE_ITEMS (
  LEDGER_ID integer
  , ISSUE_ID serial
  , ACTION_STATUS_ID integer
  , SEVERE_LEVEL_ID integer
  , FOUND_USER character varying(32)
  , FOUND_DATE timestamp
  , FOUND_PROCESS_ID INT
  , CATEGORY_ID integer
  , ISSUE_DETAIL character varying(16384)
  , CAUSED character varying(16384)
  , COUNTERMEASURES character varying(16384)
  , CORRESPONDING_USER_ID character varying(32)
  , CORRESPONDING_TIME time default 00:00:00
  , CORRESPONDING_END_DATE timestamp
  , CONFIRMED_ID character varying(32)
  , COMFIRMED_DATE timestamp
  , constraint ISSUE_ITEMS_PKC primary key (LEDGER_ID,ISSUE_ID)
) ;

create index ISSUE_ITEMS_IX1
  on ISSUE_ITEMS(LEDGER_ID,ISSUE_ID,SEVERE_LEVEL_ID);

create index ISSUE_ITEMS_IX2
  on ISSUE_ITEMS(LEDGER_ID,ISSUE_ID,CORRESPONDING_END_DATE);

-- 課題台帳
create table ISSUE_LEDGER (
  LEDGER_ID serial
  , LEDGER_NAME character varying(64)
  , OPEN_STATUS_ID integer
  , constraint ISSUE_LEDGER_PKC primary key (LEDGER_ID)
) ;

-- システム設定
create table SYSTEM_CONFIG (
  CONFIG_KEY character varying(32)
  , CONFIG_VALUE character varying(256)
  , constraint SYSTEM_CONFIG_PKC primary key (CONFIG_KEY)
) ;

alter table ISSUE_LEDGER
  add constraint ISSUE_LEDGER_FK1 foreign key (OPEN_STATUS_ID) references STATUS_MASTER(STATUS_ID)
  on delete cascade
  on update cascade;

alter table ISSUE_ITEMS
  add constraint ISSUE_ITEMS_FK1 foreign key (CATEGORY_ID) references CATEGORY_MASTER(CATEGORY_ID)
  on delete cascade
  on update cascade;

alter table ISSUE_ITEMS
  add constraint ISSUE_ITEMS_FK2 foreign key (FOUND_PROCESS_ID) references PROCESS_MASTER(PROCESS_ID)
  on delete cascade
  on update cascade;

alter table ISSUE_ITEMS
  add constraint ISSUE_ITEMS_FK3 foreign key (SEVERE_LEVEL_ID) references SEVERE_LEVEL_MASTER(SEVERE_LEVEL_ID)
  on delete cascade
  on update cascade;

alter table ISSUE_ITEMS
  add constraint ISSUE_ITEMS_FK4 foreign key (LEDGER_ID) references ISSUE_LEDGER(LEDGER_ID)
  on delete cascade
  on update cascade;

comment on table STATUS_MASTER is 'ステータスマスタ';
comment on column STATUS_MASTER.STATUS_ID is 'ステータスID';
comment on column STATUS_MASTER.STATUS_NAME is 'ステータス';

comment on table GROUP_BELONGINGS is 'グループ所属';
comment on column GROUP_BELONGINGS.USER_ID is 'ユーザーID';
comment on column GROUP_BELONGINGS.GROUP_ID is 'グループID';

comment on table GROUP_MASTER is 'グループマスタ';
comment on column GROUP_MASTER.GROUP_ID is 'グループID';
comment on column GROUP_MASTER.GROUP_DESC is 'グループ概要';

comment on table USERS is 'ユーザーマスタ';
comment on column USERS.USER_ID is 'ユーザーID';
comment on column USERS.ENCODED_PASSWD is 'パスワード';
comment on column USERS.DISPLAY_NAME is '表示名';
comment on column USERS.LAST_LOGIN_DATE is '最終ログイン日時';
comment on column USERS.MAIN_GROUP_ID is '主グループ';

comment on table CATEGORY_MASTER is 'カテゴリーマスタ';
comment on column CATEGORY_MASTER.CATEGORY_ID is 'カテゴリーID';
comment on column CATEGORY_MASTER.CATEGORY_NAME is 'カテゴリー';

comment on table PROCESS_MASTER is '工程マスタ';
comment on column PROCESS_MASTER.PROCESS_ID is '工程ID';
comment on column PROCESS_MASTER.PROCESS_NAME is '工程';

comment on table SEVERE_LEVEL_MASTER is '緊急度マスタ';
comment on column SEVERE_LEVEL_MASTER.SEVERE_LEVEL_ID is '緊急度ID';
comment on column SEVERE_LEVEL_MASTER.SEVERE_LEVEL is '緊急度';

comment on table ISSUE_ITEMS is '課題項目';
comment on column ISSUE_ITEMS.LEDGER_ID is '課題台帳ID';
comment on column ISSUE_ITEMS.ISSUE_ID is '課題項目ID';
comment on column ISSUE_ITEMS.ACTION_STATUS_ID is '対応状況';
comment on column ISSUE_ITEMS.SEVERE_LEVEL_ID is '緊急度';
comment on column ISSUE_ITEMS.FOUND_USER is '発見者';
comment on column ISSUE_ITEMS.FOUND_DATE is '発見日';
comment on column ISSUE_ITEMS.FOUND_PROCESS_ID is '工程';
comment on column ISSUE_ITEMS.CATEGORY_ID is 'カテゴリー';
comment on column ISSUE_ITEMS.ISSUE_DETAIL is '障害内容';
comment on column ISSUE_ITEMS.CAUSED is '原因';
comment on column ISSUE_ITEMS.COUNTERMEASURES is '対策内容';
comment on column ISSUE_ITEMS.CORRESPONDING_USER_ID is '対応者';
comment on column ISSUE_ITEMS.CORRESPONDING_TIME is '対応時間';
comment on column ISSUE_ITEMS.CORRESPONDING_END_DATE is '対応終了日';
comment on column ISSUE_ITEMS.CONFIRMED_ID is '確認者';
comment on column ISSUE_ITEMS.COMFIRMED_DATE is '確認日';

comment on table ISSUE_LEDGER is '課題台帳';
comment on column ISSUE_LEDGER.LEDGER_ID is '台帳ID';
comment on column ISSUE_LEDGER.LEDGER_NAME is '台帳名';
comment on column ISSUE_LEDGER.OPEN_STATUS_ID is 'ステータス';

comment on table SYSTEM_CONFIG is 'システム設定';
comment on column SYSTEM_CONFIG.CONFIG_KEY is '設定キー';
comment on column SYSTEM_CONFIG.CONFIG_VALUE is '設定値';

