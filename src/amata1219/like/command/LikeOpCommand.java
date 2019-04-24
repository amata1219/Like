package amata1219.like.command;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;

import amata1219.like.Config;
import amata1219.like.Like;
import amata1219.like.Util;

public class LikeOpCommand implements CommandExecutor {

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

			Like move = Util.Likes.get(args.nextLong());
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

			Like delete = Util.Likes.get(args.nextLong());
			if(delete == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			Util.delete(delete);
			Util.tell(sender, ChatColor.GREEN, "Like(" + delete.getId() + ")を削除しました。");
			break;
		case "deleteplayer":
			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "プレイヤーを指定して下さい。");
				return;
			}

			OfflinePlayer target = Bukkit.getOfflinePlayer(args.next());
			if(target == null || !target.hasPlayedBefore() || target.getName() == null || target.getName().equals("-1")){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーは存在しません。");
				return;
			}

			UUID uuid = target.getUniqueId();
			if(!Util.Mines.containsKey(uuid)){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーはLikeを作成していません。");
				return;
			}

			int counter = 0;
			for(Like like : new ArrayList<>(Util.Mines.get(uuid))){
				Util.nonSaveDelete(like);
				counter++;
			}
			HologramDatabase.trySaveToDisk();
			Util.tell(sender, ChatColor.GREEN, target.getName() + "が作成したLike(" + counter + "個)を全て削除しました。");
			break;
		case "deleteworld":
			World world = Bukkit.getWorld(args.next());
			if(world == null){
				Util.tell(sender, ChatColor.RED, "指定されたワールドは存在しません。");
				return;
			}

			int count = 0;
			for(Like like : new ArrayList<>(Util.Likes.values())){
				if(!like.getWorld().equals(world))
					continue;

				Util.nonSaveDelete(like);
				count++;
			}
			HologramDatabase.trySaveToDisk();
			Util.tell(sender, ChatColor.GREEN, world.getName() + "ワールドに存在するLike(" + count +"個)を全て削除しました。");
			break;
		case "changeowner":
			if(!args.hasNextLong()){
				Util.tell(sender, ChatColor.RED, "編集するLikeのIDを指定して下さい。。");
				return;
			}

			Like change = Util.Likes.get(args.nextLong());
			if(change == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "プレイヤーを指定して下さい。");
				return;
			}

			OfflinePlayer owner = Bukkit.getOfflinePlayer(args.next());
			if(owner == null || !owner.hasPlayedBefore() || owner.getName() == null || owner.getName().equals("-1")){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーは存在しません。");
				return;
			}

			Util.changeOwner(change, owner.getUniqueId());
			Util.tell(sender, ChatColor.GREEN, change.getStringId() + "のオーナーを" + owner.getName() + "に変更しました。");
			break;
		case "changedata":
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

			OfflinePlayer next = Bukkit.getOfflinePlayer(args.next());
			if(next == null || !next.hasPlayedBefore() || next.getName() == null || next.getName().equals("-1")){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤー(第2引数)は存在しません。");
				return;
			}

			int cout = 0;
			for(Like like : new ArrayList<>(Util.Mines.get(oldid))){
				Util.changeOwner(like, next.getUniqueId());
				cout++;
			}

			Util.tell(sender, ChatColor.GREEN, old.getName() + "のLike(" + cout + "個)のオーナーを" + next.getName() + "に変更しました。");
			break;
		case "reload":
			Util.Config.reload();
			Util.loadConfigValues();
			Util.tell(sender, ChatColor.GREEN, "コンフィグを再読み込みしました。");
			break;
		case "limit":
			String s = args.next();
			Flag flag = s.equals("add") ? Flag.ADD : (s.equals("sub") ? Flag.SUB : Flag.SET);
			if(!args.hasNext()){
				Util.tell(sender, ChatColor.RED, "プレイヤーを指定して下さい。");
				return;
			}

			OfflinePlayer offp = Bukkit.getOfflinePlayer(args.next());
			if(offp == null || !offp.hasPlayedBefore() || offp.getName() == null || offp.getName().equals("-1")){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤー(第1引数)は存在しません。");
				return;
			}

			if(!args.hasNextInt()){
				Util.tell(sender, ChatColor.RED, "上限数を指定して下さい。");
				return;
			}

			String su = offp.getUniqueId().toString();
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
			System.out.println("test: " + limit);
			fc.set(su, limit);
			c.update();
			Util.tell(sender, ChatColor.GREEN, offp.getName() + "さんのLike作成上限数を" + limit + "に設定しました。");
			break;
		default:
			break;
		}
	}

	public enum Flag {

		SET,
		ADD,
		SUB;

	}

}
