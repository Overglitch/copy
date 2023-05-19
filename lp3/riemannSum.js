class RiemannSum {
    constructor(polynomial, a, b, n) {
        this.terms = this.parsePolynomial(polynomial);
        this.coefficients = this.getCoefficients(this.terms);
        this.exponents = this.getExponents(this.terms);
        this.a = a;
        this.b = b;
        this.n = n;
    }

    parsePolynomial(polynomial) {
        const termRegex = /(?<!\^)(?=[+-](?!-))/;
        let terms = polynomial.split(termRegex);
        
        for (let i = 0; i < terms.length; i++) {
            terms[i] = terms[i].trim();
            
            if (terms[i].endsWith("^")) {
                terms[i] += terms[i + 1];
                terms[i + 1] = "";
            }
        }
        
        return terms.filter(term => term !== "");
    }

    getCoefficients(terms) {
        const coefficients = new Array(terms.length);
        const coefficientPattern = /([-+]?\d+)/;
        
        for (let i = 0; i < terms.length; i++) {
            const term = terms[i];
            const coefficientMatcher = term.match(coefficientPattern);
        
            if (coefficientMatcher) {
                let coefficient;
                if (term.includes("x")) {
                    coefficient = coefficientMatcher[1] || (term.startsWith("-") ? "-1" : "1");
                } else {
                    coefficient = coefficientMatcher[1];
                }
                coefficients[i] = parseFloat(coefficient);
                } else {
                    coefficients[i] = 0.0;
            }
        }
        
        return coefficients;
    }

    getExponents(terms) {
        const exponents = new Array(terms.length);
        const exponentPattern = /x\^([-+]?\d+)/;
        
        for (let i = 0; i < terms.length; i++) {
            const term = terms[i];
            const exponentMatcher = term.match(exponentPattern);
            
            if (exponentMatcher) {
                exponents[i] = parseInt(exponentMatcher[1]);
            } else {
                exponents[i] = 0;
            }
        }
        
        return exponents;
    }
}

class RiemannSumThread {
    constructor(terms, coefficients, exponents, a, b, numIntervals, thread_index) {
        this.terms = terms;
        this.coefficients = coefficients;
        this.exponents = exponents;
        this.a = a;
        this.b = b;
        this.numIntervals = numIntervals;
        this.thread_index = thread_index;
    }

    start() {
        this.calculatePartialSum();
        console.log(`Hilo (${this.thread_index + 1}) | a=${this.a} - b=${this.b} - n=${this.numIntervals} - area=${this.sum}`);
    }

    join() {
        return new Promise((resolve, reject) => {
            const interval = setInterval(() => {
                if (this.isPartialSumReady()) {
                    clearInterval(interval);
                    resolve();
                }
            }, 100);
        });
    }

    calculatePartialSum() {
        let sum = 0.0;
        const delta_x = (this.b - this.a) / this.numIntervals;

        for (let i = 0; i < this.numIntervals; i++) {
            const x = this.a + (i + 0.5) * delta_x;
            const y = this.evaluatePolynomial(x);
            sum += y * delta_x;
        }
        this.sum = sum;
    }

    evaluatePolynomial(x) {
        let result = 0.0;
        for (let i = 0; i < this.terms.length; i++) {
            const coefficient = this.coefficients[i];
            const exponent = this.exponents[i];
            result += coefficient * Math.pow(x, exponent);
        }
        return result;
    }


    isPartialSumReady() {
        return typeof this.sum !== 'undefined';
    }
}

module.exports = {
    RiemannSum,
    RiemannSumThread,
};
