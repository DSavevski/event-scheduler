package com.sorsix.eventscheduler.api;

import com.sorsix.eventscheduler.domain.User;
import com.sorsix.eventscheduler.service.PictureService;
import com.sorsix.eventscheduler.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Collections;
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

    @GetMapping
    public User user(Principal principal) {
        return userService.getUserWithPrincipal(principal);
    }

    @PutMapping()
    public User updateUser(@RequestBody Map<String, String> firstAndLastName, Principal principal) {
        User user = userService.findByUserName(principal.getName());
        user.setFirstName(firstAndLastName.get("firstName"));
        user.setLastName(firstAndLastName.get("lastName"));
        return userService.updateUser(user);
    }

    @PostMapping(value = "/{id}/upload_image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean uploadImage(@RequestParam("image") MultipartFile image,
                               @PathVariable Long id) throws IOException {
        return userService.uploadImage(id,
                image.getBytes(), image.getContentType(),
                image.getSize(), image.getName()) != null;
    }

    @PostMapping(value = "/reset_password")
    public Map<String, String> resetPassword(@RequestBody Map<String, String> passwords,
                                             Principal principal) {
        String msg = "Your old password is incorrect!";

        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        boolean result = userService.resetPassword(oldPassword, newPassword, principal);
        if (result) msg = null;

        return Collections.singletonMap("message", msg);
    }

    @GetMapping(value = "/provider")
    public Map<String, String> getProvider(Principal principal) {
        User user = userService.findByUserName(principal.getName());
        String provider = user.getProvider().toString();
        return Collections.singletonMap("provider", provider);
    }

    @GetMapping(value = "/{id}/image")
    public void getPicture(@PathVariable Long id,
                           HttpServletResponse response) throws IOException {
        pictureService.getPicture(id, response);
    }

}
