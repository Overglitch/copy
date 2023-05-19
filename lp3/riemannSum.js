class RiemannSum {
  constructor(polynomial, a, b, n) {
    this.terms = this.parsePolynomial(polynomial);
    this.coefficients = this.getCoefficients(this.terms);
    this.exponents = this.getExponents(this.terms);
    this.a = a;
    this.b = b;
    this.n = n;
  }

  parsePolynomial(polynomialExpression) {
    const terms = [];
    const pattern = /(-?\d+(?:\.\d+)?)\*?x\^(-?\d+)/g;
    let match;

    while ((match = pattern.exec(polynomialExpression)) !== null) {
      const coefficient = parseFloat(match[1]);
      const exponent = parseInt(match[2]);
      terms.push([coefficient, exponent]);
    }

    return terms;
  }

  getCoefficients(terms) {
    return terms.map((term) => term[0]);
  }

  getExponents(terms) {
    return terms.map((term) => term[1]);
  }
}
class RiemannSumThread {
  constructor(
    terms,
    coefficients,
    exponents,
    a,
    b,
    numIntervals,
    thread_index
  ) {
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
    console.log(
      `Hilo (${this.thread_index + 1}) | a=${this.a} - b=${this.b} - n=${
        this.numIntervals
      } - area=${this.sum}`
    );
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
    return typeof this.sum !== "undefined";
  }
}

module.exports = {
  RiemannSum,
  RiemannSumThread,
};
