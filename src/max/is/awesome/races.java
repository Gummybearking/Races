package max.is.awesome;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;





public class races extends JavaPlugin {
	public final Logger logger = Logger.getLogger("Minecraft");
    public static Permission permission = null;
    public static Economy economy = null;
    public static Chat chat = null;
    
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    private boolean setupPermissions()
    {
        RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
        if (permissionProvider != null) {
            permission = permissionProvider.getProvider();
        }
        return (permission != null);
    }

    public void onEnable() {
    	PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " Has Been Enabled!");
		setupPermissions();
		setupEconomy();
		getServer().getPluginManager().registerEvents(new rlistener(this), this);
		
		File config = new File(this.getDataFolder(), "config.yml");
		if(!config.exists()){
			this.saveDefaultConfig();
			System.out.println("[Races] No config.yml detected, config.yml created");

		}
		
		
		
		
	
		
		

	}
		
	public void onDisable() {
		PluginDescriptionFile pdffile = this.getDescription();
		this.logger.info(pdffile.getName() + " Has Been Disabled!");
	}
	public boolean onCommand(CommandSender Sender, Command cmd, String commandLabel, String[] args){
		Player player = (Player) Sender;
		if(commandLabel.equalsIgnoreCase("races")){
			
			if(args.length !=0){
				if(args[0].equalsIgnoreCase("join")){
					if (args.length == 2){
						
						boolean InKeys = false;
						Set<String> list = getConfig().getKeys(true);
						String race = capFirstLetter(args[1]);
						
						for(String key : list){
							if(key.equalsIgnoreCase("races." + race)){
								InKeys = true;
							}
						}
						if(InKeys){
							if(!getConfig().getString("users." + player.getName()).equalsIgnoreCase("No-Race")){
								player.sendMessage(ChatColor.RED + "Usage Error: You already joined a race!");
								player.sendMessage(ChatColor.RED + "Use /Races leave <race> to leave");
							}else{
								permission.playerAddGroup(player, "races" + race);
								player.sendMessage(ChatColor.AQUA + "You" + ChatColor.GRAY + " have joined the race: " + race);
								getServer().broadcastMessage(ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + " has joined the race: " + race);
								setRace(player.getName(), race);
								saveConfig();
							}
						}else {
								player.sendMessage(ChatColor.RED + "Usage Error: Race does not exist!");
						}
					}
				}
					
					
				
				if (args.length == 1){
					if(args[0].equalsIgnoreCase("list")){
						Set<String> list = getConfig().getKeys(true);
						player.sendMessage(ChatColor.GRAY + "[]--- " + ChatColor.AQUA + "Races List"+ ChatColor.GRAY +" ---[]");
						
						for (String key : list){
						    if (key.startsWith("races.") && !key.endsWith(".info") && !key.endsWith(".description") && !key.endsWith("bounty")){
						    	String thekey = key.replace("races.", "");
						    	if(getConfig().getString("races." + thekey + ".info") != null){	
						    		
						    		player.sendMessage(ChatColor.AQUA + thekey +ChatColor.GRAY+ ": " + getConfig().getString("races." + thekey + ".info") );
						    	}else{
						    		player.sendMessage(ChatColor.AQUA + thekey);
						    	}
						    }
						}
					}
					if(args[0].equalsIgnoreCase("Help") || args[0].equalsIgnoreCase("?") ){
						
						player.sendMessage(ChatColor.GRAY + "[]--- " + ChatColor.AQUA + "Races Help"+ ChatColor.GRAY +" ---[]");
						player.sendMessage(ChatColor.AQUA + "Races Commands" + ChatColor.GRAY + ": ");
						player.sendMessage(ChatColor.AQUA + "  List" + ChatColor.GRAY + ": Lists Races created by server owner");
						player.sendMessage(ChatColor.AQUA + "  Join" + ChatColor.GRAY + ": Joins a Race");
						player.sendMessage(ChatColor.AQUA + "  Leave" + ChatColor.GRAY + ": Leaves a Race ");
						player.sendMessage(ChatColor.AQUA + "  Get" + ChatColor.GRAY + ": Gets a player's race");
						player.sendMessage(ChatColor.AQUA + "  Info" + ChatColor.GRAY + ": Gives a description of a race");
						
						if(permission.has(player, "races.admin")|| player.isOp()){
							player.sendMessage(ChatColor.AQUA + "Admin Commands" + ChatColor.GRAY + ": Only Visible to ");
							player.sendMessage(ChatColor.AQUA + "  Forcejoin" + ChatColor.GRAY + ": Force a player to join a race");
							player.sendMessage(ChatColor.AQUA + "  ForceLeave" + ChatColor.GRAY + ": Force a player to leave a race");
							player.sendMessage(ChatColor.AQUA + "  Reload" + ChatColor.GRAY + ": Reload config");
						}
						if(getConfig().getString("config.allowBounties").equalsIgnoreCase("true")){
							player.sendMessage(ChatColor.AQUA + "Bounty" + ChatColor.GRAY + ": Check the bounty of a race");
						}
						player.sendMessage(ChatColor.AQUA + "Races Version" + ChatColor.GRAY + ": " + this.getDescription().getVersion());
					}
					
				}
				if(args[0].equalsIgnoreCase("leave")){
					if(args.length == 1){
							
							if(!getConfig().getString("users." + player.getName()).equalsIgnoreCase("No-Race")){
								
									if(args.length == 1){
										permission.playerRemoveGroup(player, "races" + getConfig().getString("users." + player.getName()));
										setRace(player.getName(), "No-Race");
										saveConfig();
										
										player.sendMessage(ChatColor.AQUA + "You have left your race!");
									}							
								
							}else{
								player.sendMessage(ChatColor.RED + "Usage Error: You have not chosen a race!");
							}
						
						
						
					}else{
						player.sendMessage(ChatColor.RED + "Usage Error: /Races leave");
					}				
				}
				if (args[0].equalsIgnoreCase("info")){
					if (args.length == 2){
						boolean isInKeys = inKeys(args[1]);
						String race = capFirstLetter(args[1]);
						if(isInKeys){
							String info = getConfig().getString("races." + race + ".description");
							if (info != null){
								player.sendMessage(ChatColor.AQUA + race + ChatColor.GRAY + ": " + info);
							}else{ 
								player.sendMessage(ChatColor.RED + "Usage Error: No description found!");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Usage Error: Race does not exist!");						
						}
					}
				}
				if(args[0].equalsIgnoreCase("get")){
					if(args.length == 2){
						if(getConfig().getKeys(true).contains("users." + args[1])){
							if(!getConfig().getString("users." + args[1]).equalsIgnoreCase("No-Race")){
								player.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GRAY + " is a member of the Race " + getConfig().getString("users." + args[1]));
						
							}else{
								player.sendMessage(ChatColor.AQUA + args[1] + ChatColor.GRAY + " has not joined a Race");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Error: No player found with the name " + args[1] + " (Hint: Caps Matter!)");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Usage Error: /Races get <player name>");
					}
				}
				if(args[0].equalsIgnoreCase("forcejoin")){
					if(permission.has(player, "races.admin")|| player.isOp()){
						if(args.length == 3){
							if(getConfig().getKeys(true).contains("races." + args[2])){
								player.sendMessage(ChatColor.AQUA + "You have forced " +args[1] +" into " + args[2]);
								permission.playerAddGroup(getServer().getPlayer(args[1]), "races"+args[2]);
								setRace(args[1], args[2] );
								saveConfig();
							}else{
								player.sendMessage(ChatColor.RED + "Usage Error: Race does not exist (Hint: add Bypass to the end of the command, to force join anyway)");
							}
						}else if(args.length == 4){
							if(args[3].equalsIgnoreCase("bypass")){
								player.sendMessage(ChatColor.AQUA + "You have forced " +args[1] +" into " + args[2]);
								permission.playerAddGroup(getServer().getPlayer(args[1]), "races"+args[2]);
								setRace(args[1], args[2] );
								saveConfig();
								
							}
						}
					}else{
						player.sendMessage(ChatColor.RED + "Sorry, You do not have the required permissions!");
					}
				}
				if(args[0].equalsIgnoreCase("forceleave")){
					
					if(permission.has(player, "races.admin")|| player.isOp()){
						if(args.length == 3){
							if(getConfig().getKeys(true).contains("races." + args[2])){
								player.sendMessage(ChatColor.AQUA + "You have forced " +args[1] +" out of " + args[2]);
								permission.playerRemoveGroup(getServer().getPlayer(args[1]), "races"+args[2]);
								setRace(args[1], "No-Race" );
								saveConfig();
							}else{
								player.sendMessage(ChatColor.RED + "Usage Error: Race does not exist (Hint: add Bypass to the end of the command, to force join anyway)");
							}
						}else if(args.length == 4){
							if(args[3].equalsIgnoreCase("bypass")){
								player.sendMessage(ChatColor.AQUA + "You have forced " +args[1] +" into " + args[2]);
								permission.playerRemoveGroup(getServer().getPlayer(args[1]), "races"+args[2]);
								setRace(args[1], "No-Race");
								saveConfig();
								
							}
						}
					}else{
						player.sendMessage(ChatColor.RED + "Sorry, You do not have the required permissions!");
					}
				}
				if(args[0].equalsIgnoreCase("bounty")){
					if(getConfig().getString("config.allowBounties").equalsIgnoreCase("true")){
						if(args.length == 2){
							boolean inkeys = inKeys(args[1]);
							String race = capFirstLetter(args[1]);
							if(inkeys){
								player.sendMessage(ChatColor.AQUA + "The Race " + race + ChatColor.GRAY + " has a bounty of " + ChatColor.GOLD + getConfig().getString("races." + race + ".bounty" ) + " Voxels");
							}else{
								player.sendMessage(ChatColor.RED + "Usage Error: Race does not exist!");
							}
						}else{
							player.sendMessage(ChatColor.RED + "Usage Error: /Races bounty <race>");
						}
					}else{
						player.sendMessage(ChatColor.RED + "Error: Server does not allow bounties");
					}
				}
				if(args[0].equalsIgnoreCase("reload")){
					if(permission.has(player, "races.admin")||player.isOp()){
						player.sendMessage(ChatColor.AQUA + "Reloading Races!");
						reloadConfig();
						saveConfig();
						player.sendMessage(ChatColor.AQUA + "Races reloaded!");
					   
					}
				}
				
				
				
			
			}else{
				player.sendMessage(ChatColor.GRAY + "[]---------[ "+ChatColor.AQUA + "Races " +  this.getDescription().getVersion() + ChatColor.GRAY + " ]---------[]");

				
				player.sendMessage(ChatColor.AQUA + "Use "  + ChatColor.GRAY+ "/Races help" + ChatColor.AQUA + " to get started with races");
				player.sendMessage(ChatColor.AQUA + "By GummyBearKing");
				String playerrace = getConfig().getString("users." + player.getName());
				player.sendMessage(ChatColor.AQUA + "Current Race" + ChatColor.GRAY + ": " + playerrace.replace("-", " "));
			}
				
		}
		
	
			
		return false;
	}
	public void setRace(String p, String race){
		getConfig().set("users." + p, race);
	}
	public void checkConfig(){
		
			if(getConfig().getString("config.killerMessage") == null){
				getConfig().set("config.killerMessage", "'You have killed %p the %r'");
				saveConfig();
				System.out.println("[Races] No KillerMessage option detected, option created");
				
			}
			if(getConfig().getString("config.allowBounties") == null){
				getConfig().set("config.allowBounties", "false");
				saveConfig();
				System.out.println("[Races] No AllowBounties option detected, option created");
				
			}
			if(getConfig().getString("config.bountyBroadcast") == null){
				getConfig().set("config.bountyBroadcast", "%k has earned %m Voxels for killing %p the %r");
				saveConfig();
				System.out.println("[Races] No AllowBounties option detected, option created");
		
			}	
			saveConfig();
		
	}
	public String capFirstLetter(String string){
		String firstLetter = string.substring(0,1);
		String remainder = string.substring(1);
		String combined = firstLetter.toUpperCase() + remainder.toLowerCase();
		return combined;
		
	}
	public boolean inKeys(String s){
		boolean InKeys = false;
		Set<String> list = getConfig().getKeys(true);
		
		for(String key : list){
			if(key.equalsIgnoreCase("races." + s)){
				InKeys = true;
			}
		}
		return InKeys;
	}
	
	
}//end
