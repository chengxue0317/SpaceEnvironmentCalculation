import matplotlib.pyplot as plt
import numpy as np
import datetime
import iri90
import time
import sys
import os
import configparser
import new_new_1
import dmPython
from scipy.interpolate import interp1d
from datetime import datetime,timedelta
import pandas as pd
from itertools import chain
import warnings
warnings.filterwarnings("ignore")


#获取指定时间的全球电子含量分布图
def get_el(F107_time, F107, AP, altkm, figure_save_path):

    altkm=[altkm]
    degree = 15
    lon = np.arange(-180, 181, degree)
    lat = np.arange(90, -91, -degree)
    lon2d_1deg, lat2d_1deg = np.meshgrid(lon, lat)

    if not os.path.exists(figure_save_path):
        os.makedirs(figure_save_path)

    el = np.zeros((13, 25))
    for l in range(len(F107_time)):
        mkfile_time = str(F107_time[l]).replace('-','').replace(' ','').replace(':','')
        dtime = datetime(F107_time[l].year, F107_time[l].month, F107_time[l].day, F107_time[l].hour)
        f_107 = F107[l]
        ap = AP[l]
        result = new_new_1.main(lat2d_1deg, lon2d_1deg, dtime, altkm, f_107, ap, el)   #利用new_new_2.py文件进行多线程计算结果

        plt.figure(figsize=(10, 5))
        plt.subplots_adjust(top=1, bottom=0, left=0, right=1, hspace=0, wspace=0)
        plt.margins(0, 0)
        plt.contourf(result, 120, cmap='jet')
        plt.axis('off')
        plt.savefig(os.path.join(figure_save_path, altkm[0] +'_'+mkfile_time + '.jpg'), dpi=100)
        print("结果保存成功！！！")

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
    start = time.time()
    iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0] + '/DLXJS_DB.ini'
    conn = Connect_SQL(iniPath)
    all_f107_data, all_ap_data, all_f107_time_new = get_data(startTime, endTime, conn)
    get_el(all_f107_time_new, all_f107_data, all_ap_data, altkm,figure_save_path)
    end = time.time()
    print('Running time: %s Seconds' % (end - start))
if __name__ == '__main__':

    # altkm = 500
    # startTime = '2022-11-01 00:00:00'
    # endTime = '2022-11-02 00:00:00'
    # figure_save_path = "./TEC/new1"

    altkm = sys.argv[1]
    startTime = sys.argv[2]
    endTime = sys.argv[3]
    figure_save_path = sys.argv[4]

    main(altkm, startTime, endTime, figure_save_path)


