# Car Rental Management System (Marigold Rentals)

A college mini-project built with Java Servlets, JSP, XML storage (DOM parser), Maven and Apache Tomcat 10.1.
No database and no framework: all data lives in three XML files.

## User Accounts

You can register new customer accounts directly from the Register page. Default administrative accounts can be configured within your local data storage (`users.xml`).

## Setup (VS Code + Tomcat 10.1)

**1. Install the tools**
- JDK 25 LTS or newer (check with `java -version`)
- Apache Maven 3.9+ (check with `mvn -version`)
- Apache Tomcat **10.1.x** (download the zip, extract it, for example to `C:\tomcat10`). Tomcat 9 will not work because this project uses `jakarta.*` packages.
- VS Code with the **Extension Pack for Java**. Optional: **Community Server Connectors** (Red Hat) to start Tomcat from inside VS Code.

**2. Build the WAR file**

Open the `car-rental` folder in VS Code, open a terminal (Ctrl + `) and run:

```
mvn clean package
```

This creates `target/car-rental.war`.

**3. Deploy and run (choose one)**

*Option A: manual*
1. Copy `target/car-rental.war` into `<tomcat>/webapps/`.
2. Start Tomcat: `<tomcat>\bin\startup.bat` (Windows) or `<tomcat>/bin/startup.sh` (Linux/Mac).
3. Open http://localhost:8080/car-rental/
4. Stop Tomcat with `shutdown.bat` / `shutdown.sh`.

*Option B: from VS Code (Community Server Connectors)*
1. Open the **Servers** view, click **+ Create New Server**, choose your extracted Tomcat 10.1 folder.
2. Right-click the server, **Add Deployment**, pick `target/car-rental.war`.
3. Right-click the server, **Start Server**, then right-click again and choose **Show in Browser** (or open http://localhost:8080/car-rental/).
4. After changing code, run `mvn clean package` again, then right-click the server, **Publish Server (Full)**.

## Where is the data stored?

The XML files are copied on first run to a folder in your user home:

- Windows: `C:\Users\<you>\car-rental-data\`
- Linux/Mac: `~/car-rental-data/`

Because the files live outside the WAR, **data survives server restarts and re-deployments**.
To reset to the original sample data, stop Tomcat and delete the `car-rental-data` folder.

## Project structure

```
car-rental/
├── pom.xml                              Maven build file (WAR packaging, Jakarta + JSTL dependencies)
└── src/main/
    ├── java/com/carrental/
    │   ├── model/    User (abstract), Customer, Admin, Car, Booking
    │   ├── dao/      GenericDAO (interface), UserDAO, CarDAO, BookingDAO
    │   ├── servlet/  LoginServlet, RegisterServlet, CarServlet, BookingServlet, AdminServlet
    │   └── util/     XMLUtil, FormUtil, DataException
    ├── resources/data/   users.xml, cars.xml, bookings.xml   (sample data, copied on first run)
    └── webapp/
        ├── index.jsp, login.jsp, register.jsp
        ├── css/style.css, js/app.js, images/
        └── WEB-INF/
            ├── web.xml
            └── views/   cars, car-details, booking, my-bookings,
                         admin-dashboard, manage-cars, manage-bookings (.jsp)
                         header / footer / messages / car-card / car-image (.jspf fragments)
```

The pages inside `WEB-INF/views` can only be reached through a servlet, so a page that needs data is never opened without it.

## What each file does

**Model** (plain classes with private fields, getters and setters)
- `User`: abstract base class (id, name, e-mail, password, phone) with an abstract `getRole()`.
- `Customer`, `Admin`: extend `User` and override `getRole()`.
- `Car`: one car (name, type, fuel, transmission, seats, price per day, colour, availability).
- `Booking`: one booking. The constructor copies car details and calls `calculateTotal()` (days x price per day). `isCancellable()` says whether the customer may still cancel.

**DAO** (the only classes that touch XML)
- `GenericDAO<T>`: interface with `add`, `getAll`, `getById`, `update`, `delete`.
- `CarDAO`, `BookingDAO`, `UserDAO`: implement the interface. Each has two private helpers: `fromElement()` (XML to object) and `fillElement()` (object to XML). Extras: `getAvailable()`, `getByUserId()`, `login()`, `getByEmail()`.

**Util**
- `XMLUtil`: loads and saves XML with the DOM parser, finds elements by id, reads/writes element text, creates the next id (C009, B1006...).
- `FormUtil`: reads form fields without nulls or extra spaces.
- `DataException`: our own checked exception for file problems.

**Servlets**
| Servlet | URLs | Job |
|---|---|---|
| `LoginServlet` | `/login`, `/logout` | check credentials, store `user` in the session, log out |
| `RegisterServlet` | `/register` | validate the form, create a `Customer` |
| `CarServlet` | `/home`, `/cars`, `/car-details` | home page, car list with type filter, car details |
| `BookingServlet` | `/booking`, `/my-bookings`, `/cancel-booking` | book a car, list own bookings, cancel |
| `AdminServlet` | `/admin/dashboard`, `/admin/cars`, `/admin/bookings` (+ save, delete, status) | all admin work; rejects anyone who is not an `Admin` |

## How a booking works

1. Customer opens a car and clicks **Book this car** (`GET /booking?carId=C001`).
2. The form shows the price; JavaScript updates the total while the days are typed.
3. Submitting (`POST /booking`) creates `new Booking(userId, name, car, days)`, which calculates `totalAmount = days x pricePerDay` and sets the status to Pending.
4. `BookingDAO.add()` generates the booking id and appends the booking to `bookings.xml`.
5. The servlet redirects to the confirmation page, which shows the booking ID, customer name, car, days, price per day, total and status.
6. The admin approves or rejects it under **Manage bookings**. The customer can cancel while it is Pending or Approved.

## Java concepts and where to find them (for your viva)

| Concept | Where |
|---|---|
| Classes and objects | `Car`, `Booking`; objects created in the DAOs and servlets |
| Encapsulation | private fields with getters/setters in every model class |
| Inheritance | `Customer` and `Admin` extend `User` |
| Abstraction | abstract class `User` with abstract `getRole()` |
| Interface | `GenericDAO<T>`, implemented by three DAOs (also uses generics) |
| Method overriding | `getRole()` in `Customer`/`Admin`, `toString()` in `Admin` |
| Polymorphism | `UserDAO.fromElement()` returns `User` that is really an `Admin` or `Customer`; `instanceof Admin` in the servlets |
| Exception handling | custom `DataException`; try/catch in `XMLUtil` and the servlets |
| Collections | `ArrayList<Car>`, `List<Booking>` in DAOs; `Collections.reverse()` for newest-first lists |
| Servlets | `doGet` / `doPost`, `@WebServlet`, redirect vs forward |
| JSP | JSTL tags (`c:forEach`, `c:if`, `c:choose`) and EL (`${car.name}`) |
| Session management | `session.setAttribute("user", user)` on login, `session.invalidate()` on logout |
| XML parsing | DOM parser in `XMLUtil` (`DocumentBuilder`, `Transformer`) |
| MVC | Model = `model` + `dao`, View = JSPs, Controller = servlets |
| Thread safety | DAOs lock on `XMLUtil.LOCK` so two requests never write at once |

## Notes and limits (it is a college project)

- Passwords are stored as plain text in `users.xml` to keep the code simple. A real system would store hashes.
- There is no date-based availability check. The admin controls availability with the "Available for booking" tick box.
- Car pictures are drawn in the car's colour so the project works offline. To use a real photo, copy the file into `src/main/webapp/images/` and type its file name in **Manage cars**.
- The page font (Manrope) loads from Google Fonts. Without internet the browser uses a similar system font.

## Troubleshooting

| Problem | Fix |
|---|---|
| 404 at `/car-rental/` | Wait a few seconds for Tomcat to unpack the WAR, check that `webapps/car-rental.war` exists, and that you use Tomcat 10.1 |
| `jakarta.tags.core` or taglib error | JSTL is missing from the WAR. Run `mvn clean package` again and check that `WEB-INF/lib` in the WAR has the two JSTL jars |
| `javax.servlet` errors | You are using Tomcat 9 or older. Use Tomcat 10.1 |
| Port 8080 already in use | Stop the other program, or change the port in `<tomcat>/conf/server.xml` |
| `mvn` not recognised | Add Maven's `bin` folder to PATH and reopen the terminal |
| Want the original data back | Stop Tomcat and delete the `car-rental-data` folder in your home directory |
