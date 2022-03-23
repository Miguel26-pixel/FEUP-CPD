import numpy as np
from matplotlib import pyplot as plt

cpp_metrics = open("charts/metrics/cpp_line_product_metrics.txt").readlines()
rs_metrics = open("charts/metrics/rs_line_multiplication_metrics.txt").readlines()

cpp_metrics = [x.split(";") for x in cpp_metrics]
rs_metrics = [x.split(";") for x in rs_metrics]

for x in cpp_metrics:
    x[3] = x[3].rstrip()
for x in rs_metrics:
    x[3] = x[3].rstrip()

cpp_metrics = [[int(x[0]), float(x[1]), float(x[2]), float(x[3]), float(x[4])] for x in cpp_metrics]
rs_metrics = [[int(x[0]), float(x[1]), float(x[2]), float(x[3])] for x in rs_metrics]

cpp_xarr = np.array([x[0] for x in cpp_metrics])
cpp_yarr = np.array([x[1]*1000 for x in cpp_metrics])

rs_xarr = np.array([x[0] for x in rs_metrics])
rs_yarr = np.array([x[1] for x in rs_metrics])

plt.plot(cpp_xarr, cpp_yarr, "r")
plt.plot(rs_xarr, rs_yarr, "g")
plt.show()