-- Create users table
CREATE TABLE IF NOT EXISTS `users` (
   `user_id` INT NOT NULL AUTO_INCREMENT,
   `username` VARCHAR(50) NOT NULL,
    `password` VARCHAR(255) NOT NULL,
    `email` VARCHAR(255),
    PRIMARY KEY (`user_id`),
    UNIQUE INDEX `username_UNIQUE` (`username` ASC)
    ) ENGINE=InnoDB;

-- Create games table
CREATE TABLE IF NOT EXISTS `games` (
   `game_id` INT NOT NULL AUTO_INCREMENT,
   `game_name` VARCHAR(255) NOT NULL,
    `white_user_id` INT,
    `black_user_id` INT,
    `game_state` TEXT NOT NULL,
    PRIMARY KEY (`game_id`),
    INDEX `fk_white_user_idx` (`white_user_id` ASC),
    INDEX `fk_black_user_idx` (`black_user_id` ASC),
    CONSTRAINT `fk_white_user`
    FOREIGN KEY (`white_user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION,
    CONSTRAINT `fk_black_user`
    FOREIGN KEY (`black_user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE SET NULL
    ON UPDATE NO ACTION
    ) ENGINE=InnoDB;

-- Create auth_tokens table
CREATE TABLE IF NOT EXISTS `auth_tokens` (
 `token_id` INT NOT NULL AUTO_INCREMENT,
 `auth_token` VARCHAR(255) NOT NULL,
    `user_id` INT NOT NULL,
    `creation_time` DATETIME NOT NULL,
    `expiry_time` DATETIME,
    PRIMARY KEY (`token_id`),
    UNIQUE INDEX `auth_token_UNIQUE` (`auth_token` ASC),
    INDEX `fk_user_token_idx` (`user_id` ASC),
    CONSTRAINT `fk_user_token`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`user_id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
    ) ENGINE=InnoDB;
