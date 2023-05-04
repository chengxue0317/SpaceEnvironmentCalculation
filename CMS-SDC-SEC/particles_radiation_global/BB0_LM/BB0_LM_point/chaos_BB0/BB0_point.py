#!/usr/bin/python3
# -*- coding: utf-8 -*-
# @Time    : 2022/12/7 9:09
# @Author  : ziwei
# @Email   : 995829916@qq.com
# @Software: PyCharm
import dmPython
import datetime
import shutil
import numpy as np
from chaosmagpy.data_utils import mjd2000
from chaosmagpy import load_CHAOS_matfile
import glob
from dateutil.relativedelta import relativedelta
import warnings
warnings.filterwarnings('ignore')
import os
#from tqdm import tqdm
#import matplotlib.pyplot as plt
#from mpl_toolkits.basemap import Basemap
import time
#from sqlalchemy import create_engine
import pandas as pd
from string import Template
from configparser import ConfigParser
FILEPATH_CHAOS = glob.glob('./chaos_BB0/data/CHAOS-*.mat')[0]
R_REF = 6371.2 #地球平均半径

def Connect_SQL(iniPath):
    cfg = ConfigParser()
    cfg.read(iniPath)
    sql_t=dict(cfg.items('sqltype'))['sqltype']
    while sql_t == 'mysql':
        import pymysql
        sql_cfg = dict(cfg.items("mysql"))
        conn = pymysql.connect(  #MySQLdb
            host=sql_cfg['host'],
            port=int(sql_cfg['port']),
            user=sql_cfg['user'],
            passwd=sql_cfg['password'],
            db=sql_cfg['db'],
            charset="utf8")
        cur = conn.cursor()  # 使用cursor()方法创建一个游标对象
        if cur:
            print('>>>mysql数据库连接成功<<<')
        break
    while sql_t == 'dmsql':
        
        sql_cfg = dict(cfg.items("dmsql"))
        conn = dmPython.connect(
            user=sql_cfg['user'],
            password=sql_cfg['password'],
            server=sql_cfg['server'],
            port=sql_cfg['port'])
        cur = conn.cursor()
        if cur:
            print('>>>dmsql数据库连接成功<<<')
        break
    return conn, cur

def getB(t, radius, theta, phi):
    # load the CHAOS model
    model = load_CHAOS_matfile(FILEPATH_CHAOS)
    # print(model)

    # print('Computing core field.')
    B_core = model.synth_values_tdep(t, radius, theta, phi,grid=False)  #,grid=True 返回随时间变化的径向、余纬、垂直方向地磁分量

    # print('Computing crustal field up to degree 110.')
    B_crust = model.synth_values_static(radius, theta, phi, nmax=110)  # 返回静态内磁场径向、余纬、垂直方向地磁场

    # complete internal contribution
    B_radius_int = B_core[0] + B_crust[0]
    B_theta_int = B_core[1] + B_crust[1]
    B_phi_int = B_core[2] + B_crust[2]

    # print('Computing field due to external sources, incl. induced field: GSM.')
    B_gsm = model.synth_values_gsm(t, radius, theta, phi, source='all',grid=False)  # 返回远磁层磁场径向、余纬、垂直方向地磁场

    # print('Computing field due to external sources, incl. induced field: SM.')
    B_sm = model.synth_values_sm(t, radius, theta, phi, source='all',grid=False)  # 返回进磁层磁场径向、余纬、垂直方向地磁场

    # complete external field contribution
    B_radius_ext = B_gsm[0] + B_sm[0]
    B_theta_ext = B_gsm[1] + B_sm[1]
    B_phi_ext = B_gsm[2] + B_sm[2]

    # complete forward computation
    B_radius = B_radius_int + B_radius_ext
    B_theta = B_theta_int + B_theta_ext
    B_phi = B_phi_int + B_phi_ext

    B = np.sqrt(B_radius ** 2 + B_theta ** 2 + B_phi ** 2)
    return B
def save_RCindex():
    dt=datetime.datetime.now().strftime('%Y%m%d')
    from pathlib import Path
    my_rc=Path(os.path.join('./chaos_BB0/rc',dt+'_RC_file.h5'))
    if my_rc.is_file():
        pass
    else:
        if not os.path.exists(os.path.dirname(my_rc)):
            os.makedirs(os.path.dirname(my_rc))
        else:
            shutil.rmtree(os.path.dirname(my_rc))
            os.mkdir(os.path.dirname(my_rc))
        from chaosmagpy.data_utils import save_RC_h5file
        save_RC_h5file(my_rc)
    import chaosmagpy as cp
    cp.basicConfig['file.RC_index'] = my_rc
def main(time,alt,lon,lat):
    for i,t in enumerate(time) :
        t=datetime.datetime.strptime(t,"%Y-%m-%d %H:%M:%S")
        if t>datetime.datetime.strptime('2022-08-31 00:00:00', "%Y-%m-%d %H:%M:%S"):
            t=t-relativedelta(years=20)
        mjdtime = mjd2000(t)
        time[i]=mjdtime
    # give inputs
    calat=90-np.array(lat)  # colat in deg
    lon = np.array(lon)  # longitude in deg
    # grid_lon,grid_calat=np.meshgrid(lon,calat)

    # calat=np.squeeze(grid_calat.reshape([1,-1]))
    # lon=np.squeeze(grid_lon.reshape([1,-1]))
    radius = np.array(alt)  + R_REF  # alt from altitude in km
    mjdtime = np.array(time)  # time in mjd2000

    # for i in tqdm( range(0, len(calat), batch_size),desc='Processing'):
    theta = calat
    phi = lon
    radius = radius
    mtime = mjdtime

    b=getB(mtime, radius, theta, phi)
    theta_ = [90] * len(theta)
    b0 = getB(mtime, radius, theta_, phi)
    bb0 = b / b0
    # print(bb0[0])
    return list(bb0)