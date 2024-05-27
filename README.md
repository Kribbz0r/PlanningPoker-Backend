Quarkus MongoDB Projekt
Introduktion
Detta är ett Quarkus-projekt som använder MongoDB som databas. Quarkus är en Kubernetes-native Java-stack som är utformad för att fungera med GraalVM och HotSpot. MongoDB är en dokumentdatabas, vilket innebär att den lagrar data i BSON-dokument.

Förutsättningar
Java 17 eller senare
Maven 3.6.2+


Installation Back-end:
Klona detta repo till din lokala maskin  ( https://github.com/Kribbz0r/PlanningPoker-Backend )
Lägg .env-filen som du har fått av Grupp 1 i roten på projektet
Navigera till roten i projektet
Kör "./mvnw compile quarkus:dev" för att starta applikationen


Installation Front-end:
Klona detta repo till din lokala maskin   ( https://github.com/D-Hankin/PlanningPoker-FrontendDev )
Kör "npm install" för att installera de nödvändiga paketen
Kör "npm run dev" för att starta applikationen



Användning:

När en Admin loggar in får hen en JWT-token som är giltig i 24h. Den är sparad i local storage. Hen möts av tre navigeringsknappar; Employees, Projects och Create Project. Employees är det förinställda valet och hen ser alla anställda. Admin kan ge och ta ifrån behörighet till appen.
Vid klick på Projects syns alla projekt som i sin tur visar all status kring projektet. Det finns ett flertal fält som går att redigera.
Vid klick på Create Projects syns ett textfält och en Submit-knapp. När ett projektnamn är valt omvandlas fältet och tar istället in uppgiftsnamn. Det går att lägga till flera uppgifter. När alla uppgifter är tillagda och admin klickar på Release Project skapas projektet med alla uppgifter.
Utloggning sker uppe i höger hörn.

När en Anställd loggar in får hen en JWT-token som är giltig i 24h. Den är sparad i local storage. Hen möts av en lista med alla projekt som hen har tillgång till. Vid klick på ett projekt möts den anställde av ett flertal val i kolumnen Under Vote. I kolumnerna In Progress och Complete syns statusinformation om pågående och avslutade uppgifter.



Redan existerande användare:
(admin)
användarnamn: alice@example.com
lösenord: 111111

användarnamn: bob@example.com
lösenord: 111111

användarnamn: john@example.com
lösenord: 111111

användarnamn: david@example.com
lösenord: 111111



Inloggning:
/security/login    
                Denna GET end-point kräver ett användarnamn och lösenord i JSON-fomat i bodyn.
                Används för att lögga in och få en JWT-token

Användarhantering:
/user/all-users    
                Denna GET end-point ger en admin alla användare som inte är administratörer.
                Skicka in den JWT-token du fick vid inloggning av admin i en header.

/user/get-user     
                Denna GET end-point hämtar den inloggade användaren.
                Skicka in den JWT-token du fick vid inloggning som en header.

/user/change-access
                Denna PATCH end-point ändrar en anställds rättigheter att använda appen.
                Skicka in admins JWT-token och den anställdes email i headers för att använda.

/user/number-with-access
                Denna GET end-point visar hur många behöriga anställda det finns i databasen.
                Skicka in admins JWT-token i en header.

Uppgiftshantering:
/tasks/new-project
                Denna PATCH end-point uppdaterat listan med projekt i databasen och skapar ett nytt projekt med ingående sträng.
                Skicka in admins JWT-token och det önskade projektnamnet i headers för att använda.

/tasks/get-projects
                Denna GET end-point ger användaren alla projekt.
                Skicka in användarens JWT-token i en header för att använda.

/tasks/get-tasks
                Denna GET end-point hämtar alla uppgifter som användaren har behörighet att se.
                Skicka in användarens JWT-token och önskat projektnamn i en 

/tasks/new-task
                Denna POST end-point lägger till en uppgift i det valda projektet.
                Skicka in admins JWT-token, projektets namn och uppgiftens namn i varsin header.

/tasks/edit-task
                Denna PATCH end-point uppdaterar en uppgift med de valda värdena.
                Skicka in användarens JWT-token, projektnamn(string), användarens email(string) som headers, samt
                 uppgiften(Task) som en JSON-sträng i body. 

/tasks/archive-project
                Denna PATCH end-point arkiverar projektet (soft delete) genom att ta bort projektet från Projects-kollektionen.




******************************************************************************************************




# planningpoker

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Packaging and running the application

The application can be packaged using:
```shell script
./mvnw package
```
It produces the `quarkus-run.jar` file in the `target/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/quarkus-app/lib/` directory.

The application is now runnable using `java -jar target/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:
```shell script
./mvnw package -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar target/*-runner.jar`.

## Creating a native executable

You can create a native executable using: 
```shell script
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: 
```shell script
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./target/planningpoker-1.0.0-SNAPSHOT-runner`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/maven-tooling.

## Related Guides

- Hibernate ORM ([guide](https://quarkus.io/guides/hibernate-orm)): Define your persistent model with Hibernate ORM and Jakarta Persistence
- Narayana JTA - Transaction manager ([guide](https://quarkus.io/guides/transaction)): Offer JTA transaction support (included in Hibernate ORM)
- Hibernate Validator ([guide](https://quarkus.io/guides/validation)): Validate object properties (field, getter) and method parameters for your beans (REST, CDI, Jakarta Persistence)
- SmallRye OpenAPI ([guide](https://quarkus.io/guides/openapi-swaggerui)): Document your REST APIs with OpenAPI - comes with Swagger UI
- RESTEasy Classic ([guide](https://quarkus.io/guides/resteasy)): REST endpoint framework implementing Jakarta REST and more
- JDBC Driver - PostgreSQL ([guide](https://quarkus.io/guides/datasource)): Connect to the PostgreSQL database via JDBC

## Provided Code

### Hibernate ORM

Create your first JPA entity

[Related guide section...](https://quarkus.io/guides/hibernate-orm)



### RESTEasy JAX-RS

Easily start your RESTful Web Services

[Related guide section...](https://quarkus.io/guides/getting-started#the-jax-rs-resources)
