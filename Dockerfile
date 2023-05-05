# Build arguments.
ARG USER_ID=3001
ARG USER_NAME=vireo
ARG HOME_DIR=/$USER_NAME
ARG SOURCE_DIR=$HOME_DIR/source
ARG APP_PATH=/var/vireo
ARG NODE_ENV=production

# Maven stage.
FROM maven:3-eclipse-temurin-11-alpine as maven
ARG USER_ID
ARG USER_NAME
ARG HOME_DIR
ARG SOURCE_DIR
ARG APP_PATH
ARG NODE_ENV

ENV NODE_ENV=$NODE_ENV

# Create the group (use a high ID to attempt to avoid conflits).
RUN addgroup -g $USER_ID $USER_NAME

# Create the user (use a high ID to attempt to avoid conflits).
RUN adduser -h $HOME_DIR -u $USER_ID -G $USER_NAME -D $USER_NAME

# Ensure source directory exists and has appropriate file permissions.
RUN mkdir -p $SOURCE_DIR && \
    chown $USER_ID:$USER_ID $SOURCE_DIR && \
    chmod g+s $SOURCE_DIR

# Upgrade the system and install dependencies.
RUN apk -U upgrade && \
    apk add --update --no-cache nodejs npm make g++ py3-pip

# Set deployment directory.
WORKDIR $SOURCE_DIR

# Copy in files from outside of docker.
COPY ./pom.xml ./pom.xml
COPY ./assembly.xml ./assembly.xml
COPY ./.wvr/build-config.js ./.wvr/build-config.js
COPY ./src ./src
COPY ./build ./build
COPY ./package.json ./package.json

# Change ownership of source directory.
RUN chown -R $USER_ID:$USER_ID $SOURCE_DIR

# Login as user.
USER $USER_NAME

# Build.
RUN mvn package -Pproduction

# JRE Stage.
FROM eclipse-temurin:11-alpine
ARG USER_ID
ARG USER_NAME
ARG HOME_DIR
ARG SOURCE_DIR
ARG APP_PATH

ENV APP_PATH=$APP_PATH

# Create the group (use a high ID to attempt to avoid conflits).
RUN addgroup -g $USER_ID $USER_NAME

# Create the user (use a high ID to attempt to avoid conflits).
RUN adduser -h $HOME_DIR -u $USER_ID -G $USER_NAME -D $USER_NAME

# Ensure app path directory exists and has appropriate file permissions.
RUN mkdir -p $APP_PATH && \
    chown $USER_ID:$USER_ID $APP_PATH && \
    chmod g+s $APP_PATH

# Update the system and install gettext for envsubst.
RUN apk -U upgrade && \
    apk add --update --no-cache gettext

# Copy files from outside docker to inside.
COPY build/appConfig.js.template /usr/local/app/templates/appConfig.js.template
COPY build/docker-entrypoint.sh /usr/local/bin/docker-entrypoint.sh
RUN chmod ugo+x /usr/local/bin/docker-entrypoint.sh

# Login as user.
USER $USER_NAME

# Set deployment directory.
WORKDIR $HOME_DIR


# Copy over the built artifact and library from the maven image.
COPY --from=maven $SOURCE_DIR/target/vireo-*.war ./vireo.war
COPY --from=maven $SOURCE_DIR/target/libs ./libs

EXPOSE 9000

# Entrypoint performs environment substitution on appConfig.js.
ENTRYPOINT ["docker-entrypoint.sh"]

# Run java command.
CMD ["java", "-jar", "./vireo.war"]
