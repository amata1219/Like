package amata1219.like;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.World;
import com.gmail.filoghost.holographicdisplays.api.handler.TouchHandler;
import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;
import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.NamedHologramManager;
import com.gmail.filoghost.holographicdisplays.object.line.CraftHologramLine;
import com.gmail.filoghost.holographicdisplays.object.line.CraftTouchableLine;

import amata1219.like.config.MainConfig;
import amata1219.like.masquerade.text.Text;
import amata1219.like.player.PlayerData;
import amata1219.like.reflection.Method;
import amata1219.like.ui.AdministratorUI;
import amata1219.like.ui.LikeEditingUI;
import amata1219.like.ui.LikeInformationUI;

public class Like {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd (E) HH:mm:ss");
	private static final Method<CraftTouchableLine, Void> setTouchHandler = Method.of_(
		CraftTouchableLine.class,
		"setTouchhandler", 
		TouchHandler.class, World.class, double.class, double.class, double.class
	);
			
	private final Main plugin = Main.plugin();
	private final MainConfig config = plugin.config();
	
	public final long id;
	public final NamedHologram hologram;
	
	private UUID owner;
	private int favorites;
	
	public Like(NamedHologram hologram, UUID owner, int favorites){
		this.id = Long.parseLong(hologram.getName());
		this.hologram = hologram;
		this.owner = owner;
		this.favorites = favorites;
		
		hologram.appendTextLine(config.likeFavoritesText().apply(favorites));
		hologram.appendTextLine(config.likeDescription().apply(owner));
		hologram.appendTextLine(config.likeUsage());
		
		setTouchHandler(false);
	}
	
	public Like(NamedHologram hologram, UUID owner){
		this(hologram, owner, 0);
	}
	
	public World world(){
		return hologram.getWorld();
	}
	
	public int x(){
		return (int) hologram.getX();
	}
	
	public int y(){
		return (int) hologram.getY();
	}
	
	public int z(){
		return (int) hologram.getZ();
	}
	
	public UUID owner(){
		return owner;
	}
	
	public void setOwner(UUID uuid){
		Objects.requireNonNull(uuid);
		plugin.players.get(owner).unregisterLike(this);
		this.owner = uuid;
		plugin.players.get(owner).registerLike(this);
	}
	
	public boolean isOwner(UUID uuid){
		return owner.equals(uuid);
	}
	
	public String description(){
		return ((TextLine) hologram.getLine(1)).getText();
	}
	
	public void setDescription(String description){
		rewriteHologramLine(1, description);
	}
	
	public int favorites(){
		return favorites;
	}
	
	public void incrementFavorites(){
		favorites++;
		rewriteHologramLine(0, config.likeFavoritesText().apply(favorites));
	}
	
	public void decrementFavorites(){
		favorites = Math.min(favorites - 1, 0);
		rewriteHologramLine(0, config.likeFavoritesText().apply(favorites));
	}
	
	private void rewriteHologramLine(int index, String text){
		List<CraftHologramLine> lines = hologram.getLinesUnsafe();
		((CraftHologramLine) lines.get(index)).despawn();
		lines.set(index, HologramDatabase.readLineFromString(text, hologram));
		hologram.refreshAll();
		if(index == 0){
			setTouchHandler(true);
			setTouchHandler(false);
		}
		HologramDatabase.saveHologram(hologram);
		//HologramDatabase.trySaveToDisk();
	}
	
	public String creationTimestamp(){
		return DATE_FORMAT.format(id);
	}
	
	public void delete(){
		plugin.players.get(owner).likes.remove(this);
		plugin.players.values().stream().forEach(data -> data.unfavoriteLike(this));
		hologram.delete();
		NamedHologramManager.removeHologram(hologram);
		HologramDatabase.deleteHologram(hologram.getName());
		HologramDatabase.trySaveToDisk();
	}
	
	private void setTouchHandler(boolean delete){
		TouchHandler handler = delete ? null : player -> {
			UUID uuid = player.getUniqueId();
			if(player.isSneaking()){
				if(isOwner(uuid)) new LikeEditingUI(this).open(player);
				else if(player.hasPermission(Main.OPERATOR_PERMISSION)) new AdministratorUI(this).open(player);
				else new LikeInformationUI(this).open(player);
			}else{
				if(isOwner(uuid)){
					Text.of("&c-自分のLikeはお気に入りに登録出来ません。").accept(player::sendMessage);
					return;
				}
				
				PlayerData data = plugin.players.get(uuid);
				if(data.favoriteLikes.containsKey(this)){
					Text.of("&c-このLikeは既にお気に入りに登録しています。").accept(player::sendMessage);
					return;
				}
				
				data.favoriteLike(this);
				Text.of("&a-このLikeをお気に入りに登録しました！", config.tip()).accept(player::sendMessage);
			}
		};
		Location loc = hologram.getLocation();
		loc.setPitch(90.0F);
		CraftTouchableLine line = (CraftTouchableLine) hologram.getLine(0);
		setTouchHandler.invoke(line, handler, loc.getWorld(), loc.getX(), loc.getY() - line.getHeight() * 3, loc.getZ());
	}
	
	@Override
	public String toString(){
		return owner.toString() + "," + favorites;
	}
	
}
