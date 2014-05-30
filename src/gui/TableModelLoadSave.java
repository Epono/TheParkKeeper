package gui;

import java.text.DateFormat;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import management.Map;

/** Manage the table showing the saves.
 */
public class TableModelLoadSave extends AbstractTableModel {
	private static final long serialVersionUID = 8664485939023909772L;
	private ArrayList<Map> saves;
	private final String[] headers = { "Name", "Creation Date", "Number of turns", "Duration" };

	public TableModelLoadSave(ArrayList<Map> saves) {
		this.saves = saves;
	}

	@Override
	public int getRowCount() {
		return saves.size();
	}

	@Override
	public int getColumnCount() {
		return headers.length;
	}

	@Override
	public String getColumnName(int columnIndex) {
		return headers[columnIndex];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		switch (columnIndex) {
		case 0:
			return saves.get(rowIndex).getName();
		case 1:
			return DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT).format(saves.get(rowIndex).getCreationDate());
		case 2:
			return saves.get(rowIndex).getNumberOfTurns();
		case 3:
			return DateFormat.getTimeInstance().format(saves.get(rowIndex).getDuration() - 3600000);
		default:
			throw new IllegalArgumentException();
		}
	}

	/** Refreshes the table with the given <b>Map</b> list.
	 * 
	 * @param newData : the <b>Map</b> list to be used to refresh the table.
	 */
	public void setData(ArrayList<Map> newData) {
		saves = newData;
		super.fireTableDataChanged();
	}
}