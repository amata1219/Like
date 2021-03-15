package amata1219.like.bryionake.constant;

import amata1219.like.bryionake.dsl.caster.SafeCaster;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSenderCasters {

    public static final SafeCaster<CommandSender, Player, String> casterToPlayer = new SafeCaster<>(() -> Constants.ERROR_MESSAGE_PREFIX + "ゲーム内から実行して下さい。");

}
