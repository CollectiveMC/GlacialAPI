package com.glacialrush.api.game.obtainable;

import com.glacialrush.api.game.obtainable.item.AbilityEffect;
import com.glacialrush.api.game.obtainable.item.ObtainableType;
import com.glacialrush.api.sfx.Audio;

public class Ability extends Obtainable
{
	private Audio abilityFiredSound;
	private AbilityEffect abilityEffect;
	private Integer cooldown;
	private Integer duration;
	
	public Ability(ObtainableBank obtainableBank)
	{
		super(obtainableBank);
		
		setObtainableType(ObtainableType.ABILITY);
	}
	
	public Integer getCooldown()
	{
		return cooldown;
	}
	
	public void setCooldown(Integer cooldown)
	{
		this.cooldown = cooldown;
	}
	
	public Integer getDuration()
	{
		return duration;
	}
	
	public void setDuration(Integer duration)
	{
		this.duration = duration;
	}
	
	public AbilityEffect getAbilityEffect()
	{
		return abilityEffect;
	}
	
	public void setAbilityEffect(AbilityEffect abilityEffect)
	{
		this.abilityEffect = abilityEffect;
	}
	
	public Audio getAbilityFiredSound()
	{
		return abilityFiredSound;
	}
	
	public void setAbilityFiredSound(Audio abilityFiredSound)
	{
		this.abilityFiredSound = abilityFiredSound;
	}
}
