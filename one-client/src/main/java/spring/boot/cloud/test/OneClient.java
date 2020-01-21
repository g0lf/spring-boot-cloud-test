package spring.boot.cloud.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@SpringBootApplication
@RestController
@EnableEurekaClient
@RequestMapping("/one-client")
public class OneClient {

//    @Value("${user.role}")
//    private String role;

    public static void main(String[] args) {
        SpringApplication.run(OneClient.class, args);
    }

//    @GetMapping(
//            value = "/whoami/{username}",
//            produces = MediaType.TEXT_PLAIN_VALUE)
//    public String whoami(@PathVariable("username") String username) {
//        return String.format("Hello! You're %s and you'll become a(n) %s...\n", username, role);
//    }

    private List<Book> bookList = Arrays.asList(
            new Book(1L, "Baeldung goes to the market", "Tim Schimandle"),
            new Book(2L, "Baeldung goes to the park", "Slavisa")
    );

    @GetMapping("")
    public List<Book> findAllBooks() {
        return bookList;
    }

    @GetMapping("/{bookId}")
    public Book findBook(@PathVariable Long bookId) {
        return bookList.stream().filter(b -> b.getId().equals(bookId)).findFirst().orElse(null);
    }


}
