# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table artist (
  id                        integer auto_increment not null,
  name                      varchar(255),
  constraint pk_artist primary key (id))
;

create table order_request (
  id                        integer auto_increment not null,
  member_id                 varchar(255),
  constraint pk_order_request primary key (id))
;

create table stock (
  id                        integer auto_increment not null,
  variation_id              integer,
  seat_id                   varchar(255),
  order_id                  integer,
  updated_at                datetime not null,
  constraint uq_stock_1 unique (variation_id,seat_id),
  constraint pk_stock primary key (id))
;

create table ticket (
  id                        integer auto_increment not null,
  name                      varchar(255),
  artist_id                 integer,
  constraint pk_ticket primary key (id))
;

create table variation (
  id                        integer auto_increment not null,
  name                      varchar(255),
  ticket_id                 integer,
  constraint pk_variation primary key (id))
;




# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table artist;

drop table order_request;

drop table stock;

drop table ticket;

drop table variation;

SET FOREIGN_KEY_CHECKS=1;

