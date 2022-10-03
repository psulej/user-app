package dev.psulej.userapp;

import java.sql.*;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    private NamedParameterJdbcTemplate jdbcTemplate;

    public UserController(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public PaginationResponse<BasicUser> getUsers(
        @RequestParam(value = "firstName", required = false) String firstName,
        @RequestParam(value = "lastName", required = false) String lastName,
        @RequestParam(value = "sort", defaultValue = "id") String sort,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "5") int size
        ) {
        String sql = "SELECT id, first_name, last_name, login, email FROM users WHERE 1 = 1";
        String countSql = "SELECT count(*) FROM users WHERE 1 = 1";

        Map<String, Object> parameters = new HashMap<>();

        if (firstName != null) {
            sql += " AND lower(first_name) LIKE lower(:firstName)";
            countSql += " AND lower(first_name) LIKE lower(:firstName)";
            parameters.put("firstName", firstName + '%');
        }
        if (lastName != null) {
            sql += " AND lower(last_name) LIKE lower(:lastName)";
            countSql += " AND lower(last_name) LIKE lower(:lastName)";
            parameters.put("lastName", lastName + '%');
        }

        String sortColumnName = getOrderByParameter(sort);
        sql += " ORDER BY " + sortColumnName;
        sql += " LIMIT " + size;
        sql += " OFFSET  " + page * size;

        RowMapper<BasicUser> rowMapper = new RowMapper<>() {
            @Override
            public BasicUser mapRow(ResultSet rs, int rowNum) throws SQLException {
                long id = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                return new BasicUser(id, firstName, lastName);
            }
        };
        List<BasicUser> users = jdbcTemplate.query(sql, parameters, rowMapper);

        RowMapper<Long> countRowMapper = new RowMapper<>() {
            @Override
            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong(1);
            }
        };
        Long totalItems = jdbcTemplate.queryForObject(countSql, parameters, countRowMapper);

        long totalPages = (long) (Math.ceil(totalItems / (size * 1.0)));
        int currentPage = page;
        PaginationResponse<BasicUser> response = new PaginationResponse<>(totalItems, totalPages, currentPage,users);
        return response;
    }

    private static String getOrderByParameter(String sort) {
        Map<String, String> orderByColumns = new HashMap<>();
        orderByColumns.put("id", "id");
        orderByColumns.put("firstName", "first_name");
        orderByColumns.put("lastName", "last_name");
        orderByColumns.put("email", "email");
        orderByColumns.put("login", "login");

        String sortColumnName = orderByColumns.getOrDefault(sort, "id");
        return sortColumnName;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable long id) {;
        String sql = "SELECT u.id, u.first_name, u.last_name, u.login, u.email, ua.country, ua.city, ua.street, ua.house_number, ua.zip_code " +
                "FROM users u " +
                "JOIN user_addresses ua on u.id = ua.user_id " +
                "WHERE id = :id";
        Map<String,Object> parameters = new HashMap<>();
        parameters.put("id",id);

        RowMapper<User> userRowMapper = new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                long id = rs.getLong("id");
                String firstName = rs.getString("first_name");
                String lastName = rs.getString("last_name");
                String login = rs.getString("login");
                String email = rs.getString("email");
                String country = rs.getString("country");
                String city = rs.getString("city");
                String street = rs.getString("street");
                String houseNumber = rs.getString("house_number");
                String zipCode = rs.getString("zip_code");

                Address address = new Address(country,city,street,houseNumber,zipCode);
                return new User(id,firstName,lastName,login,email,address);
            }
        };

        User user = jdbcTemplate.queryForObject(sql, parameters, userRowMapper);
        return user;
    }

    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User createUser(@RequestBody User newUser) {

        boolean emailExists = validateEmail(newUser.email);
        boolean loginExists = validateLogin(newUser.login);

        if (emailExists) {
            throw new EmailExistsException();
        }

        if (loginExists) {
            throw new EmailExistsException();
        }

        long userId = insertUser(newUser);
        Address address = insertUserAdress(newUser, userId);

        return new User(
                userId,
                newUser.firstName,
                newUser.lastName,
                newUser.login,
                newUser.email,
                address
        );
    }

    private boolean validateEmail(String email) {
        boolean emailExists = false;

        String sql = "select exists(select 1 from users where email = :email)";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("email", email);

        RowMapper<Boolean> rowMapper = new RowMapper<>() {
            @Override
            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                boolean isEmailExisting = rs.getBoolean(1);
                return isEmailExisting;
            }
        };

        emailExists = jdbcTemplate.queryForObject(sql, parameters, rowMapper);
        return emailExists;
    }

    private boolean validateLogin(String login) {
        boolean loginExists = false;

        String sql = "select exists(select 1 from users where login = :login)";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("login", login);

        RowMapper<Boolean> rowMapper = new RowMapper<>() {
            @Override
            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                boolean isLoginExisting = rs.getBoolean(1);
                return isLoginExisting;
            }
        };

        loginExists = jdbcTemplate.queryForObject(sql, parameters, rowMapper);
        return loginExists;
    }

    private boolean validateEmailForUpdate(long id,String email) {
        boolean emailExists = false;

        String sql = "select exists(select 1 from users where email = :email AND id != :id)";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("email", email);
        parameters.put("id", id);

        RowMapper<Boolean> rowMapper = new RowMapper<>() {
            @Override
            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                boolean isEmailExisting = rs.getBoolean(1);
                return isEmailExisting;
            }
        };

        emailExists = jdbcTemplate.queryForObject(sql, parameters, rowMapper);
        return emailExists;
    }

    private boolean validateLoginForUpdate(long id,String login) {
        boolean loginExists = false;

        String sql = "select exists(select 1 from users where login = :login AND id != :id)";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("login", login);
        parameters.put("id", id);

        RowMapper<Boolean> rowMapper = new RowMapper<>() {
            @Override
            public Boolean mapRow(ResultSet rs, int rowNum) throws SQLException {
                boolean isLoginExisting = rs.getBoolean(1);
                return isLoginExisting;
            }
        };

        loginExists = jdbcTemplate.queryForObject(sql, parameters, rowMapper);
        return loginExists;
    }


    private Address insertUserAdress(User newUser, long userId) {
        String sql = "INSERT INTO user_addresses(user_id, country, city, street, house_number, zip_code) " +
                "VALUES(:id, :country, :city, :street, :houseNumber, :zipCode)";
        HashMap<String, Object> parameters = new HashMap<>();

        parameters.put("id", userId);
        Address address = newUser.address;
        parameters.put("country", address.country);
        parameters.put("city", address.city);
        parameters.put("street", address.street);
        parameters.put("houseNumber", address.houseNumber);
        parameters.put("zipCode", address.zipCode);

        jdbcTemplate.update(sql, parameters);
        return address;
    }

    private long insertUser(User newUser) {
        String sql =
                "INSERT INTO users(id, first_name, last_name, login, email)" +
                " VALUES (nextval('users_seq'), :firstName, :lastName, :login, :email)";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", newUser.firstName);
        parameters.put("lastName", newUser.lastName);
        parameters.put("login" , newUser.login);
        parameters.put("email" , newUser.email);
        KeyHolder key = new GeneratedKeyHolder(); // zwraca id dla usera
        jdbcTemplate.update(sql, new MapSqlParameterSource(parameters), key, new String[] { "id" });
        long userId = key.getKey().longValue();
        return userId;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable long id, @RequestBody User existingUser) {

        boolean emailExists = validateEmailForUpdate(id,existingUser.email);
        boolean loginExists = validateLoginForUpdate(id,existingUser.login);

        if (emailExists) {
            throw new EmailExistsException();
        }

        if (loginExists) {
            throw new EmailExistsException();
        }

        setUser(id,existingUser);
        String sql = "UPDATE user_addresses SET country = :country, city = :city, street = :street, house_number = :houseNumber, zip_code = :zipCode WHERE user_id = :id";
        HashMap<String, Object> parameters = new HashMap<>();
        Address address = existingUser.address;
        parameters.put("id",id);
        parameters.put("country",existingUser.address.country);
        parameters.put("city",existingUser.address.city);
        parameters.put("street",existingUser.address.street);
        parameters.put("houseNumber",existingUser.address.houseNumber);
        parameters.put("zipCode",existingUser.address.zipCode);
        jdbcTemplate.update(sql, parameters);
        return new User(
                id,
                existingUser.firstName,
                existingUser.lastName,
                existingUser.login,
                existingUser.email,
                existingUser.address
        );
    }

    private void setUser(long id, User existingUser) {
        String sql = "UPDATE users SET first_name = :firstName, last_name = :lastName, login = :login, email = :email WHERE id = :id";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("firstName", existingUser.firstName);
        parameters.put("lastName", existingUser.lastName);
        parameters.put("id", id);
        parameters.put("email", existingUser.email);
        parameters.put("login", existingUser.login);
        jdbcTemplate.update(sql, new MapSqlParameterSource(parameters));
    }

    @RequestMapping(value = "/users/{id}" , method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable long id){
        deleteUserAddresses(id);
        deleteUsers(id);
    }

    @ExceptionHandler(EmailExistsException.class)
    private ResponseEntity<?> handleEmailExistsException(EmailExistsException e) {
        return new ResponseEntity<>("EMAIL_EXISTS", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LoginExistsException.class)
    private ResponseEntity<?> handleLoginExistsException(EmailExistsException e) {
        return new ResponseEntity<>("LOGIN_EXISTS", HttpStatus.BAD_REQUEST);
    }

    private void deleteUserAddresses(long id) {
        String sql = "DELETE FROM user_addresses WHERE user_id = :userId";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("userId", id);
        jdbcTemplate.update(sql, parameters);
    }

    private void deleteUsers(long id) {
        String sql = "DELETE FROM users WHERE id = :id";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("id", id);
        jdbcTemplate.update(sql, parameters);
    }
}
