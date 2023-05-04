'''
传参：
    python magnetic_global_V1.py "2021-10-20 12:00:00" 770 D:\空间天气\空间动力学计算\code\chaos_submit\out
'''

import datetime
import numpy as np
from chaosmagpy.data_utils import mjd2000
from chaosmagpy import load_CHAOS_matfile
import glob
import matplotlib.pyplot as plt
from mpl_toolkits.basemap import Basemap
import time
import warnings
warnings.filterwarnings('ignore')
from configparser import ConfigParser
FILEPATH_CHAOS = glob.glob('/export/chaos_submit/data/CHAOS-*.mat')[0]
R_REF = 6371.2 #地球平均半径
def getB(t, radius, theta, phi):
    # load the CHAOS model
    model = load_CHAOS_matfile(FILEPATH_CHAOS)
    # print(model)

    # print('Computing core field.')
    B_core = model.synth_values_tdep(t, radius, theta, phi,grid=True)  #,grid=True 返回随时间变化的径向、余纬、垂直方向地磁分量

    # print('Computing crustal field up to degree 110.')
    B_crust = model.synth_values_static(radius, theta, phi, nmax=110)  # 返回静态内磁场径向、余纬、垂直方向地磁场

    # complete internal contribution
    B_radius_int = B_core[0] + B_crust[0]
    B_theta_int = B_core[1] + B_crust[1]
    B_phi_int = B_core[2] + B_crust[2]

    # print('Computing field due to external sources, incl. induced field: GSM.')
    B_gsm = model.synth_values_gsm(t, radius, theta, phi, source='all',grid=True)  # 返回远磁层磁场径向、余纬、垂直方向地磁场

    # print('Computing field due to external sources, incl. induced field: SM.')
    B_sm = model.synth_values_sm(t, radius, theta, phi, source='all',grid=True)  # 返回进磁层磁场径向、余纬、垂直方向地磁场

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

def main(t,alt,outpath):
    try:
        start_lat=90
        end_lat=-90
        start_lon=-180
        end_lon=180
        lon_interval=1
        lat_interval=-1
        dt=datetime.datetime.strptime(t,"%Y-%m-%d %H:%M:%S")
        mjdtime = mjd2000(dt.year, dt.month, dt.day,dt.hour,dt.minute,dt.second)
        lat,lon=[],[]
        [lat.append(i) for i in np.arange(start_lat,end_lat,lat_interval)]
        calat=[90-i for i in lat]
        [lon.append(i) for i in np.arange(start_lon,end_lon,lon_interval)]

        # give inputs
        calat = np.array(calat)  # colat in deg
        lon = np.array(lon)  # longitude in deg
        # grid_lon,grid_calat=np.meshgrid(lon,calat)

        # calat=np.squeeze(grid_calat.reshape([1,-1]))
        # lon=np.squeeze(grid_lon.reshape([1,-1]))
        radius = np.array([alt]*len(calat)) + R_REF  # alt from altitude in km
        mjdtime = np.array([mjdtime]*len(calat))  # time in mjd2000
        TIME, ALT, LAT, LON, B, B0, BB0 = [], [], [], [],  [],  [], []
        for li in range(0,len(lon),len(lon)//2):
            loni=lon[li:li+len(lon)//2]
            # for i in tqdm( range(0, len(calat), batch_size),desc='Processing'):
            theta = calat
            phi = loni
            radius = radius
            mtime = mjdtime

            b=getB(mtime, radius, theta, phi)
            theta_ = [90] * len(theta)
            b0 = getB(mtime, radius, theta_, phi)
            bb0 = b / b0

            B.append(b)
            # B0.append(b0)
            # BB0.append(bb0)

        #入库
        # BB0 = np.hstack((BB0[0], BB0[1])).flatten('A').astype(str)
        # grid_lon, grid_lat = np.meshgrid(lon, lat)
        # grid_lon=grid_lon.flatten('A').astype(str)
        # grid_lat=grid_lat.flatten('A').astype(str)
        # TIME=[dt.strftime("%Y-%m-%d %H:%M:%S")]*len(BB0)
        # ALT=list(map(str,[alt]*len(BB0)))
        # DATA=list(zip(TIME, ALT,grid_lon,grid_lat,BB0))
        # conn, cur=Connect_SQL(iniPath)
        # sql = "INSERT INTO SDC_BB0_GLOBAL (TIME, ALT,LON,LAT,BB0) VALUES (?, ?, ?, ?, ?)"
        # cur.executemany(sql,DATA)
        # conn.commit()

        #保存
        import os
        B_ = np.hstack((B[0], B[1]))
        fig = plt.figure(figsize=[2, 1])
        map = Basemap(llcrnrlon=-180, urcrnrlon=179, llcrnrlat=-89, urcrnrlat=90, resolution='c')  # projection='moll',
        # map.drawcoastlines(linewidth=0.2)
        # map.drawmeridians(np.arange(-180, 180, 30))
        # map.drawparallels(np.arange(-90, 90, 30))
        Lon, Lat = np.meshgrid(lon, lat)
        X, Y = map(Lon, Lat)
        lvls = np.linspace(B_.min(), B_.max(), 5000)
        C = map.contourf(X, Y, B_, levels=lvls, alpha=1, cmap=plt.cm.rainbow)  # ,levels=lvls,cmap=plt.cm.rainbow)#
        # map.colorbar(C,'bottom',ticks=np.arange(15000,75000,5000))
        # map.colorbar(C,'bottom')
        # plt.title(t+" magnetic field")
        plt.subplots_adjust(top=1, bottom=0, left=0, right=1, hspace=0, wspace=0)
        plt.axis('off')
        fig.patch.set_alpha(0.5)
        name=t.replace(' ', '_').replace(':', '-') + '_' + str(alt) + 'km.png'
        plt.savefig(os.path.join(outpath, name), format='png',dpi=300, pad_inches=0.0)
        print(t.replace(' ', '_').replace(':', '-') + '_' + str(alt) + 'km.png')
    except:
        print(False)


if __name__ in "__main__":
    import sys
    k = sys.argv[1:]
    main(k[0][1:-1], eval(k[1]), k[2])
    # start=time.time()
    # dt='2021-10-20 00:00:00'
    # alt=860  #km
    # outpath=r'D:\空间天气\空间动力学计算\code\chaos_submit\out'
    # main(dt,alt,outpath)
    # print(time.time()-start)
