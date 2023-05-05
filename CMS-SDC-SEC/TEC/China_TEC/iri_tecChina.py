import os
import sys
import time
import dmPython
import new_new_1
import numpy as np
import configparser
import pandas as pd
import matplotlib as mpl

from itertools import chain
import matplotlib.pyplot as plt
import matplotlib.ticker as ticker
from datetime import datetime, timedelta
from mpl_toolkits.basemap import Basemap
import warnings
warnings.filterwarnings("ignore")

plt.figure(figsize=(10, 6))
map = Basemap(llcrnrlon=70, llcrnrlat=13, urcrnrlon=137, urcrnrlat=55)
map.drawcountries(linewidth=0.8, color='k')
map.drawcoastlines(linewidth=0.8, color='k')
parallels = np.linspace(13, 55, 6)
map.drawparallels(parallels, labels=[True, False, False, False])
meridians = np.linspace(70, 137, 6)
map.drawmeridians(meridians, labels=[False, False, False, True])

norm = mpl.colors.Normalize(vmin=0, vmax=200)
plt.ylabel('Latitude/(deg)', labelpad=40, fontsize=12.5)
plt.xlabel('Longitude/(deg)', labelpad=15, fontsize=12.5)
sm = plt.cm.ScalarMappable(norm=norm, cmap='jet')
cb = plt.colorbar(sm, fraction=0.03, pad=0.05, cmap='jet', )
tick_locator = ticker.MaxNLocator(nbins=4)
cb.locator = tick_locator
cb.update_ticks()
cb.ax.tick_params(labelsize=12.5)
# 设置颜色条的title
cb.ax.set_title('TECU', fontsize=12.5)

#卡尔曼滤波计算实现数据同化
def kalman(kalman_adc_old):
    # Q为这一轮的心里的预估误差
    Q = 0.001
    # R为下一轮的测量误差
    R = 0.1
    # Accumulated_Error为上一轮的估计误差，具体呈现为所有误差的累计
    Accumulated_Error = 1
    ADC_Value = kalman_adc_old + np.tile(np.array(np.repeat(0, 1)), (13, 25))
    # 初始旧值
    # 新的值相比旧的值差太大时进行跟踪
    if abs(ADC_Value.all()^kalman_adc_old.all())>1:
        Old_Input = ADC_Value*0.382 + kalman_adc_old*0.618
    else:
        Old_Input = kalman_adc_old
    # 上一轮的 总误差=累计误差^2+预估误差^2
    Old_Error_All = (Accumulated_Error**2 + Q**2)**(1/2)
    # R为这一轮的预估误差
    # H为利用均方差计算出来的双方的相信度
    H = Old_Error_All**2/(Old_Error_All**2 + R**2)
    # 旧值 + 1.00001/(1.00001+0.1) * (新值-旧值)
    kalman_adc = Old_Input + H * (ADC_Value - Old_Input)
    # 计算新的累计误差
    Accumulated_Error = ((1 - H)*Old_Error_All**2)**(1/2)
    # 新值变为旧值
    kalman_adc_old = kalman_adc
    return kalman_adc

def get_picture(altkm, lat2d_1deg, lon2d_1deg, F107_time,F107, AP, el, figure_save_path):

    for l in range(len(F107_time)):
        mkfile_time = str(F107_time[l]).replace('-', '').replace(' ', '').replace(':', '')
        figure_save_name = os.path.join(figure_save_path, altkm[0] + '_' + mkfile_time + '.jpg')
        dtime = datetime(F107_time[l].year, F107_time[l].month, F107_time[l].day, F107_time[l].hour)
        f_107 = F107[l]
        ap = AP[l]

        result = new_new_1.main(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el)   #利用new_new_2.py文件进行多线程计算结果

        plt.contourf(lon2d_1deg, lat2d_1deg, result,120,cmap='jet')
        plt.title(label="TEC_Density_Chinese  at" + '  ' + mkfile_time)
        plt.savefig(figure_save_name, dpi = 100)
        print("结果保存成功！！！！")

# 获取指定时间的全球电子含量分布图
def get_el(F107_time, F107, AP, altkm, figure_save_path):

    altkm = [altkm]
    degree = 50
    lon = np.arange(-180, 181, degree)
    lat = np.arange(90, -91, -degree)

    lon2d_1deg, lat2d_1deg = np.meshgrid(lon, lat)

    el = np.zeros((4, 8))
    if not os.path.exists(figure_save_path):
        os.makedirs(figure_save_path)

    get_picture(altkm, lat2d_1deg, lon2d_1deg, F107_time,F107, AP, el, figure_save_path)

#获取开始时间和结束时间范围内的所有F107数据和AP数据
def get_data(startTime, endTime, conn):
    f107_data_new, f107_time_new, ap_data_new, ap_time_new = [], [], [], []
    F107_data = "SELECT * FROM SEC_F107_FLUX"
    AP_data = "SELECT * FROM  SEC_AP_INDEX"
    pdate_f107 = pd.read_sql(F107_data, conn)
    pdate_ap = pd.read_sql(AP_data, conn)
    f107_data = np.array(pdate_f107['F107']).tolist()
    f107_time = pdate_f107['TIME'].tolist()
    ap_data = np.array(pdate_ap['AP']).tolist()
    ap_time = pdate_ap['TIME'].tolist()

    ss_time_f107 = datetime.strptime(startTime.split(' ')[0], "%Y-%m-%d")
    ee_time_f107 = datetime.strptime(endTime.split(' ')[0], "%Y-%m-%d")

    ss_time_ap = datetime.strptime(startTime, "%Y-%m-%d %H:%M:%S")
    ee_time_ap = datetime.strptime(endTime, "%Y-%m-%d %H:%M:%S")

    # 获取时间范围内的天数和小时数
    duration_days = (datetime.strptime(endTime, "%Y-%m-%d %H:%M:%S") - datetime.strptime(startTime, "%Y-%m-%d %H:%M:%S")).days * 24
    duration_hours = (datetime.strptime(endTime, "%Y-%m-%d %H:%M:%S") - datetime.strptime(startTime, "%Y-%m-%d %H:%M:%S")).seconds / 3600

    duration_hours_new = duration_days + duration_hours

    for i in range(len(f107_data)):
        f107_time_1 = datetime.strptime(str(f107_time[i]).split(' ')[0], "%Y-%m-%d")
        if ss_time_f107 <= f107_time_1 <= ee_time_f107:
            f107_data_new.append(f107_data[i])
            f107_time_new.append(f107_time[i])

    for j in range(len(ap_data)):
        ap_time_1 = datetime.strptime(str(ap_time[j]), "%Y-%m-%d %H:%M:%S")
        if ss_time_ap <= ap_time_1 <= ee_time_ap:
            ap_data1 = [val for val in [str(ap_data[j])] for k in range(3)]
            ap_data_new.append(ap_data1)
    ap_data_2 = list(chain.from_iterable(ap_data_new))
    all_f107_data = [val for val in f107_data_new for k in range(24)][:int(duration_hours_new)]

    all_f107_time_new, all_ap_data = [], []
    for h in range(len(f107_time_new)):
        for t in range(0, 24):
            time_new = datetime.strptime(str(f107_time_new[h] + timedelta(hours=t)), "%Y-%m-%d %H:%M:%S")
            if ss_time_ap <= time_new < ee_time_ap:
                all_f107_time_new.append(time_new)
                all_ap_data.append(ap_data_2[t])

    return all_f107_data, all_ap_data, all_f107_time_new

#连接数据库
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

#主函数
def main(altkm, startTime, endTime,figure_save_path):
    ss = time.time()
    iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0] + '/DLXJS_DB.ini'
    cursor,conn= Connect_SQL(iniPath)
    all_f107_data, all_ap_data, all_f107_time_new = get_data(startTime, endTime, conn)
    get_el(all_f107_time_new, all_f107_data, all_ap_data, altkm,figure_save_path)
    ee = time.time()
    print("代码运行时间：", int(ee) - int(ss))

if __name__ == '__main__':

    # altkm = 100
    # startTime = '2022-11-02 00:00:00'
    # endTime = '2022-11-03 00:00:00'
    # figure_save_path = "./KF_IRI_TEC/picture1"

    altkm = sys.argv[1]
    startTime = sys.argv[2]
    endTime = sys.argv[3]
    figure_save_path = sys.argv[4]

    main(altkm, startTime, endTime, figure_save_path)