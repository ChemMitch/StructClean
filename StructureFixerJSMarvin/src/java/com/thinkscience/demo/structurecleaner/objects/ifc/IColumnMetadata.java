/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.ifc;

/**
 *
 * @author Mitch
 */
public interface IColumnMetadata {

		public String getColumnName();
		public void setColumnName(String columnName);
		public String getColumnLabel();
		public void setColumnLabel(String columnLabel);
		public String getColumnCSSClass();
		public void setColumnCSSClass(String columnCSSClass);
		public FieldType getColumnType();
		public void setColumnType(FieldType columnType);
		public double getStructureBoxWidth();
		public void setStructureBoxWidth(double structureBoxWidth);
		public boolean isIsID();
		public void setIsID(boolean isID);
		public boolean isDisplayColumn();
		public void setDisplayColumn(boolean displayColumn);
		public String getColumnFormat();
		public void setColumnFormat(String columnFormat);
		public double getStructureBoxHeight();
		public void setStructureBoxHeight(double structureBoxHeight);
}
