/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.objects.impl;

import java.util.ArrayList;
import com.thinkscience.demo.structurecleaner.objects.ifc.IFieldData;
import com.thinkscience.demo.structurecleaner.objects.ifc.IFieldDataList;

/**
 *
 * @author Mitch
 */
public class FieldDataList implements IFieldDataList
{

	private ArrayList<IFieldData> _data;

	public ArrayList<IFieldData> getData()
	{
		return _data;
	}

	public void setData(ArrayList<IFieldData> data)
	{
		_data = data;
	}

	public String getStringDataForField(String fieldName)
	{
		Object data = getDataForField(fieldName);
		if (data != null)
		{
			return data.toString();
		}
		return null;
	}

	public Double getDoubleDataForField(String fieldName)
	{
		Double ret = null;

		Object data = getDataForField(fieldName);
		if (data != null)
		{
			try
			{
				ret = Double.parseDouble(data.toString());
			} catch (NumberFormatException nfe)
			{
			}
		}
		return ret;
	}

	public Float getFloatDataForField(String fieldName)
	{
		Float ret = null;

		Object data = getDataForField(fieldName);
		if (data != null)
		{
			try
			{
				ret = Float.parseFloat(data.toString());
			} catch (NumberFormatException nfe)
			{
			}
		}
		return ret;
	}

	public Integer getIntegerDataForField(String fieldName)
	{
		Integer ret = null;

		Object data = getDataForField(fieldName);
		if (data != null)
		{
			try
			{
				ret = Integer.parseInt(data.toString());
			} catch (NumberFormatException nfe)
			{
			}
		}
		return ret;
	}

	public Object getDataForField(String fieldName)
	{
		for (IFieldData field : _data)
		{
			if (field.getFieldName().equalsIgnoreCase(fieldName))
			{
				return field.getFieldValue();
			}
		}
		return null;
	}
}
