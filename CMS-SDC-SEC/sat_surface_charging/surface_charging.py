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
from cal_U import Calculate_balance_U
import iri90
import math
import json
from mpl_toolkits.basemap import Basemap
from matplotlib import animation
import matplotlib.animation as anime


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


# 转换为一年中的某一天
def cal_jday(yyyymmdd):
    dt = datetime.datetime.strptime(str(yyyymmdd), '%Y%m%d')
    tt = dt.timetuple()
    Jyear = tt.tm_year
    Jday = tt.tm_yday
    return Jday

# 连接达梦数据库
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
cursor,conn = Connect_SQL(iniPath)

# 从数据库读取星下点数据
Time_start = str(sys.argv[1]).replace('"','')
Time_end = str(sys.argv[2]).replace('"','')
SAT_ID = str(sys.argv[3]).replace("'",'')
Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start,Time_end,SAT_ID)
P = pd.read_sql(Time_span, conn)

# 读取时间
T = P.TIME.values
LAT = []
LON = []
ALT = []
TIME = []
if len(T)>500:
    step = int(len(T)/500)
else:
    step = 1
for i in range(1,len(T),step):
    LAT.append(P.LAT.values[i])
    LON.append(P.LON.values[i])
    ALT.append(P.ALT.values[i])
    TIME.append(T[i])

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

print('finished loading data')

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

print('finished reading data')


# 计算充电电压
#sys.stdout = open(os.devnull, 'w')
Te = np.zeros(len(TIME))
Ne = np.zeros(len(TIME))
Ti = np.zeros(len(TIME))
Ni = np.zeros(len(TIME))
time_e = []
for i in range(0,len(Te)):

    altkm = ALT[i]
    if altkm <= 200:
        altkm = 200
	
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

    if altkm<2500:
	   # 计算电子数密度和电子温度 IRI90模型,高度范围50~2000 Km
        iono = iri90.runiri(dtime, [altkm], latlon, f107, f107a, ap)
        Te[i] = iono[:,3]/11605  # 单位eV
        Ne[i] = iono[:,0]/1e6  # cm-3
        Ti[i] = iono[:,2]/11605
        Ni[i] = (iono[:,4]+iono[:,5]+iono[:,6]+iono[:,7])/1e6
    else:
        Te[i] = 1e3
        Ti[i] = 1e3
        Ne[i] = 4
        Ni[i] = 4

    # 如果离子温度为空，近似认为离子温度=电子温度
    if pd.isna(Ni[i]):
        Ni[i] = Ti[i]
    time_e.append(str(dtime))

sys.stdout = sys.__stdout__

# 删除IRI模型模拟的NaN值
ind1 = np.where(pd.isna(Te))[0]
ind2 = np.where(pd.isna(Ne))[0]
ind3 = np.where(pd.isna(Ti))[0]
ind4 = np.where(pd.isna(Ni))[0]
ind =[ind1,ind2,ind3,ind4]
k = []
for i in range(len(ind)):
    if len(ind[i]) == 0:
        k.append(i)
lis = [n for i, n in enumerate(ind) if i not in k ]

Te = np.delete(Te,lis)  #ev
Ne = np.delete(Ne,lis)
Ti = np.delete(Ti,lis)  #cm-3
Ni = np.delete(Ni,lis)
LAT = np.delete(LAT,lis)
LON = np.delete(LON,lis)

dtime = np.delete(np.array(time_e),lis)
print('finished modelling plasma')


# 读取参数数据
Material_paras = int(sys.argv[4])

# 计算表面充电电压
U_lla = np.zeros(len(Te))
for i in range(0,len(Te)):
    if (math.fabs(LAT[i])>90) & (Te[i]<1):  # 这里仅仅限制了纬度、未来还要限制高度。
        Auroral_Precipitated_Particles = 'T'
    else:
        Auroral_Precipitated_Particles = 'F'
    U_lla[i] = round(Calculate_balance_U(Te[i],Ti[i],Ne[i],Ni[i],Material_paras,Auroral_Precipitated_Particles,LAT[i]),2)

# 以JSON形式显示结果
time_js = list(map(str,dtime))
u_js = list(map(float,U_lla))
dic = {}
dic['time'] = time_js
dic['lat'] = LAT.tolist()
dic['lon'] = LON.tolist()
dic['u'] = u_js
print('###')
print(json.dumps(dic))
print('###')
   
def plot():
    x = np.arange(len(U_lla))
    plt.plot(x,U_lla,label='method 1')
    plt.savefig('compare.jpg',dpi=300)
    plt.clf()
    fig,ax = plt.subplots()
    plt.rcParams['axes.titlesize'] = 8

    metadata = dict(title="Movie", artist="sourabh")
    writer = anime.PillowWriter(fps=10, metadata=metadata)

    with writer.saving(fig, "exp3d.gif", 100):
        for i in range(len(LAT)):
        
            m = Basemap(llcrnrlat=-90, llcrnrlon=-180, urcrnrlat=90, urcrnrlon=180)
            m.drawcoastlines()

            plt.title('Time: %s Latitude: %s Longitude: %s Surface Charging: %s'%(time_js[i],round(LAT[i],2),round(LON[i],2),U_lla[i]))
       
            if U_lla[i] > -500:
                colors = 'green'
            elif U_lla[i]<-500:
                colors = 'red'
            ax.scatter(LON[i], LAT[i], c=colors,s = 20)
            ax.margins(0)
            writer.grab_frame()
            plt.cla()
