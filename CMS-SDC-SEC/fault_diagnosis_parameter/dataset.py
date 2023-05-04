import random

import numpy as np
import datetime
import time

def get_time(start_time,time_span,counts):

    time = datetime.datetime.strptime(start_time,'%m/%d/%y %H:%M:%S')
    list_time = []
    for i in range(counts):

        time = time +  datetime.timedelta(minutes=i*time_span)
        list_time.append(time)
    return list_time

def list_get_data_4values(counts):
    # 通过本函数可以返回一个或一组服从“0~1”均匀分布的随机样本值

    xs_1 = np.random.choice(range(0,300),counts)
    # print("F10.7:",xs_1)

    xs_2 = np.random.choice(range(0,9),counts)
    # print("KP：",xs_2)

    xs_3 = np.random.choice(range(0, 200), counts)
    # print("AP:", xs_3)

    xs_4 = np.random.choice(range(-500,0),counts)
    # print("DST:",xs_4)

    list_4_values = []
    for i in range(counts):
        list_4_values += [[xs_1[i]] + [xs_2[i]] + [xs_3[i]] +[xs_4[i]]]
    # print(list_4_values)
    return list_4_values
def get_data_XY(counts,list_4_values):
    # xs为ndarray类型，用于生成ys，以及 后续机器学习用
    xs = np.array(list_4_values)
    # print(xs)

    # xs = np.random.rand(counts, 4)
    # print(xs)
    ys = np.zeros(counts)
    for i in range(counts):
        x = xs[i]
        # if (np.power(x[0] , 2) + np.power(x[1] , 2) + np.power(x[2], 2) + np.power(x[3], 2)) < 1:
        # if (np.power(x[0] - 0.5, 2) + np.power(x[1] - 0.5, 2) + np.power(x[2] - 0.5,2) + np.power(x[3]-0.5,2)) < 0.5:
        tmp_value = (x[0]/100) + x[1] + x[2]/100 + abs(x[3]/100)
        if tmp_value< 3:
            ys[i] = 0
        elif (tmp_value >=3) and (tmp_value <=6):
            ys[i] = 1
        elif (tmp_value >= 6) and (tmp_value <= 9):
            ys[i] = 2
        elif (tmp_value >= 9) and (tmp_value <= 12):
            ys[i] = 3
        else:
            ys[i] = 4

    return xs,ys

# if __name__ == '__main__':
#
#    # 按照如下样例传递时间参数
#     start_time = '01/01/21 00:00:00'
#    # 每两个数字之间的间隔，可以是3小时，3分钟，3秒，3天等。
#     time_span = 3
#    # 生成n个时间间隔为3*时间单位（天/时/分/秒）的数据，并写入列表。
#     get_time(start_time,time_span,100)
