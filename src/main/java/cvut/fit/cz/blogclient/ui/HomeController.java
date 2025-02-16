package cvut.fit.cz.blogclient.ui;

import cvut.fit.cz.blogclient.data.AuthClient;
import cvut.fit.cz.blogclient.data.PostClient;
import cvut.fit.cz.blogclient.model.Login.LoginRequest;
import cvut.fit.cz.blogclient.model.Post.PostDto;
import cvut.fit.cz.blogclient.model.User.UserDto;
import cvut.fit.cz.blogclient.util.JwtTokenHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping()

public class HomeController {

    private final AuthClient authClient;
    private final PostClient postClient;
    private final JwtTokenHolder jth;

    public String jwtToken;

    @Autowired
    public HomeController(AuthClient authClient, PostClient postClient, JwtTokenHolder jth) {
        this.authClient = authClient;
        this.postClient = postClient;
        this.jth = jth;
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "Home/login";
    }

    @PostMapping("/login")
    public Mono<String> login(@ModelAttribute LoginRequest loginRequest, Model model) {
            return authClient.login(loginRequest).map(authResponse -> {

            if (authResponse.getToken() != null) {
                jwtToken = authResponse.getToken();
                jth.setToken(jwtToken);
                return "redirect:/users";
            }
                model.addAttribute("error", "Invalid username or password");
                return "Home/login";

            }).onErrorResume(e -> {
                System.out.println("ERROR: " + e.getMessage());
                model.addAttribute("error", "Invalid username or password");
                return Mono.just("Home/login");
            });
    }

    @PostMapping("/logout")
    public String logout(){
        authClient.logout();
        jth.setToken(null);
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String homePage(Model model) {
        String token = jth.getToken();
        if(token == null){
            return "redirect:/login";
        }
        model.addAttribute("user", authClient.getCurrentUser(token));
        model.addAttribute("blogs", authClient.getCurrentUsersBlogs(token));
        return "User/profile";
    }

    @GetMapping("/{path:[^\\.]+}**")
    public String forward(){
        return "redirect:/login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute(new UserDto());
        return "User/registration";
    }
    @PostMapping("/register")
    public Mono<String> registerUser(@ModelAttribute UserDto user, Model model) {
        return authClient.create(user)
                .thenReturn("redirect:/login")
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                        model.addAttribute("error", "Registration failed: User already exists!");
                        return Mono.just("User/registration");
                    }
                    model.addAttribute("error", "Unexpected error occurred!");
                    return Mono.just("User/registration");
                })
                .onErrorResume(Exception.class, e -> {
                    model.addAttribute("error", "Something went wrong! Please try again.");
                    return Mono.just("User/registration");
                });
    }
    @GetMapping()
    public String index(@ModelAttribute PostDto posts, Model model){
        String token = jth.getToken();
        model.addAttribute("posts", postClient.getAll(token));
        if(token == null){
            return "Home/indexnologin";
        }
        return "Home/index";
    }




}
