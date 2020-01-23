/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.thinkscience.demo.structurecleaner.objects.impl;

import com.thinkscience.demo.structurecleaner.objects.ifc.IOverlaidStructureData;

/**
 *
 * @author Mitch
 */
public class OverlaidStructureData  implements IOverlaidStructureData{

	public OverlaidStructureData()
	{
	}
	
	public OverlaidStructureData( String molName, double molWt, String molFMLA,
			String id)
	{
		_molName = molName;
		_molWt = molWt;
		_molFMLA = molFMLA;
		_id = id;
	}

	public OverlaidStructureData( String molName, double molWt, String molFMLA,
			String id, String chimeStringBefore, String chimeStringAfter )
	{
		_molName = molName;
		_molWt = molWt;
		_molFMLA = molFMLA;
		_id = id;
		_chimeStringBefore =chimeStringBefore;
		_chimeStringAfter =chimeStringAfter;
	}

	public OverlaidStructureData( String molName, double molWt, String molFMLA,
			String id, String chimeStringBefore, String chimeStringAfter,
			String chimeStringAfter2 )
	{
		_molName = molName;
		_molWt = molWt;
		_molFMLA = molFMLA;
		_id = id;
		_chimeStringBefore =chimeStringBefore;
		_chimeStringAfter =chimeStringAfter;
		_chimeStringAfter2 =chimeStringAfter2;
	}

	private String _molName;
	private byte[] _imageData;
	private double _molWt;
	private String _molFMLA;
	private String _id;
	private String _chimeStringBefore;
	private String _chimeStringAfter;
	private String _chimeStringAfter2;
	/**
	 * @return the _molName
	 */
	public String getMolName()
	{
		return _molName;
	}

	/**
	 * @param molName the _molName to set
	 */
	public void setMolName(String molName)
	{
		this._molName = molName;
	}

	/**
	 * @return the _imageData
	 */
	public byte[] getImageData()
	{
		return _imageData;
	}

	/**
	 * @param imageData the _imageData to set
	 */
	public void setImageData(byte[] imageData)
	{
		this._imageData = imageData;
	}

	/**
	 * @return the _molWt
	 */
	public double getMolWt()
	{
		return _molWt;
	}

	/**
	 * @param molWt the _molWt to set
	 */
	public void setMolWt(double molWt)
	{
		this._molWt = molWt;
	}

	/**
	 * @return the _molFMLA
	 */
	public String getMolFMLA()
	{
		return _molFMLA;
	}

	/**
	 * @param molFMLA the _molFMLA to set
	 */
	public void setMolFMLA(String molFMLA)
	{
		this._molFMLA = molFMLA;
	}

	/**
	 * @return the _id
	 */
	public String getId()
	{
		return _id;
	}

	/**
	 * @param id the _id to set
	 */
	public void setId(String id)
	{
		this._id = id;
	}
	public String getChimeStringAfter()
	{
		return _chimeStringAfter;
	}

	public String getChimeStringAfter2()
	{
		return _chimeStringAfter2;
	}

	public void setChimeStringAfter(String _chimeStringAfter)
	{
		this._chimeStringAfter = _chimeStringAfter;
	}

	public void setChimeStringAfter2(String _chimeStringAfter)
	{
		this._chimeStringAfter = _chimeStringAfter2;
	}

	public String getChimeStringBefore()
	{
		return _chimeStringBefore;
	}

	public void setChimeStringBefore(String _chimeStringBefore)
	{
		this._chimeStringBefore = _chimeStringBefore;
	}

}
