- 임시 데이터베이스 생성
create database test3;


-- use test3;
-- 임시 데이터베이스 삭제
-- drop database test3;
-- 테이블 생성

-- ###############################################################################
-- DROP TABLE
-- ###############################################################################
-- DROP TABLE IF EXISTS `user`;


-- ###############################################################################
-- TABLE 생성
-- ###############################################################################

create TABLE IF NOT EXISTS `user` (
	`user_no` bigint auto_increment not null,
	`email` varchar(50) not null unique,
	`password` varchar(255) not null,
	`username` varchar(10) null,
	`nickname` varchar(50) null unique,
	`user_desc` varchar(255) null,
	`phone_number` varchar(20) not null,
	`point` int not null default 0,
	`create_date` timestamp not null default current_timestamp,
	`update_date` timestamp not null default current_timestamp on
update
	current_timestamp,
	`is_active` bit not null default 0,
	primary key(user_no)
) engine = innodb;

create TABLE IF NOT EXISTS `role` (
	`role_id` char(5) not null,
	`role_name` varchar(20) not null,
   primary key(role_id)
) engine = innodb;

create TABLE IF NOT EXISTS `user_role` (
	`user_no` bigint not null,
	`role_id` char(5) not null,
   primary key(user_no,
role_id)
) engine = innodb;

create TABLE IF NOT EXISTS `address` (
	`address_no` bigint auto_increment not null,
	`user_no` bigint not null,
	`receiver_name` varchar(10) not null,
	`receiver_phone` varchar(20) not null,
	`zipcode` char(5) not null,
	`address` varchar(100) not null,
	`detail_address` varchar(100) null,
	`default_address` bit not null default 0,
    primary key(address_no)
) engine = innodb;

create TABLE IF NOT EXISTS `coupon` (
	`coupon_no` bigint auto_increment not null,
	`coupon_name` varchar(100) not null,
	`coupon_desc` varchar(255) not null,
	`coupon_type` enum('amount',
'percent') not null,
	`discount_value` int not null,
	`create_date` timestamp not null default current_timestamp,
	`available_days` int not null default 0,
    primary key(coupon_no)
) engine = innodb;

create TABLE IF NOT EXISTS `user_coupon` (
	`user_no` bigint not null,
	`coupon_no` bigint not null,
	`issue_date` timestamp not null default current_timestamp,
	`expire_date` timestamp not null,
	`is_active` bit not null default 0,
    primary key(user_no,
coupon_no)
) engine = innodb;

create TABLE IF NOT EXISTS `wishlist` (
	`wishlist_no` bigint auto_increment not null,
	`user_no` bigint not null,
	`product_size_no` bigint not null,
	`create_date` timestamp not null default current_timestamp,
	primary key(wishlist_no)
) engine = innodb;

create TABLE IF NOT EXISTS `product` (
	`product_no` bigint auto_increment not null,
	`product_title` varchar(200) not null,
	`product_sub_title` varchar(200) not null,
	`model_id` varchar(50),
	`brand_id` varchar(100),
	`product_color` varchar(100) null,
	`release_price` bigint,
	`release_date` date,
	`create_date` datetime not null default current_timestamp,
	`update_date` datetime not null default current_timestamp on
update
	current_timestamp,
	`views_count` bigint not null default 0,
	`meta_keyword` varchar(1000) null,
	`is_active` bit not null default 0,
	primary key(product_no)
) engine = innodb;

create TABLE IF NOT EXISTS `product_size` (
	`product_no` bigint not null,
	`size_name` varchar(10) not null,
	`stock` bigint not null default 0,
	`sell_count` bigint not null default 0,
    primary key(`product_no`,
`size_name`)
) engine = innodb;

create TABLE IF NOT EXISTS `brand` (
	`brand_id` varchar(100) not null,
	`brand_name` varchar(100) not null,
	`brand_sub_name` varchar(100) not null,
	`brand_logo_url` varchar(2000) null,
	`is_active` bit not null default 0,
	`create_date` datetime not null default current_timestamp,
	`update_date` datetime not null default current_timestamp on
update
	current_timestamp,
	primary key(`brand_id`)
) engine = innodb;

create TABLE IF NOT EXISTS `category` (
	`category_no` bigint auto_increment not null,
	`category_name` varchar(100) not null,
	`create_date` datetime not null default current_timestamp,
	`update_date` datetime not null default current_timestamp on
update
	current_timestamp,
	primary key(category_no)
) engine = innodb;


create TABLE IF NOT EXISTS `product_images` (
	`image_no` bigint auto_increment not null,
	`product_no` bigint not null,
	`image_url` varchar(2000) null,
	`create_date` datetime not null default current_timestamp,
	`is_active` bit not null default 0,
   primary key(image_no)
) engine = innodb;

create TABLE IF NOT EXISTS `bid` (
	`bid_no` bigint auto_increment not null,
	`user_no` bigint not null,
	`product_size_no` bigint not null,
    `product_no` bigint not null,
	`bid_type` enum('sell',
'buy') not null,
	`bid_price` int not null,
	`create_date` timestamp not null default current_timestamp,
	`bid_status` enum('bid_progress',
'bid_cancel',
'bid_complete') not null default 'bid_progress',
    primary key(bid_no)
) engine = innodb;

create TABLE IF NOT EXISTS `payment` (
	`payment_no` bigint auto_increment not null,
	`bid_no` bigint not null,
	`payment_method` enum('mobile_payment',
'card_payment',
'deposit_payment') not null,
	`payment_price` int not null,
	`payment_date` timestamp not null,
	`payment_status` enum('payment_progress',
'payment_cancel',
'payment_complete') not null default 'payment_progress',
    primary key(payment_no)
) engine = innodb;


-- ###############################################################################
-- 제약조건 추가
-- ###############################################################################

alter table `user_role` add constraint `FK_user_TO_user_role_1` foreign key (`user_no`)
references `user` (`user_no`) on
delete
	cascade;

alter table `user_role` add constraint `FK_role_TO_user_role_1` foreign key (`role_id`)
references `role` (`role_id`);

alter table `user_coupon` add constraint `FK_user_TO_user_coupon_1` foreign key (`user_no`)
references `user` (`user_no`) on
delete
	cascade;

alter table `user_coupon` add constraint `FK_coupon_TO_user_coupon_1` foreign key (`coupon_no`)
references `coupon` (`coupon_no`) on
delete
	cascade;

alter table `product` add constraint `FK_product_TO_brand_1` foreign key (`brand_id`)
references `brand` (`brand_id`) on
delete
	set
	null;

alter table `product_category` add constraint `FK_product_TO_category_1` foreign key (`product_no`)
references `product` (`product_no`) on
delete
	cascade;

alter table `product_category` add constraint `FK_product_TO_category_2` foreign key (`category_no`)
references `category` (`category_no`) on
delete
	cascade;

alter table `product_images` add constraint `FK_product_TO_images_2` foreign key (`product_no`)
references `product` (`product_no`) on
delete
	cascade;