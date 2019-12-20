package amata1219.like;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.World;

import com.gmail.filoghost.holographicdisplays.api.line.TextLine;
import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;
import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.line.CraftHologramLine;

import amata1219.like.config.MainConfig;

public class Like {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd (E) HH:mm:ss");
	
	private final MainConfig config = Main.plugin().config();
	
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
		
		//OldMain.applyTouchHandler(this, false);
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
	
	public void setCreator(UUID uuid){
		this.owner = Objects.requireNonNull(uuid);
		
		/*
		 * old creator
		 * 
		 * data.myLikes.remove(this)
		 * 
		 * new creator
		 * 
		 * data.myLikes.add(this)
		 */
	}
	
	public boolean isCreator(UUID uuid){
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
		HologramDatabase.saveHologram(hologram);
		HologramDatabase.trySaveToDisk();
	}
	
	public String creationTimestamp(){
		return DATE_FORMAT.format(id);
	}
	
	@Override
	public String toString(){
		return owner.toString() + "," + favorites;
	}
	
}
