import socket


def server_program():
    # get the hostname
    host = "localhost"
    port = 50000  # initiate port no above 1024

    print("Starting Robocode Server...", host, port)

    server_socket = socket.socket()  # get instance
    # look closely. The bind() function takes tuple as argument
    server_socket.bind((host, port))  # bind host address and port together

    # configure how many client the server can listen simultaneously
    server_socket.listen(5)

    # print("Connection from: " + str(address))
    while True:
        conn, address = server_socket.accept()  # accept new connection
        # receive data stream. it won't accept data packet greater than 1024 bytes
        data = conn.recv(1024).decode()
        if not data:
            # if data is not received break
            break

        stringToClient = "FUCK YOU DAVE!!!!"

        print("Received data from robocode: " + str(data), "\nSending to client:", stringToClient)

        conn.send(stringToClient.encode("UTF-8"))  # send data to the client
        conn.close()


if __name__ == '__main__':
    server_program()
