package amata1219.like.ui;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import amata1219.like.sound.SoundEffects;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.MainConfig.IconType;
import amata1219.like.config.MainConfig.InvitationText;
import amata1219.like.masquerade.dsl.InventoryUI;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.option.Lines;
import amata1219.like.masquerade.text.Text;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;

public class InvitationConfirmationUI implements InventoryUI {
	
	private final Main plugin = Main.plugin();
	private final Economy economy = plugin.economy();
	private final MainConfig config = plugin.config();
	private final Like like;
	private final InventoryUI previous;
	
	public InvitationConfirmationUI(Like like, InventoryUI previous){
		this.like = like;
		this.previous = previous;
	}
	
	@Override
	public Function<Player, Layout> layout() {
		return build(Lines.x1, (p, l) -> {
			l.title = "招待の実行確認画面";
			
			l.defaultSlot(s -> {
				s.icon(i -> {
					i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
					i.displayName = " ";
				});
			});
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.LIKE);
					i.displayName = Text.of("&a&l-%s").format(like.id);
					i.amount = Math.min(Math.max(like.favorites(), 1), 64);
					i.lore(
						Text.of("&7-%s").format(like.description()),
						"",
						Text.of("&7-お気に入り数: &a-%s").format(like.favorites()),
						Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()),
						Text.of("&7-ワールド: &a-%s").format(config.worldAlias(like.world())),
						Text.of("&7-座標: &a-X-&7-: &a-%s Y-&7-: &a-%s Z-&7-: &a-%s").format(like.x(), like.y(), like.z())
					);
				});
			}, 1);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.GO_TO_LIKE_TELEPORTATION_OR_LIKE_INVITATION_CONFIRMATION_PAGE);
					i.displayName = Text.of("&a-このLikeに近くのプレイヤーを招待する！ (%s" + config.unitOfCost() + ")").format(config.invitationCosts());
				});
				
				s.onClick(e -> {
					double costs = config.invitationCosts();
					if(!economy.has(p, costs)){
						Text.of("&c-所持金が足りません。招待するには%s" + config.unitOfCost() + "必要です。").apply(costs).sendTo(p);
						return;
					}
					
					final int radius = config.radiusOfInvitationScope();
					List<Player> playersNearby = p.getNearbyEntities(radius, radius, radius).stream()
							.filter(entity -> entity.getType() == EntityType.PLAYER)
							.map(entity -> (Player) entity)
							.collect(Collectors.toList());
					
					p.closeInventory();
					
					if(playersNearby.isEmpty()){
						Text.of("&c-近くに誰もいないため招待出来ませんでした。").sendTo(p);
						Text.of("&7-※招待コストは消費されていません。").sendTo(p);
						return;
					}
					
					InvitationText text = config.invitationText().apply(p, like);
					playersNearby.forEach(invitee -> text.clone().apply(invitee).accept(t -> {
						TextComponent component = new TextComponent(t);
						
						String command = Text.of("/liketoken %s %s").format(Main.INVITATION_TOKEN, like.id);
						component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
						
						TextComponent description = new TextComponent(Text.color("&7-クリックするとこのLikeにテレポートします！"));
						component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[] {description}));
						
						invitee.spigot().sendMessage(component);

						SoundEffects.OPERATED.play(invitee);
					}));
					
					economy.withdrawPlayer(p, costs);
					
					Text.of("&a-%s人のプレイヤーを招待しました。").apply(playersNearby.size()).accept(p::sendMessage);

					SoundEffects.SUCCEEDED.play(p);
				});
			}, 5);
			
			l.put(s -> {
				s.icon(i -> {
					i.material = config.material(IconType.CANCEL_LIKE_TELEPORTATION);
					i.displayName = Text.color("&c-前のページに戻る！");
				});
				
				s.onClick(e -> previous.open(p));
			}, 7);
		});
	}

}
