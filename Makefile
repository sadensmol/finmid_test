start: stop
	docker-compose up -d --force-recreate &&\
	./gradlew bootRun

stop:
	docker-compose down


