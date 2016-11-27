package com.glacialrush.api.dispatch.notification;

import java.util.UUID;
import com.glacialrush.api.sfx.Audio;
import org.bukkit.ChatColor;

public enum NotificationPreset
{
	GAME_YOU_FIGHT_WITH(new Notification().setTitleb(ChatColor.DARK_GRAY + "You fight with%s %s").setDelay(18).setDisplay(43).setAudio(Audio.COMBAT_INSANEKILL)),
	COMBAT_KILLED(new Notification().setTitlec("%s killed %s " + ChatColor.GREEN + " (%s" + ChatColor.GREEN + ")").setDisplay(10).setPriority(NotificationPriority.VERYLOW)),
	COMBAT_RELENTLESS(new Notification().setTitlec("%s is RELENTLESS! " + ChatColor.GREEN + " (%s" + ChatColor.GREEN + ")").setDisplay(30).setPriority(NotificationPriority.LOW)),
	JOIN(new Notification().setTitlec("%s " + ChatColor.AQUA + "fights with %s").setDisplay(30).setPriority(NotificationPriority.MEDIUM)),
	QUIT(new Notification().setTitlec("%s " + ChatColor.AQUA + "quit the game.").setDisplay(30).setPriority(NotificationPriority.MEDIUM)),
	UNLOCK(new Notification().setTitlea(ChatColor.DARK_GRAY + "Unlocked " + ChatColor.AQUA + "%s").setTitleb(ChatColor.DARK_GRAY + "Equip it in your loadout.").setDisplay(50).setPriority(NotificationPriority.HIGH).setAudio(Audio.CAPTURE_CAPTURE)),
	UNLOCK_RANK(new Notification().setTitlea(ChatColor.DARK_GRAY + "Unlocked " + ChatColor.AQUA + "%s").setTitleb(ChatColor.DARK_GRAY + "Thanks!").setDisplay(50).setPriority(NotificationPriority.HIGH).setAudio(Audio.CAPTURE_CAPTURE)),
	FOUND(new Notification().setTitleb(ChatColor.DARK_GRAY + "You found " + ChatColor.AQUA + "%s").setDisplay(30).setPriority(NotificationPriority.MEDIUM).setAudio(Audio.UI_ACTION)),
	EXPERIENCE(new Notification().setTitleb(ChatColor.DARK_GRAY + "+ " + ChatColor.AQUA + "%sXP").setTitlec(ChatColor.AQUA + "%s " + ChatColor.AQUA + "[+%s]").setDisplay(8).setAudio(Audio.EXPERIENCE_EARN).setPriority(NotificationPriority.HIGH)),
	VOTE_EXPERIENCE(new Notification().setTitleb(ChatColor.DARK_GRAY + "+ " + ChatColor.AQUA + "%sXP").setTitlec(ChatColor.AQUA + "Thanks!").setDisplay(15).setAudio(Audio.EXPERIENCE_EARN).setPriority(NotificationPriority.HIGHEST)),
	VOTED(new Notification().setTitlea(ChatColor.DARK_GRAY + "Thanks!" + ChatColor.AQUA + "Thank you for voting!").setTitlec(ChatColor.AQUA + "Here's the stuff").setDisplay(50).setAudio(Audio.EXPERIENCE_RANKUP).setPriority(NotificationPriority.HIGHEST)),
	PALADINS(new Notification().setTitleb("%s" + ChatColor.DARK_GRAY + "'s Paladins are ENRAGED!").setDisplay(30).setDelay(10).setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.HIGH)),
	EXPERIENCE_RANKUP(new Notification().setTitlea(ChatColor.DARK_GRAY + "Rank " + ChatColor.AQUA + "%s").setTitleb(ChatColor.GREEN + "Total XP: " + ChatColor.AQUA + "%s").setDisplay(20).setAudio(Audio.EXPERIENCE_RANKUP).setPriority(NotificationPriority.MEDIUM)),
	TERRITORY_CAPTURED(new Notification().setTitlea(ChatColor.DARK_GRAY + "%sTerritory Captured").setTitleb("%s").setTitlec("%sHighest Influence: %s").setDisplay(30).setDelay(10).setAudio(Audio.REGION_CAPTURE).setPriority(NotificationPriority.VERYHIGH)),
	BOOSTED(new Notification().setTitlea(ChatColor.AQUA + "Boost Active!").setTitleb(ChatColor.DARK_GRAY + "You're Boosting %s").setDisplay(60).setDelay(10).setAudio(Audio.REGION_CAPTURE).setPriority(NotificationPriority.VERYHIGH)),
	TERRITORY_LOST(new Notification().setTitlea(ChatColor.DARK_GRAY + "%sTerritory Lost").setTitleb("%s").setTitlec("%sHighest Influence: %s").setDisplay(30).setDelay(10).setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.VERYHIGH)),
	WARPGATE_LOST(new Notification().setTitlea("%sWarpgate Lost").setTitleb("%sYou lost a warpgate").setDisplay(30).setDelay(10).setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.VERYHIGH)),
	WARPGATE_CAPTURED(new Notification().setTitlea("%sWarpgate Captured").setTitleb("%sYou have captured a warpgate").setDisplay(30).setDelay(10).setAudio(Audio.REGION_CAPTURE).setPriority(NotificationPriority.VERYHIGH)),
	FACTION_DEFEAT(new Notification().setTitlea("%sDEFEAT!").setTitleb("%sYou have been eliminated from the game!").setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.VERYHIGH).setDelay(10).setDisplay(60)),
	FACTION_VICTORIOUS(new Notification().setTitlea("%sVICTORY!").setTitleb("%s was Victorious!").setAudio(Audio.COMBAT_CENA).setPriority(NotificationPriority.HIGHEST).setDelay(48).setDisplay(60)),
	FACTION_ELIMINATED(new Notification().setTitlea("%s " + ChatColor.DARK_GRAY + "Has Fallen!").setTitleb("%s has been eliminated from the game!").setAudio(Audio.REGION_CAPTURE).setPriority(NotificationPriority.VERYHIGH).setDelay(10).setDisplay(60)),
	WARNING_LEAVE(new Notification().setTitlea(ChatColor.RED + "" + ChatColor.UNDERLINE + "WARNING").setTitleb(ChatColor.RED + "Leave this area NOW!").setAudio(Audio.UI_FAIL).setPriority(NotificationPriority.HIGHEST).setDisplay(25).setDupeTag(UUID.randomUUID()).setNoDupe(true)),
	WARNING_TEAMING(new Notification().setTitlea(ChatColor.RED + "" + ChatColor.UNDERLINE + "WARNING").setTitleb(ChatColor.YELLOW + "Do not team with %s!").setAudio(Audio.UI_FAIL).setPriority(NotificationPriority.HIGHEST).setDisplay(65).setDupeTag(UUID.randomUUID()).setNoDupe(true)),
	VILLAGE_CAPTURED(new Notification().setTitlea("%sVillage Captured").setTitleb("%s").setTitlec("%sHighest Influence: %s").setDisplay(30).setDelay(10).setAudio(Audio.REGION_CAPTURE).setPriority(NotificationPriority.VERYHIGH)),
	VILLAGE_LOST(new Notification().setTitlea("%sVillage Lost").setTitleb("%s").setTitlec("%sHighest Influence: %s").setDisplay(30).setDelay(10).setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.VERYHIGH)),
	CAPTURE_NO_CONNECTED_REGION(new Notification().setTitlec("%s Secured").noDupe(UUID.randomUUID())),
	CAPTURE_CAPTURING(new Notification().setTitlec("%s").noDupe(UUID.randomUUID())),
	CAPTURE_TOOK(new Notification().setTitlec("%s is taking %s").setDisplay(40)),
	CAPTURE_CONTESTED(new Notification().setTitlec("%s").noDupe(UUID.randomUUID())),
	CAPTURE_SECURED(new Notification().setTitlec("%s").noDupe(UUID.randomUUID())),
	RESPAWN_COOLDOWN(new Notification().setTitlec(ChatColor.DARK_GRAY + "Respawning in " + ChatColor.AQUA + " %s").setOngoing(true)),
	ACTION_TIMER(new Notification().setTitleb(ChatColor.AQUA + "Nearby %s").setTitlec(ChatColor.DARK_GRAY + "Respawning in " + ChatColor.AQUA + " %s").setOngoing(true)),
	SQUAD_OBJECT(new Notification().setTitleb("%sObjective: %s").setDisplay(60)),
	SQUAD_POINT(new Notification().setTitlec("%sSquad Beacon Updated, Check your compass.")),
	SQUAD_EVENT(new Notification().setTitlec("%s")),
	RESPAWN_SPECTATE(new Notification().setTitlec(ChatColor.AQUA + "Spectating in " + ChatColor.RED + " %s").setOngoing(true)),
	RESPAWN_DEATH_COOLDOWN(new Notification().setTitlea(ChatColor.RED + "%s by %s").setTitleb(ChatColor.DARK_RED + "Assisted by %s").setTitlec(ChatColor.AQUA + "Respawning in " + ChatColor.RED + " %s").setOngoing(true)),
	DEPLOY_COOLDOWN(new Notification().setTitleb(ChatColor.AQUA + "Deploying to " + ChatColor.GREEN + "%s").setTitlec(ChatColor.AQUA + "Deploying in " + ChatColor.RED + " %s").setOngoing(true)),
	TELEPORT_COMPLETED(new Notification().setTitlec(ChatColor.GREEN + "Respawned!").setDisplay(30)),
	TELEPORT_CANCELLED_COMBAT(new Notification().setTitlec(ChatColor.RED + "Teleport Cancelled. " + ChatColor.YELLOW + " COMBAT").setDisplay(30).setAudio(Audio.UI_FAIL)),
	GAME_FINISHED_ON_JOIN(new Notification().setTitleb(ChatColor.RED + "Game Finished.").setTitlec(ChatColor.YELLOW + "Please choose another game!").setDisplay(50).setAudio(Audio.UI_FAIL)),
	GAME_QUIT(new Notification().setTitlec(ChatColor.GREEN + "Quit Game.").setDisplay(50).setAudio(Audio.UI_ACTION)),
	ACHEIVEMENT(new Notification().setTitleb(ChatColor.GOLD + "%s").setDisplay(30).setAudio(Audio.UI_ACTION).setPriority(NotificationPriority.LOWEST)),
	RANK_TITLE(new Notification().setTitlea(ChatColor.AQUA + "%s").setTitleb(ChatColor.DARK_GRAY + "Earned Rank Title").setDisplay(30).setDelay(10).setAudio(Audio.COMBAT_INSANEKILL).setPriority(NotificationPriority.LOW)),
	FOUND_MAP_FRAGMENT(new Notification().setTitlea(ChatColor.DARK_GRAY + "You Found").setTitleb(ChatColor.AQUA + "%s Treasure Map Fragments.").setDisplay(30).setDelay(10).setAudio(Audio.CAPTURE_CAPTURE).setPriority(NotificationPriority.LOW)),
	BOUNTY_PLACED(new Notification().setTitlea(ChatColor.RED + "Bounty" + ChatColor.DARK_GRAY + " Placed").setTitleb(ChatColor.DARK_RED + "WANTED " + ChatColor.DARK_GRAY + "<> %s").setTitlec(ChatColor.AQUA + "Take this bounty in the bounty menu. (press e, click Bounties)").setDisplay(60).setDelay(10).setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.VERYLOW)),
	BOUNTY_FULFILLED(new Notification().setTitlea(ChatColor.RED + "Bounty" + ChatColor.DARK_GRAY + " Fulfilled").setTitleb(ChatColor.DARK_RED + "DEAD " + ChatColor.DARK_GRAY + "<> %s").setTitlec(ChatColor.AQUA + "REWARD: %s Skill").setDisplay(60).setDelay(10).setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.VERYLOW)),
	BOUNTY_HUNTING(new Notification().setTitlea(ChatColor.RED + "Bounty" + ChatColor.DARK_GRAY + " Active").setTitleb(ChatColor.DARK_RED + "%s IS HUNTING YOU DOWN").setTitlec(ChatColor.AQUA + "Better run!").setDisplay(60).setDelay(10).setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.HIGH)),
	BOUNTY_TAKEN(new Notification().setTitlea(ChatColor.RED + "Bounty" + ChatColor.DARK_GRAY + " Accepted").setTitleb(ChatColor.DARK_RED + "Find and KILL %s").setTitlec(ChatColor.AQUA + "Better get going!").setDisplay(60).setDelay(10).setAudio(Audio.REGION_LOST).setPriority(NotificationPriority.HIGH)),
	NO_ACTION(new Notification().setTitlec(ChatColor.RED + "No action is going on right now.").setDisplay(30).setAudio(Audio.UI_FAIL).setPriority(NotificationPriority.HIGH).setNoDupe(true).setDupeTag(UUID.randomUUID())),
	NO_ACTION_COOLDOWN(new Notification().setTitlec(ChatColor.RED + "You must wait %s before using this.").setDisplay(30).setAudio(Audio.UI_FAIL).setPriority(NotificationPriority.HIGH).setNoDupe(true).setDupeTag(UUID.randomUUID())),
	GAME_ALREADY_PLAYING(new Notification().setTitleb(ChatColor.RED + "You are already playing this game!").setTitlec(ChatColor.DARK_RED + "You can use /leave!").setDisplay(50).setAudio(Audio.UI_FAIL));
	
	private Notification notification;
	
	private NotificationPreset(Notification notification)
	{
		this.notification = notification;
	}

	public Notification getNotification()
	{
		return notification.copy();
	}
	
	public Notification format(Object[] a, Object[] b, Object[] c)
	{
		Notification n = notification.copy();
		
		if(a != null)
		{
			if(n.getTitlea() != null && !n.getTitlea().equals("") && !n.getTitlea().equals(" "))
			{
				n.setTitlea(String.format(n.getTitlea(), a));
			}
		}
		
		if(b != null)
		{
			if(n.getTitleb() != null && !n.getTitleb().equals("") && !n.getTitleb().equals(" "))
			{
				n.setTitleb(String.format(n.getTitleb(), b));
			}
		}
		
		if(c != null)
		{
			if(n.getTitlec() != null && !n.getTitlec().equals("") && !n.getTitlec().equals(" "))
			{
				n.setTitlec(String.format(n.getTitlec(), c));
			}
		}
		
		return n;
	}
}
