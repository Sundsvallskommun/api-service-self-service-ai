    -- The session id is no longer the id of the session in Eneo. The Eneo session is started lazily by the first
    -- question, and its id is stored here. Sessions created before this change were created in Eneo up front with the
    -- same id, so all of them are backfilled.
    alter table session
       add column eneo_session_id varchar(255);

    update session
       set eneo_session_id = session_id
     where eneo_session_id is null;
