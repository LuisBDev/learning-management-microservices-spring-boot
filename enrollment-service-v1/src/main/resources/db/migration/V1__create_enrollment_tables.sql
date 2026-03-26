CREATE TABLE course_enrollments (
    id               uuid PRIMARY KEY,
    course_id        uuid        NOT NULL,
    student_user_id  uuid        NOT NULL,
    status           varchar(30) NOT NULL,
    enrolled_at      timestamp   NOT NULL,
    updated_at       timestamp,
    UNIQUE (course_id, student_user_id)
);

CREATE TABLE enrollment_events (
    id               uuid PRIMARY KEY,
    enrollment_id    uuid        NOT NULL REFERENCES course_enrollments(id),
    event_type       varchar(40) NOT NULL,
    previous_status  varchar(30),
    new_status       varchar(30),
    event_detail     text,
    triggered_by     uuid,
    created_at       timestamp   NOT NULL
);
