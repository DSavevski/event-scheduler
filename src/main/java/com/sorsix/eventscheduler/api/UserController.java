package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.Picture;
import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.PictureService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dragan on 7/18/17.
 */

@RestController
@RequestMapping(value = "/api/user")
public class UserController {

    private UserService userService;
    private PictureService pictureService;

    public UserController(UserService userService, PictureService pictureService) {
        this.userService = userService;
        this.pictureService = pictureService;
    }

    @GetMapping(value="check")
    public boolean checkUser(){
        return userService.checkUser();

    }
    @GetMapping
    public User user(Principal principal) {
        return userService.getUserWithPrincipal(principal);
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public User updateUser(@RequestBody Map<String, String> firstAndLastName, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        user.setFirstName(firstAndLastName.get("firstName"));
        user.setLastName(firstAndLastName.get("lastName"));
        return userService.updateUser(user);
    }
    @PostMapping(value = "/upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void uploadImage(@RequestParam("image") MultipartFile image,
                            @PathVariable Long id) throws IOException {

        User user = userService.findUserById(id);

        Picture pictureToSave = new Picture();
        pictureToSave.data = image.getBytes();
        pictureToSave.contentType = image.getContentType();
        pictureToSave.size = image.getSize();
        pictureToSave.fileName = image.getName();
        pictureService.savePicture(pictureToSave);

        Picture oldPicture= user.getPicture();
        if(oldPicture != null){
            user.setPicture(null);
            pictureService.deletePicture(oldPicture.getId());
        }

        user.setPicture(pictureToSave);

        userService.updateUser(user);
    }

    @PostMapping(value = "/reset", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> resetPassword(@RequestBody Map<String, String> passwords,
                                             Principal principal) {

        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        boolean result = userService.resetPassword(oldPassword, newPassword, principal);
        Map<String, String> returnMsg = new HashMap<>();

        if (result) {
            returnMsg.put("message", null);
        } else {
            returnMsg.put("message", "Your old password is incorrect!");
        }

        return returnMsg;
    }

    @GetMapping(value = "/provider", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, String> getProvider(Principal principal){
        String provider = userService.findByUserName(principal.getName()).getProvider().toString();

        Map<String, String> map = new HashMap<>();
        map.put("provider", provider);

        return map;
    }

}
