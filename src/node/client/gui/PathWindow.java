package node.client.gui;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;
import javax.swing.JButton;

import org.joda.time.DateTime;
import org.joda.time.Seconds;

import data.trainnetwork.Network;
import data.trainnetwork.Section;
import data.trainnetwork.Station;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import javax.swing.JTextPane;
import java.awt.SystemColor;

public class PathWindow extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5484130921694615760L;
	private JPanel numberOfHopstext;
	private JButton btnBookJourney;
	
	private List<Section> sections;
	private JLabel lblArrivetime;
	private JLabel lblStarttime;
	private JLabel lblCostvalue;
	private JLabel lblTraveltime;
	private JTextPane textPane;

	/**
	 * @return the btnBookJourney
	 */
	public JButton getBtnBookJourney() {
		return btnBookJourney;
	}

	/**
	 * Create the frame.
	 */
	public PathWindow(Network network, List<Section> sections, String origin, String target) {
		setResizable(false);
		this.sections = sections;
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 550, 314);
		numberOfHopstext = new JPanel();
		numberOfHopstext.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(numberOfHopstext);
		numberOfHopstext.setLayout(null);
		
		JLabel lblTheJourney = new JLabel("Book journey....");
		lblTheJourney.setFont(new Font("Helvetica", Font.BOLD, 14));
		lblTheJourney.setBounds(6, 6, 266, 16);
		numberOfHopstext.add(lblTheJourney);
		
		JLabel lblFrom = new JLabel("From:");
		lblFrom.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblFrom.setForeground(Color.GRAY);
		lblFrom.setBounds(6, 36, 61, 16);
		numberOfHopstext.add(lblFrom);
		
		JLabel lblTo = new JLabel("To:");
		lblTo.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblTo.setForeground(Color.GRAY);
		lblTo.setBounds(6, 64, 61, 16);
		numberOfHopstext.add(lblTo);
		
		JLabel lblSourcestation = new JLabel(origin);
		lblSourcestation.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblSourcestation.setBounds(60, 35, 192, 16);
		numberOfHopstext.add(lblSourcestation);
		
		JLabel lblTargetstation = new JLabel(target);
		lblTargetstation.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblTargetstation.setBounds(60, 64, 192, 16);
		numberOfHopstext.add(lblTargetstation);
		
		JLabel lblLeavesAt = new JLabel("Leaves at:");
		lblLeavesAt.setForeground(Color.GRAY);
		lblLeavesAt.setBounds(231, 35, 72, 16);
		numberOfHopstext.add(lblLeavesAt);
		
		JLabel lblArrivesAt = new JLabel("Arrives at:");
		lblArrivesAt.setForeground(Color.GRAY);
		lblArrivesAt.setBounds(231, 63, 72, 16);
		numberOfHopstext.add(lblArrivesAt);
		
		lblStarttime = new JLabel("startTime");
		lblStarttime.setBounds(300, 35, 244, 16);
		numberOfHopstext.add(lblStarttime);
		
		lblArrivetime = new JLabel("arriveTime");
		lblArrivetime.setBounds(300, 63, 244, 16);
		numberOfHopstext.add(lblArrivetime);
		
		JLabel lblTravelTime = new JLabel("Travel Time:");
		lblTravelTime.setForeground(Color.GRAY);
		lblTravelTime.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblTravelTime.setBounds(6, 109, 91, 16);
		numberOfHopstext.add(lblTravelTime);
		
		JLabel lblCost = new JLabel("Cost:");
		lblCost.setForeground(Color.GRAY);
		lblCost.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblCost.setBounds(6, 137, 61, 16);
		numberOfHopstext.add(lblCost);
		
		JLabel lblNumberOfHops = new JLabel("Number of hops:");
		lblNumberOfHops.setForeground(Color.GRAY);
		lblNumberOfHops.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblNumberOfHops.setBounds(6, 165, 114, 16);
		numberOfHopstext.add(lblNumberOfHops);
		
		lblTraveltime = new JLabel("travelTime");
		lblTraveltime.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblTraveltime.setBounds(126, 109, 225, 16);
		numberOfHopstext.add(lblTraveltime);
		
		lblCostvalue = new JLabel("costValue");
		lblCostvalue.setFont(new Font("Helvetica", Font.PLAIN, 13));
		lblCostvalue.setBounds(126, 137, 192, 16);
		numberOfHopstext.add(lblCostvalue);
		
		JButton btnClose = new JButton("Cancel & Close");
		btnClose.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				dispose();
			}
		});
		btnClose.setBounds(6, 257, 159, 29);
		numberOfHopstext.add(btnClose);
		
		btnBookJourney = new JButton("Book Journey");
		btnBookJourney.setBounds(385, 257, 159, 29);
		numberOfHopstext.add(btnBookJourney);
		
		textPane = new JTextPane();
		textPane.setBackground(SystemColor.window);
		textPane.setBounds(126, 165, 418, 80);
		numberOfHopstext.add(textPane);
		
		populateData(network);
	}

	private void populateData(Network network) {
		String datePattern = "dd MMM YYYY HH:mm:ss";
		
		DateTime start = sections.get(0).getStartTime();
		lblStarttime.setText(start.toString(datePattern));
		
		Section last = sections.get(sections.size()-1);
		DateTime end = last.getStartTime().plusSeconds((int) last.getJourneyLength());
		lblArrivetime.setText(end.toString(datePattern));
		
		Seconds seconds = Seconds.secondsBetween(start, end);
		lblTraveltime.setText("" + seconds.toStandardMinutes().getMinutes() + " minutes");
		
		int cost = 0;
		Section previous = null;
		Vector<Station> hopStations = new Vector<Station>();
		for (Section section : sections) {
			cost += section.getCost();
			if (previous != null && section.isOfSameRoute(previous)) {
				hopStations.add(network.getEdgeSource(section));
			}
			previous = section;
		}
		
		lblCostvalue.setText(cost + " EUR");
		
		if (hopStations.size() == 0) {
			textPane.setText("No hops, this is a direct train!");
		} else {
			String stationsList = "";
			for (int i = 0; i < hopStations.size(); i++) {
				stationsList += hopStations.get(i).getName();
				if (i < hopStations.size()-1) stationsList += ", ";
			}
			textPane.setText(hopStations.size() + " hops\nYou will need to change trains at the following stations: " + stationsList);
		}
	}
	
	public JButton getBookingButton() {
		return btnBookJourney;
	}
}
