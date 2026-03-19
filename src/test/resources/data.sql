INSERT INTO accounts
(email, username, ruolo)
values('EMAIL1', 'TEST1', 'ADMIN');

INSERT INTO accounts
(email, username, ruolo)
values('EMAIL2', 'TEST2', 'ADMIN');

INSERT INTO accounts
(email, username, ruolo)
values('EMAIL3', 'TEST3', 'ADMIN');

INSERT INTO accounts
(email, username, ruolo)
values('TROLLMAIL', 'TROLL', 'ADMIN');

INSERT INTO carrelli
(id_account)
values(2);

INSERT INTO case_editrici(indirizzo, nome, descrizione, email)
values
('Indirizzo1', 'Nome1', 'Descrizione1', 'Email1'),
('Indirizzo2', 'Casa', 'Descrizione2', 'Email2');

INSERT INTO spedizioni(tipo_spedizione)
values
('Aereo'),
('Nave'),
('Treno'),
('Camion');