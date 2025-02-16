package cvut.fit.cz.blogclient.util;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WebClientResponseException.Forbidden.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbiddenError(WebClientResponseException ex, Model model) {
        model.addAttribute("error", "Access Denied!");
        return "redirect:/";
    }
}