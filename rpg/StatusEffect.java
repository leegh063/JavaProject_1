package rpg;

import java.io.Serializable;

public class StatusEffect implements Serializable {
    private static final long serialVersionUID = 1L;

    // 💡 순서 변경 반영
    public enum EffectType { BURN, POISON }

    private final String id;
    private final String name;
    private final EffectType type;
    private final int duration;
    private final int value;

    public StatusEffect(String id, String name, EffectType type, int duration, int value) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.duration = duration;
        this.value = value;
    }

    // 💡 값은 변경 없이 유지
    public static final StatusEffect BURN = new StatusEffect("BURN", "화상", EffectType.BURN, 3, 5);
    public static final StatusEffect POISON = new StatusEffect("POISON", "독", EffectType.POISON, 5, 2);

    public String getId() { return id; }
    public String getName() { return name; }
    public EffectType getType() { return type; }
    public int getDuration() { return duration; }
    public int getValue() { return value; }
}