#!/usr/bin/python3
# -*- coding: utf-8 -*-
# @Time    : 2022/12/7 9:09
# @Author  : ziwei
# @Email   : 995829916@qq.com
# @Software: PyCharm
import numpy as np
import datetime
# from mpl_toolkits.mplot3d import Axes3D
from IRBEM_LM.IRBEM import MagFields
import pandas as pd
import warnings

warnings.filterwarnings("ignore")
iniPath = 'xw.ini'
from configparser import ConfigParser


def Connect_SQL(iniPath):
    cfg = ConfigParser()
    cfg.read(iniPath)
    sql_t = dict(cfg.items('sqltype'))['sqltype']
    while sql_t == 'mysql':
        import pymysql
        sql_cfg = dict(cfg.items("mysql"))
        conn = pymysql.connect(  # MySQLdb
            host=sql_cfg['host'],
            port=int(sql_cfg['port']),
            user=sql_cfg['user'],
            passwd=sql_cfg['password'],
            db=sql_cfg['db'],
            charset="utf8")
        cur = conn.cursor()  # 使用cursor()方法创建一个游标对象
        # if cur:
        #     print('>>>mysql数据库连接成功<<<')
        break
    while sql_t == 'dmsql':
        import dmPython
        sql_cfg = dict(cfg.items("dmsql"))
        conn = dmPython.connect(
            user=sql_cfg['user'],
            password=sql_cfg['password'],
            server=sql_cfg['server'],
            port=sql_cfg['port'])
        cur = conn.cursor()
        # if cur:
        #     print('>>>dmsql数据库连接成功<<<')
        break
    return conn, cur


def out_date(year, day):
    fir_day = datetime.datetime(year, 1, 1)
    zone = datetime.timedelta(days=day - 1)
    return datetime.datetime.strftime(fir_day + zone, "%Y-%m-%d")


# A few test and visualization scripts.
def main(time, alt, lon, lat, test_datetime=True):
    """
    This test function will test is the make_lstar1() function works correctly.
    If you run this, the output should be the follwing. 
    
    {'MLT': [8.34044753112316], 'xj': [9.898414822276834], 'lstar': [-1e+31],
    'Lm': [4.631806704496794], 'bmin': [268.5087756309121], 
    'blocal': [39730.828875776126]}
    """

    dt = [datetime.datetime.strptime(t, "%Y-%m-%d %H:%M:%S") for t in time]
    # give inputs
    lat = np.array(lat)  # colat in deg
    lon = np.array(lon)  # longitude in deg

    conn, cur = Connect_SQL(iniPath)
    # sql = "SELECT KP FROM SEC_KP_INDEX where TIME = '%s'" % (dt.strftime('%Y-%m-%d'))
    if len(dt) > 1:
        sql = "SELECT KP FROM SEC_KP_INDEX where TIME in %s" % \
              (str(tuple([t.strftime('%Y-%m-%d') for t in dt])))
    else:
        sql = "SELECT KP FROM SEC_KP_INDEX where TIME in %s" % (
            f"('{dt[0].strftime('%Y-%m-%d')}')")
    data = pd.read_sql(sql, conn)
    kp = data.mean(axis=1).tolist()
    # model = MagFields(options=[0, 0, 0, 0, 0], verbose=True)
    model = MagFields(options=[0, 0, 0, 0, 0], verbose=True)

    LLA = {}
    if test_datetime:
        LLA['dateTime'] = dt
    else:
        LLA['dateTime'] = ['2015-02-02T06:12:43', '2015-02-02T06:12:43']
    LLA['x2'] = lat
    LLA['x3'] = lon
    LLA['x1'] = alt

    maginput = {'Kp': kp}

    model.make_lstar(LLA, maginput)
    Lm_out = model.make_lstar_output['Lm']
    blocal = model.make_lstar_output['blocal']
    bmin = model.make_lstar_output['bmin']
    # print(Lm_out)
    return Lm_out, blocal, bmin

# if __name__ == '__main__':
#     time = '2023-11-20 00:00:00'
#     alt = 450 # km
#     lon=109.8
#     lat=45.5
#     main(time, alt,lon,lat)
