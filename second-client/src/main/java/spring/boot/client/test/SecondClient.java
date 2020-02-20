package spring.boot.client.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@SpringBootApplication
@EnableEurekaClient
@RestController
@RequestMapping("/second-client")
//можно указывать конкретную рибоновскую конфигурацию
//@RibbonClient(name = "hello-service", configuration = HelloServiceConfiguration.class)
//для удобства работы с рестом можно использовать feign client, он также работает с испльзованием ribbon
@EnableFeignClients
public class SecondClient {

    public static void main(String[] args) {
        SpringApplication.run(SecondClient.class, args);
    }

    private List<Rating> ratingList = Arrays.asList(
            new Rating(1L, 1L, 2),
            new Rating(2L, 1L, 3),
            new Rating(3L, 2L, 4),
            new Rating(4L, 2L, 5)
    );

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FirstClient firstClient;

    //load-balance со стороны клиента https://www.baeldung.com/spring-cloud-rest-client-with-netflix-ribbon
    @LoadBalanced
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @GetMapping("")
    public List<Rating> findRatingsByBookId(@RequestParam Long bookId) {
        return bookId == null || bookId.equals(0L) ? Collections.EMPTY_LIST : ratingList.stream().filter(r -> r.getBookId().equals(bookId)).collect(Collectors.toList());
    }

    @GetMapping("/all")
    public List<Rating> findAllRatings() {
        return ratingList;
    }

    //либо так
    @GetMapping("/books")
    public List<Rating> getBooksListFromFirstClient() {
        List list = restTemplate.getForObject("http://one-client/one-client", List.class);
        return list;
    }

    //либо так
    @GetMapping("/getAuthor/{bookId}")
    public String getAuthor(@PathVariable Long bookId) {
        return firstClient.findBookAuthor(bookId);
    }

    @FeignClient("one-client")
    interface FirstClient {
        @RequestMapping(value = "/one-client/getAuthor/{bookId}", method = GET, consumes = "application/json")
        String findBookAuthor(@PathVariable("bookId")  Long bookId);
    }
}
