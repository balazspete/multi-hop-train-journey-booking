package node.client.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.JButton;

import data.system.Ticket;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

public class TicketsWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2898058837618835760L;
	private JPanel contentPane;
	
	private Vector<JButton> cancelButtons;

	/**
	 * Create the frame.
	 */
	public TicketsWindow(List<Ticket> tickets) {
		setResizable(false);
		cancelButtons = new Vector<JButton>();
		
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setPreferredSize(new Dimension(240, 500));
		scrollPane.setViewportView(panel);
		
		for (Ticket ticket : tickets) {
			Box horizontalBox= getTicketBox(ticket);
			panel.add(horizontalBox);
		}
		
		JLabel lblBookedJourneys = new JLabel("Booked journeys...");
		lblBookedJourneys.setFont(new Font("Helvetica", Font.BOLD, 14));
		contentPane.add(lblBookedJourneys, BorderLayout.NORTH);
	}
	
	/**
	 * Get the cancel buttons
	 * @return teh cancel buttons
	 */
	public Vector<JButton> getCancelButtons() {
		return cancelButtons;
	}

	private Box getTicketBox(Ticket ticket) {
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setSize(430, horizontalBox.getHeight());
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalBox.add(horizontalStrut);
		
		JLabel lblNewLabel = new JLabel(ticket.getSource() + "-" + ticket.getTarget() + "@" + ticket.getStartTime().toString("dd MMM YYYY HH:mm"));
		horizontalBox.add(lblNewLabel);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox.add(horizontalGlue);
		
		JButton btnCancelTicket = new JButton("Cancel ticket");
		horizontalBox.add(btnCancelTicket);
		cancelButtons.add(btnCancelTicket);
		
		return horizontalBox;
	}
}
