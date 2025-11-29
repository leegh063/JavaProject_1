package rpg;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game {
    private static final String SAVE_FILE_NAME = "savegame.dat"; // 🚩 저장 파일 이름 상수

    private Player player;
    private Scanner sc;
    private boolean isCombatMode = false;
    
    // 저장 데이터 관련 필드
    private boolean hasSaveData = false; 
    private Player lastSavedPlayer = null; 

    // 🚩 프로그램 진입점
    public static void main(String[] args) {
        new Game().start();
    }
    
    private boolean checkSaveData() {
        return hasSaveData;
    }

    public Game() {
        sc = new Scanner(System.in);
        loadGameFromFileOnStartup(); // 🚩 게임 시작 시 저장된 파일 데이터 로드 시도
    }

    // 🚩 [추가] 게임 시작 시 파일에서 데이터를 로드하는 메서드
    private void loadGameFromFileOnStartup() {
        File saveFile = new File(SAVE_FILE_NAME);
        
        if (saveFile.exists()) {
            try (FileInputStream fis = new FileInputStream(saveFile);
                 ObjectInputStream ois = new ObjectInputStream(fis)) {
                
                // 파일에서 Player 객체를 읽어 lastSavedPlayer에 저장
                this.lastSavedPlayer = (Player) ois.readObject();
                this.hasSaveData = true;
                System.out.println(Color.LIGHT_GREEN + "기존 저장 데이터를 메모리에 로드했습니다." + Color.RESET);
                
            } catch (IOException | ClassNotFoundException e) {
                System.err.println(Color.RED + "저장 파일 로드 중 오류 발생: 손상된 파일일 수 있습니다." + Color.RESET);
                this.hasSaveData = false;
                // 로드 실패 시 파일 삭제 (손상된 데이터 재사용 방지)
                saveFile.delete(); 
            }
        } else {
            this.hasSaveData = false;
        }
    }

    // 🚩 [리팩토링] 안전한 정수 입력을 위한 헬퍼 메서드
    private int readInt(String prompt) {
        // ... (readInt 로직 유지) ...
        System.out.print(prompt);
        int choice = 0;
        
        try {
            if (sc.hasNextInt()) {
                choice = sc.nextInt();
            } else {
                sc.next();
                return -1; 
            }
        } catch (Exception e) {
            System.out.println(Color.RED + "입력 처리 중 오류가 발생했습니다." + Color.RESET);
            return -1;
        } finally {
            sc.nextLine(); 
        }
        return choice;
    }

    public void start() {
        System.out.println(Color.YELLOW + "===== RPG 게임 시작 =====" + Color.RESET);
        
        while (player == null) {
            System.out.println("\n1. 새 게임  2. 불러오기  3. 종료");
            int choice = readInt("선택: "); 

            if (choice == 1) {
                newGame();
            } else if (choice == 2) {
                loadGameOnStart();
            } else if (choice == 3) {
                System.out.println("게임을 종료합니다.");
                System.exit(0);
            } else if (choice == -1) {
                System.out.println(Color.RED + "잘못된 입력입니다. 숫자를 입력해 주세요." + Color.RESET);
            } else {
                System.out.println(Color.RED + "올바른 메뉴 번호를 선택해 주세요." + Color.RESET);
            }
        }
        
        mainLoop();
    }

    private void newGame() {
        System.out.println("플레이어 이름 입력:");
        String name = sc.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println(Color.RED + "이름을 반드시 입력해야 합니다." + Color.RESET);
            return;
        }
        player = new Player(name);
        System.out.println(Color.GREEN + name + "님, 새 게임을 시작합니다!" + Color.RESET);
    }

    private void loadGameOnStart() {
        if (checkSaveData()) {
            int loadChoice = readInt("저장된 데이터가 있습니다. 불러오시겠습니까? (1. 예  2. 아니오): ");
            
            if (loadChoice == 1) {
                // lastSavedPlayer는 이미 파일에서 로드된 객체입니다.
                this.player = this.lastSavedPlayer.deepCopy(); 
                System.out.println(Color.GREEN + "게임 불러오기 완료!" + Color.RESET);
            } else if (loadChoice == 2) {
                System.out.println("데이터 로드를 취소합니다.");
            } else {
                System.out.println(Color.RED + "잘못된 선택입니다. 로드를 취소합니다." + Color.RESET);
            }
        } else {
            System.out.println(Color.RED + "저장된 데이터가 없습니다." + Color.RESET);
        }
    }
    
    // 🚩 메인 진행 화면 (Main Loop)
    private void mainLoop() {
        // ... (mainLoop 로직 유지) ...
        while (player.isAlive()) {
            System.out.println(Color.LIGHT_BLUE + "\n================ [메 인 메 뉴] ================" + Color.RESET);
            System.out.println("1. 전투  2. 상태창  3. 휴식하기  4. 인벤토리  5. 스킬 확인");
            System.out.println("6. 저장  7. 불러오기  8. 종료  9. 자결");
            
            int choice = readInt("선택: ");
            
            if (choice >= 1 && choice <= 9) {
                player.applyStatusEffects(); 
            } else if (choice == -1) {
                System.out.println(Color.RED + "잘못된 입력입니다. 메뉴 번호를 입력해 주세요." + Color.RESET);
                continue;
            }

            switch (choice) {
                case 1 -> startCombat(); 
                case 2 -> player.showStatus();
                case 3 -> rest(); 
                case 4 -> showInventory(); 
                case 5 -> showSkills(); 
                case 6 -> saveGame(); 
                case 7 -> loadGameFromMenu(); 
                case 8 -> exitGame(); 
                case 9 -> suicide(); 
                default -> System.out.println(Color.RED + "선택할 수 없는 번호입니다." + Color.RESET);
            }
        }
        
        if (!player.isAlive()) {
            gameOverMenu();
        }
    }
    
    private void startCombat() {
        // ... (startCombat 로직 유지) ...
        List<Monster> defaultMonsters = Monster.createDefaultMonsters();
        Random rand = new Random();
        Monster monster = defaultMonsters.get(rand.nextInt(defaultMonsters.size()));
        
        System.out.println(Color.RED + "\n>> 야생의 " + monster.getName() + "이(가) 나타났다! <<" + Color.RESET);
        
        isCombatMode = true;
        combatLoop(monster);
        isCombatMode = false;
    }
    
    // 🚩 전투 턴 관리 루프 (안정성 강화)
    private void combatLoop(Monster monster) {
        // ... (combatLoop 로직 유지) ...
        boolean escaped = false; 

        while (player.isAlive() && monster.isAlive() && !escaped) {
            
            System.out.println("\n----------------------------------------");
            player.showCombatStatus(); 
            System.out.println(Color.RED + "[" + monster.getName() + "]" + Color.RESET + 
                                 " HP: " + monster.getHP() + "/" + monster.maxHP); 
            System.out.println("----------------------------------------");
            
            // 1. 플레이어 턴
            System.out.println(Color.LIGHT_GREEN + "\n[플레이어 턴] 행동을 선택하세요." + Color.RESET);
            System.out.println("1. 공격  2. 스킬 사용  3. 아이템 사용  4. 도망가기");
            
            int choice = readInt("선택: ");
            
            boolean turnUsed = true; 

            if (choice == -1) {
                System.out.println(Color.RED + "잘못된 입력입니다. 턴을 소모합니다." + Color.RESET);
            } else {
                switch (choice) {
                    case 1 -> basicAttack(monster); 
                    case 2 -> turnUsed = useSkillInCombat(monster); 
                    case 3 -> turnUsed = useItemInCombat(); 
                    case 4 -> escaped = attemptEscape(); 
                    default -> {
                        System.out.println(Color.RED + "올바른 번호를 선택해 주세요. 턴을 소모합니다." + Color.RESET);
                        turnUsed = true; 
                    }
                }
            }
            
            if (!turnUsed || escaped) {
                if (!turnUsed) {
                    continue; 
                } else {
                    break;
                }
            }
            
            if (!monster.isAlive()) break;

            // 2. 상태 이상 효과 적용 및 몬스터 턴
            player.applyStatusEffects();
            
            // 🚩 [안정성 강화] 상태 이상 피해로 인한 플레이어 사망 체크
            if (!player.isAlive()) break; 
            
            if (monster.isAlive()) {
                System.out.println(Color.RED + "\n[몬스터 턴]" + Color.RESET);
                monster.chooseAndUseSkill(player);
                monster.applyStatusEffects();
            }
        } 
        
        // 3. 전투 종료 결과 처리
        if (escaped) {
            System.out.println(Color.YELLOW + "\n성공적으로 전투에서 도망쳤습니다!" + Color.RESET);
        } else if (!player.isAlive()) {
            System.out.println(Color.RED + "\n플레이어가 쓰러졌습니다." + Color.RESET);
        } else if (!monster.isAlive()) {
            System.out.println(Color.GREEN + "\n" + monster.getName() + "를 물리쳤습니다!" + Color.RESET);
            player.gainExp(monster.getExpReward());
            player.addGold(monster.getGoldReward());
            System.out.println(Color.GREEN + "EXP +" + monster.getExpReward() + ", GOLD +" + monster.getGoldReward() + " 획득!" + Color.RESET);
        }
    }
    
    // ... (basicAttack, useSkillInCombat 등 전투 관련 메서드 유지) ...
    private void basicAttack(Combatant target) {
        int damage = player.getAttack();
        target.takeDamage(damage);
        System.out.println(Color.ORANGE + player.getName() + "의 기본 공격! → " + 
                             target.getName() + "에게 " + damage + " 피해!" + Color.RESET);
    }
    
    private boolean useSkillInCombat(Combatant target) {
        List<Skill> skills = player.getSkills();
        if (skills.isEmpty()) {
            System.out.println(Color.YELLOW + "배운 스킬이 없습니다!" + Color.RESET);
            return false;
        }
        
        System.out.println(Color.BLUE + "\n[스킬 선택]" + Color.RESET);
        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            System.out.printf("%d. %s (MP: %d, 피해: %d)\n", 
                              i + 1, s.getName(), s.getMpCost(), s.getDamage());
        }
        System.out.println("0. 취소");

        int choice = readInt("선택: ");
        
        if (choice == 0) {
            System.out.println("스킬 사용을 취소합니다. 다시 행동을 선택하세요.");
            return false; 
        } else if (choice == -1) {
            System.out.println(Color.RED + "잘못된 입력입니다. 스킬 사용을 취소합니다." + Color.RESET);
            return false;
        }
        
        int index = choice - 1;
        
        try {
            player.useSkill(index, (Entity)target); 
            return true;
        } catch (GameException e) {
            System.out.println(e.getMessage());
            return false; 
        }
    }
    
    private boolean useItemInCombat() {
        System.out.println(Color.YELLOW + "아이템이 없습니다! 행동을 다시 선택하세요." + Color.RESET);
        return false; 
    }
    
    private boolean attemptEscape() {
        return true; 
    }

    // --- 메인 메뉴 기능들 ---
    
    private void rest() {
        player.restoreAll();
        System.out.println(Color.GREEN + "휴식 완료: HP/MP가 모두 회복되었습니다." + Color.RESET);
    }

    private void showInventory() {
        System.out.println(Color.YELLOW + "인벤토리에 아이템이 없습니다!" + Color.RESET);
    }
    
    private void showSkills() {
        List<Skill> skills = player.getSkills();
        System.out.println("\n" + Color.BLUE + "[스킬 목록]" + Color.RESET);

        if (skills.isEmpty()) {
            System.out.println(Color.YELLOW + "스킬이 없습니다!" + Color.RESET);
            return;
        }

        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            System.out.printf("%d. %s (MP: %d, 피해: %d, 상태: %s, 확률: %.0f%%)\n", 
                              i + 1, s.getName(), s.getMpCost(), s.getDamage(), 
                              s.getStatusEffect().getName(), s.getStatusChance() * 100);
        }
        
        if (!isCombatMode) {
            System.out.println(Color.RED + "\n참고: 전투 중에만 스킬을 사용할 수 있습니다!" + Color.RESET);
        }
    }

    // 🚩 [수정] 파일에 저장 (영구 저장)
    private void saveGame() {
        try (FileOutputStream fos = new FileOutputStream(SAVE_FILE_NAME);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            
            // 현재 플레이어 객체를 파일에 직렬화
            oos.writeObject(this.player); 
            
            // in-memory 저장본도 업데이트하여 메뉴에서 즉시 불러오기 가능하도록 함
            this.lastSavedPlayer = this.player.deepCopy(); 
            this.hasSaveData = true;
            
            System.out.println(Color.GREEN + "게임 저장 완료! 현재 진행도가 파일에 저장되었습니다." + Color.RESET);
        } catch (IOException e) {
            System.out.println(Color.RED + "저장에 실패했습니다: " + e.getMessage() + Color.RESET);
        }
    }
    
    private void loadGameFromMenu() {
        if (!hasSaveData) {
            System.out.println(GameException.noSaveData().getMessage());
            return;
        }

        int loadChoice = readInt("저장된 데이터를 불러오시겠습니까? (1. 예  2. 아니오): ");
        
        if (loadChoice == 1) {
            // lastSavedPlayer는 파일에서 로드되거나 마지막으로 저장된 상태
            this.player = this.lastSavedPlayer.deepCopy(); 
            System.out.println(Color.GREEN + "마지막 저장 데이터를 불러왔습니다!" + Color.RESET);
        } else if (loadChoice != 2) {
             System.out.println(Color.RED + "잘못된 선택입니다. 로드를 취소합니다." + Color.RESET);
        } else {
            System.out.println("데이터 로드를 취소합니다.");
        }
    }

    private void exitGame() {
        System.out.println("게임을 종료합니다.");
        System.exit(0);
    }
    
    private void suicide() {
        player.takeDamage(player.getHP());
        System.out.println(Color.RED + player.getName() + "가 스스로 목숨을 끊었습니다." + Color.RESET);
    }

    // 🚨 게임 오버 메뉴
    private void gameOverMenu() {
        // ... (gameOverMenu 로직 유지) ...
        System.out.println(Color.RED + "\n[G A M E    O V E R]" + Color.RESET);
        
        if (hasSaveData && lastSavedPlayer != null) {
            System.out.println("1. 다시하기 (마지막 저장 데이터 불러오기)  2. 종료하기");
        } else {
            System.out.println("저장된 데이터가 없습니다.");
            System.out.println("2. 종료하기 (2번 입력)");
        }

        int choice = readInt("선택: ");

        if (choice == 1 && hasSaveData && lastSavedPlayer != null) {
            System.out.println(Color.GREEN + "마지막 저장 데이터로 다시 시작합니다..." + Color.RESET);
            this.player = this.lastSavedPlayer.deepCopy();
            mainLoop();
        } else {
            exitGame();
        }
    }
    
    public boolean isCombatMode() {
        return isCombatMode;
    }

    public void setCombatMode(boolean combatMode) {
        this.isCombatMode = combatMode;
    }
}