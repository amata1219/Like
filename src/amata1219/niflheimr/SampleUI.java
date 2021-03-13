package amata1219.niflheimr;

import amata1219.niflheimr.dsl.InventoryLayout;
import amata1219.niflheimr.dsl.InventoryUI;
import amata1219.niflheimr.dsl.component.format.InventoryLines;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

import java.util.stream.IntStream;

public class SampleUI implements InventoryUI {

    @Override
    public InventoryLayout layout(Player viewer) {
        //9x3のインベントリを作る
        return build(InventoryLines.x3, l -> {
            //インベントリのタイトルを設定する
            l.title = ChatColor.GRAY + "the untouchable inventory";

            //0番目のスロットの設定をする
            l.putSlot(s -> {
                //アイコンの設定をする
                s.icon(i -> {
                    //表示名を設定する
                    i.displayName = ChatColor.AQUA + "aquatic sword";
                    //説明文を設定する
                    i.lore(
                            ChatColor.GRAY + "this is a legendary sword made of water.",
                            ChatColor.GRAY + "you are far too low level to touch this."
                    );
                    //見た目をダイヤの剣にする
                    i.material = Material.DIAMOND_SWORD;
                    //6個に設定する
                    i.amount = 6;
                    //耐久値を100に設定する
                    i.damage = 100;
                    //エンチャントを付与する
                    i.enchant(Enchantment.DAMAGE_ALL, 6);
                    //エンチャントオーラを付与する
                    i.gleam();
                });

                //このスロットがクリックされた時のアクションを設定する
                s.onClick(e -> {
                    //このInventoryUIを開いているプレイヤーにサウンド効果を与える
                    viewer.playSound(
                            viewer.getLocation(),
                            e.isShiftClick() ? Sound.BLOCK_GLASS_BREAK : Sound.ENTITY_PLAYER_SWIM,
                            1.0f,
                            1.5f
                    );

                    //同上のプレイヤーにメッセージを送信する
                    viewer.sendMessage(ChatColor.GRAY + "you shouldn't touch this......");
                });
            }, 0);

            //1～12番目のスロットの設定をする
            l.putSlot(s -> {
                //アイコンの設定をする
                s.icon(i -> {
                    //ベースとなるアイテムを設定する
                    i.basedItemStack = new ItemStack(Material.SPLASH_POTION);

                    //アイテムの設定を直接書き換える
                    i.raw(item -> {
                        PotionMeta meta = (PotionMeta) item;
                        meta.setBasePotionData(new PotionData(PotionType.WATER_BREATHING));
                        item.setItemMeta(meta);
                    });

                    //このアイコンがクリックされた時のアクションを設定する
                    i.onClick(e -> {
                        //アイテムの個数を1個だけ減らす
                        e.setAmount(e.getAmount() - 1);
                    });
                });
            }, IntStream.rangeClosed(1, 12));

            //InventoryUIが開かれた状態でクリックが行われた時のアクションを設定する
            l.onClick(e -> {
                //インベントリ外をクリックしていたらサウンドを再生する
                if (e.isOutOfInventory()) viewer.playSound(
                        viewer.getLocation(),
                        Sound.BLOCK_ANVIL_PLACE,
                        0.25f,
                        0.8f
                );
            });

            //InventoryUIを開いた時のアクションを設定する
            l.onOpen(e -> {
                //InventoryUIを開いたプレイヤーに1ダメージを与える
                viewer.damage(1.0f);
            });

            //InventoryUIを閉じた時のアクションを設定する
            l.onClose(e -> {
                //InventoryUIを開いていたプレイヤーの経験値を10減らす
                viewer.setExp(Math.max(viewer.getExp() - 10, 0));
            });
        });
    }

}
