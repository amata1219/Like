package amata1219.like.command;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;

import amata1219.like.Config;
import amata1219.like.OldLike;
import amata1219.like.Util;

public class LikeOpCommand implements CommandExecutor {
	
	private static final Pattern UUID_PATTERN = Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
	
	@SuppressWarnings("deprecation")
	@Override
	public void onCommand(CommandSender sender, Args args){
		switch(args.next()){
		case "move":
			if(Util.isNotPlayer(sender))
				return;

			if(!args.hasNextLong()){
				Util.tell(sender, ChatColor.RED, "移動するLikeのIDを指定して下さい。。");
				return;
			}

			OldLike move = Util.Likes.get(args.nextLong());
			if(move == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			Util.move(move, Util.castPlayer(sender).getLocation());
			Util.tell(sender, ChatColor.GREEN, "Like(" + move.getId() + ")を移動しました。");
			break;
		case "delete":
			if(!args.hasNextLong()){
				Util.tell(sender, ChatColor.RED, "削除するLikeのIDを指定して下さい。。");
				return;
			}

			OldLike delete = Util.Likes.get(args.nextLong());
			if(delete == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			Util.delete(delete);
			Util.tell(sender, ChatColor.GREEN, "Like(" + delete.getId() + ")を削除しました。");
			break;
		case "deleteplayer":{
			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "プレイヤーを指定して下さい。");
				return;
			}

			Optional<OfflinePlayer> offline = getPlayer(args.next());
			if(!offline.isPresent()){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーは存在しません。");
				return;
			}
			
			OfflinePlayer player = offline.get();
			UUID uuid = player.getUniqueId();
			if(!Util.Mines.containsKey(uuid)){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーはLikeを作成していません。");
				return;
			}

			int counter = 0;
			for(OldLike like : new ArrayList<>(Util.Mines.get(uuid))){
				Util.nonSaveDelete(like);
				counter++;
			}
			HologramDatabase.trySaveToDisk();
			Util.tell(sender, ChatColor.GREEN, player.getName() + "が作成したLike(" + counter + "個)を全て削除しました。");
			break;
		}case "deleteworld":
			World world = Bukkit.getWorld(args.next());
			if(world == null){
				Util.tell(sender, ChatColor.RED, "指定されたワールドは存在しません。");
				return;
			}

			int count = 0;
			for(OldLike like : new ArrayList<>(Util.Likes.values())){
				if(!like.getWorld().equals(world))
					continue;

				Util.nonSaveDelete(like);
				count++;
			}
			HologramDatabase.trySaveToDisk();
			Util.tell(sender, ChatColor.GREEN, world.getName() + "ワールドに存在するLike(" + count +"個)を全て削除しました。");
			break;
		case "changeowner":{
			if(!args.hasNextLong()){
				Util.tell(sender, ChatColor.RED, "編集するLikeのIDを指定して下さい。。");
				return;
			}

			OldLike change = Util.Likes.get(args.nextLong());
			if(change == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "プレイヤーを指定して下さい。");
				return;
			}

			Optional<OfflinePlayer> offline = getPlayer(args.next());
			if(!offline.isPresent()){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーは存在しません。");
				return;
			}
			
			OfflinePlayer player = offline.get();
			Util.changeOwner(change, player.getUniqueId());
			Util.tell(sender, ChatColor.GREEN, change.getStringId() + "のオーナーを" + player.getName() + "に変更しました。");
			break;
		}case "changedata":{
			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "プレイヤーを指定して下さい。");
				return;
			}

			OfflinePlayer old = Bukkit.getOfflinePlayer(args.next());
			if(old == null || !old.hasPlayedBefore() || old.getName() == null || old.getName().equals("-1")){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤー(第1引数)は存在しません。");
				return;
			}

			UUID oldid = old.getUniqueId();
			if(!Util.Mines.containsKey(oldid)){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤー(第1引数)はLikeを作成していません。");
				return;
			}

			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "プレイヤーを指定して下さい。");
				return;
			}

			Optional<OfflinePlayer> offline = getPlayer(args.next());
			if(!offline.isPresent()){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーは存在しません。");
				return;
			}
			
			OfflinePlayer player = offline.get();
			int cout = 0;
			for(OldLike like : new ArrayList<>(Util.Mines.get(oldid))){
				Util.changeOwner(like, player.getUniqueId());
				cout++;
			}

			Util.tell(sender, ChatColor.GREEN, old.getName() + "のLike(" + cout + "個)のオーナーを" + player.getName() + "に変更しました。");
			break;
		}case "reload":
			Util.Config.reload();
			Util.loadConfigValues();
			Util.tell(sender, ChatColor.GREEN, "コンフィグを再読み込みしました。");
			break;
		case "limit":{
			String s = args.next();
			
			Flag flag = s.equals("add") ? Flag.ADD : (s.equals("sub") ? Flag.SUB : Flag.SET);
			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "プレイヤーを指定して下さい。");
				return;
			}
			
			Optional<OfflinePlayer> offline = getPlayer(args.next());
			if(!offline.isPresent()){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーは存在しません。");
				return;
			}
			
			OfflinePlayer player = offline.get();
			if(!args.hasNextInt()){
				Util.tell(sender, ChatColor.RED, "上限数を指定して下さい。");
				return;
			}

			String su = player.getUniqueId().toString();
			Config c = Util.LimitConfig;
			FileConfiguration fc = c.get();
			int limit = args.nextInt();
			switch(flag){
			case SET:
				break;
			case ADD:
				limit += fc.getInt(su);
				break;
			case SUB:
				limit = Math.max(fc.getInt(su) - limit, 0);
				break;
			}
			fc.set(su, limit);
			c.update();
			Util.tell(sender, ChatColor.GREEN, player.getName() + "さんのLike作成上限数を" + limit + "に設定しました。");
			break;
		}default:
			break;
		}
	}
	
	@SuppressWarnings("deprecation")
	private Optional<OfflinePlayer> getPlayer(String nameOrText){
		OfflinePlayer player = null;
		if(UUID_PATTERN.matcher(nameOrText).find()) player = Bukkit.getOfflinePlayer(UUID.fromString(nameOrText));
		else player = Bukkit.getOfflinePlayer(nameOrText);
		if(player != null && (!player.hasPlayedBefore() || player.getName().equals("-1"))) player = null;
		return Optional.ofNullable(player);
	}

	public enum Flag {

		SET,
		ADD,
		SUB;

	}

}
