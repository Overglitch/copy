const { TCPClient } = require('./tcpClient');
const { RiemannSum, RiemannSumThread } = require('./riemannSum');

class Client {
    constructor() {
        this.sums = new Array(40);
        this.tcpClient = null;
    }

    start() {
        this.tcpClient = new TCPClient('127.0.0.1', this.receiveFromClient.bind(this));
        this.tcpClient.run();
    }

    receiveFromClient(input) {
        console.log('Mensaje recibido:', input);
        if (input.trim().includes('enviar')) {
            const parts = input.split(' ');

            const polynomialExpression = parts[1];
            const a = parseFloat(parts[2]);
            const b = parseFloat(parts[3]);
            const n = parseInt(parts[4]);

            console.log(`Variables: a=${a} b=${b} n=${n}\n`);
            this.process(polynomialExpression, a, b, n);
        }
    }

    sendToClient(message) {
        if (this.tcpClient !== null) {
            this.tcpClient.sendMessage(message);
        }
    }

    process(polynomial, a, b, n) {
        const riemannSum = new RiemannSum(polynomial, a, b, n);
        const T = 6;
        const n_i = Math.floor(n / T);
        const delta_x_i =  (b - a) / T;
        const threads = new Array(T);

        for (let i = 0; i < T; i++) {
            const start = a + i * delta_x_i;
            const end = i === T - 1 ? b : a + (i + 1) * delta_x_i;
            const numIntervals = i === T - 1 ? n - n_i * (T - 1) : n_i;

            threads[i] = new RiemannSumThread(
                riemannSum.terms,
                riemannSum.coefficients,
                riemannSum.exponents,
                start,
                end,
                numIntervals,
                i
            );
            threads[i].start();
        }

        Promise.all(threads.map((thread) => thread.join()))
            .then(() => {
                let partialSum = 0.0;
                for (let i = 0; i < T; i++) {
                    partialSum += threads[i].sum;
                }
                console.log('\nResultado del cliente:', partialSum);
                this.sendToClient(`Resultado ${partialSum}`);
            })
            .catch((error) => {
                console.log('Error:', error);
            });
    }
}

const client = new Client();
client.start();
