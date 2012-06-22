package pl.edu.pw.mini.jozwickij.ttfedit.tables;

import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;

public class TTFTable_cvt extends TTFTable {

	@Override
	public String getTableName() {
		return super.getTableName()+" "; /* wyrownanie do 4 bajtow */
	}
	

}
