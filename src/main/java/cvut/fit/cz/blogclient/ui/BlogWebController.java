package cvut.fit.cz.blogclient.ui;

import cvut.fit.cz.blogclient.data.AuthClient;
import cvut.fit.cz.blogclient.data.BlogClient;
import cvut.fit.cz.blogclient.data.PostClient;
import cvut.fit.cz.blogclient.model.Blog.BlogDto;
import cvut.fit.cz.blogclient.model.Post.PostDto;
import cvut.fit.cz.blogclient.model.User.UserDto;
import cvut.fit.cz.blogclient.util.JwtTokenHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Controller
@RequestMapping("/blogs")
public class BlogWebController {

    private final BlogClient blogClient;
    private final AuthClient authClient;
    private final PostClient postClient;
    private final JwtTokenHolder jth;

    public BlogWebController(BlogClient blogClient, AuthClient authClient, PostClient postClient, HomeController hm, JwtTokenHolder jth) {
        this.blogClient = blogClient;
        this.authClient = authClient;
        this.postClient = postClient;
        this.jth = jth;
    }

    @GetMapping
    public String getBlogs(Model m){
        m.addAttribute("blogs", blogClient.getAllBlogsWithPosts(jth.getToken()));
        return "Blog/blogs";
    }
    @GetMapping("/{id}")
    public Mono<String> getBlog(@PathVariable("id") long id, Model model) {
        String token = jth.getToken();

        Mono<UserDto> userMono = authClient.getCurrentUser(token);
        Mono<BlogDto> blogMono = blogClient.getById(id, token);
        Flux<PostDto> postsFlux = blogClient.getPostsByBlogId(id, token);

        return Mono.zip(userMono, blogMono, postsFlux.collectList())
                .map(tuple -> {
                    UserDto user = tuple.getT1();
                    BlogDto blog = tuple.getT2();
                    List<PostDto> posts = tuple.getT3();

                    model.addAttribute("user", user);
                    model.addAttribute("blog", blog);
                    model.addAttribute("posts", posts);

                    return "Blog/blog";
                });
    }
    @PostMapping("/create")
    public String createBlog(@ModelAttribute BlogDto blogDto, Model model){
        String token = jth.getToken();
        model.addAttribute(blogClient.create(blogDto, token));
        return "redirect:/profile";
    }
    @GetMapping("/create")
    public String showCreate(Model model){
        String token = jth.getToken();
        if(token == null){
            return "redirect:/login";
        }
        model.addAttribute(new BlogDto());
        return "Blog/createBlog";
    }

    @GetMapping("/{id}/create")
    public String ShowCreatePost(@PathVariable("id") long id, Model model){
        System.out.println(id);
        model.addAttribute(new PostDto());
        return "Post/createPost";
    }

    @PostMapping("/{id}/create")
    public String createPost(@PathVariable("id") long id, Model model, @ModelAttribute PostDto post){
        String token = jth.getToken();
        model.addAttribute(postClient.create(post, id, token));
        return "redirect:/profile";
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable("id") long id, Model model){
        String token = jth.getToken();
        System.out.println("ALERT");
        model.addAttribute(blogClient.deleteById(id, token));
        return "redirect:/profile";
    }

}
