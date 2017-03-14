package net.frozenorb.potpvp.event.command;

public final class EventBeginCommand {

    /*@Command(names = {"event begin"}, permission = "")
    public static void eventBegin(Player sender, @Param(name = "type") EventType type, @Param(name="countdown") int countdown, @Param(name="restricted") boolean restricted) {
        EventHandler eventHandler = PotPvPSI.getInstance().getEventHandler();
        eventHandler.beginEvent(type, sender.getUniqueId(), countdown, restricted);

        String message1 = "&6&l%s &f&lis hosting a &b&l%s &f&levent!";
        String message2 = "&f&lEvent begins in &b&l%s&f&l, join with emerald in hotbar!";

        message1 = String.format(message1, sender.getName(), type.getName());
        message2 = String.format(message2, TimeUtils.formatIntoDetailedString(countdown));

        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message1));
        Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', message2));
    }*/

}