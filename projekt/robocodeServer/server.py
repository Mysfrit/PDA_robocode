import socket
import os
import numpy as np
from os import path

def appendLinesToFile(lines):
    with open("data.csv", 'a') as file:
        file.write(str.join('\n', lines) + "\n")

def loadLearnedModel():
    from keras.models import load_model
    return load_model('T1_09567_SavedModel_noro.h5')

def listenForData():
    # get the hostname
    host = "localhost"
    port = 49000  # initiate port no above 1024

    print("Starting Robocode NN Server in listening mode...", host, port)

    server_socket = socket.socket()  # get instance
    # look closely. The bind() function takes tuple as argument
    server_socket.bind((host, port))  # bind host address and port together

    # configure how many clients the server can listen simultaneously
    server_socket.listen(5)

    # prepare lines to write
    lines = list()

    # add csv header
    if not path.exists("data.csv"):
        lines.append("ourX,ourY,ourRadarHeading,distanceToTarget,enemyX,enemyY,enemyHeading,enemyEnergy,hittable")

    i = 0
    while True:
        i += 1

        # accept new connection
        conn, address = server_socket.accept()
        # receive data stream. it won't accept data packet greater than 1024 bytes
        data = conn.recv(1024)
        if not data:
            # if data is not received - continue listening
            continue
        
        # decode data and exclude first two characters (some random bytes)
        decodedLine = data.decode("utf-8", errors="replace")[2:]

        # add to lines
        lines.append(decodedLine)

        # write to file every 100 new entries
        if i % 100 == 0:
            appendLinesToFile(lines)
            lines.clear()

def listenForPredicting():
    # get the hostname
    host = "localhost"
    port = 49000  # initiate port no above 1024

    print("Starting Robocode NN Server in predicting mode...", host, port)

    server_socket = socket.socket()  # get instance
    # the bind() function takes tuple as argument
    server_socket.bind((host, port))  # bind host address and port together

    # configure how many client the server can listen simultaneously
    server_socket.listen(5)

    ourModel = loadLearnedModel()

    while True:
        # accept new connection
        conn, address = server_socket.accept()
        # receive data stream. it won't accept data packet greater than 1024 bytes
        data = conn.recv(1024)

        if not data:
            # if data is not received - continue listening
            continue

        # decode data and exclude first two characters (some random bytes)
        decodedLine = data.decode("utf-8", errors="replace")[2:]
        print(decodedLine)

        decodedLine = np.array([float(x) for x in decodedLine.split(",")]).reshape(1, 8)

        outcome = ourModel.predict(decodedLine)[0:1][0][0:1][0]
        print(outcome)
        conn.send(str(outcome).encode("UTF-8"))  # send outcome to the client
        conn.close()

if __name__ == '__main__':
    # change current working directory to the folder which contains this file
    os.chdir(os.path.dirname(os.path.abspath(__file__)))

    # listening mode
    #listenForData()

    # predicting mode
    listenForPredicting()

