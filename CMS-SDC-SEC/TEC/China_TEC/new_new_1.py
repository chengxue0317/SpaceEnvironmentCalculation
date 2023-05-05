import iri90
import multiprocessing
import numpy as np
import time

def get_data4(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(3,4):
        for j in range(8):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[3])

def get_data3(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(2,3):
        for j in range(8):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[2])

def get_data2(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(1,2):
        for j in range(8):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[1])

def get_data1(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(0,1):
        for j in range(8):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[0])

def main(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el):

    jobs_1, jobs_2, jobs_3, jobs_4 = [], [], [], []
    q = multiprocessing.Queue()

    p1 = multiprocessing.Process(target=get_data1, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p2 = multiprocessing.Process(target=get_data2, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p3 = multiprocessing.Process(target=get_data3, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p4 = multiprocessing.Process(target=get_data4, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))

    p1.start()
    jobs_1.append(p1)
    result1 = np.array([q.get() for j in jobs_1]).flatten()
    p1.join()

    p2.start()
    jobs_2.append(p2)
    result2 = np.array([q.get() for j in jobs_2]).flatten()

    p3.start()
    jobs_3.append(p3)
    result3 = np.array([q.get() for j in jobs_3]).flatten()

    p4.start()
    jobs_4.append(p4)
    result4 = np.array([q.get() for j in jobs_4]).flatten()

    result = np.array([result1, result2, result3, result4]).reshape(4, 8)
    return result