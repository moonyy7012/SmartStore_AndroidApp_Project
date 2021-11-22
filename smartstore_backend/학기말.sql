use ssafy_mobile_cafe;

CREATE TABLE IF NOT EXISTS `t_coupon` (
	`id` int NOT NULL auto_increment,
    `name` varchar(50),
    `type` varchar(20),
    PRIMARY KEY(`id`)
  )
ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS `t_user_coupon` (
	`id` int NOT NULL auto_increment,
    `user_id` varchar(100),
    `coupon_id` int,
    `publish_time` timestamp default current_timestamp,
    `validate` date,
    `is_used` varchar(20) default "not used",
    `use_time` datetime,
    primary key(`id`),
    FOREIGN KEY(`user_id`)
    REFERENCES `t_user`(`id`),
    FOREIGN KEY(`coupon_id`)
    REFERENCES `t_coupon`(`id`)
)
ENGINE = InnoDB;

insert into t_coupon(name, type) values("10% 할인 쿠폰", "DISCOUNT 10");
insert into t_coupon(name, type) values("15% 할인 쿠폰", "DISCOUNT 15");

insert into t_user_coupon(user_id, coupon_id, validate, is_used) values("id 01", 1, "2022-01-24", "not used");
insert into t_user_coupon(user_id, coupon_id, validate, is_used) values("id 01", 2, "2022-01-24", "used");
insert into t_user_coupon(user_id, coupon_id, validate, is_used) values("id 02", 2, "2022-01-24", "not used");
insert into t_user_coupon(user_id, coupon_id, validate, is_used) values("id 01", 2, "2022-01-24", "not used");

desc t_user_coupon;