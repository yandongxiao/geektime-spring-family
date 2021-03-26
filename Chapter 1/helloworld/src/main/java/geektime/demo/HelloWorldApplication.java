package geektime.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@SpringBootApplication
@RestController
public class HelloWorldApplication {

    private String status = "online";

    public static void main(String[] args) {
        SpringApplication.run(HelloWorldApplication.class, args);
    }


    @RequestMapping("/health/online")
    public String doOnline() {
        status = "online";
        return "do online";
    }

    @RequestMapping("/health/offline")
    public String doOffline() {
        status = "offline";
        return "do offline";
    }

    @RequestMapping("/health/status")
    public String status(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("offline".equals(status)) {
            response.setStatus(400);
            return "bad request";
        }
        response.setStatus(200);
        return "ok";
    }

    @RequestMapping("/health/status1")
    public String status1() {
        if ("offline".equals(status)) {
            return "{\"code\": 400}";
        }
        return "{\"code\": 200}";
    }

    @RequestMapping("/health/status2")
    public String status2() {
        if ("offline".equals(status)) {
            return "400";
        }
        return "200";
    }

    @RequestMapping("/health/status3")
    public String status3() {
        if ("offline".equals(status)) {
            return "offline";
        }
        return "alive";
    }
}

