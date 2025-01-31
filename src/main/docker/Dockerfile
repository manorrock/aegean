FROM eclipse-temurin:21
RUN apt-get update && apt-get install -y unzip && \
    cd /opt && \
    export GLASSFISH_VERSION=7.0.21 && \
    curl --insecure -L -O https://download.eclipse.org/ee4j/glassfish/glassfish-${GLASSFISH_VERSION}.zip && \
    unzip glassfish-${GLASSFISH_VERSION}.zip && \
    mv glassfish7 glassfish && \
    rm glassfish-${GLASSFISH_VERSION}.zip
CMD ["/opt/glassfish/bin/asadmin", "start-domain", "-v"]
EXPOSE 8080
WORKDIR /mnt
ENV ROOT_DIRECTORY=/mnt
COPY target/aegean.war /opt/glassfish/glassfish/domains/domain1/autodeploy/aegean.war
