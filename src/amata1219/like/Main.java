package amata1219.like;

import amata1219.like.bookmark.Bookmark;
import amata1219.like.bookmark.BookmarkDatabase;
import amata1219.like.chunk.LikeMap;
import amata1219.like.command.*;
import amata1219.like.config.LikeDatabase;
import amata1219.like.config.LikeLimitDatabase;
import amata1219.like.config.MainConfig;
import amata1219.like.config.TourConfig;
import amata1219.like.listener.ControlLikeViewListener;
import amata1219.like.listener.CreatePlayerDataListener;
import amata1219.like.listener.EditingLikeDescriptionListener;
import amata1219.like.listener.UIListener;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.enchantment.GleamEnchantment;
import amata1219.like.playerdata.PlayerData;
import amata1219.like.playerdata.PlayerDatabase;
import amata1219.like.task.TourRegularNotificationTask;
import amata1219.like.tuplet.Tuple;
import at.pcgamingfreaks.UUIDConverter;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

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
	private TourConfig tourConfig;
	
	public final HashMap<Long, Like> likes = new HashMap<>();
	public final LikeMap likeMap = new LikeMap();
	public final HashMap<UUID, PlayerData> players = new HashMap<>();
	public final HashMap<String, Bookmark> bookmarks = new HashMap<>();
	public final HashMap<UUID, Long> descriptionEditors = new HashMap<>();
	public final HashSet<UUID> cooldownMap = new HashSet<>();

	public ControlLikeViewListener controlLikeViewListener;

	public BukkitTask tourRegularNotificationTask;

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
			Enchantment.registerEnchantment(GleamEnchantment.INSTANCE);
			acceptingNew.set(null, false);
			acceptingNew.setAccessible(false);
		} catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException ignored) {

		}

		registerEventListeners(
			new UIListener(),
			new CreatePlayerDataListener(),
			new EditingLikeDescriptionListener(),
			controlLikeViewListener = new ControlLikeViewListener()
		);
		
		config = new MainConfig();
		
		executors.put("like", new LikeCommand());
		executors.put("likec", LikeCreationCommand.INSTANCE);
		executors.put("likel", LikeListCommand.INSTANCE);
		executors.put("likes", new LikeStatusCommand());
		executors.put("liketoken", new LikeTokenCommand());
		executors.put("likeb", new BookmarkCommand());
		executors.put("likeop", new LikeOperatorCommand());
		executors.put("likerandom", new LikeRandomCommand());
		executors.put("liket", LikeTourCommand.INSTANCE);
		executors.put("likesearch", new LikeSearchCommand());
		executors.put("likeorscu", new OpenRangeSearchConfirmationUICommand());
		
		getServer().getScheduler().runTaskLater(this, () -> {
			likeDatabase = new LikeDatabase();
			Tuple<HashMap<Long, Like>, HashMap<UUID, List<Like>>> maps = likeDatabase.readAll();
			maps.first.forEach(likes::put);

			for (Like like : likes.values()) likeMap.put(like);
			
			playerDatabase = new PlayerDatabase();
			playerDatabase.readAll(maps.second).forEach(players::put);
			
			likeLimitDatabase = new LikeLimitDatabase();
			bookmarkDatabase = new BookmarkDatabase();
			bookmarkDatabase.readAll().forEach(bookmarks::put);

			tourConfig = new TourConfig();
			if (tourConfig.notificationIsEnabled()) tourRegularNotificationTask = getServer().getScheduler().runTaskTimerAsynchronously(this, new TourRegularNotificationTask(tourConfig), tourConfig.notificationIntervalTicks(), tourConfig.notificationIntervalTicks());
		}, 15);
	}
	
	@Override
	public void onDisable(){
		if (tourRegularNotificationTask != null) tourRegularNotificationTask.cancel();

		bookmarkDatabase.writeAll();
		likeLimitDatabase.update();
		playerDatabase.writeAll();
		likeDatabase.writeAll();

		for (Player player : getServer().getOnlinePlayers()) {
			if (player.getOpenInventory().getTopInventory().getHolder() instanceof Layout) player.closeInventory();
		}

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

	public TourConfig tourConfig() {
		return tourConfig;
	}

}
