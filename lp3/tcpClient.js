const net = require('net');

class TcpClient {
    constructor(ipAddress, messageListener) {
        this.serverIP = ipAddress;
        this.SERVER_PORT = 4444;
        this.messageListener = messageListener;
        this.out = null;
    }

    sendMessage(message) {
        if (this.out !== null && !this.out.destroyed) {
            this.out.write(message + '\n');
        }
    }

    run() {
        const serverAddress = this.serverIP;
        const serverPort = this.SERVER_PORT;

        const client = net.createConnection(serverPort, serverAddress, () => {
            this.out = client;
        });

        client.on('data', (data) => {
            const serverMessage = data.toString().trim();
            if (serverMessage !== '') {
                this.messageListener(serverMessage);
            }
        });

        client.on('end', () => {
            console.log('Desconectado del servidor');
        });

        client.on('error', (error) => {
            console.error('Error de conexión:', error);
        });
    }
}

module.exports = {
    TCPClient: TcpClient,
};

