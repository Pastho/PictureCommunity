package de.hska.iwii.picturecommunity.controller;

import de.hska.iwii.picturecommunity.backend.dao.PictureDAO;
import de.hska.iwii.picturecommunity.backend.dao.UserDAO;
import de.hska.iwii.picturecommunity.backend.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Author: Thomas Pasberg
 * Created: 10.12.2014
 */

@Component
@Scope("session")
@Qualifier("registerController")
public class FriendController {

    private String searchString = "";
    private String resultString = "";

    private List<String> friends;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private PictureDAO pictureDAO;

    @Autowired
    @Qualifier("org.springframework.security.authenticationManager")
    protected AuthenticationManager authenticationManager;

    // ====================================================
    // Begin of setter and getter methods
    // ====================================================

    public String getResultString() {
        if (!searchString.isEmpty()) {

            User friendToAdd = userDAO.findUserByName(searchString);

            if (friendToAdd != null) {
                User user = getCurrentUser();
                if (!friendToAdd.equals(user)) {
                    if (!user.getFriendsOf().contains(friendToAdd)) {
                        user.getFriendsOf().add(friendToAdd);
                        userDAO.updateUser(user);
                        resultString = "Der Benutzer " + searchString + " wurde als Freund hinzugefügt!";
                    } else {
                        resultString = "Der Benutzer " + searchString + " ist bereits als Freund vorhanden.";
                    }
                } else {
                    resultString = "Sie können sich nicht selbst als Freund hinzufügen.";
                }
            } else {
                resultString = "Der Benutzer " + searchString + " konnte nicht als Freund hinzugefügt werden.";
            }
        }
        setSearchString("");
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }

    public String getSearchString() {
        return this.searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public List<String> getFriends() {

        User user = getCurrentUser();
        User possibleFriend = null;

        // reset current friends
        this.friends = new ArrayList<>();

        if (user != null) {
            Set<User> friendsOfUser = user.getFriendsOf();

            if (friendsOfUser.size() > 0) {
                // get current online users
                List<Object> principles = sessionRegistry.getAllPrincipals();

                for (Object principle : principles) {
                    if (principle instanceof User) {
                        possibleFriend = userDAO.findUserByMailaddress(((User) principle).getEmail());

                        // check if its a friend or not
                        if (friendsOfUser.contains(possibleFriend)) {
                            this.friends.add(possibleFriend.getName() + " ist Online");
                            friendsOfUser.remove(possibleFriend);
                        }
                    }
                } // end of principals

                for (User friend : friendsOfUser) {
                    this.friends.add(friend.getName() + " ist Offline");
                }
            }
        }

        return this.friends;
    }

// ====================================================
// End of setter and getter methods
// ====================================================

    /**
     * Returns the user object of the current user
     *
     * @return The user object of the current user
     */

    private User getCurrentUser() {
        User user = null;

        // read user from context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof User) {
            // create user object
            user = userDAO.findUserByMailaddress(((User) principal).getEmail());
        }

        return user;
    }
}