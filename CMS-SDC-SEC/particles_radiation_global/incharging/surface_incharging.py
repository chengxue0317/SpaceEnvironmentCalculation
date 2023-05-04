import dmPython
import numpy as np
import iri90
import datetime
import random
import sys,os
import pandas as pd
import warnings
warnings.filterwarnings("ignore")
import matplotlib.pyplot as plt
from charging3 import select_para,Calculate_integ_J,Calculate_D,Calculate_J_se,Calculate_J_si,Calculate_J_be,Calculate_J_ph,Calculate_J_free,Calculate_J_surface,Calculate_U
import iri90

# 卫星充放电效应算法

'''
根据型号工程的特点和卫星材料状态，将卫星表面划分为若干典型区域，根据每个
典型的材料和接地状况，针对性的分别采用不同的充电模型，进行卫星表面充电
电位的最坏情况分析。
'''

# 卫星表面表面充电分析
# 主要分析表面绝缘材料任意一点与接地点之间的的电位差，绝缘材料的接地方式
# 将决定该材料表面充放电应用的分析模型。绝缘材料的接地方式主要包括长条材料
# 端点接地和薄膜材料背面接地，不同接地方式将选用不同的分析模型。

# 长条一端接地模型
# 适用于：多层隔热材料、氟46（F46）类材料和光学太阳反射镜
# 变量 V：材料对接地点的最高电压
# 变量 J: 空间充电电流密度
# 变量 F: 材料表面电阻率
# 变量 D: 材料宽度
# 变量 L: 材料长度

def Cal_surface_charge_type1(J,F,L):
	V = J*F*L*L/2
	return V

# 薄膜材料背面接地模型
# 适用于：防静电白漆类材料
# 变量 V:表面材料任一点到接地点之间的电位差；
# 变量 J:空间充电电流密度；
# 变量 F:材料的电阻率
# 变量 T:材料厚度

def Cal_surface_charge_type2(J,F,T):
	V = J*F*T
	return V


# 转换为一年中的某一天
def cal_jday(yyyymmdd):
    dt = datetime.datetime.strptime(str(yyyymmdd), '%Y%m%d')
    tt = dt.timetuple()
    Jyear = tt.tm_year
    Jday = tt.tm_yday
    return Jday


# 连接达梦数据库
conn = dmPython.connect(user='SDC', password='sdc123456', server='219.145.62.54',port=15236, autoCommit=True)
cursor = conn.cursor()

# 从数据库读取星下点数据
Time_start = str(sys.argv[1])
Time_end = str(sys.argv[2])
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

# 读取地磁AP数据
Time_Ap_start = str(int(Time_start.split('-')[0])-1)+'-'+str(Time_start.split('-')[1])+'-'+str(Time_start.split('-')[2])
Time_Ap_end = str(int(Time_end.split('-')[0])+1)+'-'+str(Time_end.split('-')[1])+'-'+str(Time_end.split('-')[2])
Time_span = "select AP,TIME from SEC_AP_INDEX where TIME between '%s' and '%s' " % (Time_Ap_start,Time_Ap_end)
P = pd.read_sql(Time_span, conn)
AP = P.AP.values
TIME_AP = P.TIME.values

# 读取太阳F107数据
Time_F107_start = str(int(Time_start.split('-')[0])-1)+'-'+str(Time_start.split('-')[1])+'-'+str(Time_start.split('-')[2])
Time_F107_end = str(int(Time_end.split('-')[0])+1)+'-'+str(Time_end.split('-')[1])+'-'+str(Time_end.split('-')[2])
Time_span = "select F107,TIME from SEC_F107_FLUX where TIME between '%s' and '%s' " % (Time_F107_start,Time_F107_end)
P = pd.read_sql(Time_span, conn)
F107 = P.F107.values
TIME_F107 = P.TIME.values

# LLA时间数据转换
UTSEC_lla = np.zeros(len(TIME))
day_sum_lla = np.zeros(len(TIME))
year_lla = np.zeros(len(TIME))
month_lla = np.zeros(len(TIME))
day_lla = np.zeros(len(TIME))
ttime_lla = np.zeros(len(TIME))
hour_lla = np.zeros(len(TIME))
minu_lla = np.zeros(len(TIME))
sec_lla = np.zeros(len(TIME))

for i in range (0,len(TIME)):
    t_ymd = str(TIME[i]).split('T')[0]
    t_hms = str(TIME[i]).split('T')[1]
    year = int(t_ymd.split('-')[0])
    month = int(t_ymd.split('-')[1])
    day = int(t_ymd.split('-')[2])
    hour = int(t_hms.split(':')[0])
    minu = int(t_hms.split(':')[1])
    sec = int(t_hms.split(':')[2].split('.')[0])
    
    UTSEC_lla[i] = int(hour)*3600+int(minu)*60+int(sec)*1
    year_lla[i] = year
    month_lla[i] = month
    day_lla[i] = day
    hour_lla[i] = hour
    minu_lla[i] = minu
    sec_lla[i] = sec

    # 寻找日期最大值和最小值
    # 一年中的多少天
    if (month<10)&(day<10) :
        day_sum_lla[i] = cal_jday(str(year)+'0'+str(month)+'0'+str(day))

    elif (month<10)&(day>=10):
        day_sum_lla[i] = cal_jday(str(year)+'0'+str(month)+str(day))

    elif (month>=10)&(day<10):
        day_sum_lla[i] = cal_jday(str(year)+str(month)+'0'+str(day))

    else:
        day_sum_lla[i] = cal_jday(str(year)+str(month)+str(day))

# Ap时间数据转换
year_ap = np.zeros(len(TIME_AP))
month_ap = np.zeros(len(TIME_AP))
day_ap = np.zeros(len(TIME_AP))
for i in range(0,len(TIME_AP)):
    t_ymd = str(TIME_AP[i]).split('T')[0]
    t_hms = str(TIME_AP[i]).split('T')[1]
    year_ap[i] = int(t_ymd.split('-')[0])
    month_ap[i] = int(t_ymd.split('-')[1])
    day_ap[i] = int(t_ymd.split('-')[2])


# 在AP数据里寻找轨道数据对应的Ap
missing_value = -99999
AP_LLA = np.zeros(len(TIME))+missing_value
for i in range (0,len(TIME)):
    ind = np.where((year_ap==year_lla[i]) & (month_ap==month_lla[i]) & (day_ap==day_lla[i]))
    if (hour_lla[i]>=0) & (hour_lla[i]<3):
        ind = np.array(ind[0][0])
    elif (hour_lla[i]>=3) & (hour_lla[i]<6):
        ind = np.array(ind[0][1])
    elif (hour_lla[i]>=6) & (hour_lla[i]<9):
        ind = np.array(ind[0][2])
    elif (hour_lla[i]>=9) & (hour_lla[i]<12):
        ind = np.array(ind[0][3])
    elif (hour_lla[i]>=12) & (hour_lla[i]<15):
        ind = np.array(ind[0][4])
    elif (hour_lla[i]>=15) & (hour_lla[i]<18):
        ind = np.array(ind[0][5])
    elif (hour_lla[i]>=18) & (hour_lla[i]<21):
        ind = np.array(ind[0][6])
    elif (hour_lla[i]>=21) & (hour_lla[i]<24):
        ind = np.array(ind[0][7])

    AP_LLA[i]=AP[ind]
AP_LLA = np.array(AP_LLA)

# F107 时间转换
year_f107 = np.zeros(len(TIME_F107))
month_f107 = np.zeros(len(TIME_F107))
day_f107 = np.zeros(len(TIME_F107))
day_sum_f107 = np.zeros(len(TIME_F107))
for i in range(0,len(TIME_F107)):
    t_ymd = str(TIME_F107[i]).split('T')[0]
    year = int(t_ymd.split('-')[0])
    month = int(t_ymd.split('-')[1])
    day = int(t_ymd.split('-')[2])

    if (month<10)&(day<10) :
        day_sum_f107[i] = cal_jday(str(year)+'0'+str(month)+'0'+str(day))
    elif (month<10)&(day>=10):
        day_sum_f107[i] = cal_jday(str(year)+'0'+str(month)+str(day))
    elif (month>=10)&(day<10):
        day_sum_f107[i] = cal_jday(str(year)+str(month)+'0'+str(day))
    else:
        day_sum_f107[i] = cal_jday(str(year)+str(month)+str(day))
    
    year_f107[i] = year
    month_f107[i] = month
    day_f107[i] = day

# 在F107数据里寻找轨道数据对应的F107值
missing_value = -99999
F107_LLA = np.zeros(len(TIME))+missing_value
for i in range (0,len(TIME)): 

    if (year_lla[i]%4==0 and year_lla[i]%100 !=0):
        day_delt = 365
    else:
        day_delt = 364


    if day_sum_lla[i]>=1:
        ind = np.where((year_f107==year_lla[i]) & (day_sum_f107==day_sum_lla[i]))

    ind = np.array(ind[0])

    if len(ind)!=0:
        F107_LLA[i]=F107[ind]
F107_LLA = np.array(F107_LLA)

# 在F07数据里寻找轨道数据对应的81天平均F107值
missing_value = -99999
F107a_LLA = np.zeros(len(TIME))+missing_value
for i in range (0,len(TIME)):
    ind = np.where((year_f107==year_lla[i]) & (month_f107==month_lla[i]) & (day_f107==day_lla[i]))
    ind = np.array(ind[0])
    if len(ind)!=0:
        F107a_LLA[i]=np.nanmean(F107[int(ind)-40:int(ind)+41])
F107a_LLA = np.array(F107a_LLA)


# 计算电子密度
# 对于LEO等离子体，航天器速度远大于离子热速度而远小于电子热速度，
# 相对于航天器而言，离子被看作以航天器速度迎面而来的束流，而电子
# 仍以热速度运动，电子电流密度为：
# J = 6.43e-15*Density[m-3]*Temperature[eV]

sys.stdout = open(os.devnull, 'w')
temp_e = np.arange(len(TIME))
density_e = np.arange(len(TIME))
time_e = []
for i in range(0,len(TIME)):
    altkm = [ALT[i]]
    if float(str(altkm)[1:-1]) >=2000:
        altkm = 2000
    elif float(str(altkm)[1:-1]) <= 50:
        altkm = 50
	# 日期
    dtime = datetime.datetime(int(year_lla[i]), int(month_lla[i]), int(day_lla[i]), int(hour_lla[i]), int(minu_lla[i]), int(sec_lla[i]))
    # 经度纬度
    latlon = (LAT[i], LON[i])
	# F107指数
    f107 = F107_LLA[i]
	# F107a指数
    f107a = F107a_LLA[i]
	# Ap指数
    ap = AP_LLA[i]
	# 计算电子数密度和电子温度 IRI90模型,高度范围50~2000 Km
    iono = iri90.runiri(dtime, altkm, latlon, f107, f107a, ap)
    temp_e[i] = iono[:,3]  # 单位eV
    density_e[i] = iono[:,0]/1e6  # cm-3
    time_e.append(str(dtime))

sys.stdout = sys.__stdout__

