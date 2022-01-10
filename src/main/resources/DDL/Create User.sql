# For MySQL || MariaDB

CREATE TABLE `user`
(
    `id`          bigint(20) NOT NULL AUTO_INCREMENT,
    `money`       bigint(20)   DEFAULT NULL,
    `name`        varchar(255) DEFAULT NULL,
    `delete_res`  datetime     DEFAULT NULL,
    `delete_date` datetime     DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1
  DEFAULT CHARSET = utf8;
