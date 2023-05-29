package hk.ust.comp3021.utils;

import hk.ust.comp3021.person.User;
import java.util.Date;

public class UserRegister {
    private final String assignedUserID;
    private final String userName;
    private final Date registerTime;

    public UserRegister(String assignedUserID, String userName, Date registerTime){
        this.assignedUserID = assignedUserID;
        this.userName = userName;
        this.registerTime = registerTime;
    }

    public User register() {
        User u = new User(this.assignedUserID, this.userName, this.registerTime);
        return u;
    }

    public String getAssignedUserID() {
        return assignedUserID;
    }

    public String getUserName() {
        return userName;
    }

    public Date getRegisterTime() {
        return registerTime;
    }
}
