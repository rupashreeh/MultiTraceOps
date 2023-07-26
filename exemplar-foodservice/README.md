# PODS-Spring

# Dev Environment details:

Java:11
Maven: 3.8.4 
Spring Boot: 2.6.3 (Used Spring initializer to set up the spring boot project)
Python :3.9

To run wallet service
---------------------

cd wallet
mvn clean install
docker build -t wallet .

To run on the Ubuntu 20.04 use the command mentioned below <br>
Place the initialData.txt file in / folder <br>
Ensure you have correct permissions for the file <br> 
Use the command below to allow the all permissions to any user, if needed <br> 

$ sudo chmod 444 /initialData.txt

$ docker run --add-host=host.docker.internal:host-gateway -p 8080:8080 -v /initialData.txt:/initialData.txt restaurant

$ docker run --add-host=host.docker.internal:host-gateway -p 8081:8081 -v /initialData.txt:/initialData.txt delivery

$ docker run --add-host=host.docker.internal:host-gateway -p 8082:8082 -v /initialData.txt:/initialData.txt wallet

On windows we can do run as follows

docker run -p 8082:8082 -v C:\initialData.txt:/initialData.txt wallet

On windows MINGW64

docker run -p 8082:8082 -v //c/initialData.txt:/initialData.txt wallet


To run restaurant service
--------------------------

cd restaurant

mvn clean install

docker build -t restaurant .

To run the docker container on the Ubuntu 20.04 use the command mentioned below <br>
Place the initialData.txt file in / folder (same as before, don't repeat if already done.) <br> 
Ensure you have correct permissions for the file. <br>
Use the command below to allow the all permissions to any user, if needed. <br> 

$ sudo chmod 444 /initialData.txt

$ docker run --add-host=host.docker.internal:host-gateway -p 8080:8080 -v /initialData.txt:/initialData.txt restaurant

On windows 10 we can do run as follows

docker run -p 8080:8080 -v C:\initialData.txt:/initialData.txt restaurant

On windows 10 MINGW64

docker run -p 8080:8080 -v //c/initialData.txt:/initialData.txt restaurant

To run delivery service
------------------------

$ cd delivery 

$ mvn clean install 

$ docker build -t delivery . 

To run the docker container on the Ubuntu 20.04 use the command mentioned below <br>
Place the initialData.txt file in / folder <br>
Ensure you have correct permissions for the file <br> 
Use the command below to allow the all permissions to any user, if needed <br>

$ sudo chmod 444 /initialData.txt

$ docker run --add-host=host.docker.internal:host-gateway -p 8081:8081 -v /initialData.txt:/initialData.txt delivery

On windows 10 we can do run as follows

docker run -p 8081:8081 -v C:\initialData.txt:/initialData.txt delivery

On windows 10 MINGW64

docker run -p 8081:8081 -v //c/initialData.txt:/initialData.txt delivery

Testing:

Postman for manual testing
Java Unit tests - they run when the jar is built
Python integration tests - Our python tests folder contains this
Please find the readme for the test cases in the tests folder