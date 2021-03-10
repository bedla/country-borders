# Crossing borders

This application is able to search border crossing between two countries. It uses public data of countries loaded
from `.json` file/URL.

## Build and run application

Requirements
- Java 11
- Maven 3

Steps

1. Clone git repository
1. Run `mvn clean verify` command to build and test application
1. Than start application run `mvn spring-boot:run`
    1. application will download countries data from the internet (see `application.properties` for URL)
    1. to use local data you can start application with `local` Spring profile with
       command `mvn spring-boot:run -Dspring-boot.run.profiles=local`

If you prefer running application from your IDE, starting class is `CountryBordersApplication`.

## Technical details

Application uses Dijkstra routing algorithm from my favorite [JGraphT](https://jgrapht.org/) library to determine the shortest path between two countries.

It downloads countries data from GitHub. Loads them into memory and when HTTP request is invoked it searches path between countries.

Take a look at tests how to call REST endpoints. Or use IntelliJ IDEA build-in HTTP client definitions in `main.http` file.

Or use Curl command, eg. `curl -X GET --location "http://localhost:8080/routing/CZE/ITA" -H "Accept: application/json"`
