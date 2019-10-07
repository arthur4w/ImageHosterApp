package ImageHoster.service;

import ImageHoster.model.User;
import ImageHoster.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    //Call the registerUser() method in the UserRepository class to persist the user record in the database
    public boolean registerUser(User newUser) {

    	//Checking if the password is valid or not
        if (this.isPasswordValid(newUser.getPassword())){
            userRepository.registerUser(newUser);
            return true;
        }
        else return false;
    }

    //Since we did not have any user in the database, therefore the user with username 'upgrad' and password 'password' was hard-coded
    //This method returned true if the username was 'upgrad' and password is 'password'
    //But now let us change the implementation of this method
    //This method receives the User type object
    //Calls the checkUser() method in the Repository passing the username and password which checks the username and password in the database
    //The Repository returns User type object if user with entered username and password exists in the database
    //Else returns null
    public User login(User user) {
        User existingUser = userRepository.checkUser(user.getUsername(), user.getPassword());
        if (existingUser != null) {
            return existingUser;
        } else {
            return null;
        }
    }

    //Checking if the password entered meets the desired criterion of complexities or not
    public boolean isPasswordValid(String password){

        boolean isValid = false;

        //Making sure it has got at least one number in it
        Pattern numbers = Pattern.compile( "[0-9]" );
        Matcher numbersMatcher = numbers.matcher( password );

        //Making sure it has got at least one alphabet of any case in it
        Pattern alphabets = Pattern.compile( "[a-zA-Z]" );
        Matcher alphabetsMatcher = alphabets.matcher( password );

        //Making sure it has got at least one special character din it
        Pattern special = Pattern.compile( "[^a-z A-Z0-9]" );
        Matcher specialMatcher = special.matcher( password );

        boolean theNumber = numbersMatcher.find();
        boolean theAlphabet = alphabetsMatcher.find();
        boolean theSpecialChar = specialMatcher.find();

        //If all criteria are met, we're good to go
        if(theAlphabet && theNumber && theSpecialChar)
            isValid = true;
        return isValid;
    }
}