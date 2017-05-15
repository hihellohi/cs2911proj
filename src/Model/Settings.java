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

    public static Settings getInstance() {
        return instance;
    }

    public Settings() {
        difficulty = Difficulty.MEDIUM;
    }

    public Difficulty getDifficulty() {
        return difficulty;
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
