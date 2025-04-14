    create table history (
        created datetime(6),
        customer_nbr varchar(255),
        party_id varchar(255),
        session_id varchar(255) not null,
        lime_history longtext,
        primary key (session_id)
    ) engine=InnoDB;

    create index session_id_idx 
       on history (session_id);

    create index party_id_idx 
       on history (party_id);

