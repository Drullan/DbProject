import java.util.ArrayList;
import java.util.Date;

import javax.swing.table.AbstractTableModel;


public class PalletTable extends AbstractTableModel{
	private String[] columnNames;
	private ArrayList<Object[]> data;
	private Database db;
	private String palletId;
	private String cookieN;
	private String producedFrom;
	private String producedTo;
	private String customer;
	private int blocked;
	public PalletTable(Database db){
		super();
		this.db=db;
		columnNames = new String[]{"Pallet id",
	            "Product Name",
	            "Location",
	            "Placed Date",
	            "Delivery Date",
	            "Date Produced",
	            "Date Delivered",
	            "Destination",
	            "Order Nbr",
	            "Blocked"};
		data = db.getPallets();
		palletId="";
		cookieN="";
		producedFrom="";
		producedTo="";
		customer="";
		blocked=0;
	}
	
	public void update(){
		filter(palletId,cookieN,producedFrom,producedTo,customer,blocked);
	//	data=db.getPallets();
	}
	
	public void filter(String palletId,String cookieN,String producedFrom, String producedTo,String customer, int blocked){
		this.palletId=palletId;
		this.cookieN=cookieN;
		this.producedFrom=producedFrom;
		this.producedTo=producedTo;
		this.customer=customer;
		this.blocked=blocked;
		data=db.getfilterdPallets(palletId, cookieN, producedFrom, producedTo, customer, blocked);
	}
	
	@Override
	public String getColumnName(int col) {
        return columnNames[col];
    }
	
	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		return data.get(row)[col];
	}
	
	@Override
	public Class<?> getColumnClass(int col){
		if(col==columnNames.length-1){
			return Boolean.class;
		}
		return super.getColumnClass(col);
	}
	
	
	@Override
	public boolean isCellEditable(int row, int col){
		return col==columnNames.length-1;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col){
		if(col==columnNames.length-1){
			if((boolean)data.get(row)[col]!=(boolean)value){
				db.updatePallet((int)data.get(row)[0], (boolean)value);
				update();
				fireTableCellUpdated(row,col);
				fireTableCellUpdated(row,2);
			}
		}
	}
	


}
