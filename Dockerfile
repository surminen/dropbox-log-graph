# Set the base image
FROM centos

# Dockerfile author / maintainer 
MAINTAINER Martin Surminen <surminen@gmail.com> 

RUN yum install -y curl sudo git maven
RUN sudo yum install -y epel-release
RUN curl --silent --location https://rpm.nodesource.com/setup_9.x | sudo bash -
RUN sudo yum install -y nodejs
RUN npm install -g yarn

# Expose default port
EXPOSE 8080

# Set the working directory
WORKDIR /devel

# Set the default command
CMD /bin/bash -c "yarn install --modules-folder ./src/main/resources/static; mvn spring-boot:run"
