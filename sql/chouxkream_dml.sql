-- #############################################
-- ## Chouxkream DML
-- #############################################


select count(*) from product p where category_no is not null;
select product_title , category_no  from product p where category_no = 22;

select * from category c ;

insert into category(category_no, category_name) values('1', '스니커즈');
insert into category(category_no, category_name) values('37', '샌들/슬리퍼');
insert into category(category_no, category_name) values('70', '플랫');
insert into category(category_no, category_name) values('69', '로퍼');
insert into category(category_no, category_name) values('55', '더비/레이스업');
insert into category(category_no, category_name) values('62', '힐/펌프스');
insert into category(category_no, category_name) values('35', '부츠');
insert into category(category_no, category_name) values('71', '기타 신발');


insert into category(category_no, category_name) values('22', '자켓');
insert into category(category_no, category_name) values('72', '아노락');
insert into category(category_no, category_name) values('21', '코트');
insert into category(category_no, category_name) values('20', '패딩');
insert into category(category_no, category_name) values('73', '기타 아우터');


insert into category(category_no, category_name) values('27', '반팔 티셔츠');
insert into category(category_no, category_name) values('26', '긴팔 티셔츠');
insert into category(category_no, category_name) values('75', '가디건');
insert into category(category_no, category_name) values('28', '셔츠');
insert into category(category_no, category_name) values('23', '후드');
insert into category(category_no, category_name) values('74', '후드 집업');
insert into category(category_no, category_name) values('24', '스웨트셔츠');
insert into category(category_no, category_name) values('76', '슬리브리스');
insert into category(category_no, category_name) values('77', '원피스');
insert into category(category_no, category_name) values('25', '니트');
insert into category(category_no, category_name) values('78', '기타 상의');


insert into category(category_no, category_name) values('29', '바지');
insert into category(category_no, category_name) values('30', '반바지');
insert into category(category_no, category_name) values('50', '스커트');
insert into category(category_no, category_name) values('79', '레깅스');
insert into category(category_no, category_name) values('80', '기타 하의');



