import sys
from pathlib import Path
sys.path.append(str(Path(__file__).resolve().parents[1]) + '/AP8AE8/orbitalPlaneRad')
import orbital_plane as op

import pandas as pd
import cmaps as cm
import matplotlib as mpl
import matplotlib.pyplot as plt
import numpy as np
from matplotlib.colors import LogNorm

import argparse
import datetime

def read_flux_data(whichm, energy, filepath):
    model_dict = {'ae8': 1, 'ap8': 2}
    if model_dict[whichm] == 1:
        names = ['latitude', 'longitude', 'altitude', 'date', 'E1', 'E2', 'E3', 'E4', 'E5']
    elif model_dict[whichm] == 2:
        names = ['latitude', 'longitude', 'altitude', 'date', 'P1', 'P2', 'P3', 'P4', 'P5', 'P6']

    # read in data
    flux_file_path = filepath
    data = pd.read_csv(flux_file_path, names=names)
    if model_dict[whichm] == 1:
        data.loc[data['E1'] < 0, 'E1'] = 0
        data.loc[data['E2'] < 0, 'E2'] = 0
        data.loc[data['E3'] < 0, 'E3'] = 0
        data.loc[data['E4'] < 0, 'E4'] = 0
        data.loc[data['E5'] < 0, 'E5'] = 0
    elif model_dict[whichm] == 2:
        data.loc[data['P1'] < 0, 'P1'] = 0
        data.loc[data['P2'] < 0, 'P2'] = 0
        data.loc[data['P3'] < 0, 'P3'] = 0
        data.loc[data['P4'] < 0, 'P4'] = 0
        data.loc[data['P5'] < 0, 'P5'] = 0
        data.loc[data['P6'] < 0, 'P6'] = 0
    # data的长度由时间和高度决定，高度固定为-200到+200，5米为间隔，即80个，样例
    # 时间数据为100个时间点，所以该数据的长度为0-7999 8000行数据
    df = pd.DataFrame(
        {
            'date': list(data['date']),
            'altitude': list(data['altitude']),
            'flux': data[energy]
        }
    )
    flux = df.set_index(['altitude', 'date'])
    # 运用unstack,不写参数，默认转换最里层的index，也就是key2；
    flux = flux.unstack()
    return flux, data['altitude'].drop_duplicates()


def draw(flux, alt, sat_trace, filepath):
    cmap = cm.WhiteBlueGreenYellowRed
    norm = LogNorm(vmax=flux.unstack().max())
    fig = plt.figure(figsize=(10, 5), dpi=150)
    # fig.subplots_adjust(bottom=0.8)
    ax = plt.subplot(1, 1, 1)

    ax.set_xticks([0, 19, 39, 59, 79, 99])
    xticklabels = []
    for i in [0, 19, 39, 59, 79, 99]:
        xticklabels.append(
            'lat: ' + str(location[i][0]) + '\n' + 'lon: ' + str(location[i][1]) + '\n' + str(location[i][2]))

    ax.set_xticklabels(xticklabels, fontsize=8)
    ylabel = 'Height (KM)'
    ax.set_ylabel(ylabel)
    level = np.arange(0, flux.unstack().max(), 5)
    contourf = ax.contourf(np.linspace(0, 99, 100), alt, flux, level, cmap=cmap, norm=norm)
    fig.colorbar(mappable=mpl.cm.ScalarMappable(norm=norm, cmap=cmap), ax=ax, orientation='horizontal',
                 label='Particle Flux(/cm$^2$•s•sr)', extend='both', shrink=0.7)
    ax.plot(np.arange(0, 100, 1), sat_trace['alt'], linewidth=2.0, label='Satellite trace', color='black')
    fig.legend(bbox_to_anchor=(0.87, 0.45))
    plt.savefig(filepath)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    # 使用 `-` 来指明短选项
    # 使用 `--` 来指明可选项
    parser.add_argument("-n", "--name", type=str, default="XWINC011")
    parser.add_argument("-s", "--start", type=str, default="2022-11-07 18:02:52.000")
    parser.add_argument("-f", "--whatf", type=str, help="differential - 1, range - 2, above - 3", default='above')
    parser.add_argument("-m", "--whichm", type=str, help="ae8 - 1, ap8 - 2", default='ap8')
    parser.add_argument("-c", "--channel", type=str, help="ae8: 5 channels P1-P5; ap8: 6 channels P1-P6", default='P3')
    args = parser.parse_args()

    args.start = args.start.replace('"', '')
    end_time = (datetime.datetime.strptime(args.start[0:19],"%Y-%m-%d %H:%M:%S") + datetime.timedelta(seconds=99)).strftime("%Y-%m-%d %H:%M:%S") + '.000'
    filedir, filenum = op.main(args.start, args.name, args.whatf, args.whichm)
    filepath = filedir + '/' + filenum
    flux, alt = read_flux_data(args.whichm, args.channel, filepath + '/flux.txt')
    sat_trace = pd.read_csv(filepath + '/sat_trace.txt')
    location = list(zip(list(sat_trace['lat']), list(sat_trace['lon']), list(sat_trace['date'])))
    draw(flux, alt, sat_trace, filepath + '/profile.png')
    print(filedir + '/' + filenum)
