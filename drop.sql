
    alter table anagrafiche 
       drop 
       foreign key FKoc2ya8cyewupshbwv2fuwqutx;

    alter table carrelli 
       drop 
       foreign key FK8bwmqjwt5v85bcicyrb2cp03y;

    alter table carrelli_manga 
       drop 
       foreign key FKkhw77gip7pc9w9cu7chrk621q;

    alter table carrelli_manga 
       drop 
       foreign key FK5rb9g7l8o4on0ublb7pwu4r8p;

    alter table manga 
       drop 
       foreign key FKk0v2gv11etiuocmnsp8rkm1lb;

    alter table manga_autori 
       drop 
       foreign key FK8sm8r52ml93185a8y5aqcusyg;

    alter table manga_autori 
       drop 
       foreign key FKbrfhd0saru9t2rikafxn8y2lh;

    alter table manga_generi 
       drop 
       foreign key FK6nu1inap7k0aerp8gaq7aojh6;

    alter table manga_generi 
       drop 
       foreign key FKlwtko0bh46dyuukj5g5d6f27l;

    alter table ordini 
       drop 
       foreign key FKeahnhannuwy5mebvhxy8lqe8b;

    alter table ordini 
       drop 
       foreign key FK76mc7hr7sthilhjy1rerhshmg;

    alter table ordini 
       drop 
       foreign key FKgp5h6gfg8c52s54gtsoyc2894;

    alter table ordini 
       drop 
       foreign key FKo9jvgplgfwdyw1w66gxj8i3iq;

    alter table righeordine 
       drop 
       foreign key FKqofnxqjgpy7und7xfak25es7v;

    alter table righeordine 
       drop 
       foreign key FKa30cbtoatkcx6verd9yoyguse;

    drop table if exists accounts;

    drop table if exists anagrafiche;

    drop table if exists autori;

    drop table if exists carrelli;

    drop table if exists carrelli_manga;

    drop table if exists case_editrici;

    drop table if exists generi;

    drop table if exists manga;

    drop table if exists manga_autori;

    drop table if exists manga_generi;

    drop table if exists ordini;

    drop table if exists pagamenti;

    drop table if exists righeordine;

    drop table if exists spedizioni;

    drop table if exists stati_ordine;
