import numpy as np
import pandas as pd
import sys
import os


def valid_numbers(x):
    """
    保留有效数字
    :param x: 原始数字
    :return: 保留有效位数后的数字
    """
    x = float(np.format_float_positional(x, precision=5, unique=False, fractional=False, trim='k'))
    return x


def read_file(h, i, scenario):
    current_dir = os.path.dirname(os.path.abspath(__file__))
    file_name = current_dir+'/data/flux_'+str(scenario)+'_inc_'+str(i)+'h_'+str(h)+'.flx.tsv'
    D = pd.read_csv(file_name, sep='\t', header=1)
    return D


def get_filelist():
    inc_index = []
    h_index = []
    current_dir = os.path.dirname(os.path.abspath(__file__))
    for file_name in os.listdir(current_dir+'/data'):
        inc_index.append(int(file_name.split('inc_')[1].split('h')[0]))
        h_index.append(int(file_name.split('h_')[1].split('.flx')[0]))
    inc_index = list(map(int, inc_index))
    h_index = list(map(int, h_index))
    inc_index = np.array(inc_index)
    h_index = np.array(h_index)
    return inc_index, h_index


def Cal_energy_flux(D, ion):
    ion_index = 'Z='+str(int(ion))
    energy = np.array(D['Unnamed: 0'], dtype=float)
    Z = np.array(D[ion_index]).astype(str)
    ind = np.where(Z != ' ')
    energy = np.array(energy[ind], dtype=float)
    flux = np.array(Z[ind], dtype=float)
    return energy, flux


# Parameter
incl = float(str(sys.argv[1]))   # 倾角
geo_height = (float(str(sys.argv[2])) + float(str(sys.argv[3])))/2  # 近地点远地点平均
# ion: Z=1....Z=91
ion_index = int(str(sys.argv[4]))  # 重离子序数


# geographic height
h_low = int(geo_height/50)*50
h_high = h_low+50


# inclination
i_low = int(incl/2)*2
i_high = i_low+2


# near-Earth Interplanetary/Geosynchronous Orbit
if geo_height > 1500:
    h_low = 36000
    h_high = 36000
    geo_height = 36000
    i_low = 0
    i_high = 0

print('height_low:', h_low)
print('height_high:', h_high)
print('inclination_low:', i_low)
print('inclination_high:', i_high)


def Cal_ion_flux_in_different_scenario(incl, geo_height, ion_index, scenario):
    
    # interp height of i low
    D_h_low_i_low = read_file(h_low, i_low, scenario)
    energy_height_low, flux_height_low = Cal_energy_flux(D_h_low_i_low, ion_index)

    D_h_high_i_low = read_file(h_high, i_low, scenario)
    energy_height_high, flux_height_high = Cal_energy_flux(D_h_high_i_low, ion_index) 

    energy_start = max(min(energy_height_low), min(energy_height_high))
    energy_end = min(max(energy_height_low), max(energy_height_high))

    ind = np.where((energy_height_low <= energy_end) & (energy_height_low >= energy_start))
    flux_low = flux_height_low[ind]
    energy_i_low = energy_height_low[ind]

    ind = np.where((energy_height_high <= energy_end) & (energy_height_high >= energy_start))
    flux_high = flux_height_high[ind]

    flux_i_low = np.zeros(len(energy_i_low))
    for i in range(len(energy_i_low)):
        flux_i_low[i] = flux_low[i]+(flux_high[i]-flux_low[i])*((geo_height-h_low)/50)

    # interp height of i high
    D_h_low_i_high = read_file(h_low, i_high, scenario)
    energy_height_low, flux_height_low = Cal_energy_flux(D_h_low_i_high, ion_index)

    D_h_high_i_high = read_file(h_high, i_high, scenario)
    energy_height_high, flux_height_high = Cal_energy_flux(D_h_high_i_high, ion_index) 

    energy_start = max(min(energy_height_low), min(energy_height_high))
    energy_end = min(max(energy_height_low), max(energy_height_high))

    ind = np.where((energy_height_low <= energy_end) & (energy_height_low >= energy_start))
    flux_low = flux_height_low[ind]
    energy_i_high = energy_height_low[ind]

    ind = np.where((energy_height_high <= energy_end) & (energy_height_high >= energy_start))
    flux_high = flux_height_high[ind]

    flux_i_high = np.zeros(len(energy_i_high))
    for i in range(len(energy_i_high)):
        flux_i_high[i] = flux_low[i]+(flux_high[i]-flux_low[i])*((geo_height-h_low)/50)

    # 2 flux with target h：flux_i_high; flux_i_low
    # 2 energy with target h: energy_i_high; energy_i_low
    energy_start = max(min(energy_i_low), min(energy_i_high))
    energy_end = min(max(energy_i_low), max(energy_i_high))

    ind = np.where((energy_i_low <= energy_end) & (energy_i_low >= energy_start))
    energy = energy_i_low[ind]

    flux_i_high = flux_i_high[ind]
    flux_i_low = flux_i_low[ind]

    flux = np.zeros(len(energy))
    for i in range(len(energy)):
        flux[i] = flux_i_low[i]+(flux_i_high[i]-flux_i_low[i])*((incl-i_low)/1)

    # valid_numbers
    vec_valid_numbers = np.vectorize(valid_numbers)
    flux = vec_valid_numbers(flux)

    return energy, flux


try:
    energy_solarmax, flux_solarmax = Cal_ion_flux_in_different_scenario(incl, geo_height, ion_index, scenario='solarmax')
except:
    energy_solarmax = 0
    flux_solarmax = 0

try:
    energy_solarmin, flux_solarmin = Cal_ion_flux_in_different_scenario(incl, geo_height, ion_index, scenario='solarmin')
except:
    energy_solarmin = 0
    flux_solarmin = 0

try:
    energy_worstweek, flux_worstweek = Cal_ion_flux_in_different_scenario(incl, geo_height, ion_index, scenario='worstweek')
except:
    energy_worstweek = 0
    flux_worstweek = 0


# 以JSON形式输出结果
dic = {}
if isinstance(energy_solarmin, np.ndarray):
    dic['energy_solarmin'] = energy_solarmin.tolist()
    dic['flux_solarmin'] = flux_solarmin.tolist()
else:
    dic['energy_solarmin'] = 0
    dic['flux_solarmin'] = 0


if isinstance(energy_solarmax, np.ndarray):
    dic['energy_solarmax'] = energy_solarmax.tolist()
    dic['flux_solarmax'] = flux_solarmax.tolist()
else:
    dic['energy_solarmax'] = 0
    dic['flux_solarmax'] = 0

if isinstance(energy_worstweek, np.ndarray):
    dic['energy_worstweek'] = energy_worstweek.tolist()
    dic['flux_worstweek'] = flux_worstweek.tolist()
else:
    dic['energy_worstweek'] = 0
    dic['flux_worstweek'] = 0
print('###')
print(dic)
print('###')


# plt.plot(energy_solarmin.tolist(),flux_solarmin.tolist())
# plt.xscale('log')
# plt.yscale('log')
# plt.xlim((1e0,1e5))
# plt.ylim((1e-6,1e2))
# plt.xlabel('Kinetic Energy(MeV/nucleon)')
# plt.ylabel('Flux(m-2-s-sr-MeV/nuc)-1')
# plt.savefig('test_solarmin')