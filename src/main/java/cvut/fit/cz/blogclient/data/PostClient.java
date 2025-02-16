package cvut.fit.cz.blogclient.data;

import cvut.fit.cz.blogclient.model.Blog.BlogDto;
import cvut.fit.cz.blogclient.model.Post.PostDto;
import cvut.fit.cz.blogclient.util.JwtTokenHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class PostClient {
    private final WebClient postWebClient;

    public PostClient(@Value("${blog_backend}") String backendUrl, JwtTokenHolder jht) {
        postWebClient = WebClient.create(backendUrl + "/posts");
    }

    public Flux<PostDto> getAll(String jwtToken){
        return postWebClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(PostDto.class);
    }
    public Mono<String> create(PostDto postDto, Long blog_id, String jwtToken) {
        return postWebClient.post()
                .uri("/create/{id_blog}", blog_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .bodyValue(postDto)
                .retrieve()
                .bodyToMono(String.class);
    }

    public Mono<PostDto> getById(Long id, String jwtToken){
        return postWebClient.get()
                .uri("/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(PostDto.class);
    }
    public Flux<BlogDto> getBlogsByPostId(Long id, String jwtToken){
        return postWebClient.get()
                .uri("/{id}/blogs", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(BlogDto.class);
    }
    public Mono<String> addPostToBlog(Long post_id, Long blog_id, String jwtToken){
        return postWebClient.post()
                .uri("/{id_post}/addto/{id_blog}", post_id, blog_id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(String.class);
    }
    public Mono<String> deleteById(Long id, String jwtToken){
        return postWebClient.post()
                .uri("/{id_post}/delete", id)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(String.class);
    }

}
