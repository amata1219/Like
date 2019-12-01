package amata1219.like.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.monad.Option;

public abstract class Yaml extends YamlConfiguration {

	protected final JavaPlugin plugin;
	private final File file;
	private final String resourceFileName;

	public Yaml(JavaPlugin plugin, String fileName){
		this(plugin, new File(plugin.getDataFolder(), fileName));
	}

	public Yaml(JavaPlugin plugin, File file){
		this.plugin = plugin;
		this.file = file;
		this.resourceFileName = file.getName().replace('\\', '/');
	}

	public void save(){
		try {
			save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reload(){
		super.map.clear();

		try {
			load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
		Option.of(plugin.getResource(resourceFileName))
		.map(input -> new InputStreamReader(input, StandardCharsets.UTF_8))
		.map(YamlConfiguration::loadConfiguration)
		.then(this::setDefaults);
		
		readAll();
	}
	
	public abstract void readAll();

	public void update(){
		save();
		reload();
	}
	
	public List<String> list(String path){
		return getStringList(path);
	}
	
	public String colored(String path){
		return ChatColor.translateAlternateColorCodes('&', getString(path));
	}
	
	public Section section(String path){
		return new Section(this, path);
	}
	
	public class Section {
		
		private final Yaml yaml;
		private final String loc;
		
		private Section(Yaml yaml, String loc){
			this.yaml = yaml;
			this.loc = loc + ".";
		}
		
		public String string(String path){
			return yaml.getString(loc + path);
		}
		
		public String colored(String path){
			return yaml.colored(loc + path);
		}
		
		public int integer(String path){
			return yaml.getInt(loc + path);
		}
		
		public double doub1e(String path){
			return yaml.getDouble(path);
		}

	}
	
}