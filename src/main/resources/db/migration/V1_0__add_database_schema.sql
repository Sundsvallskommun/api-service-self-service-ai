    -- --------------------------------------------------------------------------------------------------------------------------------------
	-- Shedlock table (this will not be created by schema file - i.e do not remove the section below when replacing the content in this file)
    -- --------------------------------------------------------------------------------------------------------------------------------------
    create table shedlock (
        name varchar(64) not null,
        lock_until timestamp(3) not null default current_timestamp(3) on update current_timestamp(3),
        locked_at timestamp(3) not null default current_timestamp(3),
        locked_by varchar(255) not null,
        primary key (name)
    ) engine=InnoDB;
    -- --------------------------------------------------------------------------------------------------------------------------------------
	-- Shedlock table (this will not be created by schema file - i.e do not remove the section above when replacing the content in this file)
    -- --------------------------------------------------------------------------------------------------------------------------------------

    create table file (
        file_id varchar(255) not null,
        session_id varchar(255),
        primary key (file_id)
    ) engine=InnoDB;

    create table session (
        created datetime(6),
        initialized datetime(6),
        last_accessed datetime(6),
        initiation_status mediumtext,
        municipality_id varchar(255) not null,
        session_id varchar(255) not null,
        primary key (session_id)
    ) engine=InnoDB;

    create index file_id_idx 
       on file (file_id);

    create index session_id_idx 
       on file (session_id);

    alter table if exists file 
       add constraint uq_session_id_file_id unique (session_id, file_id);

    create index municipality_id_idx 
       on session (municipality_id);

    create index session_id_municipality_id_idx 
       on session (session_id, municipality_id);

    alter table if exists file 
       add constraint fk_session_file 
       foreign key (session_id) 
       references session (session_id);
