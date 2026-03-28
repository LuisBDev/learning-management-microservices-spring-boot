CREATE TABLE assignment_grades
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    assignment_id     UUID                        NOT NULL,
    course_id         UUID                        NOT NULL,
    student_user_id   UUID                        NOT NULL,
    submission_id     UUID                        NOT NULL,
    score             DECIMAL(5, 2),
    grade_status      VARCHAR(30)                 NOT NULL,
    teacher_comment   TEXT,
    graded_by_user_id UUID,
    graded_at         TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_assignment_grades PRIMARY KEY (id)
);

CREATE TABLE assignment_submission_files
(
    id            UUID         NOT NULL,
    submission_id UUID         NOT NULL,
    file_name     VARCHAR(255) NOT NULL,
    content_type  VARCHAR(120),
    size_bytes    BIGINT,
    checksum      VARCHAR(128),
    file_data     BYTEA,
    uploaded_at   TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_assignment_submission_files PRIMARY KEY (id)
);

CREATE TABLE assignment_submissions
(
    id                UUID                        NOT NULL,
    created_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    assignment_id     UUID                        NOT NULL,
    course_id         UUID                        NOT NULL,
    student_user_id   UUID                        NOT NULL,
    submitted_at      TIMESTAMP WITHOUT TIME ZONE,
    submission_status VARCHAR(30)                 NOT NULL,
    student_comment   TEXT,
    CONSTRAINT pk_assignment_submissions PRIMARY KEY (id)
);

ALTER TABLE assignment_submissions
    ADD CONSTRAINT uc_74015789c9d9cdbc384dcd5fa UNIQUE (assignment_id, student_user_id);

ALTER TABLE assignment_grades
    ADD CONSTRAINT uc_assignment_grades_submission UNIQUE (submission_id);

ALTER TABLE assignment_grades
    ADD CONSTRAINT FK_ASSIGNMENT_GRADES_ON_SUBMISSION FOREIGN KEY (submission_id) REFERENCES assignment_submissions (id);

ALTER TABLE assignment_submission_files
    ADD CONSTRAINT FK_ASSIGNMENT_SUBMISSION_FILES_ON_SUBMISSION FOREIGN KEY (submission_id) REFERENCES assignment_submissions (id);