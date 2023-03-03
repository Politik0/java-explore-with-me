delete from endpoint_hits;

ALTER TABLE endpoint_hits ALTER COLUMN ID RESTART WITH 1;