/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.utilities;

import java.util.ArrayList;

/**
 *
 * @author Mitch
 */
public class ArrayListUtilities
{

	public static void DumpArrayList(ArrayList<String> data, String introduction)
	{
		System.out.println(introduction);
		if (data == null)
		{
			System.out.println(" [NULL] ");
			return;
		}
		for (String item : data)
		{
			System.out.println("\t" + item);
		}

	}

	public static void ConsolidateArrayLists(ArrayList<String> oldList,
			ArrayList<String> newList, ArrayList<String> listToIgnore)
	{
		if (oldList != null && oldList.size() > 0)
		{
			if (listToIgnore != null)
			{
				oldList.removeAll(listToIgnore);
			}
			for (String id : oldList)
			{
				if (!newList.contains(id))
				{
					newList.add(id);
				}
			}
		}
	}
}
