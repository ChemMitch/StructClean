/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.objects.ifc;

import java.util.ArrayList;

/**
 * Represents a set of Fields (name and data) for one record from a
 * database.
 * @author Mitch
 */
public interface IFieldDataList
{

	public ArrayList<IFieldData> getData();

	public void setData(ArrayList<IFieldData> data);

	public String getStringDataForField(String fieldName);

	public Double getDoubleDataForField(String fieldName);

	public Float getFloatDataForField(String fieldName);

	public Integer getIntegerDataForField(String fieldName);

	public Object getDataForField(String fieldName);
}
