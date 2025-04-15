INSERT INTO session(municipality_id, session_id, party_id, customer_nbr, created, initialized, last_accessed, status)
VALUES ('2281', 'a6602aba-0b21-4abf-a869-60c583570129', '52663047-49cd-4f1a-b474-beea8e4eb36f', NULL, '2025-01-01 10:00:00', NULL, NULL, NULL), -- Created but has not yet completed its initialization phase
       ('2281', '4dc21d5e-8a70-45fb-b225-367fcd383a2e', 'e7d536c8-86a7-4a65-98ce-e700e65e31d7', '16324', '2025-01-01 11:00:00', '2025-01-01 11:00:30', NULL, 'Successfully initialized'), -- Created and successfully initialized, but not yet accessed
       ('2281', 'b72af369-1764-486d-8d4d-17158318f6dd', 'f6eb1c0c-2919-48f6-b2b6-65a5f636b87b', '24958', '2025-01-01 13:00:00', '2025-01-01 13:00:30', '2025-01-01 13:01:00', 'Successfully initialized'), -- Created and successfully initialized and accessed session 1
       ('2281', '158cfabe-1c3d-433c-b71f-1c909beaa291', '9bb8472c-f528-4ed0-ba12-d5b6548d429e', '11393', '2025-01-01 12:00:00', '2025-01-01 12:00:30', '2025-01-01 12:01:00', 'Successfully initialized'), -- Created and successfully initialized and accessed session 2
       ('2281', '07b9dbbe-d36f-4312-8bd2-a4abec07ee20', '9bb8472c-f528-4ed0-ba12-d5b6548d429e', '11393', '2025-01-01 12:00:00', '2025-01-01 12:00:30', '2025-01-01 12:01:00', 'Successfully initialized'), -- Created and successfully initialized and accessed session 3
       ('2281', '8212c515-6f7a-4e1c-a6b4-a2e265f018ed', 'f6edb8b8-98c4-4427-9495-1713fada3d2d', NULL, '2025-01-01 14:00:00', NULL, NULL, 'Initialization failed');  -- Created but failed in initialization phase

INSERT INTO file(file_id, session_id)
VALUES ('5ef193cd-96a7-4861-a33d-e01528618f2e', '4dc21d5e-8a70-45fb-b225-367fcd383a2e'), -- file 1 connected to created, initialized but not yet accessed session
       ('2f60ca4c-828b-4f4e-818f-432d53d61f83', '4dc21d5e-8a70-45fb-b225-367fcd383a2e'), -- file 2 connected to created, initialized but not yet accessed session
       ('f6ad5520-659b-4795-8e42-2451f692aae4', 'b72af369-1764-486d-8d4d-17158318f6dd'), -- file 1 connected to created, initialized and accessed session 1
       ('ccb4eee0-4f6c-44d3-8e23-94e318971614', 'b72af369-1764-486d-8d4d-17158318f6dd'), -- file 2 connected to created, initialized and accessed session 1
       ('811bcd0e-fe12-448e-85c5-2248a4a12e6d', '158cfabe-1c3d-433c-b71f-1c909beaa291'), -- file 1 connected to created, initialized and accessed session 2
       ('fb3a9d99-cbd8-4671-87ab-313f54412cde', '07b9dbbe-d36f-4312-8bd2-a4abec07ee20'); -- file 1 connected to created, initialized and accessed session 3
