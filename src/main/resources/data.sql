CREATE TABLE customers(id INT auto_increment NOT NULL, 
								lastname VARCHAR(100), 
                                firstname VARCHAR(100),
                                company VARCHAR(200),
                                mail VARCHAR(255),
                                phone VARCHAR(15),
                                mobile VARCHAR(15),
                                notes VARCHAR(255),
                                active BOOLEAN,
                                PRIMARY KEY (id));
                                    
CREATE TABLE orders(id INT auto_increment NOT NULL,
									customer_id INT,
                                    label VARCHAR(100),
                                    adr_et DECIMAL,
                                    number_of_days DECIMAL,
                                    tva DECIMAL,
                                    status VARCHAR(30),
                                    type VARCHAR(100),
                                    notes VARCHAR(255),
                                    PRIMARY KEY (id),
                                    FOREIGN KEY (customer_id) REFERENCES customers(id));                                    

CREATE TABLE users (id INT auto_increment NOT NULL,
									   username VARCHAR(30),
                                       password VARCHAR(255),
                                       mail VARCHAR(255),
                                       PRIMARY KEY(id));

INSERT INTO customers (id, lastname, firstname, company, mail, mobile, notes, active) VALUES (1, 'GILBERT', 'Marc', 'Wijin', 'marc.gilbert@wijin.tech', '0666666666', 'Mes notes', true);

INSERT INTO customers (id, lastname, firstname, company, mail, mobile, notes, active) VALUES (2, 'KENOBI', 'Obi-Wan', 'Jedis', 'obiwan.kenobi@jedis.com', '0666666666', 'Les notes d''Obi Wan', true);

INSERT INTO customers (id, lastname, firstname, company, mail, mobile, notes, active) VALUES (3, 'MCCLANE', 'John', 'NYPD', 'john.mcclane@nypd.com', '0666666666', 'Les notes de John', false);

INSERT INTO customers (id, lastname, firstname, company, mail, mobile, notes, active) VALUES (4, 'MCFLY', 'Marty', 'DOC', 'marty.mcfly@doc.com', NULL, 'Les notes de Marty', false);

INSERT INTO orders (customer_id, label, adr_et, number_of_days, tva, status, type, notes) VALUES (1, 'Formation Java', 450.0, 5, 20, 'En cours', 'Forfait', 'Test');

INSERT INTO orders (customer_id, label, adr_et, number_of_days, tva, status, type, notes) VALUES (1, 'Formation Spring', 450.0, 3, 20.0, 'En attente', 'Forfait', 'Test');

INSERT INTO orders (customer_id, label, adr_et, number_of_days, tva, status, type, notes) VALUES (2, 'Formation Jedi', 1500.0, 2, 20.0, 'Payée', 'Forfait', 'Notes sur la formation');

INSERT INTO users (username, password, mail) VALUES ('mgilbert', '1234', 'marc.gilbert@wijin.tech');