"""
@File    ：satellit_radiation_plot.py
@Author  ：Wangrry
@Date    ：2023/03/21/021 14:54 
The purpose of this file is .

In order to achieve the

Typical usage example:

  foo = ClassFoo()
  bar = foo.FunctionBar()
"""
import dmPython
import os
import sys
from pathlib import Path

import pandas as pd

sys.path.append(str(Path(__file__).resolve().parents[1]) + '/AP8AE8/GlobalRadiationDistribution')
sys.path.append(str(Path(__file__).resolve().parents[1]) + '/AP8AE8')

import global_radiation as gr
from read_flux_data import read_flux_data

import argparse
import numpy as np
import cartopy.crs as ccrs
import cmaps
import matplotlib.pyplot as plt
import matplotlib as mpl
from matplotlib.colors import LogNorm

cmap = cmaps.WhiteBlueGreenYellowRed
pi = 3.1415926
mpl.use('AGG')

def draw_log_plot(flux, filepath, df):
    x_edges, y_edges = np.linspace(-180, 179, 360), np.linspace(-90, 89, 180)
    fig = plt.figure(figsize=(10, 5), constrained_layout=True)
    ax = plt.axes(projection=ccrs.PlateCarree())

    ylabel = 'Longitude'
    ax.set_ylabel(ylabel)
    xlabel = 'Latitude'
    ax.set_xlabel(xlabel)

    ax.set_xticks([-150, -120, -90, -60, -30, 0, 30, 60 ,90, 120, 150])
    ax.set_yticks([-60, -30, 0, 30, 60])
    ax.set_xticklabels(['-150°', '-120°', '-90°', '-60°', '-30°', '0°', '30°', '60°', '90°', '120°', '150°'])
    ax.set_yticklabels(['-60°', '-30°', '0°', '30°', '60°'])
    ax.pcolormesh(x_edges, y_edges, flux, cmap=cmap, norm=LogNorm(vmin=1,vmax=flux.unstack().max()), rasterized=True)
    ax.scatter(df['lon'], df['lat'], s=7, c='black', transform=ccrs.PlateCarree(), label='Satellite trace')
    # ax.plot(df['lon'], df['lat'], color='black', label='Satellite trace')
    fig.colorbar(mappable=mpl.cm.ScalarMappable(norm=LogNorm(vmin=1,vmax=flux.unstack().max()), cmap=cmap), ax=ax, orientation='vertical',
                 label='Particle Flux(/cm$^2$•s•sr)', extend='both', shrink=0.7)
    fig.legend(bbox_to_anchor=(0.87, 0.21))

    if not os.path.exists(filedir + '/plots/' + filenum):
        os.makedirs(filedir + '/plots/' + filenum)    
    plt.savefig(filedir + '/plots/' + filenum + '/satellite_radiation.png')
    

def get_saa_flux(time_start, altitude, whatf, whichm):
    san = gr.solar_activity(time_start)
    return gr.main(time_start, altitude, whatf, whichm, san)  # return filedir, filenum


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    # 使用 `-` 来指明短选项
    # 使用 `--` 来指明可选项
    parser.add_argument("-sn", "--satellite_name", type=str, default='XWINC011') # 卫星
    parser.add_argument("-dt", "--date_time", type=str, help="date and time, ex: 2022-11-07 04:00:00",
                        default="2022-11-07 05:12:00")  # 时间
    parser.add_argument("-f", "--whatf", type=str, help="differential - 1, range - 2, above - 3", default='above')
    parser.add_argument("-m", "--whichm", type=str, help="ae8 - 1, ap8 - 2", default='ap8')  # 模式类型
    parser.add_argument("-c", "--channel", type=str, help="ae8: 5 channels P1-P5; ap8: 6 channels P1-P6",
                        default='P3')  # 通道类型
    args = parser.parse_args()
    args.date_time = args.date_time.replace('"', '')

    try:
        properties = {'user': 'SDC', 'password': 'sdc123456', 'server': '219.145.62.54', 'port': 15236,
                      'autoCommit': True, }
        conn = dmPython.connect(**properties)
        cursor = conn.cursor()
    except (dmPython.Error, Exception) as err:
        print(err)

    sql = "select LAT, LON, ALT, TIME from SEC_SATELLITE_LLA where SAT_ID = '" + args.satellite_name + "' and TIME >= '" \
          + args.date_time + "' order by TIME limit 10000"
    cursor.execute(sql)
    altitude = cursor.fetchone()[2]
    date_time = str(cursor.fetchone()[3])
    satellite_info = cursor.fetchall()
    df = pd.DataFrame(satellite_info, columns=['lat', 'lon', 'altitude', 'time'])
    filedir, filenum = get_saa_flux(date_time, altitude, args.whatf, args.whichm)
    filepath = filedir + '/' + filenum
    flux = read_flux_data(args.whichm, args.channel, filepath + '/flux.txt')
    draw_log_plot(flux, filedir + '/plots/' + filenum, df)
    os.system('rm -rf ' + filepath + '/flux.txt')
    print(filedir + '/plots/' + filenum)