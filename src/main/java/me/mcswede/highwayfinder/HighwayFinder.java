package me.mcswede.highwayfinder;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import static java.lang.Math.abs;

public class HighwayFinder extends JavaPlugin {

    private double centerX;
    private double centerZ;
    private double centerY;
    private double width;
    private double height;
    private double distance;
    private String direction;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        centerX = getConfig().getDouble("center.x");
        centerZ = getConfig().getDouble("center.z");
        centerY = getConfig().getDouble("center.y");
        width = getConfig().getDouble("dimensions.width");
        height = getConfig().getDouble("dimensions.height");
        getLogger().info("HighwayFinder has been enabled.");
    }

    @Override
    public void onDisable() {
        getLogger().info("HighwayFinder has been disabled.");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MiniMessage miniMessage = MiniMessage.miniMessage(); // Create the MiniMessage instance
        if (label.equalsIgnoreCase("highway")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(miniMessage.deserialize("This command can only be used by players."));
                return true;
            }

            Player player = (Player) sender;
            if (!player.hasPermission("highwayfinder.use")) {
                player.sendMessage(miniMessage.deserialize("<gray>You do not have permission to use this command.</gray>"));
                return true;
            }

            if (player.getWorld().getName().equals("world_nether")) {
                double x = player.getLocation().getX() - centerX;
                double z = player.getLocation().getZ() - centerZ;
                double y = player.getLocation().getY() - centerY;

                String highwayDirection = getClosestHighway(x, z);
                if (abs(distance) > (width/2)) {
                    player.sendMessage(miniMessage.deserialize("<gray>The closest highway is the " + highwayDirection + " highway.<newline>Which is <green>" + abs((int)Math.round(distance)) + "</green> blocks to the " + direction + "<newline>and " + getVerticalDirection(y) + "."));
                }
                else {
                    if (y >= 0 && y <= height) {
                        player.sendMessage(miniMessage.deserialize("<gray>You are already on the highway.</gray>"));
                    }
                    else {
                        player.sendMessage(miniMessage.deserialize("<gray>The highway is " + getVerticalDirection(y) + " from your current position."));
                    }
                }
            } else {
                player.sendMessage(miniMessage.deserialize("<gray>You must be in the Nether to use this command.</gray>"));
            }
        }

        return true;
    }

    private String getClosestHighway(double x, double z) {
        double distanceToNorth = z;
        double distanceToSouth = -z;
        double distanceToEast = -x;
        double distanceToWest = x;

        String closestHighway = "<bold><color:#E02443>North</color></bold>";
        double closestDistance = distanceToNorth;
        direction = x<0 ? "<green>East</green>": "<green>West</green>";
        distance = x;

        if (distanceToSouth < closestDistance) {
            closestHighway = "<bold><color:#17BF63>South</color></bold>";
            closestDistance = distanceToSouth;
            direction = x<0 ? "<green>East</green>": "<green>West</green>";
            distance = x;
        }

        if (distanceToEast < closestDistance) {
            closestHighway = "<bold><color:#EBB617>East</color></bold>";
            closestDistance = distanceToEast;
            direction = z<0 ? "<green>South</green>": "<green>North</green>";
            distance = z;
        }

        if (distanceToWest < closestDistance) {
            closestHighway = "<bold><color:#1D72F2>West</color></bold>";
            closestDistance = distanceToWest;
            direction = z<0 ? "<green>South</green>": "<green>North</green>";
            distance = z;
        }

        return closestHighway;
    }

    private String getVerticalDirection(double y) {
        String upDown;
        if (y>=-0.5 && y<0.5) {
            return "on this height";
        }
        else {
            if (y<0) {
                upDown = "<green>up</green>";
            } else {
                upDown = "<green>down</green>";
            }
            return "<green>" + abs((int)Math.round(y)) + "</green> blocks " + upDown;
        }
    }
}
