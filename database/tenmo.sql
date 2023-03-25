BEGIN TRANSACTION;

DROP TABLE IF EXISTS transfer, account, tenmo_user, transfer_type, transfer_status;
DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id, seq_transfer_id;


CREATE TABLE transfer_type (
	transfer_type_id serial NOT NULL,
	transfer_type_desc varchar(10) NOT NULL,
	CONSTRAINT PK_transfer_type PRIMARY KEY (transfer_type_id)
);

CREATE TABLE transfer_status (
	transfer_status_id serial NOT NULL,
	transfer_status_desc varchar(10) NOT NULL,
	CONSTRAINT PK_transfer_status PRIMARY KEY (transfer_status_id)
);

CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) UNIQUE NOT NULL,
	password_hash varchar(200) NOT NULL,
	role varchar(20),
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
);

CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance decimal(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE SEQUENCE seq_transfer_id
  INCREMENT BY 1
  START WITH 3001
  NO MAXVALUE;

CREATE TABLE transfer (
	transfer_id int NOT NULL DEFAULT nextval('seq_transfer_id'),
	transfer_type_id int NOT NULL,
	transfer_status_id int NOT NULL,
	account_from int NOT NULL,
	account_to int NOT NULL,
	amount decimal(13, 2) NOT NULL,
	CONSTRAINT PK_transfer PRIMARY KEY (transfer_id),
	CONSTRAINT FK_transfer_account_from FOREIGN KEY (account_from) REFERENCES account (account_id),
	CONSTRAINT FK_transfer_account_to FOREIGN KEY (account_to) REFERENCES account (account_id),
	CONSTRAINT FK_transfer_transfer_status FOREIGN KEY (transfer_status_id) REFERENCES transfer_status (transfer_status_id),
	CONSTRAINT FK_transfer_transfer_type FOREIGN KEY (transfer_type_id) REFERENCES transfer_type (transfer_type_id),
	CONSTRAINT CK_transfer_not_same_account CHECK (account_from <> account_to),
	CONSTRAINT CK_transfer_amount_gt_0 CHECK (amount > 0)
);

INSERT INTO transfer_status (transfer_status_desc) VALUES ('Pending');
INSERT INTO transfer_status (transfer_status_desc) VALUES ('Approved');
INSERT INTO transfer_status (transfer_status_desc) VALUES ('Rejected');

INSERT INTO transfer_type (transfer_type_desc) VALUES ('Request');
INSERT INTO transfer_type (transfer_type_desc) VALUES ('Send');

COMMIT;

SELECT * FROM tenmo_user;

UPDATE tenmo_user SET role = 'ADMIN' WHERE username = 'user5';

SELECT balance FROM account WHERE user_id = 1001;

SELECT * FROM account;

SELECT * FROM transfer;

Start Transaction;
UPDATE account SET balance = (balance - 50) WHERE account_id = 2001;
UPDATE account SET balance = (balance + 50) WHERE account_id = 2002;
INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (2, 2, 2001, 2002, 50);
COMMIT;

Start Transaction;
UPDATE account SET balance = (balance - 1000) WHERE account_id = 2003;
UPDATE account SET balance = (balance + 1000) WHERE account_id = 2002;
INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (2, 2, 2003, 2002, 1000);
COMMIT;

Start Transaction;
UPDATE account SET balance = (balance - 1000) WHERE account_id = 2003;
UPDATE account SET balance = (balance + 1000) WHERE account_id = 2002;
INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (2, 2, 2003, 2002, 1000);
COMMIT;