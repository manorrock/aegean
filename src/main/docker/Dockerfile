FROM eclipse-temurin:19
RUN cd /opt && \
    export TOMCAT_VERSION=9.0.80 && \
    curl --insecure -L -O https://archive.apache.org/dist/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    tar xfvz apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    mv apache-tomcat-${TOMCAT_VERSION} tomcat && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    rm -rf tomcat/webapps/*docs* && \
    rm -rf tomcat/webapps/*examples* && \
    rm -rf tomcat/webapps/*manager* && \
    rm -rf tomcat/webapps/ROOT*
CMD ["/opt/tomcat/bin/catalina.sh", "run"]
EXPOSE 8080
WORKDIR /mnt
ENV ROOT_DIRECTORY /mnt
COPY target/aegean.war /opt/tomcat/webapps/ROOT.war
