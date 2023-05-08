"""
@File    ：sat_orbit_rad.py.py
@Author  ：Wangrry
@Date    ：2023/03/07/007 14:18 
The purpose of this file is .

In order to achieve the

Typical usage example:

  foo = ClassFoo()
  bar = foo.FunctionBar()
"""
import dmPython
import numpy as np

from pathlib import Path
import sys
import random
import os

import argparse
filedir = str(Path(__file__).resolve().parents[1])
sys.path.append(filedir)
filedir += '/plots'

from ap8ae8 import *
import pandas as pd
import cmaps

cmap = cmaps.WhiteBlueGreenYellowRed

def solar_activity(date):
    sql = "SELECT F107 FROM SEC_F107_FLUX where TIME = '" + \
        date[0:10] + " 00:00:00.000'"
    cursor.execute(sql)
    f107 = cursor.fetchone()
    if f107[0] < 90:
        return 0
    elif f107[0] > 180:
        return 1
    else:
        return -1


def get_satellite_trace(satellite_id, start_time, end_time):
    sql = "select LAT, LON, ALT, TIME from SEC_SATELLITE_LLA where SAT_ID = '" + satellite_id + "' and TIME between '" \
          + start_time + "' and '" + end_time + "'"
    cursor.execute(sql)
    vars = cursor.fetchall()
    satellite_orbit_info = pd.DataFrame(
        vars, columns=['lat', 'lon', 'alt', 'date'])
    return satellite_orbit_info


def get_flux(data, whatf, whichm, solar_activity_num):
    data['date'] = data['date'].astype(str)
    model_list = {'ae8': ['ae8min', 'ae8max'], 'ap8': ['ap8min', 'ap8max']}
    if solar_activity_num >= 0:
        path = ap8ae8(data['date'], data['alt'], data['lon'], data['lat'], whatf,
                      model_list[whichm][solar_activity_num])
        return path + '/flux.txt'
    else:
        path1 = ap8ae8(data['date'], data['alt'], data['lon'],
                       data['lat'], whatf, model_list[whichm][0])
        path2 = ap8ae8(data['date'], data['alt'], data['lon'],
                       data['lat'], whatf, model_list[whichm][1])

        df = (pd.read_csv(path1 + '/flux.txt') +
              pd.read_csv(path2 + '/flux.txt'))
        if whichm == 'ap8':
            df = pd.read_csv(
                path1 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'P1', 'P2', 'P3', 'P4', 'P5', 'P6'])
            df1 = pd.read_csv(
                path2 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'P1', 'P2', 'P3', 'P4', 'P5', 'P6'])
            df += df1
            df.iloc[:, 4:9] /= 2
            df['lat'] = df1['lat']
            df['lon'] = df1['lon']
            df['date'] = df1['date']
            df['alt'] = df1['alt']
        elif whichm == 'ae8':
            df = pd.read_csv(
                path1 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'E1', 'E2', 'E3', 'E4', 'E5'])
            df1 = pd.read_csv(
                path2 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'E1', 'E2', 'E3', 'E4', 'E5'])
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
                df.to_csv(filedir + '/' + filenum + '/flux.txt',
                          mode='w', header=None, index=None)
                break
        # print(filedir, filenum)
        return filedir, filenum


def get_sat_orbit_rad(filedir, filenum, whichm, channel):
    model_list = {'ap8': ['lat', 'lon', 'alt', 'date', 'P1', 'P2', 'P3', 'P4', 'P5', 'P6'],
                  'ae8': ['lat', 'lon', 'alt', 'date', 'E1', 'E2', 'E3', 'E4', 'E5']}
    sat_orbit_rad = pd.read_csv(filedir + '/' + filenum + '/flux.txt', names=model_list[whichm])
    sat_orbit_rad = sat_orbit_rad.applymap(lambda x: x.strip() if isinstance(x, str) else x)
    sepcified_sat_orbit_rad = sat_orbit_rad[['lat', 'lon', 'alt', 'date', channel]]
    flux_ln =np.log10(sepcified_sat_orbit_rad[channel])
    rgba = num2rgba(flux_ln)
    rgba = [tuple(l) for l in rgba]
    # rgba = list(map(list, zip(*rgba)))
    sepcified_sat_orbit_rad['rgba'] = rgba
    sepcified_sat_orbit_rad = sepcified_sat_orbit_rad.to_json(orient='records')
    # sepcified_sat_orbit_rad = json.dumps(sepcified_sat_orbit_rad, indent=4, ensure_ascii=False)
    print(sepcified_sat_orbit_rad)



def main(start_time, end_time, sat_name, whatf, whichm, channel):
    san = solar_activity(start_time)
    satellite_orbit_info = get_satellite_trace(sat_name, start_time, end_time)
    filedir, filenum = get_flux(satellite_orbit_info, whatf, whichm, san)
    get_sat_orbit_rad(filedir, filenum, whichm, channel)

def num2rgba(array):
    array = array * 254 /array.max()
    return cmap(array)

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    # 使用 `-` 来指明短选项
    # 使用 `--` 来指明可选项
    parser.add_argument("-n", "--name", type=str, default="XWINC011")
    parser.add_argument("-s", "--start", type=str,
                        default="2022-11-07 18:02:52.000")
    parser.add_argument("-e", "--end", type=str, default="2022-11-07 19:04:31.000")
    parser.add_argument("-f", "--whatf", type=str,
                        help="differential - 1, range - 2, above - 3", default='above')
    parser.add_argument("-m", "--whichm", type=str,
                        help="ae8 - 1, ap8 - 2", default='ap8')
    parser.add_argument("-c", "--channel", type=str,
                        help="ae8: 5 channels E1-E5; ap8: 6 channels P1-P6", default='P3')
    args = parser.parse_args()
    args.start = args.start.replace('"', '')
    args.end = args.end.replace('"', '')

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
    
    try:
        iniPath = os.path.dirname(os.path.abspath(__file__)).split('/CMS-SDC-SEC')[0]+'/DLXJS_DB.ini'
        cursor,conn = Connect_SQL(iniPath)
    except (dmPython.Error, Exception) as err:
        print(err)
        
    main(args.start, args.end, args.name, args.whatf, args.whichm, args.channel)
