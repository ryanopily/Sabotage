package ml.sabotage.utils;

import java.io.File;

public class SabUtils {
	
	public static final File SOURCE = new File("plugins/Sabotage/worlds");
	
    public static int calcSabs(int playerCount) {
        return (playerCount/5) + 1;
    }
}