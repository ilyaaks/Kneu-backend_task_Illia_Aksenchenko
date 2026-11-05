CREATE TABLE medications (
    id     BIGSERIAL    PRIMARY KEY,
    name   VARCHAR(255) NOT NULL,
    dosage VARCHAR(255) NOT NULL
);

INSERT INTO medications (id, name, dosage) VALUES
    (1, 'Aspirin',     '100mg'),
    (2, 'Metformin',   '500mg'),
    (3, 'Lisinopril',  '10mg'),
    (4, 'Amoxicillin', '250mg'),
    (5, 'Omeprazole',  '20mg');

ALTER SEQUENCE medications_id_seq RESTART WITH 6;
