CREATE TABLE assignment_material_files
(
    id            UUID         NOT NULL,
    assignment_id UUID         NOT NULL,
    file_name     VARCHAR(255) NOT NULL,
    content_type  VARCHAR(120),
    size_bytes    BIGINT,
    checksum      VARCHAR(128),
    file_data     BYTEA,
    uploaded_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_assignment_material_files PRIMARY KEY (id)
);

CREATE TABLE assignments
(
    id                 UUID NOT NULL,
    resource_id        UUID NOT NULL,
    instructions_text  TEXT,
    available_from     TIMESTAMP WITHOUT TIME ZONE,
    due_at             TIMESTAMP WITHOUT TIME ZONE,
    max_score          DECIMAL(5, 2),
    allow_resubmission BOOLEAN,
    updated_at         TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_assignments PRIMARY KEY (id)
);

CREATE TABLE course_recordings
(
    id              UUID         NOT NULL,
    resource_url_id UUID         NOT NULL,
    recording_name  VARCHAR(255) NOT NULL,
    class_date      date,
    comment_text    TEXT,
    uploaded_at     TIMESTAMP WITHOUT TIME ZONE,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_course_recordings PRIMARY KEY (id)
);

CREATE TABLE course_resources
(
    id            UUID         NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    course_id     UUID         NOT NULL,
    section_id    UUID         NOT NULL,
    resource_type VARCHAR(30)  NOT NULL,
    title         VARCHAR(200) NOT NULL,
    position      INTEGER      NOT NULL,
    visible       BOOLEAN      NOT NULL,
    published     BOOLEAN      NOT NULL,
    created_by    UUID,
    CONSTRAINT pk_course_resources PRIMARY KEY (id)
);

CREATE TABLE course_sections
(
    id          UUID         NOT NULL,
    created_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    course_id   UUID         NOT NULL,
    title       VARCHAR(200) NOT NULL,
    description TEXT,
    position    INTEGER      NOT NULL,
    visible     BOOLEAN      NOT NULL,
    CONSTRAINT pk_course_sections PRIMARY KEY (id)
);

CREATE TABLE course_teachers
(
    id              UUID NOT NULL,
    created_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    course_id       UUID NOT NULL,
    teacher_user_id UUID NOT NULL,
    assigned_by     UUID,
    assigned_at     TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_course_teachers PRIMARY KEY (id)
);

CREATE TABLE courses
(
    id         UUID         NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    code       VARCHAR(50)  NOT NULL,
    title      VARCHAR(200) NOT NULL,
    summary    TEXT,
    status     VARCHAR(30)  NOT NULL,
    created_by UUID,
    CONSTRAINT pk_courses PRIMARY KEY (id)
);

CREATE TABLE resource_files
(
    id           UUID         NOT NULL,
    resource_id  UUID         NOT NULL,
    file_name    VARCHAR(255) NOT NULL,
    content_type VARCHAR(120),
    size_bytes   BIGINT,
    checksum     VARCHAR(128),
    file_data    BYTEA,
    uploaded_at  TIMESTAMP WITHOUT TIME ZONE,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_resource_files PRIMARY KEY (id)
);

CREATE TABLE resource_texts
(
    id           UUID NOT NULL,
    resource_id  UUID NOT NULL,
    content_text TEXT,
    updated_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_resource_texts PRIMARY KEY (id)
);

CREATE TABLE resource_urls
(
    id              UUID NOT NULL,
    resource_id     UUID NOT NULL,
    url             TEXT NOT NULL,
    url_kind        VARCHAR(30),
    open_in_new_tab BOOLEAN,
    updated_at      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_resource_urls PRIMARY KEY (id)
);

ALTER TABLE course_resources
    ADD CONSTRAINT uc_1854bb314ff8b4f95e1de6248 UNIQUE (section_id, position);

ALTER TABLE course_teachers
    ADD CONSTRAINT uc_3b6208cadaa7c5621abeb36d7 UNIQUE (course_id, teacher_user_id);

ALTER TABLE course_sections
    ADD CONSTRAINT uc_568357cf5f800664988317568 UNIQUE (course_id, position);

ALTER TABLE assignments
    ADD CONSTRAINT uc_assignments_resource UNIQUE (resource_id);

ALTER TABLE course_recordings
    ADD CONSTRAINT uc_course_recordings_resource_url UNIQUE (resource_url_id);

ALTER TABLE courses
    ADD CONSTRAINT uc_courses_code UNIQUE (code);

ALTER TABLE resource_files
    ADD CONSTRAINT uc_resource_files_resource UNIQUE (resource_id);

ALTER TABLE resource_texts
    ADD CONSTRAINT uc_resource_texts_resource UNIQUE (resource_id);

ALTER TABLE resource_urls
    ADD CONSTRAINT uc_resource_urls_resource UNIQUE (resource_id);

ALTER TABLE assignments
    ADD CONSTRAINT FK_ASSIGNMENTS_ON_RESOURCE FOREIGN KEY (resource_id) REFERENCES course_resources (id);

ALTER TABLE assignment_material_files
    ADD CONSTRAINT FK_ASSIGNMENT_MATERIAL_FILES_ON_ASSIGNMENT FOREIGN KEY (assignment_id) REFERENCES assignments (id);

ALTER TABLE course_recordings
    ADD CONSTRAINT FK_COURSE_RECORDINGS_ON_RESOURCE_URL FOREIGN KEY (resource_url_id) REFERENCES resource_urls (id);

ALTER TABLE course_resources
    ADD CONSTRAINT FK_COURSE_RESOURCES_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

ALTER TABLE course_resources
    ADD CONSTRAINT FK_COURSE_RESOURCES_ON_SECTION FOREIGN KEY (section_id) REFERENCES course_sections (id);

ALTER TABLE course_sections
    ADD CONSTRAINT FK_COURSE_SECTIONS_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

ALTER TABLE course_teachers
    ADD CONSTRAINT FK_COURSE_TEACHERS_ON_COURSE FOREIGN KEY (course_id) REFERENCES courses (id);

ALTER TABLE resource_files
    ADD CONSTRAINT FK_RESOURCE_FILES_ON_RESOURCE FOREIGN KEY (resource_id) REFERENCES course_resources (id);

ALTER TABLE resource_texts
    ADD CONSTRAINT FK_RESOURCE_TEXTS_ON_RESOURCE FOREIGN KEY (resource_id) REFERENCES course_resources (id);

ALTER TABLE resource_urls
    ADD CONSTRAINT FK_RESOURCE_URLS_ON_RESOURCE FOREIGN KEY (resource_id) REFERENCES course_resources (id);