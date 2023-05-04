#!/usr/bin/python
# -*- coding = utf-8 -*-


import sys
# import matplotlib
# matplotlib.use('Qt5Agg')
import pandas as pd
# import MySQLdb
from configparser import ConfigParser
import os
import matplotlib.pyplot as plt
from pylab import mpl
from sqlalchemy import create_engine
import numpy as np
import warnings
warnings.filterwarnings('ignore')

mpl.rcParams['font.sans-serif'] = ['SimHei']  
mpl.rcParams['axes.unicode_minus'] = False  


def Connect_SQL(iniPath):
    cfg = ConfigParser()
    cfg.read(iniPath)
    import dmPython
    sql_cfg = dict(cfg.items("dmsql"))
    conn = dmPython.connect(
        user=sql_cfg['user'],
        password=sql_cfg['password'],
        server=sql_cfg['server'],
        port=sql_cfg['port'])
    cur = conn.cursor()
    return conn, cur


def main(startime, endtime, iniPath, outpath):
    # try:
    print('iniPath:', iniPath)
    conn, cur = Connect_SQL(iniPath)
    sql_dst = "SELECT TIME,DST FROM SEC_DST_INDEX WHERE TIME between '%s' and '%s'" % (startime, endtime)
    dst = pd.read_sql(sql_dst, conn)

    sql_kp = "SELECT TIME,KP FROM SEC_KP_INDEX WHERE TIME between '%s' and '%s'" % (
    startime, endtime)
    kp = pd.read_sql(sql_kp, conn)
    kp_t=kp['TIME']
    kp = kp._get_numeric_data()
    kp[kp < - 9999] = np.nan
    kp[kp > 9999] = np.nan
    kp['KP'] = kp.mean(axis=1)

    sql_pare = "SELECT TIME,E2 FROM SEC_PARTICLE_FLUX WHERE TIME between '%s' and '%s'" % (startime, endtime)
    par_e2 = pd.read_sql(sql_pare, conn)

    sql_parp = "SELECT TIME,P1 FROM SEC_PARTICLE_FLUX WHERE TIME between '%s' and '%s'" % (startime, endtime)
    par_p1 = pd.read_sql(sql_parp, conn)

    if not os.path.exists(outpath):
        os.makedirs(outpath)
    plt.subplots(figsize=(16, 10))
    ax = plt.subplot(4, 1, 1)
    if not dst.empty:
        dst = dst[(dst.DST <= 9999) & (dst.DST >= -9999)]
        # dst.replace(-1.0E31 and 1.0E31, np.NaN, inplace=True)
        plt.plot(dst['TIME'], dst['DST'], color='red', linewidth=0.5)
    else:
        plt.text(0.5, 0.5, s="There is no data in the period", transform=ax.transAxes, fontsize=15)
    plt.ylabel('Dst(nT)', fontsize=13)
    plt.xticks(size=11)
    plt.yticks(size=11)
    plt.title('Dst', loc='right', fontsize=12, color='k', fontstyle='italic', fontweight="heavy")

    ax = plt.subplot(4, 1, 2)
    if not kp.empty:
        color=[]
        for i in kp['KP']:
            if i <5:
                color.append('#92D050')
            elif i>=5 and i<6:
                color.append('#F6EB14')
            elif i>=6 and i<7:
                color.append('#FFC800')
            elif i>=7 and i<8:
                color.append('#FF9600')
            else:
                color.append('#FF0000')
        plt.bar(kp_t, kp['KP'], color=color, linewidth=0.5,width=0.1)
    else:
        plt.text(0.5, 0.5, s="There is no data in the period", transform=ax.transAxes, fontsize=15)
    plt.ylabel('Kp', fontsize=13)
    plt.xticks(size=11)
    plt.yticks(size=11)
    plt.title('KP', loc='right', fontsize=12, color='k', fontstyle='italic', fontweight="heavy")

    ax = plt.subplot(4, 1, 3)
    if not par_e2.empty:
        par_e2 = par_e2[(par_e2.E2 <= 9999) & (par_e2.E2 >= -9999)]
        # par.replace(-1.0E31 and 1.0E31, np.NaN, inplace=True)
        plt.plot(par_e2['TIME'], par_e2['E2'], color='red', linewidth=0.5)
    else:
        plt.text(0.5, 0.5, s="There is no data in the period", transform=ax.transAxes, fontsize=15)
    plt.ylabel('E2(count/(cm$^2$*s*sr))', fontsize=13)
    plt.xticks(size=11)
    plt.yticks(size=11)
    plt.title('Energy ≥2Mev Channel electron flux', loc='right', fontsize=12, color='k', fontstyle='italic', fontweight="heavy")

    plt.subplot(4, 1, 4)
    if not par_p1.empty:
        par_p1 = par_p1[(par_p1.P1 <= 9999) & (par_p1.P1 >= -9999)]
        plt.bar(par_p1['TIME'], par_p1['P1'], color='b', linewidth=0.5,width=0.1)
    else:
        plt.text(0.5, 0.5, s="There is no data in the period", transform=ax.transAxes, fontsize=15)
    plt.ylabel('P1(count/(cm$^2$*s*sr))', fontsize=13)
    plt.xticks(size=11)
    plt.yticks(size=11)
    plt.title('3~5MeV integral proton flux', loc='right', fontsize=12, color='k', fontstyle='italic', fontweight="heavy")
    plt.subplots_adjust(hspace=0.3)

    plt.subplots_adjust(left=0.1, right=0.9, bottom=0.1, top=0.9, wspace=0.3, hspace=0.3)
    plt.savefig(os.path.join(outpath, 'week1.png'), dpi=300, bbox_inches='tight')

    # plt.show()


# except:
#     return (False)
if __name__ in '__main__':
    k=sys.argv[1:]
    main(k[0],k[1],k[2],k[3])
    # startime = '2011-11-01 00:00:00'
    # endtime = '2011-11-07 00:00:00'
    # iniPath = r'D:\空间天气\空间动力学计算\code\simple\xw.ini'
    # outpath = r'D:\空间天气\空间动力学计算\code\simple'
    # main(startime, endtime, iniPath, outpath)
