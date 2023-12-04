package com.example.eSmartRecruit.config;

import com.example.eSmartRecruit.exception.UserException;
import com.example.eSmartRecruit.services.impl.UserService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Base64;

@AllArgsConstructor
@Data
public class ExtractUser {
    //private final JwtService jwtService;
    //private UserStatus status;
    private Integer userId;
    private boolean isEnabled;
    private final UserService userService;

    private static String decode(String encodedString){
        return new String(Base64.getUrlDecoder().decode(encodedString));
    }
    public ExtractUser(String token, UserService userService) throws JSONException, UserException {
        this.userService = userService;

        String jwt = token.substring(7);
        String[] parts = jwt.split("\\.");
        JSONObject payload = new JSONObject(decode(parts[1]));
        this.userId = payload.getInt("jti");
        //System.out.println(userId);

        //this.status = UserStatus.valueOf(payload.getString("aud"));
        this.isEnabled = userService.isEnabled(userId);
        //System.out.println(userRole.name());
    }



}
