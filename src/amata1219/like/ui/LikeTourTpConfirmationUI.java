package amata1219.like.ui;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.config.TourConfig;
import amata1219.like.masquerade.dsl.InventoryUI;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.option.Lines;
import amata1219.like.masquerade.text.Text;
import amata1219.like.sound.SoundEffects;
import amata1219.like.task.TaskRunner;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class LikeTourTpConfirmationUI implements InventoryUI {

    private final Main plugin = Main.plugin();
    private final Economy economy = plugin.economy();
    private final MainConfig config = plugin.config();
    private final Like like;
    private final InventoryUI previous;

    public LikeTourTpConfirmationUI(Like like, InventoryUI previous){
        this.like = like;
        this.previous = previous;
    }

    @Override
    public Function<Player, Layout> layout() {
        return build(Lines.x1, (p, l) -> {
            l.title = "テレポートの実行確認画面";

            l.defaultSlot(s -> s.icon(i -> {
                i.material = Material.LIGHT_GRAY_STAINED_GLASS_PANE;
                i.displayName = " ";
            }));

            l.put(s -> s.icon(i -> {
                i.material = config.material(MainConfig.IconType.LIKE);
                i.displayName = Text.of("&a&l-%s").format(like.id);
                i.amount = Math.min(Math.max(like.favorites(), 1), 64);
                i.lore(
                        Text.of("&7-%s").format(like.description()),
                        "",
                        Text.of("&7-作成者: &a-%s").format(like.ownerName()),
                        Text.of("&7-お気に入り数: &a-%s").format(like.favorites()),
                        Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()),
                        Text.of("&7-ワールド: &a-%s").format(config.worldAlias(like.world())),
                        Text.of("&7-座標: &a-X-&7-: &a-%s Y-&7-: &a-%s Z-&7-: &a-%s").format(like.x(), like.y(), like.z())
                );
            }), 1);

            l.put(s -> {
                s.icon(i -> {
                    i.material = config.material(MainConfig.IconType.TELEPORT_TO_LIKE);
                    i.displayName = Text.of("&a-このLikeにテレポートする！ (%s" + config.unitOfCost() + ")").format(config.teleportationCosts());
                });

                s.onClick(e -> {
                    double costs = config.teleportationCosts();
                    if(!economy.has(p, costs)){
                        Text.of("&c-テレポートコストが足りません。テレポートするには%s" + config.unitOfCost() + "必要です。").apply(costs).sendTo(p);
                        return;
                    }
                    economy.withdrawPlayer(p, costs);
                    economy.depositPlayer(Bukkit.getOfflinePlayer(like.owner()), config.teleportationCosts());
                    p.teleport(like.hologram.getLocation());
                    config.teleportationText().apply(like).accept(p::sendMessage);

                    SoundEffects.SUCCEEDED.play(p);

                    TourConfig config = Main.plugin().tourConfig();
                    TaskRunner.runTaskLaterAsynchronously(task -> {
                        TextComponent message = new TextComponent(config.guideMessage());
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/like tour"));
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text(ChatColor.GREEN + "クリックで /like tour コマンドを実行しツアー専用UIを開きます！")));
                        p.spigot().sendMessage(message);
                        SoundEffects.OPERATED.play(p);
                    }, config.guideDelayTicks());
                });
            }, 4);

            l.put(s -> {
                s.icon(i -> {
                    i.material = config.material(MainConfig.IconType.CANCEL_LIKE_TELEPORTATION);
                    i.displayName = Text.color("&c-前のページに戻る！");
                });

                s.onClick(e -> previous.open(p));
            }, 7);

        });
    }

}