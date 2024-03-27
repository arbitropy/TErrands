package Controllers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Card implements Serializable {
    public String cardColor;
    public String cardName;
    public List<String> taskList = new ArrayList<String>();
    public List<String> assignedTo = new ArrayList<String>();
    public Card(String name, String color){
         cardName = name;
         cardColor = color;
     }

}
