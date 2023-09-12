import pandas as pd
import warnings
import os
import numpy as np
import datetime
import random
import sys
from matplotlib import pyplot as plt
from PIL import Image
from msis_simu import Connect_SQL, timestamp, standard_time
from msis_simu import get_targetTime_previous_F107, get_targetTime_81centremean_F107, get_targetTime_Apmatrix
from F107_Ap_forecast import forecast_length


warnings.filterwarnings("ignore")
np.set_printoptions(suppress=True)


# 读取flux文件
def Read_msis_txt(dir_name_flux, filedir_flux):
    txt_all = []
    f = open(filedir_flux+'/figure/'+dir_name_flux+'/msis2.0_simulations.txt')
    file_size = len(f.readlines())
    ff = open(filedir_flux+'/figure/'+dir_name_flux+'/msis2.0_simulations.txt')
    for i in range(file_size):
        txt = float(ff.readline().replace(' ', '').replace('\n', ''))
        txt_all.append(txt)
    txt_all = np.array(txt_all)
    return txt_all


def Cal_global_den(model_time, z, conn):
    # model_time: MSIS模拟大气密度时间
    # z: MSIS模拟大气密度高度

    # 生成经纬网格
    degree = 1.0
    lon = np.arange(-180, 181, degree)
    lat = np.arange(90, -91, -degree)
    lon2d, lat2d = np.meshgrid(lon, lat)

    # F107时间
    model_time = model_time.replace('"', '')
    if (len(model_time.split('-')[1]) == 1):
        model_time = model_time[:5]+'0'+model_time[5:]
    if (len(model_time.split('-')[2].split(' ')[0]) == 1):
        model_time = model_time[:8]+'0'+model_time[8:]
    time_f107 = model_time.split()[0]+'T12:00:00.00000000'
    time_ap = model_time.replace(' ', 'T')+'.00000000'
    
    timef107_s, f107_s, timeap_s, ap_s = forecast_length(conn, time_f107)
    # 读取太阳F107数据。最终F107值为：(1)F107, (2) F107_t
    Time_F107_start = standard_time(timestamp(time_f107.split('T')[0] + ' 12:00:00')-86400*41)
    Time_F107_end = standard_time(timestamp(time_f107.split('T')[0] + ' 12:00:00')+86400*41)

    Time_span = "select F107,TIME from SEC_F107_FLUX where TIME between '%s' and '%s' " % (Time_F107_start, Time_F107_end)
    P = pd.read_sql(Time_span, conn)
    F107 = P.F107.values  # 数据库里的F107值
    F107 = np.array(F107.tolist()+f107_s)  # 将数据库的F107值与预测F107值合并 
    F107_t = P.TIME.values
    t_new = []
    for i in range(len(F107_t)):
        t_new.append(str(F107_t[i]))  # 数据库里的时间
    F107_t = np.array(t_new+timef107_s)  # 将数据库的时间与预测的时间合并
    # 将时间转换为时间戳
    F107_tstamp = np.zeros(len(F107_t))
    for i in range(len(F107_t)):
        F107_tstamp[i] = timestamp(str(F107_t[i]).split('T')[0] + ' 12:00:00')  # 格式为时间戳
    
    # 输出是否预报了F107值
    if len(timef107_s) > 0:
        print('预报了F107值,从', timef107_s[0].split('T')[0], '到', timef107_s[-1].split('T')[0])
    else:
        print('未预报F107值')

    # 生成高度，单位：km
    if z > 2000:
        sys.exit()

    lat1d = []
    lon1d = []
    time1d = []
    z1d = []
    for i in range(0, lon2d.shape[1]):
        for j in range(0, lon2d.shape[0]):
            lat1d.append(lat2d[j, i])
            lon1d.append(lon2d[j, i])
            time1d.append(model_time)
            z1d.append(z)

    # 读取AP数据
    # 读取地磁AP数据。最终AP为: (1)AP,(2)AP_t
    Time_AP_start = standard_time(timestamp(time_ap.split('T')[0] + ' 12:00:00')-86400*4)
    Time_AP_end = standard_time(timestamp(time_ap.split('T')[0] + ' 12:00:00')+86400)
    Time_span = "select AP,TIME from SEC_AP_INDEX where TIME between '%s' and '%s' " % (Time_AP_start, Time_AP_end)
    P = pd.read_sql(Time_span, conn)
    AP = P.AP.values.astype(np.int)  # 数据库里的Ap
    AP = np.array(AP.tolist()+ap_s)  # 将数据库的AP值与预测值合并
    AP_t = P.TIME.values
    t_new = []
    for i in range(len(AP_t)):
        t_new.append(str(AP_t[i]))  # 数据库里的时间
    AP_t = np.array(t_new+timeap_s)  # 将数据库的时间与预测的时间合并
    # 将时间转换为时间戳
    AP_tstamp = np.zeros(len(AP_t))
    for i in range(len(AP_t)):
        AP_tstamp[i] = timestamp(str(AP_t[i]).replace('T', ' ').split('.')[0])  # Ap时间戳
    # 输出是否预报了AP值
    if len(timeap_s) > 0:
        print('预报了AP值,从', timeap_s[0], '到', timeap_s[-1])
    else:
        print('未预报AP值')

    # day of year
    day = datetime.datetime.strptime(model_time, '%Y-%m-%d %H:%M:%S')
    day_of_year = day.strftime("%j")

    # UTC
    utc = int(model_time[11:13])*3600 + int(model_time[14:16])*60 + int(model_time[17:19])

    # F107_previous_day
    F107_previous_day = get_targetTime_previous_F107(time_f107, F107, F107_tstamp)
        
    # F107_81mean
    F107_81mean = get_targetTime_81centremean_F107(time_f107, F107, F107_tstamp)

    # AP矩阵
    APmatrix = get_targetTime_Apmatrix(time_ap, AP, AP_tstamp)

    AP1 = APmatrix[0]
    AP2 = APmatrix[1]
    AP3 = APmatrix[2]
    AP4 = APmatrix[3]
    AP5 = APmatrix[4]
    AP6 = APmatrix[5]
    AP7 = APmatrix[6]

    # 判断是否存在figure文件夹
    current_dir = os.path.dirname(os.path.abspath(__file__))
    if os.path.exists(current_dir+'/figure') == False:
        os.system('mkdir '+current_dir+'/figure')

    # 建立模拟结果文件夹(唯一性)
    current_dir = current_dir
    while True:
        dir_name = str(random.randint(1, 99999))
        if os.path.exists(current_dir+'/figure/'+dir_name) == False:
            os.system('mkdir '+current_dir+'/figure/'+dir_name)
            break
    os.system('cp '+current_dir+'/' + 'msis2.0_test_storm.exe '+current_dir+'/figure/'+dir_name)
    os.system('cp '+current_dir+'/' + 'msis20.parm '+current_dir+'/figure/'+dir_name)

    # 将msis背景场写入Txt文件
    with open(current_dir+'/figure/'+dir_name+'/msis2.0_forcing.txt', 'w', encoding='utf-8') as f:
        for i in range(0, len(lat1d)):
            f.write(str(day_of_year))  
            f.write('  ')
            f.write(str(utc))
            f.write('  ')
            f.write(str(float(z1d[i])))
            f.write('  ')
            f.write(str(float(lat1d[i])))
            f.write('  ')
            f.write(str(float(lon1d[i])))
            f.write('  ')
            f.write('99')
            f.write('  ')
            f.write(str(F107_81mean))
            f.write('  ')
            f.write(str(F107_previous_day))
            f.write('  ')
            f.write(str(AP1))
            f.write('  ')
            f.write(str(AP2))
            f.write('  ')
            f.write(str(AP3))
            f.write('  ')
            f.write(str(AP4))
            f.write('  ')
            f.write(str(AP5))
            f.write('  ')
            f.write(str(AP6))
            f.write('  ')
            f.write(str(AP7))
            f.write('\n')

    # 运行FORTRAN seu.exe
    os.system('cd '+current_dir+'/figure/'+dir_name+';  ./msis2.0_test_storm.exe >/dev/null 2>&1') 
    txt = Read_msis_txt(dir_name, current_dir)

    # 1d 转化为2d
    s = 0
    num_row = lon2d.shape[0]
    num_column = lon2d.shape[1]
    p2d = np.zeros([num_row, num_column])-88888
    for i in range(0, num_column):
        m = i*num_row
        n = (i+1)*num_row
        p2d[:, s] = txt[m:n]
        s = s+1
    p2d = p2d*1000

    #######################################################################
    # 画图
    fig = plt.contourf(lon2d, lat2d, p2d, 400, cmap='rainbow')
    plt.rcParams["figure.facecolor"] = 'black'
    plt.rcParams["savefig.facecolor"] = 'black'
    # x轴刻度值trick的关闭
    plt.xticks([])
    # x轴刻度值trick的关闭
    plt.yticks([])
    # 关闭上下左右坐标轴
    plt.gca().spines['top'].set_visible(False)
    plt.gca().spines['bottom'].set_visible(False)
    plt.gca().spines['left'].set_visible(False)
    plt.gca().spines['right'].set_visible(False)

    # colorbar 设置
    clb = plt.colorbar(orientation='horizontal', shrink=1.1)
    clb.ax.set_title('[Air Density/(kg/m$^{3}$)]', color='white')
    # 设置colorbar label的颜色
    clb.ax.tick_params(labelcolor='white')
    clb.ax.tick_params(color='white')
    clb.ax.tick_params(labelsize=8)

    path = current_dir+'/figure/'+dir_name

    # 存colorbar图
    plt.savefig(path+'/test.jpg', dpi=600)
    img = Image.open(path+'/test.jpg')
    # 左边界 #上边界  #右边界  #下边界
    region = img.crop((100, 2060, 3840, 2700))
    region.save(path+'/colorbar.jpg')

    # 存contourf图
    plt.clf()
    plt.contourf(lon2d, lat2d, p2d, 400, cmap='rainbow')
    # x轴刻度值trick的关闭
    plt.xticks([])
    # x轴刻度值trick的关闭
    plt.yticks([])
    # 关闭上下左右坐标轴
    plt.gca().spines['top'].set_visible(False)
    plt.gca().spines['bottom'].set_visible(False)
    plt.gca().spines['left'].set_visible(False)
    plt.gca().spines['right'].set_visible(False)

    # 去除空白
    plt.axis('off')
    plt.gca().xaxis.set_major_locator(plt.NullLocator())
    plt.gca().yaxis.set_major_locator(plt.NullLocator())
    plt.subplots_adjust(top=1, bottom=0, right=1, left=0, hspace=0, wspace=0)
    plt.margins(0, 0)
    plt.savefig(path+'/main_figure.jpg', dpi=600, pad_inches=0)

    # 删除多余数据
    os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/test.jpg')
    os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/msis2.0_simulations.txt')
    os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/msis2.0_forcing.txt')
    os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/msis2.0_test_storm.exe')
    os.system('rm -rf '+current_dir+'/figure/'+dir_name+'/msis20.parm')

    # 输出文件路径
    return ('###'+path+'###')


model_time = str(sys.argv[1])
z = float(sys.argv[2])
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
conn = Connect_SQL(iniPath)
print(Cal_global_den(model_time, z, conn))