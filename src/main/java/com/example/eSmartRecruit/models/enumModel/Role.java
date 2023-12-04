package com.example.eSmartRecruit.models.enumModel;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
public enum Role {
    Candidate(0),
    Admin(1),
    Interviewer(2);

    private final Integer value;


    public Integer getValue(){
        return this.value;
    }

    public static Role findRole(Integer value){
        for(Role r : values()){
            if( r.getValue().equals(value)){
                return r;
            }
        }
        return null;
    }

}
