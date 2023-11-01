-- Create users table
CREATE TABLE IF NOT EXISTS `users` (
   `user_id` INT NOT NULL AUTO_INCREMENT,
   `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255),
    PRIMARY KEY (`user_id`));

-- Create games table
CREATE TABLE IF NOT EXISTS `games` (
   `game_id` INT NOT NULL AUTO_INCREMENT,
   `game_name` VARCHAR(255) NOT NULL,
    `white_user_id` INT,
    `black_user_id` INT,
    `game_state` TEXT NOT NULL,
    PRIMARY KEY (`game_id`));

-- Create auth_tokens table
CREATE TABLE IF NOT EXISTS `auth_tokens` (
 `token_id` INT NOT NULL AUTO_INCREMENT,
 `auth_token` VARCHAR(255) NOT NULL,
    `user_id` INT NOT NULL,
    `creation_time` DATETIME NOT NULL,
    `expiry_time` DATETIME,
    PRIMARY KEY (`token_id`));
