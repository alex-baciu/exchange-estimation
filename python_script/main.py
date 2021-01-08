# importing the required module
import datetime

from dateutil.relativedelta import relativedelta
import matplotlib.pyplot as plt
from scipy.linalg import solve
from pymongo import MongoClient
import sys
import math


def polynomial(value, parameters, number_of_parameters):
    res = 0
    for i in range(number_of_parameters):
        res += pow(value, number_of_parameters - 1 - i) * parameters[i]
    return res


def main():
    if len(sys.argv) > 1:
        arguments = sys.argv[1].split('~')
        time_type = arguments[0]
        no_predictions = arguments[1]
        coin = arguments[2]
        method = arguments[3]
    else:
        sys.exit(-1)

    no_predictions_number = int(no_predictions)

    # Numarul de prametri al ecuatiei, minim 2
    g = 3

    if method.__contains__("Linear"):
        g = 2
    elif method == "Second":
        g = 3

    # Apelare baza de date
    client = MongoClient('mongodb://operator:operator@localhost:27017/?authSource=admin')
    db = client["estimationapp"]
    col = db["exchanges"]

    values = []
    names = []
    index = 0

    if time_type == "Day":
        index = 1
        days_behind = datetime.timedelta(days=30)
        today = datetime.datetime.today()
        start = today - days_behind
        input_values = col.find({'date': {'$lt': today, '$gte': start},'currency' : coin})

        for it in input_values:
            if math.isnan(it['value']):
                continue
            values.append((it['value'], index))
            names.append(it['date'].strftime("%d %b %Y"))
            index += 1

        for it in range(no_predictions_number):
            names.append((today + datetime.timedelta(days=(it+1))).strftime("%d %b %Y"))
    elif time_type == "Month":
        index = 12
        today = datetime.datetime.today()
        last_month_first_day = today.replace(day=1)

        for count in range(12):
            last_month_last_day = last_month_first_day - datetime.timedelta(days=1)
            last_month_first_day = last_month_last_day.replace(day=1)
            input_values = col.find({'date': {'$lt': last_month_last_day, '$gte': last_month_first_day}, 'currency': coin})
            avg = 0
            avg_index = 0
            for x in input_values:
                if math.isnan(x['value']):
                    continue
                avg += x['value']
                avg_index += 1
            avg = avg / avg_index
            values.append((avg, index))
            names.append(last_month_last_day.strftime("%b %Y"))
            index -= 1

        values.sort(key=lambda l: l[1])
        names.reverse()

        for it in range(no_predictions_number):
            names.append((today + relativedelta(months=+(it))).strftime("%b %Y"))
    elif time_type == "Year":
        index = 12
        today = datetime.datetime.today()
        last_year_first_day = today.replace(day=1, month=1)

        for count in range(12):
            last_year_last_day = last_year_first_day - datetime.timedelta(days=1)
            last_year_first_day = last_year_last_day.replace(day=1,month=1)
            input_values = col.find(
                {'date': {'$lt': last_year_last_day, '$gte': last_year_first_day}, 'currency': coin})
            avg = 0
            avg_index = 0
            for x in input_values:
                if math.isnan(x['value']):
                    continue
                avg += x['value']
                avg_index += 1
            avg = avg / avg_index
            values.append((avg, index))
            names.append(last_year_last_day.year)
            index -= 1

        values.sort(key=lambda l: l[1])
        names.sort()
        for it in range(no_predictions_number):
            names.append(names[-1] + 1)


    # Datele de intrare pe care se vor aplica regresia liniara
    # it_values = [(4.55, 1), (4.57, 2), (4.56, 3), (4.57, 4), (4.59, 5), (4.58, 6), (4.63, 7), (4.63, 8), (4.65, 9),
    #           (4.65, 10), (4.66, 11), (4.65, 12), (4.64, 13)]

    # Numarul de elemente
    n = len(values)

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

    x_pred = []
    y_pred = []

    for it in range(no_predictions_number):
        x_pred.append(x[-1] + it + 1)
        y_pred.append(polynomial(x_pred[-1], parameters, g))

    # plotting the points
    plt.plot(x, y, marker='o', linewidth=0, markersize=8)
    plt.plot(x_pred,y_pred,marker='o',linewidth=0, markersize=8, color='green')

    # naming the x axis
    plt.xlabel(time_type)
    # naming the y axis
    plt.ylabel('Valoare ' + coin)

    # giving a title to my graph
    plt.title('Aproximare cu polinom de gradul: ' + (g - 1).__str__())

    # Set x and y limits
    number_of_points = 100
    x_begin = x[0]
    x_end = x[-1] + no_predictions_number
    step = (x_end - x_begin) / number_of_points

    print(parameters)

    axes = plt.gca()
    axes.set_xlim([x_begin, x_end])
    axes.set_ylim([min(y+y_pred)-0.10, max(y+y_pred)+0.10])

    x_vect = []
    fx_vect = []
    for i in range(number_of_points):
        x_first = x_begin + i * step
        x_vect.append(x_first)
        fx_vect.append(polynomial(x_first, parameters, g))

    plt.xticks(x+x_pred, names, rotation='vertical')

    plt.plot(x_vect, fx_vect)
    plt.savefig('E:/an4/EP/EP-Project/exchange-estimation/client-app/src/assets/grafic.png', bbox_inches='tight')


if __name__ == "__main__":
    main()
    f = open("test.txt", "a")
    f.write(str(sys.argv))
    f.close()
    sys.exit(0)
