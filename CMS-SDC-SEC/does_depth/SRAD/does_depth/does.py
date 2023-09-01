import os
import sys
import dmPython
import warnings
import numpy as np
import pandas as pd
import time
import datetime
import random 
import datetime as dt
import multiprocessing
import f_Cal_2ddoes as Cal_2ddoes
from scipy.interpolate import interp1d
import json
from pathlib import Path
import subpoint as Cal_subpoint
sys.path.append(str(Path(__file__).resolve().parents[1]))
import AP8AE8.ap8ae8 as ae
np.set_printoptions(threshold=sys.maxsize)
warnings.filterwarnings("ignore")
'''
剂量深度曲线功能：
横坐标：深度(mm)
纵坐标：辐射剂量(Rad)
'''


# 计算时间天数差
def getDays(day1, day2):
    # 获取需要计算的时间戳
    time_array = time.strptime(day1, '%Y-%m-%d')
    timestamp = int(time.mktime(time_array))
    # 获取今天的时间戳
    time_array2 = time.strptime(day2, '%Y-%m-%d')
    timestamp2 = int(time.mktime(time_array2))
    if timestamp2 < timestamp:
        timestamp2, timestamp = timestamp, timestamp2
    # 时间戳相减，然后算出天数
    day = int((timestamp2 - timestamp)/(24 * 60 * 60))
    if day < 1:
        day = 1
    return day


def timestr_to_timestamp(timestr):
    timestr1, timestr2 = timestr.split('.')
    struct_time = time.strptime(timestr1, '%Y-%m-%d %H:%M:%S')
    seconds = time.mktime(struct_time)
    millseconds = float("0." + timestr2)
    return seconds + millseconds


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
    cursor = conn.cursor()
    return cursor, conn


# 获取数据库对应卫星星下点的时间分辨率
def get_sqltime_step(SAT_ID):
    from datetime import datetime
    Time_step = "SELECT TIME from SEC_SATELLITE_LLA where SAT_ID = '%s' order by ID desc limit 2" % (SAT_ID)
    P = pd.read_sql(Time_step, conn).TIME.values
    time_0 = datetime.timestamp(datetime.strptime(str(P[0]).replace('T', ' ').split('.')[0], '%Y-%m-%d %H:%M:%S'))
    time_1 = datetime.timestamp(datetime.strptime(str(P[1]).replace('T', ' ').split('.')[0], '%Y-%m-%d %H:%M:%S'))
    return time_0-time_1


def Cal_does(time1d, ALT, LON, LAT, material, shield_type, solar_scenario):
    # 建立模拟结果文件夹(唯一性)
    current_dir = os.path.dirname(os.path.abspath(__file__))
    while True:
        dir_name = str(random.randint(1, 999999))
        if os.path.exists(dir_name) == False:
            os.system('mkdir '+current_dir+'/'+dir_name)
            break
    os.system('cp '+current_dir+'/' + 'ELBRBAS2.DAT '+current_dir+'/'+dir_name)
    os.system('cp '+current_dir+'/' + 'PROTBAS2.DAT '+current_dir+'/'+dir_name)
    os.system('cp '+current_dir+'/' + 'seu.exe ' + current_dir+'/'+dir_name)

    if solar_scenario == 1:
        dir_name_pflux, filedir_pflux = ae.ap8ae8(time1d, ALT, LON, LAT, whatf='differential', whichm='ap8min')
        dir_name_eflux, filedir_eflux = ae.ap8ae8(time1d, ALT, LON, LAT, whatf='differential', whichm='ae8min')
    else:
        dir_name_pflux, filedir_pflux = ae.ap8ae8(time1d, ALT, LON, LAT, whatf='differential', whichm='ap8max')
        dir_name_eflux, filedir_eflux = ae.ap8ae8(time1d, ALT, LON, LAT, whatf='differential', whichm='ae8max')

    # print('ap8ae8 modelling finished')
    sptrum_point = 12
    sptrum_E = [0.1, 0.5, 1, 3, 5, 10, 20, 40, 80, 120, 160, 200]
    z_mm, does_p = Cal_2ddoes.Cal_2ddoes(time1d, current_dir, dir_name, 'p', dir_name_pflux, filedir_pflux, sptrum_point, sptrum_E, material, shield_type)
    sptrum_point = 7
    sptrum_E = [0.05, 0.1, 0.15, 0.35, 0.65, 1.2, 2.0]
    z_mm, does_e = Cal_2ddoes.Cal_2ddoes(time1d, current_dir, dir_name, 'e', dir_name_eflux, filedir_eflux, sptrum_point, sptrum_E, material, shield_type)
    
    # 删除文件夹
    os.system('rm -rf '+current_dir+'/'+dir_name)
    os.system('rm -rf '+filedir_pflux+'/'+dir_name_pflux)
    os.system('rm -rf '+filedir_eflux+'/'+dir_name_eflux)
    
    return z_mm, (does_e+does_p).astype(np.int)


start_time = time.time()    # 程序开始时间
model_points = 20000

# 连接达梦数据库
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
cursor, conn = Connect_SQL(iniPath)

# 选取时间和卫星名称
real_or_model = int(sys.argv[1])
if real_or_model == 1:
    Time_start = str(sys.argv[2]).replace('"', '')
    Time_end = str(sys.argv[3]).replace('"', '')
    SAT_ID = str(sys.argv[4]).replace("'", '')
    # 选择靶材料
    material = int(sys.argv[5])  # 范围[1,11]
    # 选择屏蔽方式
    shield_type = int(sys.argv[6])  # 范围[1,3]
    # 选择太阳活动场景
    solar_scenario = int(sys.argv[7])  # 1:solar min, 2:solar max
else:
    # 半长轴
    sat_a = float(sys.argv[2])
    # 倾角
    sat_i = float(sys.argv[3])
    # 偏心率
    sat_e = float(sys.argv[4])
    # 升交点经度
    sat_Omega = float(sys.argv[5])
    # 近拱点角距
    sat_omega = float(sys.argv[6])
    # 空间目标飞行天数
    sat_t = int(sys.argv[7])
    # 选择靶材料
    material = int(sys.argv[8])  # 范围[1,11]
    # 选择屏蔽方式
    shield_type = int(sys.argv[9])  # 范围[1,3]
    # 选择太阳活动场景
    solar_scenario = int(sys.argv[10])  # 1:solar min, 2:solar max

if real_or_model == 1:
    # 获取卫星时间分辨率
    time_sql_step = get_sqltime_step(SAT_ID)
    # print(time_sql_step)

    # 读取达梦数据,每隔多长时间(秒)取一个数据？
    time_step = 1  # 单位：s
    if time_sql_step >= time_step:
        Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start, Time_end, SAT_ID)
    else:
        Time_span = "SELECT * from SEC_SATELLITE_LLA where SAT_ID = '%s' and ID MOD %d = 0 and TIME between '%s' and '%s' and SAT_ID = '%s'" % (SAT_ID, int(time_step/time_sql_step), Time_start, Time_end, SAT_ID)
    P = pd.read_sql(Time_span, conn)

    # 读取时间
    TIME = P.TIME.values
    time1d = []
    for i in range(len(TIME)):
        time1d.append(str(TIME[i]).replace('T', ' ').split('.')[0])

    # Ap8max模拟(高能质子和高能电子)
    LAT = P.LAT.values
    LON = P.LON.values
    ALT = P.ALT.values
    if len(LAT) > model_points:
        step = int(len(LAT)/model_points)
        LAT_new, LON_new, ALT_new, time1d_new = [], [], [], []
        for i in range(0, len(LAT), step):
            LAT_new.append(LAT[i])
            LON_new.append(LON[i])
            ALT_new.append(ALT[i])
            time1d_new.append(time1d[i])
        LAT = LAT_new
        LON = LON_new
        ALT = ALT_new
        time1d = time1d_new
else:
    point_step = int((sat_t*86400)/model_points)
    sum_point = int(sat_t*86400/point_step)
    LAT, LON, ALT = Cal_subpoint.Cal_subpoint(sat_a, sat_i, sat_e, sat_Omega, sat_omega, sum_point, point_step)
    
    # 生成时间区间，以默认时间2000年1月1日为起点
    time1d = []
    ini_time = timestr_to_timestamp('2000-01-01 00:00:00.000')
    for i in range(len(LAT)):
        time1d.append(dt.datetime.fromtimestamp(ini_time + point_step*i).strftime("%Y-%m-%d %H:%M:%S"))

# print('sum point:', len(LAT))
if len(LAT) < 10000:
    z_mm, does = Cal_does(time1d, ALT, LON, LAT, material, shield_type, solar_scenario)
else:
    step = int(len(LAT)/6)
    m1, n1 = 0, step
    m2, n2 = step, step*2
    m3, n3 = step*2, step*3
    m4, n4 = step*3, step*4
    m5, n5 = step*4, step*5
    m6, n6 = step*5, len(LAT)+1
    # print("温馨提示：本机为", os.cpu_count(), "核CPU") 
    
    final_res = []
    pool = multiprocessing.Pool(processes=40)
    
    res1 = pool.apply_async(Cal_does, args=(time1d[m1: n1], ALT[m1: n1], LON[m1: n1], LAT[m1: n1], material, shield_type, solar_scenario))
    res2 = pool.apply_async(Cal_does, args=(time1d[m2: n2], ALT[m2: n2], LON[m2: n2], LAT[m2: n2], material, shield_type, solar_scenario))
    res3 = pool.apply_async(Cal_does, args=(time1d[m3: n3], ALT[m3: n3], LON[m3: n3], LAT[m3: n3], material, shield_type, solar_scenario))
    res4 = pool.apply_async(Cal_does, args=(time1d[m4: n4], ALT[m4: n4], LON[m4: n4], LAT[m4: n4], material, shield_type, solar_scenario))
    res5 = pool.apply_async(Cal_does, args=(time1d[m5: n5], ALT[m5: n5], LON[m5: n5], LAT[m5: n5], material, shield_type, solar_scenario))
    res6 = pool.apply_async(Cal_does, args=(time1d[m6: n6], ALT[m6: n6], LON[m6: n6], LAT[m6: n6], material, shield_type, solar_scenario))

    pool.close()
    pool.join()

    z_mm = res1.get()[0]
    does = res1.get()[1]+res2.get()[1]++res3.get()[1]++res4.get()[1]++res5.get()[1]++res6.get()[1]

# 剔除异常数据
index = [15, 16, 17, 18]
z_mm = np.delete(z_mm, index)
does = np.delete(does, index)
# 规整插值
f = interp1d(z_mm, does, 'cubic')
new_x = np.arange(0.01, 10, 0.01)
new_x = np.around(new_x, decimals=2)
does = f(new_x).astype(np.int)

if real_or_model == 1:
    # 外推未来辐射剂量
    time_min = datetime.datetime.strptime(time1d[0], '%Y-%m-%d %H:%M:%S')
    time_max = datetime.datetime.strptime(time1d[-1], '%Y-%m-%d %H:%M:%S')
    sum_day = (time_max - time_min).total_seconds()/86400
    # does_1 = (does/sum_day*365).astype(np.int)
    does_2 = (does/sum_day*365*2).astype(np.int)
    # does_3 = (does/sum_day*365*3).astype(np.int)
    does_4 = (does/sum_day*365*4).astype(np.int)
    # does_5 = (does/sum_day*365*5).astype(np.int)
    does_6 = (does/sum_day*365*6).astype(np.int)
    # does_7 = (does/sum_day*365*7).astype(np.int)
    # does_8 = (does/sum_day*365*8).astype(np.int)
    # does_9 = (does/sum_day*365*9).astype(np.int)
    # does_10 = (does/sum_day*365*10).astype(np.int)


# 以JSON形式输出结果
dic = {}
dic['depth'] = new_x.tolist()
dic['does'] = does.tolist()
# if real_or_model == 1:
    # dic['does1'] = does_1.tolist()
    # dic['does2'] = does_2.tolist()
    # dic['does3'] = does_3.tolist()
    # dic['does4'] = does_4.tolist()
    # dic['does5'] = does_5.tolist()
    # dic['does6'] = does_6.tolist()
    # dic['does7'] = does_7.tolist()
    # dic['does8'] = does_8.tolist()
    # dic['does9'] = does_9.tolist()
    # dic['does10'] = does_10.tolist()

print('###')
print(json.dumps(dic))
print('###')
# print('###########')
# end_time = time.time()
# print('程序运行时间为：', end_time - start_time, '秒')

# plt.plot(new_x, dic['does'], label='selected')
# plt.xlabel('mm')
# plt.ylabel('Does in Al(rad)')
# plt.plot(new_x, dic['does2'], label='future 2 year')
# plt.plot(new_x, dic['does4'], label='future 4 year')
# plt.plot(new_x, dic['does6'], label='future 6 year')
# plt.plot(new_x, dic['does8'], label='future 8 year')
# plt.plot(new_x, dic['does10'], label='future 10 year')
# plt.xscale('log')
# plt.yscale('log')
# plt.xlim((0,15))
# plt.legend()
# plt.show()
# plt.savefig('test.jpg', dpi=300)
