#!/bin/bash
set -exuo pipefail

pkill -9 -U blog

su - blog

cd /export/git/blog
git pull
mvn clean -U install -Dmaven.test.skip=true -Pproduction
cp -r /export/git/blog/blog-operate/blog-operate-web/target/blog-operate-web-1.0.0-SNAPSHOT/* /export/App/www.6zeroq.com/
cp /export/lock/www.6zeroq.com/web.xml /export/App/www.6zeroq.com/WEB-INF/web.xml
cp /export/lock/www.6zeroq.com/spring-mvc.xml /export/App/www.6zeroq.com/WEB-INF/classes/spring/spring-mvc.xml
cp /export/lock/www.6zeroq.com/rsa_private_key.pem /export/App/www.6zeroq.com/WEB-INF/classes/cer/rsa_private_key.pem
cp /export/lock/www.6zeroq.com/pkcs8_rsa_private_key.pem /export/App/www.6zeroq.com/WEB-INF/classes/cer/pkcs8_rsa_private_key.pem
cp /export/lock/www.6zeroq.com/rsa_public_key.pem /export/App/www.6zeroq.com/WEB-INF/classes/cer/rsa_public_key.pem
/export/Domains/www.6zeroq.com/server1/bin/restart.sh
