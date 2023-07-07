import os
import math
import datetime
import datetime
import sys
import numpy as np
import warnings
warnings.filterwarnings("ignore")

sys_simplify = {'GPS': 'G', 'GLONASS': 'R', 'SBAS': 'S', 'GAL': 'E', 'BDS': 'C', 'QZSS': 'J', 'IRNSS': 'I'}

#获取开始时间和结束时间段内的L1频段的电离层闪烁数据和L2频段的电离层闪烁数据
def get_all_time(strength_value_S1, strength_value_S2, file_time, stat_time, end_time):
    all_file_time, all_S1, all_S2 = [], [], []
    if len(file_time) == int(0):
        time_num = int((datetime.datetime.strptime(end_time.replace('"', ''),'%Y-%m-%d %H:%M:%S') -
                    datetime.datetime.strptime(stat_time.replace('"', ''), '%Y-%m-%d %H:%M:%S')).seconds/int(60))
        all_file_time = [None]*time_num
        all_S1 = [None]*time_num
        all_S2 = [None]*time_num

    for h in range(0, len(file_time)):
        if h <= int(len(file_time)):
            if datetime.datetime.strptime(stat_time.replace('"',''), '%Y-%m-%d %H:%M:%S') <= datetime.datetime.strptime(str(file_time[h]),'%Y-%m-%d %H:%M:%S') and \
                datetime.datetime.strptime(str(file_time[h]), '%Y-%m-%d %H:%M:%S') <= datetime.datetime.strptime(end_time.replace('"',''), '%Y-%m-%d %H:%M:%S'):
                all_S1.append(strength_value_S1[h])
                all_S2.append(strength_value_S2[h])
                all_file_time.append(file_time[h])
    return all_file_time, all_S1, all_S2

def get_DB(S1_strength,S2_strength,file_time):
    S1_strength_new,S2_strength_new, all_DB_S1,all_DB_S2 = [], [], [], []

    dates = []
    start_dt = '2023-01-01 00:00:00'
    end_dt = '2023-01-01 00:59:59'
    dt = datetime.datetime.strptime(start_dt,  "%Y-%m-%d %H:%M:%S")
    date = start_dt[:]

    while date <= end_dt:
        dates.append(date)
        dt = dt + datetime.timedelta(minutes=1)

        date = dt.strftime("%Y-%m-%d %H:%M:%S")


    for s in range(len(dates)):
        if dates[s] in file_time:
            S1_strength_new.append(S1_strength[s])
            S2_strength_new.append(S2_strength[s])

    for h in range(len(S1_strength_new)):
        if h !=0 :
            h_new_s1 = float(S1_strength_new[h])-float(S1_strength_new[h-1])
            if h_new_s1 >=0 :
                all_DB_S1.append(h_new_s1)
            else:
                all_DB_S1.append(0)
    all_DB_S1 = [0] + all_DB_S1

    for h in range(len(S2_strength_new)):
        if h != 0:
            h_new_s2 = float(S2_strength_new[h]) - float(S2_strength_new[h - 1])
            if h_new_s2 >= 0:
                all_DB_S2.append(h_new_s2)
            else:
                all_DB_S2.append(0)
    all_DB_S2 = [0] + all_DB_S2
    return all_DB_S1, all_DB_S2

#通过L1频段和L2频段的S4平均值数据和平方数据计算求得电离层闪烁数据
def get_strength(S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square):
    strength_value_S1, strength_value_S2 = [], []
    for k in range(len(S1_S4_value_average)):
        if S1_S4_value_square[k] != 0:
            S1_S4 = math.sqrt((S1_S4_value_average[k]-S1_S4_value_square[k])/S1_S4_value_square[k])
            strength_value_S1.append(S1_S4)
        else:
            S1_S4 = 0
            strength_value_S1.append(S1_S4)

        if S2_S4_value_square[k] != 0:
            S2_S4 = math.sqrt((S2_S4_value_average[k]-S2_S4_value_square[k])/S2_S4_value_square[k])
            strength_value_S2.append(S2_S4)
        else:
            S2_S4 = 0
            strength_value_S2.append(S2_S4)
    return strength_value_S1, strength_value_S2

#获取列表的平均值数据
def get_average(s_list):
    s4_list = [item / 60 for item in s_list]
    return s4_list

#获取列表数据的平方数据
def square(num):
    return num*num

#获取列表的平方数据，并将列表数据转换为float格式数据
def get_square(S_list):
    s4_list = list(map(square, map(float, S_list)))
    return s4_list

#将列表数据按照degree长度均分为多个长度为degree的列表数据
def su_data(lst, degree):
    k_list = []
    new_k_list = [lst[i:i+int(degree)] for i in range(0, len(lst), int(degree))]
    for e in range(len(new_k_list)):
        k_list.append(sum(list(map(float, new_k_list[e]))))
    return k_list

#利用传入的数据，判断卫星名称简写是否为C,如果是则获得卫星为C的TEC数据值
def get_TEC_C(file_data_list, P1_channel, P2_channel):
    C_S1_strength, C_S2_strength = [], []
    for k in range(len(file_data_list)):
        if file_data_list[k][0] == 'C':
            file_data_all = file_data_list[k].split()
            if P1_channel == 'C2I' and P2_channel == 'C6I':
                C_S1_strength.append(file_data_all[-2])
                C_S2_strength.append(file_data_all[-1])

    new_s1 = get_square(C_S1_strength)
    new_s2 = get_square(C_S2_strength)
    S1_data_1 = su_data(new_s1, int(60))
    S2_data_1 = su_data(new_s2, int(60))
    S1_S4_value_average = get_average(S1_data_1)
    S2_S4_value_average = get_average(S2_data_1)

    S1_data = su_data(C_S1_strength, int(60))
    S2_data = su_data(C_S2_strength, int(60))
    new_S1_data = get_average(S1_data)
    new_S2_data = get_average(S2_data)
    S1_S4_value_square = get_square(new_S1_data)
    S2_S4_value_square = get_square(new_S2_data)
    return S1_S4_value_average, S2_S4_value_average, S1_S4_value_square, S2_S4_value_square,C_S1_strength,C_S2_strength

#利用传入的数据，判断卫星名称简写是否为G,如果是则获得卫星为G的TEC数据值
def get_TEC_G(file_data_list, P1_channel, P2_channel):
    G_S1_strength, G_S2_strength = [], []
    for k in range(len(file_data_list)):
        if file_data_list[k][0] == 'G':
            file_data_all = file_data_list[k].split()
            if P1_channel == 'C1C' and P2_channel == 'C2W':
                G_S1_strength.append(file_data_all[-3])
                G_S2_strength.append(file_data_all[-1])

    new_s1 = get_square(G_S1_strength)
    new_s2 = get_square(G_S2_strength)
    S1_data_1 = su_data(new_s1, int(60))
    S2_data_1 = su_data(new_s2, int(60))
    S1_S4_value_average = get_average(S1_data_1)
    S2_S4_value_average = get_average(S2_data_1)

    S1_data = su_data(G_S1_strength, int(60))
    S2_data = su_data(G_S2_strength, int(60))
    new_S1_data = get_average(S1_data)
    new_S2_data = get_average(S2_data)
    S1_S4_value_square = get_square(new_S1_data)
    S2_S4_value_square = get_square(new_S2_data)
    return S1_S4_value_average, S2_S4_value_average, S1_S4_value_square, S2_S4_value_square, G_S1_strength, G_S2_strength

#通过传入的观测数据文件、卫星系统编号、卫星标号、频率1数据和频率2数据，获取卫星标号的时间段数据、频段1的S4平均值数据和频段2的S4平方值数据
def read_data(file_path, params_system, PRN, P1_channel, P2_channel):
    file_time, file_data_list, all_time = [], [], []
    files = os.listdir(file_path)
    for f in range(len(files)):
        file = files[f]
        # if not os.path.isdir(file):  #用于判断对象是否为一个文件
        if file.split('.')[-1] == '23o':
            file_data = open(file_path + "/" + file, 'r').read().split('END OF HEADER')[1].split('>')[1:]  # 打开文件
            for i in range(len(file_data)):
                file_data_new = file_data[i].split('\n')
                for j in range(1, len(file_data_new) - 1):
                    if file_data_new[j][0] == str(params_system) and file_data_new[j][1:3] == str(PRN):
                        file_data_list.append(file_data_new[j])
                        time = file_data_new[0].split('\n')[0][1:5] + '-' + file_data_new[0].split('\n')[0][6:8] + '-' + \
                               file_data_new[0].split('\n')[0][9:11] + ' ' + file_data_new[0].split('\n')[0][12:14] + ':' + \
                               file_data_new[0].split('\n')[0][15:17] + ':' + file_data_new[0].split('\n')[0][18:20]
                        if time.split(':')[-1][0] == ' ':
                            time_new = time.split(':')[-1][0].replace(' ','0')
                            time_new1 = file_data_new[0].split('\n')[0][1:5] + '-' + file_data_new[0].split('\n')[0][6:8] + '-' + \
                                           file_data_new[0].split('\n')[0][9:11] + ' ' + file_data_new[0].split('\n')[0][12:14] + ':' + \
                                           file_data_new[0].split('\n')[0][15:17] + ':' +  time_new + file_data_new[0].split('\n')[0][19:20]
                            file_time.append(time_new1)
                        else:
                            file_time.append(time)
    for h in range(len(file_time)):
        if h%60 ==0:
            all_time.append(file_time[h])
    all_time_new = sorted(all_time)
    if params_system == 'G':
        S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square, S1_strength, S2_strength = get_TEC_G(file_data_list, P1_channel, P2_channel)

    else:
        S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square, S1_strength,S2_strength  = get_TEC_C(file_data_list, P1_channel, P2_channel)

    return all_time_new, S1_S4_value_average,S2_S4_value_average,S1_S4_value_square, S2_S4_value_square, S1_strength, S2_strength

def main(file_path, params_system, PRN, P1_channel, P2_channel, stat_time, end_time):

    all_time, S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square, S1_strength, S2_strength  = read_data(file_path, params_system, PRN, P1_channel, P2_channel)
    strength_value_S1 ,strength_value_S2 = get_strength(S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square)
    file_time, S1_value, S2_vaule = get_all_time(strength_value_S1, strength_value_S2, all_time, stat_time, end_time)
    all_DB_S1, all_DB_S2 = get_DB(S1_strength, S2_strength,file_time)

    return S1_value, S2_vaule, file_time, all_DB_S1, all_DB_S2

# if __name__ == '__main__':
#
#     filepath = '..//data_new//'  #星历数据和观测数据文件路径
#     params_system ='C' #'G','C',卫星名称代号V
#     PRN = '13'      #卫星编号，
#     P1_channel = 'C2I'  # 频率通道1  #G卫星,C卫星
#     P2_channel = 'C6I'  #频率通道#
#     stat_time = '2023-01-01 00:00:00'
#     end_time = '2023-01-01 00:40:00'

    # filepath = sys.argv[1]
    # params_system = sys.argv[2]
    # PRN = sys.argv[3]
    # P1_channel = sys.argv[4]
    # P2_channel = sys.argv[5]
    # stat_time = sys.argv[6]
    # end_time = sys.argv[7]
    # main(filepath, params_system, PRN, P1_channel, P2_channel, stat_time, end_time)

