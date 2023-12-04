package com.example.eSmartRecruit.controllers.guest;

import com.example.eSmartRecruit.controllers.request_reponse.ResponseObject;
import com.example.eSmartRecruit.controllers.request_reponse.request.ChangePasswordRequest;
import com.example.eSmartRecruit.exception.UserException;
import com.example.eSmartRecruit.services.impl.PositionService;
import com.example.eSmartRecruit.services.impl.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("eSmartRecruit/")
@AllArgsConstructor
public class GuestController {
    private UserService userService;
    private PositionService positionService;

    @PutMapping("/resetpassword")
    public ResponseEntity<ResponseObject> forgotPassword(@RequestBody @Valid ChangePasswordRequest user) throws UserException {
        try {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(userService.updateUserpassword(user.getUsername(), user.getNewPassword())).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(e.getMessage()).build(), HttpStatus.NOT_IMPLEMENTED);
        }

    }


    @GetMapping("/search/{keyword}")
    public ResponseEntity<ResponseObject> searchJob(@PathVariable("keyword") String keyword)
    {
        try{
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.SUCCESS_STATUS).message(ResponseObject.SEARCH_SUCCESS).data(positionService.searchPositions(keyword)).build(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<ResponseObject>(ResponseObject.builder().status(ResponseObject.ERROR_STATUS).message(e.getMessage()).build(), HttpStatus.NOT_IMPLEMENTED);
        }

    }

}