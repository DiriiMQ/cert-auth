.RECIPEPREFIX := >
.PHONY: build test run docker-build docker-run

build:
>mvn -f backend/pom.xml clean package

test:
>mvn -f backend/pom.xml clean verify

run:
>java -jar backend/target/backend-0.0.1-SNAPSHOT.jar

docker-build:
>docker build -t signature-backend backend

docker-run:
>docker run -p 8080:8080 signature-backend
