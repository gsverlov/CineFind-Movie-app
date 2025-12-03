package controllers;

import exceptions.UserNotFoundException;
import exceptions.UsernameTakenException;
import exceptions.WrongPasswordException;
import models.User;

import java.util.Map;
import java.util.logging.Logger;

public class LoginManager {
    private User loggedInUser;
    private static final Logger logger = Logger.getLogger(LoginManager.class.getName());

    public void loginInteractor(String username, String password) throws UserNotFoundException, WrongPasswordException {
        if (!User.checkValidUser(username)) {
            throw new UserNotFoundException();
        }
        if (!checkPassword(username, password)) {
            throw new WrongPasswordException();
        }
        this.loggedInUser = User.getUser(username);
    }

    public void logout(){
        this.loggedInUser = null;
    }

    public boolean isLoggedIn(){
        return (this.loggedInUser != null);
    }

    public boolean checkPassword(String username, String password){
        Map<String, String> passwordMap = User.getUserPasswordMap();
        return passwordMap.get(username).equals(password);
    }

    public void createAccount(String username, String password) {
        try {
            new User(username, password);
        } catch (UsernameTakenException e) {
            boolean flag = true;
        }
    }

    public User getLoggedInUser(){
        return loggedInUser;
    }
}
