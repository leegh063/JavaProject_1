package rpg;

public class GameException extends Exception {
    private static final long serialVersionUID = 1L;

    public GameException(String message) {
        super(message);
    }

    public static GameException notEnoughMP() {
        return new GameException(Color.RED + "MP가 부족합니다!" + Color.RESET);
    }
    
    // 💡 추가된 통합 예외 메서드
    public static GameException notEnoughGold() {
        return new GameException(Color.RED + "골드가 부족합니다." + Color.RESET);
    }
    
    public static GameException invalidSelection() {
        return new GameException(Color.RED + "잘못된 선택입니다. 다시 선택해 주세요." + Color.RESET);
    }
    
    public static GameException noSaveData() {
        return new GameException(Color.RED + "저장된 데이터가 없습니다." + Color.RESET);
    }
    
    public static GameException targetAlreadyDead(String name) {
        return new GameException(Color.RED + name + "은(는) 이미 쓰러졌습니다." + Color.RESET);
    }
}