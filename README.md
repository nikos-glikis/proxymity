Installation
=============

Dependencies:

    1) PhantomJs
    2) gocr
    3) ImageMagick (command line command is convert)
    4) Mysql database/

All tools must be installed and added to the path must be installed and accessible from the command line.

Ubuntu:

sudo apt-get install imagemagick gocr phantomjs

Windows versions are included in the bin folder and used automatically by the code.

Tested on jdk 8.


Where are proxies Stored ?
----------------------------

Proxies are stored in the mysql databased provided. You need to provide a mysql database_user/password/database_name. The table is automatically created, along with a stored procedure

getRandomProxy()

This is called as:


    getRandomProxy(NULL); - Get any type active proxy
    getRandomProxy('http'); - Get any type active proxy of type http
    getRandomProxy('socks4'); - Get any type active proxy of type socks4
    getRandomProxy('socks5'); - Get any type active proxy of type socks5


