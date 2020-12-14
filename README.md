
# Setting enviroment
Eclipse => Run configurations => Java Application => Tab: Arguments => Field: VM Arguments => add "-DNOSECURITY=true" 


IntelliJ => Edit Configuration => VM options => add "-DNOSECURITY=true" ; Main class "tanks.RobocodeRunner"

Tested compatibility => JavaSE-9, JavaSE-14

PyCharm => Python 3.8, install all required packages
# Editing configuration
Python => NeuralNetwork.py => line 52 => name of the training file, after running and training => savedModel.h5

To real time compute => server.py => define which model to load => listening on port and waiting for robocode

If you dont want to generate .csv file, comment createCSV(); on line 18 in /projekt/VTPI_01/src/sample/MujRobot.java

First the server must run before running robocode, otherwise the robot will explode.
Check ports to ensure that they are listening on the same port.
