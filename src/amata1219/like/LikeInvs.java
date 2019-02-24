package amata1219.like;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LikeInvs {

	public static final ChatColor MINES_COLOR = ChatColor.GREEN;
	public static final ChatColor LIKES_COLOR = ChatColor.BLUE;

	private List<Inventory> mines = new LinkedList<>();
	private List<Inventory> likes = new LinkedList<>();
	private int mineLen;
	private int likeLen;

	public LikeInvs(UUID uuid){
		Util.Mines.get(uuid).parallelStream()
		.forEach(this::addMine);

		Util.MyLikes.get(uuid).getLikes().parallelStream()
		.forEach(this::addLike);
	}

	public void addMine(Like like){
		if(isFull(mineLen))
			mines.add(newPage(mines.size() - 1, true));

		mines.get(mines.size() - 1).addItem(newIcon(like, true));
		mineLen++;
	}

	public void removeMine(Like like){
		ItemStack item = newIcon(like, true);
		for(Inventory inventory : mines){
			if(!inventory.contains(item))
				continue;

			inventory.remove(item);
			mineLen--;
			break;
		}
	}

	public void moveMine(Like like){
		removeMine(like);
		addMine(like);
	}

	public void addLike(Like like){
		if(isFull(likeLen))
			likes.add(newPage(likes.size() - 1, false));

		likes.get(likes.size() - 1).addItem(newIcon(like, false));
		likeLen++;
	}

	public void removeLike(Like like){
		ItemStack item = newIcon(like, false);
		for(Inventory inventory :likes){
			if(!inventory.contains(item))
				continue;

			inventory.remove(item);
			likeLen--;
			break;
		}
	}

	public void moveLike(Like like){
		removeLike(like);
		addLike(like);
	}

	private boolean isFull(int len){
		return len % 52 == 0;
	}

	private Inventory newPage(int page, boolean isMine){
		Inventory inventory = Util.createInventory(54, String.valueOf(page));
		inventory.setItem(45, Util.newItem(Util.PageButton, (isMine ? LikeInvs.MINES_COLOR : LikeInvs.LIKES_COLOR) + "前のページへ"));
		inventory.setItem(53, Util.newItem(Util.PageButton, (isMine ? LikeInvs.MINES_COLOR : LikeInvs.LIKES_COLOR) + "次のページへ"));
		return inventory;
	}

	private ItemStack newIcon(Like like, boolean isMine){
		ItemStack item = Util.newItem(Util.LikeIcon, like.getStringId());
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add(ChatColor.GRAY + like.getLore().getText());
		if(!isMine)
			lore.add(ChatColor.GRAY + "作成者 - " + Util.getName(like.getOwner()));
		lore.add(ChatColor.GRAY + "お気に入り数 - " + like.getLikeCount());
		lore.add(ChatColor.GRAY + "作成日 - " + like.getCreationTimestamp());
		lore.add(ChatColor.GRAY + "ワールド - " + Util.Worlds.get(like.getWorld().getName()));
		lore.add(ChatColor.GRAY + "座標 - X:" + like.getX() + " Y: " + like.getY() + " Z: " + like.getZ());
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

}
