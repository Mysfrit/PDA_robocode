package sample; 
 
import robocode.*; 
 
import java.awt.*; 
import java.awt.geom.Point2D; 
import java.io.*; 
import java.net.Socket; 
import java.util.Random; 
 
public class MujRobot extends AdvancedRobot { 
	int hittable = 0; 
	String option = "stay_still"; //for curve_line uncomment onHitWall & onHitRobot; otherwise comment it 
	 
	boolean direction = true; 
 
	public void run() { 
		createCSV(); 
		 
		setAdjustGunForRobotTurn(true); 
		setAdjustRadarForGunTurn(true); 
		switch (option) { 
		case "stay_still": 
			while (true) { 
				turnRadarRight(100); 
			} 
		case "straight_line": 
			while (true) {				 
				turnRadarRight(100); 
				if(direction) { 
					ahead(250); 
					direction = false; 
				} 
				else { 
					back(250); 
					direction = true; 
				}				 
			} 
		case "curve_line":						 
			while(true){ 
				turnRadarRight(100); 
				setAhead(40000); 
				setTurnLeft(90); 
				waitFor(new TurnCompleteCondition(this)); 
			} 
		case "random": 
			while(true) { 
				Random rand = new Random(); 
				turnRadarRight(100); 
				if(rand.nextInt(101) > 30) { 
					setAhead(rand.nextInt(400)); 
				} 
				else { 
					setBack(rand.nextInt(400)); 
				}				 
				if(rand.nextInt(101) > 50) { 
					setTurnRight(rand.nextInt(180)); 
					waitFor(new TurnCompleteCondition(this)); 
				} 
				else { 
					setTurnLeft(rand.nextInt(180)); 
					waitFor(new TurnCompleteCondition(this)); 
				}				 
			}			 
		default: 
			System.out.println("Wrong option has been chosen!"); 
			break; 
		} 
	} 
 
 
	public void onHitWall(HitWallEvent e) { 
		turnRight(0 - getHeading()); 
		ahead(5000); 
		turnLeft(90); 
		ahead(5000); 
		 
		turnRight(180); 
		ahead(getBattleFieldWidth()/2); 
		turnRight(90); 
		ahead(getBattleFieldHeight()/4); 
		turnRight(90); 
	} 
 
	public void onHitRobot(HitRobotEvent e) { 
		turnRight(0 - getHeading()); 
		ahead(5000); 
		turnLeft(90); 
		ahead(5000); 
		 
		turnRight(180); 
		ahead(getBattleFieldWidth()/2); 
		turnRight(90); 
		ahead(getBattleFieldHeight()/4); 
		turnRight(90); 
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
			Socket socket = new Socket("localhost", 49000); 
 
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
			System.out.println(a);
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
		
		double received = Double.parseDouble(c); //edit
		
		
		if(received < 0.25) {
			System.out.println("farba zelena");
			this.setBodyColor(Color.green);
			this.setGunColor(Color.green);			
		}
		else if (0.25 < received && received < 0.5)
		{
			System.out.println("farba zlta");
			this.setBodyColor(Color.yellow);
			this.setGunColor(Color.yellow);
		}
		else if (0.5 < received && received < 0.75)
		{
			System.out.println("farba oranzova");
			this.setBodyColor(Color.orange);
			this.setGunColor(Color.orange);
		}
		else if (received > 0.75)
		{
			System.out.println("farba cervena");
			this.setBodyColor(Color.red);
			this.setGunColor(Color.red);
		}
		
	}
}