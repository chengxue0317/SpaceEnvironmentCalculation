'''
传参：
    python magnetic_global_V1.py "2021-10-20 12:00:00" 770 D:\空间天气\空间动力学计算\code\chaos_submit\out
'''

import os
import sys
pwd_z=os.getcwd()  #主项目路径
pwd_c=sys.path[0]  #我的项目路径
os.chdir(pwd_c)
import time
start=time.time()
import datetime
import pandas as pd
import numpy as np
from chaosmagpy.data_utils import mjd2000,timestamp
from chaosmagpy import load_CHAOS_matfile
import glob
import matplotlib.pyplot as plt
#from mpl_toolkits.basemap import Basemap
import time
import warnings
warnings.filterwarnings('ignore')
#from configparser import ConfigParser
#print( glob.glob('data/CHAOS-*.mat'))
# FILEPATH_CHAOS = glob.glob('data/CHAOS-*.mat')[0]
#FILEPATH_CHAOS = glob.glob('/tmp/lizw/data/CHAOS-*.mat')[0]
R_REF = 6371.2 #地球平均半径
'''def getB(t, radius, theta, phi):
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
    # B_radius = B_radius_int + B_radius_ext
    # B_theta = B_theta_int + B_theta_ext
    # B_phi = B_phi_int + B_phi_ext
    # B = np.sqrt(B_radius ** 2 + B_theta ** 2 + B_phi ** 2)
    B_int=np.sqrt(B_radius_int ** 2 + B_theta_int ** 2 + B_phi_int ** 2)
    B_ext = np.sqrt(B_radius_ext ** 2 + B_theta_ext ** 2 + B_phi_ext ** 2)
    return B_int,B_ext'''

def main(starttime,endtime,alt,outpath):
   # try:
        start_lat=90
        end_lat=-90
        start_lon=-180
        end_lon=180
        lon_interval=1
        lat_interval=-1

        outpath=os.path.join(outpath,starttime.replace('-', '').replace(' ', '').replace(':', '') + '-' + endtime.replace('-', '').replace(' ', '').replace(':', '')+'-'+str(alt)+'km')

        if os.path.exists(outpath):
            import shutil
            shutil.rmtree(outpath)
            os.mkdir(outpath)
        else:
            os.mkdir(outpath)
        stdt=datetime.datetime.strptime(starttime,"%Y-%m-%d %H:%M:%S")
        endt=datetime.datetime.strptime(endtime,"%Y-%m-%d %H:%M:%S")
        traveldt=(endt-stdt)/10
        dts=pd.date_range(stdt,endt,freq=traveldt)
        mjdtime = mjd2000(dts)
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
        # time in mjd2000
        process_list = []
        for mtime in mjdtime:
            index=list(mjdtime).index(mtime)
            from multiprocessing import Process
            from magnetic_global_V2_subpro import main as promain
            p =Process(target=promain, args=(mtime,calat,lon,lat,radius,alt,outpath,index))
            p.start()
            process_list.append(p)
        for i in process_list:
            p.join()


        print(outpath)
        os.chdir(pwd_z)
   # except:
   #     print(False)
if __name__ in "__main__":
    import sys
    k = sys.argv[1:]
    main(k[0][1:-1],k[1][1:-1], eval(k[2]), k[3])

    # import sys
    # k = sys.argv[1:]
    # main(k[0], eval(k[1]), k[2])

    # starttime='2012-01-18 00:00:00'
    # endtime='2012-11-11 00:00:01'
    # alt=10  #km
    # outpath=r'.\out'
    # main(starttime,endtime,alt,outpath)
    # print('time/s:',time.time()-start)
