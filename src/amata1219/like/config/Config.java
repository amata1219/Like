package amata1219.like.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import amata1219.like.Main;

public abstract class Config {
	
	protected final Main plugin = Main.plugin();
	private FileConfiguration config;
	private final File file;
	private final String name;
	
	public Config(String name){
		this.name = name;
		this.file = new File(plugin.getDataFolder(), name);
		saveDefault();
	}
	
	public void saveDefault(){
		if(!file.exists()) plugin.saveResource(name, false);
	}
	
	public FileConfiguration config(){
		if(config == null) reload();
		return config;
	}
	
	public void save(){
		if(config == null) return;
		
		try{
			config().save(file);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public void reload(){
		config = YamlConfiguration.loadConfiguration(file);
		InputStream stream = plugin.getResource(name);
		if(stream == null) return;
		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8)));
	}
	
	public void update(){
		save();
		reload();
	}
	
	protected String color(String s){
		return ChatColor.translateAlternateColorCodes('&', s);
	}
	
	protected Material material(String s){
		return Material.valueOf(s);
	}
	
	public abstract void load();

}
