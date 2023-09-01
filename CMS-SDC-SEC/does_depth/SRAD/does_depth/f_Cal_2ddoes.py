import numpy as np
import f_Cal_shieldose2 as Cal_shieldose2
import datetime


# 读取flux文件
def Cal_2ddoes(time1d, current_dir, dir_name, Var, dir_name_flux, filedir_flux, sptrum_point, sptrum_E, material, shield_type):

    txt_all = []
    f = open(filedir_flux+'/'+dir_name_flux+'/flux.txt')
    file_size = len(f.readlines())
    ff = open(filedir_flux+'/'+dir_name_flux+'/flux.txt')
    for i in range(file_size):
        txt = list(map(float, ff.readline().replace('\n', '').split(',')[3:3+sptrum_point]))
        txt_all.append(txt)
    txt_all = np.array(txt_all)
    ind = np.where(txt_all < 0)
    txt_all[ind] = 0
    txt_all = np.mean(txt_all, axis=0)
    s = 0
    for i in txt_all:
        txt_all[s] = round(i, 1)
        s += 1

    # 计算时间差
    time_min = datetime.datetime.strptime(time1d[0], '%Y-%m-%d %H:%M:%S')
    time_max = datetime.datetime.strptime(time1d[-1], '%Y-%m-%d %H:%M:%S')
    sum_seconds = (time_max - time_min).total_seconds()  # 微秒除以1000可以得到毫秒

    # 能谱点的个数？
    # sptrum_point = 12

    # 积分能谱能量？
    # sptrum_E = [0.1,0.5,1,3,5,10,20,40,80,120,160,200]
    sptrum_E = str(sptrum_E)
    sptrum_E = sptrum_E[1:-1]
    sptrum_E = sptrum_E.replace(',', ' ')

    # 积分能谱通量？
    sptrum_fx = np.zeros(sptrum_point)
    for i in range(sptrum_point):
        sptrum_fx[i] = txt_all[i]-0.5

    # 剔除0值变0.1,否则报错
    ind = np.where(sptrum_fx <= 0)
    sptrum_fx[ind] = 0.01
    sptrum_fx = str(sptrum_fx)
    sptrum_fx = sptrum_fx[1:-1]
    if Var == 'p':
        does, z_mm = Cal_shieldose2.Cal_shieldose2(current_dir, dir_name, 'p', material, shield_type, sptrum_point, sptrum_E, sptrum_fx, sum_seconds)  # 计算辐射剂量
    else:
        does, z_mm = Cal_shieldose2.Cal_shieldose2(current_dir, dir_name, 'e', material, shield_type, sptrum_point, sptrum_E, sptrum_fx, sum_seconds)  # 计算辐射剂量

    does = list(map(float, does))
    z_mm = list(map(float, z_mm))
    does = np.array(does)
    z_mm = np.array(z_mm)
    return z_mm, does
