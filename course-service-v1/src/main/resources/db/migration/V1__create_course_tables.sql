CREATE TABLE courses (
    id            uuid PRIMARY KEY,
    code          varchar(50)  NOT NULL UNIQUE,
    title         varchar(200) NOT NULL,
    summary       text,
    status        varchar(30)  NOT NULL,
    created_by    uuid,
    created_at    timestamp    NOT NULL,
    updated_at    timestamp    NOT NULL
);

CREATE TABLE course_teachers (
    id               uuid PRIMARY KEY,
    course_id        uuid         NOT NULL REFERENCES courses(id),
    teacher_user_id  uuid         NOT NULL,
    assigned_by      uuid,
    assigned_at      timestamp,
    created_at       timestamp    NOT NULL,
    updated_at       timestamp    NOT NULL,
    UNIQUE (course_id, teacher_user_id)
);

CREATE TABLE course_sections (
    id          uuid PRIMARY KEY,
    course_id   uuid         NOT NULL REFERENCES courses(id),
    title       varchar(200) NOT NULL,
    description text,
    position    int          NOT NULL,
    visible     boolean      NOT NULL,
    created_at  timestamp    NOT NULL,
    updated_at  timestamp    NOT NULL,
    UNIQUE (course_id, position)
);

CREATE TABLE course_resources (
    id             uuid PRIMARY KEY,
    course_id      uuid         NOT NULL REFERENCES courses(id),
    section_id     uuid         NOT NULL REFERENCES course_sections(id),
    resource_type  varchar(30)  NOT NULL,
    title          varchar(200) NOT NULL,
    position       int          NOT NULL,
    visible        boolean      NOT NULL,
    published      boolean      NOT NULL,
    created_by     uuid,
    created_at     timestamp    NOT NULL,
    updated_at     timestamp    NOT NULL,
    UNIQUE (section_id, position)
);

CREATE TABLE resource_files (
    id            uuid PRIMARY KEY,
    resource_id   uuid         NOT NULL UNIQUE REFERENCES course_resources(id),
    file_name     varchar(255) NOT NULL,
    content_type  varchar(120),
    size_bytes    bigint,
    checksum      varchar(128),
    file_data     bytea,
    uploaded_at   timestamp,
    updated_at    timestamp
);

CREATE TABLE resource_texts (
    id              uuid PRIMARY KEY,
    resource_id     uuid NOT NULL UNIQUE REFERENCES course_resources(id),
    content_text    text,
    updated_at      timestamp
);

CREATE TABLE resource_urls (
    id              uuid PRIMARY KEY,
    resource_id     uuid NOT NULL UNIQUE REFERENCES course_resources(id),
    url             text NOT NULL,
    url_kind        varchar(30),
    open_in_new_tab boolean,
    updated_at      timestamp
);

CREATE TABLE assignments (
    id                 uuid PRIMARY KEY,
    resource_id        uuid NOT NULL UNIQUE REFERENCES course_resources(id),
    instructions_text  text,
    available_from     timestamp,
    due_at             timestamp,
    max_score          numeric(5,2),
    allow_resubmission boolean,
    updated_at         timestamp
);

CREATE TABLE assignment_material_files (
    id              uuid PRIMARY KEY,
    assignment_id   uuid         NOT NULL REFERENCES assignments(id),
    file_name       varchar(255) NOT NULL,
    content_type    varchar(120),
    size_bytes      bigint,
    checksum        varchar(128),
    file_data       bytea,
    uploaded_at     timestamp
);

CREATE TABLE course_recordings (
    id               uuid PRIMARY KEY,
    resource_url_id  uuid         NOT NULL UNIQUE REFERENCES resource_urls(id),
    recording_name   varchar(255) NOT NULL,
    class_date       date,
    comment_text     text,
    uploaded_at      timestamp,
    updated_at       timestamp
);
