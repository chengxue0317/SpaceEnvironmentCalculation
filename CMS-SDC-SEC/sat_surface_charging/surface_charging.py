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
import time


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

# 获取数据库对应卫星星下点的时间分辨率
def get_sqltime_step(SAT_ID):
    from datetime import datetime
    Time_step = "SELECT TIME from SEC_SATELLITE_LLA where SAT_ID = '%s' order by ID desc limit 2" %(SAT_ID)
    P = pd.read_sql(Time_step, conn).TIME.values
    time_0 = datetime.timestamp(datetime.strptime(str(P[0]).replace('T',' ').split('.')[0],'%Y-%m-%d %H:%M:%S'))
    time_1 = datetime.timestamp(datetime.strptime(str(P[1]).replace('T',' ').split('.')[0],'%Y-%m-%d %H:%M:%S'))
    return time_0-time_1


# 转换为一年中的某一天
def cal_jday(yyyymmdd):
    dt = datetime.datetime.strptime(str(yyyymmdd), '%Y%m%d')
    tt = dt.timetuple()
    Jyear = tt.tm_year
    Jday = tt.tm_yday
    return Jday

# 获取参数信息
def get_parameter(sat_material):
    current_dir = os.path.dirname(os.path.abspath(__file__))
    f = open(current_dir+'/parameter.txt')
    size = len(f.readlines())
    f = open(current_dir+'/parameter.txt')
    para_name = []
    para_E_me = []
    para_cigema_me = []
    para_E_mi = []
    para_Y1 = []
    para_Z = []
    para_J_ph0 = []
    for i in range(size):
        txt = f.readline().split('#')
        if i>0:
            para_name.append(txt[0])
            para_E_me.append(txt[1].split()[4])
            para_cigema_me.append(txt[1].split()[3])
            para_E_mi.append(txt[1].split()[2])
            para_Y1.append(txt[1].split()[1])
            para_Z.append(txt[1].split()[0])
            para_J_ph0.append(txt[1].split()[5])
    E_me = float(para_E_me[para_name.index(sat_material)])
    cigema_me = float(para_cigema_me[para_name.index(sat_material)])
    E_mi = float(para_E_mi[para_name.index(sat_material)])
    Y1 = float(para_Y1[para_name.index(sat_material)])
    Z = float(para_Z[para_name.index(sat_material)])
    J_ph0 = float(para_J_ph0[para_name.index(sat_material)])
    return [E_me,cigema_me,E_mi,Y1,Z,J_ph0]


# 连接达梦数据库
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
cursor,conn = Connect_SQL(iniPath)


# 从数据库读取星下点数据
Time_start = str(sys.argv[1]).replace('"','')
Time_end = str(sys.argv[2]).replace('"','')
SAT_ID = str(sys.argv[3]).replace("'",'')


# 获取卫星时间分辨率
time_sql_step = get_sqltime_step(SAT_ID)


# 读取达梦数据,每隔多长时间(秒)取一个数据？
time_step = 600  #单位：s
if time_sql_step>=time_step:
    Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start,Time_end,SAT_ID)
else:
    Time_span = "SELECT * from SEC_SATELLITE_LLA where SAT_ID = '%s' and ID MOD %d = 0 and TIME between '%s' and '%s' and SAT_ID = '%s'"%(SAT_ID,int(time_step/time_sql_step),Time_start,Time_end,SAT_ID)
P_LLA = pd.read_sql(Time_span, conn)


# 读取地磁AP数据
Time_Ap_start = str(int(Time_start.split('-')[0])-1)+'-'+str(Time_start.split('-')[1])+'-'+str(Time_start.split('-')[2])
Time_Ap_end = str(int(Time_end.split('-')[0])+1)+'-'+str(Time_end.split('-')[1])+'-'+str(Time_end.split('-')[2])
Time_span = "select AP,TIME from SEC_AP_INDEX where TIME between '%s' and '%s' " % (Time_Ap_start,Time_Ap_end)
P_AP = pd.read_sql(Time_span, conn)


# 读取太阳F107数据
Time_F107_start = str(int(Time_start.split('-')[0])-1)+'-'+str(Time_start.split('-')[1])+'-'+str(Time_start.split('-')[2])
Time_F107_end = str(int(Time_end.split('-')[0])+1)+'-'+str(Time_end.split('-')[1])+'-'+str(Time_end.split('-')[2])
Time_span = "select F107,TIME from SEC_F107_FLUX where TIME between '%s' and '%s' " % (Time_F107_start,Time_F107_end)
P_F107 = pd.read_sql(Time_span, conn)

print('finished loading data')

def Cal_sat_surface_charging(P_LLA,P_AP,P_F107):
    T = P_LLA.TIME.values
    LAT = []
    LON = []
    ALT = []
    TIME = []
    if len(T)>500:
        step = int(len(T)/500)
    else:
        step = 1
    for i in range(1,len(T),step):
        LAT.append(P_LLA.LAT.values[i])
        LON.append(P_LLA.LON.values[i])
        ALT.append(P_LLA.ALT.values[i])
        TIME.append(T[i])

    AP = P_AP.AP.values
    TIME_AP = P_AP.TIME.values
    F107 = P_F107.F107.values
    TIME_F107 = P_F107.TIME.values

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
            Ne[i] = 1
            Ni[i] = 1

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
    ALT = np.delete(ALT,lis)

    #星下点保留两位小数
    LAT = np.around(LAT,decimals=2)
    LON = np.around(LON,decimals=2)
    ALT = np.around(ALT,decimals=2)

    dtime = np.delete(np.array(time_e),lis)
    print('finished modelling plasma')


    # 读取参数数据
    Material_paras = get_parameter(sys.argv[4])

    # 计算表面充电电压
    U_lla = np.zeros(len(Te))
    for i in range(0,len(Te)):
        # 是否考虑等离子体沉降
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
    dic['alt'] = ALT.tolist()
    dic['u'] = u_js
    return dic

   
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

import multiprocessing
import time as timec
import os
print("温馨提示：本机为",os.cpu_count(),"核CPU")  


def error_callback(error):
    print(f"Error info: {error}")

# 时间戳
def timestamp(shijian):
    s_t=time.strptime(shijian,"%Y-%m-%d %H:%M:%S")
    mkt=int(time.mktime(s_t))
    return(mkt)


if __name__ == "__main__":
    step  = int(np.shape(P_LLA)[0]/6)
    m1, n1 = 0, step
    m2, n2 = step, step*2
    m3, n3 = step*2, step*3
    m4, n4 = step*3, step*4
    m5, n5 = step*4, step*5
    m6, n6 = step*5, np.shape(P_LLA)[0]+1
    start_time = timec.time()    # 程序开始时间

    final_res = []
    pool = multiprocessing.Pool(processes = 40)
   
    res1 = pool.apply_async(Cal_sat_surface_charging, args=(P_LLA[m1:n1],P_AP,P_F107),error_callback=error_callback)
    res2 = pool.apply_async(Cal_sat_surface_charging, args=(P_LLA[m2:n2],P_AP,P_F107),error_callback=error_callback)
    res3 = pool.apply_async(Cal_sat_surface_charging, args=(P_LLA[m3:n3],P_AP,P_F107),error_callback=error_callback)
    res4 = pool.apply_async(Cal_sat_surface_charging, args=(P_LLA[m4:n4],P_AP,P_F107),error_callback=error_callback)
    res5 = pool.apply_async(Cal_sat_surface_charging, args=(P_LLA[m5:n5],P_AP,P_F107),error_callback=error_callback)
    res6 = pool.apply_async(Cal_sat_surface_charging, args=(P_LLA[m6:n6],P_AP,P_F107),error_callback=error_callback)
 
    pool.close()
    pool.join()   #调用join之前，先调用close函数，否则会出错。执行完close后不会有新的进程加入到pool,join函数等待所有子进程结束

    time = res1.get()['time']+res2.get()['time']+res3.get()['time']+res4.get()['time']+res5.get()['time']+res6.get()['time']
    lat = res1.get()['lat']+res2.get()['lat']+res3.get()['lat']+res4.get()['lat']+res5.get()['lat']+res6.get()['lat']
    lon = res1.get()['lon']+res2.get()['lon']+res3.get()['lon']+res4.get()['lon']+res5.get()['lon']+res6.get()['lon']
    alt = res1.get()['alt']+res2.get()['alt']+res3.get()['alt']+res4.get()['alt']+res5.get()['alt']+res6.get()['alt']
    u = res1.get()['u']+res2.get()['u']+res3.get()['u']+res4.get()['u']+res5.get()['u']+res6.get()['u']
    dic = {}
    dic['time'] = time
    dic['lat'] = lat
    dic['lon'] = lon
    dic['alt'] = alt
    dic['u'] = u
    print('###')
    print(json.dumps(dic))
    print('###')
    end_time = timec.time()
    print('程序运行时间为：', end_time - start_time, '秒')

   



#    print ("Successfully")
#    import time
#    end_time = time.time()    # 程序结束时间
#    run_time = end_time - start_time    # 程序的运行时间，单位为秒
#    print("运行时间：",run_time,'秒')
#    final_res = [timestamp(res1.get()['time'][0]),timestamp(res2.get()['time'][0]),timestamp(res3.get()['time'][0]),timestamp(res4.get()['time'][0]),timestamp(res5.get()['time'][0]),timestamp(res6.get()['time'][0])]
#    final_res = np.array(final_res)
#    for i in range(6):
#        print(int(np.where((final_res-final_res[i])==0)[0]))