package amata1219.like.ui;

import java.util.function.Function;

import amata1219.like.sound.SoundEffects;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.like.masquerade.dsl.InventoryUI;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.item.Skull;
import amata1219.like.masquerade.option.Lines;
import amata1219.like.masquerade.text.Text;

public class LikeEditingUI implements InventoryUI {
	
	private final MainConfig config = Main.plugin().config();
	private final Like like;
	
	public LikeEditingUI(Like like){
		this.like = like;
	}

	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x1, (p, l) -> {
			l.title = "Likeの編集";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			l.put(s -> {
				s.icon(i -> {
					i.basedItemStack = Skull.createFrom(like.owner());
					i.displayName = Text.of("&a-作成者-&7-:-&f %s").format(like.ownerName());
				});
			}, 1);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.FAVORITES);
					i.displayName = Text.of("&a-お気に入りの数-&7-:-&f %s").format(like.favorites());
					i.amount = Math.min(Math.max(like.favorites(), 1), 64);
				});
			}, 3);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.CREATION_TIMESTAMP);
					i.displayName = Text.of("&a-作成日時-&7-:-&f %s").format(like.creationTimestamp());
				});
			}, 4);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.ID);
					i.displayName = Text.of("&a-ID-&7-:-&f %s").format(like.id);
					i.lore(
							ChatColor.GRAY + "クリックでチャット欄にこのLikeのIDを出力します。",
							ChatColor.GRAY + "左クリックの場合は出力と同時にインベントリを閉じます。",
							ChatColor.GRAY + "右クリックの場合はインベントリは閉じません。",
							ChatColor.GRAY + "出力されたIDはクリックでクリップボードにコピーすることができます。"
					);
				});

				s.onClick(e -> {
					TextComponent component = new TextComponent(ChatColor.GREEN + "[Like]: このLikeのIDは " + like.id + " です。クリックでIDをコピーできます。");
					component.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(like.id)));
					component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(ChatColor.GRAY + "Click to copy the ID!")));
					p.spigot().sendMessage(component);
					if (e.clickType.isLeftClick()) p.closeInventory();
					SoundEffects.OPERATED.play(p);
				});
			}, 5);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.EDIT_DESCRIPTION);
					i.displayName = Text.color("&a-表示内容の編集");
				});
				
				s.onClick(e -> {
					Main.plugin().descriptionEditors.put(p.getUniqueId(), like.id);
					p.closeInventory();
					Text.of(
						"&a-新しい表示内容をチャット欄に入力して下さい。",
						"cancelと入力した場合はキャンセルされます。"
					).sendTo(p);
					SoundEffects.OPERATED.play(p);
				});
			}, 6);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.GO_TO_LIKE_DELETION_PAGE);
					i.displayName = Text.color("&c-Likeの削除");
				});
				
				s.onClick(e -> {
					new DeletingLikeConfirmationUI(like).open(p);
					SoundEffects.OPERATED.play(p);
				});
			}, 7);
		});
	}
	
}
