
    set client_min_messages = WARNING;

    alter table if exists anagrafiche 
       drop constraint if exists FKppcbv6im3teug10ygcq2nw6h4;

    alter table if exists carrelli 
       drop constraint if exists FK8bwmqjwt5v85bcicyrb2cp03y;

    alter table if exists fatture 
       drop constraint if exists FK1yam3fnrr4nuexay4p8i9e928;

    alter table if exists manga 
       drop constraint if exists FKk0v2gv11etiuocmnsp8rkm1lb;

    alter table if exists manga 
       drop constraint if exists FKpj9g3xx3pcil600o1gpwwsh4h;

    alter table if exists manga_autori 
       drop constraint if exists FK8sm8r52ml93185a8y5aqcusyg;

    alter table if exists manga_autori 
       drop constraint if exists FKbrfhd0saru9t2rikafxn8y2lh;

    alter table if exists manga_generi 
       drop constraint if exists FK6nu1inap7k0aerp8gaq7aojh6;

    alter table if exists manga_generi 
       drop constraint if exists FKlwtko0bh46dyuukj5g5d6f27l;

    alter table if exists ordini 
       drop constraint if exists FKeahnhannuwy5mebvhxy8lqe8b;

    alter table if exists ordini 
       drop constraint if exists FKnvr4b33empa4luxi45jqindcn;

    alter table if exists ordini 
       drop constraint if exists FKo9jvgplgfwdyw1w66gxj8i3iq;

    alter table if exists ordini 
       drop constraint if exists FK6j0iarcfjdeljghsl5hu41ywk;

    alter table if exists ordini 
       drop constraint if exists FKf7s1k810ss61tb8h9yan3kiax;

    alter table if exists righe_carrello 
       drop constraint if exists FKbkny6hvx39y1qpv8knmm4p1m6;

    alter table if exists righe_carrello 
       drop constraint if exists FK647fphayoh41pfui4qmym6mnv;

    alter table if exists righe_fattura 
       drop constraint if exists FKsnxaxq3vxgxbf9tb0bp7ucocv;

    alter table if exists righe_ordine 
       drop constraint if exists FKqylepa0isnmlaqhnny012eydn;

    alter table if exists righe_ordine 
       drop constraint if exists FK2ybnsnkyw4w9tfkxeojq1lr6h;

    drop table if exists accounts cascade;

    drop table if exists anagrafiche cascade;

    drop table if exists autori cascade;

    drop table if exists carrelli cascade;

    drop table if exists case_editrici cascade;

    drop table if exists fatture cascade;

    drop table if exists generi cascade;

    drop table if exists manga cascade;

    drop table if exists manga_autori cascade;

    drop table if exists manga_generi cascade;

    drop table if exists ordini cascade;

    drop table if exists righe_carrello cascade;

    drop table if exists righe_fattura cascade;

    drop table if exists righe_ordine cascade;

    drop table if exists saghe cascade;

    drop table if exists stati_ordine cascade;

    drop table if exists system_messages cascade;

    drop table if exists tipi_pagamento cascade;

    drop table if exists tipi_spedizione cascade;
