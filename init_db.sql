-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema Gym
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema Gym
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `Gym` DEFAULT CHARACTER SET utf8 ;
USE `Gym` ;

-- -----------------------------------------------------
-- Table `Gym`.`members`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Gym`.`members` (
  `member_id` INT NOT NULL AUTO_INCREMENT,
  `first_name` VARCHAR(50) NOT NULL,
  `last_name` VARCHAR(50) NOT NULL,
  `birth_date` DATETIME NOT NULL,
  `phone` VARCHAR(45) NOT NULL,
  `email` VARCHAR(255) NOT NULL,
  `registration_date` DATETIME NOT NULL,
  PRIMARY KEY (`member_id`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `Gym`.`memberships`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Gym`.`memberships` (
  `membership_id` INT NOT NULL AUTO_INCREMENT,
  `price` DECIMAL(7,2) NOT NULL,
  `period` ENUM('MONTHLY', 'QUARTERLY', 'ANNUAL') NOT NULL,
  `start_date` DATETIME NOT NULL,
  `end_date` DATETIME NOT NULL,
  `member_id` INT NOT NULL,
  PRIMARY KEY (`membership_id`),
  INDEX `fk_membership_members_idx` (`member_id` ASC) VISIBLE,
  CONSTRAINT `fk_memberships_members`
    FOREIGN KEY (`member_id`)
    REFERENCES `Gym`.`members` (`member_id`)
    ON DELETE NO ACTION
    ON UPDATE CASCADE)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
