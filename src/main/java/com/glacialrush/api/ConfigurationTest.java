package com.glacialrush.api;

import com.glacialrush.api.configuration.Configuration;
import com.glacialrush.api.object.GList;

public class ConfigurationTest implements Configuration
{
	private String testString;
	private Boolean testBoolean;
	private Byte testByte;
	private Short testShort;
	private Long testLong;
	private Double testDouble;
	private Float testFloat;
	private Integer testInteger;
	private GList<String> testGListString;
	
	public ConfigurationTest inst()
	{
		testString = "test";
		testBoolean = false;
		testByte = 4;
		testShort = 3;
		testLong = 123456789l;
		testDouble = 0.554;
		testFloat = 0.909f;
		testInteger = 534;
		testGListString = new GList<String>().qadd("test").qadd("list");
		
		return this;
	}

	public String getTestString()
	{
		return testString;
	}

	public void setTestString(String testString)
	{
		this.testString = testString;
	}

	public Boolean getTestBoolean()
	{
		return testBoolean;
	}

	public void setTestBoolean(Boolean testBoolean)
	{
		this.testBoolean = testBoolean;
	}

	public Byte getTestByte()
	{
		return testByte;
	}

	public void setTestByte(Byte testByte)
	{
		this.testByte = testByte;
	}

	public Short getTestShort()
	{
		return testShort;
	}

	public void setTestShort(Short testShort)
	{
		this.testShort = testShort;
	}

	public Long getTestLong()
	{
		return testLong;
	}

	public void setTestLong(Long testLong)
	{
		this.testLong = testLong;
	}

	public Double getTestDouble()
	{
		return testDouble;
	}

	public void setTestDouble(Double testDouble)
	{
		this.testDouble = testDouble;
	}

	public Float getTestFloat()
	{
		return testFloat;
	}

	public void setTestFloat(Float testFloat)
	{
		this.testFloat = testFloat;
	}

	public Integer getTestInteger()
	{
		return testInteger;
	}

	public void setTestInteger(Integer testInteger)
	{
		this.testInteger = testInteger;
	}

	public GList<String> getTestGListString()
	{
		return testGListString;
	}

	public void setTestGListString(GList<String> testGListString)
	{
		this.testGListString = testGListString;
	}
}
