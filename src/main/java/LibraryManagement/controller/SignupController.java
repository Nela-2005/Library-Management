package LibraryManagement.controller;
import LibraryManagement.entity.User;
import LibraryManagement.repository.UserRepository;
import LibraryManagement.service.SignupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
public class SignupController {

    private final PasswordEncoder passwordEncoder;
    private final SignupService signupService;

    public SignupController(PasswordEncoder passwordEncoder, SignupService signupService) {
        this.passwordEncoder = passwordEncoder;
        this.signupService = signupService;
    }

    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user, Model model) {
        System.out.println("Saving user");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRoles(Collections.singletonList("ROLE_USER"));
        SignupService.SignupResponse signupResponse = signupService.saveUser(user);
        System.out.println(signupResponse);
        if (signupResponse.isSuccess()){
            model.addAttribute("successMessage", "Regjistrimi u krye me sukses! Mund të logoheni.");
            return "login";
        }else {
            model.addAttribute("errorMessage", signupResponse.getMessage());
            return "signup";
        }
    }
}
