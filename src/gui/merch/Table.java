package gui.merch;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

/**
	 * Class represents the JTable 
	 * 
	 */
	public class Table extends JTable
	{
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor used to initialise the table and set it's table model to the one received
		 * @param model
		 */
		public Table(DefaultTableModel model)
		{
			super();
			this.setModel(model);
		}
		
		@Override
		public boolean isCellEditable(int row, int column)
		{
			return false;
		}

	}