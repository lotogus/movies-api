
# Technical details

## Assumptions & decisions made

- on app startup will get `app.client.migration.ids` properties and migrate the OpenMovie's to Movie DB
   - after that it's not necessary to get Movie from external API
- just for simulate some external dependency, when `critics` are requested will get them from OpenMovie's API (for getting updated votes & califications)
   - a circuit breaker was implemented in order to avoid cascade failures
   - TODO: add cache in order to avoid too may requests

## run locally

1. start a MongoDB instance

        docker-compose up

2. package app

        ./mvnw package

3. run app (let's suppose the key is "88888")

        java -jar -Dapp.client.key=88888 infra/target/infra-0.0.1-SNAPSHOT.jar

4. check swagger page

        http://localhost:8080/swagger-ui/

## App layers

The application architectue tried to accomplish (basically) the Uncle's Bob clean architecture:

   ![App layers](doc/layers.png?raw=true "Layers")

I usually build a multimodule project divided in two parts:

### domain

in domain layer all the business entities, interfaces (repositories, clients, events) are abstracted, so we can work in the Actions, pure business logic.

### infra

in infra layer all interfaces details are implemented (mongo repository, http client, circuit breaker, etc). there we can decide to use Springboot or another framework like Micronaut.


## Either everything

'cause I like Railway Oriented Programming https://fsharpforfunandprofit.com/rop/ I like to design solutions using Either just to avoid complexity.


### TODOs

- [ ] Add MockMvc tests in order to get nice coverage
- [ ] Add cache for API requests (evaluating to use https://github.com/konrad-kaminski/spring-kotlin-coroutine for @Cacheable annotation on coroutines)
- [ ] add JWT validation & role access for API endpoints
- [ ] improve swagger doc w/model examples, error returns
- [ ] Add repositories tests
   