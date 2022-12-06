create table javalibrary.users (
	user_id varchar(16) not null unique,
    username varchar(50) not null,
    description varchar(500) not null,
    password varchar(16) not null,
    phone varchar(8) not null,
    email varchar(100) not null unique,
    status varchar(7) not null default "offline"
);
insert into javalibrary.users (user_id, username, description, password, phone, email,status)
values ("zxcv1234zxcv1234", "bataa", "", "12345678", "99117700", "bat44512@gmail.com", "offline");

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `createUser`(id varchar(16), username varchar(50), description varchar(500), password varchar(16), phone varchar(8), email varchar(100), status  varchar(7))
BEGIN
	insert into javalibrary.users (user_id, username, description, password, phone, email,status)
	values
		(id, username, description, password, phone , email, status);
END //
DELIMITER ;

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUsers`()
BEGIN
	select user_id, username, description, phone , email, status from javalibrary.users;
END //
DELIMITER ;

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `getUserById`(id varchar(16))
BEGIN
	select user_id, username, description, phone , email, status from javalibrary.users where user_id = id;
END //
DELIMITER ;

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `loginUserByEmail`(e varchar(100))
BEGIN
	select * from javalibrary.users where email = e limit 1;
END //
DELIMITER ;

call `javalibrary`.`createUser`("1234zsdlk32334", "ganaa", "d", "12345678", "99117700", "ganaa@gmail.com", "offline");
call javalibrary.getUsers();
call javalibrary.getUserById("1234zsdlk32333");
call javalibrary.loginUserByEmail("bat44512@gmail.com");

-- ///// 
-- ///// Message
-- /////
create table javalibrary.Message (
    message text not null,
    content text,
    seenDate datetime,
    sendDate datetime not null,
    from_user_id varchar(16) not null,
    to_user_id varchar(16) not null
);
insert into javalibrary.Message (message, content, seenDate, sendDate, from_user_id, to_user_id)
values("EZ", "", "2020-01-01 10:10:10", "2020-01-01 10:10:20", "zxcv1234zxcv1234", "1234zsdlk32333"),
	("Zzo", "", "2020-01-01 10:10:31", "2020-01-01 10:40:20", "zxcv1234zxcv1234", "1234zsdlk32333"),
	("Twn do", "", "2020-01-01 10:10:30", "2020-01-01 10:20:20", "1234zsdlk32333", "zxcv1234zxcv1234"),
    ("Hi", "", "2020-01-01 10:10:10", "2020-01-01 10:10:20", "zxcv1234zxcv1234", "1234zsdlk32334");

select * from javalibrary.Message;

select * from javalibrary.Message 
where from_user_id = "zxcv1234zxcv1234" and to_user_id = "1234zsdlk32333" 
union
select * from javalibrary.Message
where from_user_id = "1234zsdlk32333" and to_user_id = "zxcv1234zxcv1234" 
order by sendDate asc 

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `FromToMessage`(from_user varchar(16), to_user varchar(16))
BEGIN
	select * from javalibrary.Message 
	where from_user_id = from_user and to_user_id = to_user
	union
	select * from javalibrary.Message
	where from_user_id = to_user and to_user_id = from_user
	order by sendDate asc limit 50;
END //
DELIMITER ;

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `sendMessage`(message1 text, content1 text, seenDate1 datetime, sendDate1 datetime, from_user_id1 varchar(16), to_user_id1 varchar(16))
BEGIN
	insert into javalibrary.Message (message, content, seenDate, sendDate, from_user_id, to_user_id)
	values(message1, content1, seenDate1, sendDate1, from_user_id1, to_user_id1);
END //
DELIMITER ;

call javalibrary.FromToMessage("zxcv1234zxcv1234", "1234zsdlk32333");
call javalibrary.sendMessage("Enter your text here..", "", "2021-01-01 10:10:30", "2021-01-01 10:20:20", "1234zsdlk32333", "zxcv1234zxcv1234");

-- ///// 
-- ///// Friends list
-- /////
create table javalibrary.FriendList (
	my_id varchar(16) not null,
    friend_id varchar(16) not null
);

insert into javalibrary.Friendlist (my_id, friend_id)
values("zxcv1234zxcv1234", "1234zsdlk32333"),
	("zxcv1234zxcv1234", "1234zsdlk32334"),
    ("1234zsdlk32333", "zxcv1234zxcv1234"),
    ("1234zsdlk32333", "1234zsdlk32334"),
    ("1234zsdlk32334", "zxcv1234zxcv1234");



select friend_id from javalibrary.Friendlist
where my_id = "zxcv1234zxcv1234";

DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `getFriendList`(my_id1 varchar(16))
BEGIN
	select friend_id from javalibrary.Friendlist
	where my_id = my_id1;
END //
DELIMITER ;

call javalibrary.getFriendList("zxcv1234zxcv1234");








create table javalibrary.book (
	book_id int not null unique,
	book_title varchar(50) not null unique,
	book_date date not null
);

insert into javalibrary.book (book_id, book_title, book_date)
values
	(1, "The Tiny Dragon", "2022-06-04"),
	(2, "Vampire", "2022-05-04"),
	(3, "Back end", "2022-03-04");
    
select * from javalibrary.book;


-- ----------------------------------------------------------------------------------------------
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `GBook`(b_name varchar(50))
BEGIN
	select * from javalibrary.book where book_title = b_name;
END //
DELIMITER ;

call javalibrary.GBook("Vampire");
call javalibrary.getBookInfo;
call javalibrary.getByNameInfo("Vampire");

-- ----------------------------------------------------------------------------------------------
DELIMITER //
CREATE DEFINER=`root`@`localhost` PROCEDURE `setBook`(id int , title varchar(50), dates date)
BEGIN
	insert into javalibrary.book (book_id, book_title, book_date)
	values
		(id, title, dates);
END //
DELIMITER ;

call javalibrary.setBook(4,"Front end", "2022-10-26");

-- ----------------------------------------------------------------------------------------------
DELIMITER //
create function `getBookInfo`()
returns VARCHAR(50)
begin	
  DECLARE NAME_FOUND VARCHAR(50);
  select book_title into NAME_FOUND from  javalibrary.book where book_title = "Vampire";
  return NAME_FOUND;
end  //
DELIMITER ;
select javalibrary.getBookInfo();

-- ----------------------------------------------------------------------------------------------
