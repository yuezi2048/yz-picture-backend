FROM louislivi/jdk:17

WORKDIR /home
ADD target/yu-picture-backend-0.0.1-SNAPSHOT.jar /home/user-center-my-backend-0.0.1-SNAPSHOT.jar

ENV MY_JAVA_OPTS=""
CMD java ${JAVA_OPTS} ${MY_JAVA_OPTS} -jar target/yu-picture-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod