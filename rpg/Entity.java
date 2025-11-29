package rpg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// 💡 Combatant 인터페이스 구현 추가
public abstract class Entity implements Serializable, Combatant {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected int hp, maxHP;
    protected int mp, maxMP;
    protected int attack;

    protected List<StatusEffectInstance> statusEffects = new ArrayList<>();

    public Entity(String name, int maxHP, int maxMP, int attack) {
        this.name = name;
        this.maxHP = maxHP;
        this.hp = maxHP;
        this.maxMP = maxMP;
        this.mp = maxMP;
        this.attack = attack;
    }

    // 💡 Combatant/캡슐화 관련 Getter/Method 구현
    @Override
    public String getName() { return name; }
    @Override
    public int getAttack() { return attack; }
    @Override
    public int getHP() { return hp; }
    @Override
    public int getMP() { return mp; }
    @Override
    public boolean isAlive() { return hp > 0; }

    @Override
    public void takeDamage(int dmg) { hp = Math.max(0, hp - dmg); }
    
    // 💡 Combatant 인터페이스 구현 및 Item 클래스에서 사용 가능하도록 추가
    @Override 
    public void heal(int amount) { hp = Math.min(maxHP, hp + amount); }

    // 🚩 [수정] 상태 이상 중복 및 초기화 로직 추가
    public void addStatusEffect(StatusEffect effect) {
        // 1. 이미 상태 이상이 있는지 확인 (단일 상태 이상만 허용)
        if (!statusEffects.isEmpty()) {
            StatusEffectInstance currentEffectInstance = statusEffects.get(0);
            StatusEffect currentEffect = currentEffectInstance.effect;

            // 2. 같은 종류인지 확인 (EffectType 기준)
            if (currentEffect.getType() == effect.getType()) {
                // A. 같은 종류: 지속 시간 초기화
                currentEffectInstance.remaining = effect.getDuration();
                System.out.println(Color.YELLOW + name + "에게 걸린 " + effect.getName() + " 상태의 지속 시간이 " + currentEffect.getDuration() + "턴으로 초기화되었습니다." + Color.RESET);
                return;
            } else {
                // B. 다른 종류: 중복 부여 방지
                System.out.println(Color.RED + name + "은(는) 이미 " + currentEffect.getName() + " 상태에 걸려있어 " + effect.getName() + " 상태를 추가할 수 없습니다." + Color.RESET);
                return;
            }
        }

        // 3. 상태 이상이 없으면 새로 추가하고 메시지 출력 (Skill.java에서 출력 로직 제거됨)
        statusEffects.add(new StatusEffectInstance(effect));
        System.out.println(Color.PURPLE + name + "에게 " + effect.getName() + " 상태가 부여되었습니다!" + Color.RESET);
    }

    public void applyStatusEffects() {
        List<StatusEffectInstance> toRemove = new ArrayList<>();
        for (StatusEffectInstance sei : statusEffects) {
            int value = sei.effect.getValue();
            
            // 💡 상태 이상 효과 적용 (데미지를 takeDamage()로 처리)
            takeDamage(value); 
            System.out.println(Color.RED + name + "은(는) " + sei.effect.getName() + " 효과로 " + value + " 피해를 입었습니다." + Color.RESET);
            
            sei.remaining--;
            if (sei.remaining <= 0) toRemove.add(sei);
        }
        statusEffects.removeAll(toRemove);
    }

    protected static class StatusEffectInstance implements Serializable {
        private static final long serialVersionUID = 1L;
        StatusEffect effect;
        int remaining;

        StatusEffectInstance(StatusEffect effect) {
            this.effect = effect;
            this.remaining = effect.getDuration();
        }
    }
}