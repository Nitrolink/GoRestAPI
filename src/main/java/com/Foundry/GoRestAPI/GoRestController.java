package com.Foundry.GoRestAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/gorest/user")
public class GoRestController {
    private final String goRest_URL = "https://gorest.co.in/public/v1/users/";

    @Autowired
    public Environment env;

    @GetMapping("/")
    public String rootRoute(){
        return "Welcome Home";
    }

    @GetMapping("/get")
    public Object getUser(RestTemplate restTemplate, @RequestParam (name = "id")String id){
        try{
            return restTemplate.getForObject(goRest_URL + id,GoRestResponse.class).getData();

        }
        catch(HttpClientErrorException.NotFound e){
            return "ID did not match user in database";
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }


    @PostMapping("/post")
    public Object postUser(RestTemplate restTemplate, @RequestParam(name = "name")String name,@RequestParam(name = "email")String email,@RequestParam(name = "gender")String gender,@RequestParam(name = "status")String status){
        try{
            HttpHeaders headers= new HttpHeaders();
            headers.setBearerAuth(env.getProperty("access.token"));

            GoRestUser newUser = new GoRestUser(name, email, gender, status);
            HttpEntity<GoRestUser> request = new HttpEntity<>(newUser,headers);
            return restTemplate.exchange(goRest_URL,HttpMethod.POST,request,GoRestResponse.class);
        }
        catch(Exception e){
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @PutMapping("/put")
    public Object putUser(RestTemplate restTemplate,@RequestParam(name = "id")String id, @RequestParam(name = "name")String name,@RequestParam(name = "email")String email,@RequestParam(name = "gender")String gender,@RequestParam(name = "status")String status){
        try{
            HttpHeaders headers= new HttpHeaders();
            headers.setBearerAuth(env.getProperty("access.token"));

            GoRestUser newUser = new GoRestUser(name, email, gender, status);
            HttpEntity<GoRestUser> request = new HttpEntity<>(newUser,headers);
            return restTemplate.exchange(goRest_URL + id,HttpMethod.PUT,request,GoRestResponse.class);
        }
        catch(Exception e){
            System.out.println(e.getClass());
            System.out.println(e.getMessage());
            return e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteUser (RestTemplate restTemplate, @RequestParam(name = "id")String id){
        try{HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(env.getProperty("access.token"));
            HttpEntity request = new HttpEntity(headers);
            restTemplate.exchange(goRest_URL + id, HttpMethod.DELETE, request,GoRestResponse.class);
            return "Sucessful Deletion";
        }
        catch(HttpClientErrorException.Unauthorized e){
            return "You need Authorization";
        }
        catch(HttpClientErrorException.NotFound e){
            return "ID did not match user in database";
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return e.getMessage();
        }

    }

}
