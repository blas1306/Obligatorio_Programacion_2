package uy.edu.um.doors;

import java.util.ArrayList;

public class Process implements Comparable<Process> {

    private int pid;
    private int uid;
    private String name;
    private int priority;
    private String status;
    private ArrayList<Events> events;
    private String finishedType;

    public Process (int pid, int uid, String name, ArrayList<Events> events){

        this.pid=pid;
        this.uid=uid;
        this.name=name;
        this.priority=0;
        this.status="NEW";
        this.events=events;

    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEvents(ArrayList<Events> events) {
        this.events = events;
    }

    public ArrayList<Events> getEvents() {
        return events;
    }

    public void setFinishedType (String type) {this.finishedType = type;}

    public String getFinishedType () {return finishedType;}

    @Override
    public int compareTo(Process o) {
        return this.priority - o.priority;
    }
}
