package amata1219.like.config;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Material;
import org.bukkit.World;

import com.google.common.collect.Maps;

import amata1219.like.Main;
import amata1219.like.monad.Option;
import at.pcgamingfreaks.UUIDConverter;

public class MainConfig extends Yaml {
	
	private final HashMap<World, String> aliases = Maps.newHashMap();
	private final HashMap<IconType, Material> materials = Maps.newHashMap();
	private String favorites, description, usage, tip, invitation;
	
	/*
	 * get
	 * text.replace()~~~
	 * player.send(text) or holo.setLine(text)
	 * 
	 * favorites(64).apply(player::sendMessage)
	 * 
	 * 
	 */
	
	public MainConfig(){
		super(Main.instance(), "config.yml");
	}
	
	public boolean canLikesBeCreatedIn(World world){
		return aliases.containsKey(world);
	}
	
	public Option<String> alias(World world){
		return Option.of(aliases.get(world));
	}
	
	public Material icon(IconType type){
		return materials.get(type);
	}
	
	public String favorites(int favorites){
		return this.favorites.replace("%like_count%", String.valueOf(favorites));
	}
	
	public String description(UUID owner){
		return description.replace("%player%", UUIDConverter.getNameFromUUID(owner));
	}
	
	public String usage(){
		return usage;
	}
	
	public String tip(){
		return tip;
	}
	
	public String invitation(){
		return invitation;
	}
	
	public enum IconType {
		
		LIKE,
		FAVORITES,
		TIMESTAMP,
		ID,
		EDIT,
		DELETION,
		CONFIRM_DELETION,
		UNFAVORITE,
		OTHER_LIKES,
		BACK_TO_PREVIOUS_PAGE,
		GO_TO_NEXT_PAGE,
	}

}
