PRAGMA foreign_keys=OFF;
DROP TABLE IF EXISTS ingridient;
DROP TABLE IF EXISTS cookie;
DROP TABLE IF EXISTS reciepeItem;
DROP TABLE IF EXISTS pallet;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS myOrder;
DROP TABLE IF EXISTS orderItem;
DROP TABLE IF EXISTS palletItem;
PRAGMA foreign_keys=ON;

CREATE TABLE ingridient(
	ingridientName varchar2(128) PRIMARY KEY,
	amountInStorage int,
	deliveryDate date,
	deliveryAmount int
);

CREATE TABLE cookie(
	cookieName varchar2(128) PRIMARY KEY
);

CREATE TABLE reciepeItem(
	cookieName varchar2(128),
	amount int,
	ingridientName varchar2(128),
	FOREIGN KEY (cookieName) REFERENCES cookie(cookieName),
	FOREIGN KEY (ingridientName) REFERENCES ingridient(ingridientName),
	PRIMARY KEY (cookieName,ingridientName)
);

CREATE TABLE pallet(
	palletId integer PRIMARY KEY AUTOINCREMENT,
	dateProduced date,
	isBlocked int,
	dateDelivered date,
	cookieName varchar2(128),
	location varchar2(64),
	FOREIGN KEY (cookieName) REFERENCES cookie(cookieName)
);

CREATE TABLE palletItem(
	palletId int,
	orderNbr,
	FOREIGN KEY (orderNbr) REFERENCES myOrder(orderNbr),
	FOREIGN KEY (palletId) REFERENCES pallet(palletId),
	PRIMARY KEY (palletId,orderNbr)
);

CREATE TABLE customer(
	customerName varchar2(128) PRIMARY KEY,
	address varchar2(128)
);

CREATE TABLE myOrder(
	orderNbr integer PRIMARY KEY AUTOINCREMENT,
	placedDate date,
	deliveryDate date,
	customerName varchar2(128),
	FOREIGN KEY (customerName) REFERENCES customer(customerName)
);

CREATE TABLE orderItem(
	orderNbr int,
	cookieName varchar2(128),
	nbrPallets int,
	FOREIGN KEY (orderNbr) REFERENCES myOrder(orderNbr),
	FOREIGN KEY (cookieName) REFERENCES cookie(cookieName),
	PRIMARY KEY (cookieName,orderNbr)
);
