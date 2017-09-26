package com.sorsix.eventscheduler.domain.dto;

import com.sorsix.eventscheduler.domain.Event;

public class DisplayEventsDto extends Event {

    public Long eventId;
    public boolean isGoing;
    public Long totalGoings;

    public static DisplayEventsDto fromEvent(Event event){
        DisplayEventsDto displayEventsDto = new DisplayEventsDto();

        displayEventsDto.eventId = event.getId();
        displayEventsDto.setName(event.getName());
        displayEventsDto.setStartDate(event.getStartDate());
        displayEventsDto.setEndDate(event.getEndDate());
        displayEventsDto.setPlace(event.getPlace());
        displayEventsDto.setDescription(event.getDescription());
        displayEventsDto.setCreator(event.getCreator());
        displayEventsDto.setPicture(event.getPicture());
        displayEventsDto.setCity(event.getCity());

        return displayEventsDto;
    }

}
