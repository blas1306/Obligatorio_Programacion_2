package uy.edu.um.doors;

import lombok.Getter;

import java.util.ArrayList;

public class events {


    private int pid;
    private String type;
    private ArrayList<String> instructions;

    public events (int pid, String type,ArrayList<String> instructions){
        this.pid= pid;
        this.type=type;
        this.instructions= new ArrayList<String>(instructions);

    }


    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getPid() {
        return pid;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setInstructions(String instruction) {
        this.instructions.add(instruction);
    }

    public ArrayList<String> getInstructions() {
        return  this.instructions;
    }
}
