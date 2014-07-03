Play 1.x
========

This is a benchmark application for Play 1.2.x and 1.3.x.

This application implements `Round 8` test cases and requires `java 1.6+`.

The application uses the [Rythm template engine](https://github.com/greenlaw110/play-rythm) and Hibernate persistence (3.6.10 for Play 1.2.x and 4.1.3 for Play 1.3.x).

Installation
============
Install Mysql 5.6+
Create the database 
```sql
# create benchmark user
GRANT ALL ON *.* TO 'benchmarkdbuser'@'%' IDENTIFIED BY 'benchmarkdbpass';
GRANT ALL ON *.* TO 'benchmarkdbuser'@'localhost' IDENTIFIED BY 'benchmarkdbpass';

DROP DATABASE IF EXISTS hello_world;
CREATE DATABASE hello_world;
USE hello_world;
```

Download [Play](http://www.playframework.com/download)
Add the path to the `PATH` variable

Run the application
`play run`

Insert the data: http://localhost:8080/setup

Launch
======

Extra urls
==========

For convenience, the application include the following actions: 
  - [index](): a help page that lists all the actions
  - [populateDb](): create the test datasets in the DB

Output
======

/json
-----
```
< HTTP/1.1 200 OK
< Server: Play! Framework;1.3.x-1.3.0RC1;dev
< Content-Type: application/json; charset=utf-8
< Date: Thu, 03 Jul 2014 10:11:10 GMT
< Set-Cookie: PLAY_FLASH=; Expires=Thu, 03 Jul 2014 10:11:10 GMT; Path=/
< Set-Cookie: PLAY_ERRORS=; Expires=Thu, 03 Jul 2014 10:11:10 GMT; Path=/
< Set-Cookie: PLAY_SESSION=; Expires=Thu, 03 Jul 2014 10:11:10 GMT; Path=/
< Cache-Control: no-cache
< Content-Length: 26

{"message":"Hello World!"}
```

json
```
```
json
```
```

json
```
```
json
```
```
json
```
```
json
```
```
