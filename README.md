# form3-payment-api

#### Short summary:
Application is written with usage of Vert.x, RxJava2, Vavr.io and MongoDB as a database.
I tried to write it in functional style (wherever it makes sense).
I omitted on purpose some of implementations and tests (I tried to focus on my approach of designing and coding instead of writing fully functinal production-ready application).

#### How to run:
1. You will need docker up and running
2. `make install` will do the job (it creates a fat jar and boots up the application according to config in `docker-compose.yml`)

When application is up and ready it will bind to port `8080` and expose Swagger docs under `localhost:8080/swagger/`

#### What has been done:
- Designed REST API (OpenAPI 3 standard)
- Application splited into modules
- Basic examples of my approach of unit testing and e2e testing
- Added docker support
- Added circuit breakers support.

#### What is missing:
- Implementation of some endpoints (ie. `health`)
- I wanted to add verification of `x-request-id` header (to avoid duplication of POST requests), but I decided to skip it. However I'd use Redis as a common cache for storing request ids with their payload and on each POST request I'd verify if Payment was already processed. If yes -> `409 Conflict` as a response.
