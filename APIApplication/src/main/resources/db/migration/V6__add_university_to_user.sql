ALTER TABLE user_info
    ADD university_id INTEGER;

ALTER TABLE user_info
    ADD CONSTRAINT FK_USER_INFO_ON_UNIVERSITY FOREIGN KEY (university_id) REFERENCES universities (id);