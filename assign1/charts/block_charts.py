import numpy as np
from matplotlib import pyplot as plt

metrics = open("charts/metrics/cpp_block_multiplication_metrics.txt").readlines()

metrics = [x.split(";") for x in metrics]

arr_128  = [x for x in metrics if x[1] == "128"]
arr_256  = [x for x in metrics if x[1] == "256"]
arr_512  = [x for x in metrics if x[1] == "512"]
arr_1024 = [x for x in metrics if x[1] == "1024"]


arr_128 = [[int(x[0]), float(x[1]), float(x[2]), float(x[3]), float(x[4])] for x in arr_128]
arr_256 = [[int(x[0]), float(x[1]), float(x[2]), float(x[3]), float(x[4])] for x in arr_256]
arr_512 = [[int(x[0]), float(x[1]), float(x[2]), float(x[3]), float(x[4])] for x in arr_512]
arr_1024 = [[int(x[0]), float(x[1]), float(x[2]), float(x[3]), float(x[4])] for x in arr_1024]

xarr = np.array([x[0] for x in arr_128])

yarr_128 = np.array([x[4]/(x[2] * 1000) for x in arr_128])
yarr_256 = np.array([x[4]/(x[2] * 1000) for x in arr_256])
yarr_512 = np.array([x[4]/(x[2] * 1000) for x in arr_512])
yarr_1024 = np.array([x[4]/(x[2] * 1000) for x in arr_1024])

plt.plot(xarr, yarr_128, "r", marker="D", label="128 block size")
plt.plot(xarr, yarr_256, "g", marker="D", label="256 block size")
plt.plot(xarr, yarr_512, "b", marker="D", label="512 block size")
plt.plot(xarr, yarr_1024, "y", marker="D", label="1024 block size")

plt.legend(loc="upper left")
plt.grid()

plt.xlabel("Matrix Size (n in a nxn matrix)")
plt.ylabel("L2 cache misses per milissecond (miss/ms)")
plt.show()