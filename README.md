# Is It Up?

## Start instructions

- <code>docker-compose up -d</code>
- Check if frontend, backend, and database are all up with <code>docker ps</code> (mysql can take a while to get ready)
- Visit [Frontend](http://localhost:3000) or [Backend Swagger](http://localhost:8080/swagger-ui.html)

## Tech decisions

- Reactive backend (Spring Webflux, R2DBC)
- Server sent events only on change
- JavaScript/React Frontend with Material UI
- MySQL database
- Docker
- Caffeine cache for each connecting client

## TODOs

- Update operation from Frontend
- Multi user support
- Input validation (Both Frontend and Backend)
