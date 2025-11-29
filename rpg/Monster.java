package rpg;

import java.util.ArrayList;
import java.util.List;

public class Monster extends Entity {
    private List<Skill> skills;
    private int expReward;
    private int goldReward;

    public Monster(String name, int maxHP, int attack, int expReward, int goldReward) {
        super(name, maxHP, 0, attack);
        this.expReward = expReward;
        this.goldReward = goldReward;
        skills = new ArrayList<>();
    }

    public List<Skill> getSkills() { return skills; }

    public void addSkill(Skill skill) { skills.add(skill); }

    public void chooseAndUseSkill(Entity target) {
        if (skills.isEmpty()) {
            target.takeDamage(attack);
            System.out.println(name + "의 일반 공격 → " + target.getName() + "에게 " + attack + " 피해!");
        } else {
            // 현재는 스킬이 있다면 첫 번째 스킬만 사용
            Skill s = skills.get(0); 
            s.use(this, target); 
        }
    }

    public int getExpReward() { return expReward; }
    public int getGoldReward() { return goldReward; }

    // 💡 몬스터 초기화 및 스탯 수정 반영
    public static List<Monster> createDefaultMonsters() {
        List<Monster> monsters = new ArrayList<>();

        // 슬라임: 체력 20, 공격력 5, 경험치 8, 골드 5
        Monster slime = new Monster("슬라임", 20, 5, 8, 5);
        
        // 고블린: 체력 35, 공격력 8, 경험치 12, 골드 10
        Monster goblin = new Monster("고블린", 35, 8, 12, 10);
        
        // 늑대: 체력 45, 공격력 10, 경험치 15, 골드 15
        Monster wolf = new Monster("늑대", 45, 10, 15, 15);

        monsters.add(slime);
        monsters.add(goblin);
        monsters.add(wolf);

        return monsters;
    }
}