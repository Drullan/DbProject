//package dbtLab3;

import java.sql.*;
//import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


/**
 * Database is a class that specifies the interface to the movie
 * database. Uses JDBC.
 */
public class Database {

    /**
     * The database connection.
     */
    private Connection conn;

    /**
     * Create the database interface object. Connection to the
     * database is performed later.
     */
    public Database() {
        conn = null;
    }

    /**
     * Open a connection to the database, using the specified user
     * name and password.
     */
    public boolean openConnection(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    //try to create give customer return warning if the customer already exists
    public String tryCreateCustomer(String name, String address){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery("select * from customer where customerName== \"" +  name + "\"");
    		if(rs.next()){
    			String addr = rs.getString("address");
    			if(address.equals(addr)){
    	    		stmt.close();
    	    		rs.close();
    				return null;
    			}
    			else{
    	    		stmt.close();
    	    		rs.close();
    				return "Warning, this customer already exists with a different address: " + addr +" do you want to override it?"; 
    			}
    			
    		}
    		stmt.executeUpdate("insert into customer(address,customerName) values (\"" + address + "\",\""+name + "\")");
    		stmt.close();
    		rs.close();
    		return null;
    		
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    	return null;
    }
    
    //update current existing customer with new address
    public void updateCustomer(String name, String address){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		stmt.executeUpdate("update customer set address = \"" + address + "\" where customerName == \"" + name + "\"");
    		stmt.close();
    		
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    
    //place an order
    public void placeOrder(String customerName, String deliveryDate,String cookieName,int quantity){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		Date date = new Date();
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
			df.format(date); 
    		stmt.executeUpdate("insert into myOrder(placedDate,deliveryDate,customerName) values (\""+df.format(date) + "\",\""+deliveryDate+"\",\""+customerName+"\")");
    		ResultSet rs = stmt.executeQuery("select orderNbr from myOrder where placedDate == \"" + df.format(date) + 
    				"\" and deliveryDate ==\"" + deliveryDate + "\" and customerName ==\"" + customerName+"\"");
    		int id = rs.getInt("orderNbr");
    		stmt.executeUpdate("insert into orderItem(orderNbr,cookieName,nbrPallets) values (" + id + ",\"" + cookieName + "\","+quantity+")");
    		stmt.close();
    		
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    }
    
    //get a list of pallets which have been filterd by given parameters, leave paramters empty to not filter
    public ArrayList<Object[]> getfilterdPallets(String palletId,String cookieN,String producedFrom, String producedTo,String customer, int blocked){
    	HashMap<Integer,Object[]> result = new HashMap<Integer,Object[]>();
    	Statement stmt;
		try {
			stmt = conn.createStatement();
			ResultSet rs;
			String pallets = palletId.equals("") ? "":" and p.palletId=="+palletId;
			String cookies = cookieN.equals("") ? "":" and p.cookieName==\""+cookieN+"\"";
			String producedf = producedFrom.equals("") ? "": " and dateProduced >= " + producedFrom;
			String producedt = producedTo.equals("") ? "": " and dateProduced <= " + producedTo;
			String customers = customer.equals("") ? "": " and m.customerName==\"" + customer + "\"";
			String blocker;
			switch(blocked){
				case 0:
					blocker = "";
					break;
				case 1:
					blocker = " and isBlocked != 0";
					break;
				case 2:
					blocker = " and isBlocked == 0";
					break;
				default:
					blocker = "";
					break;
			}
			if(customers.equals("")){
			//	cookies = cookies.replace("p.", "");
			//	pallets = pallets.replace("p.", "");
				rs = stmt.executeQuery("select * from pallet p where (location == \"in freezer\" or location == \"stalled\")"+ pallets+cookies+producedf+producedt+blocker);
		    	while(rs.next()){
		    		int id = rs.getInt("palletId");
		    		String cookieName = rs.getString("cookieName");
		    		String location = rs.getString("location");
		    		boolean isBlocked = rs.getInt("isBlocked")!=0;
		    		String dateProduced = formatDate(rs.getString("dateProduced"));
		    		String dateDelivered = formatDate(rs.getString("dateDelivered"));
		    		result.put(id, new Object[]{id,cookieName,location, "","",dateProduced,dateDelivered,"","",isBlocked});
		    	}
			}
			
			
			
			
			
			rs = stmt.executeQuery("select m.orderNbr, address,location,p.cookieName,dateDelivered,isBlocked,dateProduced,p.palletId,deliveryDate,placedDate"
					+ " from customer c,myOrder m,orderItem i,pallet p, palletItem pi"
					+ " where c.customerName==m.customerName and i.orderNbr==m.orderNbr"
					+ " and i.cookieName==p.cookieName and m.orderNbr==pi.orderNbr and p.palletId==pi.palletId"
					+ pallets+cookies+producedf+producedt+customers+blocker);
			
			while(rs.next()){
	    		int id = rs.getInt("palletId");
	    		String cookieName = rs.getString("cookieName");
	    		String destination = rs.getString("address");
	    		String location = rs.getString("location");
	    		boolean isBlocked = rs.getInt("isBlocked")!=0;
	    		String deliveryDate = formatDate(rs.getString("deliveryDate"));
	    		String dateProduced = formatDate(rs.getString("dateProduced"));
	    		String dateDelivered = formatDate(rs.getString("dateDelivered"));
	    		String placedDate = formatDate(rs.getString("placedDate"));
	    		int orderNbr = rs.getInt("orderNbr");
	    		result.put(id, new Object[]{id,cookieName,location, placedDate,deliveryDate,dateProduced,dateDelivered,destination,orderNbr,isBlocked});
	    	}

	    	rs.close();
	    	stmt.close();

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new ArrayList<Object[]>(result.values());
    }
    
    //return all pallets
    public ArrayList<Object[]> getPallets(){
    	HashMap<Integer,Object[]> result = new HashMap<Integer,Object[]>();
    	Statement stmt;
		try {
			stmt = conn.createStatement();
			
	    	
			ResultSet rs = stmt.executeQuery("select * from pallet where location == \"in freezer\" or location == \"stalled\"");
	    	while(rs.next()){
	    		int id = rs.getInt("palletId");
	    		String cookieName = rs.getString("cookieName");
	    		String location = rs.getString("location");
	    		boolean isBlocked = rs.getInt("isBlocked")!=0;
	    		String dateProduced = formatDate(rs.getString("dateProduced"));
	    		String dateDelivered = formatDate(rs.getString("dateDelivered"));
	    		result.put(id,new Object[]{id,cookieName,location, "","",dateProduced,dateDelivered,"","",isBlocked});
	    	}

			rs = stmt.executeQuery("select m.orderNbr, address,location,p.cookieName,dateDelivered,isBlocked,dateProduced,p.palletId,deliveryDate,placedDate"
											+ " from customer c,myOrder m,orderItem i,pallet p, palletItem pi"
											+ " where c.customerName==m.customerName and i.orderNbr==m.orderNbr"
											+ " and i.cookieName==p.cookieName and m.orderNbr==pi.orderNbr and p.palletId==pi.palletId");
	    	while(rs.next()){
	    		int id = rs.getInt("palletId");
	    		String cookieName = rs.getString("cookieName");
	    		String destination = rs.getString("address");
	    		String location = rs.getString("location");
	    		boolean isBlocked = rs.getInt("isBlocked")!=0;
	    		String deliveryDate = formatDate(rs.getString("deliveryDate"));
	    		String dateProduced = formatDate(rs.getString("dateProduced"));
	    		String dateDelivered = formatDate(rs.getString("dateDelivered"));
	    		String placedDate = formatDate(rs.getString("placedDate"));
	    		int orderNbr = rs.getInt("orderNbr");
	    		result.put(id,new Object[]{id,cookieName,location, placedDate,deliveryDate,dateProduced,dateDelivered,destination,orderNbr,isBlocked});
	    	}

	    	rs.close();
	    	stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new ArrayList<Object[]>(result.values());
    }
    
    private String formatDate(String input) throws ParseException{
		if(!input.equals("")){
    		SimpleDateFormat parser = new SimpleDateFormat("yyyyMMdd");
			Date deldate = parser.parse(input);
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			input=df.format(deldate);
		}
		return input;
    }
    
    //return all ingredients in the database
    public ArrayList<Object[]> getIngridients(){
    	ArrayList<Object[]> result = new ArrayList<Object[]>();
    	Statement stmt;
		try {
			stmt = conn.createStatement();
			//fetch reservations
			ResultSet rs = stmt.executeQuery("select * from ingridient");
			
	    	while(rs.next()){
	    		String ing = rs.getString("ingridientName");
	    		int amount = rs.getInt("amountInStorage");
	    		String deliveryDate = formatDate(rs.getString("deliveryDate"));
	    		int deliveryAmount = rs.getInt("deliveryAmount");
	    		result.add(new Object[]{ing,amount,deliveryDate,deliveryAmount});
	    	}
	    	stmt.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}

    	return result;
    }
    
    //try and create a pallet returns error if it lacks ingridients
    public String tryCreatePallets(String cookieName, int nbrOfPallets){
    	Statement stmt;
    	Statement stmt2;
    	try{
    		conn.setAutoCommit(false);
    		
    		stmt = conn.createStatement();
    		stmt2 = conn.createStatement();
    		ResultSet rs = stmt.executeQuery("Select amount,r.ingridientName,amountInStorage from reciepeItem r,ingridient i where cookieName == \"" 
    				+ cookieName + "\" and r.ingridientName==i.ingridientName");
    		while(rs.next()){
    			String ingridient = rs.getString("ingridientName");
    			int amount = rs.getInt("amount");
    			int amountInStorage = rs.getInt("amountInStorage");
    			if(amount*15*10*36*nbrOfPallets>amountInStorage){
    	    		stmt.close();
    	    		stmt2.close();
    	    		rs.close();
    	    		conn.rollback();
    	    		conn.setAutoCommit(true);
    				return "Error, you tried to make " + nbrOfPallets + " pallets of " + cookieName + " which requires " +
        			amount*15*10*36*nbrOfPallets + " of " + ingridient + " but there's only " + amountInStorage + " in storage";
    			}
    			stmt2.addBatch("update ingridient set amountInStorage = amountInStorage - " + amount*15*10*36*nbrOfPallets + " where ingridientName == \"" + ingridient +"\"" );
    		}
    		stmt2.executeBatch();
    		Date date = new Date();
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
    		for(int i=0; i<nbrOfPallets; i++){
				stmt.addBatch("insert into pallet(dateProduced,isBlocked,dateDelivered,cookieName,location) values (\"" 
				+ df.format(date) + "\"," + 0  + ",\"\",\""+cookieName+"\",\"in freezer\")" );
    		}
    		stmt.executeBatch();
    		conn.commit();
    		conn.setAutoCommit(true);
    		stmt.close();
    		stmt2.close();
    		rs.close();	
    	}catch(SQLException e){
    		e.printStackTrace();
    		return "Unkown error";
    	}
    	return null;
    }
    
    //truncate order table, only used while developing
    public void clearOrders(){
    	try{
    		Statement stmt = conn.createStatement();
    		stmt.executeUpdate("delete from orderItem");
    		stmt.executeUpdate("delete from myOrder");
    		stmt.executeUpdate("delete from pallet");
    		stmt.close();
    	}catch(SQLException e){
    		
    	}
    }
    
    //map all free pallets which contains the given cookie name to an order if there is one
    public void updateOrders(String cookieName){
    	Statement stmt;
    	Statement stmt2;
    	Statement stmt3;
    	Statement stmt4;
    	try{
    		stmt = conn.createStatement();
    		stmt2 = conn.createStatement();
    		stmt3 = conn.createStatement();
    		stmt4 = conn.createStatement();
    		ResultSet rs = stmt.executeQuery("select orderNbr,nbrPallets from orderItem where cookieName == \"" + cookieName +"\"");
    		while(rs.next()){
    			int id = rs.getInt("orderNbr");
    			int nbrOfPallets = rs.getInt("nbrPallets");
				ResultSet rs2 = stmt2.executeQuery("Select palletId from pallet where cookieName == \"" + cookieName + "\" and location == \"in freezer\" "
						+ "and isBlocked==0 order by dateProduced asc");
				ResultSet rs3 = stmt3.executeQuery("Select deliveryDate from myOrder where orderNbr == " + id);
				while(rs2.next()){
					int palletId = rs2.getInt("palletId");
					if(nbrOfPallets<=0){
						break;
					}
					stmt4.executeUpdate("update orderItem set nbrPallets = nbrPallets-1 where orderNbr == " + id + " and cookieName == \"" + cookieName + "\"");
					nbrOfPallets--;
					Date date = new Date();
		    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		    		Date date2 = df.parse(rs3.getString("deliveryDate"));
		    		String message = (date2.before(date)) ? "\"delivered\",dateDelivered=\"" + df.format(date) + "\"":"\"on the way\",dateDelivered=\"\"";
					stmt4.executeUpdate("update pallet set location = " + message +" where palletId == " + palletId);
					stmt4.executeUpdate("insert into palletItem(palletId,orderNbr) values (" + palletId + "," + id +")");
				}
				rs2.close();
				rs3.close();
				stmt2.close();
				stmt3.close();
				stmt4.close();
    		}
    		rs.close();
    		stmt.close();
    	}catch(SQLException e){
    		e.printStackTrace();
    	} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    }
    
    //return all cookies stored in the database
    public String[] getCookies(){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery("Select count(cookieName) from cookie");
    		int size = rs.getInt("count(CookieName)");
    		String[] result = new String[size];
    		rs = stmt.executeQuery("select cookieName from cookie");
    		int i=0;
    		while(rs.next()){
    			result[i]=rs.getString("cookieName");
    			i++;
    		}
    		stmt.close();
    		rs.close();
    		return result;
    		
    	}catch(SQLException e){
    		
    	}
    	return null;
    }
    
    //add a cookie to the database
    public boolean addCookie(String cookieName){
    	Statement stmt;
    	try{
    		//conn.setAutoCommit(false);
    		stmt = conn.createStatement();
    		stmt.executeUpdate("insert into cookie(cookieName)"+ "values (\"" + cookieName.toLowerCase() + "\")");
	    	stmt.close();
    		return true;
    		
    	}catch (SQLException e){
			return false;
    	}
    }
    
    //check if a cookie exists
    public boolean cookieExists(String cookieName){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery("select * from cookie where cookieName== \""+cookieName.toLowerCase()+"\"");
	    	boolean result = rs.next();
    		stmt.close();
	    	rs.close();
    		return result;
    		
    	}catch (SQLException e){
			e.printStackTrace();
			return false;
    	}
    }
    
    //update existing cookie with new values
    public void updateReciepeItems(String cookieName,ArrayList<String> ingridients, ArrayList<Integer> quantities){
    	Statement stmt;
    	try{
    		conn.setAutoCommit(false);
    		stmt = conn.createStatement();
    		stmt.executeUpdate("delete from reciepeItem where cookieName == \""+cookieName+"\"");
    		for(int i=0; i<ingridients.size(); i++){
    			stmt.executeUpdate("insert into reciepeItem(cookieName,ingridientName,amount) values (\""+cookieName.toLowerCase()+"\",\""+ingridients.get(i).toLowerCase()+"\","+quantities.get(i)+")");
    		}
    		conn.commit();
			conn.setAutoCommit(true);
    		stmt.close();
    	}catch (SQLException e){
    		try {
				conn.rollback();
				conn.setAutoCommit(true);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

    	}
    }
    
    //create a reciepe item that maps from ingridients to cookiename
    public boolean addReciepeItems(String cookieName,ArrayList<String> ingridients, ArrayList<Integer> quantities){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		for(int i=0; i<ingridients.size(); i++){
    			stmt.executeUpdate("insert into reciepeItem(cookieName,ingridientName,amount) values (\""+cookieName.toLowerCase()+"\",\""+ingridients.get(i).toLowerCase()+"\","+quantities.get(i)+")");
    		}
    		stmt.close();
    	}catch (SQLException e){
			return false;
    	}
    	return true;
    }
    
    //block the given product from date to date, leave dates empty to block everything
    public void block(String cookieName,String dateFrom,String dateTo){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		String from = dateFrom.equals("") ? "":" and dateProduced >= " + dateFrom;
    		String to = dateTo.equals("") ? "":" and dateProduced <= " + dateTo;
    		stmt.executeUpdate("update pallet set isBlocked = 1, location = \"stalled\" where location!=\"delivered\" and"
    				+ " cookieName ==\"" + cookieName + "\"" + from + to);
    		stmt.close();
    	}catch (SQLException e){
			e.printStackTrace();
    	}
    }
    
    //block/unblock the pallet with given id
    public void updatePallet(int id,boolean blocked){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery("select deliveryDate from palletItem p,myOrder m where palletId == " + id + " and p.OrderNbr==m.orderNbr");
    		String location;
			if(rs.next()){
	    		Date date = new Date();
	    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
	    		Date stored = df.parse(rs.getString("deliveryDate"));
	    		location = date.before(stored) ? "\"on the way\"": "\"delivered\",dateDelivered = \"" + df.format(date) +"\"";
			}
			else{
				location ="\"in freezer\"";
			}
    		String v = blocked ? "1, location = \"stalled\"":"0, location =" + location;
    		stmt.executeUpdate("update pallet set isBlocked = " + v + " where palletId == " + id + " and location!=\"delivered\"");
    		stmt.close();
    	}catch (SQLException e){
			e.printStackTrace();
    	} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    //set new deliverydate to the given ingrident
    public boolean updateIngridient(String ingridient, String deliveryDate){
    	Statement stmt;
    	try{
    		//conn.setAutoCommit(false);
    		stmt = conn.createStatement();
    		Date date = new Date();
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
    		stmt.executeUpdate("update ingridient set deliveryDate = \""+deliveryDate+"\""
    				+ "where ingridientName == \"" + ingridient.toLowerCase() + "\"");
	    	stmt.close();
    		return true;
    		
    	}catch (SQLException e){
			e.printStackTrace();
			return false;
    	}
    }
    
    //check if the given ingridient exists in database
    public boolean ingridientExists(String ingridient){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		ResultSet rs = stmt.executeQuery("select * from ingridient where ingridientName== \""+ingridient.toLowerCase()+"\"");
	    	boolean result = rs.next();
    		stmt.close();
	    	rs.close();
    		return result;
    		
    	}catch (SQLException e){
			e.printStackTrace();
			return false;
    	}
    }
    
    //update delivery amount of the given ingridient
    public boolean updateIngridient(String ingridient, int amount){
    	Statement stmt;
    	try{
    		//conn.setAutoCommit(false);
    		stmt = conn.createStatement();
    		Date date = new Date();
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
    		stmt.executeUpdate("update ingridient set deliveryAmount = \""+amount+"\""
    				+ "where ingridientName == \"" + ingridient.toLowerCase() + "\"");
	    	stmt.close();
    		return true;
    		
    	}catch (SQLException e){
			e.printStackTrace();
			return false;
    	}
    }
    
    //add an ingridient to the database
    public String addIngridient(String ingridient, int amount, String deliveryDate, int deliveryAmount){
    	Statement stmt;
    	try{
    		//conn.setAutoCommit(false);
    		stmt = conn.createStatement();
    		stmt.executeUpdate("insert into ingridient(ingridientName,amountInStorage,deliveryDate,deliveryAmount)"+
    				"values (\"" + ingridient.toLowerCase() + "\"," + amount + ",\"" + deliveryDate +"\"," + deliveryAmount+")");
	    	stmt.close();
    		return null;
    		
    	}catch (SQLException e){
			if(e.getErrorCode() == 19){
				return "Error, this ingridient already exists! Please use the table to update values";
			}
			else{
				e.printStackTrace();
			}
			return "Error, unkown database error when trying to add row please report to an admin";
    	}
    }
    
    //check if any ingridient should've been delivered
    public void updateIngridients(){
    	Statement stmt;
    	try{
    		stmt = conn.createStatement();
    		Date date = new Date();
    		DateFormat df = new SimpleDateFormat("yyyyMMdd");
    		stmt.executeUpdate("update ingridient set amountInStorage = amountInStorage+deliveryAmount,deliveryAmount = 0"
    				+ ",deliveryDate =\"\" where deliveryDate <= \"" + df.format(date) + "\" and deliveryDate !=\"\"");
	    	stmt.close();
    	}catch (SQLException e){
			if(e.getErrorCode() == 19){
			}
			else{
				e.printStackTrace();
			}
    	}
    	
    }

    /**
     * Close the connection to the database.
     */
    public void closeConnection() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check if the connection to the database has been established
     * 
     * @return true if the connection has been established
     */
    public boolean isConnected() {
        return conn != null;
    }

    /* --- insert own code here --- */
}
