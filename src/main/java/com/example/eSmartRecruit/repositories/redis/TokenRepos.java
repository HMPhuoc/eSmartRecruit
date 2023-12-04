package com.example.eSmartRecruit.repositories.redis;

import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.models.redis.Token;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

@Repository
@AllArgsConstructor
public class TokenRepos {

    public static final String HASH_KEY = "TOKEN";
    private RedisTemplate template;

    public Token save(Token token){
        template.opsForHash().put(token.getUsername(),HASH_KEY,token.getToken());
        template.expire(token.getUsername(),Duration.ofMinutes(60));
        return token;
    }

//    public List<Token> findAll(){
//        return template.opsForHash().values();
//    }

    public String findTokenByUsername(String username){
        return (String) template.opsForHash().get(username,HASH_KEY);
    }

    public String deleteToken(String username){
        if (findTokenByUsername(username)==null){
            return "No token found";
        }
        template.opsForHash().delete(username,HASH_KEY);
        return ResponseObject.LOGOUT_SUCCESS;
    }
}
