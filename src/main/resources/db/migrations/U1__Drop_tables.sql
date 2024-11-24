ALTER TABLE User_Project DROP CONSTRAINT fk_user_project_user;
ALTER TABLE User_Project DROP CONSTRAINT fk_user_project_project;
DROP TABLE IF EXISTS User_Project;

ALTER TABLE Tasks DROP CONSTRAINT fk_task_user;
ALTER TABLE Tasks DROP CONSTRAINT fk_task_project;
DROP TABLE IF EXISTS Tasks;

ALTER TABLE Projects DROP CONSTRAINT fk_creator;
DROP TABLE IF EXISTS Projects;

DROP TABLE IF EXISTS Users;

DROP SEQUENCE IF EXISTS tasks_id_seq;
DROP SEQUENCE IF EXISTS projects_id_seq;
DROP SEQUENCE IF EXISTS users_id_seq;
