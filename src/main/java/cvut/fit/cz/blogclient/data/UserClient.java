package cvut.fit.cz.blogclient.data;

import cvut.fit.cz.blogclient.model.Blog.BlogDto;
import cvut.fit.cz.blogclient.model.User.UserDto;
import cvut.fit.cz.blogclient.util.JwtTokenHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Component
public class UserClient {
    private static final String ONE_URI = "/{id}";
    private final WebClient userWebClient;
    public UserClient(@Value("${blog_backend}") String backendUrl) {
        this.userWebClient = WebClient.create(backendUrl + "/users");
    }


    public Flux<UserDto> getAll(String jwtToken){
        return userWebClient.get()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(UserDto.class);
    }

    public Mono<UserDto> getById(Long id, String jwtToken) {
        return userWebClient.get()

                .uri("/id/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(UserDto.class);
    }
    public Mono<UserDto> getByUsername(String username, String jwtToken) {
        return userWebClient.get()

                .uri("/name/{username}", username)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<Void> deleteById(Long id, String jwtToken) {
        return userWebClient.delete()
                .uri(ONE_URI, id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(Void.class);
    }
    public Flux<BlogDto> getAllUsersBlogs(Long id, String jwtToken){
        return userWebClient.get()
                .uri("/id/{id}" + "/blogs", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(BlogDto.class);
    }
    public Flux<BlogDto> getAllUsersBlogsByUsername(String username, String jwtToken){
        return userWebClient.get()
                .uri("/name/{username}/blogs", username)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(BlogDto.class);
    }
}
