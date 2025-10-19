# Api-Factory-Technical-Exercise
Api Factory – Technical Exercise 

##  Description
This Spring Boot application (Java 25) manages clients and their contracts in an insurance context.  
This application lets you manage clients and their contracts: you can create, view, update, and delete them, track contract details, and calculate totals.

## Architecture / Design

The project follows a layered architecture:

- **Controller layer**: exposes REST endpoints for client and contract operations.  
- **Service layer**: contains business logic, including soft deletion and contract updates.  
- **Repository layer**: uses Spring Data JPA to interact with the database.  
- **Model layer**: defines JPA entities (`Client`, `ClientContract`) with a `deleted` flag for logical deletion.  
- **DTO layer**: separates API payloads from internal data models.  
- **Exception layer**: handles errors globally with custom exceptions.  
- **Config layer**: includes a `DataSeeder` to populate the database with sample data at startup.

This structure promotes clarity, maintainability, and automatic schema generation through JPA.


## ⚙️ Requirements
- Java 25
- Maven
- H2 database (embedded)
- Postman (recommended for testing)

## 🚀 Running the Application Locally

1. Clone the repository:
   ```bash
   git clone <repo-url>
   cd <repo-folder>
   ```

2. Launch the app:
   ```bash
   mvn spring-boot:run
   ```

3. Access H2 console (if enabled):
   ```
   http://localhost:8080/h2-console
   ```

4. Test the API using Postman:
   - A Postman collection is included in the repository under the `Postman/` folder:  
     `Postman/Vaudoise Assurances - Technical Exercise API.postman_collection.json`
   - In Postman:
     - Open the app and click **Import**
     - Select the file from the `Postman/` folder
     - The collection will appear in your workspace
     - Use the preconfigured requests to test in the Scenarios folder

## ✅ Proof of Functionality
All endpoints are tested and functional.  
The included Postman collection demonstrates correct behavior and expected responses.  
Soft deletion updates contracts automatically, and validation is handled via custom exceptions.
