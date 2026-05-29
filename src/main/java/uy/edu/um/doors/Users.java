package uy.edu.um.doors;

public class Users {

    private int uid;
    private String alias;
    private String type;

    public Users(int uid, String alias, String type){
        this.uid=uid;
        this.alias=alias;
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

}
