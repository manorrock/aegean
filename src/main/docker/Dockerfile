FROM debian
RUN apt-get update && \
    apt-get upgrade -y && \
    apt-get install -y curl tar gzip && \
    cd /usr/local && \
    curl --insecure -L -O https://cdn.azul.com/zulu/bin/zulu11.35.15-ca-jdk11.0.5-linux_x64.tar.gz && \
    tar xfvz zulu11.35.15-ca-jdk11.0.5-linux_x64.tar.gz && \
    mv zulu11.35.15-ca-jdk11.0.5-linux_x64 jdk && \
    rm zulu11.35.15-ca-jdk11.0.5-linux_x64.tar.gz && \
    curl --insecure -L -O https://repo1.maven.org/maven2/cloud/piranha/piranha-micro/20.4.0/piranha-micro-20.4.0-all.jar && \
    mkdir -p /usr/local/piranha && \
    mv piranha-micro-20.4.0-all.jar /usr/local/piranha/piranha-micro.jar && \
    rm -rf /var/lib/apt/lists/*
ENV PATH=$PATH:/usr/local/jdk/bin
WORKDIR /usr/local/piranha
CMD ["java", "-jar", "piranha-micro.jar", "--war", "ROOT.war"]
COPY target/aegean.war /usr/local/piranha/ROOT.war
