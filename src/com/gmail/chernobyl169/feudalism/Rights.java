package com.gmail.chernobyl169.feudalism;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.YamlConfiguration;

public class Rights {

	private Quadrant cow, chicken, sheep, pig, horse;
	private Quadrant sugar, pumpkin, melon, wheat, carrot, potato, wart;
	private Quadrant clay, potion, fish, wither;
	
	private YamlConfiguration config;
	private static File configFile;
	
	private final String filename = "rights.yml"; 
	
	private void saveRights() {
		try { config.save(configFile); }
		catch (IOException e) { e.printStackTrace(); }
	}
	
	
	public Rights(FeudalismPlugin plugin) {
		configFile = new File(plugin.getDataFolder(), filename);
		config = YamlConfiguration.loadConfiguration(configFile);
		
		cow = Quadrant.toQuadrant(config.getString("cow"));
		chicken = Quadrant.toQuadrant(config.getString("chicken"));
		sheep = Quadrant.toQuadrant(config.getString("sheep"));
		pig = Quadrant.toQuadrant(config.getString("pig"));
		horse = Quadrant.toQuadrant(config.getString("horse"));
		sugar = Quadrant.toQuadrant(config.getString("sugar"));
		pumpkin = Quadrant.toQuadrant(config.getString("pumpkin"));
		melon = Quadrant.toQuadrant(config.getString("melon"));
		wheat = Quadrant.toQuadrant(config.getString("wheat"));
		carrot = Quadrant.toQuadrant(config.getString("carrot"));
		potato = Quadrant.toQuadrant(config.getString("potato"));
		wart = Quadrant.toQuadrant(config.getString("wart"));
		clay = Quadrant.toQuadrant(config.getString("clay"));
		fish = Quadrant.toQuadrant(config.getString("fish"));
		potion = Quadrant.toQuadrant(config.getString("potion"));
		wither = Quadrant.toQuadrant(config.getString("wither"));
	}
	
	public void setClay(Quadrant q) {
		clay = q;
		config.set("clay", q.toString());
		saveRights();
	}
	public void setFish(Quadrant q) {
		fish = q;
		config.set("fish", q.toString());
		saveRights();
	}
	public void setPotion(Quadrant q) {
		potion = q;
		config.set("potion", q.toString());
		saveRights();
	}
	public void setWither(Quadrant q) {
		wither = q;
		config.set("wither", q.toString());
		saveRights();
	}
	public void setCow(Quadrant q) {
		cow = q;
		config.set("cow", q.toString());
		saveRights();
	}
	public void setChicken(Quadrant q) {
		chicken = q;
		config.set("chicken", q.toString());
		saveRights();
	}
	public void setSheep(Quadrant q) {
		sheep = q;
		config.set("sheep", q.toString());
		saveRights();
	}
	public void setPig(Quadrant q) {
		pig = q;
		config.set("pig", q.toString());
		saveRights();
	}
	public void setHorse(Quadrant q) {
		horse = q;
		config.set("horse", q.toString());
		saveRights();
	}
	public void setSugar(Quadrant q) {
		sugar = q;
		config.set("sugar", q.toString());
		saveRights();
	}
	public void setMelon(Quadrant q) {
		melon = q;
		config.set("melon", q.toString());
		saveRights();
	}
	public void setPumpkin(Quadrant q) {
		pumpkin = q;
		config.set("pumpkin", q.toString());
		saveRights();
	}
	public void setWheat(Quadrant q) {
		wheat = q;
		config.set("wheat", q.toString());
		saveRights();
	}
	public void setCarrot(Quadrant q) {
		carrot = q;
		config.set("carrot", q.toString());
		saveRights();
	}
	public void setPotato(Quadrant q) {
		potato = q;
		config.set("potato", q.toString());
		saveRights();
	}
	public void setWart(Quadrant q) {
		wart = q;
		config.set("wart", q.toString());
		saveRights();
	}

	public Quadrant clay() { return clay; }
	public Quadrant fish() { return fish; }
	public Quadrant potion() { return potion; }
	public Quadrant wither() { return wither; }
	public Quadrant cow() { return cow; }
	public Quadrant chicken() { return chicken; }
	public Quadrant sheep() { return sheep; }
	public Quadrant pig() { return pig; }
	public Quadrant horse() { return horse; }
	public Quadrant sugar() { return sugar; }
	public Quadrant pumpkin() { return pumpkin; }
	public Quadrant melon() { return melon; }
	public Quadrant wheat() { return wheat; }
	public Quadrant carrot() { return carrot; }
	public Quadrant potato() { return potato; }
	public Quadrant wart() { return wart; }
}
