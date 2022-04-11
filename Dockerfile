From tomcat:8.0.51-jre8-alpine
ADD *.jar /usr/local/tomcat/webapps/
CMD ["catalina.sh","run"]
