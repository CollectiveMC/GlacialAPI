package com.glacialrush.api.dispatch.notification;

import java.util.UUID;
import org.bukkit.entity.Player;
import com.glacialrush.api.GlacialPlugin;
import com.glacialrush.api.dispatch.Title;
import com.glacialrush.api.sfx.Audio;

public class Notification
{
	private String titlea;
	private String titleb;
	private String titlec;
	private Integer delay;
	private Integer display;
	private Audio audio;
	private Boolean ongoing;
	private NotificationPriority priority;
	private Boolean noDupe;
	private UUID dupeTag;
	
	public Notification()
	{
		this.display = 1;
		this.priority = NotificationPriority.MEDIUM;
		this.ongoing = false;
		this.noDupe = false;
		this.delay = 0;
	}
	
	public Notification(String titlea, String titleb, String titlec, Integer delay, Audio audio)
	{
		this.titlea = titlea;
		this.titleb = titleb;
		this.titlec = titlec;
		this.delay = delay;
		this.audio = audio;
		this.display = 1;
		this.priority = NotificationPriority.MEDIUM;
		this.ongoing = false;
		this.noDupe = false;
		this.delay = 0;
	}
	
	public Notification(String titlea, String titleb, String titlec, Audio audio)
	{
		this.titlea = titlea;
		this.titleb = titleb;
		this.titlec = titlec;
		this.audio = audio;
		this.display = 1;
		this.priority = NotificationPriority.MEDIUM;
		this.ongoing = false;
		this.noDupe = false;
		this.delay = 0;
	}
	
	public Notification(String titlea, String titleb, String titlec)
	{
		this.titlea = titlea;
		this.titleb = titleb;
		this.titlec = titlec;
		this.display = 1;
		this.priority = NotificationPriority.MEDIUM;
		this.ongoing = false;
		this.noDupe = false;
		this.delay = 0;
	}
	
	public void show(final Player p)
	{
		if(audio != null && !ongoing)
		{
			audio.play(p);
		}
		
		if((titlea != null && !titlea.equals("") && !titlea.equals(" ")) || (titleb != null && !titleb.equals("") && !titleb.equals(" ")) || (titlec != null && !titlec.equals("") && !titlec.equals(" ")))
		{
			final Title t = new Title();
			
			t.setFadeInTime(0);
			t.setFadeOutTime(45);
			t.setStayTime(display);
			
			if(titlea != null && !titlea.equals("") && !titlea.equals(" "))
			{
				t.setTitle(titlea);
			}
			
			if(titleb != null && !titleb.equals("") && !titleb.equals(" "))
			{
				t.setSubTitle(titleb);
			}
			
			if(titlec != null && !titlec.equals("") && !titlec.equals(" "))
			{
				t.setSubSubTitle(titlec);
			}
			
			if(delay != null)
			{
				GlacialPlugin.instance().scheduleSyncTask(delay, new Runnable()
				{
					@Override
					public void run()
					{
						t.send(p);
					}
				});
			}
			
			else
			{
				t.send(p);
			}
		}
	}
	
	public Notification noDupe(UUID uuid)
	{
		noDupe = true;
		dupeTag = uuid;
		return this;
	}

	public String getTitlea()
	{
		return titlea;
	}

	public Notification setTitlea(String titlea)
	{
		this.titlea = titlea;
		return this;
	}

	public String getTitleb()
	{
		return titleb;
	}

	public Notification setTitleb(String titleb)
	{
		this.titleb = titleb;
		return this;
	}

	public String getTitlec()
	{
		return titlec;
	}

	public Notification setTitlec(String titlec)
	{
		this.titlec = titlec;
		return this;
	}

	public Integer getDelay()
	{
		return delay;
	}

	public Notification setDelay(Integer delay)
	{
		this.delay = delay;
		return this;
	}

	public Audio getAudio()
	{
		return audio;
	}

	public Notification setAudio(Audio audio)
	{
		this.audio = audio;
		return this;
	}

	public Integer getDisplay()
	{
		return display;
	}

	public Notification setDisplay(Integer display)
	{
		this.display = display;
		return this;
	}
	
	public Boolean getNoDupe()
	{
		return noDupe;
	}

	public Notification setNoDupe(Boolean noDupe)
	{
		this.noDupe = noDupe;
		return this;
	}

	public UUID getDupeTag()
	{
		return dupeTag;
	}

	public Notification setDupeTag(UUID dupeTag)
	{
		this.dupeTag = dupeTag;
		return this;
	}

	public Boolean getOngoing()
	{
		return ongoing;
	}

	public Notification copy()
	{
		return new Notification().setTitlea(titlea).setTitleb(titleb).setTitlec(titlec).setDelay(delay).setDisplay(display).setAudio(audio).setOngoing(ongoing).setPriority(priority).setNoDupe(noDupe).setDupeTag(dupeTag);
	}

	public Boolean isOngoing()
	{
		return ongoing;
	}

	public Notification setOngoing(Boolean ongoing)
	{
		this.ongoing = ongoing;
		return this;
	}

	public NotificationPriority getPriority()
	{
		return priority;
	}

	public Notification setPriority(NotificationPriority priority)
	{
		this.priority = priority;
		return this;
	}
}
