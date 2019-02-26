install:
	./gradlew :api:shadowJar
	docker-compose up -d