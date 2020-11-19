package sample;

import robocode.*;
import robocode.ScannedRobotEvent;

import java.io.*;
import java.net.Socket;

public class MujRobot extends Robot {
	private int hit = 0;
	
	public void run() {
		while (true) {
			turnLeft(360);
		}
	}
	
	public void onHitByBullet(HitByBulletEvent e) {
		this.hit = 1;
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		double angle = Math.toRadians((getHeading() + e.getBearing()) % 360);

		double enemyX = getX() + Math.sin(angle) * e.getDistance();
		double enemyY = getY() + Math.cos(angle) * e.getDistance();

		String line = this.getX() + "," + this.getY() + "," + this.getHeading() + "," + this.getRadarHeading() + "," +
            e.getDistance() + "," + this.getVelocity() + "," + this.getEnergy()  + "," + enemyX + "," + enemyY + "," + e.getHeading() + "," +
            e.getVelocity() + "," + e.getEnergy() + "," + hit;

		sendToPython(line);
	}

	private void sendToPython(String line){
		try {
			Socket socket = new Socket("localhost", 49000);

			// get the output stream from the socket.
			OutputStream outputStream = socket.getOutputStream();

			// create a data output stream from the output stream so we can send data through it
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);

			// write the message we want to send
			dataOutputStream.writeUTF(line);

            // send the message
			dataOutputStream.flush();
			socket.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
			System.out.println("I don't feel so good, Mr. Stark");
		}
	}
}