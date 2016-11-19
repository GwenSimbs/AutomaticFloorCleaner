package com.example.gwen.automaticfloorcleaner;

/**
 * Created by Gwen on 11/13/2016.
 */
public class Room {

    private String room_name;

    public Room(String room_name){

        this.setRoom_name(room_name);

    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
    }

}


