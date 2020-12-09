package sample;

import robocode.*;
import robocode.ScannedRobotEvent;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.*;
import java.net.Socket;

public class MujRobot extends AdvancedRobot {
	private int hit = 0;
	int hittable = 0;

	public void run() {
		createCSV();
		while (true) {
			turnLeft(360);
		}
	}



	public void onScannedRobot(ScannedRobotEvent e) {
		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);

		double enemyX = getX() + Math.sin(angle) * e.getDistance();
		double enemyY = getY() + Math.cos(angle) * e.getDistance();



		var enemyDegreeFromUs = absoluteBearing(getX(),getY(), enemyX,enemyY);

		if((e.getHeading()+150) % 360 <= enemyDegreeFromUs && enemyDegreeFromUs <= (e.getHeading() + 210) % 360){
			hittable = 1;
		}
		String data = this.getX() + "," + this.getY() + "," + this.getRadarHeading() + "," + e.getDistance() + "," + enemyX + "," + enemyY + ","
				+ e.getHeading() + "," + e.getEnergy() + "," + hittable;



		sendToPython(data.substring(0, data.length() - 2));

		try {
			writeToFile(data);
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		hittable = 0;
	}

	private void createCSV() {
		File csv = new File("test.csv");
		if(!csv.exists()) {
			try (PrintWriter writer = new PrintWriter(new File("test.csv"))) {
				StringBuilder sb = new StringBuilder();
				sb.append("ourX,ourY,ourRadarHeading,distanceToTarget,enemyX,enemyY,enemyHeading,enemyEnergy,hittable\n");
				writer.append(sb.toString());
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
			}

		}
	}

	private void writeToFile(String data) throws IOException {

		File csv = new File("test.csv");


		if (csv.exists()) {

			FileWriter fr = new FileWriter(csv, true);
			BufferedWriter br = new BufferedWriter(fr);
			PrintWriter pr = new PrintWriter(br);
			pr.println(data);
			pr.close();
			br.close();
			fr.close();
		}

	}

	private double absoluteBearing(double x1, double y1, double x2, double y2) {
		double xo = x2 - x1;
		double yo = y2 - y1;

		double hyp = Point2D.distance(x1, y1, x2, y2);

		double arcSin = Math.toDegrees(Math.asin(xo / hyp));
		double bearing = 0;

		if (xo > 0 && yo > 0) { // both pos: lower-Left
			bearing = arcSin;
		} else if (xo < 0 && yo > 0) { // x neg, y pos: lower-right
			bearing = 360 + arcSin; // arcsin is negative here, actuall 360 - ang
		} else if (xo > 0 && yo < 0) { // x pos, y neg: upper-left
			bearing = 180 - arcSin;
		} else if (xo < 0 && yo < 0) { // both neg: upper-right
			bearing = 180 - arcSin; // arcsin is negative here, actually 180 + ang
		}

		return bearing;
	}

	private void sendToPython(String line){
		try {
			Socket socket = new Socket("localhost", 50000);

			// get the output stream from the socket.
			OutputStream outputStream = socket.getOutputStream();

			// create a data output stream from the output stream so we can send data through it
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

			// write the message we want to send
			dataOutputStream.writeUTF(line);

			// send the message
			dataOutputStream.flush();



			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));

			var a  = inFromServer.readLine();
			if(!a.isEmpty()){
				changeColor(a);
			}

			socket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			System.out.println("I don't feel so good, Mr. Stark");
		}
	}

	private void changeColor(String c) {
		this.setBodyColor(new Color(0,0,(int)(Double.parseDouble(c)*255)));
	}
}