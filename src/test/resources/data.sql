
INSERT INTO system_messages (code, messaggio) VALUES('rest_deleted', 'Elemento eliminato con successo');
INSERT INTO system_messages (code, messaggio) VALUES('rest_updated', 'Elemento aggiornato con successo');
INSERT INTO system_messages (code, messaggio) VALUES('rest_created', 'Elemento creato con successo');
INSERT INTO system_messages (code, messaggio) VALUES('!exists_sta', 'Stato d ordine non esistente');
INSERT INTO system_messages (code, messaggio) VALUES('null_sta', 'Stato d ordine assente');
INSERT INTO system_messages (code, messaggio) VALUES('exists_sta', 'Stato d ordine già esistente');
INSERT INTO system_messages (code, messaggio) VALUES('!exists_row', 'Riga d ordine non esistente');
INSERT INTO system_messages (code, messaggio) VALUES('null_row', 'Riga d ordine assente');
INSERT INTO system_messages (code, messaggio) VALUES('exists_row', 'Riga d ordine già esistente');
INSERT INTO system_messages (code, messaggio) VALUES('null_ord', 'Riga d ordine assente');
INSERT INTO system_messages (code, messaggio) VALUES('!exists_ord', 'Riga d ordine già esistente');
INSERT INTO system_messages (code, messaggio) VALUES('!valid_tar', 'Targa non valida');


-- =========================
-- BASE TABLES (NO FK)
-- =========================

INSERT INTO accounts (email, username, ruolo)
VALUES ('user@test.com', 'user1', 'USER');

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
