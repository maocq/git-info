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
    readme_url VARCHAR(250) NOT NULL,

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
    CONSTRAINT project_id_fk FOREIGN KEY (project_id) REFERENCES projects (id)
);


# --- !Downs

DROP TABLE commits;
DROP TABLE projects;