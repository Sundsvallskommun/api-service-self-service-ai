INSERT INTO session(municipality_id, session_id, created, initialized, last_accessed, initiation_status)
VALUES ('2281', 'a6602aba-0b21-4abf-a869-60c583570129', '2025-01-01 10:00:00', NULL, NULL, NULL), -- Created but has not yet completed its initialization phase
       ('2281', '4dc21d5e-8a70-45fb-b225-367fcd383a2e', '2025-01-01 11:00:00', '2025-01-01 11:00:30', NULL, 'Successfully initialized'), -- Created and successfully initialized, but not yet accessed
       ('2281', 'b72af369-1764-486d-8d4d-17158318f6dd', '2025-01-01 13:00:00', '2025-01-01 13:00:30', '2025-01-01 13:01:00', 'Successfully initialized'), -- Created and successfully initialized and accessed
       ('2281', '158cfabe-1c3d-433c-b71f-1c909beaa291', '2025-01-01 12:00:00', '2025-01-01 12:00:30', '2025-01-01 12:01:00', 'Successfully initialized'), -- Created and successfully initialized and accessed
       ('2281', '8212c515-6f7a-4e1c-a6b4-a2e265f018ed', '2025-01-01 14:00:00', NULL, NULL, 'Initialization failed');  -- Created but failed in initialization phase

INSERT INTO file(file_id, session_id)
VALUES ('5ef193cd-96a7-4861-a33d-e01528618f2e', '4dc21d5e-8a70-45fb-b225-367fcd383a2e'), -- file 1 connected to created, initialized but not yet accessed session
       ('2f60ca4c-828b-4f4e-818f-432d53d61f83', '4dc21d5e-8a70-45fb-b225-367fcd383a2e'), -- file 2 connected to created, initialized but not yet accessed session
       ('f6ad5520-659b-4795-8e42-2451f692aae4', 'b72af369-1764-486d-8d4d-17158318f6dd'), -- file 1 connected to created, initialized and accessed session 1
       ('ccb4eee0-4f6c-44d3-8e23-94e318971614', 'b72af369-1764-486d-8d4d-17158318f6dd'), -- file 2 connected to created, initialized and accessed session 1
       ('811bcd0e-fe12-448e-85c5-2248a4a12e6d', '158cfabe-1c3d-433c-b71f-1c909beaa291'); -- file 1 connected to created, initialized and accessed session 2
