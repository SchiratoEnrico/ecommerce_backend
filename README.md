Per il database (siamo in postgres) utilizzaimo questa convenzione:

si utilizza snake_case
tutto minuscolo
nomi tabelle plurale
nome colonne singolare

per le classi Java invece snakeCase - > nomeAttributo 

I dati sono salvati nel database tutti in uppercase

Messaggi di errore memorizzati in tabella nel database. Nelle varie implementations lanciamo eccezioni con codice dell'errore. Nei controller recuperiamo il messaggio effettivo. Quindi solo nei controller avremo il service dei messaggi iniettato.

Nomencaltura codici messaggi errore:

rest_created    Elemento creato con successo
rest_deleted    Elemento eliminato con successo
rest_updated    Elemento aggiornato con successo
!exists_bic    Bicicletta non esistente
null_vei    Tipo veicolo assente
exists_sos    Tipo sospensione già esistente

Nei dto input utilizziamo stringhe e classi wrapper. Evitiamo di avere LocalDAte o robe strane. Negli output invece usiamo formati più corretti.

Al momento occupiamoci di create, update, listAll, findById, delete. Filtri ci occuperemo poi

  RICORDA DI RICONTROLLARE RELAZIONI (no OneToOne su ID)
