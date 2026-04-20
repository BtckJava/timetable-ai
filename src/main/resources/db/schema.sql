CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS study_plans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    domain VARCHAR(120) NOT NULL,
    goal TEXT NOT NULL,
    level VARCHAR(50) NOT NULL,
    duration_days INTEGER NOT NULL CHECK (duration_days > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_study_plans_user
        FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS schedule_slots (
    id BIGSERIAL PRIMARY KEY,
    study_plan_id BIGINT NOT NULL,
    date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    topic VARCHAR(255) NOT NULL,
    sub_topic VARCHAR(255),
    resource_url TEXT,
    CONSTRAINT fk_schedule_slots_plan
        FOREIGN KEY (study_plan_id) REFERENCES study_plans(id) ON DELETE CASCADE,
    CONSTRAINT chk_time_order CHECK (start_time < end_time)
);

CREATE INDEX IF NOT EXISTS idx_schedule_slots_date ON schedule_slots(date);
CREATE INDEX IF NOT EXISTS idx_schedule_slots_study_plan_id ON schedule_slots(study_plan_id);
CREATE INDEX IF NOT EXISTS idx_schedule_slots_plan_date ON schedule_slots(study_plan_id, date);

