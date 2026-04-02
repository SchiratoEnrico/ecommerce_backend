-- =========================
-- SYSTEM MESSAGES
-- =========================

-- null / missing field errors
INSERT INTO system_messages VALUES ('null_req',     'Richiesta non valida');
INSERT INTO system_messages VALUES ('null_usr',     'Username assente');
INSERT INTO system_messages VALUES ('null_pwd',     'Password assente');
INSERT INTO system_messages VALUES ('null_ema',     'Email assente');
INSERT INTO system_messages VALUES ('null_ruo',     'Ruolo assente');
INSERT INTO system_messages VALUES ('null_acc',     'Account assente');
INSERT INTO system_messages VALUES ('null_nom',     'Nome assente');
INSERT INTO system_messages VALUES ('null_cog',     'Cognome assente');
INSERT INTO system_messages VALUES ('null_cgn',     'Cognome assente');
INSERT INTO system_messages VALUES ('null_dsc',     'Descrizione assente');
INSERT INTO system_messages VALUES ('null_dtn',     'Data di nascita assente');
INSERT INTO system_messages VALUES ('null_sta',     'Stato assente');
INSERT INTO system_messages VALUES ('null_cit',     'Città assente');
INSERT INTO system_messages VALUES ('null_pro',     'Provincia assente');
INSERT INTO system_messages VALUES ('null_cap',     'CAP assente');
INSERT INTO system_messages VALUES ('null_via',     'Via assente');
INSERT INTO system_messages VALUES ('null_ind',     'Indirizzo assente');
INSERT INTO system_messages VALUES ('null_ana',     'Anagrafica assente');
INSERT INTO system_messages VALUES ('null_pre',     'Campo predefinito assente');
INSERT INTO system_messages VALUES ('null_pag',     'Tipo pagamento assente');
INSERT INTO system_messages VALUES ('null_spe',     'Tipo spedizione assente');
INSERT INTO system_messages VALUES ('null_ord',     'Ordine assente');
INSERT INTO system_messages VALUES ('null_dat',     'Data assente');
INSERT INTO system_messages VALUES ('null_man',     'Manga assente');
INSERT INTO system_messages VALUES ('null_aut',     'Autore assente');
INSERT INTO system_messages VALUES ('null_gen',     'Genere assente');
INSERT INTO system_messages VALUES ('null_ced',     'Casa editrice assente');
INSERT INTO system_messages VALUES ('null_isb',     'ISBN assente');
INSERT INTO system_messages VALUES ('null_tit',     'Titolo assente');
INSERT INTO system_messages VALUES ('null_ncp',     'Numero copie assente');
INSERT INTO system_messages VALUES ('null_dtp',     'Data pubblicazione assente');
INSERT INTO system_messages VALUES ('null_sag',     'Saga assente');
INSERT INTO system_messages VALUES ('null_snom',    'Nome saga assente');
INSERT INTO system_messages VALUES ('null_sagvol',  'Volume di saga assente');
INSERT INTO system_messages VALUES ('null_desc',    'Descrizione assente');
INSERT INTO system_messages VALUES ('null_des',     'Descrizione assente');
INSERT INTO system_messages VALUES ('null_fat',     'Fattura assente');
INSERT INTO system_messages VALUES ('null_rig',     'Riga fattura assente');
INSERT INTO system_messages VALUES ('null_isn',     'ISBN riga fattura assente');
INSERT INTO system_messages VALUES ('null_num_fat', 'Numero fattura assente');
INSERT INTO system_messages VALUES ('null_rig_fat', 'Righe fattura assenti');
INSERT INTO system_messages VALUES ('null_qua',     'Quantità assente');
INSERT INTO system_messages VALUES ('null_crq',     'RigaCarrelloRequest non può essere null');
INSERT INTO system_messages VALUES ('null_cri',     'Id carrello non può essere null');

-- not found errors
INSERT INTO system_messages VALUES ('!exists_acc',     'Account non trovato');
INSERT INTO system_messages VALUES ('!exists_ana',     'Anagrafica non trovata');
INSERT INTO system_messages VALUES ('!exists_aut',     'Autore non trovato');
INSERT INTO system_messages VALUES ('!exists_gen',     'Genere non trovato');
INSERT INTO system_messages VALUES ('!exists_ced',     'Casa editrice non trovata');
INSERT INTO system_messages VALUES ('!exists_man',     'Manga non trovato');
INSERT INTO system_messages VALUES ('!exists_sag',     'Saga non trovata');
INSERT INTO system_messages VALUES ('!exists_ord',     'Ordine non trovato');
INSERT INTO system_messages VALUES ('!exists_sta',     'Stato ordine non trovato');
INSERT INTO system_messages VALUES ('!exists_pag',     'Tipo pagamento non trovato');
INSERT INTO system_messages VALUES ('!exists_spe',     'Tipo spedizione non trovato');
INSERT INTO system_messages VALUES ('!exists_fat',     'Fattura non trovata');
INSERT INTO system_messages VALUES ('!exists_rig_fat', 'Riga fattura non trovata');
INSERT INTO system_messages VALUES ('!exists_rig',     'Riga non trovata');
INSERT INTO system_messages VALUES ('!exists_row',     'Riga ordine non trovata');
INSERT INTO system_messages VALUES ('!exists_car',     'Carrello non trovato');
INSERT INTO system_messages VALUES ('!exists_rcr',     'Riga carrello non trovata');
INSERT INTO system_messages VALUES ('!exists_rcar',    'Riga carrello non trovata');
INSERT INTO system_messages VALUES ('!exists_casa',    'Casa editrice non trovata');
INSERT INTO system_messages VALUES ('!exists_up',      'File upload vuoto');
INSERT INTO system_messages VALUES ('carrello_ntfnd',  'Carrello non trovato');

-- already exists errors
INSERT INTO system_messages VALUES ('exists_usr',     'Username già presente');
INSERT INTO system_messages VALUES ('exists_ema',     'Email già presente');
INSERT INTO system_messages VALUES ('exists_aut',     'Autore già presente');
INSERT INTO system_messages VALUES ('exists_gen',     'Genere già presente');
INSERT INTO system_messages VALUES ('exists_ced',     'Casa editrice già presente');
INSERT INTO system_messages VALUES ('exists_casa',    'Casa editrice già presente');
INSERT INTO system_messages VALUES ('exists_man',     'Manga già presente (ISBN duplicato)');
INSERT INTO system_messages VALUES ('exists_sag',     'Saga già presente');
INSERT INTO system_messages VALUES ('exists_sagman',  'Impossibile eliminare: saga ha manga allegati');
INSERT INTO system_messages VALUES ('exists_sagvol',  'Volume già esistente per questa saga');
INSERT INTO system_messages VALUES ('exists_sta',     'Stato ordine già presente');
INSERT INTO system_messages VALUES ('exists_pag',     'Tipo pagamento già presente');
INSERT INTO system_messages VALUES ('exists_spe',     'Tipo spedizione già presente');

-- linked / constraint errors
INSERT INTO system_messages VALUES ('linked_ord',    'Manga collegato a ordini: eliminazione bloccata');
INSERT INTO system_messages VALUES ('linked_car',    'Manga collegato a carrelli: eliminazione bloccata');
INSERT INTO system_messages VALUES ('linked_man',    'Elemento collegato a manga: eliminazione bloccata');
INSERT INTO system_messages VALUES ('order_sta',     'Stato ordine in uso: eliminazione bloccata');
INSERT INTO system_messages VALUES ('order_pag',     'Tipo pagamento in uso: eliminazione bloccata');
INSERT INTO system_messages VALUES ('order_spe',     'Tipo spedizione in uso: eliminazione bloccata');
INSERT INTO system_messages VALUES ('casa_man',      'Casa editrice ha manga allegati: eliminazione bloccata');
INSERT INTO system_messages VALUES ('wrong_acc_ana', 'Anagrafica non appartiene a questo account');
INSERT INTO system_messages VALUES ('id_chng',       'Impossibile cambiare il carrello di una riga');
INSERT INTO system_messages VALUES ('id_required',   'ID obbligatorio per aggiornamento');
INSERT INTO system_messages VALUES ('last_adm',      'Impossibile eliminare l ultimo amministratore');
INSERT INTO system_messages VALUES ('invalid_path',  'Percorso file non valido');

-- auth errors
INSERT INTO system_messages VALUES ('!valid_log',   'Credenziali non valide');
INSERT INTO system_messages VALUES ('!valid_rol',   'Ruolo non valido');

-- password errors
INSERT INTO system_messages VALUES ('pwd_short',   'Password troppo corta (minimo 6 caratteri)');
INSERT INTO system_messages VALUES ('pwd_upper',   'Password deve contenere almeno una lettera maiuscola');
INSERT INTO system_messages VALUES ('pwd_lower',   'Password deve contenere almeno una lettera minuscola');
INSERT INTO system_messages VALUES ('pwd_digit',   'Password deve contenere almeno un numero');
INSERT INTO system_messages VALUES ('pwd_special', 'Password deve contenere almeno un carattere speciale');

-- upload errors
INSERT INTO system_messages VALUES ('upload_inv',  'Tipo file non valido: solo immagini accettate');
INSERT INTO system_messages VALUES ('upsave_err',  'Errore salvataggio file immagine');
INSERT INTO system_messages VALUES ('udir_err',    'Errore creazione directory upload');
INSERT INTO system_messages VALUES ('null_idxs',   'Specificare isbn o id per associare l immagine');

-- generic REST responses
INSERT INTO system_messages VALUES ('rest_created', 'Elemento creato con successo');
INSERT INTO system_messages VALUES ('rest_updated', 'Elemento aggiornato con successo');
INSERT INTO system_messages VALUES ('rest_deleted', 'Elemento eliminato con successo');

-- =========================
-- LOOKUP TABLES
-- =========================

INSERT INTO stati_ordine (stato_ordine) VALUES ('CREATED');
INSERT INTO stati_ordine (stato_ordine) VALUES ('CONFERMATO');
INSERT INTO stati_ordine (stato_ordine) VALUES ('SPEDITO');
INSERT INTO stati_ordine (stato_ordine) VALUES ('CONSEGNATO');
INSERT INTO stati_ordine (stato_ordine) VALUES ('ANNULLATO');

INSERT INTO tipi_pagamento (tipo_pagamento) VALUES ('PAYPAL');
INSERT INTO tipi_pagamento (tipo_pagamento) VALUES ('CARTA_DI_CREDITO');
INSERT INTO tipi_pagamento (tipo_pagamento) VALUES ('BONIFICO');

INSERT INTO tipi_spedizione (tipo_spedizione) VALUES ('STANDARD');
INSERT INTO tipi_spedizione (tipo_spedizione) VALUES ('EXPRESS');

INSERT INTO generi (descrizione) VALUES ('AZIONE');
INSERT INTO generi (descrizione) VALUES ('SHONEN');
INSERT INTO generi (descrizione) VALUES ('SEINEN');
INSERT INTO generi (descrizione) VALUES ('AVVENTURA');

INSERT INTO case_editrici (nome, descrizione, indirizzo, email)
VALUES ('Shueisha', 'Major Japanese publisher', 'Tokyo, Japan', 'info@shueisha.jp');

INSERT INTO case_editrici (nome, descrizione, indirizzo, email)
VALUES ('JPOP', 'Italian manga publisher', 'Via Roma 1, Milano', 'info@jpop.it');

INSERT INTO autori (nome, cognome, data_nascita, descrizione)
VALUES ('Akira', 'Toriyama', '1955-04-05', 'Dragon Ball author');

INSERT INTO autori (nome, cognome, data_nascita, descrizione)
VALUES ('Eiichiro', 'Oda', '1975-01-01', 'One Piece author');

-- =========================
-- ACCOUNTS
-- =========================

INSERT INTO accounts (username, password, email, data_creazione, ruolo)
VALUES ('MarioRossi', 'Password1!', 'mario.rossi@email.com', CURRENT_TIMESTAMP, 'USER');

INSERT INTO accounts (username, password, email, data_creazione, ruolo)
VALUES ('AdminUser', 'Password1!', 'admin@email.com', CURRENT_TIMESTAMP, 'ADMIN');

INSERT INTO anagrafiche (id_account, predefinito, nome, cognome, via, citta, provincia, cap, stato)
VALUES (1, TRUE, 'Mario', 'Rossi', 'Via Roma 1', 'Roma', 'RM', '00100', 'Italia');

INSERT INTO carrelli (id_account) VALUES (1);

-- =========================
-- SAGA + MANGA
-- =========================

INSERT INTO saghe (nome, descrizione, immagine, proxy)
VALUES ('One Piece', 'Saga sui pirati', NULL, FALSE);

INSERT INTO saghe (nome, descrizione, immagine, proxy)
VALUES ('Dragon Ball', 'Saga sui guerrieri alieni', NULL, FALSE);

INSERT INTO manga (isbn, titolo, data_pubblicazione, id_casa_editrice, numero_copie, prezzo, immagine, id_saga, saga_volume)
VALUES ('ISBN001', 'One Piece Vol.1', '1997-07-22', 1, 100, 9.99, NULL, 1, 1);

INSERT INTO manga (isbn, titolo, data_pubblicazione, id_casa_editrice, numero_copie, prezzo, immagine, id_saga, saga_volume)
VALUES ('ISBN002', 'Dragon Ball Vol.1', '1984-12-03', 1, 50, 7.99, NULL, 2, 1);

INSERT INTO manga_autori (id_autore, isbn_manga) VALUES (2, 'ISBN001');
INSERT INTO manga_autori (id_autore, isbn_manga) VALUES (1, 'ISBN002');

INSERT INTO manga_generi (id_genere, isbn_manga) VALUES (2, 'ISBN001');
INSERT INTO manga_generi (id_genere, isbn_manga) VALUES (1, 'ISBN002');

-- =========================
-- CART
-- =========================

INSERT INTO righe_carrello (id_carrello, isbn_manga, numero_copie) VALUES (1, 'ISBN001', 2);
INSERT INTO righe_carrello (id_carrello, isbn_manga, numero_copie) VALUES (1, 'ISBN002', 1);

-- =========================
-- ORDERS
-- =========================

INSERT INTO ordini (data, id_account, id_stato, id_tipo_pagamento, id_tipo_spedizione, id_anagrafica)
VALUES (CURRENT_DATE, 1, 1, 1, 1, 1);

INSERT INTO righe_ordine (id_ordine, isbn_manga, numero_copie, prezzo) VALUES (1, 'ISBN001', 2, 9.99);
INSERT INTO righe_ordine (id_ordine, isbn_manga, numero_copie, prezzo) VALUES (1, 'ISBN002', 1, 7.99);

-- =========================
-- INVOICES
-- =========================

INSERT INTO fatture (
    numero_fattura, data_emissione, totale, costo_spedizione,
    tipo_pagamento, tipo_spedizione,
    cliente_nome, cliente_cognome, cliente_email,
    cliente_indirizzo, cliente_citta, cliente_provincia, cliente_cap, cliente_stato,
    stato, stato_reclamo, id_ordine
) VALUES (
    'FAT-2026-001', CURRENT_DATE, 25.97, 5.00,
    'PAYPAL', 'STANDARD',
    'Mario', 'Rossi', 'mario.rossi@email.com',
    'Via Roma 1', 'Roma', 'RM', '00100', 'Italia',
    'ATTIVA', NULL, 1
);

INSERT INTO righe_fattura (id_fattura, titolo, autore, isbn, prezzo_unitario, quantita, totale_riga)
VALUES (1, 'One Piece Vol.1', 'Eiichiro Oda', 'ISBN001', 9.99, 2, 19.98);

INSERT INTO righe_fattura (id_fattura, titolo, autore, isbn, prezzo_unitario, quantita, totale_riga)
VALUES (1, 'Dragon Ball Vol.1', 'Akira Toriyama', 'ISBN002', 7.99, 1, 7.99);