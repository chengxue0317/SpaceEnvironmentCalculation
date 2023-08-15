# -*- coding: utf-8 -*-
import datetime
import os
import sys
import time
import dmPython
from math import ceil
import numpy as np
from configparser import ConfigParser

''' 该代码主要实现对tec数据文件的解析和入库功能，通过TEC数据和数据库配置文件将数据解析入库'''

#将解析后的数据存入数据库中，当数据入库成功后提示“数据入库保存成功！！！！！！！”
def toSql_data(ini_path,data_key,data_value):
    table_name = 'SEC_IONOSPHERIC_TEC'
    iniPath = os.path.abspath(ini_path)
    cfg = ConfigParser()
    cfg.read(iniPath)
    sql_cfg = dict(cfg.items("dmsql"))
    conn = dmPython.connect(
        user=sql_cfg['user'],
        password=sql_cfg['password'],
        server=sql_cfg['server'],
        port=sql_cfg['port'])
    cue = conn.cursor()  # 使用cursor()方法创建一个游标对象
    sql = "insert into %s %s values %s" % (table_name, data_key, data_value)     #将数据存入数据库中
    cue.execute(sql)
    conn.commit()
    print("数据入库保存成功！！！！！！！！！！！")

#按照指定长度均分列表，lst为要切分的列表，num为均分长度
def split_list(lst, num):
    return list(map(lambda x: lst[x * num : x * num + num], list(range(0, ceil(len(lst) / num)))))

#读取txt中的时间数据、经纬度数据、高度数据和TEC数据
def get_data_all(abs_file_path,abs_file_list,ini_path):
    for file in abs_file_list:
        file = open(os.path.join(abs_file_path, file),'r',encoding='UTF-8')
        lines = file.read().split('START OF TEC MAP ')[1:]
        for data in lines:
            # 获取时间数据
            data_time = data.lstrip().split('\n')[0].split()
            year, month, day, hour, minute, second = data_time[0], data_time[1], data_time[2], data_time[3], data_time[4], data_time[5]  # 获取时间数据
            if len(month) == int(2):
                month = data_time[1]
            else:
                month = '0' + data_time[1]
            if len(day) == int(2):
                day = data_time[2]
            else:
                day = '0' + data_time[2]
            if len(hour) == int(2):
                hour = data_time[3]
            else:
                hour = '0' + data_time[3]
            if len(minute) == int(2):
                minute = data_time[4]
            else:
                minute = '0' + data_time[4]
            if len(second) == int(2):
                second = data_time[5]
            else:
                second = '0' + data_time[5]
            time = year + '-' + month + '-' + day + ' ' + hour + ':' + second + ':' + minute  # 获取时间

            # 获取经度数据、纬度数据、时间数据和TEC数据
            data_lon_lat = data.lstrip().split('\n')[1:-2]
            new_data_lon_lat_list = split_list(data_lon_lat, 6)
            for h in range(len(new_data_lon_lat_list)):
                all_lon, all_lat, all_time, all_hight, all_data_list = [], [], [], [], []
                lat_end = new_data_lon_lat_list[h][0].rsplit()[0].replace('-', ' -').split()[0]     # 纬度结束值
                lon_start = new_data_lon_lat_list[h][0].rsplit()[0].replace('-', ' -').split()[1]   # 经度开始值
                lon_end = new_data_lon_lat_list[h][0].rsplit()[1]                                   # 经度结束值
                lon_step = new_data_lon_lat_list[h][0].rsplit()[2]                                  # 经度间隔值
                hight = new_data_lon_lat_list[h][0].rsplit()[3]                                     # 高度
                all_lon = np.arange(float(lon_start), float(lon_end) + float(lon_step), float(lon_step)).astype(str).tolist()  # 经度列表
                all_lat = [lat_end] * len(all_lon)                                                  # 纬度列表
                all_time = [time] * len(all_lon)                                                    # 时间列表
                all_hight = [hight] * len(all_lon)                                                  # 高度列表

                # 获取经度数据、纬度数据、时间数据和电离层TEC数据的字典关键字(key)
                all_lon_key = ['TEC_LON'] * len(all_lon)
                all_lat_key = ['TEC_LAT'] * len(all_lat)
                all_time_key = ['TIME'] * len(all_time)
                all_height_key = ['HEIGHT'] * len(all_hight)
                for g in range(1,len(new_data_lon_lat_list[h])):
                    if g > 0:
                        all_data_list.append(new_data_lon_lat_list[h][g].split())
                new_all_data_list = [element for sublist in all_data_list for element in sublist]    # 利用列表推导式将嵌套列表中的所有元素提取到一个列表中，获取TEC数据列表
                all_data_key = ['VTEC'] * len(new_all_data_list)
                for k in range(len(new_all_data_list)):
                    all_key = [all_time_key[k]] + [all_lon_key[k]] + [all_lat_key[k]] + [all_data_key[k]] + [all_height_key[k]]    #经度、纬度、时间、TEC和高度数据键
                    all_value = [all_time[k]] + [all_lon[k]] + [all_lat[k]] + [new_all_data_list[k]] + [all_hight[k]]              #经度、纬度、时间、TEC和高度数据数值
                    all_key = str(tuple(all_key))
                    all_value = str(tuple(all_value))
                    all_key = all_key.replace("'","")
                    toSql_data(ini_path,all_key,all_value)
        file.close()        #关闭文件

#获取tec文件和数据库配置文件
def get_file(file_path,ini_path):

    abs_file_path = os.path.abspath(file_path)
    abs_ini_path = os.path.abspath(ini_path)
    abs_file_list, abs_ini_list = [], []

    for dirpath, dirnames, filenames in os.walk(abs_file_path):
        for filename in filenames:
            abs_file_list.append(filename)
    for dirpath, dirnames, filenames in os.walk(abs_ini_path):
        for filename in filenames:
            abs_ini_list.append(filename)
    return abs_file_path,abs_file_list,abs_ini_list

#主函数
def main(file_path,ini_path):
    ss = time.time()
    abs_file_path,abs_file_list,abs_ini_list = get_file(file_path,ini_path)
    get_data_all(abs_file_path, abs_file_list,ini_path)
    ee = time.time()
    duration = ee - ss
    print("代码运行时长为：%.2f秒" % duration)

if __name__ == '__main__':
    # file_path = '../data/'
    # ini_path = '../DLXJS_DB.ini'

    file_path = sys.argv[1]
    ini_path = sys.argv[2]
    main(file_path,ini_path)
