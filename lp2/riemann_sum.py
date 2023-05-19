import re
import threading


class RiemannSum:
    def __init__(self, a, b, n, polinomio):
        self.a = a
        self.b = b
        self.n = n
        self.terms = self.get_terms(polinomio)
        self.coefficients = self.get_coefficients(self.terms)
        self.exponents = self.get_exponents(self.terms)

    @staticmethod
    def get_terms(polynomial_expression):
        terms = []
        pattern = r'(-?\d+(?:\.\d+)?)\*?x\^(-?\d+)'
        matches = re.findall(pattern, polynomial_expression)

        for match in matches:
            coefficient = float(match[0])
            exponent = int(match[1])
            terms.append((coefficient, exponent))

        return terms

    @staticmethod
    def get_coefficients(terms):
        coefficients = [term[0] for term in terms]
        return coefficients

    @staticmethod
    def get_exponents(terms):
        exponents = [term[1] for term in terms]
        return exponents


class RiemannSumThread(threading.Thread):
    def __init__(self, terms, coefficients, exponents, a, b, num_intervals, thread_index, sums):
        threading.Thread.__init__(self)
        self.terms = terms
        self.coefficients = coefficients
        self.exponents = exponents
        self.a = a
        self.b = b
        self.num_intervals = num_intervals
        self.thread_index = thread_index
        self.sums = sums

    def run(self):
        sum_result = self.calculate_sum()
        self.sums[self.thread_index] = sum_result
        print(f"Hilo ({self.thread_index + 1}) | a={self.a:.4f} - b={self.b:.4f} - n={self.num_intervals} - area={sum_result}")

    def calculate_sum(self):
        delta_x = (self.b - self.a) / self.num_intervals
        sum_result = 0.0

        for i in range(self.num_intervals):
            x = self.a + (i + 0.5) * delta_x
            term_sum = 0.0

            for j in range(len(self.terms)):
                term_sum += self.coefficients[j] * (x ** self.exponents[j])

            sum_result += term_sum * delta_x

        return sum_result
