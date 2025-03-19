
    create table file (
        file_id varchar(255) not null,
        session_id varchar(255),
        primary key (file_id)
    ) engine=InnoDB;

    create table session (
        created datetime(6),
        initialized datetime(6),
        last_accessed datetime(6),
        customer_nbr varchar(255),
        municipality_id varchar(255) not null,
        party_id varchar(255) not null,
        session_id varchar(255) not null,
        status mediumtext,
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
