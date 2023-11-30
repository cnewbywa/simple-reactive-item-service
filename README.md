# Simple Reactive Item Service

## Overview
This project uses a subset of the features in [item-service](https://github.com/cnewbywa/item-service), built with [Spring Webflux](https://docs.spring.io/spring-framework/reference/web/webflux.html) using reactive libraries and supporting native builds.

### Features
* Reactive rest service for adding, deleting and fetching an item
* [OpenAPI](https://www.openapis.org/) support with [Swagger UI](https://swagger.io/tools/swagger-ui/)
* Persisting items in a [Mongo](https://www.mongodb.com/) database
* [OAuth2](https://oauth.net/2/) support using [Keycloak](https://www.keycloak.org/)

### Building the service

#### Prerequisites
* [GraalVM](https://www.graalvm.org/) for native executable
* GraalVM and [Docker](https://www.docker.com/) for native image

#### Testing
```
./mvnw clean verify
```

With code coverage report (for both unit and integration tests):

```
./mvnw clean verify jacoco:report jacoco:report-integration
```
#### Build
Jar running on JVM: 

```
./mvnw clean package
```

Native executable: 

```
./mvnw -Pnative clean native:compile
```

Native image: 

```
./mvnw -Pnative clean spring-boot:build-image
```

### Starting the service

#### Prerequisites
* bindings/ca-certificates/ca.crt needs to be added to the local ca truststore when service is run locally
* cnewbywa.auth needs to be added to `/etc/hosts` or OSX/Windows equivalent when service is run locally
* Mongo db has been started. Can be started with `docker/db/start.sh` which uses [Docker Compose](https://docs.docker.com/compose/) to start a pre-configured container. Please note that a docker secret (mongo_root_password) needs to be created before starting. Location of it is set in `docker-compose.yml`.
* Auth service has been started. Can be started with docker/runtime/auth/start-auth.sh in [common-services](https://github.com/cnewbywa/common-services) repository which uses Docker Compose to start a pre-configured container

#### Start
Local JVM: 

```
./mvnw spring-boot:run -Dspring-boot.run.arguments="--ITEM_PASSWORD=items --item.keystore.password=itemservice" -Dspring-boot.run.profiles=local
```

Local native executable: 

```
./target/simple-reactive-item-service --ITEM_PASSWORD=items --item.keystore.password=itemservice --spring.profiles.active=local
```

Native image running as a container in Docker: `./start.sh` in `docker/app` directory

### Using the service
The service is available at https://localhost:9443/items.
The OpenAPI definition is available at https://localhost:9443/v3/api-docs.
The Swagger UI is available at https://localhost:9443/webjars/swagger-ui/index.html.

When calling the service you need an access token. Example request using curl:

```
curl -i --insecure --request POST 'https://cnewbywa.auth:443/realms/item/protocol/openid-connect/token' --header 'Content-Type: application/x-www-form-urlencoded' --data-urlencode 'grant_type=password' --data-urlencode 'client_id=item-app' --data-urlencode 'username=test.user' --data-urlencode 'password=Password1'
```