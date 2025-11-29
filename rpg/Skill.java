package rpg;

import java.io.Serializable;
import java.util.Random;

public class Skill implements Serializable {
    private static final long serialVersionUID = 1L;

    private final String name;
    private final int mpCost;
    private final int damage;
    private final StatusEffect statusEffect;
    private final double statusChance;

    private static final Random RNG = new Random();

    public Skill(String name, int mpCost, int damage, StatusEffect statusEffect, double statusChance) {
        this.name = name;
        this.mpCost = mpCost;
        this.damage = damage;
        this.statusEffect = statusEffect;
        this.statusChance = statusChance;
    }

    public String getName() { return name; }
    public int getMpCost() { return mpCost; }
    public int getDamage() { return damage; }
    public StatusEffect getStatusEffect() { return statusEffect; }
    public double getStatusChance() { return statusChance; }

    public void use(Entity user, Entity target) {
        target.takeDamage(damage);
        System.out.println(user.getName() + "의 " + name + " → " + target.getName() + "에게 " + damage + " 피해!");

        if(statusEffect != null && RNG.nextDouble() <= statusChance) {
            target.addStatusEffect(statusEffect); 
            // 🚩 [수정] 상태 이상 부여 메시지 출력은 Entity.java로 이동했으므로 이 코드는 제거되었습니다.
        }
    }

    // 💡 MP 소모량 8 (유지)
    public static Skill fireball() {
        return new Skill("파이어볼", 8, 12, StatusEffect.BURN, 0.3);
    }

    // 💡 MP 소모량 5로 수정 반영
    public static Skill venom() {
        return new Skill("맹독", 5, 8, StatusEffect.POISON, 1.0);
    }
}