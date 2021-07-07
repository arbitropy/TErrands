package Controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Project implements Serializable {
    public String fileName;
    public List<Card> cards = new ArrayList<Card>();
    public List<String> collaborators = new ArrayList<>();
    public String projectName;
    public String admin;
    public  Project(String name,String admin){
        projectName = name;
        this.admin = admin;
        collaborators.add(admin);
    }



}
