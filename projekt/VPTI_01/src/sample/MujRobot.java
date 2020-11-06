package sample;

import robocode.HitByBulletEvent;
import robocode.Robot;
import robocode.ScannedRobotEvent;
import robocode.StatusEvent;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class MujRobot extends Robot {
	
	/**
	 * MyFirstRobot's run method - Seesaw
	 */
	public void run() {

		while (true) {
			ahead(100); // Move ahead 100
			turnGunRight(360); // Spin gun around
			back(100); // Move back 100
			turnGunRight(360); // Spin gun around
		}
	}

	/**
	 * Fire when we see a robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		fire(1);
	}

	/**
	 * We were hit!  Turn perpendicular to the bullet,
	 * so our seesaw might avoid a future shot.
	 */
	public void onHitByBullet(HitByBulletEvent e) {
		turnLeft(90 - e.getBearing());
	}

	@Override
	public void onStatus(StatusEvent e) {
		String sentence;
		String responseFromIAServer;
		String dataToSendFromRobocode2 = "100;100;image_data";
		String dataToSendFromRobocode = "hi";
		System.out.println("Sending data: "+dataToSendFromRobocode);
		InputStream inStream = new ByteArrayInputStream(dataToSendFromRobocode.getBytes(StandardCharsets.UTF_8));
		BufferedReader inFromUser = new BufferedReader(new InputStreamReader(inStream));
		Socket clientSocket = null;
		try {
			clientSocket = new Socket("localhost", 5000);
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			sentence = inFromUser.readLine();
			outToServer.writeBytes(sentence + 'n');
			responseFromIAServer = inFromServer.readLine();
			System.out.println("Action to do from server: " + responseFromIAServer);
			clientSocket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}


	}
}