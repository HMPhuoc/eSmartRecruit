package com.example.eSmartRecruit.services;

import com.example.eSmartRecruit.models.Application;


public interface IApplicationService {
    public String apply(Application applications);
    public String update(Integer candidateId,Application applications, Integer id);
    public String deletejob(Integer candidateId, Integer jobid);
}
