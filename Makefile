.RECIPEPREFIX := >
.PHONY: build test run docker-build docker-run docker-compose-up docker-compose-down docker-compose-build frontend-build frontend-dev dev-stack docker-compose-logs

# Backend commands
build:
>mvn -f backend/pom.xml clean package

test:
>mvn -f backend/pom.xml clean verify

run:
>java -jar backend/target/backend-0.0.1-SNAPSHOT.jar

# Individual Docker commands
docker-build:
>docker build -t signature-backend backend

docker-run:
>docker run -p 8080:8080 signature-backend

# Frontend commands
frontend-build:
>cd frontend && npm run build

frontend-dev:
>cd frontend && npm run dev

# Docker Compose commands
docker-compose-build:
>docker-compose build

docker-compose-up:
>docker-compose up -d

docker-compose-down:
>docker-compose down

docker-compose-logs:
>docker-compose logs -f

# Full stack development
dev-stack: docker-compose-up
>@echo "Full stack running:"
>@echo "  Frontend: http://localhost:3000"
>@echo "  Backend:  http://localhost:8081"
>@echo "  Health:   http://localhost:8081/health"
