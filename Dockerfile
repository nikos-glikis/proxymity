# Docker file for Proxymity

FROM maven:3.3.9-jdk-8

# Update packages
RUN apt-get update
# Install required packages
ENV DEBIAN_FRONTEND noninteractive

RUN apt-get install -q -y  --force-yes apt-utils net-tools tor proxychains  imagemagick gocr libfreetype6 libfreetype6-dev libfontconfig1 libfontconfig1-dev wget mysql-server build-essential g++ flex bison gperf ruby perl libsqlite3-dev libfontconfig1-dev libicu-dev libfreetype6 libssl-dev libpng-dev libjpeg-dev python libx11-dev libxext-dev
RUN wget https://bitbucket.org/ariya/phantomjs/downloads/phantomjs-2.1.1-linux-x86_64.tar.bz2
RUN tar xvjf phantomjs-2.1.1-linux-x86_64.tar.bz2
RUN mv phantomjs-2.1.1-linux-x86_64 /usr/local/share
RUN ln -sf /usr/local/share/phantomjs-2.1.1-linux-x86_64/bin/phantomjs /usr/local/bin

#LAMP
RUN apt-get -q -y  --force-yes  install apache2  php5 php-pear php5-mysql
RUN service apache2 restart
RUN mkdir -p /var/log/httpd
ADD web/proxymity.conf /etc/apache2/sites-available/proxymity.conf
RUN a2ensite proxymity.conf
RUN a2ensite proxymity

EXPOSE 3306 3309
EXPOSE 80 8983

# Add maven to path
ENV PATH /opt/apache-maven-3.3.9/bin:$PATH


ADD ./ /opt/proxymity
VOLUME /var/lib/mysql

RUN cd /opt/proxymity/ && mvn  -T 4  clean compile assembly:single


#ENTRYPOINT service mysql start ; service tor start && cd /opt/proxymity/ && ./build.sh && java -cp .:dependency/*:target/com.tools.proxymity-1.0-jar-with-dependencies.jar com.object0r.tools.proxymity.MainCollect
ENTRYPOINT service mysql start ; service tor start ; service apache2 start && cd /opt/proxymity/  && java -cp .:dependency/*:target/com.tools.proxymity-1.0-jar-with-dependencies.jar com.object0r.tools.proxymity.MainCollect
