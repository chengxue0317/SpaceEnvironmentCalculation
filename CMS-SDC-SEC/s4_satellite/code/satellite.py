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

#利用传入的数据，判断卫星名称简写是否为S,如果是则获得卫星为S的TEC数据值
def get_TEC_S(file_data_list, P1_channel, P2_channel):
    S_S1_strength,S_S2_strength= [], []
    for k in range(len(file_data_list)):
        if file_data_list[k][0] == 'S':
            file_data_all = file_data_list[k].split()
            if len(file_data_all) == 6:
                file_data_all = file_data_all
            else:
                file_data_all_new = []
                file_data_all_new.extend('0' for _ in range(int(6)-len(file_data_all)))
                file_data_all = file_data_all + file_data_all_new
            v_value, s_value = [], []
            for w in range(len(file_data_all)):
                if w == 3 or w == 6 :
                    s_value.append(file_data_all[w])
            if P1_channel == 'C1C':
                S1 = s_value[0]
            else:
                S1 = s_value[0]

            if P2_channel == 'C1C':
                S2 = s_value[0]
            else:
                S2 = s_value[0]
            S_S1_strength.append(S1)
            S_S2_strength.append(S2)
    new_s1 = get_square(S_S1_strength)
    new_s2 = get_square(S_S2_strength)
    S1_data_1 = su_data(new_s1, int(60))
    S2_data_1 = su_data(new_s2, int(60))
    S1_S4_value_average = get_average(S1_data_1)
    S2_S4_value_average = get_average(S2_data_1)

    S1_data = su_data(S_S1_strength, int(60))
    S2_data = su_data(S_S2_strength, int(60))
    new_S1_data = get_average(S1_data)
    new_S2_data = get_average(S2_data)
    S1_S4_value_square = get_square(new_S1_data)
    S2_S4_value_square = get_square(new_S2_data)
    return S1_S4_value_average, S2_S4_value_average, S1_S4_value_square, S2_S4_value_square

#利用传入的数据，判断卫星名称简写是否为R,如果是则获得卫星为R的TEC数据值
def get_TEC_R(file_data_list, P1_channel, P2_channel):
    R_S1_strength,R_S2_strength = [], []
    for k in range(len(file_data_list)):
        if file_data_list[k][0] == 'R':
            file_data_all = file_data_list[k].split()
            if len(file_data_all) == 12:
                file_data_all = file_data_all
            else:
                file_data_all_new = []
                file_data_all_new.extend('0' for _ in range(int(12)-len(file_data_all)))
                file_data_all = file_data_all + file_data_all_new
            v_value, s_value = [], []
            for w in range(len(file_data_all)):
                if w == 3 or w == 6 or w == 9 or w == 12:
                    s_value.append(file_data_all[w])
            if P1_channel == 'C1C':
                S1 = s_value[0]
            elif P1_channel == 'C1P':
                S1 = s_value[0]
            elif P1_channel == 'C2C':
                S1 = s_value[0]
            else:
                S1 = s_value[0]

            if P2_channel == 'C1C':
                S2 = s_value[0]
            elif P2_channel == 'C1P':
                S2 = s_value[0]
            elif P2_channel == 'C2C':
                S2 = s_value[0]
            else:
                S2 = s_value[0]

            R_S1_strength.append(S1)
            R_S2_strength.append(S2)
    new_s1 = get_square(R_S1_strength)
    new_s2 = get_square(R_S2_strength)
    S1_data_1 = su_data(new_s1, int(60))
    S2_data_1 = su_data(new_s2, int(60))
    S1_S4_value_average = get_average(S1_data_1)
    S2_S4_value_average = get_average(S2_data_1)

    S1_data = su_data(R_S1_strength, int(60))
    S2_data = su_data(R_S2_strength, int(60))
    new_S1_data = get_average(S1_data)
    new_S2_data = get_average(S2_data)
    S1_S4_value_square = get_square(new_S1_data)
    S2_S4_value_square = get_square(new_S2_data)
    return S1_S4_value_average, S2_S4_value_average, S1_S4_value_square, S2_S4_value_square

#利用传入的数据，判断卫星名称简写是否为E,如果是则获得卫星为E的TEC数据值
def get_TEC_E(file_data_list, P1_channel, P2_channel):
    E_S1_strength, E_S2_strength = [], []
    for k in range(len(file_data_list)):
       if file_data_list[k][0] == 'E':
           file_data_all = file_data_list[k].split()
           if len(file_data_all) == 15:
               file_data_all = file_data_all
           else:
               file_data_all_new = []
               file_data_all_new.extend('0' for _ in range(int(15) - len(file_data_all)))
               file_data_all = file_data_all + file_data_all_new
           v_value, s_value = [], []
           for w in range(len(file_data_all)):
               if w == 3 or w == 6 or w == 9 or w == 12 or w == 15:
                   s_value.append(file_data_all[w])
           if P1_channel == 'C1C':
               S1 = s_value[0]
           elif P1_channel == 'C6C':
               S1 = s_value[1]
           elif P1_channel == 'C7Q':
               S1 = s_value[2]
           elif P1_channel == 'C8Q':
               S1 = s_value[3]
           else:
               S1 = s_value[4]

           if P2_channel == 'C1C':
               S2 = s_value[0]
           elif P2_channel == 'C6C':
               S2 = s_value[1]
           elif P2_channel == 'C7Q':
               S2 = s_value[2]
           elif P2_channel == 'C8Q':
               S2 = s_value[3]
           else:
               S2 = s_value[4]
           E_S1_strength.append(S1)
           E_S2_strength.append(S2)
    new_s1 = get_square(E_S1_strength)
    new_s2 = get_square(E_S2_strength)
    S1_data_1 = su_data(new_s1, int(60))
    S2_data_1 = su_data(new_s2, int(60))
    S1_S4_value_average = get_average(S1_data_1)
    S2_S4_value_average = get_average(S2_data_1)

    S1_data = su_data(E_S1_strength, int(60))
    S2_data = su_data(E_S2_strength, int(60))
    new_S1_data = get_average(S1_data)
    new_S2_data = get_average(S2_data)
    S1_S4_value_square = get_square(new_S1_data)
    S2_S4_value_square = get_square(new_S2_data)
    return S1_S4_value_average, S2_S4_value_average, S1_S4_value_square, S2_S4_value_square

#利用传入的数据，判断卫星名称简写是否为C,如果是则获得卫星为C的TEC数据值
def get_TEC_C(file_data_list, P1_channel, P2_channel):
    C_S1_strength, C_S2_strength = [], []
    for k in range(len(file_data_list)):
        if file_data_list[k][0] == 'C':
            file_data_all = file_data_list[k].split()
            if len(file_data_all) == 9:
                file_data_all = file_data_all
            else:
                file_data_all_new = []
                file_data_all_new.extend('0' for _ in range(int(9)-len(file_data_all)))
                file_data_all = file_data_all + file_data_all_new
            v_value, s_value = [], []
            for w in range(len(file_data_all)):
                if w == 3 or w == 6 or w == 9 :
                    s_value.append(file_data_all[w])

            if P1_channel == 'C2I':
                S1 = s_value[0]
            elif P1_channel == 'C6I':
                S1 = s_value[1]
            else:
                S1 = s_value[2]

            if P2_channel == 'C2I':
                S2 = s_value[0]
            elif P2_channel == 'C6I':
                S2 = s_value[1]
            else :
                S2 = s_value[2]
            C_S1_strength.append(S1)
            C_S2_strength.append(S2)
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
    return S1_S4_value_average, S2_S4_value_average, S1_S4_value_square, S2_S4_value_square

#利用传入的数据，判断卫星名称简写是否为G,如果是则获得卫星为G的TEC数据值
def get_TEC_G(file_data_list, P1_channel, P2_channel):
    G_S1_strength, G_S2_strength = [], []
    for k in range(len(file_data_list)):
        if file_data_list[k][0] == 'G':
            file_data_all = file_data_list[k].split()
            if len(file_data_all) == 16:
                file_data_all = file_data_all
            else:
                file_data_all_new = []
                file_data_all_new.extend('0' for _ in range(int(16)-len(file_data_all)))
                file_data_all = file_data_all + file_data_all_new
            s_value = []
            for w in range(len(file_data_all)):
                if w == 3 or w == 6 or w == 9 or w == 12 or w == 15:
                    s_value.append(file_data_all[w])
            if P1_channel == 'C1C':
                S1 = s_value[0]
            elif P1_channel == 'C1w':
                S1 = s_value[1]
            elif P1_channel == 'C2W':
                S1 = s_value[2]
            elif P1_channel == 'C2L':
                S1 = s_value[3]
            else :
                S1 = s_value[4]

            if P2_channel == 'C1C':
                S2 = s_value[0]
            elif P2_channel == 'C1w':
                S2 = s_value[1]
            elif P2_channel == 'C2W':
                S2 = s_value[2]
            elif P2_channel == 'C2L':
                S2 = s_value[3]
            else:
                S2 = s_value[4]
            G_S1_strength.append(S1)
            G_S2_strength.append(S2)

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
    return S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square

#通过传入的观测数据文件、卫星系统编号、卫星标号、频率1数据和频率2数据，获取卫星标号的时间段数据、频段1的S4平均值数据和频段2的S4平方值数据
def read_data(file_path, params_system, PRN, P1_channel, P2_channel):
    file_time, file_data_list, all_time = [], [], []
    files = os.listdir(file_path)
    for file in files:
        if not os.path.isdir(file):
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
    if params_system == 'G':
        S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square = get_TEC_G(file_data_list, P1_channel, P2_channel)
    elif params_system == 'C':
        S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square  = get_TEC_C(file_data_list, P1_channel, P2_channel)
    elif params_system == 'E':
        S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square  = get_TEC_E(file_data_list, P1_channel, P2_channel)
    elif params_system == 'R':
        S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square  = get_TEC_R(file_data_list, P1_channel, P2_channel)
    else:
        S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square  = get_TEC_S(file_data_list, P1_channel, P2_channel)

    return all_time, S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square

def main(file_path, params_system, PRN, P1_channel, P2_channel, stat_time, end_time):

    all_time, S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square  = read_data(file_path, params_system, PRN, P1_channel, P2_channel)
    strength_value_S1 ,strength_value_S2 = get_strength(S1_S4_value_average,S2_S4_value_average,S1_S4_value_square,S2_S4_value_square)
    file_time, S1_value, S2_vaule = get_all_time(strength_value_S1, strength_value_S2, all_time, stat_time, end_time)
    return S1_value, S2_vaule, file_time
#
# if __name__ == '__main__':
#
#     filepath = '..//data//'  #星历数据和观测数据文件路径
#     params_system ='E' #'G','C','E','R','S',卫星名称代号V
#     PRN = '05'      #卫星编号，01是G卫星，23是C卫星，02是E卫星，11是R卫星
#     P1_channel = 'C1C'  # 频率通道1  #G卫星,C卫星
#     P2_channel = 'C6C'  #频率通道#
#     stat_time = '2023-01-01 00:10:00'
#     end_time = '2023-01-01 00:40:00'

    # filepath = sys.argv[1]
    # params_system = sys.argv[2]
    # PRN = sys.argv[3]
    # P1_channel = sys.argv[4]
    # P2_channel = sys.argv[5]
    # stat_time = sys.argv[6]
    # end_time = sys.argv[7]
    # main(filepath, params_system, PRN, P1_channel, P2_channel, stat_time, end_time)