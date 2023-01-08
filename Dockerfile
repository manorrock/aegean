FROM eclipse-temurin:17
RUN cd /opt && \
    curl --insecure -L -O https://archive.apache.org/dist/tomcat/tomcat-9/v9.0.43/bin/apache-tomcat-9.0.43.tar.gz && \
    tar xfvz apache-tomcat-9.0.43.tar.gz && \
    mv apache-tomcat-9.0.43 tomcat && \
    rm apache-tomcat-9.0.43.tar.gz && \
    rm -rf tomcat/webapps/*docs* && \
    rm -rf tomcat/webapps/*examples* && \
    rm -rf tomcat/webapps/*manager* && \
    rm -rf tomcat/webapps/ROOT*
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
EXPOSE 8080
WORKDIR /mnt
ENV ROOT_DIRECTORY /mnt
COPY target/aegean.war /opt/tomcat/webapps/ROOT.war
