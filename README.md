# fit-build

Software written in JavaFX to manage fitness members and their memberships in a virtual fitness gym.

## Tech stack

- Java 21
- JavaFX 21.0.2 (FXML views)
- Maven
- MySQL (via MySQL Connector/J 8.4.0)

## Architecture

- `gym.Main` — application entry point, loads `views/main.fxml`
- `gym.models` — `Member`, `Membership`
- `gym.dao` — `Database` (singleton MySQL connection), `MemberDAO`, `MembershipDAO`
- `gym.controllers.MainController` — wires the UI to the DAOs
- `views/main.fxml` + `styles/style.css` — UI: Members, Memberships, and Statistics tabs

## Features

- Add / update / delete members
- Add / renew / cancel memberships, with auto-computed end dates (monthly, quarterly, annual)
- Statistics dashboard: total members, active subscriptions, revenue, breakdown by period
- CSV export of members and memberships

## Database setup

The app connects to a local MySQL server. Create the database and tables with the provided script:

```sql
-- run init_db.sql against your MySQL server
```

Then create `src/main/resources/db.properties` (gitignored, not committed) with:

```properties
db.url=jdbc:mysql://localhost:3306/Gym
db.user=root
db.password=root123
```

Adjust the URL/user/password to match your local MySQL instance.

## Running

```bash
mvn clean compile javafx:run
```

## Testing

1. Start MySQL and run `init_db.sql` to create the `Gym` schema/tables.
2. Launch the app with `mvn javafx:run`.
3. Add a member, then select it and add a membership — verify the end date matches the chosen period.
4. Try renewing/cancelling a membership and check the Statistics tab updates accordingly.
5. Export to CSV and confirm the file content is correct.
6. Restart the app to confirm data persists in MySQL.
