package amata1219.like;

import java.text.SimpleDateFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;
import com.gmail.filoghost.holographicdisplays.object.NamedHologram;
import com.gmail.filoghost.holographicdisplays.object.line.CraftHologramLine;

public class Like {

	private static final SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd (E) HH:mm:ss");

	private final NamedHologram hologram;
	private UUID owner;
	private int likeCount;

	public Like(NamedHologram hologram, UUID owner){
		this.hologram = hologram;
		this.owner = owner;
		this.likeCount = 0;

		hologram.appendTextLine(Util.Counter.replace(Util.PLACE_HOLDER_OF_LIKE_COUNT, "0"));
		hologram.appendTextLine(Util.Lore.replace(Util.PLACE_HOLDER_OF_PLAYER_NAME, Util.getName(owner)));
		hologram.appendTextLine(Util.Message);

		Main.applyTouchHandler(this, false);
	}

	public Like(NamedHologram hologram, UUID owner, int likeCount){
		this.hologram = hologram;
		this.owner = owner;
		this.likeCount = likeCount;
	}

	public NamedHologram getHologram(){
		return hologram;
	}

	public long getId(){
		return Long.parseLong(hologram.getName());
	}

	public String getStringId(){
		return String.valueOf(hologram.getName());
	}

	public String getCreationTimestamp(){
		return Like.format.format(getId());
	}

	public UUID getOwner(){
		return owner;
	}

	public void setOwner(UUID owner){
		this.owner = owner;
	}

	public boolean isOwner(UUID uuid){
		return owner.equals(uuid);
	}

	public int getLikeCount(){
		return likeCount;
	}

	public int incrementLikeCount(){
		likeCount++;
		updateCounter();
		return likeCount;
	}

	public int decrementLikeCount(){
		if(likeCount > 0)
			likeCount--;

		updateCounter();
		return likeCount;
	}

	public void updateCounter(){
		((CraftHologramLine) hologram.getLinesUnsafe().get(0)).despawn();
		hologram.getLinesUnsafe().set(0, HologramDatabase.readLineFromString(Util.Counter.replace(Util.PLACE_HOLDER_OF_LIKE_COUNT, String.valueOf(likeCount)), hologram));
		refresh();
		save(true);
	}

	public void editLore(String lore){
		((CraftHologramLine) hologram.getLinesUnsafe().get(1)).despawn();
		hologram.getLinesUnsafe().set(1, HologramDatabase.readLineFromString(lore.replace(Util.PLACE_HOLDER_OF_PLAYER_NAME, Bukkit.getOfflinePlayer(owner).getName()), hologram));
		refresh();
		save(true);
	}

	public World getWorld(){
		return hologram.getWorld();
	}

	public int getX(){
		return Util.toInt(hologram.getX());
	}

	public int getY(){
		return Util.toInt(hologram.getY());
	}

	public int getZ(){
		return Util.toInt(hologram.getZ());
	}

	public Location getLocation(Location location){
		Location loc = location.clone();
		loc.setWorld(getWorld());
		loc.setX(hologram.getX());
		loc.setY(hologram.getY());
		loc.setZ(hologram.getZ());
		return loc;
	}

	public void move(Location location){
		hologram.teleport(location);
	}

	public String getLore(){
		return Util.castTextLine(hologram.getLine(1)).getText();
	}

	public void refresh(){
		hologram.refreshAll();
	}

	public void save(boolean apply){
		HologramDatabase.saveHologram(hologram);

		if(apply)
			HologramDatabase.trySaveToDisk();
	}

	@Override
	public String toString(){
		return owner.toString() + "," + likeCount;
	}

}
