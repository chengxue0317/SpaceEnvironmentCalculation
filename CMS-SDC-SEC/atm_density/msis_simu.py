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


def timestamp(shijian):
    s_t = time.strptime(shijian, "%Y-%m-%d %H:%M:%S")
    mkt = int(time.mktime(s_t))
    return (mkt)


def shijian(timeStamp):
    timeArray = time.localtime(timeStamp)
    otherStyleTime = time.strftime("%Y-%m-%d %H:%M:%S", timeArray)
    return otherStyleTime


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


def Cal_traj_den(Time_start, Time_end, SAT_ID):
    
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

    # 读取地磁AP数据
    Time_AP_start = shijian(timestamp(Time_start.split(' ')[0] + ' 12:00:00')-86400*3)
    Time_AP_end = shijian(timestamp(Time_end.split(' ')[0] + ' 12:00:00')+86400)
    Time_span = "select AP,TIME from SEC_AP_INDEX where TIME between '%s' and '%s' " % (Time_AP_start, Time_AP_end)
    P = pd.read_sql(Time_span, conn)
    AP = P.AP.values.astype(np.int)
    t = P.TIME.values
    TIME_AP = np.zeros(len(t))
    for i in range(len(t)):
        TIME_AP[i] = timestamp(str(t[i]).replace('T', ' ').split('.')[0])

    # 读取太阳F107数据
    Time_F107_start = shijian(timestamp(Time_start.split(' ')[0] + ' 12:00:00')-86400*41)
    Time_F107_end = shijian(timestamp(Time_end.split(' ')[0] + ' 12:00:00')+86400*41)
    Time_span = "select F107,TIME from SEC_F107_FLUX where TIME between '%s' and '%s' " % (Time_F107_start, Time_F107_end)
    P = pd.read_sql(Time_span, conn)
    F107 = P.F107.values
    t = P.TIME.values
    TIME_F107 = np.zeros(len(t))
    for i in range(len(t)):
        TIME_F107[i] = timestamp(str(t[i]).split('T')[0] + ' 12:00:00')

    # 数据转换：day_of_year; utc, f107_previous_day; f107_81mean; AP1~AP7
    day_of_year = np.zeros(len(TIME))
    utc = np.zeros(len(TIME))
    f107_previous_day = np.zeros(len(TIME))
    f107_81mean = np.zeros(len(TIME))
    time_std = []
    AP1_7 = np.zeros((len(TIME), 7))
    t_std = np.array([1, 4, 7, 10, 13, 16, 19, 22])
    for i in range(len(TIME)):
        t = str(TIME[i])
        time_std.append(t.replace('T', ' ').split('.')[0])
        # day_of_year
        day_of_year[i] = datetime.datetime.strptime(str(t).replace('T', ' ').split('.')[0], '%Y-%m-%d %H:%M:%S').strftime('%j')
         
        # utc
        utc[i] = int(t[11:13])*3600 + int(t[14:16])*60 + int(t[17:19])
        
        # f107_previous_day
        time_str = t.split('T')[0] + ' 12:00:00'
        time_f107_previous_day = timestamp(time_str)-86400
        ind = np.where(TIME_F107 == time_f107_previous_day)
        f107_previous_day[i] = F107[ind]
        # f107_81mean
        f107_81_start = timestamp(time_str)-86400*40
        f107_81_end = timestamp(time_str)+86400*40
        ind = np.where((TIME_F107 >= f107_81_start) & (TIME_F107 <= f107_81_end))
        f107_81mean[i] = round(np.nanmean(F107[ind]))
        
        # AP1~AP7
        ap_time = t.split(':')[0].replace('T', ' ')+':15:00'
        t = int(ap_time[11:13])
        ind = np.where(abs(t_std-t) == min(abs(t_std-t)))
        k = str(t_std[ind][0])
        if len(k) < 2:
            k = '0'+str(t_std[ind][0])
        ap_time_end = replace_char(ap_time, k[0], 11)
        ap_time_end = replace_char(ap_time_end, k[1], 12)
        ap_time_start = timestamp(ap_time_end)-57*3600
        ap_time_end = timestamp(ap_time_end)

        ind = np.where((TIME_AP >= ap_time_start) & (TIME_AP <= ap_time_end))
        AP1_7[i, 0] = AP[ind][19]
        AP1_7[i, 1] = AP[ind][19]
        AP1_7[i, 2] = AP[ind][18]
        AP1_7[i, 3] = AP[ind][17]
        AP1_7[i, 4] = AP[ind][16]
        AP1_7[i, 5] = round(np.nanmean(AP[ind][8:16]))
        AP1_7[i, 6] = round(np.nanmean(AP[ind][0:8]))
        
    # df = pd.DataFrame()
    # df['TIME'] = TIME
    # df['LAT'] = LAT
    # df['LON'] = LON
    # df['ALT'] = ALT
    # df['F107_pre'] = f107_previous_day
    # df['F107_81mean'] = f107_81mean
    # df['AP1'] = AP1_7[:,0]
    # df['AP2'] = AP1_7[:,1]
    # df['AP3'] = AP1_7[:,2]
    # df['AP4'] = AP1_7[:,3]
    # df['AP5'] = AP1_7[:,4]
    # df['AP6'] = AP1_7[:,5]
    # df['AP7'] = AP1_7[:,6]

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
            f.write(str(int(day_of_year[i])))  
            f.write('  ')
            f.write(str(int(utc[i])))
            f.write('  ')
            f.write(str(float(ALT[i])))
            f.write('  ')
            f.write(str(float(LAT[i])))
            f.write('  ')
            f.write(str(float(LON[i])))
            f.write('  ')
            f.write('99')
            f.write('  ')
            f.write(str(float(f107_81mean[i])))
            f.write('  ')
            f.write(str(float(f107_previous_day[i])))
            f.write('  ')
            f.write(str(float(AP1_7[i, 0])))
            f.write('  ')
            f.write(str(float(AP1_7[i, 1])))
            f.write('  ')
            f.write(str(float(AP1_7[i, 2])))
            f.write('  ')
            f.write(str(float(AP1_7[i, 3])))
            f.write('  ')
            f.write(str(float(AP1_7[i, 4])))
            f.write('  ')
            f.write(str(float(AP1_7[i, 5])))
            f.write('  ')
            f.write(str(float(AP1_7[i, 6])))
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


# 从数据库读取星下点数据
# 连接达梦数据库
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
conn = Connect_SQL(iniPath)
Time_start = str(sys.argv[1]).replace('"', '').replace("'", '')
Time_end = str(sys.argv[2]).replace('"', '').replace("'", '')
SAT_ID = str(sys.argv[3]).replace('"', '').replace("'", '')
print('###')
print(Cal_traj_den(Time_start, Time_end, SAT_ID))
print('###')
