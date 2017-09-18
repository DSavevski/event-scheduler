package com.sorsix.eventscheduler.domain.dto;

import com.sorsix.eventscheduler.domain.Event;

public class DisplayEventsDto extends Event {

    private boolean isGoing;



    public boolean isGoing() {
        return isGoing;
    }

    public void setGoing(boolean going) {
        isGoing = going;
    }
}
