INSERT INTO user (username, userPass) VALUES 
	('admin', 'admin'),
    ('gideon', 'white'),
    ('jace', 'blue'),
    ('liliana', 'black'),
    ('chandra', 'red'),
    ('nissa', 'green')
;

INSERT INTO user (username, userPass) VALUES 
	('123456789', 'password'),
    ('111111111', 'password'),
    ('000000000', 'password'),
    ('101010101', 'password'),
    ('987654321', 'password')
;

INSERT INTO appointment (aptDate, patientName, doctorName, username) VALUES
	(1607623200, 'Anuj Patel', 'Dr. Who', '123456789'),
    (1607709600, 'Jigar Patel', 'Dr. Mario', '111111111'),
    (1607796000, 'Keith Purves', 'Dr. Brown', '000000000'),
    (1607882400, 'Kohl Henrikson', 'Dr. Strange', '101010101')
;

INSERT INTO appointment (aptDate, patientName, username) VALUES
    (1607968800, 'Shubh Negi', '987654321')
;

INSERT INTO appointment (aptDate, patientName) VALUES
    (1607968800, 'Saurabh Maroo'),
    (1608055200, 'Tyler Mathisen'),
    (1608055200, 'Yash Patel')
;

INSERT INTO session (username) VALUES
		('admin'),
        ('liliana'),
        ('123456789')
;

INSERT INTO chatMessage (messageContent, username, sentByStaff, timeSent) VALUES
	('Staff Message 1', '123456789', true, 1607623200),
    ('Staff Message 2', '111111111', true, 1607623200),
    ('Staff Message 3', '000000000', true, 1607623200),
    ('Staff Message 4', '101010101', true, 1607623200),
    ('Staff Message 5', '101010101', true, 1607623200),
    ('User Message 1', '123456789', false, 1607623200),
    ('User Message 2', '111111111', false, 1607623200),
    ('User Message 3', '000000000', false, 1607623200),
    ('User Message 4', '000000000', false, 1607623200),
    ('User Message 5', '101010101', false, 1607623200)
;