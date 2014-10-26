/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sk.uniza.fri.duracik2.gui.reflection;

/**
 *
 * @author Unlink
 */
public class Field
{
	private String aName;
	private Class<?> aType;

	public Field(String aName, Class<?> aType)
	{
		this.aName = aName;
		this.aType = aType;
	}

	public String getName()
	{
		return aName;
	}

	public Class<?> getType()
	{
		return aType;
	}
	
	
}
