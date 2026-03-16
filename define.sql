
    create table accounts (
        id integer not null auto_increment,
        email varchar(100) not null,
        username varchar(100) not null,
        ruolo varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table anagrafiche (
        id integer not null auto_increment,
        id_anagrafica integer,
        predefinito bit not null,
        cap varchar(100) not null,
        citta varchar(100) not null,
        cognome varchar(100) not null,
        nome varchar(100) not null,
        provincia varchar(100) not null,
        stato varchar(100) not null,
        via varchar(100) not null,
        primary key (id)
    ) engine=InnoDB;

    create table autori (
        data_nascita date not null,
        id integer not null auto_increment,
        cognome varchar(255) not null,
        descrizione varchar(255) not null,
        nome varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table carrelli (
        id integer not null auto_increment,
        id_account integer,
        primary key (id)
    ) engine=InnoDB;

    create table carrelli_manga (
        carrelli_id integer not null,
        manga_isbn varchar(255) not null
    ) engine=InnoDB;

    create table case_editrici (
        id integer not null auto_increment,
        indirizzo varchar(100) not null,
        nome varchar(100) not null,
        descrizione varchar(255) not null,
        email varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table generi (
        id integer not null auto_increment,
        descrizione varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    create table manga (
        data_pubblicazione date not null,
        id_casa_editrice integer not null,
        numero_copie integer not null,
        prezzo float(53) not null,
        immagine varchar(255) not null,
        isbn varchar(255) not null,
        titolo varchar(255) not null,
        primary key (isbn)
    ) engine=InnoDB;

    create table manga_autori (
        id_autore integer not null,
        isbn_manga varchar(255) not null
    ) engine=InnoDB;

    create table manga_generi (
        id_genere integer not null,
        isbn_manga varchar(255) not null
    ) engine=InnoDB;

    create table ordini (
        data date not null,
        id integer not null auto_increment,
        id_account integer,
        id_pagamento integer,
        id_spedizione integer,
        id_stato integer,
        primary key (id)
    ) engine=InnoDB;

    create table pagamenti (
        id integer not null auto_increment,
        tipo_pagamento varchar(100) not null,
        primary key (id)
    ) engine=InnoDB;

    create table righeordine (
        id integer not null auto_increment,
        id_ordine integer not null,
        numero_copie integer not null,
        id_manga varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table spedizioni (
        id integer not null auto_increment,
        tipo_spedizione varchar(100) not null,
        primary key (id)
    ) engine=InnoDB;

    create table stati_ordine (
        id integer not null auto_increment,
        stato_ordine varchar(255) not null,
        primary key (id)
    ) engine=InnoDB;

    alter table carrelli 
       add constraint UKijuiqdqs8gmjmgp5yu7uxgk8y unique (id_account);

    alter table carrelli_manga 
       add constraint UKg5q6rd75ltioct9wqgrla40wc unique (manga_isbn);

    alter table righeordine 
       add constraint UK9agvth9ucknagomtxtya9lngb unique (id_manga);

    alter table anagrafiche 
       add constraint FKoc2ya8cyewupshbwv2fuwqutx 
       foreign key (id_anagrafica) 
       references accounts (id);

    alter table carrelli 
       add constraint FK8bwmqjwt5v85bcicyrb2cp03y 
       foreign key (id_account) 
       references accounts (id);

    alter table carrelli_manga 
       add constraint FKkhw77gip7pc9w9cu7chrk621q 
       foreign key (manga_isbn) 
       references manga (isbn);

    alter table carrelli_manga 
       add constraint FK5rb9g7l8o4on0ublb7pwu4r8p 
       foreign key (carrelli_id) 
       references carrelli (id);

    alter table manga 
       add constraint FKk0v2gv11etiuocmnsp8rkm1lb 
       foreign key (id_casa_editrice) 
       references case_editrici (id);

    alter table manga_autori 
       add constraint FK8sm8r52ml93185a8y5aqcusyg 
       foreign key (id_autore) 
       references autori (id);

    alter table manga_autori 
       add constraint FKbrfhd0saru9t2rikafxn8y2lh 
       foreign key (isbn_manga) 
       references manga (isbn);

    alter table manga_generi 
       add constraint FK6nu1inap7k0aerp8gaq7aojh6 
       foreign key (id_genere) 
       references generi (id);

    alter table manga_generi 
       add constraint FKlwtko0bh46dyuukj5g5d6f27l 
       foreign key (isbn_manga) 
       references manga (isbn);

    alter table ordini 
       add constraint FKeahnhannuwy5mebvhxy8lqe8b 
       foreign key (id_account) 
       references accounts (id);

    alter table ordini 
       add constraint FK76mc7hr7sthilhjy1rerhshmg 
       foreign key (id_pagamento) 
       references pagamenti (id);

    alter table ordini 
       add constraint FKgp5h6gfg8c52s54gtsoyc2894 
       foreign key (id_spedizione) 
       references spedizioni (id);

    alter table ordini 
       add constraint FKo9jvgplgfwdyw1w66gxj8i3iq 
       foreign key (id_stato) 
       references stati_ordine (id);

    alter table righeordine 
       add constraint FKqofnxqjgpy7und7xfak25es7v 
       foreign key (id_manga) 
       references manga (isbn);

    alter table righeordine 
       add constraint FKa30cbtoatkcx6verd9yoyguse 
       foreign key (id_ordine) 
       references ordini (id);
