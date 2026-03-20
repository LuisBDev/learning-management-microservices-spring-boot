-- Roles (with ROLE_ prefix for Spring Security)
INSERT INTO roles (name) VALUES ('ROLE_ADMIN');
INSERT INTO roles (name) VALUES ('ROLE_TEACHER');
INSERT INTO roles (name) VALUES ('ROLE_STUDENT');

-- Permissions
INSERT INTO permissions (name) VALUES ('USER_MANAGE');
INSERT INTO permissions (name) VALUES ('ROLE_MANAGE');
INSERT INTO permissions (name) VALUES ('PERMISSION_MANAGE');
INSERT INTO permissions (name) VALUES ('COURSE_CREATE');
INSERT INTO permissions (name) VALUES ('COURSE_UPDATE');
INSERT INTO permissions (name) VALUES ('COURSE_PUBLISH');
INSERT INTO permissions (name) VALUES ('SECTION_MANAGE');
INSERT INTO permissions (name) VALUES ('RESOURCE_MANAGE');
INSERT INTO permissions (name) VALUES ('ASSIGNMENT_MANAGE');
INSERT INTO permissions (name) VALUES ('ASSIGNMENT_GRADE');
INSERT INTO permissions (name) VALUES ('ENROLLMENT_MANAGE');
INSERT INTO permissions (name) VALUES ('ENROLLMENT_SUSPEND');
INSERT INTO permissions (name) VALUES ('ENROLLMENT_CANCEL');

-- ROLE_ADMIN: all permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_ADMIN';

-- ROLE_TEACHER: course, section, resource, assignment, enrollment, grade permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_TEACHER'
  AND p.name IN (
    'COURSE_CREATE', 'COURSE_UPDATE',
    'SECTION_MANAGE', 'RESOURCE_MANAGE',
    'ASSIGNMENT_MANAGE', 'ASSIGNMENT_GRADE'
  );
