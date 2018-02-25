create table serverconfiguration (
    id                  integer primary key,

    httphostname        varchar(255),
    httpport            integer,
    serverdescription   varchar(255)
);

create table systemuser (
    id          integer primary key,

    name        varchar(255)    not null,
    email       varchar(255)    unique not null,
    password    varchar(16)     not null,
    phone       varchar(16),
    address     varchar(255)
);

create table hibernate_sequences (
    sequence_name          varchar(32) primary key
);

-- Insira as configurações iniciais do servidor ($HOSTNAME, $PORT, $DESCRIPTION)
insert into serverconfiguration values (1, '127.0.0.1', 8080, 'DECORA TEST');

-- Insire usuario root para teste
insert into systemuser values (1, 'Root', 'root@decora.com', 'c2VuaGE=', '99999999', 'Root Street');