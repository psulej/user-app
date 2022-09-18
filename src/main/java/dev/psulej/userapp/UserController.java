package dev.psulej.userapp;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
public class UserController {

    private long idSequence = 1;
    private final List<User> users;

    public UserController() {
        List<User> users = new ArrayList<>();
        users.add(new User(idSequence++, "Jan", "Kowalski","jan32","jan32@yandex.com"));
        users.add(new User(idSequence++, "Jacek", "Kowalski","jacusss","jacko2213w@wp.pl"));
        users.add(new User(idSequence++, "Konwalia", "Kowalski","tesop","konwaliawalia@gmail.com"));
        users.add(new User(idSequence++, "Pawel", "Rak","rakk","pawlopaulo@wp.pl"));
        users.add(new User(idSequence++, "Krzysztof", "Kowal","cristopher","cristo@outlook.com"));
        users.add(new User(idSequence++, "Zbigniew", "Jeden","zibi1337","zibijabadibi@gmail.com"));
        users.add(new User(idSequence++, "Jan", "Konopnicki","jKonop2323","janekkonopnicki@gmail.com"));
        this.users = users;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<BasicUser> getUsers(
        @RequestParam(value = "firstName", required = false) String firstName,
        @RequestParam(value = "lastName", required = false) String lastName,
        @RequestParam(value = "sort", defaultValue = "id") String sort) {
        List<User> filteredUsers = new ArrayList<>(users);

        for (User user : users) {
            if (firstName != null) {
                if (!startsWithIgnoreCase(user.getFirstName(), firstName)) {
                    filteredUsers.remove(user);
                }
            }
            if (lastName != null) {
                if (!startsWithIgnoreCase(user.getLastName(), lastName)) {
                    filteredUsers.remove(user);
                }
            }
        }

        filteredUsers.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                if (sort.equalsIgnoreCase("firstName")) {
                    return o1.firstName.compareToIgnoreCase(o2.firstName);
                } else if (sort.equalsIgnoreCase("lastName")) {
                    return o1.lastName.compareToIgnoreCase(o2.lastName);
                } else if (sort.equalsIgnoreCase("id")) {
                    return o1.id.compareTo(o2.id);
                } else {
                    throw new IllegalArgumentException("Unrecognized parameter: " + sort);
                }
            }
        });

        List<BasicUser> basicUsers = new ArrayList<>();
        for (User user : filteredUsers) {
            BasicUser basicUser = new BasicUser(user.getId(), user.getFirstName(), user.getLastName());
            basicUsers.add(basicUser);
        }
        return basicUsers;
    }

    private boolean startsWithIgnoreCase(String str1, String str2) {
        return str1.toUpperCase().startsWith(str2.toUpperCase());
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET)
    public User getUser(@PathVariable long id) {
        for (User user : users) {
            if (user.id == id) {
                return user;
            }
        }
        throw new UserNotFoundException();
    }


    @RequestMapping(value = "/users", method = RequestMethod.POST)
    public User createUser(@RequestBody User newUser) {
        newUser.id = idSequence++;
        users.add(newUser);
        return newUser;
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
    public User updateUser(@PathVariable long id, @RequestBody User existingUser) {
        for(User user : this.users){
            if(user.id == id){
                user.firstName = existingUser.getFirstName();
                user.lastName = existingUser.getLastName();
                return user;
            }
        }

        throw new UserNotFoundException();
    }

    @RequestMapping(value = "/users/{id}" , method = RequestMethod.DELETE)
    public void deleteUser(@PathVariable long id){
        List<User> usersCopy = new ArrayList<>(this.users);
        for(User user : usersCopy){
            if(user.id == id){
                this.users.remove(user);
            }
        }
    }
}
