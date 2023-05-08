"""
@File    ：orbital_plane.py.py
@Author  ：Wangrry
@Date    ：2023/03/06/006 14:55 
The purpose of this file is .

In order to achieve the

Typical usage example:

  foo = ClassFoo()
  bar = foo.FunctionBar()
"""
import dmPython
import argparse
import copy
import os
import random
import sys
from pathlib import Path

filedir = str(Path(__file__).resolve().parents[1])
sys.path.append(filedir)
filedir += '/plots'

from ap8ae8 import *
import pandas as pd


def solar_activity(date):
    
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

    iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
    cursor,conn = Connect_SQL(iniPath)
    sql = "SELECT F107 FROM SEC_F107_FLUX where TIME = '" + date[0:10] + " 00:00:00.000'"
    cursor.execute(sql)
    f107 = cursor.fetchone()
    if f107[0] < 90:
        return 0
    elif f107[0] > 180:
        return 1
    else:
        return -1


def get_satellite_trace(satellite_id, start_time):
    properties = {'user': 'SDC', 'password': 'sdc123456', 'server': '219.145.62.54', 'port': 15236,
              'autoCommit': True, }
    conn = dmPython.connect(**properties)
    cursor = conn.cursor()
    sql = "select LAT, LON, ALT, TIME from SEC_SATELLITE_LLA where SAT_ID = '" + satellite_id + "' and TIME >= '" \
          + start_time + "' order by TIME limit 100"
    cursor.execute(sql)
    vars = cursor.fetchall()
    satellite_info = pd.DataFrame(vars, columns=['lat', 'lon', 'alt', 'date'])
    satellite_trace = copy.copy(satellite_info)
    alt = satellite_info['alt'][0]
    alt_bot = int(alt - (alt % 10) - 400)
    alt_top = int(alt - (alt % 10) + 400)
    data = pd.DataFrame(columns=['lat', 'lon', 'alt', 'date'])
    for height in range(alt_bot, alt_top, 5):
        satellite_info['alt'] = height
        data = pd.concat([data, satellite_info])
    data = data.reset_index(drop=True)
    return data, satellite_trace


def get_flux(data, whatf, whichm, solar_activity_num):
    data['date'] = data['date'].astype(str)
    model_list = {'ae8': ['ae8min', 'ae8max'], 'ap8': ['ap8min', 'ap8max']}
    if solar_activity_num >= 0:
        path = ap8ae8(data['date'], data['alt'], data['lon'], data['lat'], whatf,
                      model_list[whichm][solar_activity_num])
        return path + '/flux.txt'
    else:
        path1 = ap8ae8(data['date'], data['alt'], data['lon'], data['lat'], whatf, model_list[whichm][0])
        path2 = ap8ae8(data['date'], data['alt'], data['lon'], data['lat'], whatf, model_list[whichm][1])

        df = (pd.read_csv(path1 + '/flux.txt') + pd.read_csv(path2 + '/flux.txt'))
        if whichm == 'ap8':
            df = pd.read_csv(path1 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'P1', 'P2', 'P3', 'P4', 'P5', 'P6'])
            df1 = pd.read_csv(path2 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'P1', 'P2', 'P3', 'P4', 'P5', 'P6'])
            df += df1
            df.iloc[:, 4:9] /= 2
            df['lat'] = df1['lat']
            df['lon'] = df1['lon']
            df['date'] = df1['date']
            df['alt'] = df1['alt']
        elif whichm == 'ae8':
            df = pd.read_csv(path1 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'E1', 'E2', 'E3', 'E4', 'E5'])
            df1 = pd.read_csv(path2 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'E1', 'E2', 'E3', 'E4', 'E5'])
            df += df1
            df.iloc[:, 4:8] /= 2
            df['lat'] = df1['lat']
            df['lon'] = df1['lon']
            df['date'] = df1['date']
            df['alt'] = df1['alt']
        # exit()
        os.system('rm -rf ' + path1)
        os.system('rm -rf ' + path2)
        while True:
            filenum = str(random.randint(1, 99999999))
            if not os.path.exists(filedir + '/' + filenum):
                # 创建文件夹供不同用户使用
                os.system('mkdir ' + filedir + '/' + filenum)
                df.to_csv(filedir + '/' + filenum + '/flux.txt', mode='w', header=None, index=None)
                break
        # print(filedir, filenum)
        return filedir, filenum


def main(start_time, sat_name, whatf, whichm):
    san = solar_activity(start_time)
    data, satellite_trace = get_satellite_trace(sat_name, start_time)
    filedir, filenum = get_flux(data, whatf, whichm, san)
    satellite_trace.to_csv(filedir + '/' + filenum + '/sat_trace.txt', index=False)
    return filedir, filenum

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    # 使用 `-` 来指明短选项
    # 使用 `--` 来指明可选项
    parser.add_argument("-n", "--name", type=str, default="XWINC011")
    parser.add_argument("-s", "--start", type=str, default="2022-11-07 18:02:52.000")
    parser.add_argument("-f", "--whatf", type=str, help="differential - 1, range - 2, above - 3", default='above')
    parser.add_argument("-m", "--whichm", type=str, help="ae8 - 1, ap8 - 2", default='ap8')
    args = parser.parse_args()
    args.start = args.start.replace('"', '')
    main(args.start, args.name, args.whatf, args.whichm)
