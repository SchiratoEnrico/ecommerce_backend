
insert into system_messages (code, messaggio) values ('null_usr', 'Username assente');
insert into system_messages (code, messaggio) values ('null_pdw', 'Password assente');
insert into system_messages (code, messaggio) values ('null_ema', 'Email assente');
insert into system_messages (code, messaggio) values ('null_ruo', 'Ruolo assente');
insert into system_messages (code, messaggio) values ('null_acc', 'Account assente');
insert into system_messages (code, messaggio) values ('null_nom', 'Nome assente');
insert into system_messages (code, messaggio) values ('null_cog', 'Cognome assente');
insert into system_messages (code, messaggio) values ('null_sta', 'Stato assente');
insert into system_messages (code, messaggio) values ('null_cit', 'Citta assente');
insert into system_messages (code, messaggio) values ('null_pro', 'Provincia assente');
insert into system_messages (code, messaggio) values ('null_cap', 'Cap assente');
insert into system_messages (code, messaggio) values ('null_via', 'Via assente');
insert into system_messages (code, messaggio) values ('null_ana', 'Anagrafica assente');
insert into system_messages (code, messaggio) values ('null_pag', 'Tipo pagamento assente');
insert into system_messages (code, messaggio) values ('null_pre', 'Predefinito assente');
insert into system_messages (code, messaggio) values ('!exists_acc', 'Account non esistente');
insert into system_messages (code, messaggio) values ('!exists_ana', 'Informazione anagrafica non esistente');
insert into system_messages (code, messaggio) values ('!exists_pag', 'Tipo pagamento non esistente');
insert into system_messages (code, messaggio) values ('exists_usr', 'Username già presente');
insert into system_messages (code, messaggio) values ('exists_ema', 'Email già presente');
insert into system_messages (code, messaggio) values ('rest_created', 'Elemento creato con successo');
insert into system_messages (code, messaggio) values ('rest_deleted', 'Elemento eliminato con successo');
insert into system_messages (code, messaggio) values ('rest_updated', 'Elemento aggiornato con successo');
insert into system_messages (code, messaggio) values ('pwd_short', 'Password troppo corta');
insert into system_messages (code, messaggio) values ('pwd_upper', 'Password non contiene caratteri maiuscoli');
insert into system_messages (code, messaggio) values ('pwd_lower', 'Password non contiene caratteri minuscoli');
insert into system_messages (code, messaggio) values ('pwd_digit', 'Password non contiene caratteri numerici');
insert into system_messages (code, messaggio) values ('pwd_special', 'Password non contiene caratteri speciali');

-- =========================
-- BASE TABLES (NO FK)
-- =========================

insert into accounts (username, password, email, ruolo) values ('MarioRossi', 'Password1!', 'mario.rossi@email.com', 'USER');
insert into accounts (username, password, email, ruolo) values ('AdminUser', 'Password1!', 'admin@email.com', 'ADMIN');

INSERT INTO stati_ordine (stato_ordine)
VALUES ('CREATED');

INSERT INTO tipi_pagamento (tipo_pagamento)
VALUES ('PAYPAL');

INSERT INTO tipi_spedizione (tipo_spedizione)
VALUES ('STANDARD');

INSERT INTO generi (descrizione)
VALUES ('AZIONE'),
       ('SHONEN');

INSERT INTO case_editrici (indirizzo, nome, descrizione, email)
VALUES 
('Tokyo', 'Shueisha', 'Famous publisher', 'info@shueisha.jp'),
('Milano', 'JPOP', 'Italian publisher', 'info@jpop.it');

INSERT INTO autori (nome, cognome, descrizione, data_nascita)
VALUES 
('Akira', 'Toriyama', 'Dragon Ball author', '1955-04-05'),
('Eiichiro', 'Oda', 'One Piece author', '1975-01-01');


-- =========================
-- MANGA + RELATIONS
-- =========================

INSERT INTO manga (isbn, titolo, data_pubblicazione, id_casa_editrice, numero_copie, prezzo, immagine)
VALUES 
('ISBN001', 'One Piece Vol.1', '1997-07-22', 1, 100, 9.99, 'img1.jpg'),
('ISBN002', 'Dragon Ball Vol.1', '1984-12-03', 1, 50, 7.99, 'img2.jpg');

INSERT INTO manga_autori (id_autore, isbn_manga)
VALUES 
(2, 'ISBN001'),
(1, 'ISBN002');

INSERT INTO manga_generi (id_genere, isbn_manga)
VALUES 
(2, 'ISBN001'),
(1, 'ISBN002');


-- =========================
-- ACCOUNT RELATED
-- =========================

INSERT INTO carrelli (id_account)
VALUES (1);

INSERT INTO anagrafiche (id_account, predefinito, nome, cognome,
    via, citta, provincia, cap, stato
)
VALUES (
    1, true, 'Mario', 'Rossi',
    'Via Roma 1', 'Roma', 'RM', '00100', 'Italia'
);


-- =========================
-- CART ITEMS
-- =========================

INSERT INTO righe_carrello (id_carrello, isbn_manga, numero_copie)
VALUES 
(1, 'ISBN001', 2);

-- =========================
-- ORDERS
-- =========================

INSERT INTO ordini (
    data, id_account, id_stato, id_tipo_pagamento, id_tipo_spedizione
)
VALUES (
    CURRENT_DATE, 1, 1, 1, 1
);

INSERT INTO righe_ordine (
    id_ordine, isbn_manga, numero_copie, prezzo
)
VALUES 
(1, 'ISBN001', 2, 9.99);
