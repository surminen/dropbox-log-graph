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

# Add code
ADD . /devel

# Install Yarn dependencies
RUN yarn install --modules-folder ./src/main/resources/static

#RUN ls -l node_modules
RUN ls -l ./src/main/resources/static

# Set the default command
CMD ["mvn", "spring-boot:run"]
