# Movies API

- A robust REST API for managing a movie database.
- Built with Spring Boot and JPA for seamless functionality.
- Created to digitize and manage movie records efficiently.

## 🎯 Features

- Complete CRUD operations for movies, actors, and genres.
- Many-to-many relationship management between entities.
- Filtering capabilities by genre, actor, or release year.
- Pagination support for list endpoints.
- Case-insensitive search by movie title.
- Comprehensive error handling for common scenarios.
- SQLite database integration.
- Force deletion option for related entities.

## 🛠️ Technologies

- Java 21
- Spring Boot
- Spring Data JPA
- Hibernate ORM
- SQLite Database
- Maven Dependency Management

## 📋 Dependencies

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.xerial</groupId>
        <artifactId>sqlite-jdbc</artifactId>
        <version>3.46.1.3</version>
    </dependency>
    <dependency>
        <groupId>org.hibernate.orm</groupId>
        <artifactId>hibernate-community-dialects</artifactId>
        <version>6.0.0.Final</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

## 📋 Prerequisites

To set up and run the project, ensure you have the following tools and software:

- **Java 21** or higher
- **Maven 3.6+**
- **SQLite 3+**
- **Postman** (optional, for API testing)

## 🚀 Getting Started

### Installation
Follow these steps to set up the project locally:

1. **Clone the repository**:
    ```bash
    git clone https://github.com/MrFox-hub/MoviesApi-Java
    cd kmdb
    ```

2. **Configure the database**:
    - Make sure SQLite is installed on your system.
    - The database configuration is already pre-defined in `src/main/resources/application.properties`.

3. **Build the project**:
    ```bash
    mvn clean install
    ```

4. **Run the application**:
    ```bash
    mvn spring-boot:run
    ```

The API will be accessible at `http://localhost:8080`.

## 🔍 API Endpoints

### Movies
- `GET /api/movies` - Fetch all movies.
- `GET /api/movies/{id}` - Retrieve a specific movie by ID.
- `POST /api/movies` - Add a new movie.
- `PATCH /api/movies/{id}` - Update an existing movie.
- `DELETE /api/movies/{id}` - Delete a movie.

### Actors

- `GET /api/actors` - Fetch all actors.
- `GET /api/actors/{id}` - Retrieve a specific actor by ID.
- `POST /api/actors` - Add a new actor.
- `PATCH /api/actors/{id}` - Update an existing actor.
- `DELETE /api/actors/{id}` - Delete an actor.

### Genres

- `GET /api/genres` - Fetch all genres.
- `GET /api/genres/{id}` - Retrieve a specific genre by ID.
- `POST /api/genres` - Add a new genre.
- `PATCH /api/genres/{id}` - Update an existing genre.
- `DELETE /api/genres/{id}` - Delete a genre.

## 📝 Request/Response Examples

### Creating a Movie
#### Request

```json
POST /api/movies
{
    "title": "The Matrix",
    "releaseYear": 1999,
    "duration": 136,
    "genreIds": [1, 2],
    "actorIds": [1, 2]
}
```
### Response
```{
    "id": 1,
    "title": "The Matrix",
    "releaseYear": 1999,
    "duration": 136,
    "genres": [
        { "id": 1, "name": "Action" },
        { "id": 2, "name": "Sci-Fi" }
    ],
    "actors": [
        { "id": 1, "name": "Keanu Reeves" },
        { "id": 2, "name": "Laurence Fishburne" }
    ],
    "description": "A groundbreaking sci-fi movie."
}
```
## 🎨 Extras
The API includes several advanced features designed to enhance usability and ensure data integrity. These extras allow for efficient data retrieval, clear responses, and dynamic management of relationships. Below are the highlights:
### Search by Name
- Perform case-insensitive searches for movies, actors, and genres by name.

- Example:
```bash
GET /api/actors/search?name=John Doe
```

### Pagination Support
- All list endpoints support pagination using query parameters for optimized data retrieval.
- Query Parameters:
    - page: Specifies the page number (starting from 0).
    - size: Defines the number of items per page.
- Example:
```bash
GET /api/movies?page=1&size=5
```

### Trimmed Metadata
- Pagination metadata is displayed at the end of responses to improve readability.
- Metadata includes:
    - totalPages: Total number of pages.
    - currentPage: Current page number (1-based index).
    - pageSize: Number of items per page
- Example:
```bash
{
    "content": [...],
    "totalPages": 5,
    "currentPage": 2,
    "pageSize": 10
}

```
### Detailed/Minimal Responses
- Endpoints support toggling between minimal and detailed views using the detailed query parameter:
    - false (default): Returns minimal information.
    - true: Includes additional details such as descriptions and related entities.

#### Minimal responses are concise, providing only the most essential fields.
#### Detailed responses include comprehensive data, such as relationships (e.g., actors and genres for movies).
- Example Usage:

```bash
GET /api/actors?detailed=true
```

- Returns (detailed):
```bash
{
    "name": "John Doe",
    "description": "Award-winning actor",
    "birthDate": "2003-08-16",
    "movies": [
        { "title": "The Matrix", "releaseYear": 1999 },
        { "title": "John Wick", "releaseYear": 2014 }
    ]
}
```
- Returns (minimal):
```bash
{
    "name": "John Doe",
    "birthDate": "2003-08-16"
}
```

### Duplication Checker
- Prevents duplicate entries for movies, actors, and genres by validating the name field during creation or updates.
- Ensures database integrity by rejecting records with names that already exist (case-insensitive).
- Example:
    - Attempting to create a new actor with the name "John Doe" when "John Doe" already exists will result in an error response.
Example Response (Duplicate Entry):
```bash
{
    "error": "Actor with name 'John Doe' already exists."
}
```
- Should Apply to:
    - Movies (e.g., title uniqueness).
    - Actors (e.g., name uniqueness).
    - Genres (e.g., name uniqueness).

### Dynamic Relationship Handling
- Previously, relationships were appended, leaving outdated associations in the database, which could cause conflicts.
- The new approach ensures that any update replaces previous relationships with the current ones, making them the official and only associations.
#### This guarantees data consistency and eliminates ambiguity in relationship dynamics.

## 🔒 Error Handling
The API employs consistent and meaningful error responses using standard HTTP status codes. Below are the main codes used and their implications:

- **200 OK**: The request was successfully processed.
- **201 Created**: The resource was successfully created.
- **204 No Content**: The resource was successfully deleted.
- **400 Bad Request**: The request contains invalid data or parameters.
- **404 Not Found**: The requested resource does not exist.
- **405 Method Not Allowed**: The HTTP method used is not supported for the requested endpoint.
- **500 Internal Server Error**: An unexpected error occurred on the server.

## 🧪 Testing
You can test the API endpoints using Postman or a similar REST client. A Postman collection is provided in the repository to streamline the testing process.

#### Steps to Test
- Import Postman Collection:

- Import the Movie_Database_API.postman_collection.json file into Postman.
- This collection contains pre-configured requests for all API endpoints.
- Configure Environment:

- Set the base URL to http://localhost:8080 or the appropriate host where the API is deployed.
- Adjust parameters like id, actorIds, or genreIds as needed.
- Run Requests:

- Use the provided requests in the collection to test CRUD operations and search functionality.
- Example Testing Workflow
- Add a new movie using the POST /api/movies endpoint.
- Retrieve the added movie using the GET /api/movies/{id} endpoint.
- Update the movie details using the PATCH /api/movies/{id} endpoint.
- Delete the movie using the DELETE /api/movies/{id} endpoint.
## 👥 Authors
This project was developed and maintained by Martti Rebane and helping hand (Mentor @Flamingo). Feel free to reach out for collaboration or improvements.
Discord
```plaintext
@bitchzon3
```

### Thank You