package amata1219.like.ui;

import amata1219.like.Like;
import amata1219.like.Main;
import amata1219.like.config.MainConfig;
import amata1219.like.listener.ControlLikeViewListener;
import amata1219.like.masquerade.dsl.InventoryUI;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.option.Lines;
import amata1219.like.masquerade.text.Text;
import amata1219.like.sound.SoundEffects;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.function.Function;

public class LikeRangeSearchTpConfirmationUI implements InventoryUI {

    private final Main plugin = Main.plugin();
    private final Economy economy = plugin.economy();
    private final MainConfig config = plugin.config();
    private final Like like;
    private final InventoryUI previous;

    public LikeRangeSearchTpConfirmationUI(Like like, InventoryUI previous){
        this.like = like;
        this.previous = previous;
    }

    @Override
    public Function<Player, Layout> layout() {
        return build(Lines.x1, (player, l) -> {
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
                    if (!economy.has(player, costs)) {
                        Text.of("&c-テレポートコストが足りません。テレポートするには%s" + config.unitOfCost() + "必要です。").apply(costs).sendTo(player);
                        SoundEffects.FAILED.play(player);
                        return;
                    }
                    economy.withdrawPlayer(player, costs);
                    economy.depositPlayer(Bukkit.getOfflinePlayer(like.owner()), costs);

                    ControlLikeViewListener listener = plugin.controlLikeViewListener;
                    listener.viewersToRespawnPoints.remove(player);
                    listener.disableViewingMode(player);

                    player.closeInventory();

                    SoundEffects.SUCCEEDED.play(player);
                    config.teleportationText().apply(like).accept(player::sendMessage);
                });
            }, 6);

            l.put(s -> {
                s.icon(i -> {
                    i.material = config.material(MainConfig.IconType.CANCEL_LIKE_TELEPORTATION);
                    i.displayName = Text.color("&c-確認をやめて元の場所に戻る");
                });

                s.onClick(e -> {
                    ControlLikeViewListener listener = plugin.controlLikeViewListener;

                    listener.disableViewingMode(player);

                    Location respawnPoint = listener.viewersToRespawnPoints.remove(player);
                    player.teleport(respawnPoint);

                    previous.open(player);

                    player.sendMessage(ChatColor.RED + "元の場所に戻りました。");
                    SoundEffects.CANCEL.play(player);
                });
            }, 7);

        });
    }

}
