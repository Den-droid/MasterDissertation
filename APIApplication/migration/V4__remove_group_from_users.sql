ALTER TABLE users
    DROP CONSTRAINT fkemfuglprp85bh5xwhfm898ysc;

ALTER TABLE users
    DROP COLUMN group_id;

CREATE SEQUENCE IF NOT EXISTS user_info_id_seq;
ALTER TABLE user_info
    ALTER COLUMN id SET NOT NULL;
ALTER TABLE user_info
    ALTER COLUMN id SET DEFAULT nextval('user_info_id_seq');

ALTER SEQUENCE user_info_id_seq OWNED BY user_info.id;