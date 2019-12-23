package amata1219.like.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import amata1219.like.Main;

public abstract class Config {
	
	protected static final HashMap<String, Material> MATERIALS = new HashMap<>();
	
	static {
		for(Material material : Material.values()) MATERIALS.put(material.toString(), material);
		System.out.println(MATERIALS.size());
	}
	
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
		if(!MATERIALS.containsKey(s)) throw new IllegalArgumentException("No enum constant " + s);
		return MATERIALS.get(s);
	}
	
	/*protected Material material(String s){
		try{
			return Material.valueOf(s);
		}catch(Exception e){
			return Material.GRASS_BLOCK;
		}
	}*/
	
	public abstract void load();

}
