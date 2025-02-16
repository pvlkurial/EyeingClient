package cvut.fit.cz.blogclient.data;

import cvut.fit.cz.blogclient.model.Auth.AuthResponse;
import cvut.fit.cz.blogclient.model.Blog.BlogDto;
import cvut.fit.cz.blogclient.model.Login.LoginRequest;
import cvut.fit.cz.blogclient.model.User.UserDto;
import cvut.fit.cz.blogclient.util.JwtTokenHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class AuthClient {
    private final WebClient authWebClient;

    public AuthClient(@Value("${blog_backend}") String backendUrl) {
        this.authWebClient = WebClient.create(backendUrl);
    }

    public Mono<AuthResponse> login(LoginRequest loginRequest) {
        return authWebClient.post()
                .uri("/auth/login") // Backend login endpoint
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(loginRequest)
                .retrieve()
                .bodyToMono(AuthResponse.class);
    }

    public Mono<UserDto> getCurrentUser(String jwtToken) {
        return authWebClient.get()
                .uri("/auth/current-user")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(UserDto.class);
    }

    public Mono<String> create(UserDto user) {
        return authWebClient.post()
                .uri("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(user)
                .retrieve()
                .bodyToMono(String.class);
    }
    public Flux<BlogDto> getCurrentUsersBlogs(String jwtToken){
                return authWebClient.get()
                .uri("/auth/current-user/blogs")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToFlux(BlogDto.class);
    }
    public Mono<String> logout(){
        return authWebClient.post()
                .uri("/auth/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class);
    }
}
