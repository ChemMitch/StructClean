create or replace trigger NEWDB2D_UPD_TRIG
	BEFORE INSERT OR UPDATE ON "NEWDB_2D_MOLTABLE"
	FOR EACH ROW
	DECLARE
        --molname VARCHAR2(4000);
	smi clob;
	inchival clob;
	inchikeyval varchar2(40);
	preCommandCount number;
	CORINACOMMANDresult varchar2(128);
	delete_old_3d_command varchar2(256);
	insert_new3d_command varchar2(256);

	model_source_value varchar2(32) := 'CORINA 3.48 0022  22.01.2010';
	MOLNAMET1 VARCHAR2(100);
	V_JOB_NAME_3D VARCHAR2(256);
	V_JOB_NAME_PUBCHEM VARCHAR2(256);
	V_JOBNO_3D NUMBER;
	v_new_molfile CLOB;
	STAR_ATOM constant varchar2(3) := ' * ';

BEGIN
	dbms_output.enable;
	dbms_output.put_line( ':NEW.ID' || to_char(:NEW.ID));


	IF ((INSERTING AND :NEW.CDBREGNO IS NOT NULL) OR (UPDATING('CDBREGNO')))
	THEN
		RAISE_APPLICATION_ERROR(-20100, 'CDBREGNO is not user-updatable');
	END IF;
	IF (INSERTING) THEN
		-- Generate cdbregno
		SELECT "MDLDB_2D".NEWDB_2D_SQ_CDBREGNO.NEXTVAL INTO :NEW.CDBREGNO FROM DUAL;
	END IF;
	IF (INSERTING) THEN
		EXECUTE IMMEDIATE 'INSERT INTO "NEWDB_2D_KEYS" (CDBREGNO,MOL2DKEYS) VALUES (:cdbregno,:MOL2DKEYS)'
			USING :NEW.CDBREGNO,MDLAUX.MOLKEYS('"MDLDB_2D".newdb_2d_molidx',:NEW.CTAB,'DEC DELIM=, LEAD TRAIL SUB');

		--dbms_output.enable;
		dbms_output.put_line( 'value of new.id: ' || :new.id);

		EXECUTE IMMEDIATE 'select count(*) from chemid.smiles1 where txtsuperlistid = upper(:superlistid)' 
			into preCommandCount using :NEW.ID;
		dbms_output.put_line( 'value of preCommandCount: ' || to_char(preCommandCount));

		if( preCommandCount > 0) then
			dbms_output.put_line( 'Before delete command ' );
			execute immediate 'delete from chemid.smiles1 where txtsuperlistid = upper(:superlistid)' using :new.ID;
			dbms_output.put_line( 'After delete command ' );
		end if;

		execute immediate 'select count(*) from chemid.INCHI2 where id = upper(:superlistid)' 
			into preCommandCount using :NEW.ID;
		dbms_output.put_line( 'second value of preCommandCount: ' || to_char(preCommandCount));
		if( preCommandCount > 0) then
			execute immediate 'delete from chemid.INCHI2 where id = upper(:superlistid)' using :NEW.ID;
			dbms_output.put_line( 'After second delete command ' );
		end if;

		if( :new.ctab is not null) then

			select smiles(:new.ctab) into smi from dual;
			if( smi is null) then
				dbms_output.put_line( 'SMILES not calculated!' );
			END IF;
			--dbms_output.put_line( 'value of smi: ' || substr(smi,1,20));

			if( smi is not null and length(smi) > 0) then
				execute immediate 'insert into chemid.smiles1 (txtsuperlistid, smiles) values (upper(:superlistid), :newsmiless)' 
					using :NEW.ID, smi;
			end if;

			select inchi(:new.ctab) into inchival from dual;
			select inchikey(:new.ctab) into inchikeyval from dual;

			IF( LENGTH(INCHIVAL) > 0 AND LENGTH(INCHIKEYVAL) > 0) THEN
				IF( INSTR(INCHIVAL, 'InChI=') > 0) THEN
					INCHIVAL := SUBSTR(INCHIVAL,7);
				end if;
				execute immediate 'insert into chemid.INCHI2 (id, inchi, inchikey) values (upper(:superlistid), :inchiv, :inchikeyv)'
					using :NEW.ID, inchival, inchikeyval;
			end if;
			
			--Mol Wt record in CHEMID
			execute immediate 'UPDATE chemid.tbl_superlist SET molweight=(SELECT molwt(:struct) from dual), inchikey=:inchikeyval, last_mod=sysdate WHERE txtsuperlistid = upper(:superlistid)'
				using :new.ctab, inchikeyval, :new.id;

		else
			dbms_output.put_line( 'CTAB is null!');
		end if;
	ELSIF (UPDATING('CTAB')) THEN
		EXECUTE IMMEDIATE 'UPDATE "NEWDB_2D_KEYS" SET MOL2DKEYS=:MOL2DKEYS WHERE CDBREGNO=:cdbregno'
			USING MDLAUX.MOLKEYS('"MDLDB_2D".newdb_2d_molidx',:NEW.CTAB,'DECDELIM=, LEAD TRAIL SUB'),:OLD.CDBREGNO;

		execute immediate 'delete from chemid.smiles1 where txtsuperlistid = upper(:superlistid)'
			using :NEW.ID;

		execute immediate 'delete from chemid.INCHI2 where id = upper(:superlistid)'
			using :NEW.ID;

		select smiles(:new.ctab) into smi from dual;
		if( length(smi) > 0) then
			execute immediate 'insert into chemid.smiles1 (txtsuperlistid, smiles) values (upper(:superlistid), :newsmiless)'
				using :NEW.ID, smi;
		end if;

		select inchi(:new.ctab) into inchival from dual;
		select inchikey(:new.ctab) into inchikeyval from dual;

		if( length(inchival) > 0 and length(inchikeyval) > 0) then
			IF( INSTR(INCHIVAL, 'InChI=') > 0) THEN
				INCHIVAL := SUBSTR(INCHIVAL,7);
			END IF;
			
			execute immediate 'insert into chemid.INCHI2 (id, inchi, inchikey) values (upper(:superlistid), :inchiv, :inchikeyv)'
				using :NEW.ID, inchival, inchikeyval;
		end if;
		
		--Mol Wt record in CHEMID
		execute immediate 'UPDATE chemid.tbl_superlist SET molweight=(SELECT molwt(:struct) from dual), inchikey=:inchikeyval, last_mod=sysdate WHERE txtsuperlistid = upper(:superlistid)'
			using :new.ctab, :new.id;
	END IF;


	IF ((INSERTING AND :NEW.MOLWEIGHT IS NULL) OR (UPDATING('CTAB') AND NOT UPDATING('MOLWEIGHT'))) THEN
		:NEW.MOLWEIGHT := MDLAUX.MOLWT('"MDLDB_2D".newdb_2d_molidx',:NEW.CTAB);
	END IF;
	IF ((INSERTING AND :NEW.STRUCT_DATE IS NULL) OR (UPDATING('CTAB') AND NOT UPDATING('STRUCT_DATE'))) THEN

		:NEW.STRUCT_DATE := SYSDATE;
	END IF;

	IF ((INSERTING OR UPDATING('ID')) AND :NEW.ID IS NOT NULL) THEN
		 -- Upper-case extreg
		:NEW.ID := UPPER(:NEW.ID);
	END IF;

	-- write MOL file and update MOL weight in chemid.superlist
	if (:new.ctab is not null) and (:new.id is not null) then
		
		UPDATE chemid.tbl_superlist
		SET molweight= :new.molweight, inchikey=inchikeyval, LAST_MOD=SYSDATE
		WHERE
		txtsuperlistid = :new.id;
		dbms_output.put_line( 'updated molweight in chemid.tbl_superlist to be: ' ||
		 :new.molweight);
	end if;

	 --3D
	if((inserting or UPDATING) and :new.ctab IS NOT NULL AND (:new.do_3d IS NULL OR upper(:new.do_3d ) = 'TRUE')) then
		select molfile(:new.ctab ) into v_new_molfile from dual;
		--make sure the molfile does not contain '*' atoms
		if instr(v_new_molfile, STAR_ATOM ) =0 then
			v_job_name_3d := 'RUNCORINAproc( ''' ||:new.id || ''',''id'', ''ctab'', ''NEWDB_2D_MOLTABLE'', ''mdldb_3d.newdb3d3_moltable'');';
			dbms_job.submit( V_JOBNO_3D, v_job_name_3d, sysdate, null);
			DBMS_OUTPUT.PUT_LINE('Submitted Job number (for 3D) : ' || TO_CHAR(V_JOBNO_3D));
		end if;

	end if;

END;