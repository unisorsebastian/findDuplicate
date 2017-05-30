package ro.jmind.model;

import java.util.ResourceBundle;

public class Constants {
    public static final ResourceBundle RB;
    public static final String DELIMITER;
    
    static {
        RB = ResourceBundle.getBundle("config");
        DELIMITER = RB.getString("delimiter");
    }
    

}
