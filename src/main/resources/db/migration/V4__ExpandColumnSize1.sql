-- expand column
ALTER TABLE ISSUE_ITEMS ALTER ISSUE_DETAIL TYPE character varying(32768);
ALTER TABLE ISSUE_ITEMS ALTER CAUSED TYPE character varying(32768);
ALTER TABLE ISSUE_ITEMS ALTER COUNTERMEASURES TYPE character varying(32768);
