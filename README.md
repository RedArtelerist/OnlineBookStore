# 📚 Online Book Store 📚
The Online Bookstore Management System is an application that not only provides a seamless experience for shoppers and managers but also prioritizes security through the implementation of authentication and authorization functionalities using JWT tokens. Shoppers can easily join, explore books, manage their shopping carts, make purchases, and review order history. Meanwhile, managers have the tools to efficiently arrange books, organize categories, and oversee order statuses. This project aims to create a user-friendly API that enhances the online book-buying experience while streamlining management tasks for administrators.

## Features
### For Shoppers (User)
- **User Authentication:**
  - Join the store securely.
  - Sign in to explore and purchase books.
- **Book Browsing:**
  - View all available books.
  - Detailed view of individual books.
  - Filter books by title, authors, and price.
- **Category Exploration:**
  - Browse through all available book categories.
  - View books under a specific category.
- **Shopping Cart Management:**
  - Add books to the cart.
  - View and manage items in the cart.
  - Remove books from the cart.
- **Purchase Handling:**
  - Purchase all items in the cart.
  - Access past order history.
- **Order Details:**
  - View all items in an order.
  - Detailed view of specific items in an order.
### For Managers (Admin)
- **Book Management:**
  - Add, modify, or remove books from the store.
- **Category Organization:**
  - Create, edit, or delete book categories.
- **Order Management:**
  - Update order status.

## Technologies
- **Programming language:** `Java 17`
- **Spring Framework:** `Spring Boot v3.1.5`, `Spring Data`, `Spring Security v6.1.5` (Authentication using JWT token)
- **Database Management:** `MySQL 8.0.33`, `Hibernate`, `Liquibase v4.20.0`
- **Testing:** `JUnit 5`, `Mockito`, `TestContainers v1.18.0`
- **Deployment and Cloud Services:** `Docker 3.8`, `AWS`
- **Additional instruments:** `Maven`, `Lombok`, `Mapstruct`
- **Documentation:** `Swagger`

## Class diagram
![image](https://github.com/RedArtelerist/OnlineBookStore/assets/56000560/ca8912f7-cdc0-4cff-9af7-5b156a96e4cd)
### Entities
1. `User`: Contains user authentication and personal information.
2. `Role`: Represents user roles like admin or user.
3. `Book`: Represents books available in the store.
4. `Category`: Represents book categories.
5. `ShoppingCart`: Represents a user's shopping cart.
6. `CartItem`: Represents items in a user's shopping cart.
7. `Order`: Represents orders placed by users.
8. `OrderItem`: Represents items in a user's order.

## Endpoints 
### Authentication Controller
```
POST: /api/auth/register - Register new user in the system
POST: /api/auth/login - Login into the system
```
### Category management
```
GET: /api/categories - Look at all categories (USER/ADMIN)
GET: /api/categories/{id} - Look at specific category (USER/ADMIN)
POST: /api/categories - Add new category (ADMIN)
PUT: /api/categories/{id} - Edit category (ADMIN)
DELETE: /api/categories/{id} - Delete category (ADMIN)
GET: /api/categories/{id}/books - Look at all books by specific category (USER/ADMIN)
```
### Book management
```
GET: /api/books - Look at all books (USER/ADMIN)
GET: /api/books/{id} - Look at specific book (USER/ADMIN)
POST: /api/books/ - Add new book (ADMIN)
PUT: /api/books/{id} - Edit book (ADMIN)
DELETE: /api/books/{id} - Delete book (ADMIN)
GET: /api/books/search - Filter books by authors, title, and price (USER/ADMIN)
```
### Cart management
```
GET: /api/cart - Look at the cart (USER/ADMIN)
POST: /api/cart - Add item to the cart (USER/ADMIN)
PUT: /api/cart/cart-items/{cartItemId} - Edit a quantity of a specific cartItem (USER/ADMIN)
DELETE: /api/cart/cart-items/{cartItemId} - Remove cart item (USER/ADMIN)
```
### Order management
```
GET: /api/orders - Look at order history (USER/ADMIN)
POST: /api/orders - Create a new order by cart (USER/ADMIN)
GET: /api/orders/{orderId}/items - Look at specific order (USER/ADMIN)
GET: /api/orders/{orderId}/items/{itemId} - Look at specific order item in an order (USER/ADMIN)
PATCH: /api/orders/{id} - Edit order status of a specific order (ADMIN)
```

## Project Launch with Docker
1. Clone the repository from GitHub: `git clone https://github.com/RedArtelerist/OnlineBookStore.git`
2. Create a `.env` file with the necessary environment variables (as an example for filling - `.env.sample`). 
3. Run `mvn clean package` command
4. Install Docker: <a href="https://docs.docker.com/get-docker/">Get Docker</a>
5. Run `docker-compose build && docker-compose up` command to build and start the Docker containers
6. The application should be running at `http://localhost:8088`. You can test the operation of the application using swagger `http://localhost:8088/swagger-ui/index.html`.

## Project demonstration
### Video demo
<a href="https://www.loom.com/share/d875e1c1a6e649c4a7b765ca6001f79e">Link for short demo of this API</a>
### Try it yourself
Here you can test book store API http://ec2-54-162-136-159.compute-1.amazonaws.com/swagger-ui/index.html \
If you want access `ADMIN` role you can use next credentials: 
- email: `admin@gmail.com`
- password: `admin1`
 
