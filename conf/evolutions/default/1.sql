# --- !Ups

CREATE TABLE projects
(
    id INTEGER NOT NULL,
    description VARCHAR(500) NOT NULL,
    name VARCHAR(200) NOT NULL,
    name_with_namespace VARCHAR(250) NOT NULL,
    path VARCHAR(200) NOT NULL,
    path_with_namespace VARCHAR(250) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    default_branch VARCHAR(200) NOT NULL,
    ssh_url_to_repo VARCHAR(250) NOT NULL,
    http_url_to_repo VARCHAR(250) NOT NULL,
    web_url VARCHAR(250) NOT NULL,
    updating BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT project_pk PRIMARY KEY (id)
);

CREATE TABLE commits
(
    id VARCHAR(100) NOT NULL,
    short_id VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    parent_ids VARCHAR(250) NOT NULL,
    title VARCHAR(250) NOT NULL,
    message VARCHAR(500) NOT NULL,
    author_name VARCHAR(250) NOT NULL,
    author_email VARCHAR(250) NOT NULL,
    authored_date TIMESTAMP WITH TIME ZONE NOT NULL,
    committer_name VARCHAR(250) NOT NULL,
    committer_email VARCHAR(250) NOT NULL,
    committed_date TIMESTAMP WITH TIME ZONE NOT NULL,
    project_id INTEGER NOT NULL,

    CONSTRAINT commit_pk PRIMARY KEY (id),
    CONSTRAINT commit_project_id_fk FOREIGN KEY (project_id) REFERENCES projects (id)
);

CREATE TABLE diffs
(
    id SERIAL,
    old_path VARCHAR(250) NOT NULL,
    new_path VARCHAR(250) NOT NULL,
    a_mode VARCHAR(100) NOT NULL,
    b_mode VARCHAR(100) NOT NULL,
    new_file BOOLEAN NOT NULL,
    renamed_file BOOLEAN NOT NULL,
    deleted_file BOOLEAN NOT NULL,
    diff TEXT NOT NULL,
    additions INTEGER NOT NULL,
    deletions INTEGER NOT NULL,
    commit_id VARCHAR(100) NOT NULL,

    CONSTRAINT diff_pk PRIMARY KEY (id),
    CONSTRAINT diff_commit_id_fk FOREIGN KEY (commit_id) REFERENCES commits (id)
);

CREATE TABLE users
(
    id INTEGER NOT NULL,
    name VARCHAR(200) NOT NULL,
    username VARCHAR(200) NOT NULL,
    avatar_url VARCHAR(250) NOT NULL,
    web_url VARCHAR(250) NOT NULL,

    CONSTRAINT user_pk PRIMARY KEY (id)
)


# --- !Downs

DROP TABLE diffs;
DROP TABLE commits;
DROP TABLE projects;
DROP TABLE users;
