package project;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Random;

import acm.graphics.GImage;
import acm.graphics.GLabel;
import acm.graphics.GLine;
import acm.graphics.GRect;
import acm.program.GraphicsProgram;
import project.cards.LargeHealthPotion;
import project.cards.Revive;
import project.cards.Slash;
import project.cards.SmallHealthPotion;
import project.cards.Stab;
import project.cards.Stick;
import starter.GraphicsPane;

//ANDREW

public class BoardGraphics extends GraphicsPane {

	private MainMenu program;
	
	private Font statsFont = new Font("TimesRoman", Font.PLAIN, 50);
	
	private GImage background = new GImage("media/images/DungeonBackground.jpg");
	
	private static GRect enemyHealthBar;
	//private static GRect enemyArmorBar;
	
	private static GLabel playerHealthText;
	private static GImage playerHealthBar = new GImage("media/images/PlayerHealth.png", 0, 0);
	private static GRect playerHealthDamageBar;
	
	private static GLabel playerManaText;
	private static GImage playerManaBar = new GImage("media/images/PlayerMana.png", 0, 82);
	private static GRect playerManaUseBar;
	
	private static GImage endTurnButton = new GImage("media/images/EndTurnButton.png", 1700, 800);
	
	private static GLabel turnText;
	private int turnNumber;
	
	private int levelNumber = 1;
	
	//private GLabel playerArmorText; //TODO
	//private GImage playerArmorBar; //TODO
	
	//TODO Player end button.

	private static int PLAYER_BAR_WIDTH;
	private static int ENEMY_BAR_WIDTH;
	
	private Player player;
	private Enemy enemy;
	
	private boolean isPlayerTurn = true;
	
	public BoardGraphics(MainMenu program) {
		super();
		this.program = program;
		initializeObjects();
	}
	
	public void initializeObjects() {
		enemyHealthBar = new GRect(843, 640, 193, 15);
		enemyHealthBar.setFillColor(Color.RED);
		enemyHealthBar.setFilled(true);
		
		//enemyArmorBar = new GRect(843, 680, 193, 5);
		//enemyArmorBar.setFillColor(Color.GRAY);
		//enemyArmorBar.setFilled(true);
		
		playerHealthBar.setSize(366, 82);
		playerHealthDamageBar = new GRect(80, 12, 273, 57);
		playerHealthDamageBar.setFilled(true);
		playerHealthDamageBar.setFillColor(Color.GREEN);
		playerHealthDamageBar.setColor(Color.GREEN);
		playerHealthText = new GLabel("10/10");
		playerHealthText.setLocation(176, 57);
		playerHealthText.setFont(statsFont);
		
		playerManaBar.setSize(366, 82);
		playerManaUseBar = new GRect(80, 94, 273, 57);
		playerManaUseBar.setFilled(true);
		playerManaUseBar.setFillColor(Color.BLUE);
		playerManaUseBar.setColor(Color.BLUE);
		playerManaText = new GLabel("10/10");
		playerManaText.setLocation(176, 140);
		playerManaText.setFont(statsFont);
		
		turnNumber = 1;
		
		turnText = new GLabel("Turn: 1");
		turnText.setFont(statsFont);
		turnText.setLocation(1700, 50);
		turnText.setColor(Color.WHITE);
		/*
		playerArmorBar = new GImage("media/images/PlayerArmor.png", 0, 164);
		playerArmorBar.setSize(148, 82);
		playerArmorText = new GLabel("10"); //TODO getMana
		playerArmorText.setLocation(82, 220);
		playerArmorText.setFont(statsFont);
		add(playerArmorBar);
		add(playerArmorText);
		*/
		
		PLAYER_BAR_WIDTH = (int) playerHealthDamageBar.getWidth();
		ENEMY_BAR_WIDTH = (int) enemyHealthBar.getWidth();
		
	}
	
	public void checkIfDead() {
		if (enemy.isDead()) {
			if (levelNumber >= Integer.parseInt(ConfigManager.getPath("level"))) {
				ConfigManager.setPath("level", String.valueOf(levelNumber + 1));
			}
			program.getMapGraphics().loadLevels();
			
			isPlayerTurn = true;

			player.resetDeck();
			
			program.openGame();
		}
	}
	
	public void increaseTurn() {
		if (isPlayerTurn) {
			turnText.setLabel("Turn: " + turnNumber++);
			turnText.setColor(Color.WHITE);
		} else {
			turnText.setColor(Color.RED);
		}
	}
	
	public void setLevelNumber(int levelNumber) {
		this.levelNumber = levelNumber;
	}
	
	public void setPlayer(Player player) {
		this.player = player;
	}
	
	public void setEnemy(Enemy enemy) {
		this.enemy = enemy;
		enemy.getSprite().setBounds(500, 200, 900, 450);
	}
	
	public void loadCards() {
		player.loadHand();
		enemy.loadHand();
		
		entityDrawCard(player);
	}
	
	private void entityDrawCard(Entity entity) {
		if (entity.getHand().isEmpty() && entity.getDeck().isEmpty()) {
			entity.resetDeck();
			entityDrawCard(entity);
		} else {
			if (!entity.getDeck().isEmpty()) {
				Card randomCardFromDeck = entity.getDeck().get(new Random().nextInt(entity.getDeck().size()));
				entity.getHand().add(randomCardFromDeck);
				entity.getDeck().remove(randomCardFromDeck);
			}
		}
		reloadHand();
	}
	
	private void reloadHand() {
		int x = 0;

		for (Card cards : player.getHand()) {
			cards.getPicture().setLocation((202 * x) + 400, 720);
			program.add(cards.getPicture());
			x++;
		}

		x = 0;
		for (Card cards : enemy.getHand()) {
			cards.getPicture().setLocation((101 * x) + 900, 0);
			cards.getPicture().setSize(100, 200);
			program.add(cards.getPicture());
			x++;
		}
	}
	
	public void changeEntityStats(Entity entity, int amt, boolean isHealth) {
		boolean isPositive = amt > 0;
		
		if (isHealth) {
			int newHp = entity.getHp() + amt;
			if (newHp > entity.getMaxHp())
				newHp = entity.getMaxHp();
			if (newHp < 0)
				newHp = 0;
					
			entity.setHp(newHp);
			
			if (entity instanceof Player) {
				playerHealthDamageBar.setSize((entity.getHp()*PLAYER_BAR_WIDTH)/entity.getMaxHp(), playerHealthDamageBar.getHeight());
				playerHealthText.setLabel(entity.getHp() + "/" + entity.getMaxHp());
			} else {
				enemyHealthBar.setSize((entity.getHp()*ENEMY_BAR_WIDTH)/entity.getMaxHp(), enemyHealthBar.getHeight());
			}
		} else {
			int newMana = entity.getMana() + amt;
			if (newMana > entity.getMaxMana())
				newMana = entity.getMaxMana();
			if (newMana < 0)
				newMana = 0;
					
			entity.setMana(newMana);
			
			if (entity instanceof Player) {
				playerManaUseBar.setSize((entity.getMana()*PLAYER_BAR_WIDTH)/entity.getMaxMana(), playerManaUseBar.getHeight());
				playerManaText.setLabel(entity.getMana() + "/" + entity.getMaxMana());
			}
		}
		
		
		new Thread() {
	        public void run() {
	        	GLabel statLabel;
	        	
	        	if (isPositive) {
	        		statLabel = new GLabel("+" + amt);
	        		if (isHealth) {
		        		statLabel.setColor(Color.GREEN);
	        		} else {
	        			statLabel.setColor(Color.BLUE);
	        		}
	        	} else {
	        		statLabel = new GLabel("" + amt);
	        		if (isHealth) {
	        			statLabel.setColor(Color.RED);
	        		} else {
	        			statLabel.setColor(Color.MAGENTA);
	        		}
	        	}
	        	
	        	statLabel.setFont(statsFont);
	        	if (entity instanceof Player) {
	        		if (isHealth) {
	        			statLabel.setLocation(400, 50);
	        		} else {
	        			statLabel.setLocation(400, 132);
	        		}
	        	} else {
	        		if (isHealth) {
	        			statLabel.setLocation(1050, 640);
	        		} else {
	        			statLabel.setLocation(1050, 660);
	        		}
	        	}
	        	program.add(statLabel);
	    		
	    		for (int x = 0; x < 30; x++) {
	    			statLabel.move(0, 1);
	    			program.pause(30);
	    		}
	    		
	    		program.remove(statLabel);
	        }
	    }.start();
		
	}
		
	@Override
	public void mousePressed(MouseEvent e) {
		
		GImage currElem = (GImage) program.getElementAt(e.getX(), e.getY());
		
		if (isPlayerTurn) {
			if (currElem == endTurnButton) {
				new Thread() {
					public void run() {
						playEnemyTurn();
					}
				}.start();
				
				//todo hide statlabel?
				changeEntityStats(player, player.getMaxMana() - player.getMana(), false);
				
				return;
			}
			for (Card cards : player.getHand()) {
				if (currElem == cards.getPicture()) {
					if (player.getMana() - cards.getMana() >= 0) {
						cards.play(this, isPlayerTurn, player, enemy);
						
						player.getDiscard().add(cards);
						program.remove(cards.getPicture());
						
						player.getHand().remove(cards);
						
						checkIfDead();
						
						changeEntityStats(player, -cards.getMana(), false);
					}
					break;
				}
			}
		}
	}
	
	public void playEnemyTurn() {
		isPlayerTurn = false;

		entityDrawCard(enemy);
		
		increaseTurn();
		program.pause(2000);
		
		Card randomEnemyCard = enemy.getHand().get(new Random().nextInt(enemy.getHand().size()));
		randomEnemyCard.play(this, isPlayerTurn, player, enemy);
		
		checkIfDead();
		
		program.remove(randomEnemyCard.getPicture());
		enemy.getDiscard().add(randomEnemyCard);
		enemy.getHand().remove(randomEnemyCard);
		
		entityDrawCard(player);
		
		reloadHand();
		
		isPlayerTurn = true;
		increaseTurn();
	}

	@Override
	public void showContents() {
		program.add(background);
		
		program.add(enemyHealthBar);
		//program.add(enemyArmorBar);

		program.add(playerHealthBar);
		program.add(playerHealthDamageBar);
		program.add(playerHealthText);
		
		program.add(playerManaBar);
		program.add(playerManaUseBar);
		program.add(playerManaText);
		
		program.add(endTurnButton);
		
		program.add(turnText);
		
		program.add(enemy.getSprite());
		
		reloadHand();
	}

	@Override
	public void hideContents() {
		program.remove(background);
		
		program.remove(enemyHealthBar);
		//program.remove(enemyArmorBar);

		program.remove(playerHealthBar);
		program.remove(playerHealthDamageBar);
		program.remove(playerHealthText);
		
		program.remove(playerManaBar);
		program.remove(playerManaUseBar);
		program.remove(playerManaText);
		
		program.remove(endTurnButton);
		
		program.remove(turnText);
		
		program.remove(enemy.getSprite());
	}
	
}
