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
import amata1219.like.monad.Try;

public class Like {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd (E) HH:mm:ss");
	
	private final MainConfig config = Main.instance().config();
	
	public final long id;
	public final NamedHologram hologram;
	
	private UUID creator;
	private int favorites;
	
	public Like(NamedHologram hologram, UUID creator){
		id = Try.of(() -> Long.parseLong(hologram.getName()))
				.getOrElseThrow(() -> new IllegalArgumentException("Likeを読み込めませんでした(Hologram@" + hologram.getName() + ", Owner@" + creator + ")"));
		this.hologram = hologram;
		this.creator = creator;
		
		hologram.appendTextLine(config.likeFavoritesText(0));
		hologram.appendTextLine(config.likeExplanation(creator));
		hologram.appendTextLine(config.likeUsage());
		
		//OldMain.applyTouchHandler(this, false);
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
	
	public UUID creator(){
		return creator;
	}
	
	public void setCreator(UUID uuid){
		this.creator = Objects.requireNonNull(uuid);
	}
	
	public boolean isCreator(UUID uuid){
		return creator.equals(uuid);
	}
	
	public String lore(){
		return ((TextLine) hologram.getLine(1)).getText();
	}
	
	public void setLore(String lore){
		rewriteHologramLine(1, lore);
	}
	
	public int favorites(){
		return favorites;
	}
	
	public void incrementFavorites(){
		favorites++;
		rewriteHologramLine(0, config.likeFavoritesText(favorites));
	}
	
	public void decrementFavorites(){
		favorites = Math.min(favorites - 1, 0);
		rewriteHologramLine(0, config.likeFavoritesText(favorites));
	}
	
	private void rewriteHologramLine(int index, String text){
		List<CraftHologramLine> lines = hologram.getLinesUnsafe();
		((CraftHologramLine) lines.get(index)).despawn();
		lines.set(index, HologramDatabase.readLineFromString(text, hologram));
		hologram.refreshAll();
		HologramDatabase.saveHologram(hologram);
	}
	
	/*private void save(){
		HologramDatabase.saveHologram(hologram);
		HologramDatabase.trySaveToDisk();
	}*/
	
	public String creationTimestamp(){
		return DATE_FORMAT.format(id);
	}
	
	@Override
	public String toString(){
		return creator.toString() + "," + favorites;
	}
	
}
