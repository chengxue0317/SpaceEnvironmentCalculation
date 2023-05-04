import dmPython
import numpy as np
np.set_printoptions(suppress=True)
import datetime
import random
import sys,os
import pandas as pd
import warnings
warnings.filterwarnings("ignore")
from pathlib import Path
sys.path.append(str(Path(__file__).resolve().parents[1]))
import AP8AE8.ap8ae8flux as ae
import df
from datetime import datetime
import time
from matplotlib import pyplot as plt
import math
import json


# 深层充放电效应
# 美国空军实验室的预报和警报高能电子引发深层充电效应的判断为：
# 卫星轨道上能量E>2MeV的高能电子的通量满足：
# >3*10e8 （cm2.sr.day）持续3天
# >1*10e9 （cm2.sr.day）持续1天

# 连接达梦数据库
conn = dmPython.connect(user='SDC', password='sdc123456', server='219.145.62.54',port=15236, autoCommit=True)
cursor = conn.cursor()


# 选择时间范围
# 从数据库读取星下点数据
Time_start = str(sys.argv[1])
Time_end = str(sys.argv[2])
SAT_ID = str(sys.argv[3])

# 判断所选卫星是否在数据库内
SAT_name = "select SAT_ID from SEC_SATELLITE_LLA where TIME between '%s' and '%s'"%(Time_start,Time_end)
P = pd.read_sql(SAT_name,conn)
P = str(P.SAT_ID.values).replace(' ','')
# 如果不在，删除文件夹并退出
k = SAT_ID in P
if k == False:
    sys.exit()

# 定义真实读取的星下点数据，比显示数据多显示过去若干天。
if (int(Time_start.split('-')[2])<4) & (int(Time_start.split('-')[1])>1):
    Time_real_start = str(Time_start.split('-')[0])+'-'+str(int(Time_start.split('-')[1])-1)+'-'+'25'   
elif (int(Time_start.split('-')[2])>=4):
    Time_real_start = str(Time_start.split('-')[0])+'-'+str(Time_start.split('-')[1])+ str(int(Time_start.split('-')[1])-3)
elif (int(Time_start.split('-')[2])<4) & (int(Time_start.split('-')[1])==1):
    Time_real_start = str(int(Time_start.split('-')[0])-1)+'-'+'12-25'

# 读取显示数据，显示数据比真实读取的星下点数据少若干天。
Time_span = "select TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start,Time_end,SAT_ID)
P = pd.read_sql(Time_span, conn)
TIME = P.TIME.values
time_dis=[]
TIME = P.TIME.values
for i in range(len(TIME)):
    time_dis.append(str(TIME[i]).replace('T',' ').split('.')[0])
time_dis = np.array(time_dis)

########### 读取真实数据
Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_real_start,Time_end,SAT_ID)
P = pd.read_sql(Time_span, conn)
# 读取星下点纬度数据
LAT = P.LAT.values
# 读取星下点经度数据
LON = P.LON.values
# 读取星下点高度数据
ALT = P.ALT.values
# 读取星下点时间
time_lla=[]
TIME = P.TIME.values
for i in range(len(TIME)):
    time_lla.append(str(TIME[i]).replace('T',' ').split('.')[0])
time_lla = np.array(time_lla)

# 读取F10.7数据
Time_span = "select F107,TIME from SEC_F107_FLUX where TIME between '%s' and '%s'" % (Time_real_start,Time_end)
P = pd.read_sql(Time_span, conn)
F107 = P.F107.values
TIME = P.TIME.values
if len(F107)==0:
    sys.exit()
# 读取F107时间
time_f107=[]
TIME = P.TIME.values
for i in range(len(TIME)):
    time_f107.append(str(TIME[i]).replace('T',' ').split('.')[0].split(' ')[0])

# 将F107时间转换至星下点时间
f107_lla = []
time_f107 = np.array(time_f107)
for i in range(len(time_lla)):
    t = time_lla[i].split(' ')[0]
    ind = np.where(time_f107 == t)[0]
    f107_lla.append(F107[ind][0])




# 判断f107_lla是否大于100，用以选择不同的ae8模型
standard_f107 = 100
ind = np.where(np.array(f107_lla)>= standard_f107)
ind = ind[:][0]

# 经度、纬度、高度、时间变量
# 太阳活动高年
lat_max = LAT[ind].tolist()
lon_max = LON[ind].tolist()
alt_max = ALT[ind].tolist()
time_lla_max = time_lla[ind].tolist()

# 太阳活动低年
ind = np.where(np.array(f107_lla)< standard_f107)
ind = ind[:][0]
lat_min = LAT[ind].tolist()
lon_min = LON[ind].tolist()
alt_min = ALT[ind].tolist()
time_lla_min = time_lla[ind].tolist()

# 计算太阳低年电子通量
dir_name_min = []
if len(lat_min)>0:
    dir_name_min,filedir_min = ae.ap8ae8(time_lla_min, alt_min, lon_min, lat_min, whatf='range', whichm='ae8min', plot=False)

# 计算太阳高年电子通量
dir_name_max = []
if len(lat_max)>0:
    dir_name_max,filedir_max = ae.ap8ae8(time_lla_max, alt_max, lon_max, lat_max, whatf='range', whichm='ae8min', plot=False)

# 读取结果
if len(dir_name_min)>0:
    # 读取模拟结果
    f = open(filedir_min+'/'+dir_name_min+'/flux.txt')
    lines = f.readlines()
    E_min = []
    for line in lines:
        E_min.append(line.split()[-1])

if len(dir_name_max)>0:
    # 读取模拟结果
    f = open(filedir_max+'/'+dir_name_max+'/flux.txt')
    lines = f.readlines()
    E_max = []
    for line in lines:
        E_max.append(line.split()[-1])

# 合并太阳高年太阳低年的结果
E = np.zeros(len(time_lla))-9999
ind_t = np.zeros(len(time_lla))-9999
for i in range(len(time_lla)):
    ind = np.where(np.array(time_lla_min) == time_lla[i])
    if len(ind[:][0])>0:
        E[i] = E_min[int(ind[:][0])]
    else:
        ind = np.where(np.array(time_lla_max) == time_lla[i])
        E[i] = E_max[int(ind[:][0])]

# 剔除异常值
ind = np.where(E<0)
E[ind] = 0

# 卫星轨道上能量E>2MeV的高能电子的通量满足：
# >3*10e8 （cm2.sr.day）持续3天
# >1*10e9 （cm2.sr.day）持续1天

# 将两组时间转换成时间戳
time_lla_int = np.zeros(len(time_lla))
for i in range(len(time_lla)):
    time_lla_int[i] = time.mktime(time.strptime(time_lla[i],'%Y-%m-%d %H:%M:%S'))

E_3day_mean = np.zeros(len(time_dis))
E_1day_mean = np.zeros(len(time_dis))
for i in range(len(time_dis)):
    t = time.mktime(time.strptime(time_dis[i],'%Y-%m-%d %H:%M:%S'))
    delta = t-time_lla_int
    delta = np.array(delta)
    # 计算持续3天的高能电子结果
    ind = np.where((delta<=3*24*3600) & (delta>0))
    E_3day_mean[i] = np.mean(E[ind])
    # 计算持续1天的高能电子结果
    ind = np.where((delta<=1*24*3600) & (delta>0))
    E_1day_mean[i] = np.mean(E[ind])

# 单位转换
E_3day_mean = list(map(int,E_3day_mean*24*3600/(4*math.pi)))
E_1day_mean = list(map(int,E_1day_mean*24*3600/(4*math.pi)))


# 以JSON形式显示结果
dic = {}
dic['time'] = time_dis.tolist()
dic['three_day_flux'] = E_3day_mean
dic['one_day_flux'] = E_1day_mean
dic['three_day_standard'] = 3e8
dic['one_day_standard'] = 1e9

print('###')
print(json.dumps(dic))
print('###')

# 删除相关文件夹
if len(dir_name_max)>0:
    os.system('rm -rf '+filedir_max+'/'+dir_name_max)
if len(dir_name_min)>0:
    os.system('rm -rf '+filedir_min+'/'+dir_name_min)

t = np.arange(len(E_1day_mean))
line_3day = np.zeros(len(E_1day_mean))+3e8
line_1day = np.zeros(len(E_1day_mean))+1e9
plt.plot(t,E_1day_mean,label= '1-day Flux',color='red')
plt.plot(t,E_3day_mean,label = '3-day Flux',color='blue')
plt.plot(t,line_3day,color='blue',linestyle='--')
plt.plot(t,line_1day,color='red',linestyle='--')
plt.yscale('log')
plt.legend()
plt.xlabel('Time')
plt.ylabel('E (particles/day*cm2*sr)')
plt.savefig('test.jpg',dpi=300)





