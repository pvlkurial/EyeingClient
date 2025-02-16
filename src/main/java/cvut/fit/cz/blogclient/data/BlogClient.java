package cvut.fit.cz.blogclient.data;

import cvut.fit.cz.blogclient.model.Blog.BlogDto;
import cvut.fit.cz.blogclient.model.Post.PostDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Component
public class BlogClient {
    private static final String ONE_URI = "/{id}";
    private final WebClient blogWebClient;

    public BlogClient(@Value("${blog_backend}") String backendUrl) {
        this.blogWebClient = WebClient.create(backendUrl + "/blogs");
    }

    public Mono<String> create(BlogDto blog, String jwtToken) {
        return blogWebClient.post()
                .uri("/create")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .bodyValue(blog)
                .retrieve()
                .bodyToMono(String.class);
    }
    public Flux<BlogDto> getAll(String jwtToken){
        return blogWebClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(BlogDto.class);
    }

    public Mono<BlogDto> getById(Long id, String jwtToken) {
        return blogWebClient.get()
                .uri(ONE_URI, id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(BlogDto.class);
    }

    public Flux<PostDto> getPostsByBlogId(Long id, String jwtToken){
        return blogWebClient.get()
                .uri(ONE_URI + "/posts", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(PostDto.class);
    }
    public Mono<String> deleteById(Long id, String jwtToken){
        return blogWebClient.post()
                .uri("/{id}/delete", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Flux<BlogDto> getAllBlogsWithPosts(String jwtToken){
        return blogWebClient.get()
                .uri("/withposts")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(BlogDto.class);
    }

}
