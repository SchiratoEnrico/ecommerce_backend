
    alter table anagrafiche 
       drop constraint FKppcbv6im3teug10ygcq2nw6h4;

    alter table carrelli 
       drop constraint FK8bwmqjwt5v85bcicyrb2cp03y;

    alter table manga 
       drop constraint FKk0v2gv11etiuocmnsp8rkm1lb;

    alter table manga_autori 
       drop constraint FK8sm8r52ml93185a8y5aqcusyg;

    alter table manga_autori 
       drop constraint FKbrfhd0saru9t2rikafxn8y2lh;

    alter table manga_generi 
       drop constraint FK6nu1inap7k0aerp8gaq7aojh6;

    alter table manga_generi 
       drop constraint FKlwtko0bh46dyuukj5g5d6f27l;

    alter table ordini 
       drop constraint FKeahnhannuwy5mebvhxy8lqe8b;

    alter table ordini 
       drop constraint FKo9jvgplgfwdyw1w66gxj8i3iq;

    alter table ordini 
       drop constraint FK6j0iarcfjdeljghsl5hu41ywk;

    alter table ordini 
       drop constraint FKf7s1k810ss61tb8h9yan3kiax;

    alter table righe_carrello 
       drop constraint FKbkny6hvx39y1qpv8knmm4p1m6;

    alter table righe_carrello 
       drop constraint FK647fphayoh41pfui4qmym6mnv;

    alter table righe_ordine 
       drop constraint FKqylepa0isnmlaqhnny012eydn;

    alter table righe_ordine 
       drop constraint FK2ybnsnkyw4w9tfkxeojq1lr6h;

    drop table accounts;

    drop table anagrafiche;

    drop table autori;

    drop table carrelli;

    drop table case_editrici;

    drop table generi;

    drop table manga;

    drop table manga_autori;

    drop table manga_generi;

    drop table ordini;

    drop table righe_carrello;

    drop table righe_ordine;

    drop table stati_ordine;

    drop table system_messages;

    drop table tipi_pagamento;

    drop table tipi_spedizione;
