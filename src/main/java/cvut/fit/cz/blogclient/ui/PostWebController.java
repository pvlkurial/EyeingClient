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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/posts")
public class PostWebController {

    private final PostClient postClient;
    private final AuthClient authClient;
    private final JwtTokenHolder jth;

    public PostWebController(PostClient postClient, BlogClient blogClient, AuthClient authClient, JwtTokenHolder jth) {
        this.postClient = postClient;
        this.authClient = authClient;
        this.jth = jth;
    }

    @GetMapping("/{id}")
    public Mono<String> getBlog(@PathVariable("id") long id, Model model) {
        String token = jth.getToken();
        Mono<UserDto> userMono = authClient.getCurrentUser(token);
        Mono<PostDto> postMono = postClient.getById(id, token);
        Flux<BlogDto> blogsMono = postClient.getBlogsByPostId(id, token);
        Flux<BlogDto> myBlogsMono = authClient.getCurrentUsersBlogs(token);

        return Mono.zip(
                userMono,
                postMono,
                blogsMono.collectList(),
                myBlogsMono.collectList()
        ).map(tuple -> {
            model.addAttribute("user", tuple.getT1());
            model.addAttribute("post", tuple.getT2());
            model.addAttribute("blogs", tuple.getT3());
            model.addAttribute("myBlogs", tuple.getT4());

            return "Post/post"; // Return the view name
        });
    }

    @PostMapping("/{post_id}/addto/{blog_id}")
    public String addPostToBlog(@PathVariable("post_id") long post_id, @PathVariable("blog_id") long blog_id, Model model){
        String token = jth.getToken();
        //postClient.addPostToBlog(post_id, blog_id, token);
        model.addAttribute("newAttribute", postClient.addPostToBlog(post_id, blog_id, token));
        return "redirect:/blogs/{blog_id}";
    }

    @PostMapping("/{post_id}/delete")
    public String deletePost(@PathVariable("post_id") long post_id, Model model){
        String token = jth.getToken();
        model.addAttribute(postClient.deleteById(post_id, token));
        return "redirect:/profile";
    }




}
