package amata1219.like.config;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.masquerade.text.Text;

import static amata1219.like.config.MainConfig.IconType.*;

public class MainConfig extends Config {
	
	private final HashMap<World, String> worlds2aliases = Maps.newHashMap();
	private ImmutableMap<IconType, Material> icons2materials;
	private String likeFavoritesText;
	private String likeDescription;
	private String likeUsage;
	private String tip;
	private int numberOfSecondsOfLikeCreationCooldown;
	private double teleportationCosts;
	private String teleportationMessage;
	private double invitationCosts;
	private int radiusOfInvitationScope;
	private String invitationMessage;
	private int rangeSearchRadiusLimit;
	private double rangeSearchTeleportationCosts;
	private String randomTeleportationMessage;
	private long randomTeleportationDelayedTicks;
	private double randomTeleportationCosts;
	private String unitOfCost;
	
	public MainConfig(){
		super("config.yml");
		load();
	}
	
	@Override
	public void load() {
		FileConfiguration config = config();
		
		worlds2aliases.clear();

		for (String worldNameAndAlias : config.getStringList("Map of worlds where like creation is enabled and aliases")) {
			String[] split = worldNameAndAlias.split(":");
			World world = Bukkit.getWorld(split[0]);
			worlds2aliases.put(world, split[1]);
		}
		
		ConfigurationSection lines = config.getConfigurationSection("Like holograms text lines");
		likeFavoritesText = color(lines.getString("Favorites"));
		likeDescription = color(lines.getString("Description"));
		likeUsage = color(lines.getString("Usage"));
		
		tip = color(config.getString("Tip"));
		
		ConfigurationSection icons = config.getConfigurationSection("Icon materials on inventory UI");
		icons2materials = new ImmutableMap.Builder<IconType, Material>()
			.put(FAVORITES, material(icons.getString("Favorites")))
			.put(CREATION_TIMESTAMP, material(icons.getString("Creation timestamp")))
			.put(ID, material(icons.getString("ID")))
			.put(UNFAVORITE, material(icons.getString("Unfavorite")))
			.put(EDIT_DESCRIPTION, material(icons.getString("Edit description")))
			.put(GO_TO_LIKE_TELEPORTATION_OR_LIKE_INVITATION_CONFIRMATION_PAGE, material(icons.getString("Go to like teleportation or like invitation confirmation page")))
			.put(TELEPORT_TO_LIKE, material(icons.getString("Teleport to like")))
			.put(CANCEL_LIKE_TELEPORTATION, material(icons.getString("Cancel like teleportation")))
			.put(INVITE_TO_LIKE, material(icons.getString("Invite to like")))
			.put(CANCEL_LIKE_INVITATION, material(icons.getString("Cancel like invitation")))
			.put(GO_TO_LIKE_DELETION_PAGE, material(icons.getString("Go to like deletion page")))
			.put(DELETE_LIKE, material(icons.getString("Delete like")))
			.put(CANCEL_LIKE_DELETION, material(icons.getString("Cancel like deletion")))
			.put(LIKE, material(icons.getString("Like")))
			.put(OWNERS_OTHER_LIKES, material(icons.getString("Owners other likes")))
			.put(SORT_BY_CREATION_DATE_IN_ASCENDING_ORDER, material(icons.getString("Sort by creation date in ascending order")))
			.put(SORT_BY_CREATION_DATE_IN_DESCENDING_ORDER, material(icons.getString("Sort by creation date in descending order")))
			.put(SORT_BY_FAVORITES_IN_ASCENDING_ORDER, material(icons.getString("Sort by favorites in ascending order")))
			.put(SORT_BY_FAVORITES_IN_DESCENDING_ORDER, material(icons.getString("Sort by favorites in descending order")))
			.build();
		
		numberOfSecondsOfLikeCreationCooldown = config.getInt("Number of seconds of like creation cooldown");
		
		ConfigurationSection teleportation = config.getConfigurationSection("Teleportation");
		teleportationCosts = teleportation.getDouble("Costs");
		teleportationMessage = color(teleportation.getString("Message"));
		
		ConfigurationSection invitation = config.getConfigurationSection("Invitation");
		invitationCosts = invitation.getDouble("Costs");
		radiusOfInvitationScope = invitation.getInt("Radius of scope");
		invitationMessage = color(invitation.getString("Message"));

		ConfigurationSection rangeSearch = config.getConfigurationSection("Range search");
		rangeSearchRadiusLimit = rangeSearch.getInt("Radius limit");
		rangeSearchTeleportationCosts = rangeSearch.getDouble("Teleportation costs");

		ConfigurationSection randomTeleportation = config.getConfigurationSection("Random teleportation");
		randomTeleportationMessage = color(randomTeleportation.getString("Message"));
		randomTeleportationDelayedTicks = randomTeleportation.getLong("Delayed ticks");
		randomTeleportationCosts = randomTeleportation.getDouble("Costs");

		unitOfCost = config.getString("Unit of cost");
	}
	
	public boolean canLikesBeCreatedIn(World world){
		return worlds2aliases.containsKey(world);
	}
	
	public String worldAlias(World world) {
		return worlds2aliases.getOrDefault(world, "Unknown");
	}
	
	public Material material(IconType type){
		return icons2materials.get(type);
	}
	
	public LikeFavoritesText likeFavoritesText(){
		return new LikeFavoritesText(likeFavoritesText);
	}
	
	public LikeDescriptionText likeDescription(){
		return new LikeDescriptionText(likeDescription);
	}
	
	public String likeUsage(){
		return likeUsage;
	}
	
	public String tip(){
		return tip;
	}
	
	public int numberOfSecondsOfLikeCreationCooldown(){
		return numberOfSecondsOfLikeCreationCooldown * 20;
	}
	
	public double teleportationCosts(){
		return teleportationCosts;
	}
	
	public TeleportationText teleportationText(){
		return new TeleportationText(teleportationMessage);
	}
	
	public double invitationCosts(){
		return invitationCosts;
	}
	
	public int radiusOfInvitationScope(){
		return radiusOfInvitationScope;
	}
	
	public InvitationText invitationText(){
		return new InvitationText(invitationMessage);
	}

	public int rangeSearchRadiusLimit() {
		return rangeSearchRadiusLimit;
	}

	public double rangeSearchTeleportationCosts() {
		return rangeSearchTeleportationCosts;
	}

	public String randomTeleportationMessage() {
		return randomTeleportationMessage;
	}

	public long randomTeleportationDelayedTicks() {
		return randomTeleportationDelayedTicks;
	}

	public double randomTeleportationCosts() {
		return randomTeleportationCosts;
	}

	public String unitOfCost() {
		return unitOfCost;
	}

	public enum IconType {
		
		FAVORITES,
		CREATION_TIMESTAMP,
		ID,
		UNFAVORITE,
		EDIT_DESCRIPTION,
		GO_TO_LIKE_TELEPORTATION_OR_LIKE_INVITATION_CONFIRMATION_PAGE,
		TELEPORT_TO_LIKE,
		CANCEL_LIKE_TELEPORTATION,
		INVITE_TO_LIKE,
		CANCEL_LIKE_INVITATION,
		GO_TO_LIKE_DELETION_PAGE,
		DELETE_LIKE,
		CANCEL_LIKE_DELETION,
		LIKE,
		OWNERS_OTHER_LIKES,
		SORT_BY_CREATION_DATE_IN_ASCENDING_ORDER,
		SORT_BY_CREATION_DATE_IN_DESCENDING_ORDER,
		SORT_BY_FAVORITES_IN_ASCENDING_ORDER,
		SORT_BY_FAVORITES_IN_DESCENDING_ORDER;
		
	}
	
	public static class LikeFavoritesText extends Text {
		
		private LikeFavoritesText(String text){
			super(text);
		}
		
		public String apply(int favorites){
			return text.replace("%favorites%", String.valueOf(favorites));
		}
	}
	
	public static class LikeDescriptionText extends Text {
		
		private LikeDescriptionText(String text){
			super(text);
		}
		
		public String apply(UUID owner){
			return text.replace("%owner%", Main.nameFrom(owner));
		}
		
	}
	
	public static class TeleportationText extends Text {
		
		private TeleportationText(String text){
			super(text);
		}
		
		public Text apply(Like to){
			text = text.replace("%description%", to.description())
					.replace("%owner%", to.ownerName());
			return this;
		}
		
	}
	
	public static class InvitationText extends Text {
		
		private InvitationText(String text){
			super(text);
		}
		
		public InvitationText apply(Player inviter, Like to){
			text = text.replace("%description%", to.description())
					.replace("%owner%", to.ownerName())
					.replace("%inviter%", inviter.getName());
			return this;
		}
		
		public Text apply(Player invitee){
			text = text.replace("%invitee%", invitee.getName());
			return this;
		}
		
		@Override
		public InvitationText clone(){
			return new InvitationText(text);
		}
		
	}
	
}
