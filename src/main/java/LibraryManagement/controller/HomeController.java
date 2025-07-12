package LibraryManagement.controller;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {

            User user = (User) authentication.getPrincipal();
            String username = user.getUsername();

            model.addAttribute("username", username);
        } else {
            model.addAttribute("username", "Vizitor");
        }

        return "home";
    }
}