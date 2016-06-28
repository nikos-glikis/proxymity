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

#build phantomjs from source
#RUN git clone git://github.com/ariya/phantomjs.git
#RUN cd phantomjs ; git checkout 2.1.1
#RUN cd phantomjs ; git submodule init
#RUN cd phantomjs ; git submodule update
#RUN cd phantomjs ; python build.py


EXPOSE 3306 3309

# Add maven to path
ENV PATH /opt/apache-maven-3.3.9/bin:$PATH


ADD ./ /opt/proxymity
VOLUME /var/lib/mysql

RUN cd /opt/proxymity/ && ./build.sh


#ENTRYPOINT service mysql start ; service tor start && cd /opt/proxymity/ && ./build.sh && java -cp .:dependency/*:target/com.tools.proxymity-1.0-jar-with-dependencies.jar com.object0r.tools.proxymity.MainCollect
ENTRYPOINT service mysql start ; service tor start && cd /opt/proxymity/  && java -cp .:dependency/*:target/com.tools.proxymity-1.0-jar-with-dependencies.jar com.object0r.tools.proxymity.MainCollect
