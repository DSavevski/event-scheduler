package com.sorsix.eventscheduler.service;

import com.sorsix.eventscheduler.domain.Picture;
import com.sorsix.eventscheduler.repository.PictureRepository;
import org.springframework.stereotype.Service;

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
}
