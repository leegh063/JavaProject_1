package rpg;

public interface Combatant {
    String getName();
    int getHP();
    int getMP();
    int getAttack();
    // 💡 getDefense()는 방어력 기능 보류에 따라 제거됨
    boolean isAlive();

    void takeDamage(int amount);
    void heal(int amount);
}