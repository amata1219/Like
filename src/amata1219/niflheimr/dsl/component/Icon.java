package amata1219.niflheimr.dsl.component;

import amata1219.niflheimr.constant.Constants;
import amata1219.niflheimr.enchantment.GleamEnchantment;
import com.google.common.collect.Iterables;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.function.Consumer;

public class Icon {

    public ItemStack basedItemStack;
    public Material material = Material.AIR;
    public int amount = 1;
    public int damage;
    public String displayName;
    public List<String> lore = new ArrayList<>();
    public Map<Enchantment, Integer> enchantments = new HashMap<>();
    public Set<ItemFlag> flags = new HashSet<>();
    private Consumer<ItemStack> raw = Constants.noOperation(), actionOnClick = Constants.noOperation();

    public void lore(String... lines){
        lore.addAll(Arrays.asList(lines));
    }

    public void enchant(Enchantment enchantment, int level){
        enchantments.put(enchantment, level);
    }

    public void gleam(){
        enchant(GleamEnchantment.INSTANCE, 1);
    }

    public void itemFlags(ItemFlag... flags){
        this.flags.addAll(Arrays.asList(flags));
    }

    public void raw(Consumer<ItemStack> settings) {
        raw = settings;
    }

    public Consumer<ItemStack>  actionOnClick() {
        return actionOnClick;
    }

    public void onClick(Consumer<ItemStack> actionOnClick) {
        this.actionOnClick = actionOnClick;
    }

    public void apply(ItemStack item) {
        item.setAmount(amount);

        ItemMeta meta = item.getItemMeta();
        if(meta != null){
            if(meta instanceof Damageable) ((Damageable) meta).setDamage(damage);
            meta.setDisplayName(displayName);
            meta.setLore(lore);
            enchantments.forEach((enchantment, level) -> meta.addEnchant(enchantment, level, true));
            meta.addItemFlags(Iterables.toArray(flags, ItemFlag.class));
            item.setItemMeta(meta);
        }

        raw.accept(item);
    }

    public ItemStack toItemStack(){
        ItemStack item = basedItemStack != null ? basedItemStack : new ItemStack(material);
        apply(item);
        return item;
    }

}
