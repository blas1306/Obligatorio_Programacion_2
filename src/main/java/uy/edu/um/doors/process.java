package uy.edu.um.doors;

public class process {

    private int pid;
    private int uid;
    private String name;
    private int priority;
    private String estado;

    public process (int pid, int uid, String name){

        this.pid=pid;
        this.uid=uid;
        this.name=name;
        this.priority=0;
        this.estado="NEW";
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }




    private void catculatePrio(){

    }

}
