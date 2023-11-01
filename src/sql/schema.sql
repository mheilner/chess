-- Create users table
DROP TABLE IF EXISTS `users`;
CREATE TABLE IF NOT EXISTS `users` (
   `user_id` INT NOT NULL AUTO_INCREMENT,
   `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255),
    PRIMARY KEY (`user_id`));

-- Drop the games table if it exists
DROP TABLE IF EXISTS `games`;
-- Create games table
CREATE TABLE IF NOT EXISTS `games` (
   `game_id` VARCHAR(255) NOT NULL,
   `game_name` VARCHAR(255) NOT NULL,
    `white_username` INT,
    `black_username` INT,
    `game_state` TEXT,
    PRIMARY KEY (`game_id`));


-- Create auth_tokens table
DROP TABLE IF EXISTS `auth_tokens`;
CREATE TABLE IF NOT EXISTS `auth_tokens` (
 `token_id` INT NOT NULL AUTO_INCREMENT,
 `auth_token` VARCHAR(255),
`username` VARCHAR(255),
    PRIMARY KEY (`token_id`));
