/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.thinkscience.demo.structurecleaner.objects.impl;

//import com.sun.xml.internal.ws.util.StringUtils;
import com.thinkscience.demo.structurecleaner.objects.ifc.IStructureProcessor;
import java.sql.*;
import java.util.ArrayList;
import org.apache.commons.lang3.StringUtils;
/**
 *
 * @author Mitch
 */
public class StructureProcessor implements IStructureProcessor
{

	//Transform a set of structures using a Cheshire script
	/*
	 * @param
	 */
	@Override
	public boolean processUsingCheshire(String tableName,
			String templateStructure, ArrayList<String> IDs,
			String script, String sqlToRunScript, String idFieldName,
			Connection conn, StringBuilder errMsgs)
	{

		//Now the SQL to adjust the structures
		String updateSQL = String.format(sqlToRunScript, tableName, script,
				idFieldName, StringUtils.join(IDs.toArray(), ","));
		try
		{
			
			//make sure we don't commit until ready
			conn.setAutoCommit(false);

			System.out.println("processUsingCheshire is about to use SQL "
					+ updateSQL);
			Statement statementFix = conn.createStatement();
			int result = statementFix.executeUpdate(updateSQL);

			if (result == 0)
			{
				SQLWarning warning1 = statementFix.getWarnings();

				String msg ="executeUpdate() returned 0 in processUsingCheshire";
				if( warning1 != null ) msg += "Warning: " + warning1.getMessage();
				System.out.println( msg );
				errMsgs.append(msg);
				return statementFix.execute(updateSQL);
			}
			else
			{
				conn.commit();
			}
		} catch (SQLException ex2)
		{
			System.out.println("Error running Cheshire update SQL: "
					+ ex2.getMessage());
			System.out.println("	SQL: " + updateSQL);
			errMsgs.append("Error running Cheshire update SQL: ");
			errMsgs.append( ex2.getMessage());
			return false;
		}

		return true;
	}

	@Override
	public boolean initCheshire(String cheshireInitSQL,
			Connection conn, StringBuilder errorMsgs)
	{
		//First, sql to intialize the Cheshire environment
		try
		{
			Statement statement1 = conn.createStatement();
			ResultSet initResult = statement1.executeQuery(cheshireInitSQL);
			if (initResult == null || !initResult.next())
			{
				//Error!
				System.out.println("Error initializing Cheshire; command did not produce a result!");
				errorMsgs.append( "Error initializing Cheshire; command did not produce a result!");
				return false;
			} else
			{
				return true;
			}
		} catch (SQLException ex)
		{
			System.out.println("Error initializing Cheshire: "
					+ ex.getMessage());
			errorMsgs.append("Error initializing Cheshire: ");
			errorMsgs.append(ex.getMessage());
			return false;
		}
	}

	@Override
	public boolean runCheshireMoleculeLoadingSQL(String targetMolString,
			String baseCheshireLoadingSQL, Connection conn, StringBuilder errorMessages,
			String cheshireTask)
	{
		//Form the complete command
		String actualCheshireLoadingSQL = String.format(baseCheshireLoadingSQL,
				targetMolString);
		String debugMsg = "runCheshireMoleculeLoadingSQL about to use SQL '"
				+  actualCheshireLoadingSQL + "'";
		System.out.println(debugMsg);
		
		try
		{
			Statement statement1 = conn.createStatement();
			ResultSet initResult = statement1.executeQuery(actualCheshireLoadingSQL);
			if (initResult == null || !initResult.next())
			{
				//Error!
				String msg = String.format("Error performing %s; command did not produce a result!",
						cheshireTask);
				System.err.println(msg );
				errorMessages.append(msg);
				return false;
			} else
			{
				int result = initResult.getInt(1);
				if( result <= 0)
				{
				String msg = String.format("Error performing %s; command did produced a 0 or negative result!",
						cheshireTask);
				System.err.println(msg );
				errorMessages.append(msg);
					return false;
				}
			}
			return true;

		} catch (SQLException ex)
		{
			String msg = String.format("Error performing %s: %s",
					cheshireTask, ex.getMessage());

			System.out.println(msg);
			errorMessages.append(msg);
			return false;
		}
	}

	@Override
	public boolean runGeneralCheshireSQL( String cheshireLoadingSQL,
			Connection conn, StringBuilder errorMessages, String cheshireTask)
	{
		try
		{
			Statement statement1 = conn.createStatement();
			ResultSet initResult = statement1.executeQuery(cheshireLoadingSQL);
			if (initResult == null || !initResult.next())
			{
				//Error!
				String msg = String.format("Error performing %s; command did not produce a result!",
						cheshireTask);
				System.out.println(msg );
				errorMessages.append(msg);
				return false;
			} else
			{
				return true;
			}
		} catch (SQLException ex)
		{
			String msg = String.format("Error performing %s: %s",
					cheshireTask, ex.getMessage());

			System.out.println(msg);
			errorMessages.append(msg);
			return false;
		}
	}

}
