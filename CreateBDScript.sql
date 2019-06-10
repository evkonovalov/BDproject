CREATE TABLE public.client (
	clientID SERIAL NOT NULL,
	"name" text UNIQUE,
	address text NULL,
	checkindate date NULL,
	fine float4 NULL,
	CONSTRAINT clientPK PRIMARY KEY (clientID)
);

CREATE TABLE public.library (
	libraryID SERIAL NOT NULL,
	"name" text UNIQUE,
	address text NULL,
	workinghoursbegin time NULL,
	workinghoursend time NULL,
	CONSTRAINT libraryPK PRIMARY KEY (libraryID)
);

CREATE TABLE public.genre (
	genreID SERIAL NOT NULL,
	"name" text UNIQUE,
	CONSTRAINT genrePK PRIMARY KEY (genreID)
);

CREATE TABLE public.author (
	authorID SERIAL NOT NULL,
	"name" text UNIQUE,
	century int4 null,
	CONSTRAINT authorPK PRIMARY KEY (authorID)
);

CREATE TABLE public.book (
	bookID SERIAL NOT NULL,
	"name" text NULL,
	ISBN text NULL,
	quantity int4 null,
	libraryID int4 NOT NULL,
	CONSTRAINT bookPK PRIMARY KEY (bookID),
	CONSTRAINT libraryFK FOREIGN KEY (libraryID) REFERENCES library(libraryID)
);

CREATE TABLE public.genre_book (
	genreID int4 NOT NULL,
	bookID int4 NOT NULL,
	CONSTRAINT genre_bookPK PRIMARY KEY (genreID, bookID),
	CONSTRAINT bookFK FOREIGN KEY (bookID) REFERENCES book(bookID),
	CONSTRAINT genreFK FOREIGN KEY (genreID) REFERENCES genre(genreID)
);

CREATE TABLE public.author_book (
	authorID int4 NOT NULL,
	bookID int4 NOT NULL,
	CONSTRAINT author_bookPK PRIMARY KEY (authorID, bookID),
	CONSTRAINT authorFK FOREIGN KEY (authorID) REFERENCES author(authorID),
	CONSTRAINT bookFK FOREIGN KEY (bookID) REFERENCES book(bookID)
);

CREATE TABLE public."order" (
	orderID SERIAL NOT NULL,
	datefrom date NULL,
	dateto date NULL,
	active bool NULL,
	clientID int4 NOT NULL,
	bookID int4 NOT NULL,
	libraryID int4 NOT NULL,
	CONSTRAINT orderPK PRIMARY KEY (orderID),
	CONSTRAINT bookFK FOREIGN KEY (bookID) REFERENCES book(bookID),
	CONSTRAINT clientFK FOREIGN KEY (clientID) REFERENCES client(clientID),
	CONSTRAINT libraryFK FOREIGN KEY (libraryID) REFERENCES library(libraryID)
);
