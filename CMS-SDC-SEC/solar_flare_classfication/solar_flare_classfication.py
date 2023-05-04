# !/D:\Anaconda\envs
# -*- coding: utf-8 -*-
# @Time : 2022/11/7 14:06
# @Author : Jiyh
#

import configparser
import json
import matplotlib.pyplot as plt
plt.rcParams['font.sans-serif']=['SimHei'] #显示中文标签
plt.rcParams['axes.unicode_minus']=False
from scipy.signal import find_peaks
import datetime
import dmPython
import matplotlib.ticker as ticker






# 历史的分级；
def Function_alarm(UNIT_ID,DEVICE_ID,STA_ID,data,alarm_time,dict_level):
    # 转化为列表
    x = list(data.keys())
    y = list(data.values())
    # 绘图查看
    fig, ax = plt.subplots(1, 1)
    ax.plot(x, y)
    plt.xlabel('时间')
    plt.ylabel('通量')
    ax.xaxis.set_major_locator(ticker.MultipleLocator(base=60))
    plt.xticks(rotation=90)
    # plt.show()

    # 提取当天峰值及对应时间
    # peak_id, peak_property = find_peaks(y, height=(10**(-5),10**(-4)), distance=60)
    peak_id, peak_property = find_peaks(y, height=10 ** (-8), distance=60)

    # print(peak_id, peak_property)

    peak_time = []
    for i in peak_id:
        # year = int(x[i][0:4])
        # month = int(x[i][5:7])
        # day = int(x[i][8:10])
        # hour = int(x[i][11:13])
        # minite = int(x[i][14:16])
        # second = int(x[i][17:19])
        # # # print(year,month,day,hour,minite,second)
        # time_datetime = datetime.datetime(year,month,day,hour,minite,second)
        #
        # print(time_datetime)
        # peak_time.append(time_datetime)

        # datetime_t = datetime.datetime.strptime(str_time_t, "%Y-%m-%d %H:%M:%S")
        # peak_time.append(time_datetime)

        str_time_t = x[i].strip('Z').replace("T", " ")
        peak_time.append(str_time_t)

    peak_max_flux = peak_property['peak_heights']
    # print(peak_max_flux)

    list_level = []
    for i in peak_max_flux:
        if i < level_set[0]:
            level = '0'
            list_level.append(level)
        elif i < level_set[1]:
            level = '1'
            list_level.append(level)
        elif i < level_set[2]:
            level = '2'
            list_level.append(level)
        else:
            level = '3'
            list_level.append(level)
    # print(list_level)



    LIST_ALL = []
    for i in range(len(list_level)):
        time_t = peak_time[i]

        level_t = str(list_level[i])
        #
        # print(level_t)
        # print(dict_level[level_t])
        # print(time_t.strftime("%Y-%m-%d %H:%M:%S"),peak_max_flux,)
        # warning_content = "在" + time_t.strftime("%Y-%m-%d %H:%M:%S") + " ,峰值达到"+str(peak_max_flux[i])+",发出" + dict_level[level_t]
        warning_content = "在" + str(time_t)+ " ,峰值达到"+str(peak_max_flux[i])+",发出" + dict_level[level_t]

        # print(warning_content)
        # LIST_Single=[UNIT_ID, DEVICE_ID, STA_ID,alarm_time,peak_time[i],warning_content,list_level[i]]
        LIST_Single=[UNIT_ID, DEVICE_ID, STA_ID,peak_time[i],peak_time[i],warning_content,list_level[i]]

        # print(LIST_Single)
        LIST_ALL.append(LIST_Single)

    return LIST_ALL

def bulid_3days_function(t1, t2, t3):
    list_future_3days = []

    list_future_days_1 = []
    for i in t1:
        list_future_days_1.append(i[5]+";")
    list_future_days_11 = " ".join(list_future_days_1)
    list_future_3days.append(list_future_days_11)

    list_future_days_2 = []
    for i in t2:
        list_future_days_2.append(i[5]+";")
    list_future_days_22 = " ".join(list_future_days_2)
    list_future_3days.append(list_future_days_22)

    list_future_days_3 = []
    for i in t3:
        list_future_days_3.append(i[5]+";")
    list_future_days_33 = " ".join(list_future_days_3)
    list_future_3days.append(list_future_days_33)

    return list_future_3days






def func_conn_dmpython(dmsql_cfg):
    try:
        conn=dmPython.connect(**dmsql_cfg)
        print("成功连接数据库！")
        cursor = conn.cursor()
        # cursor.execute('select * from SEC_XRAY_ALARM')

        # 入库 SEC_XRAY_ALARM ,当天的
        sql0 = "insert into SEC_XRAY_ALARM (UNIT_ID,DEVICE_ID,STA_ID,PUBLISH_TIME,THRESHOLD_TIME,CONTENT,LEVEL) values('{0}','{1}','{2}','{3}','{4}','{5}','{6}')"
        try:
            for list_1 in t0:
                # sql = "insert into SEC_XRAY_ALARM (UNIT_ID,DEVICE_ID,STA_ID,PUBLISH_TIME,THRESHOLD_TIME,CONTENT,LEVEL) values()
                cursor.execute(sql0.format(*list_1))
                conn.commit()
        except:
            conn.rollback()

        # 入库 SEC_ALARM_FORECAST
        sql1 = "insert into SEC_ALARM_FORECAST(SXR1,SXR2,SXR3) values('{0}','{1}','{2}')"

        cursor.execute(sql1.format(*list_future_3days))
        conn.commit()

        cursor.close()
        conn.close()
    except Exception as err:
        print("未能连接至数据库！")




config = configparser.ConfigParser()
config.read("info.ini", encoding="utf-8")

# 输入数据
input_xrays_7days = config.get('classify', 'input_xrays_7days')

# 参数设置
channel = config.get('classify', 'channel')

pre_data_time_info = config.get('classify', 'pre_data_time_span')
pre_data_time_span = json.loads(pre_data_time_info)
print(pre_data_time_span)

time = config.get('classify', 'time')
# 系统预设时间
Preset_system_time = config.get('classify', 'Preset_system_time')
print(Preset_system_time)
# 分别对应A,B,C,M,X
UNIT_ID = config.get('classify', 'UNIT_ID')
DEVICE_ID = config.get('classify', 'DEVICE_ID')
STA_ID = config.get('classify', 'STA_ID')

ALARM_INFO = config.get('classify', 'ALARM_TIME')
ALARM_TIME = json.loads(ALARM_INFO)
print(ALARM_TIME)
# 太阳软X射线耀斑强度分级
# 本标准按照GB/T 1.1-2009给出的规则起草。
# 本标准由全国卫星气象与空间天气标准化委员会空间天气监测预警分技术委员会（SAC/TC 347/SC 3）提出并归口。
# 本标准起草单位：国家卫星气象中心（国家卫星气象中心（国家空间天气监测预警中心））
# A级： 10**-8 <= Fx < 10**-7
# B级： 10**-7 <= Fx < 10**-6
# C级： 10**-6 <= Fx < 10**-5
# M级： 10**-5 <= Fx < 10**-4
# X级： 10**-4 <= Fx
# 本项目共分为三级，将AB合并为一级、MX合并为一级。
# 即项目分类标准为：
# 0级： 无警报
# 1级： 10**-8 <= Fx < 10**-6（黄色警报）
# 2级： 10**-6 <= Fx < 10**-5（橙色警报）
# 3级： 10**-5 <= Fx    （红色警报）
level_INFO = config.get('classify', 'level_set')
print(level_INFO)
level_set = json.loads(level_INFO)
print(level_set)





dict_level = {"0": "无警报",
              "1": "黄色警报",
              "2": "橙色警报",
              "3": "红色警报"}

current_time= datetime.datetime.strptime(Preset_system_time,'%Y-%m-%d').date()

future_24 = current_time + datetime.timedelta(days=1)

future_48 = current_time + datetime.timedelta(days=2)

future_72 = current_time + datetime.timedelta(days=3)






with open(input_xrays_7days, encoding='utf-8') as a:
    # 读取文件
    result = json.load(a)

    # 做第一遍预处理，提取需要预测的未来整天数据
    re_list = []
    for i in result:
        # print(i[time][0:10])
        if i[time][0:10] in pre_data_time_span:
            re_list.append(i)
    # print(re_list)

    # 定义一个数组
    new_list = []
    # 循环遍历，筛选'0.1-0.8nm'波长的重新组成字典构成的列表
    for j in re_list:  # j 是字典
        if channel in j.values():
            new_list.append(j)
    # print(new_list)
    #
    # 构建由时间和通量组成的一个字典
    res = dict()
    for j in new_list:
        res[j['time_tag']] = j['flux']
    # print(res)
    #
    # # 提取当天的峰值，对应时间，等级颜色
    # 提取当天数据

    res_current = {key: value for key, value in res.items() if key[0:10] in current_time.strftime('%Y-%m-%d')}

    t0 = Function_alarm(UNIT_ID, DEVICE_ID, STA_ID, res_current, ALARM_TIME[0], dict_level)
    # print(t0)
    #
    res_future_24 = {key: value for key, value in res.items() if key[0:10] in future_24.strftime('%Y-%m-%d')}
    t1=Function_alarm(UNIT_ID, DEVICE_ID, STA_ID, res_future_24, ALARM_TIME[1], dict_level)
    # print(t1)

    res_future_48 = {key: value for key, value in res.items() if key[0:10] in future_48.strftime('%Y-%m-%d')}
    t2 = Function_alarm(UNIT_ID, DEVICE_ID, STA_ID, res_future_48, ALARM_TIME[2], dict_level)
    # print(t2)

    res_future_72 = {key: value for key, value in res.items() if key[0:10] in future_72.strftime('%Y-%m-%d')}
    t3 = Function_alarm(UNIT_ID, DEVICE_ID, STA_ID, res_future_72, ALARM_TIME[3], dict_level)
    # print(t3)


    list_future_3days = bulid_3days_function(t1,t2,t3)

    # print(list_future_3days)

    config = configparser.ConfigParser()
    config.read("xw.ini", encoding="utf-8")
    dmsql_cfg = dict(config.items("dmsql"))
    text = func_conn_dmpython(dmsql_cfg)








