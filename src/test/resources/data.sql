INSERT INTO tipi_freno
(id, tipo_freno)
VALUES(1, 'TAMBURO');

INSERT INTO tipi_sospensione
(id, tipo_sospensione)
VALUES(1, 'MOLLA');

INSERT INTO categorie
(id, categoria)
VALUES(1, 'UTILITARIA');

INSERT INTO colori
(id, colore)
VALUES(1, 'BLU');

INSERT INTO marche
(id, marca)
VALUES(1, 'FIAT');

INSERT INTO tipi_alimentazione
(id, tipo_alimentazione)
VALUES(1, 'MANUALE');

INSERT INTO tipi_veicolo
(id, tipo_veicolo)
VALUES(1, 'MACCHINA');

INSERT INTO system_messages (code, messaggio)
VALUES('rest_created', 'Elemento creato con successo');

INSERT INTO system_messages (code, messaggio)
VALUES('rest_deleted', 'Elemento eliminato con successo');

INSERT INTO system_messages (code, messaggio)
VALUES('rest_updated', 'Elemento aggiornato con successo');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_bic', 'Bicicletta non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_cat', 'Categoria non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_col', 'Colore non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_mac', 'Macchina non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_mar', 'Marca non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_mot', 'Moto non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_ali', 'Tipo alimentazione non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_fre', 'Tipo freno non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_sos', 'Tipo sospensione non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_vei', 'Tipo veicolo non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_nma', 'Numero marce assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_fre', 'Tipo freno assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_sos', 'Tipo sospensione assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_pie', 'Modalità piega assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_cat', 'Categoria assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_col', 'Colore assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_ccc', 'Cilindrata assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_por', 'Numero porte assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_tar', 'Targa assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_mar', 'Marca assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_ali', 'Tipo alimentazione assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_vei', 'Tipo veicolo assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_ann', 'Anno produzione assente');

INSERT INTO system_messages (code, messaggio)
VALUES('null_ruo', 'Numero di ruote assente');

INSERT INTO system_messages (code, messaggio)
VALUES('exists_cat', 'Categoria già esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('exists_col', 'Colore già esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('exists_tar', 'Targa già esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('exists_mar', 'Marca già esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('exists_ali', 'Tipo alimentazione già esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('exists_fre', 'Tipo freno già esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('exists_sos', 'Tipo sospensione già esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('exists_vei', 'Tipo veicolo già esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_veh', 'Veicolo non esistente');

INSERT INTO system_messages (code, messaggio)
VALUES('!exists_fil', 'Filtro incompatibile');

INSERT INTO system_messages (code, messaggio)
VALUES('!valid_tar', 'Targa non valida');
