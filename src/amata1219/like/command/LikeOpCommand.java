package amata1219.like.command;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.gmail.filoghost.holographicdisplays.disk.HologramDatabase;

import amata1219.like.Like;
import amata1219.like.Util;

public class LikeOpCommand implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public void onCommand(CommandSender sender, Args args){
		switch(args.get()){
		case "move":
			if(Util.isNotPlayer(sender))
				return;

			Like move = Util.Likes.get(args.getNumber());
			if(move == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			Util.move(move, Util.castPlayer(sender).getLocation());
			Util.tell(sender, ChatColor.GREEN, "Like(" + move.getId() + ")を移動しました。");
			break;
		case "delete":
			Like delete = Util.Likes.get(args.getNumber());
			if(delete == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			Util.delete(delete);
			Util.tell(sender, ChatColor.GREEN, "Like(" + delete.getId() + ")を削除しました。");
			break;
		case "deleteplayer":
			OfflinePlayer target = Bukkit.getOfflinePlayer(args.get());
			if(target == null){
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
			World world = Bukkit.getWorld(args.get());
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
			Like change = Util.Likes.get(args.getNumber());
			if(change == null){
				Util.tell(sender, ChatColor.RED, "指定されたIDのLikeは存在しません。");
				return;
			}

			OfflinePlayer owner = Bukkit.getOfflinePlayer(args.get());
			if(owner == null || owner.getName() == null || owner.getName().equals("-1")){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤーは存在しません。");
				return;
			}

			Util.changeOwner(change, owner.getUniqueId());
			Util.tell(sender, ChatColor.GREEN, change.getStringId() + "のオーナーを" + owner.getName() + "に変更しました。");
			break;
		case "changedata":
			OfflinePlayer old = Bukkit.getOfflinePlayer(args.get());
			if(old == null || old.getName() == null || old.getName().equals("-1")){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤー(第1引数)は存在しません。");
				return;
			}

			UUID oldid = old.getUniqueId();
			if(!Util.Mines.containsKey(oldid)){
				Util.tell(sender, ChatColor.RED, "指定されたプレイヤー(第1引数)はLikeを作成していません。");
				return;
			}

			OfflinePlayer next = Bukkit.getOfflinePlayer(args.get());
			if(next == null || next.getName() == null || next.getName().equals("-1")){
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
		default:
			break;
		}
	}

}
