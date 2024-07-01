CREATE DATABASE IF NOT EXISTS teadb;
-- Create the user and grant privileges
CREATE USER IF NOT EXISTS 'teauser'@'%' IDENTIFIED BY 'teapassword';
GRANT ALL PRIVILEGES ON teadb.* TO 'teauser'@'%';
FLUSH PRIVILEGES;
