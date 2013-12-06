package util;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Component;
import javax.swing.Box;
import java.util.Collection;

import org.joda.time.DateTime;
import java.awt.Color;

/**
 * A visual {@link Collection} inspector.
 * Displays the string representation of the input collection, refreshes every 2 seconds.
 * @author Balazs Pete
 *
 */
@SuppressWarnings("rawtypes")
public class CollectionInspector extends Thread {

	private JPanel contentPane;
	private JScrollPane scrollPane;
	private Collection collection;

	/**
	 * Create the frame.
	 */
	public CollectionInspector(Collection collection, String title) {
		this.collection = collection;
		JFrame frame = new JFrame();
		
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setBounds(100, 100, 450, 300);
		frame.setTitle(title);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		frame.setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		
		JLabel lblSize = new JLabel("Size: " + collection.size());
		panel.add(lblSize);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut);
		
		JLabel lblLastupdated = new JLabel("Last updated: " + DateTime.now().toString("HH:mm:ss"));
		panel.add(lblLastupdated);
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		frame.setVisible(true);
		update();
	}

	private void update() {
		JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS)); 
		panel_1.setSize(scrollPane.getWidth(), 1000);
		scrollPane.setViewportView(panel_1);
		
		
		int i = 0;
		for (Object entry : collection) {
			JPanel horizontalBox = new JPanel();
			panel_1.add(horizontalBox);
			
			JLabel lblIndex = new JLabel(i++ + ":");
			lblIndex.setForeground(Color.GRAY);
			horizontalBox.add(lblIndex);
			
			JLabel lblTostring = new JLabel(entry.toString());
			horizontalBox.add(lblTostring);
		}
	}
	
	public void run() {
		try {
			sleep(2000);
		} catch (InterruptedException e) {
			System.err.println("Failed to sleep: " + e.getMessage());
		}
		update();
	}
}
