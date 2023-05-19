package sockets.caso1.lp2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RiemannSum {
    private final double a;
    private final double b;
    private final int n;
    public final String[] terms;
    public final String[] coefficients;
    public final String[] exponents;

    public RiemannSum(String polynomialExpression, double a, double b, int n) {
        this.a = a;
        this.b = b;
        this.n = n;
        terms = getTerms(polynomialExpression);
        coefficients = getCoefficients(terms);
        exponents = getExponents(terms);
    }

    public RiemannSum(String[] terms, String[] coefficients, String[] exponents, double a, double b, int n) {
        this.terms = terms;
        this.coefficients = coefficients;
        this.exponents = exponents;
        this.a = a;
        this.b = b;
        this.n = n;
    }

    private static String[] getTerms(String polynomial) {
        String termRegex = "((?<!\\^)(?=[+-](?!-)))";
        String[] terms = polynomial.split(termRegex);
        for (int i = 0; i < terms.length; i++) {
            terms[i] = terms[i].trim();
            if (terms[i].endsWith("^")) {
                terms[i] += terms[i + 1];
                terms[i + 1] = "";
            }
        }
        return terms;
    }

    public static String[] getCoefficients(String[] terms) {
        String[] coefficients = new String[terms.length];
        Pattern coefficientPattern = Pattern.compile("([-+]?)(\\d+)");

        for (int i = 0; i < terms.length; i++) {
            String term = terms[i];
            Matcher coefficientMatcher = coefficientPattern.matcher(term);

            if (coefficientMatcher.find()) {
                String sign = coefficientMatcher.group(1);
                String coefficient = coefficientMatcher.group(2);
                coefficients[i] = (sign.equals("-") ? "-" : "") + coefficient;
            } else {
                coefficients[i] = "0";
            }
        }

        return coefficients;
    }

    public static String[] getExponents(String[] terms) {
        String[] exponents = new String[terms.length];
        Pattern exponentPattern = Pattern.compile("x\\^([-+]?\\d+)");

        for (int i = 0; i < terms.length; i++) {
            String term = terms[i];
            Matcher exponentMatcher = exponentPattern.matcher(term);

            if (exponentMatcher.find()) {
                exponents[i] = exponentMatcher.group(1);
            } else {
                exponents[i] = "0";
            }
        }
        return exponents;
    }

    private double evaluatePolynomial(double x) {
        double result = 0.0;
        for (int i = 0; i < terms.length; i++) {
            String coefficientStr = coefficients[i];
            String exponentStr = exponents[i];

            double coefficient = coefficientStr.isEmpty() ? 0.0 : Double.parseDouble(coefficientStr);
            int exponent = exponentStr.isEmpty() ? 1 : Integer.parseInt(exponentStr);

            result += coefficient * Math.pow(x, exponent);
        }
        return result;
    }

    public double calculate() {
        double delta = (b - a) / (double) n;
        double sum = 0.0;

        for (int i = 0; i < n; i++) {
            double x = a + (i + 0.5) * delta;
            sum += evaluatePolynomial(x);
        }

        return sum * delta;
    }
}