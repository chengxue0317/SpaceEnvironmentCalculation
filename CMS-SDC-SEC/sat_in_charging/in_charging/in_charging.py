import dmPython
import numpy as np
import math
import sys,os
from pathlib import Path
import pandas as pd
from datetime import datetime
import json
import re
import random
import time
sys.path.append(str(Path(__file__).resolve().parents[1]))
import BB0_LM.BB0_LM_point.BB0LM as BB0LM
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
    return conn


# 获取材料参数
# R: 电导率 Ω-1·cm-1
# p：密度 g/cm3
def get_parameter(sat_material):
    current_dir = os.path.dirname(os.path.abspath(__file__))
    f = open(current_dir+'/parameter.txt')
    size = len(f.readlines())
    f = open(current_dir+'/parameter.txt')
    para_name = []
    para_conductivity = []
    para_density = []
    for i in range(size):
        txt = f.readline().split()
        if i > 0:
            para_name.append(txt[0])
            para_conductivity.append(txt[1])
            para_density.append(txt[2])
    conductivity = float(para_conductivity[para_name.index(sat_material)])
    density = float(para_density[para_name.index(sat_material)])
    return conductivity, density


# 计算辐射衰减银子m值
def Calculate_m(L):
    if L > 4:
        m = 0.6
    elif L <= 4:
        m = 0.6+0.06*(4-L)+0.46*pow(4-L, 6)
    return m


# 计算季节周期活动参数
def Calculate_seasonal_para(date1, date2):
    y1, m1, d1 = date1.split("-")
    y2, m2, d2 = date2.split("-")
    cur_day = datetime(int(y1), int(m1), int(d1))
    next_day = datetime(int(y2), int(m2), int(d2))
    return abs((next_day - cur_day).days)/364


# 计算L值>3的卫星轨道辐射环境的积分能谱
def Calculate_F_L1(L, E, fsc, fyr):  # fsc:在太阳活动极小年为0，到下一个极小年为1
    # 计算a
    a = 42*(L-2.8)*2*math.exp(-0.39*(L-2.8)*2)
    # 计算fr
    k1 = 8.8+0.5*math.sin(2*math.pi*(fsc-0.55))
    k2 = 1-0.0435*math.cos(math.pi*2*fyr)
    fr = pow(10, k1*k2)
    # 计算E0
    if fsc <= 0.8:
        E0 = 0.39+0.14*fsc
    else:
        E0 = 0.39+0.56*(1-fsc)
    # 计算b
    b = 1.73-0.106*L
    # 计算F
    F = a*fr*math.exp((2-E)/(b*E0))/86400

    return F  # cm-2 s-1 sr-1


# 更新txt文件
def updateFile(file, old_str, new_str):
    with open(file, "r", encoding="utf-8") as f1, open("%s.bak" % file, "w", encoding="utf-8") as f2:
        for line in f1:
            f2.write(re.sub(old_str, new_str, line))
    os.remove(file)
    os.rename("%s.bak" % file, file)


# ### 计算L值<3的卫星轨道辐射环境的积分能谱
# 计算E0, F0值，构建能谱参数
def Calculate_E0F0(flux):
    E_ae8 = [0.15, 0.35, 0.65, 1.2, 2.0]
    F1 = flux[0]-flux[1]
    E1 = E_ae8[0]+(E_ae8[1]-E_ae8[0])/2
    F2 = flux[3]-flux[4]
    E2 = E_ae8[3]+(E_ae8[4]-E_ae8[3])/2
    if (F1 <= 0) | (F2 <= 0):
        F0 = 0 
    E0 = (E2-E1)/math.log(F1/F2, math.e)
    F0 = F1/math.exp(-E1/E0)
    return E0, F0


# 写BB0LM.txt并用ae8模拟生成flux.txt
def ae8_modelling(BB0, LM, current_dir, dir_name):
    txt = open(current_dir+'/'+dir_name+'/BB0LM.txt', mode='w')
    txt.write(str(len(BB0))+'\n')
    txt.write('3\n')
    txt.write('1\n')
    txt.write('bb0,lm,lat,lon,date\n')
    for i in range(len(BB0)):
        txt.write("%s,%s,%s,-180.0,90.0,2020-12-11 12:12:12" % (i, BB0[i], LM[i]))
        txt.write('\n')
    txt.close()
    os.system('cd '+current_dir+'/'+dir_name+'; ' './ap8ae8')


# 根据能谱参数计算卫星轨道辐射环境的积分能谱
def Calculate_F_L2(E0, F0, E):
    F = np.zeros(len(E))
    for i in range(len(E)):
        F[i] = F0*math.exp(-E[i]/E0)
    return F


# 计算电子最大射程
def Calculate_EL(E):
    den = 2.7
    R = 5.5*E*(1-0.9841/(1+3*E))/den
    return R  # mm


# 获取数据库对应卫星星下点的时间分辨率
def get_sqltime_step(SAT_ID):
    from datetime import datetime
    Time_step = "SELECT TIME from SEC_SATELLITE_LLA where SAT_ID = '%s' order by ID desc limit 2" % (SAT_ID)
    P = pd.read_sql(Time_step, conn).TIME.values
    time_0 = datetime.timestamp(datetime.strptime(str(P[0]).replace('T', ' ').split('.')[0], '%Y-%m-%d %H:%M:%S'))
    time_1 = datetime.timestamp(datetime.strptime(str(P[1]).replace('T', ' ').split('.')[0], '%Y-%m-%d %H:%M:%S'))
    return time_0-time_1


def Cal_sat_in_charging(P, mp):

    # 建立模拟结果文件夹(唯一性)
    current_dir = os.path.dirname(os.path.abspath(__file__))
    while True:
        dir_name = str(random.randint(1, 9999999))
        if os.path.exists(dir_name) == False:
            os.system('mkdir '+current_dir+'/'+dir_name)
            break

    # 将so文件和ap8ae8放入新生成的随机文件夹内
    os.system('cp '+current_dir+'/'+'/ap8ae8 ' + current_dir+'/'+dir_name)
    os.system('cp '+current_dir+'/'+'/libirbem.so ' + current_dir+'/'+dir_name)

    # 计算电子通量随屏蔽厚度的变化
    T = P.TIME.values
    LAT = []
    LON = []
    ALT = []
    TIME = []

    for i in range(len(T)):
        TIME.append(str(T[i]).replace('T', ' ').split('.')[0])
        LAT.append(P.LAT.values[i])
        LON.append(P.LON.values[i])
        ALT.append(P.ALT.values[i])

    # 计算BB0 L值
    data = BB0LM.main(TIME, ALT, LAT, LON)
    data = pd.DataFrame.from_dict(data)
    data['ALT'] = ALT

    # 剔除LM>8.5和异常值
    ind = np.where((np.array(data['bb0']) > 1000) | (np.array(data['bb0']) < 0) | (np.array(data['LM']) > 8.5))[0].tolist()
    data.drop(index=ind, inplace=True)
    data['LM'] = abs(data['LM'])

    # 将data分为两组数据，用于不同的参数化方案计算电子通量
    data = data.sort_values(by=['LM'], ascending=False)
    data_F_L1 = data[data.LM > 3]
    data_F_L2 = data[data.LM <= 3]

    # 读取BB0 和 LM值
    BB0 = np.array(data_F_L1['bb0'])
    LM = np.array(data_F_L1['LM'])

    # 计算辐射削减参数G
    G = np.ones(len(BB0))
    for i in range(len(BB0)):
        if (LM[i] >= 3):
            m = Calculate_m(LM[i])
            b = BB0[i]
            bc = 0.6/(0.311654/pow(LM[i], 3))
            if b < bc:
                G[i] = pow(b, -m)*pow(1-0.52*b/pow(LM[i], 3), m+0.5)
                if np.isnan(G[i]):
                    G[i] = 0
            else:
                G[i] = 0
    data_F_L1['G'] = G
    TIME_F_L1 = data_F_L1['date']

    # 计算季节参数
    TIME_F_L1 = list(map(str, TIME_F_L1))
    fyr = np.zeros(len(TIME_F_L1))
    for i in range(len(TIME_F_L1)):
        year = int(TIME_F_L1[i].split('-')[0])
        month = int(TIME_F_L1[i].split('-')[1])
        day = int(TIME_F_L1[i].split('-')[2].split(' ')[0])
        date1 = str(year)+'-'+str(month)+'-'+str(day)
        if ((month <= 12) & (day < 22)) | (month < 12):   
            date2 = str(year-1)+'-12-22'
        elif (month == 12) & (day >= 22):
            date2 = str(year)+'-12-22'
        fyr[i] = Calculate_seasonal_para(date1, date2)

    # 计算电场强度
    # 太阳活动参数
    fsc = 0
    E = np.arange(0.01, 20, 0.01)
    F_L1 = np.zeros(len(E))
    materi = int(sys.argv[4])

    R, p = get_parameter(sys.argv[5])
    # 屏蔽材料：等效铝厚
    depth_shield = float(sys.argv[6])*p/2.7

    R, p = get_parameter(sys.argv[7])
    # 介质材料：等效铝厚 
    depth_materi = float(sys.argv[8])*p/2.7

    EL_shield = np.zeros(len(E)) 
    EL_materi = np.zeros(len(E))
    U_1_1 = np.zeros(len(TIME_F_L1))

    for i in range(len(E)):
        # 根据屏蔽厚度depth计算能够穿过的电子能量E0和其对应的通量F0
        EL_shield[i] = abs(Calculate_EL(E[i])-depth_shield)
        EL_materi[i] = abs(Calculate_EL(E[i])-depth_materi-depth_shield)
    ind0 = np.where(EL_shield == min(EL_shield))
    ind1 = np.where(EL_materi == min(EL_materi))
    
    # 当L>3时，由拟合参数化方案计算通量
    for point in range(len(TIME_F_L1)): 
            
        for i in range(len(E)):
            F_L1[i] = G[point]*Calculate_F_L1(LM[point], E[i], fsc, fyr[point])
        E0 = E[ind0]
        F0 = F_L1[ind0]
        
        E1 = E[ind1]
        F1 = F_L1[ind1]
        # 透过屏蔽材料在介质中沉积的电子通量
        F_delt = F0-F1
        # 将沉积电子通量转化为充电电流
        J = 1.602e-19*F_delt
        U_1_1[point] = J/R

    # 当L<3时，由AE8模型计算通量
    BB0 = np.array(data_F_L2['bb0'])
    LM = np.array(data_F_L2['LM'])
    U_1_2 = np.zeros(len(BB0))
    ae8_modelling(BB0, LM, current_dir, dir_name)
    # 读 flux.txt 
    f = open(current_dir+'/'+dir_name+'/flux.txt')
    for i in range(len(BB0)):
        flux = list(map(float, np.array(f.readline().replace('\n', '').replace(' ', '').split(',')[3:8])))
        if sum(flux) < 0:
            # 当ae8模拟值为空时，定义通量为0
            flux = [0, 0, 0, 0, 0]

        if flux[0] == 0:
            F0 = 0
            F1 = 0
        else:
            E0, F0 = Calculate_E0F0(flux)
            F_L2 = Calculate_F_L2(E0, F0, E)
            F0 = F_L2[ind0]
            F1 = F_L2[ind1]
        F_delt = F0-F1
        # 将沉积电子通量转化为充电电流
        J = 1.602e-19*F_delt
        U_1_2[i] = J/R
    U1 = np.concatenate([U_1_1, U_1_2])

    data['U1'] = U1

    # 判断有无深层充电风险
    U1_results = []

    # NASA-HDBK-4002给出：绝大部分良好的绝缘材料，其击穿电压阈值为2e5 V/cm
    for i in range(len(U1)):
        if U1[i] < 2e5:
            U1_results.append('safe')
        else:
            U1_results.append('danger')
    data['U1_re'] = U1_results

    # 将时间转化为时间戳 
    data_t = np.array(data['date'])
    timeStamp = np.zeros(len(data_t))
    for i in range(len(data_t)): 
        timeArray = time.strptime(str(data_t[i]), '%Y-%m-%d %H:%M:%S')
        timeStamp[i] = int(time.mktime(timeArray))
    data['ts'] = timeStamp
    data = data.sort_values(by=['ts'], ascending=True)

    # 以JSON形式显示结果
    dic = {}
    time_js = list(map(str, np.array(data['date'])))
    dic['time'] = time_js
    dic['lat'] = LAT
    dic['lon'] = LON
    dic['alt'] = ALT
    u1_js = list(map(int, np.array(data['U1'])))
    dic['u1'] = u1_js
    dic['u1_res'] = U1_results
    os.system('rm -rf '+current_dir+'/'+dir_name)
    return dic


# 从数据库读取星下点数据
Time_start = str(sys.argv[1])
Time_end = str(sys.argv[2])
SAT_ID = str(sys.argv[3])

# 连接达梦数据库
iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
conn = Connect_SQL(iniPath)

# 获取卫星时间分辨率
time_sql_step = get_sqltime_step(SAT_ID)

# 读取达梦数据,每隔多长时间(秒)取一个数据？
time_step = 60  # 单位：s
if time_sql_step >= time_step:
    Time_span = "select LAT,LON,ALT,TIME from SEC_SATELLITE_LLA where TIME between '%s' and '%s' and SAT_ID = '%s'" % (Time_start, Time_end, SAT_ID)
else:
    Time_span = "SELECT * from SEC_SATELLITE_LLA where SAT_ID = '%s' and ID MOD %d = 0 and TIME between '%s' and '%s' and SAT_ID = '%s'" % (SAT_ID, int(time_step/time_sql_step), Time_start, Time_end, SAT_ID)
P = pd.read_sql(Time_span, conn)

# start_time = time.time()    # 程序开始时间
# Cal_sat_in_charging(P)
# end_time = time.time()
# print('程序运行时间为：', end_time - start_time, '秒')


if __name__ == "__main__":
    mp = 1 
    if np.shape(P)[0] < 1000:
        start_time = time.time()    # 程序开始时间
        dic = Cal_sat_in_charging(P, mp)
        print('###')
        print(json.dumps(dic))
        print('###')
        print(len(P))
        end_time = time.time()
        print('程序运行时间为：', end_time - start_time, '秒')
    else:
        import multiprocessing
        import time as timec
        step = int(np.shape(P)[0]/6)
        m1, n1 = 0, step
        m2, n2 = step, step*2
        m3, n3 = step*2, step*3
        m4, n4 = step*3, step*4
        m5, n5 = step*4, step*5
        m6, n6 = step*5, np.shape(P)[0]+1
        start_time = timec.time()    # 程序开始时间
        final_res = []
        pool = multiprocessing.Pool(processes=40)
        res1 = pool.apply_async(Cal_sat_in_charging, args=(P[m1:n1], mp))
        res2 = pool.apply_async(Cal_sat_in_charging, args=(P[m2:n2], mp))
        res3 = pool.apply_async(Cal_sat_in_charging, args=(P[m3:n3], mp))
        res4 = pool.apply_async(Cal_sat_in_charging, args=(P[m4:n4], mp))
        res5 = pool.apply_async(Cal_sat_in_charging, args=(P[m5:n5], mp))
        res6 = pool.apply_async(Cal_sat_in_charging, args=(P[m6:n6], mp))
        pool.close()
        pool.join()  # 调用join之前，先调用close函数，否则会出错。执行完close后不会有新的进程加入到pool,join函数等待所有子进程结束
    
        time = res1.get()['time']+res2.get()['time']+res3.get()['time']+res4.get()['time']+res5.get()['time']+res6.get()['time']
        lat = res1.get()['lat']+res2.get()['lat']+res3.get()['lat']+res4.get()['lat']+res5.get()['lat']+res6.get()['lat']
        lon = res1.get()['lon']+res2.get()['lon']+res3.get()['lon']+res4.get()['lon']+res5.get()['lon']+res6.get()['lon']
        alt = res1.get()['alt']+res2.get()['alt']+res3.get()['alt']+res4.get()['alt']+res5.get()['alt']+res6.get()['alt']
        u1 = res1.get()['u1']+res2.get()['u1']+res3.get()['u1']+res4.get()['u1']+res5.get()['u1']+res6.get()['u1']
        u1_res = res1.get()['u1_res']+res2.get()['u1_res']+res3.get()['u1_res']+res4.get()['u1_res']+res5.get()['u1_res']+res6.get()['u1_res']
        dic = {}
        dic['time'] = time
        dic['lat'] = lat
        dic['lon'] = lon
        dic['alt'] = alt
        dic['u1'] = u1
        dic['u1_res'] = u1_res
        print('###')
        print(json.dumps(dic))
        print('###')
        end_time = timec.time()
        print('程序运行时间为：', end_time - start_time, '秒')
        print(len(P))
