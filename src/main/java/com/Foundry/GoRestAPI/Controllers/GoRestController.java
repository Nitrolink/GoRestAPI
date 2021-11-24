package com.Foundry.GoRestAPI.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.Foundry.GoRestAPI.Models.*;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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


    @GetMapping("/pageone")
    public GoRestMultiResponse pageOne(RestTemplate restTemplate){
        return restTemplate.getForObject(goRest_URL, GoRestMultiResponse.class);
    }

    @GetMapping("/page/{index}")
    public GoRestMultiResponse getPage(@PathVariable String index,RestTemplate restTemplate){
        return restTemplate.getForObject("https://gorest.co.in/public/v1/users?page="+index + "&access-token=" + env.getProperty("access.token"), GoRestMultiResponse.class);
    }

    @GetMapping("/pages")
    public  ArrayList<GoRestUser> getPages(@RequestBody Map<String, Integer> range,RestTemplate restTemplate){
        ArrayList<GoRestUser> endUsers = new ArrayList<>();
        for (int i = range.get("Start"); i <= range.get("End"); i++) {
            GoRestMultiResponse page = getPage(String.valueOf(i),restTemplate);
            GoRestUser[] users = page.getData();
            endUsers.addAll(List.of(users));
        }
        return endUsers;

    }

    @GetMapping("/full")
    public ArrayList<GoRestUser> fullUsers(RestTemplate restTemplate){
        ArrayList<GoRestUser> endUsers = new ArrayList<>();
        GoRestMultiResponse temp = pageOne(restTemplate);

        for (int i = 1; i < temp.getMeta().getPagination().getPages(); i++) {
            GoRestMultiResponse page = getPage(String.valueOf(i),restTemplate);
            GoRestUser[] users = page.getData();
            endUsers.addAll(List.of(users));
        }
        return endUsers;
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
    public Object putUser(RestTemplate restTemplate,@RequestParam(name = "id")String id,
                          @RequestBody GoRestUser user){
        try{
            HttpHeaders headers= new HttpHeaders();
            headers.setBearerAuth(env.getProperty("access.token"));

            GoRestUser newUser = user;


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
        try{
            HttpHeaders headers = new HttpHeaders();
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
