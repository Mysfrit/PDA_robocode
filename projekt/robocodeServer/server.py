import socket


def server_program():
    # get the hostname
    host = "localhost"
    port = 5000  # initiate port no above 1024

    print("Starting Robocode Server...", host, port)

    server_socket = socket.socket()  # get instance
    # look closely. The bind() function takes tuple as argument
    server_socket.bind((host, port))  # bind host address and port together

    # configure how many client the server can listen simultaneously

    while True:
        server_socket.listen(1)
        conn, address = server_socket.accept()  # accept new connection
        print("Connection from: " + str(address))
        # receive data stream. it won't accept data packet greater than 1024 bytes
        data = conn.recv(1024).decode()
        if not data:
            # if data is not received break
            break


        command2 = "while (true) {ahead(100); turnGunRight(360);back(100);turnGunRight(360);}"
        command = "up;shot\n"cid
        print("Received data from robocode: " + str(data), "Decision:", command2)

        conn.send(command.encode())  # send data to the client

    conn.close()  # close the connection


if __name__ == '__main__':
    server_program()
