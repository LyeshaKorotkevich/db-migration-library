CREATE SEQUENCE users_id_seq START 101;
CREATE TABLE Users (
    user_id INT NOT NULL DEFAULT nextval('users_id_seq') PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE SEQUENCE projects_id_seq START 101;
CREATE TABLE Projects (
    project_id INT NOT NULL DEFAULT nextval('projects_id_seq') PRIMARY KEY,
    creator_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_date DATE,
    end_date DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_creator FOREIGN KEY (creator_id) REFERENCES Users(user_id) ON DELETE CASCADE
);

CREATE SEQUENCE tasks_id_seq START 101;
CREATE TABLE Tasks (
    task_id INT NOT NULL DEFAULT nextval('tasks_id_seq') PRIMARY KEY,
    user_id INT NOT NULL,
    project_id INT NOT NULL,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    due_date TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_task_user FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_task_project FOREIGN KEY (project_id) REFERENCES Projects(project_id) ON DELETE CASCADE
);

CREATE TABLE User_Project (
    user_id INT NOT NULL,
    project_id INT NOT NULL,
    PRIMARY KEY (user_id, project_id),
    CONSTRAINT fk_user_project_user FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_user_project_project FOREIGN KEY (project_id) REFERENCES Projects(project_id) ON DELETE CASCADE
);
