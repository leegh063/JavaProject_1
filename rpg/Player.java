package rpg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player extends Entity implements Serializable { 
    private static final long serialVersionUID = 1L; 

    private int level;
    private int exp;
    private int gold;
    private List<Skill> skills;
    // 🚩 [리팩토링] 다음 레벨까지 필요한 경험치를 저장하는 필드 (캐싱)
    private int nextRequiredExp; 

    public Player(String name) { 
        super(name, 100, 30, 10);
        this.level = 1;
        this.exp = 0;
        this.gold = 0;
        this.skills = new ArrayList<>();
        // 🚩 초기 다음 필요 경험치 설정
        this.nextRequiredExp = 20; 
    }

    // 🚩 [리팩토링] 캐시된 값을 반환 (EXP 계산 로직 반복 제거)
    private int requiredExpOptimized() {
        return this.nextRequiredExp; 
    }

    public void gainExp(int amount) {
        exp += amount;
        
        while (exp >= requiredExpOptimized()) {
            exp -= requiredExpOptimized();
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        
        maxHP += 10;
        maxMP += 5;
        attack += 2;
        
        // 🚩 [리팩토링] 레벨업 시에만 다음 필요 경험치 계산 및 업데이트
        this.nextRequiredExp = calculateNextRequiredExp();
        
        restoreAll();

        if (level == 3) {
            if (skills.stream().noneMatch(s -> s.getName().equals("파이어볼"))) {
                skills.add(Skill.fireball());
                System.out.println(Color.YELLOW + ">> 새로운 스킬 [파이어볼]을 습득했습니다! <<" + Color.RESET);
            }
            if (skills.stream().noneMatch(s -> s.getName().equals("맹독"))) {
                skills.add(Skill.venom());
                System.out.println(Color.YELLOW + ">> 새로운 스킬 [맹독]을 습득했습니다! <<" + Color.RESET);
            }
        }
    }
    
    // 🚩 [추가] 다음 레벨에 필요한 경험치를 계산하는 내부 메서드 (기존 로직)
    private int calculateNextRequiredExp() {
        if (this.level == 1) {
            return 20; 
        }
        
        int baseExp = 20;
        int required = 20;
        
        for (int i = 2; i <= this.level; i++) {
            if (i > 2) {
                baseExp = (int) Math.round(baseExp * 1.2);
                required = baseExp;
            }
        }
        return required;
    }
    
    public void restoreAll() {
        this.hp = this.maxHP;
        this.mp = this.maxMP;
    }
    
    public void addGold(int amount) { gold += amount; }
    
    public void spendGold(int amount) throws GameException {
        if (gold < amount) throw GameException.notEnoughGold();
        gold -= amount;
    }

    public List<Skill> getSkills() { return skills; }

    public void useSkill(int index, Entity target) throws GameException {
        if (index < 0 || index >= skills.size()) throw GameException.invalidSelection();
        
        Skill s = skills.get(index);
        if (mp < s.getMpCost()) throw GameException.notEnoughMP();
        
        mp -= s.getMpCost();
        s.use(this, target);
    }

    public void showStatus() {
        System.out.println("\n" + Color.YELLOW + "========================================" + Color.RESET);
        System.out.println(Color.YELLOW + "      [플 레 이 어 상 태]" + Color.RESET);
        System.out.println(Color.YELLOW + "========================================" + Color.RESET);
        
        System.out.printf("%-10s: %s\n", "이름", name); 
        System.out.printf("%-10s: %d\n", "레벨", level);
        
        String hpPart = String.format("(%s%d/%d%s)", Color.RED, hp, maxHP, Color.RESET);
        String mpPart = String.format("(%s%d/%d%s)", Color.BLUE, mp, maxMP, Color.RESET);
        System.out.printf("%-10s: %s %s\n", "HP/MP", hpPart, mpPart);

        System.out.printf("%-10s: %s%d%s\n", "공격력", Color.ORANGE, attack, Color.RESET);
        
        int nextExp = requiredExpOptimized(); 
        System.out.printf("%-10s: (%s%d/%d%s)\n", 
                          "경험치", Color.LIGHT_GREEN, exp, nextExp, Color.RESET);
        
        System.out.printf("%-10s: %d G\n", "골드", gold);

        if (!statusEffects.isEmpty()) {
            System.out.print(Color.PURPLE + ">> 상태이상: " + Color.RESET);
            for (StatusEffectInstance sei : statusEffects) {
                String statusColor = sei.effect.getType() == StatusEffect.EffectType.BURN ? Color.ORANGE : Color.PURPLE;
                System.out.print(statusColor + sei.effect.getName() + "(" + sei.remaining + "턴)" + Color.RESET + " ");
            }
            System.out.println();
        }
        System.out.println(Color.YELLOW + "========================================" + Color.RESET);
    }

    public void showCombatStatus() {
        System.out.print(Color.GREEN + "[" + name + "]" + Color.RESET);
        System.out.printf(" HP: %s%d/%d%s ", Color.RED, hp, maxHP, Color.RESET);
        System.out.printf("MP: %s%d/%d%s ", Color.BLUE, mp, maxMP, Color.RESET);
        
        if (!statusEffects.isEmpty()) {
             System.out.print(Color.PURPLE + "(상태이상: " + Color.RESET);
            for (StatusEffectInstance sei : statusEffects) {
                String statusColor = sei.effect.getType() == StatusEffect.EffectType.BURN ? Color.ORANGE : Color.PURPLE;
                System.out.print(statusColor + sei.effect.getName() + Color.RESET + " ");
            }
            System.out.print(")");
        }
        System.out.println();
    }
    
    // 깊은 복사를 위한 유틸리티 메서드
    public Player deepCopy() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(this);
            oos.flush();

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bis);
            return (Player) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null; 
        }
    }
}