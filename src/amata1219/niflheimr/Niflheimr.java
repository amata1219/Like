package amata1219.niflheimr;

import amata1219.niflheimr.dsl.InventoryOperationListener;
import amata1219.niflheimr.enchantment.GleamEnchantment;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class Niflheimr extends JavaPlugin {

    private static Niflheimr instance;

    @Override
    public void onEnable() {
        instance = this;

        Field acceptingNew;
        try {
            acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
            acceptingNew.setAccessible(true);
            acceptingNew.set(null, true);
            Enchantment.registerEnchantment(GleamEnchantment.INSTANCE);
            acceptingNew.set(null, false);
            acceptingNew.setAccessible(false);
        } catch (NoSuchFieldException | IllegalAccessException e) {

        }

        getServer().getPluginManager().registerEvents(new InventoryOperationListener(), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }

    public static Niflheimr instance() {
        return instance;
    }
}
