package max.is.awesome;


import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class rlistener implements Listener{
	public races plugin;
	 
	public rlistener(races p) {
	    plugin = p;
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		String defaultRace = plugin.getConfig().getString("config.defaultRace");
		if(plugin.getConfig().getString("users." + player.getName()) == null){
			if(defaultRace == null) defaultRace = "No-Race";
			plugin.getConfig().set("users." + player.getName(), defaultRace );
			plugin.saveConfig();
			player.sendMessage(ChatColor.AQUA + "[Races] You have been automaticaly assigned to the default race " + defaultRace + " if you wish to change, use /Races help for information");
		}
	}
	@EventHandler
	public void onEntityDeath(PlayerDeathEvent event) {
	    Entity player = event.getEntity();
	    if ((event.getEntity().getLastDamageCause() instanceof EntityDamageByEntityEvent)) {
	      Entity killer = ((EntityDamageByEntityEvent)event.getEntity().getLastDamageCause()).getDamager();
	      if (((player instanceof Player)) && ((killer instanceof Player))) {
	    	  Player p = (Player) event.getEntity();
	    	  Player d = (Player) killer;
	    	  if(!plugin.getConfig().getString("users." + p.getName()).equalsIgnoreCase("No-race")){
	    		  String message = plugin.getConfig().getString("config.killerMessage");
		    	  message = message.replace("%p", p.getDisplayName());
		    	  message = message.replace("%r", plugin.getConfig().getString("users." + p.getName()));

		    	  d.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
		    	  if(plugin.getConfig().getString("config.allowBounties").equalsIgnoreCase("true")){
		    		  races.economy.depositPlayer(d.getName(), plugin.getConfig().getInt("races." + plugin.getConfig().getString("users." + p.getName())));
		    		  String bmessage = plugin.getConfig().getString("config.bountyBroadcast").replace("%k", d.getDisplayName()).replace("%r", plugin.getConfig().getString("users." + p.getName())).replace("%p", p.getDisplayName()).replace("%b", plugin.getConfig().getString("races." + plugin.getConfig().getString("users." + p.getName())));
		    		  plugin.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', bmessage));
		    	  }
		    	  
	    	  }
	    	  
	      }
	    }
	}
	
	
}
	