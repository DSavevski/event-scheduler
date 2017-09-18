package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.Picture;
import com.sorsix.eventscheduler.repository.PictureRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Service
public class PictureService {
    private PictureRepository pictureRepository;

    public PictureService(PictureRepository pictureRepository) {
        this.pictureRepository = pictureRepository;
    }

    public void savePicture(Picture picture){
        pictureRepository.save(picture);
    }

    public Picture findById(Long id){
        return pictureRepository.findOne(id);
    }

    public String deletePicture(Long Id) {
        try {
            pictureRepository.delete(Id);
            return "Successfully deleted!";
        } catch (Exception ex) {
            return "Picture was not deleted!";
        }

    }

    public void getPicture(Long id, HttpServletResponse response) throws IOException {
        Picture picture = pictureRepository.findOne(id);

        if (picture != null) {
            response.setContentType(picture.contentType);
            response.setContentLength((int) picture.size);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(picture.data);
            outputStream.flush();
            outputStream.close();
        }
    }
}
