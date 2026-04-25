-- Insert Languages
INSERT IGNORE INTO languages (id, name, code) VALUES (1, 'English', 'en');
INSERT IGNORE INTO languages (id, name, code) VALUES (2, 'Spanish', 'es');
INSERT IGNORE INTO languages (id, name, code) VALUES (3, 'French', 'fr');
INSERT IGNORE INTO languages (id, name, code) VALUES (4, 'German', 'de');

-- Insert Admin User
INSERT IGNORE INTO users (id, username, password, role, native_language) VALUES (1, 'admin', 'admin123', 'ADMIN', 'en');
