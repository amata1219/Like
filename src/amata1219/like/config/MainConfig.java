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
import amata1219.like.masquerade.text.Text;
import amata1219.like.monad.Maybe;
import amata1219.like.tuplet.Tuple;
import at.pcgamingfreaks.UUIDConverter;

import static amata1219.like.config.MainConfig.IconType.*;

public class MainConfig extends Yaml {
	
	private final HashMap<World, String> worlds2aliases = Maps.newHashMap();
	private ImmutableMap<IconType, Material> icons2materials;
	private String likeFavoritesText;
	private String likeDescription;
	private String likeUsage;
	private String tip;
	private int numberOfSecondsOfLikeCreationCooldown;
	private int likeCreationLimitPerChunk;
	private double teleportationCosts;
	private String teleportationMessage;
	private double invitationCosts;
	private int radiusOfInvitationScope;
	private String invitationMessage;
	
	public MainConfig(){
		super(Main.plugin(), "config.yml");
		readAll();
	}
	
	@Override
	public void readAll() {
		worlds2aliases.clear();
		list("Map of worlds where like creation is enabled and aliases").stream()
		.map(s -> s.split(","))
		.map(s -> Tuple.of(Maybe.unit(Bukkit.getWorld(s[0])), s[1]))
		.forEach(t -> t.first.apply(w -> worlds2aliases.put(w, t.second)));
		
		Section lines = section("Like holograms'' text lines");
		likeFavoritesText = lines.colored("Favorites");
		likeDescription = lines.colored("Description");
		likeUsage = lines.colored("Usage");
		
		tip = colored("Tip");
		
		Section icons = section("Icon materials on inventory UI");
		icons2materials = new ImmutableMap.Builder<IconType, Material>()
			.put(FAVORITES, icons.material("Favorites"))
			.put(CREATION_TIMESTAMP, icons.material("Creation timestamp"))
			.put(ID, icons.material("ID"))
			.put(UNFAVORITE, icons.material("Unfavorite"))
			.put(EDIT_DESCRIPTION, icons.material("Edit description"))
			.put(GO_TO_LIKE_TELEPORTATION_OR_LIKE_INVITATION_CONFIRMATION_PAGE, icons.material("Go to like teleportation or like invitation confirmation page"))
			.put(TELEPORT_TO_LIKE, icons.material("Teleport to like"))
			.put(CANCEL_LIKE_TELEPORTATION, icons.material("Cancel like teleportation"))
			.put(INVITE_TO_LIKE, icons.material("Invite to like"))
			.put(CANCEL_LIKE_INVITATION, icons.material("Cancel like invitation"))
			.put(GO_TO_LIKE_DELETION_PAGE, icons.material("Go to like deletion page"))
			.put(DELETE_LIKE, icons.material("Delete like"))
			.put(CANCEL_LIKE_DELETION, icons.material("Cancel like deletion"))
			.put(LIKE, icons.material("Like"))
			.put(OWNERS_OTHER_LIKES, icons.material("Owner''s other likes"))
			.put(SORT_BY_CREATION_DATE_IN_ASCENDING_ORDER, icons.material("Sort by creation date in ascending order"))
			.put(SORT_BY_CREATION_DATE_IN_DESCENDING_ORDER, icons.material("Sort by creation date in descending order"))
			.put(SORT_BY_FAVORITES_IN_ASCENDING_ORDER, icons.material("Sort by favorites in ascending order"))
			.put(SORT_BY_FAVORITES_IN_DESCENDING_ORDER, icons.material("Sort by favorites in descending order"))
			.build();
		
		numberOfSecondsOfLikeCreationCooldown = getInt("Number of seconds of like creation cooldown");
		likeCreationLimitPerChunk = getInt("Like creation limit per chunk");
		
		Section teleportation = section("Teleportation");
		teleportationCosts = teleportation.doub1e("Costs");
		teleportationMessage = teleportation.colored("Message");
		
		Section invitation = section("Invitation");
		invitationCosts = invitation.doub1e("Costs");
		radiusOfInvitationScope = invitation.integer("Radius of scope");
		invitationMessage = invitation.colored("Message");
	}
	
	public boolean canLikesBeCreatedIn(World world){
		return worlds2aliases.containsKey(world);
	}
	
	public Maybe<String> worldAlias(World world){
		return Maybe.unit(worlds2aliases.get(world));
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
		return numberOfSecondsOfLikeCreationCooldown;
	}
	
	public int likeCreationLimitPerChunk(){
		return likeCreationLimitPerChunk;
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
	
	public class LikeFavoritesText extends Text {
		
		private LikeFavoritesText(String text){
			super(text);
		}
		
		public Text apply(int favorites){
			text = text.replace("%favorites%", String.valueOf(favorites));
			return this;
		}
	}
	
	public class LikeDescriptionText extends Text {
		
		private LikeDescriptionText(String text){
			super(text);
		}
		
		public Text apply(UUID owner){
			text = text.replace("%owner%", UUIDConverter.getNameFromUUID(owner));
			return this;
		}
		
	}
	
	public class TeleportationText extends Text {
		
		private TeleportationText(String text){
			super(text);
		}
		
		public Text apply(Like to){
			text = text.replace("%description%", to.description())
					.replace("%owner%", UUIDConverter.getNameFromUUID(to.owner()));
			return this;
		}
		
	}
	
	public class InvitationText extends Text {
		
		private InvitationText(String text){
			super(text);
		}
		
		public InvitationText apply(Player inviter, Like to){
			text = text.replace("%description%", to.description())
					.replace("%owner%", UUIDConverter.getNameFromUUID(to.owner()))
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
