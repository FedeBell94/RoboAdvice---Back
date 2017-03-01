package it.uiip.digitalgarage.roboadvice.utils;

import java.util.*;

/**
 * Created by feder on 01/03/2017.
 */
public class AuthProvider {

    private static AuthProvider instance = null;
    private final Map<Integer, String> userTokenMap = new HashMap<>();

    private AuthProvider(){}

    public static synchronized AuthProvider getInstance(){
        if(instance == null){
            instance = new AuthProvider();
        }
        return instance;
    }

    public String setUserToken(int userId){
        String userToken = this.generateToken(userId);
        this.userTokenMap.put(userId, userToken);
        return userToken;
    }

    public void removeUserToken(int userId){
        this.userTokenMap.remove(userId);
    }

    public boolean checkToken(String userToken){
        Logger.debug(AuthProvider.class, "Check token called " + userToken);
        for(Map.Entry curr : userTokenMap.entrySet()){
            if(curr.getValue().equals(userToken)){
                return true;
            }
        }
        return false;
    }

    private String generateToken(int userId){
        return UUID.randomUUID().toString().toUpperCase() + "|" + userId;
    }
}
