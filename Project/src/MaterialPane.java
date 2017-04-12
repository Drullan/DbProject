import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;


public class MaterialPane extends BasicPane {

	private static final long serialVersionUID = 1;

	/**
	 * The text field where the user id is entered.
	 */
	private JTable table;
	private MaterialTable myTable;
	/**
	 * The number of the field where the user id is entered.
	 */

	/**
	 * The total number of fields in the fields array.
	 */

	/**
	 * Create the login pane.
	 * 
	 * @param db
	 *            The database object.
	 */
	public MaterialPane(Database db) {
		super(db);

	}

	/**
	 * Create the top panel, consisting of the text field.
	 * 
	 * @return The top panel.
	 */
	public JComponent createTopPanel() {
		JTextField ingridient = new JTextField(20);
		JTextField quantity = new JTextField(5);
		JTextField deliveryDate = new JTextField(5);
		JLabel inglabel = new JLabel("Ingridient");
		JLabel quantLabel = new JLabel("Delivery amount");
		JLabel dateLabel = new JLabel("Delivery date");
		JPanel panel = new JPanel();
		JPanel botPanel = new JPanel();
		panel.setLayout(new GridBagLayout());
		botPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		myTable = new MaterialTable(db);
		table = new JTable(myTable);
		table.setPreferredScrollableViewportSize(new Dimension(700, 70));
        table.setFillsViewportHeight(true);
		JScrollPane scroll = new JScrollPane(table);
		scroll.setPreferredSize(new Dimension(450,250));
		JButton addButton = new JButton("Add ingridient");
		addButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//querry shit
				try{
					if(ingridient.getText().equals("")){
						messageLabel.setText("Error, ingridient must have a value");
						return;
					}
					int quant = Integer.parseInt(quantity.getText());
					SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd");
					Date deldate = parser.parse(deliveryDate.getText());
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
					System.out.println(df.format(deldate));
					String res = db.addIngridient(ingridient.getText(), 0, df.format(deldate), quant);
					if(res==null){
						myTable.addRow(new Object[]{ingridient.getText(),0,deliveryDate.getText(),quant});
						messageLabel.setText("");
						ingridient.setText("");
						quantity.setText("");
						deliveryDate.setText("");
					}
					else{
						messageLabel.setText(res);
					}

				}catch(NumberFormatException e){
					messageLabel.setText("Error, quantity must be an int");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					messageLabel.setText("Error, wrong format of date, please use yyyy-MM-dd");
				}
				
			}
			
		});
		c.gridx=0;
		c.gridy=1;
		botPanel.add(addButton,c);
		c.gridx=1;
		botPanel.add(ingridient,c);
		c.gridx=2;
		botPanel.add(quantity,c);
		c.gridx=3;
		botPanel.add(deliveryDate,c);
		c.gridx=1;
		c.gridy=0;
		botPanel.add(inglabel,c);
		c.gridx=2;
		botPanel.add(quantLabel,c);
		c.gridx=3;
		botPanel.add(dateLabel,c);
		c.gridx = 0;
		panel.add(scroll,c);
		c.gridy = 1;
		panel.add(botPanel,c);

		return panel;
	}

	/**
	 * Create the bottom panel, consisting of the login button and the message
	 * line.
	 * 
	 * @return The bottom panel.
	 */
	public JComponent createBottomPanel() {
		JButton[] buttons = new JButton[0];
		ActionHandler actHand = new ActionHandler();
		return new ButtonAndMessagePanel(buttons, messageLabel, actHand);
	}

	/**
	 * Perform the entry actions of this pane, i.e. clear the message line.
	 */
	public void entryActions() {
		clearMessage();
		myTable.update();
	}

	/**
	 * A class which listens for button clicks.
	 */
	class ActionHandler implements ActionListener {
		/**
		 * Called when the user clicks the login button. Checks with the
		 * database if the user exists, and if so notifies the CurrentUser
		 * object.
		 * 
		 * @param e
		 *            The event object (not used).
		 */
		public void actionPerformed(ActionEvent e) {
			int action = Integer.parseInt(e.getActionCommand());
			switch(action){
			case 0:	
				System.out.println("save");
				break;
			case 1:
				System.out.println("cancel");
				break;
			default:
				break;
			}
		}
	}

}
