drop table if exists job;

drop table if exists job_execution;

create table `job`(
    id varchar(36) not null,
    payload text default null,
    status varchar(20) not null default 'PENDING',
    scheduled_time datetime(6) not null,
    retry_count integer default 0,
    max_retries integer default 3,
    version integer default 0,
    primary key(id)
) engine=InnoDB;

create table `job_execution`(
    id varchar(36) not null,
    job_id varchar(36) default null,
    status varchar(36) default null,
    error_message text default null,
    started_at datetime(6) default null,
    finished_at datetime(6) default null,
    primary key(id),
    constraint fk_job_execution_job foreign key(job_id) references job(id)
) engine=InnoDB;

create index idx_job_execution_job_id on job_execution(job_id);