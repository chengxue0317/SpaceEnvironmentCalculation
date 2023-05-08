import argparse
import os
import random
import sys
from pathlib import Path

import dmPython
import numpy as np

filedir = str(Path(__file__).resolve().parents[1])
sys.path.append(filedir)
filedir += '/plots'

from ap8ae8 import *
import pandas as pd

def solar_activity(date):
    """
    调用数据库中F10.7数据，判断太阳活跃程度
    """

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

    sql = "SELECT F107 FROM SEC_F107_FLUX WHERE TIME = '" + date[0:10] + " 00:00:00.000'"
    cursor.execute(sql)
    f107 = cursor.fetchone()

    if f107[0] < 90:
        return 0
    elif f107[0] > 180:
        return 1
    else:
        return -1


def main(date, altitude, whatf, whichm, solar_activity_num):
    model_list = {'ae8': ['ae8min', 'ae8max'], 'ap8': ['ap8min', 'ap8max']}
    lon = np.arange(-180, 180, 1)
    lat = np.arange(-90, 90, 1)
    nlon = len(lon)
    nlat = len(lat)

    data = pd.DataFrame(index=range(nlat * nlon),
                        columns=['lon', 'lat', 'alt', 'date'])
    for i in range(len(lon)):
        data.loc[len(lat) * i:len(lat) * (i + 1) - 1, 'lon'] = lon[i]
        data.loc[len(lat) * i:len(lat) * (i + 1) - 1, 'lat'] = lat.tolist()
    data['date'] = date
    data['alt'] = altitude

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
        elif whichm == 'ae8':
            df = pd.read_csv(path1 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'E1', 'E2', 'E3', 'E4', 'E5'])
            df1 = pd.read_csv(path2 + '/flux.txt', names=['lat', 'lon', 'alt', 'date', 'E1', 'E2', 'E3', 'E4', 'E5'])
            df += df1
            df.iloc[:, 4:8] /= 2
            df['lat'] = df1['lat']
            df['lon'] = df1['lon']
            df['date'] = df1['date']
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


if __name__ == '__main__':
    # date = ['2024-12-31 00:00:00', '2012-12-31 00:00:00', '2019-12-31 00:00:00']
    # alt = [900km,489,689] #km
    # lon = [46.9,78,169]
    # lat = [56,79,23]
    parser = argparse.ArgumentParser()
    # 使用 `-` 来指明短选项
    # 使用 `--` 来指明可选项
    parser.add_argument("-d", "--date", type=str, help="date and time, ex: 2021-12-31 00:00:00",
                        default="2022-11-07 11:28:34")
    parser.add_argument("-a", "--altitude", type=int, help="satellite altitude, unit: km", default=1151)
    parser.add_argument("-f", "--whatf", type=str, help="differential - 1, range - 2, above - 3", default='above')
    parser.add_argument("-m", "--whichm", type=str, help="ae8 - 1, ap8 - 2", default='ap8')
    args = parser.parse_args()
    args.date = args.date.repalce('"', '')

    san = solar_activity(args.date)
    main(args.date, args.altitude, args.whatf, args.whichm, san)