package amata1219.like;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import amata1219.like.masquerade.dsl.InventoryUI;
import com.gmail.filoghost.holographicdisplays.disk.StringConverter;
import com.gmail.filoghost.holographicdisplays.object.line.CraftTextLine;
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
import amata1219.like.masquerade.task.AsyncTask;
import amata1219.like.masquerade.text.Text;
import amata1219.like.playerdata.PlayerData;
import amata1219.like.ui.AdministratorUI;
import amata1219.like.ui.LikeEditingUI;
import amata1219.like.ui.LikeInformationUI;
import org.bukkit.entity.Player;

public class Like {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd (E) HH:mm:ss");
	private static final Method setTouchHandler;

	static {
		Method touchHandlerSetter = null;
		try {
			touchHandlerSetter = CraftTouchableLine.class.getDeclaredMethod("setTouchHandler", TouchHandler.class, World.class, double.class, double.class, double.class);
			touchHandlerSetter.setAccessible(true);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		setTouchHandler = touchHandlerSetter;
	}
			
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
		enableTouchHandler();
	}
	
	public Like(NamedHologram hologram, UUID owner){
		this.id = Long.parseLong(hologram.getName());
		this.hologram = hologram;
		this.owner = owner;
		
		appendTextLine(config.likeFavoritesText().apply(favorites));
		appendTextLine(config.likeDescription().apply(owner));
		appendTextLine(config.likeUsage());

		enableTouchHandler();
	}

	private void appendTextLine(String text) {
		CraftTextLine line = hologram.appendTextLine(text);
		line.setSerializedConfigValue(text);
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
		PlayerData newOwner = plugin.players.get(uuid);
		if (newOwner.isFavoriteLike(this)) {
			decrementFavorites();
			newOwner.unfavoriteLike(this);
		}
		plugin.players.get(owner).unregisterLike(this);
		this.owner = uuid;
		newOwner.registerLike(this);
	}
	
	public boolean isOwner(UUID uuid){
		return owner.equals(uuid);
	}
	
	public String ownerName(){
		return Main.nameFrom(owner);
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
		lines.get(index).despawn();
		String formattedText = StringConverter.toReadableFormat(text);
		CraftTextLine line = new CraftTextLine(hologram, formattedText);
		line.setSerializedConfigValue(formattedText);
		lines.set(index, line);
		hologram.refreshAll();
		if (index == 0) {
			disableTouchHandler();
			enableTouchHandler();
		}
		save();
	}
	
	public String creationTimestamp(){
		return DATE_FORMAT.format(id);
	}
	
	public void teleportTo(Location loc){
		hologram.teleport(loc.add(0, 2, 0));
		hologram.despawnEntities();
		hologram.refreshAll();
		disableTouchHandler();
		enableTouchHandler();
		save();
	}
	
	public void save(){
		HologramDatabase.saveHologram(hologram);
		HologramDatabase.trySaveToDisk();
	}
	
	public void delete(boolean alsoSave){
		plugin.players.get(owner).unregisterLike(this);
		AsyncTask.define(() -> plugin.players.values().forEach(data -> data.unfavoriteLike(this))).execute();
		plugin.bookmarks.values().forEach(bookmark -> bookmark.likes.remove(this));
		plugin.likes.remove(id);
		plugin.likeDatabase().remove(this);
		hologram.delete();
		NamedHologramManager.removeHologram(hologram);
		HologramDatabase.deleteHologram(hologram.getName());
		if(alsoSave) HologramDatabase.trySaveToDisk();
	}

	private void enableTouchHandler() {
		setTouchHandler(this::touchHandler);
	}

	private void touchHandler(Player player) {
		if (player.isSneaking()) {
			InventoryUI ui;
			if (isOwner(player.getUniqueId())) ui = new LikeEditingUI(this);
			else if (player.hasPermission(Main.OPERATOR_PERMISSION)) ui = new AdministratorUI(this);
			else ui = new LikeInformationUI(this);
			ui.open(player);
		}else{
			if (isOwner(player.getUniqueId())) {
				Text.of("&c-自分のLikeはお気に入りに登録できません。").sendTo(player);
				return;
			}

			PlayerData data = plugin.players.get(player.getUniqueId());
			if(data.isFavoriteLike(this)){
				Text.of("&c-このLikeは既にお気に入りに登録しています。").sendTo(player);
				return;
			}

			data.favoriteLike(this);
			incrementFavorites();
			Text.of("&a-このLikeをお気に入りに登録しました！", config.tip()).sendTo(player);
		}
	}

	private void disableTouchHandler() {
		setTouchHandler(null);
	}

	private void setTouchHandler(TouchHandler handler){
		Location loc = hologram.getLocation();
		loc.setPitch(90.0F);
		CraftTouchableLine line = (CraftTouchableLine) hologram.getLine(0);
		setTouchHandler(line, handler, loc.getWorld(), loc.getX(), loc.getY() - line.getHeight() * 3, loc.getZ());
		line = (CraftTouchableLine) hologram.getLine(2);
		setTouchHandler(line, handler, loc.getWorld(), loc.getX(), loc.getY() - line.getHeight() * 1, loc.getZ());
	}

	private static void setTouchHandler(CraftTouchableLine line, TouchHandler handler, World world, double x, double y, double z) {
		try {
			setTouchHandler.invoke(line, handler, world, x, y, z);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String toString(){
		return owner.toString() + "," + favorites;
	}
	
}
