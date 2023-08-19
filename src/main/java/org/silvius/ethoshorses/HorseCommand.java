package org.silvius.ethoshorses;

import de.tr7zw.nbtapi.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
public class HorseCommand implements CommandExecutor, Listener {



    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public HorseCommand(){





    }
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if (commandSender instanceof Player) {
            final Player player = ((Player) commandSender).getPlayer();

            assert player != null;
            if (!player.hasPermission("ethoshorses.pferd")) {
                player.sendMessage(ChatColor.RED + "Keine Berechtigung");
                return true;
            }
            if (strings.length == 0) {
                player.sendMessage(ChatColor.RED + "Fehlende Argumente");
                return true;
            }
            if (strings.length == 2) {
                player.sendMessage(ChatColor.RED + "Zu viele Argumente");
                return true;
            }
            switch (strings[0]) {
                case "unzip": {
                    if (unzipHorse(player)) return true;


                    break;
                }
                case "zip": {
                    if(player.isInsideVehicle()){
                        Entity vehicle = player.getVehicle();
                        assert vehicle!=null;
                        if (zipHorse(player, vehicle)) return true;


                    }

                    break;
                }
            }


        }
        return true;
    }

    private static boolean zipHorse(Player player, Entity entity) {
        if(!(entity instanceof Tameable)){return false;}
        AbstractHorse abstractHorse = (AbstractHorse) entity;
        NBTEntity nbtHorse = new NBTEntity(entity);
        Object object = nbtHorse.getCompound();

        if(!abstractHorse.isTamed()){
            return true;
        }
        double speed = Objects.requireNonNull(abstractHorse.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED)).getBaseValue();
        double maxHealth = Objects.requireNonNull(abstractHorse.getAttribute(Attribute.GENERIC_MAX_HEALTH)).getBaseValue();
        double currHealth = abstractHorse.getHealth();
        double jumpStrength = Objects.requireNonNull(abstractHorse.getAttribute(Attribute.HORSE_JUMP_STRENGTH)).getBaseValue();
        if(abstractHorse.getInventory().getSaddle()==null){
            return true;}



        ItemStack saddle = new ItemStack(Material.SADDLE);
        ItemMeta itemMeta = saddle.getItemMeta();
        final PersistentDataContainer data = itemMeta.getPersistentDataContainer();

        final NamespacedKey isSaddleKey = new NamespacedKey(EthosHorses.getPlugin(), "magischerSattel");
        data.set(isSaddleKey, PersistentDataType.STRING, object.toString());
        ArrayList<Component> lore = new ArrayList<>();

        if(abstractHorse.getType()==EntityType.HORSE){
            Horse horse = (Horse) abstractHorse;

            ItemStack armor = horse.getInventory().getArmor();



            lore.add(loreBuilder("Züchter: ", player.getName(), NamedTextColor.GRAY, NamedTextColor.AQUA));
            lore.add(loreBuilder("Art: ", "Pferd", NamedTextColor.GRAY, NamedTextColor.DARK_PURPLE));
            lore.add(loreBuilder("Leben: ", (int) Math.round(currHealth)+"/"+(int) Math.round(maxHealth), NamedTextColor.GRAY, NamedTextColor.YELLOW));


            lore.add(loreBuilder("Geschwindigkeit: ", String.valueOf(round(speed*100, 2)), NamedTextColor.GRAY, NamedTextColor.YELLOW));
            lore.add(loreBuilder("Sprungkraft: ", String.valueOf(round(jumpStrength*100, 2)), NamedTextColor.GRAY, NamedTextColor.YELLOW));
            if(armor!=null){
                switch(armor.getType().name()){
                    case "LEATHER_HORSE_ARMOR":
                        lore.add(loreBuilder("Rüstung: ", "Lederrüstung", NamedTextColor.GRAY, NamedTextColor.YELLOW));
                        break;
                    case "IRON_HORSE_ARMOR":
                        lore.add(loreBuilder("Rüstung: ", "Eisenrüstung", NamedTextColor.GRAY, NamedTextColor.YELLOW));
                        break;
                    case "GOLD_HORSE_ARMOR":
                        lore.add(loreBuilder("Rüstung: ", "Goldrüstung", NamedTextColor.GRAY, NamedTextColor.YELLOW));
                    case "DIAMOND_HORSE_ARMOR":
                        lore.add(loreBuilder("Rüstung: ", "Diamantrüstung", NamedTextColor.GRAY, NamedTextColor.YELLOW));}
            }
            else{
                lore.add(loreBuilder("Rüstung: ", "Keine", NamedTextColor.GRAY, NamedTextColor.YELLOW));
            }



        }
        if(abstractHorse.getType()==EntityType.SKELETON_HORSE){


            lore.add(loreBuilder("Züchter: ", player.getName(), NamedTextColor.GRAY, NamedTextColor.AQUA));
            lore.add(loreBuilder("Art: ", "Skelettpferd", NamedTextColor.GRAY, NamedTextColor.DARK_PURPLE));
            lore.add(loreBuilder("Leben: ", (int) Math.round(currHealth)+"/"+(int) Math.round(maxHealth), NamedTextColor.GRAY, NamedTextColor.YELLOW));


            lore.add(loreBuilder("Geschwindigkeit: ", String.valueOf(round(speed*100, 2)), NamedTextColor.GRAY, NamedTextColor.YELLOW));
            lore.add(loreBuilder("Sprungkraft: ", String.valueOf(round(jumpStrength*100, 2)), NamedTextColor.GRAY, NamedTextColor.YELLOW));

            itemMeta.lore(lore);
            itemMeta.displayName(Component.text("Magischer Sattel").color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));


        }
        if(abstractHorse.getType()==EntityType.ZOMBIE_HORSE){

            lore.add(loreBuilder("Züchter: ", player.getName(), NamedTextColor.GRAY, NamedTextColor.AQUA));
            lore.add(loreBuilder("Art: ", "Zombiepferd", NamedTextColor.GRAY, NamedTextColor.DARK_PURPLE));
            lore.add(loreBuilder("Leben: ", (int) Math.round(currHealth)+"/"+(int) Math.round(maxHealth), NamedTextColor.GRAY, NamedTextColor.YELLOW));


            lore.add(loreBuilder("Geschwindigkeit: ", String.valueOf(round(speed*100, 2)), NamedTextColor.GRAY, NamedTextColor.YELLOW));
            lore.add(loreBuilder("Sprungkraft: ", String.valueOf(round(jumpStrength*100, 2)), NamedTextColor.GRAY, NamedTextColor.YELLOW));

            itemMeta.lore(lore);
            itemMeta.displayName(Component.text("Magischer Sattel").color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));



        }


        if(abstractHorse.getType()==EntityType.MULE){

            lore.add(loreBuilder("Züchter: ", player.getName(), NamedTextColor.GRAY, NamedTextColor.AQUA));
            lore.add(loreBuilder("Art: ", "Maultier", NamedTextColor.GRAY, NamedTextColor.DARK_PURPLE));
            lore.add(loreBuilder("Leben: ", (int) Math.round(currHealth)+"/"+(int) Math.round(maxHealth), NamedTextColor.GRAY, NamedTextColor.YELLOW));


            lore.add(loreBuilder("Geschwindigkeit: ", String.valueOf(round(speed*100, 2)), NamedTextColor.GRAY, NamedTextColor.YELLOW));
            itemMeta.lore(lore);
            itemMeta.displayName(Component.text("Magischer Sattel").color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));





        }

        if(abstractHorse.getType()==EntityType.DONKEY){

            lore.add(loreBuilder("Züchter: ", player.getName(), NamedTextColor.GRAY, NamedTextColor.AQUA));
            lore.add(loreBuilder("Art: ", "Esel", NamedTextColor.GRAY, NamedTextColor.DARK_PURPLE));
            lore.add(loreBuilder("Leben: ", (int) Math.round(currHealth)+"/"+(int) Math.round(maxHealth), NamedTextColor.GRAY, NamedTextColor.YELLOW));


            lore.add(loreBuilder("Geschwindigkeit: ", String.valueOf(round(speed*100, 2)), NamedTextColor.GRAY, NamedTextColor.YELLOW));
            itemMeta.lore(lore);
            itemMeta.displayName(Component.text("Magischer Sattel").color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));

        }

        itemMeta.lore(lore);
        itemMeta.displayName(Component.text("Magischer Sattel").color(NamedTextColor.DARK_GREEN).decoration(TextDecoration.ITALIC, false));
        saddle.setItemMeta(itemMeta);
        player.getInventory().addItem(saddle);
        abstractHorse.remove();


        return false;
    }

    private boolean unzipHorse(Player player) {

        if (!(player.getInventory().getItemInMainHand().getType() == Material.SADDLE)) {
            return true;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        ItemMeta itemMeta = item.getItemMeta();
        final PersistentDataContainer data = itemMeta.getPersistentDataContainer();
        final NamespacedKey isSaddleKey = new NamespacedKey(EthosHorses.getPlugin(), "magischerSattel");

        if(!(data.has(isSaddleKey))){return true;}
        List<Component> lore = itemMeta.lore();

        assert lore != null;
        String type = ((TextComponent) lore.get(1).children().get(0)).content();
        String nbtString = data.get(isSaddleKey, PersistentDataType.STRING);
        NBTContainer nbtContainer = (NBTContainer) NBT.parseNBT(nbtString);
        nbtContainer.removeKey("Pos");
        nbtContainer.removeKey("UUID");
        AbstractHorse horse = null;
        World world = player.getWorld();
        if(type.equals("Pferd")){

             horse = (AbstractHorse) world.spawnEntity(player.getLocation(), EntityType.HORSE);

        }
        if(type.equals("Skelettpferd")){
             horse = (AbstractHorse) world.spawnEntity(player.getLocation(), EntityType.SKELETON_HORSE);
}
        if(type.equals("Zombiepferd")){
             horse = (AbstractHorse) world.spawnEntity(player.getLocation(), EntityType.ZOMBIE_HORSE);
}


        if(type.equals("Maultier")){
             horse = (AbstractHorse) world.spawnEntity(player.getLocation(), EntityType.MULE);

        }
        if(type.equals("Esel")){
             horse = (AbstractHorse) world.spawnEntity(player.getLocation(), EntityType.DONKEY);

        }
        NBTEntity nbtEntity = new NBTEntity(horse);
        nbtEntity.mergeCompound(nbtContainer);
        horse.setTamed(true);
        horse.setOwner(player);
        player.getInventory().getItemInMainHand().setAmount(0);
        horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
        horse.addPassenger(player);
        return false;
    }

    private static Component loreBuilder(String s1, String s2, NamedTextColor c1, NamedTextColor c2){
        return Component.text(s1).color(c1).decoration(TextDecoration.ITALIC, false).append(Component.text(s2).color(c2)).decoration(TextDecoration.ITALIC, false);

    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(!player.isSneaking()){return;}
        Entity vehicle = event.getRightClicked();

        zipHorse(player, vehicle);
    }

    @EventHandler
    public void onBlockInteractEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(!(event.getAction()== Action.RIGHT_CLICK_BLOCK)){return;}
        unzipHorse(player);
    }
}
