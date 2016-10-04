-- Project Name : PIMS
-- Date/Time    : 2016/10/04 23:44:39
-- Author       : 2016 HAJIME Fukuna (a.k.a. f97one)
-- RDBMS Type   : PostgreSQL
-- Application  : A5:SQL Mk-2

-- 台帳参照ユーザー
drop table if exists LEDGER_REF_USER cascade;

create table LEDGER_REF_USER (
  LEDGER_ID integer
  , USER_ID character varying(32)
  , constraint LEDGER_REF_USER_PKC primary key (LEDGER_ID,USER_ID)
) ;

-- ステータスマスタ
drop table if exists STATUS_MASTER cascade;

create table STATUS_MASTER (
  STATUS_ID integer
  , STATUS_NAME character varying(16)
  , DISP_ORDER integer default 0
  , constraint STATUS_MASTER_PKC primary key (STATUS_ID)
) ;

-- ユーザーマスタ
drop table if exists USERS cascade;

create table USERS (
  USER_ID character varying(32)
  , ENCODED_PASSWD character varying(128) default ' ' not null
  , DISPLAY_NAME character varying(128)
  , MAIL_ADDRESS character varying(128)
  , LAST_LOGIN_DATE timestamp
  , AUTHORITY character varying(64) default ' ' not null
  , constraint USERS_PKC primary key (USER_ID)
) ;

-- カテゴリーマスタ
drop table if exists CATEGORY_MASTER cascade;

create table CATEGORY_MASTER (
  CATEGORY_ID integer
  , CATEGORY_NAME character varying(128)
  , DISP_ORDER integer
  , constraint CATEGORY_MASTER_PKC primary key (CATEGORY_ID)
) ;

-- 工程マスタ
drop table if exists PROCESS_MASTER cascade;

create table PROCESS_MASTER (
  PROCESS_ID integer
  , PROCESS_NAME character varying(16)
  , DISP_ORDER integer
  , constraint PROCESS_MASTER_PKC primary key (PROCESS_ID)
) ;

-- 緊急度マスタ
drop table if exists SEVERE_LEVEL_MASTER cascade;

create table SEVERE_LEVEL_MASTER (
  SEVERE_LEVEL_ID integer
  , SEVERE_LEVEL character varying(8)
  , DISP_ORDER integer default 0
  , constraint SEVERE_LEVEL_MASTER_PKC primary key (SEVERE_LEVEL_ID)
) ;

-- 課題項目
drop table if exists ISSUE_ITEMS cascade;

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
  , CORRESPONDING_TIME time
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
drop table if exists ISSUE_LEDGER cascade;

create table ISSUE_LEDGER (
  LEDGER_ID serial
  , LEDGER_NAME character varying(64)
  , OPEN_STATUS_ID integer
  , IS_PUBLIC boolean default false not null
  , LAST_UPDATED_AT timestamp
  , constraint ISSUE_LEDGER_PKC primary key (LEDGER_ID)
) ;

-- システム設定
drop table if exists SYSTEM_CONFIG cascade;

create table SYSTEM_CONFIG (
  CONFIG_KEY character varying(32)
  , CONFIG_VALUE character varying(256)
  , constraint SYSTEM_CONFIG_PKC primary key (CONFIG_KEY)
) ;

alter table LEDGER_REF_USER
  add constraint LEDGER_REF_USER_FK1 foreign key (USER_ID) references USERS(USER_ID)
  on delete cascade
  on update cascade;

alter table LEDGER_REF_USER
  add constraint LEDGER_REF_USER_FK2 foreign key (LEDGER_ID) references ISSUE_LEDGER(LEDGER_ID)
  on delete cascade
  on update cascade;

alter table ISSUE_ITEMS
  add constraint ISSUE_ITEMS_FK1 foreign key (CATEGORY_ID) references CATEGORY_MASTER(CATEGORY_ID)
  on delete cascade
  on update cascade;

alter table ISSUE_LEDGER
  add constraint ISSUE_LEDGER_FK1 foreign key (OPEN_STATUS_ID) references STATUS_MASTER(STATUS_ID)
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

comment on table LEDGER_REF_USER is '台帳参照ユーザー';
comment on column LEDGER_REF_USER.LEDGER_ID is '台帳ID';
comment on column LEDGER_REF_USER.USER_ID is 'ユーザーID';

comment on table STATUS_MASTER is 'ステータスマスタ';
comment on column STATUS_MASTER.STATUS_ID is 'ステータスID';
comment on column STATUS_MASTER.STATUS_NAME is 'ステータス';
comment on column STATUS_MASTER.DISP_ORDER is '表示順序';

comment on table USERS is 'ユーザーマスタ';
comment on column USERS.USER_ID is 'ユーザーID';
comment on column USERS.ENCODED_PASSWD is 'パスワード';
comment on column USERS.DISPLAY_NAME is '表示名';
comment on column USERS.MAIL_ADDRESS is 'メールアドレス';
comment on column USERS.LAST_LOGIN_DATE is '最終ログイン日時';
comment on column USERS.AUTHORITY is '権限';

comment on table CATEGORY_MASTER is 'カテゴリーマスタ';
comment on column CATEGORY_MASTER.CATEGORY_ID is 'カテゴリーID';
comment on column CATEGORY_MASTER.CATEGORY_NAME is 'カテゴリー';
comment on column CATEGORY_MASTER.DISP_ORDER is '表示順序';

comment on table PROCESS_MASTER is '工程マスタ';
comment on column PROCESS_MASTER.PROCESS_ID is '工程ID';
comment on column PROCESS_MASTER.PROCESS_NAME is '工程';
comment on column PROCESS_MASTER.DISP_ORDER is '表示順序';

comment on table SEVERE_LEVEL_MASTER is '緊急度マスタ';
comment on column SEVERE_LEVEL_MASTER.SEVERE_LEVEL_ID is '緊急度ID';
comment on column SEVERE_LEVEL_MASTER.SEVERE_LEVEL is '緊急度';
comment on column SEVERE_LEVEL_MASTER.DISP_ORDER is '表示順序';

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
comment on column ISSUE_LEDGER.IS_PUBLIC is '公開可能フラグ	 trueのときログインしていなくても参照可能';
comment on column ISSUE_LEDGER.LAST_UPDATED_AT is '更新日時';

comment on table SYSTEM_CONFIG is 'システム設定';
comment on column SYSTEM_CONFIG.CONFIG_KEY is '設定キー';
comment on column SYSTEM_CONFIG.CONFIG_VALUE is '設定値';

