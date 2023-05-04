
import time
start=time.time()
import datetime
import pandas as pd
import numpy as np
from chaosmagpy.data_utils import mjd2000,timestamp
from chaosmagpy import load_CHAOS_matfile
import glob
import cartopy.crs as ccrs
import cartopy.feature as cfeature
from matplotlib.colors import LogNorm
import matplotlib.pyplot as plt
#from mpl_toolkits.basemap import Basemap
import time
import warnings
warnings.filterwarnings('ignore')
FILEPATH_CHAOS = glob.glob('data/CHAOS-*.mat')[0]
# from multiprocessing import Process
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

    B_int=np.sqrt(B_radius_int ** 2 + B_theta_int ** 2 + B_phi_int ** 2)
    B_ext = np.sqrt(B_radius_ext ** 2 + B_theta_ext ** 2 + B_phi_ext ** 2)
    # B_ext = B_phi_int
    return B_int,B_ext
def main(mtime,calat,lon,lat,radius,alt,outpath,index):
    mtime = np.array([mtime] * len(calat))
    TIME, ALT, LAT, LON, B_int,B_ext, B0, BB0 = [], [], [], [],  [],  [], [],[]
    for li in range(0,len(lon),len(lon)//2):
        loni=lon[li:li+len(lon)//2]
        theta = calat
        phi = loni
        radius = radius

        b_int,b_ext=getB(mtime, radius, theta, phi)

        B_int.append(b_int)
        B_ext.append(b_ext)
    # print('计算/s:', time.time() - start)
    #保存
    def Bint_tofig(B,type):
        import os
        B_ = np.hstack((B[0], B[1]))
        fig = plt.figure(figsize=[6, 3])  #
        from mpl_toolkits.basemap import Basemap
        map = Basemap(llcrnrlon=-180, urcrnrlon=179, llcrnrlat=-89, urcrnrlat=90, resolution='c')  # projection='moll',
        map.drawcoastlines(linewidth=0.1)#,color='0.5'
        Lon, Lat = np.meshgrid(lon, lat)
        X, Y = map(Lon, Lat)
        lvls = np.arange(20000, 60000, 50)
        # print(B_.min(), B_.max())
        C = map.contourf(X, Y, B_, levels=lvls, alpha=1, cmap=plt.cm.rainbow,extend='both')  # ,levels=lvls,cmap=plt.cm.rainbow)#
        # C = plt.pcolormesh(lon, lat, B_,alpha=1,norm=LogNorm(vmin=10000, vmax=50000), cmap=plt.cm.rainbow)  # ,levels=lvls,cmap=plt.cm.rainbow)#
        plt.subplots_adjust(top=1, bottom=0, left=0, right=1, hspace=0, wspace=0)
        plt.axis('off')
        fig.patch.set_alpha(0.5)
        # plt.colorbar(orientation='horizontal')
        os.path.exists(os.path.join(outpath,''))
        # name=type+timestamp(mtime).astype(datetime.datetime)[0].strftime('%Y-%m-%d_%H-%M-%S')+'_' + str(alt) + 'km.png'
        name=type+str(index) + '.png'
        # print('画图:', time.time() - start)
        plt.savefig(os.path.join(outpath, name), format='png',dpi=200, pad_inches=0.0)
        # print('保存:', time.time() - start)

    def Bext_tofig(B, type):
        import os
        B_ = np.hstack((B[0], B[1]))
        fig = plt.figure(figsize=[6, 3])#
        from mpl_toolkits.basemap import Basemap
        map = Basemap(llcrnrlon=-180, urcrnrlon=179, llcrnrlat=-89, urcrnrlat=90, resolution='c')  # projection='moll',
        map.drawcoastlines(linewidth=0.1)#,color='0.5'
        Lon, Lat = np.meshgrid(lon, lat)
        X, Y = map(Lon, Lat)
        # lvls = np.linspace(0.1,30.,100)
        lvls = np.arange(0.,50.,0.05)
        C = map.contourf(X, Y, B_, levels=lvls, alpha=1, cmap=plt.cm.bwr,extend='both')  # ,levels=lvls,cmap=plt.cm.rainbow)#
        plt.subplots_adjust(top=1, bottom=0, left=0, right=1, hspace=0, wspace=0)
        plt.axis('off')
        # plt.alpha = 0.2
        fig.patch.set_alpha(0.5)
        # plt.colorbar(orientation='horizontal')
        os.path.exists(os.path.join(outpath, ''))
        # name = type + timestamp(mtime).astype(datetime.datetime)[0].strftime('%Y-%m-%d_%H-%M-%S') + '_' + str(alt) + 'km.png'
        name = type + str(index) + '.png'
        # print('画图:', time.time() - start)
        plt.savefig(os.path.join(outpath, name), format='png', dpi=200, pad_inches=0.0)
        # print('保存:', time.time() - start)

    Bint_tofig(B_int, 'int_')
    Bext_tofig(B_ext, 'ext_')
