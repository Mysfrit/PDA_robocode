import socket
import os
from os import path

def appendLinesToFile(lines):
    with open("data.csv", 'a') as file:
        file.write(str.join('\n', lines) + "\n")

def listenForData():
    # get the hostname
    host = "localhost"
    port = 49000  # initiate port no above 1024

    print("Starting Robocode Server...", host, port)

    server_socket = socket.socket()  # get instance
    # look closely. The bind() function takes tuple as argument
    server_socket.bind((host, port))  # bind host address and port together

    # configure how many clients the server can listen simultaneously
    server_socket.listen(5)

    # prepare lines to write
    lines = list()

    # add csv header
    if not path.exists("data.csv"):
        lines.append("ourX,ourY,ourHeading,ourRadarHeading,distanceToTarget,ourVelocity,ourEnergy,enemyX,enemyY,enemyHeading,enemyVelocity,enemyEnergy,hit")

    i = 0
    while True:
        i += 1

        # accept new connection
        conn, address = server_socket.accept()
        # receive data stream. it won't accept data packet greater than 1024 bytes
        data = conn.recv(1024)
        if not data:
            # if data is not received - continue
            continue
        
        # decode data and exclude first two characters (some random bytes)
        decodedData = data.decode("utf-8", errors="replace")[2:]

        # add to lines
        lines.append(decodedData)

        # write to file every 100 new entries
        if i % 100 == 0:
            appendLinesToFile(lines)
            lines.clear()

if __name__ == '__main__':
    # change current working directory to the folder which contains this file
    os.chdir(os.path.dirname(os.path.abspath(__file__)))
    listenForData()
