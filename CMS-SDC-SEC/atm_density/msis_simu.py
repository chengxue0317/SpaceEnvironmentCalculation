import dmPython
import pandas as pd
import os
import warnings
import numpy as np
import datetime
import random
import sys
from matplotlib import cm
from scipy.interpolate import interp1d
from matplotlib.colors import LinearSegmentedColormap
from matplotlib import pyplot as plt
from PIL import Image
import json
import time
from F107_Ap_forecast import forecast_length
warnings.filterwarnings("ignore")
np.set_printoptions(suppress=True)


def Connect_SQL(iniPath):
    from configparser import ConfigParser
    cfg = ConfigParser()
    cfg.read(iniPath)
    sql_cfg = dict(cfg.items("dmsql"))
    conn = dmPython.connect(
        user=sql_cfg['user'],
        password=sql_cfg['password'],
        server=sql_cfg['server'],
        port=sql_cfg['port'])
    return conn


def valid_numbers(x):
    x = float(np.format_float_positional(x, precision=3, unique=False, fractional=False, trim='k'))
    return x


def replace_char(old_string, char, index):
    """
    字符串按索引位置替换字符
    old_string: 原始字符串
    char： 要替换成啥？
    index： 下标
    """
    old_string = str(old_string)
    # 新的字符串 = 旧字符串[:要替换的索引位置] + 替换成的目标字符 + 旧字符串[要替换的索引位置+1:]
    new_string = old_string[:index] + char + old_string[index + 1:]
    return new_string


def timestamp(standard_time):
    # 由标准时间转换为时间戳
    s_t = time.strptime(standard_time, "%Y-%m-%d %H:%M:%S")
    mkt = int(time.mktime(s_t))
    return (mkt)


def standard_time(timeStamp):
    # 由时间戳转化为标准时间
    timeArray = time.localtime(timeStamp)
    otherStyleTime = time.strftime("%Y-%m-%d %H:%M:%S", timeArray)
    return otherStyleTime


def get_targetTime_previous_F107(t, F107, F107_tstamp):
    '''
    t: 目标时间'2023-02-02T20:00:00.00000000'(list)
    F107: 空间环境指数 (np.array)
    F107_tstamp: 空间环境指数对应的时间(np.array)
    '''
    time_str = t.split('T')[0] + ' 12:00:00'
    time_F107_previous_day = timestamp(time_str)-86400
    ind = np.where(F107_tstamp == time_F107_previous_day)
    return F107[ind][0]


def get_targetTime_81centremean_F107(t, F107, F107_tstamp):
    '''
    t: 目标时间'2023-02-02T20:00:00.00000000'(list)
    F107: 空间环境指数 (np.array)
    F107_tstamp: 空间环境指数对应的时间(np.array)
    '''
    time_str = t.split('T')[0] + ' 12:00:00'
    f107_81_start = timestamp(time_str)-86400*40  # 前40天
    f107_81_end = timestamp(time_str)+86400*40  # 后40天
    ind = np.where((F107_tstamp >= f107_81_start) & (F107_tstamp <= f107_81_end))
    if (len(F107[ind]) != 81):
        print('81天平均F107数据读取错误')
        sys.exit()
    return round(np.nanmean(F107[ind]))


def get_targetTime_Apmatrix(t, AP, AP_tstamp):
    '''
    t: 目标时间'2023-02-02T20:00:00.00000000'(list)
    F107: 空间环境指数 (np.array)
    F107_tstamp: 空间环境指数对应的时间(np.array)
    '''
    H_targetT = int(t[11:13])  # 获取小时
    if H_targetT >= 0 and H_targetT < 3:
        ap_time = t.split('T')[0]+' 01:15:00'
    elif H_targetT >= 3 and H_targetT < 6:
        ap_time = t.split('T')[0]+' 04:15:00'
    elif H_targetT >= 6 and H_targetT < 9:
        ap_time = t.split('T')[0]+' 07:15:00'
    elif H_targetT >= 9 and H_targetT < 12:
        ap_time = t.split('T')[0]+' 10:15:00'
    elif H_targetT >= 12 and H_targetT < 15:
        ap_time = t.split('T')[0]+' 13:15:00'
    elif H_targetT >= 15 and H_targetT < 18:
        ap_time = t.split('T')[0]+' 16:15:00'
    elif H_targetT >= 18 and H_targetT < 21:
        ap_time = t.split('T')[0]+' 19:15:00'
    else:
        ap_time = t.split('T')[0]+' 22:15:00'
    ind = np.where(AP_tstamp == timestamp(ap_time))
    AP1 = round(np.mean(AP[ind[0][0]-4:ind[0][0]+4]))
    AP2 = AP[ind[0][0]]
    AP3 = AP[ind[0][0]-1]
    AP4 = AP[ind[0][0]-2]
    AP5 = AP[ind[0][0]-3]
    AP6 = AP[ind[0][0]-4]
    AP7 = round(np.mean(AP[ind[0][0]-12:ind[0][0]-4]))
    AP8 = round(np.mean(AP[ind[0][0]-20:ind[0][0]-12]))
    return [AP1, AP2, AP3, AP4, AP5, AP6, AP7, AP8]


# 获取数据库对应卫星星下点的时间分辨率
def get_sqltime_step(SAT_ID):
    from datetime import datetime
    Time_step = "SELECT TIME from SEC_SATELLITE_LLA where SAT_ID = '%s' order by ID desc limit 2" % (SAT_ID)
    P = pd.read_sql(Time_step, conn).TIME.values
    time_0 = datetime.timestamp(datetime.strptime(str(P[0]).replace('T', ' ').split('.')[0], '%Y-%m-%d %H:%M:%S'))
    time_1 = datetime.timestamp(datetime.strptime(str(P[1]).replace('T', ' ').split('.')[0], '%Y-%m-%d %H:%M:%S'))
    return time_0-time_1


def interpolate(inp, fi):
    i, f = int(fi // 1), fi % 1  # Split floating-point index into whole & fractional parts.
    j = i+1 if f > 0 else i  # Avoid index error.
    return round((1-f) * inp[i] + f * inp[j], 2)


def x_reb(current_dir, dir_name, density):  # numpy array
    inp = np.arange(1, 256).tolist()
    x_set = list(set(density))
    x_set_sort = np.array(sorted(x_set))
    new_len = len(x_set_sort)

    colormap_float = np.zeros((255, 3), np.float)
    for i in range(0, 255, 1):
        colormap_float[i, 0] = cm.jet(i)[0]
        colormap_float[i, 1] = cm.jet(i)[1]
        colormap_float[i, 2] = cm.jet(i)[2]

    delta = (len(inp)-1) / (new_len-1)
    outp = [interpolate(inp, i*delta) for i in range(new_len)]

    f1 = interp1d(inp, colormap_float[:, 0])
    f2 = interp1d(inp, colormap_float[:, 1])
    f3 = interp1d(inp, colormap_float[:, 2])
    color_r = f1(outp)
    color_g = f2(outp)
    color_b = f3(outp)
    color_map = np.zeros((len(outp), 3))
    color_map[:, 0] = color_r 
    color_map[:, 1] = color_g 
    color_map[:, 2] = color_b 
    
    color_new = np.zeros((len(density), 3), np.float)
    for i in range(len(density)):
        ind = np.where(x_set_sort == density[i])
        color_new[i, 0] = round(float(color_r[ind]), 3)
        color_new[i, 1] = round(float(color_g[ind]), 3)
        color_new[i, 2] = round(float(color_b[ind]), 3)

    rgb_table = LinearSegmentedColormap.from_list('sst cmap', color_map)
    plt.scatter(x=np.arange(len(x_set_sort)), y=np.arange(len(x_set_sort)), c=x_set_sort, cmap=rgb_table)
    plt.rcParams["figure.facecolor"] = 'black'
    plt.rcParams["savefig.facecolor"] = 'black'
    clb = plt.colorbar(orientation='horizontal', shrink=1.1)
    clb.ax.tick_params(labelcolor='white')
    clb.ax.tick_params(color='white')
    clb.ax.tick_params(labelsize=8)
    clb.ax.set_title('[Air Density/(kg/m$^{3}$)]', color='white')
    plt.savefig(current_dir+'/'+dir_name+'/test.jpg', dpi=600)
    img = Image.open(current_dir+'/'+dir_name+'/test.jpg')
    # 左边界 #上边界  #右边界  #下边界
    region = img.crop((100, 2060, 3840, 2700))
    region.save(current_dir+'/'+dir_name+'/colorbar.jpg')
    return color_new


# 读取flux文件
def Read_msis_txt(dir_name, file_path):
    txt_all = []
    f = open(file_path+'/'+dir_name+'/'+'msis2.0_simulations.txt')
    file_size = len(f.readlines())
    ff = open(file_path+'/'+dir_name+'/'+'msis2.0_simulations.txt')
    for i in range(file_size):
        txt = float(ff.readline().replace('\n', '').replace(' ', ''))
        txt_all.append(txt)
    txt_all = np.array(txt_all)*1000
    return txt_all


def Cal_traj_den(Time_start, Time_end, SAT_ID, conn):

    # 获取卫星时间分辨率
    time_sql_step = get_sqltime_step(SAT_ID)

    # 读取达梦数据,每隔多长时间(秒)取一个数据？
    time_step = 60  # 单位：s
    if time_sql_step >= time_step:
        Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start, Time_end, SAT_ID)
    else:
        Time_span = "SELECT * from SEC_SATELLITE_LLA where SAT_ID = '%s' and ID MOD %d = 0 and TIME between '%s' and '%s' and SAT_ID = '%s'" % (SAT_ID, int(time_step/time_sql_step), Time_start, Time_end, SAT_ID)
    P = pd.read_sql(Time_span, conn)

    # 读取纬度数据
    LAT = P.LAT.values
    # 读取经度数据
    LON = P.LON.values
    # 读取高度数据
    ALT = P.ALT.values
    # 读取时间
    TIME = P.TIME.values
##########################################################################
    time_new = []
    for i in range(len(TIME)):
        time_new.append(str(TIME[i]).replace('2011-01-01', '2023-02-27'))
    TIME = time_new
##########################################################################
    # 预报：至多往后预报60天。
    # 输出变量：timef107_s, f107_s, timeap_s, ap_s
    LLAtime_terminated = TIME[-1]
    timef107_s, f107_s, timeap_s, ap_s = forecast_length(conn, LLAtime_terminated)
    
    # 读取空间环境数据
    Time_start = TIME[0]  # 读取卫星起始时间
    Time_end = TIME[-1]  # 读取卫星终止时间
    print('卫星起始时间：', Time_start.split('T')[0])
    print('卫星终止时间：', Time_end.split('T')[0])
    
    # 读取地磁AP数据。最终AP为: (1)AP,(2)AP_t
    Time_AP_start = standard_time(timestamp(Time_start.split('T')[0] + ' 12:00:00')-86400*4)
    Time_AP_end = standard_time(timestamp(Time_end.split('T')[0] + ' 12:00:00')+86400)
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

    # 读取太阳F107数据。最终F107值为：(1)F107, (2) F107_t
    Time_F107_start = standard_time(timestamp(Time_start.split('T')[0] + ' 12:00:00')-86400*41)
    Time_F107_end = standard_time(timestamp(Time_end.split('T')[0] + ' 12:00:00')+86400*41)

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

    # ############################空间数据转换#############################
    # 数据转换：
    # Day_of_year:年积日
    # UTC: UTC时间
    # F107_previous_day:F107前一天的值
    # F107_81mean:F107 81天中心平均值
    # AP_matrix:AP矩阵
    Day_of_year = np.zeros(len(TIME))
    UTC = np.zeros(len(TIME))
    F107_previous_day = np.zeros(len(TIME))
    F107_81mean = np.zeros(len(TIME))
    time_std = []
    AP_matrix = np.zeros((len(TIME), 7))
   
    for i in range(len(TIME)):
        t = str(TIME[i])
        time_std.append(t.replace('T', ' ').split('.')[0])

        # Day_of_year
        Day_of_year[i] = datetime.datetime.strptime(str(t).replace('T', ' ').split('.')[0], '%Y-%m-%d %H:%M:%S').strftime('%j')
         
        # UTC
        UTC[i] = int(t[11:13])*3600 + int(t[14:16])*60 + int(t[17:19])
        
        # F107_previous_day
        F107_previous_day[i] = get_targetTime_previous_F107(t, F107, F107_tstamp)

        # F107_81mean
        F107_81mean[i] = get_targetTime_81centremean_F107(t, F107, F107_tstamp)
        
        # AP1~AP7
        # Geomagnetic activity index array:
        # (1) Daily Ap
        # (2) 3 hr ap index for current time
        # (3) 3 hr ap index for 3 hrs before current time
        # (4) 3 hr ap index for 6 hrs before current time
        # (5) 3 hr ap index for 9 hrs before current time
        # (6) Average of eight 3 hr ap indices from 12 to 33 hrs
        #     prior to current time (8 numbers average)
        # (7) Average of eight 3 hr ap indices from 36 to 57 hrs
        #     prior to current time (8 numbers average)
        APmatrix = get_targetTime_Apmatrix(t, AP, AP_tstamp)
        
        AP_matrix[i, 0] = APmatrix[0]
        AP_matrix[i, 1] = APmatrix[1]
        AP_matrix[i, 2] = APmatrix[2]
        AP_matrix[i, 3] = APmatrix[3]
        AP_matrix[i, 4] = APmatrix[4]
        AP_matrix[i, 5] = APmatrix[5]
        AP_matrix[i, 6] = APmatrix[6]

    # df = pd.DataFrame()
    # df['TIME'] = TIME
    # df['LAT'] = LAT
    # df['LON'] = LON
    # df['ALT'] = ALT
    # df['F107_pre'] = F107_previous_day
    # df['F107_81mean'] = F107_81mean
    # df['AP1'] = AP_matrix[:, 0]
    # df['AP2'] = AP_matrix[:, 1]
    # df['AP3'] = AP_matrix[:, 2]
    # df['AP4'] = AP_matrix[:, 3]
    # df['AP5'] = AP_matrix[:, 4]
    # df['AP6'] = AP_matrix[:, 5]
    # df['AP7'] = AP_matrix[:, 6]
    # print(df)
    # 建立模拟结果文件夹(唯一性)
    current_dir = os.path.dirname(os.path.abspath(__file__))
    while True:
        dir_name = str(random.randint(1, 999999))
        if os.path.exists(dir_name) == False:
            os.system('mkdir '+current_dir+'/'+dir_name)
            break
    os.system('cp '+current_dir+'/' + 'msis2.0_test_storm.exe '+current_dir+'/'+dir_name)
    os.system('cp '+current_dir+'/' + 'msis20.parm '+current_dir+'/'+dir_name)

    # 将msis背景场写入Txt文件
    with open(current_dir+'/'+dir_name+'/msis2.0_forcing.txt', 'w', encoding='utf-8') as f:
        for i in range(0, len(TIME)):
            f.write(str(int(Day_of_year[i])))  
            f.write('  ')
            f.write(str(int(UTC[i])))
            f.write('  ')
            f.write(str(float(ALT[i])))
            f.write('  ')
            f.write(str(float(LAT[i])))
            f.write('  ')
            f.write(str(float(LON[i])))
            f.write('  ')
            f.write('99')
            f.write('  ')
            f.write(str(float(F107_81mean[i])))
            f.write('  ')
            f.write(str(float(F107_previous_day[i])))
            f.write('  ')
            f.write(str(float(AP_matrix[i, 0])))
            f.write('  ')
            f.write(str(float(AP_matrix[i, 1])))
            f.write('  ')
            f.write(str(float(AP_matrix[i, 2])))
            f.write('  ')
            f.write(str(float(AP_matrix[i, 3])))
            f.write('  ')
            f.write(str(float(AP_matrix[i, 4])))
            f.write('  ')
            f.write(str(float(AP_matrix[i, 5])))
            f.write('  ')
            f.write(str(float(AP_matrix[i, 6])))
            f.write('\n')

    # 运行FORTRAN seu.exe
    os.system('cd '+current_dir+'/'+dir_name+'; ./msis2.0_test_storm.exe >/dev/null 2>&1') 

    vec_valid_numbers = np.vectorize(valid_numbers)
    density = vec_valid_numbers(Read_msis_txt(dir_name, current_dir))

    switch_color = int(sys.argv[4])

    # 以JSON形式输出结果
    dic = {}
    dic['density'] = density.tolist()
    dic['time'] = time_std
    dic['lat'] = LAT.tolist()
    dic['lon'] = LON.tolist()
    dic['alt'] = ALT.tolist()

    if switch_color == 1:
        color = x_reb(current_dir, dir_name, density)
        dic['r'] = color[:, 0].tolist()
        dic['g'] = color[:, 1].tolist()
        dic['b'] = color[:, 2].tolist()
        dic['colorbar'] = current_dir+'/'+dir_name

    # 删除多余文件
    if switch_color == 1:
        os.system('rm -rf '+current_dir+'/'+dir_name+'/msis2.0_forcing.txt')
        os.system('rm -rf '+current_dir+'/'+dir_name+'/msis2.0_simulations.txt')
        os.system('rm -rf '+current_dir+'/'+dir_name+'/msis20.parm')
        os.system('rm -rf '+current_dir+'/'+dir_name+'/msis2.0_test_storm.exe')
        os.system('rm -rf '+current_dir+'/'+dir_name+'/test.jpg')
    else:
        os.system('rm -rf '+current_dir+'/'+dir_name)

    return (json.dumps(dic))


if __name__ == '__main__':
    # 从数据库读取星下点数据
    # 连接达梦数据库
    iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
    conn = Connect_SQL(iniPath)
    Time_start = str(sys.argv[1]).replace('"', '').replace("'", '')
    Time_end = str(sys.argv[2]).replace('"', '').replace("'", '')
    SAT_ID = str(sys.argv[3]).replace('"', '').replace("'", '')
    print('###')
    print(Cal_traj_den(Time_start, Time_end, SAT_ID, conn))
    print('###')
