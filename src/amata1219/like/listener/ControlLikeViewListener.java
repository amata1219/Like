package amata1219.like.listener;

import amata1219.like.Like;
import amata1219.like.ui.LikeRangeSearchingUI;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;

public class ControlLikeViewListener implements Listener {

    public final HashMap<Player, Location> viewersToRespawnPoints = new HashMap<>();
    public final HashMap<Player, Like> viewersToLikesViewed = new HashMap<>();
    public final HashMap<Player, LikeRangeSearchingUI> viewersToUIs = new HashMap<>();

    @EventHandler(ignoreCancelled = true)
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (!viewersToRespawnPoints.containsKey(player)) return;

        switch (event.getCause()) {
            case PLUGIN:
            case COMMAND:
                player.sendMessage(ChatColor.RED + "Likeの確認モードが解除されました。");
                return;
            default:
                event.setCancelled(true);
                disableViewingMode(player);
                return;
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        if (!viewersToRespawnPoints.containsKey(event.getPlayer())) return;

        Location from = event.getFrom(), to = event.getTo();
        if (to != null && (from.getX() != to.getX() || from.getZ() != to.getZ())) event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (!viewersToRespawnPoints.containsKey(player)) return;

        player.teleport(viewersToRespawnPoints.remove(player));
        disableViewingMode(player);
    }

    public void disableViewingMode(Player viewer) {
        viewersToLikesViewed.remove(viewer);
        viewersToUIs.remove(viewer);

    }

}
