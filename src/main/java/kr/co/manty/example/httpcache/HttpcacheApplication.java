package kr.co.manty.example.httpcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.WebRequest;

import java.util.Calendar;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
@RestController
public class HttpcacheApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpcacheApplication.class, args);
    }

    private final AtomicInteger count = new AtomicInteger();
    private Calendar etagSource = Calendar.getInstance();

    @GetMapping("/server")
    public String server(WebRequest webRequest) {
        int result = count.incrementAndGet() / 5;
        if (count.get() % 5 == 0 ) {
            etagSource = Calendar.getInstance();
        }

        if (webRequest.checkNotModified(String.valueOf(etagSource.getTimeInMillis()))) {
            return null;
        }

        return String.valueOf(result);
    }

    @Autowired
    RestTemplate restTemplate;

    @GetMapping("/client")
    public String client() {
        return IntStream.range(0, 5).boxed()
                .map(it -> restTemplate.getForObject("http://localhost:8080/server", String.class))
                .collect(Collectors.joining(","));
    }

}
