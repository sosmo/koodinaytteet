drop table salasanat;

create table salasanat (
	tunnus varchar(100) not null,
	salasana varchar(250) not null,

	primary key (tunnus));


insert into salasanat (tunnus, salasana)
values ('tiea218@foo.example', 'e995798a0fa5bacd7f6a3d7e28a6d9f9a93ac0ed');
