# user-project-api
User Project Api Service

### Assumptions taken
1. Not Encoding Passwords as part of this assignment. But redacted from response and removed from logs.
2. Email is Unique 
3. Deleting a user currently will delete it’s external projects too. Can be implemented other way round by throwing an error. 
4. Have applied uniqueness to project name too. Same project name cannot be added again to a user.
5. Update user will update name and password only. Not updating email for now. 

### Module structure
- `user-project-api-service.main.docker` - Docker File, env file for docker compose and prometheus config are defined here
- `user-project-api-service.main.java` - application entry point application context, rest controllers are defined here
- `user-project-api-service.main.resources` - Spring boot application properties config and Liquibase DB changelogs are defined here
- `mssql` - Containerised MS SQL instance
- `htmlReport` - contains test coverage report. Have 90% class coverage, 80% method and line coverage.
- `root project` - Root build.gradle, docker compose and deploy all script are defined here


### Build and Deploy

- Run `./scripts/deploy-all.sh` from root folder of this repo. It will deploy mssql instance, user-project-api-service. Metrics has been configured and exposed from spring boot app and the script will also deploy prometheus and grafana container for metrics scraping and visualisation.

- You can access the service at:
  `localhost:${PORT}` PORT (currently 8080) is defined in `user-project-api-service/src/main/docker/env.list`
  Swagger Documentation which can be used to test the api at:
  `http://localhost:${PORT}/swagger-ui/index.html`

- You can access prometheus dashboard on - localhost:9090.

- You can access grafana dashboard on - localhost:3000.

Authentication also implemented with initial user creds in db migration `user-project-api-service/src/main/resources/db/migration/data/insert_admin_user.sql`:

`username`: admin@example.com
`password`: password123

### Stop the docker stack

run `docker compose stop` from root folder of this repo or `./scripts/stop-all.sh`

### Run Tests

run `./gradlew test` or `./scripts/test-all.sh`

### Test & Deploy both

run `./scripts/testAnddeploy-all.sh`