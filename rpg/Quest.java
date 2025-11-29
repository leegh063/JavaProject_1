package rpg;

import java.io.Serializable;

public class Quest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;
    private String description;
    private boolean completed;

    public Quest(String title, String description) {
        this.title = title;
        this.description = description;
        this.completed = false;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCompleted() { return completed; }

    public void complete() {
        this.completed = true;
        System.out.println(Color.YELLOW + "퀘스트 완료: " + title + Color.RESET);
    }
}
