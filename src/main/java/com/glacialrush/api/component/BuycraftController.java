package com.glacialrush.api.component;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.glacialrush.Info;
import com.glacialrush.api.GlacialPlugin;
import org.bukkit.ChatColor;

public class BuycraftController extends Controller implements CommandExecutor
{ 
	public BuycraftController(GlacialPlugin pl)
	{
		super(pl);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
		boolean isAuthenticated = !(sender instanceof Player);
		
		if(command.getName().equals(Info.CMD_REMOTE))
		{
			if(isAuthenticated)
			{
				if(args.length == 3)
				{
					if(args[0].equals("bcpch"))
					{
						
					}
					
					else
					{
						sender.sendMessage("  This is a remote connector. Services such as the website, and buycraft will use this");
						sender.sendMessage("  to communicate with hash messages. There is nothing you can do here, unless");
						sender.sendMessage("  you can guess a 256 bit token followed by 7 24 bit tokens.");
					}
				}
				
				else
				{
					sender.sendMessage("  This is a remote connector. Services such as the website, and buycraft will use this");
					sender.sendMessage("  to communicate with hash messages. There is nothing you can do here, unless");
					sender.sendMessage("  you can guess a 256 bit token followed by 7 24 bit tokens.");
				}
			}
			
			else
			{
				sender.sendMessage(ChatColor.RED + "Denied.");
			}
		}
		
		return false;
	}
}
