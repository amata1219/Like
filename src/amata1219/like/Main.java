package amata1219.like;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import amata1219.niflheimr.dsl.InventoryOperationListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import amata1219.like.bookmark.Bookmark;
import amata1219.like.bookmark.BookmarkDatabase;
import amata1219.like.command.BookmarkCommand;
import amata1219.like.command.LikeCommand;
import amata1219.like.command.LikeCreationCommand;
import amata1219.like.command.LikeListCommand;
import amata1219.like.command.LikeStatusCommand;
import amata1219.like.command.LikeOperatorCommand;
import amata1219.like.command.LikeTeleportationAuthenticationCommand;
import amata1219.like.config.LikeDatabase;
import amata1219.like.config.LikeLimitDatabase;
import amata1219.like.config.MainConfig;
import amata1219.like.listener.CreatePlayerDataListener;
import amata1219.like.listener.EditLikeDescriptionListener;
import amata1219.like.listener.UIListener;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.monad.Maybe;
import amata1219.like.player.PlayerData;
import amata1219.like.player.PlayerDatabase;
import amata1219.like.reflection.SafeCast;
import amata1219.like.tuplet.Tuple;
import at.pcgamingfreaks.UUIDConverter;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

public class Main extends JavaPlugin {
	
	private static Main plugin;
	
	public static final HashMap<UUID, String> UUID_CACHE = new HashMap<>();
	
	public static final String INVITATION_TOKEN = UUID.randomUUID().toString();
	public static final String OPERATOR_PERMISSION = "like.likeop";
	
	public static Main plugin(){
		return plugin;
	}
	
	public static String nameFrom(UUID uuid){
		String name = UUID_CACHE.get(uuid);
		if(name != null) return name;
		name = UUIDConverter.getNameFromUUID(uuid);
		UUID_CACHE.put(uuid, name);
		return name;
	}
	
	private Economy economy;
	
	private final HashMap<String, CommandExecutor> executors = new HashMap<>();
	
	private MainConfig config;
	private LikeDatabase likeDatabase;
	private PlayerDatabase playerDatabase;
	private LikeLimitDatabase likeLimitDatabase;
	private BookmarkDatabase bookmarkDatabase;
	
	public final HashMap<Long, Like> likes = new HashMap<>();
	public final HashMap<UUID, PlayerData> players = new HashMap<>();
	public final HashMap<String, Bookmark> bookmarks = new HashMap<>();
	public final HashMap<UUID, Long> descriptionEditors = new HashMap<>();
	public final HashSet<UUID> cooldownMap = new HashSet<>();
	
	@Override
	public void onEnable(){
		plugin = this;
		
		Plugin vault = getServer().getPluginManager().getPlugin("Vault");
		if(!(vault instanceof Vault)) throw new NullPointerException("Not found Vault.");

		RegisteredServiceProvider<Economy> provider = getServer().getServicesManager().getRegistration(Economy.class);
		if(provider == null) throw new NullPointerException("Not found Vault.");

		economy = provider.getProvider();

		Field acceptingNew;
		try {
			acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
			acceptingNew.setAccessible(true);
			acceptingNew.set(null, true);
			Enchantment.registerEnchantment(amata1219.niflheimr.enchantment.GleamEnchantment.INSTANCE);
			acceptingNew.set(null, false);
			acceptingNew.setAccessible(false);
		} catch (NoSuchFieldException | IllegalAccessException ignored) {

		}

		registerEventListeners(
			new UIListener(),
			new InventoryOperationListener(),
			new CreatePlayerDataListener(),
			new EditLikeDescriptionListener()
		);
		
		config = new MainConfig();
		
		executors.put("like", LikeCommand.executor);
		executors.put("likec", LikeCreationCommand.executor);
		executors.put("likel", LikeListCommand.executor);
		executors.put("likes", LikeStatusCommand.executor);
		executors.put("liketoken", LikeTeleportationAuthenticationCommand.executor);
		executors.put("likeb", BookmarkCommand.executor);
		executors.put("likeop", LikeOperatorCommand.executor);
		
		getServer().getScheduler().runTaskLater(this, () -> {
			likeDatabase = new LikeDatabase();
			Tuple<HashMap<Long, Like>, HashMap<UUID, List<Like>>> maps = likeDatabase.readAll();
			maps.first.forEach(likes::put);
			
			playerDatabase = new PlayerDatabase();
			playerDatabase.readAll(maps.second).forEach(players::put);
			
			likeLimitDatabase = new LikeLimitDatabase();
			bookmarkDatabase = new BookmarkDatabase();
			bookmarkDatabase.readAll().forEach(bookmarks::put);
		}, 5);
	}
	
	@Override
	public void onDisable(){
		bookmarkDatabase.writeAll();
		likeLimitDatabase.update();
		playerDatabase.writeAll();
		likeDatabase.writeAll();

		getServer().getOnlinePlayers().forEach(player -> {
			Maybe.unit(player.getOpenInventory())
			.map(InventoryView::getTopInventory)
			.map(Inventory::getHolder)
			.flatMap(x -> SafeCast.cast(x, Layout.class))
			.apply(x -> player.closeInventory());
		});

		HandlerList.unregisterAll(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		return executors.get(command.getName()).onCommand(sender, command, label, args);
	}
	
	private void registerEventListeners(Listener... listeners){
		for(Listener listener : listeners) getServer().getPluginManager().registerEvents(listener, this);
	}
	
	public Economy economy(){
		return economy;
	}
	
	public MainConfig config(){
		return config;
	}
	
	public LikeDatabase likeDatabase(){
		return likeDatabase;
	}
	
	public PlayerDatabase playerDatabase(){
		return playerDatabase;
	}
	
	public LikeLimitDatabase likeLimitDatabase(){
		return likeLimitDatabase;
	}
	
	public BookmarkDatabase bookmarkDatabase(){
		return bookmarkDatabase;
	}

}
