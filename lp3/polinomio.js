function getTerms(polynomial) {
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
  
  function getCoefficients(terms) {
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
        coefficients[i] = coefficient;
      } else {
        coefficients[i] = "0";
      }
    }
  
    return coefficients;
  }
  
  function getExponents(terms) {
    const exponents = new Array(terms.length);
    const exponentPattern = /x\^([-+]?\d+)/;
    
    for (let i = 0; i < terms.length; i++) {
      const term = terms[i];
      const exponentMatcher = term.match(exponentPattern);
      
      if (exponentMatcher) {
        exponents[i] = exponentMatcher[1];
      } else {
        exponents[i] = "0";
      }
    }
    
    return exponents;
  }

const polynomial = "1x^2";
const terms = getTerms(polynomial);
const coefficients = getCoefficients(terms);
const exponents = getExponents(terms);

console.log("Terms:", terms);
console.log("Coefficients:", coefficients);
console.log("Exponents:", exponents);
