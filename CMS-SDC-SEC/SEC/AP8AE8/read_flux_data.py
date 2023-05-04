"""
@File    ：read_flux_data.py
@Author  ：Wangrry
@Date    ：2023/01/09 09:48 
The purpose of this file is .

In order to achieve the

Typical usage example:

  foo = ClassFoo()
  bar = foo.FunctionBar()
"""
import pandas as pd

pi = 3.1415926


def read_flux_data(whichm, energy, filepath):
    model_dict = {'ae8': 1, 'ap8': 2}
    if model_dict[whichm] == 1:
        names = ['latitude', 'longitude', 'alt', 'date', 'E1', 'E2', 'E3', 'E4', 'E5']
    elif model_dict[whichm] == 2:
        names = ['latitude', 'longitude', 'alt', 'date', 'P1', 'P2', 'P3', 'P4', 'P5', 'P6']

    # read in data
    flux_file_path = filepath
    data = pd.read_csv(flux_file_path, names=names)
    # print(data)
    # exit()
    df = pd.DataFrame(
        {
            'latitude': list(data['latitude']),
            'longitude': list(data['longitude']),
            'flux': data[energy]
        }
    )
    flux = df.set_index(['longitude', 'latitude'])
    # 运用unstack,不写参数，默认转换最里层的index，也就是key2；
    flux = flux.unstack()
    flux.reset_index()
    flux /= 4 * pi
    return flux

if __name__ == '__main__':
    read_flux_data('ap8', 'P3', '/home/wrry/work/SEC/AP8AE8/29512348/flux.txt')