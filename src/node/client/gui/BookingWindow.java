package node.client.gui;

import java.awt.Component;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

import org.joda.time.DateTime;

import data.trainnetwork.Section;
import data.trainnetwork.Section.ScoreMode;
import data.trainnetwork.Station;

import node.client.Client;

import java.awt.Color;
import java.util.Set;

/**
 * The interface to book a journey
 * @author Balazs Pete
 *
 */
public class BookingWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1431077072857854129L;
	private JPanel contentPane;
	private JRadioButton[] routeOptions = new JRadioButton[3];
	private JLabel lblLoading;
	private JComboBox destinationBox;
	private JComboBox originBox;
	private JComboBox dateBox;
	private JComboBox timeBox;
	
	private static final String
		fastest_journey = "Fastest journey",
		cheapest_journey = "Cheapest journey",
		least_hops = "Least number of hops";
	private JButton btnSearchButton;
	private JButton btnR;

	/**
	 * @return the btnR
	 */
	public JButton getBtnR() {
		return btnR;
	}

	/**
	 * Create the frame.
	 */
	public BookingWindow(Set<Station> stations) {
		setVisible(false);
		
		String[] dates = getDates();
		String[] times = getTimes();
		
		setBounds(100, 100, 550, 250);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblBookANew = new JLabel("Book a New Journey");
		lblBookANew.setFont(new Font("Helvetica", Font.BOLD, 14));
		lblBookANew.setBounds(6, 6, 141, 16);
		contentPane.add(lblBookANew);
		
		originBox = new JComboBox();
		originBox.setEditable(true);
		originBox.setBounds(6, 68, 250, 27);
		contentPane.add(originBox);
		addStationsToList(originBox, stations);
		
		JLabel lblToBookA = new JLabel("To book a journey, select the origin, destination, and the desired depature time...");
		lblToBookA.setFont(new Font("Helvetica", Font.PLAIN, 11));
		lblToBookA.setBounds(6, 24, 413, 16);
		contentPane.add(lblToBookA);
		
		JLabel lblOrigin = new JLabel("Origin");
		lblOrigin.setBounds(16, 52, 61, 16);
		contentPane.add(lblOrigin);
		
		destinationBox = new JComboBox();
		destinationBox.setEditable(true);
		destinationBox.setBounds(294, 70, 250, 27);
		contentPane.add(destinationBox);
		addStationsToList(destinationBox, stations);
		
		JLabel lblDestination = new JLabel("Destination");
		lblDestination.setBounds(303, 52, 87, 16);
		contentPane.add(lblDestination);
		
		dateBox = new JComboBox();
		dateBox.setBounds(54, 107, 203, 27);
		contentPane.add(dateBox);
		for (String date : dates) {
			dateBox.addItem(date);
		}
		
		JLabel lblDate = new JLabel("Date");
		lblDate.setBounds(16, 111, 61, 16);
		contentPane.add(lblDate);
		
		JLabel lblTime = new JLabel("Time");
		lblTime.setBounds(303, 111, 61, 16);
		contentPane.add(lblTime);
		
		timeBox = new JComboBox();
		timeBox.setBounds(341, 107, 203, 27);
		contentPane.add(timeBox);
		for (String time : times) {
			timeBox.addItem(time);
		}
		
		JLabel lblSearchFor = new JLabel("Search for...");
		lblSearchFor.setBounds(16, 146, 113, 16);
		contentPane.add(lblSearchFor);
		
		routeOptions[0] = new JRadioButton(fastest_journey);
		routeOptions[0].setSelected(true);
		Section.scoreMode = ScoreMode.TravelTime;
		routeOptions[0].setBounds(6, 162, 141, 23);
		contentPane.add(routeOptions[0]);
		
		routeOptions[1] = new JRadioButton(cheapest_journey);
		routeOptions[1].setBounds(176, 162, 141, 23);
		contentPane.add(routeOptions[1]);
		
		routeOptions[2] = new JRadioButton(least_hops);
		routeOptions[2].setBounds(355, 162, 189, 23);
		contentPane.add(routeOptions[2]);
		
		btnSearchButton = new JButton("Search");
		btnSearchButton.setBounds(441, 6, 103, 29);
		contentPane.add(btnSearchButton);
		
		lblLoading = new JLabel("");
		lblLoading.setForeground(Color.DARK_GRAY);
		lblLoading.setFont(new Font("Helvetica", Font.BOLD | Font.ITALIC, 13));
		lblLoading.setBounds(16, 206, 528, 16);
		lblLoading.setVisible(false);
		contentPane.add(lblLoading);
		
		btnR = new JButton("R");
		btnR.setBounds(403, 6, 37, 29);
		contentPane.add(btnR);
		
		MouseAdapter optionsAdapter = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				disableOthers(e.getComponent());
				applyRouteOption((JRadioButton) e.getComponent());
			}
		};
		
		routeOptions[0].addMouseListener(optionsAdapter);
		routeOptions[1].addMouseListener(optionsAdapter);
		routeOptions[2].addMouseListener(optionsAdapter);
	}
	
	public void printStatus(String message) {
		lblLoading.setText(message);
		lblLoading.setVisible(true);
	}
	
	public JButton getSearchButton() {
		return btnSearchButton;
	}
	
	private void addStationsToList(JComboBox box, Set<Station> stations) {
		for (Station station : stations) {
			box.addItem(station.getID() + "=" + station.getName());
		}
	}
	
	private void disableOthers(Component button) {
		for (JRadioButton _button : routeOptions) {
			if (!_button.equals(button)) {
				_button.setSelected(false);
			}
		}
	}
	
	private String[] getDates() {
		DateTime date = DateTime.now();
		String[] dates = new String[Client.MAX_DATES];
		for (int i = 0; i < Client.MAX_DATES; i++) {
			dates[i] = date.plusDays(i).toString("YYYY-MM-dd");
		}
		
		return dates;
	}
	
	private String[] getTimes() {
		String[] times = new String[96];
		for (int i = 0; i < 24; i++) {
			for (int j = 0; j < 4; j++) {
				times[i*4 + j] = (i<10 ? "0" : "") + i + ":" + (j == 0 ? "00" : (j*15)) + ":00" ;
			}
		}
		
		return times;
	}
	
	public String getOriginID() {
		if (originBox.getSelectedItem() == null) return null;
		return originBox.getSelectedItem().toString().split("=")[0];
	}
	
	public String getDestinationID() {
		if (destinationBox.getSelectedItem() == null) return null;
		return destinationBox.getSelectedItem().toString().split("=")[0];
	}
	
	public DateTime getStartDateTime() {
		String out = dateBox.getSelectedItem().toString() + "T"+
				timeBox.getSelectedItem().toString();
		
		return DateTime.parse(out);
	}
	
	private void applyRouteOption(JRadioButton option) {
		String current = option.getText().intern();
		
		ScoreMode mode = null;
		// Can do reference based matching as string is internalised
		if (current == fastest_journey) {
			mode = ScoreMode.TravelTime;
		} else if (current == cheapest_journey) {
			mode = ScoreMode.Cost;
		} else if (current == least_hops) {
			mode = ScoreMode.NumberOfHops;
		}
		
		if (mode != null) {
			Section.scoreMode = mode;
			System.out.println("BookingWindow: Switched section scoringmode to " + mode);
		}
	}
}
