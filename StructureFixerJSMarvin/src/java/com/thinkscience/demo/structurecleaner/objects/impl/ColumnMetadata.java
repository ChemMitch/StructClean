/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.impl;

import com.thinkscience.demo.structurecleaner.objects.ifc.FieldType;
import com.thinkscience.demo.structurecleaner.objects.ifc.IColumnMetadata;
import java.io.Serializable;

/**
 * This class represents information about one field in a ResultsSet
 * that the application will process into an HTML table.
 * @author Mitch
 */
public class ColumnMetadata implements Serializable, IColumnMetadata
{

	private String columnName;
	private String columnLabel;
	private String columnCSSClass;
	private FieldType columnType;
	private double structureBoxWidth;
	private double structureBoxHeight;
	private boolean isID;
	private boolean displayColumn;
	private String columnFormat;

	public ColumnMetadata( )
	{
		columnName = "";
		columnLabel ="";
		columnCSSClass  = "";
		columnType = FieldType.UNKNOWN;
		structureBoxWidth =0;
		isID = false;
		displayColumn = false;
		columnFormat = "";
	}

	public ColumnMetadata( String colName, String colLabel, String colCSSClass,
		FieldType colType, double strBoxWidth,  double strBoxHeight,
		boolean isIDField, boolean displayCol,
		String colFormat)
	{
		columnName = colName;
		columnLabel =colLabel;
		columnCSSClass  = colCSSClass;
		columnType = colType;
		structureBoxWidth = strBoxWidth;
		structureBoxHeight =strBoxHeight;
		isID = isIDField;
		displayColumn = displayCol;
		columnFormat = colFormat;
	}

	/**
	 * @return the columnName
	 */
	public String getColumnName()
	{
		return columnName;
	}

	/**
	 * @param columnName the columnName to set
	 */
	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	/**
	 * @return the columnLabel
	 */
	public String getColumnLabel()
	{
		return columnLabel;
	}

	/**
	 * @param columnLabel the columnLabel to set
	 */
	public void setColumnLabel(String columnLabel)
	{
		this.columnLabel = columnLabel;
	}

	/**
	 * @return the columnCSSClass
	 */
	public String getColumnCSSClass()
	{
		return columnCSSClass;
	}

	/**
	 * @param columnCSSClass the columnCSSClass to set
	 */
	public void setColumnCSSClass(String columnCSSClass)
	{
		this.columnCSSClass = columnCSSClass;
	}

	/**
	 * @return the columnType
	 */
	public FieldType getColumnType()
	{
		return columnType;
	}

	/**
	 * @param columnType the columnType to set
	 */
	public void setColumnType(FieldType columnType)
	{
		this.columnType = columnType;
	}

	/**
	 * @return the structureBoxWidth
	 */
	public double getStructureBoxWidth()
	{
		return structureBoxWidth;
	}

	/**
	 * @param structureBoxWidth the structureBoxWidth to set
	 */
	public void setStructureBoxWidth(double structureBoxWidth)
	{
		this.structureBoxWidth = structureBoxWidth;
	}

	/**
	 * @return the isID
	 */
	public boolean isIsID()
	{
		return isID;
	}

	/**
	 * @param isID the isID to set
	 */
	public void setIsID(boolean isID)
	{
		this.isID = isID;
	}

	/**
	 * @return the displayColumn
	 */
	public boolean isDisplayColumn()
	{
		return displayColumn;
	}

	/**
	 * @param displayColumn the displayColumn to set
	 */
	public void setDisplayColumn(boolean displayColumn)
	{
		this.displayColumn = displayColumn;
	}

	/**
	 * @return the numberFormat
	 */
	public String getColumnFormat()
	{
		return columnFormat;
	}

	/**
	 * @param numberFormat the numberFormat to set
	 */
	public void setColumnFormat(String columnFormat)
	{
		this.columnFormat = columnFormat;
	}

	/**
	 * @return the structureBoxHeight
	 */
	public double getStructureBoxHeight()
	{
		return structureBoxHeight;
	}

	/**
	 * @param structureBoxHeight the structureBoxHeight to set
	 */
	public void setStructureBoxHeight(double structureBoxHeight)
	{
		this.structureBoxHeight = structureBoxHeight;
	}
}
