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
    def get_terms(polynomial):
        term_regex = r"(?<!\^)(?=[+-](?!-))"
        terms = re.split(term_regex, polynomial)
        
        for i in range(len(terms)):
            terms[i] = terms[i].strip()
            
            if terms[i].endswith("^"):
                terms[i] += terms[i + 1]
                terms[i + 1] = ""
        
        return list(filter(lambda term: term != "", terms))

    @staticmethod
    def get_coefficients(terms):
        coefficients = [None] * len(terms)
        coefficient_pattern = r"([-+]?\d+)"
        
        for i in range(len(terms)):
            term = terms[i]
            coefficient_matcher = re.search(coefficient_pattern, term)
            
            if coefficient_matcher:
                coefficient = coefficient_matcher.group(1)
                
                if "x" in term:
                    coefficient = coefficient or ("-1" if term.startswith("-") else "1")
                
                coefficients[i] = float(coefficient)
            else:
                coefficients[i] = 0.0
        
        return coefficients

    @staticmethod
    def get_exponents(terms):
        exponents = [None] * len(terms)
        exponent_pattern = r"x\^([-+]?\d+)"
        
        for i in range(len(terms)):
            term = terms[i]
            exponent_matcher = re.search(exponent_pattern, term)
            
            if exponent_matcher:
                exponents[i] = int(exponent_matcher.group(1))
            else:
                exponents[i] = 0
        
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
