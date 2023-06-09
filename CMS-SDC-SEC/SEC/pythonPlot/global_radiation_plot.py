'''
Author: wangrenruoyu@piesat.cn 
email: wangrry@hotmail.com
Date: 2023-05-08 11:16:43
LastEditors: wangrenruoyu@piesat.cn wangrry@hotmail.com
LastEditTime: 2023-06-09 15:23:43
FilePath: /SpaceEnvironmentCalculation/CMS-SDC-SEC/SEC/pythonPlot/global_radiation_plot.py
Description: 
'''
"""
@File    ：flux_global_plot.py.py
@Author  ：Wangrry
@Date    ：2022/12/13 16:42 
The purpose of this file is .

In order to achieve the

Typical usage example:

  foo = ClassFoo()
  bar = foo.FunctionBar()
"""
import ssl

# ssl._create_default_https_context = ssl._create_unverified_context
import os
import sys
from pathlib import Path

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


def draw_single_colorbar(flux, filepath):
    flux_max = flux.unstack().max()

    fig, ax = plt.subplots(figsize=(6, 1))
    fig.subplots_adjust(bottom=0.5)

    norm = mpl.colors.LogNorm(vmin=1, vmax=flux_max)

    cb = fig.colorbar(
        mpl.cm.ScalarMappable(norm=norm, cmap=cmap),
        cax=ax, orientation="horizontal",
    )
    cb.set_label(label='Particle Flux(/cm$^2$•s•sr)', color='white', size=15.)

    cb.ax.tick_params(colors='white')
    fig.savefig(filepath, transparent=True, bbox_inches='tight')
    plt.clf()


def draw_log_plot(flux, filepath):
    x_edges, y_edges = np.linspace(-180, 179, 360), np.linspace(-90, 89, 180)

    fig = plt.figure(figsize=(10, 5), constrained_layout=True)
    ax = plt.axes(projection=ccrs.PlateCarree())

    ax.pcolormesh(x_edges, y_edges, flux, cmap=cmap, norm=LogNorm(vmax=flux.unstack().max()), rasterized=True)
    plt.axis('off')
    fig.savefig(filepath, transparent=True, bbox_inches='tight', pad_inches=0)


def draw(flux, filepath):
    if not os.path.exists(filepath):
        os.makedirs(filepath)
    draw_single_colorbar(flux, filepath + '/color_bar.png')
    draw_log_plot(flux, filepath + '/log_plot.png')


def get_saa_flux(time_start, altitude, whatf, whichm):
    san = gr.solar_activity(time_start)
    return gr.main(time_start, altitude, whatf, whichm, san)  # return filedir, filenum


if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    # 使用 `-` 来指明短选项
    # 使用 `--` 来指明可选项
    parser.add_argument("-dt", "--date_time", type=str, help="date and time, ex: 2022-11-07 04:00:00",
                        default="2022-11-07 05:12:00")
    parser.add_argument("-a", "--altitude", type=int, help="satellite altitude, unit: km", default=1151)
    parser.add_argument("-f", "--whatf", type=str, help="differential - 1, range - 2, above - 3", default='above')
    parser.add_argument("-m", "--whichm", type=str, help="ae8 - 1, ap8 - 2", default='ap8')
    parser.add_argument("-c", "--channel", type=str, help="ae8: 5 channels P1-P5; ap8: 6 channels P1-P6", default='P3')
    args = parser.parse_args()

    args.date_time = args.date_time.replace('"', '')
    filedir, filenum = get_saa_flux(args.date_time, args.altitude, args.whatf, args.whichm)
    filepath = filedir + '/' + filenum
    flux = read_flux_data(args.whichm, args.channel, filepath + '/flux.txt')
    draw(flux, filedir + '/plots/' + filenum)
    os.system('rm -rf ' + filepath + '/flux.txt')
    print(filedir + '/plots/' + filenum)

