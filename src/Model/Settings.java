package Model;

/**
 * Created by adley on 15/05/17.
 */
public class Settings {
    private final static Settings instance = new Settings();

    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private Difficulty difficulty;
    private int TCPPort;
    private String name;

    public static Settings getInstance() {
        return instance;
    }

    private Settings() {
        difficulty = Difficulty.MEDIUM;
        TCPPort = 1337;
        name = "";
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public int getTCPPort() {
        return TCPPort;
    }

    public boolean setTCPPort(String port){
        try {
            int portNumber = Integer.parseInt(port);
            if(portNumber < 0 || portNumber > 65535){
                return false;
            }
            TCPPort = portNumber;
            return true;
        }
        catch (NumberFormatException ex){
            return false;
        }
    }

    public String getDifficultyString() {
        switch (difficulty) {
            case EASY:
                return "Easy";
            case MEDIUM:
                return "Medium";
            case HARD:
                return "Hard";
        }
        return "";
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
