
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;


public class MaterialTable extends AbstractTableModel{
	private String[] columnNames;
	private ArrayList<Object[]> testdata;
	private Database db;
	public MaterialTable(Database db){
		super();
		this.db=db;
		columnNames = new String[]{"Ingridient", "Quantity in Storage","Next Delivery","Delivery amount"};
	/*	testdata = new ArrayList<Object[]>();
		testdata.add(new Object[]{"butter",new Integer(0),"2016-03-16",new Integer(50)});
		testdata.add(new Object[]{"Salt", new Integer(100),"2016-03-15",new Integer(530)});
		testdata.add(new Object[]{"Sugar", new Integer(200),"2016-03-12",new Integer(250)});
		testdata.add(new Object[]{"Blueberries", new Integer(260),"2016-03-17",new Integer(150)});*/
		db.updateIngridients();
		testdata = db.getIngridients();
/*		{
					{"Butter", new Integer(0)},
					{"Salt", new Integer(100)},
					{"Sugar", new Integer(200)},
					{"Chokolate", new Integer(340)},
					{"Blueberries", new Integer(260)},
					};*/
	}
	
	public void update(){
		db.updateIngridients();
		testdata = db.getIngridients();
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
		return testdata.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		// TODO Auto-generated method stub
		return testdata.get(row)[col];
	}
	
	@Override
	public Class<?> getColumnClass(int col){
		if(col==columnNames.length-1){
			return Integer.class;
		}
		return super.getColumnClass(col);
	}
	
	public void addRow(Object[] value){
		testdata.add(value);
		fireTableDataChanged();
	}
	
	@Override
	public boolean isCellEditable(int row, int col){
		return col>columnNames.length-3;
	}
	
	@Override
	public void setValueAt(Object value, int row, int col){
		if(col==columnNames.length-1){
			
			if((int)testdata.get(row)[col]!=(int)value){
				if(db.updateIngridient((String)testdata.get(row)[0], (int)value)){
					testdata.get(row)[col] = value;
					JOptionPane.showMessageDialog(null,"Updated database!");
					fireTableCellUpdated(row,col);
				}
			}


			System.out.println("UPDATE " + testdata.get(row)[0] + ", " + testdata.get(row)[1] + ", " + value);
			//query stuff

		}
		else if(col == columnNames.length-2){
			String temp = (String)testdata.get(row)[col];
			if(!temp.equals((String)value)){
				
				SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
				try {
					Date deldate = parser.parse((String)value);
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
					if(db.updateIngridient((String)testdata.get(row)[0], df.format(deldate))){
						testdata.get(row)[col] = value;
						JOptionPane.showMessageDialog(null,"Updated database!");
						fireTableCellUpdated(row,col);
					}
				} catch (ParseException e) {
					System.out.println("Wrong formate of date please use yyyy-MM-dd");
					//e.printStackTrace();
				}

			}
			System.out.println("UPDATE " + testdata.get(row)[0] + ", " + testdata.get(row)[1] + ", " + value);
		}
		
	}
	


}
