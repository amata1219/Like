package amata1219.like.config;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.monad.Option;
import amata1219.like.tuplet.Tuple;
import at.pcgamingfreaks.UUIDConverter;

public class MainConfig extends Yaml {
	
	private final HashMap<World, String> worlds2aliases = Maps.newHashMap();
	private final HashMap<IconType, Material> icons2materials = Maps.newHashMap();
	private String favorites, explanation, usage, tip, invitation;
	
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
		readAll();
	}
	
	@Override
	public void readAll() {
		worlds2aliases.clear();
		list("List of worlds where like creation is enabled and aliases").stream()
		.map(s -> s.split(","))
		.map(s -> Tuple.of(Option.of(Bukkit.getWorld(s[0])), s[1]))
		.forEach(t -> t.first.then(w -> worlds2aliases.put(w, t.second)));
		
		Section lines = section("Sequence of like holograms'' text lines.");
		favorites = lines.colored("Favorites");
		explanation = lines.colored("Explanation");
		usage = lines.colored("Usage");
	}
	
	public boolean canLikesBeCreatedIn(World world){
		return worlds2aliases.containsKey(world);
	}
	
	public Option<String> alias(World world){
		return Option.of(worlds2aliases.get(world));
	}
	
	public Material icon(IconType type){
		return icons2materials.get(type);
	}
	
	public String favorites(int favorites){
		return this.favorites.replace("%favorites%", String.valueOf(favorites));
	}
	
	public String explanation(UUID maker){
		return explanation.replace("%maker%", UUIDConverter.getNameFromUUID(maker));
	}
	
	public String usage(){
		return usage;
	}
	
	public String tip(){
		return tip;
	}
	
	public String invitation(Player invitee, Like like){
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
