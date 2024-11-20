INSERT INTO Users (username, email, password) VALUES
('user1', 'user1@example.com', 'password1'),
('user2', 'user2@example.com', 'password2');

INSERT INTO Projects (creator_id, name, description, start_date, end_date) VALUES
(101, 'SQL Learning Plan', 'Plan for mastering SQL.', '2024-11-15', '2024-11-25'),
(102, 'Task Manager Project', 'Develop a task management application.', '2024-11-10', '2024-12-10');

INSERT INTO User_Project (user_id, project_id) VALUES
(101, 101),
(101, 102),
(102, 102);

INSERT INTO Tasks (user_id, project_id, title, description, due_date) VALUES
(101, 101, 'Learn SQL', 'Study SQL.', '2024-11-18 12:00:00'),
(101, 102, 'Create task', 'Create task', '2024-11-19 18:52:52'),
(102, 102, 'Assign task', 'Assign task', '2024-11-15 09:02:02');
