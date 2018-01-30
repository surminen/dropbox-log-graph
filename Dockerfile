# Set the base image
FROM maven:alpine

# Dockerfile author / maintainer 
MAINTAINER Martin Surminen <surminen@gmail.com> 

# Update application repository list and install the Redis server. 
#RUN apt-get update
#RUN apt-get install -y redis-server

# Expose default port
EXPOSE 8080

# Set the working directory
WORKDIR /devel

# Add code
ADD . /devel

# Maven build
#CMD ["mvn", "clean", "install"]

# Set the default command
CMD ["mvn", "spring-boot:run"]
