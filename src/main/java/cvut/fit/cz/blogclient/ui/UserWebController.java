package cvut.fit.cz.blogclient.ui;

import cvut.fit.cz.blogclient.data.BlogClient;
import cvut.fit.cz.blogclient.data.PostClient;
import cvut.fit.cz.blogclient.data.UserClient;
import cvut.fit.cz.blogclient.model.User.UserDto;
import cvut.fit.cz.blogclient.util.JwtTokenHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
public class UserWebController {

    private final UserClient userClient;
    private final JwtTokenHolder jth;

    public UserWebController(UserClient userClient, HomeController hm, BlogClient blogClient, JwtTokenHolder jth) {
        this.userClient = userClient;
        this.jth = jth;
    }

    @GetMapping
    public String getUsers(Model model) {
        String token = jth.getToken();
        model.addAttribute("users", userClient.getAll(token));
        return "User/users";
    }

    @GetMapping("/id/{id}")
    public String getUser(@PathVariable("id") long id, Model model) {
        String token = jth.getToken();
        model.addAttribute("user", userClient.getById(id, token));
        model.addAttribute("blogs", userClient.getAllUsersBlogs(id, token));
        return "User/user";
    }

    @GetMapping("/{username}")
    public String getUserByUsername(@PathVariable("username") String username, Model model) {
        String token = jth.getToken();
        model.addAttribute("user", userClient.getByUsername(username, token));
        model.addAttribute("blogs", userClient.getAllUsersBlogsByUsername(username, token));
        return "User/user";
    }

    @PostMapping("/{id}/delete")
    public String deleteUser(@PathVariable("id") Long id, Model model) {
        String token = jth.getToken();
        model.addAttribute(userClient.deleteById(id, token));
        return "redirect:/users";
    }
}
