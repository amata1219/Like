package amata1219.like.ui;

import amata1219.like.Like;
import amata1219.like.config.MainConfig;
import amata1219.like.listener.ControlLikeViewListener;
import amata1219.like.masquerade.dsl.component.Layout;
import amata1219.like.masquerade.text.Text;
import amata1219.like.sound.SoundEffects;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.IntStream;

public class LikeRangeSearchingUI extends AbstractSortableLikeListUI {

    private final List<Like> likes;

    public LikeRangeSearchingUI(List<Like> likes) {
        super();
        this.likes = likes;
    }
    @Override
    protected List<Like> likes() {
        return likes;
    }

    @Override
    protected void layout(Player player, Layout l, List<Like> likes) {
        super.layout(player, l, likes);

        l.title = Text.of("周辺のLike一覧 @ %sページ目").format(index + 1);

        int remainder = likes.size() - (index * 45) >= 45 ? 45 : likes.size() % 45;
        IntStream.range(0, remainder != 0 ? remainder : likes.isEmpty() ? 0 : 45).forEach(slotIndex -> {
            l.put(s -> {
                final Like like = likes.get(slotIndex);
                s.icon(i -> {
                    i.material = config.material(MainConfig.IconType.LIKE);
                    i.displayName = Text.of("&a&l-%s").format(like.id);
                    i.amount = Math.min(Math.max(like.favorites(), 1), 64);
                    i.lore(
                            Text.of("&7-%s").format(like.description()),
                            "",
                            Text.of("&7-お気に入り数: &a-%s").format(like.favorites()),
                            Text.of("&7-作成日時: &a-%s").format(like.creationTimestamp()),
                            Text.of("&7-ワールド: &a-%s").format(config.worldAlias(like.world())),
                            Text.of("&7-座標: &a-X-&7-: &a-%s Y-&7-: &a-%s Z-&7-: &a-%s").format(like.x(), like.y(), like.z()),
                            "",
                            Text.color("&7-クリック: Likeのある場所に視点が移動します。")
                    );
                });

                s.onClick(e -> {
                    Location respawnPoint = player.getLocation();
                    player.teleport(like.hologram.getLocation());
                    ControlLikeViewListener listener = plugin.controlLikeViewListener;
                    listener.viewersToRespawnPoints.put(player, respawnPoint);
                    listener.viewersToLikesViewed.put(player, like);
                    listener.viewersToUIs.put(player, this);

                    SoundEffects.OPERATED.play(player);

                    TextComponent component = new TextComponent(ChatColor.GREEN + "[Like]: " + ChatColor.GRAY + "テレポートの確定・キャンセルはこちらを" + ChatColor.GREEN + "クリック" + ChatColor.GRAY + "！  " + ChatColor.GREEN + "専用GUI" + ChatColor.GRAY + "が開きます！");
                    component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/likeorscu"));
                    component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.hover.content.Text("左クリックで専用GUIを表示")));

                    player.sendMessage("----------------------------------------------------------------------");
                    player.spigot().sendMessage(component);
                    player.sendMessage(ChatColor.RED + "※視点移動中は水平方向へ動くことができません。");
                    player.sendMessage(ChatColor.RED + "※スタックなどの不具合が生じた場合はログアウトを行ってください。元の位置にリセットされます。");
                    player.sendMessage("----------------------------------------------------------------------");
                });
            }, slotIndex);
        });
    }
}
