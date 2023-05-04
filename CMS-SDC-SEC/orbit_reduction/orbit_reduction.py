import dmPython
import sys
import numpy as np
import random
import os
import pandas as pd
import json
import functools
import warnings
warnings.filterwarnings("ignore")

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
    return cursor,conn

# 将list切片
def return_cut_list(lst):
        rt = []
        n = 0
        for i in range(len(lst)-1):
                if lst[i] != lst[i+1]:
                        rt.append(lst[n:i+1])
                        n = i+1
        rt.append(lst[n:])
        return rt

# 轨道半长轴衰减

# 读取太阳F107数据
# 连接达梦数据库
Time_start = str(sys.argv[1])
Time_end = str(sys.argv[2])
# 连接达梦数据库
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
cursor,conn = Connect_SQL(iniPath)
Time_span = "select F107,TIME from SEC_F107_FLUX where TIME between '%s' and '%s'" % (Time_start,Time_end)
P = pd.read_sql(Time_span, conn)
F107 = P.F107.values
TIME = P.TIME.values


T = []
for i in range (len(TIME)):
    T.append(str(TIME[i]).replace('T',' ').split('.')[0])

T_orbit = T

if len(F107)==0:
    sys.exit()
orbit = 2*F107-19
orbit_daymean = []
for i in range(len(orbit)):
    orbit_daymean.append(round(orbit[i],2))

# 轨道高度
# 从数据库读取星下点数据
SAT_ID = str(sys.argv[3])
Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start,Time_end,SAT_ID)
P = pd.read_sql(Time_span, conn)
# 读取纬度数据
LAT = P.LAT.values
# 读取经度数据
LON = P.LON.values
# 读取高度数据
ALT = P.ALT.values
# 读取时间
TIME = P.TIME.values

#############################################
import time
sys_start_time = time.time()
#############################################

# 输出天平均高度
T = []
for i in range (len(TIME)):
    T.append(str(TIME[i]).replace('T',' ').split('.')[0].split(' ')[0])
T_s = return_cut_list(T) 
s = 0

TIME_daymean = []
while True:
    try:
        TIME_daymean.append(T_s[s][0])
        s = s+1
    except:
        break
T = np.array(T)
ALT_daymean = []
for i in TIME_daymean:
    t=np.where(T ==i)[0]
    ALT_daymean.append(round(np.nanmean(ALT[t]),2))

# 输出月平均高度
T = []
for i in range (len(TIME)):
    T.append(str(TIME[i]).replace('T',' ').split('.')[0].split(' ')[0].split('-')[0]+'-'+str(TIME[i]).replace('T',' ').split('.')[0].split(' ')[0].split('-')[1])
T_s = return_cut_list(T)
s = 0
TIME_monthmean = []
while True:
    try:
        TIME_monthmean.append(T_s[s][0])
        s = s+1
    except:
        break

T = np.array(T)
ALT_monthmean = []
for i in TIME_monthmean:
    t=np.where(T ==i)[0]
    ALT_monthmean.append(round(np.nanmean(ALT[t]),2))

if len(TIME_daymean)==0:
    sys.exit()

# 以JSON形式显示结果
dic = {}
dic['day_alt_time'] = TIME_daymean
dic['month_alt_time'] = TIME_monthmean
dic['daily semi-major time'] = T_orbit
dic['daily semi-major axis decrease'] = orbit_daymean
dic['daily alt change'] = ALT_daymean
dic['monthly alt change'] = ALT_monthmean
print('###')
print(json.dumps(dic))
print('###')

