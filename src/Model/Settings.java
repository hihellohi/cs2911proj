package Model;

/**
 * Class to control the settings.
 *
 */
public class Settings {
    private final static Settings instance = new Settings();

    /**
     * Enum of the possible difficulties.
     *
     */
    public enum Difficulty {
        EASY,
        MEDIUM,
        HARD
    }

    private Difficulty difficulty;
    private int TCPPort;
    private String name;

    /**
     * Return the instance of the settings.
     *
     * @return instance
     */
    public static Settings getInstance() {
        return instance;
    }

    /**
     * Setup with default settings.
     *
     */
    private Settings() {
        difficulty = Difficulty.MEDIUM;
        TCPPort = 1337;
        name = "";
    }

    /**
     * Getter to return the current difficulty.
     *
     * @return difficulty
     */
    public Difficulty getDifficulty() {
        return difficulty;
    }

    /**
     * Getter to return the name of the client.
     *
     * @return name
     */
    public String getName(){
        return name;
    }

    /**
     * Setter to set the name of the client.
     *
     * @param name name of player
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Getter to return the TCP port.
     *
     * @return TCPPort
     */
    public int getTCPPort() {
        return TCPPort;
    }

    /**
     * Setter to set the TCP Port.
     *
     * @param port port number
     * @return if a port is in use or not
     */
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

    /**
     * Return the difficulty as a string.
     *
     * @return difficulty
     */
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

    /**
     * Set the difficulty.
     *
     * @param difficulty difficulty of the game
     * @pre difficulty != null
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }
}
