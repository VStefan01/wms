INSERT into app_user (version,deleted, email, name, active, password) values (1,b'0','alex@ghb-software.com','Alexandru Gheboianu', b'1','$2a$10$zhlLopDUMrE0NR/aMfejA.Krv2qDQ.hc8K6d1wql3H0HySOYykSFy');


INSERT INTO groups (version, name, application_group) VALUES ('1', 'group.administrators', b'1');
INSERT INTO groups (version, name, application_group) VALUES ('1', 'group.superadmin', b'0');
INSERT INTO groups (version, name, application_group) VALUES ('1', 'group.warehouseManager', b'1');

INSERT INTO role_category (name) VALUES ('role.category.users');
INSERT INTO role_category (name) VALUES ('role.category.warehouse');


INSERT INTO role (description, label, name,role_category_id) VALUES ('role.desc.users.read', 'role.users.read', 'USERS_READ',1);
INSERT INTO role (description, label, name,role_category_id) VALUES ('role.desc.groups.read', 'role.groups.read', 'GROUPS_READ',1);
INSERT INTO role (description, label, name,role_category_id) VALUES ('role.desc.users.write', 'role.users.write', 'USERS_WRITE',1);
INSERT INTO role (description, label, name,role_category_id) VALUES ('role.desc.groups.write', 'role.groups.write', 'GROUPS_WRITE',1);
INSERT INTO role (description, label, name,role_category_id) VALUES ('role.desc.buildings.read', 'role.buildings.read', 'BUILDINGS_READ',2);
INSERT INTO role (description, label, name,role_category_id) VALUES ('role.desc.buildings.write', 'role.buildings.write', 'BUILDINGS_WRITE',2);
INSERT INTO role (description, label, name,role_category_id) VALUES ('role.desc.superadmin', 'role.superadmin', 'SUPER_ADMIN',null);

INSERT INTO group_roles (group_id, role_id) VALUES ('1', '1');
INSERT INTO group_roles (group_id, role_id) VALUES ('1', '2');
INSERT INTO group_roles (group_id, role_id) VALUES ('2', '3');

INSERT INTO group_users (group_id, user_id) VALUES ('1','1');


--Required for 'Remember Me' token:
CREATE TABLE IF NOT EXISTS persistent_logins (
    username VARCHAR(64) NOT NULL,
    series VARCHAR(64) NOT NULL,
    token VARCHAR(64) NOT NULL,
    last_used TIMESTAMP NOT NULL,
    PRIMARY KEY (series)
);