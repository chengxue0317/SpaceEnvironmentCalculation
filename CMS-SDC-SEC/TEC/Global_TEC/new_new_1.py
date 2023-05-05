import iri90
import multiprocessing
import numpy as np
import time

def get_data13(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(12,13):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[12])

def get_data12(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(11,12):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[11])

def get_data11(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(10,11):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[10])

def get_data10(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(9,10):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[9])

def get_data9(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(8,9):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[8])

def get_data8(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(7,8):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[7])

def get_data7(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(6,7):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[6])

def get_data6(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(5,6):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[5])

def get_data5(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(4,5):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[4])

def get_data4(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(3,4):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[3])

def get_data3(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(2,3):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[2])

def get_data2(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(1,2):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[1])

def get_data1(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el, q):
    for i in range(0,1):
        for j in range(25):
            latlon = (lat2d_1deg[i, j], lon2d_1deg[i, j])
            iono = iri90.runiri(dtime, altkm, latlon, f_107, f_107, ap)
            el[i, j] = iono[:, 0]
    q.put(el[0])

def main(lat2d_1deg, lon2d_1deg ,dtime, altkm, f_107, ap, el):

    jobs_1, jobs_2, jobs_3, jobs_4 ,jobs_5, jobs_6, jobs_7, jobs_8 ,jobs_9, jobs_10, jobs_11, jobs_12 , jobs_13 = [], [], [], [],[], [], [], [],[], [], [], [], []
    q = multiprocessing.Queue()

    p1 = multiprocessing.Process(target=get_data1, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p2 = multiprocessing.Process(target=get_data2, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p3 = multiprocessing.Process(target=get_data3, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p4 = multiprocessing.Process(target=get_data4, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p5 = multiprocessing.Process(target=get_data5, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p6 = multiprocessing.Process(target=get_data6, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p7 = multiprocessing.Process(target=get_data7, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p8 = multiprocessing.Process(target=get_data8, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p9 = multiprocessing.Process(target=get_data9, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p10 = multiprocessing.Process(target=get_data10, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p11 = multiprocessing.Process(target=get_data11, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p12 = multiprocessing.Process(target=get_data12, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))
    p13 = multiprocessing.Process(target=get_data13, args=(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el, q))


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

    p5.start()
    jobs_5.append(p5)
    result5 = np.array([q.get() for j in jobs_5]).flatten()
    p5.join()

    p6.start()
    jobs_6.append(p6)
    result6 = np.array([q.get() for j in jobs_6]).flatten()

    p7.start()
    jobs_7.append(p7)
    result7 = np.array([q.get() for j in jobs_7]).flatten()

    p8.start()
    jobs_8.append(p8)
    result8 = np.array([q.get() for j in jobs_8]).flatten()

    p9.start()
    jobs_9.append(p9)
    result9 = np.array([q.get() for j in jobs_9]).flatten()
    p9.join()

    p10.start()
    jobs_10.append(p10)
    result10 = np.array([q.get() for j in jobs_10]).flatten()

    p11.start()
    jobs_11.append(p11)
    result11 = np.array([q.get() for j in jobs_11]).flatten()

    p12.start()
    jobs_12.append(p12)
    result12 = np.array([q.get() for j in jobs_12]).flatten()

    p13.start()
    jobs_13.append(p13)
    result13 = np.array([q.get() for j in jobs_13]).flatten()

    result = np.array([result1, result2, result3, result4,result5,result6,result7,result8,result9,result10,result11,result12,result13]).reshape(13, 25)
    return result