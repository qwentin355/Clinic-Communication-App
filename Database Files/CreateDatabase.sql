DROP TABLE IF EXISTS appointment;
DROP TABLE IF EXISTS chatMessage;
DROP TABLE IF EXISTS session;
DROP TABLE IF EXISTS user;

CREATE TABLE user (
	username		VARCHAR(10)		PRIMARY KEY,
    userPass		VARCHAR(20)		NOT NULL
);

CREATE TABLE session (
	sessionID		INT				PRIMARY KEY AUTO_INCREMENT,
    username		VARCHAR(10)		NOT NULL,
    FOREIGN KEY (username) REFERENCES user(username)
);

CREATE TABLE appointment (
	appointmentID	INT				PRIMARY KEY AUTO_INCREMENT,
    aptDate			LONG			NOT NULL,
    patientName		VARCHAR(30)		NOT NULL,
    doctorName		VARCHAR(30)		,
    username		VARCHAR(10)		,
    FOREIGN KEY (username) REFERENCES user(username)
);

CREATE TABLE chatMessage (
	messageID		INT				PRIMARY KEY AUTO_INCREMENT,
    messageContent	VARCHAR(255)	,
    username		VARCHAR(10)		NOT NULL,
    sentByStaff		BOOL			DEFAULT true,
    timeSent		LONG			NOT NULL,
    FOREIGN KEY (username) REFERENCES user(username)
);

CREATE TABLE waitingroom (
	pKey INT PRIMARY KEY AUTO_INCREMENT,
	base64string   MEDIUMBLOB   NOT NULL
);