package it.uiip.digitalgarage.roboadvice.utils;

import it.uiip.digitalgarage.roboadvice.persistence.model.User;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to keep in memory all the user logged with the respective token. For each user is provided only
 * one user token which is unique.
 * This class in implemented with Singleton pattern
 */
public class AuthProvider {

    /**
     * The unique instance of this class.
     */
    private static AuthProvider instance = null;

    /**
     * Here all the user token and users are stored.
     */
    private final Map<String, Integer> userTokenMap = new HashMap<>();

    /**
     * Private constructor of the class. Singleton pattern.
     */
    private AuthProvider() {}

    /**
     * Get instance method of Singleton pattern.
     *
     * @return The unique instance of this class.
     */
    public static synchronized AuthProvider getInstance() {
        if (instance == null) {
            instance = new AuthProvider();
        }
        return instance;
    }

    public String bindUserToken(User user) {
        // Remove the previous token if already set and never logged out
        removeUserToken(user);
        // Generate and insert the new token
        String userToken = this.generateToken(user);
        this.userTokenMap.put(userToken, user.getId());
        return userToken;
    }

    public void removeUserToken(User user) {
        for (Map.Entry<String, Integer> curr : userTokenMap.entrySet()) {
            if (curr.getValue() == user.getId()) {
                userTokenMap.remove(curr.getKey());
            }
        }
    }

    /**
     * This method checks if an user token is bind with some user.
     *
     * @param userToken
     *         The token of the user to check.
     *
     * @return The id of the user binded with the user token passed, null otherwise.
     */
    public Integer checkUserToken(String userToken) {
        Logger.debug(AuthProvider.class, "Check user token called " + userToken);
        return userTokenMap.get(userToken);
    }

    private String generateToken(User user) {
        //return UUID.randomUUID().toString().toUpperCase() + "|" + user.getId();
        return String.valueOf(user.getId());
    }
}
