package node.client.gui;

import javax.swing.JFrame;
import java.awt.FlowLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.Font;

/**
 * The main interface of the client
 * @author Balazs Pete
 *
 */
public class MainWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5263052532689178159L;
	private JButton btnBookTrainJourney;
	/**
	 * @return the btnBookTrainJourney
	 */
	public JButton getBtnBookTrainJourney() {
		return btnBookTrainJourney;
	}

	/**
	 * @return the btnCancelABooking
	 */
	public JButton getBtnCancelABooking() {
		return btnCancelABooking;
	}

	private JButton btnCancelABooking;

	/**
	 * Create the application.
	 */
	public MainWindow() {
		setResizable(false);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		setBounds(100, 100, 250, 150);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JLabel lblMultihopTrainJourney = new JLabel("Multi-hop train journey");
		lblMultihopTrainJourney.setFont(new Font("Helvetica", Font.BOLD, 14));
		getContentPane().add(lblMultihopTrainJourney);
		
		JLabel lblBookingSystem = new JLabel("Booking system");
		lblBookingSystem.setFont(new Font("Helvetica", Font.BOLD, 14));
		getContentPane().add(lblBookingSystem);
		
		btnBookTrainJourney = new JButton("Book a Train Journey");
		btnBookTrainJourney.setToolTipText("Book a new journey");
		btnBookTrainJourney.setFont(new Font("Helvetica", Font.PLAIN, 13));
		getContentPane().add(btnBookTrainJourney);
		
		btnCancelABooking = new JButton("Manage Booked Journeys");
		btnCancelABooking.setToolTipText("View and manage booked journeys");
		btnCancelABooking.setFont(new Font("Helvetica", Font.PLAIN, 13));
		getContentPane().add(btnCancelABooking);
	}

}
