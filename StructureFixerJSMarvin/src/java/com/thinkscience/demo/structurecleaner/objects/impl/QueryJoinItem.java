/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.objects.impl;

import com.thinkscience.demo.structurecleaner.objects.ifc.IQueryJoinItem;
/**
 *
 * @author Mitch
 */
public class QueryJoinItem implements IQueryJoinItem
{
	public QueryJoinItem(String newLeftTable, String newLeftItem,
			String newRightTable, String newRightItem)
	{
		_leftTableRerence = newLeftTable;
		_leftFieldName =newLeftItem;
		_rightTableRerence = newRightTable;
		_rightFieldName=	newRightItem;
	}
	public String getLeftFieldName()
	{
		return _leftFieldName;
	}
	public void setLeftFieldName(String newValue)
	{
		_leftFieldName = newValue;
	}

	public String getRightFieldName()
	{
		return _rightFieldName;
	}

	public void setRightFieldName(String newValue)
	{
		_rightFieldName = newValue;
	}
	public String getLeftTableRerence()
	{
		return _leftTableRerence;
	}
	public String getRightTableRerence()
	{
		return _rightTableRerence;
	}
	public void setRightTableRerence(String newValue)
	{
		_rightTableRerence = newValue;
	}
	public void setLeftTableRerence(String newValue)
	{
		_leftTableRerence = newValue;
	}

	private String _leftFieldName;
	private String _rightFieldName;
	private String _leftTableRerence;
	private String _rightTableRerence;
}
