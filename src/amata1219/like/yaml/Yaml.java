package amata1219.like.yaml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.monad.Option;

public class Yaml extends YamlConfiguration {

	protected final JavaPlugin plugin;
	private final File file;
	protected final String name;
	private final String resourceFileName;

	public Yaml(JavaPlugin plugin, String fileName){
		this(plugin, new File(plugin.getDataFolder(), fileName));
	}

	public Yaml(JavaPlugin plugin, File file){
		this(plugin, file, file.getName());
	}

	public Yaml(JavaPlugin plugin, File file, String resourceFileName){
		this.plugin = plugin;
		this.file = file;
		this.resourceFileName = resourceFileName.replace('\\', '/');

		String fileName = file.getName();
		name = fileName.substring(0, fileName.length() - 4);

		saveResource(resourceFileName);

		reload();
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
	}

	public void update(){
		save();
		reload();
	}

	private void saveResource(String resourceFileName){
		if(file.exists()) return;
		
		InputStream in = plugin.getResource(resourceFileName);
		if(in == null) throw new IllegalArgumentException("The embedded resource '" + resourceFileName + "' cannot be found in " + file);

		String path = file.getPath();
		int lastIndex = path.lastIndexOf(47);
		File outDir = new File(file.getParent(), path.substring(0, Math.max(lastIndex, 0)));

		if (outDir.exists()) outDir.mkdirs();

		OutputStream output = null;
		try{
			output = new FileOutputStream(file);
			byte[] buf = new byte[1024];
			int length = 0;
			while((length = in.read(buf)) > 0) output.write(buf, 0, length);
			output.close();
			in.close();
		}catch(IOException ex){
			plugin.getLogger().log(Level.SEVERE, "Cound not save " + file.getName() + " to " + file, ex);
		}
	}

}
