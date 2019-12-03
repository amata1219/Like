package amata1219.like.config;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.monad.Option;
import amata1219.like.tuplet.Tuple;
import at.pcgamingfreaks.UUIDConverter;

import static amata1219.like.config.MainConfig.IconType.*;

public class MainConfig extends Yaml {
	
	private final HashMap<World, String> worlds2aliases = Maps.newHashMap();
	private ImmutableMap<IconType, Material> icons2materials;
	private String likeFavoritesText;
	private String likeExplanation;
	private String likeUsage;
	private String tip;
	private int numberOfSecondsOfLikeCreationCooldown;
	private int likeCreationLimitPerChunk;
	private double teleportCosts;
	private double invitationCosts;
	private int radiusOfInvitationScope;
	private String invitationMessage;
	
	public MainConfig(){
		super(Main.instance(), "config.yml");
		readAll();
	}
	
	@Override
	public void readAll() {
		worlds2aliases.clear();
		list("Map of worlds where like creation is enabled and aliases").stream()
		.map(s -> s.split(","))
		.map(s -> Tuple.of(Option.of(Bukkit.getWorld(s[0])), s[1]))
		.forEach(t -> t.first.then(w -> worlds2aliases.put(w, t.second)));
		
		Section lines = section("Like holograms'' text lines");
		likeFavoritesText = lines.colored("Favorites");
		likeExplanation = lines.colored("Explanation");
		likeUsage = lines.colored("Usage");
		
		tip = colored("Tip");
		
		Section icons = section("Icon materials on inventory UI");
		icons2materials = new ImmutableMap.Builder<IconType, Material>()
			.put(FAVORITES, icons.material("Favorites"))
			.put(CREATION_TIMESTAMP, icons.material("Creation timestamp"))
			.put(ID, icons.material("ID"))
			.put(UNFAVORITE, icons.material("Unfavorite"))
			.put(EDIT_EXPLANATION, icons.material("Edit explanation"))
			.put(PROCEED_TO_CONFIRMATION_PAGE_OF_DELETING_LIKE, icons.material("Proceed to confirmation page of deleting like"))
			.put(DELETE_LIKE, icons.material("Delete like"))
			.put(CANCEL_LIKE_DELETION, icons.material("Cancel like deletion"))
			.put(LIKE, icons.material("Like"))
			.put(CREATORS_OTHER_LIKES, icons.material("Creator''s other likes"))
			.put(BACK_TO_PREVIOUS_PAGE, icons.material("Back to previous page"))
			.put(GO_TO_NEXT_PAGE, icons.material("Go to next page"))
			.build();
		
		numberOfSecondsOfLikeCreationCooldown = getInt("Number of seconds of like creation cooldown");
		likeCreationLimitPerChunk = getInt("Like creation limit per chunk");
		teleportCosts = getDouble("Teleport costs");
		
		Section invitation = section("Invitation");
		invitationCosts = invitation.doub1e("Costs");
		radiusOfInvitationScope = invitation.integer("Radius of scope");
		invitationMessage = invitation.colored("Message");
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
	
	public String likeFavoritesText(int favorites){
		return this.likeFavoritesText.replace("%favorites%", String.valueOf(favorites));
	}
	
	public String likeExplanation(UUID maker){
		return likeExplanation.replace("%maker%", UUIDConverter.getNameFromUUID(maker));
	}
	
	public String likeUsage(){
		return likeUsage;
	}
	
	public String tip(){
		return tip;
	}
	
	public int numberOfSecondsOfLikeCreationCooldown(){
		return numberOfSecondsOfLikeCreationCooldown;
	}
	
	public int likeCreationLimitPerChunk(){
		return likeCreationLimitPerChunk;
	}
	
	public double teleportCosts(){
		return teleportCosts;
	}
	
	public double invitationCosts(){
		return invitationCosts;
	}
	
	public int radiusOfInvitationScope(){
		return radiusOfInvitationScope;
	}
	
	public String invitationMessage(Player invitee, Like like){
		return invitationMessage;
	}
	
	public enum IconType {
		
		FAVORITES,
		CREATION_TIMESTAMP,
		ID,
		UNFAVORITE,
		EDIT_EXPLANATION,
		PROCEED_TO_CONFIRMATION_PAGE_OF_DELETING_LIKE,
		DELETE_LIKE,
		CANCEL_LIKE_DELETION,
		LIKE,
		CREATORS_OTHER_LIKES,
		BACK_TO_PREVIOUS_PAGE,
		GO_TO_NEXT_PAGE;
		
	}
	
}
