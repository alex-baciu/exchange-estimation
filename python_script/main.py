# importing the required module
import matplotlib.pyplot as plt
from scipy.linalg import solve
from pymongo import MongoClient
import sys;


def polynomial(value, parameters, number_of_parameters):
    res = 0
    for i in range(number_of_parameters):
        res += pow(value, number_of_parameters - 1 - i) * parameters[i]
    return res


def main():
    client = MongoClient('mongodb://operator:operator@localhost:27017/?authSource=admin')

    print(client.list_database_names())

    # Datele de intrare pe care se vor aplica regresia liniara
    values = [(4.55, 1), (4.57, 2), (4.56, 3), (4.57, 4), (4.59, 5), (4.58, 6), (4.63, 7), (4.63, 8), (4.65, 9),
              (4.65, 10), (4.66, 11), (4.65, 12), (4.64, 13)]

    # Numarul de elemente
    n = len(values)

    # Numarul de prametri al ecuatiei, minim 2
    g = 3

    # Initializare parametri a si b ai dreptei pe care o cautam a * x + b = y
    # parameters = [0 for i in range(g)]

    # a = 0
    # b = 0

    # Aplicam metoda celor mai mici patrate
    # Algoritmul va fi detaliat intr-un document separat numit "Regresie cu metoda celor mai mici patrate"

    # Initializam matricea de coeficienti
    A = [0] * g
    for i in range(g):
        A[i] = [0] * g

    B = [0] * g

    # yCoeff = it[0] if i == 0 else 1

    # Calculam matricea de coeficienti A
    for it in values:
        for i in range(g):
            for j in range(g):
                A[i][j] += pow(it[1], i + g - 1 - j)

    # Calculam vectorul de coeficienti B
    for it in values:
        for i in range(g):
            B[i] += it[0] * pow(it[1], i)

    # Vectorul de parametri ai functiei polinomiale
    parameters = solve(A, B)

    # Afisare rezultate intermediare
    # print("b = " + parameters[1].__str__())
    # print("a = " + parameters[0].__str__())
    # print("xSum = " + A[0][0].__str__())
    # print("ySum = " + B[0].__str__())
    # print("xSquaredSum = " + A[1][0].__str__())
    # print("xySum = " + B[1].__str__())

    # x axis values
    x = list(map(lambda e: e[1], values.__iter__()))
    # corresponding y axis values
    y = list(map(lambda e: e[0], values.__iter__()))

    # plotting the points
    plt.plot(x, y, marker='o', linewidth=0, markersize=12)


    # naming the x axis
    plt.xlabel('x - axis')
    # naming the y axis
    plt.ylabel('y - axis')

    # giving a title to my graph
    plt.title('Aproximare cu polinom de gradul :' + (g-1).__str__())

    # Set x and y limits
    number_of_points = 100
    x_begin = x[0]
    x_end = x[-1] + 5
    step = (x_end - x_begin) / number_of_points

    print(parameters)

    axes = plt.gca()
    axes.set_xlim([x_begin, x_end])
    axes.set_ylim([4.5,4.7])

    x_vect = []
    fx_vect = []
    for i in range(number_of_points):
        x_first = x_begin + i * step
        x_vect.append(x_first)
        fx_vect.append(polynomial(x_first, parameters, g))

    plt.plot(x_vect, fx_vect)

    # function to show the plot
    plt.savefig('E:/an4/EP/EP-Project/exchange-estimation/client-app/src/assets/grafic.png')



if __name__ == "__main__":
    main()
    f = open("test.txt", "a")
    f.write(str(sys.argv))
    f.close()
    sys.exit(200)
