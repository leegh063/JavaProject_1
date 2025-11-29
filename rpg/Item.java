package rpg;

import java.io.Serializable;

public class Item implements Serializable {
    private final String name;
    private final String desc;
    private final int healAmount;

    public Item(String name, String desc, int healAmount) {
        this.name = name;
        this.desc = desc;
        this.healAmount = healAmount;
    }

    public String getName() { return name; }
    public String getDesc() { return desc; }
    public int getHealAmount() { return healAmount; }

    public void use(Player p) {
        p.heal(healAmount);
        System.out.println(Color.GREEN + p.getName() + "의 HP가 " + healAmount + " 회복되었습니다!" + Color.RESET);
    }
}
