package amata1219.like;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	private final OldMain plugin = OldMain.getPlugin();

	private String name;
	private File file;
	private FileConfiguration config;

	public Config(String name){
		this.name = name + ".yml";
		this.file = new File(plugin.getDataFolder(), this.name);
		make();
	}

	public FileConfiguration get(){
		return config == null ? reload() : config;
	}

	public void make(){
		if(!file.exists())
			plugin.saveResource(name, false);
	}

	public void save(){
		if(config == null)
			return;

		try {
			get().save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FileConfiguration reload(){
		config = YamlConfiguration.loadConfiguration(file);

		InputStream in = plugin.getResource(name);
		if(in == null)
			return config;

		config.setDefaults(YamlConfiguration.loadConfiguration(new InputStreamReader(in, StandardCharsets.UTF_8)));
		return config;
	}

	public void update(){
		save();
		reload();
	}

}