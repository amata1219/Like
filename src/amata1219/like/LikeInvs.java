package amata1219.like;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.milkbowl.vault.economy.Economy;

public class LikeInvs {

	private List<Inventory> mines = new LinkedList<>();
	private List<Inventory> likes = new LinkedList<>();
	private List<Long> mineList = new ArrayList<>();
	private List<Long> likeList = new ArrayList<>();
	private int mineLen;
	private int likeLen;

	public LikeInvs(UUID uuid){
		Util.MyLikes.get(uuid).getLikes().parallelStream()
		.forEach(this::addLike);

		if(!Util.Mines.containsKey(uuid))
			return;

		Util.Mines.get(uuid).parallelStream()
		.forEach(this::addMine);
	}

	public boolean hasMine(Like like){
		return mineList.contains(like.getId());
	}

	public void addMine(Like like){
		if(isFull(mineLen))
			mines.add(newPage(mines.size() - 1, true));

		mines.get(mines.size() - 1).addItem(newIcon(like, true));
		mineList.add(like.getId());
		mineLen++;
	}

	public void removeMine(Like like){
		ItemStack item = newIcon(like, true);
		for(Inventory inventory : mines){
			if(!inventory.contains(item))
				continue;

			inventory.remove(item);
			mineList.remove((Object) like.getId());
			mineLen--;
			break;
		}
	}

	public void moveMine(Like like){
		removeMine(like);
		addMine(like);
	}

	public Inventory firstMine(){
		return mines.get(0);
	}

	public boolean hasBeforeMine(int page){
		return page > 0;
	}

	public Inventory getBeforeMine(int page){
		if(!hasBeforeMine(page))
			return mines.get(page);

		return mines.get(page - 1);
	}

	public boolean hasNextMine(int page){
		return page < mines.size() - 1;
	}

	public Inventory getNextMine(int page){
		if(!hasNextMine(page))
			return mines.get(page);

		return mines.get(page + 1);
	}

	public boolean hasLike(Like like){
		return likeList.contains(like.getId());
	}

	public void addLike(Like like){
		if(isFull(likeLen))
			likes.add(newPage(likes.size() - 1, false));

		likes.get(likes.size() - 1).addItem(newIcon(like, false));
		likeList.add(like.getId());
		likeLen++;
	}

	public void removeLike(Like like){
		String id = like.getStringId();
		for(Inventory inventory : likes){
			CraftInventory
			if(!inventory.contains(item))
				continue;

			inventory.remove(item);
			likeList.remove((Object) like.getId());
			likeLen--;
			break;
		}
	}

	public void moveLike(Like like){
		removeLike(like);
		addLike(like);
	}

	public Inventory firstLike(){
		return likes.get(0);
	}

	public boolean hasBeforeLike(int page){
		return page > 0;
	}

	public Inventory getBeforeLike(int page){
		if(!hasBeforeLike(page))
			return likes.get(page);

		return likes.get(page - 1);
	}

	public boolean hasNextLike(int page){
		return page < likes.size() - 1;
	}

	public Inventory getNextLike(int page){
		if(!hasNextLike(page))
			return likes.get(page);

		return likes.get(page + 1);
	}

	private boolean isFull(int len){
		return len % 52 == 0;
	}

	private Inventory newPage(int page, boolean isMine){
		Inventory inventory = Util.createInventory(54, (isMine ? "Mine" : "MyLike") + String.valueOf(page));
		inventory.setItem(45, Util.newItem(Util.PageButton, ChatColor.GREEN + "前のページへ"));
		inventory.setItem(53, Util.newItem(Util.PageButton, ChatColor.GREEN + "次のページへ"));
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
		lore.add("");
		lore.add(ChatColor.GRAY + "操作説明");
		Economy economy = Main.getEconomy();
		lore.add(ChatColor.GRAY + "左クリック - Likeの座標にテレポート(コスト: " + economy.format(Util.Tp) + ")");
		lore.add(ChatColor.GRAY + "右クリック - 半径" + Util.Range + "マス以内にいるプレイヤーに招待ボタンを表示(コスト: " + economy.format(Util.Invite) + ")");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static Like toLike(ItemStack icon){
		if(icon == null || icon.getType() == Material.AIR)
			return null;

		ItemMeta meta = icon.getItemMeta();
		if(meta == null || !meta.hasDisplayName())
			return null;

		return Util.Likes.get(Long.parseLong(meta.getDisplayName().substring(2)));
	}

}
