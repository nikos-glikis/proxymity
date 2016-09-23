Proxymity
=========
Proxymity is a tool that extracts anonymous proxies using various ways. 

Then it constantly verifies them and tests their capabilities.

It scans lots of online websites and employs advanced techniques to bypass protections like Javascript Evaluation and captchas.

Installation
============

The easiest way to run Proxymity is to use Docker (Described below).

If you want to install it in Debian/Ubuntu read below.

Dependencies:

    1) PhantomJs
    2) gocr
    3) ImageMagick (command line command is convert)
    4) Mysql database/

All tools must be installed and added to the path and accessible from the command line.

Ubuntu:

sudo apt-get install imagemagick gocr phantomjs

Windows versions are included in the bin folder and used automatically by the code.

Tested on JDK 8.

Where are proxies Stored ?
--------------------------

Proxies are stored in the mysql databased provided. You need to provide a mysql database_user/password/database_name. The table is automatically created, along with a stored procedure

getRandomProxy()

This is called as:

    getRandomProxy(NULL); - Get any type active proxy
    getRandomProxy('http'); - Get any type active proxy of type http
    getRandomProxy('socks4'); - Get any type active proxy of type socks4
    getRandomProxy('socks5'); - Get any type active proxy of type socks5

On a Docker machine you can access the proxies by visiting: http://localhost:81/activeproxies.php?start=0&limit=100

Docker
======
The easiest way to run this is with Docker.

Make sure you have Docker installed. Instructions for all major distributions here:

    https://docs.docker.com/engine/installation/

Then run these commands:

    ./build_docker.sh
    ./run_docker.sh
    
 You can see the proxies at http://localhost:81/activeproxies.php?start=0&limit=100
 
 Adjust start and limit to your linking.
 
 The output is json and self-explained.
 
Configuration
==============
- config.properties.sample contains all values that can be set.
- Copy config.properties.sample to config.properties and edit it.
- The file for Docker is Docker/config.properties

Windows
========

Binaries files are included for portability on windows. You are free to delete bin directory in if not needed.

Troubleshoot
============

- Phantomjjs download fails in ./build_docker.sh
    - Open manually in browser https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-linux-x86_64.tar.bz2 and try again.
    - Create the connection an then cancel, there is no need for actual download.
    - Then run ./build_docker.sh again.
    