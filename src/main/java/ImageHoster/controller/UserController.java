package ImageHoster.controller;

import ImageHoster.model.Image;
import ImageHoster.model.User;
import ImageHoster.model.UserProfile;
import ImageHoster.service.ImageService;
import ImageHoster.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpSession;
import java.util.List;


@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    //Controller for the Registration page
    @RequestMapping("users/registration")
    public String registration(Model model) {
        User user = new User();
        UserProfile profile = new UserProfile();
        user.setProfile(profile);
        model.addAttribute("User", user);
        return "users/registration";
    }

    //Controller for the Registration of a new user with a POST request
    @RequestMapping(value = "users/registration", method = RequestMethod.POST)
    public String registerUser(User user, Model model) {

    	//If the user registers successfully complying with the complexities of the password
    	//Redirection to the login page
        if(this.userService.registerUser(user))
            return "redirect:/users/login";
        
        //If the user fails to set up a password of required complexities
        //Redirection to the Registration page
        else {
            String error = "Password must contain atleast 1 alphabet, 1 number & 1 special character";
            model.addAttribute("passwordTypeError", error);
            model.addAttribute("User", user);
            return "users/registration";
        }
    }

    //Controller for the Login page of a successfully registered user
    @RequestMapping("users/login")
    public String login() {
        return "users/login";
    }

    //Controller for the Login page of a user with POST request
    @RequestMapping(value = "users/login", method = RequestMethod.POST)
    
    //Checking if the user already exists as a registered user
    public String loginUser(User user, HttpSession session) {
        User existingUser = userService.login(user);
        
        //If the user is an existing one, redirect to the homepage
        if (existingUser != null) {
            session.setAttribute("loggeduser", existingUser);
            return "redirect:/images";
        }
        
        //If the user doesn't exits beforehand, redirect back to the login page
        else {
            return "users/login";
        }
    }

    //Controller for logging the user out 
    @RequestMapping(value = "users/logout", method = RequestMethod.POST)
    public String logout(Model model, HttpSession session) {
        session.invalidate();
        List<Image> images = imageService.getAllImages();
        model.addAttribute("images", images);
        return "index";
    }
}